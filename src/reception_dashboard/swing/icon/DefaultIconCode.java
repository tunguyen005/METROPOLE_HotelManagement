package reception_dashboard.swing.icon;

import reception_dashboard.swing.icon.IconCode;

public class DefaultIconCode implements IconCode {

    private final char unicode;
    private final String fontFamily;

    public DefaultIconCode(String fontFamily, char unicode) {
        this.fontFamily = fontFamily;
        this.unicode = unicode;
    }

    @Override
    public String name() {
        return "[" + getUnicode() + "]";
    }

    @Override
    public char getUnicode() {
        return unicode;
    }

    @Override
    public String getFontFamily() {
        return fontFamily;
    }
}
