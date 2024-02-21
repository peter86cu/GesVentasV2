package com.ayalait.gesventas.service;

import com.ayalait.gesventas.controller.LoginController;
 

import com.ayalait.gesventas.request.RequestAddRol;
import com.ayalait.modelo.*;
import com.ayalait.response.*;
import com.ayalait.utils.ErrorState;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

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



public final class wsRoles {
	private String hostSeguridad;
	ObjectWriter ow = (new ObjectMapper()).writer().withDefaultPrettyPrinter();

	@Autowired
	RestTemplate restTemplate= new RestTemplate();
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
				this.hostSeguridad = p.getProperty("server.seguridad");
			}
		} catch (FileNotFoundException var3) {
			Logger.getLogger(Session.class.getName()).log(Level.SEVERE, (String) null, var3);
		}

	}

	public wsRoles() {
		try {
			if(LoginController.desarrollo){
				hostSeguridad = "http://localhost:7000";
			}else{
				cargarServer();
			}
		} catch (IOException var2) {
			Logger.getLogger(Session.class.getName()).log(Level.SEVERE, (String) null, var2);
		}

	}

 	/*public ResponseGestionesModulos consultarGestionesPorModulo(String token, int idmodulo) {
		 
		ResponseGestionesModulos responseResult = new ResponseGestionesModulos();
		try {

			String url = this.hostSeguridad + "/usuario/add";
			HttpHeaders headers = new HttpHeaders();

			headers.set("Authorization", "Bearer "+token);
			HttpEntity<Void> requestEntity = new HttpEntity<>(null, headers);
			URI uri = new URI(url);
			ResponseEntity<String> response = restTemplate.exchange(uri , HttpMethod.POST, requestEntity,String.class);

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

		try {
			WebTarget webTarjet = cliente.target(this.hostSeguridad + "/roles/lista/gestiones?id=" + idmodulo);
			Builder invoker = webTarjet.request(new String[] { "application/json" }).header("Authorization",
					"Bearer " + token);
			response = invoker.get();

			if (response.getStatus() == 200) {
				responseJson = (String) response.readEntity(String.class);
				responseResult.setStatus(true);
				responseResult.setCode(response.getStatus());
				Gestiones[] data = (Gestiones[]) (new Gson()).fromJson(responseJson, Gestiones[].class);
				responseResult.setGestiones(data);
				return responseResult;
			}

			responseResult.setStatus(false);
			responseResult.setCode(response.getStatus());
			responseResult.setResultado((String) response.readEntity(String.class));
			return responseResult;

		} catch (JsonSyntaxException var16) {
			responseResult.setCode(406);
			responseResult.setStatus(false);
			responseResult.setResultado(var16.getMessage());
			return responseResult;
		} catch (ProcessingException var17) {
			responseResult.setCode(500);
			responseResult.setStatus(false);
			responseResult.setResultado(var17.getCause().getMessage());
			return responseResult;
		} finally {
			if (response != null) {
				response.close();
			}

			if (cliente != null) {
				cliente.close();
			}

		}

	}*/

	public ResponseListaRoles consultarListaRoles(String token) {
		 
		ResponseListaRoles responseResult = new ResponseListaRoles();
		try {

			String url = this.hostSeguridad + "/roles/lista/roles";
			HttpHeaders headers = new HttpHeaders();

			headers.set("Authorization", "Bearer "+token);
			HttpEntity<Void> requestEntity = new HttpEntity<>(null, headers);
			URI uri = new URI(url);
			ResponseEntity<List<Roles>> response = restTemplate.exchange(uri , HttpMethod.GET, requestEntity,
					new ParameterizedTypeReference<List<Roles>>() {
					});

			if (response.getStatusCodeValue() == 200) {

				responseResult.setStatus(true);
				responseResult.setRoles(response.getBody());
 
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

	public ResponseBuscarRol buscarRolPorId(String token, int id) {
		 
		ResponseBuscarRol responseResult = new ResponseBuscarRol();
		try {

			String url = this.hostSeguridad + "/roles/buscar/id?id=" + id;
			HttpHeaders headers = new HttpHeaders();

			headers.set("Authorization", "Bearer "+token);
			HttpEntity<Void> requestEntity = new HttpEntity<>(null, headers);
			URI uri = new URI(url);
			ResponseEntity<Roles> response = restTemplate.exchange(uri , HttpMethod.GET, requestEntity,Roles.class);

			if (response.getStatusCodeValue() == 200) {

				responseResult.setStatus(true);
				responseResult.setRoles(response.getBody());
 
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

	public ResponseResultado addRol(RequestAddRol request, String token) {
		 
		ResponseResultado responseResult = new ResponseResultado();
		
		try {

			String url = this.hostSeguridad + "/usuario/add";
			HttpHeaders headers = new HttpHeaders();

			headers.set("Authorization", "Bearer "+token);
			HttpEntity<Void> requestEntity = new HttpEntity<>(null, headers);
			URI uri = new URI(url);
			ResponseEntity<String> response = restTemplate.exchange(uri , HttpMethod.POST, requestEntity,String.class);

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
}