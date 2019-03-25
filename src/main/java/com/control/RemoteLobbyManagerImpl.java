package com.control;

import com.model.LoginException;
import com.view.RemoteClient;
import com.model.MainModel;
import com.model.Player;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


/**
 * Impementation of the {@link RemoteLobbyManager} interface.
 * @see RemoteLobbyManager
 */
public class RemoteLobbyManagerImpl extends UnicastRemoteObject implements RemoteLobbyManager {

    private transient final MainModel model;


    /**
     * Constructs a RemoteLobbyManagerImpl
     * @throws RemoteException if rmi connection problem occurred.
     */
    public RemoteLobbyManagerImpl() throws RemoteException {
        super();
        model = MainModel.getModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized String login(String username, String password, RemoteClient client) throws RemoteException, LoginException {
        System.out.println("Logging in: " + username);

        String token;
        try {
            token = model.login(username, password);
        } catch (LoginException e) {
            System.out.println(e.getMessage());
            throw e;
        }

        client.printMessage("Hi " + username);
        System.out.println(username + " logged successfully");
        model.addRemoteClient(username, client);
        return token;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void logout(String token) throws RemoteException {
        if(model.getPlayerByToken(token) == null)
            return;

        model.logout(token);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void searchMultiplayerGame(String token, RemoteClient client) throws RemoteException {


        Player player = model.getPlayerByToken(token);

        if(player == null)
            return;

        System.out.println(player.getUsername() + " search a game");

        //check if the player was already in an ongoing match
        if(model.isPlayerInGame(player)) {

            System.out.println(player.getUsername() + " reentered");
            GameControllerImpl controller = model.getPlayerGame(player);
            client.resumeGame(controller);
        }
        else
            model.enqueuePlayer(token, client);


    }

    /**
     * {@inheritDoc}
     */
    @Override
    public  synchronized  void startSoloGame(String token, RemoteClient client) throws RemoteException {
        Player player = model.getPlayerByToken(token);

        if(player == null)
            throw new RemoteException("invalid token");

        System.out.println(player.getUsername() + " starting solo game");

        if(model.isPlayerInGame(player)) {

            System.out.println(player.getUsername() + " reentered");
            GameControllerImpl controller = model.getPlayerGame(player);
            client.resumeGame(controller);
        }
        else
            model.singlePlayerGame(token, client);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void dequeue(String token) throws RemoteException {
        model.dequeuePlayer(token);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStatistics() throws RemoteException {
        return model.getPlayerSortedByVictories();

    }
}
