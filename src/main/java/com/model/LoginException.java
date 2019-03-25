package com.model;

/**
 * <p>A LoginException may occurs during a login attempt.
 * It can be caused by:</p>
 * <ul>
 *     <li>Username too long: there is it a maximum length for username</li>
 *     <li>User already logged: it seems that the specified username is already logged</li>
 *     <li>Wrong password: the password provided does not match with the password associate with the specified username</li>
 * </ul>
 * These messages can be retrieve calling getMessage().
 */
public class LoginException extends Exception {
    /**
     * Constructs a LoginException with the specified detail message.
     * @param message the detail message.
     */
    LoginException(String message) {
        super(message);
    }
}
