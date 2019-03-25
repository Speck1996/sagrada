package com.control;


import com.model.MainModel;
import com.model.Player;
import com.ClientState;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * It is a thread that concurrently reads a player's request and reply.
 */
public class SocketReader extends Thread {

    private final MainModel model;
    private final String token;
    private final Socket socket;
    private GameControllerImpl controller;
    private ClientState state = ClientState.MAINMENU;


    /**
     * Constructs a SocketReader that manages the player identified by the client token connected to the specified socket.
     * @param token the client's token.
     * @param socket the client's socket.
     */
    public SocketReader(String token, Socket socket) {
        this.model = MainModel.getModel();
        this.token = token;
        this.socket = socket;
    }

    /**
     * Starts this SocketReader.
     */
    @Override
    public void run() {

        Scanner in;
        PrintWriter out ;

        try {
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Error while reading a socket");
            return;
        }

        while(!isInterrupted()) {
            try {
                String command = in.nextLine();

                if (controller != null) {
                    try {
                        controller.playerCommand(token, command);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                else if (command.equals("#Logout")) {
                        MainModel.getModel().logout(token);
                        break;
                }
                else if(state == ClientState.MAINMENU) {

                    switch (command) {
                        case "mg":
                            out.println("@NEUTRAL#");
                            out.flush();

                            //check if the player was already in an ongoing match
                            Player player = model.getPlayerByToken(token);

                            System.out.println("Search");

                            if(player != null && model.isPlayerInGame(player)) {

                                System.out.println(player.getUsername() + " reentered");
                                GameControllerImpl controller = model.getPlayerGame(player);
                                this.controller = controller;
                                controller.playerReenters(token, player.getUsername(), socket);
                            } else
                                model.enqueuePlayer(token, socket);

                            state = ClientState.LOBBY;
                            break;
                        case "sg":
                            out.println("@NEUTRAL#");
                            out.flush();

                            //check if the player was already in an ongoing match
                            Player soloPlayer = model.getPlayerByToken(token);

                            System.out.println("Search");

                            if(soloPlayer != null && model.isPlayerInGame(soloPlayer)) {

                                System.out.println(soloPlayer.getUsername() + " reentered");
                                GameControllerImpl controller = model.getPlayerGame(soloPlayer);
                                this.controller = controller;
                                controller.playerReenters(token, soloPlayer.getUsername(), socket);
                            } else
                                model.singlePlayerGame(token, socket);

                            state = ClientState.NEUTRAL;
                            break;
                        case "gs":
                            out.println("@p" + model.getPlayerSortedByVictories() + "#p");
                            out.println("@c" + model.getMainMenuChoices());
                            out.flush();
                            break;
                        default:
                            out.println("Wrong input\n" + model.getMainMenuChoices());
                    }

                }
                else if(state == ClientState.ENDGAME) {

                    switch (command) {
                        case "g":
                            out.println("@NEUTRAL#");
                            out.flush();

                            model.enqueuePlayer(token, socket);
                            state = ClientState.LOBBY;
                            break;
                        case "s":
                            out.println("@NEUTRAL#");
                            out.flush();
                            System.out.println("starting new solo game");

                            model.singlePlayerGame(token, socket);

                            state = ClientState.NEUTRAL;
                            break;
                        case "m":
                            out.println("@MAIN#" + model.getMainMenuChoices());
                            out.flush();
                            state = ClientState.MAINMENU;
                            break;
                        default:
                            out.println("Wrong input\n" + model.getEndGameChoices());
                            out.flush();
                    }
                }
                else if(state == ClientState.LOBBY) {
                    switch (command) {
                        case "m":
                            model.dequeuePlayer(token);
                            out.println("@MAIN#" + model.getMainMenuChoices());
                            out.flush();

                            state = ClientState.MAINMENU;
                            break;
                    }
                }
                else
                    System.out.println("I don't understand");

            } catch (NoSuchElementException e) {



                System.out.println("NoSuchEl");
                if(controller != null) {
                    //controller.setPlayerOffline(token);
                    try {
                        controller.playerCommand(token, "#Logout");
                    } catch (RemoteException e1) {
                        e1.printStackTrace();
                    }
                }
                else
                    MainModel.getModel().logout(token);

                return;
            }
        }
    }


    /**
     * Set a Game Controller to this SocketReader to which eventually forwards the client's commands.
     * @param controller the Game Controller.
     */
    public void setController(GameControllerImpl controller) {
        this.controller = controller;
    }

    /**
     * Set the specified state to this SocketReader.
     * The state can alter the behavior of this SocketReader.
     * @param state
     */
    public void setState(ClientState state) {
        this.state = state;
    }


    /**
     * Returns the player that this SocketReader is serving.
     * @return the player that this SocketReader is serving.
     */
    public Player getPlayer() {
        return model.getPlayerByToken(token);
    }
}
