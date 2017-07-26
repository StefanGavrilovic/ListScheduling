/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import graph.Link;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineJoin;

/**
 *
 * @author Stefan
 */
public class Link2D extends Group {

    private static final double STROKE_WIDTH = 2.0;
    private static final Color DEPEDENCY_COLOR = Color.BLUE;
    private static final Color ANTI_DEPENDENCY_COLOR = Color.RED;
    private static final Color TRANSIENT_COLOR = Color.GREEN;
    private static final Color TYPE_UNDETERMINED_COLOR = Color.BLACK;

    private final Line line;
    private int linkType;
    private Node2D nodeFrom;
    private Node2D nodeTo;

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public Link2D(Node2D nodeFrom, Node2D nodeTo, int linkType) {
        this.nodeFrom = nodeFrom;
        this.nodeTo = nodeTo;
        this.line = createLine(nodeFrom, nodeTo);
        this.linkType = linkType;
        line.setStroke(getLineColor(linkType));
        line.setStrokeWidth(STROKE_WIDTH);
        line.setStrokeLineJoin(StrokeLineJoin.MITER);
        this.getChildren().add(line);
    }

    public int getLinkType() {
        return linkType;
    }

    public void setLinkType(int linkType) {
        this.linkType = linkType;
    }

    public Node2D getNodeFrom() {
        return nodeFrom;
    }

    public void setNodeFrom(Node2D nodeFrom) {
        this.nodeFrom = nodeFrom;
    }

    public Node2D getNodeTo() {
        return nodeTo;
    }

    public void setNodeTo(Node2D nodeTo) {
        this.nodeTo = nodeTo;
    }

    private Line createLine(Node2D nodeFrom, Node2D nodeTo) {
        double xFrom = nodeFrom.getTranslateX();
        double yFrom = nodeFrom.getTranslateY();
        double xTo = nodeTo.getTranslateX();
        double yTo = nodeTo.getTranslateY();

        if (xFrom == xTo) {
            yFrom += Node2D.NODE_RADIUS;
            yTo -= Node2D.NODE_RADIUS;
        } else if (xFrom < xTo) {
            yFrom += yFrom == yTo ? 0 : Node2D.NODE_RADIUS * Math.cos(45);
            xFrom += Node2D.NODE_RADIUS * (yFrom == yTo ? 1 : Math.sin(45));
            yTo -= yFrom == yTo ? 0 : Node2D.NODE_RADIUS * Math.cos(45);
            xTo -= Node2D.NODE_RADIUS * (yFrom == yTo ? 1 : Math.sin(45));
        } else {
            yFrom += yFrom == yTo ? 0 : Node2D.NODE_RADIUS * Math.cos(45);
            xFrom -= Node2D.NODE_RADIUS * (yFrom == yTo ? 1 : Math.sin(45));
            yTo -= yFrom == yTo ? 0 : Node2D.NODE_RADIUS * Math.cos(45);
            xTo += Node2D.NODE_RADIUS * (yFrom == yTo ? 1 : Math.sin(45));
        }
        return new Line(xFrom, yFrom, xTo, yTo);
    }

    private Color getLineColor(int type) {
        switch (type) {
            case Link.TYPE_UNDETERMINED:
                return TYPE_UNDETERMINED_COLOR;
            case Link.DEPENDENCY:
                return DEPEDENCY_COLOR;
            case Link.ANTI_DEPENDENCY:
                return ANTI_DEPENDENCY_COLOR;
            case Link.TRANSIENT:
                return TRANSIENT_COLOR;
            default:
                return Color.WHITE;
        }
    }
}
