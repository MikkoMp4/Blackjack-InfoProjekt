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

            clearConsole();
            System.out.println("\n--------------------------------");
            System.out.println("Aktuelles Guthaben: $" + playerBalance);
            System.out.println("Aktueller Einsatz: $" + currentBet);
            System.out.println("Drücke Enter, um die Runde zu starten...");
            scanner.nextLine();

            int roundResult = playRound();

            switch (roundResult) {
                case 1 -> {
                    System.out.println("Du hast gewonnen!");
                    playerBalance += currentBet;
                }
                case 2 -> {
                    System.out.println("Blackjack! Du gewinnst 1.5x Einsatz!");
                    playerBalance += (int)(currentBet * 1.5);
                }
                case 3 -> {
                    System.out.println("Unentschieden – dein Einsatz wird zurückerstattet.");
                }
                default -> {
                    System.out.println("Du hast verloren.");
                    playerBalance -= currentBet;
                }
            }

            if (playerBalance <= 0) {
                System.out.println("\nGame Over. Du hast kein Geld mehr.");
                break;
            }

            System.out.println("Drücke Enter, um fortzufahren...");
            scanner.nextLine();
            clearConsole();

            currentBet *= 2;
            if (currentBet > playerBalance) {
                currentBet = playerBalance;
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
        List<Integer> playerHand = new ArrayList<>();
        List<Integer> dealerHand = new ArrayList<>();

        playerHand.add(drawCard());
        playerHand.add(drawCard());
        dealerHand.add(drawCard());
        dealerHand.add(drawCard());

        int playerSum = sumHand(playerHand);

        while (true) {
            clearConsole();

            System.out.println("Karte des Dealers: [" + dealerHand.get(0) + ", ?]");
            System.out.println("Deine Karten: " + playerHand + " (Summe: " + playerSum + ")");
            

            if (playerSum == 21 && playerHand.size() == 2) {
                return 2; // Blackjack
            }

            System.out.println("Möchtest du eine weitere Karte? (j/n)");
            String input = scanner.nextLine();
            if (!input.equalsIgnoreCase("j")) break;
            playerHand.add(drawCard());
            playerSum = sumHand(playerHand);
            if (playerSum > 21) {
                clearConsole();
                System.out.println("Deine Karten: " + playerHand + " (Summe: " + playerSum + ")");
                System.out.println("Du hast dich überkauft!");
                return 0;
            }
        }

        clearConsole();
        System.out.println("Deine Karten: " + playerHand + " (Summe: " + playerSum + ")");
        System.out.println("Karten des Dealers: " + dealerHand + " (Summe: " + sumHand(dealerHand) + ")");
        while (sumHand(dealerHand) < 17) {
            dealerHand.add(drawCard());
            System.out.println("Dealer zieht eine Karte: " + dealerHand.get(dealerHand.size() - 1));
            System.out.println("Karten des Dealers: " + dealerHand + " (Summe: " + sumHand(dealerHand) + ")");
            try {
                Thread.sleep(1000); // Pause für 1 Sekunde, um den Dealerzug zu simulieren
            } catch (Exception e) {
                // TODO: handle exception
            }
        }

        int dealerSum = sumHand(dealerHand);
        playerSum = sumHand(playerHand);

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

    public static void main(String[] args) {
        logic game = new logic();
        game.startGame();
    }
}
