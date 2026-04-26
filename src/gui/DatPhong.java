package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import controller.DonDatPhongController;
import entity.DonDatPhong;
import entity.Phong;

public class DatPhong extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final Color APP_BG = new Color(237, 242, 247);
    private static final Color PANEL_BG = new Color(248, 250, 253);
    private static final Color CARD_BORDER = new Color(210, 220, 235);
    private static final Color TEXT_DARK = new Color(24, 39, 75);
    private static final Color BUTTON_BG = new Color(233, 239, 248);

    private JTextField txtTimMaPhong;
    private JComboBox<String> cboLoaiPhong;
    private JComboBox<String> cboTrangThai;
    private JButton btnTaiLai;

    private JPanel pnlDanhSachPhong;

    public DatPhong() {
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 16));
        setBackground(APP_BG);
        setBorder(new EmptyBorder(12, 12, 12, 12));

        add(createFilterPanel(), BorderLayout.NORTH);
        add(createRoomArea(), BorderLayout.CENTER);
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(PANEL_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER),
                new EmptyBorder(16, 18, 16, 18)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lblMaPhong = createLabel("Mã phòng:");
        JLabel lblLoaiPhong = createLabel("Loại phòng:");
        JLabel lblTrangThai = createLabel("Trạng thái:");

        txtTimMaPhong = new JTextField();
        styleTextField(txtTimMaPhong);

        cboLoaiPhong = new JComboBox<>(new String[] {
                "Tất cả", "Phòng Thượng hạng" ,"Phòng Cao cấp", "Phòng Tiêu chuẩn", "Phòng Gia đình", "Phòng Sang trọng"
        });

        cboTrangThai = new JComboBox<>(new String[] {
                "Tất cả", "Trống", "Đã đặt", "Đang sử dụng", "Bảo trì"
        });

        styleComboBox(cboLoaiPhong);
        styleComboBox(cboTrangThai);

        btnTaiLai = createButton("Tải lại");

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        panel.add(lblMaPhong, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        panel.add(txtTimMaPhong, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        panel.add(lblLoaiPhong, gbc);

        gbc.gridx = 3;
        gbc.weightx = 0.6;
        panel.add(cboLoaiPhong, gbc);

        gbc.gridx = 4;
        gbc.weightx = 0;
        panel.add(lblTrangThai, gbc);

        gbc.gridx = 5;
        gbc.weightx = 0.6;
        panel.add(cboTrangThai, gbc);

        gbc.gridx = 6;
        gbc.weightx = 0;
        panel.add(btnTaiLai, gbc);

        return panel;
    }

    private JScrollPane createRoomArea() {
        pnlDanhSachPhong = new JPanel();
        pnlDanhSachPhong.setLayout(
                new javax.swing.BoxLayout(pnlDanhSachPhong, javax.swing.BoxLayout.Y_AXIS)
        );
        pnlDanhSachPhong.setBackground(APP_BG);
        pnlDanhSachPhong.setBorder(new EmptyBorder(4, 4, 4, 4));

        JScrollPane scrollPane = new JScrollPane(pnlDanhSachPhong);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER),
                new EmptyBorder(8, 8, 8, 8)
        ));
        scrollPane.getViewport().setBackground(APP_BG);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        return scrollPane;
    }

    public void renderRooms(List<Phong> rooms) {
        pnlDanhSachPhong.removeAll();

        Map<String, Phong> uniqueRooms = new LinkedHashMap<>();

        for (Phong room : rooms) {
            uniqueRooms.put(room.getMaPhong(), room);
        }

        List<Phong> sortedRooms = new ArrayList<>(uniqueRooms.values());
        sortedRooms.sort(
                Comparator.comparing(Phong::getLoaiPhong)
                        .thenComparing(Phong::getMaPhong)
        );

        Map<String, List<Phong>> groupMap = new LinkedHashMap<>();

        for (Phong room : sortedRooms) {
            String groupName = room.getLoaiPhong();

            if (!groupMap.containsKey(groupName)) {
                groupMap.put(groupName, new ArrayList<>());
            }

            groupMap.get(groupName).add(room);
        }

        for (Map.Entry<String, List<Phong>> entry : groupMap.entrySet()) {
            String groupName = entry.getKey();
            List<Phong> groupRooms = entry.getValue();

            JPanel groupPanel = new JPanel(new BorderLayout());
            groupPanel.setOpaque(false);
            groupPanel.setAlignmentX(LEFT_ALIGNMENT);

            JLabel groupTitle = new JLabel(groupName);
            groupTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
            groupTitle.setForeground(new Color(91, 168, 217));
            groupTitle.setBorder(new EmptyBorder(8, 4, 8, 4));

            JPanel rowsPanel = new JPanel();
            rowsPanel.setLayout(new BoxLayout(rowsPanel, BoxLayout.Y_AXIS));
            rowsPanel.setOpaque(false);
            rowsPanel.setBorder(new EmptyBorder(0, 0, 16, 0));

            JPanel rowPanel = null;

            for (int i = 0; i < groupRooms.size(); i++) {
                if (i % 4 == 0) {
                    rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
                    rowPanel.setOpaque(false);
                    rowPanel.setAlignmentX(LEFT_ALIGNMENT);
                    rowsPanel.add(rowPanel);
                }

                rowPanel.add(new PhongCardPanel(groupRooms.get(i)));
            }

            groupPanel.add(groupTitle, BorderLayout.NORTH);
            groupPanel.add(rowsPanel, BorderLayout.CENTER);

            pnlDanhSachPhong.add(groupPanel);
        }

        pnlDanhSachPhong.revalidate();
        pnlDanhSachPhong.repaint();
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        label.setForeground(TEXT_DARK);
        label.setPreferredSize(new java.awt.Dimension(110, 38));
        return label;
    }

    private void styleTextField(JTextField textField) {
        textField.setPreferredSize(new java.awt.Dimension(180, 38));
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER),
                new EmptyBorder(6, 10, 6, 10)
        ));
    }

    private void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setPreferredSize(new java.awt.Dimension(180, 38));
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setBackground(Color.WHITE);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setForeground(TEXT_DARK);
        button.setBackground(BUTTON_BG);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
        button.setPreferredSize(new java.awt.Dimension(120, 42));
        return button;
    }

    public JTextField getTxtTimMaPhong() {
        return txtTimMaPhong;
    }

    public JComboBox<String> getCboLoaiPhong() {
        return cboLoaiPhong;
    }

    public JComboBox<String> getCboTrangThai() {
        return cboTrangThai;
    }

    public JButton getBtnTaiLai() {
        return btnTaiLai;
    }

    public JPanel getPnlDanhSachPhong() {
        return pnlDanhSachPhong;
    }

    public static JPanel createPanel() {
        DatPhong panel = new DatPhong();
        new DonDatPhongController(panel);
        return panel;
    }
}