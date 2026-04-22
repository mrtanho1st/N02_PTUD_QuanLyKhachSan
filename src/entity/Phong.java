package entity;

public class Phong {
    private String maPhong;
    private String loaiPhong;
    private int soNguoiToiDa;
    private double giaPhong;
    private String trangThai;
    
    
	public Phong(String maPhong) {
		super();
		this.maPhong = maPhong;
	}


	public Phong(String maPhong, String loaiPhong, int soNguoiToiDa, double giaPhong, String trangThai) {
		super();
		this.maPhong = maPhong;
		this.loaiPhong = loaiPhong;
		this.soNguoiToiDa = soNguoiToiDa;
		this.giaPhong = giaPhong;
		this.trangThai = trangThai;
	}


	public String getMaPhong() {
		return maPhong;
	}


	public void setMaPhong(String maPhong) {
		this.maPhong = maPhong;
	}


	public String getLoaiPhong() {
		return loaiPhong;
	}


	public void setLoaiPhong(String loaiPhong) {
		this.loaiPhong = loaiPhong;
	}


	public int getSoNguoiToiDa() {
		return soNguoiToiDa;
	}


	public void setSoNguoiToiDa(int soNguoiToiDa) {
		this.soNguoiToiDa = soNguoiToiDa;
	}


	public double getGiaPhong() {
		return giaPhong;
	}


	public void setGiaPhong(double giaPhong) {
		this.giaPhong = giaPhong;
	}


	public String getTrangThai() {
		return trangThai;
	}


	public void setTrangThai(String trangThai) {
		this.trangThai = trangThai;
	}
	
	
    
    
}
