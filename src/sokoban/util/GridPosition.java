package sokoban.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Kristian Thomassen, Harry Keightley
 */
public class GridPosition {

    private final int x;
    private final int y;

    public GridPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public GridPosition move(Direction direction) {
        if (direction == Direction.UP) {
            return new GridPosition(x, y - 1);
        } else if (direction == Direction.DOWN) {
            return new GridPosition(x, y + 1);
        } else if (direction == Direction.LEFT) {
            return new GridPosition(x - 1, y);
        } else { // RIGHT
            return new GridPosition(x + 1, y);
        }
    }
    
    /**
     * Mahattan distance to another position
     * 
     * @param other
     * @return
     */
    public int distanceTo(GridPosition other) {
        return Math.abs(other.getX() - this.x) + Math.abs(other.getY() - this.y);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GridPosition)) {
            return false;
        }
        
        GridPosition other = (GridPosition) o;
        
        return x == other.x && y == other.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Pos: (" + this.getX() + ", " + this.getY() + ')';
    }

    public static List<GridPosition> getStretch(GridPosition a, GridPosition b) {
        ArrayList<GridPosition> result = new ArrayList<>();

        boolean sameX = a.getX() == b.getX();
        int fixed = sameX ? a.getX() : a.getY(); // Fixed coordinate

        int lowerBound, upperBound;
        if (sameX) {
            // Disgusting
            lowerBound = Math.min(a.getY(), b.getY());
            upperBound = Math.max(a.getY(), b.getY());
            for (int y = lowerBound; y <= upperBound; y++) {
                result.add(new GridPosition(fixed, y));
            }

        } else {
            lowerBound = Math.min(a.getX(), b.getX());
            upperBound = Math.max(a.getX(), b.getX());
            for (int x = lowerBound; x <= upperBound; x++) {
                result.add(new GridPosition(x, fixed));
            }
        }

        return result;
    }
}
