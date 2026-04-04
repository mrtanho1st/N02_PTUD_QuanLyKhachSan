IF DB_ID('HotelManagement') IS NULL
BEGIN
    CREATE DATABASE HotelManagement;
END;
GO

USE HotelManagement;
GO

IF OBJECT_ID('dbo.KhachHang', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.KhachHang (
        MaKH NVARCHAR(20) NOT NULL PRIMARY KEY,
        HoTen NVARCHAR(100) NOT NULL,
        SoDienThoai NVARCHAR(20) NOT NULL,
        Email NVARCHAR(120) NULL,
        CCCD NVARCHAR(20) NULL,
        PhanLoai NVARCHAR(30) NOT NULL,
        Diem INT NOT NULL DEFAULT 0
    );
END;
GO

IF OBJECT_ID('dbo.DichVu', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.DichVu (
        MaDV NVARCHAR(20) NOT NULL PRIMARY KEY,
        TenDV NVARCHAR(120) NOT NULL,
        NhomDV NVARCHAR(50) NOT NULL,
        DonViTinh NVARCHAR(30) NOT NULL,
        DonGia DECIMAL(18,2) NOT NULL,
        TrangThai NVARCHAR(30) NOT NULL
    );
END;
GO

IF OBJECT_ID('dbo.Thue', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.Thue (
        MaThue NVARCHAR(20) NOT NULL PRIMARY KEY,
        TenThue NVARCHAR(120) NOT NULL,
        MoTa NVARCHAR(255) NULL,
        TyLeThue DECIMAL(5,2) NOT NULL,
        TrangThai NVARCHAR(30) NOT NULL,
        NgayHieuLuc DATE NOT NULL,
        NgayCapNhat DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME()
    );
END;
GO

IF OBJECT_ID('dbo.HoaDon', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.HoaDon (
        MaHoaDon NVARCHAR(20) NOT NULL PRIMARY KEY,
        KhachHang NVARCHAR(100) NOT NULL,
        NhanVien NVARCHAR(100) NOT NULL,
        MaDon NVARCHAR(20) NOT NULL,
        MaThue NVARCHAR(20) NOT NULL,
        NgayLap DATE NOT NULL,
        TongTien DECIMAL(18,2) NOT NULL
    );
END;
GO

;WITH SeedKhachHang AS (
    SELECT 'KH001' AS MaKH, N'Nguyễn Văn An' AS HoTen, '0901234567' AS SoDienThoai, 'an.nguyen@gmail.com' AS Email, '079204001111' AS CCCD, N'VIP' AS PhanLoai, 2450 AS Diem
    UNION ALL SELECT 'KH002', N'Trần Thị Bình', '0912345678', 'binh.tran@gmail.com', '079204002222', N'Thân thiết', 1280
    UNION ALL SELECT 'KH003', N'Lê Hoàng Long', '0933456789', 'long.le@gmail.com', '079204003333', N'Mới', 120
    UNION ALL SELECT 'KH004', N'Phạm Minh Khoa', '0945566778', 'khoa.pham@gmail.com', '079204004444', N'Thân thiết', 860
    UNION ALL SELECT 'KH005', N'Đỗ Gia Hân', '0967788990', 'han.do@gmail.com', '079204005555', N'VIP', 3010
    UNION ALL SELECT 'KH006', N'Bùi Thanh Tùng', '0971122334', 'tung.bui@gmail.com', '079204006666', N'Thân thiết', 740
    UNION ALL SELECT 'KH007', N'Ngô Hồng Nhung', '0982233445', 'nhung.ngo@gmail.com', '079204007777', N'Mới', 90
    UNION ALL SELECT 'KH008', N'Vũ Quốc Bảo', '0903344556', 'bao.vu@gmail.com', '079204008888', N'VIP', 3560
    UNION ALL SELECT 'KH009', N'Hoàng Thu Trang', '0914455667', 'trang.hoang@gmail.com', '079204009999', N'Thân thiết', 1320
    UNION ALL SELECT 'KH010', N'Phan Đức Nam', '0925566778', 'nam.phan@gmail.com', '079204010101', N'Mới', 250
    UNION ALL SELECT 'KH011', N'Lý Khánh Linh', '0936677889', 'linh.ly@gmail.com', '079204011111', N'VIP', 4100
    UNION ALL SELECT 'KH012', N'Đặng Gia Bảo', '0947788990', 'bao.dang@gmail.com', '079204012121', N'Thân thiết', 980
    UNION ALL SELECT 'KH013', N'Tạ Minh Anh', '0958899001', 'anh.ta@gmail.com', '079204013131', N'Mới', 60
    UNION ALL SELECT 'KH014', N'Cao Nhật Quang', '0969900112', 'quang.cao@gmail.com', '079204014141', N'Thân thiết', 1500
    UNION ALL SELECT 'KH015', N'Nguyễn Bảo Trâm', '0970011223', 'tram.nguyen@gmail.com', '079204015151', N'VIP', 2780
)
INSERT INTO dbo.KhachHang (MaKH, HoTen, SoDienThoai, Email, CCCD, PhanLoai, Diem)
SELECT s.MaKH, s.HoTen, s.SoDienThoai, s.Email, s.CCCD, s.PhanLoai, s.Diem
FROM SeedKhachHang s
WHERE NOT EXISTS (SELECT 1 FROM dbo.KhachHang k WHERE k.MaKH = s.MaKH);
GO

;WITH SeedDichVu AS (
    SELECT 'DV001' AS MaDV, N'Bữa sáng buffet' AS TenDV, N'Ẩm thực' AS NhomDV, N'Suất' AS DonViTinh, CAST(180000 AS DECIMAL(18,2)) AS DonGia, N'Đang áp dụng' AS TrangThai
    UNION ALL SELECT 'DV002', N'Giặt ủi nhanh', N'Giặt ủi', N'Lần', 85000, N'Đang áp dụng'
    UNION ALL SELECT 'DV003', N'Đưa đón sân bay', N'Đưa đón', N'Lần', 350000, N'Đang áp dụng'
    UNION ALL SELECT 'DV004', N'Karaoke phòng VIP', N'Giải trí', N'Giờ', 250000, N'Tạm ngưng'
    UNION ALL SELECT 'DV005', N'Minibar', N'Ẩm thực', N'Phần', 120000, N'Đang áp dụng'
    UNION ALL SELECT 'DV006', N'Cafe máy pha', N'Ẩm thực', N'Ly', 55000, N'Đang áp dụng'
    UNION ALL SELECT 'DV007', N'Trái cây theo mùa', N'Ẩm thực', N'Phần', 95000, N'Đang áp dụng'
    UNION ALL SELECT 'DV008', N'Dọn phòng nhanh', N'Buồng phòng', N'Lần', 70000, N'Đang áp dụng'
    UNION ALL SELECT 'DV009', N'Đưa đón nội thành', N'Đưa đón', N'Lần', 220000, N'Đang áp dụng'
    UNION ALL SELECT 'DV010', N'Thuê xe máy', N'Đưa đón', N'Ngày', 180000, N'Đang áp dụng'
    UNION ALL SELECT 'DV011', N'Massage thư giãn', N'Spa', N'Giờ', 420000, N'Đang áp dụng'
    UNION ALL SELECT 'DV012', N'Sauna', N'Spa', N'Lượt', 150000, N'Đang áp dụng'
    UNION ALL SELECT 'DV013', N'Tour city 1/2 ngày', N'Du lịch', N'Gói', 890000, N'Tạm ngưng'
    UNION ALL SELECT 'DV014', N'Phòng họp nhỏ', N'Tiện ích', N'Giờ', 300000, N'Đang áp dụng'
    UNION ALL SELECT 'DV015', N'Trang trí sinh nhật', N'Sự kiện', N'Gói', 650000, N'Đang áp dụng'
)
INSERT INTO dbo.DichVu (MaDV, TenDV, NhomDV, DonViTinh, DonGia, TrangThai)
SELECT s.MaDV, s.TenDV, s.NhomDV, s.DonViTinh, s.DonGia, s.TrangThai
FROM SeedDichVu s
WHERE NOT EXISTS (SELECT 1 FROM dbo.DichVu d WHERE d.MaDV = s.MaDV);
GO

;WITH SeedThue AS (
    SELECT 'T001' AS MaThue, N'VAT 8%' AS TenThue, N'Thuế giá trị gia tăng áp dụng theo nghị định hiện hành' AS MoTa, CAST(8.00 AS DECIMAL(5,2)) AS TyLeThue, N'Đang áp dụng' AS TrangThai, CAST('2025-01-01' AS DATE) AS NgayHieuLuc
    UNION ALL SELECT 'T002', N'VAT 10%', N'Thuế giá trị gia tăng tiêu chuẩn', 10.00, N'Đang áp dụng', '2025-01-01'
    UNION ALL SELECT 'T003', N'Phụ thu lễ tết', N'Phụ thu áp dụng các ngày lễ lớn', 5.00, N'Tạm ngưng', '2025-01-01'
    UNION ALL SELECT 'T004', N'VAT dịch vụ spa', N'Mức thuế áp dụng riêng cho dịch vụ spa', 8.00, N'Đang áp dụng', '2025-02-01'
    UNION ALL SELECT 'T005', N'VAT đưa đón', N'Mức thuế cho dịch vụ vận chuyển', 10.00, N'Đang áp dụng', '2025-02-01'
    UNION ALL SELECT 'T006', N'Phụ thu cuối tuần', N'Phụ thu cho hóa đơn vào cuối tuần', 3.00, N'Đang áp dụng', '2025-03-01'
    UNION ALL SELECT 'T007', N'Phụ thu mùa cao điểm', N'Áp dụng giai đoạn du lịch cao điểm', 7.50, N'Đang áp dụng', '2025-04-01'
    UNION ALL SELECT 'T008', N'Ưu đãi thuế đoàn', N'Giảm thuế cho đoàn từ 10 khách', 4.00, N'Tạm ngưng', '2025-04-15'
    UNION ALL SELECT 'T009', N'VAT hội nghị', N'Thuế áp dụng cho dịch vụ hội nghị', 10.00, N'Đang áp dụng', '2025-05-01'
    UNION ALL SELECT 'T010', N'Phụ thu check-in sớm', N'Phụ thu cho nhận phòng trước giờ chuẩn', 2.50, N'Đang áp dụng', '2025-05-15'
    UNION ALL SELECT 'T011', N'Phụ thu check-out muộn', N'Phụ thu cho trả phòng sau giờ chuẩn', 3.50, N'Đang áp dụng', '2025-05-15'
    UNION ALL SELECT 'T012', N'VAT minibar', N'Thuế cho sản phẩm minibar', 8.00, N'Đang áp dụng', '2025-06-01'
    UNION ALL SELECT 'T013', N'VAT giải trí', N'Thuế áp dụng nhóm dịch vụ giải trí', 10.00, N'Đang áp dụng', '2025-06-10'
    UNION ALL SELECT 'T014', N'Phụ thu lễ quốc gia', N'Phụ thu theo lịch nghỉ lễ quốc gia', 6.00, N'Tạm ngưng', '2025-07-01'
    UNION ALL SELECT 'T015', N'VAT gói sự kiện', N'Thuế cho các gói trang trí sự kiện', 8.00, N'Đang áp dụng', '2025-07-15'
)
INSERT INTO dbo.Thue (MaThue, TenThue, MoTa, TyLeThue, TrangThai, NgayHieuLuc)
SELECT s.MaThue, s.TenThue, s.MoTa, s.TyLeThue, s.TrangThai, s.NgayHieuLuc
FROM SeedThue s
WHERE NOT EXISTS (SELECT 1 FROM dbo.Thue t WHERE t.MaThue = s.MaThue);
GO

;WITH SeedHoaDon AS (
    SELECT 'HD001' AS MaHoaDon, N'Nguyễn Văn An' AS KhachHang, N'Trần Thu Hà' AS NhanVien, 'DP001' AS MaDon, 'T001' AS MaThue, CAST('2026-03-31' AS DATE) AS NgayLap, CAST(3250000 AS DECIMAL(18,2)) AS TongTien
    UNION ALL SELECT 'HD002', N'Lê Hoàng Long', N'Nguyễn Minh Quân', 'DP002', 'T002', '2026-03-31', 1850000
    UNION ALL SELECT 'HD003', N'Đỗ Gia Hân', N'Phạm Ngọc Anh', 'DP003', 'T003', '2026-03-30', 5420000
    UNION ALL SELECT 'HD004', N'Trần Thị Bình', N'Trần Thu Hà', 'DP004', 'T004', '2026-03-30', 2780000
    UNION ALL SELECT 'HD005', N'Bùi Thanh Tùng', N'Nguyễn Minh Quân', 'DP005', 'T005', '2026-04-01', 1980000
    UNION ALL SELECT 'HD006', N'Ngô Hồng Nhung', N'Phạm Ngọc Anh', 'DP006', 'T006', '2026-04-01', 2290000
    UNION ALL SELECT 'HD007', N'Vũ Quốc Bảo', N'Trần Thu Hà', 'DP007', 'T007', '2026-04-02', 6140000
    UNION ALL SELECT 'HD008', N'Hoàng Thu Trang', N'Nguyễn Minh Quân', 'DP008', 'T008', '2026-04-02', 1730000
    UNION ALL SELECT 'HD009', N'Phan Đức Nam', N'Phạm Ngọc Anh', 'DP009', 'T009', '2026-04-03', 2560000
    UNION ALL SELECT 'HD010', N'Lý Khánh Linh', N'Trần Thu Hà', 'DP010', 'T010', '2026-04-03', 4870000
    UNION ALL SELECT 'HD011', N'Đặng Gia Bảo', N'Nguyễn Minh Quân', 'DP011', 'T011', '2026-04-04', 2140000
    UNION ALL SELECT 'HD012', N'Tạ Minh Anh', N'Phạm Ngọc Anh', 'DP012', 'T012', '2026-04-04', 3010000
    UNION ALL SELECT 'HD013', N'Cao Nhật Quang', N'Trần Thu Hà', 'DP013', 'T013', '2026-04-05', 3620000
    UNION ALL SELECT 'HD014', N'Nguyễn Bảo Trâm', N'Nguyễn Minh Quân', 'DP014', 'T014', '2026-04-05', 4280000
    UNION ALL SELECT 'HD015', N'Phạm Minh Khoa', N'Phạm Ngọc Anh', 'DP015', 'T015', '2026-04-06', 2390000
)
INSERT INTO dbo.HoaDon (MaHoaDon, KhachHang, NhanVien, MaDon, MaThue, NgayLap, TongTien)
SELECT s.MaHoaDon, s.KhachHang, s.NhanVien, s.MaDon, s.MaThue, s.NgayLap, s.TongTien
FROM SeedHoaDon s
WHERE NOT EXISTS (SELECT 1 FROM dbo.HoaDon h WHERE h.MaHoaDon = s.MaHoaDon);
GO

IF OBJECT_ID('dbo.NhanVien', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.NhanVien (
        MaNV NVARCHAR(20) NOT NULL PRIMARY KEY,
        HoTen NVARCHAR(100) NOT NULL,
        NgaySinh NVARCHAR(20) NOT NULL,
        SoDienThoai NVARCHAR(20) NOT NULL,
        Email NVARCHAR(120) NULL,
        GioiTinh NVARCHAR(10) NOT NULL,
        DiaChi NVARCHAR(200) NULL,
        VaiTro NVARCHAR(100) NOT NULL,
        CaLamViec NVARCHAR(50) NOT NULL,
        NgayBatDau NVARCHAR(20) NOT NULL,
        TrangThai NVARCHAR(30) NOT NULL
    );
END;
GO

IF OBJECT_ID('dbo.Phong', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.Phong (
        MaPhong NVARCHAR(20) NOT NULL PRIMARY KEY,
        SoPhong NVARCHAR(20) NOT NULL,
        SoNguoiToiDa INT NOT NULL,
        GiaPhong DECIMAL(18,2) NOT NULL,
        LoaiPhong NVARCHAR(50) NOT NULL,
        TrangThai NVARCHAR(30) NOT NULL
    );
END;
GO

IF OBJECT_ID('dbo.DatPhong', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.DatPhong (
        MaDon NVARCHAR(20) NOT NULL PRIMARY KEY,
        KhachHang NVARCHAR(100) NOT NULL,
        NhanVien NVARCHAR(100) NOT NULL,
        SoDienThoai NVARCHAR(20) NOT NULL,
        NgayNhan NVARCHAR(20) NOT NULL,
        NgayTra NVARCHAR(20) NOT NULL,
        LoaiPhong NVARCHAR(50) NOT NULL,
        SoPhong NVARCHAR(20) NOT NULL,
        SoKhach INT NOT NULL,
        TienCoc DECIMAL(18,2) NOT NULL,
        TrangThai NVARCHAR(30) NOT NULL
    );
END;
GO

IF OBJECT_ID('dbo.NhanTraPhong', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.NhanTraPhong (
        MaDon NVARCHAR(20) NOT NULL PRIMARY KEY,
        KhachHang NVARCHAR(100) NOT NULL,
        SoPhong NVARCHAR(20) NOT NULL,
        NhanPhong NVARCHAR(30) NOT NULL,
        TraPhong NVARCHAR(30) NOT NULL,
        NhanVien NVARCHAR(100) NOT NULL,
        TrangThai NVARCHAR(30) NOT NULL
    );
END;
GO

IF OBJECT_ID('dbo.KhuyenMai', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.KhuyenMai (
        MaKM NVARCHAR(20) NOT NULL PRIMARY KEY,
        TenChuongTrinh NVARCHAR(150) NOT NULL,
        Loai NVARCHAR(50) NOT NULL,
        GiaTri NVARCHAR(50) NOT NULL,
        BatDau NVARCHAR(20) NOT NULL,
        KetThuc NVARCHAR(20) NOT NULL,
        TrangThai NVARCHAR(30) NOT NULL
    );
END;
GO

;WITH SeedNhanVien AS (
    SELECT 'NV001' AS MaNV, N'Nguyễn Minh Quân' AS HoTen, '12/03/1998' AS NgaySinh, '0901234001' AS SoDienThoai,
           'quan.nv@gmail.com' AS Email, N'Nam' AS GioiTinh, N'Quận 1, TP.HCM' AS DiaChi,
           N'Nhân viên lễ tân' AS VaiTro, N'Ca sáng' AS CaLamViec, '01/06/2022' AS NgayBatDau, N'Đang làm việc' AS TrangThai
    UNION ALL SELECT 'NV002', N'Trần Thu Hà', '21/09/1996', '0901234002', 'ha.tran@gmail.com', N'Nữ', N'Quận 7, TP.HCM', N'Nhân viên quản lý', N'Ca chiều', '15/11/2021', N'Đang làm việc'
    UNION ALL SELECT 'NV003', N'Lê Đức Huy', '05/01/2000', '0901234003', 'huy.le@gmail.com', N'Nam', N'TP. Thủ Đức', N'Nhân viên lễ tân', N'Ca tối', '10/02/2024', N'Nghỉ'
    UNION ALL SELECT 'NV004', N'Phạm Ngọc Anh', '19/07/1997', '0901234004', 'anh.pham@gmail.com', N'Nữ', N'Bình Thạnh, TP.HCM', N'Nhân viên lễ tân', N'Ca xoay', '08/08/2023', N'Đang làm việc'
    UNION ALL SELECT 'NV005', N'Đỗ Gia Hân', '10/10/1999', '0901234005', 'han.do@gmail.com', N'Nữ', N'Quận 3, TP.HCM', N'Nhân viên lễ tân', N'Ca sáng', '21/03/2023', N'Đang làm việc'
    UNION ALL SELECT 'NV006', N'Ngô Quốc Bảo', '08/12/1995', '0901234006', 'bao.ngo@gmail.com', N'Nam', N'Quận 10, TP.HCM', N'Nhân viên quản lý', N'Ca chiều', '14/09/2020', N'Đang làm việc'
    UNION ALL SELECT 'NV007', N'Hoàng Minh Tâm', '16/05/2001', '0901234007', 'tam.hoang@gmail.com', N'Nam', N'Gò Vấp, TP.HCM', N'Nhân viên lễ tân', N'Ca tối', '03/01/2025', N'Đang làm việc'
    UNION ALL SELECT 'NV008', N'Võ Thu Trang', '27/11/1998', '0901234008', 'trang.vo@gmail.com', N'Nữ', N'Phú Nhuận, TP.HCM', N'Nhân viên lễ tân', N'Ca xoay', '19/04/2022', N'Đang làm việc'
    UNION ALL SELECT 'NV009', N'Bùi Khánh Linh', '09/02/1997', '0901234009', 'linh.bui@gmail.com', N'Nữ', N'Tân Bình, TP.HCM', N'Nhân viên quản lý', N'Ca sáng', '11/12/2021', N'Đang làm việc'
    UNION ALL SELECT 'NV010', N'Trương Đức Anh', '23/06/2000', '0901234010', 'anh.truong@gmail.com', N'Nam', N'Quận 5, TP.HCM', N'Nhân viên lễ tân', N'Ca chiều', '05/05/2024', N'Nghỉ'
    UNION ALL SELECT 'NV011', N'Phan Gia Bảo', '14/04/1996', '0901234011', 'bao.phan@gmail.com', N'Nam', N'Quận 11, TP.HCM', N'Nhân viên quản lý', N'Ca xoay', '07/07/2020', N'Đang làm việc'
    UNION ALL SELECT 'NV012', N'Lý Mỹ Duyên', '30/09/1999', '0901234012', 'duyen.ly@gmail.com', N'Nữ', N'Tân Phú, TP.HCM', N'Nhân viên lễ tân', N'Ca sáng', '28/02/2023', N'Đang làm việc'
    UNION ALL SELECT 'NV013', N'Cao Nhật Quang', '01/01/1998', '0901234013', 'quang.cao@gmail.com', N'Nam', N'Quận 6, TP.HCM', N'Nhân viên lễ tân', N'Ca tối', '16/06/2022', N'Đang làm việc'
    UNION ALL SELECT 'NV014', N'Đặng Minh Anh', '18/08/1997', '0901234014', 'anh.dang@gmail.com', N'Nữ', N'Quận 4, TP.HCM', N'Nhân viên lễ tân', N'Ca chiều', '10/10/2023', N'Đang làm việc'
    UNION ALL SELECT 'NV015', N'Tạ Gia Huy', '04/03/2002', '0901234015', 'huy.ta@gmail.com', N'Nam', N'Quận 8, TP.HCM', N'Nhân viên lễ tân', N'Ca xoay', '25/01/2025', N'Đang làm việc'
)
INSERT INTO dbo.NhanVien (MaNV, HoTen, NgaySinh, SoDienThoai, Email, GioiTinh, DiaChi, VaiTro, CaLamViec, NgayBatDau, TrangThai)
SELECT s.MaNV, s.HoTen, s.NgaySinh, s.SoDienThoai, s.Email, s.GioiTinh, s.DiaChi, s.VaiTro, s.CaLamViec, s.NgayBatDau, s.TrangThai
FROM SeedNhanVien s
WHERE NOT EXISTS (SELECT 1 FROM dbo.NhanVien n WHERE n.MaNV = s.MaNV);
GO

;WITH SeedPhong AS (
    SELECT 'P001' AS MaPhong, '101' AS SoPhong, 2 AS SoNguoiToiDa, CAST(850000 AS DECIMAL(18,2)) AS GiaPhong, 'Standard' AS LoaiPhong, N'Trống' AS TrangThai
    UNION ALL SELECT 'P002', '205', 3, 1250000, 'Superior', N'Đang sử dụng'
    UNION ALL SELECT 'P003', '306', 4, 1800000, 'Deluxe', N'Đang dọn'
    UNION ALL SELECT 'P004', '501', 4, 2600000, 'Suite', N'Trống'
    UNION ALL SELECT 'P005', '402', 2, 1100000, 'Superior', N'Bảo trì'
    UNION ALL SELECT 'P006', '102', 2, 900000, 'Standard', N'Trống'
    UNION ALL SELECT 'P007', '206', 3, 1300000, 'Superior', N'Đang sử dụng'
    UNION ALL SELECT 'P008', '307', 4, 1850000, 'Deluxe', N'Trống'
    UNION ALL SELECT 'P009', '502', 4, 2700000, 'Suite', N'Đang dọn'
    UNION ALL SELECT 'P010', '403', 2, 1150000, 'Superior', N'Trống'
    UNION ALL SELECT 'P011', '103', 2, 920000, 'Standard', N'Bảo trì'
    UNION ALL SELECT 'P012', '207', 3, 1350000, 'Superior', N'Trống'
    UNION ALL SELECT 'P013', '308', 4, 1900000, 'Deluxe', N'Đang sử dụng'
    UNION ALL SELECT 'P014', '503', 4, 2750000, 'Suite', N'Trống'
    UNION ALL SELECT 'P015', '404', 2, 1180000, 'Superior', N'Đang dọn'
)
INSERT INTO dbo.Phong (MaPhong, SoPhong, SoNguoiToiDa, GiaPhong, LoaiPhong, TrangThai)
SELECT s.MaPhong, s.SoPhong, s.SoNguoiToiDa, s.GiaPhong, s.LoaiPhong, s.TrangThai
FROM SeedPhong s
WHERE NOT EXISTS (SELECT 1 FROM dbo.Phong p WHERE p.MaPhong = s.MaPhong);
GO

;WITH SeedDatPhong AS (
    SELECT 'DDP001' AS MaDon, N'Nguyễn Văn An' AS KhachHang, N'Trần Thu Hà' AS NhanVien, '0901234567' AS SoDienThoai,
           '30/03/2026' AS NgayNhan, '01/04/2026' AS NgayTra, 'Deluxe' AS LoaiPhong, '306' AS SoPhong,
           2 AS SoKhach, CAST(1000000 AS DECIMAL(18,2)) AS TienCoc, N'Đã xác nhận' AS TrangThai
    UNION ALL SELECT 'DDP002', N'Trần Thị Bình', N'Nguyễn Minh Quân', '0912345678', '31/03/2026', '02/04/2026', 'Standard', '208', 1, 500000, N'Chờ xác nhận'
    UNION ALL SELECT 'DDP003', N'Lê Hoàng Long', N'Phạm Ngọc Anh', '0933456789', '29/03/2026', '30/03/2026', 'Suite', '501', 3, 2000000, N'Đã xác nhận'
    UNION ALL SELECT 'DDP004', N'Phạm Minh Khoa', N'Trần Thu Hà', '0945566778', '28/03/2026', '29/03/2026', 'Superior', '402', 2, 800000, N'Đã hủy'
    UNION ALL SELECT 'DDP005', N'Đỗ Gia Hân', N'Nguyễn Minh Quân', '0967788990', '01/04/2026', '03/04/2026', 'Deluxe', '307', 2, 1200000, N'Đã xác nhận'
    UNION ALL SELECT 'DDP006', N'Bùi Thanh Tùng', N'Trần Thu Hà', '0971122334', '02/04/2026', '04/04/2026', 'Standard', '102', 1, 500000, N'Chờ xác nhận'
    UNION ALL SELECT 'DDP007', N'Ngô Hồng Nhung', N'Phạm Ngọc Anh', '0982233445', '03/04/2026', '05/04/2026', 'Superior', '205', 2, 700000, N'Đã xác nhận'
    UNION ALL SELECT 'DDP008', N'Vũ Quốc Bảo', N'Trần Thu Hà', '0903344556', '04/04/2026', '06/04/2026', 'Suite', '503', 3, 2500000, N'Đã xác nhận'
    UNION ALL SELECT 'DDP009', N'Hoàng Thu Trang', N'Nguyễn Minh Quân', '0914455667', '05/04/2026', '06/04/2026', 'Standard', '103', 1, 450000, N'Chờ xác nhận'
    UNION ALL SELECT 'DDP010', N'Phan Đức Nam', N'Phạm Ngọc Anh', '0925566778', '06/04/2026', '08/04/2026', 'Superior', '404', 2, 850000, N'Đã hủy'
    UNION ALL SELECT 'DDP011', N'Lý Khánh Linh', N'Trần Thu Hà', '0936677889', '07/04/2026', '09/04/2026', 'Deluxe', '308', 2, 1300000, N'Đã xác nhận'
    UNION ALL SELECT 'DDP012', N'Đặng Gia Bảo', N'Nguyễn Minh Quân', '0947788990', '08/04/2026', '10/04/2026', 'Standard', '101', 1, 500000, N'Chờ xác nhận'
    UNION ALL SELECT 'DDP013', N'Tạ Minh Anh', N'Phạm Ngọc Anh', '0958899001', '09/04/2026', '11/04/2026', 'Superior', '206', 2, 900000, N'Đã xác nhận'
    UNION ALL SELECT 'DDP014', N'Cao Nhật Quang', N'Trần Thu Hà', '0969900112', '10/04/2026', '12/04/2026', 'Suite', '502', 3, 2100000, N'Chờ xác nhận'
    UNION ALL SELECT 'DDP015', N'Nguyễn Bảo Trâm', N'Nguyễn Minh Quân', '0970011223', '11/04/2026', '13/04/2026', 'Deluxe', '306', 2, 1250000, N'Đã xác nhận'
)
INSERT INTO dbo.DatPhong (MaDon, KhachHang, NhanVien, SoDienThoai, NgayNhan, NgayTra, LoaiPhong, SoPhong, SoKhach, TienCoc, TrangThai)
SELECT s.MaDon, s.KhachHang, s.NhanVien, s.SoDienThoai, s.NgayNhan, s.NgayTra, s.LoaiPhong, s.SoPhong, s.SoKhach, s.TienCoc, s.TrangThai
FROM SeedDatPhong s
WHERE NOT EXISTS (SELECT 1 FROM dbo.DatPhong d WHERE d.MaDon = s.MaDon);
GO

;WITH SeedNhanTraPhong AS (
    SELECT 'DDP001' AS MaDon, N'Nguyễn Văn An' AS KhachHang, '306' AS SoPhong, '30/03/2026 14:00' AS NhanPhong,
           '01/04/2026 11:30' AS TraPhong, N'Trần Thu Hà' AS NhanVien, N'Đã trả phòng' AS TrangThai
    UNION ALL SELECT 'DDP002', N'Trần Thị Bình', '208', '31/03/2026 13:20', '--', N'Nguyễn Minh Quân', N'Đang lưu trú'
    UNION ALL SELECT 'DDP003', N'Lê Hoàng Long', '501', '29/03/2026 15:10', '30/03/2026 10:45', N'Phạm Ngọc Anh', N'Đã trả phòng'
    UNION ALL SELECT 'DDP004', N'Phạm Minh Khoa', '402', '--', '--', N'Trần Thu Hà', N'Đang chờ nhận'
    UNION ALL SELECT 'DDP005', N'Đỗ Gia Hân', '307', '01/04/2026 13:30', '--', N'Nguyễn Minh Quân', N'Đang lưu trú'
    UNION ALL SELECT 'DDP006', N'Bùi Thanh Tùng', '102', '--', '--', N'Trần Thu Hà', N'Đang chờ nhận'
    UNION ALL SELECT 'DDP007', N'Ngô Hồng Nhung', '205', '03/04/2026 14:10', '--', N'Phạm Ngọc Anh', N'Đang lưu trú'
    UNION ALL SELECT 'DDP008', N'Vũ Quốc Bảo', '503', '04/04/2026 15:00', '06/04/2026 10:20', N'Trần Thu Hà', N'Đã trả phòng'
    UNION ALL SELECT 'DDP009', N'Hoàng Thu Trang', '103', '--', '--', N'Nguyễn Minh Quân', N'Đang chờ nhận'
    UNION ALL SELECT 'DDP010', N'Phan Đức Nam', '404', '--', '--', N'Phạm Ngọc Anh', N'Đã hủy'
    UNION ALL SELECT 'DDP011', N'Lý Khánh Linh', '308', '07/04/2026 12:40', '--', N'Trần Thu Hà', N'Đang lưu trú'
    UNION ALL SELECT 'DDP012', N'Đặng Gia Bảo', '101', '--', '--', N'Nguyễn Minh Quân', N'Đang chờ nhận'
    UNION ALL SELECT 'DDP013', N'Tạ Minh Anh', '206', '09/04/2026 14:30', '11/04/2026 10:00', N'Phạm Ngọc Anh', N'Đã trả phòng'
    UNION ALL SELECT 'DDP014', N'Cao Nhật Quang', '502', '--', '--', N'Trần Thu Hà', N'Đang chờ nhận'
    UNION ALL SELECT 'DDP015', N'Nguyễn Bảo Trâm', '306', '11/04/2026 13:10', '--', N'Nguyễn Minh Quân', N'Đang lưu trú'
)
INSERT INTO dbo.NhanTraPhong (MaDon, KhachHang, SoPhong, NhanPhong, TraPhong, NhanVien, TrangThai)
SELECT s.MaDon, s.KhachHang, s.SoPhong, s.NhanPhong, s.TraPhong, s.NhanVien, s.TrangThai
FROM SeedNhanTraPhong s
WHERE NOT EXISTS (SELECT 1 FROM dbo.NhanTraPhong n WHERE n.MaDon = s.MaDon);
GO

;WITH SeedKhuyenMai AS (
    SELECT 'KM001' AS MaKM, N'Giảm giá cuối tuần' AS TenChuongTrinh, N'Giảm theo %' AS Loai,
           '10%' AS GiaTri, '01/04/2026' AS BatDau, '30/04/2026' AS KetThuc, N'Sắp diễn ra' AS TrangThai
    UNION ALL SELECT 'KM002', N'Combo Spa + Buffet', N'Combo', '350,000', '25/03/2026', '20/04/2026', N'Đang diễn ra'
    UNION ALL SELECT 'KM003', N'Tặng nước welcome', N'Dịch vụ tặng kèm', N'Miễn phí', '01/03/2026', '31/03/2026', N'Đã kết thúc'
    UNION ALL SELECT 'KM004', N'Giảm trực tiếp hóa đơn', N'Giảm số tiền', '200,000', '15/03/2026', '15/04/2026', N'Đang diễn ra'
    UNION ALL SELECT 'KM005', N'Ưu đãi đặt sớm', N'Giảm theo %', '12%', '05/04/2026', '05/05/2026', N'Sắp diễn ra'
    UNION ALL SELECT 'KM006', N'Khách hàng thân thiết', N'Giảm số tiền', '150,000', '01/04/2026', '30/06/2026', N'Đang diễn ra'
    UNION ALL SELECT 'KM007', N'Combo gia đình', N'Combo', '500,000', '10/04/2026', '30/04/2026', N'Sắp diễn ra'
    UNION ALL SELECT 'KM008', N'Miễn phí minibar', N'Dịch vụ tặng kèm', N'Miễn phí', '20/03/2026', '20/04/2026', N'Đang diễn ra'
    UNION ALL SELECT 'KM009', N'Giảm phòng suite', N'Giảm theo %', '8%', '01/02/2026', '15/03/2026', N'Đã kết thúc'
    UNION ALL SELECT 'KM010', N'Lễ hội mùa hè', N'Giảm số tiền', '250,000', '15/04/2026', '15/05/2026', N'Sắp diễn ra'
    UNION ALL SELECT 'KM011', N'Tặng bữa sáng', N'Dịch vụ tặng kèm', N'Miễn phí', '01/04/2026', '30/04/2026', N'Đang diễn ra'
    UNION ALL SELECT 'KM012', N'Ưu đãi nhóm bạn', N'Combo', '300,000', '08/04/2026', '08/05/2026', N'Sắp diễn ra'
    UNION ALL SELECT 'KM013', N'Flash sale cuối tháng', N'Giảm theo %', '15%', '25/04/2026', '30/04/2026', N'Sắp diễn ra'
    UNION ALL SELECT 'KM014', N'Giảm giá dịch vụ spa', N'Giảm số tiền', '180,000', '01/03/2026', '31/03/2026', N'Đã kết thúc'
    UNION ALL SELECT 'KM015', N'Quà tặng check-in', N'Dịch vụ tặng kèm', 'Voucher', '02/04/2026', '25/04/2026', N'Đang diễn ra'
)
INSERT INTO dbo.KhuyenMai (MaKM, TenChuongTrinh, Loai, GiaTri, BatDau, KetThuc, TrangThai)
SELECT s.MaKM, s.TenChuongTrinh, s.Loai, s.GiaTri, s.BatDau, s.KetThuc, s.TrangThai
FROM SeedKhuyenMai s
WHERE NOT EXISTS (SELECT 1 FROM dbo.KhuyenMai k WHERE k.MaKM = s.MaKM);
GO
