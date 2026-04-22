IF DB_ID('QuanLyKhachSan') IS NULL
    CREATE DATABASE QuanLyKhachSan;
GO

USE QuanLyKhachSan;
GO

DROP TABLE IF EXISTS CTHoaDon;
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
    tinhTrang  NVARCHAR(50),
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
    CONSTRAINT chk_ngay_dat CHECK (ngayTra >= ngayNhan)
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
    maPhong  VARCHAR(10) NOT NULL,
    maDV     VARCHAR(20) NOT NULL,
    soLuong  INT NOT NULL DEFAULT 1,
    donGia   DECIMAL(15,2) NOT NULL,
    CONSTRAINT fk_pdv_phong
        FOREIGN KEY (maPhong) REFERENCES Phong(maPhong)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_pdv_dichvu
        FOREIGN KEY (maDV) REFERENCES DichVu(maDV)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);
GO

CREATE TABLE KhuyenMai (
    maKM          VARCHAR(10) PRIMARY KEY,
    tenKhuyenMai  NVARCHAR(100) NOT NULL,
    giaTri        DECIMAL(15,2) NOT NULL,
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
    maPDV      VARCHAR(20),
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
        ON DELETE SET NULL,
    CONSTRAINT fk_hoadon_pdv
        FOREIGN KEY (maPDV) REFERENCES PhieuDichVu(maPDV)
        ON UPDATE CASCADE
        ON DELETE SET NULL
);
GO

CREATE TABLE CTHoaDon (
    maHD     VARCHAR(20) NOT NULL,
    maKM     VARCHAR(10),
    maPhong  VARCHAR(10),
    maDV     VARCHAR(20),
    chiTiet  NVARCHAR(255),
    donGia   DECIMAL(15,2) NOT NULL,
    CONSTRAINT pk_cthd PRIMARY KEY (maHD),
    CONSTRAINT fk_cthd_hoadon
        FOREIGN KEY (maHD) REFERENCES HoaDon(maHD)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_cthd_khuyenmai
        FOREIGN KEY (maKM) REFERENCES KhuyenMai(maKM)
        ON UPDATE CASCADE
        ON DELETE SET NULL,
    CONSTRAINT fk_cthd_phong
        FOREIGN KEY (maPhong) REFERENCES Phong(maPhong),
    CONSTRAINT fk_cthd_dichvu
        FOREIGN KEY (maDV) REFERENCES DichVu(maDV)
);
GO

INSERT INTO KhachHang(maKH, hoTen, cccd, sdt, loaiKH, diemSo) VALUES
('KH01', N'Nguyen Van A', '012345678901', '0909123456', N'Thường', 10),
('KH02', N'Tran Van C',   '012345678902', '0909000002', N'Thường', 20),
('KH03', N'Le Thi D',     '012345678903', '0909000003', N'VIP', 35),
('KH04', N'Pham Van E',   '012345678904', '0909000004', N'Thường', 15),
('KH05', N'Hoang Thi F',  '012345678905', '0909000005', N'VIP', 40),
('KH06', N'Nguyen Van G', '012345678906', '0909000006', N'Thường', 12),
('KH07', N'Tran Thi H',   '012345678907', '0909000007', N'Thân thiết', 55),
('KH08', N'Le Van I',     '012345678908', '0909000008', N'Thường', 18),
('KH09', N'Pham Thi K',   '012345678909', '0909000009', N'VIP', 42),
('KH10', N'Hoang Van L',  '012345678910', '0909000010', N'Thường', 25),
('KH11', N'Do Thi M',     '012345678911', '0909000011', N'Thường', 14),
('KH12', N'Bui Van N',    '012345678912', '0909000012', N'Thân thiết', 60),
('KH13', N'Vo Thi O',     '012345678913', '0909000013', N'Thường', 16),
('KH14', N'Dang Van P',   '012345678914', '0909000014', N'VIP', 45),
('KH15', N'Nguyen Thi Q', '012345678915', '0909000015', N'Thường', 22),
('KH16', N'Truong Van R', '012345678916', '0909000016', N'Thường', 11),
('KH17', N'Ly Thi S',     '012345678917', '0909000017', N'Thân thiết', 58),
('KH18', N'Phan Van T',   '012345678918', '0909000018', N'Thường', 19),
('KH19', N'Mai Thi U',    '012345678919', '0909000019', N'VIP', 48),
('KH20', N'Dinh Van V',   '012345678920', '0909000020', N'Thường', 13);
GO

