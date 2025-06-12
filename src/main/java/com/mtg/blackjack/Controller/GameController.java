package com.mtg.blackjack.Controller;

import com.mtg.blackjack.Model.Player;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

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

    private logic gameLogic;
    private Player player;
    private List<Integer> dealerHand;
    private int currentBet = 1;
    private boolean gameInProgress = false;
    private boolean dealerTurn = false;

    @FXML
    public void initialize() {
        gameLogic = new logic();
        player = new Player(100);
        dealerHand = new ArrayList<>();
        updateBalanceDisplay();
        updateBetDisplay();
        startNewRound();
    }

    private void startNewRound() {
        // Reset game state
        gameInProgress = true;
        dealerTurn = false;
        gameStatusLabel.setText("");

        // Clear hands
        player.resetHand();
        dealerHand.clear();

        // Reset UI
        playerCardsBox.getChildren().clear();
        dealerCardsBox.getChildren().clear();

        // Reset buttons
        hitButton.setDisable(false);
        standButton.setDisable(false);
        nextRoundButton.setVisible(false);

        // Deal initial cards
        player.addCardToHand(gameLogic.drawCard());
        player.addCardToHand(gameLogic.drawCard());
        dealerHand.add(gameLogic.drawCard());
        dealerHand.add(gameLogic.drawCard());

        // Update UI
        updatePlayerCards();
        updateDealerCards(true); // Hide dealer's second card

        // Check for blackjack
        checkForBlackjack();
    }

    private void checkForBlackjack() {
        int playerSum = player.calculateHandSum();

        if (playerSum == 21 && player.getHand().size() == 2) {
            gameStatusLabel.setText("Blackjack! You win 1.5x your bet!");
            player.adjustBalance((int) (currentBet * 1.5));
            updateBalanceDisplay();
            endRound();
        }
    }

    @FXML
    private void onHitClicked() {
        if (!gameInProgress) return;

        // Player draws a card
        player.addCardToHand(gameLogic.drawCard());
        updatePlayerCards();

        // Check if player busts
        int playerSum = player.calculateHandSum();
        if (playerSum > 21) {
            gameStatusLabel.setText("Bust! You lose.");
            player.adjustBalance(-currentBet);
            updateBalanceDisplay();
            endRound();
        }
    }

    @FXML
    private void onStandClicked() {
        if (!gameInProgress) return;

        dealerTurn = true;
        updateDealerCards(false); // Show dealer's hidden card

        // Dealer draws cards until sum is at least 17
        while (gameLogic.sumHand(dealerHand) < 17) {
            dealerHand.add(gameLogic.drawCard());
            updateDealerCards(false);

            // Add a small delay for visual effect
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Determine winner
        int playerSum = player.calculateHandSum();
        int dealerSum = gameLogic.sumHand(dealerHand);

        if (dealerSum > 21 || playerSum > dealerSum) {
            gameStatusLabel.setText("You win!");
            player.adjustBalance(currentBet);
        } else if (dealerSum == playerSum) {
            gameStatusLabel.setText("Push - it's a tie!");
        } else {
            gameStatusLabel.setText("Dealer wins!");
            player.adjustBalance(-currentBet);
        }

        updateBalanceDisplay();
        endRound();
    }

    @FXML
    private void onNextRoundClicked() {
        // Double the bet for the next round (if possible)
        currentBet *= 2;
        if (currentBet > player.getBalance()) {
            currentBet = player.getBalance();
        }
        updateBetDisplay();

        // Check if player has money left
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
            // Load the start screen
            Parent root = FXMLLoader.load(getClass().getResource("/com/mtg/blackjack/View/StartScreen.fxml"));
            Scene scene = new Scene(root, 800, 600);

            // Get the current stage
            Stage stage = (Stage) balanceLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
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

        // First card is always visible
        if (!dealerHand.isEmpty()) {
            ImageView firstCardView = createCardImageView(dealerHand.get(0));
            dealerCardsBox.getChildren().add(firstCardView);
        }

        // Second card might be hidden
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
        // Create a card image view based on the card value
        ImageView cardView = new ImageView();
        cardView.setFitHeight(100);
        cardView.setFitWidth(70);
        cardView.setPreserveRatio(true);

        // Load the appropriate card image based on the card value
        String cardName = getCardImageName(cardValue);
        try {
            Image cardImage = new Image(getClass().getResourceAsStream("/img/cards/" + cardName));
            cardView.setImage(cardImage);
        } catch (Exception e) {
            // If image loading fails, create a simple text representation
            Label cardLabel = new Label(String.valueOf(cardValue));
            cardLabel.setStyle("-fx-background-color: white; -fx-padding: 40 25; -fx-border-color: black;");
            // We can't directly return a Label, so we'll keep the ImageView approach
        }

        return cardView;
    }

    private String getCardImageName(int cardValue) {
        // Map card values to image file names
        if (cardValue == 11) {
            return "ace_spades.png"; // Using spades as default suit
        } else {
            return cardValue + "_spades.png"; // Using spades as default suit
        }
    }

    private ImageView createCardBackImageView() {
        ImageView cardView = new ImageView();
        cardView.setFitHeight(100);
        cardView.setFitWidth(70);
        cardView.setPreserveRatio(true);

        // Load the card back image
        try {
            Image cardImage = new Image(getClass().getResourceAsStream("/img/cards/back_black_basic.png"));
            cardView.setImage(cardImage);
        } catch (Exception e) {
            // If image loading fails, create a simple representation
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
