package com.model.gameboard;

import com.model.Player;
import com.model.PlayerInGame;
import com.model.cards.objcard.PrivateObjCard;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class PlayerDescendingComparatorTest {

    @Test
    public void compare() {

        Map<PlayerInGame, Integer> playersOrder = new HashMap<>();
        PlayerDescendingComparator comparator = new PlayerDescendingComparator(playersOrder);

        PlayerInGame p1;
        PlayerInGame p2;

        p1 = new PlayerInGame(null, null);
        p2 = new PlayerInGame(null, null);


        //check inactive player
        p1.suspend();
        assertEquals(1, comparator.compare(p1, p2));

        p1.setOnline();
        p2.suspend();
        assertEquals(-1, comparator.compare(p1,p2));


        //check points
        p1 = new PlayerInGame(null, null);
        p2 = new PlayerInGame(null, null);


        p1.addPoints(10);
        assertEquals(-1, comparator.compare(p1, p2));
        p2.addPoints(20);
        assertEquals(1, comparator.compare(p1, p2));













        playersOrder.put(p1, 0);
        playersOrder.put(p2, 1);


    }
}