package entity;

public class KhachHang {
    private String maKH;
    private String hoTen;
    private String cccd;
    private String sdt;
    private String loaiKH;
    private int diem;
    
    
	public KhachHang() {
		super();
	}
	public KhachHang(String maKH, String hoTen, String cccd, String sdt, String loaiKH, int diem) {
		super();
		this.maKH = maKH;
		this.hoTen = hoTen;
		this.cccd = cccd;
		this.sdt = sdt;
		this.loaiKH = loaiKH;
		this.diem = diem;
	}
	public String getMaKH() {
		return maKH;
	}
	public void setMaKH(String maKH) {
		this.maKH = maKH;
	}
	public String getHoTen() {
		return hoTen;
	}
	public void setHoTen(String hoTen) {
		this.hoTen = hoTen;
	}
	public String getCccd() {
		return cccd;
	}
	public void setCccd(String cccd) {
		this.cccd = cccd;
	}
	public String getSdt() {
		return sdt;
	}
	public void setSdt(String sdt) {
		this.sdt = sdt;
	}
	public String getLoaiKH() {
		return loaiKH;
	}
	public void setLoaiKH(String loaiKH) {
		this.loaiKH = loaiKH;
	}
	public int getDiem() {
		return diem;
	}
	public void setDiem(int diem) {
		this.diem = diem;
	}
    
    
    
	
	
	

    
}
