package controller;

import java.awt.Component;
import java.awt.GridLayout;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import dao.DichVu_Dao;
import dao.DonDatPhong_Dao;
import dao.Phong_Dao;
import entity.DonDatPhong;
import gui.DangNhap;
import gui.DatPhong;
import gui.DatPhongDialog;
import gui.PhongCardPanel;
import gui.ThongTinDatPhongDialog;

import java.util.ArrayList;

import entity.DichVu;
import entity.NhanVien;
import entity.PhienDangNhap;
import entity.Phong;



public class DonDatPhongController {

    private DatPhong view;
    private DonDatPhong_Dao datPhongDao;
    
    private Phong_Dao phongDao;
    private DichVu_Dao dichVuDao;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public DonDatPhongController(DatPhong view) {
        this.view = view;
        this.datPhongDao = new DonDatPhong_Dao();
        this.phongDao = new Phong_Dao();
        this.dichVuDao = new DichVu_Dao();

        initController();
        loadDanhSachPhong();
    }

    private void initController() {
        view.getBtnTim().addActionListener(e -> timPhong());
        view.getBtnTaiLai().addActionListener(e -> lamMoiLoc());
    }

    private void loadDanhSachPhong() {
        List<DonDatPhong> rooms = datPhongDao.findAllRoomViews();
        view.renderRooms(rooms);
        ganSuKienChoCardPhong();
    }

    private void timPhong() {
        String maPhong = view.getTxtTimMaPhong().getText().trim();
        String loaiPhong = view.getCboLoaiPhong().getSelectedItem().toString();
        String trangThai = view.getCboTrangThai().getSelectedItem().toString();

        List<DonDatPhong> rooms = datPhongDao.searchRoomViews(maPhong, loaiPhong, trangThai);
        view.renderRooms(rooms);
        ganSuKienChoCardPhong();
    }

    private void lamMoiLoc() {
        view.getTxtTimMaPhong().setText("");
        view.getCboLoaiPhong().setSelectedIndex(0);
        view.getCboTrangThai().setSelectedIndex(0);
        loadDanhSachPhong();
    }

    private void ganSuKienChoCardPhong() {
        duyetPanel(view.getPnlDanhSachPhong());
    }

