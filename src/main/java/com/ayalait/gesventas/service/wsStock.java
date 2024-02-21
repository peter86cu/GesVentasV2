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


public final class wsStock {

	private String hostStock;
	private String hostTerminal;
	
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

	public ResponseResultado obtenerNumeroFactura(String fecha, String idusuario) {
		 
		ResponseResultado responseResult = new ResponseResultado();
		try {

			String url = this.hostStock + "/factura/numero-factura?fecha=" + fecha + "&idusuario=" + idusuario;
			 
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

	public ResponseResultado eliminarItemOC(int id) {
		 
		ResponseResultado responseResult = new ResponseResultado();
		try {

			String url = this.hostStock + "/orden/compras/delete?id=" + id;
			 
			URI uri = new URI(url);
			ResponseEntity<String> response = restTemplate.exchange(uri , HttpMethod.POST, null,String.class);

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

	public ResponseOrdenCompra obtenerOrdenCompraPorID(int id) {
		 
		ResponseOrdenCompra responseResult = new ResponseOrdenCompra();
		
		try {

			String url = this.hostStock + "/orden/compras/id?id=" + id;
			 
			URI uri = new URI(url);
			ResponseEntity<OrdenCompra> response = restTemplate.exchange(uri , HttpMethod.GET, null,OrdenCompra.class);

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
			 
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 

		return responseResult;

		 
	}

	public ResponseFacturaCompra obtenerFacturaCompraPorID(int id) {
		 
		ResponseFacturaCompra responseResult = new ResponseFacturaCompra();
		try {

			String url = this.hostStock + "/factura/compra/id?id=" + id;
			 
			URI uri = new URI(url);
			ResponseEntity<FacturaCompra> response = restTemplate.exchange(uri , HttpMethod.GET, null,FacturaCompra.class);

			if (response.getStatusCodeValue() == 200) {

				responseResult.setStatus(true);
				responseResult.setFacturaCompra(response.getBody());
 
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
}