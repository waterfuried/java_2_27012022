package lesson6;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class Client implements Informative {
    boolean networkProblem, networkError;

    DataInputStream in;
    DataOutputStream out;

    // последнее пришедшее сообщение/команда от сервера
    String lastOutput;

    AtomicBoolean serverActive, clientActive;

    // общий цикл работы: ожидание сообщений от сервера, их вывод в консоли
    // с возможностью набора и отправки сообщений ему в ней же - два потока
    public Client (String srvAddr, int srvPort) {
        // все ресурсы в try() имеют "область действительности" только внутри try:
        // при их передаче потоку они не инициализированы, в частности, сокет - не открыт
        try {
            Socket socket = new Socket(srvAddr, srvPort);
            System.out.println("Установлено соединение с сервером");
            serverActive = new AtomicBoolean(true);
            clientActive = new AtomicBoolean(true);

            // поток чтения сообщений от сервера
            Thread reader = new Thread(() -> {
                try {
                    in = new DataInputStream(socket.getInputStream());
                    while (serverActive.get() && clientActive.get()) {
                        String str = null, s;
                        try {
                            str = in.readUTF();
                        } catch (IOException ex) {
                            if (clientActive.get()) setupNetworkError(ERR_SERVER_CONNECTION);
                        }
                        boolean active = str != null && !str.equalsIgnoreCase(LOGOUT);
                        if (lastOutput == null || nonEmpty(str) || nonEmpty(lastOutput) || !active)
                            s = "[" +
                                new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date())
                                + "] сервер" + (active ? ": \"" + str + "\"" : " покинул чат") + "\n";
                        else
                            s = str + "\n";
                        printMessage(s, false);
                        lastOutput = str;
                        serverActive.set(active);
                    }
                } catch (IOException ex) {
                    if (clientActive.get()) setupNetworkError(ERR_SERVER_CONNECTION);
                } finally {
                    try {
                        socket.close();
                    } catch (IOException ex) {
                        setupNetworkError(ERR_CLOSE_SOCKET);
                    }
                }
                clearNetworkProblem();
            });
            reader.start(); // поток чтения сообщений от сервера

            // отправку сообщений серверу нет смысла выделять в отдельный поток:
            // пользователь находится в чате и как только он решит выйти из него,
            // работа клиента должна быть завершена
            try {
                out = new DataOutputStream(socket.getOutputStream());
                Scanner sc = new Scanner(System.in);
                while (serverActive.get() && clientActive.get()) {
                    printMessage("Сообщение для сервера (" + LOGOUT + " для выхода): ", true);
                    String str = sc.nextLine();
                    if (str.equalsIgnoreCase(LOGOUT)) {
                        printMessage("Произведен выход из чата\n", true);
                        printMessage("", false);
                        clientActive.set(false);
                    }
                    out.writeUTF(str);
                }
                // обработать ошибки отправки сообщения
            } catch (IOException ex) {
                if (serverActive.get() && clientActive.get()) setupNetworkError(ERR_SERVER_CONNECTION);
            } finally {
                // попадание сюда произойдет при выходе пользователя из чата:
                // поток чтения можно прервать, сокет - закрыть
                try {
                    reader.interrupt();
                    socket.close();
                } catch (IOException ex) {
                    setupNetworkError(ERR_CLOSE_SOCKET);
                }
            }
            clearNetworkProblem();
        // не удалось создать сокет: сервер не работает
        } catch (IOException ex) {
            setupNetworkError(ERR_SERVER_CONNECTION);
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
    public void setupNetworkError (String msg, Integer val) {}
    public void clearNetworkProblem () {
        if (!networkError) networkProblem = false;
    }
}