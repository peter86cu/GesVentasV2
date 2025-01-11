package com.ayalait.gesventas.service;

import com.ayalait.gesventas.controller.LoginController;
import com.ayalait.gesventas.request.RequestAddProducto;
import com.ayalait.gesventas.request.RequestCrearOrdenCompra;
import com.ayalait.gesventas.request.RequestFacturaCompra;
import com.ayalait.gesventas.request.RequestGuardarDetalleFC;
import com.ayalait.gesventas.request.RequestGuardarDetalleOC;
import com.ayalait.gesventas.request.RequestGuardarModifOC;
import com.ayalait.modelo.*;
import com.ayalait.response.*;
import com.ayalait.utils.Email;
import com.ayalait.utils.ErrorState;
import com.ayalait.utils.MarcaEmpleadoProcess;
import com.ayalait.utils.MessageCodeImpl;
import com.ayalait.utils.OCAprobadas;
import com.ayalait.utils.ResponsePrefactura;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.multishop.modelo.Prefactura;
import com.multishop.modelo.PrefacturaDetalle;
import com.multishop.modelo.PrefacturaModificaciones;

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


public final class wsStock {

	public String hostStock;
	private String hostTerminal;
	private String hostMail;
	private String hostCotizacion;
	
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
				this.hostStock = p.getProperty("server.stock");
				this.hostTerminal = p.getProperty("server.terminal");
				this.hostMail= p.getProperty("server.mail");
				this.hostCotizacion= p.getProperty("server.cotizaciones");
			}
		} catch (FileNotFoundException var3) {
			Logger.getLogger(wsStock.class.getName()).log(Level.SEVERE, (String) null, var3);
		}

	}

	public wsStock() {
		try {
			if(LoginController.desarrollo){
				hostStock = "http://localhost:8082";
				hostTerminal= "http://localhost:8087";
				hostMail="http://localhost:7002";
				hostCotizacion="http://localhost:7006";
			}else{
				cargarServer();
			}
		} catch (IOException var2) {
			Logger.getLogger(wsStock.class.getName()).log(Level.SEVERE, (String) null, var2);
		}

	}

	public ResponseResultado validarConectividadServidor() {
		 
		ResponseResultado responseResult = new ResponseResultado();

		try {

			String url = this.hostStock + "/server";
			 
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
	
	
	public ResponseResultado validarConectividadServidorTerminal() {
		 
		ResponseResultado responseResult = new ResponseResultado();
		try {

			String url =this.hostTerminal + "/server";
			 
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
		}
		 

		return responseResult;
		 
	}

	public ResponseListaProductos consultarListaProductos() {
		 
		ResponseListaProductos responseResult = new ResponseListaProductos();
		try {

			String url = this.hostStock + "/productos/lista";
			 
			URI uri = new URI(url);
			ResponseEntity<List<Producto>> response = restTemplate.exchange(uri , HttpMethod.GET, null,
					new ParameterizedTypeReference<List<Producto>>() {
					});

			if (response.getStatusCodeValue() == 200) {

				responseResult.setStatus(true);
				responseResult.setProductos(response.getBody());
 
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

	public ResponseResultado obtenerNumeroOrden(String fecha, String idusuario) {
		 
		ResponseResultado responseResult = new ResponseResultado();
		
		try {

			String url = this.hostStock + "/orden/compras/numero-orden?fecha=" + fecha + "&idusuario=" + idusuario;
			 
			//URI uri = new URI(url);
			ResponseEntity<String> response = restTemplate.exchange(url , HttpMethod.GET, null,String.class);

			if (response.getStatusCodeValue() == 200) {
				responseResult.setCode(200);
				responseResult.setStatus(true);
				responseResult.setResultado(response.getBody());
 
			}

		} catch (org.springframework.web.client.HttpServerErrorException e) {
			ErrorState data = new ErrorState();
			data.setCode(e.getStatusCode().value());
			data.setMenssage(e.getMessage());
			responseResult.setCode(data.getCode());
			responseResult.setError(data);
			 
		}/* catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		 

		return responseResult;

		 

	}
	
	public ResponseResultado obtenerNumeroPrefactura(String fecha, String idusuario) {
		 
		ResponseResultado responseResult = new ResponseResultado();
		
		try {

			String url = this.hostStock + "/prefactura/numero-orden?fecha=" + fecha + "&idusuario='" + idusuario+"'";
			 
			//URI uri = new URI(url);
			ResponseEntity<String> response = restTemplate.exchange(url , HttpMethod.GET, null,String.class);

			if (response.getStatusCodeValue() == 200) {
				responseResult.setCode(200);
				responseResult.setStatus(true);
				responseResult.setResultado(response.getBody());
 
			}

		} catch (org.springframework.web.client.HttpServerErrorException e) {
			ErrorState data = new ErrorState();
			data.setCode(e.getStatusCode().value());
			data.setMenssage(e.getMessage());
			responseResult.setCode(data.getCode());
			responseResult.setError(data);
			 
		}/* catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		 

		return responseResult;

		 

	}

	public ResponseResultado obtenerNumeroFactura(String fecha, String idusuario) {
		 
		ResponseResultado responseResult = new ResponseResultado();
		try {

			String url = this.hostStock + "/factura/numero-factura?fecha=" + fecha + "&idusuario=" + idusuario;
			 
			//URI uri = new URI(url);
			ResponseEntity<String> response = restTemplate.exchange(url , HttpMethod.GET, null,String.class);

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
			 
		}
		 

		return responseResult;

		 

	}

	public ResponseResultado eliminarItemOC(int id) {
		 
		ResponseResultado responseResult = new ResponseResultado();
		try {

			String url = this.hostStock + "/orden/compras/delete?id=" + id;
			 
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
			 
		} 		 

		return responseResult;

		 

	}
	
	public ResponseResultado eliminarItemPrefactura(int id) {
		 
		ResponseResultado responseResult = new ResponseResultado();
		try {

			String url = this.hostStock + "/prefactura/delete?id=" + id;
			 
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
			 
		} 		 

		return responseResult;

		 

	}

	public ResponseOrdenCompra obtenerOrdenCompraPorID(int id) {
		 
		ResponseOrdenCompra responseResult = new ResponseOrdenCompra();
		
		try {

			String url = this.hostStock + "/orden/compras/id?id=" + id;
			 
			//URI uri = new URI(url);
			ResponseEntity<OrdenCompra> response = restTemplate.exchange(url , HttpMethod.GET, null,OrdenCompra.class);

			if (response.getStatusCodeValue() == 200) {
				responseResult.setCode(response.getStatusCodeValue());
				responseResult.setStatus(true);
				responseResult.setOrdenCompra(response.getBody());
 
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
	
	
	
	public ResponsePrefactClient obtenerPrefacturaPorID(String id) {
		 
		ResponsePrefactClient responseResult = new ResponsePrefactClient();
		
		try {

			String url = this.hostStock + "/prefactura/id?id=" + id;
			 
			//URI uri = new URI(url);
			ResponseEntity<Prefactura> response = restTemplate.exchange(url , HttpMethod.GET, null,Prefactura.class);

			if (response.getStatusCodeValue() == 200) {
				responseResult.setCode(response.getStatusCodeValue());
				responseResult.setStatus(true);
				responseResult.setPrefactura(response.getBody());
 
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
			data.setMenssage(myJson.get("menssage").getAsString());			
			responseResult.setError(data);
		}

		return responseResult;

		 
	}
	

	public ResponseFacturaCompra obtenerFacturaCompraPorID(int id) {
		 
		ResponseFacturaCompra responseResult = new ResponseFacturaCompra();
		try {

			String url = this.hostStock + "/factura/compra/id?id=" + id;
			 
			//URI uri = new URI(url);
			ResponseEntity<FacturaCompra> response = restTemplate.exchange(url , HttpMethod.GET, null,FacturaCompra.class);

			if (response.getStatusCodeValue() == 200) {
				responseResult.setCode(200);;
				responseResult.setStatus(true);
				responseResult.setFacturaCompra(response.getBody());
 
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
	
	
	
	public ResponseCliente obtenerClientePorId(String id) {
		 
		ResponseCliente responseResult = new ResponseCliente();
		try {

			String url = this.hostStock + "/prefactura/cliente?id=" + id;
			 
			//URI uri = new URI(url);
			ResponseEntity<Cliente> response = restTemplate.exchange(url , HttpMethod.POST, null,Cliente.class);

			if (response.getStatusCodeValue() == 200) {
				responseResult.setCode(200);;
				responseResult.setStatus(true);
				responseResult.setCliente(response.getBody());
 
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
			data.setMenssage(myJson.get("menssage").getAsString());			
			responseResult.setError(data);
		}		 

		return responseResult;

		 
	}

	public ResponseVenta obtenerVentaPorId(int id) {
		 
		ResponseVenta responseResult = new ResponseVenta();
		try {

			String url = this.hostTerminal + "/venta?id=" + id;
			 
			URI uri = new URI(url);
			ResponseEntity<Ventas> response = restTemplate.exchange(uri , HttpMethod.GET, null,Ventas.class);

			if (response.getStatusCodeValue() == 200) {

				responseResult.setStatus(true);
				responseResult.setVenta(response.getBody());
 
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

	public ResponseResultado addProducto(RequestAddProducto request) {
		 
		ResponseResultado responseResult = new ResponseResultado();
		try {

			String url = this.hostStock + "/productos/add";
			HttpHeaders headers = new HttpHeaders();
 			HttpEntity<RequestAddProducto> requestEntity = new HttpEntity<>(request, headers);
			URI uri = new URI(url);
			ResponseEntity<String> response = restTemplate.exchange(uri , HttpMethod.POST, requestEntity,String.class);

			if (response.getStatusCodeValue() == 200) {
			    responseResult.setCode(200);
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

	public ResponseResultado eliminarProducto(String id) {
		 
		ResponseResultado responseResult = new ResponseResultado();
		
		try {

			String url = this.hostStock + "/productos/delete?id=" + id;
			 
			URI uri = new URI(url);
			ResponseEntity<String> response = restTemplate.exchange(uri , HttpMethod.DELETE, null,String.class);

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

	public ResponseResultado addVenta(Ventas request) {
		 
		ResponseResultado responseResult = new ResponseResultado();
		
		try {

			String url = this.hostTerminal + "/ventas/add";
			HttpHeaders headers = new HttpHeaders();
 			HttpEntity<Ventas> requestEntity = new HttpEntity<>(request, headers);
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

	public ResponseResultado crearFacturaCompra(RequestFacturaCompra request) {
		 
		ResponseResultado responseResult = new ResponseResultado();
		try {

			String url = this.hostStock + "/factura/add";
			HttpHeaders headers = new HttpHeaders();
 			HttpEntity<RequestFacturaCompra> requestEntity = new HttpEntity<>(request, headers);
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

	public ResponseResultado guardarVentaCobro(VentasCobro request) {
		 
		ResponseResultado responseResult = new ResponseResultado();
		
		try {

			String url = this.hostTerminal + "/ventas/cobro/add";
			HttpHeaders headers = new HttpHeaders();
 			HttpEntity<VentasCobro> requestEntity = new HttpEntity<>(request, headers);
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

	public ResponseResultado addModificacionOC(RequestGuardarModifOC request) {
		 
		ResponseResultado responseResult = new ResponseResultado();
		
		try {

			String url = this.hostStock + "/orden/compras/modificaciones";
			HttpHeaders headers = new HttpHeaders();
 			HttpEntity<RequestGuardarModifOC> requestEntity = new HttpEntity<>(request, headers);
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
	
	public ResponseResultado addModificacionPrefactura(PrefacturaModificaciones request) {
		 
		ResponseResultado responseResult = new ResponseResultado();
		
		try {

			String url = this.hostStock + "/prefactura/modificaciones";
			HttpHeaders headers = new HttpHeaders();
 			HttpEntity<PrefacturaModificaciones> requestEntity = new HttpEntity<>(request, headers);
			URI uri = new URI(url);
			ResponseEntity<String> response = restTemplate.exchange(uri , HttpMethod.POST, requestEntity,String.class);

			if (response.getStatusCodeValue() == 200) {
				responseResult.setCode(200);
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

	public ResponseResultado addDetalleOC(RequestGuardarDetalleOC request) {
		 
		ResponseResultado responseResult = new ResponseResultado();
		
		try {

			String url = this.hostStock + "/orden/compras/detalle";
			HttpHeaders headers = new HttpHeaders();
 			HttpEntity<RequestGuardarDetalleOC> requestEntity = new HttpEntity<>(request, headers);
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
	
	public ResponseResultado addDetallePrefactura(PrefacturaDetalle request) {
		 
		ResponseResultado responseResult = new ResponseResultado();
		
		try {

			String url = this.hostStock + "/prefactura/detalle";
			HttpHeaders headers = new HttpHeaders();
 			HttpEntity<PrefacturaDetalle> requestEntity = new HttpEntity<>(request, headers);
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

	public ResponseResultado addDetalleFC(RequestGuardarDetalleFC request) {
		 
		ResponseResultado responseResult = new ResponseResultado();
		
		try {

			String url = this.hostStock + "/factura/detalle";
			HttpHeaders headers = new HttpHeaders();
 			HttpEntity<RequestGuardarDetalleFC> requestEntity = new HttpEntity<>(request, headers);
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

	public ResponseResultado crearOrdenCompra(RequestCrearOrdenCompra request) {
		 
		ResponseResultado responseResult = new ResponseResultado();
		
		try {

			String url = this.hostStock + "/orden/compras/add";
			HttpHeaders headers = new HttpHeaders();
 			HttpEntity<RequestCrearOrdenCompra> requestEntity = new HttpEntity<>(request, headers);
			URI uri = new URI(url);
			ResponseEntity<String> response = restTemplate.exchange(uri , HttpMethod.POST, requestEntity,String.class);

			if (response.getStatusCodeValue() == 200) {
				responseResult.setCode(response.getStatusCodeValue() );
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
	
	public ResponseResultado crearPrefactura(Prefactura request) {
		 
		ResponseResultado responseResult = new ResponseResultado();
		
		try {

			String url = this.hostStock + "/prefactura/add";
			HttpHeaders headers = new HttpHeaders();
 			HttpEntity<Prefactura> requestEntity = new HttpEntity<>(request, headers);
			URI uri = new URI(url);
			ResponseEntity<String> response = restTemplate.exchange(uri , HttpMethod.POST, requestEntity,String.class);

			if (response.getStatusCodeValue() == 200) {
				responseResult.setCode(200);
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
			data.setMenssage(myJson.get("menssage").getAsString());			
			responseResult.setError(data);
		}
		 

		return responseResult;

		 
	}
	
	
	public ResponseMofPorIdPrefactura obtenerModificacionPorIdPrefactura(String id) {
		 
		ResponseMofPorIdPrefactura responseResult = new ResponseMofPorIdPrefactura();
		
		try {

			String url = this.hostStock + "/prefactura/modificaciones/obtener?id="+id;
			URI uri = new URI(url);
			ResponseEntity<PrefacturaModificaciones> response = restTemplate.exchange(uri , HttpMethod.POST, null,PrefacturaModificaciones.class);

			if (response.getStatusCodeValue() == 200) {
				responseResult.setCode(200);
				responseResult.setStatus(true);
				responseResult.setModificaciones(response.getBody());
 
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
	
	public ResponseListaPrefactura listadoPrefactura() {
		 
		ResponseListaPrefactura responseResult = new ResponseListaPrefactura();
		
		try {

			String url = this.hostStock + "/prefactura/lista";
			URI uri = new URI(url);
			ResponseEntity<List<ResponsePrefactura>> response = restTemplate.exchange(uri , HttpMethod.POST, null,new ParameterizedTypeReference<List<ResponsePrefactura>>() {
			});

			if (response.getStatusCodeValue() == 200) {
				responseResult.setCode(200);
				responseResult.setStatus(true);
				responseResult.setLstPrefacturas(response.getBody());
 
			}

		} catch (org.springframework.web.client.HttpServerErrorException e) {
			JsonParser jsonParser = new JsonParser();
			int in = e.getLocalizedMessage().indexOf("{");
			int in2 = e.getLocalizedMessage().indexOf("}");
			String cadena = e.getMessage().substring(in, in2+1);
			JsonObject myJson = (JsonObject) jsonParser.parse(cadena);
			responseResult.setCode(myJson.get("code").getAsInt());
			ErrorState data = new ErrorState();
			data.setCode(myJson.get("code").getAsInt());
			data.setMenssage(myJson.get("menssage").getAsString() );
			responseResult.setCode(data.getCode());
			responseResult.setError(data);
			responseResult.setStatus(false);
			
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
			responseResult.setCode(data.getCode());
			responseResult.setError(data);
			responseResult.setStatus(false);
			return responseResult;	
		}catch(org.springframework.web.client.ResourceAccessException ex){
			
			responseResult.setCode(505);
			ErrorState data = new ErrorState();
			data.setCode(505);
			data.setMenssage(ex.getMessage());
			responseResult.setCode(data.getCode());
			responseResult.setError(data);
			responseResult.setStatus(false);
			return responseResult;	
		}
		 

		return responseResult;

		 
	}
	
	
	public ResponseListaPrefAprobadas listadoPrefacturaAprobadas() {
		 
		ResponseListaPrefAprobadas responseResult = new ResponseListaPrefAprobadas();
		
		try {

			String url = this.hostStock + "/prefactura/aprobadas";
			URI uri = new URI(url);
			ResponseEntity<List<OCAprobadas>> response = restTemplate.exchange(uri , HttpMethod.POST, null,new ParameterizedTypeReference<List<OCAprobadas>>() {
			});

			if (response.getStatusCodeValue() == 200) {
				responseResult.setCode(200);
				responseResult.setStatus(true);
				responseResult.setPrefacAprobadas(response.getBody());
 
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

	public ResponseProducto obtenerProductoPorId(String id) {
		 
		ResponseProducto responseResult = new ResponseProducto();
		
		try {

			String url = this.hostStock + "/productos/busqueda?id=" + id;
			
			URI uri = new URI(url);
			ResponseEntity<Producto> response = restTemplate.exchange(uri , HttpMethod.GET, null,Producto.class);

			if (response.getStatusCodeValue() == 200) {
				responseResult.setCode(response.getStatusCodeValue());
				responseResult.setStatus(true);
				responseResult.setProducto(response.getBody());

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

	public ResponseResultado addDetalleVenta(VentasDetalle request) {
		 
		ResponseResultado responseResult = new ResponseResultado();
		
		try {

			String url = this.hostTerminal + "/ventas/detalles/add";
			HttpHeaders headers = new HttpHeaders();
 			HttpEntity<VentasDetalle> requestEntity = new HttpEntity<>(request, headers);
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

	public ResponseResultado limpiarCacheParametros() {
		 
		ResponseResultado responseResult = new ResponseResultado();
		
		try {

			String url = this.hostStock + "/clearCache";
			 
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
	
	
	
	
	public ResponseResultado enviarMailConfirmacion(Email email) {
		ResponseResultado responseResult = new ResponseResultado();

		try {
			HttpHeaders headers = new HttpHeaders();
			String url = this.hostMail + "/sendMailBody";
			URI uri = new URI(url);
	
			HttpEntity<Email> requestEntity = new HttpEntity<>(email, headers);
		
			ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, requestEntity,
					String.class);

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
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return responseResult;
	}
	
	
	public ResponseResultado cargarPrefacturaWEB() {
		 
		ResponseResultado responseResult = new ResponseResultado();
		try {

			String url = this.hostCotizacion + "/cotizacion/pendientes";
			 
			//URI uri = new URI(url);
			ResponseEntity<String> response = restTemplate.exchange(url , HttpMethod.GET, null,String.class);

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
		} catch(org.springframework.web.client.ResourceAccessException ex){
			
			responseResult.setCode(505);
			ErrorState data = new ErrorState();
			data.setCode(505);
			data.setMenssage(ex.getMessage());
			responseResult.setCode(data.getCode());
			responseResult.setError(data);
			responseResult.setStatus(false);
			return responseResult;	
		}
		 

		return responseResult;

		 

	}
}