package com.ayalait.gesventas.utils;

import java.util.ArrayList;
import java.util.List;

import com.ayalait.modelo.HistoricoCambio;

 public class Historico {
	
	List<HistoricoCambio> historico = new ArrayList<HistoricoCambio>();

	public List<HistoricoCambio> getHistorico() {
		return historico;
	}

	public void setHistorico(List<HistoricoCambio> historico) {
		this.historico = historico;
	}
	

}
