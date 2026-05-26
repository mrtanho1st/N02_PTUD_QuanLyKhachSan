USE master;
GO

IF DB_ID('QuanLyKhachSan') IS NOT NULL
BEGIN
    ALTER DATABASE QuanLyKhachSan SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE QuanLyKhachSan;
END
GO

CREATE DATABASE QuanLyKhachSan;
GO

USE QuanLyKhachSan;
GO

DROP TABLE IF EXISTS CTHoaDonDichVu;
DROP TABLE IF EXISTS CTHoaDonPhong;
DROP TABLE IF EXISTS HoaDon;
DROP TABLE IF EXISTS PhieuDichVu;
DROP TABLE IF EXISTS KhuyenMai;
DROP TABLE IF EXISTS Thue;
DROP TABLE IF EXISTS DichVu;
DROP TABLE IF EXISTS CTDonDatPhong;
DROP TABLE IF EXISTS DonDatPhong;
DROP TABLE IF EXISTS TaiKhoan;
DROP TABLE IF EXISTS NhanVien;
DROP TABLE IF EXISTS KhachHang;
DROP TABLE IF EXISTS Phong;
GO

CREATE TABLE Phong (
    maPhong        VARCHAR(10) PRIMARY KEY,
    loaiPhong      NVARCHAR(50) NOT NULL,
    soNguoiToiDa   INT NOT NULL,
    giaPhong       DECIMAL(15,2) NOT NULL,
    trangThaiPhong NVARCHAR(50) NOT NULL
);
GO

CREATE TABLE KhachHang (
    maKH    VARCHAR(10) PRIMARY KEY,
    hoTen   NVARCHAR(100) NOT NULL,
    cccd    VARCHAR(20) UNIQUE,
    sdt     VARCHAR(15),
    loaiKH  NVARCHAR(20) NOT NULL,
    diemSo  INT NOT NULL DEFAULT 0
);
GO

CREATE TABLE NhanVien (
    maNV                VARCHAR(10) PRIMARY KEY,
    hoTen               NVARCHAR(100) NOT NULL,
    ngaySinh            DATE,
    sdt                 VARCHAR(15),
    email               VARCHAR(100),
    gioiTinh            NVARCHAR(10),
    ngayBatDauVaoLam    DATE,
    trangThaiLamViec    NVARCHAR(50),
    diaChi              NVARCHAR(255),
    caLamViec           NVARCHAR(50),
    viTriCongViec       NVARCHAR(50)
);
GO

CREATE TABLE TaiKhoan (
    tenDangNhap VARCHAR(50) PRIMARY KEY,
    matKhau     VARCHAR(255) NOT NULL,
    vaiTro      NVARCHAR(50) NOT NULL,
    maNV        VARCHAR(10) UNIQUE,
    CONSTRAINT fk_taikhoan_nhanvien
        FOREIGN KEY (maNV) REFERENCES NhanVien(maNV)
        ON UPDATE CASCADE
        ON DELETE SET NULL
);
GO

CREATE TABLE DonDatPhong (
    maDDP      VARCHAR(20) PRIMARY KEY,
    maKH       VARCHAR(10),
    maNV       VARCHAR(10),
    tinhTrang  NVARCHAR(50) NOT NULL,
    ngayNhan   DATETIME NOT NULL,
    ngayTra    DATETIME NOT NULL,
    tienCoc    DECIMAL(15,2) NOT NULL DEFAULT 0,

    CONSTRAINT fk_ddp_khachhang
        FOREIGN KEY (maKH) REFERENCES KhachHang(maKH)
        ON UPDATE CASCADE
        ON DELETE SET NULL,

    CONSTRAINT fk_ddp_nhanvien
        FOREIGN KEY (maNV) REFERENCES NhanVien(maNV)
        ON UPDATE CASCADE
        ON DELETE SET NULL,

    CONSTRAINT chk_ddp_ngay CHECK (ngayTra >= ngayNhan)
);
GO

