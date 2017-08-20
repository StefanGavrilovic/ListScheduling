/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import graph.Graph;
import graph.Edge;
import graph.NodeGraph;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import javafx.scene.Group;

/**
 *
 * @author Stefan
 */
public class Graph2D extends Group{
    
    private Graph graph;
    private List<Node2D> nodes;
    private List<Edge2D> links;
    
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public Graph2D(Graph graph){
        this.graph = graph;
        this.nodes = new LinkedList<>();
        this.links = new LinkedList<>();
        
        int level = 0;
        int spread = 0;
        int counter = 0;
        int mod = 0;
        
        HashMap<String, Node2D> hashMap = new HashMap<>(graph.sizeGraph());
        ListIterator<NodeGraph> nodesIterator = graph.getIterator();
        while(nodesIterator.hasNext()){
            NodeGraph node = nodesIterator.next();
            Node2D node2D = new Node2D(node);
            node2D.setTranslateY(level * Node2D.NODE_RADIUS*3);
            if((counter % 2) == 0)
                node2D.setTranslateX(spread * Node2D.NODE_RADIUS*2 + (spread == 0 ? Node2D.NODE_RADIUS : Node2D.NODE_RADIUS*2));
            else
                node2D.setTranslateX(-spread * Node2D.NODE_RADIUS*2 - Node2D.NODE_RADIUS*2);
            nodes.add(node2D);
            
            ListIterator<Edge> linksIterator = node.getPredLinksIterator();
            while(linksIterator.hasNext()){
                Edge link = linksIterator.next();
                NodeGraph n = link.getNode();
                Node2D n2D = hashMap.get(n.getName());
                Edge2D line = new Edge2D(n2D, node2D, link.getLinkType());
                links.add(line);
                Edge l = n.findSuccLink(node);
                if(l != null ) {
                    l.setLink2D(line);
                }
                this.getChildren().add(line);
            }
            
            if((mod == 0) || ((mod != 0) && ((counter % mod) == 0))) {
                level++;
                spread = 1;
                if(mod == 0)
                    mod += 2;
                else
                    mod *= 2;
            }else
                spread++;
            counter++;
            
            hashMap.put(node.getName(), node2D);
            this.getChildren().add(node2D);
        }
    }

    public Graph getGraph() {
        return graph;
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }
    
    
}
