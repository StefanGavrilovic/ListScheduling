/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package listscheduling;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineJoin;

/**
 *
 * @author Stefan
 */
public class Edge extends Group {

    /**
     * ID of the link undetermined type.
     */
    public static final int TYPE_UNDETERMINED = 0;
    /**
     * ID of the link that represents dependency link (RW).
     */
    public static final int DEPENDENCY = 1;
    /**
     * ID of the link that represents anti-dependency link (WR).
     */
    public static final int ANTI_DEPENDENCY = 2;
    /**
     * ID of the link that represents outgoing link (WW).
     */
    public static final int OUTGOING_DEPENDENCY = 3;
    /**
     * ID of the link that represents transient link.
     */
    public static final int TRANSIENT = 4;
    /**
     * Width of stroke for drawing edges on graph.
     */
    public static final double STROKE_WIDTH = 2.0;
    /**
     * Color of dependency edge type for drawing.
     */
    public static final Color DEPEDENCY_COLOR = Color.YELLOW;
    /**
     * Color of anti-dependency edge type for drawing.
     */
    public static final Color ANTI_DEPENDENCY_COLOR = Color.RED;
    /**
     * Color of outgoing dependency edge type for drawing.
     */
    public static final Color OUTGOING_DEPENDENCY_COLOR = Color.ORANGE;
    /**
     * Color of transient edge type for drawing.
     */
    public static final Color TRANSIENT_COLOR = Color.LAWNGREEN;
    /**
     * Color of undetermined edge type for drawing.
     */
    public static final Color TYPE_UNDETERMINED_COLOR = Color.BLACK;

    /**
     * Node of the graph from whom this link is oriented.
     */
    private NodeGraph nodeFrom;
    /**
     * Node of the graph to whom this link is oriented.
     */
    private NodeGraph nodeTo;
    /**
     * Type of the link.
     */
    private int linkType;
    /**
     * Drawn line on scene that represents this edge in graph.
     */
    private Line line;

    /**
     * The constructor for the Link class.
     *
     * @param nodeFrom {@link NodeGraph} Node from whom this link is oriented.
     * @param nodeTo {@link NodeGraph} Node to whom this link is oriented.
     * @param linkType {@link int} ID of the link type.
     */
    public Edge(NodeGraph nodeFrom, NodeGraph nodeTo, int linkType) {
        this.nodeFrom = nodeFrom;
        this.nodeTo = nodeTo;
        this.linkType = linkType;
        this.line = null;
        this.setVisible(false);
    }

    /**
     * Getter for the NodeGraph.
     *
     * @return {@link NodeGraph} Node to whom this link is oriented.
     */
    public NodeGraph getNodeTo() {
        return nodeTo;
    }

    /**
     * Setter for the field node, that represents Node to whom this link is
     * oriented.
     *
     * @param nodeTo {@link NodeGraph} Node to whom this link is oriented.
     */
    public void setNodeTo(NodeGraph nodeTo) {
        this.nodeTo = nodeTo;
    }

    /**
     * Getter for the NodeGraph.
     *
     * @return {@link NodeGraph} Node to whom this link is oriented.
     */
    public NodeGraph getNodeFrom() {
        return nodeFrom;
    }

    /**
     * Setter for the field node, that represents Node to whom this link is
     * oriented.
     *
     * @param nodeFrom {@link NodeGraph} Node to whom this link is oriented.
     */
    public void setNodeFrom(NodeGraph nodeFrom) {
        this.nodeFrom = nodeFrom;
    }

    /**
     * Getter for the field linkType, that represents type of this link.
     *
     * @return {@link int} ID of the link type.
     */
    public int getEdgeType() {
        return linkType;
    }

    /**
     * Setter for the field linkType, that represents type of the link.
     *
     * @param linkType {@link int} ID of the link type.
     */
    public void setEdgeType(int linkType) {
        this.linkType = linkType;
    }

