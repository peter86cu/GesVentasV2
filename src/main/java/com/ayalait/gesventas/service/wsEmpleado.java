package com.ayalait.gesventas.service;

import com.ayalait.gesventas.controller.LoginController;
import com.ayalait.gesventas.request.RequestAddEmpleado;
import com.ayalait.modelo.Banco;
import com.ayalait.modelo.CalendarioMesAProcesar;
import com.ayalait.modelo.Empleado;
import com.ayalait.modelo.EmpleadoBanco;
import com.ayalait.modelo.EmpleadoCargo;
import com.ayalait.modelo.EmpleadoSalud;
import com.ayalait.modelo.EmpleadoTitulos;
import com.ayalait.modelo.EmpleadoTrabajo;
import com.ayalait.modelo.HorarioLaboral;
import com.ayalait.modelo.HorarioTrabajo;
import com.ayalait.modelo.MarcasEmpleado;
import com.ayalait.modelo.MesPago;
import com.ayalait.modelo.Modulos;
import com.ayalait.modelo.TipoDocumento;
import com.ayalait.modelo.User;
import com.ayalait.response.*;
import com.ayalait.utils.ErrorState;
import com.ayalait.utils.MarcaEmpleadoProcess;
import com.ayalait.utils.MessageCodeImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Date;
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


public class wsEmpleado {

	private String hostSeguridad;
	private String hostAdministraccion;
	private String hostRecursosHumanos;
	
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