INSERT INTO NhanVien(maNV, hoTen, ngaySinh, sdt, email, gioiTinh, ngayBatDauVaoLam, trangThaiLamViec, diaChi, caLamViec, viTriCongViec) VALUES
('NV01', N'Tran Thi B',     '1996-01-10', '0911222333', 'b@hotel.com',    N'Nữ',  '2023-01-01', N'Đang làm', N'Hà Nội',     N'Ca sáng',  N'Lễ tân'),
('NV02', N'Nguyen Thi Lan', '1998-04-12', '0911222334', 'lan@hotel.com',  N'Nữ',  '2024-01-10', N'Đang làm', N'Hà Nội',     N'Ca sáng',  N'Lễ tân'),
('NV03', N'Tran Van Minh',  '1995-08-20', '0911222335', 'minh@hotel.com', N'Nam', '2023-11-01', N'Đang làm', N'Hải Phòng',  N'Ca chiều', N'Bảo vệ'),
('NV04', N'Le Thi Hoa',     '1997-02-15', '0911222336', 'hoa@hotel.com',  N'Nữ',  '2024-03-05', N'Đang làm', N'Đà Nẵng',    N'Ca sáng',  N'Kế toán'),
('NV05', N'Pham Quoc Dat',  '1994-12-09', '0911222337', 'dat@hotel.com',  N'Nam', '2022-09-18', N'Đang làm', N'TP.HCM',     N'Ca tối',   N'Quản lý');
GO

INSERT INTO TaiKhoan(tenDangNhap, matKhau, vaiTro, maNV) VALUES
('DoThanhTuong', '123456', N'LeTan',  'NV01'),
('LeMinhTan', '123456', N'LeTan',  'NV02'),
('NguyenThiThanhThu', '123456', N'BaoVe',  'NV03'),
('nv04', '123456', N'KeToan', 'NV04'),
('nv05', '123456', N'QuanLy', 'NV05');
GO

INSERT INTO Phong(maPhong, loaiPhong, soNguoiToiDa, giaPhong, trangThaiPhong) VALUES
('P101', N'Deluxe',   2,  800000,  N'Trống'),
('P102', N'Standard', 2,  500000,  N'Trống'),
('P103', N'Standard', 2,  500000,  N'Trống'),
('P104', N'Superior', 2,  650000,  N'Trống'),
('P105', N'Superior', 3,  700000,  N'Đang sử dụng'),
('P106', N'Deluxe',   2,  800000,  N'Trống'),
('P107', N'Deluxe',   2,  850000,  N'Bảo trì'),
('P108', N'Deluxe',   3,  900000,  N'Trống'),
('P109', N'Suite',    4, 1500000,  N'Trống'),
('P110', N'Suite',    4, 1550000,  N'Đang sử dụng'),
('P111', N'Standard', 2,  520000,  N'Trống'),
('P112', N'Superior', 2,  680000,  N'Trống'),
('P113', N'Deluxe',   2,  820000,  N'Trống'),
('P114', N'Deluxe',   3,  880000,  N'Đang sử dụng'),
('P115', N'Suite',    4, 1600000,  N'Trống'),
('P116', N'Standard', 2,  540000,  N'Trống'),
('P117', N'Superior', 3,  720000,  N'Bảo trì'),
('P118', N'Deluxe',   2,  870000,  N'Trống'),
('P119', N'Suite',    5, 1700000,  N'Trống'),
('P120', N'Family',   6, 2000000,  N'Trống');
GO

