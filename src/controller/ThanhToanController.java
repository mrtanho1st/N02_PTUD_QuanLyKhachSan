package controller;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import dao.KhuyenMai_Dao;
import dao.ThanhToan_Dao;
import entity.KhuyenMai;
import gui.ThanhToan;
import gui.ThanhToanDialog;

import dao.HoaDon_Dao;
import gui.HoaDonDialog;
import dao.Thue_Dao;
import entity.Thue;
import entity.NhanVien;
import entity.PhienDangNhap;

import javax.swing.ImageIcon;
import java.awt.Image;
import java.net.URL;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class ThanhToanController {

    private final ThanhToan view;
    private final ThanhToan_Dao dao;
    private final KhuyenMai_Dao khuyenMaiDao;
    private final HoaDon_Dao hoaDonDao;
    private final Thue_Dao thueDao;

    private final NumberFormat moneyFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));

    private String maDDPDangChon = "";

    private double tienPhong = 0;
    private double tienDichVu = 0;
    private double tienCoc = 0;
    private double giamGia = 0;
    private double tienThue = 0;
    private double tongTien = 0;
    private double canThanhToan = 0;

    private ThanhToanDialog dialog;

    private Object[] donDangChon;
    private List<Object[]> dsPhongDangChon;
    private List<Object[]> dsDichVuDangChon;
    
    private static final String BANK_ID = "970428"; // NamABank
    private static final String ACCOUNT_NO = "0787702900";
    private static final String ACCOUNT_NAME = "NGUYEN THI THANH THU";

    public ThanhToanController(ThanhToan view) {
        this.view = view;
        this.dao = new ThanhToan_Dao();
        this.khuyenMaiDao = new KhuyenMai_Dao();
        this.hoaDonDao = new HoaDon_Dao();
        this.thueDao = new Thue_Dao();

        addEvents();
    }

    private void addEvents() {
        view.getBtnLamMoi().addActionListener(e -> {
            view.clearSearch();
            maDDPDangChon = "";
            donDangChon = null;
            dsPhongDangChon = null;
            dsDichVuDangChon = null;
            loadDanhSachDonDaNhan();
        });

        view.getBtnThanhToan().addActionListener(e -> moDialogThanhToan());

        view.getTblDonChoThanhToan().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = view.getTblDonChoThanhToan().getSelectedRow();

                if (row >= 0) {
                    Object value = view.getTblDonChoThanhToan().getValueAt(row, 0);
                    String maDDP = value == null ? "" : value.toString();

                    if (!maDDP.isBlank()) {
                        loadTheoMaDDP(maDDP);
                    }
                }
            }
        });

        DocumentListener searchListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                timKiemDonChoThanhToan();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                timKiemDonChoThanhToan();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                timKiemDonChoThanhToan();
            }
        };

        view.getTxtMaDDP().getDocument().addDocumentListener(searchListener);
        view.getTxtCccdSdt().getDocument().addDocumentListener(searchListener);
    }

    public void loadDanhSachDonDaNhan() {
        List<Object[]> list = dao.findDonChoThanhToan();
        fillTableDonChoThanhToan(list);

        if (!list.isEmpty()) {
            view.getTblDonChoThanhToan().setRowSelectionInterval(0, 0);
        } else {
            maDDPDangChon = "";
            donDangChon = null;
            view.clearDetail();
        }
    }

    private void timKiemDonChoThanhToan() {
        String maDDP = view.getTxtMaDDP().getText().trim();
        String cccdSdt = view.getTxtCccdSdt().getText().trim();

        List<Object[]> list = dao.searchDonChoThanhToan(maDDP, cccdSdt);
        fillTableDonChoThanhToan(list);

        if (!list.isEmpty()) {
            view.getTblDonChoThanhToan().setRowSelectionInterval(0, 0);
        } else {
            maDDPDangChon = "";
            donDangChon = null;
            view.clearDetail();
        }
    }

    private void fillTableDonChoThanhToan(List<Object[]> list) {
        DefaultTableModel model = view.getModelDonChoThanhToan();
        model.setRowCount(0);

        for (Object[] row : list) {
            double tienCocValue = toDouble(row[6]);

            model.addRow(new Object[] {
                    row[0],
                    row[1],
                    row[2],
                    row[3],
                    row[4],
                    row[5],
                    formatMoney(tienCocValue)
            });
        }
    }

    public void loadTheoMaDDP(String maDDP) {
        if (maDDP == null || maDDP.isBlank()) {
            return;
        }

        Object[] don = dao.findDonThanhToanByMaDDP(maDDP);

        if (don == null) {
            JOptionPane.showMessageDialog(view, "Không tìm thấy đơn đang chờ thanh toán.");
            return;
        }

        maDDPDangChon = maDDP;
        donDangChon = don;

        tienCoc = toDouble(don[6]);

        loadPhong(maDDP);
        loadDichVu(maDDP);
        tinhTien();
    }

    private void loadPhong(String maDDP) {
        tienPhong = 0;
        dsPhongDangChon = dao.findPhongByMaDDP(maDDP);

        for (Object[] row : dsPhongDangChon) {
            int soNgay = toInt(row[2]);
            double donGia = toDouble(row[3]);

            double thanhTien;

            if (row.length >= 5) {
                thanhTien = toDouble(row[4]);
            } else {
                thanhTien = soNgay * donGia;
            }

            tienPhong += thanhTien;
        }
    }

    private void loadDichVu(String maDDP) {
        tienDichVu = 0;
        dsDichVuDangChon = dao.findDichVuByMaDDP(maDDP);

        for (Object[] row : dsDichVuDangChon) {
            double thanhTien;

            if (row.length >= 5) {
                thanhTien = toDouble(row[4]);
            } else {
                thanhTien = toDouble(row[3]);
            }

            tienDichVu += thanhTien;
        }
    }

    private void tinhTien() {
        tongTien = tienPhong + tienDichVu;
        giamGia = 0;
        canThanhToan = tongTien - tienCoc;

        if (canThanhToan < 0) {
            canThanhToan = 0;
        }
    }

    private void moDialogThanhToan() {
        if (maDDPDangChon.isEmpty() || donDangChon == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn đơn cần thanh toán.");
            return;
        }

        dialog = new ThanhToanDialog();

        loadMaGiamGiaLenDialog();
        loadThueLenDialog();

        dialog.getBtnDong().addActionListener(e -> {
            dialog.dispose();
            dialog = null;
        });

        dialog.getBtnThanhToan().addActionListener(e -> xuLyThanhToan());


        dialog.getTxtKhachDua().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                capNhatTienThua();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                capNhatTienThua();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                capNhatTienThua();
            }
        });

        dialog.getRdoTienMat().addActionListener(e -> {
            capNhatGiaoDienPhuongThuc();
            capNhatTienThua();
        });

        dialog.getRdoChuyenKhoan().addActionListener(e -> {
            capNhatGiaoDienPhuongThuc();
            capNhatTienThua();
        });

        dialog.getCboMaGiamGia().addActionListener(e -> {
            tinhTienTheoMaGiamGiaVaThue();
            hienThiTongTienLenDialog();
            capNhatTienThua();
            if ("Chuyển khoản".equalsIgnoreCase(dialog.getPhuongThucThanhToan())) {
                capNhatGiaoDienPhuongThuc();
            }
        });
        dialog.getCboThue().addActionListener(e -> {
            tinhTienTheoMaGiamGiaVaThue();
            hienThiTongTienLenDialog();
            capNhatTienThua();

            if ("Chuyển khoản".equalsIgnoreCase(dialog.getPhuongThucThanhToan())) {
                capNhatGiaoDienPhuongThuc();
            }
        });
        

        hienThiThongTinLenDialog();
        capNhatGiaoDienPhuongThuc();

        dialog.setLocationRelativeTo(view);
        dialog.setVisible(true);
    }
    private void loadThueLenDialog() {
        if (dialog == null) {
            return;
        }

        dialog.getCboThue().removeAllItems();

        dialog.getCboThue().addItem(
                new Thue("", "Không áp dụng", "Áp dụng", 0, "Không tính thuế")
        );

        List<Thue> list = thueDao.findThueDangApDung();

        for (Thue thue : list) {
            dialog.getCboThue().addItem(thue);
        }
    }

    private void loadMaGiamGiaLenDialog() {
        if (dialog == null) {
            return;
        }

        dialog.getCboMaGiamGia().removeAllItems();

        dialog.getCboMaGiamGia().addItem(
                new KhuyenMai("", "Không áp dụng", 0, null, null)
        );

        List<KhuyenMai> list = khuyenMaiDao.findKhuyenMaiConHieuLuc();

        for (KhuyenMai km : list) {
            dialog.getCboMaGiamGia().addItem(km);
        }
    }

    private void hienThiThongTinLenDialog() {
        if (dialog == null || donDangChon == null) {
            return;
        }

        dialog.getLblMaDDP().setText(toText(donDangChon[0]));
        dialog.getLblTenKH().setText(toText(donDangChon[1]));
        dialog.getLblCCCD().setText(toText(donDangChon[2]));
        dialog.getLblSDT().setText(toText(donDangChon[3]));
        dialog.getLblNgayNhan().setText(toText(donDangChon[4]));
        dialog.getLblNgayTra().setText(toText(donDangChon[5]));

        fillPhongDialog();
        fillDichVuDialog();

        tinhTienTheoMaGiamGiaVaThue();
        hienThiTongTienLenDialog();
        capNhatTienThua();
    }

    private void fillPhongDialog() {
        DefaultTableModel model = dialog.getModelPhong();
        model.setRowCount(0);

        if (dsPhongDangChon == null) {
            return;
        }

        for (Object[] row : dsPhongDangChon) {
            String maPhong = toText(row[0]);
            String loaiPhong = toText(row[1]);
            int soNgay = toInt(row[2]);
            double donGia = toDouble(row[3]);

            double thanhTien;

            if (row.length >= 5) {
                thanhTien = toDouble(row[4]);
            } else {
                thanhTien = soNgay * donGia;
            }

            model.addRow(new Object[] {
                    maPhong,
                    loaiPhong,
                    soNgay,
                    formatMoney(donGia) + " VNĐ",
                    formatMoney(thanhTien) + " VNĐ"
            });
        }
    }

    private void fillDichVuDialog() {
        DefaultTableModel model = dialog.getModelDichVu();
        model.setRowCount(0);

        if (dsDichVuDangChon == null) {
            return;
        }

        for (Object[] row : dsDichVuDangChon) {
            String maDV = toText(row[0]);
            String tenDV = toText(row[1]);
            int soLuong = toInt(row[2]);

            double donGia;
            double thanhTien;

            if (row.length >= 5) {
                donGia = toDouble(row[3]);
                thanhTien = toDouble(row[4]);
            } else {
                donGia = 0;
                thanhTien = toDouble(row[3]);
            }

            model.addRow(new Object[] {
                    maDV,
                    tenDV,
                    soLuong,
                    donGia <= 0 ? "" : formatMoney(donGia) + " VNĐ",
                    formatMoney(thanhTien) + " VNĐ"
            });
        }
    }

    private void tinhTienTheoMaGiamGiaVaThue() {
        giamGia = 0;
        tienThue = 0;

        Object selectedKM = null;

        if (dialog != null) {
            selectedKM = dialog.getCboMaGiamGia().getSelectedItem();
        }

        if (selectedKM instanceof KhuyenMai) {
            KhuyenMai km = (KhuyenMai) selectedKM;

            if (km.getMaKM() != null && !km.getMaKM().isBlank()) {
                giamGia = tongTien * km.getGiaTri() / 100.0;
            }
        }

        double tienSauGiam = tongTien - giamGia;

        if (tienSauGiam < 0) {
            tienSauGiam = 0;
        }

        Object selectedThue = null;

        if (dialog != null) {
            selectedThue = dialog.getCboThue().getSelectedItem();
        }

        if (selectedThue instanceof Thue) {
            Thue thue = (Thue) selectedThue;

            if (thue.getMaThue() != null && !thue.getMaThue().isBlank()) {
                tienThue = tienSauGiam * thue.getTyLeThue() / 100.0;
            }
        }

        canThanhToan = tongTien - tienCoc - giamGia + tienThue;

        if (canThanhToan < 0) {
            canThanhToan = 0;
        }
    }

    private void hienThiTongTienLenDialog() {
        if (dialog == null) {
            return;
        }

        dialog.getLblTienPhong().setText(formatMoney(tienPhong) + " VNĐ");
        dialog.getLblTienDichVu().setText(formatMoney(tienDichVu) + " VNĐ");
        dialog.getLblTongTien().setText(formatMoney(tongTien) + " VNĐ");
        dialog.getLblTienCoc().setText(formatMoney(tienCoc) + " VNĐ");
        dialog.getLblGiamGia().setText(formatMoney(giamGia) + " VNĐ");
        dialog.getLblTienThue().setText(formatMoney(tienThue) + " VNĐ");
        dialog.getLblCanThanhToan().setText(formatMoney(canThanhToan) + " VNĐ");
    }

    private void capNhatGiaoDienPhuongThuc() {
        if (dialog == null) {
            return;
        }

        String phuongThuc = dialog.getPhuongThucThanhToan();

        if ("Tiền mặt".equalsIgnoreCase(phuongThuc)) {
            dialog.hienThiTienMat();
            dialog.getLblTienThua().setText("");
        } else {
            dialog.hienThiChuyenKhoan();

            dialog.getLblThongTinChuyenKhoan().setText(
                    "<html>"
                            + "Ngân hàng: Nam A Bank<br>"
                            + "Số TK: " + ACCOUNT_NO + "<br>"
                            + "Chủ TK: " + ACCOUNT_NAME + "<br>"
                            + "Số tiền: " + formatMoney(canThanhToan) + " VNĐ<br>"
                            + "Nội dung: Thanh toán đơn " + maDDPDangChon
                            + "</html>"
            );

            capNhatQrChuyenKhoan();
        }
    }

    private void capNhatTienThua() {
        if (dialog == null) {
            return;
        }

        String phuongThuc = dialog.getPhuongThucThanhToan();

        if (!"Tiền mặt".equalsIgnoreCase(phuongThuc)) {
            dialog.getLblTienThua().setText("0 VNĐ");
            return;
        }

        String khachDuaText = dialog.getTxtKhachDua().getText().trim();

        if (khachDuaText.isEmpty()) {
            dialog.getLblTienThua().setText("");
            return;
        }

        try {
            double khachDua = Double.parseDouble(khachDuaText);
            double tienThua = khachDua - canThanhToan;

            if (tienThua < 0) {
                dialog.getLblTienThua().setText("Thiếu " + formatMoney(Math.abs(tienThua)) + " VNĐ");
            } else {
                dialog.getLblTienThua().setText(formatMoney(tienThua) + " VNĐ");
            }

        } catch (NumberFormatException e) {
            dialog.getLblTienThua().setText("Không hợp lệ");
        }
    }

    private void xuLyThanhToan() {
        if (maDDPDangChon.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn đơn cần thanh toán.");
            return;
        }

        if (dialog == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng mở thông tin thanh toán.");
            return;
        }

        tinhTienTheoMaGiamGiaVaThue();
        hienThiTongTienLenDialog();

        String phuongThuc = dialog.getPhuongThucThanhToan();

        if ("Tiền mặt".equalsIgnoreCase(phuongThuc)) {
            String khachDuaText = dialog.getTxtKhachDua().getText().trim();

            if (khachDuaText.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Vui lòng nhập số tiền khách đưa.");
                return;
            }

            double khachDua;

            try {
                khachDua = Double.parseDouble(khachDuaText);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(view, "Số tiền khách đưa không hợp lệ.");
                return;
            }

            if (khachDua < canThanhToan) {
                JOptionPane.showMessageDialog(view, "Khách đưa chưa đủ tiền thanh toán.");
                return;
            }
        }

        int confirm = JOptionPane.showConfirmDialog(
                view,
                "Xác nhận thanh toán đơn " + maDDPDangChon + "?",
                "Xác nhận thanh toán",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        
        String maHD = taoHoaDon(maDDPDangChon);

        if (maHD == null || maHD.isBlank()) {
            JOptionPane.showMessageDialog(view, "Tạo hóa đơn thất bại. Chưa hoàn tất thanh toán.");
            return;
        }

        boolean ok = dao.hoanTatThanhToan(maDDPDangChon);

        if (ok) {

            if (dialog != null) {
                dialog.dispose();
                dialog = null;
            }

            HoaDonDialog hoaDonDialog = new HoaDonDialog(maHD);
            hoaDonDialog.setLocationRelativeTo(view);
            hoaDonDialog.setVisible(true);

            view.clearDetail();
            loadDanhSachDonDaNhan();

        } else {
            JOptionPane.showMessageDialog(view, "Tạo hóa đơn thành công nhưng cập nhật trạng thái đơn thất bại.");
        }
    }

    

    private String toText(Object value) {
        return value == null ? "" : value.toString();
    }

    private double toDouble(Object value) {
        if (value == null) {
            return 0;
        }

        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }

        try {
            String text = value.toString()
                    .replace("VNĐ", "")
                    .replace(",", "")
                    .trim();

            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private int toInt(Object value) {
        if (value == null) {
            return 0;
        }

        if (value instanceof Number) {
            return ((Number) value).intValue();
        }

        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String formatMoney(double amount) {
        return moneyFormat.format(amount);
    }
    
    private String taoHoaDon(String maDDP) {
        tinhTienTheoMaGiamGiaVaThue();

        String maKM = null;

        Object selected = dialog.getCboMaGiamGia().getSelectedItem();

        if (selected instanceof KhuyenMai) {
            KhuyenMai km = (KhuyenMai) selected;

            if (km.getMaKM() != null && !km.getMaKM().isBlank()) {
                maKM = km.getMaKM();
            }
        }

        String maThue = null;

        Object selectedThue = dialog.getCboThue().getSelectedItem();

        if (selectedThue instanceof Thue) {
            Thue thue = (Thue) selectedThue;

            if (thue.getMaThue() != null && !thue.getMaThue().isBlank()) {
                maThue = thue.getMaThue();
            }
        }
        
        NhanVien nvDangNhap = PhienDangNhap.getNhanVienDangNhap();

        if (nvDangNhap == null) {
            JOptionPane.showMessageDialog(view, "Không tìm thấy nhân viên đang đăng nhập.");
            return null;
        }

        String maNVLapHoaDon = nvDangNhap.getMaNV();

        return hoaDonDao.taoHoaDon(
                maDDP,
                maKM,
                maThue,
                maNVLapHoaDon,
                canThanhToan,
                dsPhongDangChon,
                dsDichVuDangChon
        );
    }
    
    private String taoLinkVietQR(double soTien, String noiDung) {
        String amount = String.valueOf((long) soTien);

        String cleanNoiDung = noiDung
                .replaceAll("[^a-zA-Z0-9 ]", "")
                .replaceAll("\\s+", " ")
                .trim();

        if (cleanNoiDung.length() > 50) {
            cleanNoiDung = cleanNoiDung.substring(0, 50);
        }

        return "https://img.vietqr.io/image/"
                + BANK_ID + "-"
                + ACCOUNT_NO + "-compact2.png"
                + "?amount=" + amount
                + "&addInfo=" + cleanNoiDung.replace(" ", "%20")
                + "&accountName=" + ACCOUNT_NAME.replace(" ", "%20");
    }


    private void capNhatQrChuyenKhoan() {
        if (dialog == null) {
            return;
        }

        try {
            String noiDung = "Thanh toan " + maDDPDangChon;
            String qrUrl = taoLinkVietQR(canThanhToan, noiDung);

            System.out.println("QR URL: " + qrUrl);

            dialog.getLblQrChuyenKhoan().setText("Đang tải QR...");
            dialog.getLblQrChuyenKhoan().setIcon(null);

            BufferedImage bufferedImage = ImageIO.read(new URL(qrUrl));

            if (bufferedImage == null) {
                dialog.getLblQrChuyenKhoan().setText("<html>Không tải<br>được QR</html>");
                return;
            }

            Image img = bufferedImage.getScaledInstance(200, 200, Image.SCALE_SMOOTH);

            dialog.getLblQrChuyenKhoan().setText("");
            dialog.getLblQrChuyenKhoan().setIcon(new ImageIcon(img));

            dialog.getLblQrChuyenKhoan().revalidate();
            dialog.getLblQrChuyenKhoan().repaint();

        } catch (Exception e) {
            e.printStackTrace();
            dialog.getLblQrChuyenKhoan().setIcon(null);
            dialog.getLblQrChuyenKhoan().setText("<html>Không tải<br>được QR</html>");
        }
    }
}