package com.model.cards.concretetoolcards;

import com.model.Player;
import com.model.PlayerInGame;
import com.model.cards.ToolCard;
import com.model.dice.*;
import com.model.gameboard.GameBoard;
import com.model.gameboard.RoundBoard;
import com.model.patterns.DieNotPlaceableException;
import com.model.patterns.WindowPatternCard;
import com.model.patterns.WindowSyntaxException;
import org.junit.Assert;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

public class ToolCardTestUtilities{

    protected static WindowPatternCard wpc;
    protected static DiceBag mockedBag;
    protected static Stock demoStock;
    protected static RoundBoard demoRd;
    protected static PlayerInGame player;
    protected static GameBoard demoGameBoard;

    public static void setUp(){
        try {
            wpc = new WindowPatternCard("0:Kaleidoscopie Dream:4:YB**1G*5*43*R*G2**BY");
        }catch(WindowSyntaxException error){
            System.out.println("Some problem with building your window");
        }
        System.out.println(wpc.toString());
        try {
            wpc.placeDice(0, 0, new Dice(DiceColor.YELLOW, DiceShade.ONE), true, true);
            wpc.placeDice(1,1, new Dice(DiceColor.RED,DiceShade.FIVE),true,true);
            wpc.placeDice(0,2,new Dice(DiceColor.RED,DiceShade.TWO),true,true);
        }catch(DieNotPlaceableException error){
            System.out.println("Can't put die here");
        }
        demoStock = new Stock();
        demoStock.insertDice(new Dice(DiceColor.GREEN,DiceShade.THREE));
        demoStock.insertDice(new Dice(DiceColor.YELLOW,DiceShade.TWO));

        demoGameBoard = new GameBoard(3,3);
        demoGameBoard.setStock(demoStock);
        mockedBag = mock(DiceBag.class);
        try {
            when(mockedBag.drawDice()).thenReturn(new Dice(DiceColor.PURPLE, DiceShade.TWO));
        }catch (NoDiceException error){
            System.out.println(error.getMessage());
        }


        demoGameBoard.setBag(mockedBag);

        demoRd = new RoundBoard();
        demoRd.nextRound();
        ArrayList<Dice> diceRoundOne = new ArrayList<>();
        diceRoundOne.add(new Dice(DiceColor.BLUE,DiceShade.THREE));
        diceRoundOne.add(new Dice(DiceColor.YELLOW,DiceShade.FOUR));
        demoRd.insertDices(diceRoundOne);
        demoRd.nextRound();
        ArrayList<Dice> diceRoundTwo = new ArrayList<>();
        diceRoundTwo.add(new Dice(DiceColor.PURPLE, DiceShade.ONE));
        diceRoundTwo.add(new Dice(DiceColor.RED,DiceShade.FOUR));
        demoRd.insertDices(diceRoundTwo);
        demoGameBoard.setRoundBoard(demoRd);

        player = spy(new PlayerInGame(new Player("demo"),null));
        player.setWindow(wpc);
        doNothing().when(player).notifyViewObserver(any(String.class));
    }



    public static void testNoMovableDiceException(ToolCard testedCard) throws CardNotUsableException{
        WindowPatternCard wpcNoDice = new WindowPatternCard("0:Kaleidoscopie Dream:4:YB**1G*5*43*R*G2**BY");
        when(ToolCardTestUtilities.player.getWindow()).thenReturn(wpcNoDice);
        testedCard.activateCard(ToolCardTestUtilities.player);
        assertEquals(ToolCardState.NEUTRAL,testedCard.getState());
    }

    public static void testWrongIndexPickWindowDie(ToolCard testedCard) throws CardNotUsableException{
        testedCard.activateCard(ToolCardTestUtilities.player);
        testedCard.playerCommand("10");
        assertEquals(ToolCardTestUtilities.wpc.getSpaces()[0][0].getDice(),new Dice(DiceColor.YELLOW,DiceShade.ONE));
        assertEquals(ToolCardTestUtilities.wpc.getSpaces()[1][1].getDice(), new Dice(DiceColor.RED,DiceShade.FIVE));
        assertEquals(ToolCardState.PICKWINDOWDIE,testedCard.getState());

    }

    public static void testAbortPlaceDie(ToolCard testedCard) throws CardNotUsableException{
        testedCard.activateCard(ToolCardTestUtilities.player);

        testedCard.playerCommand("1,1");
        testedCard.playerCommand("a");
        assertNull(ToolCardTestUtilities.wpc.getSpaces()[0][0].getDice());
        assertEquals(new Dice(DiceColor.RED,DiceShade.FIVE),ToolCardTestUtilities.wpc.getSpaces()[1][1].getDice());
        assertEquals(ToolCardState.ABORTED,testedCard.getState());
    }


    public static void testAbortPickStockDie(ToolCard demoCard) throws CardNotUsableException,NoDiceException{

        demoCard.activateCard(ToolCardTestUtilities.player);

        demoCard.playerCommand("a");

        //checking ToolCard is aborted, stock is the same as before
        Assert.assertEquals(2,ToolCardTestUtilities.demoStock.getDice().size());
        Assert.assertEquals(new Dice(DiceColor.GREEN,DiceShade.THREE),ToolCardTestUtilities.demoStock.getDice(0));
        Assert.assertEquals(new Dice(DiceColor.YELLOW,DiceShade.TWO),ToolCardTestUtilities.demoStock.getDice(1));
        Assert.assertEquals(ToolCardState.ABORTED,demoCard.getState());

    }


    public static void testWrongInputPickStockDie(ToolCard demoCard) throws CardNotUsableException,NoDiceException{

        demoCard.activateCard(ToolCardTestUtilities.player);
        demoCard.playerCommand("10");


        //asserting the stock is in the state before ToolCard Execution
        Assert.assertEquals(2,demoGameBoard.getStock().getDice().size());
        Assert.assertEquals(new Dice(DiceColor.GREEN,DiceShade.THREE),demoGameBoard.getStock().getDice(0));
        Assert.assertEquals(new Dice(DiceColor.YELLOW,DiceShade.TWO),demoGameBoard.getStock().getDice(1));

        //assert ToolCard state remains in the DieStockPick state
        Assert.assertEquals(ToolCardState.DIESTOCKPICK,demoCard.getState());

    }

    public static void testAbortSecondInput(ToolCard demoCard) throws CardNotUsableException{
        demoCard.activateCard(player);

        demoCard.playerCommand("1");
        demoCard.playerCommand("a");

        //checking place die was aborted
        Assert.assertEquals(ToolCardState.ABORTED,demoCard.getState());
        Assert.assertEquals(2, demoGameBoard.getStock().getDice().size());
        Assert.assertEquals(new Dice(DiceColor.GREEN,DiceShade.THREE),demoGameBoard.getStock().getDice().get(0));
        Assert.assertEquals(new Dice(DiceColor.YELLOW,DiceShade.TWO),demoGameBoard.getStock().getDice().get(1));
    }
}
