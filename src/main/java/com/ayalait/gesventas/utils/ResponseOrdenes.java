package com.ayalait.gesventas.utils;

public class ResponseOrdenes {
	private String id_orden_compra;
	private String fecha;
	private String plazo;
	private String forma_pago;
	private String items;
	private String estado;
	private String id_estado;
	private String nombre;
	private String proveedor;

	private int id_proveedor;

	public String getId_orden_compra() {
		return id_orden_compra;
	}
	public void setId_orden_compra(String id_orden_compra) {
		this.id_orden_compra = id_orden_compra;
	}
	public String getFecha() {
		return fecha;
	}
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
	public String getPlazo() {
		return plazo;
	}
	public void setPlazo(String plazo) {
		this.plazo = plazo;
	}
	public String getForma_pago() {
		return forma_pago;
	}
	public void setForma_pago(String forma_pago) {
		this.forma_pago = forma_pago;
	}
	public String getItems() {
		return items;
	}
	public void setItems(String items) {
		this.items = items;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	public String getId_estado() {
		return id_estado;
	}
	public void setId_estado(String id_estado) {
		this.id_estado = id_estado;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getProveedor() {
		return proveedor;
	}
	public void setProveedor(String proveedor) {
		this.proveedor = proveedor;
	}

	public int getId_proveedor() {
		return id_proveedor;
	}

	public void setId_proveedor(int id_proveedor) {
		this.id_proveedor = id_proveedor;
	}
}
