package com.view.gui;

import com.ClientState;
import com.control.GameController;
import com.control.RemoteLobbyManager;
import com.model.LoginException;
import com.view.RemoteClient;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.CountDownLatch;

/**
 * A GUI implementation of the {@link RemoteClient}.
 * @see RemoteClient
 */
public class RMIgui extends UnicastRemoteObject implements RemoteClient {

    private Stage stage;
    private SenderRMI sender;
    private final RemoteLobbyManager lobbyManager;
    private String token = null;
    private String username;
    private static ControllerWaitingScene waitControl = null;
    private static ControllerWindowSelection windControl = null;
    private static ControllerGameScene controllerGameScene = null;
    private static ControllerScoreScene scoreControl = null;
    private static ControllerMenuScene menuControl = null;
    private boolean firstView;
    private Object lock;
    private Object lobbyLock;
    private Object windowChoicesLock;
    private Object viewLock;

    private GameController gameController;
    private GUIState state;

    private final String mainMenuChoices = "What do you want to do? Multiplayer game [mg], singleplayer game [sg], see game's statistics [gs] or quit [q]";
    private final String endGameChoices = "What do you want to do now? Search a new game [g], back to main menu [m] or close [c]";

    /**
     * Constructs a RMIgui that use the specified Remote Lobby Manager for searching games and the specified SenderRMI.
     * @param lobbyManager the Remote Lobby Manager.
     * @param sender the SenderRMI associated to this RMIgui.
     * @throws RemoteException if rmi connection problem occurred.
     */
    RMIgui(RemoteLobbyManager lobbyManager, SenderRMI sender) throws RemoteException {
        super();
        this.lobbyManager = lobbyManager;
        this.sender = sender;
        lock = new Object();
        lobbyLock = new Object();
        windowChoicesLock = new Object();
        viewLock = new Object();
        firstView = true;
    }

    /**
     * Changes the value of waitControl, the controller of Waiting Scene.
     * @param c       This is the controller.
     */
    static void setWaitControl(ControllerWaitingScene c) {
        waitControl = c;
    }

    /**
     * Changes the value of menuControl, the controller of Menu Scene.
     * @param c       This is the controller.
     */
    static void setMenuControl(ControllerMenuScene c) {
        menuControl = c;
    }

    /**
     * Changes the value of windControl, the controller of Window Selection Scene.
     * @param c       This is the controller.
     */
    static void setWindControl(ControllerWindowSelection c) {
        windControl = c;
    }

    /**
     * Changes the value of gameControl, the controller of Game Scene.
     * @param c       This is the controller.
     */
    static void setGameControl(ControllerGameScene c) {
        controllerGameScene = c;
    }

    /**
     * Changes the value of scoreControl, the controller of Score Scene.
     * @param c       This is the controller.
     */
    static void setScoreControl(ControllerScoreScene c) {
        scoreControl = c;
    }

    /**
     * Starts this RemoteCLI.
     */
    public void run() {
        System.out.println("--> Welcome to Sagrada <--\n\n");
        System.out.println(">> Provide an username and a password");
    }

