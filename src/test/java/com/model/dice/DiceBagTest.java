package com.model.dice;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class DiceBagTest {
    @Test
    public void testGetter(){
        ArrayList<Dice> list = new ArrayList<>();
        DiceBag dices = new DiceBag(list);
        Dice dice1 = new Dice(DiceColor.BLUE, DiceShade.FIVE);
        Dice dice2 = new Dice(DiceColor.YELLOW, DiceShade.ONE);
        Dice dice3 = new Dice(DiceColor.RED, DiceShade.SIX);
        list.add(dice1);
        list.add(dice2);
        list.add(dice3);
        Assert.assertEquals(dices.getDices(),list);
    }

    @Test
    public void testDrawDice() throws NoDiceException{
        ArrayList<Dice> list = new ArrayList<>();
        DiceBag dices = new DiceBag(list);
        Dice dice1 = new Dice(DiceColor.BLUE, DiceShade.FIVE);
        Dice dice2 = new Dice(DiceColor.YELLOW, DiceShade.ONE);
        Dice dice3 = new Dice(DiceColor.RED, DiceShade.SIX);
        list.add(dice1);
        list.add(dice2);
        list.add(dice3);
        Dice drawn = dices.drawDice();
        if (drawn.getColor()==DiceColor.BLUE){Assert.assertEquals(drawn,dice1);}
        if (drawn.getColor()==DiceColor.YELLOW){Assert.assertEquals(drawn,dice2);}
        if (drawn.getColor()==DiceColor.RED){Assert.assertEquals(drawn,dice3);}
    }

    @Test
    public void testInsertDice(){
        ArrayList<Dice> list = new ArrayList<>();
        DiceBag dices = new DiceBag(list);
        Dice dice1 = new Dice(DiceColor.BLUE, DiceShade.FIVE);
        dices.insertDice(dice1);
        Assert.assertEquals(dice1,dices.getDices().get(0));
    }
}
