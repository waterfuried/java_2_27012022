package lesson7.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    private ServerSocket server;
    private Socket socket;
    private final int PORT = 8189;

    private List<ClientHandler> clients;
    private AuthService authService;

    public Server() {
        clients = new CopyOnWriteArrayList<>();
        authService = new SimpleAuthService();

        try {
            server = new ServerSocket(PORT);
            System.out.println("Server started");

            while (true) {
                socket = server.accept();
                System.out.println("Client connected");
                new ClientHandler(this, socket);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendBroadcastMsg (ClientHandler sender, String msg) {
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

    public void notifyAll (ClientHandler sender, int actionCode) {
        if (actionCode >= 0 && actionCode <= 1) {
            for (ClientHandler c : clients)
                c.sendMsg("Пользователь \"" + sender.getNickname() + "\" " +
                          (actionCode == 0 ? "вышел из чата" : "вошел в чат"));
        }
    }

    public boolean isUserConnected (String nickname) {
        for (ClientHandler c : clients)
            if (c.getNickname().equals(nickname))
                return true;
        return false;
    }

    public void subscribe (ClientHandler clientHandler){
        clients.add(clientHandler);
        notifyAll(clientHandler, 1);
    }

    public void unsubscribe (ClientHandler clientHandler){
        clients.remove(clientHandler);
        notifyAll(clientHandler, 0);
    }

    public AuthService getAuthService() {
        return authService;
    }

    public static void main(String[] args) { new Server(); }
}