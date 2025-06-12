package com.mtg.blackjack.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class StartScreenController {

    @FXML
    private Button endlessButton;

    @FXML
    private Button normalButton;

    @FXML
    private void onEndlessClicked(ActionEvent event) {
        System.out.println("Endlos-Modus gestartet");
        // TODO: hier den Endlos-Modus starten
    }

    @FXML
    private void onNormalClicked(ActionEvent event) {
        System.out.println("Normaler Modus gestartet");
        // TODO: hier den Normal-Modus starten
    }
}
