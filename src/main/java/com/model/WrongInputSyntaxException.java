package com.model;
/**
 * A WrongInputSyntax occurs when it is found a syntactic error in a String sent by the user through the view: the controller
 * works with predefined typology of string inputs so when the given one doesn't match the predefined one this exception is thrown.
 * The getMessage() method provides a String that explains the error.
 */
public class WrongInputSyntaxException extends Exception {

    /**
     * Constructs a WrongInputSyntaxException the specified detail message.
     * @param error the detail message
     */
        public WrongInputSyntaxException(String error){
            super(error);
        }
}
