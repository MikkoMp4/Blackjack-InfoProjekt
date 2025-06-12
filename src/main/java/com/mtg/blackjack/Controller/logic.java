package com.mtg.blackjack.Controller;

import com.mtg.blackjack.Model.Player; 
import java.util.*;

public class logic { 
    private List<Integer> deck; 
    /**
     * Konstruktor der Klasse logic, initialisiert das Kartendeck
     */
    public logic() {
        resetDeck();
    }
    /**
     * Setzt das Kartendeck zurück und mischt es neu
     */
    public void resetDeck() {
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
    /**
     * Zieht eine Karte vom Deck. Wenn das Deck leer ist, wird es zurückgesetzt.
     * @return die gezogene Karte
     */
    public int drawCard() {
        if (deck.isEmpty()) resetDeck();
        return deck.remove(0);
    }
    /**
     * Berechnet die Summe der Karten in der Hand eines Spielers.
     * Aces werden als 11 gezählt, solange die Summe nicht über 21 steigt.
     * @param hand die Hand des Spielers
     * @return die berechnete Summe
     */
    public int sumHand(List<Integer> hand) {
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
    /**
     * Simuliert eine Wartezeit in Millisekunden.
     * @param milliseconds die Wartezeit in Millisekunden
     */
    public void wait(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
