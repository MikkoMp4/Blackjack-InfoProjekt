package com.mtg.blackjack.Controller;

import com.mtg.blackjack.Model.HighScore;
import com.mtg.blackjack.Model.Player;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameController {

    @FXML private Label balanceLabel;
    @FXML private Label betLabel;
    @FXML private Label roundLabel;
    @FXML private HBox dealerCardsBox;
    @FXML private Label dealerSumLabel;
    @FXML private HBox playerCardsBox;
    @FXML private Label playerSumLabel;
    @FXML private Label gameStatusLabel;
    @FXML private Button hitButton;
    @FXML private Button standButton;
    @FXML private Button nextRoundButton;
    @FXML private VBox mainContainer;
    @FXML private VBox sidebar;
    @FXML private StackPane imageStack;
    @FXML private VBox blueBox; 
    @FXML private ImageView redRectImage;
    @FXML private ImageView personImage;
    @FXML private Label dialogLabel;
    @FXML private Pane blackOverlay;


    private logic gameLogic;
    private Player player;
    private List<Integer> dealerHand;
    private int currentBet = 1;
    private int round = 1;
    private boolean gameInProgress = false;
    private boolean dealerTurn = false;
    private HighScore highScore;
    private Timeline talkingTimeline;
    private boolean endlessMode = false;
    private MediaPlayer backgroundPlayer;
    

    

    @FXML
    public void initialize() {
        // Initialisiert die Spiel-Logik und den Spieler
        gameLogic = new logic();
        player = new Player(100);
        dealerHand = new ArrayList<>();

        // Overlay darf am Anfang keine Klicks blockieren!
         blackOverlay.setMouseTransparent(true);

        // Laden von high scores
        highScore = HighScore.loadHighScores();

        // wendet UI improvements an
        applyUIEffects();

        playBackgroundMusic();

        // Starten des Spiels
        updateBalanceDisplay();
        updateBetDisplay();

        redRectImage.setImage(new Image(getClass().getResourceAsStream("/img/RotesRechteck3.png")));
        personImage.setImage(new Image(getClass().getResourceAsStream("/img/characters/Person1.png")));

        //showIntroMessages();
    }

        public void showIntroMessages() {
        List<String> messages = new ArrayList<>();
        messages.add("Welcome to Blackjack!");
        messages.add("You start with 100 Dollars.");
        if (!endlessMode) {
            messages.add("Earn 200 Dollars to win.");
        }
        messages.add("Bets are doubled after each round.");
        messages.add("I wish you good luck.");

        showMessagesSequentially(messages.toArray(new String[0]), 0);
        
    }

   private void showMessagesSequentially(String[] messages, int index) {
        if (talkingTimeline != null) {
            talkingTimeline.stop();
        }

        if (index >= messages.length) {
            personImage.setImage(new Image(getClass().getResourceAsStream("/img/characters/Person1.png")));
            dialog("");
            showPerson2ThenStartRound();
            return;
        }

        dialog(messages[index]);

        // Animation: alle 0,2s Bild wechseln
        talkingTimeline = new Timeline();
        final boolean[] toggle = {false};
        talkingTimeline.getKeyFrames().add(
            new KeyFrame(Duration.seconds(0.2), e -> {
                if (toggle[0]) {
                    personImage.setImage(new Image(getClass().getResourceAsStream("/img/characters/Person1.png")));
                } else {
                    personImage.setImage(new Image(getClass().getResourceAsStream("/img/characters/Person3.png")));
                }
                toggle[0] = !toggle[0];
            })
        );
        talkingTimeline.setCycleCount(Timeline.INDEFINITE);
        talkingTimeline.play();

        // Nach 3 Sekunden nächste Nachricht und Animation stoppen
        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(e -> {
            talkingTimeline.stop();
            personImage.setImage(new Image(getClass().getResourceAsStream("/img/characters/Person1.png")));
            showMessagesSequentially(messages, index + 1);
        });
        pause.play();
    }

    //Karten austeilen Animation
    private void showPerson2ThenStartRound() {
    // Stoppe alle laufenden Animationen, die das Bild verändern könnten
    if (talkingTimeline != null) {
        talkingTimeline.stop();
    }

    playShuffleSound();

    personImage.setImage(new Image(getClass().getResourceAsStream("/img/characters/Person2.png")));
    PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
    pause.setOnFinished(e -> {
        personImage.setImage(new Image(getClass().getResourceAsStream("/img/characters/Person1.png")));
        startNewRound();
    });
    pause.play();
    }

    private void animatePersonImage(int repeats) {
    // repeats = wie oft Person3/Person1 abwechselnd gezeigt wird, immer mit 0,2s Abstand
    Timeline timeline = new Timeline();
    for (int i = 0; i < repeats * 2; i++) {
        final int idx = i;
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(0.2 * i), e -> {
            if (idx % 2 == 0) {
                personImage.setImage(new Image(getClass().getResourceAsStream("/img/characters/Person3.png")));
            } else {
                personImage.setImage(new Image(getClass().getResourceAsStream("/img/characters/Person1.png")));
            }
        }));
    }
    // Am Ende wieder Person1 setzen
    timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(0.2 * repeats * 2), e -> {
        personImage.setImage(new Image(getClass().getResourceAsStream("/img/characters/Person1.png")));
    }));
    timeline.play();
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

    public void setEndlessMode(boolean endlessMode) {
    this.endlessMode = endlessMode;
    showIntroMessages(); // <-- HIER aufrufen!
    }


    private void startNewRound() {
        // Reset spiel state
        gameInProgress = true;
        dealerTurn = false;
        dialogLabel.setText("");
        roundLabel.setText(String.valueOf(round));

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
            dialogLabel.setText("Blackjack! You win 1.5x your bet!");
            animateGameStatus();
            animatePersonImage(2);

            // Reichtum update
            player.adjustBalance((int) (currentBet * 1.5));
            updateBalanceDisplay();

            // Stats update
            highScore.incrementGamesPlayed();
            highScore.incrementGamesWon();
            highScore.incrementBlackjacks();
            highScore.updateHighestBalance(player.getBalance());
            highScore.saveHighScores();

            handleWinCondition();

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
        playCardSound();

        // Checken ob der Spieler besser nie nach Vegas fliegen sollte (checkt ob der Spieler über 21 ist)
        int playerSum = player.calculateHandSum();
        if (playerSum > 21) {
            dialogLabel.setText("Bust! You lose.");
            animateGameStatus();
            animatePersonImage(2);

            // Updated DEN REICHTUM DES SPIELERS
            player.adjustBalance(-currentBet);
            updateBalanceDisplay();

            // Updated die Statistik
            highScore.incrementGamesPlayed();
            highScore.updateHighestBalance(player.getBalance());
            highScore.saveHighScores();

             handleWinCondition();

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

        dealerDrawCardsAnimated();
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
            dialogLabel.setText("Too bad! You're out of money.");
            hitButton.setDisable(true);
            standButton.setDisable(true);
            nextRoundButton.setDisable(true);
            animatePersonImage(3);
            PauseTransition wait = new PauseTransition(Duration.seconds(1.2));
            wait.setOnFinished(event -> showBankruptOverlay());
            wait.play();
            return;
        }

        round++;
        showPerson2ThenStartRound();
    }

    @FXML
    private void onBackToMenuClicked() {
        try {
            // Lädt die Start-Szene
            if (backgroundPlayer != null) {
                backgroundPlayer.stop();
            }
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

    public void warten(int m) {
        try {
            Thread.sleep(m);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void dialog(String message) {
        dialogLabel.setText(message);
        PauseTransition pause = new PauseTransition(Duration.millis(message.length() * 60));
        pause.setOnFinished(e -> {
            // Hier kannst du nach der Pause etwas machen, falls nötig
        });
        pause.play();
    }

    private void showBankruptOverlay() {
        // Zeige Person4 für 2 Sekunden
        personImage.setImage(new Image(getClass().getResourceAsStream("/img/characters/Person4.png")));

        PauseTransition showPerson4 = new PauseTransition(Duration.seconds(3));
        
        showPerson4.setOnFinished(ev -> {
            // Jetzt Overlay sichtbar machen und Klicks blockieren
            blackOverlay.setOpacity(1.0);

            if (backgroundPlayer != null) {
                backgroundPlayer.stop();
            }
            playPistolSound();

            blackOverlay.setMouseTransparent(false);
            blackOverlay.toFront();

            // Nach 1 Sekunde: Fade-Out starten
            PauseTransition wait = new PauseTransition(Duration.seconds(1));
            wait.setOnFinished(e -> {
                FadeTransition fade = new FadeTransition(Duration.seconds(3), blackOverlay);
                fade.setFromValue(1.0);
                fade.setToValue(0.0);
                fade.setOnFinished(ev2 -> {
                    blackOverlay.setMouseTransparent(true); // Klicks wieder erlauben
                    blackOverlay.toBack();
                    // Optional: Person1 wieder anzeigen
                    personImage.setImage(new Image(getClass().getResourceAsStream("/img/characters/Person1.png")));
                });
                fade.play();
            });
            wait.play();
        });
        showPerson4.play();
    }

    private void handleWinCondition() {
    if (!endlessMode && player.getBalance() >= 200) {
        dialogLabel.setText("Congratulations for winning 200$!");
        personImage.setImage(new Image(getClass().getResourceAsStream("/img/characters/Person5.png")));
        hitButton.setDisable(true);
        standButton.setDisable(true);
        nextRoundButton.setDisable(true);
        gameInProgress = false;
    }
    }

    private void dealerDrawCardsAnimated() {
    dealerDrawCardsAnimatedInternal();
}

    private void dealerDrawCardsAnimatedInternal() {
        int dealerSum = gameLogic.sumHand(dealerHand);
        if (dealerSum < 17) {
            // Wenn die nächste Karte die dritte ist (also schon 2 Karten da sind)
             Duration delay = dealerHand.size() >= 2 ? Duration.seconds(1) : Duration.ZERO;
            // Erklärung: dealerHand.size()==2 -> 2 Karten liegen, die nächste ist die dritte

            PauseTransition pause = new PauseTransition(delay);
            pause.setOnFinished(e -> {
                dealerHand.add(gameLogic.drawCard());
                updateDealerCards(false);
                playCardSound();
                dealerDrawCardsAnimatedInternal();
            });
            pause.play();
        } else {
            // Nach dem Ziehen: Gewinner bestimmen und Runde beenden
            int playerSum = player.calculateHandSum();
            int dealerSumFinal = gameLogic.sumHand(dealerHand);
            boolean playerWon = false;

            if (dealerSumFinal > 21 || playerSum > dealerSumFinal) {
                dialogLabel.setText("You win!");
                animateGameStatus();
                animatePersonImage(2);
                player.adjustBalance(currentBet);
                playerWon = true;

                handleWinCondition();
            } else if (dealerSumFinal == playerSum) {
                dialogLabel.setText("Push - it's a tie!");
                animateGameStatus();
                animatePersonImage(2);
            } else {
                dialogLabel.setText("Dealer wins!");
                animateGameStatus();
                animatePersonImage(2);
                player.adjustBalance(-currentBet);
            }

            // Statistik updaten
            highScore.incrementGamesPlayed();
            if (playerWon) {
                highScore.incrementGamesWon();
            }
            highScore.updateHighestBalance(player.getBalance());
            highScore.saveHighScores();

            updateBalanceDisplay();
            endRound();
        }
    }

    public void setHighScore(HighScore highScore) {
        this.highScore = highScore;
    }

    public HighScore getHighScore() {
        return highScore;
    }

    private void playShuffleSound() {
    try {
        AudioClip shuffleSound = new AudioClip(getClass().getResource("/sounds/shuffling.mp3").toExternalForm());
        shuffleSound.setVolume(0.8);
        shuffleSound.play();
    } catch (Exception e) {
        System.err.println("Shuffle sound could not be played: " + e.getMessage());
    }
    }
    private void playPistolSound() {
    try {
        AudioClip pistolSound = new AudioClip(getClass().getResource("/sounds/pistol.mp3").toExternalForm());
        pistolSound.play();
    } catch (Exception e) {
        System.err.println("Pistol sound could not be played: " + e.getMessage());
    }
    }

    private void playCardSound() {
    try {
        AudioClip cardSound = new AudioClip(getClass().getResource("/sounds/card.mp3").toExternalForm());
        cardSound.setVolume(0.3);
        if (!gameInProgress) return;
        cardSound.play();
    } catch (Exception e) {
        System.err.println("Card sound could not be played: " + e.getMessage());
    }
    }

    private void playBackgroundMusic() {
    try {
        if (backgroundPlayer != null) {
            backgroundPlayer.stop();
        }
        Media media = new Media(getClass().getResource("/sounds/Jackblack21.mp3").toExternalForm());
        backgroundPlayer = new MediaPlayer(media);
        backgroundPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Dauerschleife
        backgroundPlayer.setVolume(0.3); // ggf. anpassen
        backgroundPlayer.play();
    } catch (Exception e) {
        System.err.println("Background music could not be played: " + e.getMessage());
    }
}
}