CREATE TABLE CTDonDatPhong (
    maDDP     VARCHAR(20) NOT NULL,
    maPhong   VARCHAR(10) NOT NULL,
    soNgay    INT NOT NULL,
    donGia    DECIMAL(15,2) NOT NULL,

    CONSTRAINT pk_ctddp PRIMARY KEY (maDDP, maPhong),

    CONSTRAINT fk_ctddp_ddp
        FOREIGN KEY (maDDP) REFERENCES DonDatPhong(maDDP)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT fk_ctddp_phong
        FOREIGN KEY (maPhong) REFERENCES Phong(maPhong)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);
GO

CREATE TABLE DichVu (
    maDV       VARCHAR(20) PRIMARY KEY,
    tenDichVu  NVARCHAR(100) NOT NULL,
    giaDichVu  DECIMAL(15,2) NOT NULL
);
GO

CREATE TABLE PhieuDichVu (
    maPDV    VARCHAR(20) PRIMARY KEY,
    maDDP    VARCHAR(20) NOT NULL,
    maPhong  VARCHAR(10) NOT NULL,
    maDV     VARCHAR(20) NOT NULL,
    soLuong  INT NOT NULL DEFAULT 1,
    donGia   DECIMAL(15,2) NOT NULL,

    CONSTRAINT fk_pdv_ddp
        FOREIGN KEY (maDDP) REFERENCES DonDatPhong(maDDP)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT fk_pdv_phong
        FOREIGN KEY (maPhong) REFERENCES Phong(maPhong),

    CONSTRAINT fk_pdv_dichvu
        FOREIGN KEY (maDV) REFERENCES DichVu(maDV)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);
GO

CREATE TABLE KhuyenMai (
    maKM          VARCHAR(10) PRIMARY KEY,
    tenKhuyenMai  NVARCHAR(100) NOT NULL,
    giaTri        DECIMAL(5,2) NOT NULL,
    ngayBatDau    DATE,
    ngayKetThuc   DATE,

    CONSTRAINT chk_ngay_km
        CHECK (ngayKetThuc IS NULL OR ngayBatDau IS NULL OR ngayKetThuc >= ngayBatDau)
);
GO

CREATE TABLE Thue (
    maThue    VARCHAR(10) PRIMARY KEY,
    tenThue   NVARCHAR(100) NOT NULL,
    trangThai NVARCHAR(50),
    tyLeThue  DECIMAL(5,2) NOT NULL,
    moTa      NVARCHAR(255)
);
GO

CREATE TABLE HoaDon (
    maHD       VARCHAR(20) PRIMARY KEY,
    maDDP      VARCHAR(20) NOT NULL,
    maKH       VARCHAR(10) NOT NULL,
    maNV       VARCHAR(10) NOT NULL,
    maThue     VARCHAR(10),
    ngayLapHD  DATE NOT NULL,
    tongTien   DECIMAL(15,2) NOT NULL DEFAULT 0,

    CONSTRAINT fk_hoadon_ddp
        FOREIGN KEY (maDDP) REFERENCES DonDatPhong(maDDP)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT fk_hoadon_kh
        FOREIGN KEY (maKH) REFERENCES KhachHang(maKH),

    CONSTRAINT fk_hoadon_nv
        FOREIGN KEY (maNV) REFERENCES NhanVien(maNV),

    CONSTRAINT fk_hoadon_thue
        FOREIGN KEY (maThue) REFERENCES Thue(maThue)
        ON UPDATE CASCADE
        ON DELETE SET NULL
);
GO

CREATE TABLE CTHoaDonPhong (
    maHD      VARCHAR(20) NOT NULL,
    maPhong   VARCHAR(10) NOT NULL,
    maKM      VARCHAR(10),
    soNgay    INT NOT NULL,
    donGia    DECIMAL(15,2) NOT NULL,
    thanhTien DECIMAL(15,2) NOT NULL,

    CONSTRAINT pk_cthd_phong PRIMARY KEY (maHD, maPhong),

    CONSTRAINT fk_cthdphong_hoadon
        FOREIGN KEY (maHD) REFERENCES HoaDon(maHD)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT fk_cthdphong_phong
        FOREIGN KEY (maPhong) REFERENCES Phong(maPhong),

    CONSTRAINT fk_cthdphong_km
        FOREIGN KEY (maKM) REFERENCES KhuyenMai(maKM)
        ON UPDATE CASCADE
        ON DELETE SET NULL
);
GO

