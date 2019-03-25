package com.model.cards.objcard;

import com.model.cards.ObjCard;
import com.model.dice.Dice;
import com.model.dice.DiceColor;
import com.model.dice.DiceShade;
import com.model.patterns.WindowPatternCard;
import com.model.patterns.WindowSyntaxException;
import org.junit.Test;

import static org.junit.Assert.*;

public class ColumnShadeVarietyTest {

    @Test
    public void computeScoreTest() throws WindowSyntaxException {
        ObjCard card = new ColumnShadeVariety("COF40", "Column Shade Variety", "Columns with no repeated values", 4);

        WindowPatternCard wpc = new WindowPatternCard("17:Bellesguard:3:B6**Y*3B***562**4*1G");

        wpc.getSpaces()[0][0].setDice(new Dice(DiceColor.BLUE, DiceShade.ONE));
        wpc.getSpaces()[1][0].setDice(new Dice(DiceColor.RED, DiceShade.FOUR));
        wpc.getSpaces()[2][0].setDice(new Dice(DiceColor.BLUE, DiceShade.ONE));

        wpc.getSpaces()[1][2].setDice(new Dice(DiceColor.BLUE, DiceShade.ONE));

        //matching
        wpc.getSpaces()[0][1].setDice(new Dice(DiceColor.RED, DiceShade.SIX));
        wpc.getSpaces()[1][1].setDice(new Dice(DiceColor.YELLOW, DiceShade.THREE));
        wpc.getSpaces()[2][1].setDice(new Dice(DiceColor.PURPLE, DiceShade.FIVE));
        wpc.getSpaces()[3][1].setDice(new Dice(DiceColor.BLUE, DiceShade.FOUR));

        //not matching
        wpc.getSpaces()[0][4].setDice(new Dice(DiceColor.YELLOW, DiceShade.ONE));
        wpc.getSpaces()[1][4].setDice(new Dice(DiceColor.RED, DiceShade.TWO));
        wpc.getSpaces()[3][4].setDice(new Dice(DiceColor.GREEN, DiceShade.THREE));

        assertEquals(4, card.computeScore(wpc.getSpaces()));
    }
}