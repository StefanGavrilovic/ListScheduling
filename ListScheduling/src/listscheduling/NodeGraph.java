/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package listscheduling;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

/**
 *
 * @author Stefan
 */
public class NodeGraph extends Group{

    public static final int NODE_RADIUS = 20;
    public static final int TEXT_WIDTH = 8;
    public static final int TEXT_HEIGHT = 10;
    public static final int TEXT_SIZE = 4;
    
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
    private final List<Edge> predLinks;
    /**
     * Operations that waits for this operation to be finished. Successor.
     */
    private final List<Edge> succLinks;
    /**
     * Approved delay for the execution of the operation.
     */
    private int delayCriticalPath;
    /**
     * Weight of the node. Heuristic algorithm for List Scheduling.
     */
    private double weightNode;
    
    /**
     * The constructor of Node in Graph.
     * @param name
     *          {@link String} name of the operation.
     * @param duration
     *          {@link String} number of cycles.
     * @param instruction
     *          {@link String} instruction to be executed.
     */
    @SuppressWarnings("OverridableMethodCallInConstructor")
    NodeGraph(String name, String duration, String instruction) {
        this.name = name; 
        this.ordNumber = Integer.parseInt(name.substring(2));
        this.duration = Integer.parseInt(duration);
        this.instruction = new Instruction(instruction);
        this.predLinks = new LinkedList<>();
        this.succLinks = new LinkedList<>();
        this.delayCriticalPath = -1;
        this.weightNode = 0.;
        
        Circle circle = new Circle(NODE_RADIUS);
        circle.setFill(Color.YELLOW);
        circle.setStroke(Color.BLACK);
        Text text = new Text(-(TEXT_WIDTH) - TEXT_SIZE, TEXT_HEIGHT / 2, this.name);

        this.getChildren().addAll(circle, text);
        this.setVisible(false);
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

    public void addPredLink(NodeGraph nodeFrom, NodeGraph nodeTo) {
        predLinks.add(new Edge(nodeFrom, nodeTo, Edge.TYPE_UNDETERMINED));
    }
    
    public void addPredLink(NodeGraph nodeFrom, NodeGraph nodeTo, int linkType) {
        predLinks.add(new Edge(nodeFrom, nodeTo, linkType));
    }

    public NodeGraph getFirstPredLink() {
        return predLinks.get(0).getNodeFrom();
    }

    public void removePredLink(NodeGraph linkNode) {
        try {
            predLinks.remove((predLinks.stream().filter(l -> l.getNodeFrom().equals(linkNode)).findFirst().get()));
        } catch(NoSuchElementException e) {
            System.out.println("No predecessor nodes");
        }
    }

    public void removePredLink(Edge link) {
        predLinks.remove(link);
        link.removeLink2D(); //test
    }
    
    public ListIterator<Edge> getPredLinksIterator() {
        return predLinks.listIterator();
    }
    
    public void addSuccLink(NodeGraph nodeFrom, NodeGraph nodeTo) {
        succLinks.add(new Edge(nodeFrom, nodeTo, Edge.TYPE_UNDETERMINED));
    }
    
    public void addSuccLink(NodeGraph nodeFrom, NodeGraph nodeTo, int linkType) {
        succLinks.add(new Edge(nodeFrom, nodeTo, linkType));
    }

    public NodeGraph getFirstSuccLink() {
        return succLinks.get(0).getNodeTo();
    }

    public void removeSuccLink(NodeGraph linkNode) {
        try {
            succLinks.remove((succLinks.stream().filter(l -> l.getNodeTo().equals(linkNode)).findFirst().get()));
        }catch (NoSuchElementException e) {
            System.out.println("No successor nodes");
        }
    }

    public void removeSuccLink(Edge link) {
        succLinks.remove(link);
        link.removeLink2D(); //test
    }
    
    public ListIterator<Edge> getSuccLinksIterator() {
        return succLinks.listIterator();
    }
    
    public Edge findSuccLink(NodeGraph linkNode) {
        ListIterator<Edge> iterator = getSuccLinksIterator();
        while (iterator.hasNext()) {
            Edge link = iterator.next();
            if (link.getNodeTo().equals(linkNode)) {
                return link;
            }
        }
        return null;
    }
    
    public void setDelayCriticalPath(int delay) {
        this.delayCriticalPath = delay;
    }
    
    public boolean OnCriticalPath() {
        return this.delayCriticalPath == 0;
    }
    
    public void setNodeWeight(double weight) {
        this.weightNode = weight;
    }
    
    public double getNodeWeight() {
        return this.weightNode;
    }
    
}
