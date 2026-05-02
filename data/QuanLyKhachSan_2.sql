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
('KH01', N'Nguyễn Văn A', '012345678901', '0909123456', N'Thường', 10),
('KH02', N'Trần Văn C',   '012345678902', '0909000002', N'Thường', 20),
('KH03', N'Lê Thị D',     '012345678903', '0909000003', N'VIP', 35),
('KH04', N'Phạm Văn E',   '012345678904', '0909000004', N'Thường', 15),
('KH05', N'Hoàng Thị F',  '012345678905', '0909000005', N'VIP', 40),
('KH06', N'Nguyễn Văn G', '012345678906', '0909000006', N'Thường', 12),
('KH07', N'Trần Thị H',   '012345678907', '0909000007', N'Thân thiết', 55),
('KH08', N'Lê Văn I',     '012345678908', '0909000008', N'Thường', 18),
('KH09', N'Phạm Thị K',   '012345678909', '0909000009', N'VIP', 42),
('KH10', N'Hoàng Văn L',  '012345678910', '0909000010', N'Thường', 25),
('KH11', N'Đỗ Thị M',     '012345678911', '0909000011', N'Thường', 14),
('KH12', N'Bùi Văn N',    '012345678912', '0909000012', N'Thân thiết', 60),
('KH13', N'Võ Thị O',     '012345678913', '0909000013', N'Thường', 16),
('KH14', N'Đặng Văn P',   '012345678914', '0909000014', N'VIP', 45),
('KH15', N'Nguyễn Thị Q', '012345678915', '0909000015', N'Thường', 22),
('KH16', N'Trương Văn R', '012345678916', '0909000016', N'Thường', 11),
('KH17', N'Lý Thị S',     '012345678917', '0909000017', N'Thân thiết', 58),
('KH18', N'Phan Văn T',   '012345678918', '0909000018', N'Thường', 19),
('KH19', N'Mai Thị U',    '012345678919', '0909000019', N'VIP', 48),
('KH20', N'Đinh Văn V',   '012345678920', '0909000020', N'Thường', 13);
GO

INSERT INTO NhanVien(maNV, hoTen, ngaySinh, sdt, email, gioiTinh, ngayBatDauVaoLam, trangThaiLamViec, diaChi, caLamViec, viTriCongViec) VALUES
('NV01', N'Đỗ Thanh Tường',        '1996-01-10', '0911222333', 'tuong@hotel.com', N'Nữ',  '2023-01-01', N'Đang làm', N'Hà Nội',    N'Ca sáng',  N'Lễ tân'),
('NV02', N'Lê Minh Tân',           '1998-04-12', '0911222334', 'tan@hotel.com',   N'Nam', '2024-01-10', N'Đang làm', N'Hà Nội',    N'Ca sáng',  N'Lễ tân'),
('NV03', N'Nguyễn Thị Thanh Thư',  '1995-08-20', '0911222335', 'thu@hotel.com',   N'Nữ',  '2023-11-01', N'Đang làm', N'Hải Phòng', N'Ca chiều', N'Lễ tân'),
('NV04', N'Nguyễn Gia Quân',       '1997-02-15', '0911222336', 'quan@hotel.com',  N'Nam', '2024-03-05', N'Đang làm', N'Đà Nẵng',   N'Ca sáng',  N'Lễ tân'),
('NV05', N'Phạm Quốc Đạt',         '1994-12-09', '0911222337', 'dat@hotel.com',   N'Nam', '2022-09-18', N'Đang làm', N'TP.HCM',    N'Ca tối',   N'Quản lý'),
('NV06', N'Trần Hoài Nam',        '1996-03-25', '0911222338', 'nam@hotel.com',    N'Nam', '2023-06-15', N'Đang làm', N'Hà Nội',    N'Ca tối',   N'Lễ tân'),
('NV07', N'Võ Thị Ngọc Anh',      '1997-07-14', '0911222339', 'anh@hotel.com',    N'Nữ',  '2024-02-20', N'Đang làm', N'Đà Nẵng',   N'Ca chiều', N'Lễ tân'),
('NV08', N'Bùi Công Minh',        '1993-11-30', '0911222340', 'minh@hotel.com',   N'Nam', '2022-12-01', N'Đang làm', N'TP.HCM',    N'Ca sáng',  N'Kế toán'),
('NV09', N'Ngô Thị Lan Hương',    '1998-05-18', '0911222341', 'huong@hotel.com',  N'Nữ',  '2024-04-10', N'Đang làm', N'Hải Phòng', N'Ca tối',   N'Lễ tân'),
('NV10', N'Đặng Quốc Bảo',        '1995-09-22', '0911222342', 'bao@hotel.com',    N'Nam', '2023-08-05', N'Đang làm', N'Cần Thơ',   N'Ca chiều', N'Bảo vệ');
GO

