package be.jonaseveraert.util.files.msWord;

import be.jonaseveraert.util.files.msWord.objects.WordObject;
import be.jonaseveraert.util.files.msWord.objects.WordParagraph;
import be.jonaseveraert.util.files.msWord.objects.WordPicture;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class WordDocument {
    private final List<WordObject> objects;

    /**
     * Creates a new word generator that can later be converted to a Word document.<br></br>
     * Use the {@link #addObjects(List)} method to add {@link WordObject WordObjects} to the document and use the {@link #export} method
     * to generate the Word document.
     */
    public WordDocument() {
        objects = new ArrayList<>();
    }

    /**
     * Creates a new word generator that can later be converted to a Word document.<br></br>
     * Use the {@link #addObjects(List)} method to add {@link WordObject WordObjects} to the document and use the {@link #export} method
     * to generate the Word document.
     * @param objects an initial list of Word objects
     */
    public WordDocument(List<WordObject> objects) {
        this.objects = objects;
    }

    public void addObjects(List<WordObject> objects) {
        this.objects.addAll(objects);
    }

    public void addObject(WordObject object) {
        this.objects.add(object);
    }

    /**
     * Creates a new Word file using the lines inputted in the {@link WordDocument}.
     * @param dest the destination where the Word file will be saved. Also contains
     *             the file name and extension.
     * @throws FileNotFoundException if the {@code dest} could not be found
     * @throws IOException if an I/0 exception occurs
     * @throws InvalidFormatException
     */
    public void export(File dest) throws IOException, InvalidFormatException {
        // Empty document
        XWPFDocument xwpfDoc = new XWPFDocument();

        for (WordObject object : objects) {
            if (object instanceof WordParagraph) {
                // Create paragraph
                XWPFParagraph paragraph = xwpfDoc.createParagraph();
                XWPFRun run = paragraph.createRun();
                run.setText(((WordParagraph) object).getText());
            } else if (object instanceof WordPicture) {
                XWPFRun run = xwpfDoc.createParagraph().createRun();
                FileInputStream fis = new FileInputStream(((WordPicture) object).getImgFile());
                run.addPicture(fis,
                        ((WordPicture) object).getPictureType(),
                        ((WordPicture) object).getFileName(),
                        Units.toEMU(((WordPicture) object).getImageDimension().width),
                        Units.toEMU(((WordPicture) object).getImageDimension().height));
                fis.close();
            }
        }

        // Write the document
        FileOutputStream fout = new FileOutputStream(dest);
        xwpfDoc.write(fout);
        // close doc
        fout.close();
    }

    /**
     * debug
     */
    public static void main(String[] args) throws IOException, InvalidFormatException {
        WordDocument gen = new WordDocument();
        List<WordObject> l = new ArrayList<>();
        WordParagraph p = new WordParagraph("test");
        l.add(p);
        l.add(new WordParagraph("testZ"));
        WordPicture picture = new WordPicture(new File("/Users/jonaseveraert/Desktop/Balloonfest (not fun) as album cover.png"));
        picture.resize(0.2);
        l.add(picture);
        gen.addObjects(l);
        gen.export(new File("/Users/jonaseveraert/Desktop/#temp/test.docx"));
    }
}
