package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import java.awt.CardLayout;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import java.util.HashMap;
import java.util.Map;

public class GiaoDienChinh extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final Color APP_BG = new Color(237, 242, 247);
    private static final Color NAV_ACTIVE = new Color(203, 223, 247);
    private static final Color SIDEBAR_BG = new Color(17, 24, 39);
    private static final Color BUTTON_TEXT = new Color(34, 58, 94);

    private final List<JButton> navButtons = new ArrayList<JButton>();
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private Map<String, JPanel> loadedPanels = new HashMap<>();

    public GiaoDienChinh() {
        initUI();
    }

    private void initUI() {
        setTitle("Quản lý khách sạn");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 800);
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(APP_BG);
        root.setBorder(new EmptyBorder(10, 10, 10, 10));

        root.add(createTopPanel(), BorderLayout.NORTH);
        root.add(createMainPanel(), BorderLayout.CENTER);

        setContentPane(root);
    }

    private JPanel createTopPanel() {
        JPanel top = new GradientPanel(new Color(15, 58, 115), new Color(31, 128, 185));
        top.setLayout(new BorderLayout());
        top.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(12, 47, 88), 1),
                new EmptyBorder(6, 12, 6, 12)));

        JLabel lblTitle = new JLabel("Quản lý thông tin khách sạn", SwingConstants.CENTER);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 31));
        lblTitle.setPreferredSize(new Dimension(0, 54));
        top.add(lblTitle, BorderLayout.CENTER);
        return top;
    }

    private JPanel createMainPanel() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(APP_BG);
        main.setBorder(new EmptyBorder(8, 0, 0, 0));

        JPanel right = new JPanel(new BorderLayout());
        right.setOpaque(false);
        right.add(createNavPanel(), BorderLayout.NORTH);
        right.add(createContentPanel(), BorderLayout.CENTER);

        main.add(createLeftPanel(), BorderLayout.WEST);
        main.add(right, BorderLayout.CENTER);
        return main;
    }

    private JPanel createNavPanel() {
        JPanel nav = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        nav.setBackground(new Color(248, 250, 253));
        nav.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(210, 220, 235)),
                new EmptyBorder(4, 10, 4, 10)));

        nav.add(createNavButton("Hệ thống", new String[] {
                "Phân quyền", "Tạo tài khoản"
        }));

        nav.add(createNavButton("Danh mục", new String[] {
                "Phòng", "Nhân viên", "Khách hàng"
        }));

        nav.add(createNavButton("Xử lí", new String[] {
                "Đặt phòng", "Hóa đơn", "Check-in/Check-out", "Phân công", "Thanh toán"
        }));

        nav.add(createNavButton("Tìm kiếm", new String[] {
                "Hóa đơn", "Khách hàng", "Phòng", "Nhân viên", "Khuyến mãi", "Dịch vụ"
        }));

        nav.add(createNavButton("Cập nhật", new String[] {
                "Khách hàng", "Phòng", "Nhân viên", "Khuyến mãi", "Dịch vụ", "Tài khoản"
        }));

        nav.add(createNavButton("Thống kê", new String[] {
                "Doanh thu theo thời gian", "Doanh thu theo phòng", "Doanh thu theo khách hàng", "Phòng đặt nhiều nhất",
                "Khách hàng điểm cao nhất"
        }));

        nav.add(createNavButton("Báo biểu", new String[] {
                "DS Khách hàng", "DS Nhân viên", "DS Phòng", "DS Khuyến mãi",
                "DS Dịch vụ", "DS Đơn đặt phòng", "DS Hóa đơn"
        }));

        if (!navButtons.isEmpty()) {
            setActiveNavButton(navButtons.get(0));
        }
        return nav;
    }

    private JPanel createLeftPanel() {
        JPanel left = new JPanel(new BorderLayout());
        left.setPreferredSize(new Dimension(160, 0));
        left.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(12, 47, 88), 1),
                new EmptyBorder(10, 10, 10, 10)));
        left.setBackground(SIDEBAR_BG);

        JLabel lblUsername = new JLabel("Hi, Tân", SwingConstants.CENTER);
        lblUsername.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblUsername.setForeground(new Color(226, 232, 240));
        lblUsername.setPreferredSize(new Dimension(190, 46));
        lblUsername.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(59, 130, 246)),
                new EmptyBorder(0, 0, 6, 0)));
        left.add(lblUsername, BorderLayout.NORTH);

        JPanel centerButtons = new JPanel();
        centerButtons.setOpaque(false);
        centerButtons.setLayout(new BoxLayout(centerButtons, BoxLayout.Y_AXIS));
        centerButtons.setBorder(new EmptyBorder(30, 0, 0, 0));

        centerButtons.add(createLeftButton("Thông tin", 186, 42));
        centerButtons.add(Box.createVerticalStrut(12));
        centerButtons.add(createLeftButton("Tài khoản", 186, 42));
        centerButtons.add(Box.createVerticalStrut(12));
        centerButtons.add(createLeftButton("Trợ giúp  ", 186, 42));
        left.add(centerButtons, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setBorder(new EmptyBorder(0, 0, 4, 0));
        JButton btnDangXuat = createDangerButton("Đăng xuất", 186, 42);
        btnDangXuat.addActionListener(e -> {
            dispose();
            new DangNhap().setVisible(true);
        });
        bottom.add(btnDangXuat);
        left.add(bottom, BorderLayout.SOUTH);

        return left;
    }

    private JPanel createContentPanel() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(APP_BG);
        contentPanel.setBorder(BorderFactory.createLineBorder(new Color(12, 47, 88), 1));

        // Panel mặc định hiển thị ảnh nền khi chưa chọn chức năng.
        JPanel defaultPanel = new BackgroundImagePanel("icon/bg.png");
        defaultPanel.setBackground(APP_BG);

        contentPanel.add(defaultPanel, "DEFAULT");
        cardLayout.show(contentPanel, "DEFAULT");

        return contentPanel;
    }

    private JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 18));
        button.setForeground(BUTTON_TEXT);
        button.setBackground(new Color(233, 239, 248));
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> {
            setActiveNavButton(button);
        });
        navButtons.add(button);
        return button;
    }

    private JButton createNavButton(String text, String[] subMenus) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 18));
        button.setForeground(BUTTON_TEXT);
        button.setBackground(new Color(233, 239, 248));
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        for (String sub : subMenus) {
            JMenuItem item = new JMenuItem(sub);
            item.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            item.addActionListener(e -> {
                setActiveNavButton(button);
                openExistingPage(text, sub);
            });
            popupMenu.add(item);
        }

        button.addActionListener(e -> {
            setActiveNavButton(button);
            popupMenu.show(button, 0, button.getHeight());
        });

        navButtons.add(button);
        return button;
    }

    private void openExistingPage(String mainMenu, String subMenu) {
        String panelKey = mainMenu + "/" + subMenu;

        // Nếu panel đã tồn tại, chỉ cần hiển thị nó
        if (loadedPanels.containsKey(panelKey)) {
            cardLayout.show(contentPanel, panelKey);
            return;
        }

        JPanel panel = null;
        if (mainMenu.equals("Hệ thống")) {
            if (subMenu.equals("Phân quyền") || subMenu.equals("Tạo tài khoản"))
                panel = QLTaiKhoan.createPanel();

        } else if (mainMenu.equals("Danh mục")) {
            if (subMenu.equals("Phòng"))
                panel = QLPhong.createPanel();
            else if (subMenu.equals("Hóa đơn"))
                panel = QLHoaDon.createPanel();
            else if (subMenu.equals("Nhân viên"))
                panel = QLNhanVien.createPanel();
            else if (subMenu.equals("Khách hàng"))
                panel = QLKhachHang.createPanel();
        } else if (mainMenu.equals("Xử lí")) {
            if (subMenu.equals("Đặt phòng"))
                panel = DatPhong.createPanel();
            else if (subMenu.equals("Check-in/Check-out"))
                panel = NhanTraPhong.createPanel();
            else if (subMenu.equals("Hóa đơn"))
                panel = QLHoaDon.createPanel();
        } else if (mainMenu.equals("Tìm kiếm")) {
            if (subMenu.equals("Hóa đơn"))
                panel = QLHoaDon.createPanel();
            else if (subMenu.equals("Khách hàng"))
                panel = QLKhachHang.createPanel();
            else if (subMenu.equals("Phòng"))
                panel = QLPhong.createPanel();
            else if (subMenu.equals("Nhân viên"))
                panel = QLNhanVien.createPanel();
            else if (subMenu.equals("Khuyến mãi"))
                panel = GiaoDienKhuyenMai.createPanel();
            else if (subMenu.equals("Dịch vụ"))
                panel = QLDichVu.createPanel();
        } else if (mainMenu.equals("Cập nhật")) {
            if (subMenu.equals("Khách hàng"))
                panel = QLKhachHang.createPanel();
            else if (subMenu.equals("Phòng"))
                panel = QLPhong.createPanel();
            else if (subMenu.equals("Nhân viên"))
                panel = QLNhanVien.createPanel();
            else if (subMenu.equals("Khuyến mãi"))
                panel = GiaoDienKhuyenMai.createPanel();
            else if (subMenu.equals("Dịch vụ"))
                panel = QLDichVu.createPanel();
            else if (subMenu.equals("Tài khoản"))
                panel = QLTaiKhoan.createPanel();
        }

        if (panel != null) {
            contentPanel.add(panel, panelKey);
            loadedPanels.put(panelKey, panel);
            cardLayout.show(contentPanel, panelKey);
        } else {
            JOptionPane.showMessageDialog(this, "Chức năng này chưa có class giao diện.");
        }
    }

    private void setActiveNavButton(JButton activeButton) {
        for (JButton button : navButtons) {
            boolean active = button == activeButton;
            button.setBackground(active ? NAV_ACTIVE : new Color(233, 239, 248));
            button.setForeground(BUTTON_TEXT);
        }
    }

    private JButton createLeftButton(String text, int width, int height) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(width, height));
        button.setFont(new Font("Segoe UI", Font.PLAIN, 19));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setForeground(BUTTON_TEXT);
        button.setBackground(new Color(232, 240, 252));
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setOpaque(true);
        return button;
    }

    private JButton createDangerButton(String text, int width, int height) {
        JButton button = createLeftButton(text, width, height);
        button.setBackground(new Color(252, 230, 230));

        return button;
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
            GradientPaint paint = new GradientPaint(0, 0, start, getWidth(), getHeight(), end);
            g2d.setPaint(paint);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.dispose();
        }
    }

    private static class BackgroundImagePanel extends JPanel {

        private static final long serialVersionUID = 1L;
        private final Image image;

        BackgroundImagePanel(String imagePath) {
            this.image = new ImageIcon(imagePath).getImage();
            setOpaque(true);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image != null && image.getWidth(this) > 0) {
                g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            applySystemLookAndFeel();
            new GiaoDienChinh().setVisible(true);
        });
    }

    private static void applySystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Keep default look and feel.
        }
    }
}
