package com.view;

import com.control.GameController;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface for RMI client.
 */
public interface RemoteClient extends Remote {

    /**
     * Print a generic message.
     * @param s the message to print.
     * @throws RemoteException if rmi connection problem occurred.
     */
    void printMessage(String s) throws RemoteException;

    /**
     * Prepares to receive windows choice.
     * @param s the message.
     * @throws RemoteException if rmi connection problem occurred.
     */
    void showWindowsScene (String s) throws RemoteException;

    /**
     * Print the windows among which the player must choose.
     * @param s the windows.
     * @throws RemoteException if rmi connection problem occurred.
     */
    void printWindowChoices(String s) throws RemoteException;

    /**
     * Print the entire current state of a match if the player is reentering after a disconnection.
     * @param s the message.
     * @throws RemoteException if rmi connection problem occurred.
     */
    void printOnReentering (String s) throws RemoteException;

    /**
     * Update this player complete view.
     * @param s the view.
     * @throws RemoteException if rmi connection problem occurred.
     */
    void updateView(String s) throws RemoteException;

    /**
     * Update this player window.
     * @param s the window.
     * @throws RemoteException if rmi connection problem occurred.
     */
    void updateWindow(String s) throws RemoteException;

    /**
     * Update the dice stock.
     * @param s the stock.
     * @throws RemoteException if rmi connection problem occurred.
     */
    void updateStock(String s) throws RemoteException;

    /**
     * Print a change from the multiplayer lobby.
     * @param s the message.
     * @throws IOException if rmi connection problem occurred.
     */
    void printLobby (String s) throws IOException;

    /**
     * Prepares to start a new match with the specified game controller.
     * @param gameController the game controller.
     * @throws RemoteException if rmi connection problem occurred.
     */
    void startGame(GameController gameController) throws RemoteException;

    /**
     * Resume a previously started match, identified by the given controller, after a disconnection.
     * @param gameController the controller of the started match.
     * @throws RemoteException if rmi connection problem occurred.
     */
    void resumeGame(GameController gameController) throws RemoteException;

    /**
     * Terminate a match and prepare for a new one.
     * @throws RemoteException if rmi connection problem occurred.
     */
    void endGame() throws RemoteException;

    /**
     * Ping method for periodically check connection.
     * @throws RemoteException if rmi connection problem occurred.
     */
    void ping() throws RemoteException;
}
