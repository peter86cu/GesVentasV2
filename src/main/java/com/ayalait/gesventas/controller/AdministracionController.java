package com.ayalait.gesventas.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.ayalait.gesventas.request.RequestLogAdmin;
import com.ayalait.gesventas.utils.EjecutarProceso;
import com.ayalait.gesventas.utils.FormatoFecha;
import com.ayalait.gesventas.utils.Utils;
import com.ayalait.modelo.*;
import com.ayalait.response.*;
import com.google.gson.Gson;
@Controller
public class AdministracionController {
	
	@GetMapping({ "/administracion" })
	public String administracion(Model modelo) throws SQLException {
		if (LoginController.session.getToken() != null) {
			modelo.addAttribute("user", LoginController.session.getUser());

			modelo.addAttribute("lstProcesos", Utils.listadoServiciosAdmin());
			
			ResponseMonedas responseM = LoginController.conParam.listarMonedas();
			if (responseM.isStatus()) {
				modelo.addAttribute("listaMoneda", Arrays.asList(responseM.getMonedas()));
			}else {
				modelo.addAttribute("listaMoneda", new ArrayList<Moneda>());

			}
			ResponseListaEstadoProcesos responseEstado= LoginController.conParam.listarEstadoProcesos();
			if(responseEstado.isStatus()) {
				modelo.addAttribute("listaEstado", Arrays.asList(responseEstado.getEstado()));

			}else {
				modelo.addAttribute("listaEstado", new ArrayList<EstadoProceso>());

			}

			return "administracion";
		}
		LoginController.session = new Session();
		return "login";
	}
	
	
	@PostMapping({ LoginController.ruta+"/ejecutar-proceso" })
	public void ejecutar(@ModelAttribute("accion") String accion, @ModelAttribute("proceso") int id_proceso,
						 Model modelo, HttpServletResponse responseHttp) throws IOException, ParseException {
		if (LoginController.session.getToken() != null) {
			modelo.addAttribute("user", LoginController.session.getUser());
			RequestLogAdmin request = new RequestLogAdmin();
			AdministracionLog admin = new AdministracionLog();
			ResponseEjecutarProceso ejecucion = EjecutarProceso.respaldoBD();
			if (ejecucion.isStatus()) {
				admin.setId_estado(2);
			} else {
				admin.setId_estado(3);
			}
			admin.setId(0);
			admin.setId_administracion(id_proceso);
			admin.setId_usuario_ejecuto(LoginController.session.getUser().getIdusuario());

			admin.setResultado(new Gson().toJson(ejecucion.getLog()));
			
			admin.setFecha_ejecucion(Utils.obtenerFechaPorFormato(FormatoFecha.YYYYMMDDH24.getFormato()));
			request.setAdminLog(admin);
			ResponseResultado response = LoginController.conParam.addLogAdmin(admin);
			if (response.isStatus()) {
				String json = new Gson().toJson(ejecucion);
				responseHttp.setContentType("application/json");
				responseHttp.setCharacterEncoding("UTF-8");
				responseHttp.getWriter().write(json);
			} else {
				String json = new Gson().toJson(response);
				responseHttp.setContentType("application/json");
				responseHttp.setCharacterEncoding("UTF-8");
				responseHttp.getWriter().write(json);
			}
		} else {
			responseHttp.setContentType("application/json");
			responseHttp.setCharacterEncoding("UTF-8");
			responseHttp.getWriter().write("Session caducada");
		}
	}
	
	
	
	@PostMapping({ LoginController.ruta+"/reiniciar-servidor" })
	public void reiniciar( @ModelAttribute("proceso") int id_proceso,
						 Model modelo, HttpServletResponse responseHttp) throws IOException, ParseException {
		if (LoginController.session.getToken() != null) {
			modelo.addAttribute("user", LoginController.session.getUser());
			ResponseResultado response = new ResponseResultado();
			AdministracionLog admin = new AdministracionLog();
			if(id_proceso==3) {
				
			}
			if(id_proceso==4) {
				response= LoginController.conParam.reiniciarServidor(LoginController.conParam.getHostRecursosHumanos());
				if(response.isStatus()) {
					admin.setId_estado(2);
				}else {
					admin.setId_estado(3);
				}
				admin.setId(0);
				admin.setId_administracion(id_proceso);
				admin.setId_usuario_ejecuto(LoginController.session.getUser().getIdusuario());

				admin.setResultado(response.getResultado());				
				admin.setFecha_ejecucion(Utils.obtenerFechaPorFormato(FormatoFecha.YYYYMMDDH24.getFormato()));
				String json = new Gson().toJson(response);
				responseHttp.setContentType("application/json");
				responseHttp.setCharacterEncoding("UTF-8");
				responseHttp.getWriter().write(json);
			}
			
		}
		
		}
	
	
	@PostMapping({ LoginController.ruta+"/stop-servidor" })
	public void stop( @ModelAttribute("proceso") int id_proceso,
						 Model modelo, HttpServletResponse responseHttp) throws IOException, ParseException {
		if (LoginController.session.getToken() != null) {
			modelo.addAttribute("user", LoginController.session.getUser());
			ResponseResultado response = new ResponseResultado();
			AdministracionLog admin = new AdministracionLog();
			if(id_proceso==3) {
				response= LoginController.conParam.reiniciarServidor(LoginController.conParam.getHostSeguridad());
				String json = new Gson().toJson(response);
				responseHttp.setContentType("application/json");
				responseHttp.setCharacterEncoding("UTF-8");
				responseHttp.getWriter().write(json);
			}
			if(id_proceso==4) {
				response= LoginController.conParam.stopServidor(LoginController.conParam.getHostRecursosHumanos());
				if(response.isStatus()) {
					admin.setId_estado(2);
				}else {
					admin.setId_estado(3);
				}
				admin.setId(0);
				admin.setId_administracion(id_proceso);
				admin.setId_usuario_ejecuto(LoginController.session.getUser().getIdusuario());

				admin.setResultado(response.getResultado());				
				admin.setFecha_ejecucion(Utils.obtenerFechaPorFormato(FormatoFecha.YYYYMMDDH24.getFormato()));
				ResponseResultado responseLog = LoginController.conParam.addLogAdmin(admin);
				
				String json = new Gson().toJson(response);
				responseHttp.setContentType("application/json");
				responseHttp.setCharacterEncoding("UTF-8");
				responseHttp.getWriter().write(json);
			}
			
		}
		
		}

}
