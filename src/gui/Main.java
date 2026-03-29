package gui;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    private static void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Keep default look and feel when system look and feel is unavailable.
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            setSystemLookAndFeel();
            new GiaoDienChinh().setVisible(true);
        });
    }
}
