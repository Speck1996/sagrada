package com.model.patterns;

import com.model.WrongInputSyntaxException;
import com.model.dice.Dice;
import com.model.dice.DiceColor;
import com.model.dice.DiceShade;
import com.model.dice.NoDiceException;
import com.model.dice.Stock;

import java.util.ArrayList;
import java.util.List;


/**
 * Represents the window pattern that must be fill with appropriate {@link Dice} respecting all the constraints.
 * Every player in a game has exactly one WindowPatternCard and player's purpose is to fill more spaces possible using dice from the {@link Stock}.
 */
public class WindowPatternCard {
    public final static int ROW = 4;
    public final static int COL = 5;

    private final byte id;
    private final String name;
    private final int difficulty;       //number of star
    private WindowPatternCard pairedWindow;
    private boolean empty;
    private final DiceSpace[][] spaces;
    private int diceOnBoard;
    private WindowPatternCard cache;


    /**
     * <p>Construct a new WindowPatternCard parsing a String.
     * The string must have the follow syntax:</p>
     * <center>[id]:[name]:[difficulty]:[spaces]</center>
     * <p>where [id] and [difficulty] are integer and [name] an arbitrary string.
     * The [id] must be unique.
     * The [spaces] are a sequence of twenty cells described by a character that eventually specify a constraint.
     * Every space can impose a single constraints about colors (blue, purple, green, red, yellow) or about shade (one, two, three, four, five, six), or none of them.
     * The character are the following:
     * <br>
     *
     * <ul style="list-style-type:none">
     *     <li>Color constraints:
     *     <ul>
     *         <li>'B' for blue</li>
     *         <li>'P' for purple</li>
     *         <li>'G' for green</li>
     *         <li>'R' for red</li>
     *         <li>'Y' for yellow</li>
     *     </ul>
     *     </li>
     *     <li>Shade constraints:
     *     <ul>
     *         <li>'1' for one</li>
     *         <li>'2' for two</li>
     *         <li>'3' for three</li>
     *         <li>'4' for four</li>
     *         <li>'5' for five</li>
     *         <li>'6' for six</li>
     *     </ul>
     *     </li>
     * </ul>
     * If a cell does not impose any constraint it is described by the '*' character.
     *
     *
     * @param windowDescription the String that describe the WindowPatternCard.
     * @throws WindowSyntaxException if the syntax of the parameter is wrong.
     */
    public WindowPatternCard(String windowDescription) throws WindowSyntaxException {
        diceOnBoard = 0;
        String[] windowData = windowDescription.split(":", 0);

        if(windowData.length != 4)
            throw new WindowSyntaxException("Found " + windowData.length + " arguments (4 required)");

        try {
            id = Byte.parseByte(windowData[0]);
        } catch (NumberFormatException e) {
            throw new WindowSyntaxException("Wrong format for id");
        }

        name = windowData[1];
        try {
            difficulty = Integer.parseInt(windowData[2]);
        } catch (NumberFormatException e ) {
            throw new WindowSyntaxException("Wrong format for difficulty");
        }
        String pattern = windowData[3];
        if(pattern.length() != ROW * COL)
            throw new WindowSyntaxException("Wrong number of DiceSpace (" + (ROW*COL) + " required)");

        spaces = new DiceSpace[ROW][COL];
        for(int i=0; i<ROW; i++) {
            for(int j=0; j<COL; j++) {
                char currentChar = pattern.charAt(i*COL + j);

                if(currentChar == '*') {
                    spaces[i][j] = new DiceSpace(DiceColor.NEUTRAL, DiceShade.NEUTRAL);
                }
                else if(DiceColor.getListOfAbbreviation().contains(currentChar)) {
                    for(DiceColor dc: DiceColor.values()) {
                        if(dc.getAbbreviation() == currentChar) {
                            spaces[i][j] = new DiceSpace(dc, DiceShade.NEUTRAL);
                            break;
                        }
                    }
                }
                else if(DiceShade.getListOfAbbreviation().contains(currentChar)) {
                    for(DiceShade ds: DiceShade.values()) {
                        if(ds.getAbbreviation() == currentChar) {
                            spaces[i][j] = new DiceSpace(DiceColor.NEUTRAL, ds);
                            break;
                        }
                    }
                }
                else
                    throw new WindowSyntaxException("Illegal character for DiceSpace definition (found '" + currentChar + "')");
            }
        }

        empty = true;
    }


