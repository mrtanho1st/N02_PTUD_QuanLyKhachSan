package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import javax.swing.JOptionPane;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.jdatepicker.JDatePicker;
import org.jdatepicker.UtilDateModel;

import controller.DonDatPhongController;
import entity.Phong;

public class DatPhong extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final Color APP_BG = new Color(237, 242, 247);
    private static final Color PANEL_BG = new Color(248, 250, 253);
    private static final Color CARD_BORDER = new Color(210, 220, 235);
    private static final Color TEXT_DARK = new Color(24, 39, 75);
    private static final Color BUTTON_BG = new Color(233, 239, 248);
    private static final Color BUTTON_PRIMARY = new Color(220, 235, 255);
    private static final Color BUTTON_DANGER = new Color(252, 230, 230);

    private static final Color SELECTED_BG = new Color(219, 234, 254);
    private static final Color SELECTED_BORDER = new Color(37, 99, 235);
    private static final Color DISABLED_BG = new Color(241, 245, 249);

    private JTextField txtTimMaPhong;
    private JComboBox<String> cboLoaiPhong;
    private JComboBox<String> cboTrangThai;
    private JButton btnTaiLai;

    private JDatePicker dateTuNgay;
    private JDatePicker dateDenNgay;

    private JPanel pnlDanhSachPhong;

    private JButton btnTaoDonDatPhong;
    private JButton btnBoChonPhong;
    private JLabel lblSoPhongDaChon;

    private final List<Phong> selectedRooms = new ArrayList<>();
    private final Set<String> selectedRoomIds = new LinkedHashSet<>();
    private final Map<String, JPanel> roomCardWrappers = new LinkedHashMap<>();

    private Consumer<Phong> occupiedRoomClickListener;

    public DatPhong() {
        initUI();
    }

    // private void initUI() {
    // setLayout(new BorderLayout(0, 16));
    // setBackground(APP_BG);
    // setBorder(new EmptyBorder(12, 12, 12, 12));
    //
    // add(createFilterPanel(), BorderLayout.NORTH);
    // add(createRoomArea(), BorderLayout.CENTER);
    // add(createActionPanel(), BorderLayout.SOUTH);
    // }
    private void initUI() {
        setLayout(new BorderLayout(0, 16));
        setBackground(APP_BG);
        setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(APP_BG);

        topPanel.add(createFilterPanel());
        topPanel.add(createDatePanel()); // 👈 thêm ở đây

        add(topPanel, BorderLayout.NORTH);
        add(createRoomArea(), BorderLayout.CENTER);
        add(createActionPanel(), BorderLayout.SOUTH);
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

        JLabel lblMaPhong = createLabel("Mã phòng:");
        JLabel lblLoaiPhong = createLabel("Loại phòng:");
        JLabel lblTrangThai = createLabel("Trạng thái:");

        txtTimMaPhong = new JTextField();
        styleTextField(txtTimMaPhong);

        cboLoaiPhong = new JComboBox<>();
        

        cboTrangThai = new JComboBox<>(new String[] {
                "Tất cả", "Trống", "Đã đặt", "Đang sử dụng", "Quá hạn", "Bảo trì"
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

    private JPanel createDatePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        panel.setBackground(PANEL_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER),
                new EmptyBorder(10, 18, 10, 18)));

        JLabel lblTuNgay = createLabel("Từ ngày:");
        JLabel lblDenNgay = createLabel("Đến ngày:");

        dateTuNgay = createDatePicker();
        dateDenNgay = createDatePicker();

        dateTuNgay.setPreferredSize(new Dimension(160, 35));
        dateDenNgay.setPreferredSize(new Dimension(160, 35));

        panel.add(lblTuNgay);
        panel.add(dateTuNgay);
        panel.add(lblDenNgay);
        panel.add(dateDenNgay);

        return panel;
    }

    private JScrollPane createRoomArea() {
        pnlDanhSachPhong = new JPanel();
        pnlDanhSachPhong.setLayout(new BoxLayout(pnlDanhSachPhong, BoxLayout.Y_AXIS));
        pnlDanhSachPhong.setBackground(APP_BG);
        pnlDanhSachPhong.setBorder(new EmptyBorder(4, 4, 4, 4));

        JScrollPane scrollPane = new JScrollPane(pnlDanhSachPhong);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER),
                new EmptyBorder(8, 8, 8, 8)));
        scrollPane.getViewport().setBackground(APP_BG);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        return scrollPane;
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new BorderLayout(12, 0));
        panel.setBackground(PANEL_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER),
                new EmptyBorder(12, 18, 12, 18)));

        lblSoPhongDaChon = new JLabel("Đã chọn: 0 phòng");
        lblSoPhongDaChon.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblSoPhongDaChon.setForeground(TEXT_DARK);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        btnBoChonPhong = createDangerButton("Bỏ chọn phòng");
        btnTaoDonDatPhong = createPrimaryButton("Tạo đơn đặt phòng");

        btnBoChonPhong.addActionListener(e -> clearSelectedRooms());

        buttonPanel.add(btnBoChonPhong);
        buttonPanel.add(btnTaoDonDatPhong);

        panel.add(lblSoPhongDaChon, BorderLayout.WEST);
        panel.add(buttonPanel, BorderLayout.EAST);

        return panel;
    }

    public void renderRooms(List<Phong> rooms) {
        pnlDanhSachPhong.removeAll();
        roomCardWrappers.clear();

        Map<String, Phong> uniqueRooms = new LinkedHashMap<>();

        for (Phong room : rooms) {
            uniqueRooms.put(room.getMaPhong(), room);
        }

        List<Phong> sortedRooms = new ArrayList<>(uniqueRooms.values());
        sortedRooms.sort(
                Comparator.comparing(Phong::getLoaiPhong)
                        .thenComparing(Phong::getMaPhong));

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

                Phong room = groupRooms.get(i);
                JPanel roomWrapper = createSelectableRoomCard(room);
                rowPanel.add(roomWrapper);
            }

            groupPanel.add(groupTitle, BorderLayout.NORTH);
            groupPanel.add(rowsPanel, BorderLayout.CENTER);

            pnlDanhSachPhong.add(groupPanel);
        }

        removeSelectedRoomsNotInList(uniqueRooms);
        updateSelectedLabel();

        pnlDanhSachPhong.revalidate();
        pnlDanhSachPhong.repaint();
    }

    private JPanel createSelectableRoomCard(Phong room) {
        PhongCardPanel card = new PhongCardPanel(room);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(new EmptyBorder(3, 3, 3, 3));
        wrapper.setBackground(Color.WHITE);
        wrapper.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        wrapper.add(card, BorderLayout.CENTER);

        roomCardWrappers.put(room.getMaPhong(), wrapper);

        updateRoomCardColor(room, wrapper);

        wrapper.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toggleSelectRoom(room);
            }
        });

        addMouseListenerToChildren(card, room);

        return wrapper;
    }

    private JDatePicker createDatePicker() {
        UtilDateModel model = new UtilDateModel();
        JDatePicker datePicker = new JDatePicker(model);

        datePicker.setPreferredSize(new Dimension(220, 38));
        datePicker.setMinimumSize(new Dimension(100, 38));
        datePicker.setBackground(Color.WHITE);
        datePicker.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        styleDatePickerChildren(datePicker);

        return datePicker;
    }

    private void styleDatePickerChildren(Container container) {
        for (Component comp : container.getComponents()) {
            comp.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            if (comp instanceof JTextField) {
                JTextField textField = (JTextField) comp;
                textField.setBackground(Color.WHITE);
                textField.setForeground(TEXT_DARK);
                textField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(CARD_BORDER),
                        new EmptyBorder(6, 8, 6, 8)));
                textField.setPreferredSize(new Dimension(160, 34));
                textField.setMinimumSize(new Dimension(120, 34));
            }

            if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                button.setText("📅");
                button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
                button.setFocusPainted(false);
                button.setBackground(BUTTON_BG);
                button.setForeground(TEXT_DARK);
                button.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
                button.setPreferredSize(new Dimension(36, 34));
                button.setMinimumSize(new Dimension(36, 34));
                button.setMargin(new Insets(0, 0, 0, 0));
            }

            if (comp instanceof Container) {
                styleDatePickerChildren((Container) comp);
            }
        }
    }

    private void addMouseListenerToChildren(Component component, Phong room) {
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toggleSelectRoom(room);
            }
        });

        if (component instanceof JPanel) {
            Component[] children = ((JPanel) component).getComponents();

            for (Component child : children) {
                addMouseListenerToChildren(child, room);
            }
        }
    }

    private void toggleSelectRoom(Phong room) {
        if (room == null) {
            return;
        }

        String trangThai = room.getTrangThai();

        if ("Trống".equalsIgnoreCase(trangThai)) {
            togglePhongTrong(room);
            return;
        }

        if ("Đã đặt".equalsIgnoreCase(trangThai)
                || "Đang sử dụng".equalsIgnoreCase(trangThai)
                || "Đã nhận".equalsIgnoreCase(trangThai)
                || "Quá hạn".equalsIgnoreCase(trangThai)) {

            if (occupiedRoomClickListener != null) {
                occupiedRoomClickListener.accept(room);
            }

            return;
        }

        if ("Bảo trì".equalsIgnoreCase(trangThai)) {
            JOptionPane.showMessageDialog(this, "Phòng đang bảo trì, không thể thao tác.");
            return;
        }

        JOptionPane.showMessageDialog(this, "Trạng thái phòng không hợp lệ: " + trangThai);
    }

    private void togglePhongTrong(Phong room) {
        String maPhong = room.getMaPhong();

        if (selectedRoomIds.contains(maPhong)) {
            selectedRoomIds.remove(maPhong);
            selectedRooms.removeIf(p -> p.getMaPhong().equals(maPhong));
        } else {
            selectedRoomIds.add(maPhong);
            selectedRooms.add(room);
        }

        JPanel wrapper = roomCardWrappers.get(maPhong);

        if (wrapper != null) {
            updateRoomCardColor(room, wrapper);
        }

        updateSelectedLabel();
    }

    private boolean isRoomSelectable(Phong room) {
        return "Trống".equalsIgnoreCase(room.getTrangThai());
    }

    private void updateRoomCardColor(Phong room, JPanel wrapper) {
        boolean selected = selectedRoomIds.contains(room.getMaPhong());
        boolean selectable = isRoomSelectable(room);

        if (selected) {
            wrapper.setBackground(SELECTED_BG);
            wrapper.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(SELECTED_BORDER, 3),
                    new EmptyBorder(3, 3, 3, 3)));
        } else if ("Quá hạn".equalsIgnoreCase(room.getTrangThai())) {
            wrapper.setBackground(new Color(252, 226, 226));
            wrapper.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 38, 38), 2),
                    new EmptyBorder(3, 3, 3, 3)));
            wrapper.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        } else if (!selectable) {
            wrapper.setBackground(DISABLED_BG);
            wrapper.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                    new EmptyBorder(3, 3, 3, 3)));
            wrapper.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        } else {
            wrapper.setBackground(Color.WHITE);
            wrapper.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(CARD_BORDER, 1),
                    new EmptyBorder(3, 3, 3, 3)));
            wrapper.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        wrapper.revalidate();
        wrapper.repaint();
    }

    private void updateSelectedLabel() {
        lblSoPhongDaChon.setText("Đã chọn: " + selectedRooms.size() + " phòng");
    }

    private void removeSelectedRoomsNotInList(Map<String, Phong> currentRooms) {
        selectedRooms.removeIf(room -> !currentRooms.containsKey(room.getMaPhong()));

        selectedRoomIds.clear();

        for (Phong room : selectedRooms) {
            selectedRoomIds.add(room.getMaPhong());
        }
    }

    public void clearSelectedRooms() {
        selectedRooms.clear();
        selectedRoomIds.clear();

        for (Map.Entry<String, JPanel> entry : roomCardWrappers.entrySet()) {
            JPanel wrapper = entry.getValue();
            wrapper.setBackground(Color.WHITE);
            wrapper.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(CARD_BORDER, 1),
                    new EmptyBorder(3, 3, 3, 3)));
            wrapper.repaint();
        }

        updateSelectedLabel();
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        label.setForeground(TEXT_DARK);
        label.setPreferredSize(new Dimension(110, 38));
        return label;
    }

    private void styleTextField(JTextField textField) {
        textField.setPreferredSize(new Dimension(180, 38));
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER),
                new EmptyBorder(6, 10, 6, 10)));
    }

    private void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setPreferredSize(new Dimension(180, 38));
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
        button.setPreferredSize(new Dimension(140, 42));
        return button;
    }

    private JButton createPrimaryButton(String text) {
        JButton button = createButton(text);
        button.setBackground(BUTTON_PRIMARY);
        button.setPreferredSize(new Dimension(180, 42));
        return button;
    }

    private JButton createDangerButton(String text) {
        JButton button = createButton(text);
        button.setBackground(BUTTON_DANGER);
        button.setPreferredSize(new Dimension(150, 42));
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

    public JButton getBtnTaoDonDatPhong() {
        return btnTaoDonDatPhong;
    }

    public JButton getBtnBoChonPhong() {
        return btnBoChonPhong;
    }

    public JLabel getLblSoPhongDaChon() {
        return lblSoPhongDaChon;
    }

    public List<Phong> getSelectedRooms() {
        return new ArrayList<>(selectedRooms);
    }

    public List<String> getSelectedRoomIds() {
        return new ArrayList<>(selectedRoomIds);
    }

    public void setOccupiedRoomClickListener(Consumer<Phong> listener) {
        this.occupiedRoomClickListener = listener;
    }

    public boolean hasSelectedRooms() {
        return !selectedRooms.isEmpty();
    }

    public JDatePicker getDateTuNgay() {
        return dateTuNgay;
    }

    public JDatePicker getDateDenNgay() {
        return dateDenNgay;
    }

    public static JPanel createPanel(GiaoDienChinh giaoDienChinh) {
        DatPhong panel = new DatPhong();
        new DonDatPhongController(panel, giaoDienChinh);
        return panel;
    }

    public static JPanel createPanel() {
        return createPanel(null);
    }
}
