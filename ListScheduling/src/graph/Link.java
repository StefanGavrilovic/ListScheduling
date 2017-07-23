/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graph;

/**
 *
 * @author Stefan
 */
public class Link {
    
    public static final int TYPE_UNDETERMINED = 0;
    public static final int DEPENDENCY = 1;
    public static final int ANTI_DEPENDENCY = 2;
    public static final int TRANSIENT = 3;
    
    private NodeGraph node;
    private int linkType;

    public Link(NodeGraph node, int linkType) {
        this.node = node;
        this.linkType = linkType;
    }

    public NodeGraph getNode() {
        return node;
    }

    public void setNode(NodeGraph node) {
        this.node = node;
    }

    public int getLinkType() {
        return linkType;
    }

    public void setLinkType(int linkType) {
        this.linkType = linkType;
    }
    
}
