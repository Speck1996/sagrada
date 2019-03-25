package com.view.gui;

import javafx.scene.image.Image;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * This class handles all the interactions between a Socket GUI client and the Server/Controller.
 */
public class Sender {

    private Scanner in;
    private PrintWriter out;
    private String username;
    private Image card;
    private String cost;
    private boolean fromMenu = false;
    private boolean returnToMenu = false;

    /**
     * Saves the fact that player quit from lobby.
     * @return this returnToMenu.
     */
    boolean getReturnToMenu() {
        return returnToMenu;
    }

    /**
     * Changes the value of returnToMenu indicating if the player is reentering in lobby after quit.
     * @param f       This is the new returnToMenu.
     */
    void setReturnToMenu(boolean f) {
        returnToMenu = f;
    }

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
     * Gets the card selected by the player.
     * @return this card.
     */
    public Image getCard() {
        return card;
    }

    /**
     * Changes the card selected by player that he or she wants to zoom.
     * @param i       This is the card selected from the player.
     */
    public void setCard(Image i) {
        card = i;
    }

    /**
     * Gets the username of the player.
     * @return this username.
     */
    public String getUsername(){
        return username;
    }

    /**
     * Changes the username chosen by the player.
     * @param n       This is the username of the player.
     */
    public void setUsername(String n) {
        username = n;
    }

    /**
     * Gets the scanner related to Socket messages received from the Server by the Client.
     * @return this in.
     */
    public Scanner getIn() {
        return in;
    }

    /**
     * Gets the printWriter related to Socket messages sent by the Client to the Server.
     * @return this out.
     */
    public PrintWriter getOut() {
        return out;
    }

    /**
     * Prints the command that Client wants to send to Server and flushes it through the Socket.
     * @param cmd       This is the command that the Client wants to send to Server.
     */
    public void send(String cmd) {
        out.println(cmd);
        out.flush();
    }

    /**
     * Reads messages that Server flushes on Socket in order to send it to Client.
     * @return message received from Server.
     */
    public String read() {
        String message = in.nextLine();
        System.out.println("" + message);
        return message;
    }

    /**
     * Starts this Sender for Socket.
     * @param host the IP address of the server.
     * @param port the port of the server.
     */
    public void start(String host, int port) {
        Socket socket;

        try {
            socket = new Socket(host, port);
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Unable to reach the server, check hostname and port");
            System.exit(-1);
        }
    }
}