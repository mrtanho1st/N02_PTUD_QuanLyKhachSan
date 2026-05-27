package gui;

import java.io.FileOutputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.net.URL;

import com.itextpdf.text.Image;

public class HoaDonPdfExporter {

    private static final NumberFormat moneyFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    private static final String BANK_ID = "970428"; // NamABank
    private static final String ACCOUNT_NO = "0787702900";
    private static final String ACCOUNT_NAME = "NGUYEN THI THANH THU";

    public static void exportHoaDon(
            String filePath,
            Object[] thongTinHD,
            List<Object[]> dsPhong,
            List<Object[]> dsDichVu) throws Exception {
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        BaseFont baseFont = BaseFont.createFont(
                "C:/Windows/Fonts/arial.ttf",
                BaseFont.IDENTITY_H,
                BaseFont.EMBEDDED);

        Font titleFont = new Font(baseFont, 18, Font.BOLD);
        Font headerFont = new Font(baseFont, 12, Font.BOLD);
        Font normalFont = new Font(baseFont, 11, Font.NORMAL);
        Font boldFont = new Font(baseFont, 11, Font.BOLD);

        String maHD = toText(thongTinHD[0]);
        String ngayThanhToan = thongTinHD[1] == null ? "" : dateFormat.format(thongTinHD[1]);

        String tenKH = toText(thongTinHD[2]);
        String cccd = toText(thongTinHD[3]);
        String sdt = toText(thongTinHD[4]);

        String maNVLapDon = toText(thongTinHD[5]);
        String tenNVLapDon = toText(thongTinHD[6]);
        String maNVLapHoaDon = toText(thongTinHD[7]);
        String tenNVLapHoaDon = toText(thongTinHD[8]);

        Date ngayNhanDate = toDate(thongTinHD[9]);
        Date ngayTraDate = toDate(thongTinHD[10]);

        String ngayNhan = ngayNhanDate == null ? "" : dateFormat.format(ngayNhanDate);
        String ngayTra = ngayTraDate == null ? "" : dateFormat.format(ngayTraDate);
        String thoiGianLuuTru = formatThoiGianLuuTru(ngayNhanDate, ngayTraDate);

        double tienCoc = toDouble(thongTinHD[12]);
        double tongTien = toDouble(thongTinHD[13]);

        double tyLeThue = 0;
        if (thongTinHD.length > 15) {
            tyLeThue = toDouble(thongTinHD[15]);
        }

        String maKM = "";
        String tenKhuyenMai = "";
        double tyLeGiamGia = 0;

        if (thongTinHD.length > 16) {
            maKM = toText(thongTinHD[16]);
        }

        if (thongTinHD.length > 17) {
            tenKhuyenMai = toText(thongTinHD[17]);
        }

        if (thongTinHD.length > 18) {
            tyLeGiamGia = toDouble(thongTinHD[18]);
        }

        double tongTienPhong = 0;
        for (Object[] p : dsPhong) {
            tongTienPhong += tinhTienPhongTheoThoiGian(ngayNhanDate, ngayTraDate, toDouble(p[3]));
        }

        double tongTienDV = 0;
        for (Object[] dv : dsDichVu)
            tongTienDV += toDouble(dv[4]);

        double tongTruocGiam = tongTienPhong + tongTienDV;

        double giamGia = tongTruocGiam * tyLeGiamGia / 100.0;

        double tienSauGiam = tongTruocGiam - giamGia;
        if (tienSauGiam < 0) {
            tienSauGiam = 0;
        }

        double thue = tienSauGiam * tyLeThue / 100.0;
        // Tính tiền phạt: so sánh ngày trả (ngayTraDate) và ngày lập hóa đơn
        // (thongTinHD[1])
        double tienPhat = 0;
        try {
            Date ngayLapDate = (Date) thongTinHD[1];

            if (ngayTraDate != null && ngayLapDate != null && ngayLapDate.after(ngayTraDate)) {
                long minutesOver = (ngayLapDate.getTime() - ngayTraDate.getTime()) / 60000L;

                if (minutesOver < 0) {
                    minutesOver = 0;
                }

                for (Object[] p : dsPhong) {
                    double donGia = toDouble(p[3]);
                    double thanhTien = tinhTienPhongTheoThoiGian(ngayNhanDate, ngayTraDate, donGia);

                    double phiPhat = 0;

                    if (minutesOver <= 30) {
                        phiPhat = 0;
                    } else if (minutesOver <= 120) {
                        phiPhat = 0.10 * thanhTien;
                    } else if (minutesOver <= 240) {
                        phiPhat = 0.30 * thanhTien;
                    } else if (minutesOver <= 360) {
                        phiPhat = 0.50 * thanhTien;
                    } else {
                        phiPhat = donGia; // 100% = 1 ngày
                    }

                    tienPhat += phiPhat;
                }
            }
        } catch (Exception ex) {
            // ignore
        }

        tongTruocGiam = tongTienPhong + tongTienDV + tienPhat;
        giamGia = tongTruocGiam * tyLeGiamGia / 100.0;
        tienSauGiam = tongTruocGiam - giamGia;

        if (tienSauGiam < 0) {
            tienSauGiam = 0;
        }

        thue = tienSauGiam * tyLeThue / 100.0;
        tongTien = tongTruocGiam;
        // ===== HEADER =====
        Paragraph hotel = new Paragraph("KHÁCH SẠN IMPERIAL", titleFont);
        hotel.setAlignment(Element.ALIGN_CENTER);
        document.add(hotel);

        Paragraph address = new Paragraph(
                "Địa chỉ: 12 Nguyễn Huệ, Quận 1, TP.HCM",
                normalFont);
        address.setAlignment(Element.ALIGN_CENTER);
        document.add(address);

        Paragraph hotline = new Paragraph(
                "Hotline: 0909 999 999",
                normalFont);
        hotline.setAlignment(Element.ALIGN_CENTER);
        document.add(hotline);

        Paragraph title = new Paragraph("HÓA ĐƠN THANH TOÁN", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingBefore(10);
        title.setSpacingAfter(12);
        document.add(title);

        PdfPTable topInfo = new PdfPTable(2);
        topInfo.setWidthPercentage(100);
        topInfo.setWidths(new float[] { 1, 1 });

        addNoBorderCell(topInfo, "Mã hóa đơn: " + maHD, boldFont);
        addNoBorderCell(topInfo, "Ngày thanh toán: " + ngayThanhToan, boldFont);

        document.add(topInfo);

        // ===== THÔNG TIN KHÁCH / NHÂN VIÊN =====
        PdfPTable info = new PdfPTable(2);
        info.setWidthPercentage(100);
        info.setSpacingBefore(10);
        info.setSpacingAfter(10);

        // Thông tin theo thứ tự và nhãn giống `HoaDonDialog`
        addNoBorderCell(info, "Tên khách hàng: " + tenKH, normalFont);
        addNoBorderCell(info, "NV lập hóa đơn: " + formatNhanVien(maNVLapHoaDon, tenNVLapHoaDon), normalFont);

        addNoBorderCell(info, "CCCD: " + cccd, normalFont);
        addNoBorderCell(info, "SĐT: " + sdt, normalFont);

        addNoBorderCell(info, "Ngày nhận phòng: " + ngayNhan, normalFont);
        addNoBorderCell(info, "Ngày trả phòng: " + ngayTra, normalFont);

        addNoBorderCell(info, "Thời gian lưu trú: " + thoiGianLuuTru, normalFont);

        addNoBorderCell(info, "", normalFont);

        document.add(info);

        // ===== BẢNG PHÒNG =====
        Paragraph phongTitle = new Paragraph("THÔNG TIN PHÒNG SỬ DỤNG", headerFont);
        phongTitle.setSpacingBefore(8);
        phongTitle.setSpacingAfter(6);
        document.add(phongTitle);

        PdfPTable tblPhong = new PdfPTable(5);
        tblPhong.setWidthPercentage(100);
        tblPhong.setWidths(new float[] { 0.8f, 1.5f, 1.2f, 1.2f, 1.5f });

        addHeaderCell(tblPhong, "STT", boldFont);
        addHeaderCell(tblPhong, "Tên phòng", boldFont);
        addHeaderCell(tblPhong, "Thời gian lưu trú", boldFont);
        addHeaderCell(tblPhong, "Đơn giá", boldFont);
        addHeaderCell(tblPhong, "Thành tiền", boldFont);

        for (int i = 0; i < dsPhong.size(); i++) {
            Object[] p = dsPhong.get(i);
            double thanhTien = tinhTienPhongTheoThoiGian(ngayNhanDate, ngayTraDate, toDouble(p[3]));
            addBodyCell(tblPhong, String.valueOf(i + 1), normalFont);
            addBodyCell(tblPhong, toText(p[0]) + " - " + toText(p[1]), normalFont);
            addBodyCell(tblPhong, thoiGianLuuTru, normalFont);
            addBodyCell(tblPhong, formatMoney(toDouble(p[3])) + " VNĐ", normalFont);
            addBodyCell(tblPhong, formatMoney(thanhTien) + " VNĐ", normalFont);
        }

        document.add(tblPhong);

        // ===== BẢNG DỊCH VỤ =====
        Paragraph dvTitle = new Paragraph("DỊCH VỤ THUÊ THÊM", headerFont);
        dvTitle.setSpacingBefore(10);
        dvTitle.setSpacingAfter(6);
        document.add(dvTitle);

        PdfPTable tblDV = new PdfPTable(5);
        tblDV.setWidthPercentage(100);
        tblDV.setWidths(new float[] { 0.8f, 2.5f, 1.2f, 1.2f, 1.5f });

        addHeaderCell(tblDV, "STT", boldFont);
        addHeaderCell(tblDV, "Tên dịch vụ", boldFont);
        addHeaderCell(tblDV, "Số lượng", boldFont);
        addHeaderCell(tblDV, "Đơn giá", boldFont);
        addHeaderCell(tblDV, "Thành tiền", boldFont);

        for (int i = 0; i < dsDichVu.size(); i++) {
            Object[] dv = dsDichVu.get(i);

            addBodyCell(tblDV, String.valueOf(i + 1), normalFont);
            addBodyCell(tblDV, toText(dv[0]) + " - " + toText(dv[1]), normalFont);
            addBodyCell(tblDV, String.valueOf(toInt(dv[2])), normalFont);
            addBodyCell(tblDV, formatMoney(toDouble(dv[3])) + " VNĐ", normalFont);
            addBodyCell(tblDV, formatMoney(toDouble(dv[4])) + " VNĐ", normalFont);
        }

        document.add(tblDV);

        // ===== TỔNG TIỀN =====
        PdfPTable tongKet = new PdfPTable(2);
        tongKet.setWidthPercentage(45);
        tongKet.setHorizontalAlignment(Element.ALIGN_RIGHT);
        tongKet.setSpacingBefore(12);

        addNoBorderCell(tongKet, "Tiền phòng:", boldFont);
        addNoBorderCell(tongKet, formatMoney(tongTienPhong) + " VNĐ", normalFont);

        addNoBorderCell(tongKet, "Tiền dịch vụ:", boldFont);
        addNoBorderCell(tongKet, formatMoney(tongTienDV) + " VNĐ", normalFont);

        addNoBorderCell(tongKet, "Tiền cọc:", boldFont);
        addNoBorderCell(tongKet, formatMoney(tienCoc) + " VNĐ", normalFont);

        addNoBorderCell(tongKet, "Phí phạt:", boldFont);
        addNoBorderCell(tongKet, formatMoney(tienPhat) + " VNĐ", normalFont);

        // addNoBorderCell(tongKet, "Khuyến mãi:", boldFont);
        // if (!maKM.isBlank()) {
        // addNoBorderCell(tongKet, maKM + " - " + tenKhuyenMai, normalFont);
        // } else {
        // addNoBorderCell(tongKet, "Không áp dụng", normalFont);
        // }

        addNoBorderCell(tongKet, "Giảm giá:", boldFont);
        if (tyLeGiamGia > 0) {
            addNoBorderCell(tongKet,
                    formatMoney(giamGia) + " VNĐ (" + formatMoney(tyLeGiamGia) + "%)",
                    normalFont);
        } else {
            addNoBorderCell(tongKet, "0 VNĐ", normalFont);
        }

        addNoBorderCell(tongKet, "Thuế:", boldFont);
        if (tyLeThue > 0) {
            addNoBorderCell(tongKet,
                    formatMoney(thue) + " VNĐ (" + formatMoney(tyLeThue) + "%)",
                    normalFont);
        } else {
            addNoBorderCell(tongKet, "0 VNĐ", normalFont);
        }

        addNoBorderCell(tongKet, "Tổng tiền:", boldFont);
        addNoBorderCell(tongKet, formatMoney(tongTien) + " VNĐ", boldFont);

        document.add(tongKet);

        // ===== QR CHUYỂN KHOẢN =====
        Paragraph qrTitle = new Paragraph("\nQR CHUYỂN KHOẢN", headerFont);
        qrTitle.setAlignment(Element.ALIGN_CENTER);
        document.add(qrTitle);

        String qrUrl = taoLinkVietQR(tongTien, "Thanh toan " + maHD);
        Image qrImage = Image.getInstance(new URL(qrUrl));
        qrImage.scaleToFit(140, 140);
        qrImage.setAlignment(Element.ALIGN_CENTER);
        document.add(qrImage);

        Paragraph qrInfo = new Paragraph(
                "Ngân hàng: NamABank\n"
                        + "Số tài khoản: 0787702900\n"
                        + "Chủ tài khoản: NGUYEN THI THANH THU",
                normalFont);
        qrInfo.setAlignment(Element.ALIGN_CENTER);
        qrInfo.setSpacingBefore(6);
        document.add(qrInfo);

        document.close();
    }

    private static String formatNhanVien(String maNV, String tenNV) {
        if (maNV == null)
            maNV = "";
        if (tenNV == null)
            tenNV = "";

        maNV = maNV.trim();
        tenNV = tenNV.trim();

        if (maNV.isEmpty() && tenNV.isEmpty()) {
            return "";
        }

        if (maNV.isEmpty()) {
            return tenNV;
        }

        if (tenNV.isEmpty()) {
            return maNV;
        }

        return maNV + " - " + tenNV;
    }

    private static void addNoBorderCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setPadding(4);
        table.addCell(cell);
    }

