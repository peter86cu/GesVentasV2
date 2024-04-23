package com.ayalait.gesventas.controller;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Blob;
import java.sql.SQLException;
import java.text.ParseException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;


import com.ayalait.gesventas.request.RequestVentaDevoluciones;
import com.ayalait.gesventas.utils.*;
import com.ayalait.modelo.*;
import com.ayalait.modelo.ItemsVenta;
import com.ayalait.response.*;
import com.ayalait.utils.ItemsSelect2;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;


@Controller
public class ContableController {

    private static FormatoFecha fechaSinHora;
    private static FormatoFecha fechaConHora;
    private static RequestVentaDevoluciones devolucion= new RequestVentaDevoluciones();
    private static List<VentasProductosDevolucion> lstProductos= new ArrayList<VentasProductosDevolucion>();


    public ContableController() {
        fechaSinHora = FormatoFecha.YYYYMMDD;
        fechaConHora = FormatoFecha.YYYYMMDDH24;
    }

   
    @GetMapping({"/contable"})
    public String tipoGastos(Model modelo, RedirectAttributes attribute) {
        if (LoginController.session.getToken() != null) {
        	ResponseMonedas responseM = LoginController.conParam.listarMonedas();
			String fecha = "";
			modelo.addAttribute("user", LoginController.session.getUser());
			if (responseM.isStatus()) {
				modelo.addAttribute("listaMoneda",  responseM.getMonedas() );

				HistoricoCambio cambio = new HistoricoCambio();
				modelo.addAttribute("fechaapertura", fecha);
				modelo.addAttribute("cambio", cambio);
				 

			}else {
				modelo.addAttribute("listaMoneda", new ArrayList<Moneda>());
			}
			
			ResponseListaMesPago meses = LoginController.conEmpl.mesesAPagar();
			if (meses.isStatus()) {
				modelo.addAttribute("meses", meses.getMes());

			} else {
				modelo.addAttribute("meses", new ArrayList<MesPago>());

			}
			return "contable";
           


        } else {
            return "login";
        }
    }

    @PostMapping({ LoginController.ruta + "/buscar-gastos" })
	public void obtenerTitulo(@ModelAttribute("accion") String accion, @ModelAttribute("q") String busqueda,
							  Model modelo, HttpServletResponse responseHttp) throws IOException, ParseException, SQLException {
		if (LoginController.session.getToken() != null) {
			List<ItemsSelect2> lstItems= new ArrayList<ItemsSelect2>();
			modelo.addAttribute("user", LoginController.session.getUser());
			 ResponseListaTipoGastos response = LoginController.conContable.listaTipoGastos();
			 if(response.isStatus()) {
				 if(!response.getTipoGasto().isEmpty()) {
					 if(busqueda.toUpperCase().equalsIgnoreCase("Todo")) {
						 for(TipoGastos gasto: response.getTipoGasto()) {
							 ItemsSelect2 items= new ItemsSelect2();
							 items.setId(String.valueOf(gasto.getId()) );
							 items.setText(gasto.getTipogasto());
							 items.setType(gasto.getGasto());
							 lstItems.add(items);

						 } 
					 }else {
						 for(TipoGastos gasto: response.getTipoGasto()) {
							 if(gasto.getTipogasto().contains(busqueda.toUpperCase())) {
								 ItemsSelect2 items= new ItemsSelect2();
								 items.setId(String.valueOf(gasto.getId()) );
								 items.setText(gasto.getTipogasto());
								 items.setType(gasto.getGasto());
								 lstItems.add(items); 
							 }
							  
					 }
					

					 }
				 }
			 }
			String json = (new Gson()).toJson(lstItems);
			responseHttp.setContentType("application/json");
			responseHttp.setCharacterEncoding("UTF-8");
			responseHttp.getWriter().write(json);
		} else {
			responseHttp.setContentType("application/json");
			responseHttp.setCharacterEncoding("UTF-8");
			responseHttp.getWriter().write("LoginController.session caducada");
		}

	}
    
    
    @PostMapping({ LoginController.ruta + "/buscar-centro-costo" })
	public void obtenerCentroCosto(@ModelAttribute("accion") String accion, @ModelAttribute("q") String busqueda,
							  Model modelo, HttpServletResponse responseHttp) throws IOException, ParseException, SQLException {
		if (LoginController.session.getToken() != null) {
			List<ItemsSelect2> lstItems= new ArrayList<ItemsSelect2>();
			modelo.addAttribute("user", LoginController.session.getUser());
			 ResponseListaCentroCosto response = LoginController.conContable.listaCentroCosto();
			 if(response.isStatus()) {
				 if(!response.getCentroCosto().isEmpty()) {
					 if(busqueda.toUpperCase().equalsIgnoreCase("Todo")) {
						 for(CentroCosto gasto: response.getCentroCosto()) {
							 ItemsSelect2 items= new ItemsSelect2();
							 items.setId(String.valueOf(gasto.getId())  );
							 items.setText(gasto.getDescripcion());
							 items.setType(gasto.getCodigo());
							 lstItems.add(items);

						 } 
					 }else {
						 for(CentroCosto gasto: response.getCentroCosto()) {
							 if(gasto.getCodigo().contains(busqueda.toUpperCase())) {
								 ItemsSelect2 items= new ItemsSelect2();
								 items.setId(String.valueOf(gasto.getId()) );
								 items.setText(gasto.getDescripcion());
								 items.setType(gasto.getCodigo());
								 lstItems.add(items); 
							 }
							  
					 }
					

					 }
				 }
			 }
			String json = (new Gson()).toJson(lstItems);
			responseHttp.setContentType("application/json");
			responseHttp.setCharacterEncoding("UTF-8");
			responseHttp.getWriter().write(json);
		} else {
			responseHttp.setContentType("application/json");
			responseHttp.setCharacterEncoding("UTF-8");
			responseHttp.getWriter().write("LoginController.session caducada");
		}

	}
    
