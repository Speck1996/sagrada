package com.model.boards;

import com.model.dice.Dice;
import com.model.dice.DiceColor;
import com.model.dice.DiceShade;
import com.model.gameboard.RoundBoard;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class RoundBoardTest {
    RoundBoard board = new RoundBoard();
    ArrayList<Dice> dices = new ArrayList<>();
    Dice dice1 = new Dice(DiceColor.BLUE, DiceShade.FIVE);
    Dice dice2 = new Dice(DiceColor.YELLOW,DiceShade.THREE);
    Dice dice3 = new Dice(DiceColor.PURPLE,DiceShade.SIX);

    @Test
    public void testGetAndNextRound(){
        board.nextRound();
        Assert.assertEquals(board.getRound(),1);
    }

    @Test
    public void testGetAndInsertDices(){
        dices.add(dice1);
        board.insertDices(dices);
        Assert.assertEquals(board.getDice(0).get(0),dice1);
        Assert.assertEquals(board.getDice(0).size(),1);
        board.nextRound();
        dices.set(0,dice2);
        dices.add(dice3);
        board.insertDices(dices);
        RoundBoard roundBoard2 = new RoundBoard(board);

        Assert.assertEquals(board.getRound(),1);
        Assert.assertEquals(board.getDice(1).size(),2);
        Assert.assertEquals(board.getDice(1).get(0),dice2);
        Assert.assertEquals(board.getDice(1).get(1),dice3);
    }

    @Test
    public void testChangeDice(){
        dices.add(dice1);
        dices.add(dice2);
        board.insertDices(dices);
        Dice newdice = board.changeDice(0,0,dice3);
        ArrayList<Dice> newlist = new ArrayList<>();
        newlist.add(dice3);
        newlist.add(dice2);
        Assert.assertEquals(newdice,dice1);
        Assert.assertEquals(board.getDice(0),newlist);
    }
}
