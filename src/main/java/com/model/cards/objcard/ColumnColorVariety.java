package com.model.cards.objcard;

import com.model.dice.DiceColor;
import com.model.patterns.DiceSpace;

/**
 * Column Color Variety Public Object Card.
 * The pattern of this card is: column with no repeated colors.
 * @see PublicObjCard
 */
public class ColumnColorVariety extends PublicObjCard {

    /**
     * Constructs a ColumnColorVariety card.
     * @param id the id of the card.
     * @param title the title of the card.
     * @param description the description of the card.
     * @param multiplyingFactor the multiplying factor of the card.
     * @see PublicObjCard#PublicObjCard(String, String, String, int)
     */
    ColumnColorVariety(String id, String title, String description, int multiplyingFactor) {
        super(id, title, description, multiplyingFactor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int computeScore(DiceSpace[][] window) {
        int matching = 0;
        int counts[] = new int[DiceColor.values().length-1]; //count[i] contains the number of occurrences of the i+1 DiceColor enum constant in the present column

        boolean colMatch; //true if the current column is matching the color variety (during its inspection)

        for(int i=0; i<window[0].length; i++) {
            for(int k=0; k<counts.length; k++)
                counts[k] = 0;

            colMatch = true;
            for(int j=0; j<window.length; j++) {
                if(window[j][i].getDice() == null)
                    colMatch = false;
                else {
                    if(counts[window[j][i].getDice().getColor().ordinal()-1] != 0)
                        colMatch = false;
                    else
                        counts[window[j][i].getDice().getColor().ordinal()-1]++;
                }
            }

            if(colMatch)
                matching++;
        }

        return matching * getFactor();
    }
}
