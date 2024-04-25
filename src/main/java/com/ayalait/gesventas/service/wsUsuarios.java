package com.ayalait.gesventas.service;

import com.ayalait.gesventas.controller.LoginController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

  import com.ayalait.logguerclass.Notification;
import com.ayalait.modelo.EstadoProceso;
import com.ayalait.modelo.Roles;
import com.ayalait.modelo.User;
import com.ayalait.response.ResponseListaUsuario;
import com.ayalait.response.ResponseResultado;
import com.ayalait.response.ResponseUsuario;
import com.ayalait.utils.ErrorState;
import com.ayalait.utils.MessageCodeImpl;
import com.ayalait.gesventas.request.*;

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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public final class wsUsuarios {
	private String hostSeguridad;

	public String getHostSeguridad() {
		return hostSeguridad;
	}

	public void setHostSeguridad(String hostSeguridad) {
		this.hostSeguridad = hostSeguridad;
	}

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
				this.hostSeguridad = p.getProperty("server.seguridad");
			}
		} catch (FileNotFoundException var3) {
			Logger.getLogger(wsUsuarios.class.getName()).log(Level.SEVERE, (String) null, var3);
		}

	}

	public wsUsuarios() {
		try {
			if(LoginController.desarrollo){
				hostSeguridad = "http://localhost:7000";
			}else{
				cargarServer();
			}
		} catch (IOException var2) {
			Logger.getLogger(wsUsuarios.class.getName()).log(Level.SEVERE, (String) null, var2);
		}

	}

	public ResponseResultado obtenerToken(String user, String pwd) {
		ResponseResultado responseResult = new ResponseResultado();

		try {
			HttpHeaders headers = new HttpHeaders();
			String url = this.hostSeguridad + "/login/token?user=" + user + "&pwd=" + pwd;
			URI uri = new URI(url);
			ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, null, String.class);

			if (response.getStatusCodeValue() == 200) {
				responseResult.setCode(response.getStatusCodeValue());
				responseResult.setStatus(true);
				responseResult.setResultado(response.getBody());
				return responseResult;
			}

		} catch (org.springframework.web.client.HttpServerErrorException e) {

			/*ErrorState data = new ErrorState();
			data.setCode(e.getStatusCode().value());
			data.setMenssage(e.getMessage());
			responseResult.setCode(data.getCode());
			responseResult.setError(data);*/
			return responseResult;

		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (org.springframework.web.client.ResourceAccessException e) {
			responseResult.setStatus(false);
			ErrorState data = new ErrorState();
			data.setCode(500);
			data.setMenssage(MessageCodeImpl.getMensajeHttp(com.ayalait.utils.Messages.err_connection_timed_out ));
			responseResult.setCode(data.getCode());
			responseResult.setError(data);
		}catch (org.springframework.web.client.HttpClientErrorException e) {
			responseResult.setStatus(false);
			ErrorState data = new ErrorState();
			data.setCode(e.getRawStatusCode());
			data.setMenssage(MessageCodeImpl.getMensajeHttp(String.valueOf(e.getRawStatusCode())  ));
			responseResult.setCode(data.getCode());
			responseResult.setError(data);
		}

		return responseResult;

		

	}

 	public ResponseResultado validarConectividadServidor() {
		 
		ResponseResultado responseResult = new ResponseResultado();
		
		try {

			String url = this.hostSeguridad + "/server";
			 
			URI uri = new URI(url);
			ResponseEntity<String> response = restTemplate.exchange(uri , HttpMethod.GET, null,String.class);

			if (response.getStatusCodeValue() == 200) {

				responseResult.setStatus(true);
				responseResult.setResultado(response.getBody());
 
			}else {
				responseResult.setStatus(false);
				responseResult.setResultado("Módulo en mantenimiento.");

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


	public ResponseResultado validarToken(String token) {
		 
		ResponseResultado responseResult = new ResponseResultado();
		
		try {

			String url = this.hostSeguridad + "/login/validar";
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

	public ResponseUsuario obtenerDatosUsuarioLogin(String token, String user) {
		 
		ResponseUsuario responseResult = new ResponseUsuario();
		
		try {

			String url = this.hostSeguridad + "/usuario/buscar?user="+user;
			HttpHeaders headers = new HttpHeaders();
			
			headers.set("Authorization", "Bearer "+token);
			headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
			HttpEntity<Void> requestEntity = new HttpEntity<>(null, headers);
			URI uri = new URI(url);
			ResponseEntity<User> response = restTemplate.exchange(uri , HttpMethod.GET, requestEntity,User.class);

			if (response.getStatusCodeValue() == 200) {
				responseResult.setCode(response.getStatusCodeValue());
				responseResult.setStatus(true);
				responseResult.setUser((User)response.getBody());
 
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

	public ResponseUsuario buscarUsuarioPorId(String token, String id) {
		 
		ResponseUsuario responseResult = new ResponseUsuario();
		
		try {

			String url = this.hostSeguridad + "/usuario/id-usuario?id=" + id;
			HttpHeaders headers = new HttpHeaders();

			headers.set("Authorization", "Bearer "+token);
			HttpEntity<Void> requestEntity = new HttpEntity<>(null, headers);
			URI uri = new URI(url);
			ResponseEntity<Object> response = restTemplate.exchange(uri , HttpMethod.POST, requestEntity,Object.class);

			if (response.getStatusCodeValue() == 200) {

				responseResult.setStatus(true);
				responseResult.setUser((User)response.getBody());
 
			}else {
				responseResult.setStatus(false);
				responseResult.setResultado((String)response.getBody());
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


	public ResponseUsuario validarUsuario(String usuario, String token) {
		 
		ResponseUsuario responseResult = new ResponseUsuario();
		
		try {

			String url = this.hostSeguridad + "/usuario/buscar?user=" + usuario;
			HttpHeaders headers = new HttpHeaders();

			headers.set("Authorization", "Bearer "+token);
			HttpEntity<Void> requestEntity = new HttpEntity<>(null, headers);
			URI uri = new URI(url);
			ResponseEntity<Object> response = restTemplate.exchange(uri , HttpMethod.GET, requestEntity,Object.class);

			if (response.getStatusCodeValue() == 200) {

				responseResult.setStatus(true);
				responseResult.setUser((User)response.getBody());
 
			}else {
				responseResult.setStatus(false);
				responseResult.setResultado((String)response.getBody());
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

	public ResponseResultado addUsuario(RequestAddUsuario request, String token) {
		 
		ResponseResultado responseResult = new ResponseResultado();
		
		try {

			String url = this.hostSeguridad + "/usuario/add";
			HttpHeaders headers = new HttpHeaders();

			headers.set("Authorization", "Bearer "+token);
 			HttpEntity<RequestAddUsuario> requestEntity = new HttpEntity<>(request, headers);
			URI uri = new URI(url);
			ResponseEntity<String> response = restTemplate.exchange(uri , HttpMethod.POST, requestEntity,String.class);

			if (response.getStatusCodeValue() == 200) {

				responseResult.setStatus(true);
				responseResult.setResultado(response.getBody());
 
			}else {
				responseResult.setStatus(false);
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

	public ResponseUsuario eliminarUsuario(int idusuario, String token) {
		 
		ResponseUsuario responseResult = new ResponseUsuario();
		
		try {

			String url = this.hostSeguridad + "/usuario/eliminar?id=" + idusuario;
			HttpHeaders headers = new HttpHeaders();

			headers.set("Authorization", "Bearer "+token);
 			HttpEntity<Void> requestEntity = new HttpEntity<>(null, headers);
			URI uri = new URI(url);
			ResponseEntity<String> response = restTemplate.exchange(uri , HttpMethod.GET, requestEntity,String.class);

			if (response.getStatusCodeValue() == 200) {

				responseResult.setStatus(true);
				responseResult.setResultado(response.getBody());
 
			}else {
				responseResult.setStatus(false);
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

	public ResponseListaUsuario listadUsuariosPorEmpresa(int id, String token) {
		 
		ResponseListaUsuario responseResult = new ResponseListaUsuario();
		
		try {

			String url = this.hostSeguridad + "/usuario/id?id=" + id;
			HttpHeaders headers = new HttpHeaders();

			headers.set("Authorization", "Bearer "+token);
 			HttpEntity<Void> requestEntity = new HttpEntity<>(null, headers);

			URI uri = new URI(url);
			ResponseEntity<List<User>> response = restTemplate.exchange(uri , HttpMethod.GET, requestEntity,
					new ParameterizedTypeReference<List<User>>() {
					});

			if (response.getStatusCodeValue() == 200) {
				responseResult.setCode(response.getStatusCodeValue());
				responseResult.setStatus(true);
				responseResult.setUser(response.getBody());

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

	public ResponseListaUsuario listadoUsuarios(String token) {
		 
		ResponseListaUsuario responseResult = new ResponseListaUsuario();
		
		try {

			String url = this.hostSeguridad + "/usuario/lista";
			HttpHeaders headers = new HttpHeaders();

			headers.set("Authorization", "Bearer "+token);
 			HttpEntity<Void> requestEntity = new HttpEntity<>(null, headers);

			URI uri = new URI(url);
			ResponseEntity<List<User>> response = restTemplate.exchange(uri , HttpMethod.GET, requestEntity,
					new ParameterizedTypeReference<List<User>>() {
					});

			if (response.getStatusCodeValue() == 200) {

				responseResult.setStatus(true);
				responseResult.setUser(response.getBody());

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
	
	
	
	public ResponseResultado cambiarPassword(String token,String idUsuario, String password) {
		 
		ResponseResultado responseResult = new ResponseResultado();
		
		try {

			String url = this.hostSeguridad + "/usuario/password/cambio?id="+idUsuario+"&pass="+password;
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