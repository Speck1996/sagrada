package com.model.cards.objcard;

import com.model.dice.DiceShade;
import com.model.patterns.DiceSpace;

/**
 * A PairsShades is a Deep Shades, Medium Shades or Light Shades Public Object Card.
 * The pattern of these cards is: sets of v1 &amp; v2 values anywhere, where v1 &amp; v2 are:
 * <ul>
 *     <li>5 &amp; 6 for Deep Shades</li>
 *     <li>3 &amp; 4 for Medium Shades</li>
 *     <li>1 &amp; 2 for Light Shades</li>
 * </ul>
 * @see PublicObjCard
 */
public class PairsShades extends PublicObjCard {

    private DiceShade shade1;
    private DiceShade shade2;

    /**
     * Constructs a PairsShades card.
     * @param id the id of the card.
     * @param title the title of the card.
     * @param description the description of the card.
     * @param multiplyingFactor the multiplying factor of the card.
     * @param shade1 the first shade value.
     * @param shade2 the second shade value.
     * @see PublicObjCard#PublicObjCard(String, String, String, int)
     */
    PairsShades(String id, String title, String description, int multiplyingFactor, DiceShade shade1, DiceShade shade2) {
        super(id, title, description, multiplyingFactor);
        this.shade1 = shade1;
        this.shade2 = shade2;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int computeScore(DiceSpace[][] window) {
        int[] counts = new int[2];

        for(int i=0; i<window.length; i++) {
            for(int j=0; j<window[0].length; j++) {
                if (window[i][j].getDice() != null && window[i][j].getDice().getShade() == shade1)
                    counts[0]++;
                else if (window[i][j].getDice() != null && window[i][j].getDice().getShade() == shade2)
                    counts[1]++;
            }
        }

        return counts[0] <= counts[1] ? counts[0] * getFactor() : counts[1] * getFactor();
    }
}
