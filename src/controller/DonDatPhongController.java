package controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;
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
import gui.DonDatPhongDialog;
import gui.ThongTinDatPhongDialog;

public class DonDatPhongController {

    private static DatPhong view;
    private DonDatPhong_Dao datPhongDao;
    private static Phong_Dao phongDao;
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

        view.getBtnBoChonPhong().addActionListener(e -> view.clearSelectedRooms());

        view.getBtnTaoDonDatPhong().addActionListener(e -> moDialogTaoDonDatPhong());

        view.setOccupiedRoomClickListener(phong -> moDialogThongTinPhongDangCoDon(phong));

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
        view.getDateTuNgay().addActionListener(e -> timPhongKhongThongBao());
        view.getDateDenNgay().addActionListener(e -> timPhongKhongThongBao());
    }
    public static  void reloadData() {
        loadDanhSachPhong();
    }

    private static void loadDanhSachPhong() {
        List<Phong> rooms = phongDao.findAll();
        view.renderRooms(rooms);
    }

    private void timPhongKhongThongBao() {
        String maPhong = view.getTxtTimMaPhong().getText().trim();
        String loaiPhong = view.getCboLoaiPhong().getSelectedItem().toString();
        String trangThai = view.getCboTrangThai().getSelectedItem().toString();

        Date tuNgayUtil = (Date) view.getDateTuNgay().getModel().getValue();
        
        Date denNgayUtil = (Date) view.getDateDenNgay().getModel().getValue();

        List<Phong> rooms;
        
        java.sql.Date tuNgay = null;
        java.sql.Date denNgay = null;

        if (tuNgayUtil != null) {
            tuNgay = new java.sql.Date(tuNgayUtil.getTime());
        }

        if (denNgayUtil != null) {
            denNgay = new java.sql.Date(denNgayUtil.getTime());
        }

        if (tuNgay != null && denNgay != null) {
            if (denNgay.before(tuNgay)) {
                JOptionPane.showMessageDialog(view, "Ngày kết thúc phải sau ngày bắt đầu");
                return;
            }

            rooms = phongDao.search(maPhong,null, null,  loaiPhong, trangThai, tuNgay , denNgay); // ✅ đổi hàm
        } else {
            rooms = phongDao.findAll();
        }

        view.renderRooms(rooms);
    }
    private void lamMoiLoc() {
        view.getTxtTimMaPhong().setText("");
        view.getCboLoaiPhong().setSelectedIndex(0);
        view.getCboTrangThai().setSelectedIndex(0);

        view.clearSelectedRooms();
        loadDanhSachPhong();
    }

    private void moDialogTaoDonDatPhong() {
        if (!view.hasSelectedRooms()) {
            JOptionPane.showMessageDialog(
                    view,
                    "Vui lòng chọn ít nhất một phòng để tạo đơn đặt phòng."
            );
            return;
        }

        List<Phong> dsPhongDaChon = view.getSelectedRooms();

        NhanVien nhanVien = PhienDangNhap.getNhanVienDangNhap();

        if (nhanVien == null) {
            JOptionPane.showMessageDialog(
                    view,
                    "Không xác định được nhân viên đang đăng nhập."
            );
            return;
        }

        List<DichVu> dsDichVu = dichVuDao.findAll();

        DonDatPhongDialog dialog = new DonDatPhongDialog(
                null,
                dsPhongDaChon,
                nhanVien,
                dsDichVu
        );

        dialog.setLocationRelativeTo(view);
        dialog.setVisible(true);

        if (!dialog.isSucceeded()) {
            return;
        }

        try {
            String tienCocStr = dialog.getTienCoc();
            double tienCoc = 0;

            if (tienCocStr != null && !tienCocStr.isBlank()) {
                tienCoc = Double.parseDouble(tienCocStr);
            }

            boolean success = datPhongDao.datPhong(
                    dialog.getDsPhongDaChon(),
                    nhanVien,
                    dialog.getTenKhachHang(),
                    dialog.getCccd(),
                    dialog.getSdt(),
                    dialog.getLoaiKH(),
                    dialog.getDiemSo(),
                    new java.sql.Timestamp(dialog.getNgayNhan().getTime()),
                    new java.sql.Timestamp(dialog.getNgayTra().getTime()),
                    tienCoc,
                    dialog.isCheckInNgay(),
                    dialog.getDichVuDaChon()
            );

            if (success) {
                JOptionPane.showMessageDialog(view, "Tạo đơn đặt phòng thành công.");
                view.clearSelectedRooms();
                loadDanhSachPhong();
            } else {
                JOptionPane.showMessageDialog(view, "Tạo đơn đặt phòng thất bại.");
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(view, "Tiền cọc phải là số hợp lệ.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Có lỗi khi tạo đơn đặt phòng: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void moDialogThongTinPhongDangCoDon(Phong phong) {
        if (phong == null) {
            JOptionPane.showMessageDialog(view, "Không tìm thấy thông tin phòng.");
            return;
        }

        DonDatPhong donDatPhong = datPhongDao.findRoomDetailByMaPhong(phong.getMaPhong());

        if (donDatPhong == null) {
            JOptionPane.showMessageDialog(view, "Không tìm thấy thông tin đặt phòng của phòng này.");
            return;
        }

        ThongTinDatPhongDialog dialog = new ThongTinDatPhongDialog(donDatPhong);
        dialog.setLocationRelativeTo(view);
        dialog.setVisible(true);

        if (dialog.isUpdated()) {
            String ngayNhanMoi = dialog.getNgayNhan();
            String ngayTraMoi = dialog.getNgayTra();

            capNhatThoiGianDatPhong(donDatPhong, ngayNhanMoi, ngayTraMoi);
            return;
        }

        if (dialog.isThemDichVu()) {
            JOptionPane.showMessageDialog(view, "Mở dialog thêm dịch vụ ở đây.");
            return;
        }

        loadDanhSachPhong();
    }

    private void capNhatThoiGianDatPhong(DonDatPhong room, String ngayNhanStr, String ngayTraStr) {
        if (ngayNhanStr == null || ngayTraStr == null
                || ngayNhanStr.isBlank() || ngayTraStr.isBlank()) {
            JOptionPane.showMessageDialog(
                    view,
                    "Ngày nhận và ngày trả không được để trống.",
                    "Thiếu thông tin",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            LocalDateTime ngayNhan = LocalDateTime.parse(ngayNhanStr, formatter);
            LocalDateTime ngayTra = LocalDateTime.parse(ngayTraStr, formatter);

            if (!ngayTra.isAfter(ngayNhan)) {
                JOptionPane.showMessageDialog(
                        view,
                        "Ngày trả phải sau ngày nhận.",
                        "Lỗi thời gian",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            JOptionPane.showMessageDialog(
                    view,
                    "Cập nhật thời gian đặt phòng thành công cho phòng " + room.getMaPhong(),
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE
            );

            loadDanhSachPhong();

        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(
                    view,
                    "Ngày giờ phải đúng định dạng dd/MM/yyyy HH:mm",
                    "Lỗi định dạng",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    view,
                    "Có lỗi khi cập nhật thời gian: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}