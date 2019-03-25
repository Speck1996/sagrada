package com.model.cards.concretetoolcards;

import com.model.MoveAbortedException;
import com.model.WrongInputSyntaxException;
import com.model.dice.*;
import com.model.gameboard.RoundBoard;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ToolCardInputHandlerTest {

    @Test
    public void getCoordinates() {
        ToolCardInputHandler test = new ToolCardInputHandler();
        try {
            test.getCoordinates("10,10");
        }catch (WrongInputSyntaxException error){
            Assert.assertEquals("10,10 not accepted, wrong syntax input",error.getMessage());
        }catch (MoveAbortedException error){
            System.out.println("not supposed to go here");
        }
        try {
            test.getCoordinates("b,b");
        }catch (WrongInputSyntaxException error){
            Assert.assertEquals("b,b not accepted, wrong syntax input",error.getMessage());
        }catch (MoveAbortedException error){
            System.out.println("not supposed to go here");
        }
        try{
            test.getCoordinates("a");
        }catch (MoveAbortedException error){
            Assert.assertEquals("Space coordinate selection aborted", error.getMessage());
        }catch (Exception e){
            System.out.println("error");
        }

        try {
            test.getCoordinates("7,4");
        }catch (WrongInputSyntaxException error){
            Assert.assertEquals("7,4 not accepted: selected space doesn't exist",error.getMessage());
        }catch (MoveAbortedException error){
            System.out.println("not supposed to go here");
        }
    }


    @Test
    public void selectRoundCoordinates() {
        RoundBoard roundBoard = new RoundBoard();
        ToolCardInputHandler parser = new ToolCardInputHandler();
        try {
            parser.selectRoundCoordinates(roundBoard,"10.10");
        }catch(WrongInputSyntaxException error){
            Assert.assertEquals("10.10 not accepted, wrong syntax input",error.getMessage());
        }catch (MoveAbortedException e){
            System.out.println("error");
        }
        try {
            parser.selectRoundCoordinates(roundBoard,"1,2");
        }catch(WrongInputSyntaxException error){
            Assert.assertEquals("1,2 not accepted: wrong round index",error.getMessage());
        }catch (MoveAbortedException e){
            System.out.println("error");
        }
        List<Dice> dice = new ArrayList<>();
        dice.add(new Dice(DiceColor.GREEN,DiceShade.SIX));
        roundBoard.nextRound();

        roundBoard.insertDices(dice);
        try {
            parser.selectRoundCoordinates(roundBoard,"1,2");
        }catch(WrongInputSyntaxException error){
            Assert.assertEquals("1,2 not accepted: wrong die index",error.getMessage());
        }catch (MoveAbortedException e){
            System.out.println("error");
        }

    }

    @Test
    public void pickStockDie() {
        ToolCardInputHandler parser = new ToolCardInputHandler();
        try {
            parser.pickStockDie(new Stock(), "1");
        }catch (NoDiceException error){
            Assert.assertEquals("10,10 not accepted, wrong syntax input",error.getMessage());
        }catch (Exception error){
            System.out.println("error");
        }

    }


}