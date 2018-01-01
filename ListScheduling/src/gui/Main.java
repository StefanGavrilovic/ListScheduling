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
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseButton;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
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

    /**
     * Constants.
     */
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
    private Group buttonBar;
    private Group execution;
    private ComboBox setCores;
    private TextArea legBody;
    private ComboBox setLegend;

    /**
     * Graph.
     */
    private List<NodeGraph> nodes;
    private List<Edge> edges;
    private Group graph = null;
    private Text graphLabel = null;

    /**
     * Main Stage, scene, root, etc.
     */
    private Stage mainStage = null;
    private Group root = null;
    private ExecutionUnit execUnitRoot = null;
    private Group controlBarRoot = null;
    private ScrollPane graphRoot = null;
    private ScrollPane cpuRoot = null;
    private Scene scene = null;
    private SubScene subSceneEU = null;
    private SubScene subSceneGraph = null;

    /**
     * Timer, help variables, etc.
     */
    final private MyTimer timer = new MyTimer();
    public static boolean algFinished = false;
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
        currentState = state.START;
        fileLoaded = false;
        algFinished = false;
        graphLabel = new Text(0, FONT_SIZE, "Graph Representation - Load Program");

        root = new Group();
        makeMenu();
        execUnitRoot = new ExecutionUnit(Integer.parseInt(((String) setCores.getValue()).split(" ")[0]),
                WINDOW_HEIGHT - WINDOW_TEXT_AREA_HEIGHT - 3 * WINDOW_HEIGHT_BUTTON);
        cpuRoot = new ScrollPane(execUnitRoot);
        cpuRoot.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        cpuRoot.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        cpuRoot.setPannable(true);

        subSceneEU = new SubScene(cpuRoot, WINDOW_WIDTH - WINDOW_WIDTH_UTILS, WINDOW_HEIGHT - WINDOW_TEXT_AREA_HEIGHT - 3 * WINDOW_HEIGHT_BUTTON);
        subSceneEU.setTranslateY(WINDOW_TEXT_AREA_HEIGHT + 3 * WINDOW_HEIGHT_BUTTON);
        //subScene.setFill(Color.LIGHTGRAY);

        root.getChildren().addAll(controlBarRoot, subSceneEU, graphLabel);
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
                graphLabel.toFront();
                break;
            case LOAD_GRAPH:
                showGraph();
                execUnitRoot.removeList();
                graphLabel.setText("Graph Representation - Generate Graph");
                graphLabel.toFront();
                break;
            case INSPECT_GRAPH:
                nodes.forEach(n -> {
                    n.setDelayCriticalPath(-1);
                    n.changeBodyColor(ListSchedulings.PREPARE);
                });
                Graphs.removeDeadCode(nodes, edges);
                //Graphs.checkForNewDeadCode(nodes, edges);
                Graphs.removeTransientLinks(edges);
                execUnitRoot.makeList(nodes.size());
                graphLabel.setText("Graph Representation - Inspect Graph");
                break;
            case CRITICAL_PATH:
                nodes.forEach(n -> {
                    n.setNodeWeight(-1.0f);
                    n.setDelayCriticalPath(-1);
                    n.changeBodyColor(ListSchedulings.PREPARE);
                });
                Graphs.criticalPath(nodes);
                graphLabel.setText("Graph Representation - Determine Critical Path");
                break;
            case RUN_ALGORITHM:
                Graphs.determineHeuristicWeights(nodes);
                graphLabel.setText("Graph Representation - Determine Weights");
                break;
            case EXECUTE:
                ListSchedulings.executeInstruction(nodes, edges, execUnitRoot);
                graphLabel.setText("Graph Representation - Execute");
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
        subSceneEU = null;
        subSceneGraph = null;
        execUnitRoot = null;
        graphRoot = null;
        cpuRoot = null;
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
            graphRoot = null;
        }
        graph = new Group();
        graphRoot = new ScrollPane(graph);
        graphRoot.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        graphRoot.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        graphRoot.setPannable(true);
        //graphRoot.setMinViewportHeight(WINDOW_TEXT_AREA_HEIGHT + 3 * WINDOW_HEIGHT_BUTTON);
        //graphRoot.setMinViewportWidth(WINDOW_WIDTH - WINDOW_WIDTH_UTILS);
        //graphRoot.setMaxSize(WINDOW_WIDTH - WINDOW_WIDTH_UTILS, WINDOW_TEXT_AREA_HEIGHT + 3 * WINDOW_HEIGHT_BUTTON);
        //graphRoot.setMinSize(WINDOW_WIDTH - WINDOW_WIDTH_UTILS, WINDOW_TEXT_AREA_HEIGHT + 3 * WINDOW_HEIGHT_BUTTON);
        subSceneGraph = new SubScene(graphRoot, WINDOW_WIDTH - WINDOW_WIDTH_UTILS, WINDOW_TEXT_AREA_HEIGHT + 3 * WINDOW_HEIGHT_BUTTON);

        nodes = Graphs.makeGraphLogic(textPath.getText());
        edges = Graphs.drawGraph(nodes, graph);

        graph.setTranslateX(WINDOW_WIDTH / 40);
        graph.setTranslateY(WINDOW_HEIGHT / 20);
        root.getChildren().add(subSceneGraph);
    }

    /**
     * This method is used for making control menu for interacting with
     * application.
     */
    private void makeMenu() {
        controlBarRoot = new Group();

        //control input
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

        makeButtonBar();

        //control core
        setCores = new ComboBox(FXCollections.observableArrayList(
                "1 Core",
                "2 Cores",
                "4 Cores"
        ));
        setCores.setValue("1 Core");
        setCores.setOnAction(event -> {
            Optional.ofNullable(execUnitRoot)
                    .ifPresent(s -> Optional.ofNullable(setCores.getValue())
                    .ifPresent(val -> s.reRender(Integer.parseInt(((String) val).split(" ")[0]))));
        });
        setCores.setMinSize(WINDOW_WIDTH_UTILS, WINDOW_HEIGHT_BUTTON);
        setCores.setMaxSize(WINDOW_WIDTH_UTILS, WINDOW_HEIGHT_BUTTON);
        setCores.setTranslateY(WINDOW_TEXT_AREA_HEIGHT + WINDOW_HEIGHT_BUTTON * 3);

        //legend
        Label headline = new Label("LEGEND");
        headline.setAlignment(Pos.CENTER);
        headline.setTranslateY(WINDOW_TEXT_AREA_HEIGHT + WINDOW_HEIGHT_BUTTON * 4);
        headline.setMaxSize(WINDOW_WIDTH_UTILS, WINDOW_HEIGHT_BUTTON);
        headline.setMinSize(WINDOW_WIDTH_UTILS, WINDOW_HEIGHT_BUTTON);
        headline.setFont(new Font(FONT_SIZE));

        setLegend = new ComboBox(FXCollections.observableArrayList(
                "Node Color",
                "Edge Color"
        ));
        setLegend.setOnAction(event -> legBody.setText(((String) setLegend.getValue()).equals("Node Color")
                ? ListSchedulings.legendNodeGraph() : Edge.legendEdge()));
        setLegend.setValue("Node Color");
        setLegend.setMinSize(WINDOW_WIDTH_UTILS, WINDOW_HEIGHT_BUTTON);
        setLegend.setMaxSize(WINDOW_WIDTH_UTILS, WINDOW_HEIGHT_BUTTON);
        setLegend.setTranslateY(WINDOW_TEXT_AREA_HEIGHT + WINDOW_HEIGHT_BUTTON * 5);

        legBody = new TextArea(ListSchedulings.legendNodeGraph());
        legBody.setWrapText(true);
        legBody.setTranslateY(WINDOW_TEXT_AREA_HEIGHT + WINDOW_HEIGHT_BUTTON * 6);
        legBody.setMinSize(WINDOW_WIDTH_UTILS, WINDOW_HEIGHT - legBody.getTranslateY());
        legBody.setMaxSize(WINDOW_WIDTH_UTILS, WINDOW_HEIGHT - legBody.getTranslateY());
        legBody.setEditable(false);

        controlBarRoot.getChildren().addAll(buttonBar, textFromFile, textPath, setCores, headline, setLegend, legBody);
        controlBarRoot.getTransforms().add(new Translate(WINDOW_WIDTH - WINDOW_WIDTH_UTILS - WINDOW_TOLERANCE, 0));
    }

    /**
     * This method is used for deleting control menu.
     */
    private void clearMenu() {
        controlBarRoot = null;
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
                    String tmpCoreNum = (String) setCores.getValue();
                    initApplication(false);
                    setCores.setValue(tmpCoreNum);
                    execUnitRoot.reRender(Integer.parseInt(tmpCoreNum.split(" ")[0]));
                    textFromFile.setText(new String(Files.readAllBytes(file.toPath())));
                    textPath.setText(txtFile.getPath());
                    fileLoaded = true;
                    graphLabel.setText("Graph Representation - Start State");
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
            graphLabel.setText("Graph Representation - Start State");
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
            setCores.setDisable(currentState != state.START);
        }

    }

}
