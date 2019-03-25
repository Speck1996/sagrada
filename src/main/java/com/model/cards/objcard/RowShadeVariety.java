package com.model.cards.objcard;

import com.model.dice.DiceShade;
import com.model.patterns.DiceSpace;

/**
 * Row Shade Variety Public Object Card.
 * The pattern of this card is: row with no repeated values.
 * @see PublicObjCard
 */
public class RowShadeVariety extends PublicObjCard {

    /**
     * Constructs a RowShadeVariety card.
     * @param id the id of the card.
     * @param title the title of the card.
     * @param description the description of the card.
     * @param multiplyingFactor the multiplying factor of the card.
     * @see PublicObjCard#PublicObjCard(String, String, String, int)
     */
    RowShadeVariety(String id, String title, String description, int multiplyingFactor) {
        super(id, title, description, multiplyingFactor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int computeScore(DiceSpace[][] window) {
        int matching = 0;
        int counts[] = new int[DiceShade.values().length-1]; //count[i] contains the number of occurrences of the i+1 DiceShade enum constant in the present row

        boolean rowMatch;  //true if the current row is matching the shade variety (during its inspection)

        for(int i=0; i<window.length; i++) {
            for(int k=0; k<counts.length; k++)
                counts[k] = 0;

            rowMatch = true;
            for(int j=0; rowMatch && j<window[i].length; j++) {
                if(window[i][j].getDice() == null)
                    rowMatch = false;
                else {
                    if(counts[window[i][j].getDice().getShade().ordinal()-1] != 0)
                        rowMatch = false;
                    else
                        counts[window[i][j].getDice().getShade().ordinal()-1]++;
                }
            }

            if(rowMatch)
                matching++;
        }

        return matching * getFactor();
    }
}
