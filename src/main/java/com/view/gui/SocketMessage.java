package com.view.gui;

import javafx.application.Platform;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

/**
 * Reads and handles all messages sent from Server to GUI Socket during one multiplayer game,
 * it is necessary in order to catch all messages and keep listening without freezing GUI.
 */
public class SocketMessage implements Runnable {
    private ControllerGameScene controller;
    private Scanner in;
    private boolean stop;

    /**
     * Constructs a SocketMessage that use the specified ControllerGameScene in order to catch and handle all messages sent
     * to GUI; the Scanner given is the one of related Socket.
     * @param control the Controller for GameScene.
     * @param received the Scanner used to listen.
     */
    SocketMessage(ControllerGameScene control, Scanner received) {
        controller = control;
        in = received;
    }

    /**
     * Starts this SocketMessage and handles all messages that will receive.
     */
    @Override
    public void run() {
        String serverCommand;
        while (true) {
            stop = controller.getStopReader();
            if(stop){
                return;
            }
            serverCommand = in.nextLine();

            if (serverCommand.startsWith("@p")) {
                final CountDownLatch latch = new CountDownLatch(1);
                Platform.runLater(new Runnable() {
                    public void run() {
                        controller.messageReader();
                        latch.countDown();
                    }
                });
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
            else if (serverCommand.startsWith("@v")) {
                final CountDownLatch latch = new CountDownLatch(1);
                Platform.runLater(new Runnable() {
                    public void run() {
                        controller.updateView();
                        latch.countDown();
                    }
                });
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
            else if (serverCommand.startsWith("@u")) {
                final CountDownLatch latch = new CountDownLatch(1);
                Platform.runLater(new Runnable() {
                    public void run() {
                        controller.updateWindow();
                        latch.countDown();
                    }
                });
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
            else if (serverCommand.startsWith("@s")) {
                final CountDownLatch latch = new CountDownLatch(1);
                Platform.runLater(new Runnable() {
                    public void run() {
                        controller.updateStock();
                        latch.countDown();
                    }
                });
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
            else
                System.out.println(serverCommand);
        }
    }
}
