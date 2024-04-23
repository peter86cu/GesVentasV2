package com.ayalait.gesventas.service;

import com.ayalait.gesventas.controller.LoginController;
 import com.ayalait.modelo.*;
 import com.ayalait.response.*;
import com.ayalait.utils.ErrorState;
import com.ayalait.utils.MessageCodeImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.Gson;

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
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


public final class wsContable {

	private String hostContable;
	

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

				hostContable = p.getProperty("server.contable");
			}
		} catch (FileNotFoundException ex) {
			Logger.getLogger(wsContable.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	public wsContable() {
		try {
			if(LoginController.desarrollo){
				hostContable = "http://localhost:7003";
			}else{
				cargarServer();
			}

		} catch (IOException ex) {
			Logger.getLogger(wsContable.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	 public ResponseResultado validarConectividadServidor() {
		 
		ResponseResultado responseResult = new ResponseResultado();
		
		try {

			String url = this.hostContable + "/server";
			 
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

	public ResponseListaTipoGastos listaTipoGastos() {

		 
		ResponseListaTipoGastos responseResult = new ResponseListaTipoGastos();
		try {

			String url = this.hostContable + "/contable/tipo-gastos";
			
			URI uri = new URI(url);
			ResponseEntity<List<TipoGastos>> response = restTemplate.exchange(uri , HttpMethod.POST, null,
					new ParameterizedTypeReference<List<TipoGastos>>() {
			});

			if (response.getStatusCodeValue() == 200) {
				responseResult.setCode(response.getStatusCodeValue());
				responseResult.setStatus(true);
				responseResult.setTipoGasto(response.getBody());
 
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
	
	
	public ResponseResultado addGasto(ContableLibroDiario gastos) {

		 
		ResponseResultado responseResult = new ResponseResultado();
		try {

			String url = this.hostContable + "/contable/gastos/add";
			String data= new Gson().toJson(gastos);
			HttpEntity<String> requestEntity = new HttpEntity<>(data, null);
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
	
	
	public ResponseListaCostosDirect obterCostosDirectos(int mes, int anio) {

		 
		ResponseListaCostosDirect responseResult = new ResponseListaCostosDirect();
		try {

			String url = this.hostContable + "/contable/costos-directos?mes="+mes+"&anio="+anio;
			
			URI uri = new URI(url);
			ResponseEntity<List<ContableLibroDiario>> response = restTemplate.exchange(uri , HttpMethod.GET, null,
					new ParameterizedTypeReference<List<ContableLibroDiario>>() {
			});

			if (response.getStatusCodeValue() == 200) {
				responseResult.setCode(response.getStatusCodeValue());
				responseResult.setStatus(true);
				responseResult.setLibroDiario(response.getBody());
 
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
			data.setMenssage(e.getMessage());
			responseResult.setCode(data.getCode());
			responseResult.setError(data);
		}
		 

		return responseResult;	
		 
	}
	
	
	public ResponseResultado addCalculoCostos(CalculoCosto calculo) {

		 
		ResponseResultado responseResult = new ResponseResultado();
		try {

			String url = this.hostContable + "/contable/calculo/add";
			String data= new Gson().toJson(calculo);
			HttpEntity<String> requestEntity = new HttpEntity<>(data, null);
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
			data.setMenssage(e.getMessage());
			responseResult.setCode(data.getCode());
			responseResult.setError(data);
		}
		 

		return responseResult;	
		 
	}
	
	
	
	public ResponseResultado addCentroCosto(CentroCosto centro) {

		 
		ResponseResultado responseResult = new ResponseResultado();
		try {

			String url = this.hostContable + "/contable/centro-costo/add";
			String data= new Gson().toJson(centro);
			HttpEntity<String> requestEntity = new HttpEntity<>(data, null);
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
	
	
	public ResponseListaCentroCosto listaCentroCosto() {

		 
		ResponseListaCentroCosto responseResult = new ResponseListaCentroCosto();
		try {

			String url = this.hostContable + "/contable/centro-costo/lista";
			
			URI uri = new URI(url);
			ResponseEntity<List<CentroCosto>> response = restTemplate.exchange(uri , HttpMethod.POST, null,
					new ParameterizedTypeReference<List<CentroCosto>>() {
			});

			if (response.getStatusCodeValue() == 200) {
				responseResult.setCode(response.getStatusCodeValue());
				responseResult.setStatus(true);
				responseResult.setCentroCosto(response.getBody());
 
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

}
