package com.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * It is the general representation of a player.
 * It holds the username and the statistics of a player
 */

public class Player implements Serializable {

    private String username;
    private int numOfVictories;
    private int numOfDefeat;
    private int totPoints;
    private double totGameTime;

    /**
     * Constructs a Player with the specified username.
     * @param username the player's username.
     */
    public Player(String username) {
        this.username = username;
        this.numOfVictories = 0;
        this.numOfDefeat = 0;
        this.totPoints = 0;
        this.totGameTime = 0;
    }

    /**
     * Returns this player's username.
     * @return this player's username.
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Returns the number of victories of this player.
     * @return the number of victories of this player.
     */
    int getNumOfVictories() {
        return numOfVictories;
    }

    /**
     * Returns the number of defeat of this player.
     * @return the number of defeat of this player.
     */
    int getNumOfDefeat() {
        return numOfDefeat;
    }

    /**
     * Returns the total points of this player.
     * @return the total points of this player.
     */
    int getTotPoints() {
        return totPoints;
    }

    /**
     * Returns the total game time of this player.
     * @return the total game time of this player.
     */
    double getTotGameTime() {
        return totGameTime;
    }

    /**
     * Add a new victory to this player.
     */
    public void newVictory() {
        this.numOfVictories++;
    }

    /**
     * Add a new defeat to this player.
     */
    public void newDefeat() {
        this.numOfDefeat++;
    }

    /**
     * Add the specified points to this player total points.
     * @param points the points to add.
     */
    public void addPoints(int points) {
        this.totPoints += points;
    }

    /**
     * Add the specified minutes to this player total game time.
     * @param minutes the minutes to add.
     */
    public void addTime(double minutes) {
        this.totGameTime += minutes;
    }


    /**
     * Method for equality check for Player.
     * @param o the Object to check equality with.
     * @return true if this Player is equal to the parameter.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(username, player.username);
    }
}
