package com.control;

import com.model.LoginException;
import com.view.RemoteClient;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * It is used by client to request general task not regarding a match.
 */
public interface RemoteLobbyManager extends Remote {
    /**
     * Try to login a user with the specified username and password.
     * Use the specified RemoteClient for reply and store it for future communications if the login was successfully.
     * @param username the player's username.
     * @param password the player's password.
     * @param client the player's RemoteClient.
     * @return the client token
     * @throws RemoteException if rmi connection problem occurred.
     * @throws LoginException if login failed.
     */
    String login(String username, String password, RemoteClient client) throws RemoteException, LoginException;

    /**
     * Logout the player identified by the specified token.
     * @param token the player's token.
     * @throws RemoteException if rmi connection problem occurred.
     */
    void logout(String token) throws RemoteException;

    /**
     * Add the player, identified by the token and by its RemoteClient, to the queue for multiplayer game.
     * If result that the player was already in an ongoing match, reinsert it in the match.
     * @param token the player's token.
     * @param client the player's RemoteClient.
     * @throws RemoteException if rmi connection problem occurred.
     */
    void searchMultiplayerGame(String token, RemoteClient client) throws RemoteException;

    /**
     * Start or resume a singleplayer game for the player, identified by the token and by its RemoteClient.
     * @param token the player's token.
     * @param client the player0s RemoteClient.
     * @throws RemoteException if rmi connection problem occurred.
     */
    void startSoloGame(String token, RemoteClient client) throws RemoteException;

    /**
     * Remove the player identified by the token from the queue for multiplayer game.
     * @param token the players' token.
     * @throws RemoteException if rmi connection problem occurred.
     */
    void dequeue(String token) throws RemoteException;

    /**
     * Returns the global statistics.
     * @return the global statistics.
     * @throws RemoteException if rmi connection problem occurred.
     */
    String getStatistics() throws RemoteException;
}
