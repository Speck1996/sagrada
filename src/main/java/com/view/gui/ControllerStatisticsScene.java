package com.view.gui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * This is the Controller for StatisticsScene, it displays general statistics and handles the possible choices to start
 * a new Multiplayer Game, a new Solo Game, to go back to Main Menu and to quit for a player, after the end of a game
 */
public class ControllerStatisticsScene {

    private Sender senderSocket;
    private SenderRMI senderRMI;
    private boolean isRMI;
    private Stage stage;

    @FXML
    private TextArea statsBox;
    @FXML
    private Button menuButton;

    /**
     * Initialization of StatisticsScene: stage is taken from
     * @see GUI
     * and isRMI is taken from
     * @see ControllerStartScene
     * For both Socket and RMI reads messages from Server and displays Global Rank and Statistics.
     */
    @FXML
    private void initialize() {
        isRMI = ControllerStartScene.getIsRMI();
        stage = GUI.getStage();
        menuButton.setVisible(true);
        menuButton.setMouseTransparent(false);

        if(!isRMI) {
            senderSocket = ControllerStartScene.getSenderSocket();
            String message;
            do {
                message = senderSocket.read();
                if(!(message.equals("@p")) && !(message.equals("#p")) && !(message.equals("Global Rank:"))) {
                    statsBox.appendText(message + "\n");
                }
            } while (!(message.equals("#p")));
            if (senderSocket.getFromMenu()) {
                menuButton.setVisible(false);
                menuButton.setMouseTransparent(true);
                senderSocket.read();
            }
        } else {
            senderRMI = ControllerStartScene.getSenderRMI();
            String[] message;
            message = senderRMI.getStatistics().split("\n");
            for (int i = 0; i<message.length; i++) {
                if(!(message[i]).equals("Global Rank:")) {
                    statsBox.appendText(message[i] + "\n");
                }
            }
            if (senderRMI.getFromMenu()) {
                menuButton.setVisible(false);
                menuButton.setMouseTransparent(true);
            }
        }
    }

    /**
     * When new Multiplayer Button is clicked, sends to Server the command in order to start a new Multiplayer Game.
     * @param event     Event triggered by Start New Multiplayer Button when it is clicked.
     */
    @FXML
    public void newMultiGame(ActionEvent event) {
        if (!isRMI) {
            if (senderSocket.getFromMenu()) senderSocket.send("mg");
            else senderSocket.send("g");
            senderSocket.setFromMenu(false);
            showWaitingScene();
        } else {
            if (senderRMI.getFromMenu()) senderRMI.readInput("mg");
            else senderRMI.readInput("g");
            senderRMI.setFromMenu(false);
            showWaitingScene();
        }
    }

    /**
     * Starts a new singleplayer game... or maybe not... but just for now.
     * @param event     Event triggered by Start New Singleplayer Button when it is clicked.
     */
    @FXML
    public void newSingleGame(ActionEvent event) {
        Alert userAlert = new Alert(Alert.AlertType.INFORMATION, "We are working for you, single player GUI will be released with next DLC, for now try it with CLI!");
        userAlert.setTitle("Singleplayer advisor");
        userAlert.setHeaderText(null);
        userAlert.showAndWait();
    }

    /**
     * When Main Menu Button is clicked, sends to Server the command in order to return to Main Menu.
     * @param event     Event triggered by Main Menu Button when it is clicked.
     */
    @FXML
    public void backToMenu(ActionEvent event) {
        if (!isRMI) {
            senderSocket.send("m");
            showMenuScene();
        } else {
            senderRMI.readInput("m");
            showMenuScene();
        }
    }

    /**
     * When Quit Button is clicked, closes spp and terminates related Client process.
     * @param event     Event triggered by Quit Button when it is clicked.
     */
    @FXML
    public void quit(ActionEvent event) {
        Platform.exit();
        System.exit(0);
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
     * Shows the Waiting Scene for multiplayer.
     */
    private void showWaitingScene() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/WaitingScene.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            System.out.println("Unable to load FXML WaitingScene file");
        }
        stage.setScene(new Scene(root,1000,600));
        stage.show();
    }
}
