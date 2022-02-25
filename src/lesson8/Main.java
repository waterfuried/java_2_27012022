package lesson8;

/*
1. Разобраться с кодом.
2. Добавить отключение неавторизованных пользователей по таймауту
   (120 сек. ждём после подключения клиента, и если он не авторизовался
   за это время, закрываем соединение).
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