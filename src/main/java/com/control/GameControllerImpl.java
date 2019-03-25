package com.control;

import com.model.cards.WrongIdException;
import com.model.cards.concretetoolcards.CardNotUsableException;
import com.model.cards.concretetoolcards.ToolCardState;
import com.view.RemoteClient;
import com.model.*;
import com.model.gameboard.GameBoard;
import com.model.gameboard.MaxPlayerReachedException;

import com.model.cards.ObjCard;
import com.model.dice.*;
import com.model.patterns.DieNotPlaceableException;
import com.model.patterns.WindowPatternCard;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;


/**
 * The Game Controller TODO
 */
public class GameControllerImpl extends UnicastRemoteObject implements GameController, Runnable {

    private static int timerTime;

    private final transient String[] tokens;  //players in this match
    //<token, ...>
    private transient Map<String, RemoteClient> rmiPlayers;
    private transient Map<String, Socket> socketPlayers;
    protected transient Map<String, PlayerInGame> playerByToken;

    protected transient GameBoard gameBoard;
    protected transient MainModel model;

    protected int toolcardIndex;  //which toolcard is in use

    protected String startingMessage = "Start game.\nPlayers selecting windows";

    protected int privatePlayerCards;
    protected int toolCardNumber;
    protected int publicObjCardNumber;

    private Map<String, WindowPatternCard[]> selectableWindowsByToken = new HashMap<>();


    private final String windowChoices = "Select a window [w1|w2|w3|w4]";
    /**
     * The general commands available at the begining of a turn.
     */
    public static final String commandChoices = "What you want to do: pass [p], place a die [d], use a toolcard [t]";
    private final String diceChoices = "I need 3 integer [dice,row,col] or [a] to abort";
    protected String toolCardChoices = "Which toolcard do you want? [1|2|3] or [a] to abort";

    protected char whichPrint;


    /**
     * Constructs a GameControllerImpl that will manages a new game with the players identified by the specified tokens.
     * @param tokens the client tokens of the players.
     * @throws RemoteException if rmi connection problem occurred.
     */
    public GameControllerImpl(String[] tokens) throws RemoteException {
        super();
        this.tokens = tokens;
        this.model = MainModel.getModel();
        rmiPlayers = new HashMap<>();
        socketPlayers = new HashMap<>();
        playerByToken = new HashMap<>();


        toolcardIndex = -1;


        // multiplayer parameters
        privatePlayerCards = 1;
        toolCardNumber = 3;

        //3 publicObjCards both for single and multiplayer
        publicObjCardNumber = 3;
    }


    /**
     * Starts a new game managed by this Game Controller.
     */
    @Override
    public void run()  {

        int numPlayers = tokens.length;

        //map player->controller (useful for connection lost)
        for(int i=0; i<numPlayers; i++) {
            Player player = model.getPlayerByToken(tokens[i]);
            model.addPlayerToGame(player, this);
        }


        //find the networking technology
        for(int i=0; i<numPlayers; i++) {
            if(model.containsRemoteClient(tokens[i]))
                rmiPlayers.put(tokens[i], model.removeRemoteClient(tokens[i]));
            else
                socketPlayers.put(tokens[i], model.removeSocket(tokens[i]));
        }


        //instantiate the game
        startGame();


        createPlayers();
        gameBoard.startChronos();


    }


