package be.jonaseveraert.util.files.msWord.objects.paragraphObjects;

import org.apache.poi.util.NotImplemented;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.jetbrains.annotations.Nullable;

/**
 * A break object that can be inserted in a paragraph.
 * TODO: implement
 */
@NotImplemented
public class WordParagraphBreak extends WordParagraphObject {
    private final BreakType breakType;

    @NotImplemented
    public WordParagraphBreak(@Nullable BreakType type) {
        breakType = type;
    }

    @NotImplemented
    public BreakType getBreakType() {
        return breakType;
    }
}
