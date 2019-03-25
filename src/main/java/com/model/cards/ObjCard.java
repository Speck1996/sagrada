package com.model.cards;


import com.model.patterns.DiceSpace;

/**Class that extends the card class from which inherits the id, the title, and the description and its getters
 * It adds an integer attribute that is used in the computeScore method: is the multiplying factor used in the specific
 * ObjCards which inherit from this in the compute score method
 * It has an abstract compute score method that will be implemented in his children (every typology has its own algorithm)
 * and returns an integer (this will represent the point obtained from the objcard)
 * @see com.model.cards.objcard.PrivateObjCard
 * @see com.model.cards.objcard.ColorDiagonals
 * @see com.model.cards.objcard.ColumnColorVariety
 * @see com.model.cards.objcard.ColumnShadeVariety
 * @see com.model.cards.objcard.PairsShades
 * @see com.model.cards.objcard.RowColorVariety
 * @see com.model.cards.objcard.ShadeVariety
 * @see com.model.cards.objcard.RowShadeVariety
 */



public abstract class ObjCard extends Card {

    /**Integer that will be used as a multiplying factor for the object card
     * algorithm
     */
    private int multiplyingFactor;

    /**
     * Constructs an ObjCard.
     * @param id the id of the card.
     * @param title the title of the card.
     * @param name the name of the card.
     * @param multiplyingFactor the multiplaying factor of the card.
     * @see Card#Card(String, String, String)
     */
    public ObjCard(String id, String title, String name, int multiplyingFactor){
        super(id,title,name);
        this.multiplyingFactor = multiplyingFactor;
    }


    /**
     * Computes the score of this card on the specified window.
     * @param window the two-dimensional array representing the window used in the compute score algorithm.
     * @return the points obtained.
     */
    public abstract int computeScore(DiceSpace[][] window);   //this will calculate points with the right algorithm depending on the obj card


    /**Getter for the multiplying factor
     * @return integer, the multiplying factor used in compute score
     */
    public int getFactor(){
        return this.multiplyingFactor;
    }


    /** Equals method of the objcard, adds the multiplying factor check to the card equals method
     * @param o this will be the card to be confronted with
     * @return true if the attributes (id,title,description,multiplying factor) of the two cards matches
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObjCard card = (ObjCard) o;
        return super.equals(o) &&
                card.multiplyingFactor == multiplyingFactor;
    }

    /**toString for objcards
     * @return string that represents the card: example: 0000 - demoTitle - demoDescription - 2
     */
    @Override
    public String toString() {
        return super.toString() + " - factor: " + multiplyingFactor;
    }
}
