package com;

import com.view.CLI;
import com.control.RemoteLobbyManager;
import com.view.gui.GUI;
import javafx.application.Application;
import com.view.RemoteCLI;

import java.net.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Enumeration;
import java.util.Scanner;

/**
 * The client for the Sagrada Game.
 */
public class Client {

    private final static int DEFAULT_SOCKET = 1444;
    private final static int DEFAULT_RMI = 1099;
    private final static String DEFAULT_HOST = "localhost";

    private static Scanner stdin = null;

    /**
     *<p>Starts the game client.
     *First of all the player must choose between CLI or GUI and after that if used an RMI or Socket connection.
     *
     *<br>
     * If arguments are provided must be exactly two in the following order:
     * </p>
     * <ul>
     *     <li>A string with the ip address of the server</li>
     *     <li>the port number of the server</li>
     * </ul>
     * If the no arguments are provided, default values will be used.
     *
     * @param args the arguments passed to the server.
     */
    public static void main(String[] args) {

        String host = DEFAULT_HOST;
        int port = 0;


        //parse arguments
        if(args.length >= 2) {
            host = args[0];
            try {
                port = Integer.parseUnsignedInt(args[1]);

            } catch (NumberFormatException e) {
                System.out.println("Second argument must be the port");
                System.exit(-1);
            }
        }

        System.out.println("port provided: " + port);


        Client.stdin = new Scanner(System.in);

        String cliGUI;
        String rmiSocket;
        do {
            System.out.println("CLI or GUI [c/g]? ");
            cliGUI = stdin.nextLine();
        } while (!cliGUI.equals("c") && !cliGUI.equals("g"));

        if (cliGUI.equals("g")) {
            GUI.setPort(port);
            GUI.setHost(host);
            Application.launch(GUI.class, args);
        } else {

            do {
                System.out.println("RMI or Socket [r/s]? ");
                rmiSocket = stdin.nextLine();
            } while (!rmiSocket.equals("r") && !rmiSocket.equals("s"));


            if (rmiSocket.equals("r"))
                rmiClient(host, port == 0 ? DEFAULT_RMI : port);
            else
                new CLI(stdin, host, port == 0 ? DEFAULT_SOCKET : port).run();
        }
    }


    /**
     * Start a new RMI client.
     * @param host the IP address of the registry.
     * @param port the port number of the registry.
     */
    private static void rmiClient(String host, int port) {
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
                System.out.println("Can't find lobbymanager");
                System.exit(-1);
            }
            System.out.println("found remote object");

            //new client's remote object
            new RemoteCLI(lobbyManager, stdin).run();
        } catch (RemoteException e) {
            System.out.println("Unable to find the registry, check hostname and port");
            e.printStackTrace();
        }
    }

}
