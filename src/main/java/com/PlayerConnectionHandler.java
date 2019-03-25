package com;

import com.control.SocketReader;
import com.model.LoginException;
import com.model.MainModel;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.Scanner;

/**
 * This runnable manages a new socket connection startup.
 * A new PlayerConnectionHandler is create every time a new socket connection is accepted.
 */
public class PlayerConnectionHandler implements Runnable {

    private Socket socket;

    /**
     * Constructs a PlayerConnectionHandler that handles the specified socket.
     * @param socket the socket to be handle.
     */
    PlayerConnectionHandler(Socket socket) {
        this.socket = socket;
    }

    /**
     * Starts this PlayerConnectionHandler.
     */
    @Override
    public void run() {
        Scanner in;
        PrintWriter out;
        MainModel model;

        try {
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Error while handling a socket");
            return;
        }

        model = MainModel.getModel();

        String line;
        String token = null;

        while(token == null) {
            line = in.nextLine();
            String[] data = line.split(" ");

            if(data.length == 2) {
                System.out.println("Logging in: " + data[0] + " pass: " + data[1]);

                try {
                    token = model.login(data[0], data[1]);
                    out.println(token);
                    out.flush();
                    System.out.println(data[0] + " logged successfully");
                } catch (LoginException e) {
                    System.out.println(e.getMessage());
                    out.println(e.getMessage());
                }

            } else {
                out.println("");
            }

            out.flush();
        }

        //the player is logged

        out.println(model.getMainMenuChoices());
        out.flush();

        SocketReader socketReader = new SocketReader(token, socket);
        model.addSocketReader(token, socketReader);
        socketReader.start();

    }
}
