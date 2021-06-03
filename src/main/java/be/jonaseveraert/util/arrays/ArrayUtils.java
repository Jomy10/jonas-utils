package be.jonaseveraert.util.arrays;

/**
 * A collection of methods for arrays.
 * @author Jonas Everaert
 * @author <a href="https://jonaseveraert.be">jonaseveraert.be</a>
 * @since 1.0
 */
public abstract class ArrayUtils {

    // Todo: also make all methods without explicit type in the name for other array types
    // Byte Arrays //
    /**
     * Copies a srcArray, but only the first bytes until {@code outputArraySize}.
     * @param srcArray the array you want to copy from
     * @param outputArraySize the size you want the output array to be. Or the amount of bytes
     *                        you want to copy from the src array to the output array minus 1
     * @return An array that contains the data of the {@code srcArray}, but only containing {@code outputArraySize} bytes
     *
     * @throws OutputArraySizeTooLargeException when the {@code outputArraySize} is bigger than the {@code srcArray}'s length
     */
    public static byte[] partArray(byte[] srcArray, int outputArraySize) throws OutputArraySizeTooLargeException {
        byte[] partArray = new byte[outputArraySize];
        try {
            System.arraycopy(srcArray, 0, partArray, 0, outputArraySize);
        } catch (IndexOutOfBoundsException e) {
            throw new OutputArraySizeTooLargeException("The array could not be copied because the outputArraySize is bigger than the srcArray", e);
        }
        return partArray;
    }

    /**
     * Copies a srcArray, but only the first bytes until {@code outputArraySize}.
     * @param srcArray the array you want to copy from
     * @param outputArraySize the size you want the output array to be. Or the amount of bytes
     *                        you want to copy from the src array to the output array minus 1
     * @param srcArrayStartingPosition the offset where the method will start copying the srcArray
     * @return An array that contains the data of the {@code srcArray}, but only containing {@code outputArraySize} bytes
     *
     * @throws OutputArraySizeTooLargeException when the {@code outputArraySize} is bigger than the {@code srcArray}'s length - {@code srcArrayStartingPosition}
     */
    public static byte[] partArray(byte[] srcArray, int outputArraySize, int srcArrayStartingPosition) throws OutputArraySizeTooLargeException {
        byte[] partArray = new byte[outputArraySize];
        try {
            System.arraycopy(srcArray, srcArrayStartingPosition, partArray, 0, outputArraySize);
        } catch (IndexOutOfBoundsException e) {
            throw new OutputArraySizeTooLargeException("The array could not be copied because the outputArraySize is bigger than the srcArray", e);
        }
        return partArray;
    }

    /**
     * Inverts all bytes in an array
     * @param bytes the array you want to invert
     * @return a new array containing the inverted bytes
     */
    public static byte[] invertByteArray(byte[] bytes) {
        if (bytes == null) {
            return null;
            // TODO: throw empty byte array expcetion
        }

        byte[] outputArray = new byte[bytes.length];
        for(int i = 0; i < bytes.length; i++) {
            outputArray[i] = (byte) ~bytes[i];
        }
        return outputArray;
    }

    /**
     * Repeats an array into a new array x times
     * @param array the array you want to copy x times
     * @param repeat the amount of times you want to copy the array into the new array
     * @return an array containing the content of {@code array} {@code repeat} times.
     */
    public static byte[] repeatArray(byte[] array, int repeat) {
        byte[] result = new byte[array.length * repeat];
        for (int i = 0; i < result.length; i++) {
            result[i] = array[i % array.length];
        }
        return result;
    }
}
