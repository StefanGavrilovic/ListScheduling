/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graph;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author Stefan
 */
public class InstructionTest {
    
    private final Instruction instr;
    
    public InstructionTest() {
        System.out.println("* UtilsJUnit4Test: construct");
        instr = new Instruction("C=A+B");
    }
    
    @BeforeClass
    public static void setUpClass() {
        System.out.println("* UtilsJUnit4Test: @BeforeClass method");
    }
    
    @AfterClass
    public static void tearDownClass() {
        System.out.println("* UtilsJUnit4Test: @AfterClass method");
    }
    
    @Before
    public void setUp() {
        System.out.println("* UtilsJUnit4Test: @Before method");
    }
    
    @After
    public void tearDown() {
        System.out.println("* UtilsJUnit4Test: @After method");
    }

    /**
     * Test of getResult method, of class Instruction.
     */
    @Test
    public void testGetResult() {
        System.out.println("getResult");
        String expResult = "C";
        String result = instr.getResult();
        assertEquals(expResult, result);
    }

    /**
     * Test of setResult method, of class Instruction.
     */
    @Ignore
    @Test
    public void testSetResult() {
        System.out.println("setResult");
        String result_2 = "";
        Instruction instance = null;
        instance.setResult(result_2);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getA method, of class Instruction.
     */
    @Test
    public void testGetA() {
        System.out.println("getA");
        String expResult = "A";
        String result = instr.getA();
        assertEquals(expResult, result);
    }

    /**
     * Test of setA method, of class Instruction.
     */
    @Ignore
    @Test
    public void testSetA() {
        System.out.println("setA");
        String A = "";
        Instruction instance = null;
        instance.setA(A);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getB method, of class Instruction.
     */
    @Test
    public void testGetB() {
        System.out.println("getB");
        String expResult = "B";
        String result = instr.getB();
        assertEquals(expResult, result);
    }

    /**
     * Test of setB method, of class Instruction.
     */
    @Ignore
    @Test
    public void testSetB() {
        System.out.println("setB");
        String B = "";
        Instruction instance = null;
        instance.setB(B);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
