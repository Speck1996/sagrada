package com;

import com.control.GameControllerImpl;
import com.control.RemoteLobbyManagerImpl;
import com.model.MainModel;
import com.model.gameboard.GameBoard;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * The server of the Sagrada Game.
 */
public class Server {

    private final static int DEFAULT_PORT_RMI = 1099;
    private final static int DEFAULT_PORT_SOCKET = 1444;
    private final static int DEFAULT_TURN_TIMER_TIME = 60000;
    private final static int DEFAULT_LOBBY_TIMER_TIME = 15000;
    private final static int DEFAULT_WINDOWS_TIMER_TIME = 10000;

    public final static String errorMessage = "You can't : ";


    /**
     * Starts the server.
     * After the startup, the server begins to accept client request via RMI or Socket.
     *
     * It is possible to provide by program arguments different port numbers for Socket and RMI or different time for all the game timer (in milliseconds).
     * To do this, the non-negative integer must be preceded by a prefix that indicates which port/timer is involved.
     * The possible prefix are:
     * <ul>
     *     <li>"s": to change socket port number</li>
     *     <li>"r": to change rmi port number</li>
     *     <li>"tt": to change turn timer time</li>
     *     <li>"tl": to change lobby timer time</li>
     *     <li>"tw": to change windows selection timer time</li>
     * </ul>
     * The arguments are accepted in any order.
     * If any or all the port/time are not provided, default values will be use.
     * For example, the following arguments use 2020 as socket port and set the turn timer to 1 minutes:
     *
     * <center>{@code s2020 tt60000}</center>
     *
     * @param args the arguments passed to the server.
     */
    public static void main(String[] args) {

        int portSocket = DEFAULT_PORT_SOCKET;
        int portRmi = DEFAULT_PORT_RMI;
        int turnTimerTime = DEFAULT_TURN_TIMER_TIME;
        int lobbyTimerTime = DEFAULT_LOBBY_TIMER_TIME;
        int windowsTimerTime = DEFAULT_WINDOWS_TIMER_TIME;
        String host = "localhost";



        //read program arguments
        for(String s: args) {
            try {
                if(s.startsWith("s"))
                    portSocket = Integer.parseUnsignedInt(s.substring(1));
                else if(s.startsWith("r"))
                    portRmi = Integer.parseUnsignedInt(s.substring(1));
                else if(s.startsWith("tt"))
                    turnTimerTime = Integer.parseUnsignedInt(s.substring(2));
                else if(s.startsWith("tl"))
                    lobbyTimerTime = Integer.parseUnsignedInt(s.substring(2));
                else if(s.startsWith("tw"))
                    windowsTimerTime = Integer.parseUnsignedInt(s.substring(2));
                else if(s.startsWith("host"))
                    host = s.substring(4);


            } catch (NumberFormatException e) {
                System.out.println("Wrong arguments (port or milliseconds expected)");
                System.exit(-1);
            }
        }



        System.setProperty("java.rmi.server.hostname", host);
        System.out.println("Server on host:" +host);


        System.out.println("RMI port: " + portRmi);
        System.out.println("Socket port: " + portSocket);
        System.out.println("Turn timer time: " + turnTimerTime);
        System.out.println("Lobby timer time: " + lobbyTimerTime);
        System.out.println("Windows selection timer time: " + windowsTimerTime);

        GameBoard.setTimerTime(turnTimerTime);
        MainModel.setTimerTime(lobbyTimerTime);
        GameControllerImpl.setTimerTime(windowsTimerTime);



        RemoteLobbyManagerImpl lobbyManager = null;


        try {
            lobbyManager = new RemoteLobbyManagerImpl();
        } catch (RemoteException e) {
            System.out.println("Error trying to create a LobbyManager");
            System.exit(-1);
        }

        System.out.println("LobbyManager exported");

        try {
            //  Registry registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
            Registry registry = LocateRegistry.getRegistry(portRmi);
            registry.rebind("lobbymanager", lobbyManager);
        } catch (RemoteException e) {
            System.out.println("Error with rmi registry");
            System.exit(-1);
        }
        System.out.println("Bound lobbymanager in registry");


        //socket management

        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(portSocket);
        } catch (IOException e) {
            System.out.println("Error with ServerSocket");
            System.exit(-1);
        }

        System.out.println("Server ready");

        while(true) {
            try {
                Socket socket = serverSocket.accept();

                //create new thread to handle client requests
                new Thread(new PlayerConnectionHandler(socket)).start();

            } catch (IOException e) {
                System.out.println("Error while accepting socket connection");
                System.exit(-1);
            }
        }

    }
}
