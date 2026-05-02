package controller;

import org.jfree.data.category.DefaultCategoryDataset;

import gui.QLThongke;
import dao.*;
import entity.LoaiThongKe;

import java.sql.Date;
import java.util.ArrayList;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class QLThongKeController {
    private QLThongke view;
    private HoaDon_Dao hoaDonDao;
    private KhachHang_Dao khachHangDao;
    private Phong_Dao phongDao;
    private DichVu_Dao dichVuDao;
    private NhanVien_Dao nhanVienDao;
    private DonDatPhong_Dao donDatPhongDao;
    
    private NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    public QLThongKeController(QLThongke view) {
        this.view = view;
        this.hoaDonDao = new HoaDon_Dao();
        this.khachHangDao = new KhachHang_Dao();
        this.phongDao = new Phong_Dao();
        this.dichVuDao = new DichVu_Dao();
        this.nhanVienDao = new NhanVien_Dao();
        this.donDatPhongDao = new DonDatPhong_Dao();
        
        initEventListeners();
        loadInitialData();
    }

    private void initEventListeners() {
        view.getBtnLamMoi().addActionListener(e -> {
            view.getTxtTuKhoa().setText("");
            if(view.getCboLoc1().getItemCount() > 0) view.getCboLoc1().setSelectedIndex(0);
            if(view.getCboLoc2().getItemCount() > 0) view.getCboLoc2().setSelectedIndex(0);
            if(view.getCboLoc3().getItemCount() > 0) view.getCboLoc3().setSelectedIndex(0);
            view.getDateTuNgay().getModel().setValue(null);
            view.getDateDenNgay().getModel().setValue(null);
            loadData();
        });
        view.getBtnXuatExcel().addActionListener(e -> xuatExcel());
        view.getBtnInPdf().addActionListener(e -> inPdf());

        view.getTxtTuKhoa().getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { loadData(); }
            @Override public void removeUpdate(DocumentEvent e) { loadData(); }
            @Override public void changedUpdate(DocumentEvent e) { loadData(); }
        });

        ActionListener comboListener = e -> loadData();
        view.getCboLoc1().addActionListener(comboListener);
        view.getCboLoc2().addActionListener(comboListener);
        view.getCboLoc3().addActionListener(comboListener);

        ChangeListener dateListener = e -> loadData();
        view.getDateTuNgay().getModel().addChangeListener(dateListener);
        view.getDateDenNgay().getModel().addChangeListener(dateListener);
    }

    private void loadInitialData() {
        loadData();
    }

    public void loadData() {
        if (view.getLoaiThongKe() == null) return;
        
        switch (view.getLoaiThongKe()) {
            case DOANH_THU_THEO_THOI_GIAN: loadDoanhThuTheoThoiGian(); break;
            case DOANH_THU_THEO_KHACH_HANG: loadDoanhThuTheoKhachHang(); break;
            case DOANH_THU_THEO_PHONG: loadDoanhThuTheoPhong(); break;
            case KHACH_HANG_DIEM_CAO_NHAT: loadKhachHangDiemCaoNhat(); break;
            case PHONG_DAT_NHIEU_NHAT: loadPhongDatNhieuNhat(); break;
            case THONG_KE_DICH_VU: loadThongKeDichVu(); break;
            case THONG_KE_HOA_DON: loadThongKeHoaDon(); break;
            case THONG_KE_DON_DAT_PHONG: loadThongKeDonDatPhong(); break;
            case THONG_KE_THEO_NHAN_VIEN: loadThongKeTheoNhanVien(); break;
        }
    }

    // =======================================================
    // CÁC HÀM XỬ LÝ LOAD DỮ LIỆU
    // =======================================================

    private void loadDoanhThuTheoThoiGian() {
        try {
            Date tuNgay = getDateFromView(view.getDateTuNgay());
            Date denNgay = getDateFromView(view.getDateDenNgay());
            String tuKhoa = view.getTxtTuKhoa().getText().trim();

            if (tuNgay == null || denNgay == null) {
                denNgay = new Date(System.currentTimeMillis());
                tuNgay = new Date(System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000));
            }
            
            List<Object[]> rawData = hoaDonDao.getDoanhThuTheoThoiGianChiTiet(tuNgay, denNgay);
            List<Object[]> listData = new ArrayList<>();
            if (rawData != null) {
                for (Object[] row : rawData) {
                    if (containsKeyword(tuKhoa, row[0])) {
                        listData.add(row);
                    }
                }
            }
            
            int totalHoaDon = 0; 
            double totalDoanhThu = 0, doanhThuCaoNhat = 0; 
            String ngayMax = "";

            view.getTableModel().setRowCount(0);
            if (!listData.isEmpty()) {
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
                view.setCardValues(String.valueOf(totalHoaDon), formatCurrency(totalDoanhThu), ngayMax.isEmpty() ? "N/A" : ngayMax);
                updateChartByTime(listData);
            } else {
                view.setCardValues("0", "0 VNĐ", "N/A");
                view.getTableModel().setRowCount(0);
                updateChartByTime(null);
            }
        } catch (Exception e) { 
            String errorMsg = "Lỗi load doanh thu: " + e.getMessage();
            System.err.println(errorMsg);
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, errorMsg, "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadDoanhThuTheoKhachHang() {
        try {
            String tuKhoa = view.getTxtTuKhoa().getText().trim();
            String loaiKH = getComboValue(view.getCboLoc1());
            List<Object[]> rawData = hoaDonDao.getDoanhThuTheoKhachHang(getDateFromView(view.getDateTuNgay()), getDateFromView(view.getDateDenNgay()));
            List<Object[]> listData = new ArrayList<>();
            int totalLuotLuuTru = 0; double chiTieuCaoNhat = 0;

            view.getTableModel().setRowCount(0);
            if (rawData != null) {
                for (Object[] row : rawData) {
                    if (!matchesExactFilter(loaiKH, row[2])) continue;
                    if (!containsKeyword(tuKhoa, row[0], row[1], row[2])) continue;
                    listData.add(row);

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
            String tuKhoa = view.getTxtTuKhoa().getText().trim();
            String loaiPhong = getComboValue(view.getCboLoc1());
            List<Object[]> rawData = hoaDonDao.getDoanhThuTheoPhong(getDateFromView(view.getDateTuNgay()), getDateFromView(view.getDateDenNgay()));
            List<Object[]> listData = new ArrayList<>();
            int totalLuotThue = 0; double totalDoanhThu = 0, doanhThuMax = 0;

            view.getTableModel().setRowCount(0);
            if (rawData != null) {
                for (Object[] row : rawData) {
                    if (!matchesExactFilter(loaiPhong, row[1])) continue;
                    if (!containsKeyword(tuKhoa, row[0], row[1])) continue;
                    listData.add(row);

                    double doanhThu = ((Number) row[4]).doubleValue();
                    totalLuotThue += ((Number) row[2]).intValue();
                    totalDoanhThu += doanhThu;
                    if (doanhThu > doanhThuMax) doanhThuMax = doanhThu;

                    Object[] displayRow = row.clone();
                    displayRow[4] = formatCurrency(doanhThu);
                    view.getTableModel().addRow(displayRow);
                }
            }

            view.setCardValues(String.valueOf(totalLuotThue), formatCurrency(totalDoanhThu), formatCurrency(doanhThuMax));
            updateChartByRoom(listData);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadKhachHangDiemCaoNhat() {
        try {
            String tuKhoa = view.getTxtTuKhoa().getText().trim();
            String loaiKH = getComboValue(view.getCboLoc1());
            List<Object[]> rawData = hoaDonDao.getKhachHangDiemCaoNhat();
            List<Object[]> listData = new ArrayList<>();
            double tongDiem = 0, diemCaoNhat = 0;

            view.getTableModel().setRowCount(0);
            if (rawData != null) {
                for (Object[] row : rawData) {
                    if (!matchesExactFilter(loaiKH, row[4])) continue;
                    if (!containsKeyword(tuKhoa, row[1], row[2], row[3], row[4])) continue;
                    listData.add(row);
                }
            }

            listData = resequenceRanks(listData);
            for (Object[] row : listData) {
                    double diem = ((Number) row[5]).doubleValue();
                    tongDiem += diem;
                    if (diem > diemCaoNhat) diemCaoNhat = diem;
                    view.getTableModel().addRow(row);
            }

            int size = listData != null ? listData.size() : 0;
            view.setCardValues(String.valueOf(size), String.format("%.0f Điểm", diemCaoNhat), String.format("%.1f Điểm/Khách", size > 0 ? tongDiem / size : 0));
            updateChartByCustomerPoint(listData);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadPhongDatNhieuNhat() {
        try {
            String tuKhoa = view.getTxtTuKhoa().getText().trim();
            String loaiPhong = getComboValue(view.getCboLoc1());
            String trangThai = getComboValue(view.getCboLoc2());
            List<Object[]> rawData = hoaDonDao.getPhongDatNhieuNhat(getDateFromView(view.getDateTuNgay()), getDateFromView(view.getDateDenNgay()));
            List<Object[]> listData = new ArrayList<>();
            int totalDatPhong = 0, totalSoNgay = 0;

            view.getTableModel().setRowCount(0);
            if (rawData != null) {
                for (Object[] row : rawData) {
                    if (!matchesExactFilter(loaiPhong, row[2])) continue;
                    if (!matchesExactFilter(trangThai, row.length > 5 ? row[5] : null)) continue;
                    if (!containsKeyword(tuKhoa, row[1], row[2], row.length > 5 ? row[5] : null)) continue;
                    listData.add(row);
                }
            }

            listData = resequenceRanks(listData);
            for (Object[] row : listData) {
                totalDatPhong += ((Number) row[3]).intValue();
                totalSoNgay += ((Number) row[4]).intValue();
                view.getTableModel().addRow(row);
            }

            String topPhong = (listData != null && !listData.isEmpty()) ? (String) listData.get(0)[1] : "N/A";
            view.setCardValues(String.valueOf(totalDatPhong), String.valueOf(totalSoNgay), topPhong);
            updateChartByBooking(listData);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadThongKeDichVu() {
        try {
            String tuKhoa = view.getTxtTuKhoa().getText().trim();
            List<Object[]> rawData = dichVuDao.getThongKeDichVu(getDateFromView(view.getDateTuNgay()), getDateFromView(view.getDateDenNgay()));
            List<Object[]> listData = new ArrayList<>();
            int totalSuDung = 0; double totalDoanhThu = 0, doanhThuMax = 0;

            view.getTableModel().setRowCount(0);
            if (rawData != null) {
                for (Object[] row : rawData) {
                    if (!containsKeyword(tuKhoa, row[0], row[1])) continue;
                    listData.add(row);

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
            String giaTriHoaDon = getComboValue(view.getCboLoc1());
            List<Object[]> rawData = hoaDonDao.getThongKeHoaDon(getDateFromView(view.getDateTuNgay()), getDateFromView(view.getDateDenNgay()), view.getTxtTuKhoa().getText());
            List<Object[]> listData = new ArrayList<>();
            double totalTienPhong = 0, totalTienDichVu = 0;

            view.getTableModel().setRowCount(0);
            if (rawData != null) {
                for (Object[] row : rawData) {
                    double tongTienHoaDon = row[7] != null ? ((Number) row[7]).doubleValue() : 0;
                    if (!matchesAmountRange(giaTriHoaDon, tongTienHoaDon)) continue;
                    listData.add(row);

                    double tienPhong = row[4] != null ? ((Number) row[4]).doubleValue() : 0;
                    double tienDichVu = row[5] != null ? ((Number) row[5]).doubleValue() : 0;
                    
                    totalTienPhong += tienPhong; totalTienDichVu += tienDichVu;

                    Object[] displayRow = row.clone();
                    displayRow[4] = formatCurrency(tienPhong);
                    displayRow[5] = formatCurrency(tienDichVu);
                    displayRow[6] = row[6] == null ? "" : row[6].toString();
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
            String tuKhoa = view.getTxtTuKhoa().getText().trim();
            String tinhTrang = getComboValue(view.getCboLoc1());
            if ("Tất cả".equals(tinhTrang)) tinhTrang = null;

            List<Object[]> rawData = donDatPhongDao.getThongKeDonDatPhong(
                    getDateFromView(view.getDateTuNgay()),
                    getDateFromView(view.getDateDenNgay()),
                    tinhTrang);
            List<Object[]> listData = new ArrayList<>();
            int totalDon = 0, totalHoanThanh = 0; double totalTienCoc = 0;

            view.getTableModel().setRowCount(0);
            if (rawData != null) {
                for (Object[] row : rawData) {
                    if (!containsKeyword(tuKhoa, row[0])) continue;
                    listData.add(row);

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
            String tuKhoa = view.getTxtTuKhoa().getText().trim();

            if ("Tất cả".equals(caLamViec)) caLamViec = null;
            if ("Tất cả".equals(thang)) thang = null;
            if ("Tất cả".equals(viTriCongViec)) viTriCongViec = null;
            if (tuKhoa.isEmpty()) tuKhoa = null;

            List<Object[]> listData = nhanVienDao.getThongKeTheoNhanVien(getDateFromView(view.getDateTuNgay()), getDateFromView(view.getDateDenNgay()), tuKhoa, caLamViec, thang, viTriCongViec);
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
        if (listData != null) {
            for (int i = 0; i < Math.min(10, listData.size()); i++) {
                Object[] row = listData.get(i);
                dataset.addValue(((Number) row[4]).doubleValue(), "Doanh thu", (String) row[0]); // Cột 0 là Mã Phòng
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
                dataset.addValue(((Number) row[3]).intValue(), "Số lượt đặt", (String) row[1]);
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
                dataset.addValue(((Number) row[1]).intValue(), "Số đơn", (String) row[0]); // Cột 0 là Thời gian
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

    private void xuatExcel() {
        TableExportHelper.exportCsv(
                view,
                view.getTableModel(),
                "thong_ke_" + view.getLoaiThongKe().name().toLowerCase() + ".csv",
                "Xuất thống kê thành công!");
    }

    private void inPdf() {
        TableExportHelper.printTable(
                view,
                view.getTblBaoBieu(),
                "Thống kê " + view.getLoaiThongKe().getTenHienThi(),
                "thống kê");
    }

    private String getComboValue(javax.swing.JComboBox<String> comboBox) {
        Object selected = comboBox.getSelectedItem();
        return selected == null ? "Tất cả" : selected.toString();
    }

    private boolean matchesExactFilter(String selectedValue, Object actualValue) {
        if (selectedValue == null || selectedValue.isBlank() || "Tất cả".equalsIgnoreCase(selectedValue)) {
            return true;
        }

        return actualValue != null && selectedValue.equalsIgnoreCase(actualValue.toString());
    }

    private boolean matchesAmountRange(String selectedRange, double amount) {
        if (selectedRange == null || selectedRange.isBlank() || "Tất cả".equalsIgnoreCase(selectedRange)) {
            return true;
        }

        if ("Dưới 1 triệu".equalsIgnoreCase(selectedRange)) {
            return amount < 1_000_000;
        }

        if ("Từ 1 - 3 triệu".equalsIgnoreCase(selectedRange)) {
            return amount >= 1_000_000 && amount <= 3_000_000;
        }

        if ("Trên 3 triệu".equalsIgnoreCase(selectedRange)) {
            return amount > 3_000_000;
        }

        return true;
    }

    private boolean containsKeyword(String keyword, Object... values) {
        if (keyword == null || keyword.isBlank()) {
            return true;
        }

        String normalizedKeyword = keyword.trim().toLowerCase();
        for (Object value : values) {
            if (value != null && value.toString().toLowerCase().contains(normalizedKeyword)) {
                return true;
            }
        }
        return false;
    }

    private List<Object[]> resequenceRanks(List<Object[]> source) {
        List<Object[]> resequenced = new ArrayList<>();
        for (int i = 0; i < source.size(); i++) {
            Object[] row = source.get(i).clone();
            row[0] = i + 1;
            resequenced.add(row);
        }
        return resequenced;
    }

    private String formatCurrency(double amount) {
        return String.format("%,.0f VNĐ", amount);
    }
}
