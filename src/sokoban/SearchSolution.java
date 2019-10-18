package sokoban;

/**
 * @author Kristian Thomassen, Harry Keightley
 */
public class SearchSolution {
    private final SearchPath path;
    private final int nodesGenerated;
    private final int fringeNodes;
    private final int exploredNodes;
    private final long timeTaken;

    public SearchSolution(SearchPath path, int nodesGenerated,
            int fringeNodes, int exploredNodes, long timeTaken) {
        this.path = path;
        this.nodesGenerated = nodesGenerated;
        this.fringeNodes = fringeNodes;
        this.exploredNodes = exploredNodes;
        this.timeTaken = timeTaken;
    }

    public int numExploredNodes() {
        return exploredNodes;
    }

    public int numFringeNodes() {
        return fringeNodes;
    }

    public int numNodesGenerated() {
        return nodesGenerated;
    }

    public SearchPath getPath() {
        return path;
    }

    public long getTimeTaken() {
        return timeTaken;
    }

    @Override
    public String toString() {
        return path.toString() + System.lineSeparator() +
                nodesGenerated + ", " +
                fringeNodes + ", " +
                exploredNodes + ", " +
                timeTaken;
    }
}
