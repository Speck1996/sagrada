package com.model.cards.concretetoolcards;

import com.model.PlayerInGame;
import com.model.cards.ToolCard;
import com.model.dice.*;
import com.model.gameboard.GameBoard;

import com.model.gameboard.RoundBoard;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import java.util.List;

import static com.model.cards.concretetoolcards.ToolCardTestUtilities.wpc;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GlazingHammerTest {
    private ToolCard demoCard;
    private PlayerInGame mockedPlayer;
    private GameBoard mockedGameBoard;

    //setting up the ToolCard execution context
    @Before
    public void setUp(){
        ToolCardTestUtilities.setUp();
        demoCard = new GlazingHammer("demo","demo","demo");
        mockedGameBoard = mock(GameBoard.class);
        DiceBag mockedBag = mock(DiceBag.class);
        RoundBoard mockedRd= mock(RoundBoard.class);
        mockedPlayer = mock(PlayerInGame.class);
        when(mockedPlayer.hasPlayedDice()).thenReturn(false);
        when(mockedGameBoard.isAscendant()).thenReturn(false);

        Stock stock = new Stock();
        when(mockedPlayer.getWindow()).thenReturn(wpc);
        when(mockedPlayer.getFavorTokens()).thenReturn(5);

        stock.insertDice(new Dice(DiceColor.BLUE,DiceShade.TWO));
        stock.insertDice(new Dice(DiceColor.PURPLE,DiceShade.FOUR));
        when(mockedGameBoard.getStock()).thenReturn(stock);
        when(mockedGameBoard.getBag()).thenReturn(mockedBag);
        when(mockedGameBoard.getGameRoundBoard()).thenReturn(mockedRd);
        demoCard.setGameBoard(mockedGameBoard);
        when(mockedGameBoard.getCurrentPlayer()).thenReturn(mockedPlayer);

    }


    //tests card activation+execution (glazing hammer is one of these cards that execute directly after the activation
    //without human input/interaction
    @Test
    public void activateCardTest() {
        try {
            //saving old dice in this list
            List<Dice> diceBeforeCard = mockedGameBoard.getStock().getDice();
            demoCard.activateCard(mockedPlayer);


            //assert stock size doesn't change
            assertEquals(2,mockedGameBoard.getStock().getDice().size());

            //assert the new value of each dice is between 1 and 6,color remains the same, new state is executed
            for(int i = 0; i < mockedGameBoard.getStock().getDice().size(); i++){
                Dice dice = mockedGameBoard.getStock().getDice().get(i);
                assertTrue(dice.getShade().ordinal()>= 1 && dice.getShade().ordinal()<=6);
                Dice oldDice = diceBeforeCard.get(i);
                assertSame(dice.getColor(), oldDice.getColor());
                assertEquals(ToolCardState.EXECUTED, demoCard.getState());
            }

            System.out.println(mockedGameBoard.getStock().toString());
        }catch (CardNotUsableException error){
            System.out.println(error.getMessage());
        }

    }


    @Rule
    public ExpectedException exception = ExpectedException.none();


    //testing ToolCard is not usable if it is not the second turn
    @Test
    public void testNotSecondTurnException() throws CardNotUsableException{
        when(mockedGameBoard.isAscendant()).thenReturn(true);
        exception.expect(CardNotUsableException.class);
        exception.expectMessage("This is not your second turn, you can't use this card");
        demoCard.activateCard(mockedPlayer);
        assertEquals(ToolCardState.NEUTRAL,demoCard.getState());
    }

    //testing ToolCard is not usable if player has already played his dice
    @Test
    public void testPlayerDicePlayedException() throws CardNotUsableException{
        when(mockedPlayer.hasPlayedDice()).thenReturn(true);
        exception.expect(CardNotUsableException.class);
        exception.expectMessage("You can use this card only before placing your die" );
        demoCard.activateCard(mockedPlayer);
        assertEquals(ToolCardState.NEUTRAL,demoCard.getState());


    }



    @Test
    public void activateCardSinglePlayer() throws CardNotUsableException{
        demoCard.activateCard(mockedPlayer,new Dice(DiceColor.BLUE,DiceShade.FOUR));
        Assert.assertEquals(ToolCardState.EXECUTED,demoCard.getState());
    }


    @Test
    public void testNotSecondTurnExceptionSinglePlayer()throws CardNotUsableException{
        when(mockedGameBoard.isAscendant()).thenReturn(true);
        exception.expect(CardNotUsableException.class);
        exception.expectMessage("This is not your second turn, you can't use this card" );
        demoCard.activateCard(mockedPlayer, new Dice(DiceColor.BLUE,DiceShade.FOUR));
        assertEquals(ToolCardState.NEUTRAL,demoCard.getState());

    }

    @Test
    public void testPlayerDicePlayedExceptionSinglePlayer() throws CardNotUsableException{
        when(mockedPlayer.hasPlayedDice()).thenReturn(true);
        exception.expect(CardNotUsableException.class);
        exception.expectMessage("You can use this card only before placing your die" );
        demoCard.activateCard(mockedPlayer, new Dice(DiceColor.BLUE,DiceShade.THREE));
        assertEquals(ToolCardState.NEUTRAL,demoCard.getState());


    }
}