/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import graph.NodeGraph;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

/**
 *
 * @author Stefan
 */
public class Node2D extends Group {

    public static final int NODE_RADIUS = 20;
    public static final int TEXT_WIDTH = 8;
    public static final int TEXT_HEIGHT = 10;
    public static final int TEXT_SIZE = 4;

    private NodeGraph node;
    
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public Node2D(NodeGraph node) {
        this.node = node;
        Circle circle = new Circle(NODE_RADIUS);
        circle.setFill(Color.YELLOW);
        circle.setStroke(Color.BLACK);
        Text text = new Text(-(TEXT_WIDTH) - TEXT_SIZE, TEXT_HEIGHT / 2, node.getName());

        this.getChildren().addAll(circle, text);
    }

    public NodeGraph getNode() {
        return node;
    }

    public void setNode(NodeGraph node) {
        this.node = node;
    }
    
}
