/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;

/**
 * The Instruction class represents simplified instructions for the purposes of
 * list scheduling algorithm.
 * 
 * @author Stefan
 */
public class Instruction {

    /**
     * Instruction's result.
     */
    private String result;
    /**
     * Instruction's operand.
     */
    private String A;
    /**
     * Instruction's operand.
     */
    private String B;
    /**
     * Instruction's operation.
     */
    private String op;

    /**
     * The Constructor.
     * 
     * @param instruction {@link String} Represents instruction.
     */
    Instruction(String instruction) {
        String[] tmp1 = instruction.split("=");
        result = tmp1[0];
        String[] tmp2 = tmp1[1].split("[+|/|*|-]");
        A = tmp2[0];
        B = tmp2[1];
        op = tmp1[1].substring(A.length(), A.length() + 1);
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getA() {
        return A;
    }

    public void setA(String A) {
        this.A = A;
    }

    public String getB() {
        return B;
    }

    public void setB(String B) {
        this.B = B;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }
    
    @Override
    public String toString() {
        return String.join(" ", result + "=", A, op, B);
    }

}
