package com.ayalait.gesventas.request;

import java.io.Serializable;

import com.ayalait.modelo.DetalleFacturaCompra;

 
public class RequestGuardarDetalleFC implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private DetalleFacturaCompra detalleFacturaCompra;

	public DetalleFacturaCompra getDetalleFacturaCompra() {
		return detalleFacturaCompra;
	}

	public void setDetalleFacturaCompra(DetalleFacturaCompra detalleFacturaCompra) {
		this.detalleFacturaCompra = detalleFacturaCompra;
	}

	

	
	

	
}
