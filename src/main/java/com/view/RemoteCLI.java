package com.view;

import com.ClientState;
import com.control.GameController;
import com.control.RemoteLobbyManager;
import com.model.LoginException;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

/**
 * A command line implementation of the {@link RemoteClient}.
 * @see RemoteClient
 */
public class RemoteCLI extends UnicastRemoteObject implements RemoteClient {  //CLI for rmi users

    private final Scanner stdin;
    private final RemoteLobbyManager lobbyManager;
    private String token = null;
    private String username;

    private GameController gameController;
    private ClientState state = ClientState.NEUTRAL;

    private final String mainMenuChoices = "What do you want to do? Multiplayer game [mg], singleplayer game [sg], see game's statistics [gs] or quit [q]";
    private final String endGameChoices = "What do you want to do now? Search a new multiplayer game [g], start a new solo game [s] , back to main menu [m] or close [c]";

    /**
     * Constructs a RemoteCLI that use the specified Remote Lobby Manager for searching games and the specified standard input.
     * @param lobbyManager the Remote Lobby Manager.
     * @param stdin the standard input.
     * @throws RemoteException if rmi connection problem occurred.
     */
    public RemoteCLI(RemoteLobbyManager lobbyManager, Scanner stdin) throws RemoteException {
        super();
        this.stdin = stdin;
        this.lobbyManager = lobbyManager;
    }


    /**
     * Starts this RemoteCLI.
     * Manages the login and all the user input.
     * @throws RemoteException if rmi connection problem occurred.
     */
    public void run() throws RemoteException {
        System.out.println("--> Welcome to Sagrada <--\n\n");


        do {
            System.out.println(">> Provide an username and a password");
            String line = stdin.nextLine();
            String[] data = line.split(" ");

            if (data.length == 2) {

                try {
                    token = lobbyManager.login(data[0], data[1], this);
                    username = data[0];
                    System.out.println("Login as "+data[0]);
                    System.out.println("token: \n " + token + "\n");
                } catch (LoginException e) {
                    System.out.println(e.getMessage());
                }
            }

        } while (token == null);


        //logout if the client closes its terminal
        Thread shutdownHook = new Thread(() -> {
                System.out.println("Hey, shutting down!");

                try {

                    if (gameController != null) {
                        gameController.playerCommand(token, "#Logout");
                    } else
                        lobbyManager.logout(token);

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            });

        Runtime.getRuntime().addShutdownHook(shutdownHook);


        //now I am logged


        state = ClientState.MAINMENU;
        System.out.println(mainMenuChoices);


        readInput();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void printMessage(String s) throws RemoteException {
        System.out.println(s);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void printLobby (String s) throws RemoteException {
        System.out.println(s);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void printWindowChoices(String s) throws RemoteException {
        System.out.println(s);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateView(String s) throws RemoteException {
        System.out.println(s);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateWindow(String s) throws RemoteException {
        System.out.println(s);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateStock(String s) throws RemoteException {
        System.out.println(s);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showWindowsScene (String s) throws RemoteException {
        System.out.println(s);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void printOnReentering (String s) throws RemoteException {
        System.out.println(s);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startGame(GameController gameController) throws RemoteException {
        this.gameController = gameController;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resumeGame(GameController gameController) throws RemoteException {
        //this happen when the player logout or crash and then reenters the game
        this.gameController = gameController;
        this.gameController.playerReenters(token, username, this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void endGame() throws RemoteException {
        state = ClientState.ENDGAME;
        System.out.println(endGameChoices);
        gameController = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void ping() throws RemoteException {

    }

    /**
     * Reads player input and send to server.
     */
    private void readInput() {
        while (true) {
            String command = stdin.nextLine();

            //check if exist a controller, if it doesn't it means that the game is not started so must be a menu choice
            if(gameController != null) {

                try {
                    gameController.playerCommand(token, command);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            else if(state == ClientState.ENDGAME) {

                switch (command) {
                    case "g":
                        try {
                            lobbyManager.searchMultiplayerGame(token, this);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        state = ClientState.LOBBY;
                        break;
                    case "s" :
                        try {
                            lobbyManager.startSoloGame(token, this);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        state = ClientState.NEUTRAL;
                        break;
                    case "m":
                        System.out.println(mainMenuChoices);
                        state = ClientState.MAINMENU;
                        break;
                    case "c":
                        Runtime.getRuntime().exit(12);
                    default:
                        System.out.println("Wrong input\n" + endGameChoices);
                }
            }
            else if(state == ClientState.MAINMENU) {

                switch (command) {
                    case "mg":
                        try {
                            lobbyManager.searchMultiplayerGame(token, this);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        state = ClientState.LOBBY;
                        break;
                    case "sg":
                        try {
                            lobbyManager.startSoloGame(token, this);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        state = ClientState.NEUTRAL;
                        break;
                    case "gs":
                        try {
                            System.out.println(lobbyManager.getStatistics());
                            System.out.println(mainMenuChoices);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "q":
                        Runtime.getRuntime().exit(12);
                    default:
                        System.out.println("Wrong input\n" + mainMenuChoices);
                }
            }
            else if(state == ClientState.LOBBY) {
                switch (command) {
                    case "m":
                        try {
                            lobbyManager.dequeue(token);
                            System.out.println(mainMenuChoices);
                            state = ClientState.MAINMENU;
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        System.out.println("Wrong input");
                }
            }
        }
    }
}