INSERT INTO TaiKhoan(tenDangNhap, matKhau, vaiTro, maNV) VALUES
('DoThanhTuong', '123456', N'LeTan',  'NV01'),
('LeMinhTan', '123456', N'LeTan',  'NV02'),
('NguyenThiThanhThu', '123456', N'LeTan',  'NV03'),
('NguyenGiaQuan', '123456', N'LeTan', 'NV04'),
('nv05', '123456', N'QuanLy', 'NV05');
GO

INSERT INTO Phong(maPhong, loaiPhong, soNguoiToiDa, giaPhong, trangThaiPhong) VALUES
('P101', N'Phòng Thượng hạng', 2, 800000,  N'Trống'),
('P102', N'Phòng Tiêu chuẩn',  2, 500000,  N'Trống'),
('P103', N'Phòng Tiêu chuẩn',  2, 500000,  N'Trống'),
('P104', N'Phòng Cao cấp',     2, 650000,  N'Trống'),
('P105', N'Phòng Cao cấp',     3, 700000,  N'Trống'),
('P106', N'Phòng Sang trọng',  2, 800000,  N'Trống'),
('P108', N'Phòng Sang trọng',  3, 900000,  N'Trống'),
('P109', N'Phòng Sang trọng',  4, 1500000, N'Trống'),
('P110', N'Phòng Sang trọng',  4, 1550000, N'Trống'),
('P111', N'Phòng Tiêu chuẩn',  2, 520000,  N'Trống'),

('P112', N'Phòng Cao cấp',     2, 680000,  N'Đang sử dụng'),
('P113', N'Phòng Sang trọng',  2, 820000,  N'Đang sử dụng'),
('P114', N'Phòng Sang trọng',  3, 880000,  N'Đang sử dụng'),
('P115', N'Phòng Gia đình',    4, 1600000, N'Đang sử dụng'),
('P116', N'Phòng Tiêu chuẩn',  2, 540000,  N'Đang sử dụng'),
('P118', N'Phòng Sang trọng',  2, 870000,  N'Đang sử dụng'),
('P125', N'Phòng Tiêu chuẩn',  1, 390000,  N'Đang sử dụng'),
('P131', N'Phòng Sang trọng',  3, 920000,  N'Đang sử dụng'),
('P139', N'Phòng Cao cấp',     3, 750000,  N'Đang sử dụng'),

('P119', N'Phòng Gia đình',    5, 1700000, N'Đã đặt'),
('P120', N'Phòng Gia đình',    6, 2000000, N'Đã đặt'),
('P126', N'Phòng Tiêu chuẩn',  2, 520000,  N'Đã đặt'),
('P127', N'Phòng Tiêu chuẩn',  2, 530000,  N'Đã đặt'),
('P128', N'Phòng Cao cấp',     2, 680000,  N'Đã đặt'),
('P129', N'Phòng Cao cấp',     3, 720000,  N'Đã đặt'),
('P130', N'Phòng Sang trọng',  2, 850000,  N'Đã đặt'),