    /**
     * <p>Construct a new WindowPatternCard cloning another WindowPatternCard</p>
     * @param clonedWpc the window to be cloned
     */
    public WindowPatternCard(WindowPatternCard clonedWpc){
        this.id = clonedWpc.getId();
        this.name = clonedWpc.getName();
        this.difficulty = clonedWpc.getDifficulty();
        this.empty = clonedWpc.isEmpty();
        this.diceOnBoard = clonedWpc.diceOnBoard;

        //cloning spaces
        DiceSpace[][] tmpSpaces = clonedWpc.getSpaces();
        this.spaces = new DiceSpace[ROW][COL];

        for(int i=0; i<ROW; i++) {
            for(int j=0; j<COL; j++) {
                this.spaces[i][j] = new DiceSpace(tmpSpaces[i][j].getColor(), tmpSpaces[i][j].getShade());
                if(tmpSpaces[i][j].getDice() != null) {

                    this.spaces[i][j].setDice(new Dice(tmpSpaces[i][j].getDice())); //cloning the dice too
                }
            }
        }

        this.cache = null;

    }

    /**
     * Returns the identifier of this window
     * @return the id of this window
     */
    public byte getId() {
        return id;
    }

    /**
     * Returns the name of this window
     * @return the name of this window
     */
    String getName() {
        return name;
    }

    /**
     * Returns the difficulty of this window.
     * @return the difficulty of this window.
     */
    public int getDifficulty() {
        return difficulty;
    }

    /**
     * Returns true if this window is empty (that is it does not have any Dice placed).
     * @return true if this window is empty.
     */
    boolean isEmpty() {
        return empty;
    }

    /**
     * Returns a two-dimensional array representing the spaces of this window.
     * @return a two-dimensional array representing the spaces of this window.
     */
    public DiceSpace[][] getSpaces() {
        return spaces;
    }

    /**
     * Set the paired WindowPatternCard of this window.
     * @param wpc the paired WindowPatternCard.
     */
    void setPairedWindow(WindowPatternCard wpc) {
        this.pairedWindow = wpc;
    }

    /**
     * Get the paired WindowPatternCard of this window.
     * @return the paired WindowPatternCard of this window.
     */
    public WindowPatternCard getPairedWindow() {
        return this.pairedWindow;
    }


