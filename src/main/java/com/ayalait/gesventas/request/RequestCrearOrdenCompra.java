package com.ayalait.gesventas.request;

import java.io.Serializable;

import com.ayalait.modelo.OrdenCompra;

  

public class RequestCrearOrdenCompra implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private OrdenCompra ordenCompra;

	public OrdenCompra getOrdenCompra() {
		return ordenCompra;
	}

	public void setOrdenCompra(OrdenCompra ordenCompra) {
		this.ordenCompra = ordenCompra;
	}

	

	
}
