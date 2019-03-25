package com.model.cards.concretetoolcards;


import com.model.gameboard.RoundBoard;

import com.model.cards.ToolCard;
import com.model.dice.*;
import com.model.patterns.DieNotPlaceableException;
import com.model.patterns.WindowPatternCard;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static com.model.cards.concretetoolcards.ToolCardTestUtilities.demoGameBoard;


import static com.model.cards.concretetoolcards.ToolCardTestUtilities.player;

public class TapWheelTest {
    private ToolCard demoCard;


    @Before
    public void setUp(){
        ToolCardTestUtilities.setUp();
        demoCard = new TapWheel("demo","demo","demo");
        demoCard.setGameBoard(demoGameBoard);

    }


    @Test
    public void testPlayerCommand()throws CardNotUsableException{

        demoCard.activateCard(player);


        demoCard.playerCommand("Y");
        demoCard.playerCommand("2,2");
        demoCard.playerCommand("2,2");
        demoCard.playerCommand("2,4");

        demoCard.playerCommand("Y");
        demoCard.playerCommand("1,3");
        demoCard.playerCommand("3,3");
        //checking that the dice was correctly placed in the desired position
        Assert.assertEquals(new Dice(DiceColor.RED, DiceShade.FIVE), player.getWindow().getSpaces()[1][3].getDice());
        Assert.assertEquals(new Dice(DiceColor.RED, DiceShade.TWO), player.getWindow().getSpaces()[2][2].getDice());
        Assert.assertNull(player.getWindow().getSpaces()[1][1].getDice());
        Assert.assertNull(player.getWindow().getSpaces()[0][2].getDice());


        //checking that the previous space is now empty
        System.out.println(player.getWindow().toString());

    }


    @Test
    public void testNoMovingExecution() throws CardNotUsableException{

        demoCard.activateCard(player);
        demoCard.playerCommand("N");


        Assert.assertEquals(new Dice(DiceColor.RED, DiceShade.FIVE), player.getWindow().getSpaces()[1][1].getDice());
        Assert.assertEquals(new Dice(DiceColor.RED, DiceShade.TWO), player.getWindow().getSpaces()[0][2].getDice());

        Assert.assertEquals(ToolCardState.EXECUTED,demoCard.getState());
    }

    @Test
    public void abortMovingSelection()throws CardNotUsableException{

        demoCard.activateCard(player);
        demoCard.playerCommand("a");

        Assert.assertEquals(ToolCardState.ABORTED,demoCard.getState());
    }

    @Test
    public void testAbortRoundBoardPicking() throws CardNotUsableException{
        demoCard.activateCard(player);


        demoCard.playerCommand("Y");
        demoCard.playerCommand("a");

        Assert.assertEquals(ToolCardState.ABORTED,demoCard.getState());

    }

    @Test
    public void testWrongInputMovingSelection() throws CardNotUsableException{
        for(int i = 0; i < 2; i++) {
            setUp();
            demoCard.activateCard(player);
            if(i == 0) {
                demoCard.playerCommand("I tried so hard And got so far");
            }else{
                demoCard.playerCommand("10");
            }

            Assert.assertEquals(ToolCardState.USERDEMAND, demoCard.getState());
        }
    }


    @Test
    public void testWrongInputRoundBoardPick()throws CardNotUsableException{
        for(int i = 0; i < 2; i++) {
            setUp();
            demoCard.activateCard(player);
            demoCard.playerCommand("Y");
            if(i == 0) {
                demoCard.playerCommand("But in the end It doesn't even matter");
            }else{
                demoCard.playerCommand("10");
            }
            Assert.assertEquals(ToolCardState.ROUNDBOARDPICK, demoCard.getState());
        }
    }

    @Test
    public void testWrongColorDiePick() throws CardNotUsableException{
        demoCard.activateCard(player);
        demoCard.playerCommand("Y");
        demoCard.playerCommand("2,2");
        demoCard.playerCommand("1,1");
        Assert.assertEquals(ToolCardState.PICKWINDOWDIE, demoCard.getState());

    }

