package com.mtg.blackjack.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
public class StartScreenController {

    @FXML private VBox rootVBox;
    @FXML private ImageView logoView;
    @FXML private HBox buttonBox;
    @FXML private Button endlessButton;
    @FXML private Button normalButton;

    @FXML
    public void initialize() {
        // Logo laden
        logoView.setImage(new Image(getClass().getResourceAsStream("/img/Logo.png")));

        // Logo auf 50% der Fensterbreite skalieren
        logoView.fitWidthProperty()
                .bind(rootVBox.widthProperty().multiply(0.5));

        // Buttons teilen die HBox-Breite gleich auf (abzgl. spacing)
        endlessButton.prefWidthProperty()
                .bind(
                        buttonBox.widthProperty()
                                .subtract(buttonBox.spacingProperty())
                                .divide(2)
                );
        normalButton.prefWidthProperty()
                .bind(endlessButton.prefWidthProperty());
    }

    @FXML
    private void onEndlessClicked() {
        System.out.println("Endlos-Modus gestartet");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mtg/blackjack/View/Game.fxml"));
            Parent gameRoot = loader.load();
            GameController controller = loader.getController();
            controller.setEndlessMode(true); // Endlos-Modus aktivieren

            Stage stage = (Stage) endlessButton.getScene().getWindow();
            stage.setScene(new Scene(gameRoot, 800, 600));
            stage.show();
            stage.setMaximized(true);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading Game.fxml: " + e.getMessage());
        }
    }

    @FXML
    private void onNormalClicked() {
        System.out.println("Normaler Modus gestartet");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mtg/blackjack/View/Game.fxml"));
            Parent gameRoot = loader.load();
            GameController controller = loader.getController();
            controller.setEndlessMode(false); // Normal-Modus

            Stage stage = (Stage) normalButton.getScene().getWindow();
            stage.setScene(new Scene(gameRoot, 800, 600));
            stage.show();
            stage.setMaximized(true);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading Game.fxml: " + e.getMessage());
        }
    }
}
