package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import entity.DichVu;
import entity.NhanVien;
import entity.Phong;

public class DatPhongDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private static final Color APP_BG = new Color(242, 246, 252);
    private static final Color PANEL_BG = Color.WHITE;
    private static final Color BORDER = new Color(214, 223, 235);
    private static final Color TITLE = new Color(36, 64, 102);
    private static final Color PRIMARY = new Color(91, 168, 217);

    private final Phong phong;
    private final NhanVien nhanVien;
    private final List<DichVu> dsDichVu;

    private JTextField txtTenKH;
    private JTextField txtCCCD;
    private JTextField txtSDT;
    private JTextField txtTienCoc;
    private JCheckBox chkCheckInNgay;

    private JSpinner spnNgayNhan;
    private JSpinner spnNgayTra;

    private JTable tblDichVu;
    private DefaultTableModel modelDichVu;

    private boolean succeeded = false;

    public DatPhongDialog(Frame owner, Phong phong, NhanVien nhanVien, List<DichVu> dsDichVu) {
        super(owner, "Đặt phòng - " + phong.getMaPhong(), true);
        this.phong = phong;
        this.nhanVien = nhanVien;
        this.dsDichVu = dsDichVu;
        initUI();
    }

    private void initUI() {
        setSize(900, 650);
        setMinimumSize(new Dimension(860, 650));
        setLocationRelativeTo(getOwner());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBackground(APP_BG);
        root.setBorder(new EmptyBorder(14, 14, 14, 14));
        setContentPane(root);

        JLabel lblHeader = new JLabel("PHIẾU ĐẶT PHÒNG");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblHeader.setForeground(TITLE);
        root.add(lblHeader, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        center.add(createNhanVienPanel());
        center.add(Box.createVerticalStrut(10));
        center.add(createThongTinPhongPanel());
        center.add(Box.createVerticalStrut(10));
        center.add(createKhachHangPanel());
        center.add(Box.createVerticalStrut(10));
        center.add(createThoiGianPanel());
        center.add(Box.createVerticalStrut(10));
        center.add(createDichVuPanel());
        center.add(Box.createVerticalStrut(10));
        center.add(createThanhToanPanel());

        JScrollPane scroll = new JScrollPane(center);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        root.add(scroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);

        JButton btnDatPhong = createButton("Xác nhận đặt phòng", PRIMARY, Color.BLACK);
        JButton btnHuy = createButton("Hủy", new Color(232, 236, 242), Color.BLACK);

        btnDatPhong.addActionListener(e -> submit());
        btnHuy.addActionListener(e -> dispose());

        bottom.add(btnDatPhong);
        bottom.add(btnHuy);

        root.add(bottom, BorderLayout.SOUTH);
    }

    private JPanel createNhanVienPanel() {
        JPanel panel = createSectionPanel("Nhân viên trực ca");

        addRow(panel, 0, "Mã nhân viên:", createValueLabel(nhanVien.getMaNV()),
                "Họ tên:", createValueLabel(nhanVien.getHoTen()));
        addRow(panel, 1, "Ca làm việc:", createValueLabel(nhanVien.getCaLamViec()),
                "Vị trí:", createValueLabel(nhanVien.getViTriCongViec()));

        return panel;
    }

    private JPanel createThongTinPhongPanel() {
        JPanel panel = createSectionPanel("Thông tin phòng");

        NumberFormat nf = NumberFormat.getNumberInstance(new Locale("vi", "VN"));

        addRow(panel, 0, "Mã phòng:", createValueLabel(phong.getMaPhong()),
                "Loại phòng:", createValueLabel(phong.getLoaiPhong()));
        addRow(panel, 1, "Sức chứa tối đa:", createValueLabel(String.valueOf(phong.getSoNguoiToiDa()) + " người"),
                "Giá phòng:", createValueLabel(nf.format(phong.getGiaPhong()) + " VNĐ"));
        addRow(panel, 2, "Trạng thái:", createValueLabel(phong.getTrangThai()),
                "", new JLabel());

        return panel;
    }

    private JPanel createKhachHangPanel() {
        JPanel panel = createSectionPanel("Thông tin khách hàng");

        txtTenKH = createTextField();
        txtCCCD = createTextField();
        txtSDT = createTextField();

        addRow(panel, 0, "Tên khách hàng:", txtTenKH,
                "CCCD:", txtCCCD);
        addRow(panel, 1, "Số điện thoại:", txtSDT,
                "", new JLabel());

        return panel;
    }

    private JPanel createThoiGianPanel() {
        JPanel panel = createSectionPanel("Thời gian dự kiến check-in / check-out");

        spnNgayNhan = createDateTimeSpinner(new Date());
        spnNgayTra = createDateTimeSpinner(new Date(System.currentTimeMillis() + 2 * 60 * 60 * 1000L));

        addRow(panel, 0, "Check-in dự kiến:", spnNgayNhan,
                "Check-out dự kiến:", spnNgayTra);

        JLabel note = new JLabel("Lưu ý: DB hiện tại của bạn đang dùng kiểu DATE cho ngày nhận/ngày trả.");
        note.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        note.setForeground(new Color(160, 80, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(6, 6, 0, 6);
        panel.add(note, gbc);

        return panel;
    }

    private JPanel createDichVuPanel() {
        JPanel panel = createSectionPanel("Dịch vụ đặt trước");

        String[] cols = {"Chọn", "Mã DV", "Tên dịch vụ", "Đơn giá", "Số lượng"};
        modelDichVu = new DefaultTableModel(cols, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Boolean.class;
                if (columnIndex == 4) return Integer.class;
                return String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0 || column == 4;
            }
        };

        NumberFormat nf = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        for (DichVu dv : dsDichVu) {
            modelDichVu.addRow(new Object[]{
                false,
                dv.getMaDV(),
                dv.getTenDV(),
                nf.format(dv.getGia()) + " VNĐ",
                1
            });
        }

        tblDichVu = new JTable(modelDichVu);
        tblDichVu.setRowHeight(28);
        tblDichVu.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblDichVu.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JScrollPane sp = new JScrollPane(tblDichVu);
        sp.setPreferredSize(new Dimension(760, 180));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 4;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(6, 6, 6, 6);
        panel.add(sp, gbc);

        return panel;
    }

    private JPanel createThanhToanPanel() {
        JPanel panel = createSectionPanel("Xác nhận");

        txtTienCoc = createTextField();
        chkCheckInNgay = new JCheckBox("Khách check-in ngay sau khi tạo đơn");
        chkCheckInNgay.setBackground(PANEL_BG);
        chkCheckInNgay.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        addRow(panel, 0, "Tiền cọc:", txtTienCoc,
                "", chkCheckInNgay);

        return panel;
    }

    private JPanel createSectionPanel(String title) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(PANEL_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(14, 14, 14, 14)
        ));

        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lbl.setForeground(TITLE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 6, 10, 6);
        panel.add(lbl, gbc);

        return panel;
    }

    private void addRow(JPanel panel, int row, String l1, Component c1, String l2, Component c2) {
        int y = row + 1;
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        if (l1 != null && !l1.isEmpty()) {
            gbc.gridx = 0;
            gbc.gridy = y;
            gbc.weightx = 0;
            panel.add(createLabel(l1), gbc);
        }

        gbc.gridx = 1;
        gbc.gridy = y;
        gbc.weightx = 1;
        panel.add(c1, gbc);

        if (l2 != null && !l2.isEmpty()) {
            gbc.gridx = 2;
            gbc.gridy = y;
            gbc.weightx = 0;
            panel.add(createLabel(l2), gbc);
        }

        gbc.gridx = 3;
        gbc.gridy = y;
        gbc.weightx = 1;
        panel.add(c2, gbc);
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return lbl;
    }

    private JLabel createValueLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbl.setOpaque(true);
        lbl.setBackground(new Color(248, 250, 253));
        lbl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(8, 10, 8, 10)
        ));
        return lbl;
    }

    private JTextField createTextField() {
        JTextField txt = new JTextField();
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.setPreferredSize(new Dimension(220, 34));
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(6, 10, 6, 10)
        ));
        return txt;
    }

    private JSpinner createDateTimeSpinner(Date date) {
        SpinnerDateModel model = new SpinnerDateModel(date, null, null, java.util.Calendar.MINUTE);
        JSpinner sp = new JSpinner(model);
        sp.setEditor(new JSpinner.DateEditor(sp, "dd/MM/yyyy HH:mm"));
        sp.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sp.setPreferredSize(new Dimension(220, 34));

        JFormattedTextField tf = ((JSpinner.DefaultEditor) sp.getEditor()).getTextField();
        tf.setHorizontalAlignment(SwingConstants.LEFT);
        return sp;
    }

    private JButton createButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        return btn;
    }

    private void submit() {
        if (txtTenKH.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên khách hàng.");
            return;
        }

        if (txtCCCD.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập CCCD.");
            return;
        }

        if (txtSDT.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại.");
            return;
        }

        Date ngayNhan = getNgayNhan();
        Date ngayTra = getNgayTra();

        if (!ngayTra.after(ngayNhan)) {
            JOptionPane.showMessageDialog(this, "Thời gian check-out phải sau check-in.");
            return;
        }

        String tienCoc = txtTienCoc.getText().trim();
        if (!tienCoc.isEmpty()) {
            try {
                Double.parseDouble(tienCoc);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Tiền cọc phải là số hợp lệ.");
                return;
            }
        }

        for (int i = 0; i < modelDichVu.getRowCount(); i++) {
            Boolean chon = (Boolean) modelDichVu.getValueAt(i, 0);
            Integer soLuong = (Integer) modelDichVu.getValueAt(i, 4);
            if (Boolean.TRUE.equals(chon) && (soLuong == null || soLuong <= 0)) {
                JOptionPane.showMessageDialog(this, "Số lượng dịch vụ phải lớn hơn 0.");
                return;
            }
        }

        succeeded = true;
        dispose();
    }

    public boolean isSucceeded() {
        return succeeded;
    }

    public String getTenKhachHang() {
        return txtTenKH.getText().trim();
    }

    public String getCccd() {
        return txtCCCD.getText().trim();
    }

    public String getSdt() {
        return txtSDT.getText().trim();
    }

    public Date getNgayNhan() {
        return (Date) spnNgayNhan.getValue();
    }

    public Date getNgayTra() {
        return (Date) spnNgayTra.getValue();
    }

    public String getTienCoc() {
        return txtTienCoc.getText().trim();
    }

    public boolean isCheckInNgay() {
        return chkCheckInNgay.isSelected();
    }

    public List<DichVuDatTruoc> getDichVuDaChon() {
        List<DichVuDatTruoc> ds = new ArrayList<>();
        for (int i = 0; i < modelDichVu.getRowCount(); i++) {
            Boolean chon = (Boolean) modelDichVu.getValueAt(i, 0);
            if (Boolean.TRUE.equals(chon)) {
                String maDV = modelDichVu.getValueAt(i, 1).toString();
                String tenDV = modelDichVu.getValueAt(i, 2).toString();
                Integer soLuong = (Integer) modelDichVu.getValueAt(i, 4);
                ds.add(new DichVuDatTruoc(maDV, tenDV, soLuong));
            }
        }
        return ds;
    }

    public static class DichVuDatTruoc {
        private final String maDV;
        private final String tenDV;
        private final int soLuong;

        public DichVuDatTruoc(String maDV, String tenDV, int soLuong) {
            this.maDV = maDV;
            this.tenDV = tenDV;
            this.soLuong = soLuong;
        }

        public String getMaDV() { return maDV; }
        public String getTenDV() { return tenDV; }
        public int getSoLuong() { return soLuong; }
    }
}