('P107', N'Phòng Sang trọng',  2, 850000,  N'Bảo trì'),
('P117', N'Phòng Cao cấp',     3, 720000,  N'Bảo trì');
GO

INSERT INTO DichVu(maDV, tenDichVu, giaDichVu) VALUES
('DV01', N'Nước suối',         10000),
('DV02', N'Bia',               30000),
('DV03', N'Nước ngọt',         20000),
('DV04', N'Mì gói',            15000),
('DV05', N'Giặt ủi',           50000),
('DV06', N'Ăn sáng',           80000),
('DV07', N'Thuê xe máy',      150000),
('DV08', N'Dọn phòng',         70000),
('DV09', N'Spa',              300000),
('DV10', N'Đưa đón sân bay',  250000);
GO

INSERT INTO KhuyenMai(maKM, tenKhuyenMai, giaTri, ngayBatDau, ngayKetThuc) VALUES
('KM01', N'Giảm 10%',              10, '2026-01-01', '2026-12-31'),
('KM02', N'Giảm 20%',              20, '2026-03-01', '2026-12-31'),
('KM03', N'Khách hàng thân thiết', 15, '2026-01-01', '2026-12-31'),
('KM04', N'Lễ 30/4 - 1/5',         25, '2026-04-25', '2026-05-02');
GO

INSERT INTO Thue(maThue, tenThue, trangThai, tyLeThue, moTa) VALUES
('T01', N'VAT 5%',          N'Áp dụng',        5.00, N'Thuế VAT áp dụng cho một số dịch vụ'),
('T02', N'VAT 8%',          N'Áp dụng',        8.00, N'Thuế VAT ưu đãi'),
('T03', N'VAT 10%',         N'Áp dụng',       10.00, N'Thuế VAT tiêu chuẩn'),
('T04', N'Phụ thu lễ',      N'Áp dụng',       15.00, N'Phụ thu vào dịp lễ tết'),
('T05', N'Không tính thuế', N'Ngưng áp dụng',  0.00, N'Dùng cho trường hợp miễn thuế');
GO

INSERT INTO DonDatPhong(maDDP, maKH, maNV, tinhTrang, ngayNhan, ngayTra, tienCoc) VALUES
('DDP01', 'KH01', 'NV01', N'Hoàn thành', '2026-04-18 14:00:00', '2026-04-20 12:00:00', 200000),
('DDP02', 'KH02', 'NV02', N'Hoàn thành', '2026-04-19 14:00:00', '2026-04-21 12:00:00', 150000),
('DDP03', 'KH03', 'NV03', N'Hoàn thành', '2026-04-20 14:00:00', '2026-04-22 12:00:00', 200000),
('DDP04', 'KH04', 'NV04', N'Hoàn thành', '2026-04-21 13:30:00', '2026-04-23 11:30:00', 250000),
('DDP05', 'KH05', 'NV05', N'Hoàn thành', '2026-04-22 14:00:00', '2026-04-24 12:00:00', 100000),
('DDP06', 'KH06', 'NV01', N'Hoàn thành', '2026-04-23 14:00:00', '2026-04-25 12:00:00', 180000),
('DDP07', 'KH07', 'NV02', N'Hoàn thành', '2026-04-24 14:00:00', '2026-04-26 12:00:00', 220000),
('DDP08', 'KH08', 'NV03', N'Hoàn thành', '2026-04-25 14:30:00', '2026-04-27 12:00:00', 200000),
('DDP09', 'KH09', 'NV04', N'Hoàn thành', '2026-04-26 14:00:00', '2026-04-28 12:00:00', 120000),
('DDP10', 'KH10', 'NV05', N'Hoàn thành', '2026-04-27 14:00:00', '2026-04-29 12:00:00', 250000),

