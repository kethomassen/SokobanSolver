package sokoban.exception;

/**
 * Exception thrown when a bad map is attempted to be loaded.
 */
public class BadMapException extends Exception {

    public BadMapException() {}

    public BadMapException(String msg) {
        super(msg);
    }

}
