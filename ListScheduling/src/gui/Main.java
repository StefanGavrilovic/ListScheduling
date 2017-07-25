/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import graph.Graph;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.stage.Stage;

/**
 *
 * @author Stefan
 */
public class Main extends Application {

    private static final int WINDOW_HEIGHT = 800;
    private static final int WINDOW_WIDTH = 1024;
    private static final int WINDOW_UTILS = 100;
    
    private Graph graph;
    
    @Override
    public void start(Stage primaryStage) {

        Group root = new Group();

        graph = new Graph("test2.txt");
        Graph2D g = new Graph2D(graph);
        g.setTranslateX(WINDOW_WIDTH / 2);
        g.setTranslateY(WINDOW_HEIGHT / 20);
        root.getChildren().add(g);

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.setOnMouseClicked(e -> {
            graph.removeGraphLinks();
            Graph2D test = new Graph2D(graph);
            test.setTranslateX(WINDOW_WIDTH / 2);
            test.setTranslateY(WINDOW_HEIGHT / 15);
            root.getChildren().add(test);
        });
        //SubScene test = new SubScene(root, WINDOW_WIDTH, WINDOW_HEIGHT - WINDOW_UTILS);
        
        primaryStage.setTitle("List Scheduling");
        primaryStage.setScene(scene);
        //primaryStage.setFullScreen(true);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