CREATE TABLE CTHoaDonDichVu (
    maHD      VARCHAR(20) NOT NULL,
    maPDV     VARCHAR(20) NOT NULL,
    maKM      VARCHAR(10),
    soLuong   INT NOT NULL,
    donGia    DECIMAL(15,2) NOT NULL,
    thanhTien DECIMAL(15,2) NOT NULL,

    CONSTRAINT pk_cthd_dichvu PRIMARY KEY (maHD, maPDV),

    CONSTRAINT fk_cthddv_hoadon
        FOREIGN KEY (maHD) REFERENCES HoaDon(maHD)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT fk_cthddv_pdv
        FOREIGN KEY (maPDV) REFERENCES PhieuDichVu(maPDV),

    CONSTRAINT fk_cthddv_km
        FOREIGN KEY (maKM) REFERENCES KhuyenMai(maKM)
        ON UPDATE CASCADE
        ON DELETE SET NULL
);
GO

INSERT INTO KhachHang(maKH, hoTen, cccd, sdt, loaiKH, diemSo) VALUES
('KH01', N'Nguyễn Minh Anh',     '012345678901', '0909123456', N'Thường', 10),
('KH02', N'Trần Quốc Bảo',       '012345678902', '0909000002', N'Thường', 20),
('KH03', N'Lê Hoài Nam',         '012345678903', '0909000003', N'VIP', 35),
('KH04', N'Phạm Gia Hân',        '012345678904', '0909000004', N'Thường', 15),
('KH05', N'Hoàng Đức Long',      '012345678905', '0909000005', N'VIP', 40),
('KH06', N'Nguyễn Khánh Vy',     '012345678906', '0909000006', N'Thường', 12),
('KH07', N'Trần Nhật Minh',      '012345678907', '0909000007', N'Thân thiết', 55),
('KH08', N'Lê Thanh Tùng',       '012345678908', '0909000008', N'Thường', 18),
('KH09', N'Phạm Ngọc Mai',       '012345678909', '0909000009', N'VIP', 42),
('KH10', N'Hoàng Gia Bảo',       '012345678910', '0909000010', N'Thường', 25),
('KH11', N'Đỗ Quỳnh Anh',        '012345678911', '0909000011', N'Thường', 14),
('KH12', N'Bùi Thành Công',      '012345678912', '0909000012', N'Thân thiết', 60),
('KH13', N'Võ Minh Khang',       '012345678913', '0909000013', N'Thường', 16),
('KH14', N'Đặng Hải Đăng',       '012345678914', '0909000014', N'VIP', 45),
('KH15', N'Nguyễn Phương Linh',  '012345678915', '0909000015', N'Thường', 22),
('KH16', N'Trương Tuấn Kiệt',    '012345678916', '0909000016', N'Thường', 11),
('KH17', N'Lý Bảo Ngọc',         '012345678917', '0909000017', N'Thân thiết', 58),
('KH18', N'Phan Anh Tú',         '012345678918', '0909000018', N'Thường', 19),
('KH19', N'Mai Thiên Ân',        '012345678919', '0909000019', N'VIP', 48),
('KH20', N'Đinh Quốc Khánh',     '012345678920', '0909000020', N'Thường', 13);
GO

