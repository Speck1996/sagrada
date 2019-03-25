package com.model.gameboard;

import com.control.PlayerState;
import com.model.GameObserver;
import com.model.PlayerInGame;
import com.model.cards.ObjCard;
import com.model.cards.ToolCard;
import com.model.dice.Dice;
import com.model.dice.NoDiceException;


import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;


/**This class contains all the necessary for the solo game mode, changing some methods of the multiplayer gameboard
 * and implementing new ones, regarding the points score calculation, end game and player disconnection since there is only one
 * player.
 * @see GameBoard
 */




public class SoloGameBoard extends GameBoard {


    /**{@inheritDoc }
     */
    //same constructor of gameboard
    public SoloGameBoard(int toolCardNumber, int publicObjCardNumber){
        super(toolCardNumber,publicObjCardNumber);
    }


    /**This method changes the playerManagement of the multiplayer mode: in single player there is no need to set
     * the {@link GameBoard#currentPlayer} every round, because it will always be the first element of {@link GameBoard#matchPlayers}
     * During the execution of this method the attribute {@link GameBoard#ascendant} is also set: its value is switched to true
     * in the {@link GameBoard#checkGameOver()} method and to false in this one. The fact that is half set in the {@link GameBoard#checkGameOver()}
     * and half in this method is because its switching is linked to the number of players (when there is only one player it switches
     * every time {@link GameBoard#nextTurn()} is called, in multiplayer mode it switches when {@link GameBoard#currentPlayer} is the
     * last player that has played his round.)
     * The boolean returned is needed to distinguish between a finished or still going game
     * @see GameBoard
     * @return true if it is the last round of the last turn, false otherwise
     */
    @Override
    protected boolean playerManagement(){
        if(!ascendant && checkGameOver()){
                return true;
        }else{
            ascendant = false;
        }
        return false;
    }




    //when the user goes afk (isSuspended) a timer starts
    //if the timer finish its execution and the player is still
    //suspended the game is aborted

    /**This method is called by a {@link TimerTask} when a player is set to suspended. If time's up and the player
     * still hasn't come back in the game this method is called: after stopping the turn timer and removing the player
     * from the game it warns the user of the game aborted
     */
    private void abortSoloGame(){
        matchPlayers.forEach(PlayerInGame::stopChrono);

        System.out.println("Solo game aborted");
        if(timer != null)
            timer.cancel();

        currentPlayer = null;
        gameEnded = true;

        resetSocketReaders();

        //remove players from map player->controller
        for(PlayerInGame p: matchPlayers)
            model.removePlayerFromGame(p.getPlayer());

        for(GameObserver o: observers) {
            o.onGameEnd();
        }
    }

    //method called in gameboards.nextTurn
    //it checks if the card was activated
    //and then proceed to remove it if this condition is met

    /**{@inheritDoc}
     * After checking the ToolCardState this method checks if there is any used card by checking if there is a
     * die on the ToolCar: if this condition is met the cad is removed
     */
    @Override
    protected void checkToolCardState(){
        super.checkToolCardState();


        Iterator<ToolCard> cardIterator = toolCardsOnBoard.iterator();
        //checking if the card was used
        while(cardIterator.hasNext()){
            ToolCard nextToolCard = cardIterator.next();

            //card activation check based on the dice on the toolcard
            if(nextToolCard.getDiceSpace().getDice()!= null){
                matchPlayers.get(0).notifyViewObserver("Removing " + nextToolCard.toString());
                cardIterator.remove();
            }
        }
    }


    //recursive method (if the timer task is considered): if the private card index is selected it proceeds with point
    //calculation, if not he just set the player state to selectprivateobjcard so his input
    //will be parsed and used to select the desired private card; he will have a limited time as
    //a timer is started right after the selectprivateobjcard state

