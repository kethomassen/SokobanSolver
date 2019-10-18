package sokoban;

import java.util.ArrayList;
import java.util.List;
import sokoban.util.Direction;

/**
 * Represents node in search tree for Sokoban solver.
 * @author Kristian Thomassen, Harry Keightley
 */
public class SearchNode {

    /**
     * Current state of map in this node
     */
    private SokobanMap curMap;

    /**
     * Path to get to this node from root
     */
    private SearchPath path;

    public SearchNode(SokobanMap currentMap, SearchPath path) {
        this.curMap = currentMap;
        this.path = path;
    }

    /**
     * Gets map state for this node.
     * 
     * @return map
     */
    public SokobanMap getMap() {
        return this.curMap;
    }

    /** 
     * Gets path from initial state to this node so far.
     * 
     * @return path
     */
    public SearchPath getPathToThisNode() {
        return this.path;
    }

    /**
     * Finds the nodes that can be reached by moving in one direction from
     * current state.
     *
     * Only returns nodes in directions that are able to be reached from current
     * state/position.
     *
     * @return neighbours of this node
     */
    public List<SearchNode> getNeighbours() {

        List<SearchNode> neighbours = new ArrayList<>();

        for (Direction dir : Direction.values()) {
            if (curMap.canMove(dir)) {
                SearchNode newNode = new SearchNode(curMap.movePlayer(dir), 
                        new SearchPath(path, dir));

                neighbours.add(newNode);
            }
        }

        return neighbours;
    }
    
    public boolean equals(Object o) {
        if (!(o instanceof SearchNode)) {
            return false;
        }
        
        SearchNode other = (SearchNode) o;
        
        return other.curMap.equals(this.curMap);
    }
    
    public int hashCode() {
        return this.curMap.hashCode();
    }
}
