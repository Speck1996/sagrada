package com.model;

import com.model.gameboard.GameBoard;
import com.ClientState;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Socket implementation of the {@link GameObserver} interface.
 */
public class GameObserverSocket implements GameObserver {

    private final GameBoard gameBoard;
    private final PlayerInGame observingPlayer;
    private final Socket socket;
    private final String token;


    /**
     * Constructs a GameObserverSocket.
     * @param token the token of the client.
     * @param socket the socket of the client.
     * @param observingPlayer the observing player.
     * @param gameBoard the gameboard of the match.
     */
    public GameObserverSocket(String token, Socket socket, PlayerInGame observingPlayer, GameBoard gameBoard) {
        this.gameBoard = gameBoard;
        this.socket = socket;
        this.observingPlayer = observingPlayer;
        this.token = token;
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
                PrintWriter out = new PrintWriter(socket.getOutputStream());

                out.println("@v" + view + "#v");
                out.flush();
            } catch (IOException e) {
                gameBoard.addNotRespondingPlayer(observingPlayer);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPlayerDisconnection(PlayerInGame player) {
        if(!observingPlayer.isSuspended()) {
            String message = player.getUsername() + " is offline";
            sendMessage(message);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPlayerSuspension(PlayerInGame player) {
        if(!observingPlayer.isOffline()) {

            String message = null;

            if(observingPlayer.getUsername().equals(player.getUsername()))
                message = "You've been suspended because your weren't responding.\nWrite anything and press ENTER in order to return to the match";
            else if (!observingPlayer.isSuspended())
                message = player.getUsername() + " has been suspended";

            sendMessage(message);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onGameEnd() {
        if(!observingPlayer.isOffline()) {
            try {
                PrintWriter out = new PrintWriter(socket.getOutputStream());

                MainModel.getModel().getSocketReader(token).setState(ClientState.ENDGAME);

                out.println("@END#"+MainModel.getModel().getEndGameChoices());
                out.flush();

            } catch (IOException e) {
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
            String message = player.getUsername() + " has resumed the game";
            sendMessage(message);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendMessage(String message) {
        if(message == null)
            return;

        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            out.println("@p");
            out.flush();
            out.println(message + "#p");
            out.flush();
        } catch (IOException e) {
            gameBoard.addNotRespondingPlayer(observingPlayer);
        }

    }
    
}
