package gui;

import javax.swing.SwingUtilities;

import controller.DangNhapController;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DangNhap.applySystemLookAndFeel();

            DangNhap view = new DangNhap();
            new DangNhapController(view);

            view.setVisible(true);
        });
    }
}