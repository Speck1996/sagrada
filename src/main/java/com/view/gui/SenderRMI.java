package com.view.gui;

import com.ClientState;
import com.control.GameController;
import com.control.RemoteLobbyManager;
import javafx.scene.image.Image;

import java.rmi.RemoteException;

/**
 * This class handles all the interactions from a RMI GUI client to the Server/Controller
 */
public class SenderRMI {

    private boolean fromMenu = false;
    private String rank;
    private String statistics = "";
    private Image card;
    private String cost;
    private GameController gameController;
    private String token;
    private String username;
    private RemoteLobbyManager lobbyManager;
    private RMIgui rmiGUI;
    private ClientState state = ClientState.NEUTRAL;

    private final String mainMenuChoices = "What do you want to do? Multiplayer game [mg], singleplayer game [sg], see game's statistics [gs] or quit [q]";
    private final String endGameChoices = "What do you want to do now? Search a new game [g], back to main menu [m] or close [c]";

    /**
     * If player is zooming a Tool Card, this is the value of the actual cost of it.
     * @return this cost.
     */
    String getCost() {
        return cost;
    }

    /**
     * Displays the cost of the Tool Card selected from player.
     * @param f       This is the cost of the card.
     */
    void setCost(String f) {
        cost = f;
    }

    /**
     * If it is true, Client arrived to Statistics Scene directly from menu, if it is false, at the end of a game.
     * @return this fromMenu.
     */
    boolean getFromMenu() {
        return fromMenu;
    }

    /**
     * Changes the value of fromMenu, indicating the way Client arrived to Statistics Scene.
     * @param f       This is the origin of the Client.
     */
    void setFromMenu(boolean f) {
        fromMenu = f;
    }

    /**
     * Gets the general Statistics for players that played this game.
     *
     * @return this statistics.
     */
    public String getStatistics() {
        return statistics;
    }

    /**
     * Changes the general statistics updating with data from the match just concluded.
     *
     * @param s These are the general statistics for all the players that played this game.
     */
    public void setStatistics(String s) {
        statistics = s;
    }

    /**
     * Gets the final rank of a match.
     *
     * @return this rank.
     */
    String getRank() {
        return rank;
    }

    /**
     * Changes the final rank of the match just concluded.
     *
     * @param s This is the rank of the match concluded.
     */
    void setRank(String s) {
        rank = s;
    }

    /**
     * Gets the card selected by the player.
     *
     * @return this card.
     */
    public Image getCard() {
        return card;
    }

    /**
     * Changes the card selected by player that he or she wants to zoom.
     *
     * @param i This is the card selected from the player.
     */
    public void setCard(Image i) {
        card = i;
    }

    /**
     * Changes the state of the Client.
     *
     * @param c This is the state of the Client.
     */
    void setClientState(ClientState c) {
        state = c;
    }

    /**
     * Changes the RMIgui, this Sender communicates to Server for the RMIgui set here.
     *
     * @param g This is the RMIgui of the Client.
     */
    void setRmiGUI(RMIgui g) {
        rmiGUI = g;
    }

    /**
     * Changes the lobby manager, which is used by client to general tasks not related to a match.
     *
     * @param r This is the lobby manager.
     */
    void setLobbyManager(RemoteLobbyManager r) {
        lobbyManager = r;
    }

    /**
     * Changes the username of the player.
     *
     * @param n This is the username of the player.
     */
    public void setUsername(String n) {
        username = n;
    }

    /**
     * Gets the username selected by the player.
     *
     * @return this username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Changes the game Controller related to the current match.
     *
     * @param g This is the game Controller of the match.
     */
    void setGameController(GameController g) {
        gameController = g;
    }

    /**
     * Changes the token related to the Client login authentication.
     *
     * @param t This is the token of the player.
     */
    public void setToken(String t) {
        token = t;
    }

    /**
     * Notifies the wait in RMIgui related to Tool Card execution.
     */
    void notifyToolcard() {
        rmiGUI.notifyLock();
    }

    /**
     * Notifies the wait in RMIgui related to Lobby, when launched the RMIgui has received the Controller of the WaitingScene.
     */
    void notifyLobby() {
        rmiGUI.notifyLobby();
    }

    /**
     * Notifies the wait in RMIgui related to Window Selection,
     * when launched the RMIgui has received the Controller of the WindowSelectionScene.
     */
    void notifyWindowChoices() {
        rmiGUI.notifyWindowChoices();
    }

    /**
     * Notifies the wait in RMIgui related to printView,
     * when launched the RMIgui has received the Controller of the GameScene.
     */
    void notifyViewLock() {
        rmiGUI.notifyView();
    }

    /**
     * Reads command passed by RMIgui and sends it to Server.
     *
     * @param cmd       The command passed by GameScene.
     */
    void readInput(String cmd) {

        //check if exist a controller, if it doesn't it means that the game is not started so must be a menu choice
        if (gameController != null) {

            try {
                gameController.playerCommand(token, cmd);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if (state == ClientState.ENDGAME) {

            switch (cmd) {
                case "g":
                    try {
                        lobbyManager.searchMultiplayerGame(token, rmiGUI);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    state = ClientState.LOBBY;
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
        } else if (state == ClientState.MAINMENU) {

            switch (cmd) {
                case "mg":
                    try {
                        lobbyManager.searchMultiplayerGame(token, rmiGUI);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    state = ClientState.LOBBY;
                    break;
                case "sg":
                    state = ClientState.NEUTRAL;
                    break;
                case "gs":
                    try {
                        setStatistics(lobbyManager.getStatistics());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                case "q":
                    Runtime.getRuntime().exit(12);
                default:
                    System.out.println("Wrong input\n" + mainMenuChoices);
            }
        } else if (state == ClientState.LOBBY) {
            switch (cmd) {
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
