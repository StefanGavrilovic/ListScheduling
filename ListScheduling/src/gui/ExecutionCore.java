/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import static gui.ExecutionUnit.CPU_WIDTH;
import java.util.LinkedList;
import java.util.List;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import logic.NodeGraph;
import utils.gui.Element;

/**
 * The ExecutionCore class is GUI representation of a CPU or execution core.
 * 
 * @author zer0
 */
public class ExecutionCore extends Group {

    /**
     * Graphical list of the graph nodes executed by processingUnit.
     */
    private final TilePane executionList;
    /**
     * List of the positions for executed instructions.
     */
    private final List<Group> positionList;
    /**
     * Represents body of core.
     */
    private Group core;
    /**
     * Core position on y axis.
     */
    private final double yCoord;

    /**
     * The constructor for the ExecutionCore class.
     *
     * @param yCoord {@link Double} Core position on y axis.
     */
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public ExecutionCore(final double yCoord) {
        positionList = new LinkedList<>();
        executionList = new TilePane(Orientation.HORIZONTAL, 8, 8);
        executionList.setTranslateX(CPU_WIDTH);
        core = Element.createCircleGroup(NodeGraph.NODE_RADIUS);
        core.setTranslateX(CPU_WIDTH / 2);
        core.setTranslateY(yCoord);
        this.yCoord = yCoord;

        this.getChildren().addAll(core, executionList);
    }

    /**
     * Returns GUI objects that represents core.
     *
     * @return {@link Group} Group that represents execution core.
     */
    public final Group getCore() {
        return core;
    }

    /**
     * This method is used to create graphical representation of executed
     * instructions by CPU.
     *
     * @param numberOfNodes {@link Integer} Number of nodes loaded into CPU.
     */
    public final void makeList(int numberOfNodes) {
        if (positionList.isEmpty()) {
            executionList.setPrefColumns(numberOfNodes);
            for (int i = 0; i < numberOfNodes; i++) {
                final Group c = Element.createCircleGroup(NodeGraph.NODE_RADIUS);
                c.setTranslateY(yCoord - NodeGraph.NODE_RADIUS);
                positionList.add(c);
            }
            executionList.getChildren().addAll(positionList);
        }
    }

    /**
     * This method clear graphical representation of executed instructions by
     * CPU.
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
        group.setTranslateY(yCoord - NodeGraph.NODE_RADIUS);
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
    public Group changeEUBody(Group nextNode) {
        if (nextNode == null) {
            nextNode = Element.createCircleGroup(NodeGraph.NODE_RADIUS, Color.BLACK);
            final Label l = new Label("NOPE");
            l.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 20));
            nextNode.getChildren().add(l);
            l.setTranslateX(-29);
            l.setTranslateY(-10);
        }
        Group oldNode = this.core;
        nextNode.setVisible(true);
        nextNode.setTranslateX(oldNode.getTranslateX());
        nextNode.setTranslateY(oldNode.getTranslateY());
        this.core = nextNode;
        return oldNode;
    }

}
