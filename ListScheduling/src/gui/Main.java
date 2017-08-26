/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import listscheduling.Edge;
import listscheduling.Graphs;
import listscheduling.NodeGraph;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
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
import javafx.scene.transform.Translate;
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
    
    /**
     * Control menu.
     */
    private Button buttonNext;
    private Button buttonPrev;
    private Button buttonLoad;
    private Button buttonReload;
    private TextArea textFromFile;
    private TextArea textPath;
    private Group controlBar;
    private Group buttonBar;
    
    /**
     * Graph.
     */
    private List<NodeGraph> nodes;
    private List<Edge> edges;
    private Group graph;
    
    /**
     * Scene, root, etc.
     */
    private Group root;
    private Scene scene;
    private String fileName = "test2.txt";
    
    @Override
    public void start(Stage primaryStage) {

        root = new Group();
        graph = new Group();
        
        nodes = Graphs.makeGraphLogic(fileName);
        edges = Graphs.drawGraph(nodes, graph);
        
        graph.setTranslateX(WINDOW_WIDTH / 2);
        graph.setTranslateY(WINDOW_HEIGHT / 20);

        makeMenu();

        root.getChildren().addAll(graph, controlBar);
        scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

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

    /**
     * This method is used for making control menu for interacting with
     * application.
     */
    private void makeMenu() {
        controlBar = new Group();
        makeButtonBar();

        textFromFile = new TextArea();
        textFromFile.setEditable(false);
        try {
            textFromFile.setText(new String(Files.readAllBytes(Paths.get(fileName))));
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            textFromFile.setText("File not found. Check file path.");
        }
        textFromFile.setMaxSize(WINDOW_WIDTH_UTILS, WINDOW_TEXT_AREA_HEIGHT);
        textFromFile.setMinSize(WINDOW_WIDTH_UTILS, WINDOW_TEXT_AREA_HEIGHT);
        textFromFile.setFont(new Font(FONT_SIZE));

        textPath = new TextArea("Relative path..");
        textPath.setMaxSize(WINDOW_WIDTH_UTILS, WINDOW_HEIGHT_BUTTON / 2);
        textPath.setMinSize(WINDOW_WIDTH_UTILS, WINDOW_HEIGHT_BUTTON / 2);
        textPath.setTranslateY(WINDOW_TEXT_AREA_HEIGHT);
        
        controlBar.getChildren().addAll(buttonBar, textFromFile, textPath);
        controlBar.getTransforms().add(new Translate(WINDOW_WIDTH - WINDOW_WIDTH_UTILS - WINDOW_TOLERANCE, 0));
    }

    private void makeButtonBar() {
        buttonBar = new Group();
        
        buttonNext = new Button("Next");
        buttonNext.setMouseTransparent(false);
        buttonNext.setMinSize(WINDOW_WIDTH_BUTTON, WINDOW_HEIGHT_BUTTON);
        buttonNext.setMaxSize(WINDOW_WIDTH_BUTTON, WINDOW_HEIGHT_BUTTON);
        buttonNext.setOnMouseClicked((event) -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                buttonNext.setText("NEXT");
                Graphs.removeGraphLinks(edges);
            }
        });
        buttonNext.setTranslateX(WINDOW_WIDTH_BUTTON);
        buttonNext.setTranslateY(WINDOW_TEXT_AREA_HEIGHT + WINDOW_HEIGHT_BUTTON / 2 + WINDOW_HEIGHT_BUTTON);

        buttonPrev = new Button("Prev");
        buttonPrev.setMouseTransparent(false);
        buttonPrev.setMinSize(WINDOW_WIDTH_BUTTON, WINDOW_HEIGHT_BUTTON);
        buttonPrev.setMaxSize(WINDOW_WIDTH_BUTTON, WINDOW_HEIGHT_BUTTON);
        buttonPrev.setOnMouseClicked((event) -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                buttonPrev.setText("PREV");
            }
        });
        buttonPrev.setTranslateY(WINDOW_TEXT_AREA_HEIGHT + WINDOW_HEIGHT_BUTTON / 2 + WINDOW_HEIGHT_BUTTON);

        buttonLoad = new Button("Load");
        buttonLoad.setMouseTransparent(false);
        buttonLoad.setMinSize(WINDOW_WIDTH_BUTTON, WINDOW_HEIGHT_BUTTON);
        buttonLoad.setMaxSize(WINDOW_WIDTH_BUTTON, WINDOW_HEIGHT_BUTTON);
        buttonLoad.setOnMouseClicked((event) -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                buttonLoad.setText("LOAD");
            }
        });
        buttonLoad.setTranslateY(WINDOW_TEXT_AREA_HEIGHT + WINDOW_HEIGHT_BUTTON / 2);

        buttonReload = new Button("Reload");
        buttonReload.setMouseTransparent(false);
        buttonReload.setMinSize(WINDOW_WIDTH_BUTTON, WINDOW_HEIGHT_BUTTON);
        buttonReload.setMaxSize(WINDOW_WIDTH_BUTTON, WINDOW_HEIGHT_BUTTON);
        buttonReload.setOnMouseClicked((event) -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                buttonReload.setText("RELOAD");
            }
        });
        buttonReload.setTranslateX(WINDOW_WIDTH_BUTTON);
        buttonReload.setTranslateY(WINDOW_TEXT_AREA_HEIGHT + WINDOW_HEIGHT_BUTTON / 2);
        
        buttonBar.getChildren().addAll(buttonNext, buttonPrev, buttonLoad, buttonReload);
    }
    
}