('DDP11', 'KH11', 'NV01', N'Đã nhận', '2026-04-28 13:00:00', '2026-05-01 12:00:00', 160000),
('DDP12', 'KH12', 'NV02', N'Đã nhận', '2026-04-28 14:00:00', '2026-05-02 12:00:00', 140000),
('DDP13', 'KH13', 'NV03', N'Đã nhận', '2026-04-29 12:30:00', '2026-05-01 11:30:00', 210000),
('DDP14', 'KH14', 'NV04', N'Đã nhận', '2026-04-29 14:00:00', '2026-05-02 12:00:00', 170000),
('DDP15', 'KH15', 'NV05', N'Đã nhận', '2026-04-29 09:00:00', '2026-05-01 12:00:00', 300000),
('DDP16', 'KH16', 'NV01', N'Đã nhận', '2026-04-30 11:00:00', '2026-05-03 12:00:00', 130000),
('DDP17', 'KH17', 'NV02', N'Đã nhận', '2026-04-30 13:00:00', '2026-05-03 12:00:00', 190000),
('DDP18', 'KH18', 'NV03', N'Đã nhận', '2026-04-30 15:00:00', '2026-05-04 12:00:00', 210000),
('DDP19', 'KH19', 'NV04', N'Đã nhận', '2026-04-30 10:00:00', '2026-05-03 12:00:00', 220000),

('DDP20', 'KH20', 'NV05', N'Đã đặt', '2026-05-01 14:00:00', '2026-05-03 12:00:00', 110000),
('DDP21', 'KH01', 'NV01', N'Đã đặt', '2026-05-01 14:30:00', '2026-05-04 12:00:00', 180000),
('DDP22', 'KH02', 'NV02', N'Đã đặt', '2026-05-02 14:00:00', '2026-05-04 12:00:00', 200000),
('DDP23', 'KH03', 'NV03', N'Đã đặt', '2026-05-02 15:00:00', '2026-05-05 12:00:00', 100000),
('DDP24', 'KH04', 'NV04', N'Đã đặt', '2026-05-03 14:00:00', '2026-05-06 12:00:00', 260000),
('DDP25', 'KH05', 'NV05', N'Đã đặt', '2026-05-03 15:00:00', '2026-05-06 12:00:00', 230000);
GO

INSERT INTO CTDonDatPhong(maDDP, maPhong, soNgay, donGia) VALUES
('DDP01', 'P101', 2,  800000),
('DDP02', 'P102', 2,  500000),
('DDP03', 'P103', 2,  500000),
('DDP04', 'P104', 2,  650000),
('DDP05', 'P105', 2,  700000),
('DDP06', 'P106', 2,  800000),
('DDP07', 'P108', 2,  900000),
('DDP08', 'P109', 2, 1500000),
('DDP09', 'P110', 2, 1550000),
('DDP10', 'P111', 2,  520000),

('DDP11', 'P112', 3,  680000),
('DDP12', 'P113', 4,  820000),
('DDP13', 'P114', 2,  880000),
('DDP14', 'P115', 3, 1600000),
('DDP15', 'P116', 2,  540000),
('DDP16', 'P118', 3,  870000),
('DDP17', 'P125', 3,  390000),
('DDP18', 'P131', 4,  920000),
('DDP19', 'P139', 3,  750000),

('DDP20', 'P119', 2, 1700000),
('DDP21', 'P120', 3, 2000000),
('DDP22', 'P126', 2,  520000),
('DDP23', 'P127', 3,  530000),
('DDP24', 'P128', 3,  680000),
('DDP25', 'P129', 3,  720000);
GO

