
import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;

public class ClientSocket {
    private final Socket socket;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;

    public ClientSocket(Socket socket) throws IOException {
        this.socket = socket;
        System.out.println("Cliente " + socket.getRemoteSocketAddress() + " conectado");
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());
    }

    public SocketAddress getRemoteSocketAddress() {
        return socket.getRemoteSocketAddress();
    }

    public void close() {
        try {
            out.close();
            in.close();
            socket.close();
        } catch (IOException e) {
            System.out.println("Erro ao fechar a coneção: " + e.getMessage());
        }
    }

    public void sendMsgChat(ClientSocket sender, String texto, String room) {
        sendMsg(sender + "|" + room + "|M|" + texto);
    }

    public String getMessage() {
        try {
            return (String) in.readObject();
        } catch (ClassNotFoundException | IOException e) {
            System.out.println("Erro ao ler a mensagem: " + e.getMessage());
            return null;
        }
    }

    public void requestRooms() {
        sendMsg(socket.getRemoteSocketAddress() + "|SERVER|L|NULL");
    }

    public void conectRoom(String room) {
        sendMsg(socket.getRemoteSocketAddress() + "|SERVER|S|" + room);
    }

    public void sendMsg(String msg) {
        try {
            out.writeObject(msg);
            out.flush();
        } catch (IOException e) {
            System.out.println("Erro ao enviar a mensagem: " + e.getMessage());
        }
    }
}