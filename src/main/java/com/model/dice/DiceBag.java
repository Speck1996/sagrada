package com.model.dice;

import java.util.ArrayList;
import java.util.List;


/**Class representing the DiceBag: it contains a list of dice that can be draw.
 * A dice can be also inserted in the bag
 */
public class DiceBag {

    /**List of dice contained by the bag
     */
    private List<Dice> dice;

    /**Cache of the bag, used to temporally store the current state of the bag
     */
    private DiceBag cache;

    /**Constructor of the dicebag: the given list is the list of dice that will
     * be contained in the bag
     * @param dice the list of dice that will be set
     */
    public DiceBag(List<Dice> dice){
        this.dice = dice;
    }


    /**Clone constructor for the DiceBag
     * @param toCloneDiceBag the bag that will be cloned
     */
    public DiceBag(DiceBag toCloneDiceBag){
        List<Dice> cloneList = new ArrayList<>();
        for(Dice die: toCloneDiceBag.getDices()){
            cloneList.add(new Dice(die));
        }
        this.dice = cloneList;
    }


    /**Getter for the list of dice contained by the bag
     * @return the list of contained dice
     */
    public List<Dice> getDices(){
        return dice;
    }

    /**Method used to set the cache of the bag
     */
    public void setCache(){
        this.cache = new DiceBag(this);
    }

    /**Method used to get the cache of the bag
     * @return the cache of the bag
     */
    public DiceBag getCache(){
        return this.cache;
    }

    /**Method used to draw a dice rolling it before returning it
     * @return the drafted dice
     * @throws NoDiceException if there is no die in the stock
     */
    public Dice drawDice() throws NoDiceException {
        int randomIndex;
        int sizeOfBag = dice.size();
        try {
            randomIndex = ((int) (Math.random() * sizeOfBag));
            Dice diceDrawn = dice.get(randomIndex);
            diceDrawn.rollDice();
            dice.remove(randomIndex);
            return diceDrawn;
        }
        catch (IndexOutOfBoundsException e) {
            throw new NoDiceException("There are no dices left in the bag");
        }
    }

    /**Method used to add a die in the dicebag
     * @param inserted the dice that will be added
     */
    public void insertDice(Dice inserted){
        dice.add(inserted);
    }
}