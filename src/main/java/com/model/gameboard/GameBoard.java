package com.model.gameboard;

import com.control.PlayerState;
import com.control.SocketReader;
import com.model.*;
import com.model.cards.ObjCard;
import com.model.cards.ToolCard;
import com.model.cards.WrongIdException;
import com.model.cards.concretetoolcards.ToolCardState;
import com.model.cards.objcard.PublicObjCard;
import com.model.dice.*;

import java.util.*;

/**This class it's the central hub for the model: when a game starts an object of this class is initialized, and within this
 * class the core game operations (player, turn and end game management) take place.
 * The attributes are protected because they are used in the {@link SoloGameBoard} too.
 */


public class GameBoard {

    /**This attribute is used to get the clones of game components (List of dice,cards) and the players
     */
    protected final MainModel model;

    /**integer representing the milliseconds available in the turn
     */
    private static int timerTime;

    /**
     * The number of rounds in a match.
     */
    private static final int ROUNDS_NUM = 10;

    /** {@link com.model.gameboard.RoundBoard}, used to put remaining dice at the end
     * of the turn
     */
    protected RoundBoard gameRoundBoard;

    /**List of {@link ToolCard} available for the players
     */
    protected List<ToolCard> toolCardsOnBoard;

    /** {@link com.model.dice.DiceBag}, used to draw the dice in each turn
     */
    protected DiceBag gameDiceBag;

    /**{@link com.model.cards.ObjCard} available for the players
     */
    protected ObjCard[] objCardsOnBoard;

    /**List of {@link com.model.PlayerInGame} attending the game
     */
    protected List<PlayerInGame> matchPlayers;

    /**{@link com.model.dice.Stock}, used to store the list of placeable dice
     */
    protected Stock gameStock;

    /**List of observers
     */
    protected List<GameObserver> observers;

    /**This list disconnected {@link com.model.PlayerInGame} */
    private List<PlayerInGame> notRespondingPlayer = new ArrayList<>();

    /**{@link Timer} used for turn management*/
    protected Timer timer;

    /** Boolean used to identify the turn order (from the first player to the last if true or from the last to the
     * first if false)
     */
    protected boolean ascendant = true;

    /**{@link com.model.PlayerInGame} currently playing his turn*/
    protected PlayerInGame currentPlayer;

    /**The {@link com.control.PlayerState} identifies what kind of input the user is sending*/
    protected PlayerState currentPlayerState = PlayerState.NEUTRAL;

    /**Number of player not suspended or disconnected*/
    protected int activePlayers;

    /** If the game is ended (no more turn to play) this attribute is set to true*/
    protected boolean gameEnded;

    /**
     * True if the first round is started, false otherwise.
     */
    private boolean gameStarted;


    /**Initializes the attributes picking the list of game dice,cards and player from the model
     * @param toolCardNumber number of ToolCard to add to the toolCardsOnBoard List
     * @param publicObjCardNumber number of PublicObjCard to add to the objCardsOnBoardList
     */
    public GameBoard(int toolCardNumber,int publicObjCardNumber){

        this.model = MainModel.getModel();
        this.gameRoundBoard = new RoundBoard();

        this.gameDiceBag = new DiceBag(model.getGameDice());
        this.matchPlayers = new ArrayList<>();
        this.observers = new ArrayList<>();
        List<ToolCard> toolCards = new ArrayList<>();
        List<ObjCard> publicObjCards = new ArrayList<>();
        try {
           toolCards  = model.getToolCards();
           publicObjCards  = model.getPublicObjCards();
        }catch (WrongIdException error){
            System.out.println(error.getMessage());
        }


        Random rand = new Random();

        this.toolCardsOnBoard = new ArrayList<>();
        for(int i = 0; i < toolCardNumber; i++){
            int cardIndex = rand.nextInt(toolCards.size());
            toolCardsOnBoard.add(toolCards.remove(cardIndex));     //drafting toolcards
            toolCardsOnBoard.get(i).setGameBoard(this);
        }

        this.objCardsOnBoard = new PublicObjCard[publicObjCardNumber];
        for(int i = 0; i < publicObjCardNumber; i++){
            int cardIndex = rand.nextInt(publicObjCards.size());
            objCardsOnBoard[i] = publicObjCards.remove(cardIndex); //drafting objcards
        }

        this.gameStock = new Stock();

        this.currentPlayer = null;
        this.activePlayers = 0;
    }


