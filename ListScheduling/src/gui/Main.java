/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.io.File;
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
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.transform.Translate;
import javafx.stage.FileChooser;
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
    private Group execution;

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
    private MyTimer timer = new MyTimer();
    private boolean startedTimer = false;
    private StateMachine.state currentState;
    private boolean fileLoaded = false;

    @Override
    public void start(Stage primaryStage) {
        mainStage = primaryStage;
        root = new Group();

        currentState = state.START;
        fileLoaded = false;
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
        if (!startedTimer){
            timer.start();
            startedTimer = true;
        }
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
                nodes.forEach(n -> {
                    n.setDelayCriticalPath(-1);
                    n.changeBodyColor(Color.YELLOW);
                        });
                Graphs.removeDeadCode(nodes, edges);
                //Graphs.checkForNewDeadCode(nodes, edges);
                Graphs.removeTransientLinks(edges);
                break;
            case CRITICAL_PATH:
                nodes.forEach(n -> {
                    n.setNodeWeight(-1.0f);
                    n.setDelayCriticalPath(-1);
                    n.changeBodyColor(Color.YELLOW);
                        });
                Graphs.criticalPath(nodes);
                break;
            case RUN_ALGORITHM:
                Graphs.determineHeuristicWeights(nodes);
                break;
            case EXECUTE:
                break;
        }
    }

    @Override
    public void nextState() {
        if (currentState != state.RUN_ALGORITHM) {
            currentState = state.values()[currentState.ordinal() + 1];
        }
    }

    @Override
    public void prevState() {
        if (currentState != state.START) {
            currentState = state.values()[currentState.ordinal() - 1];
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
            Arrays.stream((NodeGraph[]) nodes.toArray(new NodeGraph[0])).forEach(e -> Graphs.removeNode(nodes, edges, e));
            nodes.clear();
            edges.clear();
            graph = null;
        }
        graph = new Group();
        nodes = Graphs.makeGraphLogic(textPath.getText());
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
        textFromFile.setText("Press load button and import program file.");

        textFromFile.setMaxSize(WINDOW_WIDTH_UTILS, WINDOW_TEXT_AREA_HEIGHT);
        textFromFile.setMinSize(WINDOW_WIDTH_UTILS, WINDOW_TEXT_AREA_HEIGHT);
        textFromFile.setFont(new Font(FONT_SIZE));

        textPath = new TextArea("Relative path..");
        textPath.setMaxSize(WINDOW_WIDTH_UTILS, WINDOW_HEIGHT_BUTTON);
        textPath.setMinSize(WINDOW_WIDTH_UTILS, WINDOW_HEIGHT_BUTTON);
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
                WINDOW_TEXT_AREA_HEIGHT + WINDOW_HEIGHT_BUTTON + WINDOW_HEIGHT_BUTTON);
        buttonNext.setOnMouseClicked((event) -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                nextState();
                execute();
            }
        });

        buttonPrev = createButton("Prev", 0,
                WINDOW_TEXT_AREA_HEIGHT + WINDOW_HEIGHT_BUTTON + WINDOW_HEIGHT_BUTTON);
        buttonPrev.setOnMouseClicked((event) -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                prevState();
                execute();
            }
        });

        buttonLoad = createButton("Load", 0,
                WINDOW_TEXT_AREA_HEIGHT + WINDOW_HEIGHT_BUTTON);
        buttonLoad.setOnMouseClicked((event) -> {
            FileChooser fileImport = new FileChooser();
            fileImport.setTitle("Open Program File");
            fileImport.setInitialDirectory(Paths.get(System.getProperty("user.dir")).toFile());
            File txtFile = fileImport.showOpenDialog(mainStage);
            try {
                textFromFile.setText(new String(Files.readAllBytes(txtFile.toPath())));
                textPath.setText(txtFile.getPath());
                fileLoaded = true;
            } catch (IOException ex) {
                textFromFile.setText("File not found. Load again.");
                fileLoaded = false;
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        buttonReload = createButton("Reload", WINDOW_WIDTH_BUTTON,
                WINDOW_TEXT_AREA_HEIGHT + WINDOW_HEIGHT_BUTTON);
        buttonReload.setOnMouseClicked((event) -> {
            String file = textFromFile.getText();
            String pathFile = textPath.getText();
            initApplication();
            textFromFile.setText(file);
            textPath.setText(pathFile);
            fileLoaded = true;
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

    private class MyTimer extends AnimationTimer {

        @Override
        public void handle(long now) {
            //update buttons
            buttonNext.setDisable(!fileLoaded);
            buttonPrev.setDisable(currentState == state.START);
            buttonLoad.setDisable(currentState != state.START);
            buttonReload.setDisable(!fileLoaded);
        }

    }
}
