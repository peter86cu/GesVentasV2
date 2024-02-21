package com.ayalait.gesventas.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;

import com.ayalait.gesventas.request.*;

import com.ayalait.gesventas.utils.*;
import com.ayalait.modelo.*;
import com.ayalait.response.*;

import javax.servlet.http.HttpServletResponse;

@Controller
public class UsuariosController {
	@Autowired
	RestTemplate restTemplate;

	@PostMapping({LoginController.ruta+ "/validar-usuarios-nuevos" })
	public void validar(Model modelo, HttpServletResponse responseHttp) throws IOException, ParseException {
		if (LoginController.session.getToken() != null) {
			modelo.addAttribute("user", LoginController.session.getUser());
			ResponseListaEmpleados response = LoginController.conEmpl
					.listadEmpleadoPorEmpresa(LoginController.session.getToken(), LoginController.session.getId_empresa());
			if(response.isStatus() && response.getEmpleados().isEmpty()) {
				ResponseResultado respuesta= new ResponseResultado();
				respuesta.setCode(406);
				respuesta.setResultado("No existen empleados nuevos.");
				String json = (new Gson()).toJson(respuesta);
                responseHttp.setContentType("application/json");
                responseHttp.setCharacterEncoding("UTF-8");
                responseHttp.getWriter().write(json);
			}else {
				ResponseResultado respuesta= new ResponseResultado();
				respuesta.setCode(200);				
				String json = (new Gson()).toJson(respuesta);
                responseHttp.setContentType("application/json");
                responseHttp.setCharacterEncoding("UTF-8");
                responseHttp.getWriter().write(json);
			}

		}
	}

	/*  	datos.append("accion",accion);
 	datos.append("email",email);
 	datos.append("usuario",usuario);
 	datos.append("password",password);
 	datos.append("idRol",idRol);
 	datos.append("estado",estado);
 	datos.append("idEmpleado",idEmpleado);*/
	
	@PostMapping({ LoginController.ruta+"/adduser" })
	public void guardarCuentas(@ModelAttribute("accion") String accion,@ModelAttribute("idUsuario") String idUsuario,@ModelAttribute("usuario") String usuario, @ModelAttribute("email") String email,
			@ModelAttribute("password") String password,@ModelAttribute("idRol") int idRol,@ModelAttribute("estado") int estado,
			@ModelAttribute("idEmpleado") String idEmpleado,Model modelo, HttpServletResponse responseHttp) throws IOException, ParseException{
		if(LoginController.session.getToken()!=null) {
			
			modelo.addAttribute("user", LoginController.session.getUser());
			
			ResponseBuscarEmpleado responseBuscarEmpleado = LoginController.conEmpl.buscarEmpleadoPorId(LoginController.session.getToken(),idEmpleado);
			if (responseBuscarEmpleado.isStatus()) {
				ResponseBuscarRol responseRol = LoginController.conRol.buscarRolPorId(LoginController.session.getToken(),idRol);
				if (responseRol.isStatus()) {
					User user = new User();
					if(accion.equalsIgnoreCase("add")) {
						user.setIdusuario(Utils.generarCodigo());	
					}else {
						user.setIdusuario(idUsuario);
					}
					user.setEmail(email);
					user.setEstado(estado);
					user.setEmpleado(responseBuscarEmpleado.getEmpleados());
					user.setIdrol(responseRol.getRoles());
					user.setUsuario(usuario);
					user.setPassword(password);
					RequestAddUsuario request = new RequestAddUsuario();
					request.setUser(user);
					ResponseResultado responseAdd = LoginController.conUser.addUsuario(request,	LoginController.session.getToken());
					
					
						 String json = (new Gson()).toJson(responseAdd);
		                    responseHttp.setContentType("application/json");
		                    responseHttp.setCharacterEncoding("UTF-8");
		                    responseHttp.getWriter().write(json);
		                    
					
		      }
			}
		
		}
		
		
	}
	
