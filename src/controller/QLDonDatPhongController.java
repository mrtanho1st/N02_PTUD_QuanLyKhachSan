package controller;

import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import dao.CheckInCheckOut_Dao;
import dao.DichVu_Dao;
import dao.DonDatPhong_Dao;
import dao.Phong_Dao;
import entity.DichVu;
import entity.DonDatPhong;
import entity.Phong;
import gui.QLDonDatPhong;

public class QLDonDatPhongController {

    private final QLDonDatPhong view;
    private final DonDatPhong_Dao donDatPhongDao;
    private final Phong_Dao phongDao;
    private final DichVu_Dao dichVuDao;
    

    public QLDonDatPhongController(QLDonDatPhong view) {
        this.view = view;
        this.donDatPhongDao = new DonDatPhong_Dao();
        this.phongDao = new Phong_Dao();
        this.dichVuDao = new DichVu_Dao();
        

        init();
    }

    private void init() {
        loadDonDatPhong();
        loadTatCaDichVuLenBang();
        loadPhongTrongLenCombo();
        addEvents();
    }

    private void addEvents() {
        view.getBtnLamMoiTim().addActionListener(e -> lamMoiTimKiem());

        view.getBtnLamMoiForm().addActionListener(e -> {
            view.clearForm();
            loadPhongTrongLenCombo();
            view.resetDichVuSelection();
        });

        view.getBtnLuu().addActionListener(e -> luuThayDoi());

        view.getBtnHuyDon().addActionListener(e -> huyDonDatPhong());

        view.getTblDonDatPhong().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && view.getTblDonDatPhong().getSelectedRow() >= 0) {
            	fillFormFromSelectedRow();

            	String maDDP = view.getTxtMaDon().getText().trim();

            	loadPhongTrongDonDangChon();
            	loadDichVuTrongDon(maDDP);
            	loadPhongCanDoiCombo();

            	view.resetDichVuSelection();
            }
        });

        view.getTblPhongTrongDon().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && view.getTblPhongTrongDon().getSelectedRow() >= 0) {
                fillPhongDangChonVaoCombo();
            }
        });

        view.getCboThaoTacPhong().addActionListener(e -> capNhatTrangThaiComboPhong());

        DocumentListener searchListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                timKiemTuDong();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                timKiemTuDong();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                timKiemTuDong();
            }
        };

        view.getTxtTimMaDon().getDocument().addDocumentListener(searchListener);
        view.getTxtTimKhachHang().getDocument().addDocumentListener(searchListener);
        view.getCboLocTrangThai().addActionListener(e -> timKiemTuDong());
        
        view.addPropertyChangeListener("xacNhanThemDichVu", e -> {
            themDichVuVaCapNhatBang();
        });
        view.addPropertyChangeListener("xoaDichVuTrongDon", e -> {
            xoaDichVuDangChon();
        });
    }
    
    private void xoaDichVuDangChon() {
        int row = view.getTblDichVuTrongDon().getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn dịch vụ cần xóa.");
            return;
        }

        String maDDP = view.getTxtMaDon().getText().trim();

        Object maPhongObj = view.getTblDichVuTrongDon().getValueAt(row, 0);
        Object maDVObj = view.getTblDichVuTrongDon().getValueAt(row, 1);

        String maPhong = maPhongObj == null ? "" : maPhongObj.toString();
        String maDV = maDVObj == null ? "" : maDVObj.toString();

        if (maPhong.isBlank() || maDV.isBlank()) {
            JOptionPane.showMessageDialog(view, "Không lấy được thông tin dịch vụ cần xóa.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                view,
                "Bạn có chắc muốn xóa dịch vụ " + maDV + " khỏi phòng " + maPhong + " không?",
                "Xác nhận xóa dịch vụ",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        boolean ok = donDatPhongDao.xoaDichVuChoPhong(maPhong, maDV);

        if (!ok) {
            JOptionPane.showMessageDialog(view, "Xóa dịch vụ thất bại.");
            return;
        }

        loadDichVuTrongDon(maDDP);

        JOptionPane.showMessageDialog(view, "Xóa dịch vụ thành công.");
    }
    private void themDichVuVaCapNhatBang() {
        String maDon = view.getTxtMaDon().getText().trim();

        if (maDon.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn đơn đặt phòng.");
            return;
        }

        String maPhong = layMaPhongDangChonTrongBang();

        if (maPhong.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn phòng trước khi thêm dịch vụ.");
            return;
        }

        boolean ok = themDichVuDuocChon();

        if (!ok) {
            return;
        }

        loadDichVuTrongDon(maDon);
        view.resetDichVuSelection();
        view.dongDialogDichVu();

//        JOptionPane.showMessageDialog(view, "Thêm dịch vụ thành công.");
    }

    private void loadDonDatPhong() {
        List<DonDatPhong> list =  donDatPhongDao.findDonDangXuLy();
        fillTableDonDatPhong(list);
    }

    private void timKiemTuDong() {
        String maDon = view.getTxtTimMaDon().getText().trim();
        String khachHang = view.getTxtTimKhachHang().getText().trim();
        String trangThai = view.getCboLocTrangThai().getSelectedItem().toString();

        List<DonDatPhong> list = donDatPhongDao.search(
                "",
                khachHang,
                "Tất cả",
                maDon,
                trangThai,
                null,
                null,
                null
        );

        fillTableDonDatPhong(list);
    }

    private void fillTableDonDatPhong(List<DonDatPhong> list) {
        DefaultTableModel model = view.getModelDonDatPhong();
        model.setRowCount(0);

        for (DonDatPhong ddp : list) {
            model.addRow(new Object[] {
                    ddp.getMaDDP(),
                    ddp.getTenKH(),
                    ddp.getMaPhong(),
                    ddp.getNgayNhan(),
                    ddp.getNgayTra(),
                    ddp.getSoNguoiToiDa(),
                    ddp.getTinhTrang()
            });
        }
    }

    private void loadTatCaDichVuLenBang() {
        DefaultTableModel model = view.getModelDichVu();
        model.setRowCount(0);

        List<DichVu> dsDichVu = dichVuDao.findAll();

        for (DichVu dv : dsDichVu) {
            model.addRow(new Object[] {
                    false,
                    dv.getMaDV(),
                    dv.getTenDV(),
                    dv.getGia(),
                    1
            });
        }
    }

    private void loadPhongTrongLenCombo() {
        view.getCboPhongTrong().removeAllItems();

        List<Phong> dsPhong = phongDao.findAll();

        for (Phong phong : dsPhong) {
            if ("Trống".equalsIgnoreCase(phong.getTrangThai())) {
                String item = phong.getMaPhong()
                        + " - " + phong.getLoaiPhong()
                        + " - " + phong.getTrangThai();

                view.getCboPhongTrong().addItem(item);
            }
        }
    }

    private void fillFormFromSelectedRow() {
        int row = view.getTblDonDatPhong().getSelectedRow();

        if (row < 0) {
            return;
        }

        String maDon = getTableValue(row, 0);
        String khachHang = getTableValue(row, 1);
        String ngayNhan = getTableValue(row, 3);
        String ngayTra = getTableValue(row, 4);
        String soNguoi = getTableValue(row, 5);
        String trangThai = getTableValue(row, 6);

        view.getTxtMaDon().setText(maDon);
        view.getTxtKhachHang().setText(khachHang);
        view.getTxtCCCD().setText("");
        view.getTxtSoDienThoai().setText("");
        view.getTxtNgayNhanPhong().setText(ngayNhan);
        view.getTxtNgayTraPhong().setText(ngayTra);
        view.getTxtSoLuongNguoi().setText(soNguoi);
        view.getCboTrangThaiDon().setSelectedItem(chuanHoaTrangThai(trangThai));
    }

    private String getTableValue(int row, int col) {
        Object value = view.getTblDonDatPhong().getValueAt(row, col);
        return value == null ? "" : value.toString();
    }

    private String chuanHoaTrangThai(String trangThai) {
        if ("Đã nhận phòng".equalsIgnoreCase(trangThai)) {
            return "Đã nhận";
        }

        return trangThai;
    }

    private void loadPhongTrongDonDangChon() {
        DefaultTableModel model = view.getModelPhongTrongDon();
        model.setRowCount(0);

        String maDon = view.getTxtMaDon().getText().trim();

        if (maDon.isEmpty()) {
            return;
        }

        List<Object[]> dsPhong = donDatPhongDao.getPhongTrongDon(maDon);

        for (Object[] row : dsPhong) {
            model.addRow(row);
        }
    }

    private void loadPhongCanDoiCombo() {
        view.getCboPhongCanDoi().removeAllItems();

        DefaultTableModel model = view.getModelPhongTrongDon();

        for (int i = 0; i < model.getRowCount(); i++) {
            Object maPhongObj = model.getValueAt(i, 0);
            Object loaiPhongObj = model.getValueAt(i, 1);

            String maPhong = maPhongObj == null ? "" : maPhongObj.toString();
            String loaiPhong = loaiPhongObj == null ? "" : loaiPhongObj.toString();

            if (!maPhong.isBlank()) {
                view.getCboPhongCanDoi().addItem(maPhong + " - " + loaiPhong);
            }
        }
    }

    private void fillPhongDangChonVaoCombo() {
        int row = view.getTblPhongTrongDon().getSelectedRow();

        if (row < 0) {
            return;
        }

        Object maPhongObj = view.getTblPhongTrongDon().getValueAt(row, 0);

        if (maPhongObj == null) {
            return;
        }

        selectComboByMa(view.getCboPhongCanDoi(), maPhongObj.toString());
    }

    private void capNhatTrangThaiComboPhong() {
        String thaoTac = view.getCboThaoTacPhong().getSelectedItem().toString();

        boolean coThaoTac = !"Không thay đổi".equalsIgnoreCase(thaoTac);
        boolean doiPhong = "Đổi phòng".equalsIgnoreCase(thaoTac);

        view.getCboPhongCanDoi().setEnabled(coThaoTac && doiPhong);
        view.getCboPhongTrong().setEnabled(coThaoTac);
    }

    private void luuThayDoi() {
        String maDon = view.getTxtMaDon().getText().trim();

        if (maDon.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn đơn đặt phòng cần lưu.");
            return;
        }

        boolean coThayDoi = false;

        boolean okCapNhat = capNhatThongTinDon();
        if (!okCapNhat) {
            return;
        }
        coThayDoi = true;

        boolean okPhong = xuLyThaoTacPhong();
        if (!okPhong) {
            return;
        }

        boolean okDichVu = themDichVuDuocChon();
        if (!okDichVu) {
            return;
        }

        if (coThayDoi) {
            JOptionPane.showMessageDialog(view, "Lưu thay đổi thành công.");

            String maDDP = view.getTxtMaDon().getText().trim();

            timKiemTuDong();
            loadPhongTrongDonDangChon();
            loadDichVuTrongDon(maDDP);
            loadPhongCanDoiCombo();
            loadTatCaDichVuLenBang();
            loadPhongTrongLenCombo();
            view.resetDichVuSelection();
        }
    }

    private boolean capNhatThongTinDon() {
        String maDon = view.getTxtMaDon().getText().trim();
        String ngayNhan = view.getTxtNgayNhanPhong().getText().trim();
        String ngayTra = view.getTxtNgayTraPhong().getText().trim();
        String soNguoiText = view.getTxtSoLuongNguoi().getText().trim();
        String trangThai = view.getCboTrangThaiDon().getSelectedItem().toString();

        if (ngayNhan.isEmpty() || ngayTra.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Ngày nhận và ngày trả không được để trống.");
            return false;
        }

        int soNguoi;

        try {
            soNguoi = Integer.parseInt(soNguoiText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(view, "Số lượng người phải là số nguyên.");
            return false;
        }

        if (soNguoi <= 0) {
            JOptionPane.showMessageDialog(view, "Số lượng người phải lớn hơn 0.");
            return false;
        }

        boolean ok = donDatPhongDao.capNhatThongTinDon(
                maDon,
                ngayNhan,
                ngayTra,
                soNguoi,
                trangThai
        );

        if (!ok) {
            JOptionPane.showMessageDialog(view, "Cập nhật thông tin đơn thất bại.");
            return false;
        }

        return true;
    }

    private boolean xuLyThaoTacPhong() {
        String thaoTac = view.getCboThaoTacPhong().getSelectedItem().toString();

        if ("Không thay đổi".equalsIgnoreCase(thaoTac)) {
            return true;
        }

        String maDon = view.getTxtMaDon().getText().trim();
        String phongMoi = layMaTuCombo(view.getCboPhongTrong().getSelectedItem());

        if (phongMoi.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn phòng trống.");
            return false;
        }

        if ("Thêm phòng".equalsIgnoreCase(thaoTac)) {
            boolean ok = donDatPhongDao.themPhongVaoDon(maDon, phongMoi);

            if (!ok) {
                JOptionPane.showMessageDialog(view, "Thêm phòng vào đơn thất bại.");
                return false;
            }

            return true;
        }

        if ("Đổi phòng".equalsIgnoreCase(thaoTac)) {
            String phongCu = layMaTuCombo(view.getCboPhongCanDoi().getSelectedItem());

            if (phongCu.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Vui lòng chọn phòng cần đổi.");
                return false;
            }

            if (phongCu.equals(phongMoi)) {
                JOptionPane.showMessageDialog(view, "Phòng mới không được trùng phòng cũ.");
                return false;
            }

            boolean ok = donDatPhongDao.doiPhongTrongDon(maDon, phongCu, phongMoi);

            if (!ok) {
                JOptionPane.showMessageDialog(view, "Đổi phòng thất bại.");
                return false;
            }

            return true;
        }

        return true;
    }

    private boolean themDichVuDuocChon() {
        String maDon = view.getTxtMaDon().getText().trim();
        String maPhong = layMaPhongDangChonTrongBang();

        if (maPhong.isEmpty()) {
            maPhong = layMaTuCombo(view.getCboPhongCanDoi().getSelectedItem());
        }

        DefaultTableModel model = view.getModelDichVu();

        boolean coChonDichVu = false;

        for (int i = 0; i < model.getRowCount(); i++) {
            Object checkedObj = model.getValueAt(i, 0);

            if (!(checkedObj instanceof Boolean) || !((Boolean) checkedObj)) {
                continue;
            }

            coChonDichVu = true;

            String maDV = model.getValueAt(i, 1).toString();

            int soLuong;

            try {
                soLuong = Integer.parseInt(model.getValueAt(i, 4).toString());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(view, "Số lượng dịch vụ ở dòng " + (i + 1) + " không hợp lệ.");
                return false;
            }

            if (soLuong <= 0) {
                JOptionPane.showMessageDialog(view, "Số lượng dịch vụ ở dòng " + (i + 1) + " phải lớn hơn 0.");
                return false;
            }

            if (maPhong.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Vui lòng chọn phòng trong đơn để thêm dịch vụ.");
                return false;
            }
            if (maDon.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Không tìm thấy mã đơn đặt phòng hiện tại.");
                return false;
            }

            boolean ok = donDatPhongDao.themDichVuChoPhong(maDon,maPhong, maDV, soLuong);

            if (!ok) {
                JOptionPane.showMessageDialog(view, "Thêm dịch vụ " + maDV + " thất bại.");
                return false;
            }
        }

        if (!coChonDichVu) {
            return true; // không chọn thì bỏ qua, KHÔNG báo lỗi
        }

        view.resetDichVuSelection();

        String maDDP = view.getTxtMaDon().getText().trim();
        loadDichVuTrongDon(maDDP);

        return true;
    }

    private String layMaPhongDangChonTrongBang() {
        int row = view.getTblPhongTrongDon().getSelectedRow();

        if (row < 0) {
            return "";
        }

        Object value = view.getTblPhongTrongDon().getValueAt(row, 0);
        return value == null ? "" : value.toString();
    }

    private void huyDonDatPhong() {
        String maDon = view.getTxtMaDon().getText().trim();

        if (maDon.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn đơn đặt phòng cần hủy.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                view,
                "Bạn có chắc muốn hủy đơn đặt phòng này không?",
                "Xác nhận hủy đơn",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        boolean ok = donDatPhongDao.huyDonDatPhong(maDon);

        if (ok) {
            JOptionPane.showMessageDialog(view, "Hủy đơn đặt phòng thành công.");
            refreshAfterAction();
        } else {
            JOptionPane.showMessageDialog(view, "Hủy đơn đặt phòng thất bại.");
        }
    }

    private void lamMoiTimKiem() {
        view.getTxtTimMaDon().setText("");
        view.getTxtTimKhachHang().setText("");
        view.getCboLocTrangThai().setSelectedIndex(0);

        loadDonDatPhong();
    }

    
    private void loadDichVuTrongDon(String maDDP) {
        DefaultTableModel model = view.getModelDichVuTrongDon();
        model.setRowCount(0);

        if (maDDP == null || maDDP.isBlank()) {
            return;
        }

        List<Object[]> dsDichVu = donDatPhongDao.getDichVuTrongDon(maDDP);

        for (Object[] row : dsDichVu) {
            model.addRow(row);
        }
    }
    
    private void refreshAfterAction() {
        timKiemTuDong();
        loadTatCaDichVuLenBang();
        loadPhongTrongLenCombo();
        view.clearForm();
    }

    private void selectComboByMa(javax.swing.JComboBox<String> combo, String ma) {
        if (ma == null || ma.isBlank()) {
            return;
        }

        for (int i = 0; i < combo.getItemCount(); i++) {
            String item = combo.getItemAt(i);

            if (item != null && item.startsWith(ma + " ")) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private String layMaTuCombo(Object selectedItem) {
        if (selectedItem == null) {
            return "";
        }

        String text = selectedItem.toString();

        if (text.isBlank()) {
            return "";
        }

        int index = text.indexOf(" - ");

        if (index == -1) {
            return text.trim();
        }

        return text.substring(0, index).trim();
    }
}