    /**This method returns the ascendant value
     * @return true if the GameBoard is in ascendant mode, false otherwise
     */
    public boolean isAscendant(){
        return this.ascendant;
    }


    /**This method manages game turns: it starts the turn timer,manages the ToolCards state (if the player timer ran out in the middle of the toolcard
     * execution calls the {@link ToolCard#revert()}, sets the currentPlayerState to {@link PlayerState#NEUTRAL} and in the end manages
     * the next player turn, the game end phase in {@link GameBoard#playerManagement()} and the special case {@link PlayerState#EXTRATURN} activated
     * by the toolcard
     *
     */
    public void nextTurn() {
        boolean gameFinished;

        if(timer != null)
            timer.cancel();

        if(currentPlayer == null)  //it's the first round of the match
            gameStarted = true;

        checkToolCardState();


        currentPlayerState = PlayerState.NEUTRAL;

        if(!gameEnded) {

            do {
                //the game is just started
                if (currentPlayer == null) {
                    currentPlayer = matchPlayers.get(0);
                    newRound();
                } else{
                    gameFinished = playerManagement();
                    if(gameFinished){
                        break;
                    }
                }



                if(currentPlayer.getSkipTurn()){
                    currentPlayer.notifyViewObserver("Turn skipped due running pliers effect");

                    //reset flag of the player
                    currentPlayer.toggleSkipTurn();
                    nextTurn();

                    //last round
                    if(currentPlayer == null)
                        break;
                    else{
                        return;
                    }

                }else {
                    currentPlayer.setNewTurn();
                }


            } while (currentPlayer.isSuspended()&& activePlayers != 0);


            if (currentPlayer == null) {
                //adding last round dice
                insertRoundBoardDice();
                endGame();
            }
            else {
                notifyObserversNewTurn();
                startTimer();
            }
        }


    }

    /**This method checks if the player is still in {@link PlayerState#TOOLCARDEXECUTION} when the {@link GameBoard#nextTurn()}
     * is called: in that case for every card if the {@link ToolCardState} is not {@link ToolCardState#EXECUTED} or
     * {@link ToolCardState#NEUTRAL} the {@link ToolCard#revert()} is called to revert the state of the game to the state before
     * the activation of the card
     */
    protected void checkToolCardState(){
        if(currentPlayerState == PlayerState.TOOLCARDEXECUTION){
            for(ToolCard card: toolCardsOnBoard){
                if(card.getState() != ToolCardState.EXECUTED && card.getState()!=ToolCardState.NEUTRAL){
                    card.revert();
                }
            }
        }
    }


    /**This method manages the setting of the {@link GameBoard#currentPlayer} depending on the round order set by
     * the attribute {@link GameBoard#ascendant}. The  {@link GameBoard#ascendant} is also toggled every time the
     * last player or the first player of the turn is reached
     * @return true if the game has reached the set number of rounds,false otherwise
     */
    protected boolean playerManagement(){
        if (matchPlayers.indexOf(currentPlayer) == 0 && !ascendant) {   //start new round
           if(checkGameOver()){
               return true;
           }
        }
        else if (matchPlayers.indexOf(currentPlayer) == matchPlayers.size() - 1 && ascendant) {          //change direction
            ascendant = false;
        } else if (ascendant) {  //next player
            currentPlayer = matchPlayers.get(matchPlayers.indexOf(currentPlayer) + 1);
        } else {
                currentPlayer = matchPlayers.get(matchPlayers.indexOf(currentPlayer) - 1);
        }
        return false;
    }


