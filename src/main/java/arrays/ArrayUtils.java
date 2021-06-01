package arrays;

/**
 * @author Jonas Everaert
 * @author <a href="https://jonaseveraert.be">jonaseveraert.be</a>
 * @since 1.0
 */
public abstract class ArrayUtils {

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
}
