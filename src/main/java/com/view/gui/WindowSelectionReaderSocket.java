package com.view.gui;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

/**
 * Reads and handles all messages sent from Server to GUI Socket when this one is in Window Selection Scene,
 * it is necessary in order to keep listening without freezing GUI end eventually send the player to End Scene if there
 * is a problem in the match initialization.
 */
public class WindowSelectionReaderSocket implements Runnable {

    private ControllerWindowSelection controller;
    private Scanner in;
    private Stage stage;

    /**
     * Constructs a WindowSelectionReaderSocket that use the specified ControllerWindowSelectionScene in order to
     * catch all messages send by Server during choosing operations; the Scanner given is the one of related Socket.
     * @param control the Controller for WindowSelectionScene.
     * @param received the Scanner used to listen.
     * @param s the stage of the current Application.
     */
    WindowSelectionReaderSocket(ControllerWindowSelection control, Scanner received, Stage s) {
        controller = control;
        in = received;
        stage = s;
    }

    /**
     * Starts this WindowSelectionReaderSocket and handles all messages that will receive.
     */
    @Override
    public void run() {
        String serverCommand;
        while (true) {
            serverCommand = in.nextLine();

            if (serverCommand.equals("@v")) {
                final CountDownLatch latch = new CountDownLatch(1);
                Platform.runLater(new Runnable() {
                    public void run() {
                        controller.showGameScene();
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
            else if (serverCommand.startsWith("@END")) {
                Platform.runLater(new Runnable() {
                    public void run() {
                        showEndScene();
                    }
                });
                return;
            }
            else
                System.out.println(serverCommand);
        }

    }

    /**
     * Shows End Scene
     */
    void showEndScene() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/EndScene.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            System.out.println("Unable to load FXML EndScene file");
        }
        stage.setScene(new Scene(root,1000,600));
        stage.show();
    }
}
