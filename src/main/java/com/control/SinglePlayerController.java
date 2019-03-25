package com.control;

import com.model.PlayerInGame;
import com.model.cards.concretetoolcards.ToolCardState;
import com.model.dice.Dice;
import com.model.dice.NoDiceException;
import com.model.gameboard.SoloGameBoard;

import java.rmi.RemoteException;
import java.util.Timer;
import java.util.TimerTask;

/**This is the controller for solo game mode: it differs from the {@link GameControllerImpl} for the initialization phase and the
 * end phase plus the ToolCard choosing phase.
 */



public class SinglePlayerController extends GameControllerImpl {

    /**game parameter for the minimum number of ToolCards */
    public static final int MINCARDS = 1;

    /**game parameter for the maximum number of ToolCards */
    public static final int MAXCARDS = 5;

    /**Object used to wait during the player game difficulty selection, when the gameboard
     * can't be initialized because it needs the number of ToolCards to put in it
     */
    private final Object lock ;

    /** final String used to communicate the possible choices for difficulty selection*/
    private final String difficultyChoices = "Select how many toolcards you want [1|2|3|4|5]";


    /**{@inheritDoc}
     * The constructor sets the parameters for the solo game mode, including the different strings. The toolCardNumber
     * is temporally set to -1, it will be then changed when the player has selected the game difficulty
     * @param tokens array containing the tokens of the player
     * @throws RemoteException when the user is not reachable
     */
    public SinglePlayerController(String[] tokens) throws RemoteException{
        super(tokens);
        toolCardNumber = -1;
        privatePlayerCards = 2;
        lock = new Object();
        startingMessage = "Game started";
        toolCardChoices = "Select the dice from the stock and the ToolCard you want: [DieIndex,ToolCardIndex] or [a] to abort";
    }




    //player have to choose how many cards he wants in single player
    //and have a limited time for his choice, if he doesn't make it in time
    //difficulty hard (1 toolcard) is automatically picked