	 @PostMapping({LoginController.ruta+"/eliminar-usuario"})
	    public void eliminarUsuario( @ModelAttribute("idUsuario") String busqueda, Model modelo, HttpServletResponse responseHttp)
	            throws IOException, ParseException
	    {
	        if (LoginController.session.getToken() != null)
	        {
	        	
	        	
	        }
	        
	    }
	
	
	  @PostMapping({LoginController.ruta+"/editar-usuario"})
	    public void buscarUsuarioEditar( @ModelAttribute("idUsuario") String busqueda, Model modelo, HttpServletResponse responseHttp)
	            throws IOException, ParseException, SQLException
	    {
	        if (LoginController.session.getToken() != null)
	        {
	        	
	        	List<User> lista = null;
	    		List<Roles> listaRoles = Utils.obtenerListaRoles(LoginController.session.getToken());
	    		List<Estados> listaEstados = Utils.obtenerListaEstado();
	    		List<Empleado> listaEmpleados = new ArrayList<Empleado>();

	    		UserAdd nuevoUserAdd = new UserAdd();

	    		ResponseListaUsuario response = LoginController.conUser
	    				.listadUsuariosPorEmpresa(LoginController.session.getId_empresa(), LoginController.session.getToken());
	    		if (response.isStatus()) {
	    			lista =  response.getUser();
	    		}
	    		ResponseListaEmpleados listaEmp = LoginController.conEmpl
	    				.listadEmpleadoPorEmpresa(LoginController.session.getToken(), LoginController.session.getId_empresa());
	    		if (response.isStatus()) {
	    			listaEmpleados.add(new Empleado("0", "SELECCIONAR"));
	    			listaEmpleados =  listaEmp.getEmpleados();
	    		}
	    		
	    		
	        	modelo.addAttribute("user", LoginController.session.getUser());
	    		modelo.addAttribute("empleadosNuevos", listaEmpleados);
	    		modelo.addAttribute("roles", listaRoles);
	    		modelo.addAttribute("estados", listaEstados);
	    		modelo.addAttribute("usuario", nuevoUserAdd);
	    		modelo.addAttribute("listaUser", lista);
	    		
	            ResponseUsuario responseUsuario= LoginController.conUser.buscarUsuarioPorId(LoginController.session.getToken(), busqueda);
	            
	            	String json = new Gson().toJson(responseUsuario);
	                responseHttp.setContentType("application/json");
	                responseHttp.setCharacterEncoding("UTF-8");
	                responseHttp.getWriter().write(json);
	           
	        	
	        }
	        
	    }

	@GetMapping({ "/user-registrados" })
	public String cuenta(Model modelo) {
		if (LoginController.session.getToken() != null) {
			List<Roles> listaRoles = Utils.obtenerListaRoles(LoginController.session.getToken());
			List<EstadoUsuarios> listaEstados = new ArrayList<EstadoUsuarios>();
			List<Empleado> listaEmpleados = new ArrayList<Empleado>();

			UserAdd nuevoUser = new UserAdd();
			List<User> lista = null;

			ResponseListaEstadosUsuarios responseEstados= LoginController.conParam.listadoEstadosUsuarios();
			if(responseEstados.isStatus()) {
				listaEstados=  responseEstados.getEstados();
				modelo.addAttribute("estados",listaEstados );

			}else {
				modelo.addAttribute("estados", new ArrayList<EstadoUsuarios>());

			}
			
			ResponseListaUsuario response = LoginController.conUser.listadUsuariosPorEmpresa(
					LoginController.session.getId_empresa(), LoginController.session.getToken());
			if (response.isStatus()) {
				lista =  response.getUser();
			}
			ResponseListaEmpleados listaEmp = LoginController.conEmpl.listadEmpleadoPorEmpresa(
					LoginController.session.getToken(), LoginController.session.getId_empresa());
			if (response.isStatus()) {
				listaEmpleados.add(new Empleado("0", "SELECCIONAR"));
				listaEmpleados =  listaEmp.getEmpleados() ;
			}
			ResponseMonedas responseM = LoginController.conParam.listarMonedas();
			if (responseM.isStatus()) {
				modelo.addAttribute("listaMoneda", responseM.getMonedas());
			}else {
				modelo.addAttribute("listaMoneda", new ArrayList<Moneda>());

			}
			modelo.addAttribute("user", LoginController.session.getUser());
			modelo.addAttribute("empleadosNuevos", listaEmpleados);
			modelo.addAttribute("roles", listaRoles);
			//modelo.addAttribute("estados", listaEstados);
			modelo.addAttribute("usuario", nuevoUser);
			modelo.addAttribute("listaUser", lista);
			return "user-registrados";
		}
		return "redirect:/";
	}

}
