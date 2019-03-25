package com.view.gui;

import com.view.shapes.RoundBoardSpace;
import com.view.shapes.DiceSpaceNode;
import com.view.shapes.VectorialRoundBoard;
import com.view.shapes.VectorialWindow;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.List;

/**
 * This is the Controller for Game Scene, it handles all the visual operations for GUI, and also all the interactions between
 * Client and Server.
 */
public class ControllerGameScene {

    private Sender senderSocket;
    private SenderRMI senderRMI;

    @FXML
    private Button resumeButton;
    @FXML
    private Button yesButton;
    @FXML
    private Button noButton;
    @FXML
    private Button value1;
    @FXML
    private Button value2;
    @FXML
    private Button value3;
    @FXML
    private Button value4;
    @FXML
    private Button value5;
    @FXML
    private Button value6;
    @FXML
    private Button plusButton;
    @FXML
    private Button minusButton;
    @FXML
    private Label messageBox;
    @FXML
    public Button toolCardButton;
    @FXML
    private ImageView die1;
    @FXML
    private ImageView die2;
    @FXML
    private ImageView die3;
    @FXML
    private ImageView die4;
    @FXML
    private ImageView die5;
    @FXML
    private ImageView die6;
    @FXML
    private ImageView die7;
    @FXML
    private ImageView die8;
    @FXML
    private ImageView die9;
    @FXML
    private ImageView privateObj;
    @FXML
    private ImageView publicObj1;
    @FXML
    private ImageView publicObj2;
    @FXML
    private ImageView publicObj3;
    @FXML
    private ImageView toolCard1;
    @FXML
    private ImageView toolCard2;
    @FXML
    private ImageView toolCard3;
    @FXML
    private BorderPane player2;
    @FXML
    private BorderPane player3;
    @FXML
    private BorderPane player4;
    @FXML
    private Label tokens;
    @FXML
    private BorderPane myWindow;
    @FXML
    private Label namePlayer2;
    @FXML
    private Label namePlayer3;
    @FXML
    private Label namePlayer4;
    @FXML
    private Label myName;
    @FXML
    private BorderPane roundBoard;

    private String autoAbortMessage = "";
    private boolean autoAbortFlag = false;
    private String invalidActionMessage = "";
    private boolean invalidActionFlag = false;
    private boolean glazHammerAborted = false;
    private String glazHammer = "";
    private Stage stage;
    private boolean stopReader = false;
    private String diceOnRoundBoard;
    private List<List<RoundBoardSpace>> roundBoardSpaces;
    private String color;
    private String number;
    private String toolCardInUse;
    private String[] toolCardAssociated = new String[3];
    private GUIState state = GUIState.NORMAL;
    private boolean isRMI;
    private boolean picked = false;
    private int dieSelected;
    private DiceSpaceNode[][] nodes;
    private String tokenNumber;
    private ImageView[] publicObj = new ImageView[3];
    private ImageView[] toolCards = new ImageView[3];
    private String[] toolCardCost = new String[3];
    private ImageView[] dice = new ImageView[9];
    private VectorialWindow wind;
    private VectorialRoundBoard vectRoundBoard;
    private VectorialWindow[] users = new VectorialWindow[3];
    private String msg;
    private String[] names = new String[3];

    /**
     * Initialization of Game Scene: stage is taken from
     * @see GUI
     * and isRMI is taken from
     * @see ControllerStartScene
     * It also initialize all starting elemnts, putting in their respective arrays in order to make all future operations
     * more simple.
     * After this it calls the proper FirstView Method (Socket or RMI) in order to catch from Server the entire initial
     * configuration of board.
     */
    @FXML
    private void initialize() {
        isRMI = ControllerStartScene.getIsRMI();
        stage = GUI.getStage();
        publicObj[0] = publicObj1;
        publicObj[1] = publicObj2;
        publicObj[2] = publicObj3;
        toolCards[0] = toolCard1;
        toolCards[1] = toolCard2;
        toolCards[2] = toolCard3;
        dice[0] = die1;
        dice[1] = die2;
        dice[2] = die3;
        dice[3] = die4;
        dice[4] = die5;
        dice[5] = die6;
        dice[6] = die7;
        dice[7] = die8;
        dice[8] = die9;
        names[0] = "";
        names[1] = "";
        names[2] = "";
        vectRoundBoard = new VectorialRoundBoard();
        roundBoard.setCenter(vectRoundBoard);
        plusMinusHide();
        shadesHide();
        yesNoHide();
        resumeButtonHide();
        if (!isRMI) {
            senderSocket = ControllerStartScene.getSenderSocket();
            firstView();
            tokens.setText(tokenNumber);
            myWindow.setCenter(wind);
            player2.setCenter(users[0]);
            player3.setCenter(users[1]);
            player4.setCenter(users[2]);
            namePlayer2.setText(names[0]);
            namePlayer3.setText(names[1]);
            namePlayer4.setText(names[2]);
            myName.setText(senderSocket.getUsername());
            new Thread(new SocketMessage(this, senderSocket.getIn())).start();
            placeDieInitialization();
            turnNotification(msg);
        } else {
            senderRMI = ControllerStartScene.getSenderRMI();
            RMIgui r = ControllerStartScene.getRmi();
            r.setGameControl(this);
            senderRMI.notifyViewLock();
        }
    }

    /**
     * Reads one by one all messages sent from Controller and based on these initialize for the first time the board.
     * This is for Socket.
     */
    private void firstView() {
        String message;
        //Other players' windows initialization
        int i = 0;
        do {
            message = senderSocket.read();
            String window = "";
            if (message.endsWith("Window:")) {
                String name = message.split(" ")[0];
                for (int p = 0; p < 5; p++) {
                    message = senderSocket.read();
                    window = window + message + "\n";
                }
                users[i] = new VectorialWindow(window);
                names[i] = name;
                i++;
            }
        } while (!(message.startsWith(">>Public")));

        //Public objectives initialization
        int j = 0;
        do {
            message = senderSocket.read();
            if (message.startsWith("COF60 ")) {
                Image image = new Image(getClass().getResource("/assets/Objective/public_row_color_variety.jpg").toString());
                publicObj[j].setImage(image);
                j++;
            }
            if (message.startsWith("COF50 ")) {
                Image image = new Image(getClass().getResource("/assets/Objective/public_column_color_variety.jpg").toString());
                publicObj[j].setImage(image);
                j++;
            }
            if (message.startsWith("COF40 ")) {
                Image image = new Image(getClass().getResource("/assets/Objective/public_column_shade_variety.jpg").toString());
                publicObj[j].setImage(image);
                j++;
            }
            if (message.startsWith("COF51 ")) {
                Image image = new Image(getClass().getResource("/assets/Objective/public_row_shade_variety.jpg").toString());
                publicObj[j].setImage(image);
                j++;
            }
            if (message.startsWith("COF41 ")) {
                Image image = new Image(getClass().getResource("/assets/Objective/public_color_variety.jpg").toString());
                publicObj[j].setImage(image);
                j++;
            }
            if (message.startsWith("COF52 ")) {
                Image image = new Image(getClass().getResource("/assets/Objective/public_shade_variety.jpg").toString());
                publicObj[j].setImage(image);
                j++;
            }
            if (message.startsWith("COF20 ")) {
                Image image = new Image(getClass().getResource("/assets/Objective/public_deep_shades.jpg").toString());
                publicObj[j].setImage(image);
                j++;
            }
            if (message.startsWith("COF21 ")) {
                Image image = new Image(getClass().getResource("/assets/Objective/public_medium_shades.jpg").toString());
                publicObj[j].setImage(image);
                j++;
            }
            if (message.startsWith("COF22 ")) {
                Image image = new Image(getClass().getResource("/assets/Objective/public_light_shades.jpg").toString());
                publicObj[j].setImage(image);
                j++;
            }
            if (message.startsWith("COF00 ")) {
                Image image = new Image(getClass().getResource("/assets/Objective/public_color_diagonals.jpg").toString());
                publicObj[j].setImage(image);
                j++;
            }
        } while (!(message.startsWith(">>Tool")));

        //Tool cards initialization
        int z = 0;
        for (int g = 0; g < 3; g++) {
            message = senderSocket.read();
            String line = message.split(" ")[3];
            String service = message.split("cost ")[1];
            String cost = service.split(" ")[1];
            z = toolCardInitialization(line, z, cost);
        }
        do {
            message = senderSocket.read();
        } while (!(message.startsWith(">>Dice")));

        //Dice initialization
        do {
            message = senderSocket.read();
            String[] value = message.split("\t");
            diceInitialization(value);
        } while (!(message.equals(">>Your private object card:")));

        //Private objective initialization
        do {
            message = senderSocket.read();
            privateObjectInitialization(message);
        } while (!(message.startsWith(">>Favor")));

        //Favor tokens initialization
        String[] tokenValue = message.split(" ");
        tokenNumber = tokenValue[(message.split(" ").length) - 1];

        //My window initialization
        do {
            message = senderSocket.read();
        } while (!(message.startsWith(">>Your")));

        String window = "";
        for (int s = 0; s < 5; s++) {
            message = senderSocket.read();
            window = window + message + "\n";
        }
        wind = new VectorialWindow(window);
        nodes = wind.getNodes();

        do {
            message = senderSocket.read();
        } while (!(message.endsWith("#v")));

        msg = message;
    }

