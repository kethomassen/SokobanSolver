package sokoban.solvers;

import sokoban.HeuristicFunction;
import sokoban.SearchNode;
import sokoban.SokobanSolver;

/**
 * @author Kristian Thomassen, Harry Keightley
 */
public class InformedSolver extends SokobanSolver {

    /**
     * A* solver for given heuristic function
     * 
     * @param heuristic
     */
    public InformedSolver(HeuristicFunction heuristic) {
        super((SearchNode o1, SearchNode o2) -> {
            int cost1 = heuristic.costToGoal(o1.getMap()) +
                    o1.getPathToThisNode().length();

            int cost2 = heuristic.costToGoal(o2.getMap()) +
                    o2.getPathToThisNode().length();

            return cost1 - cost2;
        });
    }
}
