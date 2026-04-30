package gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import entity.LoaiBaoBieu;
import entity.PhienDangNhap;
import javax.swing.JScrollPane;
import gui.ThongTin;

public class GiaoDienChinh extends JFrame {

    private static final long serialVersionUID = 1L;

    private static final Color APP_BG = new Color(237, 242, 247);
    private static final Color NAV_ACTIVE = new Color(203, 223, 247);
    private static final Color SIDEBAR_BG = new Color(17, 24, 39);
    private static final Color BUTTON_TEXT = new Color(34, 58, 94);
    private static final Color CARD_BORDER = new Color(220, 225, 232);

    private final List<JButton> navButtons = new ArrayList<>();
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private final Map<String, JPanel> loadedPanels = new HashMap<>();

    private JLabel lblAppTitle;

    // Nút menu trái
    private JButton btnTrangChu;
    private JButton btnThongTin;
    private JButton btnTaiKhoan;
    private JButton btnTroGiup;
    private JButton btnDangXuat;

    // Nút menu ngang
    private JButton btnHeThong;
    private JButton btnDanhMuc;
    private JButton btnXuLi;
    private JButton btnTimKiem;
    private JButton btnCapNhat;
    private JButton btnThongKe;
    private JButton btnBaoBieu;

    public GiaoDienChinh() {
        initUI();
    }

    private void initUI() {
        setTitle("Quản lý khách sạn");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(APP_BG);
        root.setBorder(new EmptyBorder(5, 5, 5, 5));

        root.add(createTopPanel(), BorderLayout.NORTH);
        root.add(createMainPanel(), BorderLayout.CENTER);

        setContentPane(root);
    }

