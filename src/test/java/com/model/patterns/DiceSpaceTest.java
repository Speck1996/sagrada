package com.model.patterns;

import com.model.dice.Dice;
import com.model.dice.DiceColor;
import com.model.dice.DiceShade;
import org.junit.Test;

import static org.junit.Assert.*;

public class DiceSpaceTest {

    @Test
    public void setterGetterTest() {
        DiceSpace ds = new DiceSpace(DiceColor.YELLOW, DiceShade.TWO);
        ds.setDice(new Dice(DiceColor.YELLOW, DiceShade.TWO));

        assertEquals(DiceColor.YELLOW, ds.getColor());
        assertEquals(DiceShade.TWO, ds.getShade());
        assertEquals(new Dice(DiceColor.YELLOW, DiceShade.TWO), ds.getDice());
    }

}