INSERT INTO NhanVien(maNV, hoTen, ngaySinh, sdt, email, gioiTinh, ngayBatDauVaoLam, trangThaiLamViec, diaChi, caLamViec, viTriCongViec) VALUES
('NV01', N'Đỗ Thanh Tường',       '2000-01-10', '0911222333', 'tuong@hotel.com', N'Nữ',  '2024-01-01', N'Đang làm', N'Hà Nội',    N'Ca sáng',  N'Lễ tân'),
('NV02', N'Lê Minh Tân',          '2001-04-12', '0911222334', 'tan@hotel.com',   N'Nam', '2024-03-10', N'Đang làm', N'Hà Nội',    N'Ca sáng',  N'Lễ tân'),
('NV03', N'Nguyễn Thị Thanh Thư', '1999-08-20', '0911222335', 'thu@hotel.com',   N'Nữ',  '2025-01-15', N'Đang làm', N'Hải Phòng', N'Ca chiều', N'Quản lý'),
('NV04', N'Nguyễn Gia Quân',      '2002-02-15', '0911222336', 'quan@hotel.com',  N'Nam', '2024-05-05', N'Đang làm', N'Đà Nẵng',   N'Ca sáng',  N'Lễ tân'),
('NV05', N'Phạm Quốc Đạt',        '1999-12-09', '0911222337', 'dat@hotel.com',   N'Nam', '2025-09-18', N'Đang làm', N'TP.HCM',    N'Ca tối',   N'Quản lý'),
('NV06', N'Trần Hoài Nam',        '2000-03-25', '0911222338', 'nam@hotel.com',   N'Nam', '2024-06-15', N'Đang làm', N'Hà Nội',    N'Ca tối',   N'Lễ tân'),
('NV07', N'Võ Thị Ngọc Anh',      '2001-07-14', '0911222339', 'anh@hotel.com',   N'Nữ',  '2025-02-20', N'Đang làm', N'Đà Nẵng',   N'Ca chiều', N'Lễ tân'),
('NV08', N'Bùi Công Minh',        '1999-11-30', '0911222340', 'minh@hotel.com',  N'Nam', '2024-12-01', N'Đang làm', N'TP.HCM',    N'Ca sáng',  N'Kế toán'),
('NV09', N'Ngô Thị Lan Hương',    '2002-05-18', '0911222341', 'huong@hotel.com', N'Nữ',  '2026-04-10', N'Đang làm', N'Hải Phòng', N'Ca tối',   N'Lễ tân'),
('NV10', N'Đặng Quốc Bảo',        '2000-09-22', '0911222342', 'bao@hotel.com',   N'Nam', '2025-08-05', N'Đang làm', N'Cần Thơ',   N'Ca chiều', N'Bảo vệ');
GO

INSERT INTO TaiKhoan(tenDangNhap, matKhau, vaiTro, maNV) VALUES
('DoThanhTuong',       '123456', N'LeTan',   'NV01'),
('LeMinhTan',          '123456', N'LeTan',   'NV02'),
('NguyenThiThanhThu',  '123456', N'QuanLy',  'NV03'),
('NguyenGiaQuan',      '123456', N'LeTan',   'NV04'),
('PhamQuocDat',        '123456', N'QuanLy',  'NV05');
GO


INSERT INTO Phong(maPhong, loaiPhong, soNguoiToiDa, giaPhong, trangThaiPhong) VALUES
('P101', N'Deluxe',           2, 2200000, N'Trống'),
('P102', N'Deluxe',           2, 2300000, N'Trống'),
('P103', N'Deluxe',           2, 2400000, N'Trống'),
('P104', N'Premium Deluxe',   2, 2600000, N'Trống'),
('P105', N'Premium Deluxe',   3, 2800000, N'Trống'),
('P106', N'Premium Deluxe',   2, 3000000, N'Trống'),
('P107', N'Grand Triple',     3, 3200000, N'Trống'),
('P108', N'Grand Triple',     3, 3400000, N'Trống'),
('P109', N'Grand Triple',     4, 3600000, N'Trống'),
('P110', N'Grand Triple',     4, 3800000, N'Trống'),

('P111', N'Victorian Suite',  2, 3900000, N'Đang sử dụng'),
('P112', N'Victorian Suite',  2, 4000000, N'Đang sử dụng'),
('P113', N'Victorian Suite',  3, 4200000, N'Đang sử dụng'),
('P114', N'Victorian Suite',  4, 4500000, N'Đang sử dụng'),

('P115', N'Deluxe',           2, 2500000, N'Đang sử dụng'),
('P116', N'Deluxe',           2, 2700000, N'Đang sử dụng'),
('P117', N'Premium Deluxe',   2, 3100000, N'Đang sử dụng'),
('P118', N'Premium Deluxe',   3, 3300000, N'Đang sử dụng'),
('P119', N'Grand Triple',     3, 3500000, N'Đang sử dụng'),