    private JPanel createTopPanel() {
        JPanel top = new GradientPanel(new Color(15, 58, 115), new Color(31, 128, 185));
        top.setLayout(new BorderLayout());
        top.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(12, 47, 88), 1),
                new EmptyBorder(10, 16, 10, 16)));
        lblAppTitle = new JLabel("QUẢN LÝ THÔNG TIN KHÁCH SẠN", SwingConstants.CENTER);
        lblAppTitle.setForeground(Color.WHITE);
        lblAppTitle.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblAppTitle.setPreferredSize(new Dimension(0, 50));

        top.add(lblAppTitle, BorderLayout.CENTER);

        return top;
    }

    private void updateAppTitle(String title) {
        lblAppTitle.setText(title.toUpperCase());
    }

    private JPanel createMainPanel() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(APP_BG);
        main.setBorder(new EmptyBorder(8, 0, 0, 0));

        JPanel right = new JPanel(new BorderLayout());
        right.setOpaque(false);

        right.add(createNavPanel(), BorderLayout.NORTH);

        JPanel centerArea = new JPanel(new BorderLayout());
        centerArea.setOpaque(false);
        centerArea.add(createContentPanel(), BorderLayout.CENTER);

        right.add(centerArea, BorderLayout.CENTER);

        main.add(createLeftPanel(), BorderLayout.WEST);
        main.add(right, BorderLayout.CENTER);

        return main;
    }

    private JPanel createPageHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(248, 250, 253));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 1, 1, new Color(210, 220, 235)),
                new EmptyBorder(16, 20, 16, 20)));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        return header;
    }

    private JPanel createNavPanel() {
        JPanel nav = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        nav.setBackground(new Color(248, 250, 253));
        nav.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(210, 220, 235)),
                new EmptyBorder(4, 10, 4, 10)));

        btnHeThong = createNavButton("Hệ thống", new String[] {
                "Phân quyền", "Tạo tài khoản"
        });
        nav.add(btnHeThong);

        btnDanhMuc = createNavButton("Danh mục", new String[] {
                "Phòng", "Nhân viên", "Khách hàng"
        });
        nav.add(btnDanhMuc);

        btnXuLi = createNavButton("Xử lí", new String[] {
                "Đặt phòng", "Hóa đơn", "Check-in/Check-out", "Thanh toán"
        });
        nav.add(btnXuLi);

        btnTimKiem = createNavButton("Tìm kiếm", new String[] {
                "Hóa đơn", "Khách hàng", "Phòng", "Nhân viên", "Khuyến mãi", "Dịch vụ", "Đơn dặt phòng"
        });
        nav.add(btnTimKiem);

        btnCapNhat = createNavButton("Cập nhật", new String[] {
                "Khách hàng", "Phòng", "Nhân viên", "Khuyến mãi", "Dịch vụ", "Đơn đặt phòng"
        });
        nav.add(btnCapNhat);

        btnThongKe = createNavButton("Thống kê", new String[] {
                "Doanh thu theo thời gian", "Doanh thu theo phòng", "Doanh thu theo khách hàng",
                "Phòng đặt nhiều nhất", "Khách hàng điểm cao nhất"
        });
        nav.add(btnThongKe);

        btnBaoBieu = createNavButton("Báo biểu", new String[] {
                "DS Khách hàng", "DS Nhân viên", "DS Phòng", "DS Khuyến mãi",
                "DS Dịch vụ", "DS Đơn đặt phòng", "DS Hóa đơn"
        });
        nav.add(btnBaoBieu);

        if (!navButtons.isEmpty()) {
            setActiveNavButton(navButtons.get(0));
        }

        return nav;
    }

    private JPanel createLeftPanel() {
        JPanel left = new JPanel(new BorderLayout());
        left.setPreferredSize(new Dimension(185, 0));
        left.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(12, 47, 88), 1),
                new EmptyBorder(12, 12, 12, 12)));
        left.setBackground(SIDEBAR_BG);

        String tenNV = "Hi, " + PhienDangNhap.getTenDangNhap();
        JLabel lblUsername = new JLabel(tenNV, SwingConstants.CENTER);
        lblUsername.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblUsername.setForeground(new Color(226, 232, 240));
        lblUsername.setPreferredSize(new Dimension(190, 56));
        lblUsername.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(59, 130, 246)),
                new EmptyBorder(0, 0, 8, 0)));
        left.add(lblUsername, BorderLayout.NORTH);

        JPanel centerButtons = new JPanel();
        centerButtons.setOpaque(false);
        centerButtons.setLayout(new BoxLayout(centerButtons, BoxLayout.Y_AXIS));
        centerButtons.setBorder(new EmptyBorder(30, 0, 0, 0));

        btnTrangChu = createLeftButton("Trang chủ", 150, 44);
        btnThongTin = createLeftButton("Thông tin", 150, 44);
        btnTaiKhoan = createLeftButton("Tài khoản", 150, 44);
        btnTroGiup = createLeftButton("Trợ giúp", 150, 44);

        centerButtons.add(btnTrangChu);
        centerButtons.add(Box.createVerticalStrut(14));
        centerButtons.add(btnThongTin);
        centerButtons.add(Box.createVerticalStrut(14));
        centerButtons.add(btnTaiKhoan);
        centerButtons.add(Box.createVerticalStrut(14));
        centerButtons.add(btnTroGiup);

        left.add(centerButtons, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setBorder(new EmptyBorder(0, 0, 6, 0));

        btnDangXuat = createDangerButton("Đăng xuất", 150, 44);
        bottom.add(btnDangXuat);
        left.add(bottom, BorderLayout.SOUTH);

        return left;
    }

    private JPanel createContentPanel() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(APP_BG);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 1, 1, new Color(210, 220, 235)),
                new EmptyBorder(16, 16, 16, 16)));

        JPanel homePanel = createHomeDashboard();

        contentPanel.add(homePanel, "DEFAULT");
        cardLayout.show(contentPanel, "DEFAULT");

        return contentPanel;
    }

    private JPanel createHomeDashboard() {
        JPanel panel = new JPanel(new BorderLayout(16, 16));
        panel.setBackground(APP_BG);

        JPanel statCards = new JPanel(new GridLayout(1, 4, 16, 16));
        statCards.setOpaque(false);

        statCards.add(createStatCard("Phòng đang thuê", "24", new Color(227, 242, 253)));
        statCards.add(createStatCard("Khách hôm nay", "58", new Color(232, 245, 233)));
        statCards.add(createStatCard("Hóa đơn chờ xử lý", "12", new Color(255, 243, 224)));
        statCards.add(createStatCard("Doanh thu hôm nay", "18.5 triệu", new Color(252, 228, 236)));

        JPanel center = new JPanel(new GridLayout(1, 2, 16, 16));
        center.setOpaque(false);
        center.add(createFeaturePanel("Truy cập nhanh", new String[] {
                "Đặt phòng mới",
                "Lập hóa đơn",
                "Tra cứu khách hàng",
                "Kiểm tra tình trạng phòng"
        }));
        center.add(createFeaturePanel("Thông báo hệ thống", new String[] {
                "3 phòng sắp trả trong hôm nay",
                "2 hóa đơn chưa thanh toán",
                "1 tài khoản nhân viên cần cấp quyền",
                "Có 5 yêu cầu cập nhật thông tin khách hàng"
        }));

        JPanel bottom = createWelcomePanel();

        panel.add(statCards, BorderLayout.NORTH);
        panel.add(center, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER),
                new EmptyBorder(18, 18, 18, 18)));

        JLabel title = new JLabel("Chào mừng bạn đến với hệ thống quản lý khách sạn");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(24, 39, 75));

        JLabel desc = new JLabel("<html>Chọn chức năng từ thanh menu ngang để bắt đầu thao tác. "
                + "Giao diện này giữ nguyên khung menu và chỉ thay đổi tiêu đề cùng nội dung ở giữa.</html>");
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        desc.setForeground(new Color(90, 105, 130));
        desc.setBorder(new EmptyBorder(8, 0, 0, 0));

        panel.add(title, BorderLayout.NORTH);
        panel.add(desc, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatCard(String title, String value, Color bgColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER),
                new EmptyBorder(18, 18, 18, 18)));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblTitle.setForeground(new Color(70, 85, 110));

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblValue.setForeground(new Color(24, 39, 75));

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);

        return card;
    }

    private JPanel createFeaturePanel(String title, String[] items) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER),
                new EmptyBorder(18, 18, 18, 18)));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(24, 39, 75));
        panel.add(lblTitle, BorderLayout.NORTH);

        JPanel listPanel = new JPanel();
        listPanel.setOpaque(false);
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBorder(new EmptyBorder(12, 0, 0, 0));

        for (String item : items) {
            JLabel label = new JLabel("• " + item);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            label.setForeground(new Color(70, 85, 110));
            label.setBorder(new EmptyBorder(6, 0, 6, 0));
            listPanel.add(label);
        }

        panel.add(listPanel, BorderLayout.CENTER);
        return panel;
    }

    private JButton createNavButton(String text, String[] subMenus) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 17));
        button.setForeground(BUTTON_TEXT);
        button.setBackground(new Color(233, 239, 248));
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        for (String sub : subMenus) {
            JMenuItem item = new JMenuItem(sub);
            item.setFont(new Font("Segoe UI", Font.PLAIN, 15));
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

    private JButton createLeftButton(String text, int width, int height) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(width, height));
        button.setMinimumSize(new Dimension(width, height));
        button.setMaximumSize(new Dimension(width, height));
        button.setFont(new Font("Segoe UI", Font.PLAIN, 18));
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

    public void showHomePage() {
        cardLayout.show(contentPanel, "DEFAULT");
        updateAppTitle("Quản lý thông tin khách sạn");

        if (!navButtons.isEmpty()) {
            setActiveNavButton(navButtons.get(0));
        }
    }

    public void logout() {
        dispose();
        new DangNhap().setVisible(true);
    }

    public boolean confirmLogout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn đăng xuất không?",
                "Xác nhận đăng xuất",
                JOptionPane.YES_NO_OPTION);
        return confirm == JOptionPane.YES_OPTION;
    }

    public void showInfoMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    public void openPage(String mainMenu, String subMenu) {
        openExistingPage(mainMenu, subMenu);
    }

    private void openExistingPage(String mainMenu, String subMenu) {
        String panelKey = mainMenu + "/" + subMenu;

        if (loadedPanels.containsKey(panelKey)) {
            cardLayout.show(contentPanel, panelKey);
            return;
        }

        JPanel panel = null;

        if (mainMenu.equals("Hệ thống")) {
            if (subMenu.equals("Phân quyền")) {
                panel = QLPhanQuyen.createPanel();
            } else if (subMenu.equals("Tạo tài khoản")) {
                panel = TaoTaiKhoan.createPanel();
            }

        } else if (mainMenu.equals("Danh mục")) {
            if (subMenu.equals("Phòng")) {
                panel = QLPhong.createPanel();
            } else if (subMenu.equals("Nhân viên")) {
                panel = QLNhanVien.createPanel();
            } else if (subMenu.equals("Khách hàng")) {
                panel = QLKhachHang.createPanel();
            }

        } else if (mainMenu.equals("Xử lí")) {
            if (subMenu.equals("Đặt phòng")) {
                panel = DatPhong.createPanel();
            } else if (subMenu.equals("Check-in/Check-out")) {
                panel = CheckInCheckOut.createPanel();
            } else if (subMenu.equals("Hóa đơn")) {
                panel = QLHoaDon.createPanel();
            } else if (subMenu.equals("Thanh toán")) {
                panel = ThanhToan.createPanel();
            }

        } else if (mainMenu.equals("Tìm kiếm")) {
            if (subMenu.equals("Hóa đơn")) {
                panel = QLHoaDon.createPanel();
            } else if (subMenu.equals("Khách hàng")) {
                panel = QLKhachHang.createPanel();
            } else if (subMenu.equals("Phòng")) {
                panel = QLPhong.createPanel();
            } else if (subMenu.equals("Nhân viên")) {
                panel = QLNhanVien.createPanel();
            } else if (subMenu.equals("Khuyến mãi")) {
                panel = QLKhuyenMai.createPanel();
            } else if (subMenu.equals("Dịch vụ")) {
                panel = QLDichVu.createPanel();
            } else if (subMenu.equals("Đơn dặt phòng")) {
                panel = QLDonDatPhong.createPanel();
            }

        } else if (mainMenu.equals("Cập nhật")) {
            if (subMenu.equals("Khách hàng")) {
                panel = QLKhachHang.createPanel();
            } else if (subMenu.equals("Phòng")) {
                panel = QLPhong.createPanel();
            } else if (subMenu.equals("Nhân viên")) {
                panel = QLNhanVien.createPanel();
            } else if (subMenu.equals("Khuyến mãi")) {
                panel = QLKhuyenMai.createPanel();
            } else if (subMenu.equals("Dịch vụ")) {
                panel = QLDichVu.createPanel();
            } else if (subMenu.equals("Đơn đặt phòng")) {
                panel = QLDonDatPhong.createPanel();
            }

        } else if (mainMenu.equals("Thống kê")) {
            showInfoMessage("Chức năng " + subMenu + " đang phát triển.");
            return;

        } else if (mainMenu.equals("Báo biểu"))

        {
            if (subMenu.equals("DS Khách hàng")) {
                panel = BaoBieu.createPanel(LoaiBaoBieu.KHACH_HANG);
            } else if (subMenu.equals("DS Nhân viên")) {
                panel = BaoBieu.createPanel(LoaiBaoBieu.NHAN_VIEN);
            } else if (subMenu.equals("DS Phòng")) {
                panel = BaoBieu.createPanel(LoaiBaoBieu.PHONG);
            } else if (subMenu.equals("DS Khuyến mãi")) {
                panel = BaoBieu.createPanel(LoaiBaoBieu.KHUYEN_MAI);
            } else if (subMenu.equals("DS Dịch vụ")) {
                panel = BaoBieu.createPanel(LoaiBaoBieu.DICH_VU);
            } else if (subMenu.equals("DS Đơn đặt phòng")) {
                panel = BaoBieu.createPanel(LoaiBaoBieu.DON_DAT_PHONG);
            } else if (subMenu.equals("DS Hóa đơn")) {
                panel = BaoBieu.createPanel(LoaiBaoBieu.HOA_DON);
            }
        }

        if (panel != null) {
            panel.setBorder(BorderFactory.createEmptyBorder());
            contentPanel.add(panel, panelKey);
            loadedPanels.put(panelKey, panel);
            cardLayout.show(contentPanel, panelKey);
            updateAppTitle("Quản lý " + subMenu);
        } else {
            JOptionPane.showMessageDialog(this, "Chức năng này chưa có class giao diện.");
        }
    }

    public void setActiveNavButton(JButton activeButton) {
        for (JButton button : navButtons) {
            boolean active = button == activeButton;
            button.setBackground(active ? NAV_ACTIVE : new Color(233, 239, 248));
            button.setForeground(BUTTON_TEXT);
        }
    }

    public JButton getBtnTrangChu() {
        return btnTrangChu;
    }

    public JButton getBtnThongTin() {
        return btnThongTin;
    }

    public JButton getBtnTaiKhoan() {
        return btnTaiKhoan;
    }

    public JButton getBtnTroGiup() {
        return btnTroGiup;
    }

    public JButton getBtnDangXuat() {
        return btnDangXuat;
    }

    public JButton getBtnHeThong() {
        return btnHeThong;
    }

    public JButton getBtnDanhMuc() {
        return btnDanhMuc;
    }

    public JButton getBtnXuLi() {
        return btnXuLi;
    }

    public JButton getBtnTimKiem() {
        return btnTimKiem;
    }

    public JButton getBtnCapNhat() {
        return btnCapNhat;
    }

    public JButton getBtnThongKe() {
        return btnThongKe;
    }

    public JButton getBtnBaoBieu() {
        return btnBaoBieu;
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

    public static void applySystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Keep default look and feel
        }
    }

    public void moThanhToanTheoDon(String maDDP) {
        String panelKey = "Xử lí/Thanh toán";

        JPanel panel = loadedPanels.get(panelKey);

        if (panel == null) {
            panel = ThanhToan.createPanel();
            panel.setBorder(BorderFactory.createEmptyBorder());
            contentPanel.add(panel, panelKey);
            loadedPanels.put(panelKey, panel);
        }

        cardLayout.show(contentPanel, panelKey);
        updateAppTitle("Quản lý Thanh toán");

        if (panel instanceof ThanhToan) {
            ThanhToan thanhToanPanel = (ThanhToan) panel;
            thanhToanPanel.loadDonThanhToan(maDDP);
        }
    }
}