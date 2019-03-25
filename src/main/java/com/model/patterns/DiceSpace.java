package com.model.patterns;

import com.model.dice.Dice;
import com.model.dice.DiceColor;
import com.model.dice.DiceShade;

import java.util.Objects;

/**
 * A DiceSpace is a space contained where can be placed a Dice.
 * Each DiceSpace may have a color constraint, a shade constraint or no constraint.
 */
public class DiceSpace {
    private final DiceColor color;
    private final DiceShade shade;
    private Dice dice;

    /**
     * Constructs a new empty DiceSpace with the specified color and shade.
     * @param color the color of the space.
     * @param shade the shade of the space.
     */
    //Preconditions: color != null && shade != null
    public DiceSpace(DiceColor color, DiceShade shade) {
        this.color = color;
        this.shade = shade;
        dice = null;
    }

    /**
     * Constructs a new DiceSpace cloning another DiceSpace.
     * If the space to be cloned contains a dice, then the new DiceSpace will contain an equal dice.
     * @param toCloneSpace the space to be cloned
     */
    public DiceSpace(DiceSpace toCloneSpace){
        this.color = toCloneSpace.getColor();
        this.shade = toCloneSpace.getShade();
        this.dice = new Dice(toCloneSpace.getDice());
    }

    /**
     * Returns the color of this space.
     * @return the color of this space.
     */
    public DiceColor getColor() {
        return color;
    }

    /**
     * Returns the shade of this space.
     * @return the shade of this space.
     */
    public DiceShade getShade() {
        return shade;
    }

    /**
     * Returns the dice contained in this space.
     * @return the dice contained in this space, null if it does not exist.
     */
    public Dice getDice() {
        return dice;
    }

    /**
     * Placed the specified dice in this space
     * @param dice the dice to be placed
     */
    public void setDice(Dice dice) {
        this.dice = dice;
    }

    /**
     * Remove the dice placed in this space.
     * After a call of this method, and before a call to setDice(), the method getDice() will returns null
     */
    void removeDice(){
        this.dice = null;
    }


    /**
     * Method for equality check for DiceSpace.
     * @param o the object to check equality with.
     * @return true if this Window is equals to the parameters.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DiceSpace diceSpace = (DiceSpace) o;
        return color == diceSpace.color &&
                shade == diceSpace.shade &&
                Objects.equals(dice, diceSpace.dice);
    }

}