    /**Checks if the GameBoard has reached the maximum number of rounds: in that case the {@link GameBoard#currentPlayer} is
     * set to null. Otherwise a new round starts and {@link GameBoard#ascendant} is set to true
     * @return true if the game has reached it's maximum number of rounds false otherwise
     */
    protected boolean checkGameOver(){
        if (gameRoundBoard.getRound() == ROUNDS_NUM) {
            //the match is over
            currentPlayer = null;
            //endGame();
            return true;
        } else {
            newRound();
            ascendant = true;
        }
        return false;

    }


    /**This method starts a timer in every new turn with a {@link TimerTask}: if the time runs out
     * and the player is still active the {@link GameBoard#nextTurn()} is called, otherwise is the player
     * isn't active it suspends the player before calling {@link GameBoard#nextTurn()}
     * @see GameBoard#suspendPlayer(PlayerInGame)
     */
    private void startTimer() {
        timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                //suspend player only if during the turn it wasn't active
                if(!currentPlayer.isActive()) {
                    System.out.println("Suspend " + currentPlayer.getUsername());

                    suspendPlayer(currentPlayer);
                }

                nextTurn();
            }
        }, timerTime);

    }


    /**Method to get the current player state
     * @return the currentPlayerState
     * @see PlayerState
     */
    public PlayerState getCurrentPlayerState() {
        return currentPlayerState;
    }

    /**Method to set the {@link GameBoard#currentPlayerState}, called by the {@link com.control.GameControllerImpl} or the
     * {@link GameBoard} to change the state depending on the requested action by user (exampled: {@link PlayerState#DICE} if
     * the user requested to place a die)
     * @param state the state to be set
     * @see com.control.GameControllerImpl#playerCommand(String, String)
     */
    public void setCurrentPlayerState(PlayerState state) {
        this.currentPlayerState = state;
    }


    /**Takes every remaining dice from the stock and add it in the current round. It then proceeds to clear the stock
     */
    private void insertRoundBoardDice(){
        //this is called every new round, the first "if" checks if the round is the first: in that case
        //there are no dice to collect
        if(gameRoundBoard.getRound() != 0){
            ArrayList<Dice> roundBoardDice = new ArrayList<>();
            for (Dice d : gameStock.getDice()) {
                roundBoardDice.add(new Dice(d));
            }
            gameRoundBoard.insertDices(roundBoardDice);
            System.out.println("Dice put in roundboard" + gameRoundBoard.toString());

            gameStock.clearStock(); //clearing the stock before filling it
        }
    }


    /**
     * Method called at the end of the game to reset all the present socketReaders removing the game controller from them
     * */
    protected void resetSocketReaders(){
        //reset SocketReaders
        for(PlayerInGame player: matchPlayers) {
            Optional<SocketReader> socketReaderOptional = model.getAllSocketReaders().stream().filter(s -> s.getPlayer() == player.getPlayer()).findFirst();

            if(socketReaderOptional.isPresent()) {
                System.out.println("Reset socketReader of: " + player.getUsername());
                socketReaderOptional.get().setController(null);
            }
        }
    }


    /** method called at the end of the game (both in the case game is finished because the last round was played
     * or because the minimum active player conditions is reached).
     * It sets the current player to null, clear the turn timer, notifies all to user of the end, calculates
     * the score obtained by every player and announce the winner. It then update the statistics for every player
     * displaying them for every user. In the end it clears the board resetting their sockets and removing the players
     * from the board
     * @see GameBoard#endInitialization()
     * @see GameBoard#announceGameScore()
     * @see GameBoard#retrieveGlobalRank()
     */
    public void endGame() {

        gameEnded = true;

        if(!gameStarted) {
            for(GameObserver o: observers) {
                o.sendMessage("It has occurred a problem during match startup");
            }

            closeGame();
            return;
        }

        endInitialization();


        for (GameObserver o : observers) {
                o.sendMessage("THE END");
        }

         announceGameScore();

        //update game statistics
        for (PlayerInGame playerGame : matchPlayers) {
            Player player = playerGame.getPlayer();

            if (matchPlayers.indexOf(playerGame) == 0)
                player.newVictory();
            else
                player.newDefeat();

            player.addPoints(playerGame.getPlayerPoints());
            player.addTime(playerGame.getChronoTime());
        }

        retrieveGlobalRank();

        closeGame();

    }

    /**
     * Prepare the controller and the gameboard for the end game
     */
    private void closeGame() {
        //reset SocketReaders
        resetSocketReaders();

        //remove players from map player->controller
        removePlayers();

        observers.clear();
    }


    /**Removes the players from the board and notifies them that the game is over
     * @see MainModel#removePlayerFromGame(Player)
     * @see GameObserver#onGameEnd()
     */
    protected void removePlayers(){

        //remove players from map player->controller
        for(PlayerInGame p: matchPlayers)
            model.removePlayerFromGame(p.getPlayer());

        for(GameObserver o: observers) {
            o.onGameEnd();
        }
    }


    /**This method stop the Chronometer of every player, cancels the timer, sets the {@link GameBoard#currentPlayer} to null and
     * the {@link PlayerState} to {@link PlayerState#NEUTRAL} because the game is ended and the player input isn't required anymore for the round
     * execution. It also sets the {@link GameBoard#gameEnded} to true.
     */
    protected void endInitialization(){
        matchPlayers.forEach(PlayerInGame::stopChrono);

        System.out.println("THE END (of the match)");
        if(timer != null)
            timer.cancel();

        currentPlayer = null;

        if(currentPlayerState != PlayerState.NEUTRAL) {
            currentPlayerState = PlayerState.NEUTRAL;

        }

    }

    /**This method calculates the gameScore obtained by each player and puts them in a string, to notify the viewers
     * of the obtained scores
     * @see GameBoard#getGameScore()
     */
    private void announceGameScore() {
        getGameScore();

        String rank = "Final rank:\n";
        for(int i=0; i<matchPlayers.size(); i++) {
            rank = rank + " " + (i+1) + ") " + matchPlayers.get(i).getUsername();
            if(matchPlayers.get(i).isSuspended())
                rank = rank + " (inactive)";
            rank = rank + " with: " + matchPlayers.get(i).getPlayerPoints() +" points\n";
        }


        for(GameObserver o: observers) {
            o.sendMessage(rank);
        }
    }


    /**This method retrieves the global rank string sorted by victories and notifies each player with
     * the global rank
     * @see MainModel#getPlayerSortedByVictories()
      */
    private void retrieveGlobalRank() {
        String globalRank = model.getPlayerSortedByVictories();

        for(GameObserver o: observers) {
            o.sendMessage(globalRank);
        }
    }


    /**This method is the getter for the {@link GameBoard#getCurrentPlayer()}
     * @return the {@link GameBoard#getCurrentPlayer()} attribute
     */
    public PlayerInGame getCurrentPlayer() {
        return currentPlayer;
    }

    /**This method is the getter for {@link GameBoard#gameEnded}
     * @return the {@link GameBoard#gameEnded} attribute
     */
    public boolean isGameEnded() {
        return gameEnded;
    }

    /**This method is the getter for {@link GameBoard#gameRoundBoard}
     * @return the {@link GameBoard#gameRoundBoard} attribute
     */
    public RoundBoard getGameRoundBoard(){
        return this.gameRoundBoard;
    }


    /**Adds the given {@link PlayerInGame} to the list of {@link GameBoard#matchPlayers} and increase the value of
     * {@link GameBoard#activePlayers} by one
     * @param p is the given {@link PlayerInGame} that has to be added in the {@link GameBoard}
     * @throws MaxPlayerReachedException if the maximum number of player is reached
     */
    public void addPlayer(PlayerInGame p) throws MaxPlayerReachedException{

        if (matchPlayers.size() == 4) {
                throw new MaxPlayerReachedException("This gameboard is full " + matchPlayers.size());
        }

        matchPlayers.add(p);
        activePlayers++;
    }

    /**Remove the given player if contained in the {@link GameBoard#matchPlayers} list, if not an exceptions is thrown
     * @param p the {@link PlayerInGame} that has to be removed
     * @throws PlayerNotFoundException if the player was not found
     */
    public void removePlayer(PlayerInGame p)throws PlayerNotFoundException{
       int matchPlayerSize = matchPlayers.size();
       matchPlayers.remove(p);
       if(matchPlayers.size() == matchPlayerSize) {
           throw new PlayerNotFoundException("PlayerInGame " + p.getUsername() + " not in this gameboard");
       }

    }

    /**This method starts the {@link com.model.Chronometer} for each player
     */
    public void startChronos() {
        matchPlayers.forEach(PlayerInGame::startChrono);
    }


    /**This method sets the {@link PlayerInGame#suspended} boolean to true and reduce the number of {@link GameBoard#activePlayers}
     * by one, notifying the others player of the suspension of the given player. It also checks how many {@link GameBoard#activePlayers}
     * are left and in the end checks if there are disconnected players
     * @param player the {@link PlayerInGame} that has to be suspended
     */
    private void suspendPlayer(PlayerInGame player) {

        player.suspend();
        activePlayers--;
        System.out.println("Active players: " + activePlayers);



        for(GameObserver o: observers) {
            o.onPlayerSuspension(player);
        }

        checkMinimumActivePlayers();


        checkNotRespondingPlayer();

    }

    /**This method sets the given {@link PlayerInGame} to offline handling the logout process and notifying other players
     * of the given player disconnection. The given {@link PlayerInGame#suspended} is also set to true and the number of
     * {@link GameBoard#activePlayers} reduced by one. In the end a check of remaining active and online players is made
     * and if the player that was set offline was the {@link GameBoard#getCurrentPlayer()} his turn the {@link GameBoard#nextTurn()} is called.
     * If the given player is already set offline the methods return without doing anything
     * @param player the {@link PlayerInGame} that has to be set offline
     */
    public synchronized void setPlayerOffline(PlayerInGame player) {

        if(player.isOffline())
            return;

        player.setOffline();
        model.logout(player);

        //remove the corresponding observers
        player.detachObserver();
        Optional<GameObserver> observerOptional = observers.stream().filter(o -> o.getObservingPlayer().equals(player)).findFirst();
        observers.remove(observerOptional.orElse(null));



        for(GameObserver o: observers) {
            o.onPlayerDisconnection(player);
        }



        player.suspend();
        activePlayers--;
        System.out.println("Active players: " + activePlayers);

       if(checkMinimumActivePlayers()){
           return;
       }

        checkNotRespondingPlayer();

        if(player == currentPlayer)
            nextTurn();

    }

    /**This method checks if the minimum active player condition (only one player left)
     *  is met: in that case it starts the {@link GameBoard#endGame()}
     * and returns true otherwise returns false without doing anything
     * @return true if the minimum active players condition is met, false otherwise
     */
    protected boolean checkMinimumActivePlayers(){
       if (activePlayers == 1) {
                endGame();
                return true;
       }

        return false;
    }

    /**This method increases the number of active players by one
     */
    public void increaseActivePlayers() {
        this.activePlayers++;
    }

    /**This method return the index of the given {@link PlayerInGame} if it is cointained in the {@link GameBoard#matchPlayers}
     * @param player the player whose index has to be found
     * @return -1 if the player is not found or the index of the player in the {@link GameBoard#matchPlayers} if found
     */
    public int getPlayerIndex(PlayerInGame player) {
        return matchPlayers.indexOf(player);
    }

    /**This method notifies the players of the rejoining of a player in the match.
     * @param player player that resume the game
     */
    public void notifyPlayerResume(PlayerInGame player) {
        for(GameObserver o: observers) {
            o.onPlayerResume(player);
        }

        checkNotRespondingPlayer();
    }


    /**Refreshes the stock drawing a number of dice equals the double of the number of player +1
     */
    protected void refreshStock(){
        for (int i = 0; i < (matchPlayers.size() * 2 + 1); i++) {
            try {
                gameStock.insertDice(gameDiceBag.drawDice());    //dice for the new turn
            } catch (NoDiceException e) {
                System.out.println("No dice left in the bag");
            }
        }
    }


    /**This methods manages new round for the board items: it inserts the left dice in the stock in the roundboard increasing it's round
     * by one and refreshes the stock
     */
    private void newRound(){

        insertRoundBoardDice();

        gameRoundBoard.nextRound();

        refreshStock();

    }

    /**Getter of {@link GameBoard#toolCardsOnBoard}
     * @return the list of ToolCards on the board
     */
    public List<ToolCard> getToolCards(){
        return this.toolCardsOnBoard;
    }

    /**Getter of {@link com.model.gameboard.GameBoard#objCardsOnBoard}
     * @return the array of objCards on board
     */
    public ObjCard[] getObjCard(){
        return this.objCardsOnBoard;
    }

    /**Getter of the {@link GameBoard#getStock()}
     * @return the stock on the board
     */
    public Stock getStock(){
        return this.gameStock;
    }

    /**Getter of the {@link GameBoard#gameDiceBag}
     * @return the DiceBag on the board
     */
    public DiceBag getBag(){return this.gameDiceBag;}


    //list of setter used to revert back the state of the gameboard if a toolcard is aborted (due time constraint or player aborting the operation)


    /**Sets the {@link GameBoard#gameDiceBag} with the given one
     * @param bag the DiceBag that has to be set in the gameboard
     */
    public void setBag(DiceBag bag){
        this.gameDiceBag = bag;
    }

    /**Sets the {@link GameBoard#gameStock} with the given one
     * @param stock the stock that has to be set in the gameboard
     */
    public void setStock(Stock stock){
        this.gameStock = stock;
    }

    /**Sets the {@link GameBoard#newRound()} with the given one
     * @param roundBoard the RoundBoard that has to be set in the gameboard
     */
    public void setRoundBoard(RoundBoard roundBoard){
        this.gameRoundBoard = roundBoard;
    }


    /**For every player in {@link GameBoard#matchPlayers} calculates the score in four phases: in the first
     * phase the points gained from the {@link PublicObjCard} are added.
     * In the second phase the points obtained from the {@link com.model.cards.objcard.PrivateObjCard} are added
     * In the third phase the points obtained from the remaining {@link PlayerInGame#favorsToken} are added
     * In the forth and last phase the points lost for every not filled {@link com.model.patterns.DiceSpace} are removed
     * It then proceeds to put the players in a map with their corresponding scores, and sorts the {@link GameBoard#matchPlayers}
     * with the {@link PlayerDescendingComparator}
     */
    private void getGameScore() {

        //where the score will be stored temporally
        int points;

        //for each player adding the score obtained with public obj cards

        for (ObjCard oCard : objCardsOnBoard) {
           for(PlayerInGame p: matchPlayers){
               points = oCard.computeScore(p.getWindow().getSpaces());
               System.out.println("Player " + p.getUsername() + " obtained "+ points + " from" + oCard.toString());
               p.addPoints(points);
           }
        }

        //foreach player adding the score obtained with private obj cards

        for (PlayerInGame p : matchPlayers) {
            ObjCard privateObjCard = p.getPlayerObjCards().get(0);
            points = privateObjCard.computeScore(p.getWindow().getSpaces());
            System.out.println("Player " + p.getUsername() + " obtained " + points + " from" + privateObjCard.toString());
            p.addPoints(points);
        }

        for(PlayerInGame p: matchPlayers){
            points = p.getFavorTokens();
            System.out.println("Player " + p.getUsername() + " obtained " + points + " from his unused favor tokens");
            p.addPoints(points);


        }


        //-1 point for each blank space

        for (PlayerInGame p : matchPlayers) {
            //20 are the spaces in the window - how many dice are on board == blank spaces
            points = 20 - p.getWindow().getDiceOnBoard();
            System.out.println("Removed " + points + " from player " + p.getUsername() + " for his unfilled spaces");
            p.removePoints(points);

        }


        Map<PlayerInGame, Integer> playersOrder = new HashMap<>();
        for(int i=0; i<matchPlayers.size(); i++)
            playersOrder.put(matchPlayers.get(i), i);

        matchPlayers.sort(new PlayerDescendingComparator(playersOrder));


    }


    /**Getter for the {@link GameBoard#matchPlayers}
     * @return the list of players in the board
     */
    public List<PlayerInGame> getMatchPlayers(){
        return matchPlayers;
    }


    /**This method build the string representing the gameboard, personalized for every player
     * @param player the player whose GameBoard string representation is built
     * @return the string representing the GameBoard
     */
    public String getView(PlayerInGame player) {  //create a specific view of the model based on the player
        String view = "\n\n>>>CURRENT STATE<<<\n\n";

        //RoundBoard
        view = view + gameRoundBoard.toString();


        //other players' WindowPatternCard
        for(PlayerInGame p: matchPlayers) {
            view = view + "\n";

            if(!p.equals(player)) {
                view = view + p.getUsername();

                if(p.isSuspended())
                    view = view + " (inactive)";

                view = view + " - Window:\n";
                view = view + p.getWindow().toString();
            }
        }


        //public object cards on the board
        view = view + "\n>>Public object cards:\n";

        for(int i = 0; i< objCardsOnBoard.length; i++) {
            view = view + objCardsOnBoard[i].toString() + "\n";
        }

        //tool cards on the board
        view = view + "\n>>Tool cards:\n";

        for(int i = 0; i< toolCardsOnBoard.size(); i++) {
            view = view + toolCardsOnBoard.get(i).toString() + "\n";
        }

        //dice in the stock
        view = view + "\n>>Dice: \n";
        for(Dice dice: gameStock.getDice()) {
            view = view + dice.toString() + "\t";
        }


        //private object card
        String objCards = new String();
        for(ObjCard privateCard: player.getPlayerObjCards()){
            objCards = objCards + "\n"+privateCard.toString();
        }
        view = view + "\n>>Your private object card:" + objCards;

        //favor tokens
        view = view + "\n>>Favor tokens: " + player.getFavorTokens();

        //player Window
        view = view + "\n>>Your window: \n" + player.getWindow().toString();

        if(currentPlayer != null) {
            if (player == currentPlayer) {
                view = view + "\n\n It's up to you\n" + "What you want to do: pass [p], place a die [d], use a toolcard [t]";
            } else {
                view = view + "\n\nIt's " + currentPlayer.getUsername() + "'s turn";
            }
        }

        return view;
    }


    /**This method adds the given {@link GameObserver} to the {@link GameBoard#observers}
     * @param observer the observer that has to be added in the list
     */
    public void attachObserver(GameObserver observer) {
        observers.add(observer);
    }

    /**Remove the given {@link GameObserver} from the {@link GameBoard#observers}
     * @param observer the observer that has to be removed from the list
     */
    public void detachObserver(GameObserver observer) {
        observers.remove(observer);
    }

    //to call after any model change

    /**This method notifies the view to every player in the new turn.
     */
    private void notifyObserversNewTurn() {
        System.out.println("Notifying views to players");

        for(GameObserver o: observers) {
            o.onNewTurn();
        }
        checkNotRespondingPlayer();
    }

    /**This method adds the given {@link PlayerInGame} to the {@link GameBoard#notRespondingPlayer} list if it is not
     * contained in it
     * @param player the player that has to be added in the list of not responding players
     */
    public void addNotRespondingPlayer(PlayerInGame player) {
        if(!notRespondingPlayer.contains(player))
            notRespondingPlayer.add(player);
    }


    /**This methods checks the {@link GameBoard#notRespondingPlayer} list and removes every{@link PlayerInGame} in this list
     *setting them offline
     */
    public void checkNotRespondingPlayer() {
        for(int i=0; i< notRespondingPlayer.size(); i++) {
            setPlayerOffline(notRespondingPlayer.remove(i));
        }
    }

    /**This method sets the time duration for the tun
     * @param timerTime the integer representing the time in milliseconds of the turn
     */
    public static void setTimerTime(int timerTime) {
        GameBoard.timerTime = timerTime;
    }
}