    @Test
    public void testWrongInputFirstDiePick() throws CardNotUsableException{
        for(int i = 0; i < 2; i++) {
            setUp();
            demoCard.activateCard(player);
            demoCard.playerCommand("Y");
            demoCard.playerCommand("2,2");
            if(i == 0){
                demoCard.playerCommand("10");
            }else {
                demoCard.playerCommand("Tralalalal");
            }
            Assert.assertEquals(ToolCardState.PICKWINDOWDIE, demoCard.getState());
        }
    }

    @Test
    public void testAbortFirstDiePick()throws CardNotUsableException{
        demoCard.activateCard(player);
        demoCard.playerCommand("Y");
        demoCard.playerCommand("2,2");
        demoCard.playerCommand("a");
        Assert.assertEquals(ToolCardState.ABORTED, demoCard.getState());
    }

    @Test
    public void testAbortSecondMovingSelection() throws CardNotUsableException{
        demoCard.activateCard(player);
        demoCard.playerCommand("Y");
        demoCard.playerCommand("2,2");
        demoCard.playerCommand("2,4");
        demoCard.playerCommand("a");
        Assert.assertEquals(ToolCardState.ABORTED,demoCard.getState());
    }

    @Test
    public void testWrongInputSecondMovingSelection() throws CardNotUsableException{
        for(int i =0; i < 2; i++) {
            setUp();
            demoCard.activateCard(player);
            demoCard.playerCommand("Y");
            demoCard.playerCommand("2,2");
            demoCard.playerCommand("2,2");
            demoCard.playerCommand("2,4");
            if(i == 0){
                demoCard.playerCommand("10");
            }else {
                demoCard.playerCommand("Up on melancholy hill ");
            }
        }
        Assert.assertEquals(ToolCardState.USERDEMAND,demoCard.getState());
    }


    @Test
    public void testMovingOnlyOneDie() throws CardNotUsableException{
        demoCard.activateCard(player);
        demoCard.playerCommand("Y");
        demoCard.playerCommand("2,2");
        demoCard.playerCommand("2,2");

        demoCard.playerCommand("2,4");
        demoCard.playerCommand("N");

        Assert.assertEquals(ToolCardState.EXECUTED,demoCard.getState());
        Assert.assertEquals(new Dice(DiceColor.RED, DiceShade.FIVE), player.getWindow().getSpaces()[1][3].getDice());
        Assert.assertNull(player.getWindow().getSpaces()[1][1].getDice());
    }

    @Test
    public void testWrongInputSecondDiePick()throws CardNotUsableException{
        for(int i = 0; i < 2; i++) {
            setUp();
            demoCard.activateCard(player);
            demoCard.playerCommand("Y");
            demoCard.playerCommand("2,2");
            demoCard.playerCommand("2,2");

            demoCard.playerCommand("2,4");
            demoCard.playerCommand("Y");
            if(i == 0){
                demoCard.playerCommand("10");
            }else {
                demoCard.playerCommand("There's a plastic tree ");
            }

            Assert.assertEquals(ToolCardState.PICKWINDOWDIE, demoCard.getState());
            Assert.assertEquals(new Dice(DiceColor.RED, DiceShade.FIVE), player.getWindow().getSpaces()[1][3].getDice());
            Assert.assertNull(player.getWindow().getSpaces()[1][1].getDice());
        }
    }

    @Test
    public void testAbortSecondWindowDiePick()throws CardNotUsableException{
        demoCard.activateCard(player);
        demoCard.playerCommand("Y");
        demoCard.playerCommand("2,2");
        demoCard.playerCommand("2,2");

        demoCard.playerCommand("2,4");
        demoCard.playerCommand("Y");
        demoCard.playerCommand("a");

        Assert.assertEquals(ToolCardState.ABORTED, demoCard.getState());

    }

