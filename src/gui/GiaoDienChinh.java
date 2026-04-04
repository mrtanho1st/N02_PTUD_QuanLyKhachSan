package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class GiaoDienChinh extends JFrame {

    private static final long serialVersionUID = 1L;

    private static final Color APP_BG = new Color(242, 242, 247);
    private static final Color PANEL_BG = new Color(255, 255, 255);
    private static final Color BORDER = new Color(229, 229, 234);
    private static final Color TEXT_PRIMARY = new Color(28, 28, 30);
    private static final Color TEXT_SECONDARY = new Color(99, 99, 102);
    private static final Color ACCENT = new Color(0, 122, 255);
    private static final Color ACCENT_SOFT = new Color(232, 242, 255);

    private final List<JButton> navButtons = new ArrayList<JButton>();

    public GiaoDienChinh() {
        initUI();
    }

    private void initUI() {
        setTitle("Quản lý khách sạn");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 820);
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBackground(APP_BG);
        root.setBorder(new EmptyBorder(12, 12, 12, 12));

        root.add(createTopBar(), BorderLayout.NORTH);
        root.add(createMainPanel(), BorderLayout.CENTER);

        setContentPane(root);
    }

    private JPanel createTopBar() {
        JPanel bar = new RoundedPanel(18, PANEL_BG);
        bar.setLayout(new BorderLayout());
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(14, 18, 14, 18)));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Imperial Hotel Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Bảng điều khiển vận hành");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(TEXT_SECONDARY);

        left.add(title);
        left.add(Box.createVerticalStrut(2));
        left.add(subtitle);

        JLabel badge = new JLabel("Online", SwingConstants.CENTER);
        badge.setOpaque(true);
        badge.setBackground(ACCENT_SOFT);
        badge.setForeground(ACCENT);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 13));
        badge.setBorder(new EmptyBorder(8, 14, 8, 14));

        bar.add(left, BorderLayout.WEST);
        bar.add(badge, BorderLayout.EAST);
        return bar;
    }

    private JPanel createMainPanel() {
        JPanel main = new JPanel(new BorderLayout(12, 0));
        main.setOpaque(false);

        main.add(createSidebar(), BorderLayout.WEST);

        JPanel right = new JPanel(new BorderLayout(10, 10));
        right.setOpaque(false);
        right.add(createNavPanel(), BorderLayout.NORTH);
        right.add(createContentPanel(), BorderLayout.CENTER);

        main.add(right, BorderLayout.CENTER);
        return main;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new RoundedPanel(18, PANEL_BG);
        sidebar.setLayout(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(16, 14, 16, 14)));

        JLabel user = new JLabel("Xin chào, Tân", SwingConstants.CENTER);
        user.setFont(new Font("Segoe UI", Font.BOLD, 21));
        user.setForeground(TEXT_PRIMARY);
        user.setBorder(new EmptyBorder(6, 0, 12, 0));
        sidebar.add(user, BorderLayout.NORTH);

        JPanel menu = new JPanel();
        menu.setOpaque(false);
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));

        menu.add(createSidebarButton("Tổng quan"));
        menu.add(Box.createVerticalStrut(10));
        menu.add(createSidebarButton("Tài khoản"));
        menu.add(Box.createVerticalStrut(10));
        menu.add(createSidebarButton("Trợ giúp"));

        sidebar.add(menu, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        JButton logout = createSidebarButton("Đăng xuất");
        logout.setBackground(new Color(255, 241, 240));
        logout.setForeground(new Color(215, 0, 21));
        FormInputSupport.configureActionButton(logout);
        bottom.add(logout);
        sidebar.add(bottom, BorderLayout.SOUTH);

        return sidebar;
    }

    private JPanel createNavPanel() {
        JPanel nav = new RoundedPanel(16, PANEL_BG);
        nav.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        nav.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(2, 10, 2, 10)));

        nav.add(createNavButton("Tìm kiếm"));
        nav.add(createNavButton("Đặt phòng"));
        nav.add(createManageNavButton());
        nav.add(createNavButton("Thống kê"));
        nav.add(createNavButton("Khuyến mãi"));
        nav.add(createNavButton("Dịch vụ"));

        if (!navButtons.isEmpty()) {
            setActiveNavButton(navButtons.get(0));
        }
        return nav;
    }

    private JPanel createContentPanel() {
        JPanel content = new RoundedPanel(18, PANEL_BG);
        content.setLayout(new BorderLayout(14, 14));
        content.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(18, 18, 18, 18)));

        JLabel title = new JLabel("Truy cập nhanh");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(TEXT_PRIMARY);
        content.add(title, BorderLayout.NORTH);

        JPanel shortcuts = new JPanel(new GridLayout(2, 3, 12, 12));
        shortcuts.setOpaque(false);
        shortcuts.add(createShortcutCard("Khách hàng", "Quản lý hồ sơ khách", () -> openScreen(new QLKhachHang())));
        shortcuts.add(createShortcutCard("Dịch vụ", "Danh mục dịch vụ", () -> openScreen(new QLDichVu())));
        shortcuts.add(createShortcutCard("Thuế", "Biểu thuế áp dụng", () -> openScreen(new QLThue())));
        shortcuts.add(createShortcutCard("Hóa đơn", "Theo dõi thanh toán", () -> openScreen(new QLHoaDon())));
        shortcuts.add(createShortcutCard("Phòng", "Quản lý trạng thái phòng", () -> openScreen(new QLPhong())));
        shortcuts.add(createShortcutCard("Đặt phòng", "Tạo đặt phòng mới", () -> openScreen(new DatPhong())));

        content.add(shortcuts, BorderLayout.CENTER);

        return content;
    }

    private JPanel createShortcutCard(String title, String desc, Runnable action) {
        JPanel card = new RoundedPanel(16, new Color(249, 249, 251));
        card.setLayout(new BorderLayout(0, 8));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(232, 232, 237), 1),
                new EmptyBorder(14, 14, 14, 14)));

        JLabel lbTitle = new JLabel(title);
        lbTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lbTitle.setForeground(TEXT_PRIMARY);

        JLabel lbDesc = new JLabel(desc);
        lbDesc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbDesc.setForeground(TEXT_SECONDARY);

        JButton open = new JButton("Mở");
        open.setPreferredSize(new Dimension(88, 34));
        open.setFont(new Font("Segoe UI", Font.BOLD, 13));
        open.setBackground(ACCENT_SOFT);
        open.setForeground(ACCENT);
        open.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        FormInputSupport.configureActionButton(open);
        open.addActionListener(e -> action.run());

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(lbTitle);
        text.add(Box.createVerticalStrut(2));
        text.add(lbDesc);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        bottom.setOpaque(false);
        bottom.add(open);

        card.add(text, BorderLayout.NORTH);
        card.add(bottom, BorderLayout.SOUTH);
        return card;
    }

    private JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setForeground(TEXT_PRIMARY);
        button.setBackground(new Color(245, 245, 247));
        button.setBorder(BorderFactory.createEmptyBorder(9, 14, 9, 14));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        FormInputSupport.configureActionButton(button);
        button.addActionListener(e -> setActiveNavButton(button));
        navButtons.add(button);
        return button;
    }

    private JButton createManageNavButton() {
        JButton button = createNavButton("Quản lí");

        JPopupMenu menu = new JPopupMenu();
        menu.setBackground(PANEL_BG);
        menu.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        menu.add(createManageMenuItem("Quản lý khách hàng", () -> openScreen(new QLKhachHang())));
        menu.add(createManageMenuItem("Quản lý dịch vụ", () -> openScreen(new QLDichVu())));
        menu.add(createManageMenuItem("Quản lý thuế", () -> openScreen(new QLThue())));
        menu.add(createManageMenuItem("Quản lý hóa đơn", () -> openScreen(new QLHoaDon())));
        menu.add(createManageMenuItem("Quản lý phòng", () -> openScreen(new QLPhong())));

        button.addActionListener(e -> {
            setActiveNavButton(button);
            menu.show(button, 0, button.getHeight());
        });

        return button;
    }

    private JMenuItem createManageMenuItem(String text, Runnable action) {
        JMenuItem item = new JMenuItem(text);
        item.setOpaque(true);
        item.setFont(new Font("Segoe UI", Font.BOLD, 14));
        item.setBackground(PANEL_BG);
        item.setForeground(TEXT_PRIMARY);
        item.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        item.addActionListener(e -> action.run());
        item.addChangeListener(e -> {
            boolean hover = item.isArmed();
            item.setBackground(hover ? ACCENT_SOFT : PANEL_BG);
            item.setForeground(hover ? ACCENT : TEXT_PRIMARY);
        });
        return item;
    }

    private void setActiveNavButton(JButton activeButton) {
        for (JButton button : navButtons) {
            boolean active = button == activeButton;
            button.setBackground(active ? ACCENT_SOFT : new Color(245, 245, 247));
            button.setForeground(active ? ACCENT : TEXT_PRIMARY);
        }
    }

    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(214, 42));
        button.setMaximumSize(new Dimension(214, 42));
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setForeground(TEXT_PRIMARY);
        button.setBackground(new Color(245, 245, 247));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        FormInputSupport.configureActionButton(button);
        return button;
    }

    private void openScreen(JFrame screen) {
        int currentState = getExtendedState();
        if ((currentState & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH) {
            screen.setExtendedState(JFrame.MAXIMIZED_BOTH);
        } else {
            screen.setBounds(getBounds());
        }
        screen.setVisible(true);
        dispose();
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
            g2d.dispose();
            super.paintComponent(g);
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
