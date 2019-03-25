package com.model.cards.objcard;

import com.model.cards.ObjCard;
import com.model.patterns.DiceSpace;


/**
 * The abstract Public Object Card.
 * The points given by a Public Object Card is calculated as the product between the number of pattern matched in the window and the multiplying factor.
 */
public abstract class PublicObjCard extends ObjCard {


    /**
     * Constructs a PublicObjCard.
     * @param id the id of the card.
     * @param title the title of the card.
     * @param description the description of the card.
     * @param multiplyingFactor the multiplying factor of the card.
     * @see ObjCard#ObjCard(String, String, String, int)
     */
    protected PublicObjCard(String id, String title, String description, int multiplyingFactor){
        super(id, title, description, multiplyingFactor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract int computeScore(DiceSpace[][] window);


}
