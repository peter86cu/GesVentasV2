package com.ayalait.gesventas.utils;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CambioTemp {
	@JsonProperty
	private String idmoneda;
	@JsonProperty
	private String value;
	public String getIdmoneda() {
		return idmoneda;
	}
	public void setIdmoneda(String idmoneda) {
		this.idmoneda = idmoneda;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	
	

}
