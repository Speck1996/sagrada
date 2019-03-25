package com.model;

import com.control.SinglePlayerController;
import com.view.RemoteClient;
import com.control.GameControllerImpl;
import com.control.SocketReader;
import com.model.cards.*;
import com.model.dice.Dice;
import com.model.dice.DiceColor;
import com.model.dice.DiceShade;
import com.model.patterns.WindowPatternCard;
import com.model.patterns.WindowPatternCardLoader;

import java.io.*;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The Main Model of Sagrada game.
 * It is a singleton that is instantiated on server startup.
 * TODO server console
 */
public class MainModel {

    //singleton

    private static MainModel instance;

    /**
     * Constructs the main model.
     * It loads all the game assets, starts pinging rmi players and enable server's console.
     */
    private MainModel() {
        this.loadModel();


        //periodically ping rmi players
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                for(RemoteClient cli: playerByRemoteClient.keySet()){
                    try {
                        cli.ping();
                    } catch (RemoteException e) {
                        Player player = playerByRemoteClient.get(cli);
                        System.out.println(player.getUsername() + "offline (ping)" );

                        if(playerByController.containsKey(player)) {
                            playerByController.get(player).setPlayerOffline(player);
                        }
                        else {
                            String tokenToRemove = null;

                            for (String token : playerByToken.keySet()) {
                                if (playerByToken.get(token).equals(player))
                                    tokenToRemove = token;
                            }

                            logout(tokenToRemove);
                        }
                    }
                }

            }
        }, 1000L * 60, 1000L * 60);


        //server console
        new Thread(() -> {
                Scanner in = new Scanner(System.in);

                while (true) {
                    String s = in.nextLine();
                    console(s);
                }
            }).start();
    }

    /**
     * Returns the reference to the Main Model.
     * @return the reference to the Main Model.
     */
    public static MainModel getModel()  {
        if(instance == null)
            instance = new MainModel();

        return instance;
    }



    //shared game components
    private final CardLoader modelCardLoader = new CardLoader();

    private final WindowPatternCardLoader wpcLoader = new WindowPatternCardLoader();
    private final List<Dice> gameDice = new ArrayList<>();




    //implementation
    private static int timerTime;
    private final int MAX_USERNAME_LENGTH = 16;
    private final String mainMenuChoices = "What do you want to do? Multiplayer game [mg], singleplayer game [sg], see game's statistics [gs]";
    private final String endGameChoices = "What do you want to do now? Search a new multiplayer game [g], start a new solo game [s], back to main menu [m]";

    private final static String credentialsFilePath = "configfiles/mainmodel/passwords.txt";
    private final Map<String, String> passwordByUsername = new HashMap<>();  //<username, password>

    private final Map<String, Player> playerByUsername = new HashMap<>();   //all players
    private final Map<String, Player> playerByToken = new HashMap<>();      //logged players
    private final Map<Player, GameControllerImpl> playerByController = new HashMap<>();  //players in a match
    private final Map<RemoteClient, Player> playerByRemoteClient = new HashMap<>();  //useful for ping

    //Queue of players waiting for a game
    //<token>
    private final Queue<String> playersQueue = new ConcurrentLinkedQueue<>();   //players waiting for a match
    private final int MAX_PLAYERS = 4;
    private final int MIN_PLAYERS = 2;
    private Timer timer;     //lobby queue timer

    //players connection
    //<token, ...>
    private final Map<String, RemoteClient> rmiPlayers = new HashMap<>();
    private final Map<String, Socket> socketPlayers = new HashMap<>();
    private final Map<String, SocketReader> socketReaderByToken = new HashMap<>();



    private final static String statsFilePath =  "configfiles/mainmodel/stats.ser";
    private List<Player> statistics = new ArrayList<>();


    /**
     * Try to login a client with the specified username and password.
     * @param username the client username.
     * @param password the client password.
     * @return the client token.
     * @throws LoginException if there is a problem with login (username too long, user already logged or wrong password).
     */
    public synchronized String login(String username, String password) throws LoginException {
        if(username.length() > MAX_USERNAME_LENGTH)
            throw new LoginException("username too long (max " + MAX_USERNAME_LENGTH + ")");

        if(playerByToken.values().stream().map(Player::getUsername).anyMatch(u -> u.equals(username))) {
            throw new LoginException("user already logged");
        }

        passwordByUsername.putIfAbsent(username, password);

        if(passwordByUsername.get(username).equals(password)) {

            String token = UUID.randomUUID().toString();

            Player player = playerByUsername.get(username);

            if(player == null) {
                player = new Player(username);
                playerByUsername.put(username, player);
                statistics.add(player);
            }

            playerByToken.put(token, player);

            return token;
        } else
            throw new LoginException("wrong password");


    }

    public synchronized Player getPlayerByUsername(String username) {
        return playerByUsername.get(username);
    }

    /**
     * retrieve a player by its token.
     * @param token the player's token.
     * @return the player corresponding to the token, or null if absent.
     */
    public synchronized Player getPlayerByToken(String token) {
        return playerByToken.get(token);
    }

    /**
     * Logout a player by its token
     * @param token the player's token.
     */
    public synchronized void logout(String token) {

        if(playerByToken.containsKey(token)) {

            //remove from playerByRemoteClient (if exists)
            Player p = playerByToken.get(token);
            for(RemoteClient cli: playerByRemoteClient.keySet()) {
                if(playerByRemoteClient.get(cli) == p) {
                    playerByRemoteClient.remove(cli);
                    break;
                }
            }






            if (playersQueue.contains(token)) {
                playersQueue.remove(token);
                printLobby();

                if (playersQueue.size() < MIN_PLAYERS && timer != null) {
                    timer.cancel();
                    notifyLobbyPlayers("Timer reset");
                }
            }


            rmiPlayers.remove(token);
            socketPlayers.remove(token);
            if (socketReaderByToken.containsKey(token)) {
                socketReaderByToken.remove(token).interrupt();
            }


            System.out.println(playerByToken.get(token).getUsername() + " logout");
            playerByToken.remove(token);
        } else
            System.out.println("Player already logged out");

    }

    /**
     * Logout a player by its PlayerInGame.
     * This method is successful if and only if the player is set as offline.
     * @param player the player to logout.
     */
    public synchronized void logout(PlayerInGame player) {

        if(player.isOffline()) {
            String tokenToRemove = null;

            for (String token : playerByToken.keySet()) {
                if (playerByToken.get(token).equals(player.getPlayer()))
                    tokenToRemove = token;
            }

            logout(tokenToRemove);
        }
    }


    /**
     * Returns the main menu choices for a client.
     * @return main menu choices.
     */
    public String getMainMenuChoices() {
        return mainMenuChoices;
    }

    /**
     * Returns the end game choices for a client.
     * @return end game choices.
     */
    public String getEndGameChoices() {
        return endGameChoices;
    }


    /**
     * Add a new SocketReader mapped to the specified token.
     * @param token the token to use as key.
     * @param socketReader the SocketReader to be mapped.
     */
    public synchronized void addSocketReader(String token, SocketReader socketReader) {
        socketReaderByToken.put(token, socketReader);
    }

    /**
     * Add a new RemoteCli.
     * @param username the username of the player.
     * @param client the RemoteCli of the client.
     */
    public void addRemoteClient(String username, RemoteClient client) {
        Player player = playerByUsername.get(username);
        playerByRemoteClient.put(client, player);
    }


    /**
     * Add a RMI player to the queue for a multiplayer match.
     * @param token the token of the client.
     * @param client the RemoteCli of the client.
     */
    public synchronized void enqueuePlayer(String token, RemoteClient client) {
        playersQueue.add(token);
        rmiPlayers.put(token, client);

        printLobby();

        if(playersQueue.size() >= MAX_PLAYERS) {
            newGame();
        }
        else if(playersQueue.size() == MIN_PLAYERS) {
            //start timer
            startTimer();
        }
    }

    /**
     * Add a Socket player to the queue for a multiplayer match.
     * @param token the token of the client.
     * @param socket the socket of the client.
     */
    public synchronized void enqueuePlayer(String token, Socket socket) {
        playersQueue.add(token);
        socketPlayers.put(token, socket);

        if(!socketReaderByToken.containsKey(token)) {
            //start a new thread that manages the user input
            SocketReader socketReader = new SocketReader(token, socket);
            addSocketReader(token, socketReader);
            socketReader.start();
        }

        printLobby();

        if(playersQueue.size() >= MAX_PLAYERS) {
            newGame();
        }
        else if(playersQueue.size() == MIN_PLAYERS) {
            //start timer
            startTimer();
        }
    }

    /**Adds the given token and socket to the mpa of socket player and
     * binds a socket single player to a new single player controller,starting the match. If the player
     * is already present in the model, the socket connection is resetted
     * @param token token of the client
     * @param socket socket of the client
     */
    public synchronized void singlePlayerGame(String token, Socket socket){
        socketPlayers.put(token, socket);

        if(!socketReaderByToken.containsKey(token)) {
            //start a new thread that manages the user input
            SocketReader socketReader = new SocketReader(token, socket);
            addSocketReader(token, socketReader);
            //socketReaderByToken.put(token, socketReader);
            socketReader.start();
        }

        try {
            String[] singlePlayerToken = new String[1];
            singlePlayerToken[0] = token;
            new Thread(new SinglePlayerController(singlePlayerToken)).start();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    /**Adds the given token and remote cli to the map of rmiPlayers and starts a new singlePlayer game
     * binding the single player controller to the player
     * @param token the token of the client.
     * @param client the RemoteCli of the client.
     */
    public synchronized void singlePlayerGame(String token, RemoteClient client){
        rmiPlayers.put(token, client);

        try {
            String[] singlePlayerToken = new String[1];
            singlePlayerToken[0] = token;
            new Thread(new SinglePlayerController(singlePlayerToken)).start();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }


    /**
     * Remove a player from the queue for a multiplayer match.
     * @param token the token of the client.
     */
    public synchronized void dequeuePlayer(String token) {
        if(playersQueue.remove(token)) {
            System.out.println("Dequeue " + playerByToken.get(token).getUsername());
            printLobby();
        }

        if(playersQueue.size() < MIN_PLAYERS && timer != null) {
            timer.cancel();
            notifyLobbyPlayers("Timer reset");
        }


    }

    /**
     * Print lobby component to players in queue.
     */
    private synchronized void printLobby() {
        String lobby = "Players:\n";

        for(String token: playersQueue) {
            lobby = lobby + " -" + playerByToken.get(token).getUsername() + "\n";
        }

        lobby = lobby + "\npress [m] to return to the main menu";

        notifyLobbyPlayers(lobby);
    }

    /**
     * Print a message to players in queue.
     * @param message the message to print.
     */
    private synchronized void notifyLobbyPlayers(String message) {

        for(String token: playersQueue) {
            if(rmiPlayers.containsKey(token)) {
                RemoteClient client = rmiPlayers.get(token);

                try {
                    client.printLobby(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Socket socket = socketPlayers.get(token);

                try {
                    PrintWriter out = new PrintWriter(socket.getOutputStream());
                    out.println("@p" + message + "#p");
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void startTimer() {
        timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Time over");
                newGame();
                timer.cancel();
            }
        }, timerTime);

        notifyLobbyPlayers("A new match will start in " + (timerTime/1000) + "s");
    }


    /**
     * Check if contains a RemotClient mapped to the specified token.
     * @param token the possible token of a RMI player.
     * @return true if contains a RemoteCli mapped to the specified token.
     */
    public synchronized boolean containsRemoteClient(String token) {
        return rmiPlayers.containsKey(token);
    }

    /**
     * Remove the RemoteClient mapped to the specified token.
     * @param token the possible token of a RMI player.
     * @return the RemoteCli removed, or null if no RemoteCli is mapped to the specified token.
     */
    public synchronized RemoteClient removeRemoteClient(String token) {
        return rmiPlayers.remove(token);
    }

    /**
     * Check if contains a Socket mapped to the specified token.
     * @param token the possible token of a socket player.
     * @return true if contains a Socket mapped to the specified token.
     */
    public synchronized boolean containsSocket(String token) {
        return socketPlayers.containsKey(token);
    }

    /**
     * Remove the Socket mapped to the specified token.
     * @param token the possible token of a socket player.
     * @return the Socket removed, or null if no Socket is mapped to the specified token.
     */
    public synchronized Socket removeSocket(String token) {
        return socketPlayers.remove(token);
    }


    private synchronized void newGame() {
        System.out.println("Start a new game; size of the queue=" + playersQueue.size());

        if(timer != null)
            timer.cancel();

        //n = number of players
        int n = Math.min(MAX_PLAYERS, playersQueue.size());

        String[] tokens = new String[n];

        for(int i=0; i<n; i++) {
            tokens[i] = playersQueue.remove();
        }

        try {
            new Thread(new GameControllerImpl(tokens)).start();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    /**
     * Add a new mapping from the specified player to the specified controller.
     * @param player the player to be mapped.
     * @param controller the controller to be mapped.
     */
    public synchronized void addPlayerToGame(Player player, GameControllerImpl controller) {
        playerByController.put(player, controller);
    }


    /**
     * Remove the mapping for the specified player to its controller.
     * @param player the player to be remove from the map.
     */
    public synchronized void removePlayerFromGame(Player player) {
        playerByController.remove(player);
    }

    /**
     * Check if the specified player is in an ongoing match.
     * @param player the player to check.
     * @return true if the specified player is in an ongoing match.
     */
    public synchronized boolean isPlayerInGame(Player player) {
        return playerByController.containsKey(player);
    }


    /**
     * Returns the controller of the game in which the specified player is playing.
     * @param player the player whose controller must be returns.
     * @return the controller of the game in which the specified player is playing.
     */
    public synchronized GameControllerImpl getPlayerGame(Player player) {
        return playerByController.get(player);
    }

    /**
     * Returns the SocketReader to which is mapped the specified token.
     * @param token the token of the client.
     * @return the SocketReader to which is mapped the specified token.
     */
    public synchronized SocketReader getSocketReader(String token) {
        return socketReaderByToken.get(token);
    }

    /**
     * Returns a collection of all the SocketReader.
     * @return a collection of all the SocketReader.
     */
    public synchronized Collection<SocketReader> getAllSocketReaders() {
        return socketReaderByToken.values();
    }


    /**
     * Returns a String representing the global rank
     * @return the global rank.
     */
    public synchronized String getPlayerSortedByVictories() {
        statistics.sort(new StatisticsVictoriesComparator());

        return getGlobalRank(10);
    }

    /**
     * Returns a String representing the global rank, restricted to the first {@code max} players.
     * @param max the maximum numbers of players.
     * @return the global rank.
     */
    private synchronized String getGlobalRank(int max) {
        String globalRank = "Global Rank:\n      Player       \t Victories \t Defeat \t Tot Points \t Tot Game Time \n";
        int n = Math.min(max,statistics.size());

        for(int i=0; i<n; i++) {
            Player currPlayer = statistics.get(i);

            String value = currPlayer.getUsername();
            globalRank = globalRank + (i+1) + ") " + value;

            int extraSpaces = 16-value.length();
            for(int j=0; j<extraSpaces; j++)
                globalRank = globalRank + " ";

            value = String.valueOf(currPlayer.getNumOfVictories());
            globalRank = globalRank + "\t " + value;

            extraSpaces = 11 - value.length() - 1;
            for(int j=0; j<extraSpaces; j++)
                globalRank = globalRank + " ";

            value = String.valueOf(currPlayer.getNumOfDefeat());
            globalRank = globalRank + "\t " + value;

            extraSpaces = 8 - value.length() - 1;
            for(int j=0; j<extraSpaces; j++)
                globalRank = globalRank + " ";

            value = String.valueOf(currPlayer.getTotPoints());
            globalRank = globalRank + "\t " + value;

            extraSpaces = 12 - value.length() - 1;
            for(int j=0; j<extraSpaces; j++)
                globalRank = globalRank + " ";

            value = String.valueOf(currPlayer.getTotGameTime());
            globalRank = globalRank + "\t " + value;

            extraSpaces = 15 - value.length() - 1;
            for(int j=0; j<extraSpaces; j++)
                globalRank = globalRank + " ";

            globalRank = globalRank + "\n";

        }

        return globalRank;
    }


    /**
     * Execute a console command.
     * @param command the command to be executed.
     */
    private synchronized void console(String command) {
        switch (command) {
            case "tokens":
                for(String t: playerByToken.keySet())
                    System.out.println(playerByToken.get(t).getUsername() + " : " + t);
                break;
            case "games":
                int i=0;
                Set<GameControllerImpl> controllers = new HashSet<>(playerByController.values());

                for(GameControllerImpl c: controllers) {
                    System.out.println("Game n. + " + i);
                    for(Player p: playerByController.keySet()) {
                        if(playerByController.get(p) == c)
                            System.out.println("\t" + p.getUsername());
                    }
                    i++;
                }
                break;
            case "queue":
                System.out.println("Queue:");
                for(String token: playersQueue)
                    System.out.println(" -" + playerByToken.get(token).getUsername());
                break;
            case "rmi":
                System.out.println("RMI players:");
                for(RemoteClient cli: playerByRemoteClient.keySet())
                    System.out.println(" -" + playerByRemoteClient.get(cli).getUsername());
                break;
            case "socket":
                System.out.println("Socket players:");
                for(String t: socketReaderByToken.keySet())
                    System.out.println(" -" + playerByToken.get(t).getUsername());
                break;
            case "passwords":
                for(String u: passwordByUsername.keySet())
                    System.out.println(u + " : " + passwordByUsername.get(u));
                break;
            case "stat":
                System.out.println(getPlayerSortedByVictories());
                break;
            default:
                System.out.println("Wrong command");

        }
    }


    /**
     * Returns a List of all the dice.
     * @return a List of all the dice.
     */
    public synchronized ArrayList<Dice> getGameDice(){
        ArrayList<Dice> clonedDice = new ArrayList<>();
        for(Dice die: gameDice){
            clonedDice.add(new Dice(die));
        }
        return clonedDice;
    }

    /**
     * Returns a List of all the toolcard.
     * @return a List of all the toolcard.
     * @throws WrongIdException
     */
    public synchronized List<ToolCard> getToolCards() throws WrongIdException{
        return modelCardLoader.getToolCardDeck();
    }

    /**
     * Returns a List of all the private object card.
     * @return a List of all the private object card.
     * @throws WrongIdException
     */
    public synchronized List<ObjCard> getPrivateObjCards() throws WrongIdException{
        return modelCardLoader.getPrivateObjCardDeck();
    }

    /**
     * Returns a List of all the public object card.
     * @return a List of all the public object card.
     * @throws WrongIdException
     */
    public synchronized  List<ObjCard> getPublicObjCards() throws WrongIdException{
        return modelCardLoader.getPublicObjCardDeck();
    }

    /**
     * Returns a List of all the window.
     * @return a List of all the window.
     */
    public synchronized List<WindowPatternCard> getWindows(){
        return wpcLoader.getWindows();
    }

    private synchronized void loadModel(){
        for(int i = 0; i < 18; i++){                                    //initializing the model with shared game objects
            gameDice.add(new Dice(DiceColor.GREEN,DiceShade.ONE));
        }
        for(int i = 0; i < 18; i++){
            gameDice.add(new Dice(DiceColor.YELLOW,DiceShade.ONE));
        }
        for(int i = 0; i < 18; i++){
            gameDice.add(new Dice(DiceColor.RED,DiceShade.ONE));
        }
        for(int i = 0; i < 18; i++) {
            gameDice.add(new Dice(DiceColor.BLUE, DiceShade.ONE));
        }
        for(int i = 0; i < 18; i++){
            gameDice.add(new Dice(DiceColor.PURPLE,DiceShade.ONE));
        }
        modelCardLoader.loadCards();

        //load passwords
        FileReader fr = null;
        BufferedReader br = null;

        try {
            fr = new FileReader(credentialsFilePath);
            br = new BufferedReader(fr);

            while(br.ready()) {
                String line = br.readLine();

                String[] data = line.split(" ");
                if(data.length == 2) {
                    passwordByUsername.put(data[0], data[1]);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(fr != null)
                    fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (br != null)
                    br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Saving players");


            try {
                FileOutputStream statsOutStream = new FileOutputStream(statsFilePath);
                ObjectOutputStream outStats = new ObjectOutputStream(statsOutStream);

                for(Player player: statistics) {
                    outStats.writeObject(player);
                }

                outStats.close();


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));


        //load statistics
        FileInputStream statsFileStream = null;
        ObjectInputStream inStats = null;
        try {
            statsFileStream = new FileInputStream(statsFilePath);
            inStats = new ObjectInputStream(statsFileStream);


            while (statsFileStream.available() != 0) {
                Player player = (Player) inStats.readObject();

                playerByUsername.put(player.getUsername(), player);
                statistics.add(player);
            }

            System.out.println("Known player:");
            for(Player p: statistics)
                System.out.println(" - " + p.getUsername());


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (EOFException e) {
            try {
                if(statsFileStream.available() == 0)
                    System.out.println("No statistics to load");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (statsFileStream != null)
                    statsFileStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if(inStats != null)
                    inStats.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    /**
     * Set a time for the lobby's timer.
     * @param timerTime lobby's timer time in milliseconds.
     */
    public static void setTimerTime(int timerTime) {
        MainModel.timerTime = timerTime;
    }
}
