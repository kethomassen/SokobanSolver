package sokoban;

import sokoban.SokobanMap;

/**
 * Represents a heuristic function for using in informed solvers
 */
@FunctionalInterface
public interface HeuristicFunction {

    /**
     * Estimate the number of steps from current state
     * to goal state (where every box is on a target).
     * 
     * @param map
     * @return estimated cost from state to goal state
     */
    int costToGoal(SokobanMap map);
}
