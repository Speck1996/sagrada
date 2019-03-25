package com.view.shapes;

import javafx.beans.binding.NumberBinding;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

/**Class representing the node of the cell of a die in the {@link VectorialRoundBoard}
 */
public class RoundBoardSpace extends StackPane {

    /** round coordinate of the space
     */
    private int round;
    /**die index coordinate of the space
     */
    private int dieIndex;

    /**the rectangle representing the RoundBoardSpace
     */
    private Rectangle spaceDraw;
    /**{@link NumberBinding} used to mantain proportions during node resize
     */
    private NumberBinding binding;

    /**String corresponding to the dice put in the node, set to -- if no dice is on the space
     */
    private String diceOnSpace;

    /**{@link DiceRectangleFiller} used to fill the rectangle with the right rectangle image
     */
    private DiceRectangleFiller filler;


    /**This method constructs the RoundBoardSpace
     * @param boardSpaceString string representing the die
     * @param round round coordinate of the node
     * @param dieIndex die index coordinate of the noded
     * @param binding {@link NumberBinding} used to mantain proportions while resizing
     */
    public RoundBoardSpace(String boardSpaceString, int round, int dieIndex, NumberBinding binding){
        //Drawing a Rectangle
        Rectangle rectangle = new Rectangle();

        //Setting the properties of the rectangle

        //setting some default properties
        this.setMinSize(0,0);
        this.setHeight(60);
        this.setWidth(60);

        //binding the dimensions
        this.binding = binding;
        this.prefHeightProperty().bind(binding);
        this.prefWidthProperty().bind(binding);


        //setting properties for the rectangle representing the dice
        rectangle.setWidth(60);
        rectangle.setHeight(60);

        //binding the dimensions

        rectangle.widthProperty().bind(binding);
        rectangle.heightProperty().bind(binding);

        //filling the rectangle with the right image
        filler = new DiceRectangleFiller();


        //see the method below, basically filling the rectangle with the right shade/color, if it is the first
        //die of a round the background roundboard image is set
        if(dieIndex == 0){
            this.fillFirstRoundSpace(rectangle,round);
        }else{
            rectangle.setFill(Color.TRANSPARENT);
        }

        //setting to attributes
        this.round = round;
        this.dieIndex = dieIndex;

        this.spaceDraw = rectangle;
        this.diceOnSpace = boardSpaceString;



        //adding the space
        getChildren().addAll(rectangle);
        if(!boardSpaceString.equals("--")){
            this.drawDiceOnSpace(boardSpaceString);

        }

//        this.setPickOnBounds(false);
////        rectangle.setPickOnBounds(false);
    }

    /**Method used for testing purpose: prints the round and die index coordinates
     * @param rectangle used to set the mouse click event
     */
    private void setMouseClick(Rectangle rectangle){
        //this will be useful maybe, or maybe not
        rectangle.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("Round:" +  (round+1)+ "\nDieIndex:" + (dieIndex+1) );
            }
        });
    }

    /**Method used to create a rectangle and filling it with the right image based on the dice string
     * @param diceString the string used to pick the right image
     */
    private void drawDiceOnSpace(String diceString ){


        //Drawing a Rectangle
        Rectangle rectangle = new Rectangle();

        this.setMinSize(0,0);


        //Setting the properties of the rectangle
        rectangle.setWidth(40);
        rectangle.setHeight(40);
        rectangle.widthProperty().bind(binding.multiply(0.75));
        rectangle.heightProperty().bind(binding.multiply(0.75));


        //picking the right dice image
        filler.fillDiceColor(rectangle,diceString);

        //setting the mouse click event
        setMouseClick(rectangle);

        //adding the rectangle node to the stackpane children list
        getChildren().add(1,rectangle);
        this.diceOnSpace = diceString;


    }

    /**This methods fills the first die index rectangle with the background image indicating the turn
     * @param rectangle the rectangle that has to be filled
     * @param round the corresponding round of the RoundBoardSpace, used to get the right image
     */
    private void fillFirstRoundSpace(Rectangle rectangle, int round){
        String pathHelper = new String();
        switch (round){
            case 0:
                pathHelper = "One";
                break;
            case 1:
                pathHelper = "Two";
                break;
            case 2:
                pathHelper = "Three";
                break;
            case 3:
                pathHelper = "Four";
                break;
            case 4:
                pathHelper = "Five";
                break;
            case 5:
                pathHelper = "Six";
                break;
            case 6:
                pathHelper = "Seven";
                break;
            case 7:
                pathHelper = "Eight";
                break;
            case 8:
                pathHelper = "Nine";
                break;
            case 9:
                pathHelper = "Ten";
        }

        Image image = new Image(getClass().getResource("/assets/RoundSpaces/RoundSpace"+pathHelper+".png").toExternalForm());
        rectangle.setFill(new ImagePattern(image));

    }


    /**Updates the view of the rectangle displayed by this node (in case a die is changed)
     * @param diceString string corresponding to user input
     */
    void updateSpaceNodeView(String diceString){
        if(!diceString.equals(this.diceOnSpace)){
            if(!this.diceOnSpace.equals("--")){
                //an update to what to display is needed
                this.getChildren().remove(1);
            }
          this.drawDiceOnSpace(diceString);
        }
    }

    /**Getter for the round attribute
     * @return the round attribute
     */
    public int getRound() {
        return round;
    }

    /**Getter for die index attribute
     * @return the die index attribute
     */
    public int getDieIndex() {
        return dieIndex;
    }

}
