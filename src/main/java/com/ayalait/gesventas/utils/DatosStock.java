package com.ayalait.gesventas.utils;

public class DatosStock { 
	
	private String id_producto;
	private int cantidad;
	public String getId_producto() {
		return id_producto;
	}
	public void setId_producto(String id_producto) {
		this.id_producto = id_producto;
	}
	public int getCantidad() {
		return cantidad;
	}
	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}
	public DatosStock(String id_producto, int cantidad) {
		super();
		this.id_producto = id_producto;
		this.cantidad = cantidad;
	}
	

}
