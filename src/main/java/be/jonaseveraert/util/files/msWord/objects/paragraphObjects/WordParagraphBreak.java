package be.jonaseveraert.util.files.msWord.objects.paragraphObjects;

import com.sun.istack.internal.Nullable;
import org.apache.poi.xwpf.usermodel.BreakType;

/**
 * A break object that can be inserted in a paragraph.
 * TODO: implement
 */
public class WordParagraphBreak extends WordParagraphObject {
    private final BreakType breakType;

    public WordParagraphBreak(@Nullable BreakType type) {
        breakType = type;
    }

    public BreakType getBreakType() {
        return breakType;
    }
}
