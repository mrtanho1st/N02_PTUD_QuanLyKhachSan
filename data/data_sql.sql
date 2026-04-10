IF DB_ID('QuanLyKhachSan') IS NULL
    CREATE DATABASE QuanLyKhachSan;
GO

USE QuanLyKhachSan;
GO

-- XOA BANG NEU DA TON TAI
DROP TABLE IF EXISTS CTHoaDon;
DROP TABLE IF EXISTS HoaDon;
DROP TABLE IF EXISTS CTDichVu;
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

-- =========================
-- 1. BANG PHONG
-- =========================
CREATE TABLE Phong (
    maPhong        VARCHAR(10) PRIMARY KEY,
    loaiPhong      NVARCHAR(50) NOT NULL,
    soNguoiToiDa   INT NOT NULL,
    giaPhong       DECIMAL(15,2) NOT NULL,
    trangThaiPhong NVARCHAR(50) NOT NULL
);

-- =========================
-- 2. BANG KHACH HANG
-- =========================
CREATE TABLE KhachHang (
    maKH    VARCHAR(10) PRIMARY KEY,
    hoTen   NVARCHAR(100) NOT NULL,
    cccd    VARCHAR(20) UNIQUE,
    sdt     VARCHAR(15)
);

-- =========================
-- 3. BANG NHAN VIEN
-- =========================
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

-- =========================
-- 4. BANG TAI KHOAN
-- =========================
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

-- =========================
-- 5. BANG DON DAT PHONG
-- =========================
CREATE TABLE DonDatPhong (
    maDDP      VARCHAR(20) PRIMARY KEY,
    maKH       VARCHAR(10),
    maNV       VARCHAR(10),
    tinhTrang  NVARCHAR(50),
    ngayNhan   DATE NOT NULL,
    ngayTra    DATE NOT NULL,
    tienCoc    DECIMAL(15,2) DEFAULT 0,

    CONSTRAINT fk_ddp_khachhang
        FOREIGN KEY (maKH) REFERENCES KhachHang(maKH)
        ON UPDATE CASCADE
        ON DELETE SET NULL,

    CONSTRAINT fk_ddp_nhanvien
        FOREIGN KEY (maNV) REFERENCES NhanVien(maNV)
        ON UPDATE CASCADE
        ON DELETE SET NULL,

    CONSTRAINT chk_ngay_dat
        CHECK (ngayTra >= ngayNhan)
);

