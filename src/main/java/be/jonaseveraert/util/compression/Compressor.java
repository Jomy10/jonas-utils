package be.jonaseveraert.util.compression;

import be.jonaseveraert.util.arrays.ArrayUtils;
import be.jonaseveraert.util.arrays.OutputArraySizeTooLargeException;

import java.nio.charset.Charset;
import java.util.zip.Deflater;

/**
 * <p>A class for compressing byte data with the ZLIB format. This is a lossless compression format.</p>
 * <p>The data can be given in String form, or directly as a byte array.</p>
 * <p>
 * Here is a code example:</p>
 * <pre>{@code
 *     // This example compresses a String into byte form, then decompresses it
 *     // again and prints it out. If the string could not be compressed, then
 *     // the string will be printed without first compressing and decompressing
 *     // it. You can also compress a byte array directly
 *     String inputString = "This is the text I will be compressing, compressing, compressing, compressing.";
 *
 *     // Compressing
 *     Compressor compressor = new Compressor(inputString, StandardCharsets.US_ASCII);
 *         compressor.setCompressionLevel(9);
 *         byte[] compressedBytes;
 *         try {
 *             compressedBytes = compressor.compress();
 *
 *             Decompressor decompressor = new Decompressor(compressedBytes);
 *             try {
 *                 byte[] decompressedBytes = decompressor.decompress();
 *
 *                 // Print the decompressed string
 *                 String output = new String(decompressedBytes, 0, decompressedBytes.length, StandardCharsets.US_ASCII);
 *                 System.out.println(output);
 *             } catch (DataFormatException e) {
 *                 e.printStackTrace();
 *             }
 *
 *         } catch (ByteArrayCannotBeCompressedException e) {
 *             // If the text could not be compressed,
 *             // then I will set the compressedBytes to the
 *             // inputString's byte form so that I use the byte
 *             // array in whatever I need it for
 *             System.out.println("Bytes could not be compressed, using uncompressed bytes instead.");
 *             compressedBytes = inputString.getBytes(StandardCharsets.US_ASCII);
 *
 *             // Print as a string again
 *             System.out.println(new String(compressedBytes, 0, compressedBytes.length, StandardCharsets.US_ASCII));
 *         }
 * }</pre>
 *
 * @author Jonas Everaert
 * @author <a href="https://jonaseveraert.be">jonaseveraert.be</a>
 * @since 1.0
 */
public class Compressor {
    /**
     * The input that will be compressed
     */
    private final byte[] input;
    private int compressionLevel = 9;

    /**
     * The {@code Compressor} can be used to compress bytes with the ZLIB format. This is a lossless compression format.
     * @param input Input as a string, which will be converted to bytes.
     * @param charset The characterset that has to be used to convert the {@code input} to bytes.
     */
    public Compressor(String input, Charset charset) {
        this.input = input.getBytes(charset);
    }

    /**
     * The {@code Compressor} can be used to compress bytes with the ZLIB format. This is a lossless compression format.
     * @param input the bytes that will be compressed
     */
    public Compressor(byte[] input) {
        this.input = input;
    }

    /**
     * Compresses the {@link #input byte array} using the {@link Deflater Deflater} class, with the {@link #compressionLevel compression level}
     * that can be set with the {@link #setCompressionLevel(int) setCompressionLevel(int)} method
     * @return A compressed byte array based on the {@link #input input byte array}
     * @throws ByteArrayCannotBeCompressedException Thrown when the output size of the compression is equal to, or bigger
     * than the input length.<p>When this exception is thrown, you should therefore use the input instead of the compressed
     * form in the code that follows this method.</p>
     */
    public byte[] compress() throws ByteArrayCannotBeCompressedException {
        Deflater compressor = new Deflater(this.compressionLevel);
        compressor.setInput(this.input);
        compressor.finish();

        byte[] compressedInput = new byte[this.input.length + 1];

        int compressedLength = compressor.deflate(compressedInput);

        if (compressedLength >= this.input.length) {
            // There are not enough returning characters in the input that the compressed version is either bigger or the same size.
            throw new ByteArrayCannotBeCompressedException("The file could not be compressed; the compressed length is bigger than the input length.");
        }

        compressor.end();

        try {
            return ArrayUtils.partArray(compressedInput, compressedLength); // Only return the part of the array that has data
        } catch (OutputArraySizeTooLargeException e) {
            e.printStackTrace();
            throw new UnknownError();
        }
    }

    public static final int MAX_COMPRESSION = 9;
    public static final int FASTEST_COMPRESSION = 1;
    public static final int NO_COMPRESSION = 0;
    /**
     * Sets the be.jonaseveraert.util.compression level, which has to be a value between 0 and 9.
     * @param level The be.jonaseveraert.util.compression level, a value between 0 and 9. 0 indicates no be.jonaseveraert.util.compression, 9 is highest be.jonaseveraert.util.compression.
     * @throws InvalidCompressionLevelException Thrown when the {@code level} is not between 0 and 9
     */
    public void setCompressionLevel(int level) {
        if (level < 0 || level > 9) {
            throw new InvalidCompressionLevelException("The be.jonaseveraert.util.compression level could not be set, because the given level is not between 0 and 9.");
        } else {
            this.compressionLevel = level;
        }
    }
}
