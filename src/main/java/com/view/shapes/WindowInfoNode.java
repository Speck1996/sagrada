package com.view.shapes;

import javafx.beans.binding.NumberBinding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**Class that creates the window info node for the Vectorial window: is a borderPane with a text field in the center
 * and the difficulty circles on the right
 */
public class WindowInfoNode extends BorderPane {

    /**Constructs the window info node: it creats a black rectangle that will fill the pane,
     * an hbox that contains the text corresponding to the window title, and an hbox of white circles
     * corresponding to the difficulty card
     * @param binding used to bind proportions
     * @param name string corresponding to the name of the window
     * @param difficulty integer corresponding to the number of circles that the cards has
     */
    public WindowInfoNode(NumberBinding binding, String name, int difficulty) {
        //Drawing a Rectangle
        Rectangle rectangle = new Rectangle();

        //Setting the properties of the rectangle
        rectangle.widthProperty().bind(this.widthProperty());

        this.prefHeightProperty().bind(binding);


        //filling the rectangle, setting and adding the font
        rectangle.setFill(Color.BLACK);
        Text windowName = new Text(name);

        this.setSnapToPixel(false);
        HBox textBox = new HBox(windowName);


        windowName.setFill(Color.WHITE);
        windowName.setFont(new Font("Book Antiqua", 16));
        windowName.setStyle("-fx-font-weight: bold;");


        //setting the attributes for the textbox
        HBox.setHgrow(windowName,Priority.ALWAYS);
        textBox.setAlignment(Pos.CENTER);
        textBox.prefHeightProperty().bind(binding);
        textBox.prefWidthProperty().bind(this.widthProperty());
        textBox.scaleXProperty().bind(this.widthProperty().divide(250));
        textBox.scaleYProperty().bind(this.heightProperty().divide(25));
        BorderPane.setMargin(textBox, new Insets(0,0,10,0)); // optional

        BorderPane.setAlignment(textBox,Pos.CENTER);
        this.setCenter(textBox);

        //setting the circles
        HBox circleBox = new HBox();
        circleBox.spacingProperty().bind(this.widthProperty().divide(50));

        for (int i = 0; i < difficulty; i++) {

            Circle difficultyCircle = new Circle();
            difficultyCircle.radiusProperty().bind(this.widthProperty().divide(80));

            difficultyCircle.setFill(Color.WHITE);

            circleBox.getChildren().add(difficultyCircle);
            HBox.setHgrow(difficultyCircle,Priority.ALWAYS);

        }

        BorderPane.setAlignment(circleBox, Pos.TOP_RIGHT);
        BorderPane.setMargin(circleBox, new Insets(0,5,0,0)); // optional
        this.setTop(circleBox);
        circleBox.setAlignment(Pos.TOP_RIGHT);
    }


    /**Sets the resizable attribute of the pane to true
     * @return true
     */
    @Override
    public boolean isResizable() {
        return true;
    }


}
