package admin_dashboard.swing.scrollbar;

import admin_dashboard.swing.scrollbar.ModernScrollBarUI;

import javax.swing.*;
import java.awt.*;

public class ScrollBarCustom extends JScrollBar {

    public ScrollBarCustom() {
        setUI(new ModernScrollBarUI());
        setPreferredSize(new Dimension(5, 5));
        setForeground(new Color(0,0,0));
        setUnitIncrement(20);
        setOpaque(false);
    }
}
