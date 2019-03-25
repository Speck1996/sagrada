package com.view;

import com.ClientState;

import java.util.Scanner;

/**
 * This Runnable is used to receiving socket messages from server at any time, independently from client input and state.
 */
public class CLIReader implements Runnable {
    private CLI cli;
    private Scanner in;


    /**
     * Constructs a CLIReader bound to the specified CLI and that read the specified Scanner.
     * @param cli the CLI to notify.
     * @param in the scanner to be read.
     */
    CLIReader(CLI cli, Scanner in) {
        this.cli = cli;
        this.in = in;
    }

    /**
     * Starts CLIReader.
     */
    @Override
    public void run() {

        String serverCommand;
        while (true) {
            serverCommand = in.nextLine();

            if (serverCommand.startsWith("@p") || serverCommand.startsWith("@w") || serverCommand.startsWith("@v") || serverCommand.startsWith("@u") || serverCommand.startsWith("@s")) {
                serverCommand = serverCommand.substring(2);
                while (true) {
                    if((serverCommand.endsWith("#p") || serverCommand.endsWith("#w") || serverCommand.endsWith("#v") || serverCommand.endsWith("#u") || serverCommand.endsWith("#s"))) {
                        System.out.println(serverCommand.substring(0,serverCommand.length()-2));
                        break;
                    }
                    System.out.println(serverCommand);
                    serverCommand = in.nextLine();
                }
            }
            else if(serverCommand.startsWith("@c")) {
                System.out.println(serverCommand + " or quit [q]");
            }
            else if(serverCommand.startsWith("@MAIN#")) {
                    cli.setState(ClientState.MAINMENU);
                    System.out.println(serverCommand.substring(6) + " or quit [q]");
            }
            else if(serverCommand.startsWith("@END#")) {
                    cli.setState(ClientState.ENDGAME);
                    System.out.println(serverCommand.substring(5) + " or close [c]");
            }
            else if(serverCommand.startsWith("@NEUTRAL#")) {
                    cli.setState(ClientState.NEUTRAL);
            }
            else
                System.out.println("I don't understand");
        }
    }
}