package com.model.dice;

import org.junit.Assert;
import org.junit.Test;

public class DiceShadeTest {
    @Test
    public void testGetter(){
        Dice dice = new Dice(DiceColor.BLUE, DiceShade.SIX);
        Assert.assertEquals(dice.getShade().getAbbreviation(),'6');
    }

    @Test
    public void testErrorIncreasingDecreasingShade(){
        DiceShade shade = DiceShade.SIX;
        try {
            shade.getNext();
        }catch (MinMaxReachedException e){
            Assert.assertEquals("You reached max possible value of shade",e.getMessage());
        }
        shade = DiceShade.ONE;
        try {
            shade.getPrevious();
        }catch (MinMaxReachedException e){
            Assert.assertEquals("You reached min possible value of shade",e.getMessage());
        }
    }
}
