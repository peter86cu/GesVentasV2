package com.ayalait.gesventas.utils;

public class ItemsOrdenCompra {
   private int id_detalle;
   private int catidad;
   private double importe;
   private String codigo;
   private String nombre;
   private String simboloUM;
   private double total;

   public int getId_detalle() {
      return this.id_detalle;
   }

   public void setId_detalle(int id_detalle) {
      this.id_detalle = id_detalle;
   }

   public int getCatidad() {
      return this.catidad;
   }

   public void setCatidad(int catidad) {
      this.catidad = catidad;
   }

   public double getImporte() {
      return this.importe;
   }

   public void setImporte(double importe) {
      this.importe = importe;
   }

   public String getCodigo() {
      return this.codigo;
   }

   public void setCodigo(String codigo) {
      this.codigo = codigo;
   }

   public String getNombre() {
      return this.nombre;
   }

   public void setNombre(String nombre) {
      this.nombre = nombre;
   }

   public double getTotal() {
      return this.total;
   }

   public void setTotal(double total) {
      this.total = total;
   }

public String getSimboloUM() {
	return simboloUM;
}

public void setSimboloUM(String simboloUM) {
	this.simboloUM = simboloUM;
}
}