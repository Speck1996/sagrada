package com.model.dice;


/**Class representing a die, main component of the game: it has a color and a shade value
 * and contains several method to modify the dice shade
 */
public class Dice {

    /**Color value of the dice
     * @see DiceColor
     */
    private  final DiceColor color;

    /**Shade value of the dice
     * @see DiceShade
     */
    private  DiceShade shade;

    /**Constructor of the dice, it sets the two attributes of the dice
     * with the given parameters
     * @param color the color that will be set in the corresponding attribute
     * @param shade the shade that will be set in the corresponding attribute
     */
    public Dice(DiceColor color, DiceShade shade) {
        this.color = color;
        this.shade = shade;
    }

    /**Clone constructor for the dice, return a clone of the given dice
     * @param cloneDie the dice that will be cloned
     */
    public Dice(Dice cloneDie){
        this.color = cloneDie.getColor();
        this.shade = cloneDie.getShade();
    }


    /**Getter for the DiceColor attribute
     * @see DiceColor
     * @return the color of the attribute
     */
    public DiceColor getColor() {
        return color;
    }


    /**Getter for the DiceShade attribute
     * @see DiceShade
     * @return the shade of the attribute
     */
    public DiceShade getShade() {
        return shade;
    }


    /**Sets the dice shade value of the dice.
     * If the dice shade value corresponds to NEUTRAL the method does nothing and
     * return false
     * @param value the shade to be set
     * @return true if the shade is set, false if the given shade is NEUTRAL
     * @see DiceShade
     */
    //method use by players in order to set a specific shade of dice (effect of a tool card)
    public boolean setShade(DiceShade value){
        if(value==DiceShade.NEUTRAL) {return false;}             //checking if value is acceptable false=no true=yes
        else {
            this.shade = value;
            return true;
        }
    }


    /**This method changes the shade with a random one (except the {@link DiceShade#NEUTRAL}
     */
    //method use by players in order to re-roll a dice (effect of a tool card)
    public void rollDice(){
        this.shade = shade.roll();
    }

    /**Set the shade of the dice to the next value
     * @throws MinMaxReachedException if the dice has {@link DiceShade#SIX}
     */
    public void nextValue() throws MinMaxReachedException{
        if(this.shade == DiceShade.SIX){
            throw new MinMaxReachedException("You can't increase the value SIX");
        }
        this.shade = shade.getNext();
    }


    /**Set the shade of the dice to the previous value
     * @throws MinMaxReachedException if the dice has {@link DiceShade#ONE}
     */
    public void previousValue() throws MinMaxReachedException{
        if(this.shade==DiceShade.ONE){throw new MinMaxReachedException("You can't decrease the value ONE");}
        this.shade = shade.getPrevious();
    }


    /**Equals method for the dice: checks if the color and the shade of the given
     * dice are the same of the one confronted
     * @param o is the given dice to has to be confronted
     * @return true if the given dice attributes matches the dice
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dice dice = (Dice) o;
        return color == dice.color &&
                shade == dice.shade;
    }


    /**String representation of the dice
     * @return the string representing the dice
     */
    @Override
    public String toString(){
        String string;
        string = "" + this.getColor().getAbbreviation() + this.getShade().getAbbreviation();
        return string;
    }

    /**Method that changes the shade of the dice with a flip (6 to 1 or 1 to 6, 2 to 5 or 5 to 2 and 3 to 4 or 4 to 3)
     */
    public void flipDice(){                         //flip the dice 6-1 5-2 3-4
        if(this.getShade().equals(DiceShade.ONE)){
            this.setShade(DiceShade.SIX);
        }
        else if(this.getShade().equals(DiceShade.SIX)){
            this.setShade(DiceShade.ONE);
        }
        else if(this.getShade().equals(DiceShade.FIVE)){
            this.setShade(DiceShade.TWO);
        }
        else if(this.getShade().equals(DiceShade.TWO)){
            this.setShade(DiceShade.FIVE);
        }
        else if(this.getShade().equals(DiceShade.THREE)){
            this.setShade(DiceShade.FOUR);
        }
        else if(this.getShade().equals(DiceShade.FOUR)){
            this.setShade(DiceShade.THREE);
        }
    }
}