/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import graph.Graph;
import graph.NodeGraph;
import java.util.HashMap;
import java.util.ListIterator;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.StrokeLineJoin;

/**
 *
 * @author Stefan
 */
public class Graph2D extends Group{
    
    
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public Graph2D(Graph graph){
        int level = 0;
        int spread = 0;
        int counter = 0;
        int mod = 0;
        
        HashMap<String, Node2D> hashMap = new HashMap<>(graph.sizeGraph());
        while(true){
            NodeGraph node = graph.getFirstNode();
            if(node == null)
                break;
            Node2D node2D = new Node2D(node);
            node2D.setTranslateY(level * Node2D.NODE_RADIUS*3);
            if((counter % 2) == 0)
                node2D.setTranslateX(spread * Node2D.NODE_RADIUS*2 + (spread == 0 ? Node2D.NODE_RADIUS : Node2D.NODE_RADIUS*2));
            else
                node2D.setTranslateX(-spread * Node2D.NODE_RADIUS*2 - Node2D.NODE_RADIUS*2);
            
            ListIterator<NodeGraph> linksIterator = node.getLinksIterator();
            while(linksIterator.hasNext()){
                NodeGraph n = linksIterator.next();
                Node2D n2D = hashMap.get(n.getName());
                Polyline line = createLine(n2D, node2D);
                line.setFill(Color.RED);
                line.setStrokeLineJoin(StrokeLineJoin.MITER);
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
    
    public Polyline createLine(Node2D nodeFrom, Node2D nodeTo){
        double xFrom = nodeFrom.getTranslateX();
        double yFrom = nodeFrom.getTranslateY();
        double xTo = nodeTo.getTranslateX();
        double yTo = nodeTo.getTranslateY();
        
        if( xFrom == xTo ) {
            yFrom += Node2D.NODE_RADIUS;
            yTo -= Node2D.NODE_RADIUS;
        }else if ( xFrom < xTo ){
            yFrom += Node2D.NODE_RADIUS*Math.cos(45);
            xFrom += Node2D.NODE_RADIUS*Math.sin(45);
            yTo -= Node2D.NODE_RADIUS*Math.cos(45);
            xTo -= Node2D.NODE_RADIUS*Math.sin(45);
        }else {
            yFrom += Node2D.NODE_RADIUS*Math.cos(45);
            xFrom -= Node2D.NODE_RADIUS*Math.sin(45);
            yTo -= Node2D.NODE_RADIUS*Math.cos(45);
            xTo += Node2D.NODE_RADIUS*Math.sin(45);
        }
        return new Polyline(xFrom, yFrom, xTo, yTo);
    }
}
