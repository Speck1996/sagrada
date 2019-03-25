package com.view.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * This is the Controller for End Scene, for both RMI and Socket handles operations
 * related to an unexpected error during match initialization.
 */
public class ControllerEndScene {

    private Sender senderSocket;
    private SenderRMI senderRMI;
    private boolean isRMI;
    private Stage stage;

    /**
     * Initialization of EndScene: stage is taken from
     * @see GUI
     * and isRMI is taken from
     * @see ControllerStartScene
     * For both Socket and RMI displays a message to explain why the player arrived here, then handles operations related
     * to this particular condition, so back to Menu or search another Multiplayer game.
     */
    @FXML
    private void initialize() {
        stage = GUI.getStage();
        isRMI = ControllerStartScene.getIsRMI();
        if(!isRMI) {
            senderSocket = ControllerStartScene.getSenderSocket();
        } else {
            senderRMI = ControllerStartScene.getSenderRMI();
        }
    }

    /**
     * Sends the command associated with the action of going back to Menu.
     * @param e event triggered by the click on Menu Button.
     */
    @FXML
    public void menuButtonClicked (ActionEvent e) {
        if (!isRMI) senderSocket.send("m");
        else senderRMI.readInput("m");
        showMenuScene();
    }

    /**
     * Sends the command associated with the action of search a new Multiplayer Game.
     * @param event event triggered by the click on New Multiplayer Game Button.
     */
    @FXML
    public void newMultiButtonClicked (ActionEvent event) {
        if(!isRMI) senderSocket.send("g");
        else senderRMI.readInput("g");
        showWaitingScene();
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
            System.out.println("Unable to load FXML Waiting Scene file");
        }
        stage.setScene(new Scene(root,1000,600));
        stage.show();
    }
}
