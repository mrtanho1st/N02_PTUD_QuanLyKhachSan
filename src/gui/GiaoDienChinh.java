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

public class GiaoDienChinh extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final Color APP_BG = new Color(237, 242, 247);
    private static final Color NAV_ACTIVE = new Color(203, 223, 247);
    private static final Color SIDEBAR_BG = new Color(17, 24, 39);
    private static final Color BUTTON_TEXT = new Color(34, 58, 94);

    private final List<JButton> navButtons = new ArrayList<JButton>();

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

        nav.add(createNavButton("Tìm kiếm"));
        nav.add(createNavButton("Đặt phòng"));
        nav.add(createNavButton("Quản lí"));
        nav.add(createNavButton("Thống kê"));
        nav.add(createNavButton("Khuyến mãi"));
        nav.add(createNavButton("Dịch vụ"));

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
        bottom.add(createDangerButton("Đăng xuất", 186, 42));
        left.add(bottom, BorderLayout.SOUTH);

        return left;
    }

    private JPanel createContentPanel() {
        JPanel content = new BackgroundImagePanel("icon/bg.png");
        content.setLayout(new GridBagLayout());
        content.setBorder(BorderFactory.createLineBorder(new Color(12, 47, 88), 1));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;

        if (!((BackgroundImagePanel) content).hasImage()) {
            JLabel lblBackground = new JLabel("Không tìm thấy ảnh: icon/bg.png");
            lblBackground.setFont(new Font("Segoe UI", Font.ITALIC, 24));
            content.add(lblBackground, gbc);
        }

        return content;
    }

    private static class BackgroundImagePanel extends JPanel {

        private static final long serialVersionUID = 1L;
        private final Image image;

        BackgroundImagePanel(String imagePath) {
            ImageIcon imageIcon = new ImageIcon(imagePath);
            if (imageIcon.getIconWidth() > 0 && imageIcon.getIconHeight() > 0) {
                image = imageIcon.getImage();
            } else {
                image = null;
                setBackground(new Color(220, 220, 220));
            }
        }

        boolean hasImage() {
            return image != null;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image != null) {
                g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(new Color(9, 24, 45, 90));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        }
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            applySystemLookAndFeel();
            new QLKhachHang().setVisible(true);
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