    /**
     * Reads one by one all messages sent from Controller and based on these initialize for the first time the board.
     * This is for RMI.
     * @param s starting situation of board.
     */
    void firstViewRMI(String s) {
        String[] line = s.split("\n");
        int i = 0;
        for (int q = 0; q < line.length; q++) {
            if (line[q].endsWith("Window:")) {
                String window = "";
                String name = line[q].split(" ")[0];
                for (int p = q + 1; p < q + 6; p++) {
                    window = window + line[p] + "\n";
                }
                users[i] = new VectorialWindow(window);
                names[i] = name;
                i++;
            } else if (line[q].startsWith(">>Public object ")) {
                int j = 0;
                for (int z = q + 1; z < q + 4; z++) {
                    if (line[z].startsWith("COF60 ")) {
                        Image image = new Image(getClass().getResource("/assets/Objective/public_row_color_variety.jpg").toString());
                        publicObj[j].setImage(image);
                        j++;
                    }
                    if (line[z].startsWith("COF50 ")) {
                        Image image = new Image(getClass().getResource("/assets/Objective/public_column_color_variety.jpg").toString());
                        publicObj[j].setImage(image);
                        j++;
                    }
                    if (line[z].startsWith("COF40 ")) {
                        Image image = new Image(getClass().getResource("/assets/Objective/public_column_shade_variety.jpg").toString());
                        publicObj[j].setImage(image);
                        j++;
                    }
                    if (line[z].startsWith("COF51 ")) {
                        Image image = new Image(getClass().getResource("/assets/Objective/public_row_shade_variety.jpg").toString());
                        publicObj[j].setImage(image);
                        j++;
                    }
                    if (line[z].startsWith("COF41 ")) {
                        Image image = new Image(getClass().getResource("/assets/Objective/public_color_variety.jpg").toString());
                        publicObj[j].setImage(image);
                        j++;
                    }
                    if (line[z].startsWith("COF52 ")) {
                        Image image = new Image(getClass().getResource("/assets/Objective/public_shade_variety.jpg").toString());
                        publicObj[j].setImage(image);
                        j++;
                    }
                    if (line[z].startsWith("COF20 ")) {
                        Image image = new Image(getClass().getResource("/assets/Objective/public_deep_shades.jpg").toString());
                        publicObj[j].setImage(image);
                        j++;
                    }
                    if (line[z].startsWith("COF21 ")) {
                        Image image = new Image(getClass().getResource("/assets/Objective/public_medium_shades.jpg").toString());
                        publicObj[j].setImage(image);
                        j++;
                    }
                    if (line[z].startsWith("COF22 ")) {
                        Image image = new Image(getClass().getResource("/assets/Objective/public_light_shades.jpg").toString());
                        publicObj[j].setImage(image);
                        j++;
                    }
                    if (line[z].startsWith("COF00 ")) {
                        Image image = new Image(getClass().getResource("/assets/Objective/public_color_diagonals.jpg").toString());
                        publicObj[j].setImage(image);
                        j++;
                    }
                }
            } else if (line[q].startsWith(">>Tool ")) {
                int z=0;
                for (int g = q+1; g < q+4; g++) {
                    String card = line[g].split(" ")[3];
                    String service = line[g].split("cost ")[1];
                    String cost = service.split(" ")[1];
                    z = toolCardInitialization(card, z, cost);
                }
            } else if (line[q].startsWith(">>Dice:")) {
                String[] value = line[q + 1].split("\t");
                diceInitialization(value);
            } else if (line[q].startsWith(">>Your private object ")) {
                privateObjectInitialization(line[q + 1]);
            } else if (line[q].startsWith(">>Favor")) {
                String[] tokenValue = line[q].split(" ");
                tokenNumber = tokenValue[(line[q].split(" ").length) - 1];
            } else if (line[q].startsWith(">>Your window")) {
                String window = "";
                for (int j = q + 1; j < q + 6; j++) {
                    window = window + line[j] + "\n";
                }
                wind = new VectorialWindow(window);
                nodes = wind.getNodes();
            }
        }
        tokens.setText(tokenNumber);
        myWindow.setCenter(wind);
        player2.setCenter(users[0]);
        player3.setCenter(users[1]);
        player4.setCenter(users[2]);
        namePlayer2.setText(names[0]);
        namePlayer3.setText(names[1]);
        namePlayer4.setText(names[2]);
        myName.setText(senderRMI.getUsername());
        placeDieInitialization();
        turnNotification(line[line.length - 1]);
    }

    /**
     * Reads the update of view sent from Controller and updates all the elements on the board that are changed.
     * This is for Socket.
     */
    void updateView() {
        state = GUIState.NORMAL;
        toolCardButton.setText("Use tool card");
        toolCardsShow();
        yesNoHide();
        plusMinusHide();
        shadesHide();
        resumeButtonHide();
        messageBox.setText("");
        invalidActionFlag = false;
        autoAbortFlag = false;
        String message;
        diceOnRoundBoard = "RoundBoard" + "\n";
        do {
            message = senderSocket.read();
            if ((message.startsWith("Round")) && (!(message.equals("RoundBoard")))) {
                diceOnRoundBoard = diceOnRoundBoard + message + "\n";
            }
        } while (!(message.endsWith("Window:")));
        vectRoundBoard.updateView(diceOnRoundBoard);
        roundBoardSpaces = vectRoundBoard.getRoundBoardSpaces();
        dieOnRoundboardInitialization();

        int w = 0;
        while (!(message.startsWith(">>Public"))) {
            String window = "";
            if (message.endsWith("Window:")) {
                String name = message.split("-")[0];
                for (int p = 0; p < 5; p++) {
                    message = senderSocket.read();
                    window = window + message + "\n";
                }
                users[w].updateView(window);
                names[w] = name;
                w++;
            }
            message = senderSocket.read();
        }
        player2.setCenter(users[0]);
        player3.setCenter(users[1]);
        player4.setCenter(users[2]);
        namePlayer2.setText(names[0]);
        namePlayer3.setText(names[1]);
        namePlayer4.setText(names[2]);

        do {
            message = senderSocket.read();
        } while (!message.startsWith(">>Tool"));

        for (int g = 0; g < 3; g++) {
            message = senderSocket.read();
            String service = message.split("cost ")[1];
            String cost = service.split(" ")[1];
            toolCardCost[g] = cost;
        }

        do {
            message = senderSocket.read();
        } while (!(message.startsWith(">>Dice")));

        do {
            message = senderSocket.read();
            if (!(message.startsWith(">>Your"))) {
                String[] value = message.split("\t");
                diceUpdate(value);
            }
        } while (!(message.startsWith(">>Your")));

        do {
            message = senderSocket.read();
        } while (!(message.startsWith(">>Favor")));

        String[] tokenValue = message.split(" ");
        tokenNumber = tokenValue[(message.split(" ").length) - 1];
        tokens.setText(tokenNumber);

        //update my window
        while (!(message.startsWith(">>Your"))) {
            message = senderSocket.read();
        }
        String window = "";
        for (int s = 0; s < 5; s++) {
            message = senderSocket.read();
            window = window + message + "\n";
        }
        wind.updateView(window);
        myWindow.setCenter(wind);

        do {
            message = senderSocket.read();
        } while (!(message.endsWith("#v")));

        turnNotification(message);
    }

