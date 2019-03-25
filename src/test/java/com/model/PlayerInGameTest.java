package com.model;

import com.model.patterns.WindowPatternCard;
import org.junit.Test;

import static org.junit.Assert.*;

public class PlayerInGameTest {

    @Test
    public void constructorTest() {
        Player p = new Player("Test");
        PlayerInGame player = new PlayerInGame(p, null);

        assertEquals("Test", player.getUsername());
        assertEquals(p, player.getPlayer());

    }

    @Test
    public void windowTest() {
        PlayerInGame p = new PlayerInGame(new Player("Test"), null);

        p.setWindow(new WindowPatternCard("0:Kaleidoscopie Dream:4:YB**1G*5*43*R*G2**BY"));
        assertEquals(0, p.getWindow().getId());
        assertEquals(4, p.getFavorTokens());

        p.removeTokens(2);
        assertEquals(2, p.getFavorTokens());
    }

    @Test
    public void flagTest() {
        PlayerInGame p = new PlayerInGame(new Player("Test"), null);

        p.setActive();
        p.setDicePlayed();
        p.setToolCardPlayed();

        assertTrue(p.isActive());
        assertTrue(p.hasPlayedDice());
        assertTrue(p.hasPlayedToolCard());

        p.setNewTurn();
        assertFalse(p.isActive());
        assertFalse(p.hasPlayedDice());
        assertFalse(p.hasPlayedToolCard());

        p.suspend();
        assertTrue(p.isSuspended());

        p.removeSuspension();
        assertFalse(p.isSuspended());

        p.setOffline();
        assertTrue(p.isOffline());

        p.setOnline();
        assertFalse(p.isOffline());

        p.toggleSkipTurn();
        assertTrue(p.getSkipTurn());
        p.toggleSkipTurn();
        assertFalse(p.getSkipTurn());
    }

}