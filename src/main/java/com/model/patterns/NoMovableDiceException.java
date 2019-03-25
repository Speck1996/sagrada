package com.model.patterns;

/**
 * A NoMovableDiceException occurs when the window doesn't have any movable dice that respect the given constraints
 * The getMessage() method provides a String that explains the error.
 */
public class NoMovableDiceException extends Exception {


    /**
     * Constructs a DiceNotPickableException with the specified detail message.
     * @param error the detail message
     */
    NoMovableDiceException(String error){
        super(error);
    }

}