    /**
     * Manages the login operations.
     * @throws RemoteException if rmi connection problem occurred.
     */
    public String login(String line) throws RemoteException {
        String[] info = line.split(" ");
        if (info.length == 2) {

            try {
                token = lobbyManager.login(info[0], info[1], this);
                username = info[0];
                System.out.println("Login as "+info[0]);
                System.out.println("token: \n " + token + "\n");
            } catch (LoginException e) {
                if(e.getMessage().contains("user already logged"))
                    return ("> user already logged") ;
                else if(e.getMessage().contains("wrong password"))
                    return ("> wrong password");
                else if(e.getMessage().contains("username too long"))
                    return ("> username too long");
                else
                    return ("Remote exception!");
            }
        }


    //logout if the client closes its terminal
    Thread shutdownHook = new Thread(() -> {
        System.out.println("Hey, shutting down!");

        try {

            if (gameController != null) {
                gameController.playerCommand(token, "#Logout");
            } else
                lobbyManager.logout(token);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    });

        Runtime.getRuntime().addShutdownHook(shutdownHook);


    //now I am logged
    stage = GUI.getStage();
    System.out.println("logged");
    sender.setClientState(ClientState.MAINMENU);
    sender.setToken(token);
    sender.setUsername(username);
    System.out.println(mainMenuChoices);
    return ("logged");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void printMessage(String s) throws RemoteException {
        if (s.contains("problem during match startup")) {
            setGameControl(null);
            Platform.runLater(new Runnable() {
                public void run() {
                    showEndScene();
                }
            });
        }
        if (s.equals("THE END")) {
            setGameControl(null);
            Platform.runLater(new Runnable() {
                public void run() {
                    showScoreScene();
                }
            });
        }
        if (s.startsWith("Final rank:")) {
            Platform.runLater(new Runnable() {
                public void run() {
                    sender.setRank(s);
                    scoreControl.scoreForRMI();
                }
            });
        }
        if (s.startsWith("Global Rank:")) {
            sender.setStatistics(s);
        }
        if(controllerGameScene != null) {
            state = controllerGameScene.getState();
                messageReader(s);
        }
    }

    /**
     * Reads and handles all general messages sent by Game Controller, not specific one, like update for game view etc.
     * @param s       This is a general message sent by Game Controller.
     */
    private void messageReader(String s) {
        String[] message = s.split("\n");
        if (state==GUIState.NORMAL) {
            Platform.runLater(new Runnable() {
                public void run() {
                    if (s.contains(">>>CURRENT STATE<<<")) controllerGameScene.updateViewRMI(s);
                    else if (s.startsWith("Stock:")) controllerGameScene.stockUpdateInToolCard(s);
                    else if (s.contains("Diff:")) controllerGameScene.windowUpdateInToolcard(s);
                    else if (s.startsWith("Abort completed")) controllerGameScene.tokenUpdateInToolcard(s);
                    else if (message[0].equals("RoundBoard")) controllerGameScene.roundBoardUpdateInToolcard(s);
                    else {
                        for (int i = 0; i < message.length; i++) {
                            controllerGameScene.messageCheck(message[i]);
                            if (message[i].startsWith("nope, wait your turn")) {
                                controllerGameScene.notYourTurn();
                            }
                            if (message[i].startsWith("You can't:")) {
                                controllerGameScene.constraintError(message[i]);
                            }
                            if (message[i].startsWith("Dice placed")) {
                                Platform.runLater(new Runnable() {
                                    public void run() {
                                        controllerGameScene.setPlaced();
                                    }
                                });
                            }
                        }
                    }
                }
            });
        } else if (state==GUIState.CHOOSING) {
            Platform.runLater(new Runnable() {
                public void run() {
                    for(int i=0; i<message.length; i++) {
                        System.out.println(message[i] + "\n");
                        controllerGameScene.toMessageBox(message[i], "yellow");
                        if(message[i].startsWith("What you want")) {
                            controllerGameScene.toMessageBox("You have already use a toolcard\nChoose a different action", "lightblue");
                            controllerGameScene.setState(GUIState.NORMAL);
                            controllerGameScene.toolCardButton.setText("Use tool card");
                        }
                    }
                }
            });
            for(int i=0; i<message.length; i++) {
                if (message[i].endsWith("activated")) {
                    synchronized (lock) {
                        try {
                            while (controllerGameScene.getState() != GUIState.TOOLCARD) {
                                lock.wait();
                            }
                        } catch (InterruptedException error) {
                            System.out.println(error.getMessage());
                        }
                    }
                }
            }
        } else if (state==GUIState.TOOLCARD) {
            Platform.runLater(new Runnable() {
                public void run() {
                    toolCardManager(s);
                }
            });
        } else if (state==GUIState.RUNNINGPLIERS) {
            if (s.startsWith("You can't:")) {
                Platform.runLater(new Runnable() {
                    public void run() {
                        controllerGameScene.constraintError(s);
                    }
                });
            }
        }
    }

    /**
     * Reads and handles all the messages related to tool cards execution.
     * @param message       This is a message sent during execution of a tool card.
     */
    private void toolCardManager(String message) {
        String card = controllerGameScene.getToolCardInUse();
        String[] line = message.split("\n");

        //Grinding Stone manager
        if (card.equals("CTA90")) {
            if (message.contains("Diff:")) controllerGameScene.grindingStoneFluxBrush(message);
            else {
                for (String s : line) {
                    controllerGameScene.grindingStoneFluxBrush(s);
                }
            }
        }

        //Cork-backed Straighedge manager
        if (card.equals("CTA80")) {
            if (message.contains("Diff:")) controllerGameScene.corkBackedStraighedge(message);
            else {
                for (String s : line) {
                    controllerGameScene.corkBackedStraighedge(s);
                }
            }
        }

        //Copper Foil Burnisher manager
        if(card.equals("CTA20")) {
            if (message.contains("Diff:")) controllerGameScene.copperFoilEglomiseBrush(message);
            else {
                for (String s : line) {
                    controllerGameScene.copperFoilEglomiseBrush(s);
                }
            }
        }

        //Eglomise Brush manager
        if(card.equals("CTA10")) {
            if (message.contains("Diff:")) controllerGameScene.copperFoilEglomiseBrush(message);
            else {
                for (String s : line) {
                    controllerGameScene.copperFoilEglomiseBrush(s);
                }
            }
        }

        //Glazing Hammer manager
        if (card.equals("CTA60")) {
            if (message.contains("Diff:")) controllerGameScene.glazingHammer(message);
            else {
                for (String s : line) {
                    controllerGameScene.glazingHammer(s);
                }
            }
        }

        //Grozing Pliers manager
        if (card.equals("CTA00")) {
            controllerGameScene.plusMinusShow();
            controllerGameScene.toolCardsHide();
            if (message.contains("Diff:")) controllerGameScene.grozingPliers(message);
            else {
                for (String s : line) {
                    controllerGameScene.grozingPliers(s);
                }
            }
        }

        //Flux Remover manager
        if(card.equals("CTA11")) {
            controllerGameScene.shadesShow();
            controllerGameScene.toolCardsHide();
            if (message.contains("Diff:")) controllerGameScene.fluxRemover(message);
            else {
                for (String s : line) {
                    controllerGameScene.fluxRemover(s);
                }
            }
        }

        //Lathekin manager
        if(card.equals("CTA30")) {
            if (message.contains("Diff:")) controllerGameScene.lathekinLensTapWheel(message);
            else {
                for (String s : line) {
                    controllerGameScene.lathekinLensTapWheel(s);
                }
            }
        }

        //Flux brush manager
        if(card.equals("CTA50")) {
            if (message.contains("Diff:")) controllerGameScene.grindingStoneFluxBrush(message);
            else {
                for (String s : line) {
                    controllerGameScene.grindingStoneFluxBrush(s);
                }
            }
        }

        //Tap Wheel manager
        if(card.equals("CTA12")) {
            controllerGameScene.yesNoShow();
            controllerGameScene.toolCardsHide();
            if (message.contains("Diff:")) controllerGameScene.lathekinLensTapWheel(message);
            else {
                for (String s : line) {
                    controllerGameScene.lathekinLensTapWheel(s);
                }
            }
        }

        //Lens Cutter manager
        if (card.equals("CTA40")) {
            if (message.contains("Diff:")) controllerGameScene.lathekinLensTapWheel(message);
            else if (line[0].equals("RoundBoard")) controllerGameScene.roundBoardUpdateInToolcard(message);
            else {
                for (String s : line) {
                    controllerGameScene.lathekinLensTapWheel(s);
                }
            }
        }

        //Running Pliers manager
        if (card.equals("CTA70")) {
            boolean autoAborted = false;
            if (message.contains("Diff:")) controllerGameScene.runningPliers(message, autoAborted);
            else {
                for (String s : line) {
                    if (s.startsWith("You can't : can't use this card")) autoAborted = true;
                    controllerGameScene.runningPliers(s, autoAborted);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void printOnReentering (String s) throws RemoteException {
        Platform.runLater(new Runnable() {
            public void run() {
                showGameScene();
            }
        });
        firstView = false;
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                synchronized (viewLock) {
                    try {
                        while (controllerGameScene==null) {
                            viewLock.wait();
                        }
                    } catch (InterruptedException error) {
                        System.out.println(error.getMessage());
                        Thread.currentThread().interrupt();
                    }
                }
                return null;
            }
        };
        task.setOnSucceeded(event -> {
            controllerGameScene.firstViewRMI(s);
        });

        Thread t1 = new Thread(task);
        t1.start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void printLobby (String s) throws IOException {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                synchronized (lobbyLock) {
                    try {
                        while (waitControl==null) {
                            lobbyLock.wait();
                        }
                    } catch (InterruptedException error) {
                        System.out.println(error.getMessage());
                        Thread.currentThread().interrupt();
                    }
                }
                return null;
            }
        };
        task.setOnSucceeded(event -> {
            waitControl.writeMessage(s);
        });

        Thread t1 = new Thread(task);
        t1.start();
    }

    /**
     * When the Controller sends messages with possible windows for player at the beginning of a match, this method
     * calls Window Selection Scene in order to show to player the information sent by Controller.
     */
    public void showWindowsScene (String s) throws RemoteException {
        final CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(new Runnable() {
            public void run() {
                showWindowSelection();
                latch.countDown();
            }
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void printWindowChoices(String s) throws RemoteException {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                synchronized (windowChoicesLock) {
                    try {
                        while (windControl==null) {
                            windowChoicesLock.wait();
                        }
                    } catch (InterruptedException error) {
                        System.out.println(error.getMessage());
                        Thread.currentThread().interrupt();
                    }
                }
                return null;
            }
        };
        task.setOnSucceeded(event -> {
            windControl.printWindows(s);
        });

        Thread t1 = new Thread(task);
        t1.start();
    }

    /**
     * Notifies that Controller Game Scene now is in ToolCard State.
     */
    void notifyLock() {
        synchronized (lock){
            lock.notifyAll();
        }
    }

    /**
     * Notifies that player is ready the receive the complete view of the current game.
     */
    void notifyView() {
        synchronized (viewLock) {
            viewLock.notifyAll();
        }
    }

    /**
     * Notifies that player is in Window Selection Scene.
     */
    void notifyWindowChoices() {
        synchronized (windowChoicesLock) {
            windowChoicesLock.notifyAll();
        }
    }

    /**
     * Notifies that player is in Waiting Scene.
     */
    void notifyLobby() {
        synchronized (lobbyLock) {
            lobbyLock.notifyAll();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateView(String s) throws RemoteException {
        if (firstView) {
            Platform.runLater(new Runnable() {
                public void run() {
                    windControl.launchGame();
                }
            });
            firstView = false;
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() {
                    synchronized (viewLock) {
                        try {
                            while (controllerGameScene==null) {
                                viewLock.wait();
                            }
                        } catch (InterruptedException error) {
                            System.out.println(error.getMessage());
                            Thread.currentThread().interrupt();
                        }
                    }
                    return null;
                }
            };
            task.setOnSucceeded(event -> {
                controllerGameScene.firstViewRMI(s);
            });

            Thread t1 = new Thread(task);
            t1.start();
        }
        else {
            Platform.runLater(new Runnable() {
                public void run() {
                    controllerGameScene.updateViewRMI(s);
                }
            });
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateWindow(String s) throws RemoteException {
        Platform.runLater(new Runnable() {
            public void run() {
                controllerGameScene.updateWindowRMI(s);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateStock(String s) throws RemoteException {
        Platform.runLater(new Runnable() {
            public void run() {
                controllerGameScene.updateStockRMI(s);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startGame(GameController gameController) throws RemoteException {
        this.gameController = gameController;
        sender.setGameController(gameController);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resumeGame(GameController gameController) throws RemoteException {
        //this happen when the player logout or crash and then reenters the game
        this.gameController = gameController;
        this.gameController.playerReenters(token, username, this);
        sender.setGameController(gameController);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void endGame() throws RemoteException {
        sender.setClientState(ClientState.ENDGAME);
        System.out.println(endGameChoices);
        gameController = null;
        sender.setGameController(null);
        firstView = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void ping() throws RemoteException {

    }

    /**
     * Shows Window Selection Scene.
     */
    private void showWindowSelection() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/WindowSelection.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setScene(new Scene(root,1000,600));
        stage.show();
    }

    /**
     * Shows Game Scene.
     */
    private void showGameScene() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/GameScene.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setScene(new Scene(root,1200,600));
        stage.show();
    }

    /**
     * Shows Score Scene.
     */
    private void showScoreScene() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ScoreScene.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setScene(new Scene(root,1000,600));
        stage.show();
    }

    /**
     * Shows End Scene.
     */
    private void showEndScene() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/EndScene.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setScene(new Scene(root,1000,600));
        stage.show();
    }
}
