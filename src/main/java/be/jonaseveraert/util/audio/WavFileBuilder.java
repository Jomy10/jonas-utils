package be.jonaseveraert.util.audio;

import be.jonaseveraert.util.progressBar.ProgressBarHandler;
import org.apache.commons.lang3.ArrayUtils;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

// TODO: support a progress bar class (give a progress bar object in the constructor) -> interface

/**
 * Class for building Wav files.<p>
 * In the see also, I linked some resources that were key to making this class. (The first one is especially helpful if
 * you want to understand how the wav file is made) <p>
 *
 * <h6>Example code:</h6>
 * <pre>{@code
 *     // This code example also shows you how you can use the progressBarHandler and
 *     // ProgressBarWindow
 *
 *     // We will copy this audio file twice into a new audiofile
 *     File audioFile = new File("pathname for a wav file with the same specifications as the WavFileBuilder");
 *
 *     // Set up the progressbar
 *     ProgressBarWindow pbWindow = new ProgressBarWindow();
 *     ProgressBarHandler pbHandler = new ProgressBarHandler(pbWindow.getjPanel());
 *
 *     // Create the WavFileBuilder
 *     WavFileBuilder wavFileBuilder = new WavFileBuilder(AUDIOFORMAT_PCM, 2, 22050, 16, pbHandler);
 *
 *     // For this example, I will add the same audio track 10 times to the
 *     // wav file we are creating
 *     for (int i = 0; i < 10; i++) {
 *         wavFileBuilder.addAudioFile(audioFile);
 *     }
 *
 *     // The path to which the wav file will be saved
 *     File outputFile = new File("pathname for the outputfile");
 *
 *     // Show the progressbar and start creating and saving the wavfile
 *     pbWindow.setVisible(true);
 *     wavFileBuilder.saveFile(outputFile);
 *
 *     // Close the window again
 *     pbWindow.setVisible(false);
 *     pbWindow.dispose();
 * }</pre>
 * @author Jonas Everaert
 * @author <a href="https://jonaseveraert.be">jonaseveraert.be</a>
 * @version %I%
 * @since 1.0
 *
 * @see <i>WAVE PCM soundfile format</i>. Stanford.edu (Dec 10, 2008). <a href="https://web.archive.org/web/20081210162727/https://ccrma.stanford.edu/CCRMA/Courses/422/projects/WaveFormat/">Wayback machine link</a>
 * @see <i>Java Sound Tutorials</i>. <a href = "https://docs.oracle.com/javase/tutorial/sound/converters.html">Java Docs (27 may 2021).</a>
 */
public class WavFileBuilder {
    // Strings are in big endian, int in little endian
    private final String chunkID = "RIFF";
    private int chunkSize;
    private final String format = "WAVE";

    // Sub chunk 1: format (fmt)
    private final String subchunk1ID = "fmt ";
    private int subchunk1Size = 16; // TODO: make dynamic to support the ExtraParams field (for future)
    private final int audioFormat;
    private final int numChannels;
    private final int sampleRate;
    private final int byteRate;
    private final int blockAlign;
    private final int bitsPerSample;

    // Sub chunk 2: data
    private final String subchunk2ID = "data";
    private int subchunk2Size;

    // Logging
    Logger logger = Logger.getLogger(WavFileBuilder.class.getName());

    // Progress bar
    ProgressBarHandler pbHandler;
    final boolean trackProgress;

