package com.model.cards.objcard;

import com.model.dice.DiceShade;
import com.model.patterns.DiceSpace;

/**
 * Column Shade Variety Public Object Card.
 * The pattern of this card is: columns with no repeated values.
 * @see PublicObjCard
 */
public class ColumnShadeVariety extends PublicObjCard {

    /**
     * Constructs a ColumnShadeVariety card.
     * @param id the id of the card.
     * @param title the title of the card.
     * @param description the description of the card.
     * @param multiplyingFactor the multiplying factor of the card.
     * @see PublicObjCard#PublicObjCard(String, String, String, int)
     */
    ColumnShadeVariety(String id, String title, String description, int multiplyingFactor) {
        super(id, title, description, multiplyingFactor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int computeScore(DiceSpace[][] window) {
        int matching = 0;
        int counts[] = new int[DiceShade.values().length-1]; //count[i] contains the number of occurrences of the i+1 DiceShade enum constant in the present column

        boolean colMatch; //true if the current column is matching the shade variety (during its inspection)

        for(int i=0; i<window[0].length; i++) {
            for(int k=0; k<counts.length; k++)
                counts[k] = 0;

            colMatch = true;
            for(int j=0; j<window.length; j++) {
                if(window[j][i].getDice() == null)
                    colMatch = false;
                else {
                    if(counts[window[j][i].getDice().getShade().ordinal()-1] != 0)
                        colMatch = false;
                    else
                        counts[window[j][i].getDice().getShade().ordinal()-1]++;
                }
            }

            if(colMatch)
                matching++;
        }

        return matching * getFactor();
    }
}