    @PostMapping("/archivo")
    public String recibirArchivo(@RequestParam("archivo") MultipartFile archivo) {
        // Procesar el archivo, por ejemplo, guardar en el servidor
        try {
            byte[] contenido = archivo.getBytes();
            // Realizar alguna acción con el contenido del archivo
            // Por ejemplo, guardar en el servidor
            // Ejemplo: FileCopyUtils.copy(contenido, new File("ruta/del/archivo"));
            return "Archivo recibido correctamente";
        } catch (IOException e) {
            e.printStackTrace();
            return "Error al recibir el archivo";
        }
    }
    // Método para obtener la extensión del archivo
    private String obtenerExtension(String nombreArchivo) {
        if (nombreArchivo != null && !nombreArchivo.isEmpty()) {
            int puntoIndex = nombreArchivo.lastIndexOf(".");
            if (puntoIndex != -1) {
                return nombreArchivo.substring(puntoIndex + 1);
            }
        }
        return "";
    }
    @PostMapping({ LoginController.ruta + "/add-costo" })
	public void agregarCostos(@RequestParam("cc") String cc,@RequestParam("fecha") String fecha,
			@RequestParam("es") String entradasalida,@RequestParam("tipo") String tipo,
			@RequestParam("idtipogasto") int idtipogasto,@RequestParam("moneda") int moneda,
			@RequestParam("saldo") double saldo,@RequestParam("estado") String estado,
			@RequestParam("iva") int iva,Model modelo, HttpServletResponse responseHttp)
			throws IOException, ParseException, SQLException {
		if (LoginController.session.getToken() != null) {
			modelo.addAttribute("user", LoginController.session.getUser());
           
		        	ContableLibroDiario contableGastos= new ContableLibroDiario();
			        contableGastos.setEstado(estado);
			        contableGastos.setIdtipoie(idtipogasto);
			        contableGastos.setCentrocosto(cc);
			        contableGastos.setEntradasalida(entradasalida);			        
			        contableGastos.setFactura("");
			        contableGastos.setTipo(tipo);
			        contableGastos.setIva(iva);
			        contableGastos.setMoneda(moneda);
			        contableGastos.setSaldo(saldo);
			        contableGastos.setFecha(fecha);
					contableGastos.setId(UUID.randomUUID().toString());
					contableGastos.setUsuario(LoginController.session.getUser().getIdusuario());
				
		        
		       
		        	ResponseResultado response= LoginController.conContable.addGasto(contableGastos);
					String json = (new Gson()).toJson(response);
					responseHttp.setContentType("application/json");
					responseHttp.setCharacterEncoding("UTF-8");
					responseHttp.getWriter().write(json);
		        

		}else {
			ResponseResultado response = new ResponseResultado();
			response.setCode(300);
			response.setStatus(false);
			response.setResultado("Sessión caduca.");
			String json = (new Gson()).toJson(response);
			responseHttp.setContentType("application/json");
			responseHttp.setCharacterEncoding("UTF-8");
			responseHttp.getWriter().write(json);
		}

	}
    
    @PostMapping({ LoginController.ruta + "/add-centro-costo" })
	public void agregarCentroCosto(@RequestParam("codigo") String codigo,@RequestParam("descripcion") String descripcion,
			@RequestParam("estado") String estado,Model modelo, HttpServletResponse responseHttp)
			throws IOException, ParseException, SQLException {
		if (LoginController.session.getToken() != null) {
			modelo.addAttribute("user", LoginController.session.getUser());
           
		        	CentroCosto add= new CentroCosto();
			        add.setEstado(estado);
			        add.setCodigo(codigo);
			        add.setDescripcion(descripcion);
			        add.setId(0);
		        
		       
		        	ResponseResultado response= LoginController.conContable.addCentroCosto(add);
					String json = (new Gson()).toJson(response);
					responseHttp.setContentType("application/json");
					responseHttp.setCharacterEncoding("UTF-8");
					responseHttp.getWriter().write(json);
		        

		}else {
			ResponseResultado response = new ResponseResultado();
			response.setCode(300);
			response.setStatus(false);
			response.setResultado("Sessión caduca.");
			String json = (new Gson()).toJson(response);
			responseHttp.setContentType("application/json");
			responseHttp.setCharacterEncoding("UTF-8");
			responseHttp.getWriter().write(json);
		}

	}
    
    
    
