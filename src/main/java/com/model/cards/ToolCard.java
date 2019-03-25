package com.model.cards;


import com.model.MoveAbortedException;
import com.model.cards.concretetoolcards.CardNotUsableException;
import com.model.cards.concretetoolcards.ToolCardInputHandler;
import com.model.cards.concretetoolcards.ToolCardState;
import com.model.gameboard.GameBoard;
import com.model.PlayerInGame;
import com.model.dice.*;
import com.model.patterns.DiceSpace;
import com.model.patterns.NoMovableDiceException;

import static com.control.GameControllerImpl.commandChoices;
import static com.Server.errorMessage;


/** Abstract class: it extends the card class and adds several attributes: tokensOnCard (integer that represents the favor
 * tokens on the card), ToolCardState: enum that represents the current state of the ToolCard, PlayerInGame: the player
 * that is using the card, gameBoard: the GameBoard the ToolCard is bound too, useful when applying ToolCard effect
 * ToolCardInputHandler: class that contains all the parsing method for string user input, DiceSpace: the diceSpace where
 * the dice is put in single player mode
 * It implements several abstract methods (activate card for both single and multiplayer) and player command that is used in
 * the specific ToolCard that inherit from this
 * There are some implemented methods too, shared between all the specific ToolCard: pay card method for single player and for multiplayer,
 * the setState method, called during the player command method to set the ToolCard state, various checks method used in the activation
 * phase of the specific ToolCard
 * @see Card
 */


public abstract class ToolCard extends Card {


    /**int representing the favor tokens on the card
     */
    private int tokensOnCard;

    /**State reached by the card
     * @see ToolCardState
     */
    private ToolCardState cardState;
    /**User of the card
     * @see PlayerInGame
     */
    protected PlayerInGame toolCardUser;
    /**GameBoard associated to the card
     * @see GameBoard
     */
    protected GameBoard gameBoard;

    /**Input handler used by the card
     * @see ToolCardInputHandler
     */
    protected ToolCardInputHandler parser;

    /**DiceSpace associated to the card
     */
    protected DiceSpace toolCardSpace;

    public ToolCard(String id, String title, String description){
        super(id,title,description);
        tokensOnCard = 0;
        this.cardState = ToolCardState.NEUTRAL;
        this.parser = new ToolCardInputHandler();
    }


    /** Method used to activate the card in multiplayer, this will be overridden in the specific toolcard: every toolcard has its checks to make
     * before activating the card
     * @param player object representing the player who wants to activate the card
     * @throws CardNotUsableException when a check is not passed (example: player doesn't have enough tokens)
     */
    public abstract void activateCard(PlayerInGame player) throws CardNotUsableException;

    /** Method used to activate the card in single player, this will be overridden in the specific ToolCard: every ToolCArd has its checks to make
     * before activating the card
     * @param player object representing the player who wants to activate the card
     * @param diePayment the dice used to pay the card
     * @throws CardNotUsableException when a check is not passed (example: player dice doesn't match the card {@link DiceSpace#color})
     */
    public abstract void activateCard(PlayerInGame player,Dice diePayment) throws CardNotUsableException;


    /** This method is used to manipulate the string and activate the desired effects depending on the card state.
     * The effects are executed with multiple calls of this method by the game controller to dynamically interact with the
     * user and parses his input differently depending on the card state.
     * In every state the user can abort the ToolCard execution by using "a" as an input
     * @param command the string that represents the user input
     * @see com.control.GameControllerImpl
     */
    public abstract  void playerCommand(String command);

    /**This method is used to bring the context of the ToolCard execution back to its state before the activation. It is
     * activated when the time of the player runs out during the player execution, or the ToolCard is aborted
     */
    public void revert() {
        this.toolCardUser.revertWindow(this.toolCardUser.getWindow().getCache());
        this.gameBoard.setBag(this.gameBoard.getBag().getCache());
        this.gameBoard.setStock(this.gameBoard.getStock().getCache());
        this.gameBoard.setRoundBoard(this.gameBoard.getGameRoundBoard().getCache());
    }

    /**Saves the current state of the toolcard context, used if something goes wrong during toolcard execution (time out, or user
     * abort)
     */
    public void setCaches(){
        this.toolCardUser.getWindow().setCache();
        this.gameBoard.getBag().setCache();
        this.gameBoard.getStock().setCache();
        this.gameBoard.getGameRoundBoard().setCache();
        this.setState(ToolCardState.NEUTRAL,null);
    }


    /**Adds favor token on card
     * @param givenTokens integer that represents the tokens to put on the card, added to the tokensCoard parameter
     */
    public void payCard(int givenTokens){
        tokensOnCard = tokensOnCard + givenTokens;
    }