    // Preconditions: dice != null
    private void usableSpace(int row, int column, Dice dice, boolean colorRestriction, boolean shadeRestriction) throws DieNotPlaceableException {

        if(row < 0 || row > ROW-1 || column < 0 || column > COL-1 ) {
            throw new DieNotPlaceableException("Wrong coordinates");
        }

        DiceSpace selectedSpace = spaces[row][column];

        if(selectedSpace.getDice() != null)
            throw new DieNotPlaceableException("Space already occupied");


        if(colorRestriction && selectedSpace.getColor() != DiceColor.NEUTRAL && selectedSpace.getColor() != dice.getColor())
            throw new DieNotPlaceableException("Violates color constraint");
        if(shadeRestriction && selectedSpace.getShade() != DiceShade.NEUTRAL && selectedSpace.getShade() != dice.getShade())
            throw new DieNotPlaceableException("Violates shade constraint");

        if(empty) {
            if(!(row == 0 || row == ROW-1 || column == 0 || column == COL-1))  //it's not on the edge
                throw new DieNotPlaceableException("First dice must be on the edge");

            //the placing is legal
          //  selectedSpace.setDice(new Dice(dice.getColor(), dice.getShade()));
          //  empty = false;
            return;
        }


        /*
        * There is already at least a dice placed on the window
        * now I control the adjacent spaces
        * */
        boolean existsAdjacent = false;

        if(row-1 >= 0 && spaces[row-1][column].getDice() != null) {
            if(spaces[row-1][column].getDice().getColor() == dice.getColor() || spaces[row-1][column].getDice().getShade() == dice.getShade()) //violates orthogonally constraint
                throw new DieNotPlaceableException("Violates adjacent constraint");
            existsAdjacent = true;
        }

        if(row+1 < ROW && spaces[row+1][column].getDice() != null) {
            if(spaces[row+1][column].getDice().getColor() == dice.getColor() || spaces[row+1][column].getDice().getShade() == dice.getShade())
                throw new DieNotPlaceableException("Violates adjacent constraint");
            existsAdjacent = true;
        }

        if(column-1 >= 0 && spaces[row][column-1].getDice() != null) {
            if(spaces[row][column-1].getDice().getColor() == dice.getColor() || spaces[row][column-1].getDice().getShade() == dice.getShade())
                throw new DieNotPlaceableException("Violates adjacent constraint");
            existsAdjacent = true;
        }

        if(column+1 < COL && spaces[row][column+1].getDice() != null) {
            if(spaces[row][column+1].getDice().getColor() == dice.getColor() || spaces[row][column+1].getDice().getShade() == dice.getShade())
                throw new DieNotPlaceableException("Violates adjacent constraint");
            existsAdjacent = true;
        }

        if(row-1 >= 0 && column-1 >= 0 && spaces[row-1][column-1].getDice() != null)
            existsAdjacent = true;
        if(row-1 >= 0 && column+1 < COL && spaces[row-1][column+1].getDice() != null)
            existsAdjacent = true;
        if(row+1 < ROW && column-1 >= 0 && spaces[row+1][column-1].getDice() != null)
            existsAdjacent = true;
        if(row+1 < ROW && column+1 < COL && spaces[row+1][column+1].getDice() != null)
            existsAdjacent = true;


        if(existsAdjacent) {     //also dice doesn't violate orthogonally constraint
       //     spaces[row][column].setDice(new Dice(dice.getColor(), dice.getShade()));
            return;
        }
        throw new DieNotPlaceableException("Must be adjacent to another dice");      //in this case doesn't exist adjacent dice
    }

    /**
     * Try to place the specified dice on the specified position respecting the constraints indicated.
     * @param row the number of the row.
     * @param column the number of column.
     * @param dice the dice to be placed.
     * @param colorRestriction true if restrictions on colors must be taken in account.
     * @param shadeRestriction true if restrictions on shades must be taken in account.
     * @return true if the placing has been successful.
     * @throws DieNotPlaceableException if the dice cannot be placed in the specified position.
     */
    public boolean placeDice(int row, int column, Dice dice, boolean colorRestriction, boolean shadeRestriction) throws DieNotPlaceableException{ //placing the die, separated this from the method before to implement the next method see code belove


        usableSpace(row, column, dice, colorRestriction, shadeRestriction);  //throw exception if not

        spaces[row][column].setDice(new Dice(dice.getColor(), dice.getShade()));
        if(empty){
            empty = false;
        }
        this.diceOnBoard++;
        return true;

    }

    /**
     * Check if exists a space in this window where the specified dice can be placed, respecting the constraints indicated.
     * @param dice the dice that potentially can be placed.
     * @param colorRestriction true if restrictions on colors must be taken in account.
     * @param shadeRestriction true if restrictions on shades must be taken in account.
     * @return true if do exist at least one space where the specified dice can be placed, false otherwise.
     */
    public boolean isPlaceable(Dice dice, boolean colorRestriction, boolean shadeRestriction){ //check for every space in the window if you can place the die

        for(int i = 0; i < ROW; i++){
            for(int j = 0; j < COL; j++){

                try {
                    usableSpace(i, j, dice, colorRestriction, shadeRestriction);
                    return true;                                                        //found if there is an usable space for my dice
                } catch (DieNotPlaceableException e) {
                    System.out.println("No space usable");
                }
            }
        }
        return false;

    }

    /**
     * Check if exists an isolated space where can be placed the specified dice.
     * @param dice the dice that potentially can be placed.
     * @return true if do exist at least one space where the specified dice can be placed isolated, false otherwise.
     */
    public boolean isPlaceableIsolate(Dice dice){
        for(int i = 0; i < ROW; i++){
            for(int j = 0; j < COL; j++){

                try {
                    usableIsolatedSpace(i,j,dice);
                    return true;                                                        //found if there is an usable space for my dice
                } catch (DieNotPlaceableException e) {
                    System.out.println("No isolated space usable");
                }
            }
        }
        return false;


    }


