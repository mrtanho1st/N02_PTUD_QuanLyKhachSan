package controller;

import org.jfree.data.category.DefaultCategoryDataset;

import gui.QLThongke;
import dao.*;
import entity.LoaiThongKe;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.xml.crypto.Data;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.jdatepicker.JDatePicker;

public class QLThongKeController {
    private QLThongke view;
    private HoaDon_Dao hoaDonDao;
    private KhachHang_Dao khachHangDao;
    private Phong_Dao phongDao;
    private DichVu_Dao dichVuDao;
    private NhanVien_Dao nhanVienDao;
    private DonDatPhong_Dao donDatPhongDao;
    private LoaiThongKe loaiThongKe;
    
    private NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    private boolean dangKhoaSuKien = false;

    public QLThongKeController(QLThongke view) {
        this.view = view;
        this.loaiThongKe = view.getLoaiThongKe();
        this.hoaDonDao = new HoaDon_Dao();
        this.khachHangDao = new KhachHang_Dao();
        this.phongDao = new Phong_Dao();
        this.dichVuDao = new DichVu_Dao();
        this.nhanVienDao = new NhanVien_Dao();
        this.donDatPhongDao = new DonDatPhong_Dao();
        
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

        view.getDateTuNgay().getModel().addChangeListener(e -> tuDongLoc());
        view.getDateDenNgay().getModel().addChangeListener(e -> tuDongLoc());
    }

    private void tuDongLoc() {
        if (dangKhoaSuKien) {
            return;
        }
        xuLyXemBaoCao();
    }

    private void loadMacDinh() {
        switch (loaiThongKe) {
            case DOANH_THU_THEO_THOI_GIAN:
                loadDoanhThuTheoThoiGian();
                break;
            case DOANH_THU_THEO_KHACH_HANG:
                loadDoanhThuTheoKhachHang();
                break;
            case DOANH_THU_THEO_PHONG:
                loadDoanhThuTheoPhong();
                break;
            case KHACH_HANG_DIEM_CAO_NHAT:
                loadKhachHangDiemCaoNhat();
                break;
            case THONG_KE_DICH_VU:
                loadThongKeDichVu();
                break;
            case THONG_KE_HOA_DON:
                loadThongKeHoaDon();
                break;
            case THONG_KE_DON_DAT_PHONG:
                loadThongKeDonDatPhong();
                break;
            case THONG_KE_THEO_NHAN_VIEN:
                loadThongKeTheoNhanVien();
                break;
        }
    }

    private void xuLyXemBaoCao() {
        try {
            switch (loaiThongKe) {
                case DOANH_THU_THEO_THOI_GIAN:
                    xuLyDoanhThuTheoThoiGian();
                    break;
                case DOANH_THU_THEO_KHACH_HANG:
                    xuLyDoanhThuTheoKhachHang();
                    break;
                case DOANH_THU_THEO_PHONG:
                    xuLyDoanhThuTheoPhong();
                    break;
                case KHACH_HANG_DIEM_CAO_NHAT:
                    xuLyKhachHangDiemCaoNhat();
                    break;
                case THONG_KE_DICH_VU:
                    xuLyThongKeDichVu();
                    break;
                case THONG_KE_HOA_DON:
                    xuLyThongKeHoaDon();
                    break;
                case THONG_KE_DON_DAT_PHONG:
                    xuLyThongKeDonDatPhong();
                    break;
                case THONG_KE_THEO_NHAN_VIEN:
                    xuLyThongKeTheoNhanVien();
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view, "Có lỗi khi tải thống kê.");
        }
    }

