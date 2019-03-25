package com.model.cards;

import com.model.Player;
import com.model.PlayerInGame;
import com.model.cards.concretetoolcards.CardNotUsableException;
import com.model.cards.concretetoolcards.CopperFoilBurnisher;
import com.model.dice.*;
import com.model.gameboard.GameBoard;
import com.model.gameboard.RoundBoard;
import com.model.patterns.WindowPatternCard;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class ToolCardTest {
    PlayerInGame player;
    GameBoard mockedGameBoard;
    DiceBag mockedBag;
    RoundBoard mockedRd;
    Stock mockedStock;
    ToolCard demoCard;

    @Before
    public void setUp(){
        player = spy(new PlayerInGame(new Player("demo"),null));

        doNothing().when(player).notifyViewObserver(any(String.class));


        mockedGameBoard = mock(GameBoard.class);
        mockedBag = mock(DiceBag.class);
        mockedStock = mock(Stock.class);
        mockedRd = mock(RoundBoard.class);
        when(mockedGameBoard.getGameRoundBoard()).thenReturn(mockedRd);
        when(mockedGameBoard.getBag()).thenReturn(mockedBag);
        when(mockedGameBoard.getStock()).thenReturn(mockedStock);

        player.setWindow(new WindowPatternCard("0:Kaleidoscopie Dream:5:YB**1G*5*43*R*G2**BY"));

        demoCard = new CopperFoilBurnisher("demo","demo","demo");
        demoCard.setGameBoard(mockedGameBoard);


    }

   @Test
    public void testPayCard() throws CardNotUsableException {

        demoCard.payCard(player);

        //check card is payed
        assertEquals(4,player.getFavorTokens());
        assertEquals(1,demoCard.getTokensOncard());

       demoCard.payCard(player);
       assertEquals(2,player.getFavorTokens());
       assertEquals(3,demoCard.getTokensOncard());

       assertEquals(player,demoCard.toolCardUser);

       try{
           demoCard.payCard(player);
       }catch (CardNotUsableException e){
           assertEquals("User doesn't have enough tokens", e.getMessage());
       }

   }


    @Rule
    public ExpectedException exception = ExpectedException.none();

   @Test
    public void testCheckDiePlaced() throws CardNotUsableException{
       player.setDicePlayed();

       exception.expect(CardNotUsableException.class);
       exception.expectMessage("Die already placed, you can't play this card, you won't lose your tokens");

       //player has already placed a die and can't use toolcard
       demoCard.checkDiePlaced(player);
   }

   @Test
    public void testSingPlayerPayCard(){
        try{
            demoCard.payCard(player,new Dice(DiceColor.GREEN,DiceShade.THREE));
        }catch (CardNotUsableException e){
            Assert.assertEquals("User selected a die with color not matching RED", e.getMessage());
        }

   }

   @Test
    public void testToString()throws CardNotUsableException{
       String expectedString = "I'm Tool Card demo demo demo DiceSpace Color: RED\tMy cost is 1 favor token";
       Assert.assertEquals(expectedString,demoCard.toString());

       demoCard.payCard(player);

       expectedString = "I'm Tool Card demo demo demo DiceSpace Color: RED\tMy cost is 2 favor tokens";
       Assert.assertEquals(expectedString,demoCard.toString());
    }

}