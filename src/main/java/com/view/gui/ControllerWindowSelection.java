package com.view.gui;

import com.view.shapes.VectorialWindow;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * This is the Controller for WindowSelection Scene, for both RMI and Socket handles operations
 * related to window selection by the player.
 */
public class ControllerWindowSelection {

    private Sender senderSocket;
    private SenderRMI senderRMI;

    @FXML
    private BorderPane window1;
    @FXML
    private BorderPane window2;
    @FXML
    private BorderPane window3;
    @FXML
    private BorderPane window4;
    @FXML
    private Label selection;

    private Stage stage;
    private VectorialWindow[] windows = new VectorialWindow[4];
    private boolean isRMI;
    private int counter = 0;
    private String[] rmiChoices = new String[4];

    /**
     * Initialization of WindowSelectionScene: stage is taken from
     * @see GUI
     * and isRMI is taken from
     * @see ControllerStartScene
     * For Socket displays the four different window available for player, after selection shows GameScene;
     * for RMI passes this controller to RMIgui in order to give it the possibility to do same things of Socket.
     */
    @FXML
    private void initialize() {
        isRMI = ControllerStartScene.getIsRMI();
        stage = GUI.getStage();
        if (!isRMI) {
            senderSocket = ControllerStartScene.getSenderSocket();
            senderSocket.setReturnToMenu(false);
            windowSelection();
            window1.setCenter(windows[0]);
            window2.setCenter(windows[1]);
            window3.setCenter(windows[2]);
            window4.setCenter(windows[3]);
            new Thread(new WindowSelectionReaderSocket(this, senderSocket.getIn(), stage)).start();
        }
        else {
            senderRMI = ControllerStartScene.getSenderRMI();
            RMIgui r = ControllerStartScene.getRmi();
            r.setWindControl(this);
            senderRMI.notifyWindowChoices();
        }
    }

    /**
     * For Socket reads the messages in order to displays the four windows passed by Server.
     */
    private void windowSelection() {
        String message;
        int i = 1;
        do {
            message = senderSocket.read();
            String window = "";
            if (message.equals("Choice n° " + i)) {
                for (int p=0; p<5; p++) {
                    message = senderSocket.read();
                    window = window + message + "\n";
                }
                windows[i-1] = new VectorialWindow(window);
                i++;
            }
        } while (!(message.endsWith("#w")));
    }

    /**
     * For RMI reads the messages in order to displays the four windows passed by Server.
     * @param s String passed from RMIgui that contains the four windows.
     */
    void printWindows(String s) {
        String window = "";
        String[] line = s.split("\n");
        for (int i=0; i<line.length; i++) {
            if (line[i].startsWith("Choice")) {
                for (int j=i+1;j<i+6;j++) {
                    window = window + line[j] + "\n";
                }
                rmiChoices[counter] = window;
                window = "";
                counter++;
            }
        }
        window1.setCenter(new VectorialWindow(rmiChoices[0]));
        window2.setCenter(new VectorialWindow(rmiChoices[1]));
        window3.setCenter(new VectorialWindow(rmiChoices[2]));
        window4.setCenter(new VectorialWindow(rmiChoices[3]));
    }

    /**
     * Allows RMI client to launch GameScene.
     */
    void launchGame() {
        if (isRMI) {
            showGameScene();
        }
    }

    /**
     * Set to First Window the choice of the player and calls the method in order to sends to Server this information.
     * @param event     Event triggered by Pick First Window Button when it is clicked.
     */
    public void selectedOne (ActionEvent event) {
        String cmd = "w1";
        onButtonPress(cmd);
    }

    /**
     * Set to Second Window the choice of the player and calls the method in order to sends to Server this information.
     * @param event     Event triggered by Pick Second Window Button when it is clicked.
     */
    public void selectedTwo (ActionEvent event) {
        String cmd = "w2";
        onButtonPress(cmd);
    }

    /**
     * Set to Third Window the choice of the player and calls the method in order to sends to Server this information.
     * @param event     Event triggered by Pick Third Window Button when it is clicked.
     */
    public void selectedThree (ActionEvent event) {
        String cmd = "w3";
        onButtonPress(cmd);
    }

    /**
     * Set to Fourth Window the choice of the player and calls the method in order to sends to Server this information.
     * @param event     Event triggered by Pick Fourth Window Button when it is clicked.
     */
    public void selectedFour (ActionEvent event) {
        String cmd = "w4";
        onButtonPress(cmd);
    }

    /**
     * Sends to Server the number of the window chosen by the player.
     * @param cmd     Which window the player selected.
     */
    private void onButtonPress(String cmd) {
        if (!isRMI) {
            senderSocket.send(cmd);
            selection.setText("Window selected, now wait for other players");
        }
        else {
            selection.setText("Window selected, now wait for other players");
            senderRMI.readInput(cmd);
        }
    }

    /**
     * Shows the GameScene for multiplayer.
     */
    void showGameScene() {
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
