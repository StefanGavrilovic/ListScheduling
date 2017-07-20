/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graph;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Stefan
 */
public class Graph {
    
    private List<NodeGraph> nodes;
    private static final int HASH_CAP = 10;
    
    public Graph(String fileName){
        nodes = new ArrayList<>();
        HashMap<String, NodeGraph> hashMap = new HashMap<>(HASH_CAP);
        
        BufferedReader inputFile = null;
        try {
            inputFile = new BufferedReader(new FileReader(fileName));
            
            while(true){
                String string = inputFile.readLine();
                if (string == null)
                    break;
                StringTokenizer st = new StringTokenizer(string, " ");
                NodeGraph node = new NodeGraph(st.nextToken(), st.nextToken(), st.nextToken());
                nodes.add(node);
                if(hashMap.containsKey(node.getInstruction().getA())){
                    node.addLink(hashMap.get(node.getInstruction().getA()));
                }
                if(hashMap.containsKey(node.getInstruction().getB())){
                    node.addLink(hashMap.get(node.getInstruction().getB()));
                }
                hashMap.put(node.getInstruction().getResult(), node);
            }
            
            hashMap.clear();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Graph.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Graph.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (inputFile != null){
                    inputFile.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(Graph.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public NodeGraph getNode(){
        if(nodes.size() > 0)
            return nodes.remove(0);
        else
            return null;
    }
}
