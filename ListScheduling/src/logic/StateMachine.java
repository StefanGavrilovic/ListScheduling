/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;

/**
 *
 * @author Stefan
 */
public interface StateMachine {

    /**
     * States of the algorithm.
     */
    public enum state {
        START, LOAD_GRAPH, INSPECT_GRAPH, CRITICAL_PATH, RUN_ALGORITHM, EXECUTE
    };

    /**
     * This method should execute methods that belong to the current state of
     * algorithm.
     */
    public void execute();

    /**
     * This method sets current state to the next state of the algorithm.
     */
    public void nextState();

    /**
     * This method sets current state to the previous state of the algorithm.
     */
    public void prevState();

}
