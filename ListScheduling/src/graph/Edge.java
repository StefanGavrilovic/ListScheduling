/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graph;

import gui.Edge2D;

/**
 *
 * @author Stefan
 */
public class Edge {
    
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
     * Node of the graph to whom this link is oriented.
     */
    private NodeGraph node;
    /**
     * Type of the link.
     */
    private int linkType;
    /**
     * Link that is drawn on scene.
     */
    private Edge2D link2D;
    
    /**
     * The constructor for the Link class.
     *
     * @param node
     *          {@link NodeGraph} Node to whom this link is oriented.
     * @param linkType 
     *          {@link int} ID of the link type.
     */
    public Edge(NodeGraph node, int linkType) {
        this.node = node;
        this.linkType = linkType;
        this.link2D = null;
    }

    /**
     * Getter for the NodeGraph.
     *
     * @return {@link NodeGraph} Node to whom this link is oriented. 
     */
    public NodeGraph getNode() {
        return node;
    }

    /**
     * Setter for the field node, that represents Node to whom this link is oriented.
     *
     * @param node 
     *          {@link NodeGraph} Node to whom this link is oriented.
     */
    public void setNode(NodeGraph node) {
        this.node = node;
    }

    /**
     * Getter for the field linkType, that represents type of this link.
     * 
     * @return {@link int} ID of the link type.
     */
    public int getLinkType() {
        return linkType;
    }

    /**
     * Setter for the field linkType, that represents type of the link.
     * 
     * @param linkType 
     *          {@link int} ID of the link type.
     */
    public void setLinkType(int linkType) {
        this.linkType = linkType;
    }

    /**
     * Getter for the field link2D, that represents link on scene.
     * 
     * @return {@link Edge2D} Drawn link on the scene.
     */
    public Edge2D getLink2D() {
        return link2D;
    }

    /**
     * Setter for the field link@d, that represents link on scene.
     * 
     * @param link2D 
     *          {@link Edge2D} Drawn link on the scene, that represents this link.
     */
    public void setLink2D(Edge2D link2D) {
        this.link2D = link2D;
    }
    
    /**
     * Hides link from the scene. 
     * TODO: See what is better way to remove it.
     */
    public void removeLink2D() {
        if (link2D != null) {
            link2D.setVisible(false);
            link2D = null;
        } else {
            System.out.println("Ovde zeza.!");
        }
    }
    
}
