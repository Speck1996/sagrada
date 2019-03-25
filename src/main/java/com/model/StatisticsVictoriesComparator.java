package com.model;

import java.util.Comparator;

/**
 * An implementation of Comparator interface for compare Player by they number of victories.
 */
public class StatisticsVictoriesComparator implements Comparator<Player> {

    /**
     *
     * @param p1 the first player to compare.
     * @param p2 the second player to compare.
     * @return the value {@code 0} if p1 and p2 has the same number of victories;
     *         a value less then {@code 0} if p1 has more victories then p2;
     *         a value greater than {@code 0} if p2 has more victories then p1.
     */
    @Override
    public int compare(Player p1, Player p2) {
        Integer p1Victories = p1.getNumOfVictories();
        Integer p2Victories = p2.getNumOfVictories();
        return p2Victories.compareTo(p1Victories);
    }
}
