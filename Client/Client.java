import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client implements Runnable {
    private static final String SERVER_ADRDESS = "127.0.0.1";
    private static final int SERVER_PORT = 4000;
    private ClientSocket clientSocket;
    private static String room = "null";
    private static boolean isPlaying = false;

    private Scanner scanner;

    public Client() {
        scanner = new Scanner(System.in);
    }

    public void start() {
        try {
            System.out.println("Cliente conectado ao servidor em " + SERVER_ADRDESS + " Porta: " + SERVER_PORT);
            clientSocket = new ClientSocket(new Socket(SERVER_ADRDESS, SERVER_PORT));
            messageLoop();

        } catch (IOException ex) {
            System.out.println("Erros ao iniciar o Cliente " + ex.getMessage());
        } finally {
            clientSocket.close();
        }
    }

    @Override
    public void run() {
        String msg;
        while ((msg = clientSocket.getMessage()) != null) {
            String[] msgSplit = msg.split("\\|");
            if (msgSplit.length > 3 && msgSplit[2].equalsIgnoreCase("M") && !isPlaying) {
                System.out.print("\033[2K\r");
                System.out.println("\r Msg recebida:" + msgSplit[3]);
                System.out.print("Digite uma mensagem: ");
            }
        }
    }

    private void messageLoop() {
        try {
            String[] msg;
            String text = "null";
            do {
                if (room.equalsIgnoreCase("null")) {
                    clientSocket.requestRooms();
                    msg = (clientSocket.getMessage()).split("\\|");
                    System.out.println("Salas de Jogo Disponiveis:\n" + msg[3]);

                    System.out.println("Escolha um servidor para se conectar:");
                    text = scanner.nextLine();
                    clientSocket.conectRoom(text);

                    msg = (clientSocket.getMessage()).split("\\|");
                    if (msg[2].equalsIgnoreCase("E")) {
                        System.out.println(msg[3]);
                    } else if (msg[2].equalsIgnoreCase("S")) {
                        System.out.println(msg[3]);
                        String[] msgSplited = msg[3].split(":");
                        room = msgSplited[1];

                    }
                    new Thread(this).start();

                } else {
                    System.out.print("Digite uma mensagem: ");
                    String mensagem = scanner.nextLine();

                    if (mensagem.equalsIgnoreCase("sair")) {
                        System.out.println("Encerrando conexão...");
                        break; // exits the messageLoop()
                    }

                    clientSocket.sendMsgChat(clientSocket, mensagem, room);
                }

            } while (!text.equalsIgnoreCase("sair"));

            /*
             * String msg;
             * do{
             * 
             * } while (!msg.equalsIgnoreCase("sair"));
             */
        } catch (Exception e) {
            System.out.print("Erros ao enviar a mensagem " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }
}