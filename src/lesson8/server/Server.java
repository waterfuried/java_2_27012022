package lesson8.server;

import lesson8.Prefs;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    private ServerSocket server;
    private Socket socket;

    private List<ClientHandler> clients;
    private AuthService authService;

    public Server() {
        clients = new CopyOnWriteArrayList<>();
        authService = new SimpleAuthService();

        try {
            server = new ServerSocket(Prefs.PORT);
            System.out.println("Server started");

            while (true) {
                socket = server.accept();
                System.out.println("Client connected");
                new ClientHandler(this, socket);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            try {
                server.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void sendBroadcastMsg (ClientHandler sender, String msg){
        for (ClientHandler c : clients) {
            c.sendMsg(String.format("[ %s ]: %s", sender.getNickname(), msg));
        }
    }

    public void sendPrivateMsg (ClientHandler sender, String receiver, String msg) {
        String message = "[ личное сообщение %s %s ]: %s";
        for (ClientHandler c : clients) {
            if (c.getNickname().equals(receiver)) {
                c.sendMsg(String.format(message, "от", sender.getNickname(), msg));
                if (!receiver.equals(sender.getNickname()))
                    sender.sendMsg(String.format(message, "для", receiver, msg));
                return;
            }
        }
        sender.sendMsg("Пользователя с ником \"" + receiver + "\" нет в чате");
    }

    public void broadcastClientList() {
        StringBuilder sb = new StringBuilder(Prefs.getCommand(Prefs.COM_CLIENTLIST));

        for (ClientHandler c : clients) {
            sb.append(" ").append(c.getNickname());
        }
        for (ClientHandler c : clients) {
            c.sendMsg(sb.toString());
        }
    }

    public boolean isUserConnected (String login) {
        for (ClientHandler c : clients)
            if (c.getLogin().equals(login))
                return true;
        return false;
    }

    public void subscribe (ClientHandler clientHandler){
        clients.add(clientHandler);
        broadcastClientList();
    }

    public void unsubscribe (ClientHandler clientHandler){
        clients.remove(clientHandler);
        broadcastClientList();
    }

    public AuthService getAuthService() {
        return authService;
    }

    public static void main(String[] args) { new Server(); }
}