    /**
     * Reads the update of view sent from Controller and updates all the elements on the board that are changed.
     * This is for RMI.
     * @param s a complete view update sent by Controller.
     */
    void updateViewRMI(String s) {
        state = GUIState.NORMAL;
        toolCardButton.setText("Use tool card");
        toolCardsShow();
        yesNoHide();
        plusMinusHide();
        shadesHide();
        resumeButtonHide();
        messageBox.setText("");
        invalidActionFlag = false;
        autoAbortFlag = false;
        String[] line = s.split("\n");
        diceOnRoundBoard = "RoundBoard" + "\n";
        int i = 0;
        for (int q = 0; q < line.length; q++) {
            if((line[q].startsWith("Round")) && (!(line[q].equals("RoundBoard")))) {
                diceOnRoundBoard = diceOnRoundBoard + line[q] + "\n";
            }
            vectRoundBoard.updateView(diceOnRoundBoard);
            roundBoardSpaces = vectRoundBoard.getRoundBoardSpaces();
            dieOnRoundboardInitialization();
            if (line[q].endsWith("Window:")) {
                String name = line[q].split("-")[0];
                String window = "";
                for (int p = q + 1; p < q + 6; p++) {
                    window = window + line[p] + "\n";
                }
                users[i].updateView(window);
                names[i] = name;
                i++;
            } else if (line[q].startsWith(">>Tool")) {
                int z = 0;
                for (int g = q+1; g < q+4; g++) {
                    String service = line[g].split("cost ")[1];
                    String cost = service.split(" ")[1];
                    toolCardCost[z] = cost;
                    z++;
                }
            } else if (line[q].startsWith(">>Dice:")) {
                String[] value = line[q + 1].split("\t");
                diceUpdate(value);
            } else if (line[q].startsWith(">>Favor")) {
                String[] tokenValue = line[q].split(" ");
                tokenNumber = tokenValue[(line[q].split(" ").length) - 1];
                tokens.setText(tokenNumber);
            } else if (line[q].startsWith(">>Your window")) {
                String window = "";
                for (int j = q + 1; j < q + 6; j++) {
                    window = window + line[j] + "\n";
                }
                wind.updateView(window);
            }
        }
        player2.setCenter(users[0]);
        player3.setCenter(users[1]);
        player4.setCenter(users[2]);
        namePlayer2.setText(names[0]);
        namePlayer3.setText(names[1]);
        namePlayer4.setText(names[2]);
        myWindow.setCenter(wind);
        turnNotification(line[line.length - 1]);
    }

    /**
     * Updates the window of the player.
     * This is for Socket.
     */
    void updateWindow() {
        String message;
        String window = "";
        for (int s = 0; s < 5; s++) {
            message = senderSocket.read();
            window = window + message + "\n";
        }
        wind.updateView(window);
        myWindow.setCenter(wind);
        message = senderSocket.read();
    }

    /**
     * Updates the window of the player.
     * This is for RMI.
     * @param s updated window sent by Controller.
     */
    void updateWindowRMI(String s) {
        wind.updateView(s);
        myWindow.setCenter(wind);
    }

    /**
     * Updates the stock.
     * This is for Socket.
     */
    void updateStock() {
        String message;
        message = senderSocket.read();
        String line = message.split("#")[0];
        String[] value = line.split("\t");
        diceUpdate(value);
    }

    /**
     * Updates the stock.
     * This is for RMI.
     * @param s new stock sent by Controller.
     */
    void updateStockRMI(String s) {
        String line = s.split("\n")[1];
        String[] value = line.split("\t");
        diceUpdate(value);
    }

