package com.view.gui;

import javafx.application.Platform;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

/**
 * Reads and handles all messages sent from Server to GUI Socket when this one is in Waiting Scene,
 * it is necessary in order to keep listening without freezing GUI.
 */
public class LobbyReaderSocket implements Runnable{

    private ControllerWaitingScene controller;
    private Scanner in;
    private Sender senderSocket;

    /**
     * Constructs a LobbyReaderSocket that use the specified ControllerWaitingScene in order to append messages on Text Area
     * in GUI; the Scanner given is the one of related Socket.
     * @param control the Controller for WaitingScene.
     * @param received the Scanner used to listen.
     */
    LobbyReaderSocket(ControllerWaitingScene control, Scanner received) {
        controller = control;
        in = received;
    }

    /**
     * Starts this LobbyReaderSocket and handles all messages that will receive.
     */
    @Override
    public void run() {
        senderSocket = ControllerStartScene.getSenderSocket();
        String serverCommand;
        while (true) {
            serverCommand = in.nextLine();

            if (serverCommand.startsWith("@MAIN") && senderSocket.getReturnToMenu()) return;
            else if (serverCommand.startsWith("@v")) {
                final CountDownLatch latch = new CountDownLatch(1);
                Platform.runLater(new Runnable() {
                    public void run() {
                        controller.startGameForPlayerReentered();
                        latch.countDown();
                    }
                });
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
                return;
            }
            else if ((!(serverCommand.equals("@NEUTRAL#"))) && (!(serverCommand.startsWith("What "))) && (!(serverCommand.endsWith("windows#p"))) && (!(serverCommand.startsWith("@END"))) && (!(serverCommand.startsWith("@MAIN"))) && (!(serverCommand.equals("@p")))) {
                controller.checkMessage(serverCommand);
            }
            else if (serverCommand.endsWith("windows#p")) {
                final CountDownLatch latch = new CountDownLatch(1);
                Platform.runLater(new Runnable() {
                    public void run() {
                        controller.showWindowSelection();
                        latch.countDown();
                    }
                });
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
                return;
            }
            else
                System.out.println(serverCommand);
        }
    }
}
