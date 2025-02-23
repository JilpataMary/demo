package com.godown.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the FXML layout
        Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
        
        // Set the title of the window
        primaryStage.setTitle("Rice Godown Billing System");
        
        // Create the Scene
        Scene scene = new Scene(root);
        
        // Set the scene to the stage
        primaryStage.setScene(scene);
        
        // Maximize the window (makes the window occupy the full screen size)
        primaryStage.setMaximized(true);
        
        // Show the stage
        primaryStage.show();
    }
      
    public static void main(String[] args) {
        launch(args);
    }
}
