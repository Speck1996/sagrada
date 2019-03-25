package com.model.cards.concretetoolcards;


import com.model.dice.*;
import com.model.cards.ToolCard;
import com.model.patterns.DieNotPlaceableException;
import com.model.patterns.WindowPatternCard;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static com.model.cards.concretetoolcards.ToolCardTestUtilities.demoGameBoard;
import static com.model.cards.concretetoolcards.ToolCardTestUtilities.player;
import static com.model.cards.concretetoolcards.ToolCardTestUtilities.wpc;
import static org.mockito.Mockito.when;


public class LathekinTest {
    private ToolCard demoCard;


    @Before
    public void setUp(){
        ToolCardTestUtilities.setUp();
        demoCard = new Lathekin("demo","demo","demo");
        demoCard.setGameBoard(demoGameBoard);

    }



    @Test
    public void testPlayerCommand()throws CardNotUsableException{


        demoCard.activateCard(player);

        demoCard.playerCommand("1,1");
        demoCard.playerCommand("1,4");
        demoCard.playerCommand("2,2");
        demoCard.playerCommand("2,4");


        //checking that the dice was correctly placed in the desired position
        Assert.assertEquals(new Dice(DiceColor.YELLOW, DiceShade.ONE), wpc.getSpaces()[0][3].getDice());
        Assert.assertEquals(new Dice(DiceColor.RED, DiceShade.FIVE), wpc.getSpaces()[1][3].getDice());

        //checking that the previous space is now empty
        Assert.assertNull(wpc.getSpaces()[0][0].getDice());
        Assert.assertNull(wpc.getSpaces()[1][1].getDice());

        System.out.println(wpc.toString());
    }

    @Test
    public void testAbortPickWindowDie()throws CardNotUsableException{
        for(int i = 0; i < 2; i++) {
            setUp();
            demoCard.activateCard(player);

            if(i == 0) {
                demoCard.playerCommand("10");
            }else {
                demoCard.playerCommand("Will eventually be a memory of a time when");
            }

            Assert.assertEquals(new Dice(DiceColor.YELLOW, DiceShade.ONE), wpc.getSpaces()[0][0].getDice());
            Assert.assertEquals(new Dice(DiceColor.RED, DiceShade.FIVE), wpc.getSpaces()[1][1].getDice());
            Assert.assertEquals(ToolCardState.PICKWINDOWDIE, demoCard.getState());
        }
    }

    @Test
    public void testWrongInputPickWindowDie()throws CardNotUsableException{
        demoCard.activateCard(player);

        demoCard.playerCommand("a");


        Assert.assertEquals(new Dice(DiceColor.YELLOW, DiceShade.ONE),wpc.getSpaces()[0][0].getDice());
        Assert.assertEquals(new Dice(DiceColor.RED, DiceShade.FIVE),wpc.getSpaces()[1][1].getDice());
        Assert.assertEquals(ToolCardState.ABORTED,demoCard.getState());



    }


    @Test
    public void testAbortPlaceDie()throws CardNotUsableException{
        demoCard.activateCard(player);

        demoCard.playerCommand("1,1");
        demoCard.playerCommand("a");


        Assert.assertEquals(new Dice(DiceColor.YELLOW, DiceShade.ONE),player.getWindow().getSpaces()[0][0].getDice());
        Assert.assertEquals(new Dice(DiceColor.RED, DiceShade.FIVE),player.getWindow().getSpaces()[1][1].getDice());
        Assert.assertEquals(ToolCardState.ABORTED,demoCard.getState());

    }


    @Test
    public void testWrongInputPlaceDie()throws CardNotUsableException{
        for(int i = 0; i < 2; i++) {
            setUp();
            demoCard.activateCard(player);


            demoCard.playerCommand("1,1");
            if(i == 0){
                demoCard.playerCommand("10");
            }else{
                demoCard.playerCommand("And even though I tried, it all fell apart What it meant to me");
            }

            Assert.assertNull(wpc.getSpaces()[0][0].getDice());
            Assert.assertEquals(new Dice(DiceColor.RED, DiceShade.FIVE), wpc.getSpaces()[1][1].getDice());
            Assert.assertEquals(ToolCardState.PLACEDIE, demoCard.getState());
        }
    }

    @Test
    public void testNoPlaceableDieAfterFirstMove() throws CardNotUsableException,DieNotPlaceableException {
        WindowPatternCard window = new WindowPatternCard("0:TEST WINDOW:4:Y3BBBYBBBBBBBBBBBBBB");
        window.placeDice(0, 0, new Dice(DiceColor.YELLOW, DiceShade.THREE), true, true);
        when(ToolCardTestUtilities.player.getWindow()).thenReturn(window);
        System.out.println(window.toString());
        demoCard.activateCard(player);
        demoCard.playerCommand("1,1");
        demoCard.playerCommand("2,1");


        Assert.assertEquals(new Dice(DiceColor.YELLOW, DiceShade.ONE),wpc.getSpaces()[0][0].getDice());
        Assert.assertEquals(new Dice(DiceColor.RED, DiceShade.FIVE),wpc.getSpaces()[1][1].getDice());
        Assert.assertEquals(ToolCardState.ABORTED,demoCard.getState());
    }

    @Test
    public void testActivateCardSinglePlayer()throws CardNotUsableException{
        demoCard.activateCard(player,new Dice(DiceColor.YELLOW,DiceShade.THREE));
        Assert.assertEquals(ToolCardState.PICKWINDOWDIE,demoCard.getState());


    }
}
