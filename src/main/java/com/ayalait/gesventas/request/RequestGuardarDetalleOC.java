package com.ayalait.gesventas.request;

import java.io.Serializable;

import com.ayalait.modelo.DetalleOrdenCompra;

 
public class RequestGuardarDetalleOC implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private DetalleOrdenCompra detalleOrdenCompra;

	public DetalleOrdenCompra getDetalleOrdenCompra() {
		return detalleOrdenCompra;
	}

	public void setDetalleOrdenCompra(DetalleOrdenCompra detalleOrdenCompra) {
		this.detalleOrdenCompra = detalleOrdenCompra;
	}

	

	
}
