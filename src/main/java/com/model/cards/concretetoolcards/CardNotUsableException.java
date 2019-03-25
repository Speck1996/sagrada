package com.model.cards.concretetoolcards;

/**
 * A CardNotUsableException occurs when the user tries to activate the card when he can't do that.
 * The getMessage() method provides a String that explains the error.
 */

public class CardNotUsableException extends Exception{

    /**
     * Constructs a CardNotUsableException with the specified detail message.
     * @param error the detail message
     */
    public CardNotUsableException(String error){
        super(error);
    }
}