    /**
     * Try to place the specified dice on the specified isolated position.
     * @param row the number of the row.
     * @param column the number of the column.
     * @param dice the dice to be placed.
     * @throws DieNotPlaceableException if the dice cannot be placed in the specified position or the position is not isolated.
     */
    //Preconditions: 0<=row<=3 && 0<=column<=4 && dice != null && !empty (?)
    public void placeDiceIsolated(int row, int column, Dice dice) throws DieNotPlaceableException {

       usableIsolatedSpace(row,column,dice);

        spaces[row][column].setDice(new Dice(dice.getColor(), dice.getShade()));
        this.diceOnBoard++;
    }


    /**This method checks if the selected space is usable for an isolated dice placement for the selected die
     * @param row row index of the space
     * @param column column index of the space
     * @param dice selected die
     * @throws DieNotPlaceableException if the die can't be placed in the space
     */
    private void usableIsolatedSpace(int row, int column, Dice dice) throws DieNotPlaceableException{
        DiceSpace selectedSpace = spaces[row][column];

        if(selectedSpace.getDice() != null)  //space already occupied
            throw new DieNotPlaceableException("Space already occupied");

        if(selectedSpace.getColor() != DiceColor.NEUTRAL && selectedSpace.getColor() != dice.getColor())  //violates color constraint
            throw new DieNotPlaceableException("Violates color restriction");
        if(selectedSpace.getShade() != DiceShade.NEUTRAL && selectedSpace.getShade() != dice.getShade())  //violates shade constraint
            throw new DieNotPlaceableException("Violates shade restriction");

        if(row-1 >= 0 && spaces[row-1][column].getDice() != null)
            throw new DieNotPlaceableException("Violates adjacent restriction");
        if(row+1 < ROW && spaces[row+1][column].getDice() != null)
            throw new DieNotPlaceableException("Violates adjacent restriction");
        if(column-1 >= 0 && spaces[row][column-1].getDice() != null)
            throw new DieNotPlaceableException("Violates adjacent restriction");
        if(column+1 < COL && spaces[row][column+1].getDice() != null)
            throw new DieNotPlaceableException("Violates adjacent restriction");
        if(row-1 >= 0 && column-1 >= 0 && spaces[row-1][column-1].getDice() != null)
            throw new DieNotPlaceableException("Violates adjacent restriction");
        if(row-1 >= 0 && column+1 < COL && spaces[row-1][column+1].getDice() != null)
            throw new DieNotPlaceableException("Violates adjacent restriction");
        if(row+1 < ROW && column-1 >= 0 && spaces[row+1][column-1].getDice() != null)
            throw new DieNotPlaceableException("Violates adjacent restriction");
        if(row+1 < ROW && column+1 < COL && spaces[row+1][column+1].getDice() != null)
            throw new DieNotPlaceableException("Violates adjacent restriction");




    }

    /**Select and removes the die from an indicated space, the die must respect the given restriction
     * @param spaceCoordinates the coordinate of the wanted die
     * @param colorConstraint the color the wanted die must respect, if neutral every color is ok
     * @param shadeConstraint the shade the wanted die must respect, if neutral every shade is ok
     * @return the wanted dice
     * @throws NoDiceException if in the indicated space there is no die
     * @throws DiceNotPickableException if in the indicated space the die doesn't respect the given constraint
     * @throws WrongInputSyntaxException if the given coordinates are not usable because out of bound
     */
    public Dice getDiceFromSpace(Integer[] spaceCoordinates, DiceColor colorConstraint, DiceShade shadeConstraint) throws NoDiceException, DiceNotPickableException,WrongInputSyntaxException {
        int row = spaceCoordinates[0];
        int column = spaceCoordinates[1];
        Dice selectedDie;
        try {
            //picking the die
            selectedDie = this.spaces[row][column].getDice();
        }catch(IndexOutOfBoundsException error) {
            throw new WrongInputSyntaxException("This space doesn't exist");
        }
        if(selectedDie == null){
            throw new NoDiceException("No dice in the selected space");
        }
        if(selectedDie.getColor() != colorConstraint && colorConstraint != DiceColor.NEUTRAL){
            throw new DiceNotPickableException("The die you have to select must be of color " + colorConstraint);
        }
        if(selectedDie.getShade() != shadeConstraint && shadeConstraint != DiceShade.NEUTRAL){
            throw new DiceNotPickableException("The die you have to select must be of shade " + shadeConstraint);
        }
        removeDiceFromSpace(spaceCoordinates);

        return selectedDie;
    }


