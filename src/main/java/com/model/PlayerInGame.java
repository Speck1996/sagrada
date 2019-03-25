package com.model;


import com.model.cards.ObjCard;
import com.model.gameboard.GameBoard;
import com.model.patterns.WindowPatternCard;

import java.util.List;
import java.util.Objects;

/**
 * A PlayerInGame represents a player during a match.
 * It is construct around a Player.
 */
public class PlayerInGame {

    private GameBoard gameBoard;
    private Player player;
    private int playerPoints;
    private boolean toolCardPlayed;
    private boolean dicePlayed;
    private int favorsToken;
    private List<ObjCard> privateObjCards;
    private MessageObserver playerObserver;
    private WindowPatternCard playerWpc;
    private int privateCardIndex;

    private final Chronometer chrono;
    private boolean suspended;  //true if during a turn the timer expired
    private boolean turnActive; //true if during the current turn the player sent any command
    private boolean offline; //true if is currently offline
    private boolean skipTurn; //true if running pliers is used--> player skip the turn


    /**
     * Constructs a PlayerInGame.
     * @param player the player to which this PlayerInGame refers.
     * @param gameBoard the gameboard of the match.
     */
    public PlayerInGame(Player player, GameBoard gameBoard) {
        this.gameBoard = gameBoard;
        this.player = player;
        this.playerPoints = 0;
        this.toolCardPlayed = false;
        this.dicePlayed = false;
        this.skipTurn = false;
        this.suspended = false;
        this.turnActive = false;
        this.privateCardIndex = -1;
        this.chrono = new Chronometer();
    }


    /**
     * Returns the username of this player.
     * @return the username of this player.
     */
    public String getUsername() {
        return player.getUsername();
    }

    /**
     * Returns the Player to which this PlayerInGame refers.
     * @return the Player to which this PlayerInGame refers.
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * Remove the specified number of favor tokens from this player.
     * @param paidToken the number of favor token to be removed.
     */
    public void removeTokens(int paidToken){
        this.favorsToken = this.favorsToken-paidToken;
    }


    /**
     * Returns the number of favor tokens of this player.
     * @return the number of favor tokens of this player.
     */
    public int getFavorTokens() {
        return this.favorsToken;
    }


    /**
     * Set a window for this player.
     * Initialize the number of favor tokens corresponding to the difficulty of the window.
     * @param selectedwpc the window for this player.
     */
    public void setWindow(WindowPatternCard selectedwpc){
        this.playerWpc = selectedwpc;
        this.favorsToken = selectedwpc.getDifficulty();
    }

    /**
     * Set the gameboard for the match.
     * @param gameBoard the gameboard for the match.
     */
    public void setGameBoard(GameBoard gameBoard){
        this.gameBoard = gameBoard;
    }

    /**
     * Revert this player's window to a previous state.
     * @param cachedWpc the window with the right state.
     */
    public void revertWindow(WindowPatternCard cachedWpc){
        this.playerWpc = cachedWpc;
    }

    /**
     * Returns this player's window.
     * @return this player's window.
     */
    public WindowPatternCard getWindow(){
        return this.playerWpc;
    }

    /**
     * Set that this player has placed a die
     */
    public void setDicePlayed(){
        this.dicePlayed = true;
    }

    /**
     * Check if the player has already played a die.
     * @return true if the player has already played a die.
     */
    public boolean hasPlayedDice(){
        return this.dicePlayed;
    }

    /**
     * Set that this player has used a toolcard.
     */
    public void setToolCardPlayed() {
        this.toolCardPlayed = true;
    }

    /**
     * Check if this player had already usa a toolcard in the current turn.
     * @return true if this player had already usa a toolcard in the current turn.
     */
    public boolean hasPlayedToolCard(){
        return this.toolCardPlayed;
    }

    /**
     * Set a new turn for this player.
     * In any case, immediately after this call, the methods hasPlayedDice(), hasPlayedToolCard() and isActive() will return false.
     * This continues to happen until a setDicePlayer(), setToolCardPlayed() or setActive() call respectively.
     */
    public void setNewTurn(){

        this.dicePlayed = false;
        this.toolCardPlayed = false;
        this.turnActive = false;
    }


    /**
     * Set the list of private object cards extracted for this player (only one if multiplayer).
     * @param givenCards the list of private object cards extracted for this player.
     */
    public void setObjCards(List<ObjCard> givenCards){
        this.privateObjCards = givenCards;
    }

