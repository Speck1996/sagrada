package com.model.gameboard;


import com.model.PlayerInGame;

import java.util.Comparator;
import java.util.Map;


/**Comparator class used to compare the player by descending score
 */
public class PlayerDescendingComparator implements Comparator<PlayerInGame> {

    /**The map containing all the players and their respective scores
     */
    private Map<PlayerInGame, Integer> playersOrder;

    /**Constructs a PlayerDescendingComparator with the given map
     * @param playersOrder
     */
    PlayerDescendingComparator(Map<PlayerInGame, Integer> playersOrder) {
        this.playersOrder = playersOrder;
    }

    /**
     * Method used to compare player score based on the game rules (checking
     * at first total player score, then the private obj card score if there is a tie,
     * then the favor tokens if there is another tie and for last the player order in the rounds)
     * @return 1 if p1 is greater than p2, 0 if they are equals, -1 if p1 lower than p2
     */
    @Override
    public int compare(PlayerInGame p1, PlayerInGame p2){

        //check for inactive players
        if(p1.isSuspended() && !p2.isSuspended())
            return 1;
        if(!p1.isSuspended() && p2.isSuspended())
            return -1;


        Integer p1Points = p1.getPlayerPoints();
        Integer p2Points = p2.getPlayerPoints();
        if(!p1Points.equals(p2Points))
            return p2Points.compareTo(p1Points);


        //tie: compare private obj points
        p1Points = p1.getPlayerObjCards().get(0).computeScore(p1.getWindow().getSpaces());
        p2Points = p2.getPlayerObjCards().get(0).computeScore(p2.getWindow().getSpaces());
        if(!p1Points.equals(p2Points))
            return p2Points.compareTo(p1Points);


        //tie: compare favor tokens
        p1Points = p1.getFavorTokens();
        p2Points = p2.getFavorTokens();
        if(!p1Points.equals(p2Points))
            return p2Points.compareTo(p1Points);

        //tie: compare players order
        p1Points = playersOrder.get(p1);
        p2Points = playersOrder.get(p2);
        return p2Points.compareTo(p1Points);
    }



}
