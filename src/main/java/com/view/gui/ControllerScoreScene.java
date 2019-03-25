package com.view.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * This is the Controller for ScoreScene, it handles the final score of the game and the possibilities for the player
 * to go back to MenuScene or to go to StatisticsScene
 */
public class ControllerScoreScene {

    private Sender senderSocket;
    private SenderRMI senderRMI;
    private boolean isRMI;
    private Stage stage;
    private String[] names = new String[4];
    private Label[] players = new Label[4];
    private String[] points = new String[4];

    @FXML
    private Label player1;
    @FXML
    private Label player2;
    @FXML
    private Label player3;
    @FXML
    private Label player4;

    /**
     * Initialization of ScoreScene: stage is taken from
     * @see GUI
     * and isRMI is taken from
     * @see ControllerStartScene
     * Initializes all Labels for players name to empty value and, for Socket,
     * reads final rank and score from Server and displays it on the scene announcing winner,
     * for RMI it calls the method to do the same thing
     */
    @FXML
    private void initialize() {
        isRMI = ControllerStartScene.getIsRMI();
        stage = GUI.getStage();
        player1.setText("");
        player2.setText("");
        player3.setText("");
        player4.setText("");
        players[0] = player1;
        players[1] = player2;
        players[2] = player3;
        players[3] = player4;

        if (!isRMI) {
            senderSocket = ControllerStartScene.getSenderSocket();
            int i = 0;
            String message;

            do {
                message = senderSocket.read();
                if (message.endsWith("points")) {
                    names[i] = message.split(" ")[2];
                    if (message.contains("inactive")) {
                        points[i] = message.split(" ")[5];
                    } else {
                        points[i] = message.split(" ")[4];
                    }
                    i++;
                }
            } while (!(message.equals("#p")));

            for (int j = 0; j < i; j++) {
                players[j].setText(names[j] + " with " + points[j] + " points");
            }

        } else {
            senderRMI = ControllerStartScene.getSenderRMI();
            RMIgui r = ControllerStartScene.getRmi();
            r.setScoreControl(this);
        }
    }

    /**
     * For RMI, it reads messages from Server in order to take final rank and score.
     * After this it displays on the scene all collected information.
     */
    void scoreForRMI() {
        int i = 0;
        String message;

        message = senderRMI.getRank();

        String[] line = message.split("\n");
        for(int k=0; k<line.length; k++) {
            if (line[k].endsWith("points")) {
                names[i] = line[k].split(" ")[2];
                if (line[k].contains("inactive")) {
                    points[i] = line[k].split(" ")[5];
                } else {
                    points[i] = line[k].split(" ")[4];
                }
                i++;
            }
        }

        for (int j = 0; j < i; j++) {
            players[j].setText(names[j] + " with " + points[j] + " points");
        }
    }

    /**
     * Sends to Server the player command in order to go to Statistics Scene when he or she clicks on
     * Statistics Button, after this calls method to Show Statistics Scene
     * @param event     Event triggered by Statistics Button when it is clicked
     */
    @FXML
    public void statsButtonClicked(ActionEvent event) {
        showStatisticsScene();
    }

    /**
     * Sends to Server the player command in order to go back Main Menu when he or she clicks on
     * Menu Button, after this calls method to Show Menu Scene
     * @param event     Event triggered by Main Menu Button when it is clicked
     */
    @FXML
    public void menuButtonClicked(ActionEvent event) {
        if(!isRMI) {
            String message;
            do {
                message = senderSocket.read();
            } while (!(message.equals("#p")));
            senderSocket.send("m");
            showMenuScene();
        } else {
            senderRMI.readInput("m");
            showMenuScene();
        }
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
}
