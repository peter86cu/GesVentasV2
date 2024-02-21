package com.ayalait.gesventas.utils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class HiloLector extends Thread{
private final InputStream is;
	
	public HiloLector(InputStream is) {
		this.is = is;
	}
	
	@Override
	public void run() {
		
		List<String> data= new ArrayList<String>();
		try {
			byte[] buffer = new byte[1000];
			int leido = is.read(buffer);
			while (leido > 0) {
				String texto = new String(buffer, 0, leido);
				System.out.print(texto);
				data.add(texto);
				leido = is.read(buffer);
			}
			EjecutarProceso.response.setResultado("Salva realida con exito.");
			EjecutarProceso.response.setCode(200);
			EjecutarProceso.response.setLog(data);
			EjecutarProceso.response.setStatus(true);
			} catch (Exception e) {
			System.err.println(e.toString());
			EjecutarProceso.response.setResultado("Error: "+e.getCause().getCause().getMessage());
			EjecutarProceso.response.setLog(data);
			EjecutarProceso.response.setCode(400);
			EjecutarProceso.response.setStatus(false);

		}
	}
}
