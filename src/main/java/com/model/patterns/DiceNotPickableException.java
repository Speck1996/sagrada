package com.model.patterns;

/**
 * A DiceNotPickableException occurs when the user tries pick a die from the window that violates the color or shade constraint .
 * The getMessage() method provides a String that explains the error.
 */
public class DiceNotPickableException extends Exception {

    /**
     * Constructs a DiceNotPickableException with the specified detail message.
     * @param error the detail message
     */
    DiceNotPickableException(String error){
        super(error);
    }
}
