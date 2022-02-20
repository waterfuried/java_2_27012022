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

    public void broadcastMsg(ClientHandler sender, String msg){
        String message = String.format("[ %s ]: %s", sender.getNickname(), msg);

        for (ClientHandler c : clients) {
            c.sendMsg(message);
        }
    }

    public void sendPrivateMsg(ClientHandler sender, String receiver, String msg) {
        String message = String.format("[ %s ]: %s", sender.getNickname(), msg);

        boolean found = false;
        for (ClientHandler c : clients) {
            if (c.getNickname().equals(receiver)) {
                c.sendMsg(message);
                found = true;
            }
        }
        if (!receiver.equals(sender.getNickname()))
            sender.sendMsg(found ? message : "Пользователя с ником \"" + receiver + "\" нет в чате");
    }

    public void notifyAll(ClientHandler sender, int actionCode) {
        if (actionCode >= 0 && actionCode <= 1) {
            String message = "Пользователь \"" + sender.getNickname() + "\" ";
            message += actionCode == 0 ? "вышел из чата" : "вошел в чат";

            for (ClientHandler c : clients) {
                c.sendMsg(message);
            }
        }
    }

    public boolean isUserConnected(String nickname) {
        boolean found = false;
        for (ClientHandler c : clients) {
            if (c.getNickname().equals(nickname)) {
                found = true;
                break;
            }
        }
        return found;
    }

    public void subscribe(ClientHandler clientHandler){
        clients.add(clientHandler);
        notifyAll(clientHandler, 1);
    }

    public void unsubscribe(ClientHandler clientHandler){
        clients.remove(clientHandler);
        notifyAll(clientHandler, 0);
    }

    public AuthService getAuthService() {
        return authService;
    }

    public static void main(String[] args) { new Server(); }
}
