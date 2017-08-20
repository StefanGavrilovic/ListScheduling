/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graph;

import java.util.ListIterator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Stefan
 */
public class GraphTest {
    
    private Graph graph;
    
    public GraphTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        graph = new Graph("test3.txt");
        graph.criticalPath();
    }
    
    @After
    public void tearDown() {
    }

    @Ignore
    @Test
    public void testRemoveLinks() {
        graph.removeGraphLinks();
        
        ListIterator<NodeGraph> nodeIterator = graph.getIterator();
        
        while(nodeIterator.hasNext()) {
            NodeGraph n = nodeIterator.next();
            
            ListIterator<Edge> linkIterator = n.getSuccLinksIterator();
            
            while(linkIterator.hasNext()) {
                Edge l = linkIterator.next();
                Assert.assertTrue(l.getLinkType() == Edge.DEPENDENCY);
            }
        }
    }
    
    @Test
    public void testCriticalPath() {
        
        double [] expected = {0., 1., 0., 0., 0., 0.};
        double [] actual = {graph.getFirstNode().getNodeWeight(), graph.getFirstNode().getNodeWeight(), graph.getFirstNode().getNodeWeight(), 
        graph.getFirstNode().getNodeWeight(), graph.getFirstNode().getNodeWeight(), graph.getFirstNode().getNodeWeight()};
        Assert.assertArrayEquals(expected, actual, 0);
    }
}
