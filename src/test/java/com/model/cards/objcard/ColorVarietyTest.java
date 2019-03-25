package com.model.cards.objcard;

import com.model.cards.ObjCard;
import com.model.dice.Dice;
import com.model.dice.DiceColor;
import com.model.dice.DiceShade;
import com.model.patterns.WindowPatternCard;
import com.model.patterns.WindowSyntaxException;
import org.junit.Test;

import static org.junit.Assert.*;

public class ColorVarietyTest {

    @Test
    public void computeScoreTest() throws WindowSyntaxException {
        ObjCard card = new ColorVariety("COF41", "Color Variety", "Sets of one of each color anywhere", 4);

        WindowPatternCard wpc = new WindowPatternCard("17:Bellesguard:3:B6**Y*3B***562**4*1G");

        wpc.getSpaces()[0][0].setDice(new Dice(DiceColor.BLUE, DiceShade.ONE));
        wpc.getSpaces()[1][2].setDice(new Dice(DiceColor.BLUE, DiceShade.ONE));
        wpc.getSpaces()[0][4].setDice(new Dice(DiceColor.YELLOW, DiceShade.ONE));
        wpc.getSpaces()[3][4].setDice(new Dice(DiceColor.GREEN, DiceShade.ONE));

        wpc.getSpaces()[0][1].setDice(new Dice(DiceColor.RED, DiceShade.SIX));
        wpc.getSpaces()[1][1].setDice(new Dice(DiceColor.GREEN, DiceShade.THREE));
        wpc.getSpaces()[1][0].setDice(new Dice(DiceColor.PURPLE, DiceShade.SIX));

        wpc.getSpaces()[3][0].setDice(new Dice(DiceColor.PURPLE, DiceShade.ONE));
        wpc.getSpaces()[3][1].setDice(new Dice(DiceColor.RED, DiceShade.FOUR));
        wpc.getSpaces()[3][2].setDice(new Dice(DiceColor.YELLOW, DiceShade.ONE));

        wpc.getSpaces()[1][4].setDice(new Dice(DiceColor.GREEN, DiceShade.TWO));
        wpc.getSpaces()[2][0].setDice(new Dice(DiceColor.GREEN, DiceShade.TWO));
        wpc.getSpaces()[2][4].setDice(new Dice(DiceColor.RED, DiceShade.THREE));

        assertEquals(8, card.computeScore(wpc.getSpaces()));

    }
}