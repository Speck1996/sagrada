package com.model.cards.concretetoolcards;

import com.model.cards.ToolCard;
import com.model.dice.*;
import com.model.patterns.DieNotPlaceableException;
import com.model.patterns.WindowPatternCard;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static com.model.cards.concretetoolcards.ToolCardTestUtilities.demoGameBoard;
import static com.model.cards.concretetoolcards.ToolCardTestUtilities.demoStock;
import static com.model.cards.concretetoolcards.ToolCardTestUtilities.player;
import static org.mockito.Mockito.when;



public class FluxRemoverTest {
    private ToolCard demoCard;


    @Before
    public void setUp(){
        ToolCardTestUtilities.setUp();
        demoCard = new FluxRemover("demo","demo","demo");
        demoCard.setGameBoard(ToolCardTestUtilities.demoGameBoard);

    }



    @Test
    public void testPlayerCommand()throws CardNotUsableException{
        demoCard.activateCard(player);

        //desired stock dice
        demoCard.playerCommand("1");

        //desired dice shade
        demoCard.playerCommand("5");

        //desired space position where the dice will be put
        demoCard.playerCommand("1,4");

        //checking that the dice was correctly placed in the desired position with the desired value and it's not present in the stock anymore
        Assert.assertEquals(new Dice(DiceColor.PURPLE, DiceShade.FIVE), ToolCardTestUtilities.wpc.getSpaces()[0][3].getDice());
        Assert.assertEquals(ToolCardState.EXECUTED,demoCard.getState());
        Assert.assertEquals(1,demoStock.getDice().size());


    }

    @Test
    public void testWrongInputPickStockDie() throws CardNotUsableException,NoDiceException{
        ToolCardTestUtilities.testWrongInputPickStockDie(demoCard);

    }


    @Test
    public void testAbortPickStockDie() throws CardNotUsableException,NoDiceException{
        ToolCardTestUtilities.testAbortPickStockDie(demoCard);

    }


    @Test
    public void testAbortShadeSelection()throws CardNotUsableException,NoDiceException{
        demoCard.activateCard(player);

        //desired stock dice
        demoCard.playerCommand("1");

        //abort
        demoCard.playerCommand("a");



        //card aborted, state is the same as before
        Assert.assertEquals(2,ToolCardTestUtilities.demoGameBoard.getStock().getDice().size());
        Assert.assertEquals(new Dice(DiceColor.GREEN,DiceShade.THREE),ToolCardTestUtilities.demoGameBoard.getStock().getDice(0));
        Assert.assertEquals(new Dice(DiceColor.YELLOW,DiceShade.TWO),ToolCardTestUtilities.demoGameBoard.getStock().getDice(1));
        Assert.assertEquals(ToolCardState.ABORTED,demoCard.getState());


    }

    @Test
    public void testWrongInputShade() throws CardNotUsableException,NoDiceException{
        for(int i = 0; i < 2; i++) {
            ToolCardTestUtilities.setUp();
            demoCard.setGameBoard(ToolCardTestUtilities.demoGameBoard);

            demoCard.activateCard(player);

            //desired stock dice
            demoCard.playerCommand("1");

            if(i == 0) {
                //shade out of bound
                demoCard.playerCommand("10");
            }else{
                //wrong syntax
                demoCard.playerCommand("One thing I don't know why");
            }
            //card aborted, state is the same as before
            Assert.assertEquals(1, ToolCardTestUtilities.demoGameBoard.getStock().getDice().size());
            Assert.assertEquals(new Dice(DiceColor.YELLOW, DiceShade.TWO), ToolCardTestUtilities.demoGameBoard.getStock().getDice(0));
            Assert.assertEquals(ToolCardState.USERDEMAND, demoCard.getState());
        }
    }


