package com.model.cards;

import org.junit.Test;

import static org.junit.Assert.*;

public class CardLoaderTest {
    CardLoader unitundertes = new CardLoader();

    @Test
    public void loadCards() {


    }

    @Test
    public void getToolcardeck() {
    }

    @Test
    public void getPublicObjCardDeck() {
    }

    @Test
    public void getPrivateobjcardeck() throws WrongIdException{
        unitundertes.loadCards();
        assertEquals("COTP0", unitundertes.getPrivateObjCardDeck().get(0).getId());
        assertEquals("COTR0", unitundertes.getPrivateObjCardDeck().get(1).getId());
    }
}