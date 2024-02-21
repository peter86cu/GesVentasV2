package com.ayalait.gesventas.utils;

import com.ayalait.modelo.*;

public class MarcaEmpleadoGestion {
	
	 private int code;
	 private MarcasEmpleado[] marcas;
	 private HorarioLaboral[] horario;
	 private String resultado;
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public MarcasEmpleado[] getMarcas() {
		return marcas;
	}
	public void setMarcas(MarcasEmpleado[] marcas) {
		this.marcas = marcas;
	}
	public HorarioLaboral[] getHorario() {
		return horario;
	}
	public void setHorario(HorarioLaboral[] horario) {
		this.horario = horario;
	}
	public String getResultado() {
		return resultado;
	}
	public void setResultado(String resultado) {
		this.resultado = resultado;
	}
	 
	 

}
