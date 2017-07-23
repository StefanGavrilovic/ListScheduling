/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graph;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 *
 * @author Stefan
 */
public class NodeGraph {

    /**
     * Name of the operation that this node represent.
     */
    private String name;
    /**
     * Number of instruction in line of execution.
     */
    private int ordNumber;
    /**
     * Duration of the operation in cycles.
     */
    private int duration;
    /**
     * Instruction that this node represent.
     */
    private Instruction instruction;
    /**
     * Operation that need to be finished, before this one starts.
     */
    private final List<Link> links;
    //private final boolean drawn;
    
    /**
     * The constructor of Node in Graph.
     * @param name
     *          {@link String} name of the operation.
     * @param duration
     *          {@link String} number of cycles.
     * @param instruction
     *          {@link String} instruction to be executed.
     */
    NodeGraph(String name, String duration, String instruction) {
        this.name = name; 
        this.ordNumber = Integer.parseInt(name.substring(2));
        this.duration = Integer.parseInt(duration);
        this.instruction = new Instruction(instruction);
        this.links = new LinkedList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrdNumber() {
        return ordNumber;
    }
    
    public void setOrdNumber (int ordNumber){
        this.ordNumber = ordNumber;
    }
    
    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Instruction getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = new Instruction(instruction);
    }

    public void addLink(NodeGraph node) {
        links.add(new Link(node,Link.TYPE_UNDETERMINED));
    }
    
    public void addLink(NodeGraph node, int linkType) {
        links.add(new Link(node, linkType));
    }

    public NodeGraph getFirstLink() {
        return links.get(0).getNode();
    }

    public void removeLink(Link link) {
        links.remove(link);
    }

    public ListIterator<Link> getLinksIterator() {
        return links.listIterator();
    }
}
