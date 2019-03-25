package com.model.cards.concretetoolcards;

/**
 * A DieAlreadyMovedException occurs when the user tries to pick a die that was already moved during the execution
 * of a mover ToolCard
 * The getMessage() method provides a String that explains the error.
 */
public class DieAlreadyMovedException extends Exception {


    /**
     * Constructs a WrongIdException with the specified detail message.
     * @param error the detail message
     */
    public DieAlreadyMovedException(String error){
        super(error);
    }
}
