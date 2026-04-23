package controller;

import java.sql.Date;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import dao.HoaDon_Dao;
import dao.KhachHang_Dao;
import entity.HoaDon;
import entity.KhachHang;
import entity.LoaiBaoBieu;
import gui.BaoBieu;


public class BaoBieuController {

    private final BaoBieu view;
    private final LoaiBaoBieu loaiBaoBieu;

    // Tạm thời làm thật cho HÓA ĐƠN trước
    private final HoaDon_Dao hoaDonDao;
    private final KhachHang_Dao khachHangDao;

    private final NumberFormat moneyFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));

    public BaoBieuController(BaoBieu view) {
        this.view = view;
        this.loaiBaoBieu = view.getLoaiBaoBieu();
        this.hoaDonDao = new HoaDon_Dao();
        this.khachHangDao = new KhachHang_Dao();

        initController();
        loadMacDinh();
    }

    private void initController() {
        view.getBtnXemBaoCao().addActionListener(e -> xuLyXemBaoCao());
        view.getBtnLamMoi().addActionListener(e -> lamMoiBoLoc());
        view.getBtnXuatExcel().addActionListener(e -> xuatExcel());
        view.getBtnInPdf().addActionListener(e -> inPdf());
    }

    private void loadMacDinh() {
        switch (loaiBaoBieu) {
            case HOA_DON:
                loadHoaDon(null, null, "", null, null);
                break;

            case KHACH_HANG:
                loadKhachHang("", "", "Tất cả");
                break;
            case NHAN_VIEN:
            case PHONG:
            case KHUYEN_MAI:
            case DICH_VU:
            case DON_DAT_PHONG:
                loadDuLieuMau();
                break;

            default:
                loadDuLieuMau();
                break;
        }
    }

    private void xuLyXemBaoCao() {
        try {
            switch (loaiBaoBieu) {
                case HOA_DON:
                    xuLyHoaDon();
                    break;

                case KHACH_HANG:
                    loadKhachHang("", "", "Tất cả");
                    break;
                case NHAN_VIEN:
                case PHONG:
                case KHUYEN_MAI:
                case DICH_VU:
                case DON_DAT_PHONG:
                    JOptionPane.showMessageDialog(view,
                            "Chức năng báo biểu " + loaiBaoBieu.getTenHienThi() + " đang nối dữ liệu.");
                    break;

                default:
                    JOptionPane.showMessageDialog(view, "Chưa hỗ trợ loại báo biểu này.");
                    break;
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(view, "Ngày phải đúng định dạng yyyy-mm-dd.");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view, "Có lỗi khi tải báo biểu.");
        }
    }

    private void xuLyHoaDon() {
        Date tuNgay = parseDate(view.getTxtTuNgay().getText().trim());
        Date denNgay = parseDate(view.getTxtDenNgay().getText().trim());
        String tuKhoa = view.getTxtTuKhoa().getText().trim();

        String nhanVien = view.getCboLoc1().getSelectedItem() == null
                ? ""
                : view.getCboLoc1().getSelectedItem().toString();

        String thue = view.getCboLoc2().getSelectedItem() == null
                ? ""
                : view.getCboLoc2().getSelectedItem().toString();

        if (tuNgay != null && denNgay != null && tuNgay.after(denNgay)) {
            JOptionPane.showMessageDialog(view, "Từ ngày phải nhỏ hơn hoặc bằng đến ngày.");
            return;
        }

        loadHoaDon(tuNgay, denNgay, tuKhoa, nhanVien, thue);
    }

    private void loadHoaDon(Date tuNgay, Date denNgay, String tuKhoa, String nhanVien, String thue) {
        List<HoaDon> ds = hoaDonDao.search(tuNgay, denNgay, tuKhoa);

        DefaultTableModel model = view.getTableModel();
        model.setRowCount(0);

        for (HoaDon item : ds) {
            if (!hopLeHoaDon(item, nhanVien, thue)) {
                continue;
            }

            model.addRow(new Object[] {
                    item.getMaHD(),
                    item.getMaDDP(),
                    item.getTenKH(),
                    item.getTenNV(),
                    item.getNgayLap(),
                    item.getMaThue(),
                    moneyFormat.format(item.getTongTien()) + " VNĐ"
            });
        }

        int tongSo = 0;
        double tongTien = 0;
        double maxTien = 0;

        for (int i = 0; i < model.getRowCount(); i++) {
            tongSo++;
            String tienStr = model.getValueAt(i, 6).toString().replace(" VNĐ", "").replace(".", "").replace(",", "");
            try {
                double tien = Double.parseDouble(tienStr);
                tongTien += tien;
                if (tien > maxTien) {
                    maxTien = tien;
                }
            } catch (Exception e) {
                // bỏ qua nếu format lỗi
            }
        }

        view.setCardValues(
                String.valueOf(tongSo),
                moneyFormat.format(tongTien) + " VNĐ",
                moneyFormat.format(maxTien) + " VNĐ"
        );
    }

    private boolean hopLeHoaDon(HoaDon item, String nhanVien, String thue) {
        boolean okNhanVien = true;
        boolean okThue = true;

        if (nhanVien != null && !nhanVien.isBlank() && !"Tất cả".equalsIgnoreCase(nhanVien)) {
            okNhanVien = item.getTenNV() != null && item.getTenNV().equalsIgnoreCase(nhanVien);
        }

        if (thue != null && !thue.isBlank() && !"Tất cả".equalsIgnoreCase(thue)) {
            okThue = item.getMaThue() != null && item.getMaThue().equalsIgnoreCase(thue);
        }

        return okNhanVien && okThue;
    }
    private void loadKhachHang(String maKH, String tenKH, String loaiKH) {
        List<KhachHang> ds = khachHangDao.search(maKH, tenKH, loaiKH);

        DefaultTableModel model = view.getTableModel();
        model.setRowCount(0);

        int tong = 0;
        int vip = 0;
        int thanThiet = 0;

        for (KhachHang kh : ds) {
            model.addRow(new Object[] {
                kh.getMaKH(),
                kh.getHoTen(),
                kh.getSdt(),
                kh.getCccd(),
                kh.getLoaiKH(),
                kh.getDiem()
            });

            tong++;
            if ("VIP".equalsIgnoreCase(kh.getLoaiKH())) vip++;
            if ("Thân thiết".equalsIgnoreCase(kh.getLoaiKH())) thanThiet++;
        }

        view.setCardValues(
            String.valueOf(tong),
            String.valueOf(vip),
            String.valueOf(thanThiet)
        );
    }

    private void lamMoiBoLoc() {
        view.getTxtTuNgay().setText("");
        view.getTxtDenNgay().setText("");
        view.getTxtTuKhoa().setText("");

        if (view.getCboLoc1().getItemCount() > 0) {
            view.getCboLoc1().setSelectedIndex(0);
        }

        if (view.getCboLoc2().getItemCount() > 0) {
            view.getCboLoc2().setSelectedIndex(0);
        }

        loadMacDinh();
    }

    private void xuatExcel() {
        JOptionPane.showMessageDialog(view, "Chức năng xuất Excel đang phát triển.");
    }

    private void inPdf() {
        JOptionPane.showMessageDialog(view, "Chức năng in / PDF đang phát triển.");
    }

    private Date parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return Date.valueOf(value);
    }

    private void loadDuLieuMau() {
        DefaultTableModel model = view.getTableModel();
        model.setRowCount(0);

        switch (loaiBaoBieu) {
            case KHACH_HANG:
                view.setCardValues("0", "0", "0");
                break;
            case NHAN_VIEN:
                view.setCardValues("0", "0", "0");
                break;
            case PHONG:
                view.setCardValues("0", "0", "0");
                break;
            case KHUYEN_MAI:
                view.setCardValues("0", "0", "0");
                break;
            case DICH_VU:
                view.setCardValues("0", "0", "0");
                break;
            case DON_DAT_PHONG:
                view.setCardValues("0", "0", "0");
                break;
            default:
                view.setCardValues("0", "0", "0");
                break;
        }
    }
}