package com.model.cards.concretetoolcards;


import com.model.cards.ToolCard;
import com.model.dice.*;
import com.model.patterns.DieNotPlaceableException;
import com.model.patterns.WindowPatternCard;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static com.model.cards.concretetoolcards.ToolCardTestUtilities.demoGameBoard;
import static com.model.cards.concretetoolcards.ToolCardTestUtilities.player;
import static com.model.cards.concretetoolcards.ToolCardTestUtilities.wpc;
import static org.mockito.Mockito.when;


public class GrozingPliersTest {
    private ToolCard demoCard;


    @Before
    public void setUp(){
       ToolCardTestUtilities.setUp();
       demoCard = new GrozingPliers("demo","demo","demo");
       demoCard.setGameBoard(demoGameBoard);
        System.out.println(wpc);
    }



    @Test
    public void testPlayerCommand()throws CardNotUsableException{
      for(int i = 0; i < 2; i++){
          setUp();
          demoCard.activateCard(player);

          demoCard.playerCommand("1");
          if(i == 0) {
              demoCard.playerCommand("I");
          }else{
              demoCard.playerCommand("D");
          }
          demoCard.playerCommand("3,2");
          //checking that the dice was correctly placed in the desired position
          if(i == 0){
              Assert.assertEquals(new Dice(DiceColor.GREEN, DiceShade.FOUR), wpc.getSpaces()[2][1].getDice());
          }else{
              Assert.assertEquals(new Dice(DiceColor.GREEN, DiceShade.TWO), wpc.getSpaces()[2][1].getDice());

          }
          System.out.println(wpc.toString());
      }

    }


    @Test
    public void testAbortPickStockDie()throws CardNotUsableException{
        demoCard.activateCard(player);

        demoCard.playerCommand("a");

        //checking place die was aborted
        Assert.assertEquals(ToolCardState.ABORTED,demoCard.getState());
        Assert.assertEquals(2, demoGameBoard.getStock().getDice().size());
        Assert.assertEquals(new Dice(DiceColor.GREEN,DiceShade.THREE),demoGameBoard.getStock().getDice().get(0));
        Assert.assertEquals(new Dice(DiceColor.YELLOW,DiceShade.TWO),demoGameBoard.getStock().getDice().get(1));

    }


    @Test
    public void testWrongInputPickStockDie()throws CardNotUsableException,NoDiceException{
        ToolCardTestUtilities.testWrongInputPickStockDie(demoCard);
    }


    @Test
    public void testAbortUserDemand() throws CardNotUsableException{
        ToolCardTestUtilities.testAbortSecondInput(demoCard);
    }


    @Test
    public void testWrongInputUserDemand() throws CardNotUsableException{
        for(int i = 0; i<2; i++) {
            setUp();
            demoCard.activateCard(player);

            demoCard.playerCommand("1");

            if(i == 0){
                demoCard.playerCommand("Time is a valuable thing, Watch it fly by as the pendulum swings");
            }else{
                demoCard.playerCommand("10");
            }

            //checking place die was aborted
            Assert.assertEquals(ToolCardState.USERDEMAND, demoCard.getState());
            Assert.assertEquals(1, demoGameBoard.getStock().getDice().size());
            Assert.assertEquals(new Dice(DiceColor.YELLOW, DiceShade.TWO), demoGameBoard.getStock().getDice().get(0));
        }

    }