('P120', N'Grand Triple',     4, 4000000, N'Đã đặt'),
('P121', N'Victorian Suite',  2, 4300000, N'Đã đặt'),
('P122', N'Victorian Suite',  2, 4400000, N'Đã đặt'),
('P123', N'Deluxe',           2, 2600000, N'Đã đặt'),
('P124', N'Premium Deluxe',   2, 3200000, N'Đã đặt'),
('P125', N'Grand Triple',     3, 3700000, N'Trống'),
('P126', N'Victorian Suite',  4, 4500000, N'Trống'),

('P127', N'Phòng hội nghị',   50, 5000000,  N'Trống'),
('P128', N'Phòng hội nghị',   80, 7000000,  N'Trống'),

('P129', N'Phòng họp',        20, 2500000,  N'Trống'),
('P130', N'Phòng họp',        30, 3200000,  N'Trống'),

('P131', N'Sảnh tiệc cưới',  200, 15000000, N'Trống'),
('P132', N'Sảnh tiệc cưới',  300, 20000000, N'Trống'),

('P133', N'Sảnh sự kiện',    150, 12000000, N'Trống'),
('P134', N'Sảnh sự kiện',    250, 18000000, N'Trống');
GO

INSERT INTO DichVu(maDV, tenDichVu, giaDichVu) VALUES
('DV01', N'Lễ tân 24/7',              0),
('DV02', N'Giữ hành lý',              0),
('DV03', N'Giặt ủi',                  50000),
('DV04', N'Đưa đón sân bay',          250000),
('DV05', N'Cho thuê xe',              300000),
('DV06', N'Đặt tour du lịch',         500000),
('DV07', N'Đặt vé máy bay',           200000),
('DV08', N'Room service',             150000),
('DV09', N'Kê giường phụ',            350000),
('DV10', N'Hồ bơi ngoài trời',        0),

('DV11', N'Hồ bơi trẻ em',            0),
('DV12', N'Khu tắm nắng',             0),
('DV13', N'Phòng gym',                0),
('DV14', N'Sân tennis',               200000),
('DV15', N'Spa',                      500000),
('DV16', N'Massage',                  400000),
('DV17', N'Phòng xông hơi',           250000),
('DV18', N'Khu vui chơi trẻ em',      100000),
('DV20', N'Bãi đỗ xe',                50000);
GO

INSERT INTO KhuyenMai(maKM, tenKhuyenMai, giaTri, ngayBatDau, ngayKetThuc) VALUES
('KM01', N'Giảm 10%',                 10, '2026-01-01', '2026-12-31'),
('KM02', N'Giảm 20%',                 20, '2026-03-01', '2026-12-31'),
('KM03', N'Khách hàng thân thiết',    15, '2026-01-01', '2026-12-31'),
('KM04', N'Lễ 30/4 - 1/5',            25, '2026-04-25', '2026-05-02'),

-- Còn hiệu lực
('KM05', N'Ưu đãi mùa hè',            18, '2026-05-01', '2026-08-31'),
('KM06', N'Đặt phòng sớm',            12, '2026-01-15', '2026-11-30'),
('KM07', N'Tri ân khách VIP',         30, '2026-02-01', '2026-12-31'),
('KM08', N'Combo nghỉ dưỡng',         22, '2026-06-01', '2026-10-31'),

-- Hết hiệu lực
('KM09', N'Khuyến mãi Tết 2025',      20, '2025-01-10', '2025-02-10'),
('KM10', N'Black Friday 2025',        35, '2025-11-20', '2025-11-30'),
('KM11', N'Ưu đãi khai trương',       40, '2024-01-01', '2024-03-31'),
('KM12', N'Giảm giá mùa đông 2025',   15, '2025-12-01', '2025-12-31');

GO

INSERT INTO Thue(maThue, tenThue, trangThai, tyLeThue, moTa) VALUES
('T01', N'VAT 5%',                N'Áp dụng',        5.00,  N'Thuế VAT áp dụng cho một số dịch vụ'),
('T02', N'VAT 8%',                N'Áp dụng',        8.00,  N'Thuế VAT ưu đãi'),
('T03', N'VAT 10%',               N'Áp dụng',       10.00,  N'Thuế VAT tiêu chuẩn'),

