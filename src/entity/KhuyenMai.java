package entity;

import java.time.LocalDate;

public class KhuyenMai {
	private String maKM;
	private String tenKM;
	private double giaTri;
	private LocalDate ngayBatDau;
	private LocalDate ngayKetThuc;
	
	
	public KhuyenMai() {
		super();
	}

	public KhuyenMai(String maKM, String tenKM, double giaTri, LocalDate ngayBatDau, LocalDate ngayKetThuc) {
		super();
		this.maKM = maKM;
		this.tenKM = tenKM;
		this.giaTri = giaTri;
		this.ngayBatDau = ngayBatDau;
		this.ngayKetThuc = ngayKetThuc;
	}

	public String getMaKM() {
		return maKM;
	}

	public void setMaKM(String maKM) {
		this.maKM = maKM;
	}

	public String getTenKM() {
		return tenKM;
	}

	public void setTenKM(String tenKM) {
		this.tenKM = tenKM;
	}

	public double getGiaTri() {
		return giaTri;
	}

	public void setGiaTri(double giaTri) {
		this.giaTri = giaTri;
	}

	public LocalDate getNgayBatDau() {
		return ngayBatDau;
	}

	public void setNgayBatDau(LocalDate ngayBatDau) {
		this.ngayBatDau = ngayBatDau;
	}

	public LocalDate getNgayKetThuc() {
		return ngayKetThuc;
	}

	public void setNgayKetThuc(LocalDate ngayKetThuc) {
		this.ngayKetThuc = ngayKetThuc;
	}
	
	
}
