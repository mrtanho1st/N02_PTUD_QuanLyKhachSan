package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class DatPhong {

    private static final long serialVersionUID = 1L;

    private static final Color APP_BG = new Color(238, 243, 250);
    private static final Color CARD_BG = new Color(255, 255, 255);
    private static final Color PRIMARY = new Color(32, 104, 185);
    private static final Color PRIMARY_DARK = new Color(15, 62, 127);
    private static final Color TEXT = new Color(33, 48, 73);
    private static final Color BUTTON_TEXT = new Color(33, 48, 73);
    private static final Color BUTTON_PRIMARY_BG = new Color(220, 235, 255);
    private static final Color BUTTON_GHOST_BG = new Color(238, 245, 255);
    private static final Color BUTTON_DANGER_BG = new Color(252, 230, 230);

    public static JPanel createPanel() {
        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setBackground(APP_BG);
        root.setBorder(new EmptyBorder(12, 14, 12, 14));

        root.add(createBody(), BorderLayout.CENTER);

        return root;
    }

    private static JPanel createBody() {
        JPanel body = new JPanel(new BorderLayout(0, 12));
        body.setOpaque(false);

        body.add(createFilterPanel(), BorderLayout.NORTH);
        body.add(createCenterPanel(), BorderLayout.CENTER);
        body.add(createActionPanel(), BorderLayout.SOUTH);
        return body;
    }

    private static JPanel createFilterPanel() {
        JPanel filter = new RoundedPanel(20, CARD_BG);
        filter.setLayout(new BorderLayout(12, 0));
        filter.setBorder(new EmptyBorder(12, 14, 12, 14));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);

        JLabel lbSearch = new JLabel("Tìm đơn đặt:");
        lbSearch.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbSearch.setForeground(TEXT);

        JTextField txtSearch = createInputField("");
        txtSearch.setPreferredSize(new Dimension(320, 36));

        JComboBox<String> cbFilter = new JComboBox<String>(new String[] {
                "Mã đơn đặt phòng", "Khách hàng", "Số phòng", "Loại phòng", "Tình trạng"
        });
        cbFilter.setPreferredSize(new Dimension(170, 36));
        cbFilter.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        left.add(lbSearch);
        left.add(txtSearch);
        left.add(cbFilter);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        right.add(createPrimaryButton("Lọc", 100, 36));
        right.add(createGhostButton("Hiển thị tất cả", 140, 36));

        filter.add(left, BorderLayout.WEST);
        filter.add(right, BorderLayout.EAST);
        return filter;
    }

    private static JPanel createCenterPanel() {
        JPanel center = new JPanel(new BorderLayout(12, 0));
        center.setOpaque(false);

        JPanel formPanel = createFormPanel();
        JPanel tablePanel = createTablePanel();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, formPanel, tablePanel);
        splitPane.setDividerLocation(480);
        splitPane.setResizeWeight(0.35);
        splitPane.setBorder(null);
        splitPane.setOpaque(false);

        center.add(splitPane, BorderLayout.CENTER);
        return center;
    }

    private static JPanel createFormPanel() {
        JPanel formCard = new RoundedPanel(20, CARD_BG);
        formCard.setLayout(new BorderLayout());
        formCard.setBorder(new EmptyBorder(14, 14, 14, 14));
        formCard.setPreferredSize(new Dimension(420, 0));

        JLabel title = new JLabel("Thông tin đơn đặt phòng");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(7, 4, 6, 4);

        addFormRow(form, gbc, "Mã đơn", createInputField(""));
        addFormRow(form, gbc, "Họ tên khách", createInputField(""));
        addFormRow(form, gbc, "Số điện thoại", createInputField(""));
        addFormRow(form, gbc, "Ngày nhận phòng", createInputField("dd/MM/yyyy"));
        addFormRow(form, gbc, "Ngày trả phòng", createInputField("dd/MM/yyyy"));
        addFormRow(form, gbc, "Loại phòng", new JComboBox<>(new String[] {
                "Standard", "Superior", "Deluxe", "Suite"
        }));
        addFormRow(form, gbc, "Số phòng", createRoomSelectionInput());
        addFormRow(form, gbc, "Số lượng khách", createInputField("1"));
        addFormRow(form, gbc, "Ca nhận phòng", new JComboBox<>(new String[] {
                "Sáng", "Chiều", "Tối"
        }));
        addFormRow(form, gbc, "Trạng thái", new JComboBox<>(new String[] {
                "Đã xác nhận", "Chờ xác nhận", "Đã hủy"
        }));

        JLabel lbNote = new JLabel("Ghi chú");
        lbNote.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbNote.setForeground(TEXT);
        lbNote.setPreferredSize(new Dimension(130, 36));

        JTextArea txtNote = new JTextArea(3, 20);
        txtNote.setLineWrap(true);
        txtNote.setWrapStyleWord(true);
        txtNote.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtNote.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 214, 235), 1),
                new EmptyBorder(6, 10, 6, 10)));

        JScrollPane noteScroll = new JScrollPane(txtNote);
        noteScroll.setPreferredSize(new Dimension(300, 85));

        gbc.gridx = 0;
        form.add(lbNote, gbc);
        gbc.gridx = 1;
        form.add(noteScroll, gbc);
        gbc.gridy++;

        JScrollPane formScroll = new JScrollPane(form);
        formScroll.setBorder(null);
        formScroll.getViewport().setBackground(CARD_BG);
        formScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        formScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        formCard.add(title, BorderLayout.NORTH);
        formCard.add(formScroll, BorderLayout.CENTER);

        return formCard;
    }

    private static JPanel createTablePanel() {
        JPanel tableCard = new RoundedPanel(20, CARD_BG);
        tableCard.setLayout(new BorderLayout(0, 10));
        tableCard.setBorder(new EmptyBorder(14, 14, 14, 14));

        JLabel title = new JLabel("Danh sách đơn đặt phòng");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT);

        DefaultTableModel tableModel = new DefaultTableModel(new Object[] {
                "Mã đơn", "Khách hàng", "Nhân viên", "SĐT", "Nhận phòng", "Trả phòng", "Loại phòng", "Số phòng",
                "Số khách",
                "Tiền cọc", "Trạng thái"
        }, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
        table.setRowHeight(34);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(231, 240, 252));
        table.getTableHeader().setForeground(TEXT);
        table.setGridColor(new Color(229, 236, 247));
        table.setShowVerticalLines(false);
        table.setSelectionBackground(new Color(210, 229, 255));
        table.setSelectionForeground(TEXT);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        table.getColumnModel().getColumn(0).setPreferredWidth(90);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(140);
        table.getColumnModel().getColumn(3).setPreferredWidth(110);
        table.getColumnModel().getColumn(4).setPreferredWidth(110);
        table.getColumnModel().getColumn(5).setPreferredWidth(110);
        table.getColumnModel().getColumn(6).setPreferredWidth(100);
        table.getColumnModel().getColumn(7).setPreferredWidth(90);
        table.getColumnModel().getColumn(8).setPreferredWidth(80);
        table.getColumnModel().getColumn(9).setPreferredWidth(110);
        table.getColumnModel().getColumn(10).setPreferredWidth(120);

        DefaultTableCellRenderer centerAlign = new DefaultTableCellRenderer();
        centerAlign.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerAlign);
        table.getColumnModel().getColumn(2).setCellRenderer(centerAlign);
        table.getColumnModel().getColumn(3).setCellRenderer(centerAlign);
        table.getColumnModel().getColumn(4).setCellRenderer(centerAlign);
        table.getColumnModel().getColumn(5).setCellRenderer(centerAlign);
        table.getColumnModel().getColumn(7).setCellRenderer(centerAlign);
        table.getColumnModel().getColumn(8).setCellRenderer(centerAlign);
        table.getColumnModel().getColumn(9).setCellRenderer(centerAlign);
        table.getColumnModel().getColumn(10).setCellRenderer(centerAlign);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(218, 229, 244), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        tableCard.add(title, BorderLayout.NORTH);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        seedData(tableModel);
        return tableCard;
    }

    private static JPanel createActionPanel() {
        JPanel actions = new RoundedPanel(20, CARD_BG);
        actions.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        actions.setBorder(new EmptyBorder(4, 10, 4, 10));

        actions.add(createGhostButton("Làm mới", 120, 38));
        actions.add(createPrimaryButton("Đặt phòng", 120, 38));
        actions.add(createPrimaryButton("Cập nhật", 120, 38));
        actions.add(createDangerButton("Hủy đặt", 120, 38));

        return actions;
    }

    private static void addFormRow(JPanel container, GridBagConstraints gbc, String labelText,
            java.awt.Component input) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        label.setForeground(TEXT);
        label.setPreferredSize(new Dimension(130, 36));

        if (input instanceof JTextField) {
            JTextField textField = (JTextField) input;
            textField.setPreferredSize(new Dimension(300, 36));
            textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            textField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 214, 235), 1),
                    new EmptyBorder(6, 10, 6, 10)));
            textField.setEditable(true);
            textField.setEnabled(true);
            textField.setFocusable(true);
        }

        if (input instanceof JComboBox) {
            JComboBox<?> combo = (JComboBox<?>) input;
            combo.setPreferredSize(new Dimension(300, 36));
            combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        }

        gbc.gridx = 0;
        container.add(label, gbc);
        gbc.gridx = 1;
        container.add(input, gbc);
        gbc.gridy++;
    }

    private static JTextField createInputField(String initialText) {
        JTextField textField = new JTextField(initialText);
        textField.setEditable(true);
        textField.setEnabled(true);
        textField.setFocusable(true);
        return textField;
    }

    private static JPanel createRoomSelectionInput() {
        JPanel roomInput = new JPanel(new BorderLayout(8, 0));
        roomInput.setOpaque(false);
        roomInput.setPreferredSize(new Dimension(300, 36));

        JTextField txtSoPhong = createInputField("");
        txtSoPhong.setPreferredSize(new Dimension(190, 36));
        txtSoPhong.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSoPhong.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 214, 235), 1),
                new EmptyBorder(6, 10, 6, 10)));

        JButton btnChonPhong = createPrimaryButton("Chọn phòng", 102, 36);

        roomInput.add(txtSoPhong, BorderLayout.CENTER);
        roomInput.add(btnChonPhong, BorderLayout.EAST);
        return roomInput;
    }

    private static JButton createPrimaryButton(String text, int width, int height) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(width, height));
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(BUTTON_TEXT);
        button.setBackground(BUTTON_PRIMARY_BG);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createLineBorder(new Color(188, 207, 233), 1));
        return button;
    }

    private static JButton createGhostButton(String text, int width, int height) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(width, height));
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(BUTTON_TEXT);
        button.setBackground(BUTTON_GHOST_BG);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createLineBorder(new Color(188, 207, 233), 1));
        return button;
    }

    private static JButton createDangerButton(String text, int width, int height) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(width, height));
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(BUTTON_TEXT);
        button.setBackground(BUTTON_DANGER_BG);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createLineBorder(new Color(235, 184, 184), 1));
        return button;
    }

    private static void seedData(DefaultTableModel tableModel) {
        tableModel.addRow(new Object[] { "DDP001", "Nguyễn Văn An", "Trần Thu Hà", "0901234567", "30/03/2026",
                "01/04/2026", "Deluxe",
                "306", 2, "1,000,000", "Đã xác nhận" });
        tableModel.addRow(new Object[] { "DDP002", "Trần Thị Bình", "Nguyễn Minh Quân", "0912345678", "31/03/2026",
                "02/04/2026", "Standard",
                "208", 1, "500,000", "Chờ xác nhận" });
        tableModel.addRow(new Object[] { "DDP003", "Lê Hoàng Long", "Phạm Ngọc Anh", "0933456789", "29/03/2026",
                "30/03/2026", "Suite",
                "501", 3, "2,000,000", "Đã xác nhận" });
        tableModel.addRow(
                new Object[] { "DDP004", "Phạm Minh Khoa", "Trần Thu Hà", "0945566778", "28/03/2026", "29/03/2026",
                        "Superior", "402", 2, "800,000", "Đã hủy" });
    }

    private static class GradientPanel extends JPanel {

        private static final long serialVersionUID = 1L;
        private final Color start;
        private final Color end;

        GradientPanel(Color start, Color end) {
            this.start = start;
            this.end = end;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setPaint(new GradientPaint(0, 0, start, getWidth(), getHeight(), end));
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.dispose();
        }
    }

    private static class RoundedPanel extends JPanel {

        private static final long serialVersionUID = 1L;
        private final int radius;
        private final Color color;

        RoundedPanel(int radius, Color color) {
            this.radius = radius;
            this.color = color;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(color);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2d.setColor(new Color(214, 226, 243));
            g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2d.dispose();
            super.paintComponent(g);
        }
    }
}
