package com.view.gui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import javafx.stage.Stage;

/**
 * This is the Controller for Waiting Scene, for both RMI and Socket handles operations
 * related to lobby events.
 */
public class ControllerWaitingScene {

    private Sender senderSocket;
    private SenderRMI senderRMI;
    private boolean isRMI;
    private Stage stage;

    @FXML
    public TextArea playersList;

    /**
     * Initialization of WaitingScene: stage is taken from
     * @see GUI
     * and isRMI is taken from
     * @see ControllerStartScene
     * For Socket displays the players queue in the lobby, it is always updated at the last event happened, when
     * a new game starts, this controller shows WindowSelectionScene; if there is a player reentering in a match
     * that is running, this controller directly shows the GameScene related to that match;
     * for RMI passes this controller to RMIgui in order to give it the possibility to do same things of Socket.
     */
    @FXML
    private void initialize() {
        isRMI = ControllerStartScene.getIsRMI();
        stage = GUI.getStage();
        if (!isRMI) {
            senderSocket = ControllerStartScene.getSenderSocket();
            new Thread(new LobbyReaderSocket(this, senderSocket.getIn())).start();
        }
        else {
            senderRMI = ControllerStartScene.getSenderRMI();
            RMIgui r = ControllerStartScene.getRmi();
            r.setWaitControl(this);
            senderRMI.notifyLobby();
        }
    }

    /**
     * Calls directly the Game Scene if the player is reentering in a match after a disconnection.
     */
    void startGameForPlayerReentered() {
        showGameScene();
    }

    /**
     * Takes messages from LobbyReaderSocket and cleans them from service tags, in order to print in a proper way.
     * @param s the message sent by Reader.
     */
    void checkMessage (String s) {
        System.out.println(s);
        String service;
        String line;
        if (s.startsWith("press [m] to return")) writeMessageSocket("\n");
        else {
            if (s.contains("@p")) {
                service = s.split("@p")[1];
                if (service.contains("#p")) {
                    line = service.split("#p")[0];
                    writeMessageSocket(line);
                } else writeMessageSocket(service);
            } else if (s.contains("#p")) {
                line = s.split("#p")[0];
                writeMessageSocket(line);
            } else writeMessageSocket(s);
        }
    }

    /**
     * Appends in the Text Area all the messages evaluated by checkMessage.
     * @param s the message passed by checkMessage.
     */
    void writeMessageSocket(String s) {
        final CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(new Runnable() {
            public void run() {
                playersList.appendText(s + "\n");
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

    /**
     * For RMI, appends to Text Area all the messages related to queue and lobby events, sent from Server.
     * @param s the message sent by Server.
     */
    void writeMessage(String s) {
        if (s.contains("press")) {
            String line = s.split("press")[0];
            playersList.appendText(line + "\n");
        }
        else playersList.appendText(s + "\n");
    }

    /**
     * Shows MenuScene and sends to Server the command for dequeue and exit from lobby.
     * @param event     Event triggered by Back to Menu Button when it is clicked.
     */
    @FXML
    public void toMenu(ActionEvent event) {
        if(!isRMI) {
            senderSocket.send("m");
            senderSocket.setReturnToMenu(true);
        }
        else senderRMI.readInput("m");
        showMenuScene();
    }

    /**
     * Shows the Window Selection Scene for multiplayer.
     */
    void showWindowSelection() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/WindowSelection.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            System.out.println("Unable to load FXML WindowSelection file");
        }
        stage.setScene(new Scene(root,1000,600));
        stage.show();
    }

    /**
     * Shows the Menu Scene.
     */
    private void showMenuScene() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/MenuScene.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            System.out.println("Unable to load FXML MenuScene file");
        }
        stage.setScene(new Scene(root,1000,600));
        stage.show();
    }

    /**
     * Shows the Game Scene for multiplayer.
     */
    private void showGameScene() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/GameScene.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            System.out.println("Unable to load FXML GameScene file");
        }
        stage.setScene(new Scene(root,1200,600));
        stage.show();
    }
}