INSERT INTO DonDatPhong(maDDP, maKH, maNV, tinhTrang, ngayNhan, ngayTra, tienCoc) VALUES
('DDP01', 'KH01', 'NV01', N'Hoàn thành', '2026-04-10 14:00:00', '2026-04-12 12:00:00', 200000),
('DDP02', 'KH02', 'NV02', N'Hoàn thành', '2026-04-11 14:00:00', '2026-04-13 12:00:00', 150000),
('DDP03', 'KH03', 'NV03', N'Hoàn thành', '2026-04-12 14:00:00', '2026-04-14 12:00:00', 200000),
('DDP04', 'KH04', 'NV04', N'Hoàn thành', '2026-04-13 13:30:00', '2026-04-15 11:30:00', 250000),
('DDP05', 'KH05', 'NV05', N'Hoàn thành', '2026-04-14 14:00:00', '2026-04-16 12:00:00', 100000),
('DDP06', 'KH06', 'NV01', N'Hoàn thành', '2026-04-15 14:00:00', '2026-04-17 12:00:00', 180000),
('DDP07', 'KH07', 'NV02', N'Hoàn thành', '2026-04-16 14:00:00', '2026-04-18 12:00:00', 220000),
('DDP08', 'KH08', 'NV03', N'Hoàn thành', '2026-04-17 14:30:00', '2026-04-19 12:00:00', 200000),
('DDP09', 'KH09', 'NV04', N'Hoàn thành', '2026-04-18 14:00:00', '2026-04-20 12:00:00', 120000),
('DDP10', 'KH10', 'NV05', N'Hoàn thành', '2026-04-19 14:00:00', '2026-04-21 12:00:00', 250000),

('DDP11', 'KH11', 'NV01', N'Đã nhận',    '2026-04-20 13:00:00', '2026-04-22 12:00:00', 160000),
('DDP12', 'KH12', 'NV02', N'Đã nhận',    '2026-04-20 14:00:00', '2026-04-23 12:00:00', 140000),
('DDP13', 'KH13', 'NV03', N'Đã nhận',    '2026-04-21 12:30:00', '2026-04-23 11:30:00', 210000),
('DDP14', 'KH14', 'NV04', N'Đã nhận',    '2026-04-21 14:00:00', '2026-04-24 12:00:00', 170000),
('DDP15', 'KH15', 'NV05', N'Đã nhận',    '2026-04-22 09:00:00', '2026-04-24 12:00:00', 300000),
('DDP16', 'KH16', 'NV01', N'Đã nhận',    '2026-04-22 11:00:00', '2026-04-25 12:00:00', 130000),

('DDP17', 'KH17', 'NV02', N'Đã đặt',     '2026-04-22 14:00:00', '2026-04-23 12:00:00', 190000),
('DDP18', 'KH18', 'NV03', N'Đã đặt',     '2026-04-22 16:00:00', '2026-04-24 12:00:00', 210000),
('DDP19', 'KH19', 'NV04', N'Đã đặt',     '2026-04-23 14:00:00', '2026-04-25 12:00:00', 220000),
('DDP20', 'KH20', 'NV05', N'Đã đặt',     '2026-04-23 15:00:00', '2026-04-26 12:00:00', 110000),
('DDP21', 'KH01', 'NV01', N'Đã đặt',     '2026-04-24 14:00:00', '2026-04-26 12:00:00', 180000),
('DDP22', 'KH02', 'NV02', N'Đã đặt',     '2026-04-24 14:30:00', '2026-04-27 12:00:00', 200000),
('DDP23', 'KH03', 'NV03', N'Đã đặt',     '2026-04-25 14:00:00', '2026-04-27 12:00:00', 100000),
('DDP24', 'KH04', 'NV04', N'Đã đặt',     '2026-04-25 15:00:00', '2026-04-28 12:00:00', 260000),
('DDP25', 'KH05', 'NV05', N'Đã đặt',     '2026-04-26 14:00:00', '2026-04-28 12:00:00', 230000),
('DDP26', 'KH06', 'NV01', N'Đã đặt',     '2026-04-26 14:30:00', '2026-04-29 12:00:00', 190000),
('DDP27', 'KH07', 'NV02', N'Đã đặt',     '2026-04-27 14:00:00', '2026-04-29 12:00:00',  90000),
('DDP28', 'KH08', 'NV03', N'Đã đặt',     '2026-04-27 15:30:00', '2026-04-30 12:00:00', 320000),
('DDP29', 'KH09', 'NV04', N'Đã đặt',     '2026-04-28 14:00:00', '2026-05-01 12:00:00', 240000),
('DDP30', 'KH10', 'NV05', N'Đã đặt',     '2026-04-29 14:00:00', '2026-05-02 12:00:00', 350000);
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
('DDP11', 'P112', 2,  680000),
('DDP12', 'P113', 2,  820000),
('DDP13', 'P114', 2,  880000),
('DDP14', 'P115', 2, 1600000),
('DDP15', 'P116', 2,  540000),
('DDP16', 'P118', 2,  870000),
('DDP17', 'P119', 2, 1700000),
('DDP18', 'P120', 2, 2000000),
('DDP19', 'P101', 2,  800000),
('DDP20', 'P102', 2,  500000),
('DDP21', 'P103', 2,  500000),
('DDP22', 'P104', 2,  650000),
('DDP23', 'P105', 2,  700000),
('DDP24', 'P106', 2,  800000),
('DDP25', 'P108', 2,  900000),
('DDP26', 'P109', 2, 1500000),
('DDP27', 'P110', 2, 1550000),
('DDP28', 'P111', 3,  520000),
('DDP29', 'P112', 3,  680000),
('DDP30', 'P113', 3,  820000);
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

