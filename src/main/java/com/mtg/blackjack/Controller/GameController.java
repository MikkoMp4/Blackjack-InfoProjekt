package com.mtg.blackjack.Controller;

import com.mtg.blackjack.Model.HighScore;
import com.mtg.blackjack.Model.Player;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameController {

    @FXML private Label balanceLabel;
    @FXML private Label betLabel;
    @FXML private HBox dealerCardsBox;
    @FXML private Label dealerSumLabel;
    @FXML private HBox playerCardsBox;
    @FXML private Label playerSumLabel;
    @FXML private Label gameStatusLabel;
    @FXML private Button hitButton;
    @FXML private Button standButton;
    @FXML private Button nextRoundButton;
    @FXML private VBox mainContainer;
    @FXML private ImageView redRectImage;

    private logic gameLogic;
    private Player player;
    private List<Integer> dealerHand;
    private int currentBet = 1;
    private boolean gameInProgress = false;
    private boolean dealerTurn = false;
    private HighScore highScore;

    @FXML
    public void initialize() {
        // Initialisiert die Spiel-Logik und den Spieler
        gameLogic = new logic();
        player = new Player(100);
        dealerHand = new ArrayList<>();

        // Laden von high scores
        highScore = HighScore.loadHighScores();

        // wendet UI improvements an
        applyUIEffects();

        // Starten des Spiels
        updateBalanceDisplay();
        updateBetDisplay();
        startNewRound();
        
        redRectImage.setImage(new Image(getClass().getResourceAsStream("/img/RotesRechteck3.png")));

    }

    /**
     * Wendet UI-Effekte an, um das Spiel visuell ansprechender zu gestalten
     */
    private void applyUIEffects() {
        // Drop shadow für die Kartenboxen
        DropShadow cardShadow = new DropShadow();
        cardShadow.setColor(Color.BLACK);
        cardShadow.setRadius(10);
        dealerCardsBox.setEffect(cardShadow);
        playerCardsBox.setEffect(cardShadow);

        // Style für die Buttons
        for (Button button : new Button[]{hitButton, standButton, nextRoundButton}) {
            button.setStyle(button.getStyle() + "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);");

            // Hover effekt hinzufügen
            button.setOnMouseEntered(e -> {
                ScaleTransition st = new ScaleTransition(Duration.millis(100), button);
                st.setToX(1.1);
                st.setToY(1.1);
                st.play();
            });

            button.setOnMouseExited(e -> {
                ScaleTransition st = new ScaleTransition(Duration.millis(100), button);
                st.setToX(1);
                st.setToY(1);
                st.play();
            });
        }

        // style zum gameStatusLabel hinzufügen
        gameStatusLabel.setStyle(gameStatusLabel.getStyle() + "-fx-effect: dropshadow(three-pass-box, rgba(255,255,255,0.5), 10, 0, 0, 0);");
    }

    private void startNewRound() {
        // Reset spiel state
        gameInProgress = true;
        dealerTurn = false;
        gameStatusLabel.setText("");

        // Clear Hands
        player.resetHand();
        dealerHand.clear();

        // Reset UI
        playerCardsBox.getChildren().clear();
        dealerCardsBox.getChildren().clear();

        // Reset buttons
        hitButton.setDisable(false);
        standButton.setDisable(false);
        nextRoundButton.setVisible(false);

        // Dealt die initial Karten
        player.addCardToHand(gameLogic.drawCard());
        player.addCardToHand(gameLogic.drawCard());
        dealerHand.add(gameLogic.drawCard());
        dealerHand.add(gameLogic.drawCard());

        // Update UI
        updatePlayerCards();
        updateDealerCards(true); // Versteckt die zweite Karte des Dealers

        // Für Blackjack checken
        checkForBlackjack();
    }

    private void checkForBlackjack() {
        int playerSum = player.calculateHandSum();

        if (playerSum == 21 && player.getHand().size() == 2) {
            // Zeigen der Blackjack Nachricht mit Animation
            gameStatusLabel.setText("Blackjack! You win 1.5x your bet!");
            animateGameStatus();

            // Reichtum update
            player.adjustBalance((int) (currentBet * 1.5));
            updateBalanceDisplay();

            // Stats update
            highScore.incrementGamesPlayed();
            highScore.incrementGamesWon();
            highScore.incrementBlackjacks();
            highScore.updateHighestBalance(player.getBalance());
            highScore.saveHighScores();

            endRound();
        }
    }

    /**
     * Animiert den Spielstatus-Label, um visuelles Feedback zu geben
     */
    private void animateGameStatus() {
        // Erstellen einer Scale Animation
        ScaleTransition st = new ScaleTransition(Duration.millis(200), gameStatusLabel);
        st.setFromX(1.0);
        st.setFromY(1.0);
        st.setToX(1.2);
        st.setToY(1.2);
        st.setCycleCount(2);
        st.setAutoReverse(true);

        // Fade Animation
        FadeTransition ft = new FadeTransition(Duration.millis(200), gameStatusLabel);
        ft.setFromValue(1.0);
        ft.setToValue(0.7);
        ft.setCycleCount(2);
        ft.setAutoReverse(true);

        // Spielt fancy Animationen ab
        st.play();
        ft.play();
    }

    @FXML
    private void onHitClicked() {
        if (!gameInProgress) return;

        // Spieler zieht eine Karte mit Animation
        int newCard = gameLogic.drawCard();
        player.addCardToHand(newCard);
        updatePlayerCards();
        animateNewCard();

        // Checken ob der Spieler besser nie nach Vegas fliegen sollte (checkt ob der Spieler über 21 ist)
        int playerSum = player.calculateHandSum();
        if (playerSum > 21) {
            gameStatusLabel.setText("Bust! You lose.");
            animateGameStatus();

            // Updated DEN REICHTUM DES SPIELERS
            player.adjustBalance(-currentBet);
            updateBalanceDisplay();

            // Updated die Statistik
            highScore.incrementGamesPlayed();
            highScore.updateHighestBalance(player.getBalance());
            highScore.saveHighScores();

            endRound();
        }
    }

    /**
     * Animiert die neue Karte, die der Spieler gezogen hat
     */
    private void animateNewCard() {
        if (playerCardsBox.getChildren().isEmpty()) return;

        // Getted die letzte Karte im PlayerCardsBox
        ImageView lastCard = (ImageView) playerCardsBox.getChildren().get(playerCardsBox.getChildren().size() - 1);

        // Erstellt und spielt eine ScaleTransition Animation
        ScaleTransition st = new ScaleTransition(Duration.millis(200), lastCard);
        st.setFromX(0.8);
        st.setFromY(0.8);
        st.setToX(1.0);
        st.setToY(1.0);
        st.play();
    }

    @FXML
    private void onStandClicked() {
        if (!gameInProgress) return;

        dealerTurn = true;
        updateDealerCards(false); // Zeigt die zweite Karte des Dealers

        // Dealer zieht Karten bis er mindestens 17 hat
        while (gameLogic.sumHand(dealerHand) < 17) {
            dealerHand.add(gameLogic.drawCard());
            updateDealerCards(false);

            // einen kleinen Delay einfügen, um die Animation zu sehen
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Gewinner bestimmen
        int playerSum = player.calculateHandSum();
        int dealerSum = gameLogic.sumHand(dealerHand);
        boolean playerWon = false;

        if (dealerSum > 21 || playerSum > dealerSum) {
            gameStatusLabel.setText("You win!");
            animateGameStatus();
            player.adjustBalance(currentBet);
            playerWon = true;
        } else if (dealerSum == playerSum) {
            gameStatusLabel.setText("Push - it's a tie!");
            animateGameStatus();
        } else {
            gameStatusLabel.setText("Dealer wins!");
            animateGameStatus();
            player.adjustBalance(-currentBet);
        }

        // Updaten der Statistik
        highScore.incrementGamesPlayed();
        if (playerWon) {
            highScore.incrementGamesWon();
        }
        highScore.updateHighestBalance(player.getBalance());
        highScore.saveHighScores();

        updateBalanceDisplay();
        endRound();
    }

    @FXML
    private void onNextRoundClicked() {
        // Verdopple den aktuellen Einsatz (wenn möglich)
        currentBet *= 2;
        if (currentBet > player.getBalance()) {
            currentBet = player.getBalance();
        }
        updateBetDisplay();

        // Checken ob der Spieler noch Geld hat
        if (player.getBalance() <= 0) {
            gameStatusLabel.setText("Game Over! You're out of money.");
            hitButton.setDisable(true);
            standButton.setDisable(true);
            nextRoundButton.setDisable(true);
            return;
        }

        startNewRound();
    }

    @FXML
    private void onBackToMenuClicked() {
        try {
            // Lädt die Start-Szene
            Parent root = FXMLLoader.load(getClass().getResource("/com/mtg/blackjack/View/StartScreen.fxml"));
            Scene scene = new Scene(root, 800, 600);

            // Getted die aktuelle Stage
            Stage stage = (Stage) balanceLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
            stage.setMaximized(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handelt den "View Statistics" button click
     */
    @FXML
    private void onViewStatisticsClicked() {
        try {
            // Lädt die Statistics-Szene
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mtg/blackjack/View/Statistics.fxml"));
            Parent root = loader.load();

            // Getted die aktuelle Stage
            Stage stage = (Stage) balanceLabel.getScene().getWindow();

            // Setzt die statistics scene
            Scene scene = new Scene(root, 800, 600);
            stage.setScene(scene);
            stage.show();
            stage.setMaximized(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updatePlayerCards() {
        playerCardsBox.getChildren().clear();
        for (int card : player.getHand()) {
            ImageView cardView = createCardImageView(card);
            playerCardsBox.getChildren().add(cardView);
        }
        playerSumLabel.setText("Sum: " + player.calculateHandSum());
    }

    private void updateDealerCards(boolean hideSecondCard) {
        dealerCardsBox.getChildren().clear();

        // Erste Karte wird immer angezeigt
        if (!dealerHand.isEmpty()) {
            ImageView firstCardView = createCardImageView(dealerHand.get(0));
            dealerCardsBox.getChildren().add(firstCardView);
        }

        // Zweite Karte wird vielleicht versteckt
        if (dealerHand.size() > 1) {
            if (hideSecondCard) {
                ImageView hiddenCardView = createCardBackImageView();
                dealerCardsBox.getChildren().add(hiddenCardView);
                dealerSumLabel.setText("Sum: ?");
            } else {
                for (int i = 1; i < dealerHand.size(); i++) {
                    ImageView cardView = createCardImageView(dealerHand.get(i));
                    dealerCardsBox.getChildren().add(cardView);
                }
                dealerSumLabel.setText("Sum: " + gameLogic.sumHand(dealerHand));
            }
        }
    }

    private ImageView createCardImageView(int cardValue) {
        // Erstellt ein ImageView für die Karte
        ImageView cardView = new ImageView();
        cardView.setFitHeight(100);
        cardView.setFitWidth(70);
        cardView.setPreserveRatio(true);

        // Lädt das richtige Kartenbild für den Kartenwert
        String cardName = getCardImageName(cardValue);
        try {
            Image cardImage = new Image(getClass().getResourceAsStream("/img/cards/" + cardName));
            cardView.setImage(cardImage);
        } catch (Exception e) {
            // Falls das Bild nicht geladen werden kann, wird eine einfache Textdarstellung verwendet
            Label cardLabel = new Label(String.valueOf(cardValue));
            cardLabel.setStyle("-fx-background-color: white; -fx-padding: 40 25; -fx-border-color: black;");
            // man kann das Label nicht direkt darstellen, also erstellen wir ein ImageView mit dem Label
        }

        return cardView;
    }

    private String getCardImageName(int cardValue) {
        // Mappen von Kartenwerten zu Bildnamen
        if (cardValue == 11) {
            return "ace_spades.png"; // Piek als Standard-Symbol
        } else {
            return cardValue + "_spades.png"; // Piek als Standard-Symbol
        }
    }

    private ImageView createCardBackImageView() {
        ImageView cardView = new ImageView();
        cardView.setFitHeight(100);
        cardView.setFitWidth(70);
        cardView.setPreserveRatio(true);

        // Lädt das Kartenrückenbild
        try {
            Image cardImage = new Image(getClass().getResourceAsStream("/img/cards/back_black_basic.png"));
            cardView.setImage(cardImage);
        } catch (Exception e) {
            // Falls das Bild nicht geladen werden kann, wird eine einfache Darstellung verwendet
            cardView.setStyle("-fx-background-color: black; -fx-border-color: white;");
        }

        return cardView;
    }

    private void updateBalanceDisplay() {
        balanceLabel.setText(String.valueOf(player.getBalance()));
    }

    private void updateBetDisplay() {
        betLabel.setText(String.valueOf(currentBet));
    }

    private void endRound() {
        gameInProgress = false;
        hitButton.setDisable(true);
        standButton.setDisable(true);
        nextRoundButton.setVisible(true);
    }
}