    public void loadData() {
        xuLyXemBaoCao();
    }
    // Reset các giá trị trên card về mặc định theo loại thống kê
    private void resetCardValues() {
        switch (loaiThongKe) {
            case DOANH_THU_THEO_THOI_GIAN:
                view.setCardValues("0", "0 VNĐ", "N/A");
                break;
            case DOANH_THU_THEO_KHACH_HANG:
                view.setCardValues("0", "0", "0 VNĐ");
                break;
            case DOANH_THU_THEO_PHONG:
                view.setCardValues("0", "0 VNĐ", "0 VNĐ");
                break;
            case KHACH_HANG_DIEM_CAO_NHAT:
                view.setCardValues("0", "0 Điểm", "0,0 Điểm/Khách");
                break;
            case THONG_KE_DICH_VU:
                view.setCardValues("0", "0 VNĐ", "0 VNĐ");
                break;
            case THONG_KE_HOA_DON:
                view.setCardValues("0", "0 VNĐ", "0 VNĐ");
                break;
            case THONG_KE_DON_DAT_PHONG:
                view.setCardValues("0", "0", "0 VNĐ");
                break;
            case THONG_KE_THEO_NHAN_VIEN:
                view.setCardValues("0", "0", "0 VNĐ");
                break;
        }
    }

    // =======================================================
    // CÁC HÀM XỬ LÝ LOGIC TRƯỚC KHI LOAD DỮ LIỆU (xuLyXXX)
    // =======================================================

    private void xuLyDoanhThuTheoThoiGian() {
        Date tuNgay = getDateFromView(view.getDateTuNgay());
        Date denNgay = getDateFromView(view.getDateDenNgay());

        if (!kiemTraKhoangNgay(tuNgay, denNgay)) {
            return;
        }

        loadDoanhThuTheoThoiGian();
    }

    private void xuLyDoanhThuTheoKhachHang() {
        String loaiKH = (String) view.getCboLoc1().getSelectedItem();
        loadDoanhThuTheoKhachHang(loaiKH);
    }

    private void xuLyDoanhThuTheoPhong() {
        String loaiPhong = (String) view.getCboLoc1().getSelectedItem();
        loadDoanhThuTheoPhong(loaiPhong);
    }

    private void xuLyKhachHangDiemCaoNhat() {
        String loaiKH = (String) view.getCboLoc1().getSelectedItem();
        loadKhachHangDiemCaoNhat(loaiKH);
    }

    private void xuLyThongKeDichVu() {
        Date tuNgay = getDateFromView(view.getDateTuNgay());
        Date denNgay = getDateFromView(view.getDateDenNgay());

        if (!kiemTraKhoangNgay(tuNgay, denNgay)) {
            return;
        }

        loadThongKeDichVu();
    }

    private void xuLyThongKeHoaDon() {
        Date tuNgay = getDateFromView(view.getDateTuNgay());
        Date denNgay = getDateFromView(view.getDateDenNgay());

        if (!kiemTraKhoangNgay(tuNgay, denNgay)) {
            return;
        }

        loadThongKeHoaDon();
    }

