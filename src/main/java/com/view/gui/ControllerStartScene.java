package com.view.gui;

import com.control.RemoteLobbyManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.*;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Enumeration;
import java.util.Scanner;

/**
 * This is the Controller for StartScene, it handles the login operations for both RMI and Socket and all
 * the starting interactions between Server and Client, it also initializes both RMI and Socket Senders
 */
public class ControllerStartScene {

    private static final int DEFAULT_SOCKET = 1444;
    private static final int DEFAULT_RMI = 1099;

    private static boolean isRMI;
    private static RMIgui rmi;
    private static Sender senderSocket = new Sender();
    private static SenderRMI senderRMI = new SenderRMI();
    private String data;
    private Stage stage;

    @FXML
    private TextField username;
    @FXML
    private PasswordField psw;
    @FXML
    private ToggleButton rmiToggle;
    @FXML
    private ToggleButton socketToggle;

    /**
     * Initialization of StartScene: stage is taken from
     * @see GUI
     */
    @FXML
    private void initialize() {
        stage = GUI.getStage();
    }

    /**
     * Gets the sender for Socket player
     * @return this senderSocket
     */
    static Sender getSenderSocket() {
        return senderSocket;
    }

    /**
     * Gets the senderRMI for RMI player
     * @return this senderRMI
     */
    static SenderRMI getSenderRMI() {
        return senderRMI;
    }

    /**
     * Gets the boolean isRMI for the current client, the value indicates connection technology
     * chosen during login operations (isRMI -> RMI) (!isRMI -> Socket)
     * @return this isRMI
     */
    static boolean getIsRMI() {
        return isRMI;
    }

    /**
     * Gets the RMIgui for current RMI player
     * @return this rmi
     */
    public static RMIgui getRmi() {
        return rmi;
    }

    /**
     * Displays an alert due to an error occurred
     * @param text    The description of the error shown in the primary part of the alert
     * @param title   The title of the alert window
     */
    private static void messageBox(String text, String title) {
        Alert userAlert = new Alert(Alert.AlertType.INFORMATION, "" + text);
        userAlert.setTitle("" + title);
        userAlert.setHeaderText(null);
        userAlert.showAndWait();
    }

    /**
     * Checks that Username and Password fields aren't empty, after this for Socket
     * starts communication with Server, if this one is reachable, checks validity of Username and Password inserted by
     * player, reading Server answer, and if all login operations are completed shows Menu Scene.
     * For RMI client calls a method that handles the same operations made for Socket
     * @param event    Event triggered by Login Button when it is clicked
     */
    public void pressButton(ActionEvent event) {

        if (username.getText().isEmpty() && psw.getText().isEmpty()) {
            messageBox("Username and password fields are empty, choose a valid username and password", "Username and password error");
        } else if (psw.getText().isEmpty()) {
                messageBox("Password field is empty, choose a valid password","Password error");
        } else if (username.getText().isEmpty()) {
            messageBox("Username field is empty, choose a valid username","Username error");
        } else if (!(socketToggle.isSelected()) && !(rmiToggle.isSelected())) {
            messageBox("You have to choose one connection technology, RMI or Socket", "No connection selected");
        } else {
            data = username.getText() + " " + psw.getText();

            int port = GUI.getPort();
            if (rmiToggle.isSelected()) {
                rmiClient(GUI.getHost(), port == 0 ? DEFAULT_RMI : port);
            } else if (socketToggle.isSelected()){
                System.out.println("--> Welcome to Sagrada <--\n\n");

                senderSocket.start(GUI.getHost(), port == 0 ? DEFAULT_SOCKET : port);
                String line;
                Scanner in = senderSocket.getIn();
                PrintWriter out = senderSocket.getOut();


                //login
                String serverAnswer;

                System.out.println(">> Provide an username and a password");
                line = data;

                if (!line.isEmpty()) {
                    out.println(line);
                    out.flush();

                    serverAnswer = in.nextLine();

                    if(serverAnswer.equals("user already logged") || serverAnswer.equals("wrong password") || serverAnswer.equals("")) {
                        System.out.println(serverAnswer);
                        if (serverAnswer.equals("user already logged")) {
                            messageBox("Username already used, provide a new username", "Username error");
                        }
                        if (serverAnswer.equals("wrong password")) {
                            messageBox("Wrong password for selected user", "Wrong password error");
                        }
                        if (serverAnswer.equals("")) {
                            messageBox("Error", "Generic error");
                        }
                    }
                    else {
                        data = line.split(" ")[0];
                        System.out.println("Hi " + data);
                        System.out.println("Login as " + data);
                        System.out.println("token: " + serverAnswer);
                        isRMI = false;
                        senderSocket.setUsername(username.getText());
                        showMenuScene();
                    }
                }
            }
        }
    }

    /**
     * Start a new RMI client.
     * @param host the IP address of the registry.
     * @param port the port number of the registry.
     */
    private void rmiClient(String host, int port) {
        String ip = "localhost";

        //code used to get the ip of the user
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                // filters out 127.0.0.1 and inactive interfaces
                if (iface.isLoopback() || !iface.isUp())
                    continue;

                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while(addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();

                    if (addr instanceof Inet6Address) continue;

                    ip = addr.getHostAddress();
//                    System.out.println(iface.getDisplayName() + " " + ip);
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }

        System.setProperty("java.rmi.server.hostname", ip);
//            System.out.println("Ip:" + ip);


        Registry registry;

        try {
            registry = LocateRegistry.getRegistry(host, port);


            System.out.println("Registry list:");
            for (String s : registry.list())
                System.out.println(s);

            RemoteLobbyManager lobbyManager = null;
            try {
                lobbyManager = (RemoteLobbyManager) registry.lookup("lobbymanager");
            } catch (NotBoundException e) {
                messageBox("Can't find lobbymanager", "Lobbymanager not found");
            }
            System.out.println("found remote object");

            //new client's remote object
            senderRMI.setLobbyManager(lobbyManager);
            rmi = new RMIgui(lobbyManager, senderRMI);
            rmi.run();
            String info = rmi.login(data);
            if (info.equals("> user already logged") || info.contains("> wrong password") || info.equals("> username too long") || info.contains("Remote exception")) {
                if (info.equals("> user already logged")) {
                    messageBox("Username already used, provide a new username", "Username error");
                }
                else if (info.equals("> wrong password")) {
                    messageBox("Wrong password for selected user", "Wrong password error");
                }
                else if (info.equals("> username too long")) {
                    messageBox("Username too long (max 16)", "Username too long");
                }
                else if (info.contains("Remote exception")) {
                    messageBox("Remote exception", "Remote exception");
                }
            }
            else {
                if (info.equals("logged")) {
                    isRMI = true;
                    senderRMI.setRmiGUI(rmi);
                    showMenuScene();
                }
            }

        } catch (RemoteException e) {
            messageBox("Unable to find the registry, check hostname and port", "Connection error");
        }
    }

    /**
     * Shows the Menu Scene
     */
    private void showMenuScene() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/MenuScene.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            System.out.println("Unable to load FXML MenuScene file");
        }
        stage.setScene(new Scene(root,1000,600));
        stage.show();
    }
}
