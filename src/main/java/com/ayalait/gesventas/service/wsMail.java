package com.ayalait.gesventas.service;

import com.ayalait.gesventas.controller.LoginController;
import com.ayalait.response.CorreosDowload;
import com.ayalait.response.ResponseDowloadMail;
import com.ayalait.response.ResponseResultado;
import com.ayalait.utils.ErrorState;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public final class wsMail {

	private String hostMail;
	private String hostTerminal;
	
	@Autowired
	RestTemplate restTemplate;

	void cargarServer() throws IOException {
		Properties p = new Properties();

		try {
			URL url = this.getClass().getClassLoader().getResource("application.properties");
			if (url == null) {
				throw new IllegalArgumentException("application.properties" + " is not found 1");
			} else {
				InputStream propertiesStream = url.openStream();
				//InputStream propertiesStream = ClassLoader.getSystemResourceAsStream("application.properties");
				p.load(propertiesStream);
				propertiesStream.close();
				this.hostMail = p.getProperty("server.stock");
				this.hostTerminal = p.getProperty("server.terminal");
			}
		} catch (FileNotFoundException var3) {
			Logger.getLogger(wsMail.class.getName()).log(Level.SEVERE, (String) null, var3);
		}

	}

	public wsMail() {
		try {
			if(LoginController.desarrollo){
				hostMail = "http://localhost:8088";
				hostTerminal= "http://localhost:8087";
			}else{
				cargarServer();
			}
		} catch (IOException var2) {
			Logger.getLogger(wsMail.class.getName()).log(Level.SEVERE, (String) null, var2);
		}

	}

 	public ResponseResultado validarConectividadServidor() {
		 
		ResponseResultado responseResult = new ResponseResultado();
		
		try {

			String url =this.hostMail + "/server";
			 
			URI uri = new URI(url);
			ResponseEntity<String> response = restTemplate.exchange(uri , HttpMethod.GET, null,String.class);

			if (response.getStatusCodeValue() == 200) {

				responseResult.setStatus(true);
				responseResult.setResultado(response.getBody());
 
			}

		} catch (org.springframework.web.client.HttpServerErrorException e) {
			ErrorState data = new ErrorState();
			data.setCode(e.getStatusCode().value());
			data.setMenssage(e.getMessage());
			responseResult.setCode(data.getCode());
			responseResult.setError(data);
			 
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 

		return responseResult; 

	}
	
	

	public ResponseDowloadMail descargarMail(String usuario, String password) {
		 
		ResponseDowloadMail responseResult = new ResponseDowloadMail();
		
		try {

			String url = this.hostMail + "/inbox/consultar?usuario=" + usuario + "&password=" + password;
			 
			URI uri = new URI(url);
			ResponseEntity<CorreosDowload> response = restTemplate.exchange(uri , HttpMethod.POST, null,CorreosDowload.class);

			if (response.getStatusCodeValue() == 200) {

				responseResult.setStatus(true);
				responseResult.setLstMail(response.getBody());
 
			}

		} catch (org.springframework.web.client.HttpServerErrorException e) {
			ErrorState data = new ErrorState();
			data.setCode(e.getStatusCode().value());
			data.setMenssage(e.getMessage());
			responseResult.setCode(data.getCode());
			responseResult.setError(data);
			 
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 

		return responseResult; 

		

	}
 

}