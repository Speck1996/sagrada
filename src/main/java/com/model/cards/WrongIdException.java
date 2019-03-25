package com.model.cards;

/**
 * A WrongIdException occurs when the id string of an item doesn't match with one of the predefined ids
 * The getMessage() method provides a String that explains the error.
 */
public class WrongIdException extends Exception {

    /**
     * Constructs a WrongIdException with the specified detail message.
     * @param error the detail message
     */
    public WrongIdException(String error){
        super(error);
    }
}
