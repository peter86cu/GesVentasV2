package com.ayalait.gesventas.request;

import java.io.Serializable;

import com.ayalait.modelo.AdministracionLog;

 
public class RequestLogAdmin implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private AdministracionLog adminLog;

	public AdministracionLog getAdminLog() {
		return adminLog;
	}

	public void setAdminLog(AdministracionLog adminLog) {
		this.adminLog = adminLog;
	}

	

	
}
