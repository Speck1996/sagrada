package com.model.cards.concretetoolcards;



import com.model.cards.ToolCard;
import com.model.dice.*;
import com.model.patterns.DieNotPlaceableException;
import com.model.patterns.WindowPatternCard;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


import static com.model.cards.concretetoolcards.ToolCardTestUtilities.*;
import static org.mockito.Mockito.when;


public class GrindingStoneTest {
    private ToolCard demoCard;

    @Before
    public void setUp(){
      ToolCardTestUtilities.setUp();
      demoCard = new GrindingStone("demo","demo","demo");
      demoCard.setGameBoard(demoGameBoard);

    }



    @Test
    public void testPlayerCommand()throws CardNotUsableException{

        demoCard.activateCard(player);
        demoCard.playerCommand("1");
        demoCard.playerCommand("1,4");

        //checking that the dice was correctly placed in the desired position
        Assert.assertEquals(new Dice(DiceColor.GREEN, DiceShade.FOUR), wpc.getSpaces()[0][3].getDice());
        Assert.assertEquals(ToolCardState.EXECUTED,demoCard.getState());

        System.out.println(wpc.toString());
    }



    @Test
    public void testNotPlaceableDie() throws CardNotUsableException,DieNotPlaceableException {
        WindowPatternCard window = new WindowPatternCard("0:TEST WINDOW:4:Y2BBBBBBBBBBBBBBBBBB");
        window.placeDice(0,0,new Dice(DiceColor.YELLOW,DiceShade.THREE),true,true);
        when(ToolCardTestUtilities.player.getWindow()).thenReturn(window);

        demoCard.activateCard(player);

        //desired stock dice
        demoCard.playerCommand("1");


        //asserting ToolCard execution aborted because the dice could not be placed and everything was the same as before
        Assert.assertEquals(ToolCardState.ABORTED,demoCard.getState());
        Assert.assertEquals(2,demoGameBoard.getStock().getDice().size());
        Assert.assertEquals(new Dice(DiceColor.GREEN,DiceShade.THREE),demoGameBoard.getStock().getDice().get(0));
        Assert.assertEquals(new Dice(DiceColor.YELLOW,DiceShade.TWO),demoGameBoard.getStock().getDice().get(1));

    }


    @Test
    public void testWrongInputPickStockDie() throws CardNotUsableException {
        for(int i = 0; i < 2; i++) {

            demoCard.activateCard(player);


            if(i == 0) {
                demoCard.playerCommand("10");
            }else{
                demoCard.playerCommand("keep that in mind I have designed this rhyme");
            }

            //asserting ToolCard execution aborted because the dice could not be placed and everything was the same as before
            Assert.assertEquals(ToolCardState.DIESTOCKPICK, demoCard.getState());
            Assert.assertEquals(2, demoGameBoard.getStock().getDice().size());
            Assert.assertEquals(new Dice(DiceColor.GREEN,DiceShade.THREE),demoGameBoard.getStock().getDice().get(0));
            Assert.assertEquals(new Dice(DiceColor.YELLOW,DiceShade.TWO),demoGameBoard.getStock().getDice().get(1));
        }
    }

    @Test
    public void testAbortPlaceDie()throws CardNotUsableException{
        System.out.println(wpc.toString());
        ToolCardTestUtilities.testAbortSecondInput(demoCard);

    }

    @Test
    public void testAbortStockDiePick()throws CardNotUsableException,NoDiceException{
        ToolCardTestUtilities.testAbortPickStockDie(demoCard);
    }


    @Test
    public void testWrongInputPlaceDie()throws CardNotUsableException{
        for(int i = 0; i < 2; i++) {
            setUp();
            demoCard.activateCard(player);

            demoCard.playerCommand("1");
            if(i == 0) {
                demoCard.playerCommand("10");
            }else{
                demoCard.playerCommand("to explain in due time, all I know");
            }

            //checking ToolCard remains in the same state because of wrong user input
            Assert.assertEquals(ToolCardState.PLACEDIE, demoCard.getState());
            Assert.assertEquals(1, demoGameBoard.getStock().getDice().size());
            Assert.assertEquals(new Dice(DiceColor.YELLOW, DiceShade.TWO), demoGameBoard.getStock().getDice().get(0));
        }
    }



    @Test
    public void testActivateCardSinglePlayer() throws CardNotUsableException{
        demoCard.activateCard(player,new Dice(DiceColor.GREEN,DiceShade.FOUR));
        Assert.assertEquals(ToolCardState.DIESTOCKPICK,demoCard.getState());




    }
}