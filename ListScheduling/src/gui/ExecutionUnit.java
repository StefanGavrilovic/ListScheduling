/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import listscheduling.NodeGraph;
import utils.GUIObjects;

/**
 * The Execution Unit class is used for creating sub scene for execution unit
 * for the list scheduling algorithm.
 *
 * @author zer0
 *
 * @see Group
 */
public class ExecutionUnit extends Group {

    /**
     * Width of graphical representation of CPU.
     */
    public static final double CPU_WIDTH = 200;
    /**
     * Height of graphical representation of CPU.
     */
    public static final double CPU_HEIGHT = 200;
    /**
     * Graphical representation of CPU.
     */
    private final Group processingUnit;
    /**
     * Graphical list of the graph nodes executed by processingUnit.
     */
    private final TilePane executionList;
    /**
     * List of the positions for executed instructions.
     */
    private List<Group> positionList;

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public ExecutionUnit(final int numOfCores, final double height) {
        processingUnit = makeExecutionUnit(numOfCores);
        processingUnit.setTranslateY(height / 2 - CPU_HEIGHT / 2);
        
        positionList = new LinkedList<>();
        executionList = new TilePane(Orientation.HORIZONTAL, 8, 8);
        executionList.setTranslateX(CPU_WIDTH);
        
        final Label label = new Label("Execution Unit");
        label.setLabelFor(processingUnit);
        
        this.getChildren().addAll(processingUnit, executionList, label);
    }

    /**
     * This method is used to create graphical representation of executed
     * instructions by CPU.
     *
     * @param numberOfNodes {@link Integer} Number of nodes loaded into CPU.
     */
    public void makeList(int numberOfNodes) {
        executionList.setPrefColumns(numberOfNodes);
        for (int i = 0; i < numberOfNodes; i++) {
            final Group core = GUIObjects.createCircleGroup(NodeGraph.NODE_RADIUS);
            core.setTranslateY(processingUnit.getTranslateY() + CPU_HEIGHT / 4 + 10);
            positionList.add(core);
        }
        executionList.getChildren().addAll(positionList);
    }

    /**
     * This method clear graphical representation of executed
     * instructions by CPU. 
     */
    public void removeList() {
        executionList.getChildren().clear();
        positionList.clear();
    }
    
    /**
     * This method adds instruction to the finished list.
     * 
     * @param group {@link Group} Added instruction.
     */
    public void addFinishedToList(final Group group) {
        executionList.getChildren().clear();
        positionList.remove(positionList.size() - 1);
        group.setTranslateX(0);
        group.setTranslateY(processingUnit.getTranslateY() + CPU_HEIGHT / 4 + 10);
        positionList.add(0, group);
        executionList.getChildren().addAll(positionList);
    }
    
    /**
     * This method is used to change currently executed instruction.
     * 
     * @param nextNode {@link Group} Instruction that is ready for execution.
     * 
     * @return {@link Group} Returns executed instruction. 
     */
    public Group changeEUBody(final Group nextNode) {
        Group oldNode = (Group) processingUnit.getChildren().stream().filter(node -> node.getClass().getTypeName().contains("Group")).findFirst()
                .orElse(processingUnit.getChildren().stream().filter(node -> node.getClass().getTypeName().contains("NodeGraph")).findFirst().orElse(null));
        processingUnit.getChildren().remove(oldNode);
        nextNode.setVisible(true);
        nextNode.setTranslateX(CPU_WIDTH / 2);
        nextNode.setTranslateY(CPU_HEIGHT / 2);
        processingUnit.getChildren().add(nextNode);
        return oldNode;
    }
    
    /**
     * This method create graphical CPU with the given number of cores.
     *
     * @param numOfCores {@link Integer} Number of CPU cores.
     *
     * @return {@link Group} Returns graphical representation for the CPU.
     */
    private Group makeExecutionUnit(final int numOfCores) {
        final Group group = new Group();

        final Stop[] stops = new Stop[] { new Stop(0.0, Color.DARKBLUE), new Stop(1.0, Color.LIGHTBLUE)};
        final Rectangle box = GUIObjects.createRectangle(CPU_WIDTH, CPU_HEIGHT, 
                new RadialGradient(0, 0, CPU_WIDTH / 2, CPU_HEIGHT / 2, NodeGraph.NODE_RADIUS*2, false, CycleMethod.REFLECT, stops));
        
        final Group core = GUIObjects.createCircleGroup(NodeGraph.NODE_RADIUS);
        core.setTranslateX(CPU_WIDTH / 2);
        core.setTranslateY(CPU_HEIGHT / 2);

        group.getChildren().addAll(box, core);
        return group;
    }

}
