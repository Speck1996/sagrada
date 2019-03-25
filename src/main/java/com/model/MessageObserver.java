package com.model;

/**
 * An observer used for notify generic messages.
 */
public interface MessageObserver {
    /**
     * Notify a generic message.
     * @param message the message to notify.
     */
    void sendMessage(String message);
}
