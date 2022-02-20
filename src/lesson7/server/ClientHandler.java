package lesson7.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private boolean authenticated;
    private String nickname;

    public ClientHandler(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;

            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
                try {
                    //цикл аутентификации
                    while (true) {
                        String str = in.readUTF();

                        if (str.startsWith("/")) {
                            if (str.equals("/end")) {
                                sendMsg("/end");
                                break;
                            }

                            if (str.startsWith("/auth")) {
                                String[] token = str.split(" ", 3);
                                if (token.length < 3) {
                                    continue;
                                }
                                String newNick = server.getAuthService().getNickname(token[1], token[2]);
                                if (newNick != null) {
                                    if (server.isUserConnected(newNick)) {
                                        sendMsg("Пользователь " + newNick + " уже находится в чате");
                                    } else {
                                        nickname = newNick;
                                        sendMsg("/auth_ok " + nickname);
                                        authenticated = true;
                                        server.subscribe(this);
                                        break;
                                    }
                                } else {
                                    sendMsg("Логин / пароль не верны");
                                }
                            }
                        }
                    }
                    //цикл работы
                    boolean active = true;
                    while (authenticated && active) {
                        String str = in.readUTF();
                        boolean broadcastMsg = true;

                        if (str.startsWith("/")) {
                            String[] s = str.substring(1).split(" ", 3);
                            switch (s[0].toLowerCase()) {
                                case "end":
                                    sendMsg("/end");
                                    active = false;
                                    break;
                                case "w":
                                    if (s.length == 3) {
                                        broadcastMsg = false;
                                        server.sendPrivateMsg(this, s[1], s[2]);
                                    }
                                    break;
                                case "auth":
                                    broadcastMsg = false;
                            }
                        }
                        if (active && broadcastMsg) server.broadcastMsg(this, str);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    server.unsubscribe(this);
                    System.out.println("Client disconnected");
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNickname() {
        return nickname;
    }
}
