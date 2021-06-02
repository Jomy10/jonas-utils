package be.jonaseveraert.util.compression;

public class ByteArrayCannotBeCompressedException extends Exception {
    public ByteArrayCannotBeCompressedException(String errorMessage) {
        super(errorMessage);
    }
}
