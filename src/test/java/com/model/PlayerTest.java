package com.model;


import org.junit.Test;

import static org.junit.Assert.*;

public class PlayerTest {

    @Test
    public void constructorTest() {
        String test = "Test";
        Player player = new Player(test);
        assertEquals(test, player.getUsername());
    }

    @Test
    public void statsTest() {
        Player player = new Player("Test");

        player.newVictory();
        player.newDefeat();
        player.newDefeat();
        player.addPoints(30);
        player.addTime(39.91);

        assertEquals(1, player.getNumOfVictories());
        assertEquals(2, player.getNumOfDefeat());
        assertEquals(30, player.getTotPoints());
        assertEquals(39.91, player.getTotGameTime(), 0.0001);
    }

    @Test
    public void equalsTest() {
        Player p1 = new Player("Test");
        assertEquals(p1, p1);

        String s = "";
        assertNotEquals(p1, s);

        Player p2 = new Player("Test");
        assertEquals(p1, p2);



    }


}