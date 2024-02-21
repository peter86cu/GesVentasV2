package com.ayalait.gesventas.request;

import java.io.Serializable;

import com.ayalait.modelo.FacturaCompra;

 
public class RequestFacturaCompra implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private FacturaCompra facturaCompra;

	public FacturaCompra getFacturaCompra() {
		return facturaCompra;
	}

	public void setFacturaCompra(FacturaCompra facturaCompra) {
		this.facturaCompra = facturaCompra;
	}

	

	
}
