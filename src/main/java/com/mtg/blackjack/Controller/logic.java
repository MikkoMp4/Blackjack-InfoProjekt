package com.mtg.blackjack.Controller;

import com.mtg.blackjack.Model.Player; import java.util.*;

public class logic { 
    private final Scanner scanner = new Scanner(System.in);
    private List<Integer> deck; 
    private Player player; 
    private int currentBet = 1;

    public static void main(String[] args) {
        logic gameLogic = new logic(); // Erstelle eine Instanz der Klasse
        gameLogic.startGame();         // Starte das Spiel
    }

public void startGame() {
    player = new Player(100); // Initiales Guthaben des Spielers
    System.out.println("Willkommen beim Jackblack-Spiel!");

    while (player.getBalance() > 0) {
        resetDeck();
        player.resetHand();

        clearConsole();
        System.out.println("\n--------------------------------");
        System.out.println("Aktuelles Guthaben: $" + player.getBalance());
        System.out.println("Aktueller Einsatz: $" + currentBet);
        System.out.println("Drücke Enter, um die Runde zu starten...");
        scanner.nextLine();

        int roundResult = playRound();

        switch (roundResult) {
            case 1 -> {
                System.out.println("Du hast gewonnen!");
                player.adjustBalance(currentBet);
            }
            case 2 -> {
                System.out.println("Blackjack! Du gewinnst 1.5x Einsatz!");
                player.adjustBalance((int) (currentBet * 1.5));
            }
            case 3 -> {
                System.out.println("Unentschieden – dein Einsatz wird zurückerstattet.");
            }
            default -> {
                System.out.println("Du hast verloren.");
                player.adjustBalance(-currentBet);
            }
        }

        if (player.getBalance() <= 0) {
            System.out.println("\nGame Over. Du hast kein Geld mehr.");
            break;
        }

        System.out.println("Drücke Enter, um fortzufahren...");
        scanner.nextLine();
        clearConsole();

        currentBet *= 2;
        if (currentBet > player.getBalance()) {
            currentBet = player.getBalance();
        }
    }
    scanner.close();
}

private void resetDeck() {
    deck = new ArrayList<>();
    for (int i = 2; i <= 10; i++) {
        for (int j = 0; j < (i == 10 ? 16 : 4); j++) {
            deck.add(i);
        }
    }
    for (int j = 0; j < 4; j++) {
        deck.add(11);
    }
    Collections.shuffle(deck);
}

private int drawCard() {
    if (deck.isEmpty()) resetDeck();
    return deck.remove(0);
}

private int playRound() {
    player.addCardToHand(drawCard());
    player.addCardToHand(drawCard());
    List<Integer> dealerHand = new ArrayList<>();
    dealerHand.add(drawCard());
    dealerHand.add(drawCard());

    int playerSum = player.calculateHandSum();

    while (true) {
        clearConsole();

        System.out.println("Karte des Dealers: [" + dealerHand.get(0) + ", ?]");
        System.out.println("Deine Karten: " + player.getHand() + " (Summe: " + playerSum + ")");

        if (playerSum == 21 && player.getHand().size() == 2) {
            return 2; // Blackjack
        }

        System.out.println("Möchtest du eine weitere Karte? (j/n)");
        String input = scanner.nextLine();
        if (!input.equalsIgnoreCase("j")) break;
        player.addCardToHand(drawCard());
        playerSum = player.calculateHandSum();
        if (playerSum > 21) {
            clearConsole();
            System.out.println("Deine Karten: " + player.getHand() + " (Summe: " + playerSum + ")");
            System.out.println("Du hast dich überkauft!");
            return 0;
        }
    }

    clearConsole();
    System.out.println("Deine Karten: " + player.getHand() + " (Summe: " + playerSum + ")");
    System.out.println("Karten des Dealers: " + dealerHand + " (Summe: " + sumHand(dealerHand) + ")");
    while (sumHand(dealerHand) < 17) {
        dealerHand.add(drawCard());
        System.out.println("Dealer zieht eine Karte: " + dealerHand.get(dealerHand.size() - 1));
        System.out.println("Karten des Dealers: " + dealerHand + " (Summe: " + sumHand(dealerHand) + ")");
        Warten(2000);
    }

    int dealerSum = sumHand(dealerHand);
    playerSum = player.calculateHandSum();

    if (dealerSum > 21 || playerSum > dealerSum) return 1;
    if (dealerSum == playerSum) return 3;
    return 0;
}

private int sumHand(List<Integer> hand) {
    int sum = 0;
    int aces = 0;
    for (int card : hand) {
        sum += card;
        if (card == 11) aces++;
    }
    while (sum > 21 && aces > 0) {
        sum -= 10;
        aces--;
    }
    return sum;
}
    private void clearConsole() {
        try {
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            for (int i = 0; i < 50; i++) System.out.println();
        }
    }

    private void Warten(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}