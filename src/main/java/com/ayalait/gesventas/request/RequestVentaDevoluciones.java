package com.ayalait.gesventas.request;

import java.io.Serializable;
import java.util.List;

import com.ayalait.modelo.*;

 

public class RequestVentaDevoluciones implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private VentasDevoluciones devolucion;
    private List<VentasProductosDevolucion> lstProductos;
	public VentasDevoluciones getDevolucion() {
		return devolucion;
	}
	public void setDevolucion(VentasDevoluciones devolucion) {
		this.devolucion = devolucion;
	}
	public List<VentasProductosDevolucion> getLstProductos() {
		return lstProductos;
	}
	public void setLstProductos(List<VentasProductosDevolucion> lstProductos) {
		this.lstProductos = lstProductos;
	}
    
    
	

	

	

}
