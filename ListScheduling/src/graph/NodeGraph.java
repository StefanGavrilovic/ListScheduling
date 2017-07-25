/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graph;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

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
     * Operations that need to be finished, before this one starts. Predecessor.
     */
    private final List<Link> predLinks;
    /**
     * Operations that waits for this operation to be finished. Successor.
     */
    private final List<Link> succLinks;
    
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
        this.predLinks = new LinkedList<>();
        this.succLinks = new LinkedList<>();
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

    public void addPredLink(NodeGraph node) {
        predLinks.add(new Link(node,Link.TYPE_UNDETERMINED));
    }
    
    public void addPredLink(NodeGraph node, int linkType) {
        predLinks.add(new Link(node, linkType));
    }

    public NodeGraph getFirstPredLink() {
        return predLinks.get(0).getNode();
    }

    public void removePredLink(NodeGraph linkNode) {
        try {
            predLinks.remove((predLinks.stream().filter(l -> l.getNode().equals(linkNode)).findFirst().get()));
        } catch(NoSuchElementException e) {
            System.out.println("No predecessor nodes");
        }
    }

    public void removePredLink(Link link) {
        predLinks.remove(link);
    }
    
    public ListIterator<Link> getPredLinksIterator() {
        return predLinks.listIterator();
    }
    
    public void addSuccLink(NodeGraph node) {
        succLinks.add(new Link(node,Link.TYPE_UNDETERMINED));
    }
    
    public void addSuccLink(NodeGraph node, int linkType) {
        succLinks.add(new Link(node, linkType));
    }

    public NodeGraph getFirstSuccLink() {
        return succLinks.get(0).getNode();
    }

    public void removeSuccLink(NodeGraph linkNode) {
        try {
            succLinks.remove((succLinks.stream().filter(l -> l.getNode().equals(linkNode)).findFirst().get()));
        }catch (NoSuchElementException e) {
            System.out.println("No successor nodes");
        }
    }

    public void removeSuccLink(Link link) {
        succLinks.remove(link);
    }
    
    public ListIterator<Link> getSuccLinksIterator() {
        return succLinks.listIterator();
    }
    
}
