package lesson8.server;

import lesson8.Prefs;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

// обработчик запросов клиента
public class ClientHandler {
    private DataInputStream in;
    private DataOutputStream out;

    private boolean authenticated;
    private String login, nickname;

    public ClientHandler(Server server, Socket socket) {
        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
                try {
                    //цикл аутентификации

                    // со стороны клиента запрос на установление связи (и открытие сокета) приходит
                    // на сервер не в абстрактном виде, а представляет собой один из двух конкретных
                    // запросов - либо авторизации, либо регистрации
                    socket.setSoTimeout(1000 * Prefs.TIMEOUT);
                    while (true) {
                        String str = in.readUTF();

                        // обработка команд
                        if (str.startsWith(Prefs.COM_ID)) {
                            String s = str.toLowerCase();

                            // команда выхода
                            if (s.equals(Prefs.getCommand(Prefs.COM_QUIT))) {
                                sendMsg(Prefs.getCommand(Prefs.COM_QUIT));
                                break;
                            }

                            // команда авторизации
                            if (s.startsWith(Prefs.getCommand(Prefs.COM_AUTHORIZE))) {
                                String[] token = s.split(" ", 3);
                                if (token.length == 3) {
                                    sendAuthorizationWarning();
                                    login = token[1];
                                    String newNick = server.getAuthService().getNickname(token[1], token[2]);
                                    if (newNick != null) {
                                        if (server.isUserConnected(login)) {
                                            sendMsg("Учетная запись уже используется пользователем " + newNick);
                                        } else {
                                            socket.setSoTimeout(0);
                                            nickname = newNick;
                                            sendMsg(Prefs.getCommand(Prefs.SRV_AUTH_OK, nickname));
                                            authenticated = true;
                                            server.subscribe(this);
                                            break;
                                        }
                                    } else {
                                        sendMsg("Логин / пароль не верны");
                                    }
                                }
                            }

                            // команда регистрации
                            if (s.startsWith(Prefs.getCommand(Prefs.COM_REGISTER))) {
                                String[] token = str.split(" ");
                                if (token.length == 4) {
                                    socket.setSoTimeout(0);
                                    sendMsg(server.getAuthService().registered(token[1], token[2], token[3])
                                            ? Prefs.getCommand(Prefs.SRV_REG_ACCEPT)
                                            : Prefs.getCommand(Prefs.SRV_REG_FAULT));
                                }
                            }
                        }
                    }

                    //цикл работы
                    while (authenticated) {
                        String str = in.readUTF();
                        boolean broadcastMsg = true;

                        if (str.startsWith(Prefs.COM_ID)) {
                            String[] s = str.substring(1).split(" ", 3);
                            switch (s[0].toLowerCase()) {
                                case Prefs.COM_QUIT:
                                    sendMsg(Prefs.getCommand(Prefs.COM_QUIT));
                                    broadcastMsg = false;
                                    break;
                                case Prefs.COM_PRIVATE_MSG:
                                    if (s.length == 3) {
                                        broadcastMsg = false;
                                        server.sendPrivateMsg(this, s[1], s[2]);
                                    }
                            }
                        }
                        if (broadcastMsg) server.sendBroadcastMsg(this, str);
                    }
                    //SocketTimeoutException
                } catch (SocketTimeoutException ex) {
                    // с отправкой команды выхода в методе connect контроллера цикл аутентификации
                    // прервется и произойдет переход далее - к циклу работы (который не начнется при
                    // отсутствии авторизации) и сокет будет закрыт перед завершением работы потока
                    sendMsg(Prefs.getCommand(Prefs.COM_QUIT));
                    try {
                        socket.setSoTimeout(0);
                    } catch (SocketException e) {
                        e.printStackTrace();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                } finally {
                    server.unsubscribe(this);
                    System.out.println("Client disconnected");
                    try {
                        socket.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
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
            if (msg.equals(Prefs.getCommand(Prefs.COM_QUIT))) authenticated = false;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String getNickname() {
        return nickname;
    }

    public String getLogin() {
        return login;
    }

    // окончание минуты/секунды в зависимости от числа в винительном(?) падеже
    private String getAccusativeEnding(int number) {
        int lastDigit = number;
        while (lastDigit > 10) lastDigit %= 10;

        String res = "";
        if (number < 10 || number > 20)
            switch (lastDigit) {
                case 1: res = "у"; break;
                case 2: case 3: case 4:  res = "ы";
            }
        return res;
    }

    // предупредить об ограничении времени авторизации
    private void sendAuthorizationWarning() {
        if (Prefs.TIMEOUT > 0) {
            int mn = Prefs.TIMEOUT / 60, sc = Prefs.TIMEOUT % 60;
            StringBuilder warnMsg = new StringBuilder("Сеанс авторизации будет завершен через ");
            if (mn > 0)
                warnMsg.append(mn).append(" минут")
                       .append(getAccusativeEnding(mn));
            if (sc > 0) {
                if (mn > 0) warnMsg.append(" ");
                warnMsg.append(sc).append(" секунд")
                       .append(getAccusativeEnding(sc));
            }
            sendMsg(warnMsg.toString());
        }
    }
}