package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JLabel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class ThongTin {

    public static JPanel createPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("Thông tin & Thông báo", SwingConstants.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setBorder(new EmptyBorder(0, 0, 12, 0));
        root.add(title, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Thông báo ca làm", createListPanel(new String[] {
                "Thiếu nhân viên ca sáng: cần 2 người phụ trợ",
                "Yêu cầu đổi ca: Lê Minh Tân đổi ca với Đỗ Thanh Tường và Nguyễn Gia Quân",
                "Nhắc nhở: nhân viên lễ tân Nguyễn Thị Thanh Thư bớt làm trò con bò trong giờ làm việc",
                "Nhắc nhở: cập nhật bảng phân ca tuần tới"
        }));

        tabs.addTab("Quy định mới", createListPanel(new String[] {
                "Quy định check-in: giờ bắt đầu 14:00, check-out 12:00",
                "Chính sách giá mùa cao điểm: tăng 20%",
                "Quy định mới về xử lí tài sản thất lạc"
        }));

        tabs.addTab("Sự kiện", createListPanel(new String[] {
                "Khuyến mãi: Mừng đại lễ 30/4 giảm 36% cho các tất cả hoá đơn (01-30/04)",
                "Khuyến mãi: Giảm 15% cho đoàn trên 36 khách (01-07/05)",
                "Đoàn khách lớn từ công ty XYZ sẽ đến 10/05",
                "Sự kiện nội bộ: Đào tạo dịch vụ 2 giờ vào 15/05",
                "Sự kiện nội bộ: Họp nhân viên vào cuối ca làm việc ngày 5/05"
        }));

        tabs.addTab("Lỗi hệ thống / Bảo trì", createListPanel(new String[] {
                "Bảo trì mạng: tạm ngưng kết nối 02:00 - 03:00 ngày 05/05",
                "Lỗi POS: đang điều tra, một số giao dịch bị trùng",
                "Cập nhật phần mềm: phiên bản 1.2.3 sẽ cài đặt tối nay"
        }));

        root.add(tabs, BorderLayout.CENTER);

        return root;
    }

    private static JScrollPane createListPanel(String[] items) {
        DefaultListModel<String> model = new DefaultListModel<>();
        for (String it : items) {
            model.addElement(it);
        }
        JList<String> list = new JList<>(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        list.setBorder(new EmptyBorder(8, 8, 8, 8));

        JScrollPane sp = new JScrollPane(list);
        sp.setBorder(BorderFactory.createEmptyBorder());
        return sp;
    }
}
