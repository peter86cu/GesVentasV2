package com.ayalait.gesventas.utils;

public class VentasPorArqueoUsuario {
	
	private int id_apertura_cajero;
	private String fecha_inicio;
	private int id_venta_cobro;
	private int cobro;
	private String tipoCobro;
	public int getId_apertura_cajero() {
		return id_apertura_cajero;
	}
	public void setId_apertura_cajero(int id_apertura_cajero) {
		this.id_apertura_cajero = id_apertura_cajero;
	}
	public String getFecha_inicio() {
		return fecha_inicio;
	}
	public void setFecha_inicio(String fecha_inicio) {
		this.fecha_inicio = fecha_inicio;
	}
	public int getId_venta_cobro() {
		return id_venta_cobro;
	}
	public void setId_venta_cobro(int id_venta_cobro) {
		this.id_venta_cobro = id_venta_cobro;
	}
	public int getCobro() {
		return cobro;
	}
	public void setCobro(int cobro) {
		this.cobro = cobro;
	}
	public String getTipoCobro() {
		return tipoCobro;
	}
	public void setTipoCobro(String tipoCobro) {
		this.tipoCobro = tipoCobro;
	}
	

}
