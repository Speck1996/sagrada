package com.view;

import com.ClientState;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * The Command Line Interface for socket users.
 */
public class CLI {

    private final String host;
    private final int port;

    private static Scanner stdin = null;

    private PrintWriter out;
    private String token;


    private ClientState state = ClientState.MAINMENU;

    /**
     * Constructs a CLI.
     * @param stdin the standard input.
     * @param host the IP address of the server.
     * @param port the port of the server.
     */
    public CLI(Scanner stdin, String host, int port) {
        CLI.stdin = stdin;
        this.host = host;
        this.port = port;
    }

    /**
     * Starts this Command Line Interface.
     * Manages the login and all the user input.
     */
    public void run() {
        Scanner in;
        String username;

        System.out.println("--> Welcome to Sagrada <--\n\n");
        Socket socket;

        try {
            socket = new Socket(host, port);
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Unable to reach the server, check hostname and port");
            return;
        }

        String line;



        //login
        String serverAnswer;
        do {
            System.out.println(">> Provide an username and a password");
            line = stdin.nextLine();

            if (!line.isEmpty()) {
                out.println(line);
                out.flush();

                serverAnswer = in.nextLine();

                if(serverAnswer.equals("user already logged") || serverAnswer.equals("wrong password") || serverAnswer.startsWith("username too long") || serverAnswer.equals("")) {
                    System.out.println(serverAnswer);
                }
                else {
                    username = line.split(" ")[0];
                    System.out.println("Hi " + username);
                    token = serverAnswer;
                    System.out.println("token: " + token);
                }
            }

        } while (line.length() == 0 || token == null);


        //logged successfully

        Thread shutdowntHook = new Thread(()-> {
            System.out.println("Hey shutting down");
            out.println("#Logout");
            out.flush();
        });

        Runtime.getRuntime().addShutdownHook(shutdowntHook);

        //retrieve main menu choices
        System.out.println(in.nextLine() + " or quit [q]");



        //start receiving server message
        new Thread(new CLIReader(this, in)).start();

        startSender();
    }

    /**
     * Reads player input and send to server.
     */
    private void startSender() {
        while (true) {
            String input = stdin.nextLine();

            if(state == ClientState.ENDGAME && input.equals("c") || state == ClientState.MAINMENU && input.equals("q")) {
                Runtime.getRuntime().exit(12);
            }

            out.println(input);
            out.flush();
        }
    }

    /**
     * Set the state of the CLI.
     * @param state the new state.
     */
    protected void setState(ClientState state) {
        this.state = state;
    }


}