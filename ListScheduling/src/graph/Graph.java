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
                if (hashMap.containsKey(node.getInstruction().getA()) && hashMap.containsKey(node.getInstruction().getB())) {
                    NodeGraph nodeA = hashMap.get(node.getInstruction().getA());
                    NodeGraph nodeB = hashMap.get(node.getInstruction().getB());
                    if (nodeA.getInstruction().getA().equals(node.getInstruction().getB()) || nodeA.getInstruction().getB().equals(node.getInstruction().getB())) {
                        node.addPredLink(nodeA, Link.DEPENDENCY);
                        node.addPredLink(nodeB, Link.TRANSIENT);
                        nodeA.addSuccLink(node, Link.DEPENDENCY);
                        nodeB.addSuccLink(node, Link.TRANSIENT);
                    } else if (nodeB.getInstruction().getA().equals(node.getInstruction().getA()) || nodeB.getInstruction().getB().equals(node.getInstruction().getA())) {
                        node.addPredLink(nodeA, Link.TRANSIENT);
                        node.addPredLink(nodeB, Link.DEPENDENCY);
                        nodeA.addSuccLink(node, Link.TRANSIENT);
                        nodeB.addSuccLink(node, Link.DEPENDENCY);
                    } else {
                        node.addPredLink(nodeA, Link.DEPENDENCY);
                        node.addPredLink(nodeB, Link.DEPENDENCY);
                        nodeA.addSuccLink(node, Link.DEPENDENCY);
                        nodeB.addSuccLink(node, Link.DEPENDENCY);
                    }
                } else {
                    if (hashMap.containsKey(node.getInstruction().getA())) {
                        NodeGraph n = hashMap.get(node.getInstruction().getA());
                        node.addPredLink(n, Link.DEPENDENCY);
                        n.addSuccLink(node, Link.DEPENDENCY);//type needed???
                    }
                    if (hashMap.containsKey(node.getInstruction().getB())) {
                        NodeGraph n = hashMap.get(node.getInstruction().getB());
                        node.addPredLink(n, Link.DEPENDENCY);
                        n.addSuccLink(node, Link.DEPENDENCY);
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
                Link link = (Link) iteratorPred.next();
                if (link.getLinkType() == Link.TRANSIENT) {
                    n.removePredLink(link);
                    link.getNode().removeSuccLink(n);
                }
            }
            
            ListIterator iteratorSucc = n.getSuccLinksIterator();
            while(iteratorSucc.hasNext()) {
                Link link = (Link) iteratorSucc.next();
                if (link.getLinkType() == Link.TRANSIENT) {
                    n.removeSuccLink(link);
                    link.getNode().removePredLink(n);
                }
            }
        }
    }

    public ListIterator<NodeGraph> getIterator() {
        return nodes.listIterator();
    }
    
}
