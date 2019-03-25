package com.model.gameboard;


/**
 * A PlayerNotFoundException occurs when a player searched in the {@link GameBoard} is not found.
 * The getMessage() method provides a String that explains the error.
 */
public class PlayerNotFoundException extends Exception {

    /**
     * Constructs a PlayerNotFoundException with the specified detail message.
     * @param error the detail message
     */
    public PlayerNotFoundException(String error){
        super(error);
    }
}