('T04', N'Phụ thu lễ Tết',        N'Áp dụng',       15.00,  N'Phụ thu vào các dịp lễ và Tết'),
('T05', N'Phụ thu cuối tuần',     N'Áp dụng',       10.00,  N'Áp dụng cho thứ 6, 7 và Chủ nhật'),
('T06', N'Phí phục vụ',           N'Áp dụng',        5.00,  N'Phí phục vụ khách hàng tại khách sạn'),

('T07', N'Thuế phòng hội nghị',   N'Áp dụng',        8.00,  N'Áp dụng cho dịch vụ hội nghị và sự kiện'),
('T08', N'Thuế dịch vụ spa',      N'Áp dụng',       10.00,  N'Áp dụng cho spa và massage'),
('T09', N'Phụ thu giường phụ',    N'Áp dụng',        7.00,  N'Áp dụng khi khách kê thêm giường'),

('T10', N'Miễn thuế ngoại giao',  N'Ngưng áp dụng',  0.00,  N'Áp dụng cho khách thuộc diện miễn thuế'),
('T11', N'Không tính thuế',       N'Ngưng áp dụng',  0.00,  N'Dùng cho trường hợp miễn thuế');

GO

INSERT INTO DonDatPhong(maDDP, maKH, maNV, tinhTrang, ngayNhan, ngayTra, tienCoc) VALUES
('DDP01', 'KH01', 'NV01', N'Hoàn thành', '2026-04-18 14:00:00', '2026-04-20 12:00:00', 1000000),
('DDP02', 'KH02', 'NV02', N'Hoàn thành', '2026-04-19 14:00:00', '2026-04-21 12:00:00', 1200000),
('DDP03', 'KH03', 'NV03', N'Hoàn thành', '2026-04-20 14:00:00', '2026-04-22 12:00:00', 1500000),
('DDP04', 'KH04', 'NV04', N'Hoàn thành', '2026-04-21 13:30:00', '2026-04-23 11:30:00', 1800000),
('DDP05', 'KH05', 'NV05', N'Hoàn thành', '2026-04-22 14:00:00', '2026-04-24 12:00:00', 2000000),

('DDP06', 'KH06', 'NV01', N'Hoàn thành', '2026-04-23 14:00:00', '2026-04-25 12:00:00', 2200000),
('DDP07', 'KH07', 'NV02', N'Hoàn thành', '2026-04-24 14:00:00', '2026-04-26 12:00:00', 2500000),
('DDP08', 'KH08', 'NV03', N'Hoàn thành', '2026-04-25 14:30:00', '2026-04-27 12:00:00', 2800000),
('DDP09', 'KH09', 'NV04', N'Hoàn thành', '2026-04-26 14:00:00', '2026-04-28 12:00:00', 3000000),
('DDP10', 'KH10', 'NV05', N'Hoàn thành', '2026-04-27 14:00:00', '2026-04-29 12:00:00', 3200000),

-- Đã nhận
('DDP11', 'KH11', 'NV01', N'Đã nhận', '2026-05-24 13:00:00', '2026-05-27 12:00:00', 3500000),
('DDP12', 'KH12', 'NV02', N'Đã nhận', '2026-05-24 14:00:00', '2026-05-28 12:00:00', 3600000),
('DDP13', 'KH13', 'NV03', N'Đã nhận', '2026-05-25 12:30:00', '2026-05-27 11:30:00', 3800000),
('DDP14', 'KH14', 'NV04', N'Đã nhận', '2026-05-25 14:00:00', '2026-05-28 12:00:00', 4000000),
('DDP15', 'KH15', 'NV05', N'Đã nhận', '2026-05-25 09:00:00', '2026-05-27 12:00:00', 1800000),

('DDP16', 'KH16', 'NV01', N'Đã nhận', '2026-05-26 11:00:00', '2026-05-29 12:00:00', 2000000),
('DDP17', 'KH17', 'NV02', N'Đã nhận', '2026-05-26 13:00:00', '2026-05-29 12:00:00', 2200000),
('DDP18', 'KH18', 'NV03', N'Đã nhận', '2026-05-26 15:00:00', '2026-05-30 12:00:00', 2500000),
('DDP19', 'KH19', 'NV04', N'Đã nhận', '2026-05-26 10:00:00', '2026-05-29 12:00:00', 2700000),