    /**
     * Create and start the match
     */
    private void startGame() {


        //notify all players that the game is starting
        for(String token: tokens){
            if(rmiPlayers.containsKey(token)) {
                RemoteClient client = rmiPlayers.get(token);

                try {
                    client.startGame(this);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            }
            else {
                model.getSocketReader(token).setController(this);
            }

            //different messages based on the controller type

            whichPrint = 'x';
            printMessageToClient(token, startingMessage);
        }


    }


    /**
     * Create the PlayerInGame for each token and extract random windows for them.
     * Also create and attach observers.
     */
    private void createPlayers() {
        gameBoard = new GameBoard(toolCardNumber,publicObjCardNumber);

        Random rand = new Random();

        List<WindowPatternCard> windows = model.getWindows();  //contains only cards with even ID
        List<ObjCard> privateObjCardDeck = new ArrayList<>();

        try {
            privateObjCardDeck = model.getPrivateObjCards();
        } catch (WrongIdException e) {
            System.out.println(e.getMessage());
        }


        for(String token: tokens) {

            initializePlayers(token);
            System.out.println("hello");
            whichPrint = 'p';
            printMessageToClient(token, "Select your window");

            PlayerInGame player = playerByToken.get(token);


            //give the private object cards to players
            pickObjCards(player, privateObjCardDeck);

            WindowPatternCard[] selectableWindows = new WindowPatternCard[2]; // letting the player choose his window here goes the list of drafted windows

            for(int i=0; i<2; i++) {
                int windowIndex = rand.nextInt(windows.size());
                System.out.println("rand window: " + windowIndex);

                selectableWindows[i] = windows.remove(windowIndex);
            }

            //save possible choice for each player
            selectableWindowsByToken.put(token, selectableWindows);

            printWindowsSelectionToClient(token);


            try {
                gameBoard.addPlayer(player); //adding the list of players to the gameBoard
            }catch(MaxPlayerReachedException error){
                System.out.println("The gameBoard is already full ");
            }



            //attach new observers
            if(rmiPlayers.containsKey(token)) {
                RemoteClient client = rmiPlayers.get(token);
                //to communicate from the model to the client
                player.attachObserver(new PlayerObserverRMI(client,player, gameBoard));
                gameBoard.attachObserver(new GameObserverRMI(client, player, gameBoard));

            } else {
                Socket socket = socketPlayers.get(token);
                player.attachObserver(new PlayerObserverSocket(socket,player, gameBoard));
                gameBoard.attachObserver(new GameObserverSocket(token, socket, player, gameBoard));

            }
        }

        //start windows selection timer
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Window selection timeout");
                for(String token: tokens) {
                    setPlayerWindow(token, "w1");
                }

            }
        }, timerTime);
    }

    /**
     * Create a new PlayerInGame for the specified client token.
     * @param token the client's token.
     */
    protected void initializePlayers(String token){
        PlayerInGame player;
        player = new PlayerInGame(model.getPlayerByToken(token), gameBoard);
        playerByToken.put(token, player);
    }


    /**
     * Extract randomly the Object Cards
     * @param player
     */
    private void pickObjCards(PlayerInGame player, List<ObjCard> privateObjCardDeck){

        Random rand = new Random();
        List<ObjCard> playerObjCards = new ArrayList<>();

        for(int i = 0; i < privatePlayerCards; i++ ){
            int objCardIndex = rand.nextInt(privateObjCardDeck.size());

            playerObjCards.add(privateObjCardDeck.remove(objCardIndex));


        }
        //giving to the player his cards
        player.setObjCards(playerObjCards);
    }


    /**
     * Go to next turn.
     */
    private synchronized void nextTurn() {

        toolcardIndex = -1;
        gameBoard.nextTurn();
    }


    /**
     * Se the specified player as offline (not responding).
     * @param player the player to set offline.
     */
    public void setPlayerOffline(Player player) { //when a player is set as offline from outside the game (i.e. connection lost)
        Optional<PlayerInGame> playerOptional = playerByToken.values().stream().filter(p -> p.getUsername().equals(player.getUsername())).findFirst();

        if(playerOptional.isPresent()) {
            PlayerInGame playerGame = playerOptional.get();

            gameBoard.setPlayerOffline(playerGame);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void playerCommand(String token, String command) throws RemoteException {

        PlayerInGame caller = playerByToken.get(token);



        System.out.println(caller.getUsername() +": " + command);
        System.out.println("CurrentPlayerState: " + gameBoard.getCurrentPlayerState());

        if(command.equals("#Logout")) {

            model.logout(token);
            if(!gameBoard.isGameEnded())
                gameBoard.setPlayerOffline(caller);

            return;
        }




        //check if the player was disabled
        if(caller.isSuspended()) {
            caller.removeSuspension();
            gameBoard.increaseActivePlayers();


            if(socketPlayers.containsKey(token)) {
                try {
                    Socket socket = socketPlayers.get(token);
                    PrintWriter out = new PrintWriter(socket.getOutputStream());
                    out.println("@v");
                    out.flush();
                    out.println(gameBoard.getView(caller) + "#v");
                    out.flush();
                } catch (IOException e) {
                    gameBoard.setPlayerOffline(playerByToken.get(token));
                }

            } else {
                whichPrint = 'p';
                printMessageToClient(token, gameBoard.getView(caller));
            }

            return;
        }



        //execute only if the caller is the current player
        if(caller == gameBoard.getCurrentPlayer()) {
            caller.setActive();


            if(gameBoard.getCurrentPlayerState() == PlayerState.NEUTRAL) {

                //pass turn
                if (command.equals("p")) {
                    System.out.println(caller.getUsername() + " wants to pass turn");
                    if (playerByToken.get(token).getSkipTurn()) {
                        whichPrint = 'p';

                        //notifying the view
                        printMessageToClient(token, "Stock: " + gameBoard.getStock().toString());
                        printMessageToClient(token,gameBoard.getCurrentPlayer().getWindow().toString());
                        printMessageToClient(token,"Running Pliers effect activated\n" + diceChoices);


                        gameBoard.setCurrentPlayerState(PlayerState.EXTRATURN);
                    } else {
                        nextTurn();
                    }
                    return;
                }

                //place a dice
                if (command.equals("d")) {
                    System.out.println(caller.getUsername() + " wants to place a dice");

                if(!caller.hasPlayedDice()) {
                    whichPrint = 'p';
                    printMessageToClient(token, diceChoices);
                    gameBoard.setCurrentPlayerState(PlayerState.DICE);
                }
                else {
                    whichPrint = 'p';
                    printMessageToClient(token, "You have already placed your die!\n" + commandChoices);
                }

                    return;
                }

                System.out.println(command);
                //use toolcard
                if (command.equals("t")) {
                    System.out.println(caller.getUsername() + " wants to use a toolcard");

                    if (!caller.hasPlayedToolCard()) {

                        whichPrint = 'p';
                        printMessageToClient(token, toolCardChoices);
                        gameBoard.setCurrentPlayerState(PlayerState.TOOLCARD);
                    } else {
                        whichPrint = 'p';
                        printMessageToClient(token, "You have already use a toolcard!\n" + commandChoices);
                    }

                    return;
                }

            }

            //the player is placing a dice
            if(gameBoard.getCurrentPlayerState() == PlayerState.DICE || gameBoard.getCurrentPlayerState() == PlayerState.EXTRATURN) {

                placeDice(token, command);
                return;
            }

            //the player is using a toolcard
            if(gameBoard.getCurrentPlayerState() == PlayerState.TOOLCARD) {
                useToolCard(token, command);
                return;
            }

            if(gameBoard.getCurrentPlayerState() == PlayerState.TOOLCARDEXECUTION){
                gameBoard.getToolCards().get(toolcardIndex).playerCommand(command);

                //checking if the player has finished executing his card
                if(gameBoard.getToolCards().get(toolcardIndex).getState() == ToolCardState.EXECUTED || gameBoard.getToolCards().get(toolcardIndex).getState() == ToolCardState.ABORTED){
                    gameBoard.setCurrentPlayerState(PlayerState.NEUTRAL);

                    if(gameBoard.getToolCards().get(toolcardIndex).getState() == ToolCardState.EXECUTED) {
                        gameBoard.getToolCards().get(toolcardIndex).setState(ToolCardState.NEUTRAL, "ToolCard " + gameBoard.getToolCards().get(toolcardIndex).getTitle() + " used");
                    }else{
                        gameBoard.getToolCards().get(toolcardIndex).setState(ToolCardState.NEUTRAL,"Abort completed, your new tokens " + caller.getFavorTokens());

                    }
                    whichPrint = 'p';
                    printMessageToClient(token, commandChoices);


                }
                return;
            }



            System.out.println("Wrong command");
            whichPrint = 'p';
            printMessageToClient(token, "Wrong command\n" + commandChoices);
        }
        else {




            //window selection

            if((caller.getWindow() == null)) {

                setPlayerWindow(token, command);

                return;
            }


            System.out.println("not player's turn");
            whichPrint = 'p';
            printMessageToClient(token, "nope, wait your turn");
        }




    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void playerReenters(String token, String username, RemoteClient client) throws RemoteException {
        if(token == null || username == null)
            return;

        Optional<PlayerInGame> playerOptional = playerByToken.values().stream().filter(p -> p.getUsername().equals(username)).findFirst();

        if(!playerOptional.isPresent())
            return;

        PlayerInGame player = playerOptional.get();

        if(!player.isOffline())
            return;

        //set as online and notify other players
        player.setOnline();
        gameBoard.increaseActivePlayers();
        gameBoard.notifyPlayerResume(player);


        //replace player's token and mappings
        int index = gameBoard.getPlayerIndex(player);
        rmiPlayers.remove(tokens[index]);
        playerByToken.remove(tokens[index]);

        tokens[index] = token;
        rmiPlayers.put(token, client);
        playerByToken.put(token, player);

        //create new observers (the old were removed)
        player.attachObserver(new PlayerObserverRMI(client, player, gameBoard));
        gameBoard.attachObserver(new GameObserverRMI(client, player, gameBoard));



        //get the current view of the model
        whichPrint = 'k';
        printMessageToClient(token, gameBoard.getView(player));
    }

    /**
     * Reenter a socket player, identified by its client token and username, in the match if it was offline.
     * Store the new Socket associated to the player for future communications.
     * @param token the client's token.
     * @param username the player's username.
     * @param socket the new socket.
     */
    void playerReenters(String token, String username, Socket socket) {
        if(token == null || username == null)
            return;

        Optional<PlayerInGame> playerOptional = playerByToken.values().stream().filter(p -> p.getUsername().equals(username)).findFirst();

        if(!playerOptional.isPresent())
            return;

        PlayerInGame player = playerOptional.get();

        if(!player.isOffline())
            return;


        //set as online and notify other players
        player.setOnline();
        gameBoard.increaseActivePlayers();
        gameBoard.notifyPlayerResume(player);

        //replace player's token and mappings
        int index = gameBoard.getPlayerIndex(player);
        socketPlayers.remove(tokens[index]);
        playerByToken.remove(tokens[index]);

        tokens[index] = token;
        socketPlayers.put(token, socket);
        playerByToken.put(token, player);

        //create new observers (the old were previously removed)
        player.attachObserver(new PlayerObserverSocket(socket, player, gameBoard));
        gameBoard.attachObserver(new GameObserverSocket(token, socket, player, gameBoard));


        //get the current view of the model
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            out.println("@v");
            out.flush();
            out.println(gameBoard.getView(player) + "#v");
            out.flush();
        } catch (IOException e) {
            gameBoard.setPlayerOffline(playerByToken.get(token));
        }
    }

    /**
     * Manages a placing of a dice parsing the string command, requested by the player identified by the specified token.
     * @param token the client's token.
     * @param command the player's command.
     */
    private void placeDice(String token, String command) {

        if(command.equals("a")) {
            if(gameBoard.getCurrentPlayerState() == PlayerState.EXTRATURN){
                whichPrint = 'p';
                printMessageToClient(token,"Second die move aborted, you will skip the next turn");
                nextTurn();
            }
            else {
                gameBoard.setCurrentPlayerState(PlayerState.NEUTRAL);
                whichPrint = 'p';
                printMessageToClient(token, commandChoices);

            }
            return;
        }

        if(command.length() != 5) {
            whichPrint = 'p';
            printMessageToClient(token, "Wrong input!\n"+ diceChoices);
            return;
        }


        int diceNum, row, col;

        try {
            diceNum = Integer.parseInt(command.substring(0,1));
            row = Integer.parseInt(command.substring(2,3));
            col = Integer.parseInt(command.substring(4));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            whichPrint = 'p';
            printMessageToClient(token, "Wrong input!\n"+ diceChoices);
            return;
        }


        Dice selectedDice = null;
        try {
            selectedDice = gameBoard.getStock().getDice(diceNum-1);
        } catch (NoDiceException e) {
            whichPrint = 'p';
            printMessageToClient(token, "This dice doesn't exist!\n"+ diceChoices);
            return;
        }

        try {
            gameBoard.getCurrentPlayer().getWindow().placeDice(row-1, col-1, selectedDice, true, true);


            //remove dice from stock
            try {
                gameBoard.getStock().removeDice(diceNum-1);
            } catch (NoDiceException e) {
                System.out.println(e.getMessage());
            }
            updateStock(token);
            updateWindow(token);

            if(gameBoard.getCurrentPlayerState() != PlayerState.EXTRATURN) {
                whichPrint = 'p';
                printMessageToClient(token, "Dice placed\n" + commandChoices);
                gameBoard.getCurrentPlayer().setDicePlayed();
                gameBoard.setCurrentPlayerState(PlayerState.NEUTRAL);
            }
            else{
                whichPrint = 'p';
                printMessageToClient(token, "Dice placed, you will skip your second turn");
                nextTurn();
            }



        } catch (DieNotPlaceableException e) {
            e.printStackTrace();
            whichPrint = 'p';
            printMessageToClient(token, "You can't: " + e.getMessage());

            if(gameBoard.getCurrentPlayerState() != PlayerState.EXTRATURN) {
                whichPrint = 'p';
                printMessageToClient(token, diceChoices);
            }else{
                whichPrint = 'p';
                printMessageToClient(token,"try again\n" + diceChoices);
            }
        }



    }

    /**
     * Manages a tool card use parsing the string command, requested by the player identified by the specified token.
     * @param token the client's token.
     * @param command the player's command.
     */
    protected void useToolCard(String token, String command) {
            if (command.length() != 1) {
                whichPrint = 'p';
                printMessageToClient(token, "Wrong input\n" + toolCardChoices);
                return;
            }

            PlayerInGame player = playerByToken.get(token);

            if (command.equals("a")) {
                gameBoard.setCurrentPlayerState(PlayerState.NEUTRAL);
                whichPrint = 'p';
                printMessageToClient(token, commandChoices);
                return;
            } else {
                int cardIndex;
                try {
                    cardIndex = Integer.parseInt(command) - 1;
                } catch (NumberFormatException error) {
                    whichPrint = 'p';
                    printMessageToClient(token, "please insert a number" + "\n" + commandChoices);
                    gameBoard.setCurrentPlayerState(PlayerState.NEUTRAL);
                    return;
                }
                if (cardIndex >= 0 && cardIndex < gameBoard.getToolCards().size()) {
                    try {
                        gameBoard.getToolCards().get(cardIndex).activateCard(player);
                        this.toolcardIndex = cardIndex;
                        if(gameBoard.getToolCards().get(cardIndex).getState() != ToolCardState.EXECUTED){
                            gameBoard.setCurrentPlayerState(PlayerState.TOOLCARDEXECUTION);
                        }else{
                            gameBoard.setCurrentPlayerState(PlayerState.NEUTRAL);
                        }
                    } catch (CardNotUsableException error) {
                        whichPrint = 'p';
                        printMessageToClient(token, error.getMessage() + "\n" + commandChoices);
                        gameBoard.setCurrentPlayerState(PlayerState.NEUTRAL);
                    }
                } else {
                    whichPrint = 'p';
                    printMessageToClient(token, "Wrong input\n" + toolCardChoices);
                }
            }
    }

    /**
     * Set the window indicated in the string command to the player identified by the client's token.
     * @param token the client's token.
     * @param command the player's command.
     */
    private synchronized void setPlayerWindow(String token, String command) {

        if(playerByToken.get(token).getWindow() != null || gameBoard.isGameEnded())
            return;


        if(command.length() == 2 && command.charAt(0) == 'w') {

            try {

                int choice = Integer.parseInt(command.substring(1));

                if (choice >= 1 && choice <= 4) {

                    PlayerInGame player = playerByToken.get(token);

                    WindowPatternCard[] choices = selectableWindowsByToken.remove(token);

                    choice--;
                    if (choice % 2 == 0)
                        player.setWindow(choices[choice / 2]);
                    else
                        player.setWindow(choices[choice / 2].getPairedWindow());

                    whichPrint = 'p';
                    printMessageToClient(token, "Window selected");

                    //check if all players have chosen their window
                    if(playerByToken.values().stream().allMatch(p -> p.getWindow() != null)) {
                        nextTurn();
                    }

                    return;

                }

            } catch (NumberFormatException e) {
                whichPrint = 'p';
                printMessageToClient(token, "wrong input!\n" + windowChoices);
            }
        }

        whichPrint = 'p';
        printMessageToClient(token, "wrong input!\n" + windowChoices);

    }


    /**
     * Print a generic message to the client identified by the specific token.
     * Manages both RMI and Socket connections.
     * @param token the client's token.
     * @param message the message.
     */
    protected void printMessageToClient(String token, String message) {

        if(playerByToken.get(token) != null && playerByToken.get(token).isOffline())
            return;

        //try .. connectionRefused

        if(rmiPlayers.containsKey(token)) {
            RemoteClient client = rmiPlayers.get(token);

            try {
                if (whichPrint=='w') {
                    client.printWindowChoices(message);
                }
                else if (whichPrint=='u') {
                    client.updateWindow(message);
                }
                else if (whichPrint=='s') {
                    client.updateStock(message);
                }
                else if (whichPrint=='p') {
                    client.printMessage(message);
                }
                else if (whichPrint=='x') {
                    client.showWindowsScene(message);
                }
                else if (whichPrint=='v') {
                    client.updateView(message);
                }
                else if (whichPrint=='k') {
                    client.printOnReentering(message);
                }
                else {
                    client.printMessage("Whichprint value unexpected: " + whichPrint);
                    client.printMessage(message);
                }
            } catch (RemoteException e) {
                gameBoard.setPlayerOffline(playerByToken.get(token));
            }
        } else {
            Socket socket = socketPlayers.get(token);

            try {
                PrintWriter out = new PrintWriter(socket.getOutputStream());
                out.println("@p");
                out.flush();
                out.println(message + "#p");
                out.flush();
            } catch (IOException e) {
                gameBoard.setPlayerOffline(playerByToken.get(token));
            }
        }
    }


    /**
     * Print the windows among which the player, identified by the token, must choose.
     * @param token the client's token.
     */
    private void printWindowsSelectionToClient(String token) {

        WindowPatternCard[] selectableWindows = selectableWindowsByToken.get(token);

        if(selectableWindows == null)
            return;

        //create window selection string
        String windowSelection = "\nHere are the list of selectable window";

        for(int i=0; i<4; i +=2) {
            windowSelection = windowSelection + "\nChoice n° " + (i+1) + "\n" + selectableWindows[i/2].toString() + "\nChoice n° " + (i+2)
                    + "\n" + selectableWindows[i/2].getPairedWindow().toString();
        }

        windowSelection = windowSelection + "\n" + windowChoices;

        if(rmiPlayers.containsKey(token)) {
            whichPrint = 'w';
            printMessageToClient(token, windowSelection);
        } else {
            Socket socket = socketPlayers.get(token);

            try {
                PrintWriter out = new PrintWriter(socket.getOutputStream());
                out.println("@w" + windowSelection + "#w");
                out.flush();
            } catch (IOException e) {
                gameBoard.setPlayerOffline(playerByToken.get(token));
            }
        }
    }


    /**
     * Send to the player, identified by the token, its updated window.
     * @param token the client's token.
     */
    private void updateWindow(String token) {
        String window = playerByToken.get(token).getWindow().toString();

        if(rmiPlayers.containsKey(token)) {
            whichPrint = 'u';
            printMessageToClient(token, window);
        } else {
            Socket socket = socketPlayers.get(token);

            try {
                PrintWriter out = new PrintWriter(socket.getOutputStream());
                out.println("@u\n" + window + "#u");
                out.flush();
            } catch (IOException e) {
                gameBoard.setPlayerOffline(playerByToken.get(token));
            }
        }
    }

    /**
     * Send to the player, identified by the token, the updated dice stock.
     * @param token the client's token.
     */
    private void updateStock(String token) {
        String stock = ">>Dice:\n" + gameBoard.getStock().toString();

        if(rmiPlayers.containsKey(token)) {
            whichPrint = 's';
            printMessageToClient(token, stock);
        } else {
            Socket socket = socketPlayers.get(token);

            try {
                PrintWriter out = new PrintWriter(socket.getOutputStream());
                out.println("@s" + stock + "#s");
                out.flush();
            } catch (IOException e) {
                gameBoard.setPlayerOffline(playerByToken.get(token));
            }
        }
    }


    /**
     * Use the specified time (in milliseconds) for windows selection timer.
     * @param timerTime the time in milliseconds.
     */
    public static void setTimerTime(int timerTime) {
        GameControllerImpl.timerTime = timerTime;
    }


}
















