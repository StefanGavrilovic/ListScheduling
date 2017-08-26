/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package listscheduling;

/**
 *
 * @author Stefan
 */
public class StateMachine {
    
    private enum state {
        START, LOAD_GRAPH, INSPECT_GRAPH, CRITICAL_PATH, RUN_ALGORITHM
    };
    private String activeState;

    public StateMachine() {
        this.activeState = state.START.name();
    }
    
    public boolean execute() {
        switch(state.valueOf(activeState)){
            case START: break;
            case LOAD_GRAPH: break;
            case INSPECT_GRAPH: break;
            case CRITICAL_PATH: break;
            case RUN_ALGORITHM: break;
        }
        return false;
    }
    
    public void nextState() {
        activeState = (state.values()[(state.valueOf(activeState).ordinal()) + 1]).name();
    }
    
    public String getCurrentState() {
        return activeState;
    }
}