    /**Checks for every dice on the window , if there is a movable dice matching the given color and respecting the given constriction
     * The movable dice can't be picked from the prohibited list of coordinates
     * @param chosenColor the color the dice movable dice must have, if it is {@link DiceColor#NEUTRAL} every dice can be picked
     * @param colorConstraint if true the the dice must match the dicespace color if it's color is different from neutral
     * @param shadeConstraint if true the the dice must match the dicespace shade if it's shade is different from neutral
     * @param prohibitedPositions list of coordinates from where the dice cannot be picked
     * @throws NoMovableDiceException if there is no movable dice
     */
    public void checkMovable(DiceColor chosenColor, boolean colorConstraint, boolean shadeConstraint, List<Integer[]> prohibitedPositions) throws NoMovableDiceException {

        //picking all space coordinates with a die that respects has the selected color
        List<Integer[]> positions = this.generatePositions(chosenColor);



        //removing coordinates of already moved dice
        if(prohibitedPositions!=null) {
            for (int i = 0; i < prohibitedPositions.size(); i++) {
                for (int j = 0; j < positions.size(); j++) {
                    if (positions.get(j)[0].equals(prohibitedPositions.get(i)[0]) && positions.get(j)[1].equals(prohibitedPositions.get(i)[1])) {
                        positions.remove(j);
                    }
                }
            }

            //no movable dice left
            if (positions.isEmpty()) {
                throw new NoMovableDiceException("There is no movable die in the window");
            }

        }

        //checking if there is a space where a dice can be placed, the dice is selected with positions saved before
        this.checkMovablePositions(positions,colorConstraint,shadeConstraint);
    }

    /**Generates the list of coordinates of space having the dice matching the given color
     * @param chosenColor is the color that dice on the window must match, if it corresponds to {@link DiceColor#NEUTRAL}
     *                    every dice can be picked
     * @return the list of positions that matches the given color
     * @throws NoMovableDiceException if there is no movable die
     */
    private List<Integer[]> generatePositions(DiceColor chosenColor) throws NoMovableDiceException{
        List<Integer[]> positions = new ArrayList<>();
        Integer[] tmpPosition;
        tmpPosition = new Integer[2];

        //saving all the coordinates of possible movable dice
        for(int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                if (this.spaces[i][j].getDice() != null ) {
                    boolean colorCheck = chosenColor != DiceColor.NEUTRAL && this.getSpaces()[i][j].getDice().getColor() == chosenColor;

                    if (colorCheck || chosenColor == DiceColor.NEUTRAL) {
                        tmpPosition[0] = i;
                        tmpPosition[1] = j;
                        positions.add(tmpPosition.clone());
                    }
                }
            }
        }

        if(positions.isEmpty()){
            throw new NoMovableDiceException("There is no movable die in the window");
        }

