package com.ayalait.gesventas.service;

import com.ayalait.fecha.FormatearFechas;
import com.ayalait.gesventas.controller.LoginController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import com.ayalait.modelo.*;
import com.ayalait.response.*;

import com.ayalait.logguerclass.Notification;
import com.ayalait.utils.ErrorState;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

public final class wsParametros {

	private String hostParametros;
	private String hostStock;
	private String hostTerminal;
    private String hostRecursosHumanos;
	private String hostSeguridad;
	public static String logger;

	ObjectWriter ow = (new ObjectMapper()).writer().withDefaultPrettyPrinter();

	@Autowired
	RestTemplate restTemplate= new RestTemplate();
	

	public String getHostStock() {
		return hostStock;
	}

	public void setHostStock(String hostStock) {
		this.hostStock = hostStock;
	}

	public String getHostTerminal() {
		return hostTerminal;
	}

	public void setHostTerminal(String hostTerminal) {
		this.hostTerminal = hostTerminal;
	}

	public String getHostRecursosHumanos() {
		return hostRecursosHumanos;
	}

	public void setHostRecursosHumanos(String hostRecursosHumanos) {
		this.hostRecursosHumanos = hostRecursosHumanos;
	}

	public String getHostSeguridad() {
		return hostSeguridad;
	}

