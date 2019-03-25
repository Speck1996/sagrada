package com.model.cards.concretetoolcards;


import com.model.dice.*;
import com.model.cards.ToolCard;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;




public class CopperFoilBurnisherTest {
    private ToolCard demoCard;


    @Before
    public void setUp(){
        ToolCardTestUtilities.setUp();
        demoCard = new CopperFoilBurnisher("demo","demo","demo");
        demoCard.setGameBoard(ToolCardTestUtilities.demoGameBoard);

    }

    @Rule
    public ExpectedException exception = ExpectedException.none();



   @Test
   public void testPlayerCommand(){

       try {
            demoCard.activateCard(ToolCardTestUtilities.player);
        }catch (CardNotUsableException error){
            System.out.println("problems");
        }
        demoCard.playerCommand("1,1");
        demoCard.playerCommand("2,3");

        //checking that the dice was correctly placed in the desired position
        Assert.assertEquals(new Dice(DiceColor.YELLOW, DiceShade.ONE), ToolCardTestUtilities.wpc.getSpaces()[1][2].getDice());

        //checking that the previous space is now empty
        Assert.assertNull(ToolCardTestUtilities.wpc.getSpaces()[0][0].getDice());
        System.out.println(ToolCardTestUtilities.wpc.toString());


   }

    @Test
    public void testAbortWindowDie() throws CardNotUsableException{


        demoCard.activateCard(ToolCardTestUtilities.player);
        demoCard.playerCommand("a");
        assertEquals(ToolCardState.ABORTED,demoCard.getState());

    }

    @Test
    public void testAbortPlaceDie() throws CardNotUsableException{
        ToolCardTestUtilities.testAbortPlaceDie(demoCard);
    }


    @Test
    public void testWrongIndexPickWindowDie() throws CardNotUsableException{
        ToolCardTestUtilities.testWrongIndexPickWindowDie(demoCard);
    }

    @Test
    public void testWrongIndexPlacePickedDie() throws CardNotUsableException{

        demoCard.activateCard(ToolCardTestUtilities.player);
        demoCard.playerCommand("1,1");
        demoCard.playerCommand("10");
        assertNull(ToolCardTestUtilities.wpc.getSpaces()[0][0].getDice());
        assertEquals(ToolCardTestUtilities.wpc.getSpaces()[1][1].getDice(), new Dice(DiceColor.RED,DiceShade.FIVE));
        assertEquals(ToolCardState.PLACEDIE,demoCard.getState());

    }

    @Test
    public void testPayCardSinglePlayer()throws CardNotUsableException{
        demoCard.activateCard(ToolCardTestUtilities.player, new Dice(DiceColor.RED,DiceShade.THREE));
        assertEquals(ToolCardState.PICKWINDOWDIE,demoCard.getState());
    }




    //testing ToolCard is not usable if it is not the first turn
    @Test
    public void testNoMovableDiceException() throws CardNotUsableException{
        exception.expect(CardNotUsableException.class);
        exception.expectMessage("No movable die, aborting...");
        ToolCardTestUtilities.testNoMovableDiceException(demoCard);
    }

}
