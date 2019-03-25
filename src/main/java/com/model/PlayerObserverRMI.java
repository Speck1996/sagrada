package com.model;

import com.model.gameboard.GameBoard;
import com.view.RemoteClient;

import java.rmi.RemoteException;

/**
 * RMI implementation of the {@link MessageObserver} interface.
 */
public class PlayerObserverRMI implements MessageObserver {

    private final GameBoard gameBoard;
    private final PlayerInGame observingPlayer;
    private final RemoteClient client;

    /**
     * Constructs a PlayerObserverRMI.
     * @param client the remote client of the client.
     * @param observingPlayer the observing player.
     * @param gameBoard the gameboard of the match
     */
    public PlayerObserverRMI(RemoteClient client, PlayerInGame observingPlayer, GameBoard gameBoard) {
        this.observingPlayer = observingPlayer;
        this.client = client;
        this.gameBoard = gameBoard;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendMessage(String view) {
        if(!observingPlayer.isSuspended()) {
            try {
                client.printMessage(view);
            } catch (RemoteException e) {
                gameBoard.addNotRespondingPlayer(observingPlayer);
            }
        }
    }




}