    private static int toInt(Object value) {
        if (value == null) {
            return 0;
        }

        if (value instanceof Number) {
            return ((Number) value).intValue();
        }

        try {
            return Integer.parseInt(value.toString().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static void addInfoCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setPadding(5);
        table.addCell(cell);
    }

    private static void addHeaderCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(6);
        table.addCell(cell);
    }

    private static void addBodyCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        table.addCell(cell);
    }

    private static String toText(Object value) {
        return value == null ? "" : value.toString();
    }

    private static double toDouble(Object value) {
        if (value == null) {
            return 0;
        }

        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }

        try {
            return Double.parseDouble(value.toString().replace(",", "").trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static String formatMoney(double amount) {
        return moneyFormat.format(amount);
    }

    private static Date toDate(Object value) {
        if (value instanceof Date) {
            return (Date) value;
        }

        return null;
    }

    private static String formatThoiGianLuuTru(Date ngayNhan, Date ngayTra) {
        if (ngayNhan == null || ngayTra == null) {
            return "";
        }

        long minutes = Duration.between(ngayNhan.toInstant(), ngayTra.toInstant()).toMinutes();
        if (minutes < 0) {
            minutes = 0;
        }

        long totalHours = minutes / 60;
        if (minutes % 60 >= 30) {
            totalHours++;
        }

        long soNgay = totalHours / 24;
        long soGio = totalHours % 24;

        return soNgay + " ngày, " + soGio + " giờ";
    }

    private static double tinhTienPhongTheoThoiGian(Date ngayNhan, Date ngayTra, double giaPhong) {
        if (ngayNhan == null || ngayTra == null || ngayTra.before(ngayNhan)) {
            return 0;
        }

        long totalMinutes = (ngayTra.getTime() - ngayNhan.getTime()) / 60000L;

        if (totalMinutes <= 0) {
            return 0;
        }

        long fullDays = totalMinutes / 1440;
        long remainderMinutes = totalMinutes % 1440;
        double base = giaPhong / 24.0;
        double tongTien = fullDays * giaPhong;

        if (remainderMinutes > 0) {
            double hours = Math.ceil(remainderMinutes / 60.0);
            double multiplier;

            if (hours <= 2) {
                multiplier = 4.0;
            } else if (hours <= 6) {
                multiplier = 3.0;
            } else if (hours <= 12) {
                multiplier = 2.2;
            } else {
                multiplier = 1.5;
            }

            double tienPhanDu = hours * base * multiplier;
            tienPhanDu = Math.min(tienPhanDu, giaPhong);
            tongTien += tienPhanDu;
        }

        return tongTien;
    }

    private static String taoLinkVietQR(double soTien, String noiDung) {
        String amount = String.valueOf((long) soTien);

        String cleanNoiDung = noiDung
                .replaceAll("[^a-zA-Z0-9 ]", "")
                .replaceAll("\\s+", " ")
                .trim();

        if (cleanNoiDung.length() > 50) {
            cleanNoiDung = cleanNoiDung.substring(0, 50);
        }

        return "https://img.vietqr.io/image/"
                + BANK_ID + "-"
                + ACCOUNT_NO + "-compact2.png"
                + "?amount=" + amount
                + "&addInfo=" + cleanNoiDung.replace(" ", "%20")
                + "&accountName=" + ACCOUNT_NAME.replace(" ", "%20");
    }
}
