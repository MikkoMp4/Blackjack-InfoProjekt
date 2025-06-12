package com.mtg.blackjack.Controller;

import com.mtg.blackjack.Model.Player; 
import java.util.*;

public class logic { 
    private List<Integer> deck; 

    public logic() {
        resetDeck();
    }

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

    public int drawCard() {
        if (deck.isEmpty()) resetDeck();
        return deck.remove(0);
    }

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

    public void wait(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
