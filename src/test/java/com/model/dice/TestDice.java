package com.model.dice;

import org.junit.Assert;
import org.junit.Test;

public class TestDice
{
    @Test
    public void testGetter() {
        Dice dice = new Dice(DiceColor.BLUE,DiceShade.FIVE);
        //test getColor
        Assert.assertEquals(DiceColor.BLUE,dice.getColor());
        //test getShade
        Assert.assertEquals(DiceShade.FIVE,dice.getShade());
    }

    @Test
    public void testSetShade() {
        Dice dice = new Dice(DiceColor.BLUE,DiceShade.FIVE);

        //passed NEUTRAL to setShade, it returns FALSE
        Assert.assertFalse(dice.setShade(DiceShade.NEUTRAL));
        //passed a different value from NEUTRAL to setShade, it returns TRUE
        Assert.assertTrue(dice.setShade(DiceShade.ONE));
        //check if the parameter passed to setShade is correctly stored in dice
        dice.setShade(DiceShade.FOUR);
        Assert.assertEquals(DiceShade.FOUR,dice.getShade());
    }

    @Test
    public void testRollDice() {
        Dice dice = new Dice(DiceColor.BLUE,DiceShade.FIVE);
        dice.rollDice();
        if (dice.getShade()==DiceShade.ONE || dice.getShade()==DiceShade.TWO ||
                dice.getShade()==DiceShade.THREE || dice.getShade()==DiceShade.FOUR ||
                    dice.getShade()==DiceShade.FIVE || dice.getShade()==DiceShade.SIX){Assert.assertTrue(true);}
                    Assert.assertNotEquals(dice.getShade(),DiceShade.NEUTRAL);
    }

    @Test
    public void testNextValue() throws MinMaxReachedException{
        Dice dice = new Dice(DiceColor.BLUE,DiceShade.FIVE);
        dice.nextValue();
        Assert.assertEquals(DiceShade.SIX,dice.getShade());
    }

    @Test
    public void testPreviousValue() throws MinMaxReachedException{
        Dice dice = new Dice(DiceColor.BLUE,DiceShade.FIVE);
        dice.previousValue();
        Assert.assertEquals(DiceShade.FOUR,dice.getShade());
    }

    @Test
    public void testFlipDice(){
        Dice dice0 = new Dice(DiceColor.BLUE,DiceShade.ONE);
        Dice dice1 = new Dice(DiceColor.BLUE,DiceShade.TWO);
        Dice dice2 = new Dice(DiceColor.BLUE,DiceShade.THREE);
        Dice dice3 = new Dice(DiceColor.BLUE,DiceShade.FOUR);
        Dice dice4 = new Dice(DiceColor.BLUE,DiceShade.FIVE);
        Dice dice5 = new Dice(DiceColor.BLUE,DiceShade.SIX);
        dice0.flipDice();
        dice1.flipDice();
        dice2.flipDice();
        dice3.flipDice();
        dice4.flipDice();
        dice5.flipDice();
        Assert.assertEquals(dice0.getShade(),DiceShade.SIX);
        Assert.assertEquals(dice1.getShade(),DiceShade.FIVE);
        Assert.assertEquals(dice2.getShade(),DiceShade.FOUR);
        Assert.assertEquals(dice3.getShade(),DiceShade.THREE);
        Assert.assertEquals(dice4.getShade(),DiceShade.TWO);
        Assert.assertEquals(dice5.getShade(),DiceShade.ONE);
    }
}
