package com.model.cards.concretetoolcards;

import com.model.dice.*;
import com.model.cards.ToolCard;
import com.model.gameboard.GameBoard;
import com.model.patterns.DieNotPlaceableException;
import com.model.patterns.WindowPatternCard;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static com.model.cards.concretetoolcards.ToolCardTestUtilities.demoGameBoard;
import static com.model.cards.concretetoolcards.ToolCardTestUtilities.player;
import static com.model.cards.concretetoolcards.ToolCardTestUtilities.wpc;
import static org.mockito.Mockito.when;



public class FluxBrushTest {
    private ToolCard demoCard;

    @Before
    public void setUp(){
        ToolCardTestUtilities.setUp();

        demoCard = new FluxBrush("demo","demo","demo");
        demoCard.setGameBoard(demoGameBoard);

    }



    @Test
    public void testPlayerCommand()throws CardNotUsableException,NoDiceException{



        demoCard.activateCard(ToolCardTestUtilities.player);

        demoCard.playerCommand("1");
        demoCard.playerCommand("1,4");

        //checking that the dice was correctly placed in the desired position, it's no more in the stock
        if(!(wpc.getSpaces()[0][3].getDice() == null)){
            Assert.assertTrue(DiceColor.GREEN.equals(player.getWindow().getSpaces()[0][3].getDice().getColor()));
            Assert.assertEquals(ToolCardState.EXECUTED,demoCard.getState());

        }else{
            Assert.assertEquals(ToolCardState.PLACEDIE, demoCard.getState());
        }
        Assert.assertEquals(1,ToolCardTestUtilities.demoStock.getDice().size());
        Assert.assertEquals(new Dice(DiceColor.YELLOW,DiceShade.TWO),ToolCardTestUtilities.demoStock.getDice(0));

        System.out.println(ToolCardTestUtilities.wpc.toString());

    }

    @Test
    public void testAbortPickStockDie() throws CardNotUsableException,NoDiceException{
        ToolCardTestUtilities.testAbortPickStockDie(demoCard);
    }


    @Test
    public void testNotPlaceableDice()throws CardNotUsableException,DieNotPlaceableException,NoDiceException {
        WindowPatternCard window = new WindowPatternCard("0:TEST WINDOW:4:YBBBBBBBBBBBBBBBBBBB");
        window.placeDice(0, 0, new Dice(DiceColor.YELLOW, DiceShade.THREE), true, true);
        when(ToolCardTestUtilities.player.getWindow()).thenReturn(window);


        GameBoard demoGameBoard = new GameBoard(3, 3);
        demoGameBoard.setStock(ToolCardTestUtilities.demoStock);
        demoCard.setGameBoard(demoGameBoard);
        demoCard.activateCard(ToolCardTestUtilities.player);
        demoCard.playerCommand("1");


        //asserting the modified dice is put back in the stock
        Assert.assertEquals(2, demoGameBoard.getStock().getDice().size());
        Assert.assertEquals(DiceColor.GREEN, demoGameBoard.getStock().getDice(1).getColor());
        Assert.assertEquals(new Dice(DiceColor.YELLOW, DiceShade.TWO), demoGameBoard.getStock().getDice(0));

        //assert ToolCard state is executed
        Assert.assertEquals(ToolCardState.EXECUTED, demoCard.getState());


    }


    @Test
    public void testWrongInputPickStockDie() throws CardNotUsableException,NoDiceException{
        ToolCardTestUtilities.testWrongInputPickStockDie(demoCard);
    }



    @Test
    public void testAbortPlaceDie()throws CardNotUsableException{

        demoCard.activateCard(ToolCardTestUtilities.player);

        demoCard.playerCommand("1");
        demoCard.playerCommand("a");

        //assert stock state is back as before
        Assert.assertEquals(2,demoGameBoard.getStock().getDice().size());
        Assert.assertEquals(new Dice(DiceColor.GREEN,DiceShade.THREE),demoGameBoard.getStock().getDice().get(0));
        Assert.assertEquals(new Dice(DiceColor.YELLOW,DiceShade.TWO),demoGameBoard.getStock().getDice().get(1));

        //assert ToolCard state is aborted
        Assert.assertEquals(ToolCardState.ABORTED,demoCard.getState());

    }


    @Test
    public void testWrongInputPlaceDie()throws CardNotUsableException{

        demoCard.activateCard(ToolCardTestUtilities.player);

        demoCard.playerCommand("1");
        demoCard.playerCommand("10,11");

        //assert stock state is back as before
        Assert.assertEquals(1,demoGameBoard.getStock().getDice().size());
        Assert.assertEquals(new Dice(DiceColor.YELLOW,DiceShade.TWO),demoGameBoard.getStock().getDice().get(0));

        //assert ToolCard state is aborted
        Assert.assertEquals(ToolCardState.PLACEDIE,demoCard.getState());

    }

    @Test
    public void testActivateSinglePlayer()throws CardNotUsableException{

        demoCard.activateCard(ToolCardTestUtilities.player,new Dice(DiceColor.PURPLE,DiceShade.THREE));
        Assert.assertEquals(ToolCardState.DIESTOCKPICK,demoCard.getState());



    }

}