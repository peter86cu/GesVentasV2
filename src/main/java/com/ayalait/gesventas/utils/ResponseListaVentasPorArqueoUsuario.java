package com.ayalait.gesventas.utils;

import java.util.List;

public class ResponseListaVentasPorArqueoUsuario {
	
	private boolean status;
    private int code;
    private List<VentasPorArqueoUsuario> lstVentas;
    private String resultado;

   

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }	

	public List<VentasPorArqueoUsuario> getLstVentas() {
		return lstVentas;
	}

	public void setLstVentas(List<VentasPorArqueoUsuario> lstVentas) {
		this.lstVentas = lstVentas;
	}

	public String getResultado() {
		return resultado;
	}

	public void setResultado(String resultado) {
		this.resultado = resultado;
	}

	


}