-- =========================
-- 6. BANG CHI TIET DON DAT PHONG
-- =========================
CREATE TABLE CTDonDatPhong (
    maCTDDP   VARCHAR(20) PRIMARY KEY,
    maDDP     VARCHAR(20) NOT NULL,
    maPhong   VARCHAR(10) NOT NULL,
    soNgay    INT NOT NULL,
    donGia    DECIMAL(15,2) NOT NULL,

    CONSTRAINT fk_ctddp_ddp
        FOREIGN KEY (maDDP) REFERENCES DonDatPhong(maDDP)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT fk_ctddp_phong
        FOREIGN KEY (maPhong) REFERENCES Phong(maPhong)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

-- =========================
-- 7. BANG DICH VU
-- =========================
CREATE TABLE DichVu (
    maDV       VARCHAR(20) PRIMARY KEY,
    tenDichVu  NVARCHAR(100) NOT NULL,
    giaDichVu  DECIMAL(15,2) NOT NULL
);

-- =========================
-- 8. BANG CHI TIET DICH VU
-- =========================
CREATE TABLE CTDichVu (
    maCTDV   VARCHAR(20) PRIMARY KEY,
    maPhong  VARCHAR(10) NOT NULL,
    maDV     VARCHAR(20) NOT NULL,
    soLuong  INT NOT NULL DEFAULT 1,
    donGia   DECIMAL(15,2) NOT NULL,

    CONSTRAINT fk_ctdv_phong
        FOREIGN KEY (maPhong) REFERENCES Phong(maPhong)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT fk_ctdv_dichvu
        FOREIGN KEY (maDV) REFERENCES DichVu(maDV)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

-- =========================
-- 9. BANG KHUYEN MAI
-- =========================
CREATE TABLE KhuyenMai (
    maKM          VARCHAR(10) PRIMARY KEY,
    tenKhuyenMai  NVARCHAR(100) NOT NULL,
    giaTri        DECIMAL(15,2) NOT NULL,
    ngayBatDau    DATE,
    ngayKetThuc   DATE,
    CONSTRAINT chk_ngay_km
        CHECK (ngayKetThuc IS NULL OR ngayBatDau IS NULL OR ngayKetThuc >= ngayBatDau)
);

-- =========================
-- 10. BANG THUE
-- =========================
CREATE TABLE Thue (
    maThue    VARCHAR(10) PRIMARY KEY,
    tenThue   NVARCHAR(100) NOT NULL,
    trangThai NVARCHAR(50),
    tyLeThue  DECIMAL(5,2) NOT NULL,
    moTa      NVARCHAR(255)
);

-- =========================
-- 11. BANG HOA DON
-- =========================
CREATE TABLE HoaDon (
    maHD       VARCHAR(20) PRIMARY KEY,
    maDDP      VARCHAR(20) NOT NULL,
    maThue     VARCHAR(10),
    ngayLapHD  DATE NOT NULL,
    tongTien   DECIMAL(15,2) NOT NULL DEFAULT 0,

    CONSTRAINT fk_hoadon_ddp
        FOREIGN KEY (maDDP) REFERENCES DonDatPhong(maDDP)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT fk_hoadon_thue
        FOREIGN KEY (maThue) REFERENCES Thue(maThue)
        ON UPDATE CASCADE
        ON DELETE SET NULL
);

-- =========================
-- 12. BANG CHI TIET HOA DON
-- =========================
CREATE TABLE CTHoaDon (
    maCTHD   VARCHAR(20) PRIMARY KEY,
    maHD     VARCHAR(20) NOT NULL,
    maKM     VARCHAR(10),
    chiTiet  NVARCHAR(255),
    donGia   DECIMAL(15,2) NOT NULL,

    CONSTRAINT fk_cthd_hoadon
        FOREIGN KEY (maHD) REFERENCES HoaDon(maHD)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT fk_cthd_khuyenmai
        FOREIGN KEY (maKM) REFERENCES KhuyenMai(maKM)
        ON UPDATE CASCADE
        ON DELETE SET NULL
);
GO

-- =========================
-- DU LIEU BANG KHACH HANG
-- =========================
INSERT INTO KhachHang(maKH, hoTen, cccd, sdt) VALUES
('KH01', N'Nguyen Van A', '012345678901', '0909123456'),
('KH02', N'Tran Van C', '012345678902', '0909000002'),
('KH03', N'Le Thi D', '012345678903', '0909000003'),
('KH04', N'Pham Van E', '012345678904', '0909000004'),
('KH05', N'Hoang Thi F', '012345678905', '0909000005'),
('KH06', N'Nguyen Van G', '012345678906', '0909000006'),
('KH07', N'Tran Thi H', '012345678907', '0909000007'),
('KH08', N'Le Van I', '012345678908', '0909000008'),
('KH09', N'Pham Thi K', '012345678909', '0909000009'),
('KH10', N'Hoang Van L', '012345678910', '0909000010'),
('KH11', N'Do Thi M', '012345678911', '0909000011'),
('KH12', N'Bui Van N', '012345678912', '0909000012'),
('KH13', N'Vo Thi O', '012345678913', '0909000013'),
('KH14', N'Dang Van P', '012345678914', '0909000014'),
('KH15', N'Nguyen Thi Q', '012345678915', '0909000015'),
('KH16', N'Truong Van R', '012345678916', '0909000016'),
('KH17', N'Ly Thi S', '012345678917', '0909000017'),
('KH18', N'Phan Van T', '012345678918', '0909000018'),
('KH19', N'Mai Thi U', '012345678919', '0909000019'),
('KH20', N'Dinh Van V', '012345678920', '0909000020');

-- =========================
-- DU LIEU BANG NHAN VIEN
-- =========================
INSERT INTO NhanVien(maNV, hoTen, ngaySinh, sdt, email, gioiTinh, ngayBatDauVaoLam, trangThaiLamViec, diaChi, caLamViec, viTriCongViec) VALUES
('NV01', N'Tran Thi B',   '1996-01-10', '0911222333', 'b@hotel.com',    N'Nữ',  '2023-01-01', N'Đang làm', N'Hà Nội',   N'Ca sáng', N'Lễ tân'),
('NV02', N'Nguyen Thi Lan', '1998-04-12', '0911222334', 'lan@hotel.com',  N'Nữ',  '2024-01-10', N'Đang làm', N'Hà Nội',   N'Ca sáng', N'Lễ tân'),
('NV03', N'Tran Van Minh',  '1995-08-20', '0911222335', 'minh@hotel.com', N'Nam', '2023-11-01', N'Đang làm', N'Hải Phòng',N'Ca chiều', N'Bảo vệ'),
('NV04', N'Le Thi Hoa',     '1997-02-15', '0911222336', 'hoa@hotel.com',  N'Nữ',  '2024-03-05', N'Đang làm', N'Đà Nẵng',  N'Ca sáng', N'Kế toán'),
('NV05', N'Pham Quoc Dat',  '1994-12-09', '0911222337', 'dat@hotel.com',  N'Nam', '2022-09-18', N'Đang làm', N'TP.HCM',   N'Ca tối',  N'Quản lý');

-- =========================
-- DU LIEU BANG PHONG
-- =========================
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

-- =========================
-- DU LIEU BANG DON DAT PHONG
-- =========================
INSERT INTO DonDatPhong(maDDP, maKH, maNV, tinhTrang, ngayNhan, ngayTra, tienCoc) VALUES
('DDP01', 'KH01', 'NV01', N'Đã đặt',     '2025-03-21', '2025-03-23', 200000),
('DDP02', 'KH02', 'NV02', N'Đã đặt',     '2025-03-22', '2025-03-24', 150000),
('DDP03', 'KH03', 'NV03', N'Đã đặt',     '2025-03-23', '2025-03-25', 200000),
('DDP04', 'KH04', 'NV04', N'Đã nhận',    '2025-03-24', '2025-03-26', 250000),
('DDP05', 'KH05', 'NV05', N'Hoàn thành', '2025-03-20', '2025-03-22', 100000),
('DDP06', 'KH06', 'NV01', N'Đã đặt',     '2025-03-25', '2025-03-27', 180000),
('DDP07', 'KH07', 'NV02', N'Đã nhận',    '2025-03-26', '2025-03-28', 220000),
('DDP08', 'KH08', 'NV03', N'Đã đặt',     '2025-03-27', '2025-03-29', 200000),
('DDP09', 'KH09', 'NV04', N'Hoàn thành', '2025-03-18', '2025-03-20', 120000),
('DDP10', 'KH10', 'NV05', N'Đã đặt',     '2025-03-28', '2025-03-30', 250000),
('DDP11', 'KH11', 'NV01', N'Đã nhận',    '2025-03-21', '2025-03-23', 160000),
('DDP12', 'KH12', 'NV02', N'Hoàn thành', '2025-03-17', '2025-03-19', 140000),
('DDP13', 'KH13', 'NV03', N'Đã đặt',     '2025-03-29', '2025-03-31', 210000),
('DDP14', 'KH14', 'NV04', N'Đã nhận',    '2025-03-19', '2025-03-21', 170000),
('DDP15', 'KH15', 'NV05', N'Đã đặt',     '2025-03-30', '2025-04-01', 300000),
('DDP16', 'KH16', 'NV01', N'Hoàn thành', '2025-03-15', '2025-03-17', 130000),
('DDP17', 'KH17', 'NV02', N'Đã đặt',     '2025-03-24', '2025-03-26', 190000),
('DDP18', 'KH18', 'NV03', N'Đã nhận',    '2025-03-25', '2025-03-27', 210000),
('DDP19', 'KH19', 'NV04', N'Đã đặt',     '2025-03-26', '2025-03-28', 220000),
('DDP20', 'KH20', 'NV05', N'Hoàn thành', '2025-03-14', '2025-03-16', 110000),
('DDP21', 'KH01', 'NV01', N'Đã đặt',     '2025-03-27', '2025-03-29', 180000),
('DDP22', 'KH02', 'NV02', N'Đã nhận',    '2025-03-28', '2025-03-30', 200000),
('DDP23', 'KH03', 'NV03', N'Hoàn thành', '2025-03-12', '2025-03-14', 100000),
('DDP24', 'KH04', 'NV04', N'Đã đặt',     '2025-03-29', '2025-03-31', 260000),
('DDP25', 'KH05', 'NV05', N'Đã nhận',    '2025-03-22', '2025-03-24', 230000),
('DDP26', 'KH06', 'NV01', N'Đã đặt',     '2025-03-23', '2025-03-25', 190000),
('DDP27', 'KH07', 'NV02', N'Hoàn thành', '2025-03-10', '2025-03-12',  90000),
('DDP28', 'KH08', 'NV03', N'Đã đặt',     '2025-03-30', '2025-04-02', 320000),
('DDP29', 'KH09', 'NV04', N'Đã nhận',    '2025-03-24', '2025-03-27', 240000),
('DDP30', 'KH10', 'NV05', N'Đã đặt',     '2025-03-31', '2025-04-03', 350000);

-- =========================
-- DU LIEU BANG DICH VU
-- =========================
INSERT INTO DichVu(maDV, tenDichVu, giaDichVu) VALUES
('DV01', N'Nước suối',        10000),
('DV02', N'Bia',              30000),
('DV03', N'Nước ngọt',        20000),
('DV04', N'Mì gói',           15000),
('DV05', N'Giặt ủi',          50000),
('DV06', N'Ăn sáng',          80000),
('DV07', N'Thuê xe máy',     150000),
('DV08', N'Dọn phòng',        70000),
('DV09', N'Spa',             300000),
('DV10', N'Đưa đón sân bay', 250000);

-- =========================
-- DU LIEU BANG KHUYEN MAI
-- =========================
INSERT INTO KhuyenMai(maKM, tenKhuyenMai, giaTri, ngayBatDau, ngayKetThuc) VALUES
('KM01', N'Giảm 10%',             10, '2025-01-01', '2025-12-31'),
('KM02', N'Giảm 20%',             20, '2025-03-01', '2025-03-31'),
('KM03', N'Khách hàng thân thiết',15, '2025-01-01', '2025-12-31'),
('KM04', N'Lễ 30/4 - 1/5',        25, '2025-04-25', '2025-05-02'),
('KM05', N'Ưu đãi mùa hè',        12, '2025-06-01', '2025-08-31'),
('KM06', N'Đặt sớm',               8, '2025-01-01', '2025-12-31'),
('KM07', N'Combo gia đình',       18, '2025-02-01', '2025-12-31'),
('KM08', N'Flash sale cuối tuần',  5, '2025-03-20', '2025-03-22');

-- =========================
-- DU LIEU BANG THUE
-- =========================
INSERT INTO Thue(maThue, tenThue, trangThai, tyLeThue, moTa) VALUES
('T01', N'VAT 5%',         N'Áp dụng',       5.00,  N'Thuế VAT áp dụng cho một số dịch vụ'),
('T02', N'VAT 8%',         N'Áp dụng',       8.00,  N'Thuế VAT ưu đãi'),
('T03', N'VAT 10%',        N'Áp dụng',      10.00,  N'Thuế VAT tiêu chuẩn'),
('T04', N'Phụ thu lễ',     N'Áp dụng',      15.00,  N'Phụ thu vào dịp lễ tết'),
('T05', N'Không tính thuế',N'Ngưng áp dụng', 0.00,  N'Dùng cho trường hợp miễn thuế');

-- =========================
-- DU LIEU BANG TAI KHOAN
-- =========================
INSERT INTO TaiKhoan(tenDangNhap, matKhau, vaiTro, maNV) VALUES
('nv01', '123456', N'LeTan',  'NV01'),
('nv02', '123456', N'LeTan',  'NV02'),
('nv03', '123456', N'BaoVe',  'NV03'),
('nv04', '123456', N'KeToan', 'NV04'),
('nv05', '123456', N'QuanLy', 'NV05');

-- =========================
-- DU LIEU BANG CT DON DAT PHONG
-- =========================
INSERT INTO CTDonDatPhong(maCTDDP, maDDP, maPhong, soNgay, donGia) VALUES
('CTDDP01', 'DDP01', 'P101', 2,  800000),
('CTDDP02', 'DDP02', 'P102', 2,  500000),
('CTDDP03', 'DDP03', 'P103', 2,  500000),
('CTDDP04', 'DDP04', 'P104', 2,  650000),
('CTDDP05', 'DDP05', 'P105', 2,  700000),
('CTDDP06', 'DDP06', 'P106', 2,  800000),
('CTDDP07', 'DDP07', 'P108', 2,  900000),
('CTDDP08', 'DDP08', 'P109', 2, 1500000),
('CTDDP09', 'DDP09', 'P110', 2, 1550000),
('CTDDP10', 'DDP10', 'P111', 2,  520000),
('CTDDP11', 'DDP11', 'P112', 2,  680000),
('CTDDP12', 'DDP12', 'P113', 2,  820000),
('CTDDP13', 'DDP13', 'P114', 2,  880000),
('CTDDP14', 'DDP14', 'P115', 2, 1600000),
('CTDDP15', 'DDP15', 'P116', 2,  540000),
('CTDDP16', 'DDP16', 'P118', 2,  870000),
('CTDDP17', 'DDP17', 'P119', 2, 1700000),
('CTDDP18', 'DDP18', 'P120', 2, 2000000),
('CTDDP19', 'DDP19', 'P101', 2,  800000),
('CTDDP20', 'DDP20', 'P102', 2,  500000),
('CTDDP21', 'DDP21', 'P103', 2,  500000),
('CTDDP22', 'DDP22', 'P104', 2,  650000),
('CTDDP23', 'DDP23', 'P105', 2,  700000),
('CTDDP24', 'DDP24', 'P106', 2,  800000),
('CTDDP25', 'DDP25', 'P108', 2,  900000),
('CTDDP26', 'DDP26', 'P109', 2, 1500000),
('CTDDP27', 'DDP27', 'P110', 2, 1550000),
('CTDDP28', 'DDP28', 'P111', 3,  520000),
('CTDDP29', 'DDP29', 'P112', 3,  680000),
('CTDDP30', 'DDP30', 'P113', 3,  820000);

-- =========================
-- DU LIEU BANG HOA DON
-- =========================
INSERT INTO HoaDon(maHD, maDDP, maThue, ngayLapHD, tongTien) VALUES
('HD01', 'DDP01', 'T03', '2025-03-23', 1760000),
('HD02', 'DDP02', 'T03', '2025-03-24', 1100000),
('HD03', 'DDP03', 'T03', '2025-03-25', 1100000),
('HD04', 'DDP04', 'T03', '2025-03-26', 1430000),
('HD05', 'DDP05', 'T03', '2025-03-22', 1540000),
('HD06', 'DDP06', 'T03', '2025-03-27', 1760000),
('HD07', 'DDP07', 'T03', '2025-03-28', 1980000),
('HD08', 'DDP08', 'T03', '2025-03-29', 3300000),
('HD09', 'DDP09', 'T03', '2025-03-20', 3410000),
('HD10', 'DDP10', 'T03', '2025-03-30', 1144000),
('HD11', 'DDP11', 'T03', '2025-03-23', 1496000),
('HD12', 'DDP12', 'T03', '2025-03-19', 1804000),
('HD13', 'DDP13', 'T03', '2025-03-31', 1936000),
('HD14', 'DDP14', 'T03', '2025-03-21', 3520000),
('HD15', 'DDP15', 'T03', '2025-04-01', 1188000),
('HD16', 'DDP16', 'T03', '2025-03-17', 1914000),
('HD17', 'DDP17', 'T03', '2025-03-26', 3740000),
('HD18', 'DDP18', 'T03', '2025-03-27', 4400000),
('HD19', 'DDP19', 'T03', '2025-03-28', 1760000),
('HD20', 'DDP20', 'T03', '2025-03-16', 1100000),
('HD21', 'DDP21', 'T03', '2025-03-29', 1100000),
('HD22', 'DDP22', 'T03', '2025-03-30', 1430000),
('HD23', 'DDP23', 'T03', '2025-03-14', 1540000),
('HD24', 'DDP24', 'T03', '2025-03-31', 1760000),
('HD25', 'DDP25', 'T03', '2025-03-24', 1980000),
('HD26', 'DDP26', 'T03', '2025-03-25', 3300000),
('HD27', 'DDP27', 'T03', '2025-03-12', 3410000),
('HD28', 'DDP28', 'T03', '2025-04-02', 1716000),
('HD29', 'DDP29', 'T03', '2025-03-27', 2244000),
('HD30', 'DDP30', 'T03', '2025-04-03', 2706000);

-- =========================
-- DU LIEU BANG CT HOA DON
-- =========================
INSERT INTO CTHoaDon(maCTHD, maHD, maKM, chiTiet, donGia) VALUES
('CTHD01', 'HD01', 'KM01', N'Tiền phòng + thuế', 1760000),
('CTHD02', 'HD02', 'KM02', N'Tiền phòng + thuế', 1100000),
('CTHD03', 'HD03', 'KM03', N'Tiền phòng + thuế', 1100000),
('CTHD04', 'HD04', 'KM04', N'Tiền phòng + thuế', 1430000),
('CTHD05', 'HD05', 'KM05', N'Tiền phòng + thuế', 1540000),
('CTHD06', 'HD06', 'KM06', N'Tiền phòng + thuế', 1760000),
('CTHD07', 'HD07', 'KM07', N'Tiền phòng + thuế', 1980000),
('CTHD08', 'HD08', 'KM08', N'Tiền phòng + thuế', 3300000),
('CTHD09', 'HD09', 'KM01', N'Tiền phòng + thuế', 3410000),
('CTHD10', 'HD10', 'KM02', N'Tiền phòng + thuế', 1144000),
('CTHD11', 'HD11', 'KM03', N'Tiền phòng + thuế', 1496000),
('CTHD12', 'HD12', 'KM04', N'Tiền phòng + thuế', 1804000),
('CTHD13', 'HD13', 'KM05', N'Tiền phòng + thuế', 1936000),
('CTHD14', 'HD14', 'KM06', N'Tiền phòng + thuế', 3520000),
('CTHD15', 'HD15', 'KM07', N'Tiền phòng + thuế', 1188000),
('CTHD16', 'HD16', 'KM08', N'Tiền phòng + thuế', 1914000),
('CTHD17', 'HD17', 'KM01', N'Tiền phòng + thuế', 3740000),
('CTHD18', 'HD18', 'KM02', N'Tiền phòng + thuế', 4400000),
('CTHD19', 'HD19', 'KM03', N'Tiền phòng + thuế', 1760000),
('CTHD20', 'HD20', 'KM04', N'Tiền phòng + thuế', 1100000),
('CTHD21', 'HD21', 'KM05', N'Tiền phòng + thuế', 1100000),
('CTHD22', 'HD22', 'KM06', N'Tiền phòng + thuế', 1430000),
('CTHD23', 'HD23', 'KM07', N'Tiền phòng + thuế', 1540000),
('CTHD24', 'HD24', 'KM08', N'Tiền phòng + thuế', 1760000),
('CTHD25', 'HD25', 'KM01', N'Tiền phòng + thuế', 1980000),
('CTHD26', 'HD26', 'KM02', N'Tiền phòng + thuế', 3300000),
('CTHD27', 'HD27', 'KM03', N'Tiền phòng + thuế', 3410000),
('CTHD28', 'HD28', 'KM04', N'Tiền phòng + thuế', 1716000),
('CTHD29', 'HD29', 'KM05', N'Tiền phòng + thuế', 2244000),
('CTHD30', 'HD30', 'KM06', N'Tiền phòng + thuế', 2706000);

-- =========================
-- DU LIEU BANG CT DICH VU
-- =========================
INSERT INTO CTDichVu(maCTDV, maPhong, maDV, soLuong, donGia) VALUES
('CTDV01', 'P101', 'DV01', 2, 10000),
('CTDV02', 'P102', 'DV03', 2, 20000),
('CTDV03', 'P103', 'DV04', 1, 15000),
('CTDV04', 'P104', 'DV05', 1, 50000),
('CTDV05', 'P105', 'DV06', 2, 80000),
('CTDV06', 'P106', 'DV01', 3, 10000),
('CTDV07', 'P108', 'DV02', 2, 30000),
('CTDV08', 'P109', 'DV09', 1, 300000),
('CTDV09', 'P110', 'DV10', 1, 250000),
('CTDV10', 'P111', 'DV06', 2, 80000),
('CTDV11', 'P112', 'DV05', 1, 50000),
('CTDV12', 'P113', 'DV01', 4, 10000),
('CTDV13', 'P114', 'DV03', 2, 20000),
('CTDV14', 'P115', 'DV09', 1, 300000),
('CTDV15', 'P116', 'DV08', 1, 70000),
('CTDV16', 'P118', 'DV07', 1, 150000),
('CTDV17', 'P119', 'DV10', 1, 250000),
('CTDV18', 'P120', 'DV06', 4, 80000),
('CTDV19', 'P101', 'DV05', 1, 50000),
('CTDV20', 'P102', 'DV01', 2, 10000);
GO

-- =========================
-- TEST NHANH
-- =========================
SELECT COUNT(*) AS soKhachHang FROM KhachHang;
SELECT COUNT(*) AS soNhanVien FROM NhanVien;
SELECT COUNT(*) AS soPhong FROM Phong;
SELECT COUNT(*) AS soDonDatPhong FROM DonDatPhong;
SELECT COUNT(*) AS soHoaDon FROM HoaDon;
GO