    // Variables for the constructor
    public static final int AUDIOFORMAT_PCM = 1;
    public static final int NUM_CHANNELS_STEREO = 2;
    public static final int NUM_CHANNELS_MONO = 1;
    // TODO: in addAudioFile: check for equal sample rate, bitsPerSample, audiFormat, bum of channels, ... -> otherwise convert
    /**
     * <p> Constructs a WavFileBuilder which can be used to create wav files.</p>
     *
     * <p>The builder takes care of the subchunks based on the parameters that are given in the constructor.</p>
     *
     * <h3>Adding audio to the wav file</h3>
     * There are 2 methods that can be used to add audio data to the WavFile.
     * One is {@link #addBytes(byte[]) addBytes} which lets you directly inject bytes
     * into the data section of the wav file.
     * The other is {@link #addAudioFile(File) addAudioFile} which lets you add the audio
     * data of another file to the wav file's audio data.
     *
     * @param audioFormat The be.jonaseveraert.util.audio format of the wav file {@link #AUDIOFORMAT_PCM PCM} = 1
     * @param numChannels The number of channels the wav file will have {@link #NUM_CHANNELS_MONO MONO} = 1,
     *                    {@link #NUM_CHANNELS_STEREO STEREO} = 2
     * @param sampleRate The sample rate of the wav file in Hz (e.g. 22050, 44100, ...)
     * @param bitsPerSample The amount of bits per sample. If 16 bits, the be.jonaseveraert.util.audio sample will contain 2 bytes per
     *                      channel. (e.g. 8, 16, ...). This is important to take into account when using the
     *                      {@link #addBytes(byte[]) addBytes} method to insert data into the wav file.
     */
    public WavFileBuilder(int audioFormat, int numChannels, int sampleRate, int bitsPerSample) {
        this.audioFormat = audioFormat;
        this.numChannels = numChannels;
        this.sampleRate = sampleRate;
        this.bitsPerSample = bitsPerSample;

        // Subchunk 1 calculations
        this.byteRate = this.sampleRate * this.numChannels * (this.bitsPerSample / 8);
        this.blockAlign = this.numChannels * (this.bitsPerSample / 8);

        this.trackProgress = false;
    }

    /**
     * <p>Constructs a WavFileBuilder which can be used to create wav files.</p>
     *
     * <p>The builder takes care of the subchunks based on the parameters that are given in the constructor.</p>
     *
     * <h3>Adding audio to the wav file</h3>
     * There are 2 methods that can be used to add audio data to the WavFile.
     * One is {@link #addBytes(byte[]) addBytes} which lets you directly inject bytes
     * into the data section of the wav file.
     * The other is {@link #addAudioFile(File) addAudioFile} which lets you add the be.jonaseveraert.util.audio
     * data of another file to the wav file's audio data.
     *
     * @param audioFormat The audio format of the wav file {@link #AUDIOFORMAT_PCM PCM} = 1
     * @param numChannels The number of channels the wav file will have {@link #NUM_CHANNELS_MONO MONO} = 1,
     *                    {@link #NUM_CHANNELS_STEREO STEREO} = 2
     * @param sampleRate The sample rate of the wav file in Hz (e.g. 22050, 44100, ...)
     * @param bitsPerSample The amount of bits per sample. If 16 bits, the audio sample will contain 2 bytes per
     *                      channel. (e.g. 8, 16, ...). This is important to take into account when using the
     *                      {@link #addBytes(byte[]) addBytes} method to insert data into the wav file.
     * @param pbHandler A ProgressBarHandler can be any class that implements the {@link ProgressBarHandler ProgressBarHandler interface}.
     *                  This object will be used to manipulate a progressbar depending on how much work the process of saving the audio still has left.
     */
    public WavFileBuilder(int audioFormat, int numChannels, int sampleRate, int bitsPerSample, ProgressBarHandler pbHandler) {
        this.audioFormat = audioFormat;
        this.numChannels = numChannels;
        this.sampleRate = sampleRate;
        this.bitsPerSample = bitsPerSample;

        // Subchunk 1 calculations
        this.byteRate = this.sampleRate * this.numChannels * (this.bitsPerSample / 8);
        this.blockAlign = this.numChannels * (this.bitsPerSample / 8);

        this.pbHandler = pbHandler;
        this.trackProgress = true;

        this.pbHandler.setNumSubProcesses(3);

        this.pbHandler.setNumActivitiesInSubProcesses(0, 2);
        this.pbHandler.setNumActivitiesInSubProcesses(1, 1);
        this.pbHandler.setNumActivitiesInSubProcesses(2, 1);
        // Other methods: determine the amount of activities/tasks later

        this.pbHandler.setSubProcessInfo(0, "Collecting file data");
        this.pbHandler.setSubProcessInfo(1, "Combining byte be.jonaseveraert.util.arrays");
        this.pbHandler.setSubProcessInfo(2, "Writing be.jonaseveraert.util.audio data");
    }

    /**
     * Contains the be.jonaseveraert.util.audio data for the wav file that is being constructed
     */
    byte[] audioBytes = null;

    /**
     * Adds audio data to the wav file from bytes
     * <p>See the "see also" for the structure of the "Data" part of a wav file</p>
     * @param audioBytes be.jonaseveraert.util.audio data
     * @see <a href="https://web.archive.org/web/20081210162727/https://ccrma.stanford.edu/CCRMA/Courses/422/projects/WaveFormat/">Wave PCM Soundfile Format</a>
     */
    public void addBytes(byte[] audioBytes) {
        if (this.audioBytes != null)
            this.audioBytes = ArrayUtils.addAll(this.audioBytes, audioBytes);
        else
            this.audioBytes = audioBytes;
    }

