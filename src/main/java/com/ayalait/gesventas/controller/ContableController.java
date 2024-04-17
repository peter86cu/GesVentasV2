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
							 lstItems.add(items);

						 } 
					 }else {
						 for(TipoGastos gasto: response.getTipoGasto()) {
							 if(gasto.getTipogasto().contains(busqueda.toUpperCase())) {
								 ItemsSelect2 items= new ItemsSelect2();
								 items.setId(String.valueOf(gasto.getId()) );
								 items.setText(gasto.getTipogasto());
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
    @PostMapping({ LoginController.ruta + "/add-gastos" })
	public void agregarDatosProfesionales(@RequestParam("tipogasto") int tipogasto,@RequestParam("moneda") int moneda,
			@RequestParam("saldo") double saldo,@RequestParam("estado") String estado,
			@RequestParam("factura") MultipartFile archivo,Model modelo, HttpServletResponse responseHttp)
			throws IOException, ParseException, SQLException {
		if (LoginController.session.getToken() != null) {
			modelo.addAttribute("user", LoginController.session.getUser());
			
			// Obtener el nombre original del archivo
            String nombreArchivo = archivo.getOriginalFilename();
            
            // Obtener la extensión del archivo
            String extension = obtenerExtension(nombreArchivo);
            
			Path path = Paths.get(LoginController.rutaFacturas + archivo.getOriginalFilename(),
					new String[0]);
			Files.copy(archivo.getInputStream(), path, new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });
			byte[] fileContent = Files.readAllBytes(path);
			String base64 = Base64.getEncoder().encodeToString(fileContent);
			
		        	ContableGastos contableGastos= new ContableGastos();
			        contableGastos.setEstado(estado);
			        contableGastos.setTipogasto(tipogasto);
			        contableGastos.setFactura(base64 );
			        contableGastos.setMoneda(moneda);
			        contableGastos.setSaldo(saldo);
			        contableGastos.setFecha(Utils.obtenerFechaPorFormato(FormatoFecha.YYYYMMDD.getFormato()));
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
   

}