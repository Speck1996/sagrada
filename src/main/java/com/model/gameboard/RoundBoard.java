package com.model.gameboard;

import java.util.ArrayList;
import java.util.List;

import com.model.dice.Dice;


/**Class representing the RoundBoard: for every round it can contain a list of dice.
 * It does some useful operations like swapping a given die with one on it, and inserting
 * a list of dice on it
 */
public class RoundBoard {

    /**The integer representing the last reached round
     */
    private int round;

    /**List of all the rounds list of dice
     */
    private List<List<Dice>> diceOnBoard;

    /**Cache attribute of the RoundBoard, where the current state can be temporally
     * stored
     */
    private RoundBoard cache;


    /**Constructor for the RoundBoard, initializes the round to 0
     * and the list of round list of dice
     */
    public RoundBoard(){
        round = 0;
        diceOnBoard = new ArrayList<>();
    }

    /**Clone constructor of the RoundBoard
     * @param roundBoard the RoundBoard that is cloned
     */
    public RoundBoard(RoundBoard roundBoard){
        this.round = roundBoard.getRound();
        this.diceOnBoard = new ArrayList<>();
        for(int i = 0; i< roundBoard.getSize(); i++){
            this.diceOnBoard.add(new ArrayList<>());
            for (int j = 0; j < roundBoard.getDice(i).size(); j++) {
                this.diceOnBoard.get(i).add(new Dice(roundBoard.getDice(i).get(j)));
            }
        }

    }

    /**Getter for the round attribute
     * @return the round attribute
     */
    //this method returns the current round of the game
    public int getRound() {
        return round;
    }

    /**Increases the round attribute by one
     */
    //this method increases by one the round counter
    public void nextRound(){
        int i=this.getRound();
        this.round=i+1;
    }

    /**Checks if the RoundBoard is not empty
     * @return false if the RoundBoard is not empty, true otherwise
     */
    //this method check if there is a die on the roundBoard: if there is a die it returns true
    public boolean isEmpty(){
        for(List<Dice> roundDieList: diceOnBoard){
            if(roundDieList.size()>0){
                return false;
            }
        }
        return  true;
    }


    /**Returns the list of dice placed on a round
     * @param whichRound the round from which will be taken the list of dice
     * @return the list of dice on the round
     */
    //this method returns all the dices left and placed on RoundBoard at the end of the requested round
    public List<Dice> getDice(int whichRound) {

        if(diceOnBoard.get(whichRound).size() == 0){
            throw new NullPointerException("No dice in the selected round");
        }
        return diceOnBoard.get(whichRound);
    }

    /**Getter for the size of list of round dice list on the RoundBoard
     * @return the size of the diceOnBoard attribute
     */
    public int getSize(){
        return diceOnBoard.size();
    }

    /**Inserts the given list of dice on the current round
     * @param leftDices the list of given dice
     */
    //this method inserts in the RoundBoard all the dices left at the end of the last round
    public void insertDices(List<Dice> leftDices) {

        diceOnBoard.add(leftDices);
    }



    /**
     * This method changes  a dice on the RoundBoard with the give one
     * and returns the dice taken from RoundBoard
     * @param round the round from which the dice will be taken
     * @param dice the integer index of the desired dice
     * @param toSwapDie the die that will be swapped
     * @return the die swapped
     * */
    public Dice changeDice(int round, int dice, Dice toSwapDie) {
        List<Dice> roundDiceList = diceOnBoard.get(round);
        if(roundDiceList.size() == 0){
            throw new NullPointerException("No die in the selected coordinates");
        }
        Dice newDice = roundDiceList.get(dice);

        roundDiceList.set(dice, toSwapDie);
        diceOnBoard.set(round,roundDiceList);
        return newDice;
    }

    /**Method used to set the cache of the RoundBoard
     */
    public void setCache(){
        this.cache = new RoundBoard(this);
    }

    /**Getter for the cache
     * @return the RoundBoard cache
     */
    public RoundBoard getCache(){
        return this.cache;
    }


    /**Method used to get the string representation of the RoundBoard
     * @return the string representing the RoundBoard
     */
    @Override
    public String toString(){
        String string = "RoundBoard";

        for(int i = 0; i < diceOnBoard.size(); i++){
            string = string + "\nRound " + (i+1);

            if(diceOnBoard.get(i).size()== 0){
                string = string+ " --";
            }
            else {
                for (int j = 0; j < diceOnBoard.get(i).size(); j++) {
                    string = string + " " + diceOnBoard.get(i).get(j).toString();

                }
            }
        }
        
        return string + "\n";
    }

}