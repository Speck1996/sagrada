package com.view.shapes;

import javafx.beans.binding.NumberBinding;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.input.MouseEvent;

/**Class that represent the DiceSpace in the window: it consists in a stackpane with one or two rectangle depending
 * if a dice is present or not
 */
public class DiceSpaceNode extends StackPane {
    /**The corresponding row of the node
     */
    private int row;
    /**The corresponding column of the node
     */
    private int column;
    /**Background rectangle of the stackpane
     */
    Rectangle spaceDraw;
    /**Usec to fill the dice rectangle with right image
     */
    private DiceRectangleFiller filler;

    /**String representing the dice on the space, set -- if there is no die
     */
    private String diceOnSpace;


    /**Constructs the DiceSpaceNode creating and filling the rectangle representing the cell with the right shage/color
     * based on the given string
     * @param spaceString the string representing the space
     * @param row the row that has to be associated to the DiceSpaceNode
     * @param column the column that has to be associated to the DiceSpaceNode
     * @param dimension the preferred dimension for the node
     * @param binding the binding used for the resizing
     */
    public DiceSpaceNode(String spaceString, int row, int column , double dimension, NumberBinding binding){
        //Drawing a Rectangle
        Rectangle rectangle = new Rectangle();

        //Setting the properties of the rectangle

       this.setMinSize(0,0);

        rectangle.setWidth(dimension);
        rectangle.setHeight(dimension);
        this.setHeight(dimension);
        this.setWidth(dimension);
        // the "\\" is needed because | is a special character for the split method
        String[] tmp = spaceString.split("\\|");


        //binding proportions for resizability
        this.prefHeightProperty().bind(binding);
        this.prefWidthProperty().bind(binding);
        rectangle.heightProperty().bind(binding);
        rectangle.widthProperty().bind(binding);
        //this will be useful for the die drawing implementation
        this.diceOnSpace = tmp[1];


        //see the method below, basically filling the rectangle with the right shade/color
        fillSpace(rectangle,tmp[0]);

        this.row = row;
        this.column = column;

        //setting action on mouseclick, maybe useless if there is a controller, maybe not
        this.setMouseClick();
        this.spaceDraw = rectangle;


        filler = new DiceRectangleFiller();

        //adding the space
        getChildren().addAll(rectangle);
    }


    /**Method used for testing purpose: prints the row and column attribute
     */
    private void setMouseClick(){
        //this will be useful maybe, or maybe not
        this.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("Row:" +  (row+1)+ "\nColumn:" + (column+1) );
            }
        });
    }

    /**Creates a new rectangle and fill it with the dice image corresponding to the dice tring
     * @param diceString the string corresponding to the dice that has to be added
     */
    private void drawDiceOnSpace(String diceString ){


        //The new die is different, removing his rectangle and creating the new one if there is a new die
        //or returning without creating new rectangles
        if(!diceString.equals(this.diceOnSpace) && this.getChildren().size() > 1){
            this.getChildren().remove(1);
            if(diceString.equals("--")){
                this.diceOnSpace = "--";
                return;
            }
        }

        double width = spaceDraw.getWidth()-10;
        double height = spaceDraw.getHeight()-10;



        //Drawing a Rectangle
        Rectangle rectangle = new Rectangle();

        //Setting the properties of the rectangle
        rectangle.setWidth(width);
        rectangle.setHeight(height);
        rectangle.widthProperty().bind(this.widthProperty().subtract(this.widthProperty().divide(8)));
        rectangle.heightProperty().bind(this.widthProperty().subtract(this.widthProperty().divide(8)));
        //calculating the center of the space
        double centerXPosition = this.getChildren().get(0).getLayoutX() + spaceDraw.getWidth()/2d;
        double centerYPosition = this.getChildren().get(0).getLayoutY() + spaceDraw.getHeight()/2d;

        //setting the dice image right in the center of the space
        rectangle.setX(centerXPosition-width/2);
        rectangle.setY(centerYPosition-height/2);

        //picking the right dice image
//        fillDiceColor(rectangle,diceString);
        filler.fillDiceColor(rectangle,diceString);

        getChildren().add(1,rectangle);
        this.diceOnSpace = diceString;


    }


    /**Method used to update the dice visualized on the node, it does nothing if the dice is the same
     * @param diceString the dice that has to be visualized on the node
     */
    void updateSpaceNodeView(String diceString){
        if(!diceString.equals(this.diceOnSpace)) {
            //update of the view needed
            this.drawDiceOnSpace(diceString);
        }
    }


    /**Method used to fill the background color/image of the node with the one corresponding to the string
     * @param mySpace the rectangle that has to be filled
     * @param spaceString the string representing the space
     */
    private  void fillSpace(Rectangle mySpace, String spaceString){
        char color = spaceString.charAt(0);
        char shade = spaceString.charAt(1);


        switch (shade) {

            //filling the rectangle with the right image associated to the space
            case '1':
                Image shadeOne = new Image(getClass().getResource("/assets/DiceSpaces/shadeOne.png").toExternalForm());
                mySpace.setFill(new ImagePattern(shadeOne));
                return;
            case '2':
                Image shadeTwo = new Image(getClass().getResource("/assets/DiceSpaces/shadeTwo.png").toExternalForm());
                mySpace.setFill(new ImagePattern(shadeTwo));
                return;
            case '3':
                Image shadeThree = new Image(getClass().getResource("/assets/DiceSpaces/shadeThree.png").toExternalForm());
                mySpace.setFill(new ImagePattern(shadeThree));
                return;
            case '4':
                Image shadeFour = new Image(getClass().getResource("/assets/DiceSpaces/shadeFour.png").toExternalForm());
                mySpace.setFill(new ImagePattern(shadeFour));
                return;
            case '5':
                Image shadeFive = new Image(getClass().getResource("/assets/DiceSpaces/shadeFive.png").toExternalForm());
                mySpace.setFill(new ImagePattern(shadeFive));
                return;
            case '6':
                Image shadeSix = new Image(getClass().getResource("/assets/DiceSpaces/shadeSix.png").toExternalForm());
                mySpace.setFill(new ImagePattern(shadeSix));
                return;

        }

        switch(color){
            //filling the rectangle with the right color
            case 'Y':
                mySpace.setFill(Color.YELLOW);
                break;
            case 'G':
                mySpace.setFill(Color.GREEN);
                break;
            case 'R':
                mySpace.setFill(Color.RED);
                break;
            case 'P':
                mySpace.setFill(Color.PURPLE);
                break;
            case 'B':
                mySpace.setFill(Color.BLUE);
                break;
            default:
                mySpace.setFill(Color.WHITE);
        }
    }


    /**Getter for the row attributes
     * @return the row attribute
     */
    public int getRow() {
        return row;
    }


    /**Getter for the column attributes
     * @return the column attribute
     */
    public int getColumn() {
        return column;
    }
    /**Setting the node resizable attribute to true
     */
    @Override
    public boolean isResizable() {
        return true;
    }

}
