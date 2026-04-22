package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import entity.DonDatPhong;

public class ThongTinDatPhongDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private static final Color BG = new Color(245, 247, 250);
    private static final Color PANEL_BG = Color.WHITE;
    private static final Color BORDER = new Color(210, 220, 235);
    private static final Color TITLE = new Color(36, 64, 102);
    private static final Color PRIMARY = new Color(91, 168, 217);

    private JTextField txtNgayNhan;
    private JTextField txtNgayTra;

    private boolean updated = false;
    private boolean themDichVu = false;

    private final DonDatPhong room;

    public ThongTinDatPhongDialog(DonDatPhong room) {
        this.room = room;
        setTitle("Thông tin đặt phòng - " + room.getMaPhong());
        setModal(true);
        setSize(560, 460);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(14, 14, 14, 14));
        setContentPane(root);

        JLabel lblHeader = new JLabel("THÔNG TIN ĐẶT PHÒNG");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHeader.setForeground(TITLE);
        root.add(lblHeader, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        center.add(createThongTinPanel());
        center.add(Box.createVerticalStrut(12));
        center.add(createThoiGianPanel());

        JScrollPane scrollPane = new JScrollPane(center);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(BG);

        root.add(scrollPane, BorderLayout.CENTER);
        root.add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JPanel createThongTinPanel() {
        JPanel panel = createSectionPanel("Chi tiết đơn đặt phòng");

        addRow(panel, 0, "Mã phòng:", createValueLabel(nullToEmpty(room.getMaPhong())),
                "Loại phòng:", createValueLabel(nullToEmpty(room.getLoaiPhong())));

        addRow(panel, 1, "Trạng thái:", createValueLabel(nullToEmpty(room.getTrangThaiPhong())),
                "Mã ĐĐP:", createValueLabel(nullToEmpty(room.getMaDDP())));

        addRow(panel, 2, "Mã KH:", createValueLabel(nullToEmpty(room.getMaKH())),
                "Tên KH:", createValueLabel(nullToEmpty(room.getTenKH())));

        addRow(panel, 3, "Tiền cọc:",
                createValueLabel(room.getTienCoc() == null ? "" : String.valueOf(room.getTienCoc())),
                "", new JLabel());

        return panel;
    }

    private JPanel createThoiGianPanel() {
        JPanel panel = createSectionPanel("Cập nhật thời gian lưu trú");

        txtNgayNhan = createTextField(nullToEmpty(room.getNgayNhan()));
        txtNgayTra = createTextField(nullToEmpty(room.getNgayTra()));

        addRow(panel, 0, "Ngày nhận:", txtNgayNhan,
                "", new JLabel());

        addRow(panel, 1, "Ngày trả:", txtNgayTra,
                "", new JLabel());

        JLabel lblNote = new JLabel("Định dạng gợi ý: dd/MM/yyyy HH:mm");
        lblNote.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblNote.setForeground(new Color(120, 120, 120));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(6, 6, 0, 6);
        panel.add(lblNote, gbc);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);

        JButton btnCapNhat = createButton("Cập nhật thời gian", PRIMARY, Color.WHITE);
        JButton btnThemDV = createButton("Thêm dịch vụ", new Color(233, 239, 248), Color.BLACK);
        JButton btnDong = createButton("Đóng", new Color(235, 238, 243), Color.BLACK);

        btnCapNhat.addActionListener(e -> xuLyCapNhat());
        btnThemDV.addActionListener(e -> xuLyThemDichVu());
        btnDong.addActionListener(e -> dispose());

        panel.add(btnCapNhat);
        panel.add(btnThemDV);
        panel.add(btnDong);

        return panel;
    }

    private JPanel createSectionPanel(String title) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(PANEL_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(14, 14, 14, 14)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTitle.setForeground(TITLE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 6, 10, 6);
        panel.add(lblTitle, gbc);

        return panel;
    }

    private void addRow(JPanel panel, int row, String l1, java.awt.Component c1, String l2, java.awt.Component c2) {
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
        lbl.setPreferredSize(new Dimension(180, 34));
        return lbl;
    }

    private JTextField createTextField(String text) {
        JTextField txt = new JTextField(text);
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.setPreferredSize(new Dimension(180, 34));
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(6, 10, 6, 10)
        ));
        return txt;
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

    private void xuLyCapNhat() {
        if (txtNgayNhan.getText().trim().isEmpty() || txtNgayTra.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không được để trống ngày nhận và ngày trả.");
            return;
        }
        updated = true;
        dispose();
    }

    private void xuLyThemDichVu() {
        themDichVu = true;
        dispose();
    }

    public boolean isUpdated() {
        return updated;
    }

    public boolean isThemDichVu() {
        return themDichVu;
    }

    public String getNgayNhan() {
        return txtNgayNhan.getText().trim();
    }

    public String getNgayTra() {
        return txtNgayTra.getText().trim();
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}