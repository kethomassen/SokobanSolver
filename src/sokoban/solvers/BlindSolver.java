package sokoban.solvers;

import java.util.Comparator;
import sokoban.SearchNode;
import sokoban.SokobanSolver;

/**
 * @author Kristian Thomassen, Harry Keightley
 */
public class BlindSolver extends SokobanSolver {

    /**
     * Uniform Cost Search Solver
     */
    public BlindSolver() {
        super((SearchNode o1, SearchNode o2) -> {
            return o1.getPathToThisNode().length() - o2.getPathToThisNode().length();
        });
    }
}
