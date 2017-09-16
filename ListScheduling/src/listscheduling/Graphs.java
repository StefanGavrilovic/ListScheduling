/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package listscheduling;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.scene.Group;

/**
 * Utility Graphs class used for creating graph in application.
 *
 * @author Stefan
 */
public class Graphs {

    /**
     * Default capacity for hash map used as help for creating graph.
     */
    private static final int HASH_CAP = 10;

    /**
     * Utility class should not hae constructor.
     */
    private Graphs() {
    }

    /**
     * This method is used for creating graph from the text file and it returns
     * list of the graph nodes.
     *
     * @param fileName {@link String} Name of the file from which graph is
     * loaded.
     *
     * @return {@link List<NodeGraph>} list of the graph nodes.
     */
    public static List<NodeGraph> makeGraphLogic(String fileName) {
        List<NodeGraph> nodes = new ArrayList<>();
        HashMap<String, NodeGraph> hashMap = new HashMap<>(HASH_CAP);

        BufferedReader inputFile = null;
        try {
            inputFile = new BufferedReader(new FileReader(fileName));

            while (true) {
                String string = inputFile.readLine();
                if (string == null) {
                    break;
                }
                StringTokenizer st = new StringTokenizer(string, " ");
                NodeGraph node = new NodeGraph(st.nextToken(), st.nextToken(), st.nextToken());
                nodes.add(node);
                //WW
                if (hashMap.containsKey(node.getInstruction().getResult())) {
                    NodeGraph nodeRes = hashMap.get(node.getInstruction().getResult());
                    //dead code - recognize - do not remove
                    boolean deadCode = true;
                    for (int i = nodeRes.getOrdNumber(); i < node.getOrdNumber(); i++) {
                        NodeGraph tempNode = nodes.get(i);
                        if ((tempNode.getInstruction().getA()
                                .equals(node.getInstruction().getResult()))
                                || (tempNode.getInstruction().getB()
                                        .equals(node.getInstruction().getResult()))) {
                            deadCode = false;
                        }
                    }
                    if (deadCode) {
                        nodeRes.setDeadResult(true);
                        hashMap.remove(nodeRes.getInstruction().getResult());
                    }
                }
                //RW
                if (hashMap.containsKey(node.getInstruction().getA())
                        && hashMap.containsKey(node.getInstruction().getB())) {
                    NodeGraph nodeA = hashMap.get(node.getInstruction().getA());
                    NodeGraph nodeB = hashMap.get(node.getInstruction().getB());
                    if (nodeA.getInstruction().getA().equals(node.getInstruction().getB())
                            || nodeA.getInstruction().getB().equals(node.getInstruction().getB())) {
                        node.addPredLink(nodeA, node, Edge.DEPENDENCY);
                        node.addPredLink(nodeB, node, Edge.TRANSIENT);
                        nodeA.addSuccLink(nodeA, node, Edge.DEPENDENCY);
                        nodeB.addSuccLink(nodeB, node, Edge.TRANSIENT);
                    } else if (nodeB.getInstruction().getA().equals(node.getInstruction().getA())
                            || nodeB.getInstruction().getB().equals(node.getInstruction().getA())) {
                        node.addPredLink(nodeA, node, Edge.TRANSIENT);
                        node.addPredLink(nodeB, node, Edge.DEPENDENCY);
                        nodeA.addSuccLink(nodeA, node, Edge.TRANSIENT);
                        nodeB.addSuccLink(nodeB, node, Edge.DEPENDENCY);
                    } else {
                        node.addPredLink(nodeA, node, Edge.DEPENDENCY);
                        node.addPredLink(nodeB, node, Edge.DEPENDENCY);
                        nodeA.addSuccLink(nodeA, node, Edge.DEPENDENCY);
                        nodeB.addSuccLink(nodeB, node, Edge.DEPENDENCY);
                    }
                } else {
                    if (hashMap.containsKey(node.getInstruction().getA())) {
                        NodeGraph n = hashMap.get(node.getInstruction().getA());
                        node.addPredLink(n, node, Edge.DEPENDENCY);
                        n.addSuccLink(n, node, Edge.DEPENDENCY);//type needed???
                    }
                    if (hashMap.containsKey(node.getInstruction().getB())) {
                        NodeGraph n = hashMap.get(node.getInstruction().getB());
                        node.addPredLink(n, node, Edge.DEPENDENCY);
                        n.addSuccLink(n, node, Edge.DEPENDENCY);
                    }
                }

                hashMap.put(node.getInstruction().getResult(), node);
            }

            hashMap.clear();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Graphs.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Graphs.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (inputFile != null) {
                    inputFile.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(Graphs.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return nodes;
    }

    /**
     * This method draws graph on screen and returns list of edges that are
     * shown on scene.
     *
     * @param nodes {@link List<NodeGraph>} List of the graph nodes.
     * @param root {@link Group} This group represents graph itself.
     *
     * @return {@link List<Edge>} List of the graph edges that are shown on
     * scene.
     */
    public static List<Edge> drawGraph(List<NodeGraph> nodes, Group root) {
        List<Edge> links = new LinkedList<>();

        int level = 0;
        int spread = 0;
        int counter = 0;
        int mod = 0;

        ListIterator<NodeGraph> nodesIterator = nodes.listIterator();
        while (nodesIterator.hasNext()) {
            NodeGraph node = nodesIterator.next();
            node.setVisible(true);
            node.setTranslateY(level * NodeGraph.NODE_RADIUS * 3);
            if ((counter % 2) == 0) {
                node.setTranslateX(spread * NodeGraph.NODE_RADIUS * 2
                        + (spread == 0 ? NodeGraph.NODE_RADIUS : NodeGraph.NODE_RADIUS * 2));
            } else {
                node.setTranslateX(-spread * NodeGraph.NODE_RADIUS * 2
                        - NodeGraph.NODE_RADIUS * 2);
            }

            ListIterator<Edge> linksIterator = node.getPredLinksIterator();
            while (linksIterator.hasNext()) {
                Edge link = linksIterator.next();
                link.drawLine();
                links.add(link);
                root.getChildren().add(link);
            }

            if ((mod == 0) || ((mod != 0) && ((counter % mod) == 0))) {
                level++;
                spread = 1;
                if (mod == 0) {
                    mod += 2;
                } else {
                    mod *= 2;
                }
            } else {
                spread++;
            }
            counter++;

            root.getChildren().add(node);
        }

        return links;
    }

    /**
     * This method is used to delete transient edges from the graph.
     *
     * @param edges {@link List<Edge>} List of the graph edges.
     */
    public static void removeTransientLinks(List<Edge> edges) {
        edges.stream().filter(e -> e.compareLinkType(Edge.TRANSIENT)).forEach(e -> {
            e.getNodeFrom().removeSuccLink(e);
            e.getNodeTo().removePredLink(e);
        });
        edges.removeAll(edges.stream().filter(e -> e.compareLinkType(Edge.TRANSIENT)).collect(Collectors.toList()));
    }

    /**
     * This method is used to remove nodes from the graph, that represents dead
     * code
     *
     * @param nodes {@link List<NodeGraph>} List of the graph nodes.
     * @param edges {@link List<Edge>} List of the graph edges.
     */
    public static void removeDeadCode(List<NodeGraph> nodes, List<Edge> edges) {
        nodes.stream().filter(node -> node.isDeadResult())
                .forEach(n -> removeNode(nodes, edges, n));
        nodes.removeAll(nodes.stream().filter(n -> n.isDeadResult()).collect(Collectors.toList()));
    }

    /**
     * This method is used for deleting the node and its links in graph.
     *
     * @param nodes {@link List<NodeGraph>} List of the graph nodes.
     * @param edges {@link List<Edge>} List of the shown graph edges.
     * @param deadNode {@link NodeGraph} Node to be deleted.
     */
    public static void removeNode(List<NodeGraph> nodes, List<Edge> edges, NodeGraph deadNode) {
        Optional.ofNullable(deadNode).ifPresent(node -> {
            nodes.stream().filter(test -> !test.isDeadResult()).forEach(n -> {
                node.setVisible(false);
                n.removePredLink(node);
                n.removeSuccLink(node);
                edges.stream().filter(e -> e.checkEdge(n, node)).findFirst().ifPresent(e -> {
                    edges.remove(e);
                    e.setVisible(false);
                });
                edges.stream().filter(e -> e.checkEdge(node, n)).findFirst().ifPresent(e -> {
                    edges.remove(e);
                    e.setVisible(false);
                });
            });
        });
    }

    /**
     * This method determines which nodes are on the critical path in the graph.
     *
     * @param nodes {@link List<NodeGraph>} List of the graph nodes.
     */
    public static void criticalPath(List<NodeGraph> nodes) {
        if (Arrays.stream((NodeGraph[]) nodes.toArray(new NodeGraph[0])).noneMatch(e -> e.OnCriticalPath())) {
            int[] est = new int[nodes.size()];
            int[] lst = new int[nodes.size()];

            NodeGraph[] arrayNodes = (NodeGraph[]) nodes.toArray(new NodeGraph[0]);
            est[0] = 0;

            for (int i = 1; i < nodes.size(); i++) {
                int max = 0;
                ListIterator iteratorPrev = (arrayNodes[i]).getPredLinksIterator();
                while (iteratorPrev.hasNext()) {
                    NodeGraph prevNode = ((Edge) iteratorPrev.next()).getNodeFrom();
                    int index = nodes.indexOf(prevNode);
                    max = max > (prevNode.getDuration() + est[index]) ? max : (prevNode.getDuration() + est[index]);
                }
                est[i] = max;
            }

            lst[nodes.size() - 1] = est[nodes.size() - 1];// + (arrayNodes[nodes.size() - 1]).getDuration();//provjeriti!

            for (int i = nodes.size() - 2; i > 0; i--) {
                int min = Integer.MAX_VALUE;
                ListIterator iteratorSucc = (arrayNodes[i]).getSuccLinksIterator();
                while (iteratorSucc.hasNext()) {
                    NodeGraph succNode = ((Edge) iteratorSucc.next()).getNodeTo();
                    int index = nodes.indexOf(succNode);
                    min = min < (lst[index] - (arrayNodes[i]).getDuration()) ? min : (lst[index] - (arrayNodes[i]).getDuration());
                }
                lst[i] = min;
            }

            for (int i = 0; i < nodes.size() - 1; i++) {
                (arrayNodes[i]).setDelayCriticalPath(lst[i] - est[i]);
            }
        }
    }

    /**
     * This method is used to determine weight (priority) of each node in the
     * graph as a part of List Scheduling algorithm.
     *
     * @param nodes {@link List<NodeGraph>} List of the graph nodes.
     */
    public static void determineHeuristicWeights(List<NodeGraph> nodes) {
        if (nodes.stream().allMatch(e -> e.getNodeWeight() == -1.0)) {
            nodes.forEach(e -> e.setNodeWeight(0.0));
            NodeGraph[] arrayNodes = (NodeGraph[]) nodes.toArray(new NodeGraph[0]);

            for (int i = nodes.size() - 1; i >= 0; i++) {
                (arrayNodes[i]).setNodeWeight(succWeight(arrayNodes[i], 1));
            }
        }
    }

    /**
     * This method is used as a help for method determineHeuristicWeights and it
     * calculate weight of the given node depending on his successors and their
     * weights.
     *
     * @param node {@link NodeGraph} Node of the graph which weight is
     * calculated.
     * @param level {@link int} Which depth level it is.
     *
     * @return {@link double} Calculated weight of the given node.
     */
    private static double succWeight(NodeGraph node, int level) {
        if (level == 3) {
            return node.getNodeWeight();
        } else {
            double weight = 0;
            ListIterator iteratorSucc = node.getSuccLinksIterator();
            while (iteratorSucc.hasNext()) {
                weight += succWeight((NodeGraph) iteratorSucc.next(), level + 1);
            }
            return (double) (1 / node.getDuration()) * weight;
        }
    }

}
