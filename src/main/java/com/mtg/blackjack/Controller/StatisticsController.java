package com.mtg.blackjack.Controller;

import com.mtg.blackjack.Model.HighScore;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Map;

/**
 * Controller für die Statistik-Seite
 */
public class StatisticsController {
    @FXML
    private Label highestBalanceLabel;

    @FXML
    private Label gamesPlayedLabel;

    @FXML
    private Label gamesWonLabel;

    @FXML
    private Label winRateLabel;

    @FXML
    private Label blackjacksLabel;

    @FXML
    private VBox achievementsBox;

    @FXML
    private Label noAchievementsLabel;

    private HighScore highScore;

    /**
     * Initialisiert den StatisticsController und lädt die Highscores
     */
    @FXML
    public void initialize() {
        // Lädt die Highscores aus der Datei
        highScore = HighScore.loadHighScores();

        // Updatet die Anzeige der Statistiken mit den geladenen Highscores
        updateStatisticsDisplay();
    }

    /**
     * Updatet die Anzeige der Statistiken
     */
    private void updateStatisticsDisplay() {
        // Nummern formatieren
        DecimalFormat df = new DecimalFormat("#.##");

        // Labels aktualisieren
        highestBalanceLabel.setText(String.valueOf(highScore.getHighestBalance()));
        gamesPlayedLabel.setText(String.valueOf(highScore.getGamesPlayed()));
        gamesWonLabel.setText(String.valueOf(highScore.getGamesWon()));
        winRateLabel.setText(df.format(highScore.getWinRate()) + "%");
        blackjacksLabel.setText(String.valueOf(highScore.getBlackjacksAchieved()));

        // achievements anzeigen
        Map<String, Integer> achievements = highScore.getAchievements();
        if (achievements.isEmpty()) {
            noAchievementsLabel.setVisible(true);
        } else {
            noAchievementsLabel.setVisible(false);
            achievementsBox.getChildren().clear();

            for (Map.Entry<String, Integer> entry : achievements.entrySet()) {
                Label achievementLabel = new Label(entry.getKey() + ": " + entry.getValue());
                achievementLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
                achievementsBox.getChildren().add(achievementLabel);
            }
        }
    }

    /**
     * Handelt den "Back to Game" button click
     */
    @FXML
    public void onBackToGameClicked() {
        try {
            // Lädt die Game-Szene
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mtg/blackjack/View/Game.fxml"));
            Parent root = loader.load();

            // die aktuelle Stage getten
            Stage stage = (Stage) highestBalanceLabel.getScene().getWindow();

            // Setzt die Game-Szene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            stage.setMaximized(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handelt den "Back to Menu" button click
     */
    @FXML
    public void onBackToMenuClicked() {
        try {
            // Lädt die Start-Szene
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mtg/blackjack/View/StartScreen.fxml"));
            Parent root = loader.load();

            // Getted die aktuelle Stage
            Stage stage = (Stage) highestBalanceLabel.getScene().getWindow();

            // Setzt die Start-Szene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            stage.setMaximized(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
