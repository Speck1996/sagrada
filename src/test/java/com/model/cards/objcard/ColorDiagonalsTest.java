package com.model.cards.objcard;

import com.model.cards.ObjCard;
import com.model.dice.Dice;
import com.model.dice.DiceColor;
import com.model.dice.DiceShade;
import com.model.patterns.WindowPatternCard;
import com.model.patterns.WindowSyntaxException;
import org.junit.Test;

import static org.junit.Assert.*;

public class ColorDiagonalsTest {

    @Test
    public void computeScoreTest() throws WindowSyntaxException {

        ObjCard card = new ColorDiagonals("COF00", "Colore Diagonals", "count of diagonally adjacent same color dice", 1);

        WindowPatternCard wpc = new WindowPatternCard("1:Firmitas:5:P6**35P3***2P1**15P4");

        for(int i=0; i<4; i++)
            wpc.getSpaces()[i][i].setDice(new Dice(DiceColor.PURPLE, DiceShade.TWO));

        wpc.getSpaces()[0][2].setDice(new Dice(DiceColor.PURPLE, DiceShade.ONE));
        wpc.getSpaces()[1][3].setDice(new Dice(DiceColor.PURPLE, DiceShade.ONE));

        wpc.getSpaces()[0][3].setDice(new Dice(DiceColor.BLUE, DiceShade.SIX));
        wpc.getSpaces()[1][2].setDice(new Dice(DiceColor.BLUE, DiceShade.THREE));
        wpc.getSpaces()[2][3].setDice(new Dice(DiceColor.BLUE, DiceShade.ONE));
        wpc.getSpaces()[3][2].setDice(new Dice(DiceColor.BLUE, DiceShade.FIVE));

        //don't care
        wpc.getSpaces()[2][1].setDice(new Dice(DiceColor.YELLOW, DiceShade.SIX));
        wpc.getSpaces()[2][4].setDice(new Dice(DiceColor.RED, DiceShade.TWO));

        assertEquals(10, card.computeScore(wpc.getSpaces()));
    }
}