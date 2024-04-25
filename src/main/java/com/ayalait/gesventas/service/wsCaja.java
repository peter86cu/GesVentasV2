package com.ayalait.gesventas.service;

import com.ayalait.gesventas.controller.LoginController;
 import com.ayalait.modelo.*;
 import com.ayalait.response.*;
import com.ayalait.utils.DiaAbierto;
import com.ayalait.utils.ErrorState;
import com.ayalait.utils.MessageCodeImpl;
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


public final class wsCaja {

	private String hostTerminal;
	

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

				hostTerminal = p.getProperty("server.terminal");
			}
		} catch (FileNotFoundException ex) {
			Logger.getLogger(wsCaja.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	public wsCaja() {
		try {
			if(LoginController.desarrollo){
				hostTerminal = "http://localhost:8087";
			}else{
				cargarServer();
			}

		} catch (IOException ex) {
			Logger.getLogger(wsCaja.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	 public ResponseResultado validarConectividadServidor() {
		 
		ResponseResultado responseResult = new ResponseResultado();
		
		try {

			String url = this.hostTerminal + "/server";
			 
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

	public ResponseResultado abrirCaja(AperturaCaja request, String token) {

		 
		ResponseResultado responseResult = new ResponseResultado();
		try {

			String url = this.hostTerminal + "/caja/abrir";
			HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", "Bearer "+token);
 			HttpEntity<AperturaCaja> requestEntity = new HttpEntity<>(request, headers); 
			URI uri = new URI(url);
			ResponseEntity<String> response = restTemplate.exchange(uri , HttpMethod.POST, requestEntity,String.class);

			if (response.getStatusCodeValue() == 200) {
				responseResult.setCode(response.getStatusCodeValue());
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
		}catch (org.springframework.web.client.HttpClientErrorException e) {
			ErrorState data = new ErrorState();
			data.setCode(e.getStatusCode().value());
			data.setMenssage(MessageCodeImpl.getMensajeServiceTerminal(String.valueOf(e.getStatusCode().value() ) ));
			responseResult.setCode(data.getCode());
			responseResult.setError(data);
		}
		 

		return responseResult;	
		 
	}
	
	
	public ResponseListadoAperturaMensual obtenerAperurasMensual(int mes, int anno) {

		 
		ResponseListadoAperturaMensual responseResult = new ResponseListadoAperturaMensual();
		
		try {

			String url = this.hostTerminal + "/caja/listado-mes?mes="+mes+"&anno=" + anno;
			 
			URI uri = new URI(url);
			ResponseEntity<List<AperturaCaja>> response = restTemplate.exchange(uri , HttpMethod.POST, null,
					new ParameterizedTypeReference<List<AperturaCaja>>() {
					});

			if (response.getStatusCodeValue() == 200) {
				responseResult.setCode(response.getStatusCodeValue());
				responseResult.setStatus(true);
				responseResult.setAperturaCaja(response.getBody());
 
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

	public ResponseValidarEstadoCaja validarCaja(String token, String fecha) {
 

		ResponseValidarEstadoCaja responseResult = new ResponseValidarEstadoCaja();
		try {

			String url = this.hostTerminal + "/caja/validar?fecha=" + fecha;
			HttpHeaders headers = new HttpHeaders();

			headers.set("Authorization", "Bearer "+token);
			HttpEntity<Void> requestEntity = new HttpEntity<>(null, headers);
			URI uri = new URI(url);
			ResponseEntity<AperturaCaja> response = restTemplate.exchange(uri , HttpMethod.GET, requestEntity,AperturaCaja.class);

			if (response.getStatusCodeValue() == 200) {
				responseResult.setCode(response.getStatusCodeValue());
				responseResult.setStatus(true);
				responseResult.setApertura(response.getBody());
 
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
	
	
	public ResponseOpenDay openDay(String fecha) {
		 

		ResponseOpenDay responseResult = new ResponseOpenDay();
		try {

			String url = this.hostTerminal + "/caja/open-day?fecha=" + fecha;
			
			URI uri = new URI(url);
			ResponseEntity<List<DiaAbierto>> response = restTemplate.exchange(uri , HttpMethod.GET, null,
					new ParameterizedTypeReference<List<DiaAbierto>>() {
			});

			if (response.getStatusCodeValue() == 200) {
				responseResult.setCode(response.getStatusCodeValue());
				responseResult.setStatus(true);
				responseResult.setOpen(response.getBody());
 
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
		}catch (org.springframework.web.client.ResourceAccessException e){
			ErrorState data = new ErrorState();
			data.setCode(300);
			responseResult.setStatus(false);
			data.setMenssage(e.getMessage());
			responseResult.setCode(data.getCode());
			responseResult.setError(data);
		}
		 
		return responseResult;
		 
	}


	public ResponseResultado guardarHistoricoCambio(HistoricoCambio request, String token) {

		 
		ResponseResultado responseResult = new ResponseResultado();
		try {

			String url = this.hostTerminal + "/cambio/add";
			HttpHeaders headers = new HttpHeaders();

			headers.set("Authorization", "Bearer "+token);
			HttpEntity<HistoricoCambio> requestEntity = new HttpEntity<>(request, headers);
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

	public ResponseAperturaTerminal obtenerAperturaTerminalPorUsuario(String token, String id, String fecha) {

		 

		ResponseAperturaTerminal responseResult = new ResponseAperturaTerminal();
		try {

			String url = this.hostTerminal + "/terminal/buscar?id=" + id + "&fecha=" + fecha;
			HttpHeaders headers = new HttpHeaders();

			headers.set("Authorization", "Bearer "+token);
			HttpEntity<Void> requestEntity = new HttpEntity<>(null, headers);
			URI uri = new URI(url);
			ResponseEntity<String> response = restTemplate.exchange(uri , HttpMethod.GET, requestEntity,String.class);

			if (response.getStatusCodeValue() == 200) {
				responseResult.setCode(response.getStatusCodeValue());
				responseResult.setStatus(true);
				AperturasTerminal ap= new Gson().fromJson(response.getBody(), AperturasTerminal.class);
				responseResult.setTerminal(ap);
 
			}else if (response.getStatusCodeValue() == 202){
				responseResult.setCode(406);
				responseResult.setStatus(true);
				ResponseApertura ap= new Gson().fromJson(response.getBody(), ResponseApertura.class);
				responseResult.setTerminalCierre(ap);
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
		catch (org.springframework.web.client.HttpClientErrorException  e) {
			JsonParser jsonParser = new JsonParser();
			int in = e.getLocalizedMessage().indexOf("{");
			int in2 = e.getLocalizedMessage().indexOf("}");
			String cadena = e.getMessage().substring(in, in2+1);
			JsonObject myJson = (JsonObject) jsonParser.parse(cadena);
			responseResult.setCode(myJson.get("code").getAsInt());
			ErrorState data = new ErrorState();
			data.setCode(myJson.get("code").getAsInt());
			data.setMenssage(myJson.get("menssage").getAsString());
			responseResult.setCode(data.getCode());
			responseResult.setError(data);
			return responseResult;	
		}
		 
		return responseResult;
		 
 	}

	public ResponseHistoricoCambio obtenerHitoricoCambioPorIDAperturaMoneda(String token, String id, int idmoneda) {
 
		ResponseHistoricoCambio responseResult = new ResponseHistoricoCambio();
		try {

			String url = this.hostTerminal + "/historico/cambio?id=" + id + "&moneda=" + idmoneda;
			HttpHeaders headers = new HttpHeaders();

			headers.set("Authorization", "Bearer "+token);
			HttpEntity<Void> requestEntity = new HttpEntity<>(null, headers);
			URI uri = new URI(url);
			ResponseEntity<HistoricoCambio> response = restTemplate.exchange(uri , HttpMethod.GET, requestEntity,HistoricoCambio.class);

			if (response.getStatusCodeValue() == 200) {

				responseResult.setStatus(true);
				responseResult.setHistoricoCambio(response.getBody());
 
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

	public ResponseResultado eliminarAperturaTerminal(String token, int id) {

		 
		ResponseResultado responseResult = new ResponseResultado();
		try {

			String url = this.hostTerminal + "/terminal/delete?id=" + id;
			HttpHeaders headers = new HttpHeaders();

			headers.set("Authorization", "Bearer "+token);
			HttpEntity<Void> requestEntity = new HttpEntity<>(null, headers);
			URI uri = new URI(url);
			ResponseEntity<String> response = restTemplate.exchange(uri , HttpMethod.DELETE, requestEntity,String.class);

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