INSERT INTO PhieuDichVu(maPDV, maPhong, maDV, soLuong, donGia) VALUES
('PDV01', 'P101', 'DV01', 2, 10000),
('PDV02', 'P102', 'DV03', 2, 20000),
('PDV03', 'P103', 'DV04', 1, 15000),
('PDV04', 'P104', 'DV05', 1, 50000),
('PDV05', 'P105', 'DV06', 2, 80000),
('PDV06', 'P106', 'DV01', 3, 10000),
('PDV07', 'P108', 'DV02', 2, 30000),
('PDV08', 'P109', 'DV09', 1, 300000),
('PDV09', 'P110', 'DV10', 1, 250000),
('PDV10', 'P111', 'DV06', 2, 80000),
('PDV11', 'P112', 'DV05', 1, 50000),
('PDV12', 'P113', 'DV01', 4, 10000),
('PDV13', 'P114', 'DV03', 2, 20000),
('PDV14', 'P115', 'DV09', 1, 300000),
('PDV15', 'P116', 'DV08', 1, 70000),
('PDV16', 'P118', 'DV07', 1, 150000),
('PDV17', 'P119', 'DV10', 1, 250000),
('PDV18', 'P120', 'DV06', 4, 80000),
('PDV19', 'P101', 'DV05', 1, 50000),
('PDV20', 'P102', 'DV01', 2, 10000);
GO

INSERT INTO KhuyenMai(maKM, tenKhuyenMai, giaTri, ngayBatDau, ngayKetThuc) VALUES
('KM01', N'Giảm 10%',              10, '2025-01-01', '2025-12-31'),
('KM02', N'Giảm 20%',              20, '2025-03-01', '2025-03-31'),
('KM03', N'Khách hàng thân thiết', 15, '2025-01-01', '2025-12-31'),
('KM04', N'Lễ 30/4 - 1/5',         25, '2025-04-25', '2025-05-02'),
('KM05', N'Ưu đãi mùa hè',         12, '2025-06-01', '2025-08-31'),
('KM06', N'Đặt sớm',                8, '2025-01-01', '2025-12-31'),
('KM07', N'Combo gia đình',        18, '2025-02-01', '2025-12-31'),
('KM08', N'Flash sale cuối tuần',   5, '2025-03-20', '2025-03-22');
GO

