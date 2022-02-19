package lesson6;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server implements Informative {
    boolean networkProblem, networkError;

    DataInputStream in;
    DataOutputStream out;

    // последнее пришедшее сообщение/команда от клиента
    String lastOutput;

    AtomicBoolean serverActive, clientActive;

    // общий цикл работы: ожидание сообщений от клиентов, их вывод в консоли
    // с возможностью набора и отправки сообщений в ней же
    public Server() {
        // сокет сервера - ожидает запросов из сети,
        // закрывать его есть смысл только при завершении работы сервера;
        try {
            ServerSocket server = new ServerSocket(PORT);
            System.out.println("Произведен запуск сервера (порт: " + PORT + ")");
            // после запуска сервера любые ошибки не должны прерывать его работу -
            // о них нужно только сообщать
            // сервер и клиент активны, пока в потоке отправки сообщений не введена команда выхода из чата
            serverActive = new AtomicBoolean(true);

            // прослушивание сети и принятие входящих соединений - добавление клиентов;
            // клиентские сокеты закрываются при выходе клиентов из чата
            while (serverActive.get()) {
                // сокет клиента - его есть смысл закрывать, когда клиент отсоединился;
                // метод accept блокирует выполнение до установления соединения
                try {
                    Socket socket = server.accept();
                    System.out.println("Установлено соединение с клиентом");

                    clientActive = new AtomicBoolean(true);

                    // с присоединением клиента запускается поток чтения сообщений от него
                    Thread reader = new Thread(() -> {
                        try {
                            in = new DataInputStream(socket.getInputStream());
                            while (serverActive.get() && clientActive.get()) {
                                String str = null, s;
                                try {
                                    str = in.readUTF();
                                } catch (IOException ex) {
                                    if (serverActive.get()) setupNetworkError(ERR_NETWORK_CONNECTION);
                                }
                                boolean active = str != null && !str.equalsIgnoreCase(LOGOUT);
                                if (lastOutput == null || nonEmpty(str) || nonEmpty(lastOutput) || !active)
                                    s = "[" +
                                            new SimpleDateFormat("HH:mm:ss").format(new Date())
                                            + "] клиент" + (active ? ": \"" + str + "\"" : " покинул чат") + "\n";
                                else
                                    s = str + "\n";
                                printMessage(s, false);
                                lastOutput = str;
                                clientActive.set(active);
                            }
                            // ошибка получения входного потока
                        } catch (IOException ex) {
                            if (serverActive.get()) setupNetworkError(ERR_NETWORK_CONNECTION);
                        } finally {
                            // когда пользователь вышел из чата, клиентский сокет можно закрыть,
                            // а работу сервера - завершать
                            try {
                                socket.close();
                                serverActive.set(false);
                            } catch (IOException ex) {
                                setupNetworkError(ERR_CLOSE_SOCKET);
                            }
                        }
                        clearNetworkProblem();
                    });
                    reader.start(); // поток чтения сообщений от клиента

                    // отправку сообщений клиенту
                    // - имеет смысл запускать только после установления с ним связи
                    // - нет смысла выделять в отдельный поток
                    try {
                        out = new DataOutputStream(socket.getOutputStream());
                        Scanner sc = new Scanner(System.in);
                        while (serverActive.get() && clientActive.get()) {
                            printMessage("Сообщение для клиента (" + LOGOUT + " для выхода): ", true);
                            String str = sc.nextLine();
                            if (str.equalsIgnoreCase(LOGOUT)) {
                                printMessage("Произведен выход из чата\n", true);
                                printMessage("", false);
                                serverActive.set(false);
                            }
                            out.writeUTF(str);
                        }
                        // обработать ошибки отправки сообщения
                    } catch (IOException ex) {
                        if (clientActive.get()) setupNetworkError(ERR_NETWORK_CONNECTION);
                    }
                    clearNetworkProblem();
                    try {
                        reader.interrupt();
                        server.close();
                    } catch (IOException ex) {
                        setupNetworkError(ERR_CLOSE_SOCKET);
                    }

                } catch (IOException ex) {
                    // попадание сюда произойдет при ожидании запросов на соединение от клиентов
                    ex.printStackTrace();
                }
            }
        } catch (IOException ex) {
            // если порт уже занят, сервер не может его открыть
            setupNetworkError(ERR_PORT_BUSY, PORT);
        }
    }

    public void setupNetworkProblem () {
        networkProblem = networkError;
    }
    public void setupNetworkError (String msg) {
        networkError = true;
        alertNetworkProblem(msg, null, networkError, networkProblem);
        setupNetworkProblem();
    }
    public void setupNetworkError (String msg, Integer val) {
        networkError = true;
        alertNetworkProblem(msg, val, networkError, networkProblem);
        setupNetworkProblem();
    }
    public void clearNetworkProblem () {
        if (!networkError) networkProblem = false;
    }

    public static void main(String[] args) {
        new Server();
    }
}