    /**
     * Hides link from the scene. TODO: See what is better way to remove it.
     */
    public void hideEdge() {
        if ((line != null) && line.isVisible()) {
            line.setVisible(false);
        }
    }

    /**
     * Draws line on scene that represents edge in graph.
     */
    public void drawLine() {
        if (line == null) {
            this.line = createLine(nodeFrom, nodeTo);
            line.setStroke(getLineColor(linkType));
            line.setStrokeWidth(STROKE_WIDTH);
            line.setStrokeLineJoin(StrokeLineJoin.MITER);
            this.getChildren().add(line);
            this.setVisible(true);
        }
    }

    /**
     * This method checks if this edge is between given nodes.
     * 
     * @param nodeFrom {@link NodeGraph} Node from searched edge is directed.
     * @param nodeTo {@link NodeGraph} Node to searched edge is directed.
     * 
     * @return {@link boolean}  
     */
    public boolean checkEdge(NodeGraph nodeFrom, NodeGraph nodeTo) {
        return this.getNodeFrom().equals(nodeFrom) && this.getNodeTo().equals(nodeTo);
    }
    
    /**
     * This method compares given type with edge type.
     * 
     * @param linkType {@link int} Given type.
     * 
     * @return {@link boolean} Returns if edge type and the given are equal.
     */
    public boolean compareLinkType(int linkType) {
        return this.linkType == linkType;
    }
    
    /**
     * Creates line that represents edge between given nodes.
     * 
     * @param nodeFrom {@link NodeGraph} Node from which line is drawn.
     * @param nodeTo {@link NodeGraph} Node to is line directed.
     * 
     * @return {@link Line} Created line as 2D object.
     */
    private Line createLine(NodeGraph nodeFrom, NodeGraph nodeTo) {
        double xFrom = nodeFrom.getTranslateX();
        double yFrom = nodeFrom.getTranslateY();
        double xTo = nodeTo.getTranslateX();
        double yTo = nodeTo.getTranslateY();

        if (xFrom == xTo) {
            yFrom += NodeGraph.NODE_RADIUS;
            yTo -= NodeGraph.NODE_RADIUS;
        } else if (xFrom < xTo) {
            yFrom += yFrom == yTo ? 0 : NodeGraph.NODE_RADIUS * Math.cos(45);
            xFrom += NodeGraph.NODE_RADIUS * (yFrom == yTo ? 1 : Math.sin(45));
            yTo -= yFrom == yTo ? 0 : NodeGraph.NODE_RADIUS * Math.cos(45);
            xTo -= NodeGraph.NODE_RADIUS * (yFrom == yTo ? 1 : Math.sin(45));
        } else {
            yFrom += yFrom == yTo ? 0 : NodeGraph.NODE_RADIUS * Math.cos(45);
            xFrom -= NodeGraph.NODE_RADIUS * (yFrom == yTo ? 1 : Math.sin(45));
            yTo -= yFrom == yTo ? 0 : NodeGraph.NODE_RADIUS * Math.cos(45);
            xTo += NodeGraph.NODE_RADIUS * (yFrom == yTo ? 1 : Math.sin(45));
        }
        return new Line(xFrom, yFrom, xTo, yTo);
    }

    /**
     * Getter for the color of the line by its type.
     * 
     * @param type {@link int} Type of edge/line.
     * 
     * @return {@link Color} Color of the line.
     */
    private Color getLineColor(int type) {
        switch (type) {
            case Edge.TYPE_UNDETERMINED:
                return TYPE_UNDETERMINED_COLOR;
            case Edge.DEPENDENCY:
                return DEPEDENCY_COLOR;
            case Edge.ANTI_DEPENDENCY:
                return ANTI_DEPENDENCY_COLOR;
            case Edge.TRANSIENT:
                return TRANSIENT_COLOR;
            case Edge.OUTGOING_DEPENDENCY:
                return OUTGOING_DEPENDENCY_COLOR;
            default:
                return Color.WHITE;
        }
    }
    
}