    // TODO: conversion class and add a getParameters method or something to this for the conversion class
    /**
     * Adds the be.jonaseveraert.util.audio data from a wav file to the wav file you are creating
     * @param file a wav file with the same parameters as the {@code WavFileBuilder}.
     * @return true if the file's be.jonaseveraert.util.audio data was successfully added to the wav file's audioData byte array, false otherwise
     */
    public boolean addAudioFile(File file) {
        // TODO: clean up and rewrite
        //int totalSamplesRead = 0;
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
            AudioFormat audioFormat = audioInputStream.getFormat();
            int bytesPerSample = audioFormat.getFrameSize();
            if (bytesPerSample == AudioSystem.NOT_SPECIFIED) {
                // some be.jonaseveraert.util.audio formats may have unspecified frame size
                // in that case we may read any amount of bytes
                bytesPerSample = -1;
            }
            // frame = sample
            long numSamples = audioInputStream.getFrameLength();
            System.out.println("bytesPerSample: " + bytesPerSample );

            // Determine the amount of bytes the data of the file contains
            int numBytes = (int) (numSamples * bytesPerSample);
            if (bytesPerSample != (bitsPerSample / 8) * numChannels) {
                System.out.println("bitsPerSample: " + bitsPerSample + " | numChannels: " + numChannels);
                // TODO: convert file
                logger.log(Level.WARNING, "The bytesPerSample of the inputted file does not equal that of the be.jonaseveraert.util.be.jonaseveraert.util.audio.WavFile you are building. TODO: file conversion");
                return false;
            }
            byte[] audioBytes = new byte[numBytes];
            try {
                int numBytesRead = 0;
                //int numSamplesRead = 0;
                // Try to read numBytes bytes from the file
                while((numBytesRead = audioInputStream.read(audioBytes)) != -1) {
                    // Calculate the number of frames actually read.
                    //numSamplesRead = numBytesRead / bytesPerSample;
                    //totalSamplesRead += numSamplesRead;
                }
                // Audio data is now in the audioBytes array
                //logger.log(Level.INFO, "Succesfully read all be.jonaseveraert.util.audio data from the file.");
                this.addBytes(audioBytes); // Add the read be.jonaseveraert.util.audio data to the file
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * The number of activities that hare completed by the process, but are not yet used to update the progressBar UI.
     */
    private int activitiesToComplete = 0;
    private boolean processFinished = false;
    /**
     * Saves the file to the location of the {@code outputFile}.
     * @param outputFile The file that will be outputted (not created yet), contains the path
     * @return true if the file was created and written to successfully. Else false.
     * @throws IOException If an I/O error occurred
     */
    public boolean saveFile(File outputFile) throws IOException {
        // TODO: 2 branchs; one for without trackProgress and one with, so that it does not need to check for the trackProgress variable each time
        if (trackProgress)
            pbHandler.startProgressBar();
        // subchunk2 calculations

        //int numBytesInData = data.length()/2;
        int numBytesInData = audioBytes.length;
        int numSamples = numBytesInData / (2 * numChannels);

        subchunk2Size = numSamples * numChannels * (bitsPerSample / 8);

        // chunk calculation
        chunkSize = 4 + (8 + subchunk1Size) + (8 + subchunk2Size);

        // convert everything to hex string //
        // Chunk descriptor
        String f_chunkID = asciiStringToHexString(chunkID);
        String f_chunkSize = intToLittleEndianHexString(chunkSize, 4);
        String f_format = asciiStringToHexString(format);

        // fmt subchunck
        String f_subchunk1ID = asciiStringToHexString(subchunk1ID);
        String f_subchunk1Size = intToLittleEndianHexString(subchunk1Size, 4);
        String f_audioformat = intToLittleEndianHexString(audioFormat, 2);
        String f_numChannels = intToLittleEndianHexString(numChannels, 2);
        String f_sampleRate = intToLittleEndianHexString(sampleRate, 4);
        String f_byteRate = intToLittleEndianHexString(byteRate, 4);
        String f_blockAlign = intToLittleEndianHexString(blockAlign, 2);
        String f_bitsPerSample = intToLittleEndianHexString(bitsPerSample, 2);

        // data subchunk
        String f_subchunk2ID = asciiStringToHexString(subchunk2ID);
        String f_subchunk2Size = intToLittleEndianHexString(subchunk2Size, 4);
        // data is stored in audioData

        // Combine all hex data into one String (except for the be.jonaseveraert.util.audio data, which is passed in as a byte array
        final String AUDIO_BYTE_STREAM_STRING = f_chunkID  + f_chunkSize + f_format
                + f_subchunk1ID + f_subchunk1Size + f_audioformat + f_numChannels + f_sampleRate + f_byteRate  + f_blockAlign + f_bitsPerSample
                + f_subchunk2ID + f_subchunk2Size;
        if (trackProgress)
            pbHandler.completeActivity(true);
        // Convert the hex data to a byte array
        final byte[] BYTES = hexStringToByteArray(AUDIO_BYTE_STREAM_STRING);
        if (trackProgress)
            pbHandler.completeActivity(true);

        // Create & write file
        if (outputFile.createNewFile()) {
            // Combine byte be.jonaseveraert.util.arrays
            byte[] audioFileBytes = ArrayUtils.addAll(BYTES, audioBytes);
            if (trackProgress)
                pbHandler.completeActivity(true);

            // NOTE: I tried using this and it caused problems with bytes that weren't UTF-8 characters, so I would be
            // careful with this one if you're trying to write individual bytes to build a file, for example
            // So, this works for ASCII, but probably not for UTF-8, do some testing
            // (https://stackoverflow.com/questions/4350084/byte-to-file-in-java)

            // Files.write(new File(filePath.toPath(), bytes); <- -- Check for performance between this and the fos
            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                fos.write(audioFileBytes); // Write the bytes into a file
            } // catch Really necessary?
            catch (IOException e) {
                logger.log(Level.SEVERE, "IOException occured");
                logger.log(Level.SEVERE, null, e);

                pbHandler.completeProcess();
                pbHandler.setMessage("Failed to create file.");
                return false;
            } // Last process finished
            if (trackProgress)
                pbHandler.completeActivity(true);

            logger.log(Level.INFO, "File created: " + outputFile.getName());
            if (trackProgress) {
                pbHandler.completeProcess();
                pbHandler.setMessage("File created: " + outputFile.getName());
            }
            return true;
        } else {
            //System.out.println("File already exists.");
            logger.log(Level.WARNING, "File already exists.");
            if (trackProgress) {
                pbHandler.completeProcess();
                pbHandler.setMessage("File " + outputFile.getName() + " already exists.");
            }
            // TODO: throw file already exists exception en @throws in java doc zetten dan
            return false;
        }
    }

    // Aiding methods
    /**
     * Converts a string containing hexadecimal to bytes
     * @param s e.g. 00014F
     * @return an array of bytes e.g. {00, 01, 4F}
     */
    private byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] bytes = new byte[len / 2];
        for (int i = 0; i < len; i+= 2) {
            bytes[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
        }
        return bytes;
    }

