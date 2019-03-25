package com.model.cards;

/**this abstract class has three string fields (id,title,description) initialized
 * in the constructor. The id is unique for every card. The methods are just gets of this fields
 * and an equals method that checks if the id,title and description matches, and a to string
 */


public abstract class Card {

    /**String representing the id of the card*/
    private String id;
    /**String representing the title of the card*/
    private String title;
    /**String representing the description of the card*/
    private String description;

    /**
     * Constructs a Card
     * @param id the id of the card.
     * @param title the title of the card.
     * @param description the description of the card.
     */
    Card(String id, String title, String description){

        this.id = id;
        this.title = title;
        this.description = description;
    }

    /**returns the id attribute of the card
     * @return the id of the card
     */
    public  String getId(){
        return this.id;
    }

    /**returns the description attribute of the card
     * @return the attribute of the card
     */
    public  String getDescription(){
        return this.description;
    }

    /**returns the title attribute of the card
     * @return the title of the card
     */
    public  String getTitle(){
        return this.title;
    }


    /**equals method for toolcard, checks if attributes matches with the given card
     * @param o this will be the card to be confronted with
     * @return true if the id, the title and the description matches false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return id == card.id &&
                title == card.title &&
                description == card.description;
    }


    /**string representation of the card
     * @return string representation: example ( 0000 - demoTitle - demoDescription)
     */
    @Override
    public String toString() {
        return id + " - " + title + " - " + description;
    }
}


