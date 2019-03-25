package com.model;

/**
 * A MoveAbortedException occurs when the user input corresponds to the abort command.
 * The getMessage() method provides a String that explains the error.
 */
public class MoveAbortedException extends Exception {

    /**
     * Constructs a MoveAbortedException the specified detail message.
     * @param error the detail message
     */
    public MoveAbortedException(String error){
        super(error);
    }
}
