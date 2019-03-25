package com.model.dice;


/**
 * A NoDiceException occurs when the user tries to get a dice from an empty list .
 * The getMessage() method provides a String that explains the error.
 */
public class NoDiceException extends Exception {

    /**
     * Constructs a NoDiceException the specified detail message.
     * @param error the detail message
     */
    public NoDiceException(String error){
        super(error);
    }
}