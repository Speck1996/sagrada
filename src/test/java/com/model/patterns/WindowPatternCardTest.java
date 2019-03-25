package com.model.patterns;

import com.model.dice.Dice;
import com.model.dice.DiceColor;
import com.model.dice.DiceShade;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class WindowPatternCardTest {

    @Test
    public void constructorTest() {
        WindowPatternCard wpc = null;

        try {
            wpc = new WindowPatternCard("0:Kaleidoscopie Dream:4:YB**1G*5*43*R*G2**BY");
        } catch (WindowSyntaxException e) {
            fail("unexpected exception");
        }

        assertEquals(0, wpc.getId());
        assertEquals("Kaleidoscopie Dream", wpc.getName());
        assertEquals(4, wpc.getDifficulty());
        assertTrue(wpc.isEmpty());


        assertNull(wpc.getPairedWindow());
        try {
            WindowPatternCard wpc2 = new WindowPatternCard("1:Firmitas:5:P6**35P3***2P1**15P4");
            wpc.setPairedWindow(wpc2);
            assertEquals(wpc2, wpc.getPairedWindow());
        } catch (WindowSyntaxException e) {
            fail("unexpected exception");
        }





        DiceSpace[][] spaces = {
                {new DiceSpace(DiceColor.YELLOW, DiceShade.NEUTRAL),
                    new DiceSpace(DiceColor.BLUE, DiceShade.NEUTRAL),
                    new DiceSpace(DiceColor.NEUTRAL, DiceShade.NEUTRAL),
                    new DiceSpace(DiceColor.NEUTRAL, DiceShade.NEUTRAL),
                    new DiceSpace(DiceColor.NEUTRAL, DiceShade.ONE)},
                {new DiceSpace(DiceColor.GREEN, DiceShade.NEUTRAL),
                        new DiceSpace(DiceColor.NEUTRAL, DiceShade.NEUTRAL),
                        new DiceSpace(DiceColor.NEUTRAL, DiceShade.FIVE),
                        new DiceSpace(DiceColor.NEUTRAL, DiceShade.NEUTRAL),
                        new DiceSpace(DiceColor.NEUTRAL, DiceShade.FOUR)},
                {new DiceSpace(DiceColor.NEUTRAL, DiceShade.THREE),
                        new DiceSpace(DiceColor.NEUTRAL, DiceShade.NEUTRAL),
                        new DiceSpace(DiceColor.RED, DiceShade.NEUTRAL),
                        new DiceSpace(DiceColor.NEUTRAL, DiceShade.NEUTRAL),
                        new DiceSpace(DiceColor.GREEN, DiceShade.NEUTRAL)},
                {new DiceSpace(DiceColor.NEUTRAL, DiceShade.TWO),
                        new DiceSpace(DiceColor.NEUTRAL, DiceShade.NEUTRAL),
                        new DiceSpace(DiceColor.NEUTRAL, DiceShade.NEUTRAL),
                        new DiceSpace(DiceColor.BLUE, DiceShade.NEUTRAL),
                        new DiceSpace(DiceColor.YELLOW, DiceShade.NEUTRAL)}
        };


        assertArrayEquals(spaces, wpc.getSpaces());

        assertEquals(0, wpc.getDiceOnBoard());

        String string = "0 - Kaleidoscopie Dream - Diff:4\n" +
                "Y0|--\tB0|--\tN0|--\tN0|--\tN1|--\t\n" +
                "G0|--\tN0|--\tN5|--\tN0|--\tN4|--\t\n" +
                "N3|--\tN0|--\tR0|--\tN0|--\tG0|--\t\n" +
                "N2|--\tN0|--\tN0|--\tB0|--\tY0|--\t\n";
        assertEquals(string, wpc.toString());



    }


    //Tests the exceptions
    @Test
    public void constructorTest2() {
        WindowPatternCard wpc = null;

        //wrong number of arguments
        try {
            wpc = new WindowPatternCard("0Kaleidoscopie Dream4:YB**1G*5*43*R*G2**BY");
            fail("It was expected a WindowSyntaxException");
        } catch (WindowSyntaxException e) {
            assertEquals("Found 2 arguments (4 required)", e.getMessage());
        }

        try {
            wpc = new WindowPatternCard("0c:Kaleidoscopie Dream:4:YB**1G*5*43*R*G2**BY");
            fail("It was expected a WindowSyntaxException");
        } catch (WindowSyntaxException e) {
            assertEquals("Wrong format for id", e.getMessage());
        }

        try {
            wpc = new WindowPatternCard("0:Kaleidoscopie Dream:4w:YB**1G*5*43*R*G2**BY");
            fail("It was expected a WindowSyntaxException");
        } catch (WindowSyntaxException e) {
            assertEquals("Wrong format for difficulty", e.getMessage());
        }

        try {
            wpc = new WindowPatternCard("0:Kaleidoscopie Dream:4:YB**1G*5*43*R*G2**BY*");
            fail("It was expected a WindowSyntaxException");
        } catch (WindowSyntaxException e) {
            assertEquals("Wrong number of DiceSpace (20 required)", e.getMessage());
        }

        try {
            wpc = new WindowPatternCard("0:Kaleidoscopie Dream:4:mB**1G*5*43*R*G2**BY");
            fail("It was expected a WindowSyntaxException");
        } catch (WindowSyntaxException e) {
            assertEquals("Illegal character for DiceSpace definition (found 'm')", e.getMessage());
        }
    }

    @Test
    public void placeDiceIsolatedTest() {

        WindowPatternCard wpc = null;

        try {
            wpc = new WindowPatternCard("0:Kaleidoscopie Dream:4:YB**1G*5*43*R*G2**BY");
        } catch (WindowSyntaxException e) {
            fail("unexpected exception");
        }

        wpc.getSpaces()[1][2].setDice(new Dice(DiceColor.YELLOW, DiceShade.FIVE));


        try {
            wpc.placeDiceIsolated(1, 2, new Dice(DiceColor.YELLOW, DiceShade.FIVE));
            fail("It was expected a DieNotPlaceableException");
        } catch (DieNotPlaceableException e) {
            assertEquals("Space already occupied", e.getMessage());
        }

        try {
            wpc.placeDiceIsolated(0, 0, new Dice(DiceColor.GREEN, DiceShade.ONE));
            fail("It was expected a DieNotPlaceableException");
        } catch (DieNotPlaceableException e) {
            assertEquals("Violates color restriction", e.getMessage());
        }

        try {
            wpc.placeDiceIsolated(0, 4, new Dice(DiceColor.BLUE, DiceShade.THREE));
            fail("It was expected a DieNotPlaceableException");
        } catch (DieNotPlaceableException e) {
            assertEquals("Violates shade restriction", e.getMessage());
        }


        try {
            wpc.placeDiceIsolated(1, 3, new Dice(DiceColor.BLUE, DiceShade.SIX));
            fail("It was expected a DieNotPlaceableException");
        } catch (DieNotPlaceableException e) {
            assertEquals("Violates adjacent restriction", e.getMessage());
        }

        try {
            wpc.placeDiceIsolated(1, 1, new Dice(DiceColor.GREEN, DiceShade.ONE));
            fail("It was expected a DieNotPlaceableException");
        } catch (DieNotPlaceableException e) {
            assertEquals("Violates adjacent restriction", e.getMessage());
        }

        try {
            wpc.placeDiceIsolated(0, 1, new Dice(DiceColor.BLUE, DiceShade.ONE));
            fail("It was expected a DieNotPlaceableException");
        } catch (DieNotPlaceableException e) {
            assertEquals("Violates adjacent restriction", e.getMessage());
        }

        try {
            wpc.placeDiceIsolated(0, 2, new Dice(DiceColor.PURPLE, DiceShade.FIVE));
            fail("It was expected a DieNotPlaceableException");
        } catch (DieNotPlaceableException e) {
            assertEquals("Violates adjacent restriction", e.getMessage());
        }

        try {
            wpc.placeDiceIsolated(0, 3, new Dice(DiceColor.RED, DiceShade.SIX));
            fail("It was expected a DieNotPlaceableException");
        } catch (DieNotPlaceableException e) {
            assertEquals("Violates adjacent restriction", e.getMessage());
        }

        try {
            wpc.placeDiceIsolated(2, 1, new Dice(DiceColor.RED, DiceShade.SIX));
            fail("It was expected a DieNotPlaceableException");
        } catch (DieNotPlaceableException e) {
            assertEquals("Violates adjacent restriction", e.getMessage());
        }

        try {
            wpc.placeDiceIsolated(2, 2, new Dice(DiceColor.RED, DiceShade.ONE));
            fail("It was expected a DieNotPlaceableException");
        } catch (DieNotPlaceableException e) {
            assertEquals("Violates adjacent restriction", e.getMessage());
        }

        try {
            wpc.placeDiceIsolated(2, 3, new Dice(DiceColor.PURPLE, DiceShade.TWO));
            fail("It was expected a DieNotPlaceableException");
        } catch (DieNotPlaceableException e) {
            assertEquals("Violates adjacent restriction", e.getMessage());
        }


        try {
            Dice dice = new Dice(DiceColor.YELLOW, DiceShade.SIX);
            wpc.placeDiceIsolated(3, 4, dice);
            assertEquals(wpc.getSpaces()[3][4].getDice(), dice);

        } catch (DieNotPlaceableException e) {
            fail("unexpected exception");
        }

    }

    @Test
    public void placeDiceTest() {
        WindowPatternCard wpc = null;

        try {
            wpc = new WindowPatternCard("0:Kaleidoscopie Dream:4:YB**1G*5*43*R*G2**BY");
        } catch (WindowSyntaxException e) {
            fail("unexpected exception");
        }


        try {
            wpc.placeDice(8,1, new Dice(DiceColor.YELLOW, DiceShade.FOUR), true, true);
            fail("It was expected a DieNotPlaceableException");
        } catch (DieNotPlaceableException e) {
            assertEquals("Wrong coordinates", e.getMessage());
        }

        try {
            wpc.placeDice(1,1, new Dice(DiceColor.YELLOW, DiceShade.SIX), true, true);
            fail("It was expected a DieNotPlaceableException");
        } catch (DieNotPlaceableException e) {
            assertEquals("First dice must be on the edge", e.getMessage());
        }

        try {
            Dice dice = new Dice(DiceColor.PURPLE, DiceShade.ONE);
            wpc.placeDice(0,2, dice, true, true);
            assertEquals(wpc.getSpaces()[0][2].getDice(), dice);
        } catch (DieNotPlaceableException e) {
            fail("unexpected exception");
        }

        try {
            wpc.placeDice(0,2, new Dice(DiceColor.BLUE, DiceShade.SIX), true, true);
            fail("It was expected a DieNotPlaceableException");
        } catch (DieNotPlaceableException e) {
            assertEquals("Space already occupied", e.getMessage());
        }

        try {
            wpc.placeDice(0,1, new Dice(DiceColor.RED, DiceShade.TWO), true, true);
            fail("It was expected a DieNotPlaceableException");
        } catch (DieNotPlaceableException e) {
            assertEquals("Violates color constraint", e.getMessage());
        }

        try {
            wpc.placeDice(1,2, new Dice(DiceColor.BLUE, DiceShade.ONE), true, true);
            fail("It was expected a DieNotPlaceableException");
        } catch (DieNotPlaceableException e) {
            assertEquals("Violates shade constraint", e.getMessage());
        }

        try {
            wpc.placeDice(3,4, new Dice(DiceColor.YELLOW, DiceShade.FIVE), true, true);
            fail("It was expected a DieNotPlaceableException");
        } catch (DieNotPlaceableException e) {
            assertEquals("Must be adjacent to another dice", e.getMessage());
        }

        try {
            wpc.placeDice(3,4, new Dice(DiceColor.BLUE, DiceShade.FIVE), false, true);
            fail("It was expected a DieNotPlaceableException");
        } catch (DieNotPlaceableException e) {
            assertEquals("Must be adjacent to another dice", e.getMessage());
        }

        try {
            wpc.placeDice(3,0, new Dice(DiceColor.BLUE, DiceShade.FIVE), true, false);
            fail("It was expected a DieNotPlaceableException");
        } catch (DieNotPlaceableException e) {
            assertEquals("Must be adjacent to another dice", e.getMessage());
        }

        try {
            wpc.placeDice(0,1, new Dice(DiceColor.RED, DiceShade.ONE), false, true);
            fail("It was expected a DieNotPlaceableException");
        } catch (DieNotPlaceableException e) {
            assertEquals("Violates adjacent constraint", e.getMessage());
        }

        try {
            Dice dice = new Dice(DiceColor.RED, DiceShade.TWO);
            wpc.placeDice(0,1, dice, false, true);
            assertEquals(wpc.getSpaces()[0][1].getDice(), dice);
        } catch (DieNotPlaceableException e) {
            fail("unexpected exception");
        }

        try {
            wpc.placeDice(1,2, new Dice(DiceColor.PURPLE, DiceShade.THREE), true, false);
            fail("It was expected a DieNotPlaceableException");
        } catch (DieNotPlaceableException e) {
            assertEquals("Violates adjacent constraint", e.getMessage());
        }

        try {
            Dice dice = new Dice(DiceColor.BLUE, DiceShade.THREE);
            wpc.placeDice(1,2, dice, true, false);
            assertEquals(wpc.getSpaces()[1][2].getDice(), dice);
        } catch (DieNotPlaceableException e) {
            fail("unexpected exception");
        }

        try {
            wpc.placeDice(1,1, new Dice(DiceColor.RED, DiceShade.FIVE), true, true);
            fail("It was expected a DieNotPlaceableException");
        } catch (DieNotPlaceableException e) {
            assertEquals("Violates adjacent constraint", e.getMessage());
        }

        try {
            Dice dice = new Dice(DiceColor.PURPLE, DiceShade.FIVE);
            wpc.placeDice(1,1, dice, true, true);
            assertEquals(wpc.getSpaces()[1][1].getDice(), dice);
        } catch (DieNotPlaceableException e) {
            fail("unexpected exception");
        }

        try {
            wpc.placeDice(0,3, new Dice(DiceColor.RED, DiceShade.ONE), true, true);
            fail("It was expected a DieNotPlaceableException");
        } catch (DieNotPlaceableException e) {
            assertEquals("Violates adjacent constraint", e.getMessage());
        }

        try {
            Dice dice = new Dice(DiceColor.RED, DiceShade.SIX);
            wpc.placeDice(0,3, dice, true, true);
            assertEquals(wpc.getSpaces()[0][3].getDice(), dice);
        } catch (DieNotPlaceableException e) {
            fail("unexpected exception");
        }

        try {
            Dice dice = new Dice(DiceColor.BLUE, DiceShade.ONE);
            wpc.placeDice(2,1, dice, true, true);
            assertEquals(wpc.getSpaces()[2][1].getDice(), dice);
        } catch (DieNotPlaceableException e) {
            fail("unexpected exception");
        }

        try {
            Dice dice = new Dice(DiceColor.YELLOW, DiceShade.THREE);
            wpc.placeDice(0,0, dice, true, true);
            assertEquals(wpc.getSpaces()[0][0].getDice(), dice);
        } catch (DieNotPlaceableException e) {
            fail("unexpected exception");
        }

        //useful for following tests
        try {
            Dice dice = new Dice(DiceColor.YELLOW, DiceShade.ONE);
            wpc.placeDiceIsolated(3,4, dice);
            assertEquals(wpc.getSpaces()[3][4].getDice(), dice);
        } catch (DieNotPlaceableException e) {
            fail("unexpected exception");
        }

        try {
            wpc.placeDice(2,4, new Dice(DiceColor.GREEN, DiceShade.ONE), true, true);
            fail("It was expected a DieNotPlaceableException");
        } catch (DieNotPlaceableException e) {
            assertEquals("Violates adjacent constraint", e.getMessage());
        }

        try {
            Dice dice = new Dice(DiceColor.GREEN, DiceShade.TWO);
            wpc.placeDice(2,4, dice, true, true);
            assertEquals(wpc.getSpaces()[2][4].getDice(), dice);
        } catch (DieNotPlaceableException e) {
            fail("unexpected exception");
        }
    }





    @Test
    public void checkMovable()throws DieNotPlaceableException{
        WindowPatternCard wpc = new WindowPatternCard("0:Kaleidoscopie Dream:4:YB**1G*5*43*R*G2**BY");
        wpc.placeDice(0,0,new Dice(DiceColor.YELLOW,DiceShade.THREE),true,true);
        wpc.placeDice(1,1,new Dice(DiceColor.YELLOW,DiceShade.FOUR),true,true);


        List<Integer[]> expectedPositions = new ArrayList<>();
        Integer[] demoPositions = new Integer[2];
        demoPositions[0] =0;
        demoPositions[1] = 0;
        expectedPositions.add(demoPositions.clone());
        demoPositions = new Integer[2];
        demoPositions[0] = 1;
        demoPositions[1] = 1;
        expectedPositions.add(demoPositions.clone());

        WindowPatternCard cachedWindow = new WindowPatternCard(wpc);

        try{

            wpc.checkMovable(DiceColor.NEUTRAL,true,true,null);

        }catch (NoMovableDiceException error){
            fail("Unexpected exception");
        }


        //checking that the checkMovable doesn't throw any exception and doesn't modify the window
        Assert.assertEquals(cachedWindow,wpc);

        try{
            wpc.checkMovable(DiceColor.RED,true,true,null);
            fail("Unexpected execution");
        }catch (NoMovableDiceException error){
            assertEquals("There is no movable die in the window", error.getMessage());
        }

        try{
            wpc.checkMovable(DiceColor.YELLOW,true,true,expectedPositions);
            fail("Unexpected execution");
        }catch (NoMovableDiceException error){
            assertEquals("There is no movable die in the window", error.getMessage());
        }


        WindowPatternCard demoWpc = new WindowPatternCard("0:Kaleidoscopie Dream:4:YBBBBB*BBBBBBBBBBBBB");
        demoWpc.placeDice(0,0,new Dice(DiceColor.YELLOW,DiceShade.THREE),true,true);
        demoWpc.placeDice(1,1,new Dice(DiceColor.YELLOW,DiceShade.FOUR),true,true);

        try{
            demoWpc.checkMovable(DiceColor.YELLOW,true,true,null);
            fail("Unexpected execution");
        }catch (NoMovableDiceException error){
            assertEquals("Found no space where to move the die", error.getMessage());
        }


    }



    @Test
    public void testSelectSpaceWithDie()throws DieNotPlaceableException{
        WindowPatternCard wpc = new WindowPatternCard("0:Kaleidoscopie Dream:4:YB**1G*5*43*R*G2**BY");
        wpc.placeDice(0,0,new Dice(DiceColor.YELLOW,DiceShade.THREE),true,true);
        wpc.placeDice(1,1,new Dice(DiceColor.YELLOW,DiceShade.FOUR),true,true);
        wpc.setCache();

        Integer[] spaceCoordinates = new Integer[2];
        spaceCoordinates[0] = 0;
        spaceCoordinates[1] = 0;

        //checking returned die equals to the one placed and counter is updated
        try{
            Assert.assertEquals(new Dice(DiceColor.YELLOW,DiceShade.THREE),wpc.getDiceFromSpace(spaceCoordinates,DiceColor.NEUTRAL,DiceShade.NEUTRAL));
            Assert.assertEquals(1,wpc.getDiceOnBoard());
        }catch (Exception e){
            fail("Unexpected exception " + e.getMessage());
        }

        spaceCoordinates[0] = 1;
        spaceCoordinates[1] = 1;
        try{
            Assert.assertEquals(new Dice(DiceColor.YELLOW,DiceShade.FOUR),wpc.getDiceFromSpace(spaceCoordinates,DiceColor.NEUTRAL,DiceShade.NEUTRAL));
            Assert.assertTrue(wpc.isEmpty());
        }catch (Exception e){
            fail("Unexpected exception " + e.getMessage());
        }

        spaceCoordinates[0] = 0;
        spaceCoordinates[1] = 0;

        wpc = wpc.getCache();
        //checking exception is thrown when picking a die not matching the color costraint
        try{
            Assert.assertEquals(new Dice(DiceColor.YELLOW,DiceShade.THREE),wpc.getDiceFromSpace(spaceCoordinates,DiceColor.RED,DiceShade.NEUTRAL));
            fail("Unexpected execution");
        }catch (Exception e){
            Assert.assertEquals("The die you have to select must be of color RED", e.getMessage());
        }

        //checking exception is thrown when picking a die not matching shade constraint
        try{
            Assert.assertEquals(new Dice(DiceColor.YELLOW,DiceShade.THREE),wpc.getDiceFromSpace(spaceCoordinates,DiceColor.YELLOW,DiceShade.FOUR));
            fail("Unexpected execution");
        }catch (Exception e){
            Assert.assertEquals("The die you have to select must be of shade FOUR", e.getMessage());
        }


        spaceCoordinates[0] = 3;
        spaceCoordinates[1] = 3;
        //checking exception is thrown when picking a die that not exists
        try{
            Assert.assertEquals(new Dice(DiceColor.YELLOW,DiceShade.THREE),wpc.getDiceFromSpace(spaceCoordinates,DiceColor.YELLOW,DiceShade.FOUR));
            fail("Unexpected execution");
        }catch (Exception e){
            Assert.assertEquals("No dice in the selected space",e.getMessage());
        }

        spaceCoordinates[0] = 3;
        spaceCoordinates[1] = 10;
        //checking exception is thrown when giving and index out of bound
        try{
            Assert.assertEquals(new Dice(DiceColor.YELLOW,DiceShade.THREE),wpc.getDiceFromSpace(spaceCoordinates,DiceColor.YELLOW,DiceShade.FOUR));
            fail("Unexpected execution");
        }catch (Exception e){
            Assert.assertEquals("This space doesn't exist",e.getMessage());
        }



    }


    @Test
    public void testIsPlaceable()throws DieNotPlaceableException{
        WindowPatternCard wpc = new WindowPatternCard("0:Kaleidoscopie Dream:4:YBBBBB*BBBBBBBBBBBBB");
        wpc.placeDice(0,0,new Dice(DiceColor.YELLOW,DiceShade.THREE),true,true);
        wpc.placeDice(1,1,new Dice(DiceColor.YELLOW,DiceShade.FOUR),true,true);

        Assert.assertFalse(wpc.isPlaceable(new Dice(DiceColor.RED,DiceShade.THREE),true,true));
        Assert.assertTrue(wpc.isPlaceable(new Dice(DiceColor.BLUE,DiceShade.TWO),true,true));
    }


    @Test
    public void testIsPlaceableIsolated()throws DieNotPlaceableException{
        WindowPatternCard wpc = new WindowPatternCard("0:Kaleidoscopie Dream:4:YBBBBB*BBBBBBBBBBBBB");
        wpc.placeDice(0,0,new Dice(DiceColor.YELLOW,DiceShade.THREE),true,true);
        wpc.placeDice(1,1,new Dice(DiceColor.YELLOW,DiceShade.FOUR),true,true);

        Assert.assertFalse(wpc.isPlaceableIsolate(new Dice(DiceColor.RED,DiceShade.THREE)));
        Assert.assertTrue(wpc.isPlaceableIsolate(new Dice(DiceColor.BLUE,DiceShade.TWO)));
    }

    @Test
    public void testNotEquals() throws DieNotPlaceableException {
        WindowPatternCard wpc = new WindowPatternCard("0:Kaleidoscopie Dream:4:YB**1G*5*43*R*G2**BY");
        wpc.placeDice(0,0,new Dice(DiceColor.YELLOW,DiceShade.THREE),true,true);
        wpc.placeDice(1,1,new Dice(DiceColor.YELLOW,DiceShade.FOUR),true,true);

        WindowPatternCard testWpc = new WindowPatternCard("1:Kaleidoscopie Dream:4:YB**1G*5*43*R*G2**BY");
        testWpc.placeDice(0,0,new Dice(DiceColor.YELLOW,DiceShade.THREE),true,true);
        testWpc.placeDice(1,1,new Dice(DiceColor.YELLOW,DiceShade.FOUR),true,true);
        assertNotEquals(testWpc,wpc);

        testWpc = new WindowPatternCard("0:Kraleidoscopie Dream:4:YB**1G*5*43*R*G2**BY");
        testWpc.placeDice(0,0,new Dice(DiceColor.YELLOW,DiceShade.THREE),true,true);
        testWpc.placeDice(1,1,new Dice(DiceColor.YELLOW,DiceShade.FOUR),true,true);
        assertNotEquals(testWpc,wpc);

        testWpc = new WindowPatternCard("0:Kaleidoscopie Dream:4:YB**1G*5*43*R*G2**BY");
        testWpc.placeDice(0,0,new Dice(DiceColor.YELLOW,DiceShade.THREE),true,true);
        assertNotEquals(testWpc,wpc);

        testWpc =new WindowPatternCard("0:Kaleidoscopie Dream:4:YBR*1G*5*43*R*G2**BY");
        testWpc.placeDice(0,0,new Dice(DiceColor.YELLOW,DiceShade.THREE),true,true);
        testWpc.placeDice(1,1,new Dice(DiceColor.YELLOW,DiceShade.FOUR),true,true);
        assertNotEquals(testWpc,wpc);


        testWpc =new WindowPatternCard("0:Kaleidoscopie Dream:5:YB**1G*5*43*R*G2**BY");
        testWpc.placeDice(0,0,new Dice(DiceColor.YELLOW,DiceShade.THREE),true,true);
        testWpc.placeDice(1,1,new Dice(DiceColor.YELLOW,DiceShade.FOUR),true,true);
        assertNotEquals(testWpc,wpc);

        testWpc  = new WindowPatternCard("0:Kaleidoscopie Dream:5:YB**1G*5*43*R*G2**BY");
        testWpc.placeDice(0,0,new Dice(DiceColor.YELLOW,DiceShade.THREE),true,true);
        testWpc.placeDice(1,1,new Dice(DiceColor.YELLOW,DiceShade.FIVE),true,true);
        assertNotEquals(testWpc,wpc);
    }



}











