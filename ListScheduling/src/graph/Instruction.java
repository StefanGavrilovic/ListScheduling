/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graph;

/**
 *
 * @author Stefan
 */
class Instruction {

    private String result;
    private String A;
    private String B;
    
    Instruction(String instruction) {
        String [] tmp1 = instruction.split("=");
        result = tmp1[0];
        String [] tmp2 = tmp1[1].split("[+|/|*|-]");
        A = tmp2[0];
        B = tmp2[1];
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
    
}
