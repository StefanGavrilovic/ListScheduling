/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import utils.logic.ListSchedulings;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.scene.Group;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

/**
 *
 * @author Stefan
 */
public class NodeGraph extends Group{

    public static final int NODE_RADIUS = 40;
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
     * Body of the Node to be shown on scene.
     */
    private final Circle body;
    /**
     * Flag to mark dead code.
     */
    private boolean deadResult;
    /**
     * Flag to mark executed node.
     */
    private boolean finished;
    /**
     * Flag to mark node chosen for execution.
     */
    private boolean chosen;
    /**
     * Text that shows node's name.
     */
    private Text text;
    /**
     * Text that shows node's weight.
     */
    private Text textW;
    /**
     * Node's tooltip.
     */
    private Tooltip tooltip;
    
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
    public NodeGraph(String name, String duration, String instruction) {
        this.name = name; 
        this.ordNumber = Integer.parseInt(name.substring(2));
        this.duration = Integer.parseInt(duration);
        this.instruction = new Instruction(instruction);
        this.predLinks = new LinkedList<>();
        this.succLinks = new LinkedList<>();
        this.delayCriticalPath = -1;
        this.weightNode = -1.0;
        this.deadResult = false;
        this.finished = false;
        this.chosen = false;
        
        body = new Circle(NODE_RADIUS);
        body.setFill(ListSchedulings.PREPARE);
        body.setStroke(Color.BLACK);
        
        final NumberFormat formatter = new DecimalFormat("#0.000");
        text = new Text(-(TEXT_WIDTH) - TEXT_SIZE, TEXT_HEIGHT / 2, this.name);
        textW = new Text(-(TEXT_WIDTH) - TEXT_SIZE * 2.5, TEXT_HEIGHT * 2, formatter.format(this.weightNode));

        tooltip = new Tooltip();
        
        this.setOnMouseEntered(event -> {
            this.getTooltip().setText(this.getInstruction().toString());
            Tooltip.install(body, tooltip);
        });
        this.setOnMouseExited(event -> {
            Tooltip.uninstall(body, tooltip);
        });
        
        this.getChildren().addAll(body, text, textW);
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
    
    public void setDelayCriticalPath(int delay) {
        this.delayCriticalPath = delay;
    }
    
    public boolean OnCriticalPath() {
        return this.delayCriticalPath == 0;
    }
    
    public void setNodeWeight(double weight) {
        this.weightNode = weight;
        final NumberFormat formatter = new DecimalFormat("#0.000");
        this.textW.setText(formatter.format(weight));
    }
    
    public double getNodeWeight() {
        return this.weightNode;
    }

    public boolean isDeadResult() {
        return deadResult;
    }

    public void setDeadResult(boolean deadResult) {
        this.deadResult = deadResult;
        this.body.setFill(ListSchedulings.PREPARE);
    }

    public boolean isFinished() {
        return finished;
    }
    
    public void setFinished(boolean finished) {
        this.finished = finished;
    }
    
    public void addPredLink(NodeGraph nodeFrom, NodeGraph nodeTo) {
        predLinks.add(new Edge(nodeFrom, nodeTo, Edge.TYPE_UNDETERMINED));
    }
    
    public void addPredLink(NodeGraph nodeFrom, NodeGraph nodeTo, int linkType) {
        predLinks.add(new Edge(nodeFrom, nodeTo, linkType));
    }

    public boolean isEmptyPredLinksList() {
        return this.predLinks.isEmpty();
    }
    
    public boolean isEmptySuccLinksList() {
        return this.succLinks.isEmpty();
    }
    
    public NodeGraph getFirstPredLink() {
        return predLinks.get(0).getNodeFrom();
    }

    public void removePredLink(NodeGraph linkNode) {
        Optional.ofNullable(predLinks).ifPresent(links -> {
            Optional.ofNullable(links.stream().filter(l -> l.getNodeFrom().equals(linkNode)).findFirst().orElse(null)).ifPresent(l -> links.remove(l));
        });
    }

    public void removePredLink(Edge link) {
        predLinks.remove(link);
        link.hideEdge(); //test
    }
    
    public ListIterator<Edge> getPredLinksIterator() {
        return predLinks.listIterator();
    }
    
    public Stream<Edge> getPredLinksAsStream() {
        return predLinks.stream();
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
        Optional.ofNullable(succLinks).ifPresent(links -> {
            Optional.ofNullable(links.stream().filter(l -> l.getNodeTo().equals(linkNode)).findFirst().orElse(null)).ifPresent(l -> links.remove(l));
        });
    }

    public void removeSuccLink(Edge link) {
        succLinks.remove(link);
        link.hideEdge(); //test
    }
    
    public ListIterator<Edge> getSuccLinksIterator() {
        return succLinks.listIterator();
    }
    
    public Stream<Edge> getSuccLinksAsStream() {
        return succLinks.stream();
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
    
    public void changeBodyColor(Color color) {
        this.body.setFill(color);
    }
    
    public boolean compareNode(NodeGraph node) {
        return this.getName().contentEquals(node.getName());
    }
    
    public void refreshWeights(double weight) {
        final NumberFormat formatter = new DecimalFormat("#0.000");
        this.textW.setText(formatter.format(weight));
    }

    public boolean isChosen() {
        return chosen;
    }

    public void setChosen(boolean chosen) {
        this.chosen = chosen;
    }

    public double getWeightNode() {
        return weightNode;
    }

    public void setWeightNode(double weightNode) {
        this.weightNode = weightNode;
    }

    public Text getText() {
        return text;
    }

    public void setText(Text text) {
        this.text = text;
    }

    public Text getTextW() {
        return textW;
    }

    public void setTextW(Text textW) {
        this.textW = textW;
    }

    public Tooltip getTooltip() {
        return tooltip;
    }

    public void setTooltip(Tooltip tooltip) {
        this.tooltip = tooltip;
    }
    
    public void removeAllSuccEdges(int type){
        this.succLinks.removeAll(this.succLinks.stream().filter(l -> l.compareLinkType(type)).peek(l1 -> l1.hideEdge()).collect(Collectors.toList()));
    }
    
    public void removeAllPredEdges(int type){
        this.predLinks.removeAll(this.predLinks.stream().filter(l -> l.compareLinkType(type)).peek(l1 -> l1.hideEdge()).collect(Collectors.toList()));
    }
    
}
