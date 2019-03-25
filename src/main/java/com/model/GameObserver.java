package com.model;

/**
 * An observer that must be informed of changes of the gameboard.
 */
public interface GameObserver extends MessageObserver {
    /**
     * Returns the player who is observing.
     * @return the player who is observing.
     */
    PlayerInGame getObservingPlayer();

    /**
     * Notify a new turn.
     */
    void onNewTurn();

    /**
     * Notify a player disconnection.
     * @param player the player that has disconnected
     */
    void onPlayerDisconnection(PlayerInGame player);

    /**
     * Notify a player suspension.
     * @param player the player that has been suspended.
     */
    void onPlayerSuspension(PlayerInGame player);

    /**
     * Notify that a player is resuming the match
     * @param player the player that resume the game
     */
    void onPlayerResume(PlayerInGame player);

    /**
     * Notify the end of the game
     */
    void onGameEnd();



}
