package com.model.cards.concretetoolcards;


import com.model.cards.ToolCard;

import com.model.gameboard.RoundBoard;

import com.model.dice.*;
import com.model.patterns.DieNotPlaceableException;
import com.model.patterns.WindowPatternCard;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static com.model.cards.concretetoolcards.ToolCardTestUtilities.*;
import static org.mockito.Mockito.when;


public class LensCutterTest {
    private ToolCard demoCard;


    @Before
    public void setUp(){
        ToolCardTestUtilities.setUp();
        demoCard = new LensCutter("demo","demo","demo");
        demoCard.setGameBoard(demoGameBoard);

    }



    @Test
    public void testPlayerCommand() throws CardNotUsableException{


       demoCard.activateCard(player);

        demoCard.playerCommand("1");
        demoCard.playerCommand("1,2");
        demoCard.playerCommand("1,4");

        //checking that the dice was correctly placed in the desired position, dice was correctly swapped
        Assert.assertEquals(ToolCardState.EXECUTED,demoCard.getState());
        Assert.assertEquals(new Dice(DiceColor.YELLOW,DiceShade.FOUR), wpc.getSpaces()[0][3].getDice());
        Assert.assertEquals(new Dice(DiceColor.GREEN,DiceShade.THREE),demoRd.getDice(0).get(1));
        Assert.assertEquals(1,demoStock.getDice().size());
        Assert.assertEquals(new Dice(DiceColor.YELLOW,DiceShade.TWO),demoStock.getDice().get(0));
    }

    @Test
    public void testAbortDieStockPick()throws CardNotUsableException,NoDiceException{
        ToolCardTestUtilities.testAbortPickStockDie(demoCard);
    }

    @Test
    public void testWrongInputDieStockPick()throws CardNotUsableException{
        for(int i = 0; i< 2; i++) {
            setUp();
            demoCard.activateCard(player);

            if(i == 0){
                demoCard.playerCommand("10");
            }else{
                demoCard.playerCommand("It's so unreal, Didn't look out below");
            }

            //checking place die was aborted
            Assert.assertEquals(ToolCardState.DIESTOCKPICK, demoCard.getState());
            Assert.assertEquals(2, demoGameBoard.getStock().getDice().size());
            Assert.assertEquals(new Dice(DiceColor.GREEN, DiceShade.THREE), demoGameBoard.getStock().getDice().get(0));
            Assert.assertEquals(new Dice(DiceColor.YELLOW, DiceShade.TWO), demoGameBoard.getStock().getDice().get(1));
        }
    }

    @Test
    public void testWrongRoundBoardInput()throws CardNotUsableException{
        for(int i = 0; i < 2; i++) {
            setUp();
            demoCard.activateCard(player);

            demoCard.playerCommand("1");
            if(i == 0){
                demoCard.playerCommand("10");
            }else{
                demoCard.playerCommand("Watch the time go right out the window Trying to hold on, but you didn't even know");
            }

            //checking that the dice was correctly placed in the desired position, dice was correctly swapped
            Assert.assertEquals(ToolCardState.ROUNDBOARDPICK, demoCard.getState());
            Assert.assertEquals(1, demoStock.getDice().size());
            Assert.assertEquals(new Dice(DiceColor.YELLOW, DiceShade.TWO), demoStock.getDice().get(0));
        }
    }

    @Test
    public void testSwappedDieNotPlaceable()throws CardNotUsableException,DieNotPlaceableException {
            setUp();
            WindowPatternCard window = new WindowPatternCard("0:TEST WINDOW:4:Y6BBBBBBBBBBBBBBBBBB");
            window.placeDice(0, 0, new Dice(DiceColor.YELLOW, DiceShade.THREE), true, true);
            when(ToolCardTestUtilities.player.getWindow()).thenReturn(window);

            demoCard.activateCard(player);

            //desired stock dice
            demoCard.playerCommand("1");
            demoCard.playerCommand("1,2");


            //asserting ToolCard execution aborted because the dice could not be placed and everything was the same as before
            Assert.assertEquals(ToolCardState.ABORTED, demoCard.getState());
            Assert.assertEquals(2, demoGameBoard.getStock().getDice().size());
            Assert.assertEquals(new Dice(DiceColor.GREEN, DiceShade.THREE), demoGameBoard.getStock().getDice().get(0));
            Assert.assertEquals(new Dice(DiceColor.YELLOW, DiceShade.TWO), demoGameBoard.getStock().getDice().get(1));
            Assert.assertEquals(new Dice(DiceColor.GREEN,DiceShade.THREE),demoRd.getDice(0).get(1));
    }

    @Test
    public void testAbortRoundBoardInput() throws CardNotUsableException{
        ToolCardTestUtilities.testAbortSecondInput(demoCard);
    }

    @Test
    public void testAbortPlaceDie()throws CardNotUsableException{
        demoCard.activateCard(player);

        demoCard.playerCommand("1");
        demoCard.playerCommand("1,2");
        demoCard.playerCommand("a");

        Assert.assertEquals(ToolCardState.ABORTED, demoCard.getState());
        Assert.assertEquals(2, demoGameBoard.getStock().getDice().size());
        Assert.assertEquals(new Dice(DiceColor.GREEN, DiceShade.THREE), demoGameBoard.getStock().getDice().get(0));
        Assert.assertEquals(new Dice(DiceColor.YELLOW, DiceShade.TWO), demoGameBoard.getStock().getDice().get(1));
        Assert.assertEquals(new Dice(DiceColor.YELLOW,DiceShade.FOUR),demoGameBoard.getGameRoundBoard().getDice(0).get(1));
    }

    @Test
    public void testWrongInputPlaceDie()throws CardNotUsableException{
        for(int i = 0; i < 2; i++) {
            setUp();
            demoCard.activateCard(player);

            demoCard.playerCommand("1");
            demoCard.playerCommand("1,2");
            if(i == 0) {
                demoCard.playerCommand("10");
            }else{
                demoCard.playerCommand("Wasted it all just to watch you go I kept everything inside");
            }
            Assert.assertEquals(ToolCardState.PLACEDIE, demoCard.getState());
            Assert.assertEquals(1, demoGameBoard.getStock().getDice().size());
            Assert.assertEquals(new Dice(DiceColor.YELLOW, DiceShade.TWO), demoGameBoard.getStock().getDice().get(0));
            Assert.assertEquals(new Dice(DiceColor.GREEN, DiceShade.THREE), demoGameBoard.getGameRoundBoard().getDice(0).get(1));
        }
    }


    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testEmptyRoundActivation() throws CardNotUsableException{
        RoundBoard roundBoard = new RoundBoard();
        demoGameBoard.setRoundBoard(roundBoard);
        exception.expect(CardNotUsableException.class);
        demoCard.setGameBoard(demoGameBoard);
        demoCard.activateCard(player);
        exception.expectMessage("No die in the roundBoard, aborting....");
        Assert.assertEquals(ToolCardState.NEUTRAL,demoCard.getState());
    }

    @Test
    public void testActivationSinglePlayer() throws CardNotUsableException{
        demoCard.activateCard(player,new Dice(DiceColor.GREEN,DiceShade.THREE));
        Assert.assertEquals(ToolCardState.DIESTOCKPICK,demoCard.getState());
    }

}