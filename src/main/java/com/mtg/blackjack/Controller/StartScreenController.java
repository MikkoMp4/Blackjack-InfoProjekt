package com.mtg.blackjack.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
public class StartScreenController {

    @FXML private VBox rootVBox;  // das ist unser geankertes VBox
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
        // TODO: Endlosmodus starten
    }

    @FXML
    private void onNormalClicked() {
        System.out.println("Normaler Modus gestartet");
        // TODO: Normalmodus starten
    }
}
