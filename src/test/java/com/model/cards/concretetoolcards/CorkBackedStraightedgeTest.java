package com.model.cards.concretetoolcards;

import com.model.dice.*;
import com.model.cards.ToolCard;
import com.model.patterns.DieNotPlaceableException;
import com.model.patterns.WindowPatternCard;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


import static com.model.cards.concretetoolcards.ToolCardTestUtilities.demoGameBoard;
import static com.model.cards.concretetoolcards.ToolCardTestUtilities.player;
import static com.model.cards.concretetoolcards.ToolCardTestUtilities.wpc;



public class CorkBackedStraightedgeTest {
    private ToolCard demoCard;
    private Stock demoStock;
    private Dice demoDice;

    @Before
    public void setUp(){
        ToolCardTestUtilities.setUp();
        demoStock = new Stock();
        demoDice = new Dice(DiceColor.GREEN,DiceShade.ONE);
        demoStock.insertDice(demoDice);
        demoStock.insertDice(new Dice(DiceColor.RED,DiceShade.THREE));
        demoGameBoard.setStock(demoStock);
        demoCard = new CorkBackedStraightedge("demo","demo","demo");
        demoCard.setGameBoard(demoGameBoard);

    }



    @Test
    public void testPlayerCommand(){


        try {
            demoCard.activateCard(ToolCardTestUtilities.player);
        }catch (CardNotUsableException error){
            System.out.println("problems");
        }
        demoCard.playerCommand("1");
        demoCard.playerCommand("4,3");

        //checking that the dice was correctly placed in the desired position
        Assert.assertEquals(new Dice(DiceColor.GREEN, DiceShade.ONE), ToolCardTestUtilities.wpc.getSpaces()[3][2].getDice());

        //assert stock not contains the used dice anymore
        Assert.assertEquals(1,demoStock.getDice().size());
        Assert.assertNotEquals(new Dice(DiceColor.GREEN,DiceShade.ONE),demoStock.getDice().get(0));
        Assert.assertTrue(!demoStock.getDice().contains(demoDice));

        System.out.println(demoStock.toString());
        System.out.println(ToolCardTestUtilities.wpc.toString());

    }


    @Test
    public void testNotIsolatedInput() throws CardNotUsableException{

        demoCard.activateCard(ToolCardTestUtilities.player);
        demoCard.playerCommand("1");
        demoCard.playerCommand("3,2");



        //assert stock not contains the used dice anymore
        Assert.assertEquals(1,demoStock.getDice().size());
        Assert.assertNotEquals(new Dice(DiceColor.GREEN,DiceShade.ONE),demoStock.getDice().get(0));
        Assert.assertTrue(!demoStock.getDice().contains(demoDice));

        //dice was not placed
        Assert.assertNull(wpc.getSpaces()[3][4].getDice());
        Assert.assertEquals(ToolCardState.PLACEDIE,demoCard.getState());

        System.out.println(wpc.toString());
        System.out.println(demoStock.toString());

    }


    @Rule
    public ExpectedException exception = ExpectedException.none();


    @Test
    public void testPickNotPlaceableDie()throws DieNotPlaceableException,CardNotUsableException,NoDiceException {
        WindowPatternCard window = new WindowPatternCard("0:TEST WINDOW:4:YBBBBBBBBBBBBBBBBBBB");
        window.placeDice(0,0,new Dice(DiceColor.YELLOW,DiceShade.THREE),true,true);
        player.setWindow(window);

        demoCard.setGameBoard(demoGameBoard);
        demoCard.activateCard(ToolCardTestUtilities.player);
        demoCard.playerCommand("1");


        //asserting the stock is in the state before ToolCard Execution
        Assert.assertEquals(2,demoGameBoard.getStock().getDice().size());
        Assert.assertEquals(new Dice(DiceColor.GREEN,DiceShade.ONE),demoGameBoard.getStock().getDice(0));
        Assert.assertEquals(new Dice(DiceColor.RED,DiceShade.THREE),demoGameBoard.getStock().getDice(1));

        //assert ToolCard state is aborted
        Assert.assertEquals(ToolCardState.ABORTED,demoCard.getState());


    }


    @Test
    public void testPickStockDieAbort() throws CardNotUsableException,NoDiceException{





        demoCard.setGameBoard(demoGameBoard);
        demoCard.activateCard(ToolCardTestUtilities.player);
        demoCard.playerCommand("a");


        //asserting the stock is in the state before ToolCard Execution
        Assert.assertEquals(2,demoGameBoard.getStock().getDice().size());
        Assert.assertEquals(new Dice(DiceColor.GREEN,DiceShade.ONE),demoGameBoard.getStock().getDice(0));
        Assert.assertEquals(new Dice(DiceColor.RED,DiceShade.THREE),demoGameBoard.getStock().getDice(1));

        //assert ToolCard state is aborted
        Assert.assertEquals(ToolCardState.ABORTED,demoCard.getState());

    }

    @Test
    public void testWrongInputPickStockDie()throws CardNotUsableException,NoDiceException {

        demoCard.setGameBoard(demoGameBoard);
        demoCard.activateCard(ToolCardTestUtilities.player);
        demoCard.playerCommand("10");


        //asserting the stock is in the state before ToolCard Execution
        Assert.assertEquals(2,demoGameBoard.getStock().getDice().size());
        Assert.assertEquals(new Dice(DiceColor.GREEN,DiceShade.ONE),demoGameBoard.getStock().getDice(0));
        Assert.assertEquals(new Dice(DiceColor.RED,DiceShade.THREE),demoGameBoard.getStock().getDice(1));

        //assert ToolCard state remains in the DieStockPick state
        Assert.assertEquals(ToolCardState.DIESTOCKPICK,demoCard.getState());

    }

    @Test
    public void testAbortPlaceDie()throws CardNotUsableException{
        demoCard.setGameBoard(demoGameBoard);

        demoCard.activateCard(ToolCardTestUtilities.player);

        demoCard.playerCommand("1");
        demoCard.playerCommand("a");

        //assert stock state is back as before
        Assert.assertEquals(2,demoGameBoard.getStock().getDice().size());
        Assert.assertEquals(new Dice(DiceColor.GREEN,DiceShade.ONE),demoGameBoard.getStock().getDice().get(0));
        Assert.assertEquals(new Dice(DiceColor.RED,DiceShade.THREE),demoGameBoard.getStock().getDice().get(1));

        //assert ToolCard state is aborted
        Assert.assertEquals(ToolCardState.ABORTED,demoCard.getState());

    }


    @Test
    public void testActivateSinglePlayer()throws CardNotUsableException{
        demoCard.activateCard(ToolCardTestUtilities.player,new Dice(DiceColor.YELLOW,DiceShade.THREE));
        Assert.assertEquals(ToolCardState.DIESTOCKPICK,demoCard.getState());

    }

}