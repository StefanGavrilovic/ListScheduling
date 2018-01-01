/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.io.File;
import logic.Edge;
import utils.logic.Graphs;
import logic.NodeGraph;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseButton;
import javafx.scene.text.Font;
import javafx.scene.transform.Translate;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import utils.logic.ListSchedulings;
import logic.StateMachine;

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
    private Label legHeader;
    private TextArea legBody;
    private ComboBox test;

    /**
     * Graph.
     */
    private List<NodeGraph> nodes;
    private List<Edge> edges;
    private Group graph = null;

    /**
     * Main Stage, scene, root, etc.
     */
    public static boolean algFinished = false;
    private Stage mainStage = null;
    private Group root = null;
    private Scene scene = null;
    private ExecutionUnit subRoot = null;
    private SubScene subScene = null;
    final private MyTimer timer = new MyTimer();
    private boolean startedTimer = false;
    private StateMachine.state currentState;
    private boolean fileLoaded = false;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    public static void setAlgorithmFinished() {
        algFinished = true;
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Execution Finished");
        a.setHeaderText("Simulation has finished");
        a.setContentText("Please reload current program or load another program and start algorithm.");
        a.showAndWait();
    }

    @Override
    public void start(Stage primaryStage) {
        mainStage = primaryStage;
        root = new Group();
        subRoot = new ExecutionUnit(2, WINDOW_HEIGHT - WINDOW_TEXT_AREA_HEIGHT - 3 * WINDOW_HEIGHT_BUTTON);

        currentState = state.START;
        fileLoaded = false;
        algFinished = false;
        makeMenu();

        subScene = new SubScene(subRoot, WINDOW_WIDTH, WINDOW_HEIGHT - WINDOW_TEXT_AREA_HEIGHT - 3 * WINDOW_HEIGHT_BUTTON);
        subScene.setTranslateY(WINDOW_TEXT_AREA_HEIGHT + 3 * WINDOW_HEIGHT_BUTTON);
        //subScene.setFill(Color.LIGHTGRAY);
        root.getChildren().addAll(controlBar, subScene);
        scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        //scene.setFill(new ImagePattern(new Image("resources/hexagon-prisms.jpg")));
//        scene.setFill(new ImagePattern(new Image("resources/coolCubes.jpg")));
        //scene.setFill(new ImagePattern(new Image("resources/stars.jpg")));

        primaryStage.setTitle("List Scheduling");
        primaryStage.setScene(scene);
        primaryStage.setX(Screen.getPrimary().getVisualBounds().getMinX());
        primaryStage.setY(Screen.getPrimary().getVisualBounds().getMinY());
        primaryStage.setWidth(WINDOW_WIDTH);
        primaryStage.setHeight(WINDOW_HEIGHT);
        primaryStage.setResizable(false);
        primaryStage.show();
        if (!startedTimer) {
            timer.start();
            startedTimer = true;
        }
    }

    @Override
    public void execute() {
        switch (currentState) {
            case START:
                initApplication(false);
                break;
            case LOAD_GRAPH:
                showGraph();
                subRoot.removeList();
                break;
            case INSPECT_GRAPH:
                nodes.forEach(n -> {
                    n.setDelayCriticalPath(-1);
                    n.changeBodyColor(ListSchedulings.PREPARE);
                });
                Graphs.removeDeadCode(nodes, edges);
                //Graphs.checkForNewDeadCode(nodes, edges);
                Graphs.removeTransientLinks(edges);
                subRoot.makeList(nodes.size());
                break;
            case CRITICAL_PATH:
                nodes.forEach(n -> {
                    n.setNodeWeight(-1.0f);
                    n.setDelayCriticalPath(-1);
                    n.changeBodyColor(ListSchedulings.PREPARE);
                });
                Graphs.criticalPath(nodes);
                break;
            case RUN_ALGORITHM:
                Graphs.determineHeuristicWeights(nodes);
                break;
            case EXECUTE:
                ListSchedulings.executeInstruction(nodes, edges, subRoot);
                break;
        }
    }

    @Override
    public void nextState() {
        if (currentState != state.EXECUTE) {
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
    private void initApplication(final boolean complete) {
        if (complete) {
            mainStage.close();
        }
        root = null;
        scene = null;
        graph = null;
        subScene = null;
        subRoot = null;
        Optional.ofNullable(edges).ifPresent(e -> e.clear());
        Optional.ofNullable(nodes).ifPresent(n -> n.clear());
        clearMenu();
        start(complete ? new Stage() : mainStage);
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

        graph.setTranslateX((WINDOW_WIDTH - WINDOW_WIDTH_UTILS) / 2);
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
        textFromFile.setWrapText(true);
        textFromFile.setEditable(false);
        textFromFile.setText("Press load button and import program file.");
        textFromFile.setOpacity(0.5);

        textFromFile.setMaxSize(WINDOW_WIDTH_UTILS, WINDOW_TEXT_AREA_HEIGHT);
        textFromFile.setMinSize(WINDOW_WIDTH_UTILS, WINDOW_TEXT_AREA_HEIGHT);
        textFromFile.setFont(new Font(FONT_SIZE));

        textPath = new TextArea("Relative path..");
        textPath.setMaxSize(WINDOW_WIDTH_UTILS, WINDOW_HEIGHT_BUTTON);
        textPath.setMinSize(WINDOW_WIDTH_UTILS, WINDOW_HEIGHT_BUTTON);
        textPath.setTranslateY(WINDOW_TEXT_AREA_HEIGHT);
        textPath.setOpacity(0.5);

        //legend
        Label headline = new Label("LEGEND");
        headline.setAlignment(Pos.CENTER);
        headline.setTranslateY(WINDOW_TEXT_AREA_HEIGHT + WINDOW_HEIGHT_BUTTON * 3);
        headline.setMaxSize(WINDOW_WIDTH_UTILS, WINDOW_HEIGHT_BUTTON);
        headline.setMinSize(WINDOW_WIDTH_UTILS, WINDOW_HEIGHT_BUTTON);
        headline.setFont(new Font(FONT_SIZE));

        test = new ComboBox(FXCollections.observableArrayList(
                "Node Color",
                "Edge Color"
        ));
        test.setOnAction(event -> legBody.setText(((String) test.getValue()).equals("Node Color")
                ? ListSchedulings.legendNodeGraph() : Edge.legendEdge()));
        test.setValue("Node Color");
        test.setMinSize(WINDOW_WIDTH_UTILS, WINDOW_HEIGHT_BUTTON);
        test.setMaxSize(WINDOW_WIDTH_UTILS, WINDOW_HEIGHT_BUTTON);
        test.setTranslateY(WINDOW_TEXT_AREA_HEIGHT + WINDOW_HEIGHT_BUTTON * 4);

        legBody = new TextArea(ListSchedulings.legendNodeGraph());
        legBody.setWrapText(true);
        legBody.setTranslateY(WINDOW_TEXT_AREA_HEIGHT + WINDOW_HEIGHT_BUTTON * 5);
        legBody.setMinSize(WINDOW_WIDTH_UTILS, WINDOW_HEIGHT - legBody.getTranslateY());
        legBody.setMaxSize(WINDOW_WIDTH_UTILS, WINDOW_HEIGHT - legBody.getTranslateY());
        legBody.setEditable(false);
        
        controlBar.getChildren().addAll(buttonBar, textFromFile, textPath, headline, test, legBody);
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
            Optional.ofNullable(txtFile).ifPresent(file -> {
                try {
                    initApplication(false);
                    textFromFile.setText(new String(Files.readAllBytes(file.toPath())));
                    textPath.setText(txtFile.getPath());
                    fileLoaded = true;
                } catch (IOException ex) {
                    textFromFile.setText("File not found. Load again.");
                    fileLoaded = false;
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        });

        buttonReload = createButton("Reload", WINDOW_WIDTH_BUTTON,
                WINDOW_TEXT_AREA_HEIGHT + WINDOW_HEIGHT_BUTTON);
        buttonReload.setOnMouseClicked((event) -> {
            String file = textFromFile.getText();
            String pathFile = textPath.getText();
            initApplication(true);
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
            buttonNext.setDisable(!fileLoaded || algFinished);
            buttonPrev.setDisable(currentState == state.START || currentState == state.EXECUTE);
            buttonLoad.setDisable(currentState != state.START && !algFinished);
            buttonReload.setDisable(!fileLoaded);
        }

    }

}
