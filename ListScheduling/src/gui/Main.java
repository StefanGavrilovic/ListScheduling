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
import java.util.Arrays;
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
import listscheduling.StateMachine;

/**
 *
 * @author Stefan
 */
public class Main extends Application implements StateMachine {

    public static final double WINDOW_HEIGHT = Screen.getPrimary().getVisualBounds().getHeight();
    public static final double WINDOW_WIDTH = Screen.getPrimary().getVisualBounds().getWidth();
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
    private Group graph = null;

    /**
     * Stage, scene, root, etc.
     */
    private Stage mainStage = null;
    private Group root = null;
    private Scene scene = null;
    private final String fileName = "test2.txt";
    private StateMachine.state currentState;

    @Override
    public void start(Stage primaryStage) {
        mainStage = primaryStage;
        root = new Group();

        currentState = state.START;
        System.out.println(currentState.name() + " " + currentState.ordinal());
        makeMenu();

        root.getChildren().addAll(controlBar);
        scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

        primaryStage.setTitle("List Scheduling");
        primaryStage.setScene(scene);
        primaryStage.setX(Screen.getPrimary().getVisualBounds().getMinX());
        primaryStage.setY(Screen.getPrimary().getVisualBounds().getMinY());
        primaryStage.setWidth(WINDOW_WIDTH);
        primaryStage.setHeight(WINDOW_HEIGHT);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void execute() {
        switch (currentState) {
            case START:
                initApplication();
                break;
            case LOAD_GRAPH:
                showGraph();
                break;
            case INSPECT_GRAPH:
                Graphs.removeGraphLinks(edges);
                break;
            case CRITICAL_PATH:
                Graphs.criticalPath(nodes);
                break;
            case RUN_ALGORITHM:
                Graphs.determineHeuristicWeights(nodes);
                break;
        }
    }

    @Override
    public void nextState() {
        if (currentState != state.RUN_ALGORITHM) {
            System.out.println(currentState.name() + " " + currentState.ordinal());
            currentState = state.values()[currentState.ordinal() + 1];
            System.out.println(currentState.name() + " " + currentState.ordinal());
        }
    }

    @Override
    public void prevState() {
        if (currentState != state.START) {
            System.out.println(currentState.name() + " " + currentState.ordinal());
            currentState = state.values()[currentState.ordinal() - 1];
            System.out.println(currentState.name() + " " + currentState.ordinal());
        }
    }

    /**
     * Initialize application and creates all control and other parts of the
     * window of this application.
     */
    private void initApplication() {
        mainStage.close();
        root = null;
        scene = null;
        graph = null;
        edges.clear();
        nodes.clear();
        clearMenu();
        start(new Stage());
    }

    /**
     * This method is used for drawing graph on scene.
     */
    private void showGraph() {
        if (graph != null) {
            Arrays.stream((NodeGraph[])nodes.toArray(new NodeGraph[0])).forEach(e -> Graphs.removeNode(nodes, edges, e));
            nodes.clear();
            edges.clear();
            graph = null;
        }
        graph = new Group();
        nodes = Graphs.makeGraphLogic(fileName);
        edges = Graphs.drawGraph(nodes, graph);

        graph.setTranslateX(WINDOW_WIDTH / 2);
        graph.setTranslateY(WINDOW_HEIGHT / 20);
        root.getChildren().add(graph);
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

    /**
     * This method is used for deleting control menu.
     */
    private void clearMenu() {
        controlBar = null;
        buttonBar = null;
        buttonLoad = null;
        buttonNext = null;
        buttonPrev = null;
        buttonReload = null;
        textFromFile = null;
        textPath = null;
    }

    /**
     * This method is used for making button menu for interacting with
     * application.
     */
    private void makeButtonBar() {
        buttonBar = new Group();

        buttonNext = createButton("Next", WINDOW_WIDTH_BUTTON,
                WINDOW_TEXT_AREA_HEIGHT + WINDOW_HEIGHT_BUTTON / 2 + WINDOW_HEIGHT_BUTTON);
        buttonNext.setOnMouseClicked((event) -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                buttonNext.setText("NEXT");
                nextState();
                execute();
            }
        });

        buttonPrev = createButton("Prev", 0,
                WINDOW_TEXT_AREA_HEIGHT + WINDOW_HEIGHT_BUTTON / 2 + WINDOW_HEIGHT_BUTTON);
        buttonPrev.setOnMouseClicked((event) -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                buttonPrev.setText("PREV");
                prevState();
                execute();
            }
        });

        buttonLoad = createButton("Load", 0,
                WINDOW_TEXT_AREA_HEIGHT + WINDOW_HEIGHT_BUTTON / 2);
        buttonLoad.setOnMouseClicked((event) -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                buttonLoad.setText("LOAD");
            }
        });

        buttonReload = createButton("Reload", WINDOW_WIDTH_BUTTON,
                WINDOW_TEXT_AREA_HEIGHT + WINDOW_HEIGHT_BUTTON / 2);
        buttonReload.setOnMouseClicked((event) -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                buttonReload.setText("RELOAD");
            }
        });

        buttonBar.getChildren().addAll(buttonNext, buttonPrev, buttonLoad, buttonReload);
    }

    private Button createButton(String name, double x, double y) {
        Button button = new Button(name);
        button.setMouseTransparent(false);
        button.setMinSize(WINDOW_WIDTH_BUTTON, WINDOW_HEIGHT_BUTTON);
        button.setMaxSize(WINDOW_WIDTH_BUTTON, WINDOW_HEIGHT_BUTTON);
        button.setTranslateX(x);
        button.setTranslateY(y);
        return button;
    }

}
