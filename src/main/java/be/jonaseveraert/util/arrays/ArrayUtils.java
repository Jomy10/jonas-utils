package be.jonaseveraert.util.arrays;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

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
     * Inverts all bytes in an array (e.g. 000101 becomes 111010)
     * @param bytes the array you want to invert
     * @return a new array containing the inverted bytes
     * @throws EmptyArrayException When the inputted {@code bytes} array is null.
     */
    public static byte[] invertByteArray(byte[] bytes) throws EmptyArrayException {
        if (bytes == null) {
            throw new EmptyArrayException("The bytes array is null and cannot be null in this method.");
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

    // HEX
    private static final byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);
    /**
     * Byte array to hex string.
     * Will use the {@code ASCII encoding}.
     * @param bytes the byte array you want to transform into a string
     * @return A String containing the bytes in hex form
     */
    public static String bytesToHex(byte[] bytes) {
        byte[] hexChars = new byte[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars, StandardCharsets.US_ASCII);
    }

    /**
     * Byte array to hex string
     * @param bytes the byte array you want to transform into a string
     * @param stringEncoding the encoding that will be used for the string
     * @return A String containing the bytes in hex form
     */
    public static String bytesToHex(byte[] bytes, Charset stringEncoding) {
        byte[] hexChars = new byte[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars, stringEncoding);
    }

    /**
     * Byte array to hex string. Will use the {@code ASCII encoding}.
     * @param bytes the byte array you want to transform into a string
     * @param addSpace if true will add a space between every hex element
     * @return A String containing the bytes in hex form
     */
    public static String bytesToHex(byte[] bytes, boolean addSpace) {
        if (!addSpace) {
            byte[] hexChars = new byte[bytes.length * 2];
            for (int j = 0; j < bytes.length; j++) {
                int v = bytes[j] & 0xFF;
                hexChars[j * 2] = HEX_ARRAY[v >>> 4];
                hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
            }
            return new String(hexChars, StandardCharsets.US_ASCII);
        } else {
            byte[] hexChars = new byte[bytes.length * 2];
            for (int j = 0; j < bytes.length; j++) {
                int v = bytes[j] & 0xFF;
                hexChars[j * 2] = HEX_ARRAY[v >>> 4];
                hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
            }
            String temp = new String(hexChars, StandardCharsets.US_ASCII);
            String[] tempArray = splitStringEvery(temp, 2);
            StringBuilder output = new StringBuilder();
            for (String s : tempArray) {
                output.append(s).append(" ");
            }
            return output.toString();
        }
    }

    // String arrays //

    /**
     * splits a string every {@code interval} th character
     * @param s the string you want to split
     * @param interval the interval
     * @return a String array containing {@code interval} characters per array
     */
    public static String[] splitStringEvery(String s, int interval) {
        int arrayLength = (int) Math.ceil(((s.length() / (double)interval)));
        String[] result = new String[arrayLength];

        int j = 0;
        int lastIndex = result.length - 1;
        for (int i = 0; i < lastIndex; i++) {
            result[i] = s.substring(j, j + interval);
            j += interval;
        } //Add the last bit
        result[lastIndex] = s.substring(j);

        return result;
    }
}
