/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils.logic;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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
import logic.Edge;
import logic.NodeGraph;

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
     * Utility class should not have constructor.
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
        HashMap<String, NodeGraph> hashMapWR = new HashMap<>(HASH_CAP);

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
                    } else {
                        nodeRes.addSuccLink(nodeRes, node, Edge.OUTGOING_DEPENDENCY);
                        node.addPredLink(nodeRes, node, Edge.OUTGOING_DEPENDENCY);
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
                //WR
                if (hashMapWR.containsKey(node.getInstruction().getResult())) {
                    NodeGraph n = hashMapWR.get(node.getInstruction().getResult());
                    //do not need to check if it is succ, because it can not be
                    //between instruction that writes to the same location (dead code)
                    n.addSuccLink(n, node, Edge.ANTI_DEPENDENCY);
                    node.addPredLink(n, node, Edge.ANTI_DEPENDENCY);
                }

                hashMap.put(node.getInstruction().getResult(), node);
                hashMapWR.put(node.getInstruction().getA(), node);
                hashMapWR.put(node.getInstruction().getB(), node);
            }

            hashMap.clear();
            hashMapWR.clear();
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
        int spread = 1;

        ListIterator<NodeGraph> nodesIterator = nodes.listIterator();

        HashMap<String, Integer> drawnNodes = new HashMap<>();
        NodeGraph node;
        while (true) {
            if (!nodesIterator.hasNext()) {
                break;
            }

            node = nodesIterator.next();

            if (!node.isEmptyPredLinksList()) {
                ListIterator<Edge> li = node.getPredLinksIterator();
                while (li.hasNext()) {
                    if (drawnNodes.get(li.next().getNodeFrom().getName()) == level) {
                        level++;
                        spread = ((level % 2) == 0) ? 1 : 2;
                        break;
                    }
                }
            }

            node.setVisible(true);
            node.setTranslateY(level * NodeGraph.NODE_RADIUS * 3);
            node.setTranslateX(spread * NodeGraph.NODE_RADIUS * 4
                    + (spread == 0 ? NodeGraph.NODE_RADIUS : NodeGraph.NODE_RADIUS * 4));

            spread += 2;

            ListIterator<Edge> linksIterator = node.getPredLinksIterator();
            while (linksIterator.hasNext()) {
                Edge link = linksIterator.next();
                link.drawLine();
                links.add(link);
                root.getChildren().add(link);
            }

            drawnNodes.put(node.getName(), level);
            root.getChildren().add(node);
        }

        drawnNodes.clear();
        return links;
    }

    /**
     * This method is used to delete transient edges from the graph.
     *
     * @param edges {@link List<Edge>} List of the graph edges.
     * @param nodes {@link List<NodeGraph>} List of the graph nodes.
     */
    public static void removeTransientLinks(List<Edge> edges, List<NodeGraph> nodes) {
        edges.removeAll(edges.stream().filter(e -> e.compareLinkType(Edge.TRANSIENT)).collect(Collectors.toList()));
        nodes.forEach(n -> n.removeAllPredEdges(Edge.TRANSIENT));
        nodes.forEach(n -> n.removeAllSuccEdges(Edge.TRANSIENT));
    }

    /**
     * This method is used to delete outgoing dependency edges from the graph.
     *
     * @param edges {@link List<Edge>} List of the graph edges.
     * @param nodes {@link List<NodeGraph>} List of the graph nodes.
     */
    public static void removeOutgoingDependencyLinks(List<Edge> edges, List<NodeGraph> nodes) {
        edges.stream().filter(e1 -> e1.compareLinkType(Edge.OUTGOING_DEPENDENCY)).forEach(e -> {
            final String renameRes = e.getNodeTo().getInstruction().getResult();
            e.getNodeTo().getSuccLinksAsStream().filter(edge -> edge.getNodeTo().getInstruction().getA().equalsIgnoreCase(renameRes)
                    || edge.getNodeTo().getInstruction().getB().equalsIgnoreCase(renameRes)).forEach(edgeRename -> {
                if (edgeRename.getNodeTo().getInstruction().getA().equalsIgnoreCase(renameRes)) {
                    edgeRename.getNodeTo().getInstruction().setA(renameRes + 1);
                } else {
                    edgeRename.getNodeTo().getInstruction().setB(renameRes + 1);
                }
            });
            e.getNodeTo().getInstruction().setResult(renameRes + 1);
        });
        edges.removeAll(edges.stream().filter(e -> e.compareLinkType(Edge.OUTGOING_DEPENDENCY)).collect(Collectors.toList()));
        nodes.forEach(n -> n.removeAllPredEdges(Edge.OUTGOING_DEPENDENCY));
        nodes.forEach(n -> n.removeAllSuccEdges(Edge.OUTGOING_DEPENDENCY));
    }

    /**
     * This method is used to delete anti dependency edges from the graph.
     *
     * <p>
     * Be careful! This method is called after removeOutgoingDependencyLinks, so
     * this method should first check if nodeTo is already renamed.
     * </p>
     *
     * @param edges {@link List<Edge>} List of the graph edges.
     * @param nodes {@link List<NodeGraph>} List of the graph nodes.
     */
    public static void removeAntiDependencyLinks(List<Edge> edges, List<NodeGraph> nodes) {
        edges.stream().filter(e1 -> e1.compareLinkType(Edge.ANTI_DEPENDENCY)).forEach(e -> {
            final String renameRes = e.getNodeTo().getInstruction().getResult();

            e.getNodeTo().getSuccLinksAsStream().filter(edge -> renameRes.startsWith(edge.getNodeTo().getInstruction().getA())
                    || renameRes.startsWith(edge.getNodeTo().getInstruction().getB())).forEach(edgeRename -> {
                if (renameRes.startsWith(edgeRename.getNodeTo().getInstruction().getA())) {
                    edgeRename.getNodeTo().getInstruction().setA(renameRes + 1);
                } else {
                    edgeRename.getNodeTo().getInstruction().setB(renameRes + 1);
                }
            });
            if (renameRes.matches("[a-zA-z]+")) {
                e.getNodeTo().getInstruction().setResult(renameRes + 1);
            }
        });
        edges.removeAll(edges.stream().filter(e -> e.compareLinkType(Edge.ANTI_DEPENDENCY)).collect(Collectors.toList()));
        nodes.forEach(n -> n.removeAllPredEdges(Edge.ANTI_DEPENDENCY));
        nodes.forEach(n -> n.removeAllSuccEdges(Edge.ANTI_DEPENDENCY));
    }

    /**
     * This method is used to remove nodes from the graph, that represents dead
     * code.
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
     * This method is used to check for more dead code, after deleting dead code
     * with method removeDeadCode.
     *
     * @param nodes {@link List<NodeGraph>} List of the graph nodes.
     * @param edges {@link List<Edge>} List of the graph edges.
     */
    public static void checkForNewDeadCode(List<NodeGraph> nodes, List<Edge> edges) {
        while (nodes.stream().filter(node -> node.isEmptySuccLinksList()).count() > 0) {
            nodes.stream().filter(node -> node.isEmptySuccLinksList()).forEach(n -> n.setDeadResult(true));
            removeDeadCode(nodes, edges);
        }
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
            nodes.stream().filter(test -> !test.isDeadResult() || !test.isFinished()).forEach(n -> {
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
                node.setFinished(true);
            });
        });
    }

    /**
     * This method determines which nodes are on the critical path in the graph.
     *
     * @param nodes {@link List<NodeGraph>} List of the graph nodes.
     */
    public static void criticalPath(List<NodeGraph> nodes) {
        if (nodes.stream().noneMatch(e -> e.OnCriticalPath())) {
            //take instructions without successors
            List<NodeGraph> nodesWithoutSuccessors = nodes.stream().filter(n -> n.isEmptySuccLinksList()).collect(Collectors.toList());
            int length = 0;
            int maxLength = 0;

            ArrayList<NodeGraph> stack = new ArrayList<>();
            ArrayList<NodeGraph> nodesOnCritical = new ArrayList<>();
            HashMap<String, NodeGraph> tempNodes = new HashMap<>();

            for (NodeGraph nodesWithoutSuccessor : nodesWithoutSuccessors) {
                stack.addAll(nodesWithoutSuccessor.getPredLinksAsStream().map(link -> link.getNodeFrom()).collect(Collectors.toList()));
                tempNodes.clear();

                while (!stack.isEmpty()) {
                    List<NodeGraph> tmp = stack.stream().collect(Collectors.toList());
                    stack.clear();
                    tmp.forEach(node -> {
                        stack.addAll(node.getPredLinksAsStream().map(link -> link.getNodeFrom()).collect(Collectors.toList()));
                        tempNodes.put(node.getName(), node);
                            });
                    length++;
                }

                if (maxLength < length) {
                    maxLength = length;
                    nodesOnCritical.clear();
                    nodesOnCritical.add(nodesWithoutSuccessor);
                    nodesOnCritical.addAll(tempNodes.values().stream().collect(Collectors.toList()));
                }
                
                length = 0;
                tempNodes.clear();
            }

            nodesOnCritical.sort((n1, n2) -> n1.getOrdNumber() - n2.getOrdNumber());

            int[] est = new int[nodesOnCritical.size()];
            int[] lst = new int[nodesOnCritical.size()];

            NodeGraph[] arrayNodes = (NodeGraph[]) nodesOnCritical.toArray(new NodeGraph[0]);
            est[0] = 0;

            for (int i = 1; i < nodesOnCritical.size(); i++) {
                int max = 0;
                ListIterator iteratorPrev = (arrayNodes[i]).getPredLinksIterator();
                while (iteratorPrev.hasNext()) {
                    NodeGraph prevNode = ((Edge) iteratorPrev.next()).getNodeFrom();
                    int index = nodesOnCritical.indexOf(prevNode);
                    if (index != -1)
                        max = max > (prevNode.getDuration() + est[index]) ? max : (prevNode.getDuration() + est[index]);
                }
                est[i] = max;
            }

            lst[nodesOnCritical.size() - 1] = est[nodesOnCritical.size() - 1];// + (arrayNodes[nodes.size() - 1]).getDuration();//provjeriti!

            for (int i = nodesOnCritical.size() - 2; i > 0; i--) {
                int min = Integer.MAX_VALUE;
                ListIterator iteratorSucc = (arrayNodes[i]).getSuccLinksIterator();
                while (iteratorSucc.hasNext()) {
                    NodeGraph succNode = ((Edge) iteratorSucc.next()).getNodeTo();
                    int index = nodesOnCritical.indexOf(succNode);
                    if (index != -1)
                        min = min < (lst[index] - (arrayNodes[i]).getDuration()) ? min : (lst[index] - (arrayNodes[i]).getDuration());
                }
                lst[i] = min;
            }

            for (int i = 0; i < nodesOnCritical.size(); i++) {
                (arrayNodes[i]).setDelayCriticalPath(lst[i] - est[i]);
            }

            nodes.stream().filter(n -> n.OnCriticalPath()).forEach(n -> n.changeBodyColor(ListSchedulings.ON_CRITICAL_PATH));
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

            for (int i = nodes.size() - 1; i >= 0; i--) {
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
                weight += succWeight(((Edge) iteratorSucc.next()).getNodeTo(), level + 1);
            }
            return weight == 0 ? (1 / (double) node.getDuration()) : (1 / (double) node.getDuration()) * weight;
        }
    }

}