INSERT INTO Thue(maThue, tenThue, trangThai, tyLeThue, moTa) VALUES
('T01', N'VAT 5%',          N'Áp dụng',        5.00, N'Thuế VAT áp dụng cho một số dịch vụ'),
('T02', N'VAT 8%',          N'Áp dụng',        8.00, N'Thuế VAT ưu đãi'),
('T03', N'VAT 10%',         N'Áp dụng',       10.00, N'Thuế VAT tiêu chuẩn'),
('T04', N'Phụ thu lễ',      N'Áp dụng',       15.00, N'Phụ thu vào dịp lễ tết'),
('T05', N'Không tính thuế', N'Ngưng áp dụng',  0.00, N'Dùng cho trường hợp miễn thuế');
GO

INSERT INTO HoaDon(maHD, maDDP, maKH, maNV, maThue, maPDV, ngayLapHD, tongTien) VALUES
('HD01', 'DDP01', 'KH01', 'NV01', 'T03', 'PDV01', '2025-03-23', 1760000),
('HD02', 'DDP02', 'KH02', 'NV02', 'T03', 'PDV02', '2025-03-24', 1100000),
('HD03', 'DDP03', 'KH03', 'NV03', 'T03', 'PDV03', '2025-03-25', 1100000),
('HD04', 'DDP04', 'KH04', 'NV04', 'T03', 'PDV04', '2025-03-26', 1430000),
('HD05', 'DDP05', 'KH05', 'NV05', 'T03', 'PDV05', '2025-03-22', 1540000),
('HD06', 'DDP06', 'KH06', 'NV01', 'T03', 'PDV06', '2025-03-27', 1760000),
('HD07', 'DDP07', 'KH07', 'NV02', 'T03', 'PDV07', '2025-03-28', 1980000),
('HD08', 'DDP08', 'KH08', 'NV03', 'T03', 'PDV08', '2025-03-29', 3300000),
('HD09', 'DDP09', 'KH09', 'NV04', 'T03', 'PDV09', '2025-03-20', 3410000),
('HD10', 'DDP10', 'KH10', 'NV05', 'T03', 'PDV10', '2025-03-30', 1144000),
('HD11', 'DDP11', 'KH11', 'NV01', 'T03', 'PDV11', '2025-03-23', 1496000),
('HD12', 'DDP12', 'KH12', 'NV02', 'T03', 'PDV12', '2025-03-19', 1804000),
('HD13', 'DDP13', 'KH13', 'NV03', 'T03', 'PDV13', '2025-03-31', 1936000),
('HD14', 'DDP14', 'KH14', 'NV04', 'T03', 'PDV14', '2025-03-21', 3520000),
('HD15', 'DDP15', 'KH15', 'NV05', 'T03', 'PDV15', '2025-04-01', 1188000),
('HD16', 'DDP16', 'KH16', 'NV01', 'T03', 'PDV16', '2025-03-17', 1914000),
('HD17', 'DDP17', 'KH17', 'NV02', 'T03', 'PDV17', '2025-03-26', 3740000),
('HD18', 'DDP18', 'KH18', 'NV03', 'T03', 'PDV18', '2025-03-27', 4400000),
('HD19', 'DDP19', 'KH19', 'NV04', 'T03', 'PDV19', '2025-03-28', 1760000),
('HD20', 'DDP20', 'KH20', 'NV05', 'T03', 'PDV20', '2025-03-16', 1100000),
('HD21', 'DDP21', 'KH01', 'NV01', 'T03', NULL,    '2025-03-29', 1100000),
('HD22', 'DDP22', 'KH02', 'NV02', 'T03', NULL,    '2025-03-30', 1430000),
('HD23', 'DDP23', 'KH03', 'NV03', 'T03', NULL,    '2025-03-14', 1540000),
('HD24', 'DDP24', 'KH04', 'NV04', 'T03', NULL,    '2025-03-31', 1760000),
('HD25', 'DDP25', 'KH05', 'NV05', 'T03', NULL,    '2025-03-24', 1980000),
('HD26', 'DDP26', 'KH06', 'NV01', 'T03', NULL,    '2025-03-25', 3300000),
('HD27', 'DDP27', 'KH07', 'NV02', 'T03', NULL,    '2025-03-12', 3410000),
('HD28', 'DDP28', 'KH08', 'NV03', 'T03', NULL,    '2025-04-02', 1716000),
('HD29', 'DDP29', 'KH09', 'NV04', 'T03', NULL,    '2025-03-27', 2244000),
('HD30', 'DDP30', 'KH10', 'NV05', 'T03', NULL,    '2025-04-03', 2706000);
GO