	public void setHostSeguridad(String hostSeguridad) {
		this.hostSeguridad = hostSeguridad;
	}

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
				this.hostParametros = p.getProperty("server.parametros");
				this.hostStock = p.getProperty("server.stock");
				this.hostTerminal = p.getProperty("server.terminal");
				this.hostSeguridad= p.getProperty("server.seguridad");
				this.hostRecursosHumanos=p.getProperty("server.rrhh");
				this.logger = p.getProperty("server.logger");
			}
		} catch (FileNotFoundException var3) {
			Logger.getLogger(wsParametros.class.getName()).log(Level.SEVERE, (String) null, var3);
		}

	}

	public wsParametros() {
		try {
			if(LoginController.desarrollo){
				hostStock = "http://localhost:8082";
				hostTerminal = "http://localhost:8087";
				hostSeguridad="http://localhost:7000";
				hostRecursosHumanos="http://localhost:8085";
				logger = "http://localhost:8086";
			}else{
				cargarServer();
			}
		} catch (IOException var2) {
			Logger.getLogger(wsParametros.class.getName()).log(Level.SEVERE, (String) null, var2);
		}

	}
	
	
	
	public ResponseResultado reiniciarServidor(String servidor) {
		
		ResponseResultado responseResult = new ResponseResultado();
		Notification noti = new Notification();

		try {

			String url = servidor + "/restart";
			noti.setFecha_inicio(FormatearFechas.obtenerFechaPorFormato("yyyy-MM-dd hh:mm:ss"));
			noti.setClass_id("GesVentas-APP");
			noti.setAccion("listarUM");
			noti.setId(UUID.randomUUID().toString());
			URI uri = new URI(url);
			ResponseEntity<String> response = restTemplate.exchange(uri , HttpMethod.GET, null,String.class);

			if (response.getStatusCodeValue() == 200) {

				responseResult.setStatus(true);
				responseResult.setResultado(response.getBody());
				noti.setResponse(ow.writeValueAsString(responseResult));

			}

		} catch (org.springframework.web.client.HttpServerErrorException e) {
			ErrorState data = new ErrorState();
			data.setCode(e.getStatusCode().value());
			data.setMenssage(e.getMessage());
			responseResult.setCode(data.getCode());
			responseResult.setError(data);
			try {
				noti.setResponse(ow.writeValueAsString(responseResult));
			} catch (JsonProcessingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		noti.setFecha_fin(FormatearFechas.obtenerFechaPorFormato("yyyy-MM-dd hh:mm:ss"));
		ResponseResultado result = guardarLog(noti);
		if (!result.isStatus()) {
			System.err.println(result.getError().getCode() + " " + result.getError().getMenssage());
		}

		return responseResult;

	}
	
	
	public ResponseResultado stopServidor(String servidor) {
		
		ResponseResultado responseResult = new ResponseResultado();
		Notification noti = new Notification();

		try {

			String url = servidor + "/stop";
			noti.setFecha_inicio(FormatearFechas.obtenerFechaPorFormato("yyyy-MM-dd hh:mm:ss"));
			noti.setClass_id("GesVentas-APP");
			noti.setAccion("stopServidor");
			noti.setId(UUID.randomUUID().toString());
			URI uri = new URI(url);
			ResponseEntity<String> response = restTemplate.exchange(uri , HttpMethod.GET, null,String.class);

			if (response.getStatusCodeValue() == 200) {

				responseResult.setStatus(true);
				responseResult.setResultado(response.getBody());
				noti.setResponse(ow.writeValueAsString(responseResult));

			}

		} catch (org.springframework.web.client.HttpServerErrorException e) {
			ErrorState data = new ErrorState();
			data.setCode(e.getStatusCode().value());
			data.setMenssage(e.getMessage());
			responseResult.setCode(data.getCode());
			responseResult.setError(data);
			try {
				noti.setResponse(ow.writeValueAsString(responseResult));
			} catch (JsonProcessingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		noti.setFecha_fin(FormatearFechas.obtenerFechaPorFormato("yyyy-MM-dd hh:mm:ss"));
		ResponseResultado result = guardarLog(noti);
		if (!result.isStatus()) {
			System.err.println(result.getError().getCode() + " " + result.getError().getMenssage());
		}

		return responseResult;


	}
	


	
	public ResponseResultado guardarLog(Notification noti) {

		ResponseResultado responseResult = new ResponseResultado();
		try {
			HttpHeaders headers = new HttpHeaders();
			String url = this.logger + "/notification";
			URI uri = new URI(url);
			HttpEntity<Notification> requestEntity = new HttpEntity<>(noti, headers);
			ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, requestEntity, String.class);

			if (response.getStatusCodeValue() == 201) {
				responseResult.setCode(response.getStatusCodeValue());
				responseResult.setStatus(true);
				responseResult.setResultado(response.getBody());
				return responseResult;
			}

		} catch (org.springframework.web.client.HttpServerErrorException e) {

			ErrorState data = new ErrorState();
			data.setCode(e.getStatusCode().value());
			data.setMenssage(e.getMessage());
			responseResult.setCode(data.getCode());
			responseResult.setError(data);
			return responseResult;

		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return responseResult;

	}
	
	public ResponseUM listarUM() {
		
		ResponseUM responseResult = new ResponseUM();
 
		try {

			String url = this.hostStock + "/parametros/um";
			 
			URI uri = new URI(url);
			ResponseEntity<List<UnidadMedida>> response = restTemplate.exchange(uri , HttpMethod.GET, null,
					new ParameterizedTypeReference<List<UnidadMedida>>() {
					});

			if (response.getStatusCodeValue() == 200) {

				responseResult.setStatus(true);
				responseResult.setUm(response.getBody());
 
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
	
	public ResponseListaEstadoProcesos listarEstadoProcesos() {
		
		ResponseListaEstadoProcesos responseResult = new ResponseListaEstadoProcesos();

		try {

			String url = this.hostSeguridad + "/proceso/estados-procesos";
			
			URI uri = new URI(url);
			ResponseEntity<List<EstadoProceso>> response = restTemplate.exchange(uri , HttpMethod.GET, null,
					new ParameterizedTypeReference<List<EstadoProceso>>() {
					});

			if (response.getStatusCodeValue() == 200) {

				responseResult.setStatus(true);
				responseResult.setEstado(response.getBody());

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
	
	public ResponseMonedas listarMonedas() {
		
		ResponseMonedas responseResult = new ResponseMonedas();

		try {

			String url = this.hostSeguridad + "/parametros/monedas";			
			URI uri = new URI(url);
			ResponseEntity<List<Moneda>> response = restTemplate.exchange(uri , HttpMethod.GET, null,
					new ParameterizedTypeReference<List<Moneda>>() {
					});

			if (response.getStatusCodeValue() == 200) {
				responseResult.setCode(response.getStatusCodeValue());
				responseResult.setStatus(true);
				responseResult.setMonedas(response.getBody());
 
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
	
	
	public ResponseListaPaises listadoPaises() {
		
		ResponseListaPaises responseResult = new ResponseListaPaises();
 
		try {

			String url = this.hostRecursosHumanos + "/parametros/paises";
 			 
			URI uri = new URI(url);
			ResponseEntity<List<Paises>> response = restTemplate.exchange(uri , HttpMethod.GET, null,
					new ParameterizedTypeReference<List<Paises>>() {
					});

			if (response.getStatusCodeValue() == 200) {

				responseResult.setStatus(true);
				responseResult.setPaises(response.getBody());
 
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
	
	
	public ResponseListaParentesco listadoParentescos() {
		

		ResponseListaParentesco responseResult = new ResponseListaParentesco();
 
		try {

			String url = this.hostRecursosHumanos + "/parametros/parentescos";
			 
			URI uri = new URI(url);
			ResponseEntity<List<Parentesco>> response = restTemplate.exchange(uri , HttpMethod.GET, null,
					new ParameterizedTypeReference<List<Parentesco>>() {
					});

			if (response.getStatusCodeValue() == 200) {

				responseResult.setStatus(true);
				responseResult.setParentesco(response.getBody());
 
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
	
	
	public ResponseListaMarcasProducto listadoMarcasProducto() {
		ResponseListaMarcasProducto responseResult = new ResponseListaMarcasProducto();

 
		try {

			String url = this.hostStock + "/parametros/marcas";			 
			URI uri = new URI(url);
			 
			ResponseEntity<List<MarcaProducto>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
					new ParameterizedTypeReference<List<MarcaProducto>>() {
					});

			if (response.getStatusCodeValue() == 200) {

				responseResult.setStatus(true);
				responseResult.setMarcas(response.getBody());
 
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

	
	public ResponseListaModeloProducto listadoModelosProducto() {
		ResponseListaModeloProducto responseResult = new ResponseListaModeloProducto();

 
		try {

			String url = this.hostStock + "/parametros/modelos";		 
			URI uri = new URI(url);
			 
			ResponseEntity<List<ModeloProducto>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
					new ParameterizedTypeReference<List<ModeloProducto>>() {
					});

			if (response.getStatusCodeValue() == 200) {

				responseResult.setStatus(true);
				responseResult.setModelo(response.getBody());
 
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


	public ResponseListaEstadosUsuarios listadoEstadosUsuarios() {
		 
		ResponseListaEstadosUsuarios responseResult = new ResponseListaEstadosUsuarios();
		
		try {

			String url = this.hostSeguridad + "/parametros/estados-usuarios";		 
			URI uri = new URI(url);
			 
			ResponseEntity<List<EstadoUsuarios>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
					new ParameterizedTypeReference<List<EstadoUsuarios>>() {
					});

			if (response.getStatusCodeValue() == 200) {
				responseResult.setCode(response.getStatusCodeValue());
				responseResult.setStatus(true);
				responseResult.setEstados(response.getBody());
 
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

	public ResponseCliente obtenerClientePorCI(String ci) {
		 
		ResponseCliente responseResult = new ResponseCliente();
 		
		try {

			String url = this.hostStock + "/parametros/clientes?ci=" + ci;		 
			URI uri = new URI(url);
			 
			ResponseEntity<Cliente> response = restTemplate.exchange(uri, HttpMethod.GET, null,Cliente.class);

			if (response.getStatusCodeValue() == 200) {

				responseResult.setStatus(true);
				responseResult.setCliente(response.getBody());
 
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

	public ResponseListaConfiguraciones cargarConfiguraciones() {
		 
		ResponseListaConfiguraciones responseResult = new ResponseListaConfiguraciones();
		try {

			String url = this.hostTerminal + "/parametros/configuraciones";		 
			URI uri = new URI(url);
			 
			ResponseEntity<List<Configuraciones>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
					new ParameterizedTypeReference<List<Configuraciones>>() {
					});

			if (response.getStatusCodeValue() == 200) {

				responseResult.setStatus(true);
				responseResult.setConfiguraciones(response.getBody());
 
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

	public ResponseListaFormasCobro listaFormasCobros() {
		 
		ResponseListaFormasCobro responseResult = new ResponseListaFormasCobro();
		
		try {

			String url = this.hostTerminal + "/parametros/formas-cobros";		 
			URI uri = new URI(url);
			 
			ResponseEntity<List<FormasCobro>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
					new ParameterizedTypeReference<List<FormasCobro>>() {
					});

			if (response.getStatusCodeValue() == 200) {

				responseResult.setStatus(true);
				responseResult.setFormasCobro(response.getBody());
 
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

	public ResponseListaTipoArqueo listarTipoArqueo() {
		 
		ResponseListaTipoArqueo responseResult = new ResponseListaTipoArqueo();
		try {

			String url = this.hostTerminal + "/parametros/tipoarqueo";		 
			URI uri = new URI(url);
			 
			ResponseEntity<List<TipoArqueo>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
					new ParameterizedTypeReference<List<TipoArqueo>>() {
					});

			if (response.getStatusCodeValue() == 200) {

				responseResult.setStatus(true);
				responseResult.setTipoArqueo(response.getBody());
 
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

	public ResponseListaBilletes listarBilletes() {
		 
		ResponseListaBilletes responseResult = new ResponseListaBilletes();
		try {

			String url = this.hostTerminal + "/parametros/billetes";		 
			URI uri = new URI(url);
			 
			ResponseEntity<List<Billetes>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
					new ParameterizedTypeReference<List<Billetes>>() {
					});

			if (response.getStatusCodeValue() == 200) {

				responseResult.setStatus(true);
				responseResult.setBilletes(response.getBody());
 
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

	public ResponseProveedor obtenerProveedorPorId(int id) {		 

		ResponseProveedor responseResult = new ResponseProveedor();
		try {

			String url = this.hostStock + "/parametros/proveedor/?id=" + id;		 
			URI uri = new URI(url);
			 
			ResponseEntity<Proveedor> response = restTemplate.exchange(uri, HttpMethod.GET, null,Proveedor.class);

			if (response.getStatusCodeValue() == 200) {

				responseResult.setStatus(true);
				responseResult.setProveedor(response.getBody());
 
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
	
	public ResponseCliente obtenerClientePorId(String id) {		 

		ResponseCliente responseResult = new ResponseCliente();
		try {

			String url = this.hostStock + "/parametros/clientes/id?id=" + id;		 
			URI uri = new URI(url);
			 
			ResponseEntity<Cliente> response = restTemplate.exchange(uri, HttpMethod.GET, null,Cliente.class);

			if (response.getStatusCodeValue() == 200) {
				responseResult.setCode(200);
				responseResult.setStatus(true);
				responseResult.setCliente(response.getBody());
 
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

	 public ResponseResultado addLogAdmin(AdministracionLog request) {
		 
		ResponseResultado responseResult = new ResponseResultado();
		 try {

				String url = this.hostSeguridad + "/log/add";
				HttpHeaders headers = new HttpHeaders();
 
	 			HttpEntity<AdministracionLog> requestEntity = new HttpEntity<>(request, headers);
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

	public ResponseResultado monedaPorDefecto() {
		 
		ResponseResultado responseResult = new ResponseResultado();
		try {

			String url = this.hostStock + "/parametros/moneda/defecto";		 
			URI uri = new URI(url);
			 
			ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, null,String.class);

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

	public ResponseCaja datosCaja(String ip) {
		 
		ResponseCaja responseResult = new ResponseCaja();
		try {

			String url = this.hostTerminal + "/parametros/caja?ip=" + ip;		 
			URI uri = new URI(url);
			 
			ResponseEntity<Caja> response = restTemplate.exchange(uri, HttpMethod.GET, null,Caja.class);

			if (response.getStatusCodeValue() == 200) {
				responseResult.setCode(response.getStatusCodeValue());
				responseResult.setStatus(true);
				responseResult.setCaja(response.getBody());
 
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

	public ResponseTipoProducto listarTiposProductos() {
		
		ResponseTipoProducto responseResult = new ResponseTipoProducto();
		
		try {

			String url = this.hostStock + "/parametros/tipoproducto";		 
			URI uri = new URI(url);
			 
			ResponseEntity<List<TipoProducto>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
					new ParameterizedTypeReference<List<TipoProducto>>() {
					});

			if (response.getStatusCodeValue() == 200) {

				responseResult.setStatus(true);
				responseResult.setTipoProductos(response.getBody());
 
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

	public ResponseListaTurnos listarTurnos() {
		 
		ResponseListaTurnos responseResult = new ResponseListaTurnos();
		try {

			String url = this.hostTerminal + "/parametros/turno";		 
			URI uri = new URI(url);
			 
			ResponseEntity<List<Turnos>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
					new ParameterizedTypeReference<List<Turnos>>() {
					});

			if (response.getStatusCodeValue() == 200) {

				responseResult.setStatus(true);
				responseResult.setTurnos(response.getBody());
 
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

	public ResponseListaTipoCaudre listarTipoCuadre() {
		 
		ResponseListaTipoCaudre responseResult = new ResponseListaTipoCaudre();
		try {

			String url = this.hostTerminal + "/parametros/tipocuadre";		 
			URI uri = new URI(url);
			 
			ResponseEntity<List<TipoCuadre>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
					new ParameterizedTypeReference<List<TipoCuadre>>() {
					});

			if (response.getStatusCodeValue() == 200) {

				responseResult.setStatus(true);
				responseResult.setTipoCaudre(response.getBody());
 
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

	public ResponseListaCajas listarCajas() {
		 
		ResponseListaCajas responseResult = new ResponseListaCajas();
		try {

			String url = this.hostTerminal + "/parametros/cajas";		 
			URI uri = new URI(url);
			 
			ResponseEntity<List<Caja>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
					new ParameterizedTypeReference<List<Caja>>() {
					});

			if (response.getStatusCodeValue() == 200) {

				responseResult.setStatus(true);
				responseResult.setCaja(response.getBody());
 
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

	public ResponseDepositos listarDepositos() {
		 
		ResponseDepositos responseResult = new ResponseDepositos();
		try {

			String url = this.hostStock + "/parametros/depositos";		 
			URI uri = new URI(url);
			 
			ResponseEntity<List<Depositos>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
					new ParameterizedTypeReference<List<Depositos>>() {
					});

			if (response.getStatusCodeValue() == 200) {

				responseResult.setStatus(true);
				responseResult.setDepositos(response.getBody());
 
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

	public ResponseFormasPagos listarFormasPagos() {
		 
		ResponseFormasPagos responseResult = new ResponseFormasPagos();
		try {

			String url = this.hostStock + "/parametros/formas-pagos";		 
			URI uri = new URI(url);
			 
			ResponseEntity<List<FormasPago>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
					new ParameterizedTypeReference<List<FormasPago>>() {
					});

			if (response.getStatusCodeValue() == 200) {

				responseResult.setStatus(true);
				responseResult.setFormasPagos(response.getBody());
 
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

	public ResponsePlazos listarPlazos() {
		 
		ResponsePlazos responseResult = new ResponsePlazos();
		try {

			String url = this.hostStock + "/parametros/plazos";		 
			URI uri = new URI(url);
			 
			ResponseEntity<List<Plazos>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
					new ParameterizedTypeReference<List<Plazos>>() {
					});

			if (response.getStatusCodeValue() == 200) {

				responseResult.setStatus(true);
				responseResult.setPlazos(response.getBody());
 
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

	public ResponseCategorias listarCategorias() {
		ResponseCategorias responseResult = new ResponseCategorias();
 
		try {

			String url = this.hostStock + "/parametros/categoria";
			URI uri = new URI(url);
			 
			ResponseEntity<List<Categoria>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
					new ParameterizedTypeReference<List<Categoria>>() {
					});

			if (response.getStatusCodeValue() == 200) {

				responseResult.setStatus(true);
				responseResult.setCategorias(response.getBody());
 
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

	public ResponseListaCargos listadoCargos(int idDepartamento) {
		 
		ResponseListaCargos responseResult = new ResponseListaCargos();
		
		try {

			String url = this.hostRecursosHumanos + "/parametros/cargos?id="+idDepartamento;		 
			URI uri = new URI(url);
			 
			ResponseEntity<List<Cargos>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
					new ParameterizedTypeReference<List<Cargos>>() {
					});

			if (response.getStatusCodeValue() == 200) {

				responseResult.setStatus(true);
				responseResult.setCargos(response.getBody());
 
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
	
	
	public ResponseListaDepartamentos listadoDepartamentos() {
		 
		ResponseListaDepartamentos responseResult = new ResponseListaDepartamentos();
		
		try {

			String url = this.hostRecursosHumanos + "/parametros/departamentos";		 
			URI uri = new URI(url);
			 
			ResponseEntity<List<Departamentos>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
					new ParameterizedTypeReference<List<Departamentos>>() {
					});

			if (response.getStatusCodeValue() == 200) {

				responseResult.setStatus(true);
				responseResult.setDepartamentos(response.getBody());
 
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

	public ResponseImpuestos listarImpuestos() {
		 
		ResponseImpuestos responseResult = new ResponseImpuestos();
		try {

			String url = this.hostStock + "/parametros/impuesto";		 
			URI uri = new URI(url);
			 
			ResponseEntity<List<Impuesto>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
					new ParameterizedTypeReference<List<Impuesto>>() {
					});

			if (response.getStatusCodeValue() == 200) {
				responseResult.setCode(response.getStatusCodeValue());
				responseResult.setStatus(true);
				responseResult.setImpuestos(response.getBody());
 
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

	public ResponseResultado limpiarCacheParametros() {
		 
		ResponseResultado responseResult = new ResponseResultado();
		try {

			String url = this.hostParametros + "/clearCache";		 
			URI uri = new URI(url);
			 
			ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, null,String.class);

			if (response.getStatusCodeValue() == 200) {

				responseResult.setStatus(true);
				responseResult.setResultado(url);
 
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

	
