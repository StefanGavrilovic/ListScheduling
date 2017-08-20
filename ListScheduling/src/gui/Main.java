/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import graph.Graph;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 *
 * @author Stefan
 */
public class Main extends Application {

    private static final double WINDOW_HEIGHT = Screen.getPrimary().getVisualBounds().getHeight();
    private static final double WINDOW_WIDTH = Screen.getPrimary().getVisualBounds().getWidth();
    private static final double WINDOW_WIDTH_UTILS = WINDOW_WIDTH / 6;
    private static final double WINDOW_TEXT_AREA_HEIGHT = WINDOW_HEIGHT / 2;
    private static final double WINDOW_WIDTH_BUTTON = WINDOW_WIDTH_UTILS / 2;
    private static final double WINDOW_HEIGHT_BUTTON = WINDOW_HEIGHT / 20;
    private static final double WINDOW_TOLERANCE = 4;
    private static final double FONT_SIZE = 20;
    
    private Graph2D graph;
    
    @Override
    public void start(Stage primaryStage) {

        Group root = new Group();

        graph = new Graph2D(new Graph("test2.txt"));
        graph.setTranslateX(WINDOW_WIDTH / 2);
        graph.setTranslateY(WINDOW_HEIGHT / 20);
        
        //button nExt
        Button buttonNext = new Button("Next");
        buttonNext.setMouseTransparent(false);
        buttonNext.setMinSize(WINDOW_WIDTH_BUTTON, WINDOW_HEIGHT_BUTTON);
        buttonNext.setMaxSize(WINDOW_WIDTH_BUTTON, WINDOW_HEIGHT_BUTTON);
        buttonNext.setOnMouseClicked((event) -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                buttonNext.setText("NEXT");
                graph.getGraph().removeGraphLinks();
            }
        });
        buttonNext.setTranslateX(WINDOW_WIDTH - WINDOW_WIDTH_UTILS - WINDOW_TOLERANCE + WINDOW_WIDTH_BUTTON);
        buttonNext.setTranslateY(WINDOW_TEXT_AREA_HEIGHT + WINDOW_HEIGHT_BUTTON / 2 + WINDOW_HEIGHT_BUTTON);
        
        //button Prev
        Button buttonPrev = new Button("Prev");
        buttonPrev.setMouseTransparent(false);
        buttonPrev.setMinSize(WINDOW_WIDTH_BUTTON, WINDOW_HEIGHT_BUTTON);
        buttonPrev.setMaxSize(WINDOW_WIDTH_BUTTON, WINDOW_HEIGHT_BUTTON);
        buttonPrev.setOnMouseClicked((event) -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                buttonPrev.setText("PREV");
            }
        });
        buttonPrev.setTranslateX(WINDOW_WIDTH - WINDOW_WIDTH_UTILS - WINDOW_TOLERANCE);
        buttonPrev.setTranslateY(WINDOW_TEXT_AREA_HEIGHT + WINDOW_HEIGHT_BUTTON / 2 + WINDOW_HEIGHT_BUTTON);
        
        //button Load
        Button buttonLoad = new Button("Load");
        buttonLoad.setMouseTransparent(false);
        buttonLoad.setMinSize(WINDOW_WIDTH_BUTTON, WINDOW_HEIGHT_BUTTON);
        buttonLoad.setMaxSize(WINDOW_WIDTH_BUTTON, WINDOW_HEIGHT_BUTTON);
        buttonLoad.setOnMouseClicked((event) -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                buttonLoad.setText("LOAD");
            }
        });
        buttonLoad.setTranslateX(WINDOW_WIDTH - WINDOW_WIDTH_UTILS - WINDOW_TOLERANCE);
        buttonLoad.setTranslateY(WINDOW_TEXT_AREA_HEIGHT + WINDOW_HEIGHT_BUTTON / 2);
        
        //button Reload
        Button buttonReload = new Button("Reload");
        buttonReload.setMouseTransparent(false);
        buttonReload.setMinSize(WINDOW_WIDTH_BUTTON, WINDOW_HEIGHT_BUTTON);
        buttonReload.setMaxSize(WINDOW_WIDTH_BUTTON, WINDOW_HEIGHT_BUTTON);
        buttonReload.setOnMouseClicked((event) -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                buttonReload.setText("RELOAD");
            }
        });
        buttonReload.setTranslateX(WINDOW_WIDTH - WINDOW_WIDTH_UTILS - WINDOW_TOLERANCE + WINDOW_WIDTH_BUTTON);
        buttonReload.setTranslateY(WINDOW_TEXT_AREA_HEIGHT + WINDOW_HEIGHT_BUTTON / 2);
        
        //text - operations
        TextArea text = new TextArea();
        text.setEditable(false);
        try {
            text.setText(new String(Files.readAllBytes(Paths.get("test2.txt"))));
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            text.setText("File not found. Check file path.");
        }
        text.setMaxSize(WINDOW_WIDTH_UTILS, WINDOW_TEXT_AREA_HEIGHT);
        text.setMinSize(WINDOW_WIDTH_UTILS, WINDOW_TEXT_AREA_HEIGHT);
        text.setTranslateX(WINDOW_WIDTH - WINDOW_WIDTH_UTILS - WINDOW_TOLERANCE);
        text.setFont(new Font(FONT_SIZE));
        
        //text - path
        TextArea textPath = new TextArea("Relative path..");
        textPath.setMaxSize(WINDOW_WIDTH_UTILS, WINDOW_HEIGHT_BUTTON / 2);
        textPath.setMinSize(WINDOW_WIDTH_UTILS, WINDOW_HEIGHT_BUTTON / 2);
        textPath.setTranslateX(WINDOW_WIDTH - WINDOW_WIDTH_UTILS - WINDOW_TOLERANCE);
        textPath.setTranslateY(WINDOW_TEXT_AREA_HEIGHT);
        
        root.getChildren().addAll(graph, buttonNext, buttonPrev, buttonLoad, buttonReload, text, textPath);
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

        primaryStage.setTitle("List Scheduling");
        primaryStage.setScene(scene);
        primaryStage.setX(Screen.getPrimary().getVisualBounds().getMinX());
        primaryStage.setY(Screen.getPrimary().getVisualBounds().getMinY());
        primaryStage.setWidth(WINDOW_WIDTH);
        primaryStage.setHeight(WINDOW_HEIGHT);
        primaryStage.setResizable(false);
        primaryStage.show();
        
        // calling update once every frame
        /*new AnimationTimer() {
            @Override
            public void handle(long currentNanoTime) {
                update();
            }
        }.start();*/
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
