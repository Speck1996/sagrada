package com.view.shapes;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Control;
import javafx.scene.layout.*;

import static com.model.patterns.WindowPatternCard.COL;
import static com.model.patterns.WindowPatternCard.ROW;


/**Class representing the WindowPatternCard: it's an HBOX that contains a VBOX. The VBOX contains
 * a gridpane with the spacenodes depending on the number of rows and columns set, and the window info node
 */
public class VectorialWindow extends HBox{


    /**{@link NumberBinding} used to mantain proportions during resize
     */
    final NumberBinding binding = Bindings.min(widthProperty(), heightProperty());

    /**Matrix containing the DiceSpaceNodes
     */
    private DiceSpaceNode[][] nodeMatrix;


    /**Constructs the Vectorial window initializing the VBOX and its children (the GRIDPANE and the WINDOWINFONODE)
     * @param windowString string corresponding to the window
     */
    public VectorialWindow(String windowString){
        final VBox vBox = new VBox();

        //to avoid flickering during resize
        vBox.setSnapToPixel(false);

        //background color of the vbox
        vBox.setStyle("-fx-background-color: black;");


        String[] rowsString = this.parseWindowString(windowString);
        nodeMatrix = new DiceSpaceNode[ROW][COL];
        //splitting the window string first by rows and then by column
        String[] tmpRow = rowsString[2].split("\n");
        String[] tmpColumn;


        //this will be merged toghether with the windowinfo node in the stack
        GridPane root = new GridPane();

        //parameters for resizability
        vBox.setFillWidth(true);
        VBox.setVgrow(root, Priority.ALWAYS);

        vBox.prefWidthProperty().bind(binding);

        vBox.setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);

        //used to mantain the padding during resize of the window
        IntegerProperty padding = new SimpleIntegerProperty(7);
        padding.bind(binding.divide(50));




        int i;
        int j ;
        double dimension = 50;
        for( i = 0;i < tmpRow.length; i++) {
            //splitting the row in his spaces(example {NO|--,G1|--,R2|--,B4|--,P5|--}
            tmpColumn = tmpRow[i].split("\t");

            //binding to maintain the space between the nodes when resizing
            NumberBinding spaceBinding = root.widthProperty().subtract(padding.multiply((tmpColumn.length+1))).divide(tmpColumn.length);
            for( j = 0 ; j < tmpColumn.length; j++) {

                // creating my beatiful beatiful node
                DiceSpaceNode node = new DiceSpaceNode( tmpColumn[j], i, j,dimension, spaceBinding);
                nodeMatrix[i][j] = node;

                // adding node to grid, don't get the reason why columns come before rows but whatever...
                root.add(node,j,i);



            }


        }

        //setting spacing between nodes
        root.setHgap(7);
        root.setVgap(7);
        root.setPadding(new Insets(7,7,0,7));
        root.vgapProperty().bind(padding);
        root.hgapProperty().bind(padding);
        root.paddingProperty().bind(Bindings.createObjectBinding(() -> new Insets(padding.doubleValue()), padding));

        // style the grid so that it has a background and gaps around the grid and between the
        // grid cells so that the background will show through as grid lines.


        root.setStyle("-fx-background-color: black;");
        // turn layout pixel snapping off on the grid so that grid lines will be an even width.
        root.setSnapToPixel(false);

        //adding grid and window to the stack

        NumberBinding windowInfoBinding = root.heightProperty().divide(8);
        BorderPane windowInfo = new WindowInfoNode(windowInfoBinding, rowsString[0],Integer.parseInt(rowsString[1]));

        windowInfo.setSnapToPixel(false);
        root.setMinWidth(COL*dimension+(COL+1)*root.getHgap());
        root.setMinHeight(ROW*dimension+(ROW+1)*root.getVgap());


        vBox.getChildren().add(root);

        VBox.setVgrow(windowInfo,Priority.SOMETIMES);
        vBox.getChildren().add(windowInfo);


        getChildren().add(vBox);



        HBox.setHgrow(this, Priority.ALWAYS);
        this.setSnapToPixel(false);

    }


    /**Method used to parse the window string in his main components (grid, title, difficulty)
     * @param windowString the string that has to be parsed
     * @return an array of string containing the string of the main components
     */
    private String[] parseWindowString(String windowString){
        String[] vectorialWindowInfoString = new String[3];

        //id + " - " + name that's why I pick the second token (index 1)
        String[] splittedWindowString = windowString.split(" - ");

        //name saved here
        vectorialWindowInfoString[0] = splittedWindowString[1];



        //continuing reading the string... diff:difficulty, splitting this too and picking second token
        vectorialWindowInfoString[1] = splittedWindowString[2].substring(splittedWindowString[2].indexOf(':')+1,splittedWindowString[2].indexOf(':')+2);

        //now picking the rest of the window string (string format of all the spaces, they starts right after the first \n"
        vectorialWindowInfoString[2] = splittedWindowString[2].substring(splittedWindowString[2].indexOf('\n')+1);

        return vectorialWindowInfoString;
    }


    /**Method used to update the rendering of the window, used when a dicespace has a new dice or the one it had was removed
     * @param windowString string parsed to update the rendering of the window
     */
    public void updateView(String windowString){
        String[] attributes = this.parseWindowString(windowString);
        String[] tmpRow = attributes[2].split("\n");
        String[] tmpColumn;


        for(int i = 0; i < ROW; i++){
            //splitting the row in his spaces(example {NO|--,G1|--,R2|--,B4|--,P5|--}
            tmpColumn = tmpRow[i].split("\t");

            for(int j = 0; j < COL;j++){

                String diceString = tmpColumn[j].split("\\|")[1];
                nodeMatrix[i][j].updateSpaceNodeView(diceString);
            }
        }
    }

    /**Getter for the {@link VectorialWindow#nodeMatrix}
     * @return the matrix of diceSpace nodes
     */
    public DiceSpaceNode[][] getNodes() {
        return nodeMatrix;
    }
}
