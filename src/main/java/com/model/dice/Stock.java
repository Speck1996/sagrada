package com.model.dice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**Class representing te stock: it contains a list of dice and method to get,
 * add or remove a die
 */
public class Stock implements Serializable {
    /**List of dice in the stock
     */
    private List<Dice> dice;

    /**Cached of the stock, used to save temporally the state of the stock
     */
    private Stock cache;

    /**Constructor for the stock, initializes the dice list
     */
    public Stock(){
        this.dice = new ArrayList<>();
    }

    /**Clone constructor for the stock
     * @param toCloneStock the stock that will be cloned
     */
    public Stock(Stock toCloneStock){
        this.dice = new ArrayList<>();
        for(Dice die: toCloneStock.getDice()){
            this.dice.add(new Dice(die));
        }
    }

    /**Getter for the list of dice in the stock
     * @return the list of dice in the stock
     */
    public List<Dice> getDice(){
        return dice;
    }

    /**Method used to remove a die from the stock
     * @param index the index of the die that has to be removed
     * @throws NoDiceException if the index does not correspond to a die in the list
     */
    public void removeDice(int index) throws NoDiceException {
        try {
            dice.remove(index);
        }
        catch (IndexOutOfBoundsException e) {
            throw new NoDiceException("There are no dices left in the stock or you are trying to take an invalid dice");
        }
    }

    /**Method used to get a dice from the list of dice
     * @param index the index of the desired die
     * @return the desired dice
     * @throws NoDiceException if the index does not correspond to any dice
     */
    public Dice getDice(int index) throws NoDiceException {
        try {
            return dice.get(index);
        } catch (IndexOutOfBoundsException e) {
            throw new NoDiceException("There are no dices left in the stock or you are trying to take an invalid dice");
        }
    }

    /**Method used to add a die in stock
     * @param inserted the dice that is  inserted in the stock
     */
    public void insertDice(Dice inserted){
        dice.add(inserted);
    }

    /**Method used to clear the stock, removes every dice in it
     */
    public void clearStock() {
        dice.clear();
    }

    /**Metho used to set the cache
     */
    public void setCache(){
        this.cache = new Stock(this);
    }

    /**Method used to get the cache
     * @return the cache
     */
    public Stock getCache(){
        return this.cache;
    }

    /**Method used to create the string representing the stock
     * @return string representing the stock
     */
    @Override
    public String toString(){
        String string = "";
        int i;
        for(i = 0; i < dice.size(); i++){
            string = string + dice.get(i).toString() + "\t";
        }
        return string;
    }
}