/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import graph.Graph;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 *
 * @author Stefan
 */
public class Main extends Application {

    private static final double WINDOW_HEIGHT = Screen.getPrimary().getVisualBounds().getHeight();
    private static final double WINDOW_WIDTH = Screen.getPrimary().getVisualBounds().getWidth();
    private static final int WINDOW_UTILS = 100;
    
    private Graph2D graph;
    
    private void update() {
        
    }
    
    @Override
    public void start(Stage primaryStage) {

        Group root = new Group();

        graph = new Graph2D(new Graph("test2.txt"));
        graph.setTranslateX(WINDOW_WIDTH / 2);
        graph.setTranslateY(WINDOW_HEIGHT / 20);
        
        Button button = new Button("Next");
        button.setMouseTransparent(false); //for button blocking
        button.resize(100, 20);
        
        root.getChildren().addAll(graph, button);
        
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        
        primaryStage.setTitle("List Scheduling");
        primaryStage.setScene(scene);
        primaryStage.setX(Screen.getPrimary().getVisualBounds().getMinX());
        primaryStage.setY(Screen.getPrimary().getVisualBounds().getMinY());
        primaryStage.setWidth(WINDOW_WIDTH);
        primaryStage.setHeight(WINDOW_HEIGHT);
        primaryStage.show();
        
        // calling update once every frame
        new AnimationTimer() {
            @Override
            public void handle(long currentNanoTime) {
                update();
            }
        }.start();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
