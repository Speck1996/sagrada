package com.view.gui;

import javafx.fxml.FXML;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * This is the Controller for ToolCardScene, it displays the tool card passed from ControllerGameScene in order to zoom it
 */
public class ControllerCardScene {

    @FXML
    private ImageView card;
    @FXML
    private Label cost;

    private Sender senderSocket;
    private SenderRMI senderRMI;
    private boolean isRMI;

    /**
     * Initialization of ToolCardScene: stage is taken from
     * @see GUI
     * and isRMI is taken from
     * @see ControllerStartScene
     * For both Socket and RMI it takes from Sender the Tool Card and it displays in a bigger dimension
     */
    @FXML
    private void initialize() {
        isRMI = ControllerStartScene.getIsRMI();
        Image image;
        String price;
        if(!isRMI) {
            senderSocket = ControllerStartScene.getSenderSocket();
            image = senderSocket.getCard();
            price = senderSocket.getCost();
            if (!price.equals("")) cost.setText("My cost is " + price);
            card.setImage(image);
        } else {
            senderRMI = ControllerStartScene.getSenderRMI();
            image = senderRMI.getCard();
            price = senderRMI.getCost();
            if (!price.equals("")) cost.setText("My cost is " + price);
            card.setImage(image);
        }
    }
}