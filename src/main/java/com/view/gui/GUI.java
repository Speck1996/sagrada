package com.view.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.io.*;

/**
 * Primary class for GUI client, initializes and sets to Sagrada the title of App Stage.
 */
public class GUI extends Application{

    private static Stage primaryStage;
    private static int port;
    private static String host;

    /**
     * Changes the value of port if you don't want to use default value.
     * @param p       new port passed.
     */
    public static void setPort(int p) {
        port = p;
    }

    /**
     * Gets the value of port, default or set by user if he or she done it.
     * @return this port.
     */
    static int getPort() {
        return port;
    }

    /**
     * Changes the value of host if you don't want to use default value.
     * @param h       new host passed.
     */
    public static void setHost(String h) {
        host = h;
    }

    /**
     * Gets the value of host, default or set by user if he or she done it.
     * @return this host.
     */
    static String getHost() {
        return host;
    }

    /**
     * Gets the stage of this Application.
     * @return this stage.
     */
    public static Stage getStage() {
        return primaryStage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("Sagrada");
        showStartScene();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void handle(WindowEvent event) {
                Platform.exit();
                System.exit(0);
            }
        });
    }

    /**
     * Shows the Start Scene
     */
    private void showStartScene() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/startScene.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            System.out.println("Unable to load FXML StartScene file");
        }
        primaryStage.setScene(new Scene(root,1000,600));
        primaryStage.show();
    }

    /**
     * {@inheritDoc}
     */
    public static void main(String[] args) {
        launch(args);
    }
}