    @Test
    public void testWrongInputPlaceSecondDie() throws CardNotUsableException{
        for(int i = 0; i < 2; i++) {
            setUp();
            demoCard.activateCard(player);
            demoCard.playerCommand("Y");
            demoCard.playerCommand("2,2");
            demoCard.playerCommand("2,2");

            demoCard.playerCommand("2,4");
            demoCard.playerCommand("Y");
            demoCard.playerCommand("1,3");
            if(i == 0) {
                demoCard.playerCommand("10");
            }else{
                demoCard.playerCommand("Are you here with me ");
            }

            Assert.assertEquals(ToolCardState.PLACEDIE, demoCard.getState());
            Assert.assertEquals(new Dice(DiceColor.RED, DiceShade.FIVE), player.getWindow().getSpaces()[1][3].getDice());
            Assert.assertNull(player.getWindow().getSpaces()[1][1].getDice());
        }
    }


    @Test
    public void testAbortPlaceSecondDie() throws CardNotUsableException{
        for(int i = 0; i < 2; i++) {
            setUp();
            demoCard.activateCard(player);
            demoCard.playerCommand("Y");
            demoCard.playerCommand("2,2");
            demoCard.playerCommand("2,2");

            demoCard.playerCommand("2,4");
            demoCard.playerCommand("Y");
            demoCard.playerCommand("1,3");
            demoCard.playerCommand("a");

            Assert.assertEquals(ToolCardState.ABORTED, demoCard.getState());

        }
    }

    @Test
    public void testWrongColorSecondDiePick() throws CardNotUsableException{
        demoCard.activateCard(player);
        demoCard.playerCommand("Y");
        demoCard.playerCommand("2,2");
        demoCard.playerCommand("2,4");
        demoCard.playerCommand("Y");
        demoCard.playerCommand("1,1");
        Assert.assertEquals(ToolCardState.PICKWINDOWDIE,demoCard.getState());
    }

    @Test
    public void testNoFirstMovableDiceSelectedColor()throws CardNotUsableException,DieNotPlaceableException {
        WindowPatternCard window = new WindowPatternCard("0:TEST WINDOW:4:Y3BBBYBBBBBBBBBBBBBB");
        window.placeDice(0, 0, new Dice(DiceColor.YELLOW, DiceShade.THREE), true, true);
        player.setWindow(window);
        demoCard.activateCard(player);
        demoCard.playerCommand("Y");
        demoCard.playerCommand("2,1");

        Assert.assertEquals(ToolCardState.EXECUTED,demoCard.getState());

    }

    @Test
    public void testNoSecondMovableDiceSelectedColor()throws CardNotUsableException,DieNotPlaceableException {
        WindowPatternCard window = new WindowPatternCard("0:TEST WINDOW:4:R3BBBRBBBBBBBBBBBBBB");
        window.placeDice(0, 0, new Dice(DiceColor.RED, DiceShade.THREE), true, true);
        player.setWindow(window);
        demoCard.activateCard(player);
        demoCard.playerCommand("Y");
        demoCard.playerCommand("2,2");
        demoCard.playerCommand("1,1");
        demoCard.playerCommand("2,1");
        demoCard.playerCommand("Y");


        Assert.assertEquals(ToolCardState.EXECUTED,demoCard.getState());

    }

    @Test
    public void testActivateSinglePLayer() throws CardNotUsableException{
        demoCard.activateCard(player,new Dice(DiceColor.BLUE,DiceShade.THREE));
        Assert.assertEquals(ToolCardState.USERDEMAND,demoCard.getState());


    }





    @Rule
    public ExpectedException exception = ExpectedException.none();



    @Test
    public void testEmptyRoundBoard() throws CardNotUsableException{
        RoundBoard emptyRoundBoard = new RoundBoard();
        demoGameBoard.setRoundBoard(emptyRoundBoard);
        exception.expect(CardNotUsableException.class);
        exception.expectMessage("No die in the roundBoard, aborting....");
        demoCard.activateCard(player);
        Assert.assertEquals(ToolCardState.NEUTRAL,demoCard.getState());
    }

}