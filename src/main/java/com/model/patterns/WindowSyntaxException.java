package com.model.patterns;

/**
 * A WindowSyntaxException occurs when it is found a syntactic error in a String describing a WindowPatterCard.
 * The getMessage() method provides a String that explains the error.
 */
public class WindowSyntaxException extends RuntimeException {
    /**
     * Constructs a WindowSyntaxException with the specified detail message.
     * @param message the detail message
     */
    WindowSyntaxException(String message) {
        super(message);
    }
}
