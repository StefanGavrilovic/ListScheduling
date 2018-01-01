/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import logic.NodeGraph;
import utils.gui.Element;

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
    public static final double CPU_WIDTH = 300;
    /**
     * Height of graphical representation of CPU.
     */
    private final double cpu_height;
    /**
     * Graphical representation of CPU.
     */
    private final Group processingUnit;
    /**
     * List of the cores and corresponding list of positions for executed
     * instructions.
     */
    private List<ExecutionCore> cores;

    /**
     * The Constructor for ExecutionUnit class.
     *
     * @param numOfCores {@link Integer} Number of cores.
     * @param height {@link Double} Maximum height for this part of window.
     */
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public ExecutionUnit(final int numOfCores, final double height) {
        this.cpu_height = height;
        this.cores = new LinkedList<>();
        processingUnit = makeExecutionUnit(numOfCores);

        final Label label = new Label("Execution Unit");
        label.setLabelFor(processingUnit);

        this.getChildren().addAll(processingUnit, label);
    }

    /**
     * This method is used to create graphical representation of executed
     * instructions by CPU.
     *
     * @param numberOfNodes {@link Integer} Number of nodes loaded into CPU.
     */
    public void makeList(int numberOfNodes) {
        cores.forEach(core -> core.makeList((numberOfNodes + cores.size() - 1) / cores.size()));
    }

    /**
     * This method clear graphical representation of executed instructions by
     * CPU.
     */
    public void removeList() {
        cores.forEach(core -> core.removeList());
    }

    /**
     * This method adds instruction to the finished list.
     *
     * @param groups {@link List} < {@link Group} > Added instructions.
     */
    public void addFinishedToList(final List<Group> groups) {
        IntStream.range(0, cores.size()).forEach(i -> cores.get(i).addFinishedToList(groups.get(i)));
    }

    /**
     * This method is used to change currently executed instruction.
     *
     * @param nextNodes {@link List} < {@link Group} > Instructions that are ready for execution.
     *
     * @return {@link Group} Returns executed instruction.
     */
    public List<Group> changeEUBody(final List<Group> nextNodes) {
        List<Group> oldNodes = new LinkedList<>();
        IntStream.range(0, cores.size()).forEach(i -> oldNodes.add(cores.get(i).changeEUBody(i >= nextNodes.size() ? null : nextNodes.get(i))));
        cores.forEach(c -> processingUnit.getChildren().add(c.getCore()));
        return oldNodes;
    }
    
    /**
     * Returns number of cores for this execution unit.
     * 
     * @return {@link Integer} Number of cores.
     */
    public int getNumOfCores() {
        return cores.size();
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
        final double dist = this.cpu_height / (numOfCores + 1);

        final Stop[] stops = new Stop[]{new Stop(0.0, Color.DARKBLUE), new Stop(1.0, Color.LIGHTBLUE)};
        final Rectangle box = Element.createRectangle(CPU_WIDTH, this.cpu_height, Color.WHITE);
                //new RadialGradient(0, 0, CPU_WIDTH / 2, this.cpu_height / 2, NodeGraph.NODE_RADIUS * 2, false, CycleMethod.REFLECT, stops));

        IntStream.range(0, numOfCores).forEach(i -> this.cores.add(new ExecutionCore((i + 1) * dist)));
        
        group.getChildren().addAll(box);
        group.getChildren().addAll(cores);
        return group;
    }

}
