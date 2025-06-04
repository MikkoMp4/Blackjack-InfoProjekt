package com.mtg.blackjack;

import java.util.*;

public class logic {
    private int playerBalance = 100;
    private int currentBet = 1;
    private final Scanner scanner = new Scanner(System.in);
    private List<Integer> deck;

    public void startGame() {
        System.out.println("Willkommen beim Jackblack-Spiel!");

        while (playerBalance > 0) {
            resetDeck();

            System.out.println("\n--------------------------------");
            System.out.println("Aktuelles Guthaben: $" + playerBalance);
            System.out.println("Aktueller Einsatz: $" + currentBet);
            System.out.println("Drücke Enter, um die Runde zu starten...");
            scanner.nextLine();

            int roundResult = playRound();

            if (roundResult == 1) {
                System.out.println("Du hast gewonnen!");
                playerBalance += currentBet;
            } else if (roundResult == 2) {
                System.out.println("Blackjack! Du gewinnst 1.5x Einsatz!");
                playerBalance += (int)(currentBet * 1.5);
            } else {
                System.out.println("Du hast verloren.");
                playerBalance -= currentBet;
            }

            if (playerBalance <= 0) {
                System.out.println("\nGame Over. Du hast kein Geld mehr.");
                break;
            }

            currentBet *= 2;
            if (currentBet > playerBalance) {
                currentBet = playerBalance;
            }
        }
        scanner.close();
    }

    private void resetDeck() {
        deck = new ArrayList<>();
        // Karten 2-10 jeweils 4-mal, 10 zusätzlich für Bube, Dame, König
        for (int i = 2; i <= 10; i++) {
            for (int j = 0; j < (i == 10 ? 16 : 4); j++) {
                deck.add(i);
            }
        }
        // Asse (11) viermal
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
        List<Integer> playerHand = new ArrayList<>();
        List<Integer> dealerHand = new ArrayList<>();

        playerHand.add(drawCard());
        playerHand.add(drawCard());
        dealerHand.add(drawCard());
        dealerHand.add(drawCard());

        int playerSum = sumHand(playerHand);
        int dealerSum = sumHand(dealerHand);

        System.out.println("Deine Karten: " + playerHand + " (Summe: " + playerSum + ")");
        System.out.println("Karte des Dealers: [" + dealerHand.get(0) + ", ?]");

        if (playerSum == 21 && playerHand.size() == 2) {
            return 2; // Blackjack
        }

        while (true) {
            System.out.println("Möchtest du eine weitere Karte? (j/n)");
            String input = scanner.nextLine();
            if (!input.equalsIgnoreCase("j")) break;
            playerHand.add(drawCard());
            playerSum = sumHand(playerHand);
            System.out.println("Deine Karten: " + playerHand + " (Summe: " + playerSum + ")");
            if (playerSum > 21) {
                System.out.println("Du hast dich überkauft!");
                return 0;
            }
        }

        System.out.println("Karten des Dealers: " + dealerHand + " (Summe: " + dealerSum + ")");
        while (dealerSum < 17) {
            dealerHand.add(drawCard());
            dealerSum = sumHand(dealerHand);
            System.out.println("Dealer zieht eine Karte: " + dealerHand.get(dealerHand.size() - 1));
            System.out.println("Karten des Dealers: " + dealerHand + " (Summe: " + dealerSum + ")");
        }

        playerSum = sumHand(playerHand); // nach Spieler-Zug neu berechnen

        if (dealerSum > 21 || playerSum > dealerSum) return 1;
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

    public static void main(String[] args) {
        logic game = new logic();
        game.startGame();
    }
}