    int getTokensOncard(){
        return this.tokensOnCard;
    }


    /**Checks if the players has enough tokens to pay the card. If it has enough tokens these tokens are removed from the player
     * and added to the card, otherwise and exception is thrown. The amount of tokens to give for toolcard activation is simply based
     * on the tokensOnCard attributes: if it is greater than 0 then there is a token on the card so its cost is 2 favor tokens
     * If the player has enough tokens the caches are set and the player is set as an user of the toolcard
     * @param player PlayerInGame parameter, the corresponding player object that wants to pay the card
     * @throws CardNotUsableException if  playeringame doens't have enough tokens
     * @see PlayerInGame
     */
    protected void payCard(PlayerInGame player) throws CardNotUsableException{
        int playerFavorTokens = player.getFavorTokens();

        if(this.getTokensOncard() == 0 && playerFavorTokens > 0) { //<---- TOOLCARD NOT YET USED: cost == 1

            player.removeTokens(1);

            System.out.println("player " + player.getUsername() + " has now " + player.getFavorTokens() +" favor tokens");

            this.payCard(1);

        }else if(this.getTokensOncard() > 0 && playerFavorTokens > 1){ //<---- TOOLCARD USED: cost == 2

            player.removeTokens(2);

            System.out.println("player " + player.getUsername() + " has now " + player.getFavorTokens() +" favor tokens");

            this.payCard(2);

        }else{
            player.notifyViewObserver(errorMessage + "You don't have enough favor tokens");

            throw new CardNotUsableException("User doesn't have enough tokens");
        }
        //setting toolcard user
        setPlayer(player);
        setCaches();

        player.notifyViewObserver("Card " + this.getTitle() + " activated");

    }

    /**Returns the diceSpace of the toolcard, used in single player mode
     * @return toolCardSpace, attribute of the toolcard
     */
    public DiceSpace getDiceSpace(){
        return toolCardSpace;
    }


    /**Corresponding paycard method for SinglePlayerMode: it checks if the die given for payment matches the color of the
     * ToolCard space: if it matches then the playerInGame is set as an user, the caches are set too and the diceSpace set
     * the dice otherwise it throws the exception
     * @param player PlayerInGame, this will be set as toolcard user attribute and will be used to notify to the player the errors
     * @param diePayment the die that will be used to pay the card
     * @throws CardNotUsableException  if the die don't match the color of the toolcard diceSpace
     */
    protected void payCard(PlayerInGame player,Dice diePayment)throws CardNotUsableException{
        if(diePayment.getColor() != this.toolCardSpace.getColor()){
            player.notifyViewObserver(errorMessage+ "You selected a die with the wrong color");
            throw new CardNotUsableException("User selected a die with color not matching " + toolCardSpace.getColor());
        }

        toolCardSpace.setDice(diePayment);
        //setting toolcard user
        setPlayer(player);
        setCaches();

        player.notifyViewObserver("Card " + this.getTitle() + " activated");
    }


    /**Set the player attribute for both the toolcard and the input handler class: they both need it to notify the user
     * of changing to the model through their observer, this method is called in the paycard method after the effective
     * payment is done
     * @param player will be put in the helper player parameter and in the ToolCard toolCardUser paramater
     * @see ToolCardInputHandler
     */
    protected void setPlayer(PlayerInGame player){
        this.parser.setPlayer(player);
        this.toolCardUser = player;
    }


    /**Set the gameBoard attribute of the ToolCard, this will be used in the specific ToolCards execution
     * @param gb put in the gameBoard attribute, will be used during specfic ToolCards execution
     */
    public void setGameBoard(GameBoard gb){
        this.gameBoard = gb;
    }

    /** Method used to get the current state of the toolcard, useful when checking if the card is used, aborted or has not
     * completed its execution
     * @return cardState, the current ToolCardState of the ToolCard
     */
    public ToolCardState getState(){
        return this.cardState;
    }


