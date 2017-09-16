/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package listscheduling;

import gui.Main;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.scene.paint.Color;

/**
 *
 * @author Stefan
 */
public class ListSchedulings {

    public static final Color PREPARE = Color.YELLOW;
    public static final Color DATA_READY = Color.RED;
    public static final Color EXECUTED = Color.GRAY;
    public static final double EXECUTING_WIDTH = Main.WINDOW_WIDTH;
    public static final double EXECUTING_HEIGHT = Main.WINDOW_HEIGHT;

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
        return Arrays.stream((NodeGraph[]) nodes.toArray(new NodeGraph[0]))
                .filter(n -> n.isEmptyPredLinksList()).collect(Collectors.toList());
    }

    /**
     * This method picks one Node from Data Ready list that has highest weight.
     *
     * @param nodes {@link List<NodeGraph>} List of the Data Ready nodes.
     *
     * @return {@link NodeGraph} Chosen node from Data Ready list.
     */
    public static NodeGraph getNodeToExecute(List<NodeGraph> nodes) {
        Optional<NodeGraph> a = Arrays.stream((NodeGraph[]) nodes.toArray(new NodeGraph[0]))
                .max((NodeGraph n1, NodeGraph n2) -> (int)(n1.getNodeWeight() - n2.getNodeWeight()));
        return a.isPresent() ? a.get() : null;
    }

    /**
     * Picks and executes instruction from the input list of the instructions.
     *
     * @param nodes {@link List<NodeGraph>} List of the graph nodes.
     * @param edges {@link List<NodeGraph>} List of the graph edges.
     */
    public static void executeInstruction(List<NodeGraph> nodes, List<Edge> edges) {
        NodeGraph node = getNodeToExecute(getDataReady(nodes));
        Graphs.removeNode(nodes, edges, node);
        node.changeBodyColor(EXECUTED);
        //move
    }

}
