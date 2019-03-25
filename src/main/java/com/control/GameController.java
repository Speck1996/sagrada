package com.control;


import com.view.RemoteClient;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The Game Controller manages the interaction between the players and the model representing the match in which the players are involved.
 */
public interface GameController extends Remote {

    /**
     * Receive and manages a string command from a player identified by its client token.
     * @param token the client's token.
     * @param command the player's command.
     * @throws RemoteException if rmi connection problem occurred.
     */
    void playerCommand(String token, String command) throws RemoteException;

    /**
     * Reenter a RMI player, identified by its client token and username, in the match if it was offline.
     * Store the new RemoteClient associated to the player for future communications.
     * @param token the client's token.
     * @param username the player's username.
     * @param client the new RemoteClient.
     * @throws RemoteException if rmi connection problem occurred.
     */
    void playerReenters(String token, String username, RemoteClient client) throws RemoteException;

}