    /** Set the ToolCard state to a new state, and notify the ToolCard user of the change with a personalized message (string
     * parameter: the executed and abort state are special states, the first one refreshes the ToolCard input handler values
     * and has a special case (contains \n) for those card that execute their effects without user input, the second one
     * call the ToolCard revert method to reset the context of the ToolCard execution before the ToolCard execution and
     * reset the input handler too
     * @param nextState the ToolCard next state, useful in playercommand to parse and use the input in the right way
     * @param personalizedMessage the string that will be used to notify the player of the state change, can be null
     *                            if there is no message to send
     * @see ToolCardState
     * @see ToolCardInputHandler
     */
    public void setState(ToolCardState nextState,String personalizedMessage){

        //personalized message is a string used to communicate to the player when there is a particular effect in a state transiction
        //sometimes this message is useless

        if(personalizedMessage != null){
            this.toolCardUser.notifyViewObserver(personalizedMessage);
        }

        this.cardState = nextState;

        switch (nextState) {
            case PICKWINDOWDIE:
                this.toolCardUser.notifyViewObserver("Select the space from where you want to take the die [row,column], [a] to abort");
                break;
            case DIESTOCKPICK:
                this.toolCardUser.notifyViewObserver("Stock: " +gameBoard.getStock().toString());
                this.toolCardUser.notifyViewObserver("Select the die from the stock [Die index], [a] to abort");
                break;
            case PLACEDIE:
                this.toolCardUser.notifyViewObserver(this.toolCardUser.getWindow().toString());
                this.toolCardUser.notifyViewObserver("Select the space coordinates where you want to put the die [row,column] , [a] to abort");
                break;
            case ROUNDBOARDPICK:
                toolCardUser.notifyViewObserver(gameBoard.getGameRoundBoard().toString());
                this.toolCardUser.notifyViewObserver("Select the round where you want to take the die from and the die  in the round selected [round,die index], [a] to abort");
                break;
            case EXECUTED:
                this.parser.flush();
                this.toolCardUser.notifyViewObserver("All done :)");
                this.toolCardUser.setToolCardPlayed();
                //code block used for these cards that execute without user input. In these cases the user must be updated
                //with the commandChoices (usually this update is done after the last input of the user but since for those
                //cards the last input is the one to activate the card this update is necessary)
                //the cards are recognized using the char \n at the end of their personalized message
                if(personalizedMessage != null && personalizedMessage.endsWith("\n")) {
                        this.toolCardUser.notifyViewObserver(this.getTitle()+ " used\n" + commandChoices);
                }
                break;
            case ABORTED:
                this.revert();
                this.toolCardUser.notifyViewObserver(gameBoard.getGameRoundBoard().toString());
                this.toolCardUser.notifyViewObserver("Stock: " + gameBoard.getStock().toString());
                this.toolCardUser.notifyViewObserver(this.toolCardUser.getWindow().toString());
                this.parser.flush();
                this.toolCardUser.setToolCardPlayed();
                break;
        }



    }

    /** Method used to check if the user has already placed a die, in this case those card who need to draft and place a die
     * cannot be executed so the exception is thrown
     * @param player used to check if the user has already placed his die
     * @throws CardNotUsableException if the hasPlayedDice of the player return false (user has already placed his die)
     */
    protected void checkDiePlaced(PlayerInGame player) throws CardNotUsableException{
        if(player.hasPlayedDice()){
            throw new CardNotUsableException(errorMessage+ "Die already placed, you can't play this card, you won't lose your tokens");
        }
    }

    /**Method that checks if the roundBoard is empty or not, used in the toolcards that works with the roundBoard.
     * The roundBoard on which is made the check is obtained through the respective get method of the gameboard
     * @throws CardNotUsableException if the roundBoard has no dice on it (isEmpty)
     * @see com.model.gameboard.RoundBoard
     */

    protected void checkRoundBoardDie() throws CardNotUsableException{
         if(this.gameBoard.getGameRoundBoard().isEmpty()){
//            player.notifyViewObserver(errorMessage+ "No die on the roundboard, you can't use this card, you won't lose your tokens");
            throw  new CardNotUsableException("No die in the roundBoard, aborting....");
        }
    }


    /**Method that checks if there is a movable die, respecting the given conditions, on the player window.
     * @param player , used to get the window
     * @param colorConstraint , boolean, if false removes the color constraint
     * @param  shadeConstraint boolean, if false removes the shade constraint
     * @throws CardNotUsableException if the window has no movable dice
     * @see PlayerInGame
     * @see com.model.patterns.WindowPatternCard
     */
    protected void checkMovableDice(PlayerInGame player, boolean colorConstraint, boolean shadeConstraint)throws CardNotUsableException{
        try {
            player.getWindow().checkMovable(DiceColor.NEUTRAL, colorConstraint, shadeConstraint,null);
        }catch (NoMovableDiceException error){

            player.notifyViewObserver(errorMessage + error.getMessage());

            throw new CardNotUsableException("No movable die, aborting...");
        }
    }


