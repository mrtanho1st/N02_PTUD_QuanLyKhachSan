package controller;

import java.sql.Date;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import dao.HoaDon_Dao;
import dao.Thue_Dao;
import entity.HoaDon;
import gui.QLHoaDon;

public class QLHoaDonController {

    private final QLHoaDon view;
    private final HoaDon_Dao hoaDonDao;
    private final Thue_Dao thueDao;
    private boolean dangNapCombo = false;

    private final NumberFormat moneyFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public QLHoaDonController(QLHoaDon view) {
        this.view = view;
        this.hoaDonDao = new HoaDon_Dao();
        this.thueDao = new Thue_Dao();

        loadComboThue();
        registerEvents();
        loadData();
    }

    private void registerEvents() {
        DocumentListener searchListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                loadData();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                loadData();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                loadData();
            }
        };

        view.getTxtTimMaHD().getDocument().addDocumentListener(searchListener);
        view.getTxtTimMaDDP().getDocument().addDocumentListener(searchListener);
        view.getTxtTimKhachHang().getDocument().addDocumentListener(searchListener);
        view.getTxtTimNhanVien().getDocument().addDocumentListener(searchListener);
        view.getTxtTimTongTien().getDocument().addDocumentListener(searchListener);

        view.getCboLocThue().addActionListener(e -> {
            if (!dangNapCombo) {
                loadData();
            }
        });
        view.getDateTuNgay().getModel().addChangeListener(e -> loadData());
        view.getDateDenNgay().getModel().addChangeListener(e -> loadData());

        view.getBtnLamMoiTim().addActionListener(e -> {
            view.clearSearchForm();
            loadData();
        });
    }

    public void loadData() {
        Date tuNgay = toSqlDate(view.getTuNgayValue());
        Date denNgay = toSqlDate(view.getDenNgayValue());

        String maHD = view.getTxtTimMaHD().getText().trim();
        String maDDP = view.getTxtTimMaDDP().getText().trim();
        String khachHang = view.getTxtTimKhachHang().getText().trim();
        String nhanVien = view.getTxtTimNhanVien().getText().trim();

        String thue = view.getCboLocThue().getSelectedItem() == null
                ? "Tất cả"
                : view.getCboLocThue().getSelectedItem().toString();

        if (!"Tất cả".equalsIgnoreCase(thue) && thue.contains(" - ")) {
            thue = thue.substring(0, thue.indexOf(" - ")).trim();
        }

        Double tongTien = parseDoubleOrNull(view.getTxtTimTongTien().getText().trim());

        List<HoaDon> ds = hoaDonDao.searchQuanLyHoaDon(
                tuNgay,
                denNgay,
                maHD,
                maDDP,
                khachHang,
                nhanVien,
                tongTien,
                thue
        );

        DefaultTableModel model = view.getTableModel();
        model.setRowCount(0);

        for (HoaDon hd : ds) {
            model.addRow(new Object[] {
                    hd.getMaHD(),
                    hd.getMaDDP(),
                    hd.getTenKH(),
                    hd.getTenNV(),
                    hd.getNgayLap() == null ? "" : dateFormat.format(hd.getNgayLap()),
                    hd.getMaThue(),
                    moneyFormat.format(hd.getTongTien()) + " VNĐ"
            });
        }
    }
    private void loadComboThue() {
        dangNapCombo = true;
        view.setDanhSachThue(thueDao.findAllThueForCombo());
        dangNapCombo = false;
    }

    private Date toSqlDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }

        return Date.valueOf(localDate);
    }

    private Double parseDoubleOrNull(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }

        try {
            String clean = text
                    .replace("VNĐ", "")
                    .replace(".", "")
                    .replace(",", "")
                    .trim();

            return Double.parseDouble(clean);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}