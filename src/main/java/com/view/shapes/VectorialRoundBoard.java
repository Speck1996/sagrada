package com.view.shapes;

import com.model.gameboard.RoundBoard;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import java.util.ArrayList;
import java.util.List;


/**Class used to represents RoundBoard in the GUI: basically is a VBOX
 * containing an expandable stackpane for every round
 */
public class VectorialRoundBoard extends VBox {

    /** Array of stackPane containing the stackpane corresponding to a expandable round cell*/
    private StackPane[] diceBoxes;

    /**predefined binding properties value, obtained through testing
     */
    final NumberBinding binding = Bindings.min(heightProperty(), heightProperty());
    final NumberBinding spaceBinding = binding.multiply(0.08);

    /**List of list containing all the RoundBoardSpace in this VBOX
     */
    private List<List<RoundBoardSpace>> roundBoardSpaces;

    /**Constructs the vectorial roundBoard: for every round (10 by default) it adds a stackPane to the diceBox and creates and add
     *a RoundBoardSpace on it (the one representing the first cell), the diceBox is added as a children of this class, and every
     * RoundBoardSpace created is added to the roundBoardSpaces List
     * It also sets all the dimension and binding properties and the expandability of the diceBox through mouse events
     */
    public VectorialRoundBoard(){

        //setting some predifined values
        this.setPrefWidth(100);
        this.setPrefHeight(100);
        this.setMaxWidth(200);
        this.setStyle("-fx-background-color: transparent");
        this.setPrefWidth(200);
        this.setFillWidth(false);
        this.setAlignment(Pos.CENTER);

        //setting spacing property value
        this.setSpacing(20);
        this.setPadding(new Insets(20,20,20,20));

        // array of contained RoundBoardSpace
        diceBoxes = new StackPane[10];

        //binding those integers  to the spacing and padding property for resizability purpose
        IntegerProperty padding = new SimpleIntegerProperty(20);
        IntegerProperty resetDicePosition = new SimpleIntegerProperty(0);
        padding.bind(binding.multiply(0.06));

        this.spacingProperty().bind(padding);
        this.paddingProperty().bind(Bindings.createObjectBinding(() -> new Insets(padding.doubleValue()/2), padding));


        //initializing the array of RoundBoardSpace
        roundBoardSpaces = new ArrayList<>();


        //initializing the first RoundBoardSpace for every round, this is the one with the background image
        for(int i = 0; i < 10; i++){

            //setting some property for the dicebox that will contain all the rectangles of the dice put on the seleted round
            StackPane diceBox = new StackPane();
            diceBox.setStyle("-fx-background-color: transparent");
            diceBox.setSnapToPixel(false);

            //initializng the list of RoundBoardSpace of the i-round
            List<RoundBoardSpace> dice = new ArrayList<>();

            //some property
            diceBox.setPrefHeight(100);
            diceBox.setPrefWidth(100);

            //creating the diceSpace and adding it to the diceBox stack
            RoundBoardSpace diceSpace = new RoundBoardSpace("--",i,0,spaceBinding);
            diceBox.getChildren().add(diceSpace);
            diceBox.setAlignment(Pos.CENTER);

            //some properties setting for the dicebox
            diceBox.setPrefHeight(60);
            diceBox.setPrefWidth(60);
            diceBox.prefWidthProperty().bind(spaceBinding);
            diceBox.prefHeightProperty().bind(spaceBinding);
            dice.add(diceSpace);

            //setting the expandability, created by translating the RoundBoardSpace
            diceBox.setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    for(int i = 0; i < diceBox.getChildren().size() ; i++){
                        diceBox.getChildren().get(i).translateXProperty().bind(binding.multiply(i).divide(12).multiply(-1));
                    }
                }
            });
            diceBox.setOnMouseExited(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    for(int i =(diceBox.getChildren().size()-1); i >= 0 ; i--){
                        diceBox.getChildren().get(i).translateXProperty().bind(resetDicePosition);

                    }
                }
            });

            //adding the roundboardspace to the list of roundboard space to
            roundBoardSpaces.add(dice);

            //setting the dicebox and addin it to the children of the vbox
            diceBoxes[i] = diceBox;
            this.getChildren().add(diceBox);
        }


        //to avoid the creation of  graphic artifacts when resizing
        this.setSnapToPixel(false);
        this.setVgrow(this,Priority.ALWAYS);
    }


    /**Parses the given string to obtain an array of string that has the string list of dice
     * @param roundBoard the string representing the roundboard
     * @return an array of strings, one for every round each containing the list of the contained dice strings
     * @see RoundBoard#toString()
     */
    private String[] roundBoardParsing(String roundBoard){

        String[] tmp = roundBoard.split("\n");
        String[] roundTokens = new String[tmp.length-1];

        //die lists starts from the token in position i+1
        for(int i = 0; i < (tmp.length-1); i++){
            roundTokens[i] = tmp[i+1];
        }

        return roundTokens;

    }


    /**Parses the string representing the list of dice in a round to get an array of dice strings
     * @param roundDice the string that has to be parsed
     * @return the array of dice strings obtained
     * @see RoundBoard#toString()
     */
    private String[] roundDiceParsing(String roundDice){
        String[] tmp = roundDice.split(" ");
        String[] tokens = new String[tmp.length-2];

        //this offset betwenn the to arrays is created by the Roundboard to string ("Round : ")
        for(int i = 0; i < (tmp.length-2); i++){
            tokens[i] = tmp[i+2];
        }

        return tokens;
    }


    /**Takes the given string, parses it to generate the diceStrings.
     * It calls for the already present roundBoardSpace (this is information is obtained checking
     * the roundBoardSpace size) the update view, and creates new RoundBoardSpaces if the dice string array is
     * longer than the RoundBoardSpace
     * @param roundBoardString the string representing the roundboard
     */
    public void updateView(String roundBoardString) {
        String[] rounds = this.roundBoardParsing(roundBoardString);
        int i;
        for (i = 0; i < rounds.length; i++) {


            String[] diceStrings = this.roundDiceParsing(rounds[i]);
            int j;

            //updating already created spaces
            for ( j = 0; j < roundBoardSpaces.get(i).size(); j++) {
                roundBoardSpaces.get(i).get(j).updateSpaceNodeView(diceStrings[j]);
            }

            //creating the new spaces
            for(int z = j; z<diceStrings.length;z++ ){

                RoundBoardSpace diceSpace = new RoundBoardSpace(diceStrings[z], i, z,spaceBinding);
                diceBoxes[i].getChildren().add(diceSpace);
                roundBoardSpaces.get(i).add(diceSpace);

            }

        }


    }

    /**Getter for the {@link VectorialRoundBoard#roundBoardSpaces
     * @return the roundBoardSpaces list of list
     */
    public List<List<RoundBoardSpace>> getRoundBoardSpaces() {
        return roundBoardSpaces;
    }
}
