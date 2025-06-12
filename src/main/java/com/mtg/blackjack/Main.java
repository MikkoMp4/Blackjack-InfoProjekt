package com.mtg.blackjack;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/com/mtg/blackjack/View/StartScreen.fxml"));
        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Jack Black");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
