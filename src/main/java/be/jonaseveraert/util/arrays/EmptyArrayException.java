package be.jonaseveraert.util.arrays;

/**
 * Thrown when an array is null and cannot be null
 */
public class EmptyArrayException extends Exception {
    public EmptyArrayException(String message) {
        super(message);
    }

    public EmptyArrayException(String message, Throwable e) {
        super(message, e);
    }
}
