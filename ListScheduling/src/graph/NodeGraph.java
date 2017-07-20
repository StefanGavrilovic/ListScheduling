/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graph;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Stefan
 */
public class NodeGraph {

    private String name;
    private int duration;
    private Instruction instruction;
    private final List<NodeGraph> links;
    
    NodeGraph(String name, String duration, String instruction) {
        this.name = name;
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
    
    public void addLink(NodeGraph node){
        links.add(node);
    }
    
    public NodeGraph getFirstLink(){
        return links.get(0);
    }
    
    public void removeLink(NodeGraph node){
        links.remove(node);
    }
    
}
