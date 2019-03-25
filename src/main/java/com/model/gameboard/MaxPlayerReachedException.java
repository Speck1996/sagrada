package com.model.gameboard;


/**
 * A MaxPlayerReachedException occurs when a player is added in a full {@link GameBoard}.
 * The getMessage() method provides a String that explains the error.
 */
public class MaxPlayerReachedException extends Exception {

    /**
     * Constructs a MaxPlayerReachedException with the specified detail message.
     * @param error the detail message
     */
    public MaxPlayerReachedException(String error){super(error);}
}
