package com.model;

import com.view.RemoteClient;
import com.model.gameboard.GameBoard;

import java.rmi.RemoteException;

/**
 * RMI implementation of the {@link GameObserver} interface.
 */
public class GameObserverRMI implements GameObserver {

    private final GameBoard gameBoard;
    private final PlayerInGame observingPlayer;
    private final RemoteClient client;

    /**
     * Constructs a GameObserverRMI.
     * @param client the remote client of the client.
     * @param observingPlayer the observing player.
     * @param gameBoard the gameboard of the match.
     */
    public GameObserverRMI(RemoteClient client, PlayerInGame observingPlayer, GameBoard gameBoard) {
        this.observingPlayer = observingPlayer;
        this.client = client;
        this.gameBoard = gameBoard;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlayerInGame getObservingPlayer() {
        return observingPlayer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onNewTurn() {
        if(!observingPlayer.isSuspended()) {
            String view = gameBoard.getView(observingPlayer);

            try {
                client.updateView(view);
            } catch (RemoteException e) {
                gameBoard.addNotRespondingPlayer(observingPlayer);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPlayerDisconnection(PlayerInGame player)  {
        if(!observingPlayer.isSuspended()) {
            try {
                client.printMessage(player.getUsername() + " is offline");
            } catch (RemoteException e) {
                gameBoard.addNotRespondingPlayer(observingPlayer);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPlayerSuspension(PlayerInGame player) {
        if(!observingPlayer.isOffline()) {
            try {
                if(observingPlayer.getUsername().equals(player.getUsername()))
                    client.printMessage("You've been suspended because your weren't responding.\nWrite anything and press ENTER in order to return to the match");
                else if(!observingPlayer.isSuspended())
                    client.printMessage(player.getUsername() + " has been suspended");

            } catch (RemoteException e) {
                gameBoard.addNotRespondingPlayer(observingPlayer);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onGameEnd() {
        if(!observingPlayer.isOffline()) {

            try {
                client.endGame();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPlayerResume(PlayerInGame player) {
        if(!observingPlayer.isSuspended()) {
            try {
                client.printMessage(player.getUsername() + " has resumed the game");
            } catch (RemoteException e) {
                gameBoard.addNotRespondingPlayer(observingPlayer);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendMessage(String message) {
        try {
            client.printMessage(message);
        } catch (RemoteException e) {
            gameBoard.addNotRespondingPlayer(observingPlayer);
        }
    }


}
