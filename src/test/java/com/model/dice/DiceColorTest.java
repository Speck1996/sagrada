package com.model.dice;

import org.junit.Assert;
import org.junit.Test;

public class DiceColorTest {
    @Test
    public void testGetter(){
        Dice dice = new Dice(DiceColor.BLUE, DiceShade.FIVE);
        Assert.assertEquals(dice.getColor().getAbbreviation(),'B');
    }
}