        return  positions;
    }


    /**Method used to check if there is a movable die on the window in the list of given positions, under the given constraints, returns an exception if there is
     * no die
     * @param positions coordinates of dice on the window
     * @param colorConstraint  color constraint for the placement, if true the dice must match the dicespace color if it has a color
     * @param shadeConstraint  shade constraint for the placement, if true the dice must match the dicespace shade if it has a shade
     * @throws NoMovableDiceException if there is no movable die
     */
    private void checkMovablePositions(List<Integer[]> positions, boolean colorConstraint, boolean shadeConstraint) throws NoMovableDiceException{

        int row;
        int column;

        Dice tmpDice;
        // this algorithm check for every die in the window if there is a space where it can be moved, ignoring the one from which if was taken

        for(Integer[] position: positions){
            //row and column of the first selected die
            row = position[0];
            column = position[1];

            //temporally pickin the die to check for every space (except the one it has been placed before) if it is movable or not
            tmpDice = new Dice(spaces[row][column].getDice());
            DiceSpace spaceClone = new DiceSpace(spaces[row][column]);
            this.removeDiceFromSpace(position);
            for(int i= 0; i < ROW; i ++){
                for(int j = 0; j < COL; j++){
                    //do not check if the die is placeable in its previous position

                    if(!(i== row && j == column)){
                        try {
                            usableSpace(i, j, tmpDice, colorConstraint, shadeConstraint);

                            //putting back the die to his place
                            spaces[row][column] = spaceClone;
                            this.diceOnBoard++;
                            return;
                        } catch (DieNotPlaceableException e) {
//                            System.out.println("Space " + (i+1) +" " + (j+1) + " not usable: "+ e.getMessage());
                        }
                    }else{
//                        System.out.println("Space " + (i+1) +" " + (j+1) + " not usable: Starting place of the die");
                    }

                }
            }
            this.diceOnBoard++;
            spaces[row][column] = spaceClone;
        }

        throw new NoMovableDiceException("Found no space where to move the die");

    }


    /**Removes the die placed in the space corresponding to the given coordinates, updating the number of dice on the board
     * and setting the empty boolean to true if the counter reach '
     * @param spaceCoordinates the coordinates of the space with the die that has to be removed
     */
    //removing dice and changing the counter
    private void removeDiceFromSpace(Integer[] spaceCoordinates){
        spaces[spaceCoordinates[0]][spaceCoordinates[1]].removeDice();
        this.diceOnBoard--;
        if(this.diceOnBoard == 0){
            this.empty = true;
        }
    }


    /**
     * Returns the number of dice placed.
     * @return the number of dice placed.
     */
    public int getDiceOnBoard(){
        return this.diceOnBoard;
    }

    /**
     * Create a new cache for this window with the current state.
     */
    public void setCache(){
        this.cache = new WindowPatternCard(this);
    }


    /**
     * Returns the cache for this window.
     * This method retrieve the state of the last call of setCache().
     * @return The cache for this window.
     */
    public WindowPatternCard getCache(){
        return this.cache;
   }


    /**
     * Method for equality check for WindowPatternCard.
     * @param o the object to check equality with.
     * @return true if this WindowPatternCard is equals to the parameter.
     */
   @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WindowPatternCard wpc = (WindowPatternCard) o;
        for(int i = 0; i <ROW; i++){
            for(int j = 0; j < COL; j++){
                if(wpc.getSpaces()[i][j].getColor() != spaces[i][j].getColor() || wpc.getSpaces()[i][j].getShade()!= spaces[i][j].getShade()){
                    return false;
                }
                if(wpc.getSpaces()[i][j].getDice()== null && spaces[i][j].getDice() != null || wpc.getSpaces()[i][j].getDice()!= null && spaces[i][j].getDice() == null){
                    return false;
                }
                if(wpc.getSpaces()[i][j].getDice()!= null && spaces[i][j].getDice()!= null &&!wpc.getSpaces()[i][j].getDice().equals(spaces[i][j].getDice() )) {
                    return false;
                }
            }
        }
        if(wpc.getId() != id){
            return false;
        }
        if(!wpc.getName().equals(name)){
            return false;
        }

        return wpc.getDifficulty() == difficulty;
    }


    /**
     * Creates a String representation of this window.
     * @return the String representation of this window.
     */
    @Override
    public String toString() {
        String string = id + " - " + name + " - Diff:" + difficulty + "\n";

        for(int i=0; i<spaces.length; i++) {
            for(int j=0; j<spaces[i].length; j++) {
                string = string + spaces[i][j].getColor().getAbbreviation() + spaces[i][j].getShade().getAbbreviation() + "|";
                if(spaces[i][j].getDice() == null)
                    string = string + "--";
                else
                    string = string + spaces[i][j].getDice().toString();
                string = string + "\t";
            }
            string = string + "\n";
        }

        return string;
    }
}




















