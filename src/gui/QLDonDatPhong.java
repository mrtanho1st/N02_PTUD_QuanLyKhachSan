package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.FlowLayout;


import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import controller.QLDonDatPhongController;

public class QLDonDatPhong extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final Color APP_BG = new Color(237, 242, 247);
    private static final Color PANEL_BG = new Color(248, 250, 253);
    private static final Color CARD_BORDER = new Color(210, 220, 235);
    private static final Color TEXT_DARK = new Color(24, 39, 75);
    private static final Color BUTTON_BG = new Color(233, 239, 248);
    private static final Color BUTTON_PRIMARY = new Color(220, 235, 255);
    private static final Color BUTTON_DANGER = new Color(252, 230, 230);

    // Bộ lọc
    private JTextField txtTimMaDon;
    private JTextField txtTimKhachHang;
    private JComboBox<String> cboLocTrangThai;
    private JButton btnLamMoiTim;

    // Bảng đơn đặt phòng
    private JTable tblDonDatPhong;
    private DefaultTableModel modelDonDatPhong;

    // Bảng dịch vụ có sẵn
    private JTable tblDichVu;
    private DefaultTableModel modelDichVu;

    // Form chi tiết đơn
    private JTextField txtMaDon;
    private JTextField txtKhachHang;
    private JTextField txtCCCD;
    private JTextField txtSoDienThoai;
    private JTextField txtNgayNhanPhong;
    private JTextField txtNgayTraPhong;
    private JTextField txtSoLuongNguoi;

    private JComboBox<String> cboTrangThaiDon;

    // Danh sách phòng trong đơn
    private JTable tblPhongTrongDon;
    private DefaultTableModel modelPhongTrongDon;
    
    //Danh sách dịch vụ
    private JTable tblDichVuTrongDon;
    private DefaultTableModel modelDichVuTrongDon;

    // Thao tác phòng
    private JComboBox<String> cboThaoTacPhong;
    private JComboBox<String> cboPhongCanDoi;
    private JComboBox<String> cboPhongTrong;

    // Nút xử lý
    private JButton btnLuu;
    private JButton btnLamMoiForm;
    private JButton btnHuyDon;
    private JButton btnThemDichVu;
    private JButton btnXoaDichVu;
    
    
    private JDialog dialogDichVu;

    public QLDonDatPhong() {
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 16));
        setBackground(APP_BG);
        setBorder(new EmptyBorder(0, 0, 0, 0));

        add(createFilterPanel(), BorderLayout.NORTH);
        add(createBodyPanel(), BorderLayout.CENTER);
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(PANEL_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER),
                new EmptyBorder(16, 18, 16, 18)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lblMaDon = createLabel("Mã đơn:");
        JLabel lblKhachHang = createLabel("Khách hàng:");
        JLabel lblTrangThai = createLabel("Trạng thái:");

        txtTimMaDon = new JTextField();
        txtTimKhachHang = new JTextField();

        cboLocTrangThai = new JComboBox<>(new String[] {
                "Tất cả", "Đã đặt", "Đã nhận","Hoàn thành", "Đã hủy"
        });

        styleTextField(txtTimMaDon);
        styleTextField(txtTimKhachHang);
        styleComboBox(cboLocTrangThai);

        btnLamMoiTim = createButton("Làm mới");

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        panel.add(lblMaDon, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(txtTimMaDon, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        panel.add(lblKhachHang, gbc);

        gbc.gridx = 3;
        gbc.weightx = 1.0;
        panel.add(txtTimKhachHang, gbc);

        gbc.gridx = 4;
        gbc.weightx = 0;
        panel.add(lblTrangThai, gbc);

        gbc.gridx = 5;
        gbc.weightx = 0.8;
        panel.add(cboLocTrangThai, gbc);

        gbc.gridx = 6;
        gbc.weightx = 0;
        panel.add(btnLamMoiTim, gbc);

        return panel;
    }

    private JPanel createBodyPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                createLeftPanel(),
                createDetailPanel());

        splitPane.setDividerLocation(600);
        splitPane.setResizeWeight(0.58);
        splitPane.setBorder(null);
        splitPane.setOpaque(false);
        splitPane.setContinuousLayout(true);

        panel.add(splitPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createLeftPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);

        wrapper.add(createDonDatPhongTablePanel(), BorderLayout.CENTER);

        initDichVuTable();
        
        return wrapper;
    }
    
    private void initDichVuTable() {
        if (modelDichVu != null && tblDichVu != null) {
            return;
        }

        String[] columns = {
                "Chọn", "Mã DV", "Tên dịch vụ", "Đơn giá", "Số lượng"
        };

        modelDichVu = new DefaultTableModel(new Object[][] {}, columns) {
            private static final long serialVersionUID = 1L;

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Boolean.class;
                }

                if (columnIndex == 4) {
                    return Integer.class;
                }

                return String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0 || column == 4;
            }
        };

        tblDichVu = new JTable(modelDichVu);
        styleTable(tblDichVu);
        tblDichVu.setRowHeight(30);

        tblDichVu.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblDichVu.getColumnModel().getColumn(1).setPreferredWidth(80);
        tblDichVu.getColumnModel().getColumn(2).setPreferredWidth(180);
        tblDichVu.getColumnModel().getColumn(3).setPreferredWidth(100);
        tblDichVu.getColumnModel().getColumn(4).setPreferredWidth(80);
    }

    private JPanel createDonDatPhongTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(PANEL_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER),
                new EmptyBorder(16, 16, 16, 16)));

        JLabel lblTitle = new JLabel("Danh sách đơn đặt phòng");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(TEXT_DARK);

        String[] columns = {
                "Mã ĐĐP", "Khách hàng", "Phòng", "Ngày nhận", "Ngày trả", "Số người", "Trạng thái"
        };

        modelDonDatPhong = new DefaultTableModel(new Object[][] {}, columns) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblDonDatPhong = new JTable(modelDonDatPhong);
        styleTable(tblDonDatPhong);

        JScrollPane scrollPane = new JScrollPane(tblDonDatPhong);
        scrollPane.setBorder(BorderFactory.createLineBorder(CARD_BORDER));

        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createDichVuTablePanel() {
        initDichVuTable();

        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(PANEL_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER),
                new EmptyBorder(16, 16, 16, 16)));

        JLabel lblTitle = new JLabel("Chọn dịch vụ");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(TEXT_DARK);

        JScrollPane scrollPane = new JScrollPane(tblDichVu);
        scrollPane.setBorder(BorderFactory.createLineBorder(CARD_BORDER));

        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createDetailPanel() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 12));
        wrapper.setBackground(APP_BG);
        wrapper.setBorder(new EmptyBorder(0, 16, 0, 0));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(PANEL_BG);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER),
                new EmptyBorder(16, 16, 16, 16)));

        JScrollPane formScroll = new JScrollPane(formPanel);
        formScroll.setBorder(null);
        formScroll.getViewport().setBackground(APP_BG);
        formScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        formScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        formScroll.getVerticalScrollBar().setUnitIncrement(16);

        JLabel lblTitle = new JLabel("Thông tin đơn đặt phòng");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(TEXT_DARK);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        txtMaDon = new JTextField();
        txtKhachHang = new JTextField();
        txtCCCD = new JTextField();
        txtSoDienThoai = new JTextField();
        txtNgayNhanPhong = new JTextField();
        txtNgayTraPhong = new JTextField();
        txtSoLuongNguoi = new JTextField();

        cboTrangThaiDon = new JComboBox<>(new String[] {
                "Đã đặt", "Đã nhận", "Đã hủy"
        });

        styleTextField(txtMaDon);
        styleTextField(txtKhachHang);
        styleTextField(txtCCCD);
        styleTextField(txtSoDienThoai);
        styleTextField(txtNgayNhanPhong);
        styleTextField(txtNgayTraPhong);
        styleTextField(txtSoLuongNguoi);
        styleComboBox(cboTrangThaiDon);

        txtMaDon.setEditable(false);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(lblTitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;

        addFormRow(formPanel, gbc, "Mã ĐĐP", txtMaDon);
        addFormRow(formPanel, gbc, "Khách hàng", txtKhachHang);
        addFormRow(formPanel, gbc, "CCCD", txtCCCD);
        addFormRow(formPanel, gbc, "Số điện thoại", txtSoDienThoai);
        addFormRow(formPanel, gbc, "Ngày nhận", txtNgayNhanPhong);
        addFormRow(formPanel, gbc, "Ngày trả", txtNgayTraPhong);
        addFormRow(formPanel, gbc, "Số lượng người", txtSoLuongNguoi);
        addFormRow(formPanel, gbc, "Trạng thái đơn", cboTrangThaiDon);

        addSectionTitle(formPanel, gbc, "Danh sách phòng trong đơn");
        addPhongTrongDonTable(formPanel, gbc);

        addSectionTitle(formPanel, gbc, "Danh sách dịch vụ trong đơn");
        addDichVuTrongDonTable(formPanel, gbc);
        addDichVuActionButtons(formPanel, gbc);

        addSectionTitle(formPanel, gbc, "Thao tác phòng");
        createPhongActionControls(formPanel, gbc);

        wrapper.add(formScroll, BorderLayout.CENTER);
        wrapper.add(createActionPanel(), BorderLayout.SOUTH);

        return wrapper;
    }

    private void addSectionTitle(JPanel panel, GridBagConstraints gbc, String title) {
        JLabel label = new JLabel(title);
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        label.setForeground(TEXT_DARK);
        label.setBorder(new EmptyBorder(10, 0, 4, 0));

        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(label, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
    }
    
    private void addDichVuActionButtons(JPanel panel, GridBagConstraints gbc) {
        btnThemDichVu = createPrimaryButton("Thêm dịch vụ");
        btnXoaDichVu = createDangerButton("Xóa dịch vụ");

        btnThemDichVu.setVisible(false);
        btnXoaDichVu.setVisible(false);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(btnThemDichVu);
        buttonPanel.add(btnXoaDichVu);

        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(buttonPanel, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;

        tblPhongTrongDon.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean daChonPhong = tblPhongTrongDon.getSelectedRow() != -1;
                btnThemDichVu.setVisible(daChonPhong);
            }
        });

        tblDichVuTrongDon.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean daChonDichVu = tblDichVuTrongDon.getSelectedRow() != -1;
                btnXoaDichVu.setVisible(daChonDichVu);
            }
        });

        btnThemDichVu.addActionListener(e -> {
            if (tblPhongTrongDon.getSelectedRow() == -1) {
                JOptionPane.showMessageDialog(
                        this,
                        "Vui lòng chọn một phòng trước khi thêm dịch vụ.",
                        "Chưa chọn phòng",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            showDichVuDialog();
        });

        btnXoaDichVu.addActionListener(e -> {
            firePropertyChange("xoaDichVuTrongDon", false, true);
        });
    }
    
    private void addDichVuTrongDonTable(JPanel panel, GridBagConstraints gbc) {
        String[] columns = {
                "Mã phòng", "Mã DV", "Tên dịch vụ", "Đơn giá", "Số lượng", "Thành tiền"
        };

        modelDichVuTrongDon = new DefaultTableModel(new Object[][] {}, columns) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblDichVuTrongDon = new JTable(modelDichVuTrongDon);
        styleTable(tblDichVuTrongDon);
        tblDichVuTrongDon.setRowHeight(28);

        tblDichVuTrongDon.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblDichVuTrongDon.getColumnModel().getColumn(1).setPreferredWidth(80);
        tblDichVuTrongDon.getColumnModel().getColumn(2).setPreferredWidth(160);
        tblDichVuTrongDon.getColumnModel().getColumn(3).setPreferredWidth(90);
        tblDichVuTrongDon.getColumnModel().getColumn(4).setPreferredWidth(80);
        tblDichVuTrongDon.getColumnModel().getColumn(5).setPreferredWidth(100);

        JScrollPane scrollPane = new JScrollPane(tblDichVuTrongDon);
        scrollPane.setPreferredSize(new Dimension(320, 130));
        scrollPane.setBorder(BorderFactory.createLineBorder(CARD_BORDER));

        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(scrollPane, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
    }
    
    private void addThemDichVuButton(JPanel panel, GridBagConstraints gbc) {
        btnThemDichVu = createPrimaryButton("Thêm dịch vụ");
        btnThemDichVu.setVisible(false);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(btnThemDichVu);

        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(buttonPanel, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;

        tblPhongTrongDon.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean daChonPhong = tblPhongTrongDon.getSelectedRow() != -1;
                btnThemDichVu.setVisible(daChonPhong);
            }
        });

        btnThemDichVu.addActionListener(e -> {
            int selectedRow = tblPhongTrongDon.getSelectedRow();

            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(
                        this,
                        "Vui lòng chọn một phòng trước khi thêm dịch vụ.",
                        "Chưa chọn phòng",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String maPhong = tblPhongTrongDon.getValueAt(selectedRow, 0).toString();

            // Lưu mã phòng đang chọn để controller biết thêm dịch vụ cho phòng nào
            firePropertyChange("themDichVuChoPhong", null, maPhong);

            showDichVuDialog();
        });
    }
    private void showDichVuDialog() {
        if (dialogDichVu == null) {
            dialogDichVu = new JDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Thêm dịch vụ",
                    java.awt.Dialog.ModalityType.APPLICATION_MODAL);

            dialogDichVu.setLayout(new BorderLayout(0, 12));

            JPanel contentPanel = createDichVuTablePanel();

            JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
            actionPanel.setBackground(APP_BG);

            JButton btnDongY = createPrimaryButton("Đồng ý");
            JButton btnDong = createButton("Đóng");

            btnDongY.addActionListener(e -> {
                firePropertyChange("xacNhanThemDichVu", false, true);
            });

            btnDong.addActionListener(e -> {
                dialogDichVu.dispose();
            });

            actionPanel.add(btnDongY);
            actionPanel.add(btnDong);

            dialogDichVu.add(contentPanel, BorderLayout.CENTER);
            dialogDichVu.add(actionPanel, BorderLayout.SOUTH);

            dialogDichVu.setSize(650, 420);
            dialogDichVu.setLocationRelativeTo(this);
        }

        dialogDichVu.setLocationRelativeTo(this);
        dialogDichVu.setVisible(true);
    }

    private void addPhongTrongDonTable(JPanel panel, GridBagConstraints gbc) {
        String[] columns = {
                "Mã phòng", "Loại phòng", "Số ngày", "Đơn giá"
        };

        modelPhongTrongDon = new DefaultTableModel(new Object[][] {}, columns) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblPhongTrongDon = new JTable(modelPhongTrongDon);
        styleTable(tblPhongTrongDon);
        tblPhongTrongDon.setRowHeight(28);

        JScrollPane scrollPane = new JScrollPane(tblPhongTrongDon);
        scrollPane.setPreferredSize(new Dimension(320, 130));
        scrollPane.setBorder(BorderFactory.createLineBorder(CARD_BORDER));

        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(scrollPane, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
    }

    private void createPhongActionControls(JPanel panel, GridBagConstraints gbc) {
        cboThaoTacPhong = new JComboBox<>(new String[] {
                "Không thay đổi", "Thêm phòng", "Đổi phòng"
        });

        cboPhongCanDoi = new JComboBox<>();
        cboPhongTrong = new JComboBox<>();

        styleComboBox(cboThaoTacPhong);
        styleComboBox(cboPhongCanDoi);
        styleComboBox(cboPhongTrong);

        addFormRow(panel, gbc, "Thao tác", cboThaoTacPhong);
        addFormRow(panel, gbc, "Phòng cần đổi", cboPhongCanDoi);
        addFormRow(panel, gbc, "Phòng trống", cboPhongTrong);
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 12, 12));
        panel.setBackground(APP_BG);

        btnLuu = createPrimaryButton("Lưu");
        btnLamMoiForm = createButton("Làm mới");
        btnHuyDon = createDangerButton("Hủy đơn");

        panel.add(btnLuu);
        panel.add(btnLamMoiForm);
        panel.add(btnHuyDon);

        return panel;
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, String label, Component component) {
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(createLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(component, gbc);

        gbc.gridy++;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        label.setForeground(TEXT_DARK);
        label.setPreferredSize(new Dimension(140, 38));
        return label;
    }

    private void styleTextField(JTextField textField) {
        textField.setPreferredSize(new Dimension(230, 38));
        textField.setMinimumSize(new Dimension(100, 38));
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER),
                new EmptyBorder(6, 10, 6, 10)));
    }

    private void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setPreferredSize(new Dimension(230, 38));
        comboBox.setMinimumSize(new Dimension(100, 38));
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER),
                new EmptyBorder(6, 10, 6, 10)));
    }

    private void styleTable(JTable table) {
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(233, 239, 248));
        table.getTableHeader().setForeground(TEXT_DARK);
        table.setGridColor(CARD_BORDER);
        table.setSelectionBackground(new Color(221, 232, 248));
        table.setSelectionForeground(TEXT_DARK);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setForeground(TEXT_DARK);
        button.setBackground(BUTTON_BG);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
        button.setPreferredSize(new Dimension(120, 40));
        return button;
    }

    private JButton createPrimaryButton(String text) {
        JButton button = createButton(text);
        button.setBackground(BUTTON_PRIMARY);
        return button;
    }

    private JButton createDangerButton(String text) {
        JButton button = createButton(text);
        button.setBackground(BUTTON_DANGER);
        return button;
    }

    public void clearForm() {
        txtMaDon.setText("");
        txtKhachHang.setText("");
        txtCCCD.setText("");
        txtSoDienThoai.setText("");
        txtNgayNhanPhong.setText("");
        txtNgayTraPhong.setText("");
        txtSoLuongNguoi.setText("");

        if (cboTrangThaiDon.getItemCount() > 0) {
            cboTrangThaiDon.setSelectedIndex(0);
        }

        if (cboThaoTacPhong.getItemCount() > 0) {
            cboThaoTacPhong.setSelectedIndex(0);
        }

        if (cboPhongCanDoi.getItemCount() > 0) {
            cboPhongCanDoi.setSelectedIndex(0);
        }

        if (cboPhongTrong.getItemCount() > 0) {
            cboPhongTrong.setSelectedIndex(0);
        }
        
        if (modelDichVuTrongDon != null) {
            modelDichVuTrongDon.setRowCount(0);
        }

        if (btnThemDichVu != null) {
            btnThemDichVu.setVisible(false);
        }
        if (btnXoaDichVu != null) {
            btnXoaDichVu.setVisible(false);
        }

        modelPhongTrongDon.setRowCount(0);
        resetDichVuSelection();

        tblDonDatPhong.clearSelection();
        tblDichVu.clearSelection();
        tblPhongTrongDon.clearSelection();

        if (btnThemDichVu != null) {
            btnThemDichVu.setVisible(false);
        }
    }
    public void dongDialogDichVu() {
        if (dialogDichVu != null) {
            dialogDichVu.dispose();
        }
    }

    public void resetDichVuSelection() {
        if (modelDichVu == null) {
            return;
        }

        for (int i = 0; i < modelDichVu.getRowCount(); i++) {
            modelDichVu.setValueAt(false, i, 0);
            modelDichVu.setValueAt(1, i, 4);
        }
    }

    public JTextField getTxtTimMaDon() {
        return txtTimMaDon;
    }

    public JTextField getTxtTimKhachHang() {
        return txtTimKhachHang;
    }

    public JComboBox<String> getCboLocTrangThai() {
        return cboLocTrangThai;
    }

    public JButton getBtnLamMoiTim() {
        return btnLamMoiTim;
    }

    public JTable getTblDonDatPhong() {
        return tblDonDatPhong;
    }

    public DefaultTableModel getModelDonDatPhong() {
        return modelDonDatPhong;
    }

    public JTable getTblDichVu() {
        return tblDichVu;
    }

    public DefaultTableModel getModelDichVu() {
        return modelDichVu;
    }

    public JTable getTblPhongTrongDon() {
        return tblPhongTrongDon;
    }

    public DefaultTableModel getModelPhongTrongDon() {
        return modelPhongTrongDon;
    }

    public JTextField getTxtMaDon() {
        return txtMaDon;
    }

    public JTextField getTxtKhachHang() {
        return txtKhachHang;
    }

    public JTextField getTxtCCCD() {
        return txtCCCD;
    }

    public JTextField getTxtSoDienThoai() {
        return txtSoDienThoai;
    }

    public JTextField getTxtNgayNhanPhong() {
        return txtNgayNhanPhong;
    }

    public JTextField getTxtNgayTraPhong() {
        return txtNgayTraPhong;
    }

    public JTextField getTxtSoLuongNguoi() {
        return txtSoLuongNguoi;
    }

    public JComboBox<String> getCboTrangThaiDon() {
        return cboTrangThaiDon;
    }

    public JComboBox<String> getCboThaoTacPhong() {
        return cboThaoTacPhong;
    }

    public JComboBox<String> getCboPhongCanDoi() {
        return cboPhongCanDoi;
    }

    public JComboBox<String> getCboPhongTrong() {
        return cboPhongTrong;
    }

    public JButton getBtnLuu() {
        return btnLuu;
    }

    public JButton getBtnLamMoiForm() {
        return btnLamMoiForm;
    }

    public JButton getBtnHuyDon() {
        return btnHuyDon;
    }
    public JButton getBtnThemDichVu() {
        return btnThemDichVu;
    }
    
    public JTable getTblDichVuTrongDon() {
        return tblDichVuTrongDon;
    }

    public DefaultTableModel getModelDichVuTrongDon() {
        return modelDichVuTrongDon;
    }
    public JButton getBtnXoaDichVu() {
        return btnXoaDichVu;
    }

    public static JPanel createPanel() {
        QLDonDatPhong panel = new QLDonDatPhong();
        new QLDonDatPhongController(panel);
        return panel;
    }
}