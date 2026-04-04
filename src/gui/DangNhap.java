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
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class DangNhap extends JFrame {

    private static final long serialVersionUID = 1L;

    private static final Color PRIMARY = new Color(201, 168, 106);
    private static final Color PRIMARY_DARK = new Color(94, 74, 43);
    private static final Color TEXT = new Color(231, 224, 206);
    private static final Color BUTTON_TEXT = new Color(231, 224, 206);

    private JPasswordField txtMatKhau;
    private char defaultEchoChar;

    public DangNhap() {
        initUI();
    }

    private void initUI() {
        setTitle("Đăng nhập hệ thống");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(960, 620);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new BackgroundImagePanel("icon/loginBG.jpg", PRIMARY_DARK, PRIMARY);
        root.setLayout(new GridBagLayout());

        JPanel loginCard = createLoginCard();
        root.add(loginCard);

        setContentPane(root);
    }

    private JPanel createLoginCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(520, 500));
        card.setBackground(new Color(28, 31, 36));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(92, 84, 68), 1),
                new EmptyBorder(24, 28, 24, 28)));

        card.add(createCardHeader(), BorderLayout.NORTH);
        card.add(createCardForm(), BorderLayout.CENTER);
        card.add(createCardFooter(), BorderLayout.SOUTH);

        return card;
    }

    private JPanel createCardHeader() {
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel icon = new JLabel("HOTEL", SwingConstants.CENTER);
        icon.setOpaque(true);
        icon.setBackground(new Color(242, 234, 210));
        icon.setForeground(TEXT);
        icon.setFont(new Font("Segoe UI", Font.BOLD, 18));
        icon.setBorder(new EmptyBorder(8, 20, 8, 20));
        icon.setAlignmentX(CENTER_ALIGNMENT);

        JLabel title = new JLabel("ĐĂNG NHẬP", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(TEXT);
        title.setAlignmentX(CENTER_ALIGNMENT);

        JLabel subTitle = new JLabel("Hệ thống quản lý khách sạn", SwingConstants.CENTER);
        subTitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subTitle.setForeground(new Color(171, 157, 129));
        subTitle.setAlignmentX(CENTER_ALIGNMENT);

        header.add(icon);
        header.add(Box.createVerticalStrut(16));
        header.add(title);
        header.add(Box.createVerticalStrut(4));
        header.add(subTitle);
        header.add(Box.createVerticalStrut(14));

        return header;
    }

    private JPanel createCardForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(8, 0, 8, 0);

        JTextField txtTenDangNhap = new JTextField();
        styleInput(txtTenDangNhap);

        txtMatKhau = new JPasswordField();
        styleInput(txtMatKhau);
        defaultEchoChar = txtMatKhau.getEchoChar();

        form.add(createInputGroup("Tên đăng nhập", txtTenDangNhap), gbc);
        gbc.gridy++;
        form.add(createInputGroup("Mật khẩu", txtMatKhau), gbc);
        gbc.gridy++;

        JPanel optionRow = new JPanel(new BorderLayout());
        optionRow.setOpaque(false);

        JCheckBox chkGhiNho = new JCheckBox("Ghi nhớ đăng nhập");
        chkGhiNho.setOpaque(false);
        chkGhiNho.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        chkGhiNho.setForeground(TEXT);

        JCheckBox chkHienMatKhau = new JCheckBox("Hiện mật khẩu");
        chkHienMatKhau.setOpaque(false);
        chkHienMatKhau.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        chkHienMatKhau.setForeground(TEXT);
        chkHienMatKhau.addActionListener(
                e -> txtMatKhau.setEchoChar(chkHienMatKhau.isSelected() ? (char) 0 : defaultEchoChar));

        optionRow.add(chkGhiNho, BorderLayout.WEST);
        optionRow.add(chkHienMatKhau, BorderLayout.EAST);
        form.add(optionRow, gbc);
        gbc.gridy++;

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        buttonRow.setOpaque(false);
        buttonRow.add(createPrimaryButton("Đăng nhập", 160, 42));
        buttonRow.add(createSecondaryButton("Thoát", 140, 42));

        form.add(buttonRow, gbc);
        return form;
    }

    private JPanel createCardFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        footer.setOpaque(false);

        JLabel help = new JLabel("Quên mật khẩu? Liên hệ quản trị viên");
        help.setForeground(new Color(187, 170, 135));
        help.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        footer.add(help);

        return footer;
    }

    private JPanel createInputGroup(String labelText, JTextField input) {
        JPanel group = new JPanel(new BorderLayout(0, 6));
        group.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        label.setForeground(TEXT);

        group.add(label, BorderLayout.NORTH);
        group.add(input, BorderLayout.CENTER);
        return group;
    }

    private void styleInput(JTextField input) {
        input.setPreferredSize(new Dimension(0, 40));
        input.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        input.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(92, 84, 68), 1),
                new EmptyBorder(8, 12, 8, 12)));
        input.setForeground(TEXT);
        configureSafeTextInput(input);
    }

    private void configureSafeTextInput(JTextField textField) {
        textField.setDragEnabled(false);
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                JTextField field = (JTextField) e.getComponent();
                int caretPosition = field.getCaretPosition();
                field.select(caretPosition, caretPosition);
            }
        });
        textField.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isMiddleMouseButton(e)) {
                    e.consume();
                }
            }
        });
    }

    private JButton createPrimaryButton(String text, int width, int height) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(width, height));
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setForeground(BUTTON_TEXT);
        button.setBackground(new Color(110, 89, 56));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createLineBorder(new Color(92, 84, 68), 1));
        return button;
    }

    private JButton createSecondaryButton(String text, int width, int height) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(width, height));
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setForeground(BUTTON_TEXT);
        button.setBackground(new Color(52, 56, 64));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createLineBorder(new Color(92, 84, 68), 1));
        return button;
    }

    private static class BackgroundImagePanel extends JPanel {

        private static final long serialVersionUID = 1L;
        private final Image image;
        private final Color start;
        private final Color end;

        BackgroundImagePanel(String imagePath, Color start, Color end) {
            ImageIcon imageIcon = new ImageIcon(imagePath);
            if (imageIcon.getIconWidth() > 0 && imageIcon.getIconHeight() > 0) {
                image = imageIcon.getImage();
            } else {
                image = null;
            }
            this.start = start;
            this.end = end;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            if (image != null) {
                g2d.drawImage(image, 0, 0, getWidth(), getHeight(), this);
                g2d.setColor(new Color(8, 10, 13, 120));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            } else {
                g2d.setPaint(new GradientPaint(0, 0, start, getWidth(), getHeight(), end));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
            g2d.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            applySystemLookAndFeel();
            new DangNhap().setVisible(true);
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
