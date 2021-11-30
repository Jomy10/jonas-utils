package be.jonaseveraert.util.files.msWord.objects;

import be.jonaseveraert.util.img.ImgUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Shape;
import org.apache.poi.util.NotImplemented;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class WordPicture extends WordObject implements Picture {
    private final File imgFile;
    private int pictureType;
    private final Dimension dimension;

    /**
     * Creates a new image that can be inserted in a {@link be.jonaseveraert.util.files.msWord.WordDocument WordDocument}
     * @param imgFile the image file
     * @throws IOException if an i/o exception occurs
     */
    public WordPicture(File imgFile) throws IOException {
        this.imgFile = imgFile;

        // Check the file extension
        String fileName = imgFile.getName();
        int indexOfDot = fileName.lastIndexOf(".");
        String extension = fileName.substring(indexOfDot).toLowerCase(Locale.ROOT);

        if (extension.contains("jpg") || extension.contains("jpeg")) {
            this.pictureType = XWPFDocument.PICTURE_TYPE_JPEG;
        } else if (extension.contains("png")) {
            this.pictureType = XWPFDocument.PICTURE_TYPE_PNG;
        } else if (extension.contains("tiff")) {
            this.pictureType = XWPFDocument.PICTURE_TYPE_TIFF;
        } else if (extension.contains("gif")) {
            this.pictureType = XWPFDocument.PICTURE_TYPE_GIF;
        }

        // Img dimensions
        dimension = ImgUtils.getImageDimension(imgFile);
    }

    public File getImgFile() {
        return imgFile;
    }

    public String getFileName() {
        return imgFile.getName();
    }

    /**
     *
     * @return the width and the height of the image
     */
    @Deprecated
    public Dimension getDimension() {
        return dimension;
    }

    /**
     * Picture type is set automatically based on the extension, but can also be set manually if problems occur.
     * @param type the picture type e.g. {@link org.apache.poi.xwpf.usermodel.XWPFDocument#PICTURE_TYPE_JPEG}
     */
    public void setPictureType(int type) {
        this.pictureType = type;
    }

    public int getPictureType() {
        return pictureType;
    }

    @Override
    public void resize() {
        this.resize(1);
    }

    @Override
    public void resize(double v) {
        this.resize(v, v);
    }

    @Override
    public void resize(double v, double v1) {
        dimension.width = (int) (v * dimension.width);
        dimension.height = (int) (v1 * dimension.height);
    }

    @Override
    @NotImplemented
    public ClientAnchor getPreferredSize() {
        return null;
    }

    @Override
    @NotImplemented
    public ClientAnchor getPreferredSize(double v, double v1) {
        return null;
    }

    @Override
    @NotImplemented
    public Dimension getImageDimension() {
        return dimension;
    }

    @Override
    @NotImplemented
    public PictureData getPictureData() {
        return null;
    }

    @Override
    @NotImplemented
    public ClientAnchor getClientAnchor() {
        return null;
    }

    @Override
    @NotImplemented
    public Sheet getSheet() {
        return null;
    }

    @Override
    @NotImplemented
    public String getShapeName() {
        return null;
    }

    @Override
    @NotImplemented
    public Shape getParent() {
        return null;
    }

    @Override
    @NotImplemented
    public ChildAnchor getAnchor() {
        return null;
    }


    private boolean fill = false;
    @Override
    @NotImplemented
    public boolean isNoFill() {
        return fill;
    }

    @Override
    @NotImplemented
    public void setNoFill(boolean b) {
        this.fill = b;
    }

    @Override
    @NotImplemented
    public void setFillColor(int i, int i1, int i2) {

    }

    @Override
    @NotImplemented
    public void setLineStyleColor(int i, int i1, int i2) {

    }
}
