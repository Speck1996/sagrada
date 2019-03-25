package com.model.cards.objcard;

import com.model.cards.ObjCard;
import com.model.dice.DiceColor;
import com.model.patterns.DiceSpace;

/**
 * All the private object cards are object cards with a specific color.
 * The points given by a private card is the sum of the values of dice of the specific color that have been placed in the window, multiply for the multiplying factor.
 */
public class PrivateObjCard extends ObjCard {

    /**Color associated to the card
     */
    private DiceColor color;

    /**
     * Constructs a PrivateObjCard.
     * @param id the id of the card.
     * @param title the title of the card.
     * @param description the description of the card.
     * @param multiplyingFactor the multiplying factor of the card.
     * @param color the specific color of the card.
     */
    PrivateObjCard(String id, String title, String description, int multiplyingFactor, DiceColor color){
        super(id,title,description, multiplyingFactor);
        this.color = color;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int computeScore(DiceSpace[][] window){

        int sum = 0;

        for(int i=0; i<window.length; i++) {
            for(int j=0; j<window[0].length; j++) {
                if(window[i][j].getDice() != null && window[i][j].getDice().getColor() == color)
                    sum += window[i][j].getDice().getShade().ordinal();
            }
        }

        return sum * getFactor();
    }
}
