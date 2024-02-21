package com.ayalait.gesventas.service;

import com.ayalait.gesventas.controller.LoginController;
import com.ayalait.gesventas.request.RequestCrearOrdenCompra;
import com.ayalait.gesventas.request.RequestVentaDevoluciones;
import com.ayalait.modelo.AperturasTerminal;
import com.ayalait.modelo.ArqueoTerminal;
import com.ayalait.modelo.ArqueosDetalle;
import com.ayalait.modelo.EstadoProceso;
import com.ayalait.modelo.Ventas;
import com.ayalait.modelo.VentasCausaDevueltos;
import com.ayalait.modelo.VentasEstados;
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

public class wsTerminal {

    private String hostTerminal;

	ObjectWriter ow = (new ObjectMapper()).writer().withDefaultPrettyPrinter();

	@Autowired
	RestTemplate restTemplate= new RestTemplate();

    public wsTerminal() {
        try {
            if (LoginController.desarrollo) {
                hostTerminal = "http://localhost:8087";
            } else {
                cargarServer();
            }
        } catch (IOException var2) {
            Logger.getLogger(wsTerminal.class.getName()).log(Level.SEVERE, (String) null, var2);
        }
    }

    void cargarServer() throws IOException {
        Properties p = new Properties();

        try {
            URL url = this.getClass().getClassLoader().getResource("application.properties");
            if (url == null) {
                throw new IllegalArgumentException("application.properties" + " is not found 1");
            } else {
                InputStream propertiesStream = url.openStream();
                p.load(propertiesStream);
                propertiesStream.close();
                this.hostTerminal = p.getProperty("server.terminal");
            }
        } catch (FileNotFoundException var3) {
            Logger.getLogger(wsTerminal.class.getName()).log(Level.SEVERE, (String) null, var3);
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


    public ResponseResultado abrirTerminal(AperturasTerminal request, String token) {
        
        ResponseResultado responseResult = new ResponseResultado();
        
        try {

			String url =this.hostTerminal + "/terminal/add";
			HttpHeaders headers = new HttpHeaders();

			headers.set("Authorization", "Bearer "+token);
 			HttpEntity<AperturasTerminal> requestEntity = new HttpEntity<>(request, headers);
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

    public ResponseResultado cerrarTerminal(AperturasTerminal request, String token) {
         
        ResponseResultado responseResult = new ResponseResultado();
        
        try {

			String url =this.hostTerminal + "/terminal/cerrar";
			HttpHeaders headers = new HttpHeaders();

			headers.set("Authorization", "Bearer "+token);
 			HttpEntity<AperturasTerminal> requestEntity = new HttpEntity<>(request, headers);
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

    public ResponseResultado guardarArqueoTerminal(ArqueoTerminal request, String token) {
        
        ResponseResultado responseResult = new ResponseResultado();
        
        try {

			String url = this.hostTerminal + "/terminal/arqueo/add";
			HttpHeaders headers = new HttpHeaders();

			headers.set("Authorization", "Bearer "+token);
 			HttpEntity<ArqueoTerminal> requestEntity = new HttpEntity<>(request, headers);
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
    
    

    public ResponseResultado eliminarArqueoYDetalle(int idArqueo) {
        
        ResponseResultado responseResult = new ResponseResultado();
        
        try {

			String url = this.hostTerminal + "/terminal/arqueo/delete?id=" + idArqueo;
			 
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

    public ResponseResultado guardarDetalleArqueoTerminal(ArqueosDetalle request, String token) {
        
        ResponseResultado responseResult = new ResponseResultado();
        
        try {

			String url = this.hostTerminal + "/terminal/arqueo/detalle/add";
			HttpHeaders headers = new HttpHeaders();

			headers.set("Authorization", "Bearer "+token);
 			HttpEntity<ArqueosDetalle> requestEntity = new HttpEntity<>(request, headers);
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


    public ResponseAperturaTerminal obtenerAperturaTerminalPorId(String token, int id) {
        
        ResponseAperturaTerminal responseResult = new ResponseAperturaTerminal();
        
        try {

			String url = this.hostTerminal + "/terminal/apertura?id=" + id;
			HttpHeaders headers = new HttpHeaders();

			headers.set("Authorization", "Bearer "+token);
 			HttpEntity<Void> requestEntity = new HttpEntity<>(null, headers);
			URI uri = new URI(url);
			ResponseEntity<AperturasTerminal> response = restTemplate.exchange(uri , HttpMethod.GET, requestEntity,AperturasTerminal.class);

			if (response.getStatusCodeValue() == 200) {

				responseResult.setStatus(true);
				responseResult.setTerminal(response.getBody());
 
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


    public ResponseResultado eliminarProductoEnVentaDetalle(int idVentaDetalle) {
         
        ResponseResultado responseResult = new ResponseResultado();
        try {

			String url = this.hostTerminal + "/venta/detalle/delete?idVentaDetalle=" + idVentaDetalle;
 
			 
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
    
    
    public ResponseUltimas10Venta ultimas10Ventas(String idUsuario, boolean acceso) {
        
        ResponseUltimas10Venta responseResult = new ResponseUltimas10Venta();
        
        try {

			String url = this.hostTerminal + "/venta/10ultimas?acceso="+acceso+"&id=" + idUsuario;
 
			 
			URI uri = new URI(url);
			ResponseEntity<List<Ventas>> response = restTemplate.exchange(uri , HttpMethod.POST, null,
					new ParameterizedTypeReference<List<Ventas>>() {
					});

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
    
    
    public ResponseResultado actualizarTransaccionVentaTarjeta(int idVenta, String transaccion) {
        
        ResponseResultado responseResult = new ResponseResultado();
        
        try {

			String url = this.hostTerminal + "/venta/actualizar/transaccion?idVenta="+idVenta+"&id_transaccion=" + transaccion;
 
			 
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

    
    public ResponseListaEstadoVentas lstadoEstadoVentas() {
        
        ResponseListaEstadoVentas responseResult = new ResponseListaEstadoVentas();
        
        try {

			String url = this.hostTerminal + "/parametros/estado-ventas";
			
			URI uri = new URI(url);
			ResponseEntity<List<VentasEstados>> response = restTemplate.exchange(uri , HttpMethod.GET, null,
					new ParameterizedTypeReference<List<VentasEstados>>() {
					});

			if (response.getStatusCodeValue() == 200) {

				responseResult.setStatus(true);
				responseResult.setEstadoVentas(response.getBody());

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
    
    
    public ResponseListaEstadoVentasDevueltos lstadoEstadoCausaDevueltosVentas() {
        
        ResponseListaEstadoVentasDevueltos responseResult = new ResponseListaEstadoVentasDevueltos();
        
        try {

			String url = this.hostTerminal + "/parametros/estado-ventas-devueltos";
			
			URI uri = new URI(url);
			ResponseEntity<List<VentasCausaDevueltos>> response = restTemplate.exchange(uri , HttpMethod.GET, null,
					new ParameterizedTypeReference<List<VentasCausaDevueltos>>() {
					});

			if (response.getStatusCodeValue() == 200) {

				responseResult.setStatus(true);
				responseResult.setCausaDevueltos(response.getBody());

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
    
    public ResponseResultado guardarDevolucionVenta(RequestVentaDevoluciones request) {
        
        ResponseResultado responseResult = new ResponseResultado();
        
        try {

			String url = this.hostTerminal + "/ventas/devolucion/add";
			HttpHeaders headers = new HttpHeaders();
 			HttpEntity<RequestVentaDevoluciones> requestEntity = new HttpEntity<>(request, headers);
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
