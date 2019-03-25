package com.model.dice;


/**
 * A MinMaxReachedException occurs when the user tries to get a previous shade of the minimum shade or a next
 * shade of the maximum shade,
 * The getMessage() method provides a String that explains the error.
 */
public class MinMaxReachedException extends Exception {


    /**
     * Constructs a MinMaxReachedException the specified detail message.
     * @param error the detail message
     */
    public MinMaxReachedException(String error){
        super(error);
    }
}