-- Đã đặt
('DDP20', 'KH20', 'NV05', N'Đã đặt', '2026-05-29 14:00:00', '2026-05-31 12:00:00', 3000000),
('DDP21', 'KH01', 'NV01', N'Đã đặt', '2026-05-30 14:30:00', '2026-06-02 12:00:00', 3200000),
('DDP22', 'KH02', 'NV02', N'Đã đặt', '2026-05-31 14:00:00', '2026-06-03 12:00:00', 3500000),
('DDP23', 'KH03', 'NV03', N'Đã đặt', '2026-06-01 15:00:00', '2026-06-04 12:00:00', 1800000),
('DDP24', 'KH04', 'NV04', N'Đã đặt', '2026-06-02 14:00:00', '2026-06-05 12:00:00', 2500000),
('DDP25', 'KH05', 'NV05', N'Đã đặt', '2026-06-03 15:00:00', '2026-06-06 12:00:00', 2800000);

GO

INSERT INTO CTDonDatPhong(maDDP, maPhong, soNgay, donGia) VALUES
('DDP01', 'P101', 2, 2200000),
('DDP02', 'P102', 2, 2300000),
('DDP03', 'P103', 2, 2400000),
('DDP04', 'P104', 2, 2600000),
('DDP05', 'P105', 2, 2800000),
('DDP06', 'P106', 2, 3000000),
('DDP07', 'P107', 2, 3200000),
('DDP08', 'P108', 2, 3400000),
('DDP09', 'P109', 2, 3600000),
('DDP10', 'P110', 2, 3800000),

('DDP11', 'P111', 3, 3900000),
('DDP12', 'P112', 4, 4000000),
('DDP13', 'P113', 2, 4200000),
('DDP14', 'P114', 3, 4500000),
('DDP15', 'P115', 2, 2500000),
('DDP16', 'P116', 3, 2700000),
('DDP17', 'P117', 3, 3100000),
('DDP18', 'P118', 4, 3300000),
('DDP19', 'P119', 3, 3500000),

('DDP20', 'P120', 2, 4000000),
('DDP21', 'P121', 3, 4300000),
('DDP22', 'P122', 2, 4400000),
('DDP23', 'P123', 3, 2600000),
('DDP24', 'P124', 3, 3200000),
('DDP25', 'P125', 3, 3700000);
GO

INSERT INTO PhieuDichVu(maPDV, maDDP, maPhong, maDV, soLuong, donGia) VALUES
('PDV01', 'DDP01', 'P101', 'DV03', 1, 50000),     -- Giặt ủi
('PDV02', 'DDP02', 'P102', 'DV08', 2, 150000),    -- Room service
('PDV03', 'DDP03', 'P103', 'DV09', 1, 350000),    -- Kê giường phụ
('PDV04', 'DDP04', 'P104', 'DV15', 1, 500000),    -- Spa
('PDV05', 'DDP05', 'P105', 'DV16', 2, 400000),    -- Massage
('PDV06', 'DDP06', 'P106', 'DV04', 1, 250000),    -- Đưa đón sân bay
('PDV07', 'DDP07', 'P107', 'DV14', 2, 200000),    -- Sân tennis
('PDV08', 'DDP08', 'P108', 'DV05', 1, 300000),    -- Cho thuê xe
('PDV09', 'DDP09', 'P109', 'DV17', 2, 250000),    -- Phòng xông hơi
('PDV10', 'DDP10', 'P110', 'DV06', 1, 500000),    -- Đặt tour du lịch

('PDV11', 'DDP11', 'P111', 'DV08', 1, 150000),    -- Room service
('PDV12', 'DDP11', 'P111', 'DV08', 1, 150000), -- Room service
('PDV13', 'DDP11', 'P111', 'DV15', 1, 500000), -- Spa
('PDV14', 'DDP11', 'P111', 'DV04', 1, 250000), -- Đưa đón sân bay
('PDV15', 'DDP12', 'P112', 'DV15', 1, 500000),    -- Spa
('PDV16', 'DDP13', 'P113', 'DV16', 1, 400000),    -- Massage
('PDV17', 'DDP14', 'P114', 'DV09', 1, 350000),    -- Kê giường phụ
('PDV18', 'DDP15', 'P115', 'DV03', 2, 50000),     -- Giặt ủi

