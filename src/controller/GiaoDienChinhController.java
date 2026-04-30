package controller;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import gui.GiaoDienChinh;
import gui.TaiKhoanDialog;
import gui.ThongTin;

public class GiaoDienChinhController {

    private final GiaoDienChinh view;

    public GiaoDienChinhController(GiaoDienChinh view) {
        this.view = view;
        registerEvents();
    }

    private void registerEvents() {
        view.getBtnTrangChu().addActionListener(e -> view.showHomePage());

        view.getBtnThongTin().addActionListener(e -> moGiaoDienThongTin());

        view.getBtnTaiKhoan().addActionListener(e -> {
            TaiKhoanDialog dialog = new TaiKhoanDialog();
            dialog.setLocationRelativeTo(view);
            dialog.setVisible(true);
        });

        view.getBtnTroGiup().addActionListener(e -> view.showInfoMessage("Liên hệ quản trị viên để được hỗ trợ."));

        view.getBtnDangXuat().addActionListener(e -> {
            if (view.confirmLogout()) {
                view.logout();
            }
        });
    }

    private void moGiaoDienThongTin() {
        JFrame frame = new JFrame("Thông tin & Thông báo");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setContentPane(ThongTin.createPanel());
        frame.setPreferredSize(new Dimension(900, 600));
        frame.pack();
        frame.setLocationRelativeTo(view);
        frame.setVisible(true);
    }
}