				// InputStream propertiesStream =
				// ClassLoader.getSystemResourceAsStream("application.properties");
				p.load(propertiesStream);
				propertiesStream.close();
				this.hostSeguridad = p.getProperty("server.seguridad");
				this.hostAdministraccion = p.getProperty("server.configuracion");
				this.hostRecursosHumanos = p.getProperty("server.rrhh");
			}
		} catch (FileNotFoundException var3) {
			Logger.getLogger(wsEmpleado.class.getName()).log(Level.SEVERE, (String) null, var3);
		}

	}

	public wsEmpleado() {
		try {
			if (LoginController.desarrollo) {
				hostSeguridad = "http://localhost:7000";
				hostRecursosHumanos = "http://localhost:8085";
			} else {
				cargarServer();
			}
		} catch (IOException var2) {
			Logger.getLogger(wsEmpleado.class.getName()).log(Level.SEVERE, (String) null, var2);
		}

	}

	
	public ResponseResultado validarConectividadServidor() {
		 
		ResponseResultado responseResult = new ResponseResultado();
		
		try {

			String url = this.hostRecursosHumanos + "/server";
			 
			URI uri = new URI(url);
			ResponseEntity<String> response = restTemplate.exchange(uri , HttpMethod.GET, null,String.class);

			if (response.getStatusCodeValue() == 200) {
				responseResult.setCode(response.getStatusCodeValue());
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
	
	
	
	

	
	public ResponseResultado validarEmpledoPorDocumento(String documento) {
		 
		ResponseResultado responseResult = new ResponseResultado();
		
		try {

			String url = this.hostRecursosHumanos + "/empleado/validar?documento="+documento;
			 
			//URI uri = new URI(url);
			ResponseEntity<String> response = restTemplate.exchange(url , HttpMethod.GET, null,String.class);

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
			 
		} 

		return responseResult;

		 

	}
	
	public ResponseUsuario obtenerEmpleado(String token, String user) {
		 
		ResponseUsuario responseResult = new ResponseUsuario();
		try {

			String url = this.hostRecursosHumanos + "/usuario/buscar?user=" + user;
			HttpHeaders headers = new HttpHeaders();

			headers.set("Authorization", "Bearer "+token);
			HttpEntity<Void> requestEntity = new HttpEntity<>(null, headers);
			URI uri = new URI(url);
			ResponseEntity<User> response = restTemplate.exchange(uri , HttpMethod.POST, requestEntity,User.class);

			if (response.getStatusCodeValue() == 200) {
				responseResult.setCode(response.getStatusCodeValue());
				responseResult.setStatus(true);
				responseResult.setUser(response.getBody());
				String urlM = this.hostRecursosHumanos + "/usermodulos?id=" + responseResult.getUser().getIdusuario();
				ResponseEntity<List<Modulos>>responseModulos = restTemplate.exchange(urlM , HttpMethod.GET, requestEntity,
						new ParameterizedTypeReference<List<Modulos>>() {
				});
				
				if (responseModulos.getStatusCodeValue() == 200) {
					responseResult.setModulos(responseModulos.getBody());
				}
 
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

	public ResponseListaEmpleados listadEmpleadoPorEmpresa(String token, int id_empresa) {
		 
		ResponseListaEmpleados responseResult = new ResponseListaEmpleados();
		
		try {

			String url = this.hostRecursosHumanos + "/empleado/nuevos?id=" + id_empresa;
			 
			//URI uri = new URI(url);
			ResponseEntity<List<Empleado>> response = restTemplate.exchange(url , HttpMethod.GET, null,
					new ParameterizedTypeReference<List<Empleado>>() {
					});

			if (response.getStatusCodeValue() == 200) {
				responseResult.setCode(response.getStatusCodeValue());
				responseResult.setStatus(true);
				responseResult.setEmpleados(response.getBody());
 
			}

		} catch (org.springframework.web.client.HttpServerErrorException e) {
			ErrorState data = new ErrorState();
			data.setCode(e.getStatusCode().value());
			data.setMenssage(e.getMessage());
			responseResult.setCode(data.getCode());
			responseResult.setError(data);
			 
		} catch (org.springframework.web.client.ResourceAccessException e){
			ErrorState data = new ErrorState();
			data.setCode(300);
			responseResult.setStatus(false);
			data.setMenssage(e.getMessage());
			responseResult.setCode(data.getCode());
			responseResult.setError(data);
		}
		 

		return responseResult;

		 
	}

	public ResponseListaTipoDoc listadoTipoDocumento() {
		 
		ResponseListaTipoDoc responseResult = new ResponseListaTipoDoc();
		
		try {

			String url = this.hostRecursosHumanos + "/parametros/tipo-documento";
			 
			URI uri = new URI(url);
			ResponseEntity<List<TipoDocumento>> response = restTemplate.exchange(uri , HttpMethod.GET, null,
					new ParameterizedTypeReference<List<TipoDocumento>>() {
					});

			if (response.getStatusCodeValue() == 200) {
				responseResult.setCode(response.getStatusCodeValue());
				responseResult.setStatus(true);
				responseResult.setDocumento(response.getBody());
 
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
	
	
	
	public ResponseListaBancos listadoBancos() {
		 
		ResponseListaBancos responseResult = new ResponseListaBancos();
		
		try {

			String url = this.hostRecursosHumanos + "/parametros/banco";
			 
			URI uri = new URI(url);
			ResponseEntity<List<Banco>> response = restTemplate.exchange(uri , HttpMethod.GET, null,
					new ParameterizedTypeReference<List<Banco>>() {
					});

			if (response.getStatusCodeValue() == 200) {
				responseResult.setCode(response.getStatusCodeValue());
				responseResult.setStatus(true);
				responseResult.setBanco(response.getBody());
 
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

	public ResponseListaMesPago mesesAPagar() {
		 
		ResponseListaMesPago responseResult = new ResponseListaMesPago();
		
		try {

			String url = this.hostRecursosHumanos + "/parametros/mes-pago";
			 
			URI uri = new URI(url);
			ResponseEntity<List<MesPago>> response = restTemplate.exchange(uri , HttpMethod.GET, null,
					new ParameterizedTypeReference<List<MesPago>>() {
					});

			if (response.getStatusCodeValue() == 200) {
				responseResult.setCode(response.getStatusCodeValue());
				responseResult.setStatus(true);
				responseResult.setMes(response.getBody());
 
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
	
	
	public ResponseMesAProcesar mesAProcesar() {
		 
		ResponseMesAProcesar responseResult = new ResponseMesAProcesar();
		
		try {

			String url = this.hostRecursosHumanos + "/calendario/mes-a-procesar";
			 
			URI uri = new URI(url);
			ResponseEntity<CalendarioMesAProcesar> response = restTemplate.exchange(uri , HttpMethod.GET, null,CalendarioMesAProcesar.class);

			if (response.getStatusCodeValue() == 200) {
				responseResult.setCode(response.getStatusCodeValue());
				responseResult.setStatus(true);
				responseResult.setMesProcesar(response.getBody());
 
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
	
	
	public ResponseListaHorarioTrabajo listadoHorarioTrabajo() {
		 
		ResponseListaHorarioTrabajo responseResult = new ResponseListaHorarioTrabajo();
		

		try {

			String url = this.hostRecursosHumanos + "/parametros/horario-trabajo";
			 
			URI uri = new URI(url);
			ResponseEntity<List<HorarioTrabajo>> response = restTemplate.exchange(uri , HttpMethod.GET, null,
					new ParameterizedTypeReference<List<HorarioTrabajo>>() {
					});

			if (response.getStatusCodeValue() == 200) {
				responseResult.setCode(response.getStatusCodeValue());
				responseResult.setStatus(true);
				responseResult.setHorario(response.getBody());
 
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
	
	public ResponseCalendarioEmpleado consultarHorarioLaboral(String documento,int dia, int mes, int annio) {
		 
		ResponseCalendarioEmpleado responseResult = new ResponseCalendarioEmpleado();
		
		try {

			String url = this.hostRecursosHumanos + "/empleado/filtro?documento="+documento+"&mes="+mes+"&annio="+annio+"&dia="+dia;
			 
			//URI uri = new URI(url);
			ResponseEntity<List<HorarioLaboral>> response = restTemplate.exchange(url , HttpMethod.GET, null,
					new ParameterizedTypeReference<List<HorarioLaboral>>() {
					});

			if (response.getStatusCodeValue() == 200) {
				responseResult.setCode(response.getStatusCodeValue());
				responseResult.setStatus(true);
				responseResult.setHorario(response.getBody());
 
			}

		} catch (org.springframework.web.client.HttpServerErrorException e) {
			ErrorState data = new ErrorState();
			data.setCode(e.getStatusCode().value());
			data.setMenssage(e.getMessage());
			responseResult.setCode(data.getCode());
			responseResult.setError(data);
			 
		}  catch (org.springframework.web.client.HttpClientErrorException e) {
			/*ErrorState data = new ErrorState();
			data.setCode(e.getStatusCode().value());
			data.setMenssage(MessageCodeImpl.getMensajeServiceTerminal(String.valueOf(e.getStatusCode().value() )));
			responseResult.setCode(e.getStatusCode().value());
			responseResult.setError(data);*/
			JsonParser jsonParser = new JsonParser();
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
	
	
	public ResponseBuscarEmpleado buscarEmpleadoPorId(String token, String id)   {
		 
		ResponseBuscarEmpleado responseResult = new ResponseBuscarEmpleado();
		
		try {

			String url = this.hostRecursosHumanos + "/empleado/buscar?id=" + id;
			HttpHeaders headers = new HttpHeaders();

			headers.set("Authorization", "Bearer "+token);
			HttpEntity<Void> requestEntity = new HttpEntity<>(null, headers);
			URI uri = new URI(url);
			ResponseEntity<Empleado> response = restTemplate.exchange(uri , HttpMethod.GET, requestEntity,Empleado.class);

			if (response.getStatusCodeValue() == 200) {
				responseResult.setCode(response.getStatusCodeValue());
				responseResult.setStatus(true);
				responseResult.setEmpleados(response.getBody());
 
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
	
	
	
	public ResponseMarcasPorEmpleado buscarMarcasEmpleado(String idEmpleado, Date fecha)   {
		 
		ResponseMarcasPorEmpleado responseResult = new ResponseMarcasPorEmpleado();
		try {

			String url = this.hostRecursosHumanos + "/empleado/marcas/buscar?id=" + idEmpleado+"&fecha="+fecha;
			 
			//URI uri = new URI(url);
			ResponseEntity<MarcasEmpleado> response = restTemplate.exchange(url , HttpMethod.GET, null,MarcasEmpleado.class);

			if (response.getStatusCodeValue() == 200) {
				responseResult.setCode(response.getStatusCodeValue());
				responseResult.setStatus(true);
				responseResult.setMarcas(response.getBody());
 
			}

		} catch (org.springframework.web.client.HttpServerErrorException e) {
			ErrorState data = new ErrorState();
			data.setCode(e.getStatusCode().value());
			data.setMenssage(e.getMessage());
			responseResult.setCode(data.getCode());
			responseResult.setError(data);
			 
		} catch (org.springframework.web.client.HttpClientErrorException e) {
			ErrorState data = new ErrorState();
			data.setCode(e.getStatusCode().value());
			data.setMenssage(e.getMessage());
			responseResult.setCode(data.getCode());
			responseResult.setError(data);
		}
		 

		return responseResult;
		 
	}
	
	public ResponseResultado generarCalendarioEmpleado(String accion,int mes, int anio)   {
		 
		ResponseResultado responseResult = new ResponseResultado();
		
		try {

			String url = this.hostRecursosHumanos + "/empleado/calendario/generar?accion="+accion+"&mes="+mes+"&anio="+anio;
			 
			//URI uri = new URI(url);
			ResponseEntity<String> response = restTemplate.exchange(url , HttpMethod.POST, null,String.class);

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
			 
		} catch (org.springframework.web.client.HttpClientErrorException e) {
			JsonParser jsonParser = new JsonParser();
			int in = e.getLocalizedMessage().indexOf("{");
			int in2 = e.getLocalizedMessage().indexOf("}");
			String cadena = e.getMessage().substring(in, in2+1);
			JsonObject myJson = (JsonObject) jsonParser.parse(cadena);
			responseResult.setCode(myJson.get("code").getAsInt());
			ErrorState data = new ErrorState();
			data.setCode(myJson.get("code").getAsInt());
			data.setMenssage(MessageCodeImpl.getMensajeServiceMarcasEmpleados(myJson.get("code").getAsString() ));
			responseResult.setCode(data.getCode());
			responseResult.setError(data);
			return responseResult;	
		}
		 

		return responseResult;

		 
	}
	
	public ResponseListaMarcasEmpleados filtrarMarcas(int mes, int anio, String estado)   {
		 
		ResponseListaMarcasEmpleados responseResult = new ResponseListaMarcasEmpleados();
		
		try {

			String url = this.hostRecursosHumanos + "/empleado/marcas/filtro?mes="+mes+"&anio="+anio+"&estado=" + estado;
			 
			//URI uri = new URI(url);
			ResponseEntity<List<MarcasEmpleado>> response = restTemplate.exchange(url , HttpMethod.POST, null,
					new ParameterizedTypeReference<List<MarcasEmpleado>>() {
			});

			if (response.getStatusCodeValue() == 200) {
				responseResult.setCode(response.getStatusCodeValue());
				responseResult.setStatus(true);
				responseResult.setMarcas(response.getBody());
 
			}

		} catch (org.springframework.web.client.HttpServerErrorException e) {
			ErrorState data = new ErrorState();
			data.setCode(e.getStatusCode().value());
			data.setMenssage(e.getMessage());
			responseResult.setCode(data.getCode());
			responseResult.setError(data);
			 
		} catch (org.springframework.web.client.HttpClientErrorException e) {
			JsonParser jsonParser = new JsonParser();
			int in = e.getLocalizedMessage().indexOf("{");
			int in2 = e.getLocalizedMessage().indexOf("}");
			String cadena = e.getMessage().substring(in, in2+1);
			JsonObject myJson = (JsonObject) jsonParser.parse(cadena);
			responseResult.setCode(myJson.get("code").getAsInt());
			ErrorState data = new ErrorState();
			data.setCode(myJson.get("code").getAsInt());
			data.setMenssage(MessageCodeImpl.getMensajeServiceMarcasEmpleados(myJson.get("code").getAsString() ));
			responseResult.setCode(data.getCode());
			responseResult.setError(data);
			return responseResult;	
		}
		 

		return responseResult;

		 
	}
	
	
	public ResponseResultado existeMesAProcesar(int mes, int anio)   {
		 
		ResponseResultado responseResult = new ResponseResultado();
		
		try {

			String url = this.hostRecursosHumanos + "/calendario/existe-mes-procesar?mes="+mes+"&anio="+anio;
			 
			//URI uri = new URI(url);
			ResponseEntity<String> response = restTemplate.exchange(url , HttpMethod.POST, null,String.class);

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
			 
		} catch (org.springframework.web.client.HttpClientErrorException e) {
			JsonParser jsonParser = new JsonParser();
			int in = e.getLocalizedMessage().indexOf("{");
			int in2 = e.getLocalizedMessage().indexOf("}");
			String cadena = e.getMessage().substring(in, in2+1);
			JsonObject myJson = (JsonObject) jsonParser.parse(cadena);
			responseResult.setCode(myJson.get("code").getAsInt());
			ErrorState data = new ErrorState();
			data.setCode(myJson.get("code").getAsInt());
			data.setMenssage(MessageCodeImpl.getMensajeServiceMarcasEmpleados(myJson.get("code").getAsString() ));
			responseResult.setCode(data.getCode());
			responseResult.setError(data);
			return responseResult;	
		}
		 

		return responseResult;

		 
	}
	
	
	public ResponseResultado procesoMarcasAsistencia(int mes, int anio)   {
		 
		ResponseResultado responseResult = new ResponseResultado();
		
		try {

			String url = this.hostRecursosHumanos + "/empleado/marcas/procesar?mes="+mes+"&anio="+anio;
			 
			//URI uri = new URI(url);
			ResponseEntity<String> response = restTemplate.exchange(url , HttpMethod.POST, null,String.class);

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
			 
		} catch (org.springframework.web.client.HttpClientErrorException e) {
			JsonParser jsonParser = new JsonParser();
			int in = e.getLocalizedMessage().indexOf("{");
			int in2 = e.getLocalizedMessage().indexOf("}");
			String cadena = e.getMessage().substring(in, in2+1);
			JsonObject myJson = (JsonObject) jsonParser.parse(cadena);
			responseResult.setCode(myJson.get("code").getAsInt());
			ErrorState data = new ErrorState();
			data.setCode(myJson.get("code").getAsInt());
			data.setMenssage(MessageCodeImpl.getMensajeServiceMarcasEmpleados(myJson.get("code").getAsString() ));
			responseResult.setCode(data.getCode());
			responseResult.setError(data);
			return responseResult;	
		}
		 

		return responseResult;

		 
	}
	
	
	public ResponseResultado procesarMarcaEmpleado(MarcasEmpleado marca)   {
		 
		ResponseResultado responseResult = new ResponseResultado();
		
		try {

			String url = this.hostRecursosHumanos + "/empleado/marcas/procesar-empleado";
			HttpEntity<MarcasEmpleado> requestEntity = new HttpEntity<>(marca, null);

			URI uri = new URI(url);
			ResponseEntity<String> response = restTemplate.exchange(uri , HttpMethod.POST, requestEntity,String.class);

			if (response.getStatusCodeValue() == 200 || response.getStatusCodeValue() == 202) {
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
			JsonParser jsonParser = new JsonParser();
			int in = e.getLocalizedMessage().indexOf("{");
			int in2 = e.getLocalizedMessage().indexOf("}");
			String cadena = e.getMessage().substring(in, in2+1);
			JsonObject myJson = (JsonObject) jsonParser.parse(cadena);
			responseResult.setCode(myJson.get("code").getAsInt());
			ErrorState data = new ErrorState();
			data.setCode(myJson.get("code").getAsInt());
			data.setMenssage(MessageCodeImpl.getMensajeServiceMarcasEmpleados(myJson.get("code").getAsString() ));
			responseResult.setCode(data.getCode());
			responseResult.setError(data);
			return responseResult;	
		}
		 

		return responseResult;

		 
	}
	
	
	
	public ResponseMarcasProcessEmpl obtenerMarcasProcesadasEmpleado(String documento, int mes, int anio)   {
		 
		ResponseMarcasProcessEmpl responseResult = new ResponseMarcasProcessEmpl();
		
		try {

			String url = this.hostRecursosHumanos + "/empleado/marcas/obtener-marcas-empleado?documento="+documento+"&mes="+mes+"&anio="+anio;

			//URI uri = new URI(url);
			ResponseEntity<List<MarcaEmpleadoProcess>> response = restTemplate.exchange(url , HttpMethod.POST, null,new ParameterizedTypeReference<List<MarcaEmpleadoProcess>>() {
			});

			if (response.getStatusCodeValue() == 200) {
				responseResult.setCode(response.getStatusCodeValue());
				responseResult.setStatus(true);
				responseResult.setProcess(response.getBody());
			}

		} catch (org.springframework.web.client.HttpServerErrorException e) {
			ErrorState data = new ErrorState();
			data.setCode(e.getStatusCode().value());
			data.setMenssage(e.getMessage());
			responseResult.setCode(data.getCode());
			responseResult.setError(data);
			 
		} catch (org.springframework.web.client.HttpClientErrorException e) {
			JsonParser jsonParser = new JsonParser();
			int in = e.getLocalizedMessage().indexOf("{");
			int in2 = e.getLocalizedMessage().indexOf("}");
			String cadena = e.getMessage().substring(in, in2+1);
			JsonObject myJson = (JsonObject) jsonParser.parse(cadena);
			responseResult.setCode(myJson.get("code").getAsInt());
			ErrorState data = new ErrorState();
			data.setCode(myJson.get("code").getAsInt());
			data.setMenssage(MessageCodeImpl.getMensajeServiceMarcasEmpleados(myJson.get("code").getAsString() ));
			responseResult.setCode(data.getCode());
			responseResult.setError(data);
			return responseResult;	
		}
		 

		return responseResult;

		 
	}

	public ResponseListaEmpleados listadEmpleado() {
		 
		ResponseListaEmpleados responseResult = new ResponseListaEmpleados();
		
		try {

			String url = this.hostRecursosHumanos + "/empleado/lista";
			 
			URI uri = new URI(url);
			ResponseEntity<List<Empleado>> response = restTemplate.exchange(uri , HttpMethod.GET, null,
					new ParameterizedTypeReference<List<Empleado>>() {
					});

			if (response.getStatusCodeValue() == 200) {
				responseResult.setCode(response.getStatusCodeValue());
				responseResult.setStatus(true);
				responseResult.setEmpleados(response.getBody());
 
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

			String url = this.hostSeguridad + "/buscar?user=" + usuario;
			HttpHeaders headers = new HttpHeaders();

			headers.set("Authorization", "Bearer "+token);
			HttpEntity<Void> requestEntity = new HttpEntity<>(null, headers);
			URI uri = new URI(url);
			ResponseEntity<User> response = restTemplate.exchange(uri , HttpMethod.GET, requestEntity,User.class);

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
		}
		 

		return responseResult;

		 
	}
	
	/*public ResponseResultado procesarMarcas(int mes, int annio,String estado ) {
		Response response = null;
		Client cliente = ClientBuilder.newClient();
		String responseJson = "";
		new ErrorResult();
		ResponseUsuario responseUser = new ResponseUsuario();

		ResponseUsuario var12;
		try {
			WebTarget webTarjet = cliente.target(hostSeguridad + "/buscar?user=" + usuario);
			Builder invoker = webTarjet.request(new String[] { "application/json" }).header("Authorization",
					"Bearer " + token);
			response = invoker.get();
			if (response.getStatus() == 200) {
				responseJson = (String) response.readEntity(String.class);
				responseUser.setStatus(true);
				responseUser.setCode(response.getStatus());
				User data = (User) (new Gson()).fromJson(responseJson, User.class);
				responseUser.setUser(data);
				return responseUser;
			}

			responseUser.setStatus(false);
			responseUser.setCode(response.getStatus());
			ErrorResult error = (ErrorResult) (new Gson()).fromJson((String) response.readEntity(String.class),
					ErrorResult.class);
			responseUser.setError(error);
			var12 = responseUser;
		} catch (RuntimeException var15) {
			System.err.println("Error " + var15.getMessage());
			return responseUser;
		} finally {
			if (response != null) {
				response.close();
			}

			if (cliente != null) {
				cliente.close();
			}

		}

		return var12;
	}*/

 	public ResponseResultado addEmpleado(RequestAddEmpleado request) {
		 
 		
		ResponseResultado responseResult = new ResponseResultado();
		try {

			String url = this.hostRecursosHumanos + "/empleado/add";
			HttpHeaders headers = new HttpHeaders();
 			HttpEntity<RequestAddEmpleado> requestEntity = new HttpEntity<>(request, headers);
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
		}
		 

		return responseResult;

		 
	}
	
	
	public ResponseResultado addMarcaEmpleado(MarcasEmpleado request) {
		 
		ResponseResultado responseResult = new ResponseResultado();
		try {

			String url = this.hostRecursosHumanos + "/empleado/marcas/add";
			HttpHeaders headers = new HttpHeaders();
 			HttpEntity<MarcasEmpleado> requestEntity = new HttpEntity<>(request, headers);
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
		}
		 

		return responseResult;

		 
	}
	
	
	public ResponseResultado addEmpleadoTitulos(EmpleadoTitulos request) {
		 
		ResponseResultado responseResult = new ResponseResultado();
		try {

			String url = this.hostRecursosHumanos + "/empleado/add/titulos";
			HttpHeaders headers = new HttpHeaders();
 			HttpEntity<EmpleadoTitulos> requestEntity = new HttpEntity<>(request, headers);
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
		}
		 

		return responseResult;

		 
	}
	
	
	public ResponseResultado addEmpleadoTrabajos(EmpleadoTrabajo request) {
		 
		ResponseResultado responseResult = new ResponseResultado();
		
		try {

			String url = this.hostRecursosHumanos + "/empleado/add/trabajos";
			HttpHeaders headers = new HttpHeaders();
 			HttpEntity<EmpleadoTrabajo> requestEntity = new HttpEntity<>(request, headers);
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
		}
		 

		return responseResult;

		 
	}
	
	
	public ResponseResultado addEmpleadoSalud(EmpleadoSalud request) {
		 
		ResponseResultado responseResult = new ResponseResultado();
		
		try {

			String url = this.hostRecursosHumanos + "/empleado/add/salud";
			HttpHeaders headers = new HttpHeaders();
 			HttpEntity<EmpleadoSalud> requestEntity = new HttpEntity<>(request, headers);
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
		}
		 

		return responseResult;

		 
	}
	
	public ResponseResultado addEmpleadoCargo(EmpleadoCargo request) {
		 
		ResponseResultado responseResult = new ResponseResultado();
		try {

			String url = this.hostRecursosHumanos + "/empleado/add/cargo";
			HttpHeaders headers = new HttpHeaders();
 			HttpEntity<EmpleadoCargo> requestEntity = new HttpEntity<>(request, headers);
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
		}
		 

		return responseResult;

		 
	}
	
	
	public ResponseResultado addEmpleadoBanco(EmpleadoBanco request) {
		 
		ResponseResultado responseResult = new ResponseResultado();
		try {

			String url = this.hostRecursosHumanos + "/empleado/add/banco";
			HttpHeaders headers = new HttpHeaders();
 			HttpEntity<EmpleadoBanco> requestEntity = new HttpEntity<>(request, headers);
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
		}
		 

		return responseResult;

		 
	}
	
	public ResponseResultado eliminarEmpleado(String id) {
		 
		ResponseResultado responseResult = new ResponseResultado();
		try {

			String url = this.hostRecursosHumanos + "/empleado/delete/empleado?id=" + id;
			 
			//URI uri = new URI(url);
			ResponseEntity<String> response = restTemplate.exchange(url , HttpMethod.DELETE, null,String.class);

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
			 
		} 

		return responseResult;
		 

	}
	
	public ResponseResultado eliminarEmpleadoTitulo(String id) {
		 
		ResponseResultado responseResult = new ResponseResultado();
		
		try {

			String url = this.hostRecursosHumanos + "/empleado/delete/titulo?id=" + id;
			 
			//URI uri = new URI(url);
			ResponseEntity<String> response = restTemplate.exchange(url , HttpMethod.DELETE, null,String.class);

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
			 
		} 

		return responseResult;

	 

	}
	
	
	public ResponseResultado eliminarEmpleadoTrabajo(String id) {
		 
		ResponseResultado responseResult = new ResponseResultado();
		try {

			String url = this.hostRecursosHumanos + "/empleado/delete/trabajo?id=" + id;
			 
			//URI uri = new URI(url);
			ResponseEntity<String> response = restTemplate.exchange(url , HttpMethod.DELETE, null,String.class);

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
			 
		}
		 

		return responseResult;

		 

	}
	
	public ResponseResultado eliminarEmpleadoSalud(String id) {
		 
		ResponseResultado responseResult = new ResponseResultado();
		
		try {

			String url = this.hostRecursosHumanos + "/empleado/delete/salud?id=" + id;
			 
			//URI uri = new URI(url);
			ResponseEntity<String> response = restTemplate.exchange(url , HttpMethod.DELETE, null,String.class);

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
			 
		} 
		 

		return responseResult;

		 

	}
	
	public ResponseResultado eliminarEmpleadoBanco(String id) {
		 
		ResponseResultado responseResult = new ResponseResultado();
		
		try {

			String url = this.hostRecursosHumanos + "/empleado/delete/banco?id=" + id;
			 
			//URI uri = new URI(url);
			ResponseEntity<String> response = restTemplate.exchange(url , HttpMethod.DELETE, null,String.class);

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
			 
		} 

		return responseResult;

		 

	}
	
	public ResponseResultado eliminarEmpleadoCargo(String id) {
		 
		ResponseResultado responseResult = new ResponseResultado();
		try {

			String url = this.hostRecursosHumanos + "/empleado/delete/cargo?id=" + id;
			 
			//URI uri = new URI(url);
			ResponseEntity<String> response = restTemplate.exchange(url , HttpMethod.DELETE, null,String.class);

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
			 
		} 
		 

		return responseResult;
		 

	} 
	
}
