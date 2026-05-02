package controller;

import java.awt.print.PrinterException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.swing.JFileChooser;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.jdatepicker.JDatePicker;

import dao.DichVu_Dao;
import dao.DonDatPhong_Dao;
import dao.HoaDon_Dao;
import dao.KhachHang_Dao;
import dao.KhuyenMai_Dao;
import dao.NhanVien_Dao;
import dao.Phong_Dao;
import entity.DichVu;
import entity.DonDatPhong;
import entity.HoaDon;
import entity.KhachHang;
import entity.KhuyenMai;
import entity.LoaiBaoBieu;
import entity.NhanVien;
import entity.Phong;
import gui.BaoBieu;

public class BaoBieuController {

    private final BaoBieu view;
    private final LoaiBaoBieu loaiBaoBieu;

    private final HoaDon_Dao hoaDonDao;
    private final KhachHang_Dao khachHangDao;
    private final DonDatPhong_Dao donDatPhongDao;
    private final Phong_Dao phongDao;
    private final NhanVien_Dao nhanVienDao;
    private final DichVu_Dao dichVuDao;
    private final KhuyenMai_Dao khuyenMaiDao;

    @SuppressWarnings("deprecation")
	private final NumberFormat moneyFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));

    private boolean dangKhoaSuKien = false;

    public BaoBieuController(BaoBieu view) {
        this.view = view;
        this.loaiBaoBieu = view.getLoaiBaoBieu();

        this.hoaDonDao = new HoaDon_Dao();
        this.khachHangDao = new KhachHang_Dao();
        this.donDatPhongDao = new DonDatPhong_Dao();
        this.phongDao = new Phong_Dao();
        this.nhanVienDao = new NhanVien_Dao();
        this.dichVuDao = new DichVu_Dao();
        this.khuyenMaiDao = new KhuyenMai_Dao();

        dangKhoaSuKien = true;
        initController();
        dangKhoaSuKien = false;
        loadMacDinh();
    }

    private void initController() {
        view.getBtnLamMoi().addActionListener(e -> lamMoiBoLoc());
        view.getBtnXuatExcel().addActionListener(e -> xuatExcel());
        view.getBtnInPdf().addActionListener(e -> inPdf());

        batSuKienTuDongLoc();
    }

    private void batSuKienTuDongLoc() {
        view.getCboLoc1().addActionListener(e -> tuDongLoc());
        view.getCboLoc2().addActionListener(e -> tuDongLoc());
        view.getCboLoc3().addActionListener(e -> tuDongLoc());

        view.getDateTuNgay().addActionListener(e -> tuDongLoc());
        view.getDateDenNgay().addActionListener(e -> tuDongLoc());

        view.getTxtTuKhoa().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                tuDongLoc();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                tuDongLoc();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                tuDongLoc();
            }
        });
    }

    private void tuDongLoc() {
        if (dangKhoaSuKien) {
            return;
        }

        xuLyXemBaoCao();
    }

    private void loadMacDinh() {
        switch (loaiBaoBieu) {
            case HOA_DON:
                loadHoaDon(null, null, "");
                break;

            case KHACH_HANG:
                loadKhachHang("", "Tất cả");
                break;

            case PHONG:
                loadPhong("", "Tất cả", "Tất cả");
                break;

            case NHAN_VIEN:
                loadNhanVien("", "Tất cả", "Tất cả", "Tất cả");
                break;

            case DON_DAT_PHONG:
                loadDonDatPhong("", "Tất cả", null, null);
                break;

            case DICH_VU:
                loadDichVu(null, null, "");
                break;

            case KHUYEN_MAI:
                loadKhuyenMai(null, null, "", "Tất cả");
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
                    xuLyKhachHang();
                    break;

                case PHONG:
                    xuLyPhong();
                    break;

                case NHAN_VIEN:
                    xuLyNhanVien();
                    break;

                case DON_DAT_PHONG:
                    xuLyDonDatPhong();
                    break;

                case DICH_VU:
                    xuLyDichVu();
                    break;

                case KHUYEN_MAI:
                    xuLyKhuyenMai();
                    break;

                default:
                    JOptionPane.showMessageDialog(view, "Chưa hỗ trợ loại báo biểu này.");
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view, "Có lỗi khi tải báo biểu.");
        }
    }

    private void xuLyHoaDon() {
        Date tuNgay = getDateFromPicker(view.getDateTuNgay());
        Date denNgay = getDateFromPicker(view.getDateDenNgay());
        String tuKhoa = view.getTxtTuKhoa().getText().trim();

        if (!kiemTraKhoangNgay(tuNgay, denNgay)) {
            return;
        }

        loadHoaDon(tuNgay, denNgay, tuKhoa);
    }

    private void xuLyKhachHang() {
        String tuKhoa = view.getTxtTuKhoa().getText().trim();
        String loaiKH = getComboValue(view.getCboLoc1());

        loadKhachHang(tuKhoa, loaiKH);
    }

    private void xuLyPhong() {
        String tuKhoa = view.getTxtTuKhoa().getText().trim();
        String loaiPhong = getComboValue(view.getCboLoc1());
        String trangThai = getComboValue(view.getCboLoc2());

        loadPhong(tuKhoa, loaiPhong, trangThai);
    }

    private void xuLyNhanVien() {
        String tuKhoa = view.getTxtTuKhoa().getText().trim();
        String caLam = getComboValue(view.getCboLoc1());
        String trangThai = getComboValue(view.getCboLoc2());
        String viTri = getComboValue(view.getCboLoc3());

        loadNhanVien(tuKhoa, caLam, trangThai, viTri);
    }

    private void xuLyDonDatPhong() {
        Date tuNgay = getDateFromPicker(view.getDateTuNgay());
        Date denNgay = getDateFromPicker(view.getDateDenNgay());
        String tuKhoa = view.getTxtTuKhoa().getText().trim();
        String tinhTrang = getComboValue(view.getCboLoc1());

        if (!kiemTraKhoangNgay(tuNgay, denNgay)) {
            return;
        }

        loadDonDatPhong(tuKhoa, tinhTrang, tuNgay, denNgay);
    }

    private void xuLyDichVu() {
        Date tuNgay = getDateFromPicker(view.getDateTuNgay());
        Date denNgay = getDateFromPicker(view.getDateDenNgay());
        String tuKhoa = view.getTxtTuKhoa().getText().trim();

        if (!kiemTraKhoangNgay(tuNgay, denNgay)) {
            return;
        }

        loadDichVu(tuNgay, denNgay, tuKhoa);
    }

    private void xuLyKhuyenMai() {
        Date tuNgay = getDateFromPicker(view.getDateTuNgay());
        Date denNgay = getDateFromPicker(view.getDateDenNgay());
        String tuKhoa = view.getTxtTuKhoa().getText().trim();
        String khoangGiaTri = getComboValue(view.getCboLoc1());

        if (!kiemTraKhoangNgay(tuNgay, denNgay)) {
            return;
        }

        loadKhuyenMai(tuNgay, denNgay, tuKhoa, khoangGiaTri);
    }

    private void loadHoaDon(Date tuNgay, Date denNgay, String tuKhoa) {
        List<HoaDon> ds = hoaDonDao.searchBaoBieu(tuNgay, denNgay, tuKhoa);

        DefaultTableModel model = view.getTableModel();
        model.setRowCount(0);

        int tongSo = 0;
        double tongDoanhThu = 0;
        double maxTien = 0;

        for (HoaDon item : ds) {
            model.addRow(new Object[] {
                    item.getMaHD(),
                    item.getMaDDP(),
                    item.getTenKH(),
                    item.getTenNV(),
                    item.getNgayLap(),
                    item.getMaThue(),
                    moneyFormat.format(item.getTongTien()) + " VNĐ"
            });

            tongSo++;
            tongDoanhThu += item.getTongTien();

            if (item.getTongTien() > maxTien) {
                maxTien = item.getTongTien();
            }
        }

        view.setCardValues(
                String.valueOf(tongSo),
                moneyFormat.format(tongDoanhThu) + " VNĐ",
                moneyFormat.format(maxTien) + " VNĐ"
        );
    }

    private void loadKhachHang(String tuKhoa, String loaiKH) {
        List<KhachHang> ds = khachHangDao.searchBaoBieu(tuKhoa, loaiKH);

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

            if ("VIP".equalsIgnoreCase(kh.getLoaiKH())) {
                vip++;
            }

            if ("Thân thiết".equalsIgnoreCase(kh.getLoaiKH())) {
                thanThiet++;
            }
        }

        view.setCardValues(
                String.valueOf(tong),
                String.valueOf(vip),
                String.valueOf(thanThiet)
        );
    }

    private void loadPhong(String tuKhoa, String loaiPhong, String trangThai) {
        List<Phong> ds = phongDao.searchBaoBieu(tuKhoa, loaiPhong, trangThai);

        DefaultTableModel model = view.getTableModel();
        model.setRowCount(0);

        int tongPhong = 0;
        int phongTrong = 0;
        int dangSuDung = 0;

        for (Phong p : ds) {
            model.addRow(new Object[] {
                    p.getMaPhong(),
                    p.getLoaiPhong(),
                    p.getSoNguoiToiDa(),
                    moneyFormat.format(p.getGiaPhong()) + " VNĐ",
                    p.getTrangThai()
            });

            tongPhong++;

            if ("Trống".equalsIgnoreCase(p.getTrangThai())) {
                phongTrong++;
            }

            if ("Đang sử dụng".equalsIgnoreCase(p.getTrangThai())) {
                dangSuDung++;
            }
        }

        view.setCardValues(
                String.valueOf(tongPhong),
                String.valueOf(phongTrong),
                String.valueOf(dangSuDung)
        );
    }

    private void loadNhanVien(String tuKhoa, String caLam, String trangThai, String viTri) {
        List<NhanVien> ds = nhanVienDao.searchBaoBieu(tuKhoa, caLam, trangThai, viTri);

        DefaultTableModel model = view.getTableModel();
        model.setRowCount(0);

        int tong = 0;
        int dangLam = 0;
        int nghiViec = 0;

        for (NhanVien nv : ds) {
            model.addRow(new Object[] {
                    nv.getMaNV(),
                    nv.getHoTen(),
                    nv.getSdt(),
                    nv.getEmail(),
                    nv.getCaLamViec(),
                    nv.getViTriCongViec(),
                    nv.getTrangThaiLamViec()
            });

            tong++;

            if ("Đang làm".equalsIgnoreCase(nv.getTrangThaiLamViec())) {
                dangLam++;
            }

            if ("Nghỉ việc".equalsIgnoreCase(nv.getTrangThaiLamViec())) {
                nghiViec++;
            }
        }

        view.setCardValues(
                String.valueOf(tong),
                String.valueOf(dangLam),
                String.valueOf(nghiViec)
        );
    }

    private void loadDonDatPhong(String tuKhoa, String tinhTrang, Date tuNgay, Date denNgay) {
        List<DonDatPhong> ds = donDatPhongDao.searchBaoBieu(tuKhoa, tinhTrang, tuNgay, denNgay);

        DefaultTableModel model = view.getTableModel();
        model.setRowCount(0);

        int tongDon = 0;
        int daNhan = 0;
        int daDat = 0;

        for (DonDatPhong item : ds) {
            model.addRow(new Object[] {
                    item.getMaDDP(),
                    item.getHoTen(),
                    item.getMaPhong(),
                    item.getNgayNhan(),
                    item.getNgayTra(),
                    item.getTinhTrang(),
                    item.getTienCoc() == null ? "" : moneyFormat.format(item.getTienCoc()) + " VNĐ"
            });

            tongDon++;

            if ("Đã nhận".equalsIgnoreCase(item.getTinhTrang())) {
                daNhan++;
            }

            if ("Đã đặt".equalsIgnoreCase(item.getTinhTrang())) {
                daDat++;
            }
        }

        view.setCardValues(
                String.valueOf(tongDon),
                String.valueOf(daNhan),
                String.valueOf(daDat)
        );
    }

    private void loadDichVu(Date tuNgay, Date denNgay, String tuKhoa) {
        List<DichVu> ds = dichVuDao.searchBaoBieu(tuNgay, denNgay, tuKhoa);

        DefaultTableModel model = view.getTableModel();
        model.setRowCount(0);

        int tongDichVu = 0;
        int tongLuotDung = 0;
        double tongDoanhThu = 0;

        for (DichVu dv : ds) {
            model.addRow(new Object[] {
                    dv.getMaDV(),
                    dv.getTenDV(),
                    moneyFormat.format(dv.getGia()) + " VNĐ",
                    dv.getSoLuotDung(),
                    moneyFormat.format(dv.getDoanhThu()) + " VNĐ"
            });

            tongDichVu++;
            tongLuotDung += dv.getSoLuotDung();
            tongDoanhThu += dv.getDoanhThu();
        }

        view.setCardValues(
                String.valueOf(tongDichVu),
                String.valueOf(tongLuotDung),
                moneyFormat.format(tongDoanhThu) + " VNĐ"
        );
    }

    private void loadKhuyenMai(Date tuNgay, Date denNgay, String tuKhoa, String khoangGiaTri) {
        List<KhuyenMai> ds = khuyenMaiDao.searchBaoBieu(tuNgay, denNgay, tuKhoa, khoangGiaTri);

        DefaultTableModel model = view.getTableModel();
        model.setRowCount(0);

        int tong = 0;
        int dangApDung = 0;
        int sapHetHan = 0;

        java.time.LocalDate today = java.time.LocalDate.now();

        for (KhuyenMai km : ds) {
            model.addRow(new Object[] {
                    km.getMaKM(),
                    km.getTenKM(),
                    km.getGiaTri() + "%",
                    km.getNgayBatDau(),
                    km.getNgayKetThuc()
            });

            tong++;

            if (km.getNgayBatDau() != null && km.getNgayKetThuc() != null) {
                boolean dangDung = !today.isBefore(km.getNgayBatDau())
                        && !today.isAfter(km.getNgayKetThuc());

                if (dangDung) {
                    dangApDung++;
                }

                long soNgayConLai = java.time.temporal.ChronoUnit.DAYS.between(today, km.getNgayKetThuc());

                if (dangDung && soNgayConLai >= 0 && soNgayConLai <= 7) {
                    sapHetHan++;
                }
            }
        }

        view.setCardValues(
                String.valueOf(tong),
                String.valueOf(dangApDung),
                String.valueOf(sapHetHan)
        );
    }

    private void lamMoiBoLoc() {
        dangKhoaSuKien = true;

        clearDatePicker(view.getDateTuNgay());
        clearDatePicker(view.getDateDenNgay());
        view.getTxtTuKhoa().setText("");

        if (view.getCboLoc1().getItemCount() > 0) {
            view.getCboLoc1().setSelectedIndex(0);
        }

        if (view.getCboLoc2().getItemCount() > 0) {
            view.getCboLoc2().setSelectedIndex(0);
        }

        if (view.getCboLoc3().getItemCount() > 0) {
            view.getCboLoc3().setSelectedIndex(0);
        }

        dangKhoaSuKien = false;

        loadMacDinh();
    }
    private void clearDatePicker(JDatePicker datePicker) {
        if (datePicker == null || datePicker.getModel() == null) {
            return;
        }

        datePicker.getModel().setValue(null);
        datePicker.getModel().setSelected(false);

        datePicker.repaint();
        datePicker.revalidate();
    }

    private void xuatExcel() {
        DefaultTableModel model = view.getTableModel();

        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(view, "Không có dữ liệu để xuất.");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu file Excel");
        fileChooser.setSelectedFile(new File("bao_bieu_" + loaiBaoBieu.name().toLowerCase() + ".csv"));

        int userSelection = fileChooser.showSaveDialog(view);

        if (userSelection != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = fileChooser.getSelectedFile();

        if (!file.getName().toLowerCase().endsWith(".csv")) {
            file = new File(file.getAbsolutePath() + ".csv");
        }

        try (
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)
                )
        ) {
            writer.write('\ufeff');

            for (int i = 0; i < model.getColumnCount(); i++) {
                writer.write(csvValue(model.getColumnName(i)));

                if (i < model.getColumnCount() - 1) {
                    writer.write(",");
                }
            }

            writer.newLine();

            for (int row = 0; row < model.getRowCount(); row++) {
                for (int col = 0; col < model.getColumnCount(); col++) {
                    Object value = model.getValueAt(row, col);
                    writer.write(csvValue(value == null ? "" : value.toString()));

                    if (col < model.getColumnCount() - 1) {
                        writer.write(",");
                    }
                }

                writer.newLine();
            }

            JOptionPane.showMessageDialog(view, "Xuất Excel thành công!");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, "Lỗi khi xuất Excel.");
        }
    }

    private String csvValue(String value) {
        String safe = value.replace("\"", "\"\"");
        return "\"" + safe + "\"";
    }

    private void inPdf() {
        try {
            DefaultTableModel model = view.getTableModel();

            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(view, "Không có dữ liệu để xuất.");
                return;
            }

            String folderPath = "export";
            File folder = new File(folderPath);

            if (!folder.exists()) {
                folder.mkdirs();
            }

            String filePath = folderPath + "/baobieu_" 
                    + loaiBaoBieu.name().toLowerCase() + ".pdf";

            // ✅ iText 5 đúng chuẩn
            Document document = new Document(PageSize.A4);

            PdfWriter.getInstance(document, new FileOutputStream(filePath));

            document.open();

            // Title
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
            Paragraph title = new Paragraph(
                    "BÁO BIỂU - " + loaiBaoBieu.getTenHienThi(),
                    titleFont
            );
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph(" ")); // dòng trống

            int columnCount = model.getColumnCount();

            PdfPTable table = new PdfPTable(columnCount);
            table.setWidthPercentage(100);

            // Header
            for (int i = 0; i < columnCount; i++) {
                PdfPCell cell = new PdfPCell(new Phrase(model.getColumnName(i)));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            // Data
            for (int row = 0; row < model.getRowCount(); row++) {
                for (int col = 0; col < columnCount; col++) {
                    Object value = model.getValueAt(row, col);
                    table.addCell(value == null ? "" : value.toString());
                }
            }

            document.add(table);
            document.close();

            JOptionPane.showMessageDialog(view, "Xuất PDF thành công!\nFile: " + filePath);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, "Lỗi khi xuất PDF.");
        }
    }

    private Date getDateFromPicker(JDatePicker datePicker) {
        Object value = datePicker.getModel().getValue();

        if (value == null) {
            return null;
        }

        if (value instanceof java.util.Date) {
            java.util.Date utilDate = (java.util.Date) value;
            return new Date(utilDate.getTime());
        }

        if (value instanceof Calendar) {
            Calendar calendar = (Calendar) value;
            return new Date(calendar.getTimeInMillis());
        }

        return null;
    }

    private boolean kiemTraKhoangNgay(Date tuNgay, Date denNgay) {
        if (tuNgay != null && denNgay != null && tuNgay.after(denNgay)) {
            JOptionPane.showMessageDialog(view, "Từ ngày phải nhỏ hơn hoặc bằng đến ngày.");
            return false;
        }

        return true;
    }

    private String getComboValue(JComboBox<String> comboBox) {
        return comboBox.getSelectedItem() == null
                ? "Tất cả"
                : comboBox.getSelectedItem().toString();
    }

    private void loadDuLieuMau() {
        DefaultTableModel model = view.getTableModel();
        model.setRowCount(0);
        view.setCardValues("0", "0", "0");
    }
}