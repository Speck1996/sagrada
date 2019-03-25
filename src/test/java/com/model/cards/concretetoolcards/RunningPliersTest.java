package com.model.cards.concretetoolcards;

import com.model.Player;
import com.model.PlayerInGame;
import com.model.cards.ToolCard;
import com.model.dice.Dice;
import com.model.dice.DiceColor;
import com.model.dice.DiceShade;
import com.model.gameboard.GameBoard;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class RunningPliersTest {
    private ToolCard demoCard;
    private GameBoard mockedGameBoard;

    @Before
    public void setUp(){
        ToolCardTestUtilities.setUp();
        demoCard = spy(new RunningPliers("demo","demo","demo"));
        doNothing().when(demoCard).setCaches();
        mockedGameBoard = mock(GameBoard.class);
        when(mockedGameBoard.isAscendant()).thenReturn(true);

        PlayerInGame demoPlayer = spy(new PlayerInGame(new Player("hello"),null));
        doNothing().when(demoPlayer).notifyViewObserver(any(String.class));
        doReturn(5).when(demoPlayer).getFavorTokens();
        demoPlayer.setWindow(ToolCardTestUtilities.wpc);
        demoCard.setGameBoard(mockedGameBoard);
        when(mockedGameBoard.getCurrentPlayer()).thenReturn(demoPlayer);
    }


    //testing toolcard is executed (player skipturn == true) and toolcard state is executed
    @Test
    public void activateCard()  {
        try {
            demoCard.activateCard(mockedGameBoard.getCurrentPlayer());
            assertTrue(mockedGameBoard.getCurrentPlayer().getSkipTurn());
            assertEquals(ToolCardState.EXECUTED,demoCard.getState());

        }catch (CardNotUsableException error){
            System.out.println(error);
        }
    }

    @Rule
    public ExpectedException exception = ExpectedException.none();


    //testing ToolCard is not usable if it is not the first turn
    @Test
    public void testNotFirstTurnException() throws CardNotUsableException{
        when(mockedGameBoard.isAscendant()).thenReturn(false);
        exception.expect(CardNotUsableException.class);
        exception.expectMessage("You can't : can't use this card in your second turn");
        demoCard.activateCard(ToolCardTestUtilities.player);
        assertEquals(ToolCardState.NEUTRAL,demoCard.getState());

    }

    @Test
    public void activateCardSinglePlayer() throws CardNotUsableException{
        demoCard.activateCard(ToolCardTestUtilities.player,new Dice(DiceColor.RED,DiceShade.FOUR));
        assertEquals(ToolCardState.EXECUTED,demoCard.getState());
    }



    @Test
    public void testNotFirstTurnExceptionSinglePlayer()  throws CardNotUsableException{
        when(mockedGameBoard.isAscendant()).thenReturn(false);
        exception.expect(CardNotUsableException.class);
        exception.expectMessage("You can't : can't use this card in your second turn");
        demoCard.activateCard(ToolCardTestUtilities.player, new Dice(DiceColor.RED,DiceShade.FOUR));
        assertEquals(ToolCardState.NEUTRAL,demoCard.getState());
    }


}