INSERT INTO PhieuDichVu(maPDV, maDDP, maPhong, maDV, soLuong, donGia) VALUES
('PDV01', 'DDP01', 'P101', 'DV01', 2, 10000),
('PDV02', 'DDP02', 'P102', 'DV03', 2, 20000),
('PDV03', 'DDP03', 'P103', 'DV04', 1, 15000),
('PDV04', 'DDP04', 'P104', 'DV05', 1, 50000),
('PDV05', 'DDP05', 'P105', 'DV06', 2, 80000),
('PDV06', 'DDP06', 'P106', 'DV01', 3, 10000),
('PDV07', 'DDP07', 'P108', 'DV02', 2, 30000),
('PDV08', 'DDP08', 'P109', 'DV09', 1, 300000),
('PDV09', 'DDP09', 'P110', 'DV10', 1, 250000),
('PDV10', 'DDP10', 'P111', 'DV06', 2, 80000),

('PDV11', 'DDP13', 'P114', 'DV03', 2, 20000),
('PDV12', 'DDP13', 'P114', 'DV06', 1, 80000),
('PDV13', 'DDP14', 'P115', 'DV09', 1, 300000),
('PDV14', 'DDP15', 'P116', 'DV08', 1, 70000),
('PDV15', 'DDP16', 'P118', 'DV07', 1, 150000);
GO

INSERT INTO HoaDon(maHD, maDDP, maKH, maNV, maThue, ngayLapHD, tongTien) VALUES
('HD01', 'DDP01', 'KH01', 'NV03', 'T03', '2026-04-20', 1582000),
('HD02', 'DDP02', 'KH02', 'NV03', 'T03', '2026-04-21', 994000),
('HD03', 'DDP03', 'KH03', 'NV03', 'T03', '2026-04-22', 916500),
('HD04', 'DDP04', 'KH04', 'NV03', 'T03', '2026-04-23', 1235000),
('HD05', 'DDP05', 'KH05', 'NV03', 'T03', '2026-04-24', 1616000),
('HD06', 'DDP06', 'KH06', 'NV03', 'T03', '2026-04-25', 1613000),
('HD07', 'DDP07', 'KH07', 'NV03', 'T03', '2026-04-26', 1826000),
('HD08', 'DDP08', 'KH08', 'NV03', 'T03', '2026-04-27', 3430000),
('HD09', 'DDP09', 'KH09', 'NV03', 'T03', '2026-04-28', 3565000),
('HD10', 'DDP10', 'KH10', 'NV03', 'T03', '2026-04-29', 1070000);
GO

INSERT INTO CTHoaDonPhong(maHD, maPhong, maKM, soNgay, donGia, thanhTien) VALUES
('HD01', 'P101', NULL, 2,  800000, 1600000),
('HD02', 'P102', NULL, 2,  500000, 1000000),
('HD03', 'P103', NULL, 2,  500000, 1000000),
('HD04', 'P104', NULL, 2,  650000, 1300000),
('HD05', 'P105', NULL, 2,  700000, 1400000),
('HD06', 'P106', NULL, 2,  800000, 1600000),
('HD07', 'P108', NULL, 2,  900000, 1800000),
('HD08', 'P109', NULL, 2, 1500000, 3000000),
('HD09', 'P110', NULL, 2, 1550000, 3100000),
('HD10', 'P111', NULL, 2,  520000, 1040000);
GO

INSERT INTO CTHoaDonDichVu(maHD, maPDV, maKM, soLuong, donGia, thanhTien) VALUES
('HD01', 'PDV01', NULL, 2, 10000, 20000),
('HD02', 'PDV02', NULL, 2, 20000, 40000),
('HD03', 'PDV03', NULL, 1, 15000, 15000),
('HD04', 'PDV04', NULL, 1, 50000, 50000),
('HD05', 'PDV05', NULL, 2, 80000, 160000),
('HD06', 'PDV06', NULL, 3, 10000, 30000),
('HD07', 'PDV07', NULL, 2, 30000, 60000),
('HD08', 'PDV08', NULL, 1, 300000, 300000),
('HD09', 'PDV09', NULL, 1, 250000, 250000),
('HD10', 'PDV10', NULL, 2, 80000, 160000);
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

