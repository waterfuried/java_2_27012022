package lesson7;

/*
  1. Разобраться с кодом
  2. * Реализовать личные сообщения, если клиент пишет «/w nick3 Привет, как дела?»,
       то только клиенту с ником nick3 и отправителю должно прийти сообщение «Привет, как дела?»
*/
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        primaryStage.setTitle("Chatty");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
