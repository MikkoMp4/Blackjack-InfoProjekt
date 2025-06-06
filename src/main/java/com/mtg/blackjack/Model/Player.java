package com.mtg.blackjack.Model;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private int balance;
    private List<Integer> hand;

    public Player(int initialBalance) {
        this.balance = initialBalance;
        this.hand = new ArrayList<>();
    }

    public int getBalance() {
        return balance;
    }

    public void adjustBalance(int amount) {
        this.balance += amount;
    }

    public List<Integer> getHand() {
        return hand;
    }

    public void addCardToHand(int card) {
        hand.add(card);
    }

    public int calculateHandSum() {
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

    public void resetHand() {
        hand.clear();
    }
}