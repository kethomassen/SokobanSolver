package sokoban;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;

/**
 * @author Kristian Thomassen, Harry Keightley
 */
public class SokobanSolver {

    private Comparator<SearchNode> comparator;

    public SokobanSolver(Comparator<SearchNode> comparator) {
        this.comparator = comparator;
    }

    /**
     * Solves a given Sokoban map
     *
     * @param map Map to solve
     * @return Solution if found, or null if no solution found
     */
    public SearchSolution solve(SokobanMap map) {
        PriorityQueue<SearchNode> queue = new PriorityQueue<>(100, comparator);
        HashSet<SokobanMap> seen = new HashSet<>();

        long startTime = System.currentTimeMillis();
        int nodesGenerated = 1;

        // add initial node
        queue.add(new SearchNode(map, SearchPath.EMPTY_PATH));

        while (!queue.isEmpty()) {
            SearchNode node = queue.poll();

            if (node.getMap().isGoalState()) {
                long timeTaken = System.currentTimeMillis() - startTime;
                int exploredNodes = seen.size();
                int fringeNodes = queue.size();

                return new SearchSolution(node.getPathToThisNode(),
                        nodesGenerated,
                        fringeNodes,
                        exploredNodes,
                        timeTaken);
            }
            
            seen.add(node.getMap());
            
            if (node.getMap().isDeadlocked()) {
                continue;
            }
            
            List<SearchNode> neighbours = node.getNeighbours();

            for (SearchNode neighbour : neighbours) {
                if (!seen.contains(neighbour.getMap())) {
                    queue.add(neighbour);
                    nodesGenerated++;
                }
            }
        }
        
        // no solution :(
        return null;
    }
}
