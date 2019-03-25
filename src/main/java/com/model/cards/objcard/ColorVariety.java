package com.model.cards.objcard;

import com.model.dice.DiceColor;
import com.model.patterns.DiceSpace;

/**
 * Color Variety Public Object Card.
 * The pattern of this card is: sets of one of each color anywhere.
 * @see PublicObjCard
 */
public class ColorVariety extends PublicObjCard {

    /**
     * Constructs a ColorVariety card.
     * @param id the id of the card.
     * @param title the title of the card.
     * @param description the description of the card.
     * @param multiplyingFactor the multiplying factor of the card.
     * @see PublicObjCard#PublicObjCard(String, String, String, int)
     */
    ColorVariety(String id, String title, String description, int multiplyingFactor) {
        super(id, title, description, multiplyingFactor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int computeScore(DiceSpace[][] window) {
        int matching;
        int counts[] = new int[DiceColor.values().length-1];

        for(int i=0; i<window.length; i++) {
            for(int j=0; j<window[0].length; j++) {
                if(window[i][j].getDice() != null) {
                    counts[window[i][j].getDice().getColor().ordinal()-1]++;
                }
            }
        }

        matching = counts[0];
        for(int k=1; k<counts.length; k++) {
            if(counts[k] < matching)
                matching = counts[k];
        }

        return matching * getFactor();
    }
}