    @Test
    public void testNoPlaceableStockPick()throws CardNotUsableException,DieNotPlaceableException,NoDiceException {
        WindowPatternCard window = new WindowPatternCard("0:TEST WINDOW:4:YBBBBBBBBBBBBBBBBBBB");
        window.placeDice(0,0,new Dice(DiceColor.YELLOW,DiceShade.THREE),true,true);
        when(ToolCardTestUtilities.player.getWindow()).thenReturn(window);

        demoCard.activateCard(player);

        //desired stock dice
        demoCard.playerCommand("1");


        Assert.assertEquals(2,ToolCardTestUtilities.demoGameBoard.getStock().getDice().size());
        Assert.assertEquals(new Dice(DiceColor.YELLOW,DiceShade.TWO),ToolCardTestUtilities.demoGameBoard.getStock().getDice(0));
        Assert.assertEquals(new Dice(DiceColor.PURPLE,DiceShade.TWO),ToolCardTestUtilities.demoGameBoard.getStock().getDice(1));

        Assert.assertEquals(ToolCardState.EXECUTED,demoCard.getState());
    }


    @Test
    public void testNoPlaceableShadeSet()throws CardNotUsableException,DieNotPlaceableException,NoDiceException {
        WindowPatternCard window = new WindowPatternCard("0:TEST WINDOW:4:Y2BBBBBBBBBBBBBBBBBB");
        window.placeDice(0,0,new Dice(DiceColor.YELLOW,DiceShade.THREE),true,true);
        when(ToolCardTestUtilities.player.getWindow()).thenReturn(window);

        demoCard.activateCard(player);

        //desired stock dice
        demoCard.playerCommand("1");

        demoCard.playerCommand("4");


        Assert.assertEquals(2,ToolCardTestUtilities.demoGameBoard.getStock().getDice().size());
        Assert.assertEquals(new Dice(DiceColor.YELLOW,DiceShade.TWO),ToolCardTestUtilities.demoGameBoard.getStock().getDice(0));
        Assert.assertEquals(new Dice(DiceColor.PURPLE,DiceShade.FOUR),ToolCardTestUtilities.demoGameBoard.getStock().getDice(1));

        Assert.assertEquals(ToolCardState.EXECUTED,demoCard.getState());
    }

    @Test
    public void testAbortPlaceDie() throws CardNotUsableException,NoDiceException{
        demoCard.activateCard(player);

        //desired stock dice
        demoCard.playerCommand("1");

        //desired dice shade
        demoCard.playerCommand("5");

        //desired space position where the dice will be put
        demoCard.playerCommand("a");

        //checking that the dice was correctly placed in the desired position with the desired value and it's not present in the stock anymore
        Assert.assertEquals(2,demoStock.getDice().size());
        Assert.assertEquals(new Dice(DiceColor.PURPLE,DiceShade.FIVE),ToolCardTestUtilities.demoGameBoard.getStock().getDice(1));
        Assert.assertEquals(new Dice(DiceColor.YELLOW,DiceShade.TWO),ToolCardTestUtilities.demoGameBoard.getStock().getDice(0));
        Assert.assertEquals(ToolCardState.EXECUTED,demoCard.getState());

    }

    @Test
    public void wrongInputPlaceDie()throws CardNotUsableException,NoDiceException{

        demoCard.activateCard(player);

        //desired stock dice
        demoCard.playerCommand("1");

        //desired dice shade
        demoCard.playerCommand("5");

        //desired space position where the dice will be put
        demoCard.playerCommand("It doesn't even matter how hard you try");

        //checking that the dice was correctly placed in the desired position with the desired value and it's not present in the stock anymore
        Assert.assertEquals(1,demoStock.getDice().size());
        Assert.assertEquals(new Dice(DiceColor.YELLOW,DiceShade.TWO),ToolCardTestUtilities.demoGameBoard.getStock().getDice(0));
        Assert.assertEquals(ToolCardState.PLACEDIE,demoCard.getState());
    }



    @Test
    public void activateCardSinglePlayer() throws CardNotUsableException{
        demoCard.activateCard(player,new Dice(DiceColor.PURPLE,DiceShade.TWO));
        Assert.assertEquals(ToolCardState.DIESTOCKPICK,demoCard.getState());

    }


}