    /**Method used to parse the user input and call the {@link ToolCardInputHandler#placePickedDie(String, boolean, boolean)},
     * it then sets the new state depending on  {@link ToolCardInputHandler#placePickedDie(String, boolean, boolean)} execution:
     * if no exception is thrown the next {@link ToolCardState} is set to {@link ToolCardState#EXECUTED}, if the {@link MoveAbortedException}
     * is throw the new state is set to {@link ToolCardState#ABORTED} otherwise there is an error in the input and the new state
     * is set to {@link ToolCardState#PLACEDIE}
     * @param command is the string representing the user input
     * @param colorRestriction if true the dice must respect color restriction otherwise it can be placed in not color matching dicespace
     * @param shadeRestriction if true the dice must respect shade restriction otherwise it can be placed in not shade matching dicespace
     * @return true if the die is placed, false otherwise. this boolean is used for the toolcards that must set the flag has player dice
     * of the PlayerInGame
     * @see PlayerInGame
     */
    protected boolean placeDie(String command, boolean colorRestriction, boolean shadeRestriction){
        try{
            parser.placePickedDie(command,colorRestriction,shadeRestriction);
            this.setState(ToolCardState.EXECUTED, "Die placed");
            return true;
        }catch (MoveAbortedException error){
            this.setState(ToolCardState.ABORTED,error.getMessage());
            return false;
        }catch(Exception error){
            this.setState(ToolCardState.PLACEDIE, errorMessage + error.getMessage());
            return false;
        }
    }


    /**Method used to parse the user input and call the {@link ToolCardInputHandler#pickStockDie(Stock, String)},
     * it then sets the new state depending on {@link ToolCardInputHandler#pickStockDie(Stock, String)} execution:
     * if no exceptions is thrown the state is not set because the dice is picked and the effect (specific in each toolcard)
     * to it still has to be applied, if {@link MoveAbortedException} is throw the new state is set to {@link ToolCardState#ABORTED}
     * otherwise there is an error in the input and the new state is set to {@link ToolCardState#PLACEDIE}
     * @param command string representing user input
     */
    protected void dieStockPick(String command){
        try {
            //picking the die
            parser.pickStockDie(gameBoard.getStock(), command);
        //user aborted the card
         }catch (MoveAbortedException error){
            this.setState(ToolCardState.ABORTED,error.getMessage());
        //user picked wrong index
        }catch (Exception error){
            this.setState(ToolCardState.DIESTOCKPICK, errorMessage+ error.getMessage());
        }
    }


    /**Method used to parse the user input and call the {@link ToolCardInputHandler#pickWindowDie(String, DiceColor, DiceShade)},
     * it then sets the new state depending on {@link ToolCardInputHandler#pickWindowDie(String, DiceColor, DiceShade)} execution:
     * if no exceptions is thrown the state is set to {@link ToolCardState#PLACEDIE},
     * if {@link MoveAbortedException} is throw the new state is set to {@link ToolCardState#ABORTED}
     * otherwise there is an error in the input and the new state is set to {@link ToolCardState#PLACEDIE}
     * @param command string representing user input
     * @param fixedColor if set to {@link DiceColor#NEUTRAL} every dice can be picked, otherwise only dice with matching color
     *                   can be picked
     * @param fixedShade if set to {@link DiceShade#NEUTRAL} every dice can be picked, otherwise only dice with matching shade
     *                   can be picked
     */

    protected void pickWindowDie(String command, DiceColor fixedColor, DiceShade fixedShade){
        try {
            parser.pickWindowDie(command, fixedColor, fixedShade);
            this.setState(ToolCardState.PLACEDIE, "die selected");
            //user aborted the move
        } catch (MoveAbortedException error) {
            this.setState(ToolCardState.ABORTED, errorMessage + error.getMessage() + " token will not be given back");
            //user inserted input in a wrong way
        }
        catch (Exception error) {
            this.setState(ToolCardState.PICKWINDOWDIE, errorMessage+error.getMessage() + " try again");
        }

    }




    /** To string method of the ToolCard: the format is of kind (I'm ToolCard demoId demoTitle demoDescription toolCardDiceSpaceColor my cost is x favor tokens
     * x is 1 or 2 depending on how many favor tokens there are on the ToolCard (if there is at least 1 then the cost of the toolcard is 2)
     * @return toolCardString, the string representing the ToolCard
     */

    @Override
    public String toString(){
        String toolCardString = "I'm Tool Card " + this.getId() +" " + this.getTitle() +" " +  this.getDescription() + " "+"DiceSpace Color: " +this.toolCardSpace.getColor();
        if(this.tokensOnCard > 0){
            toolCardString = toolCardString + "\t" + "My cost is 2 favor tokens";
        }
        else{
            toolCardString = toolCardString + "\t" + "My cost is 1 favor token";
        }
        return toolCardString;
    }

}
