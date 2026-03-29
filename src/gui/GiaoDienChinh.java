package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;

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

public class GiaoDienChinh extends JFrame {

    private static final long serialVersionUID = 1L;

    public GiaoDienChinh() {
        initUI();
    }

    private void initUI() {
        setTitle("Quản lý khách sạn");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 600);
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        root.add(createTopPanel(), BorderLayout.NORTH);
        root.add(createMainPanel(), BorderLayout.CENTER);

        setContentPane(root);
    }

    private JPanel createTopPanel() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Color.WHITE);
        top.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));

        JLabel lblTitle = new JLabel("Quản lý thông tin khách sạn", SwingConstants.CENTER);
        lblTitle.setForeground(Color.RED);
        lblTitle.setFont(new Font("Times New Roman", Font.PLAIN, 28));
        lblTitle.setPreferredSize(new Dimension(0, 42));
        top.add(lblTitle, BorderLayout.CENTER);
        return top;
    }

    private JPanel createMainPanel() {
        JPanel main = new JPanel(new BorderLayout());
        JPanel right = new JPanel(new BorderLayout());
        right.add(createNavPanel(), BorderLayout.NORTH);
        right.add(createContentPanel(), BorderLayout.CENTER);

        main.add(createLeftPanel(), BorderLayout.WEST);
        main.add(right, BorderLayout.CENTER);
        return main;
    }

    private JPanel createNavPanel() {
        JPanel nav = new JPanel(new FlowLayout(FlowLayout.LEFT, 28, 7));
        nav.setBackground(Color.WHITE);
        nav.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));

        nav.add(createNavButton("Tìm kiếm"));
        nav.add(createNavButton("Đặt phòng"));
        nav.add(createNavButton("Quản lí"));
        nav.add(createNavButton("Thống kê"));
        nav.add(createNavButton("Khuyến mãi"));
        nav.add(createNavButton("Dịch vụ"));
        return nav;
    }

    private JPanel createLeftPanel() {
        JPanel left = new JPanel(new BorderLayout());
        left.setPreferredSize(new Dimension(150, 0));
        left.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
        left.setBackground(new Color(240, 240, 240));

        JLabel lblUsername = new JLabel("Hi, Tân", SwingConstants.CENTER);
        Font font = new Font("Times New Roman", Font.BOLD, 20);
        lblUsername.setFont(font);
        lblUsername.setForeground(Color.RED);
        lblUsername.setPreferredSize(new Dimension(105, 35));
        lblUsername.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
        left.add(lblUsername, BorderLayout.NORTH);

        JPanel centerButtons = new JPanel();
        centerButtons.setOpaque(false);
        centerButtons.setLayout(new BoxLayout(centerButtons, BoxLayout.Y_AXIS));
        centerButtons.setBorder(BorderFactory.createEmptyBorder(40, 8, 0, 8));

        centerButtons.add(createLeftButton("Thông tin", 120, 35));
        centerButtons.add(Box.createVerticalStrut(18));
        centerButtons.add(createLeftButton("Tài khoản", 120, 35));
        centerButtons.add(Box.createVerticalStrut(18));
        centerButtons.add(createLeftButton("Trợ giúp", 120, 35));
        left.add(centerButtons, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createEmptyBorder(0, 8, 10, 8));
        bottom.add(createLeftButton("Đăng xuất", 140, 35));
        left.add(bottom, BorderLayout.SOUTH);

        return left;
    }

    private JPanel createContentPanel() {
        JPanel content = new BackgroundImagePanel("icon/bg.png");
        content.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 0, 0);

        if (((BackgroundImagePanel) content).hasImage()) {
            JLabel spacer = new JLabel("");
            content.add(spacer, gbc);
        } else {
            JLabel lblBackground = new JLabel("Không tìm thấy ảnh: icon/bg.png");
            lblBackground.setFont(new Font("Times New Roman", Font.ITALIC, 30));
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
            }
        }
    }

    private JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Times New Roman", Font.PLAIN, 24));
        button.setFocusPainted(false);
        return button;
    }

    private JButton createLeftButton(String text, int width, int height) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(width, height));
        button.setFont(new Font("Times New Roman", Font.PLAIN, 24));
        button.setFocusPainted(false);
        return button;
    }

}
