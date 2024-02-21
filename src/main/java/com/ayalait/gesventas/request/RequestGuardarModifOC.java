package com.ayalait.gesventas.request;

import java.io.Serializable;

import com.ayalait.modelo.OrdenCompraModificaciones;

 
public class RequestGuardarModifOC implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private OrdenCompraModificaciones ordenCompraModif;

	public OrdenCompraModificaciones getOrdenCompraModif() {
		return ordenCompraModif;
	}

	public void setOrdenCompraModif(OrdenCompraModificaciones ordenCompraModif) {
		this.ordenCompraModif = ordenCompraModif;
	}

	

	

	
}
