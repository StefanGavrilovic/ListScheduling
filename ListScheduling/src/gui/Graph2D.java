/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import graph.Graph;
import graph.NodeGraph;
import javafx.scene.Group;

/**
 *
 * @author Stefan
 */
public class Graph2D extends Group{
    
    
    public Graph2D(Graph graph){
        int i = 0;
        while(true){
            NodeGraph node = graph.getNode();
            if(node == null)
                break;
            Node2D node2D = new Node2D(node);
            node2D.setTranslateX(i * Node2D.NODE_RADIUS*2 + Node2D.NODE_RADIUS*2);
            node2D.setTranslateY(i * Node2D.NODE_RADIUS*2 + Node2D.NODE_RADIUS*2);
            i++;
            this.getChildren().add(node2D);
        }
    }
}
