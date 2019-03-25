package com.view.gui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.*;

/**
 * This is the Controller for MenuScene, it handles the possible choices of a player in the main menu.
 */
public class ControllerMenuScene {

    private Sender senderSocket;
    private SenderRMI senderRMI;
    private boolean isRMI;
    private Stage stage;
    @FXML
    private Label playerName;
    @FXML
    private GridPane grid;

    /**
     * Initialization of MenuScene: stage is taken from
     * @see GUI
     * and isRMI is taken from
     * @see ControllerStartScene
     * It is taken the correct Sender (Socket or RMI) and set Label playerName to the username value.
     */
    @FXML
    private void initialize() {
        isRMI = ControllerStartScene.getIsRMI();
        stage = GUI.getStage();

        if (!isRMI) {
            senderSocket = ControllerStartScene.getSenderSocket();
            playerName.setText(senderSocket.getUsername());
        } else {
            senderRMI = ControllerStartScene.getSenderRMI();
            RMIgui r = ControllerStartScene.getRmi();
            r.setMenuControl(this);
            playerName.setText(senderRMI.getUsername());
        }
    }

    /**
     * Starts a new multiplayer game when client click Multiplayer Game Button.
     * @param event     Event triggered by Start New Multiplayer Button when it is clicked.
     */
    @FXML
    public void startMultiplayer(ActionEvent event) {
        String choice = "mg";
        if (!isRMI) {
            senderSocket.send(choice);
            showWaitingScene();
        }
        else {
            senderRMI.readInput(choice);
            showWaitingScene();
        }
    }

    /**
     * Starts a new singleplayer game... or maybe not... but just for now.
     * @param event     Event triggered by Start New Singleplayer Button when it is clicked.
     */
    @FXML
    public void startSingleplayer(ActionEvent event) {
        Alert userAlert = new Alert(Alert.AlertType.INFORMATION, "We are working for you, single player GUI will be released with next DLC, for now try it with CLI!");
        userAlert.setTitle("Singleplayer advisor");
        userAlert.setHeaderText(null);
        userAlert.showAndWait();
    }

    /**
     * Shows the global statistics related to this game when client click Statistics Button.
     * @param event     Event triggered by Statistics Button when it is clicked.
     */
    @FXML
    public void statistics(ActionEvent event) {
        String choice = "gs";
        if (!isRMI) {
            senderSocket.send(choice);
            senderSocket.setFromMenu(true);
            senderSocket.read();
            senderSocket.read();
            showStatisticsScene();
        }
        else {
            senderRMI.readInput(choice);
            senderRMI.setFromMenu(true);
            showStatisticsScene();
        }
    }

    /**
     * Closes the entire app and terminates process.
     * @param event     Event triggered by Quit Button when it is clicked.
     */
    @FXML
    public void quit(ActionEvent event) {
        Platform.exit();
        System.exit(0);
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

    /**
     * Shows the Statistics Scene.
     */
    private void showStatisticsScene() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/StatisticsScene.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            System.out.println("Unable to load FXML StatisticsScene file");
        }
        stage.setScene(new Scene(root,1000,600));
        stage.show();
    }
}
