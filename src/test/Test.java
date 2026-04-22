package test;

import javax.swing.*;

import gui.CheckInCheckOut;
import gui.DatPhong;
import gui.QLKhachHang;
import gui.QLNhanVien;


public class Test {
	public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            JFrame frame = new JFrame("Test quản lý phòng");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JPanel panel = CheckInCheckOut.createPanel();
            frame.setContentPane(panel);

            frame.setSize(1200, 700);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