    /**Method that handles the end game: in solo mode the player must select which of the two {@link com.model.cards.objcard.PrivateObjCard}
     * wants to use. To do this if the player still hasn't selected and index the method sets the {@link PlayerState} to {@link PlayerState#SELECTPRIVATEOBJCARD} ,
     * notify the user with the selectable cards and starts a {@link TimerTask} that sets automatically the first {@link com.model.cards.objcard.PrivateObjCard}
     * if the player still hasn't chosen one.
     * If the {@link PlayerInGame#getPrivateCardIndex()} is set, the method execute the end game managing: announce who is the
     * winner and remove the player fromt he board
     */
    @Override
    public void endGame(){


        SoloGameBoard gameBoard = this;

        currentPlayer = matchPlayers.get(0);

        //integer for readability purpose
        int privateObjCardNumber = currentPlayer.getPlayerObjCards().size();

        //index still not set correctly
        if(!(currentPlayer.getPrivateCardIndex()>=0 && currentPlayer.getPrivateCardIndex()< privateObjCardNumber)) {

            currentPlayerState = PlayerState.SELECTPRIVATEOBJCARD;
            String objCards = new String();


            for (ObjCard privateCard : currentPlayer.getPlayerObjCards()) {
                objCards = objCards + "\n" + privateCard.toString();
            }

            //notifying the cards
            currentPlayer.notifyViewObserver("Game Ended, pick the private card you prefer selecting the card index:" + objCards);

            //timer for automatically setting the card index
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    if (currentPlayer != null) {
                        currentPlayer.notifyViewObserver("First card automatically picked");
                        currentPlayer.setPrivateCardIndex(0);
                        gameBoard.endGame();
                    }

                }
            }, 10000);
        }else{

            //see gameboard method
            endInitialization();

            PlayerInGame player =  matchPlayers.get(0);

            //calculating points
            getPlayerScore();
            int playerPoints = player.getPlayerPoints();
            int boardPoints = getBoardScore();

            player.notifyViewObserver("You obtained " + playerPoints);
            player.notifyViewObserver("The board obtained "+boardPoints );

            if(playerPoints>boardPoints){
                player.notifyViewObserver("Congratz you won!!");
            }else{
                player.notifyViewObserver("You lost");
            }
            player.notifyViewObserver("Game Ended");

            removePlayers();
        }

    }

    /**This method calculates the player obtained points using the selected private object card, the public object cards
     * on board and the placed dice. It then proceeds to remove the points for blank spaces
     * @see com.model.cards.objcard.PrivateObjCard
     * @see com.model.cards.objcard.PublicObjCard
     */

    private void getPlayerScore(){
        //where the score will be stored temporally
        int points;

        //in solo mode, there is only one player
        PlayerInGame player = matchPlayers.get(0);

        int privateObjSelected = player.getPrivateCardIndex();


        for (ObjCard ocard : objCardsOnBoard) {
            points = ocard.computeScore(player.getWindow().getSpaces());
            System.out.println("Player " + player.getUsername() + " obtained "+ points + " from" + ocard.toString());
            player.addPoints(points);
        }

        //in solo mode you can choose between 2 objcards

        ObjCard chosenPrivateCard = player.getPlayerObjCards().get(privateObjSelected);

        points = chosenPrivateCard.computeScore(player.getWindow().getSpaces());
        System.out.println("Player " + player.getUsername() + " obtained "+ points + " from" + chosenPrivateCard.toString());
        player.addPoints(points);

        //in solo mode the player loses 3 points for each blank space
        points = 20 - player.getWindow().getDiceOnBoard();
        points = points * 3;


        System.out.println("Removed " + points + " from player " + player.getUsername() + " for his unfilled spaces");
        player.removePoints(points);

    }


    /**This method calculates the board score using the {@link RoundBoard}: for every dice present on the {@link RoundBoard}
     * the shade value of the dice is added to the board score
     * @return the points obtained by the board, sums of all dice shade value on the RoundBoard
     */
    private  int getBoardScore(){

        RoundBoard roundBoard = gameRoundBoard;

        int boardPoints = 0;

        //summing the values of all the dice on the roundboard, de facto it is the board score for the solo game
        for(int i = 0; i <roundBoard.getRound(); i++){
            if(roundBoard.getDice(i) != null) {
                for (int j = 0; j < roundBoard.getDice(i).size(); j++) {
                    Dice roundDice = roundBoard.getDice(i).get(j);
                    boardPoints = boardPoints + roundDice.getShade().ordinal();

                }
            }
        }

        return boardPoints;
    }

    /**This method refreshes the stock by adding the desired values of dice in it, drawing them from the {@link com.model.dice.DiceBag}
     * @see com.model.dice.Stock
     * @see com.model.dice.DiceBag
     */
    //in solo mode the dice are four
    @Override
    public void refreshStock(){
        for(int i = 0; i < 4; i++){
            try {
                gameStock.insertDice(gameDiceBag.drawDice());    //dice for the new turn
            } catch (NoDiceException e) {
                System.out.println("No dice left in the bag");
            }
        }

    }

    //on player suspencion this method starts and set a timer
    //if the player doesn't resume the game by the timer end
    //the game will automatically terminate

    /**Starts a {@link TimerTask} that activates the {@link SoloGameBoard#abortSoloGame()} if the player is still suspended
     * when the time runs out.
     * @return always true because in single player mode when the minimum active player condition is always met
     */
    @Override
    protected boolean checkMinimumActivePlayers(){
        SoloGameBoard gameBoard = this;
        if(activePlayers == 0){
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    if(activePlayers == 0){
                        System.out.println("Game automatically terminated");
                        observers.get(0).sendMessage("You were afk for too long, game automatically aborted");
                        gameBoard.abortSoloGame();
                    }else{
                        System.out.println("afk timer expired");
                    }

                }
            }, 10000);

        }
        return true;
    }

}
