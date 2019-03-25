package com.model.patterns;

/**
 * A DieNotPlaceableException occurs when trying to place a die where it can not be placed.
 * The getMessage() method provides a String that explains the cause.
 */
public class DieNotPlaceableException extends Exception {
    /**
     * Constructs a DieNotPlaceableException with the specified detail message.
     * @param message the detail message.
     */
    public DieNotPlaceableException(String message){
        super(message);
    }
}
