package com.ayalait.gesventas.utils;

public enum FormatoFecha {
	
	YYYYMMDDH24(1,"yyyy-MM-dd HH:mm:ss"),
	YYYYMMDD(2,"yyyy-MM-dd"),
	H24(3,"HH:mm:ss");
	
	private int idFormato;
	private String formato;
	
	
	private FormatoFecha(int idFormato, String formato) {
		this.idFormato = idFormato;
		this.formato = formato;
	}

	public int getIdFormato() {
		return idFormato;
	}

	public void setIdFormato(int idFormato) {
		this.idFormato = idFormato;
	}

	public String getFormato() {
		return formato;
	}

	public void setFormato(String formato) {
		this.formato = formato;
	}

	
	

}
