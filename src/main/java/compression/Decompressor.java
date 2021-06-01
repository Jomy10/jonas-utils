package compression;

import arrays.OutputArraySizeTooLargeException;

import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import static arrays.ArrayUtils.partArray;

/**
 * A class that decompresses a compressed byte array that was compressed using the popular ZLIB compression library.<br/>
 * <br/>
 * For a code example, see the {@link Compressor Compressor class}
 *
 * @author Jonas Everaert
 * @author https://jonaseveraert.be
 * @since 1.0
 */
public class Decompressor {
    private final byte[] input;
    /**
     * The maximum size the outputted byte array can be
     */
    private int MAX_OUTPUT_SIZE = 1000;

    /**
     * Decompresses bytes.
     * @param compressedBytes A byte array containing compressed bytes compressed with the ZLIB compression library.
     */
    public Decompressor(byte[] compressedBytes) {
        this.input = compressedBytes;
    }

    /**
     * Decompresses the given {@link #input input} (a compressed array of bytes)
     * @return the decompressed data in byte array form.
     * @throws DataFormatException if the compressed data format is invalid.
     */
    public byte[] decompress() throws DataFormatException {
        Inflater decompressor = new Inflater();
        decompressor.setInput(input);
        byte[] output = new byte[MAX_OUTPUT_SIZE];
        int uncompressedLength = decompressor.inflate(output);
        decompressor.end();

        try {
            return partArray(output, uncompressedLength);
        } catch (OutputArraySizeTooLargeException e) {
            e.printStackTrace();
            throw new UnknownError();
        }
    }

    /**
     * Sets the maximum output size of the byte array. If the byte array you want to get has more than 1000 bytes, then
     * you can use this method to set the maximum output size.
     */
    public void setMaxOutputSize(int maxSize) {
        this.MAX_OUTPUT_SIZE = maxSize;
    }
}
