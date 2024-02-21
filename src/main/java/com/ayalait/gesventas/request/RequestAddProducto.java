package com.ayalait.gesventas.request;

import java.util.List;

import com.ayalait.modelo.*;

 public class RequestAddProducto {
	
	 private Producto producto;
	 private String accion;
	 private List<ProductoImagenes> imagenes;
	 private ProductoDetalles detalle;

	public String getAccion() {
		return accion;
	}

	public void setAccion(String accion) {
		this.accion = accion;
	}

	


	public Producto getProducto() {
	        return producto;
	    }

	    public void setProducto(Producto producto) {
	        this.producto = producto;
	    }

		public ProductoDetalles getDetalle() {
			return detalle;
		}

		public void setDetalle(ProductoDetalles detalle) {
			this.detalle = detalle;
		}

		public List<ProductoImagenes> getImagenes() {
			return imagenes;
		}

		public void setImagenes(List<ProductoImagenes> imagenes) {
			this.imagenes = imagenes;
		}
	   

}
