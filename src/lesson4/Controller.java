package lesson4;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML public TextField myText;
    @FXML public TextArea chatText;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // при запуске переключить фокус на поле ввода текста
        Platform.runLater(() -> myText.requestFocus());
    }

    boolean isEmpty (String s) {
        return s == null || s.replaceAll("\\s", "").length() == 0;
    }

    String lastInput;

    @FXML
    public void sendMsg(ActionEvent actionEvent) {
        // для нового сообщения в чате указывать время
        if (lastInput == null || !isEmpty(myText.getText()))
            chatText.appendText('[' +
                new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date())
                + "]: ");
        lastInput = myText.getText();
        chatText.appendText(lastInput + "\n");
        myText.clear();
        myText.requestFocus();
    }
}