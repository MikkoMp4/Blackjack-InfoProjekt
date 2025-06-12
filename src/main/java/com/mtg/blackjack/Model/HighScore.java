package com.mtg.blackjack.Model;

import java.io.*;
import java.util.*;

/**
 * Diese Klasse verwaltet Highscores und Spielstatistiken
 */
public class HighScore implements Serializable {
    private static final String HIGH_SCORE_FILE = "highscores.dat";
    private static final long serialVersionUID = 1L;
    
    private int highestBalance;
    private int gamesPlayed;
    private int gamesWon;
    private int blackjacksAchieved;
    private Map<String, Integer> achievements;
    
    public HighScore() {
        this.highestBalance = 0;
        this.gamesPlayed = 0;
        this.gamesWon = 0;
        this.blackjacksAchieved = 0;
        this.achievements = new HashMap<>();
    }
    
    /**
     * Aktualisiert das höchste Guthaben, falls das aktuelle Guthaben höher ist
     * @param currentBalance Das aktuelle Spielerguthaben
     * @return true, wenn ein neuer Highscore erreicht wurde
     */
    public boolean updateHighestBalance(int currentBalance) {
        if (currentBalance > highestBalance) {
            highestBalance = currentBalance;
            return true;
        }
        return false;
    }
    
    /**
     * erhöht die Anzahl der gespielten Spiele
     */
    public void incrementGamesPlayed() {
        gamesPlayed++;
    }
    
    /**
     * erhöht die Anzahl der gewonnenen Spiele
     */
    public void incrementGamesWon() {
        gamesWon++;
    }
    
    /**
     * erhöht die Anzahl der erreichten Blackjacks
     */
    public void incrementBlackjacks() {
        blackjacksAchieved++;
    }
    
    /**
     * Fügt ein Achievement hinzu oder aktualisiert es, wenn es bereits existiert
     * @param name Achievement name
     * @param value Achievement value
     */
    public void updateAchievement(String name, int value) {
        achievements.put(name, value);
    }
    
    /**
     * Getted die höchste erreichte Balance
     * @return die höchste erreichte Balance
     */
    public int getHighestBalance() {
        return highestBalance;
    }
    
    /**
     * Getted die Anzahl der gespielten Spiele
     * @return gepsielte Spiele
     */
    public int getGamesPlayed() {
        return gamesPlayed;
    }
    
    /**
     * Getted die Anzahl der gewonnenen Spiele
     * @return Gewonnene Spiele
     */
    public int getGamesWon() {
        return gamesWon;
    }
    
    /**
     * Getted die Gewinnrate in Prozent
     * @return Gewinnrate in Prozent
     */
    public double getWinRate() {
        if (gamesPlayed == 0) return 0;
        return (double) gamesWon / gamesPlayed * 100;
    }
    
    /**
     * Getted die Anzahl der erreichten Blackjacks
     * @return erreichte Blackjacks
     */
    public int getBlackjacksAchieved() {
        return blackjacksAchieved;
    }
    
    /**
     * Getted all achievements
     * @return eine Map mit den Achievements, wo der Schlüssel der Name und der Wert die Anzahl ist
     */
    public Map<String, Integer> getAchievements() {
        return achievements;
    }
    
    /**
     * Speichert die Highscores in einer Datei
     */
    public void saveHighScores() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(HIGH_SCORE_FILE))) {
            oos.writeObject(this);
        } catch (IOException e) {
            System.err.println("Fehler beim Speichern der Highscores: " + e.getMessage());
        }
    }
    
    /**
     * Lädt die Highscores aus der Datei
     * @return Das HighScore-Objekt mit den geladenen Daten
     */
    public static HighScore loadHighScores() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(HIGH_SCORE_FILE))) {
            return (HighScore) ois.readObject();
        } catch (FileNotFoundException e) {
            // Datei nicht gefunden, erstelle neues HighScore-Objekt
            return new HighScore();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading high scores: " + e.getMessage());
            return new HighScore();
        }
    }
}