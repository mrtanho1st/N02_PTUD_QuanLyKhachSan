package controller;

import java.awt.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import dao.DichVu_Dao;
import dao.DonDatPhong_Dao;
import dao.Phong_Dao;
import entity.DichVu;
import entity.DonDatPhong;
import entity.NhanVien;
import entity.PhienDangNhap;
import entity.Phong;
import gui.DatPhong;
import gui.DatPhongDialog;
import gui.PhongCardPanel;
import gui.ThongTinDatPhongDialog;

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
        view.getBtnTaiLai().addActionListener(e -> lamMoiLoc());
        ganSuKienTimKiemTuDong();
    }

    private void ganSuKienTimKiemTuDong() {
        DocumentListener docListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                timPhongKhongThongBao();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                timPhongKhongThongBao();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                timPhongKhongThongBao();
            }
        };

        view.getTxtTimMaPhong().getDocument().addDocumentListener(docListener);
        view.getCboLoaiPhong().addActionListener(e -> timPhongKhongThongBao());
        view.getCboTrangThai().addActionListener(e -> timPhongKhongThongBao());
    }

    private void loadDanhSachPhong() {
        List<Phong> rooms = phongDao.findAll();
        view.renderRooms(rooms);
        ganSuKienChoCardPhong();
    }

    private void timPhongKhongThongBao() {
        String tuKhoa = view.getTxtTimMaPhong().getText().trim();
        String loaiPhong = view.getCboLoaiPhong().getSelectedItem().toString();
        String trangThai = view.getCboTrangThai().getSelectedItem().toString();

        List<Phong> rooms = phongDao.searchBaoBieu(tuKhoa, loaiPhong, trangThai);

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

    private void xuLyChonPhong(Phong phong) {
        String trangThai = phong.getTrangThai();

        if ("Trống".equalsIgnoreCase(trangThai)) {
            moDialogDatPhong(phong);
            return;
        }

        if ("Đã đặt".equalsIgnoreCase(trangThai)
                || "Đang sử dụng".equalsIgnoreCase(trangThai)
                || "Đã nhận".equalsIgnoreCase(trangThai)) {

            DonDatPhong donDatPhong = datPhongDao.findRoomDetailByMaPhong(phong.getMaPhong());

            if (donDatPhong == null) {
                JOptionPane.showMessageDialog(view, "Không tìm thấy thông tin đặt phòng của phòng này.");
                return;
            }

            moDialogThongTinDatPhong(donDatPhong);
            return;
        }

        if ("Bảo trì".equalsIgnoreCase(trangThai)) {
            JOptionPane.showMessageDialog(view, "Phòng đang bảo trì, không thể đặt.");
            return;
        }

        JOptionPane.showMessageDialog(view, "Trạng thái phòng không hợp lệ: " + trangThai);
    }

    private void moDialogDatPhong(Phong phong) {
        if (phong == null) {
            JOptionPane.showMessageDialog(view, "Không tìm thấy thông tin phòng.");
            return;
        }

        NhanVien nhanVien = PhienDangNhap.getNhanVienDangNhap();

        if (nhanVien == null) {
            JOptionPane.showMessageDialog(view, "Không xác định được nhân viên đang đăng nhập.");
            return;
        }

        List<DichVu> dsDichVu = dichVuDao.findAll();

        DatPhongDialog dialog = new DatPhongDialog(null, phong, nhanVien, dsDichVu);
        dialog.setLocationRelativeTo(view);
        dialog.setVisible(true);

        if (!dialog.isSucceeded()) {
            return;
        }

        try {
            String tenKH = dialog.getTenKhachHang();
            String cccd = dialog.getCccd();
            String sdt = dialog.getSdt();

            java.util.Date ngayNhan = dialog.getNgayNhan();
            java.util.Date ngayTra = dialog.getNgayTra();

            String tienCocStr = dialog.getTienCoc();
            boolean checkInNgay = dialog.isCheckInNgay();

            double tienCoc = 0;

            if (tienCocStr != null && !tienCocStr.isBlank()) {
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

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(view, "Tiền cọc phải là số hợp lệ.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Có lỗi khi đặt phòng: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void moDialogThongTinDatPhong(DonDatPhong room) {
        ThongTinDatPhongDialog dialog = new ThongTinDatPhongDialog(room);
        dialog.setLocationRelativeTo(view);
        dialog.setVisible(true);

        if (dialog.isUpdated()) {
            String ngayNhanMoi = dialog.getNgayNhan();
            String ngayTraMoi = dialog.getNgayTra();

            capNhatThoiGianDatPhong(room, ngayNhanMoi, ngayTraMoi);
        } else if (dialog.isThemDichVu()) {
            JOptionPane.showMessageDialog(view, "Mở dialog thêm dịch vụ ở đây.");
        }
    }

    private void capNhatThoiGianDatPhong(DonDatPhong room, String ngayNhanStr, String ngayTraStr) {
        if (ngayNhanStr == null || ngayTraStr == null
                || ngayNhanStr.isBlank() || ngayTraStr.isBlank()) {
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
}