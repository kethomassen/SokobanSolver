package sokoban;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import sokoban.util.Direction;

/**
 * SearchPath represents a sequence of movements (Directions)
 * @author Kristian Thomassen, Harry Keightley
 */
public class SearchPath {

    public static SearchPath EMPTY_PATH = new SearchPath();

    private List<Direction> path;

    /**
     * Initialise empty search Path
     */
    private SearchPath() {
        path = new ArrayList<>();
    }

    /**
     * Create new search path by extending an existing path with an extra
     * direction
     *
     * @param existingPath Path to extend
     * @param toAdd Direction to add to end
     */
    public SearchPath(SearchPath existingPath, Direction toAdd) {
        path = new ArrayList<>(existingPath.path);
        path.add(toAdd);
    }

    public List<Direction> getPath() {
        return new ArrayList<>(this.path);
    }

    public int length() {
        return path.size();
    }

    @Override
    public String toString() {
        StringJoiner builder = new StringJoiner(", ");

        for (Direction dir : path) {
            builder.add(dir.toString());
        }

        return builder.toString();
    }

    public boolean equals(Object o) {
        if (!(o instanceof SearchPath)) {
            return false;
        }

        SearchPath other = (SearchPath) o;

        return other.path.equals(path);
    }

    public int hashCode() {
        return this.path.hashCode();
    }
}
