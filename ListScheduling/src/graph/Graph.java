/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graph;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Stefan
 */
public class Graph {

    private List<NodeGraph> nodes;
    private static final int HASH_CAP = 10;

    public Graph(String fileName) {
        nodes = new ArrayList<>();
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
                if (hashMap.containsKey(node.getInstruction().getResult())){
                    NodeGraph nodeRes = hashMap.get(node.getInstruction().getResult());
                    //dead code - recognize
                    boolean deadCode = true;
                    for(int i = nodeRes.getOrdNumber() + 1; i < node.getOrdNumber(); i++) {
                        NodeGraph tempNode = nodes.get(i);
                        if ((tempNode.getInstruction().getA().equals(node.getInstruction().getResult())) || (tempNode.getInstruction().getB().equals(node.getInstruction().getResult()))) {
                            deadCode = false;
                        }
                    }
                    if (deadCode) {
                        nodes.remove(nodeRes);
                        hashMap.remove(nodeRes.getInstruction().getResult());
                    }
                }
                //RW
                if (hashMap.containsKey(node.getInstruction().getA()) && hashMap.containsKey(node.getInstruction().getB())) {
                    NodeGraph nodeA = hashMap.get(node.getInstruction().getA());
                    NodeGraph nodeB = hashMap.get(node.getInstruction().getB());
                    if (nodeA.getInstruction().getA().equals(node.getInstruction().getB()) || nodeA.getInstruction().getB().equals(node.getInstruction().getB())) {
                        node.addPredLink(nodeA, Edge.DEPENDENCY);
                        node.addPredLink(nodeB, Edge.TRANSIENT);
                        nodeA.addSuccLink(node, Edge.DEPENDENCY);
                        nodeB.addSuccLink(node, Edge.TRANSIENT);
                    } else if (nodeB.getInstruction().getA().equals(node.getInstruction().getA()) || nodeB.getInstruction().getB().equals(node.getInstruction().getA())) {
                        node.addPredLink(nodeA, Edge.TRANSIENT);
                        node.addPredLink(nodeB, Edge.DEPENDENCY);
                        nodeA.addSuccLink(node, Edge.TRANSIENT);
                        nodeB.addSuccLink(node, Edge.DEPENDENCY);
                    } else {
                        node.addPredLink(nodeA, Edge.DEPENDENCY);
                        node.addPredLink(nodeB, Edge.DEPENDENCY);
                        nodeA.addSuccLink(node, Edge.DEPENDENCY);
                        nodeB.addSuccLink(node, Edge.DEPENDENCY);
                    }
                } else {
                    if (hashMap.containsKey(node.getInstruction().getA())) {
                        NodeGraph n = hashMap.get(node.getInstruction().getA());
                        node.addPredLink(n, Edge.DEPENDENCY);
                        n.addSuccLink(node, Edge.DEPENDENCY);//type needed???
                    }
                    if (hashMap.containsKey(node.getInstruction().getB())) {
                        NodeGraph n = hashMap.get(node.getInstruction().getB());
                        node.addPredLink(n, Edge.DEPENDENCY);
                        n.addSuccLink(node, Edge.DEPENDENCY);
                    }
                }

                hashMap.put(node.getInstruction().getResult(), node);
            }

            hashMap.clear();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Graph.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Graph.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (inputFile != null) {
                    inputFile.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(Graph.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public NodeGraph getFirstNode() {
        if (nodes.size() > 0) {
            return nodes.remove(0);
        } else {
            return null;
        }
    }

    public int sizeGraph() {
        return nodes.size();
    }

    private void removeNodeLinks(NodeGraph node) {
        if (node != null) {
            nodes.remove(node);
            ListIterator<NodeGraph> iterator = nodes.listIterator();
            while (iterator.hasNext()) {
                NodeGraph n = iterator.next();
                n.removePredLink(node);
                n.removeSuccLink(node);
            }
        }
    }

    public void removeGraphLinks() {
        ListIterator<NodeGraph> iterator = nodes.listIterator();
        while (iterator.hasNext()) {
            NodeGraph n = iterator.next();
            
            ListIterator iteratorPred = n.getPredLinksIterator();
            while(iteratorPred.hasNext()) {
                Edge link = (Edge) iteratorPred.next();
                if (link.getLinkType() == Edge.TRANSIENT) {
                    n.removePredLink(link);
                    link.getNode().removeSuccLink(n);
                }
            }
            
            ListIterator iteratorSucc = n.getSuccLinksIterator();
            while(iteratorSucc.hasNext()) {
                Edge link = (Edge) iteratorSucc.next();
                if (link.getLinkType() == Edge.TRANSIENT) {
                    n.removeSuccLink(link);
                    link.getNode().removePredLink(n);
                }
            }
        }
    }

    public ListIterator<NodeGraph> getIterator() {
        return nodes.listIterator();
    }
    
    public void criticalPath() {
        int [] est = new int [this.sizeGraph()];
        int [] lst = new int [this.sizeGraph()];
        
        NodeGraph[] arrayNodes = (NodeGraph[]) nodes.toArray(new NodeGraph[0]);
        est[0] = 0;
        
        for(int i = 1; i < this.sizeGraph(); i++) {
            int max = 0;
            ListIterator iteratorPrev = (arrayNodes[i]).getPredLinksIterator();
            while(iteratorPrev.hasNext()) {
                NodeGraph prevNode = ((Edge) iteratorPrev.next()).getNode();
                int index = nodes.indexOf(prevNode);
                max = max > (prevNode.getDuration() + est[index]) ? max : (prevNode.getDuration() + est[index]);
            }
            est[i] = max;
        }
        
        lst[this.sizeGraph() - 1] = est[this.sizeGraph() - 1];// + (arrayNodes[this.sizeGraph() - 1]).getDuration();//provjeriti!
        
        for(int i = this.sizeGraph() - 2; i > 0; i--) {
            int min = Integer.MAX_VALUE;
            ListIterator iteratorSucc = (arrayNodes[i]).getSuccLinksIterator();
            while(iteratorSucc.hasNext()) {
                NodeGraph succNode = ((Edge) iteratorSucc.next()).getNode();
                int index = nodes.indexOf(succNode);
                min = min < (lst[index] - (arrayNodes[i]).getDuration()) ? min : (lst[index] - (arrayNodes[i]).getDuration());
            }
            lst[i] = min;
        }
        
        for (int i = 0; i < this.sizeGraph() - 1; i++) {
            (arrayNodes[i]).setDelayCriticalPath(lst[i] - est[i]);
        }
    }
    
    public void determineHeuristicWeights() {
        NodeGraph[] arrayNodes = (NodeGraph[]) nodes.toArray();
        
        for(int i = this.sizeGraph() - 1; i >= 0 ; i++) {
            (arrayNodes[i]).setNodeWeight(succWeight(arrayNodes[i], 1));
        }
    }

    private double succWeight(NodeGraph node, int level) {
        if (level == 3) {
            return node.getNodeWeight();
        } else {
            double weight = 0;
            ListIterator iteratorSucc = node.getSuccLinksIterator();
            while (iteratorSucc.hasNext()) {
                weight += succWeight((NodeGraph) iteratorSucc.next(), level + 1);
            }
            return (double)(1/node.getDuration()) * weight;
        }
    }
    
}
