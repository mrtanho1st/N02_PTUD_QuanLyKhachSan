package controller;

import gui.GiaoDienChinh;

public class GiaoDienChinhController {

    private final GiaoDienChinh view;

    public GiaoDienChinhController(GiaoDienChinh view) {
        this.view = view;
        registerEvents();
    }

    private void registerEvents() {
        view.getBtnTrangChu().addActionListener(e -> view.showHomePage());

        view.getBtnThongTin().addActionListener(e ->
                view.showInfoMessage("Chức năng Thông tin đang phát triển."));

        view.getBtnTaiKhoan().addActionListener(e ->
                view.openPage("Cập nhật", "Tài khoản"));

        view.getBtnTroGiup().addActionListener(e ->
                view.showInfoMessage("Liên hệ quản trị viên để được hỗ trợ."));

        view.getBtnDangXuat().addActionListener(e -> {
            if (view.confirmLogout()) {
                view.logout();
            }
        });
    }
}