    private void duyetPanel(JPanel panel) {
        for (Component comp : panel.getComponents()) {
            if (comp instanceof PhongCardPanel) {
                PhongCardPanel card = (PhongCardPanel) comp;

                // tránh gắn listener trùng nhiều lần
                for (var ml : card.getMouseListeners()) {
                    card.removeMouseListener(ml);
                }

                card.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        xuLyChonPhong(card.getData());
                    }
                });
            } else if (comp instanceof JPanel) {
                duyetPanel((JPanel) comp);
            }
        }
    }

    private void xuLyChonPhong(DonDatPhong room) {
        String trangThai = room.getTrangThaiPhong();

        if ("Trống".equalsIgnoreCase(trangThai)) {
            // Lấy thông tin phòng thật từ DB
            Phong phong = phongDao.findById(room.getMaPhong());

            if (phong == null) {
                JOptionPane.showMessageDialog(view, "Không tìm thấy thông tin phòng.");
                return;
            }

            // Lấy nhân viên đang đăng nhập
            // Bạn tự thay bằng biến lưu ở màn hình đăng nhập
            NhanVien nhanVien = PhienDangNhap.getNhanVienDangNhap();

            if (nhanVien == null) {
                JOptionPane.showMessageDialog(view, "Không xác định được nhân viên đang đăng nhập.");
                return;
            }

            // Lấy danh sách dịch vụ từ DB
            List<DichVu> dsDichVu = dichVuDao.findAll();

            DatPhongDialog dialog = new DatPhongDialog(null, phong, nhanVien, dsDichVu);
            dialog.setLocationRelativeTo(view);
            dialog.setVisible(true);

            if (dialog.isSucceeded()) {
                String tenKH = dialog.getTenKhachHang();
                String cccd = dialog.getCccd();
                String sdt = dialog.getSdt();
                java.util.Date ngayNhan = dialog.getNgayNhan();
                java.util.Date ngayTra = dialog.getNgayTra();
                String tienCocStr = dialog.getTienCoc();
                boolean checkInNgay = dialog.isCheckInNgay();

                double tienCoc = 0;
                if (!tienCocStr.isBlank()) {
                    tienCoc = Double.parseDouble(tienCocStr);
                }

                List<DatPhongDialog.DichVuDatTruoc> dsDichVuDaChon = dialog.getDichVuDaChon();

                boolean success = datPhongDao.datPhong(
                        phong,
                        nhanVien,
                        tenKH,
                        cccd,
                        sdt,
                        new java.sql.Timestamp(ngayNhan.getTime()),
                        new java.sql.Timestamp(ngayTra.getTime()),
                        tienCoc,
                        checkInNgay,
                        dsDichVuDaChon
                );

                if (success) {
                    JOptionPane.showMessageDialog(view, "Đặt phòng thành công.");
                    loadDanhSachPhong();
                } else {
                    JOptionPane.showMessageDialog(view, "Đặt phòng thất bại.");
                }
            }

        } else if ("Đã đặt".equalsIgnoreCase(trangThai) || "Đang sử dụng".equalsIgnoreCase(trangThai)) {
            ThongTinDatPhongDialog dialog = new ThongTinDatPhongDialog(room);
            dialog.setLocationRelativeTo(view);
            dialog.setVisible(true);

            if (dialog.isUpdated()) {
                String ngayNhanMoi = dialog.getNgayNhan();
                String ngayTraMoi = dialog.getNgayTra();

                JOptionPane.showMessageDialog(view, "Cập nhật thời gian thành công.");
                loadDanhSachPhong();
            } else if (dialog.isThemDichVu()) {
                JOptionPane.showMessageDialog(view, "Mở dialog thêm dịch vụ ở đây.");
            }
        }
    }