INSERT INTO CTHoaDon(maHD, maKM, maPhong, maDV, chiTiet, donGia) VALUES
('HD01', 'KM01', 'P101', 'DV01', N'Tiền phòng + thuế', 1760000),
('HD02', 'KM02', 'P102', 'DV02', N'Tiền phòng + thuế', 1100000),
('HD03', 'KM03', 'P103', 'DV01', N'Tiền phòng + thuế', 1100000),
('HD04', 'KM04', 'P104', 'DV03', N'Tiền phòng + thuế', 1430000),
('HD05', 'KM05', 'P105', 'DV02', N'Tiền phòng + thuế', 1540000),
('HD06', 'KM06', 'P106', 'DV01', N'Tiền phòng + thuế', 1760000),
('HD07', 'KM07', 'P108', 'DV04', N'Tiền phòng + thuế', 1980000),
('HD08', 'KM08', 'P109', 'DV05', N'Tiền phòng + thuế', 3300000),
('HD09', 'KM01', 'P110', 'DV02', N'Tiền phòng + thuế', 3410000),
('HD10', 'KM02', 'P111', 'DV01', N'Tiền phòng + thuế', 1144000),
('HD11', 'KM03', 'P112', 'DV03', N'Tiền phòng + thuế', 1496000),
('HD12', 'KM04', 'P113', 'DV04', N'Tiền phòng + thuế', 1804000),
('HD13', 'KM05', 'P114', 'DV02', N'Tiền phòng + thuế', 1936000),
('HD14', 'KM06', 'P115', 'DV05', N'Tiền phòng + thuế', 3520000),
('HD15', 'KM07', 'P116', 'DV01', N'Tiền phòng + thuế', 1188000),
('HD16', 'KM08', 'P118', 'DV03', N'Tiền phòng + thuế', 1914000),
('HD17', 'KM01', 'P119', 'DV04', N'Tiền phòng + thuế', 3740000),
('HD18', 'KM02', 'P120', 'DV05', N'Tiền phòng + thuế', 4400000),
('HD19', 'KM03', 'P101', 'DV01', N'Tiền phòng + thuế', 1760000),
('HD20', 'KM04', 'P102', 'DV02', N'Tiền phòng + thuế', 1100000),
('HD21', 'KM05', 'P103', 'DV03', N'Tiền phòng + thuế', 1100000),
('HD22', 'KM06', 'P104', 'DV04', N'Tiền phòng + thuế', 1430000),
('HD23', 'KM07', 'P105', 'DV05', N'Tiền phòng + thuế', 1540000),
('HD24', 'KM08', 'P106', 'DV01', N'Tiền phòng + thuế', 1760000),
('HD25', 'KM01', 'P108', 'DV02', N'Tiền phòng + thuế', 1980000),
('HD26', 'KM02', 'P109', 'DV03', N'Tiền phòng + thuế', 3300000),
('HD27', 'KM03', 'P110', 'DV04', N'Tiền phòng + thuế', 3410000),
('HD28', 'KM04', 'P111', 'DV05', N'Tiền phòng + thuế', 1716000),
('HD29', 'KM05', 'P112', 'DV01', N'Tiền phòng + thuế', 2244000),
('HD30', 'KM06', 'P113', 'DV02', N'Tiền phòng + thuế', 2706000);
GO

SELECT COUNT(*) AS soKhachHang FROM KhachHang;
SELECT COUNT(*) AS soNhanVien FROM NhanVien;
SELECT COUNT(*) AS soPhong FROM Phong;
SELECT COUNT(*) AS soDonDatPhong FROM DonDatPhong;
SELECT COUNT(*) AS soHoaDon FROM HoaDon;
SELECT COUNT(*) AS soPhieuDichVu FROM PhieuDichVu;
GO
