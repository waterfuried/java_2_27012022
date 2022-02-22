package lesson7;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import javafx.stage.Stage;

import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class Controller implements Initializable {
    @FXML HBox msgPanel;
    @FXML TextField textField;
    @FXML TextArea textArea;

    @FXML HBox authPanel;
    @FXML TextField loginField;
    @FXML PasswordField passwordField;

    private Socket socket;
    private static final int PORT = 8189;
    private static final String ADDRESS = "localhost";

    private DataInputStream in;
    private DataOutputStream out;

    private boolean authorized;
    private String nickname;
    private Stage stage;

    public void changeUserState(boolean authorized) {
        this.authorized = authorized;
        authPanel.setVisible(!authorized);
        authPanel.setManaged(!authorized);
        msgPanel.setVisible(authorized);
        msgPanel.setManaged(authorized);

        if (!authorized) {
            nickname = "";
        }

        textArea.clear();
        setTitle(nickname);
    }

    @Override
    public void initialize (URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            stage = (Stage) textField.getScene().getWindow();
            stage.setOnCloseRequest(event -> {
                if (socket != null && !socket.isClosed()) {
                    try {
                        out.writeUTF("/end");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        });
        changeUserState(false);
    }

    // поскольку в методе запускается поток аутентификации (который может
    // продолжаться сколько угодно), чтобы метод возвращал, например,
    // boolean, корректнее дожидаться завершения потока
    private void connect() {
        try {
            socket = new Socket(ADDRESS, PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
                try {
                    //цикл аутентификации
                    while (true) {
                        // входящий поток с сервера
                        String str = in.readUTF();

                        if (str.startsWith("/")) {
                            if (str.equals("/end")) {
                                break;
                            }
                            if (str.startsWith("/auth_ok")) {
                                nickname = str.split(" ")[1];
                                changeUserState(true);
                                break;
                            }
                        } else {
                            textArea.appendText(str + "\n");
                        }
                    }
                    //цикл работы
                    while (authorized) {
                        String str = in.readUTF();

                        if (str.equals("/end")) {
                            break;
                        }

                        textArea.appendText(str + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    changeUserState(false);
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        } catch (Exception e) {
            // если сервер не запущен, будет выброшено ConnectException: "Connection refused"
            textArea.appendText("Нет связи с сервером\n");
            //e.printStackTrace();
        }
    }

    @FXML
    public void sendMsg(ActionEvent actionEvent) {
        try {
            out.writeUTF(textField.getText());
            textField.clear();
            textField.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void authorize (ActionEvent actionEvent) {
        // при попытках входа по нажатию enter установить фокус на незаполненное поле
        if (loginField.getText().trim().length() == 0) {
            loginField.requestFocus();
            return;
        }
        if (passwordField.getText().trim().length() == 0) {
            passwordField.requestFocus();
            return;
        }
        if (socket == null || socket.isClosed()) {
            connect();
        }
        String msg = String.format("/auth %s %s", loginField.getText().trim(), passwordField.getText().trim());
        passwordField.clear();

        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setTitle (String nickname) {
        Platform.runLater(() -> {
            String title = "Chatty";
            if (nickname != null && nickname.length() > 0) title += " [ " + nickname + " ]";
            stage.setTitle(title);
        });
    }
}