    @PostMapping({ LoginController.ruta + "/add-gastos" })
   	public void agregarGastos(@RequestParam("factura") MultipartFile archivo,
   			@RequestParam("cc") String cc,@RequestParam("fecha") String fecha,
			@RequestParam("es") String entradasalida,@RequestParam("tipo") String tipo,
			@RequestParam("idtipogasto") int idtipogasto,@RequestParam("moneda") int moneda,@RequestParam("iva") int iva,
			@RequestParam("saldo") double saldo,@RequestParam("estado") String estado,Model modelo, HttpServletResponse responseHttp)
   			throws IOException, ParseException, SQLException {
   		if (LoginController.session.getToken() != null) {
   			modelo.addAttribute("user", LoginController.session.getUser());
   			String base64 ="";
   			if(!archivo.isEmpty()) {
   				Path path = Paths.get(LoginController.rutaFacturas + archivo.getOriginalFilename(),
   						new String[0]);
   				//Files.copy(archivo.getInputStream(), path, new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });
   				byte[] fileContent = Files.readAllBytes(path);
   				base64 = Base64.getEncoder().encodeToString(fileContent);
   			}
   			
               
   			ContableLibroDiario contableGastos= new ContableLibroDiario();
	        contableGastos.setEstado(estado);
	        contableGastos.setIdtipoie(idtipogasto);
	        contableGastos.setCentrocosto(cc);
	        contableGastos.setEntradasalida(entradasalida);			        
	        contableGastos.setFactura(base64);
	        contableGastos.setTipo(tipo);
	        contableGastos.setIva(iva);
	        contableGastos.setMoneda(moneda);
	        contableGastos.setSaldo(saldo);
	        contableGastos.setFecha(fecha);
			contableGastos.setId(UUID.randomUUID().toString());
			contableGastos.setUsuario(LoginController.session.getUser().getIdusuario());
		
   				
   		        
   		       
   		        	ResponseResultado response= LoginController.conContable.addGasto(contableGastos);
   					String json = (new Gson()).toJson(response);
   					responseHttp.setContentType("application/json");
   					responseHttp.setCharacterEncoding("UTF-8");
   					responseHttp.getWriter().write(json);
   		        

   		}else {
   			ResponseResultado response = new ResponseResultado();
   			response.setCode(300);
   			response.setStatus(false);
   			response.setResultado("Sessión caduca.");
   			String json = (new Gson()).toJson(response);
   			responseHttp.setContentType("application/json");
   			responseHttp.setCharacterEncoding("UTF-8");
   			responseHttp.getWriter().write(json);
   		}

   	}
   
    
    @PostMapping({ LoginController.ruta + "/costos-directos" })
   	public void obtenerCostosDirectos(@ModelAttribute("mes") String mes,Model modelo, HttpServletResponse responseHttp)
   			throws IOException, ParseException, SQLException {
   		if (LoginController.session.getToken() != null) {
   			String[] data = mes.split("-");
			int mesF = Integer.parseInt(data[0]);
			int anio = Integer.parseInt(data[1]);
   		       
   		        	ResponseListaCostosDirect response= LoginController.conContable.obterCostosDirectos(mesF, anio);
   					String json = (new Gson()).toJson(response);
   					responseHttp.setContentType("application/json");
   					responseHttp.setCharacterEncoding("UTF-8");
   					responseHttp.getWriter().write(json);
   		        

   		}else {
   			ResponseResultado response = new ResponseResultado();
   			response.setCode(300);
   			response.setStatus(false);
   			response.setResultado("Sessión caduca.");
   			String json = (new Gson()).toJson(response);
   			responseHttp.setContentType("application/json");
   			responseHttp.setCharacterEncoding("UTF-8");
   			responseHttp.getWriter().write(json);
   		}

   	}
    
    
    @PostMapping({ LoginController.ruta + "/calculo-costos" })
   	public void guardarCalculoCostosDirectos(@ModelAttribute("calculo") int calculo,@ModelAttribute("fecha") String fecha,
   			Model modelo, HttpServletResponse responseHttp)
   			throws IOException, ParseException, SQLException {
   		if (LoginController.session.getToken() != null) {

			CalculoCosto cal= new CalculoCosto();
			cal.setId(0);
			cal.setFecha(fecha);
			cal.setCalculo(calculo);
			
   		        	ResponseResultado response= LoginController.conContable.addCalculoCostos(cal);
   					String json = (new Gson()).toJson(response);
   					responseHttp.setContentType("application/json");
   					responseHttp.setCharacterEncoding("UTF-8");
   					responseHttp.getWriter().write(json);
   		        

   		}else {
   			ResponseResultado response = new ResponseResultado();
   			response.setCode(300);
   			response.setStatus(false);
   			response.setResultado("Sessión caduca.");
   			String json = (new Gson()).toJson(response);
   			responseHttp.setContentType("application/json");
   			responseHttp.setCharacterEncoding("UTF-8");
   			responseHttp.getWriter().write(json);
   		}

   	}

}