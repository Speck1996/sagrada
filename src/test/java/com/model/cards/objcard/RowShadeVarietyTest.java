package com.model.cards.objcard;

import com.model.cards.ObjCard;
import com.model.dice.Dice;
import com.model.dice.DiceColor;
import com.model.dice.DiceShade;
import com.model.patterns.WindowPatternCard;
import com.model.patterns.WindowSyntaxException;
import org.junit.Test;

import static org.junit.Assert.*;

public class RowShadeVarietyTest {

    @Test
    public void computeScoreTest() throws WindowSyntaxException {
        ObjCard card = new RowShadeVariety("COF51", "Row Shade Variety", "Rows with no repeated values", 5);

        WindowPatternCard wpc = new WindowPatternCard("17:Bellesguard:3:B6**Y*3B***562**4*1G");

        wpc.getSpaces()[0][0].setDice(new Dice(DiceColor.BLUE, DiceShade.ONE));
        wpc.getSpaces()[1][0].setDice(new Dice(DiceColor.RED, DiceShade.TWO));

        wpc.getSpaces()[3][0].setDice(new Dice(DiceColor.PURPLE, DiceShade.ONE));

        wpc.getSpaces()[0][1].setDice(new Dice(DiceColor.YELLOW, DiceShade.SIX));
        wpc.getSpaces()[1][1].setDice(new Dice(DiceColor.GREEN, DiceShade.ONE));
        wpc.getSpaces()[2][1].setDice(new Dice(DiceColor.RED, DiceShade.FIVE));
        wpc.getSpaces()[3][1].setDice(new Dice(DiceColor.PURPLE, DiceShade.FOUR));

        wpc.getSpaces()[0][2].setDice(new Dice(DiceColor.RED, DiceShade.ONE));
        wpc.getSpaces()[1][2].setDice(new Dice(DiceColor.BLUE, DiceShade.THREE));
        wpc.getSpaces()[2][2].setDice(new Dice(DiceColor.YELLOW, DiceShade.SIX));
        wpc.getSpaces()[3][2].setDice(new Dice(DiceColor.BLUE, DiceShade.TWO));


        wpc.getSpaces()[1][3].setDice(new Dice(DiceColor.PURPLE, DiceShade.FIVE));
        wpc.getSpaces()[2][3].setDice(new Dice(DiceColor.RED, DiceShade.FOUR));
        wpc.getSpaces()[3][3].setDice(new Dice(DiceColor.PURPLE, DiceShade.SIX));

        wpc.getSpaces()[0][4].setDice(new Dice(DiceColor.YELLOW, DiceShade.ONE));
        wpc.getSpaces()[1][4].setDice(new Dice(DiceColor.RED, DiceShade.SIX));
        wpc.getSpaces()[2][4].setDice(new Dice(DiceColor.PURPLE, DiceShade.THREE));
        wpc.getSpaces()[3][4].setDice(new Dice(DiceColor.GREEN, DiceShade.FIVE));

        //match row 1 and 3
        assertEquals(10, card.computeScore(wpc.getSpaces()));
    }
}