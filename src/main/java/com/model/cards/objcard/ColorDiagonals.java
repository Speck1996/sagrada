package com.model.cards.objcard;

import com.model.dice.Dice;
import com.model.patterns.DiceSpace;

import java.util.HashSet;
import java.util.Set;

/**
 * Color Diagonals Public Object Card.
 * The pattern of this card is: count of diagonally adjacent same color dice.
 * @see PublicObjCard
 */
public class ColorDiagonals extends PublicObjCard {

    /**
     * Constructs a ColorDiagonals card.
     * @param id the id of the card.
     * @param title the title of the card.
     * @param description the description of the card.
     * @param multiplyingFactor the multiplying factor of the card.
     * @see PublicObjCard#PublicObjCard(String, String, String, int)
     */
    ColorDiagonals(String id, String title, String description, int multiplyingFactor) {
        super(id, title, description, multiplyingFactor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int computeScore(DiceSpace[][] window) {

        Set<Dice> matching = new HashSet<>();

        for(int i=0; i<window.length-1; i++) {
            for(int j=0; j<window[0].length; j++) {
                if(window[i][j].getDice() != null) {
                    if(j-1 >= 0 && window[i+1][j-1].getDice() != null && window[i+1][j-1].getDice().getColor() == window[i][j].getDice().getColor()) {
                        matching.add(window[i][j].getDice());
                        matching.add(window[i+1][j-1].getDice());
                    }
                    if(j+1 < window[0].length && window[i+1][j+1].getDice() != null && window[i+1][j+1].getDice().getColor() == window[i][j].getDice().getColor()) {
                        matching.add(window[i][j].getDice());
                        matching.add(window[i+1][j+1].getDice());
                    }
                }
            }
        }

        return matching.size() * getFactor();
    }
}
