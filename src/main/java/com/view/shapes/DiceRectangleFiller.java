package com.view.shapes;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;


/**Class that contains only a method used to fill a given rectangle with the right corresponding dice image
 */
 final class DiceRectangleFiller {

    /**Checks the dice representing string and based on it, fills the rectangle with the corresponding dice image
     * @param diceRectangle the rectangle that has to be filled
     * @param diceString the string representing the dice
     */
    final void fillDiceColor(Rectangle diceRectangle, String diceString){
        String[] pathHelper = new String[2];

        switch (diceString.charAt(0)){
            case 'P':
                pathHelper[0] = "Purple";
                break;
            case 'R':
                pathHelper[0] = "Red";
                break;
            case 'B':
                pathHelper[0] = "Blue";
                break;
            case 'G':
                pathHelper[0] = "Green";
                break;
            case 'Y':
                pathHelper[0] = "Yellow";
                break;
        }
        switch (diceString.charAt(1)){
            case '1':
                pathHelper[1] = "One";
                break;
            case '2':
                pathHelper[1] = "Two";
                break;
            case '3':
                pathHelper[1] = "Three";
                break;
            case '4':
                pathHelper[1] = "Four";
                break;
            case '5':
                pathHelper[1] = "Five";
                break;
            case '6':
                pathHelper[1] = "Six";
                break;
        }

        Image diceImage = new Image(getClass().getResource("/assets/Dice/Glass"+pathHelper[0]+"Dice"+pathHelper[1]+".png").toExternalForm());
        diceRectangle.setFill(new ImagePattern(diceImage));

    }

}
