package be.jonaseveraert.util.arrays;

public class OutputArraySizeTooLargeException extends Exception {
    public OutputArraySizeTooLargeException(String message) {
        super(message);
    }

    public OutputArraySizeTooLargeException(String message, Throwable e) {
        super(message, e);
    }
}
