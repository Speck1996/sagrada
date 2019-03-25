package com.model;

import com.model.gameboard.GameBoard;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Socket implementation of the {@link MessageObserver} interface.
 */
public class PlayerObserverSocket implements MessageObserver {


    private final GameBoard gameBoard;
    private final Socket socket;
    private final PlayerInGame observingPlayer;

    /**
     * Constructs a PlayerObserverSocket.
     * @param socket the socket of the client.
     * @param observingPlayer the observing player.
     * @param gameBoard the gameboard of the match.
     */
    public PlayerObserverSocket(Socket socket, PlayerInGame observingPlayer, GameBoard gameBoard) {
        this.socket = socket;
        this.observingPlayer = observingPlayer;
        this.gameBoard = gameBoard;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void sendMessage(String view)  {

        if(!observingPlayer.isSuspended()) {

            try {
                PrintWriter out = new PrintWriter(socket.getOutputStream());
                out.println("@p");
                out.flush();
                out.println(view + "#p");
                out.flush();
            } catch (IOException e) {
                gameBoard.addNotRespondingPlayer(observingPlayer);
            }
        }


    }

}