    @Test
    public void testMinMaxReachedCatch() throws CardNotUsableException {
        for (int i = 0; i < 2; i++) {
            setUp();
            demoGameBoard.getStock().insertDice(new Dice(DiceColor.GREEN, DiceShade.ONE));
            demoGameBoard.getStock().insertDice(new Dice(DiceColor.GREEN, DiceShade.SIX));
            demoCard.activateCard(player);


            if (i == 0) {
                demoCard.playerCommand("3");

                demoCard.playerCommand("D");
                Assert.assertEquals(new Dice(DiceColor.GREEN, DiceShade.SIX), demoGameBoard.getStock().getDice().get(2));


            } else {
                demoCard.playerCommand("4");

                demoCard.playerCommand("I");
                Assert.assertEquals(new Dice(DiceColor.GREEN, DiceShade.ONE), demoGameBoard.getStock().getDice().get(2));

            }

            //checking place die was aborted
            Assert.assertEquals(ToolCardState.USERDEMAND, demoCard.getState());
            Assert.assertEquals(3, demoGameBoard.getStock().getDice().size());
            Assert.assertEquals(new Dice(DiceColor.GREEN, DiceShade.THREE), demoGameBoard.getStock().getDice().get(0));
            Assert.assertEquals(new Dice(DiceColor.YELLOW, DiceShade.TWO), demoGameBoard.getStock().getDice().get(1));
        }

    }

    @Test
    public void testAbortPlaceDie() throws CardNotUsableException{
        for(int i = 0; i < 2; i++){
            setUp();
            demoCard.activateCard(player);

            demoCard.playerCommand("1");
            if(i == 0) {
                demoCard.playerCommand("I");
            }else{
                demoCard.playerCommand("D");
            }
            demoCard.playerCommand("a");

            //checking that the dice was aborted
            //checking place die was aborted
            Assert.assertEquals(ToolCardState.ABORTED,demoCard.getState());
            Assert.assertEquals(2, demoGameBoard.getStock().getDice().size());
            Assert.assertEquals(new Dice(DiceColor.GREEN,DiceShade.THREE),demoGameBoard.getStock().getDice().get(0));
            Assert.assertEquals(new Dice(DiceColor.YELLOW,DiceShade.TWO),demoGameBoard.getStock().getDice().get(1));
        }
    }


    @Test
    public void testDieNotPlaceable()throws CardNotUsableException,DieNotPlaceableException {
        for(int i = 0; i < 2; i++) {
            setUp();
            WindowPatternCard window = new WindowPatternCard("0:TEST WINDOW:4:Y6BBBBBBBBBBBBBBBBBB");
            window.placeDice(0, 0, new Dice(DiceColor.YELLOW, DiceShade.THREE), true, true);
            when(ToolCardTestUtilities.player.getWindow()).thenReturn(window);

            demoCard.activateCard(player);

            //desired stock dice
            demoCard.playerCommand("1");

            if(i == 0) {
                demoCard.playerCommand("I");
            }else{
                demoCard.playerCommand("D");
            }

            //asserting ToolCard execution aborted because the dice could not be placed and everything was the same as before
            Assert.assertEquals(ToolCardState.ABORTED, demoCard.getState());
            Assert.assertEquals(2, demoGameBoard.getStock().getDice().size());
            Assert.assertEquals(new Dice(DiceColor.GREEN, DiceShade.THREE), demoGameBoard.getStock().getDice().get(0));
            Assert.assertEquals(new Dice(DiceColor.YELLOW, DiceShade.TWO), demoGameBoard.getStock().getDice().get(1));
        }
    }

    @Test
    public void testWrongInputPlaceDie() throws CardNotUsableException{
        for(int i = 0; i < 2; i++) {
            setUp();
            demoCard.activateCard(player);

            demoCard.playerCommand("1");
            if (i == 0) {
                demoCard.playerCommand("I");
                demoCard.playerCommand("Watch it count down to the end of the day The clock ticks life away");

            } else {
                demoCard.playerCommand("D");
                demoCard.playerCommand("10");

            }
            //checking that the dice was correctly placed in the desired position
            //checking place die was aborted
            Assert.assertEquals(ToolCardState.PLACEDIE, demoCard.getState());
            Assert.assertEquals(1, demoGameBoard.getStock().getDice().size());
            Assert.assertEquals(new Dice(DiceColor.YELLOW, DiceShade.TWO), demoGameBoard.getStock().getDice().get(0));

        }

    }

    @Test
    public void testActivateSinglePlayer() throws CardNotUsableException{
        demoCard.activateCard(player,new Dice(DiceColor.PURPLE,DiceShade.SIX));
        Assert.assertEquals(ToolCardState.DIESTOCKPICK,demoCard.getState());
    }
}