    private void xuLyThongKeDonDatPhong() {
        Date tuNgay = getDateFromView(view.getDateTuNgay());
        Date denNgay = getDateFromView(view.getDateDenNgay());
        String thang = (String) view.getCboLoc1().getSelectedItem();

        // Ưu tiên 1: Nếu người dùng có chọn ngày cụ thể
        if (tuNgay != null && denNgay != null) {
            if (tuNgay.after(denNgay)) {
                JOptionPane.showMessageDialog(null, "Vui lòng chọn ngày hợp lệ (Từ ngày phải trước Đến ngày)", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            loadThongKeDonDatPhong(tuNgay, denNgay);
        }
        // Ưu tiên 2: Nếu người dùng chọn lọc theo Tháng trong ComboBox
        else if (thang != null && !"Tất cả".equals(thang)) {
            int month = Integer.parseInt(thang.replace("Tháng ", ""));
            LocalDate now = LocalDate.now(); // Sử dụng năm hiện tại
            LocalDate firstDay = now.withMonth(month).withDayOfMonth(1);
            LocalDate lastDay = now.withMonth(month).withDayOfMonth(firstDay.lengthOfMonth());
            
            loadThongKeDonDatPhong(Date.valueOf(firstDay), Date.valueOf(lastDay));
        }
        // Cuối cùng: Mặc định lấy tất cả dữ liệu
        else {
            loadThongKeDonDatPhong(null, null);
        }
    }

    private void xuLyThongKeTheoNhanVien() {
        loadThongKeTheoNhanVien();
    }

    // =======================================================
    // CÁC HÀM XỬ LÝ LOAD DỮ LIỆU
    // =======================================================
    // Doanh Thu Theo Thời Gian
    private void updateDoanhThuUI(List<Object[]> listData) {
        System.out.println("Dữ liệu doanh thu theo thời gian chi tiết: " + listData.size() + " bản ghi");
        int totalHoaDon = 0;
        double totalDoanhThu = 0, doanhThuCaoNhat = 0;
        String ngayMax = "";

        view.getTableModel().setRowCount(0); // Xóa dữ liệu cũ trong bảng

        if (listData != null && !listData.isEmpty()) {
            for (Object[] row : listData) {
                try {
                    double tongTien = row[4] != null ? ((Number) row[4]).doubleValue() : 0;
                    int soHoaDon = row[1] != null ? ((Number) row[1]).intValue() : 0;
                    double tienPhong = row[2] != null ? ((Number) row[2]).doubleValue() : 0;
                    double tienDichVu = row[3] != null ? ((Number) row[3]).doubleValue() : 0;
                    
                    totalHoaDon += soHoaDon;
                    totalDoanhThu += tongTien;
                    
                    if (tongTien > doanhThuCaoNhat) { 
                        doanhThuCaoNhat = tongTien; 
                        ngayMax = row[0] != null ? row[0].toString() : ""; 
                    }
                    
                    view.getTableModel().addRow(new Object[] {
                        row[0].toString(), soHoaDon, formatCurrency(tienPhong), 
                        formatCurrency(tienDichVu), formatCurrency(tongTien)
                    });
                } catch (Exception rowEx) {
                    System.err.println("Lỗi xử lý hàng dữ liệu: " + rowEx.getMessage());
                    rowEx.printStackTrace();
                }
            }
        }

        // Cập nhật các Card và Chart
        view.setCardValues(String.valueOf(totalHoaDon), formatCurrency(totalDoanhThu), ngayMax.isEmpty() ? "N/A" : ngayMax);
        updateChartByTime(listData);
        System.out.println("Load doanh thu thành công!");
    }
    private void loadDoanhThuTheoThoiGian() {
        try { 
        	Date tuNgay = getDateFromView(view.getDateTuNgay());
            Date denNgay = getDateFromView(view.getDateDenNgay());
            String thang = (String) view.getCboLoc1().getSelectedItem();
            List<Object[]> listData = null;

            // Ưu tiên 1: Nếu người dùng có chọn ngày cụ thể
            if (tuNgay != null && denNgay != null) {
                if (tuNgay.after(denNgay)) {
                    JOptionPane.showMessageDialog(null, "Vui lòng chọn ngày hợp lệ (Từ ngày phải trước Đến ngày)", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return; // Dừng lại không load nữa
                }
                listData = hoaDonDao.getDoanhThuTheoThoiGianChiTiet(tuNgay, denNgay);
            } 
            // Ưu tiên 2: Nếu người dùng chọn lọc theo Tháng trong ComboBox
            else if (thang != null && !"Tất cả".equals(thang)) {
                int month = Integer.parseInt(thang.replace("Tháng ", ""));
                LocalDate now = LocalDate.now(); // Sử dụng năm hiện tại
                LocalDate firstDay = now.withMonth(month).withDayOfMonth(1);
                LocalDate lastDay = now.withMonth(month).withDayOfMonth(firstDay.lengthOfMonth());
                
                listData = hoaDonDao.getDoanhThuTheoThoiGianChiTiet(Date.valueOf(firstDay), Date.valueOf(lastDay));
            } 
            // Cuối cùng: Mặc định lấy tất cả dữ liệu (hoặc 30 ngày qua tùy logic của bạn)
            else {
                listData = hoaDonDao.getDoanhThuTheoThoiGianChiTiet(null, null);
            }

            // Gọi hàm để update UI 1 lần duy nhất
            updateDoanhThuUI(listData);

        } catch (Exception e) { 
            String errorMsg = "Lỗi load doanh thu: " + e.getMessage();
            System.err.println(errorMsg);
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, errorMsg, "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadDoanhThuTheoKhachHang() {
        try {
            List<Object[]> listData = hoaDonDao.getDoanhThuTheoKhachHang(getDateFromView(view.getDateTuNgay()), getDateFromView(view.getDateDenNgay()));
            int totalLuotLuuTru = 0; double chiTieuCaoNhat = 0;

            view.getTableModel().setRowCount(0);
            if (listData != null) {
                for (Object[] row : listData) {
                    double tongChiTieu = ((Number) row[4]).doubleValue();
                    totalLuotLuuTru += ((Number) row[3]).intValue();
                    if (tongChiTieu > chiTieuCaoNhat) chiTieuCaoNhat = tongChiTieu;

                    Object[] displayRow = row.clone();
                    displayRow[4] = formatCurrency(tongChiTieu);
                    view.getTableModel().addRow(displayRow);
                }
            }

            view.setCardValues(String.valueOf(listData != null ? listData.size() : 0), String.valueOf(totalLuotLuuTru), formatCurrency(chiTieuCaoNhat));
            updateChartByCustomer(listData);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadDoanhThuTheoKhachHang(String loaiKH) {
        try {
            List<Object[]> listData = hoaDonDao.getDoanhThuTheoKhachHang(getDateFromView(view.getDateTuNgay()), getDateFromView(view.getDateDenNgay()), loaiKH);
            int totalLuotLuuTru = 0; double chiTieuCaoNhat = 0;

            view.getTableModel().setRowCount(0);
            if (listData != null) {
                for (Object[] row : listData) {
                    double tongChiTieu = ((Number) row[4]).doubleValue();
                    totalLuotLuuTru += ((Number) row[3]).intValue();
                    if (tongChiTieu > chiTieuCaoNhat) chiTieuCaoNhat = tongChiTieu;

                    Object[] displayRow = row.clone();
                    displayRow[4] = formatCurrency(tongChiTieu);
                    view.getTableModel().addRow(displayRow);
                }
            }

            view.setCardValues(String.valueOf(listData != null ? listData.size() : 0), String.valueOf(totalLuotLuuTru), formatCurrency(chiTieuCaoNhat));
            updateChartByCustomer(listData);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadDoanhThuTheoPhong() {
        try {
            Date tuNgay = getDateFromView(view.getDateTuNgay());
            Date denNgay = getDateFromView(view.getDateDenNgay());
            
            // Debug log
            System.out.println("=== LOAD DOANH THU THEO PHÒNG ===");
            System.out.println("Từ ngày: " + tuNgay);
            System.out.println("Đến ngày: " + denNgay);
            
            // Kiểm tra ngày hợp lệ
            if (tuNgay != null && denNgay != null && tuNgay.after(denNgay)) {
                JOptionPane.showMessageDialog(null, "Vui lòng chọn ngày hợp lệ (Từ ngày phải trước Đến ngày)", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Fetch dữ liệu từ DAO
            System.out.println("Đang fetch dữ liệu từ DAO...");
            List<Object[]> listData = hoaDonDao.getDoanhThuTheoPhong(tuNgay, denNgay);
            
            // Debug log
            if (listData == null) {
                System.out.println("❌ listData = NULL");
            } else {
                System.out.println("✅ Dữ liệu doanh thu theo phòng: " + listData.size() + " bản ghi");
                
                // In chi tiết dữ liệu
                for (int i = 0; i < listData.size(); i++) {
                    Object[] row = listData.get(i);
                    System.out.println("  Row " + i + ": MaPhong=" + row[0] + ", LoaiPhong=" + row[1] + 
                                     ", SoLuotThue=" + row[2] + ", DoanhThu=" + row[4]);
                }
            }
            
            // Update UI
            updateDoanhThuTheoPhongUI(listData);
            
            System.out.println("Load doanh thu theo phòng thành công!");
            System.out.println("=================================\n");
            
        } catch (Exception e) {
            String errorMsg = "Lỗi load doanh thu theo phòng: " + e.getMessage();
            System.err.println(errorMsg);
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, errorMsg, "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadDoanhThuTheoPhong(String loaiPhong) {
        try {
            Date tuNgay = getDateFromView(view.getDateTuNgay());
            Date denNgay = getDateFromView(view.getDateDenNgay());

            // Fetch dữ liệu từ DAO
            List<Object[]> listData = hoaDonDao.getDoanhThuTheoPhong(tuNgay, denNgay, loaiPhong);

            // Update UI
            updateDoanhThuTheoPhongUI(listData);

        } catch (Exception e) {
            String errorMsg = "Lỗi load doanh thu theo phòng: " + e.getMessage();
            System.err.println(errorMsg);
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, errorMsg, "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Hàm hỗ trợ update UI cho doanh thu theo phòng
    private void updateDoanhThuTheoPhongUI(List<Object[]> listData) {
        int totalLuotThue = 0;
        double totalDoanhThu = 0, doanhThuMax = 0;
        String phongMax = "";

        view.getTableModel().setRowCount(0); // Xóa dữ liệu cũ

        if (listData == null) {
            System.out.println("⚠️ listData NULL - không có dữ liệu để hiển thị");
            view.setCardValues("0", "0 VNĐ", "N/A");
            updateChartByRoom(null);
            return;
        }

        if (listData.isEmpty()) {
            System.out.println("⚠️ listData trống - không có bản ghi nào");
            view.setCardValues("0", "0 VNĐ", "N/A");
            JOptionPane.showMessageDialog(null, "Không có dữ liệu doanh thu phòng trong khoảng thời gian được chọn", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            updateChartByRoom(null);
            return;
        }

        for (Object[] row : listData) {
            try {
                double doanhThu = row[4] != null ? ((Number) row[4]).doubleValue() : 0;
                int luotThue = row[2] != null ? ((Number) row[2]).intValue() : 0;
                
                totalLuotThue += luotThue;
                totalDoanhThu += doanhThu;
                
                if (doanhThu > doanhThuMax) {
                    doanhThuMax = doanhThu;
                    phongMax = row[0] != null ? row[0].toString() : "";
                }

                // Tạo hàng hiển thị với định dạng tiền tệ
                Object[] displayRow = row.clone();
                displayRow[4] = formatCurrency(doanhThu);
                view.getTableModel().addRow(displayRow);
                
            } catch (Exception rowEx) {
                System.err.println("Lỗi xử lý hàng dữ liệu: " + rowEx.getMessage());
                rowEx.printStackTrace();
            }
        }

        // Cập nhật các Card: Tổng lượt thuê | Tổng doanh thu | Phòng doanh thu cao nhất
        view.setCardValues(
            String.valueOf(totalLuotThue), 
            formatCurrency(totalDoanhThu), 
            phongMax.isEmpty() ? "N/A" : phongMax
        );
        
        System.out.println("📊 Tổng lượt thuê: " + totalLuotThue);
        System.out.println("💰 Tổng doanh thu: " + formatCurrency(totalDoanhThu));
        System.out.println("🏆 Phòng doanh thu cao nhất: " + (phongMax.isEmpty() ? "N/A" : phongMax));
        
        // Cập nhật biểu đồ
        updateChartByRoom(listData);
    }

    private void loadKhachHangDiemCaoNhat() {
        try {
            List<Object[]> listData = hoaDonDao.getKhachHangDiemCaoNhat();
            double tongDiem = 0, diemCaoNhat = 0;

            view.getTableModel().setRowCount(0);
            if (listData != null) {
                for (Object[] row : listData) {
                    double diem = ((Number) row[5]).doubleValue();
                    tongDiem += diem;
                    if (diem > diemCaoNhat) diemCaoNhat = diem;
                    view.getTableModel().addRow(row);
                }
            }

            int size = listData != null ? listData.size() : 0;
            view.setCardValues(String.valueOf(size), String.format("%.0f Điểm", diemCaoNhat), String.format("%.1f Điểm/Khách", size > 0 ? tongDiem / size : 0));
            updateChartByCustomerPoint(listData);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadKhachHangDiemCaoNhat(String loaiKH) {
        try {
            List<Object[]> listData = hoaDonDao.getKhachHangDiemCaoNhat(loaiKH);
            double tongDiem = 0, diemCaoNhat = 0;

            view.getTableModel().setRowCount(0);
            if (listData != null) {
                for (Object[] row : listData) {
                    double diem = ((Number) row[5]).doubleValue();
                    tongDiem += diem;
                    if (diem > diemCaoNhat) diemCaoNhat = diem;
                    view.getTableModel().addRow(row);
                }
            }

            int size = listData != null ? listData.size() : 0;
            view.setCardValues(String.valueOf(size), String.format("%.0f Điểm", diemCaoNhat), String.format("%.1f Điểm/Khách", size > 0 ? tongDiem / size : 0));
            updateChartByCustomerPoint(listData);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadThongKeDichVu() {
        try {
            Date tuNgay = getDateFromView(view.getDateTuNgay());
            Date denNgay = getDateFromView(view.getDateDenNgay());
            
            List<Object[]> listData = dichVuDao.getThongKeDichVu(tuNgay, denNgay);
            int totalSuDung = 0; double totalDoanhThu = 0, doanhThuMax = 0;

            view.getTableModel().setRowCount(0);
            if (listData != null) {
                for (Object[] row : listData) {
                    double doanhThu = ((Number) row[4]).doubleValue();
                    totalSuDung += ((Number) row[3]).intValue();
                    totalDoanhThu += doanhThu;
                    if (doanhThu > doanhThuMax) doanhThuMax = doanhThu;

                    Object[] displayRow = row.clone();
                    displayRow[2] = formatCurrency(((Number) row[2]).doubleValue());
                    displayRow[4] = formatCurrency(doanhThu);
                    view.getTableModel().addRow(displayRow);
                }
            }

            view.setCardValues(String.valueOf(totalSuDung), formatCurrency(totalDoanhThu), formatCurrency(doanhThuMax));
            updateChartByService(listData);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadThongKeHoaDon() {
        try {
            Date tuNgay = getDateFromView(view.getDateTuNgay());
            Date denNgay = getDateFromView(view.getDateDenNgay());
            
            List<Object[]> listData = hoaDonDao.getThongKeHoaDon(tuNgay, denNgay);
            double totalTienPhong = 0, totalTienDichVu = 0;

            view.getTableModel().setRowCount(0);
            if (listData != null) {
                for (Object[] row : listData) {
                    double tienPhong = row[4] != null ? ((Number) row[4]).doubleValue() : 0;
                    double tienDichVu = row[5] != null ? ((Number) row[5]).doubleValue() : 0;
                    
                    totalTienPhong += tienPhong; totalTienDichVu += tienDichVu;

                    Object[] displayRow = row.clone();
                    displayRow[4] = formatCurrency(tienPhong);
                    displayRow[5] = formatCurrency(tienDichVu);
                    displayRow[6] = row[6]; // tenThue - already String
                    displayRow[7] = formatCurrency(row[7] != null ? ((Number) row[7]).doubleValue() : 0);
                    view.getTableModel().addRow(displayRow);
                }
            }

            view.setCardValues(String.valueOf(listData != null ? listData.size() : 0), formatCurrency(totalTienPhong), formatCurrency(totalTienDichVu));
            updateChartByInvoice(listData);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadThongKeDonDatPhong() {
        try {
            List<Object[]> listData = donDatPhongDao.getThongKeDonDatPhong(getDateFromView(view.getDateTuNgay()), getDateFromView(view.getDateDenNgay()));
            int totalDon = 0, totalHoanThanh = 0; double totalTienCoc = 0;

            view.getTableModel().setRowCount(0);
            if (listData != null) {
                for (Object[] row : listData) {
                    totalDon += ((Number) row[1]).intValue();
                    totalHoanThanh += ((Number) row[4]).intValue();
                    double tienCoc = row[5] != null ? ((Number) row[5]).doubleValue() : 0;
                    totalTienCoc += tienCoc;

                    Object[] displayRow = row.clone();
                    displayRow[5] = formatCurrency(tienCoc);
                    view.getTableModel().addRow(displayRow);
                }
            }

            view.setCardValues(String.valueOf(totalDon), String.valueOf(totalHoanThanh), formatCurrency(totalTienCoc));
            updateChartByBookingOrder(listData);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadThongKeDonDatPhong(Date tuNgay, Date denNgay) {
        try {
            List<Object[]> listData = donDatPhongDao.getThongKeDonDatPhong(tuNgay, denNgay);
            int totalDon = 0, totalHoanThanh = 0; double totalTienCoc = 0;

            view.getTableModel().setRowCount(0);
            if (listData != null) {
                for (Object[] row : listData) {
                    totalDon += ((Number) row[1]).intValue();
                    totalHoanThanh += ((Number) row[4]).intValue();
                    double tienCoc = row[5] != null ? ((Number) row[5]).doubleValue() : 0;
                    totalTienCoc += tienCoc;

                    Object[] displayRow = row.clone();
                    displayRow[5] = formatCurrency(tienCoc);
                    view.getTableModel().addRow(displayRow);
                }
            }

            view.setCardValues(String.valueOf(totalDon), String.valueOf(totalHoanThanh), formatCurrency(totalTienCoc));
            updateChartByBookingOrder(listData);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadThongKeTheoNhanVien() {
        try {
            String caLamViec = (String) view.getCboLoc1().getSelectedItem();
            String thang = (String) view.getCboLoc2().getSelectedItem();
            String viTriCongViec = (String) view.getCboLoc3().getSelectedItem();
          

            if ("Tất cả".equals(caLamViec)) caLamViec = null;
            if ("Tất cả".equals(thang)) thang = null;
            if ("Tất cả".equals(viTriCongViec)) viTriCongViec = null;
          

            List<Object[]> listData = nhanVienDao.getThongKeTheoNhanVien(getDateFromView(view.getDateTuNgay()), getDateFromView(view.getDateDenNgay()), caLamViec, thang, viTriCongViec);
            int totalHoaDon = 0; double totalDoanhThu = 0;

            view.getTableModel().setRowCount(0);
            if (listData != null) {
                for (Object[] row : listData) {
                    totalHoaDon += row[7] != null ? ((Number) row[7]).intValue() : 0;
                    double doanhThu = row[8] != null ? ((Number) row[8]).doubleValue() : 0.0;
                    totalDoanhThu += doanhThu;

                    Object[] displayRow = row.clone();
                    displayRow[8] = formatCurrency(doanhThu);
                    view.getTableModel().addRow(displayRow);
                }
            }

            view.setCardValues(String.valueOf(listData != null ? listData.size() : 0), String.valueOf(totalHoaDon), formatCurrency(totalDoanhThu));
            updateChartByStaff(listData);
        } catch (Exception e) { e.printStackTrace(); }
    }

    // =======================================================
    // CÁC HÀM VẼ BIỂU ĐỒ (UPDATE CHART BY ...)
    // =======================================================

    private void updateChartByTime(List<Object[]> listData) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (listData != null) {
            for (Object[] row : listData) {
                String ngayStr = row[0].toString().substring(5); // Lấy MM-DD
                dataset.addValue(((Number) row[4]).doubleValue(), "Doanh thu", ngayStr);
            }
        }
        view.updateChart(dataset, "Doanh thu theo thời gian", "Ngày", "Doanh thu (VNĐ)");
    }

    private void updateChartByCustomer(List<Object[]> listData) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (listData != null) {
            for (int i = 0; i < Math.min(10, listData.size()); i++) {
                Object[] row = listData.get(i);
                dataset.addValue(((Number) row[4]).doubleValue(), "Chi tiêu", (String) row[1]); // Cột 1 là Tên KH
            }
        }
        view.updateChart(dataset, "Top 10 Khách hàng chi tiêu cao nhất", "Khách hàng", "Doanh thu (VNĐ)");
    }

    private void updateChartByRoom(List<Object[]> listData) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        if (listData != null && !listData.isEmpty()) {
            for (int i = 0; i < Math.min(10, listData.size()); i++) {
                Object[] row = listData.get(i);
                try {
                    dataset.addValue(((Number) row[4]).doubleValue(), "Doanh thu", (String) row[0]); // Cột 0 là Mã Phòng
                } catch (Exception e) {
                    System.err.println("Lỗi xử lý biểu đồ: " + e.getMessage());
                }
            }
        }
        
        view.updateChart(dataset, "Top 10 Phòng doanh thu cao nhất", "Phòng", "Doanh thu (VNĐ)");
    }

    private void updateChartByCustomerPoint(List<Object[]> listData) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (listData != null) {
            for (int i = 0; i < Math.min(10, listData.size()); i++) {
                Object[] row = listData.get(i);
                dataset.addValue(((Number) row[5]).doubleValue(), "Điểm số", (String) row[2]); // Cột 2 là Tên KH
            }
        }
        view.updateChart(dataset, "Top 10 Khách hàng điểm cao nhất", "Khách hàng", "Điểm tích lũy");
    }

    private void updateChartByBooking(List<Object[]> listData) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (listData != null) {
            for (int i = 0; i < Math.min(10, listData.size()); i++) {
                Object[] row = listData.get(i);
                dataset.addValue(((Number) row[3]).intValue(), "Số lượt đặt", (String) row[0]); // Cột 0 là Mã Phòng
            }
        }
        view.updateChart(dataset, "Top 10 Phòng đặt nhiều nhất", "Mã Phòng", "Số lượt đặt");
    }

    private void updateChartByService(List<Object[]> listData) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (listData != null) {
            for (int i = 0; i < Math.min(10, listData.size()); i++) {
                Object[] row = listData.get(i);
                dataset.addValue(((Number) row[4]).doubleValue(), "Doanh thu", (String) row[1]); // Cột 1 là Tên DV
            }
        }
        view.updateChart(dataset, "Top 10 Dịch vụ hot nhất", "Tên dịch vụ", "Doanh thu (VNĐ)");
    }

    private void updateChartByInvoice(List<Object[]> listData) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (listData != null) {
            for (int i = 0; i < Math.min(20, listData.size()); i++) {
                Object[] row = listData.get(i);
                double tongTien = row[7] != null ? ((Number) row[7]).doubleValue() : 0;
                dataset.addValue(tongTien, "Tổng tiền", (String) row[0]); // Cột 0 là Mã Hóa Đơn
            }
        }
        view.updateChart(dataset, "Top 20 Hóa đơn giá trị nhất", "Mã Hóa Đơn", "Tổng tiền (VNĐ)");
    }

    private void updateChartByBookingOrder(List<Object[]> listData) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (listData != null) {
            for (Object[] row : listData) {
                String ngayStr = row[0].toString().substring(5); // Lấy MM-DD
                dataset.addValue(((Number) row[1]).intValue(), "Số đơn", ngayStr); // Cột 0 là Thời gian
            }
        }
        view.updateChart(dataset, "Thống kê số lượng đơn đặt", "Thời gian", "Số lượng đơn");
    }

    private void updateChartByStaff(List<Object[]> listData) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (listData != null) {
            for (int i = 0; i < Math.min(10, listData.size()); i++) {
                Object[] row = listData.get(i);
                int soHoaDon = row[7] != null ? ((Number) row[7]).intValue() : 0; 
                dataset.addValue(soHoaDon, "Số hóa đơn", (String) row[1]); // Cột 1 là Mã NV
            }
        }
        view.updateChart(dataset, "Top 10 Nhân viên", "Mã Nhân Viên", "Số hóa đơn");
    }

    // =======================================================
    // CÁC HÀM TIỆN ÍCH KHÁC
    // =======================================================

    private Date getDateFromView(org.jdatepicker.JDatePicker datePicker) {
        if (datePicker.getModel().getValue() != null) {
            java.util.Date javaDate = (java.util.Date) datePicker.getModel().getValue();
            return new Date(javaDate.getTime());
        }
        return null;
    }

    private String formatCurrency(double amount) {
        return String.format("%,.0f VNĐ", amount);
    }

    private boolean kiemTraKhoangNgay(Date tuNgay, Date denNgay) {
        if (tuNgay != null && denNgay != null && tuNgay.after(denNgay)) {
            JOptionPane.showMessageDialog(null, "Vui lòng chọn ngày hợp lệ (Từ ngày phải trước Đến ngày)", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
    
    private void lamMoiBoLoc() {
        dangKhoaSuKien = true;

        clearDatePicker(view.getDateTuNgay());
        clearDatePicker(view.getDateDenNgay());

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
        fileChooser.setSelectedFile(new File("thong_ke_" + loaiThongKe.name().toLowerCase() + ".csv"));

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

            String filePath = folderPath + "/thong_ke_" 
                    + loaiThongKe.name().toLowerCase() + ".pdf";

            Document document = new Document(PageSize.A4);

            PdfWriter.getInstance(document, new FileOutputStream(filePath));

            document.open();

            // Title
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
            Paragraph title = new Paragraph(
                    "THỐNG KÊ - " + loaiThongKe.getTenHienThi(),
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
    
    

}