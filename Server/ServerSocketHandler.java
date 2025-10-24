import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.List;

public class ServerSocketHandler {
    private final Socket ClientSocket;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;

    public ServerSocketHandler(Socket socket) throws IOException {
            this.ClientSocket = socket;
            System.out.println("Cliente " + socket.getRemoteSocketAddress() + " conectado");
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
    }

    public SocketAddress getRemoteSocketAddress(){
        return ClientSocket.getRemoteSocketAddress();
    }

    public void close(){
        try {
            in.close();
            out.close();
            ClientSocket.close();
        } catch (IOException e) {
            System.out.println("Erro ao fechar a coneção: " + e.getMessage());
        }
    }

    public String getMessage(){
        try{
            try {
                return (String) in.readObject();
            } catch (ClassNotFoundException e) {
                System.out.println("Erro ao ler a mensagem: " + e.getMessage());
            }
        } catch (IOException e) {
            return null;
        }
        return null;
    }

    public void sendErro(String msg){
        sendMsg(ClientSocket.getRemoteSocketAddress() + "|SERVER|E|" + msg);
    }

    public void sendRoomConectionConfirmation(String msg){
        sendMsg(ClientSocket.getRemoteSocketAddress() + "|SERVER|S|" + msg);
    }

    private void sendMsg(String msg){
        try {
            out.writeObject(msg);
            out.flush();
        } catch (IOException e) {
            System.out.println("Erro ao enviar a mensagem: " + e.getMessage());
        }
    }

    public void sendMsgChat(ServerSocketHandler sender, String texto, String room){
        sendMsg(sender + "|" + room + "|M|" + texto);
    }

    public void sendRooms( List<GameRoom> rooms){
        StringBuilder sb = new StringBuilder();

        for (GameRoom sala : rooms) {
            sb.append(sala.getName()).append(": ");

            int numPlayers = sala.getPlayersSize();

            if (numPlayers == 0) {
                sb.append("Sala vazia");
            } else if (numPlayers == 1) {
                sb.append(sala.getPlayers(0));
            } else {
                sb.append(sala.getPlayers(0)).append(", ").append(sala.getPlayers(1));
            }

            sb.append("\n"); // pula linha para próxima sala
        }

        sendMsg(ClientSocket.getRemoteSocketAddress() + "|SERVER|L|" + sb.toString());
    }
}