    /**
     * Returns the list the private object cards extracted for this player (only one is multiplayer).
     * @return the list the private object cards extracted for this player.
     */
    public List<ObjCard> getPlayerObjCards(){
        return this.privateObjCards;
    }

    /**
     * Set the private object card (singleplayer only).
     * @param cardIndex the index of the card.
     */
    public void setPrivateCardIndex(int cardIndex){

        //useful when timer finish
        if(this.privateCardIndex != -1){
            return;
        }
        this.privateCardIndex = cardIndex;
    }


    /**
     * Returns the index of this player private object card.
     * @return the index of this player private object card.
     */
    public int getPrivateCardIndex(){
        return this.privateCardIndex;
    }



    /**
     * Add the specified points to this player's final score.
     * @param pointsToAdd the points to add.
     */
    public void addPoints(int pointsToAdd){
        this.playerPoints = this.playerPoints + pointsToAdd;
    }

    /**
     * Remove the specified points from this player's final score.
     * @param pointsToRemove the points to remove.
     */
    public  void removePoints(int pointsToRemove){
        this.playerPoints = this.playerPoints - pointsToRemove;

        //in case the player score goes negative
        if(this.playerPoints < 0){
            this.playerPoints = 0;
        }
    }

    /**
     * Returns the final score of this player.
     * @return final score.
     */
    public int getPlayerPoints() {
        return this.playerPoints;
    }


    /**
     * Attach a new observer to this player.
     * If already existed an observer, the old one is removed.
     * This observer is used for notify messages to player's client, especially from toolcard.
     * @param observer the observer to attach.
     */
    public void attachObserver(MessageObserver observer){
        this.playerObserver = observer;
    }

    /**
     * Detach the observer from this player.
     */
    public void detachObserver() {
        this.playerObserver = null;
    }


    /**
     * Notify a message to this player's client.
     * @param message the message to notify.
     */
    public void notifyViewObserver(String message) {
        this.playerObserver.sendMessage(message);
        gameBoard.checkNotRespondingPlayer();
    }

    /**
     * Toggle the skipTurn flag.
     * If before this call getSkipTurn() returns false, after will return true and vice versa.
     */
    public void toggleSkipTurn(){
        skipTurn  = !skipTurn;
    }

    /**
     * Check if this player should skip a turn.
     * @return true if this player should skip a turn.
     */
    public boolean getSkipTurn(){
        return this.skipTurn;
    }


    /**
     * Set this player active
     */
    public void setActive() {
        turnActive = true;
    }

    /**
     * Check if this player is active.
     * @return true if this player is active.
     */
    public boolean isActive() {
        return turnActive;
    }

    /**
     * Suspend this player.
     * The next turns of this player should be skip until a removeSuspension() or setOnline() call is performed.
     */
    public void suspend() {
        suspended = true;
    }

    /**
     * Remove the suspension for this player.
     */
    public void removeSuspension() {
        suspended = false;
    }

    /**
     * Check if this player is suspended
     * @return true if this player is suspended.
     */
    public boolean isSuspended() {
        return suspended;
    }


    /**
     * Check if this player is offline.
     * @return true if this player is offline.
     */
    public boolean isOffline() {
        return offline;
    }

    /**
     * Set this player as offline and stop its chronometer.
     * This method should be call only due to client disconnection or
     */
    public void setOffline() {
        this.offline = true;
        this.chrono.stop();
    }

    /**
     * Set this player as online and restart its chronometer.
     * This method should be call only due to client reconnection.
     */
    public void setOnline() {
        this.offline = false;
        this.suspended = false;
        this.chrono.start();
    }

    /**
     * Start this player's chronometer.
     */
    public void startChrono() {
        chrono.start();
    }

    /**
     * Stop this player's chronometer.
     */
    public void stopChrono() {
        chrono.stop();
    }

    /**
     * Returns the total time measured by this player's chronometer.
     * @return the total time measured by this player's chronometer.
     */
    public double getChronoTime() {
        return chrono.getTime();
    }

    /**
     * Method for equality check for PlayerInGame.
     * @param o the Object to check equality with.
     * @return true if this PlayerInGame is equal to the parameter.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerInGame that = (PlayerInGame) o;
        return Objects.equals(player, that.player);
    }

}
