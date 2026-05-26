package controller;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import entity.PhienDangNhap;
import entity.TaiKhoan;
import gui.GiaoDienChinh;
import gui.TaiKhoanDialog;
import gui.ThongTin;

public class GiaoDienChinhController {

    private final GiaoDienChinh view;

    public GiaoDienChinhController(GiaoDienChinh view) {
        this.view = view;
        registerEvents();
        phanQuyenTheoNhanVien();
    }

    private void phanQuyenTheoNhanVien() {
        TaiKhoan tk = PhienDangNhap.getTaiKhoanDangNhap();
        if (tk == null)
            return;

        String vaiTro = tk.getVaiTro();

        // 👉 LỄ TÂN
        if (vaiTro.equalsIgnoreCase("LeTan")) {

            // làm mờ menu lớn
            view.getBtnHeThong().setEnabled(false);
            view.getBtnBaoBieu().setEnabled(false);
            view.getBtnThongKe().setEnabled(false);

            // thêm tooltip
            view.getBtnHeThong().setToolTipText("Không có quyền");
            view.getBtnBaoBieu().setToolTipText("Không có quyền");
            view.getBtnThongKe().setToolTipText("Không có quyền");

            // Gợi ý: Lễ tân không có quyền truy cập một số mục quản lý
            view.getBtnDanhMuc().setToolTipText("Không có quyền truy cập: Nhân viên");
            view.getBtnCapNhat().setToolTipText("Không có quyền truy cập: Nhân viên/Thuế/Khuyến mãi");
        }

    }

    public static boolean coQuyen(String mainMenu, String subMenu) {
        TaiKhoan tk = PhienDangNhap.getTaiKhoanDangNhap();
        if (tk == null)
            return false;

        String vaiTro = tk.getVaiTro();

        if (vaiTro.equalsIgnoreCase("LeTan")) {
            // Không cho Lễ tân truy cập các menu quản trị tổng quát
            if ("Hệ thống".equalsIgnoreCase(mainMenu))
                return false;
            if ("Báo biểu".equalsIgnoreCase(mainMenu))
                return false;
            if ("Thống kê".equalsIgnoreCase(mainMenu))
                return false;

            // Chặn mọi truy cập tới các chức năng nhạy cảm dù ở menu nào
            if (subMenu != null) {
                if ("Nhân viên".equalsIgnoreCase(subMenu))
                    return false;
                if ("Khuyến mãi".equalsIgnoreCase(subMenu))
                    return false;
                if ("Thuế".equalsIgnoreCase(subMenu))
                    return false;
            }
        }

        return true;
    }

    private void registerEvents() {
        view.getBtnTrangChu().addActionListener(e -> {
            view.showHomePage();
            DonDatPhongController.reloadData();
        });

        view.getBtnThongTin().addActionListener(e -> moGiaoDienThongTin());

        view.getBtnTaiKhoan().addActionListener(e -> {
            TaiKhoanDialog dialog = new TaiKhoanDialog();
            dialog.setLocationRelativeTo(view);
            dialog.setVisible(true);
        });

        view.getBtnTroGiup().addActionListener(e -> view.showInfoMessage(
                "Mọi thắc mắc vui lòng liên hệ quản trị viên để được hỗ trợ.\nEmail: tan2005tg@gmail.com\nSdt: 0349099412"));

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