    /**
     * Initializes space nodes of player window in order to catch parameters when clicked.
     * It handles different behaviours related to different state of the player.
     */
    private void placeDieInitialization() {
        for (DiceSpaceNode[] a : nodes) {
            for (DiceSpaceNode b : a) {
                b.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (state==GUIState.NORMAL) {
                            int row = (b.getRow()) + 1;
                            int column = (b.getColumn()) + 1;
                            placeDie(dieSelected, row, column);
                        } else if (state==GUIState.TOOLCARD) {
                            int row = (b.getRow()) + 1;
                            int column = (b.getColumn()) + 1;
                            if (!isRMI) {
                                senderSocket.send(row + "," + column);
                            } else {
                                senderRMI.readInput(row + "," + column);
                            }
                        } else if (state==GUIState.RUNNINGPLIERS) {
                            int row = (b.getRow()) + 1;
                            int column = (b.getColumn()) + 1;
                            if (!isRMI) {
                                senderSocket.send(dieSelected + "," + row + "," + column);
                            } else {
                                senderRMI.readInput(dieSelected + "," + row + "," + column);
                            }
                        }
                    }
                });
            }
        }
    }

    /**
     * Initializes dice on Round Board in order to catch parameters if they are clicked by player during use of Tool Cards.
     */
    private void dieOnRoundboardInitialization() {
        for ( List<RoundBoardSpace> a : roundBoardSpaces) {
            for (RoundBoardSpace b : a) {
                b.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (state==GUIState.TOOLCARD) {
                            int round = (b.getRound()) + 1;
                            int dieIndex = (b.getDieIndex()) + 1;
                            if (!isRMI) {
                                senderSocket.send(round + "," + dieIndex);
                            } else {
                                senderRMI.readInput(round + "," + dieIndex);
                            }
                        }
                    }
                });
            }
        }
    }

    /**
     * Associates and initializes the private object card of the player.
     * @param s the code of the private object card.
     */
    private void privateObjectInitialization(String s) {
        if (s.startsWith("COTP0 ")) {
            Image image = new Image(getClass().getResource("/assets/Objective/private_purple.jpg").toString());
            privateObj.setImage(image);
        }
        if (s.startsWith("COTR0 ")) {
            Image image = new Image(getClass().getResource("/assets/Objective/private_red.jpg").toString());
            privateObj.setImage(image);
        }
        if (s.startsWith("COTY0 ")) {
            Image image = new Image(getClass().getResource("/assets/Objective/private_yellow.jpg").toString());
            privateObj.setImage(image);
        }
        if (s.startsWith("COTG0 ")) {
            Image image = new Image(getClass().getResource("/assets/Objective/private_green.jpg").toString());
            privateObj.setImage(image);
        }
        if (s.startsWith("COTB0 ")) {
            Image image = new Image(getClass().getResource("/assets/Objective/private_blue.jpg").toString());
            privateObj.setImage(image);
        }
    }

    /**
     * Loads the correct asset of die in order to display stock, starting from the string passed by Controller.
     * @param die value in letters of a single die.
     * @return the asset for the die.
     */
    private Image setDie(String die) {
        Image image;
        switch (die) {
            case "B1":
                image = new Image(getClass().getResource("/assets/Dice/GlassBlueDiceOne.png").toString());
                break;
            case "B2":
                image = new Image(getClass().getResource("/assets/Dice/GlassBlueDiceTwo.png").toString());
                break;
            case "B3":
                image = new Image(getClass().getResource("/assets/Dice/GlassBlueDiceThree.png").toString());
                break;
            case "B4":
                image = new Image(getClass().getResource("/assets/Dice/GlassBlueDiceFour.png").toString());
                break;
            case "B5":
                image = new Image(getClass().getResource("/assets/Dice/GlassBlueDiceFive.png").toString());
                break;
            case "B6":
                image = new Image(getClass().getResource("/assets/Dice/GlassBlueDiceSix.png").toString());
                break;
            case "G1":
                image = new Image(getClass().getResource("/assets/Dice/GlassGreenDiceOne.png").toString());
                break;
            case "G2":
                image = new Image(getClass().getResource("/assets/Dice/GlassGreenDiceTwo.png").toString());
                break;
            case "G3":
                image = new Image(getClass().getResource("/assets/Dice/GlassGreenDiceThree.png").toString());
                break;
            case "G4":
                image = new Image(getClass().getResource("/assets/Dice/GlassGreenDiceFour.png").toString());
                break;
            case "G5":
                image = new Image(getClass().getResource("/assets/Dice/GlassGreenDiceFive.png").toString());
                break;
            case "G6":
                image = new Image(getClass().getResource("/assets/Dice/GlassGreenDiceSix.png").toString());
                break;
            case "P1":
                image = new Image(getClass().getResource("/assets/Dice/GlassPurpleDiceOne.png").toString());
                break;
            case "P2":
                image = new Image(getClass().getResource("/assets/Dice/GlassPurpleDiceTwo.png").toString());
                break;
            case "P3":
                image = new Image(getClass().getResource("/assets/Dice/GlassPurpleDiceThree.png").toString());
                break;
            case "P4":
                image = new Image(getClass().getResource("/assets/Dice/GlassPurpleDiceFour.png").toString());
                break;
            case "P5":
                image = new Image(getClass().getResource("/assets/Dice/GlassPurpleDiceFive.png").toString());
                break;
            case "P6":
                image = new Image(getClass().getResource("/assets/Dice/GlassPurpleDiceSix.png").toString());
                break;
            case "R1":
                image = new Image(getClass().getResource("/assets/Dice/GlassRedDiceOne.png").toString());
                break;
            case "R2":
                image = new Image(getClass().getResource("/assets/Dice/GlassRedDiceTwo.png").toString());
                break;
            case "R3":
                image = new Image(getClass().getResource("/assets/Dice/GlassRedDiceThree.png").toString());
                break;
            case "R4":
                image = new Image(getClass().getResource("/assets/Dice/GlassRedDiceFour.png").toString());
                break;
            case "R5":
                image = new Image(getClass().getResource("/assets/Dice/GlassRedDiceFive.png").toString());
                break;
            case "R6":
                image = new Image(getClass().getResource("/assets/Dice/GlassRedDiceSix.png").toString());
                break;
            case "Y1":
                image = new Image(getClass().getResource("/assets/Dice/GlassYellowDiceOne.png").toString());
                break;
            case "Y2":
                image = new Image(getClass().getResource("/assets/Dice/GlassYellowDiceTwo.png").toString());
                break;
            case "Y3":
                image = new Image(getClass().getResource("/assets/Dice/GlassYellowDiceThree.png").toString());
                break;
            case "Y4":
                image = new Image(getClass().getResource("/assets/Dice/GlassYellowDiceFour.png").toString());
                break;
            case "Y5":
                image = new Image(getClass().getResource("/assets/Dice/GlassYellowDiceFive.png").toString());
                break;
            case "Y6":
                image = new Image(getClass().getResource("/assets/Dice/GlassYellowDiceSix.png").toString());
                break;
            default:
                image = new Image(getClass().getResource("/assets/Dice/Null.jpg").toString());
                break;
        }
        return image;
    }

    /**
     * Initializes the first stock on the board.
     * @param d dice in the first stock
     */
    private void diceInitialization(String[] d) {
        int x;
        if (d.length == 5 || d.length == 7 || d.length == 9) {
            if (d.length == 5) {
                for (x = 0; x < 5; x++) {
                    dice[x].setImage(setDie(d[x]));
                }
                die6.setMouseTransparent(true);
                die7.setMouseTransparent(true);
                die8.setMouseTransparent(true);
                die9.setMouseTransparent(true);
            }
            if (d.length == 7) {
                for (x = 0; x < 7; x++) {
                    dice[x].setImage(setDie(d[x]));
                }
                die8.setMouseTransparent(true);
                die9.setMouseTransparent(true);
            }
            if (d.length == 9) {
                for (x = 0; x < 9; x++) {
                    dice[x].setImage(setDie(d[x]));
                }
            }
        }
    }

    /**
     * Updates the assets of the dice in the stock when this one is updated.
     * @param value values of dice in the stock.
     */
    private void diceUpdate(String[] value) {
        for (int x = 0; x < 9; x++) {
            try {
                dice[x].setImage(setDie(value[x]));
                dice[x].setMouseTransparent(false);
            } catch (IndexOutOfBoundsException e) {
                dice[x].setImage(null);
                dice[x].setMouseTransparent(true);
            }
        }
    }

    /**
     * Initializes the Tool Cards, associating the correct asset and storing which Tool Card is in which ImageView, this
     * will be useful for Tool Cards use.
     * @param card the code of the Tool Card.
     * @param z an index useful for storing Tool Cards correctly in the associated array.
     * @param cost the current cost expressed in favor token of the Tool Card.
     * @return the index updated.
     */
    private int toolCardInitialization(String card, int z, String cost) {
        if (card.equals("CTA00")) {
            Image image = new Image(getClass().getResource("/assets/ToolCards/grozing_pliers.png").toString());
            toolCards[z].setImage(image);
            toolCardCost[z] = cost;
            toolCardAssociated[z] = "CTA00";
            z++;
        }
        if (card.equals("CTA10")) {
            Image image = new Image(getClass().getResource("/assets/ToolCards/eglomise_brush.png").toString());
            toolCards[z].setImage(image);
            toolCardCost[z] = cost;
            toolCardAssociated[z] = "CTA10";
            z++;
        }
        if (card.equals("CTA20")) {
            Image image = new Image(getClass().getResource("/assets/ToolCards/copper_foil_burnished.png").toString());
            toolCards[z].setImage(image);
            toolCardCost[z] = cost;
            toolCardAssociated[z] = "CTA20";
            z++;
        }
        if (card.equals("CTA30")) {
            Image image = new Image(getClass().getResource("/assets/ToolCards/lathekin.png").toString());
            toolCards[z].setImage(image);
            toolCardCost[z] = cost;
            toolCardAssociated[z] = "CTA30";
            z++;
        }
        if (card.equals("CTA40")) {
            Image image = new Image(getClass().getResource("/assets/ToolCards/lens_cutter.png").toString());
            toolCards[z].setImage(image);
            toolCardCost[z] = cost;
            toolCardAssociated[z] = "CTA40";
            z++;
        }
        if (card.equals("CTA50")) {
            Image image = new Image(getClass().getResource("/assets/ToolCards/flux_brush.png").toString());
            toolCards[z].setImage(image);
            toolCardCost[z] = cost;
            toolCardAssociated[z] = "CTA50";
            z++;
        }
        if (card.equals("CTA60")) {
            Image image = new Image(getClass().getResource("/assets/ToolCards/glazing_hammer.png").toString());
            toolCards[z].setImage(image);
            toolCardCost[z] = cost;
            toolCardAssociated[z] = "CTA60";
            z++;
        }
        if (card.equals("CTA70")) {
            Image image = new Image(getClass().getResource("/assets/ToolCards/running_pliers.png").toString());
            toolCards[z].setImage(image);
            toolCardCost[z] = cost;
            toolCardAssociated[z] = "CTA70";
            z++;
        }
        if (card.equals("CTA80")) {
            Image image = new Image(getClass().getResource("/assets/ToolCards/cork-backed_straightedge.png").toString());
            toolCards[z].setImage(image);
            toolCardCost[z] = cost;
            toolCardAssociated[z] = "CTA80";
            z++;
        }
        if (card.equals("CTA90")) {
            Image image = new Image(getClass().getResource("/assets/ToolCards/grinding_stone.png").toString());
            toolCards[z].setImage(image);
            toolCardCost[z] = cost;
            toolCardAssociated[z] = "CTA90";
            z++;
        }
        if (card.equals("CTA11")) {
            Image image = new Image(getClass().getResource("/assets/ToolCards/flux_remover.png").toString());
            toolCards[z].setImage(image);
            toolCardCost[z] = cost;
            toolCardAssociated[z] = "CTA11";
            z++;
        }
        if (card.equals("CTA12")) {
            Image image = new Image(getClass().getResource("/assets/ToolCards/tap_wheel.png").toString());
            toolCards[z].setImage(image);
            toolCardCost[z] = cost;
            toolCardAssociated[z] = "CTA12";
            z++;
        }
        return z;
    }

    /**
     * Catches mouse clicks on dice; there are different behaviour related to GUIState:
     * - NORMAL state -> placeDie action or abort of placeDie.
     * - TOOLCARD and RUNNINGPLIERS state -> give coordinates of clicked die.
     * @param mouseEvent event triggered by mouse click on a die.
     */
    public void pickDie(MouseEvent mouseEvent) {
        if (state == GUIState.NORMAL) {
            if (!picked) {
                picked = true;
                messageBox.setText("You are placing a die");
                messageBox.setStyle("-fx-background-color: yellow;");
                if (!isRMI) {
                    senderSocket.send("d");
                } else {
                    senderRMI.readInput("d");
                }
                String whichDie = mouseEvent.getSource().toString();
                if (whichDie.contains("die1")) {
                    dieSelected = 1;
                }
                if (whichDie.contains("die2")) {
                    dieSelected = 2;
                }
                if (whichDie.contains("die3")) {
                    dieSelected = 3;
                }
                if (whichDie.contains("die4")) {
                    dieSelected = 4;
                }
                if (whichDie.contains("die5")) {
                    dieSelected = 5;
                }
                if (whichDie.contains("die6")) {
                    dieSelected = 6;
                }
                if (whichDie.contains("die7")) {
                    dieSelected = 7;
                }
                if (whichDie.contains("die8")) {
                    dieSelected = 8;
                }
                if (whichDie.contains("die9")) {
                    dieSelected = 9;
                }
            } else {
                picked = false;
                messageBox.setText("You have aborted your place die action");
                messageBox.setStyle("-fx-background-color: lightblue;");
                if (!isRMI) {
                    senderSocket.send("a");
                } else {
                    senderRMI.readInput("a");
                }
            }
        } else if (state == GUIState.TOOLCARD) {
            String whichDie = mouseEvent.getSource().toString();
            if (whichDie.contains("die1")) {
                if(!isRMI) senderSocket.send("1");
                if(isRMI) senderRMI.readInput("1");
            }
            if (whichDie.contains("die2")) {
                if(!isRMI) senderSocket.send("2");
                if(isRMI) senderRMI.readInput("2");
            }
            if (whichDie.contains("die3")) {
                if(!isRMI) senderSocket.send("3");
                if(isRMI) senderRMI.readInput("3");
            }
            if (whichDie.contains("die4")) {
                if(!isRMI) senderSocket.send("4");
                if(isRMI) senderRMI.readInput("4");
            }
            if (whichDie.contains("die5")) {
                if(!isRMI) senderSocket.send("5");
                if(isRMI) senderRMI.readInput("5");
            }
            if (whichDie.contains("die6")) {
                if(!isRMI) senderSocket.send("6");
                if(isRMI) senderRMI.readInput("6");
            }
            if (whichDie.contains("die7")) {
                if(!isRMI) senderSocket.send("7");
                if(isRMI) senderRMI.readInput("7");
            }
            if (whichDie.contains("die8")) {
                if(!isRMI) senderSocket.send("8");
                if(isRMI) senderRMI.readInput("8");
            }
            if (whichDie.contains("die9")) {
                if(!isRMI) senderSocket.send("9");
                if(isRMI) senderRMI.readInput("9");
            }
        } else if (state == GUIState.RUNNINGPLIERS) {
            String whichDie = mouseEvent.getSource().toString();
            if (whichDie.contains("die1")) {
                dieSelected = 1;
            }
            if (whichDie.contains("die2")) {
                dieSelected = 2;
            }
            if (whichDie.contains("die3")) {
                dieSelected = 3;
            }
            if (whichDie.contains("die4")) {
                dieSelected = 4;
            }
            if (whichDie.contains("die5")) {
                dieSelected = 5;
            }
            if (whichDie.contains("die6")) {
                dieSelected = 6;
            }
            if (whichDie.contains("die7")) {
                dieSelected = 7;
            }
            if (whichDie.contains("die8")) {
                dieSelected = 8;
            }
            if (whichDie.contains("die9")) {
                dieSelected = 9;
            }
        }
    }

    /**
     * Sends to Controller all the information for placing a die.
     * @param die die selected by player.
     * @param row row of window where player wants to placeDie.
     * @param column column of window where player wants to placeDie.
     */
    private void placeDie(int die, int row, int column) {
        String data = die + "," + row + "," + column;
        if (!isRMI) {
            senderSocket.send(data);
        } else {
            senderRMI.readInput(data);
        }

    }

    /**
     * Sends to Controller the command related to pass player turn.
     * @param event event triggered by mouse click on button.
     */
    public void passButton(ActionEvent event) {
        if (!isRMI) {
            senderSocket.send("p");
        } else {
            senderRMI.readInput("p");
        }
    }

    /**
     * Sends to Controller a command related to the actual GUIState:
     * - NORMAL state -> command for use a Tool Card.
     * - CHOOSING, TOOLCARD or RUNNINGPLIERS state -> command for abort the use of a Tool Card.
     * @param event event triggered by mouse click on button.
     */
    public void toolCardButtonClicked(ActionEvent event) {
        if (state == GUIState.NORMAL) {
            state = GUIState.CHOOSING;
            if (!isRMI) {
                senderSocket.send("t");
            } else {
                senderRMI.readInput("t");
            }
            toolCardButton.setText("Abort");
        } else if (state == GUIState.TOOLCARD || state == GUIState.CHOOSING) {
            state = GUIState.NORMAL;
            if (!isRMI) {
                senderSocket.send("a");
            } else {
                senderRMI.readInput("a");
            }
            toMessageBox("Tool card use aborted", "lightblue");
            toolCardButton.setText("Use tool card");
            toolCardsShow();
            yesNoHide();
            plusMinusHide();
            shadesHide();
        } else if (state == GUIState.RUNNINGPLIERS) {
            state = GUIState.NORMAL;
            if (!isRMI) {
                senderSocket.send("a");
            } else {
                senderRMI.readInput("a");
            }
            toolCardButton.setText("Use tool card");
        }
    }

    /**
     * Sends to Controller the command for coming back from suspension after a period of inactivity.
     * @param event event triggered by mouse click on button.
     */
    public void resumeButtonClicked(ActionEvent event) {
        if(!isRMI) senderSocket.send("k");
        else senderRMI.readInput("k");
    }

    /**
     * Permits to open in a new window the clicked card in order to see it larger, better for reading.
     * In CHOOSING state, if the click is on a Tool Card, sends the related number to Controller in order to activate it.
     * @param mouseEvent event triggered by mouse click on a toolcard or objective card.
     */
    public void cardClicked(MouseEvent mouseEvent) {
        String whichPicture = mouseEvent.getSource().toString();
        if (whichPicture.contains("privateObj")) {
            if(!isRMI) {
                senderSocket.setCard(privateObj.getImage());
                senderSocket.setCost("");
            }
            if(isRMI) {
                senderRMI.setCard(privateObj.getImage());
                senderRMI.setCost("");
            }
            cardZoom();
        }
        if (whichPicture.contains("publicObj1")) {
            if (!isRMI) {
                senderSocket.setCard(publicObj[0].getImage());
                senderSocket.setCost("");
            }
            if (isRMI) {
                senderRMI.setCard(publicObj[0].getImage());
                senderRMI.setCost("");
            }
            cardZoom();
        }
        if (whichPicture.contains("publicObj2")) {
            if (!isRMI) {
                senderSocket.setCard(publicObj[1].getImage());
                senderSocket.setCost("");
            }
            if (isRMI) {
                senderRMI.setCard(publicObj[1].getImage());
                senderRMI.setCost("");
            }
            cardZoom();
        }
        if (whichPicture.contains("publicObj3")) {
            if (!isRMI) {
                senderSocket.setCard(publicObj[2].getImage());
                senderSocket.setCost("");
            }
            if (isRMI) {
                senderRMI.setCard(publicObj[2].getImage());
                senderRMI.setCost("");
            }
            cardZoom();
        }
        if (whichPicture.contains("toolCard1")) {
            if (state == GUIState.NORMAL) {
                if (!isRMI) {
                    senderSocket.setCard(toolCards[0].getImage());
                    senderSocket.setCost(toolCardCost[0]);
                }
                if (isRMI) {
                    senderRMI.setCard(toolCards[0].getImage());
                    senderRMI.setCost(toolCardCost[0]);
                }
                cardZoom();
            } else if (state == GUIState.CHOOSING) {
                toolCardInUse = toolCardAssociated[0];
                state = GUIState.TOOLCARD;
                if (!isRMI) senderSocket.send("1");
                if (isRMI) {
                    senderRMI.readInput("1");
                    senderRMI.notifyToolcard();
                }
            }
        }
        if (whichPicture.contains("toolCard2")) {
            if (state == GUIState.NORMAL) {
                if (!isRMI) {
                    senderSocket.setCard(toolCards[1].getImage());
                    senderSocket.setCost(toolCardCost[1]);
                }
                if (isRMI) {
                    senderRMI.setCard(toolCards[1].getImage());
                    senderRMI.setCost(toolCardCost[1]);
                }
                cardZoom();
            } else if (state == GUIState.CHOOSING) {
                toolCardInUse = toolCardAssociated[1];
                state = GUIState.TOOLCARD;
                if (!isRMI) senderSocket.send("2");
                if (isRMI) {
                    senderRMI.readInput("2");
                    senderRMI.notifyToolcard();
                }
            }
        }
        if (whichPicture.contains("toolCard3")) {
            if (state == GUIState.NORMAL) {
                if (!isRMI) {
                    senderSocket.setCard(toolCards[2].getImage());
                    senderSocket.setCost(toolCardCost[2]);
                }
                if (isRMI) {
                    senderRMI.setCard(toolCards[2].getImage());
                    senderRMI.setCost(toolCardCost[2]);
                }
                cardZoom();
            } else if (state == GUIState.CHOOSING) {
                toolCardInUse = toolCardAssociated[2];
                state = GUIState.TOOLCARD;
                if (!isRMI) senderSocket.send("3");
                if (isRMI) {
                    senderRMI.readInput("3");
                    senderRMI.notifyToolcard();
                }
            }
        }
    }

    /**
     * Shows the scene for the card zoom.
     */
    private void cardZoom() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/CardScene.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            System.out.println("Unable to load FXML CardScene file");
        }
        Stage newStage = new Stage();
        newStage.setMinWidth(320);
        newStage.setMinHeight(510);
        newStage.setTitle("Card");
        newStage.setScene(new Scene(root, 300, 480));
        newStage.showAndWait();
    }

    /**
     * Handles all the messages passed by
     * @see SocketMessage
     * There is different behaviour based on actual GUI State.
     */
    void messageReader() {
        if (state==GUIState.NORMAL) {
            String message;
            do {
                message = senderSocket.read();
                messageCheck(message);
            } while (!(message.endsWith("#p")));
            if (message.equals("THE END#p")) {
                stopReader = true;
                showScoreScene();
            }
            if (message.startsWith("nope, wait your turn")) {
                notYourTurn();
            } else if (message.startsWith("You can't:")) {
                constraintError(message);
            }
        } else if (state==GUIState.CHOOSING) {
            String message;
            String service;
            do {
                message = senderSocket.read();
                if(message.contains("#p")) {
                    service = message.split("#p")[0];
                    messageBox.setText(service + "\n");
                    messageBox.setStyle("-fx-background-color: yellow");
                } else {
                    messageBox.setText(message + "\n");
                    messageBox.setStyle("-fx-background-color: yellow");
                }
            } while (!(message.endsWith("#p")));
            if(message.startsWith("What you want")) {
                messageBox.setText("You have already use a toolcard\nChoose a different action");
                messageBox.setStyle("-fx-background-color: lightblue");
                state = GUIState.NORMAL;
                toolCardButton.setText("Use tool card");
            }
        } else if (state==GUIState.TOOLCARD) {
            toolCardManager();
        } else if (state==GUIState.RUNNINGPLIERS) {
            String message;
            do {
                message = senderSocket.read();
                if (message.startsWith("You can't:")) constraintError(message);
            } while (!(message.endsWith("#p")));
        }
    }

    /**
     * For both RMI and Socket, checks the most common messages that could be arrived and handles them launching related methods.
     * @param message message passed.
     */
    void messageCheck(String message) {
        if (message.startsWith("You've been suspended")) {
            toolCardsHide();
            resumeButtonShow();
            messageBox.setText("You've been suspended because your weren't responding.\nPress 'RESUME' button on the left in order to return to the match");
            messageBox.setStyle("-fx-background-color: grey;");
        }
        if (message.equals("RoundBoard")) {
            roundBoardUpdateInToolcard(message);
        }
        if (message.startsWith("Abort completed")) {
            tokenUpdateInToolcard(message);
        }
        if (message.contains("Diff:")) {
            windowUpdateInToolcard(message);
        }
        if (message.startsWith("Stock:")) {
            stockUpdateInToolCard(message);
        }
        if (message.equals("Dice placed")) {
            picked = false;
            setPlaced();
        } else if (message.equals("You have already placed your die!")) {
            messageBox.setText("You have already placed your die!");
            messageBox.setStyle("-fx-background-color: red;");
            picked = false;
        } else if (message.equals("Running Pliers effect activated")) {
            state = GUIState.RUNNINGPLIERS;
            messageBox.setText("Running Pliers effect activated, I need 3 integer [dice,row,col] or [a] to abort");
            messageBox.setStyle("-fx-background-color: yellow;");
            toolCardButton.setText("Abort");
        }
    }

    /**
     * Notifies the player that this is not his or her turn.
     */
    void notYourTurn() {
        messageBox.setText("It's not your turn! Wait please");
        messageBox.setStyle("-fx-background-color: red;");
        picked = false;
    }

    /**
     * Notifies the player that he or she is violating a constraint of the game.
     * @param message the message has the information related to violated constraint.
     */
    void constraintError(String message) {
        String line;
        if(!isRMI) line = message.split("#")[0];
        else line = message;
        messageBox.setText(line + "\n" + "Please click on a different space on your window");
        messageBox.setStyle("-fx-background-color: red;");
    }

    /**
     * Notifies the player that he or she are making an invalid action.
     * @param s the message has the information related to invalid action.
     */
    private void wrongActionAlert(String s) {
        if(!isRMI) {
            String line = s.split("#")[0];
            messageBox.setText(line);
            invalidActionMessage = line;
        } else {
            messageBox.setText(s);
            invalidActionMessage = s;
        }
        messageBox.setStyle("-fx-background-color: red;");
    }

    /**
     * For both RMI and Socket, notifies the current player.
     * @param message message that contains current player information.
     */
    private void turnNotification(String message) {
        if (!isRMI) {
            if (message.startsWith("What you want")) {
                messageBox.setText("It's your turn!");
                messageBox.setStyle("-fx-background-color: lightgreen;");
            }
            if ((message.endsWith(" turn#v")) || message.endsWith(" turn#p")) {
                String a = message.split(" ")[1];
                String name = a.split("'")[0];
                messageBox.setText("It's " + name + " turn!");
                messageBox.setStyle("-fx-background-color: lightgreen;");
            }
        } else {
            if (message.startsWith("What you want")) {
                messageBox.setText("It's your turn!");
                messageBox.setStyle("-fx-background-color: lightgreen;");
            }
            if (message.endsWith(" turn")) {
                String a = message.split(" ")[1];
                String name = a.split("'")[0];
                messageBox.setText("It's " + name + " turn!");
                messageBox.setStyle("-fx-background-color: lightgreen;");
            }
        }
    }

    /**
     * Writes on the MessageBox a communication for the player, there is a background color related to it:
     * YELLOW -> something in progress
     * RED -> something wrong happened
     * LIGHTBLUE -> conclusion of action in progress before
     * LIGHTGREEN -> turn notification.
     * @param s message shown in the box.
     * @param color background color of the box.
     */
    void toMessageBox(String s, String color) {
        messageBox.setText(s + "\n");
        if (color.equals("yellow")) messageBox.setStyle("-fx-background-color: yellow;");
        if (color.equals("red")) messageBox.setStyle("-fx-background-color: red;");
        if (color.equals("blue")) messageBox.setStyle("-fx-background-color: lightblue;");
        if (color.equals("green")) messageBox.setStyle("-fx-background-color: lightgreen;");
    }

    /**
     * Handles and calls correct method in relation of Tool Card activated by the player.
     */
    private void toolCardManager() {
        String card = toolCardInUse;
        String message;

        //Grinding Stone manager
        if (card.equals("CTA90")) {
            do {
                message = senderSocket.read();
                grindingStoneFluxBrush(message);
            } while (!(message.endsWith("#p")));
        }

        //Cork-backed Straighedge manager
        if (card.equals("CTA80")) {
            do {
                message = senderSocket.read();
                corkBackedStraighedge(message);
            } while (!(message.endsWith("#p")));
        }

        //Copper Foil Burnisher manager
        if(card.equals("CTA20")) {
            do {
                message = senderSocket.read();
                copperFoilEglomiseBrush(message);
            } while (!(message.endsWith("#p")));
        }

        //Eglomise Brush manager
        if(card.equals("CTA10")) {
            do {
                message = senderSocket.read();
                copperFoilEglomiseBrush(message);
            } while (!(message.endsWith("#p")));
        }


        //Glazing Hammer manager
        if (card.equals("CTA60")) {
            do {
                message = senderSocket.read();
                glazingHammer(message);
            } while (!(message.endsWith("#p")));
        }


        //Grozing Pliers manager
        if (card.equals("CTA00")) {
            plusMinusShow();
            toolCardsHide();
            do {
                message = senderSocket.read();
                grozingPliers(message);
            } while (!(message.endsWith("#p")));
        }

        //Flux Remover manager
        if(card.equals("CTA11")) {
            shadesShow();
            toolCardsHide();
            do {
                message = senderSocket.read();
                fluxRemover(message);
            }while (!(message.endsWith("#p")));
        }

        //Lathekin manager
        if(card.equals("CTA30")) {
            do {
                message = senderSocket.read();
                lathekinLensTapWheel(message);
            }while (!(message.endsWith("#p")));
        }

        //Flux brush manager
        if(card.equals("CTA50")) {
            do {
                message = senderSocket.read();
                grindingStoneFluxBrush(message);
            }while (!(message.endsWith("#p")));
        }

        //Tap Wheel manager
        if(card.equals("CTA12")) {
            yesNoShow();
            toolCardsHide();
            do {
                message = senderSocket.read();
                lathekinLensTapWheel(message);
            } while (!(message.endsWith("#p")));
        }

        //Lens Cutter manager
        if (card.equals("CTA40")) {
            do {
                message = senderSocket.read();
                lathekinLensTapWheel(message);
            } while (!(message.endsWith("#p")));
        }

        //Running Pliers manager
        if (card.equals("CTA70")) {
            boolean autoAbort = false;
            do {
                message = senderSocket.read();
                if(message.startsWith("You can't : can't use this card")) autoAbort = true;
                runningPliers(message, autoAbort);
            } while (!(message.endsWith("#p")));
        }
    }

    /**
     * Based on information given during Tool Card execution, updates the Round Board.
     * @param message updated Round Board information.
     */
    void roundBoardUpdateInToolcard(String message) {
        diceOnRoundBoard = "RoundBoard" + "\n";
        if (!isRMI) {
            do {
                message = senderSocket.read();
                if (!(message.equals("#p"))) diceOnRoundBoard = diceOnRoundBoard + message + "\n";
            } while (!(message.endsWith("#p")));
        } else {
            String[] line = message.split("\n");
            for (int q = 0; q < line.length; q++) {
                if ((line[q].startsWith("Round")) && (!(line[q].equals("RoundBoard")))) {
                    diceOnRoundBoard = diceOnRoundBoard + line[q] + "\n";
                }
            }
        }
        vectRoundBoard.updateView(diceOnRoundBoard);
        roundBoardSpaces = vectRoundBoard.getRoundBoardSpaces();
        dieOnRoundboardInitialization();
    }

    /**
     * Based on information given during Tool Card execution, updates the number of favor Token for the player.
     * @param message updated token number.
     */
    void tokenUpdateInToolcard(String message) {
        if (!isRMI) {
            String service = message.split(" ")[5];
            tokens.setText(service.split("#")[0]);
        } else {
            tokens.setText(message.split(" ")[5]);
        }
        messageBox.setText("Toolcard abort complete, choose a new action");
        messageBox.setStyle("-fx-background-color: lightblue");
    }

    /**
     * Based on information given during Tool Card execution, updates the windows.
     * @param message updated windows information.
     */
    void windowUpdateInToolcard(String message) {
        String window = message + "\n";
        if(!isRMI) {
            for (int s = 0; s < 4; s++) {
                message = senderSocket.read();
                window = window + message + "\n";
            }
            wind.updateView(window);
        } else {
            wind.updateView(message);
        }
        myWindow.setCenter(wind);
    }

    /**
     * Based on information given during Tool Card execution, updates the stock.
     * @param message updated stock information.
     */
    void stockUpdateInToolCard(String message) {
        String service = message.split(" ")[1];
        String newDice;
        if(!isRMI) newDice = service.split("#")[0];
        else newDice = service;
        String[] value = newDice.split("\t");
        diceUpdate(value);
    }

    /**
     * Handles the most common messages that could be received during the execution of a Tool Card.
     * @param message message passed.
     */
    private void serverAnswerManager(String message) {
        if(message.startsWith("Stock:")) {
            stockUpdateInToolCard(message);
        }
        if (message.startsWith("Violates")) {
            wrongActionAlert(message);
            invalidActionFlag = true;
        }
        if(message.startsWith("You can't")) {
            wrongActionAlert(message);
            invalidActionFlag = true;
        }
        if (message.contains("Diff:")) {
            windowUpdateInToolcard(message);
        }
        if (message.endsWith("aborting....")) {
            String line;
            autoAbortFlag = true;
            if (message.contains("#p")) {
                line = message.split("#p")[0];
                autoAbortMessage = line;
            } else autoAbortMessage = message;
        }
        if(message.startsWith("What you want")) {
            state=GUIState.NORMAL;
            toolCardButton.setText("Use tool card");
            if(autoAbortFlag) {
                messageBox.setText(autoAbortMessage + "\nChoose a different action");
                messageBox.setStyle("-fx-background-color: lightblue");
                autoAbortFlag = false;
            } else if(invalidActionFlag) {
                messageBox.setText(invalidActionMessage + "\nAction aborted, choose a different action");
                messageBox.setStyle("-fx-background-color: lightblue");
                invalidActionFlag = false;
            } else {
                messageBox.setText("Tool card used");
                messageBox.setStyle("-fx-background-color: lightblue");
            }

            toolCardsShow();
            yesNoHide();
            plusMinusHide();
            shadesHide();
        }
    }

    /**
     * Handles Grinding Stone and Flux Brush execution.
     * @param message all messages arrived during the specific Tool Card execution.
     */
    void grindingStoneFluxBrush (String message) {
        String service;
        if ((message.contains("#p")) && !(message.equals("#p"))) {
            service = message.split("#p")[0];
            messageBox.setText(service + "\n");
        } else messageBox.setText(message + "\n");
        messageBox.setStyle("-fx-background-color: yellow;");
        serverAnswerManager(message);
        if (message.startsWith("You picked")) {
            String line = message.split(" ")[4];
            colorSelected(line);
        }
        if (message.startsWith("New die")) {
            String line = message.split(" ")[3];
            if(!isRMI) number = line.split("#")[0];
            else number = line;
        }
        if (message.startsWith("Select the space")) {
            if (invalidActionFlag) {
                messageBox.setText(invalidActionMessage + "\nChoose a different space");
                messageBox.setStyle("-fx-background-color: red");
                invalidActionFlag = false;
            } else {
                messageBox.setText("You have a " + color + " " + number + " die, choose where you want to put it");
                messageBox.setStyle("-fx-background-color: yellow;");
            }
        }
    }

    /**
     * Handles Cork-Backed Straighedge execution.
     * @param message all messages arrived during the specific Tool Card execution.
     */
    void corkBackedStraighedge (String message) {
        String service;
        if ((message.contains("#p")) && !(message.equals("#p"))) {
            service = message.split("#p")[0];
            messageBox.setText(service + "\n");
        } else messageBox.setText(message + "\n");
        messageBox.setStyle("-fx-background-color: yellow;");
        serverAnswerManager(message);
        if (message.startsWith("Select the space") && (invalidActionFlag)) {
            messageBox.setText(invalidActionMessage + "\nChoose a different space");
            messageBox.setStyle("-fx-background-color: red");
            invalidActionFlag = false;
        }
    }

    /**
     * Handles Copper Foil and Eglomise Brush execution.
     * @param message all messages arrived during the specific Tool Card execution.
     */
    void copperFoilEglomiseBrush (String message) {
        String service;
        if ((message.contains("#p")) && !(message.equals("#p"))) {
            service = message.split("#p")[0];
            messageBox.setText(service + "\n");
        } else messageBox.setText(message + "\n");
        messageBox.setStyle("-fx-background-color: yellow;");
        serverAnswerManager(message);
        if (message.startsWith("Must be adjacent")) {
            messageBox.setText("The die must be adjacent to another dice, select another space");
            messageBox.setStyle("-fx-background-color: red");
        }
        if (message.startsWith("Select the space") && (invalidActionFlag)) {
            messageBox.setText(invalidActionMessage + "\nChoose a different space");
            messageBox.setStyle("-fx-background-color: red");
            invalidActionFlag = false;
        }
    }

    /**
     * Handles Glazing Hammer execution.
     * @param message all messages arrived during the specific Tool Card execution.
     */
    void glazingHammer (String message) {
        String service;
        if ((message.contains("#p")) && !(message.equals("#p"))) {
            service = message.split("#p")[0];
            messageBox.setText(service + "\n");
        } else messageBox.setText(message + "\n");
        messageBox.setStyle("-fx-background-color: yellow;");
        if(message.startsWith("New dice")) {
            if(!isRMI) {
                String line = message.split("#")[0];
                glazHammer = glazHammer + line.split(" ")[3] + " ";
            } else {
                glazHammer = glazHammer + message.split(" ")[3] + " ";
            }
        }
        if(message.startsWith("Stock:")) {
            stockUpdateInToolCard(message);
        }
        if(message.startsWith("You can't : This is not your second turn")) {
            glazHammerAborted = true;
            wrongActionAlert(message);
        }
        if (message.contains("Diff:")) {
            windowUpdateInToolcard(message);
        }
        if(message.startsWith("What you want")) {
            if(!glazHammerAborted) {
                String[] newDice = glazHammer.split(" ");
                for (String s : newDice) {
                    System.out.println(s);
                }
                diceUpdate(newDice);
                glazHammer = "";
                state = GUIState.NORMAL;
                toolCardButton.setText("Use tool card");
                glazHammerAborted = false;
                messageBox.setText("Tool card used");
                messageBox.setStyle("-fx-background-color: lightblue");
            } else {
                glazHammer = "";
                state = GUIState.NORMAL;
                toolCardButton.setText("Use tool card");
                glazHammerAborted = false;
                messageBox.setText("This is not your second turn, you can't use this card");
                messageBox.setStyle("-fx-background-color: lightblue");
            }
        }
    }

    /**
     * Handles Grozing Pliers execution.
     * @param message all messages arrived during the specific Tool Card execution.
     */
    void grozingPliers (String message) {
        String service;
        if ((message.contains("#p")) && !(message.equals("#p"))) {
            service = message.split("#p")[0];
            messageBox.setText(service + "\n");
        } else messageBox.setText(message + "\n");
        messageBox.setStyle("-fx-background-color: yellow;");
        serverAnswerManager(message);
        if (message.equals("RoundBoard")) roundBoardUpdateInToolcard(message);
        if (message.startsWith("Time to place the die")) invalidActionFlag = false;
        if (message.startsWith("Select the space") && (invalidActionFlag)) {
            messageBox.setText(invalidActionMessage + "\nChoose a different space");
            messageBox.setStyle("-fx-background-color: red");
            invalidActionFlag = false;
        }
    }

    /**
     * Handles Lathekin, Lens Cutter and Tap Wheel execution.
     * @param message all messages arrived during the specific Tool Card execution.
     */
    void lathekinLensTapWheel (String message) {
        String service;
        if ((message.contains("#p")) && !(message.equals("#p"))) {
            service = message.split("#p")[0];
            messageBox.setText(service + "\n");
        } else messageBox.setText(message + "\n");
        messageBox.setStyle("-fx-background-color: yellow;");
        serverAnswerManager(message);
        if (message.equals("RoundBoard")) roundBoardUpdateInToolcard(message);
        if (message.startsWith("Select the space") && (invalidActionFlag)) {
            messageBox.setText(invalidActionMessage + "\nChoose a different space");
            messageBox.setStyle("-fx-background-color: red");
            invalidActionFlag = false;
        }
    }

    /**
     * Handles Flux Remover execution.
     * @param message all messages arrived during the specific Tool Card execution.
     */
    void fluxRemover (String message) {
        String service;
        if ((message.contains("#p")) && !(message.equals("#p"))) {
            service = message.split("#p")[0];
            messageBox.setText(service + "\n");
        } else messageBox.setText(message + "\n");
        messageBox.setStyle("-fx-background-color: yellow;");
        serverAnswerManager(message);
        if(message.startsWith("You drafted")) {
            String line = message.split(" ")[2];
            colorSelected(line);
            messageBox.setText("You drafted a " + color + " die, now choose the shade");
            messageBox.setStyle("-fx-background-color: yellow");
        }
        if (message.startsWith("Select the space") && (invalidActionFlag)) {
            messageBox.setText(invalidActionMessage + "\nChoose a different space");
            messageBox.setStyle("-fx-background-color: red");
            invalidActionFlag = false;
        }
    }

    /**
     * Handles Running Pliers execution.
     * @param message all messages arrived during the specific Tool Card execution.
     */
    void runningPliers (String message, boolean autoAbort) {
        String service;
        if ((message.contains("#p")) && !(message.equals("#p"))) {
            service = message.split("#p")[0];
            messageBox.setText(service + "\n");
        } else messageBox.setText(message + "\n");
        messageBox.setStyle("-fx-background-color: yellow;");
        if(message.startsWith("What you want")) {
            state=GUIState.NORMAL;
            toolCardButton.setText("Use tool card");
            if (autoAbort) {
                toMessageBox("You can't use this card in your second turn, your choice was aborted", "red");
            } else {
                toMessageBox("Tool card used", "blue");
            }
        }
    }

    /**
     * Gives to player the information about color of the die drafted during Tool Card execution.
     * @param line char for the color.
     */
    private void colorSelected (String line) {
        if(line.startsWith("B")) color = "BLUE";
        if(line.startsWith("G")) color = "GREEN";
        if(line.startsWith("P")) color = "PURPLE";
        if(line.startsWith("R")) color = "RED";
        if(line.startsWith("Y")) color = "YELLOW";
    }

    /**
     * Catches which shade button is clicked during Flux Remover execution.
     * @param mouseEvent event triggered when one of the shade button is clicked.
     */
    @FXML
    public void shadeSelection(MouseEvent mouseEvent) {
        String whichShade = mouseEvent.getSource().toString();
        if (whichShade.contains("value1")) {
            number = "1";
        }
        if (whichShade.contains("value2")) {
            number = "2";
        }
        if (whichShade.contains("value3")) {
            number = "3";
        }
        if (whichShade.contains("value4")) {
            number = "4";
        }
        if (whichShade.contains("value5")) {
            number = "5";
        }
        if (whichShade.contains("value6")) {
            number = "6";
        }
        if (!isRMI) senderSocket.send(number);
        if (isRMI) senderRMI.readInput(number);
    }

    /**
     * Catches if the player wants to go ahead during Tap Wheel execution.
     * @param event event triggered when Yes button is clicked.
     */
    @FXML
    public void yesSelected (ActionEvent event) {
        if (!isRMI) senderSocket.send("Y");
        if (isRMI) senderRMI.readInput("Y");
    }

    /**
     * Catches if the player doesn't want to go ahead during Tap Wheel execution.
     * @param event event triggered when No button is clicked.
     */
    @FXML
    public void noSelected (ActionEvent event) {
        if (!isRMI) senderSocket.send("N");
        if (isRMI) senderRMI.readInput("N");
    }

    /**
     * Catches if the player wants to increase value during Grozing Pliers execution.
     * @param event event triggered when + button is clicked.
     */
    @FXML
    public void increase(ActionEvent event) {
        if (!isRMI) senderSocket.send("I");
        if (isRMI) senderRMI.readInput("I");
    }

    /**
     * Catches if the player wants to decrease value during Grozing Pliers execution.
     * @param event event triggered when - button is clicked.
     */
    @FXML
    public void decrease(ActionEvent event) {
        if (!isRMI) senderSocket.send("D");
        if (isRMI) senderRMI.readInput("D");
    }

    /**
     * Shows Tool Cards assets.
     */
    private void toolCardsShow() {
        toolCard1.setMouseTransparent(false);
        toolCard1.setVisible(true);
        toolCard2.setMouseTransparent(false);
        toolCard2.setVisible(true);
        toolCard3.setMouseTransparent(false);
        toolCard3.setVisible(true);
    }

    /**
     * Hides Tool Cards assets, necessary during the execution of some Tool Cards.
     */
    void toolCardsHide() {
        toolCard1.setMouseTransparent(true);
        toolCard1.setVisible(false);
        toolCard2.setMouseTransparent(true);
        toolCard2.setVisible(false);
        toolCard3.setMouseTransparent(true);
        toolCard3.setVisible(false);
    }

    /**
     * Shows Yes No Buttons, necessary for Tool Cards.
     */
    void yesNoShow() {
        yesButton.setMouseTransparent(false);
        yesButton.setVisible(true);
        noButton.setMouseTransparent(false);
        noButton.setVisible(true);
    }

    /**
     * Hides Yes No Buttons, necessary during the normal execution of game.
     */
    private void yesNoHide() {
        yesButton.setMouseTransparent(true);
        yesButton.setVisible(false);
        noButton.setMouseTransparent(true);
        noButton.setVisible(false);
    }

    /**
     * Shows shades Buttons, necessary for Tool Card.
     */
    void shadesShow() {
        value1.setMouseTransparent(false);
        value1.setVisible(true);
        value2.setMouseTransparent(false);
        value2.setVisible(true);
        value3.setMouseTransparent(false);
        value3.setVisible(true);
        value4.setMouseTransparent(false);
        value4.setVisible(true);
        value5.setMouseTransparent(false);
        value5.setVisible(true);
        value6.setMouseTransparent(false);
        value6.setVisible(true);
    }

    /**
     * Hides shades Buttons, necessary during the normal execution of game.
     */
    private void shadesHide() {
        value1.setMouseTransparent(true);
        value1.setVisible(false);
        value2.setMouseTransparent(true);
        value2.setVisible(false);
        value3.setMouseTransparent(true);
        value3.setVisible(false);
        value4.setMouseTransparent(true);
        value4.setVisible(false);
        value5.setMouseTransparent(true);
        value5.setVisible(false);
        value6.setMouseTransparent(true);
        value6.setVisible(false);
    }

    /**
     * Shows + and - Buttons, necessary for Tool Card.
     */
    void plusMinusShow() {
        plusButton.setMouseTransparent(false);
        plusButton.setVisible(true);
        minusButton.setMouseTransparent(false);
        minusButton.setVisible(true);
    }

    /**
     * Hides + and - Buttons, necessary during the normal execution of game.
     */
    private void plusMinusHide() {
        plusButton.setMouseTransparent(true);
        plusButton.setVisible(false);
        minusButton.setMouseTransparent(true);
        minusButton.setVisible(false);
    }

    /**
     * Shows Resume Button, necessary when a player is suspended.
     */
    private void resumeButtonShow() {
        resumeButton.setMouseTransparent(false);
        resumeButton.setVisible(true);
    }

    /**
     * Hides Resume Buttons, necessary during the normal execution of game.
     */
    private void resumeButtonHide() {
        resumeButton.setMouseTransparent(true);
        resumeButton.setVisible(false);
    }

    /**
     * Gives the information about which Tool Card is in use in the moment when this method is called.
     * @return this toolCardInUse.
     */
    String getToolCardInUse() {
        return toolCardInUse;
    }

    /**
     * Gives the information about the condition in which the SocketMessage has to stop.
     * @return this stopReader.
     */
    boolean getStopReader() {
        return stopReader;
    }

    /**
     * Writes on the MessageBox if the placeDie action goes ok.
     */
    void setPlaced() {
        messageBox.setText("Dice placed");
        messageBox.setStyle("-fx-background-color: lightblue;");
    }

    /**
     * Gives the information about the current GUIstate.
     * @return this state.
     */
    public GUIState getState() {
        return state;
    }

    /**
     * Changes the value of GUIstate.
     * @param s the new value of GUIstate.
     */
    public void setState(GUIState s) {
        state = s;
    }

    /**
     * Shows Score Scene
     */
    private void showScoreScene() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ScoreScene.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            System.out.println("Unable to load FXML ScoreScene file");
        }
        stage.setScene(new Scene(root,1000,600));
        stage.show();
    }
}