('PDV19', 'DDP16', 'P116', 'DV05', 1, 300000),    -- Cho thuê xe
('PDV20', 'DDP17', 'P117', 'DV04', 1, 250000),    -- Đưa đón sân bay
('PDV21', 'DDP18', 'P118', 'DV14', 1, 200000),    -- Sân tennis
('PDV22', 'DDP19', 'P119', 'DV18', 2, 100000),    -- Khu vui chơi trẻ em
('PDV23', 'DDP20', 'P120', 'DV06', 1, 500000);    -- Đặt tour du lịch

GO

INSERT INTO HoaDon(maHD, maDDP, maKH, maNV, maThue, ngayLapHD, tongTien) VALUES
('HD01', 'DDP01', 'KH01', 'NV03', 'T03', '2026-04-20', 4895000),
('HD02', 'DDP02', 'KH02', 'NV03', 'T03', '2026-04-21', 5225000),
('HD03', 'DDP03', 'KH03', 'NV03', 'T03', '2026-04-22', 5637500),
('HD04', 'DDP04', 'KH04', 'NV03', 'T03', '2026-04-23', 6270000),
('HD05', 'DDP05', 'KH05', 'NV03', 'T03', '2026-04-24', 7040000),

('HD06', 'DDP06', 'KH06', 'NV03', 'T03', '2026-04-25', 6875000),
('HD07', 'DDP07', 'KH07', 'NV03', 'T03', '2026-04-26', 7480000),
('HD08', 'DDP08', 'KH08', 'NV03', 'T03', '2026-04-27', 8360000),
('HD09', 'DDP09', 'KH09', 'NV03', 'T03', '2026-04-28', 9020000),
('HD10', 'DDP10', 'KH10', 'NV03', 'T03', '2026-04-29', 8910000);

GO

INSERT INTO CTHoaDonPhong(maHD, maPhong, maKM, soNgay, donGia, thanhTien) VALUES
('HD01', 'P101', 'KM01', 2, 2200000, 3960000),
('HD02', 'P102', NULL,   2, 2300000, 4600000),
('HD03', 'P103', 'KM03', 2, 2400000, 4080000),
('HD04', 'P104', NULL,   2, 2600000, 5200000),
('HD05', 'P105', 'KM02', 2, 2800000, 4480000),

('HD06', 'P106', NULL,   2, 3000000, 6000000),
('HD07', 'P107', 'KM03', 2, 3200000, 5440000),
('HD08', 'P108', NULL,   2, 3400000, 6800000),
('HD09', 'P109', 'KM01', 2, 3600000, 6480000),
('HD10', 'P110', NULL,   2, 3800000, 7600000);

GO

INSERT INTO CTHoaDonDichVu(maHD, maPDV, maKM, soLuong, donGia, thanhTien) VALUES
('HD01', 'PDV01', NULL,   1,  50000,   50000),
('HD02', 'PDV02', NULL,   2, 150000,  300000),
('HD03', 'PDV03', 'KM01', 1, 350000,  315000),
('HD04', 'PDV04', NULL,   1, 500000,  500000),
('HD05', 'PDV05', 'KM02', 2, 400000,  640000),
('HD06', 'PDV06', NULL,   1, 250000,  250000),
('HD07', 'PDV07', NULL,   2, 200000,  400000),
('HD08', 'PDV08', NULL,   1, 300000,  300000),
('HD09', 'PDV09', NULL,   2, 250000,  500000),
('HD10', 'PDV10', 'KM01', 1, 500000,  450000);

GO

SELECT COUNT(*) AS soKhachHang FROM KhachHang;
SELECT COUNT(*) AS soNhanVien FROM NhanVien;
SELECT COUNT(*) AS soPhong FROM Phong;
SELECT COUNT(*) AS soDonDatPhong FROM DonDatPhong;
SELECT COUNT(*) AS soHoaDon FROM HoaDon;
SELECT COUNT(*) AS soPhieuDichVu FROM PhieuDichVu;
SELECT COUNT(*) AS soCTHoaDonPhong FROM CTHoaDonPhong;
SELECT COUNT(*) AS soCTHoaDonDichVu FROM CTHoaDonDichVu;
GO