    /**This initialize the {@link PlayerInGame}, start a new {@link TimerTask} to automatically select the game difficulty
     * and waits for the toolCardNumber to be set (either by the {@link TimerTask} or the player. Once the toolCardNumber
     * is set it then proceeds to initialize the {@link SoloGameBoard}
     * @param token player corresponding string identifier
     */
    @Override
    protected void initializePlayers(String token){

        PlayerInGame player;
        player = new PlayerInGame(model.getPlayerByToken(token), null);
        playerByToken.put(token, player);

        whichPrint = 'p';
        printMessageToClient(token, difficultyChoices);


        //start difficulty selection timer
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                    if(!(toolCardNumber>=MINCARDS && toolCardNumber<= MAXCARDS)){
                        whichPrint = 'p';
                        printMessageToClient(token,"Timer is out, you will have only one toolcard");
                        parseDifficulty(token, "1");
                    }
                    System.out.println("Tool Card difficulty selection timer expired");
            }
        }, 10000);


        //waiting for toolcardnumber to be set, awoken by parse difficulty
        synchronized (lock){
            try {
                while(!(toolCardNumber>=MINCARDS && toolCardNumber<=MAXCARDS)){
                    lock.wait();
                }
            }catch (InterruptedException error){
                System.out.println(error.getMessage());
                whichPrint = 'p';
                printMessageToClient(token, "Something went wrong while selecting the difficulty");
                Thread.currentThread().interrupt();
            }
        }

        //with toolcardnumber set now the game can create the gameboard
        gameBoard = new SoloGameBoard(toolCardNumber,publicObjCardNumber);

        player.setGameBoard(gameBoard);
    }

    //parses the input when the player select the difficulty

    /**This method parses the given string to obtain the integer value ,set the toolCardNumber and notify the lock.
     * If the the toolCardNumber is already set it returns without doing anything
     * @param token user string identifier
     * @param command user input
     */
    private  void parseDifficulty(String token,String command){


        if(toolCardNumber != -1)
            return;
        try {
            int choice = Integer.parseInt(command);

            if (choice >= MINCARDS && choice <= MAXCARDS) {

                whichPrint = 'p';
                printMessageToClient(token, "You will have "+ choice+
                        " at your service, remember you can only use them once");


                //awakening the initializeplayers method, now it can procede to create the gameboard cause it knows
                //how many toolCard there are in the game
                toolCardNumber = choice;

                synchronized (lock){
                    lock.notifyAll();
                }


        }else{
                whichPrint = 'p';
                printMessageToClient(token, "wrong input!\n" + difficultyChoices);
        }

        } catch (NumberFormatException e) {
            e.printStackTrace();
            whichPrint = 'p';
            printMessageToClient(token, "wrong input!\n" + difficultyChoices);
        }
    }

    /**{@inheritDoc}
     * Adds two options to the player command method: the first is used to parse the input in the single player
     * initialization phase,the second to parse the input to select the private card to use at the end of the game
     * @param token user string identifier
     * @param command user input
     * @throws RemoteException when the user is not reachable
     */
    @Override
    public void playerCommand(String token, String command)throws RemoteException{

       //ToolCard Number still has to be initialized
       if(!(toolCardNumber >= MINCARDS && toolCardNumber<=MAXCARDS)){
                parseDifficulty(token,command);
                return;
       }

       //state in which the player choose the private obj card (only single player)
       if (gameBoard.getCurrentPlayerState() == PlayerState.SELECTPRIVATEOBJCARD) {
                    setPrivateCardIndex(token, command);
                    return;
       }

       super.playerCommand(token,command);

    }


    //toolcard activation is slightly different in single player
    //users has to pay the card with a die, that's why the method
    //use toolcard is overriden: in this method the command is parsed
    //to get the toolcard index AND the die index

    /**ToolCard activation for single player mode: it parses the input, if it is an "a" the the method returns without
     * doing anything but notifying the user with the command choice, in the other cases it parses the input to get the two
     * integers it needs to select a die from the stock and activate the card
     * @param token user string identifier
     * @param command user string input
     */
    @Override
    protected void useToolCard(String token,String command){
        int dieIndex;

        //user decided to abort toolcard payment and selection
        if (command.equals("a")) {
            gameBoard.setCurrentPlayerState(PlayerState.NEUTRAL);
            whichPrint = 'p';
            printMessageToClient(token, commandChoices);
            return;

        }

        //wrong command size
        PlayerInGame player = playerByToken.get(token);
        if (command.length() != 3) {
            whichPrint = 'p';
            printMessageToClient(token, "Wrong input\n" + toolCardChoices);
            return;

        }else {

            Dice selectedDie;
            int cardIndex;

            //parsing
            try {
                String[] tokens = command.split(",");
                dieIndex = Integer.parseInt(tokens[0]) -1;
                cardIndex = Integer.parseInt(tokens[1]) - 1;
            } catch (NumberFormatException error) {
                whichPrint = 'p';
                printMessageToClient(token, "please insert a number" + "\n" + toolCardChoices);
//                gameBoard.setCurrentPlayerState(PlayerState.NEUTRAL);
                return;
            }

            //using this integer only for readability purpose
            //can't use the toolcardNumber attribute because the number
            //of toolcards changes during the game
            int diceNumber = gameBoard.getStock().getDice().size();
            int cardNumber = gameBoard.getToolCards().size();

            //index are contained in their respective list
            if (cardIndex >= 0 && cardIndex < cardNumber && dieIndex>= 0 && dieIndex< diceNumber) {

                //getting the dice, notifying an error if die doesn't exists (this shouldn't happen though)
                try{
                    selectedDie = gameBoard.getStock().getDice(dieIndex);
                } catch (NoDiceException error) {
                    whichPrint = 'p';
                    printMessageToClient(token, error.getMessage() + "\n" + toolCardChoices);
//                    gameBoard.setCurrentPlayerState(PlayerState.NEUTRAL);
                    return;
                }

                try {

                    //removing die from the stock and activating the card
                    gameBoard.getStock().removeDice(dieIndex);
                    printMessageToClient(token,"Stock: " + gameBoard.getStock().toString());
                    gameBoard.getToolCards().get(cardIndex).activateCard(player,selectedDie);

                   //useful for those cards that execute without user input
                    //so player state must be set to neutral (to select the next move)
                    if(gameBoard.getToolCards().get(cardIndex).getState() != ToolCardState.EXECUTED){
                        gameBoard.setCurrentPlayerState(PlayerState.TOOLCARDEXECUTION);
                    }else{
                        gameBoard.setCurrentPlayerState(PlayerState.NEUTRAL);
                    }

                    whichPrint = 'p';
                    this.toolcardIndex = cardIndex;

                    //no dice was removed
                }catch (NoDiceException error){
                    whichPrint = 'p';
                    printMessageToClient(token, error.getMessage() + "\n" + commandChoices);
                    gameBoard.setCurrentPlayerState(PlayerState.NEUTRAL);


                } catch (Exception error) {
                    whichPrint = 'p';
                    gameBoard.getStock().insertDice(selectedDie);
                    printMessageToClient(token,"Stock: " + gameBoard.getStock().toString());
                    printMessageToClient(token, error.getMessage() + "\n" + commandChoices);
                    gameBoard.setCurrentPlayerState(PlayerState.NEUTRAL);
                }

            //wrong card index
            } else {
                    whichPrint = 'p';
                    printMessageToClient(token, "Wrong input\n" + toolCardChoices);
            }
        }
    }


    /**This method parses the string to get an integer, if the integer is included from 0 to the {@link com.model.cards.objcard.PrivateObjCard}
     * List of the player then the {@link PlayerInGame#setPrivateCardIndex(int)} is called to set the index of the card selected
     * otherwise it just notify the user that the given string syntax was wrong
     * @param token user string identifier
     * @param command user string input
     */
    //method for user private card selection before calculating score
    private void setPrivateCardIndex(String token,String command){


        if (command.length() != 1) {
            whichPrint = 'p';
            printMessageToClient(token, "Wrong input, select the card index pls");
            return;
        }

        PlayerInGame player = playerByToken.get(token);


        int cardIndex;

        //selected card index parsing
        try {
            cardIndex = Integer.parseInt(command) - 1;
        } catch (NumberFormatException error) {
            whichPrint = 'p';
            printMessageToClient(token, "please insert a number");
            return;
        }

        if (cardIndex >= 0 && cardIndex < player.getPlayerObjCards().size()) {
            player.setPrivateCardIndex(cardIndex);
            System.out.println(player.getUsername()+"selected index " +cardIndex);
            gameBoard.endGame();

        }else{
            whichPrint = 'p';
            printMessageToClient(token,"Wrong Index, try again");
            return;
        }
    }
}
