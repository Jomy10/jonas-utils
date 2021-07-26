package be.jonaseveraert.util.files.msWord.objects;

import be.jonaseveraert.util.files.msWord.WordDocument;
import com.sun.istack.internal.NotNull;

/**
 * Represents a paragraph in Word <br></br>
 * TODO: implement {@link org.apache.poi.wp.usermodel.Paragraph}
 */
public class WordParagraph extends WordObject {
    private final StringBuilder text;

    /**
     * Creates a new Word paragraph that can be added to a {@link WordDocument WordGenerator}
     */
    public WordParagraph() {
        text = new StringBuilder();
    }
    /**
     * Creates a new Word paragraph that can be added to a {@link WordDocument WordGenerator}
     * @param text the initial text
     */
    public WordParagraph(String text) {
        if (text == null) {
            this.text = new StringBuilder();
            return;
        }

        this.text = new StringBuilder(text);
    }

    public void addText(String text) {
        this.text.append(text);
    }

    public String getText() {
        return this.text.toString();
    }

    /**
     * Replaces text in the paragraph
     * @param begin the begin index
     * @param end the end index
     * @param text the text you want it to replace  with
     */
    public void replace(int begin, int end, @NotNull String text) {
        this.text.replace(begin, end, text);
    }

    public void remove(int start, int end) {
        this.text.delete(start, end);
    }

    public void removeCharAt(int index) {
        this.text.deleteCharAt(index);
    }

    public void setCharAt(int index, char c) {
        this.text.setCharAt(index, c);
    }

    public char charAt(int index) {
        return this.text.charAt(index);
    }

    public int indexOf(String str) {
        return this.text.indexOf(str);
    }

    public int indexOf(String str, int fromIndex) {
        return this.text.indexOf(str, fromIndex);
    }

    public int lastIndexOf(String str) {
        return this.text.lastIndexOf(str);
    }

    public int lastIndexOf(String str, int fromIndex) {
        return this.text.lastIndexOf(str, fromIndex);
    }

    public void insert(int offset, String str) {
        this.text.insert(offset, str);
    }

    public void reverse() {
        this.text.reverse();
    }
}
