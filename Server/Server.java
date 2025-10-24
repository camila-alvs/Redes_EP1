import java.io.*;
import java.net.ServerSocket;
import java.util.*;

public class Server {
    private static final int PORT = 4000;
    private ServerSocket serverSocket;
    private final List<GameRoom> rooms = new ArrayList<>();
    private final List<ServerSocketHandler> clientes = new LinkedList<>();

    private final Scanner scanner = new Scanner(System.in);

    public void start() {
        try {
            String config;
            System.out.println("configurações do servidor");
            System.out.println("Crie as salas com o seguinte formato:SALA <nome da sala>");
            do {
                config = scanner.nextLine();
                createRoom(config);
            } while (!config.equalsIgnoreCase("pronto"));
            listRoom();
            System.out.println("Servidor Iniciado");
            serverSocket = new ServerSocket(PORT);
            clientConnectionLoop();
        } catch (IOException ex) {
            System.out.println("Erros ao iniciar o servidor " + ex.getMessage());
        }
    }

    private void removeClient(ServerSocketHandler client) {
        synchronized (clientes) {
            clientes.remove(client);
        }

        synchronized (rooms) {
            for (GameRoom room : rooms) {
                for (int i = 0; i < room.getPlayersSize(); i++) {
                    if (room.getPlayers(i).equals(client)) {
                        room.removePlayer(client);
                        System.out.println("Removido cliente " + client.getRemoteSocketAddress()
                                + " da sala " + room.getName());
                        break;
                    }
                }
            }
        }

        client.close();
    }

    private void clientConnectionLoop() {
        try {
            while (true) {
                ServerSocketHandler ServerSocketHandler = new ServerSocketHandler(serverSocket.accept());
                clientes.add(ServerSocketHandler);
                new Thread(() -> clientMessageLoop(ServerSocketHandler)).start();
            }
        } catch (IOException e) {
            System.out.println("Erro ao conectar cliente " + e.getMessage());
        }
    }

    private void sendMsgToALL(ServerSocketHandler sender, String texto, String sala) {
        for (GameRoom room : rooms) {
            if (room.getName().equalsIgnoreCase(sala)) {
                for (int i = 0; i < room.getPlayersSize(); i++) {
                    ServerSocketHandler destinatario = room.getPlayers(i);
                    if (!destinatario.equals(sender)) {
                        destinatario.sendMsgChat(sender, texto, sala);
                    }
                }
            }
        }
    }

    private void clientMessageLoop(ServerSocketHandler ServerSocketHandler) {
        String msg;
        try {
            while ((msg = ServerSocketHandler.getMessage()) != null) {
                String[] msgCampos = msg.split("\\|");
                System.out.println("campo 0: " + msgCampos[0] + " campo 1: " + msgCampos[1] + " campo 2: "
                        + msgCampos[2] + " campo 3: " + msgCampos[3]);
                if (msgCampos[1].equalsIgnoreCase("SERVER")) {
                    switch (msgCampos[2]) {
                        case "L":
                            ServerSocketHandler.sendRooms(rooms);
                            break;
                        case "S":
                            for (GameRoom room : rooms) {
                                if (room.getName().equalsIgnoreCase(msgCampos[3])) {
                                    if (room.getPlayersSize() >= 2) {
                                        ServerSocketHandler
                                                .sendErro("<-----ERRO: Sala lotada, por favor tente novamente----->");
                                    } else {
                                        room.addPlayers(ServerSocketHandler);
                                        ServerSocketHandler.sendRoomConectionConfirmation(
                                                "Conexão com sala bem sucedida:" + room.getName());
                                    }
                                }
                            }
                            listRoom();
                            break;
                    }
                } else {
                    if (!"sair".equalsIgnoreCase((msgCampos[3]))) {
                        System.out.println("Msg recebida do cliente " + msgCampos[0] + ": " + msgCampos[3]);
                        sendMsgToALL(ServerSocketHandler, msgCampos[3], msgCampos[1]);
                    } else {
                        removeClient(ServerSocketHandler);
                        return;
                    }
                }
            }
        } finally {
            ServerSocketHandler.close();
        }
    }

    private void createRoom(String config) {
        if (config.toLowerCase().startsWith("sala")) {
            for (GameRoom room : rooms) {
                if (room.getName().equals(config.substring(5))) {
                    System.out.println("Já existe uma sala com esse nome");
                    return;
                }
            }
            rooms.add(new GameRoom(config.substring(5)));
        } else if (config.substring(0, 3).equalsIgnoreCase("pronto")) {
            return;
        } else {
            System.out.println("comando desconhecido");
        }
    }

    private void listRoom() {
        for (GameRoom room : rooms) {
            System.out.print(room.getName() + ": ");
            room.listPlayers();
            System.out.print("\n");
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

}