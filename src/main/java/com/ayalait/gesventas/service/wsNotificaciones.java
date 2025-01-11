package com.ayalait.gesventas.service;

import com.ayalait.gesventas.controller.LoginController;
import com.ayalait.gesventas.request.RequestAddProducto;
import com.ayalait.modelo.*;
import com.ayalait.notificaciones.Notificaciones;
import com.ayalait.notificaciones.ResponseNotificacion;
import com.ayalait.response.*;
import com.ayalait.utils.DiaAbierto;
import com.ayalait.utils.ErrorState;
import com.ayalait.utils.MessageCodeImpl;
import com.ayalait.web.ResponseVisitantes;
import com.ayalait.web.VisitantesLog;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Properties;
 import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


public final class wsNotificaciones {

	private String hostNotificacion;
	

	ObjectWriter ow = (new ObjectMapper()).writer().withDefaultPrettyPrinter();

	@Autowired
	RestTemplate restTemplate = new RestTemplate();

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

				hostNotificacion = p.getProperty("server.dashboard");
			}
		} catch (FileNotFoundException ex) {
			Logger.getLogger(wsNotificaciones.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	public wsNotificaciones() {
		try {
			if(LoginController.desarrollo){
				hostNotificacion = "http://localhost:7002";
			}else{
				cargarServer();
			}

		} catch (IOException ex) {
			Logger.getLogger(wsNotificaciones.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	 public ResponseResultado validarConectividadServidor() {
		 
		ResponseResultado responseResult = new ResponseResultado();
		
		try {

			String url = this.hostNotificacion + "/server";
			 
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
		}catch (org.springframework.web.client.ResourceAccessException e) {
			responseResult.setStatus(false);
			responseResult.setResultado("Módulo en mantenimiento.");
		}
		 

		return responseResult;

		 

	}

	
	 public ResponseResultado addNotidicacion(Notificaciones request) {
		 
			ResponseResultado responseResult = new ResponseResultado();
			try {

				String url = this.hostNotificacion + "/notificaciones/add";
				HttpHeaders headers = new HttpHeaders();
	 			HttpEntity<Notificaciones> requestEntity = new HttpEntity<>(request, headers);
				URI uri = new URI(url);
				ResponseEntity<Void> response = restTemplate.exchange(uri , HttpMethod.POST, requestEntity,Void.class);

				if (response.getStatusCodeValue() == 200) {
				    responseResult.setCode(200);
					responseResult.setStatus(true);
					responseResult.setResultado("Notificación guardada.");
	 
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
			}catch (org.springframework.web.client.HttpClientErrorException e) {
				JsonParser jsonParser = new JsonParser();
				responseResult.setStatus(false);
				int in = e.getLocalizedMessage().indexOf("{");
				int in2 = e.getLocalizedMessage().indexOf("}");
				String cadena = e.getMessage().substring(in, in2+1);
				JsonObject myJson = (JsonObject) jsonParser.parse(cadena);
				responseResult.setCode(myJson.get("code").getAsInt());
				ErrorState data = new ErrorState();
				data.setCode(myJson.get("code").getAsInt());
				data.setMenssage(myJson.get("menssage").getAsString());			
				responseResult.setError(data);
			}
			 

			return responseResult;
			 
		}
	
	

	/*public ResponseNotificacion obtenerNotificaciones(String idUsuario) {
 

		ResponseNotificacion responseResult = new ResponseNotificacion();
		try {

			String url = this.hostNotificacion + "/dashboard/visitantes";
			
			URI uri = new URI(url);
			ResponseEntity<List<VisitantesLog>> response = restTemplate.exchange(uri , HttpMethod.GET, null,
					new ParameterizedTypeReference<List<VisitantesLog>>() {
					});
			
			if (response.getStatusCodeValue() == 200) {
				responseResult.setCode(response.getStatusCodeValue());
				responseResult.setStatus(true);
				responseResult.setVisitantes(response.getBody());
 
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
		}catch (org.springframework.web.client.HttpClientErrorException e) {
			JsonParser jsonParser = new JsonParser();
			responseResult.setStatus(false);
			int in = e.getLocalizedMessage().indexOf("{");
			int in2 = e.getLocalizedMessage().indexOf("}");
			String cadena = e.getMessage().substring(in, in2+1);
			JsonObject myJson = (JsonObject) jsonParser.parse(cadena);
			responseResult.setCode(myJson.get("code").getAsInt());
			ErrorState data = new ErrorState();
			data.setCode(myJson.get("code").getAsInt());
			data.setMenssage(myJson.get("menssage").getAsString());			
			responseResult.setError(data);
		}
		 
		return responseResult;
		 
	}*/
	
	
	

}
