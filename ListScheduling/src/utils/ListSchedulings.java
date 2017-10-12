/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import gui.ExecutionUnit;
import utils.Graphs;
import gui.Main;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.scene.paint.Color;
import listscheduling.Edge;
import listscheduling.NodeGraph;

/**
 *
 * @author Stefan
 */
public class ListSchedulings {

    public static final Color PREPARE = Color.LIGHTSKYBLUE;
    public static final Color DATA_READY = Color.BLUE;
    public static final Color EXECUTED = Color.GRAY;
    public static final Color ON_CRITICAL_PATH = Color.ROYALBLUE;
    public static final double EXECUTING_WIDTH = Main.WINDOW_WIDTH;
    public static final double EXECUTING_HEIGHT = Main.WINDOW_HEIGHT;

    /**
     * Utility class should not have constructor.
     */
    private ListSchedulings() {
    }

    /**
     * This method returns list of the Data Ready nodes from the graph.
     *
     * @param nodes {@link List<NodeGraph>} List of the graph nodes.
     *
     * @return {@link List<NodeGraph>} List of the Data Ready nodes.
     */
    public static List<NodeGraph> getDataReady(List<NodeGraph> nodes) {
        return nodes.stream().filter(n -> n.isEmptyPredLinksList() && !n.isFinished()).collect(Collectors.toList());
    }

    /**
     * This method picks one Node from Data Ready list that has highest weight.
     *
     * @param nodes {@link List<NodeGraph>} List of the Data Ready nodes.
     *
     * @return {@link NodeGraph} Chosen node from Data Ready list.
     */
    public static NodeGraph getNodeToExecute(List<NodeGraph> nodes) {
        NodeGraph nodeCP = nodes.stream().filter(node -> node.OnCriticalPath()).findFirst().orElse(null);
        NodeGraph nodeW = nodes.stream()
                .max((NodeGraph n1, NodeGraph n2) -> (int)(n1.getNodeWeight() - n2.getNodeWeight())).orElse(null);
        return nodeCP != null ? nodeCP : nodeW;
    }

    /**
     * Picks and executes instruction from the input list of the instructions.
     *
     * @param nodes {@link List<NodeGraph>} List of the graph nodes.
     * @param edges {@link List<NodeGraph>} List of the graph edges.
     * @param eu {@link ExecutionUnit} Execution unit that acts as CPU for instructions.
     */
    public static void executeInstruction(List<NodeGraph> nodes, List<Edge> edges, ExecutionUnit eu) {
        NodeGraph node = getNodeToExecute(getDataReady(nodes));
        if (node != null) {
            Graphs.removeNode(nodes, edges, node);
            node.changeBodyColor(EXECUTED);
            //move
            eu.addFinishedToList(eu.changeEUBody(node));
        } else {
            Main.setAlgorithmFinished();
        }
    }

}