    /**
     * Converts an int to a hexadecimal string in the little-endian format
     * @param input an integer number
     * @param numberOfBytes The number of bytes the the integer is stored in
     * @return The integer as a hexadecimal string in the little-endian byte ordering
     */
    private String intToLittleEndianHexString(int input, int numberOfBytes) {
        String hexBigEndian = Integer.toHexString(input);
        StringBuilder hexLittleEndian = new StringBuilder();
        int amountOfNumberProcessed = 0;
        for (int i = 0; i < hexBigEndian.length()/2f; i++) {
            int endIndex = hexBigEndian.length() - (i * 2);
            try {
                hexLittleEndian.append(hexBigEndian.substring(endIndex-2, endIndex));
            } catch (StringIndexOutOfBoundsException e ) {
                hexLittleEndian.append(0).append(hexBigEndian.charAt(0));
            }
            amountOfNumberProcessed++;
        }
        while (amountOfNumberProcessed != numberOfBytes) {
            hexLittleEndian.append("00");
            amountOfNumberProcessed++;
        }
        return hexLittleEndian.toString();
    }

    /**
     * Converts a string containing ascii to its hexadecimal notation
     * @param input The string that has to be converted
     * @return The string as a hexadecimal notation in the big-endian byte ordering
     */
    private String asciiStringToHexString(String input) {
        byte[] bytes = input.getBytes(StandardCharsets.US_ASCII);
        StringBuilder hex = new StringBuilder();
        for (byte b : bytes) {
            String hexChar = String.format("%02X", b);
            hex.append(hexChar);
        }
        return hex.toString().trim();
    }
}