//    private void hienThiDialogDatPhong(DonDatPhong room) {
//        JTextField txtMaKH = new JTextField();
//        JTextField txtTenKH = new JTextField();
//        JTextField txtNgayNhan = new JTextField(LocalDateTime.now().format(formatter));
//        JTextField txtNgayTra = new JTextField(LocalDateTime.now().plusHours(2).format(formatter));
//        JTextField txtTienCoc = new JTextField();
//        JCheckBox chkCheckIn = new JCheckBox("Check-in luôn");
//
//        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
//        panel.add(new JLabel("Mã phòng:"));
//        panel.add(new JLabel(room.getMaPhong()));
//        panel.add(new JLabel("Mã khách hàng:"));
//        panel.add(txtMaKH);
//        panel.add(new JLabel("Tên khách hàng:"));
//        panel.add(txtTenKH);
//        panel.add(new JLabel("Ngày giờ nhận (dd/MM/yyyy HH:mm):"));
//        panel.add(txtNgayNhan);
//        panel.add(new JLabel("Ngày giờ trả (dd/MM/yyyy HH:mm):"));
//        panel.add(txtNgayTra);
//        panel.add(new JLabel("Tiền cọc:"));
//        panel.add(txtTienCoc);
//        panel.add(new JLabel(""));
//        panel.add(chkCheckIn);
//
//        int result = JOptionPane.showConfirmDialog(
//                view,
//                panel,
//                "Đặt phòng - " + room.getMaPhong(),
//                JOptionPane.OK_CANCEL_OPTION,
//                JOptionPane.PLAIN_MESSAGE
//        );
//
//        if (result != JOptionPane.OK_OPTION) {
//            return;
//        }
//
//        String maKH = txtMaKH.getText().trim();
//        String tenKH = txtTenKH.getText().trim();
//        String ngayNhanStr = txtNgayNhan.getText().trim();
//        String ngayTraStr = txtNgayTra.getText().trim();
//        String tienCocStr = txtTienCoc.getText().trim();
//        boolean checkInNgay = chkCheckIn.isSelected();
//
//        if (maKH.isEmpty() || tenKH.isEmpty() || ngayNhanStr.isEmpty() || ngayTraStr.isEmpty()) {
//            JOptionPane.showMessageDialog(view,
//                    "Vui lòng nhập đầy đủ thông tin khách hàng và thời gian đặt phòng.",
//                    "Thiếu thông tin",
//                    JOptionPane.WARNING_MESSAGE);
//            return;
//        }
//
//        try {
//            LocalDateTime ngayNhan = LocalDateTime.parse(ngayNhanStr, formatter);
//            LocalDateTime ngayTra = LocalDateTime.parse(ngayTraStr, formatter);
//
//            if (!ngayTra.isAfter(ngayNhan)) {
//                JOptionPane.showMessageDialog(view,
//                        "Ngày giờ trả phải lớn hơn ngày giờ nhận.",
//                        "Lỗi thời gian",
//                        JOptionPane.WARNING_MESSAGE);
//                return;
//            }
//
//            Double tienCoc = null;
//            if (!tienCocStr.isEmpty()) {
//                tienCoc = Double.parseDouble(tienCocStr);
//            }
//
//            /*
//             * TODO:
//             * Gọi DAO thật của bạn tại đây, ví dụ:
//             *
//             * boolean success = datPhongDao.datPhong(
//             *      room.getMaPhong(),
//             *      maKH,
//             *      tenKH,
//             *      ngayNhan,
//             *      ngayTra,
//             *      tienCoc,
//             *      checkInNgay
//             * );
//             */
//
//            JOptionPane.showMessageDialog(view,
//                    "Đặt phòng thành công cho phòng " + room.getMaPhong()
//                            + (checkInNgay ? "\nKhách đã được check-in." : ""),
//                    "Thành công",
//                    JOptionPane.INFORMATION_MESSAGE);
//
//            loadDanhSachPhong();
//
//        } catch (DateTimeParseException ex) {
//            JOptionPane.showMessageDialog(view,
//                    "Ngày giờ không đúng định dạng dd/MM/yyyy HH:mm",
//                    "Lỗi định dạng",
//                    JOptionPane.ERROR_MESSAGE);
//        } catch (NumberFormatException ex) {
//            JOptionPane.showMessageDialog(view,
//                    "Tiền cọc phải là số hợp lệ.",
//                    "Lỗi dữ liệu",
//                    JOptionPane.ERROR_MESSAGE);
//        } catch (Exception ex) {
//            JOptionPane.showMessageDialog(view,
//                    "Có lỗi khi đặt phòng: " + ex.getMessage(),
//                    "Lỗi",
//                    JOptionPane.ERROR_MESSAGE);
//        }
//    }

