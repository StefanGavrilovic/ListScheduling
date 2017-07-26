/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graph;

import java.util.ListIterator;
import junit.framework.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Stefan
 */
public class GraphTest {
    
    private static Graph graph;
    
    public GraphTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        graph = new Graph("test2.txt");
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testRemoveLinks() {
        graph.removeGraphLinks();
        
        ListIterator<NodeGraph> nodeIterator = graph.getIterator();
        
        while(nodeIterator.hasNext()) {
            NodeGraph n = nodeIterator.next();
            
            ListIterator<Link> linkIterator = n.getSuccLinksIterator();
            
            while(linkIterator.hasNext()) {
                Link l = linkIterator.next();
                Assert.assertTrue(l.getLinkType() == Link.DEPENDENCY);
            }
        }
    }
    
}