//    private void hienThiDialogThongTinDatPhong(DonDatPhong room) {
//        JTextField txtNgayNhan = new JTextField(nullToEmpty(room.getNgayNhan()));
//        JTextField txtNgayTra = new JTextField(nullToEmpty(room.getNgayTra()));
//
//        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 8, 8));
//        infoPanel.add(new JLabel("Mã phòng:"));
//        infoPanel.add(new JLabel(nullToEmpty(room.getMaPhong())));
//        infoPanel.add(new JLabel("Loại phòng:"));
//        infoPanel.add(new JLabel(nullToEmpty(room.getLoaiPhong())));
//        infoPanel.add(new JLabel("Trạng thái:"));
//        infoPanel.add(new JLabel(nullToEmpty(room.getTrangThaiPhong())));
//        infoPanel.add(new JLabel("Mã ĐĐP:"));
//        infoPanel.add(new JLabel(nullToEmpty(room.getMaDDP())));
//        infoPanel.add(new JLabel("Mã KH:"));
//        infoPanel.add(new JLabel(nullToEmpty(room.getMaKH())));
//        infoPanel.add(new JLabel("Tên KH:"));
//        infoPanel.add(new JLabel(nullToEmpty(room.getTenKH())));
//        infoPanel.add(new JLabel("Ngày nhận:"));
//        infoPanel.add(txtNgayNhan);
//        infoPanel.add(new JLabel("Ngày trả:"));
//        infoPanel.add(txtNgayTra);
//        infoPanel.add(new JLabel("Tiền cọc:"));
//        infoPanel.add(new JLabel(room.getTienCoc() == null ? "" : String.valueOf(room.getTienCoc())));
//
//        Object[] options = { "Cập nhật thời gian", "Thêm dịch vụ", "Đóng" };
//
//        int choice = JOptionPane.showOptionDialog(
//                view,
//                infoPanel,
//                "Thông tin đặt phòng - " + room.getMaPhong(),
//                JOptionPane.DEFAULT_OPTION,
//                JOptionPane.INFORMATION_MESSAGE,
//                null,
//                options,
//                options[0]
//        );
//
//        if (choice == 0) {
//            capNhatThoiGianDatPhong(room, txtNgayNhan.getText().trim(), txtNgayTra.getText().trim());
//        } else if (choice == 1) {
//            themDichVuChoPhong(room);
//        }
//    }

    private void capNhatThoiGianDatPhong(DonDatPhong room, String ngayNhanStr, String ngayTraStr) {
        if (ngayNhanStr.isEmpty() || ngayTraStr.isEmpty()) {
            JOptionPane.showMessageDialog(view,
                    "Ngày nhận và ngày trả không được để trống.",
                    "Thiếu thông tin",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            LocalDateTime ngayNhan = LocalDateTime.parse(ngayNhanStr, formatter);
            LocalDateTime ngayTra = LocalDateTime.parse(ngayTraStr, formatter);

            if (!ngayTra.isAfter(ngayNhan)) {
                JOptionPane.showMessageDialog(view,
                        "Ngày trả phải sau ngày nhận.",
                        "Lỗi thời gian",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            /*
             * TODO:
             * boolean success = datPhongDao.capNhatThoiGianDatPhong(
             *      room.getMaDDP(),
             *      ngayNhan,
             *      ngayTra
             * );
             */

            JOptionPane.showMessageDialog(view,
                    "Cập nhật thời gian đặt phòng thành công cho phòng " + room.getMaPhong(),
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);

            loadDanhSachPhong();

        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(view,
                    "Ngày giờ phải đúng định dạng dd/MM/yyyy HH:mm",
                    "Lỗi định dạng",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view,
                    "Có lỗi khi cập nhật thời gian: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void themDichVuChoPhong(DonDatPhong room) {
        JTextField txtTenDV = new JTextField();
        JTextField txtSoLuong = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        panel.add(new JLabel("Mã phòng:"));
        panel.add(new JLabel(room.getMaPhong()));
        panel.add(new JLabel("Tên dịch vụ:"));
        panel.add(txtTenDV);
        panel.add(new JLabel("Số lượng:"));
        panel.add(txtSoLuong);

        int result = JOptionPane.showConfirmDialog(
                view,
                panel,
                "Thêm dịch vụ - " + room.getMaPhong(),
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String tenDV = txtTenDV.getText().trim();
        String soLuongStr = txtSoLuong.getText().trim();

        if (tenDV.isEmpty() || soLuongStr.isEmpty()) {
            JOptionPane.showMessageDialog(view,
                    "Vui lòng nhập đầy đủ thông tin dịch vụ.",
                    "Thiếu thông tin",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int soLuong = Integer.parseInt(soLuongStr);

            if (soLuong <= 0) {
                JOptionPane.showMessageDialog(view,
                        "Số lượng phải lớn hơn 0.",
                        "Lỗi dữ liệu",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            /*
             * TODO:
             * boolean success = datPhongDao.themDichVuChoPhong(
             *      room.getMaDDP(),
             *      tenDV,
             *      soLuong
             * );
             */

            JOptionPane.showMessageDialog(view,
                    "Đã thêm dịch vụ cho phòng " + room.getMaPhong(),
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(view,
                    "Số lượng phải là số nguyên hợp lệ.",
                    "Lỗi dữ liệu",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view,
                    "Có lỗi khi thêm dịch vụ: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}