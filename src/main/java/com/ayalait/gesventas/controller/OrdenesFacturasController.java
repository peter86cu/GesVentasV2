package com.ayalait.gesventas.controller;

import com.ayalait.gesventas.imprimir.ImprimirUtils;
import com.ayalait.gesventas.imprimir.ResponseOrdenImprimir;
import com.ayalait.gesventas.imprimir.ResponsePrefacturamprimir;
import com.google.gson.Gson;

import java.io.*;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import com.ayalait.gesventas.request.*;
import com.ayalait.gesventas.utils.*;
import com.ayalait.modelo.*;
import com.ayalait.notificaciones.Notificaciones;
import com.ayalait.response.*;
import com.ayalait.utils.Email;
import com.ayalait.utils.ResponsePrefactura;
import com.itextpdf.html2pdf.HtmlConverter;
import com.multishop.modelo.Prefactura;
import com.multishop.modelo.PrefacturaDetalle;
import com.multishop.modelo.PrefacturaModificaciones;
import com.multishop.modelo.ShoppingUsuarios;

import org.apache.logging.log4j.util.PropertySource.Comparator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletResponse;

@Controller
public class OrdenesFacturasController {

	DecimalFormat df = new DecimalFormat("#,00");
	
	@GetMapping({ "/orden-compra" })
	public String inicioOrdenCompra(Model modelo) throws SQLException {
		if (LoginController.session.getToken() != null) {
			modelo.addAttribute("user", LoginController.session.getUser());
			List<ResponseOrdenes> response = Utils.listadoOrdenesCompra();
			List<OCAprobadas> lstOCAprobadas = Utils.listadoOCAprobadas();
			OrdenCompra ordenCompra = new OrdenCompra();
			if(!response.isEmpty()){
				modelo.addAttribute("listaOrdenCompra", response);
			}else{
				modelo.addAttribute("listaOrdenCompra", new ArrayList<ResponseOrdenes>());
			}
			
			ResponseMonedas responseM = LoginController.conParam.listarMonedas();
			if (responseM.isStatus()) {
				modelo.addAttribute("listaMoneda",  responseM.getMonedas() );
			}else {
				modelo.addAttribute("listaMoneda", new ArrayList<Moneda>());

			}
			


			if(!lstOCAprobadas.isEmpty()){
				modelo.addAttribute("lstOCAprobadas", lstOCAprobadas);
			}else{
				modelo.addAttribute("lstOCAprobadas", new ArrayList<OCAprobadas>());
			}
			if(ordenCompra!=null){
				modelo.addAttribute("ordenCompra", ordenCompra);
			}
			modelo.addAttribute("itemsCompra", new ArrayList<ItemsOrdenCompra>());
			ResponseFormasPagos responseFP = LoginController.conParam.listarFormasPagos();
			if (responseFP.isStatus()) {
				modelo.addAttribute("formasPagos",  responseFP.getFormasPagos() );
			} else {
				modelo.addAttribute("formasPagos", new FormasPago());
			}

			ResponsePlazos responsePlazos = LoginController.conParam.listarPlazos();
			if (responsePlazos.isStatus()) {
				modelo.addAttribute("plazos",  responsePlazos.getPlazos() );
			} else {
				modelo.addAttribute("plazos", new Plazos());
			}

			return "orden-compra";
		} else {
			return "redirect:/";
		}
	}
/*	@GetMapping({ "/pdf/orden" })
	public String mostrarPDF(@ModelAttribute("idOrden") int idOrden,Model modelo) {
		if (LoginController.session.getToken() != null) {
			return "/pdf/orden_"+idOrden+".pdf";
		}else{
			return "login";
		}

	}*/
	
	

	
	@GetMapping({ "/prefactura" })
	public String inicioPrefactura(Model modelo) throws SQLException {
		if (LoginController.session.getToken() != null) {
			modelo.addAttribute("user", LoginController.session.getUser());
			ResponseResultado responseCot= LoginController.conStock.cargarPrefacturaWEB();
			if(!responseCot.isStatus()) {
				Notificaciones notificacion= new Notificaciones();
				notificacion.setTipo("Error");
				notificacion.setError(responseCot.getError().getMenssage());
				notificacion.setNotificacion("Cotización WEB");
				notificacion.setUserid(LoginController.session.getUser().getIdusuario());
				notificacion.setEstado("PENDIENTE");
				notificacion.setFecha(Utils.obtenerFechaPorFormato(FormatoFecha.YYYYMMDDH24.getFormato()));
				notificacion.setClase(Utils.CLASSDANGER);
				LoginController.conNotificaciones.addNotidicacion(notificacion);
			}

			ResponseListaPrefactura response = LoginController.conStock.listadoPrefactura();
			ResponseListaPrefAprobadas lstOCAprobadas = LoginController.conStock.listadoPrefacturaAprobadas();
			
			OrdenCompra ordenCompra = new OrdenCompra();
			if(response.isStatus()){
				modelo.addAttribute("listaPrefactura", response.getLstPrefacturas());
			}else{
				modelo.addAttribute("listaPrefactura", new ArrayList<ResponsePrefactura>());
			}
			
			ResponseMonedas responseM = LoginController.conParam.listarMonedas();
			if (responseM.isStatus()) {
				modelo.addAttribute("listaMoneda",  responseM.getMonedas() );
			}else {
				modelo.addAttribute("listaMoneda", new ArrayList<Moneda>());

			}
			


			if(lstOCAprobadas.isStatus()){
				modelo.addAttribute("lstAprobadas", lstOCAprobadas.getPrefacAprobadas());
			}else{
				modelo.addAttribute("lstAprobadas", new ArrayList<OCAprobadas>());
			}
			if(ordenCompra!=null){
				modelo.addAttribute("ordenCompra", ordenCompra);
			}
			modelo.addAttribute("itemsCompra", new ArrayList<ItemsOrdenCompra>());
			ResponseFormasPagos responseFP = LoginController.conParam.listarFormasPagos();
			if (responseFP.isStatus()) {
				modelo.addAttribute("formasPagos",  responseFP.getFormasPagos() );
			} else {
				modelo.addAttribute("formasPagos", new FormasPago());
			}

			ResponsePlazos responsePlazos = LoginController.conParam.listarPlazos();
			if (responsePlazos.isStatus()) {
				modelo.addAttribute("plazos",  responsePlazos.getPlazos() );
			} else {
				modelo.addAttribute("plazos", new Plazos());
			}

			return "prefactura";
		} else {
			return "redirect:/";
		}
	}
	
	@GetMapping({ "/facturas-compra" })
	public String inicioOrdenVenta(Model modelo) throws SQLException {
		if (LoginController.session.getToken() != null) {
			modelo.addAttribute("user", LoginController.session.getUser());
			List<FacturasComprasItems> response = Utils.listadoFacturasCompra();
			ResponseListaUsuario lstUser = LoginController.conUser.listadoUsuarios(LoginController.session.getToken());
			modelo.addAttribute("listaFacturaCompra", response);
			modelo.addAttribute("lstUser", lstUser.getUser() );
			ResponseFormasPagos responseFP = LoginController.conParam.listarFormasPagos();
			if (responseFP.isStatus()) {
				modelo.addAttribute("formasPagos",  responseFP.getFormasPagos() );
			} else {
				modelo.addAttribute("formasPagos", new FormasPago());
			}

			ResponsePlazos responsePlazos = LoginController.conParam.listarPlazos();
			if (responsePlazos.isStatus()) {
				modelo.addAttribute("plazos",  responsePlazos.getPlazos() );
			} else {
				modelo.addAttribute("plazos", new Plazos());
			}

			ResponseMonedas responseMonedas = LoginController.conParam.listarMonedas();
			if (responseMonedas.isStatus()) {
				modelo.addAttribute("monedas",  responseMonedas.getMonedas() );
			} else {
				modelo.addAttribute("monedas", new Moneda());
			}

			ResponseDepositos responseDepositos = LoginController.conParam.listarDepositos();
			if (responseDepositos.isStatus()) {
				modelo.addAttribute("depositos",  responseDepositos.getDepositos() );
			} else {
				modelo.addAttribute("depositos", new Depositos());
			}
			
			ResponseMonedas responseM = LoginController.conParam.listarMonedas();
			if (responseM.isStatus()) {
				modelo.addAttribute("listaMoneda",  responseM.getMonedas() );
			}else {
				modelo.addAttribute("listaMoneda", new ArrayList<Moneda>());

			}

			modelo.addAttribute("lstOrdenAprobadas", Utils.obtenerListaOrdenesAprobadas());
			return "facturas-compra";
		} else {
			return "redirect:/";
		}
	}

	@PostMapping({LoginController.ruta+ "/imprimir-orden" })
	public void imprimirOrden(@ModelAttribute("idOrden") int idOrden,@ModelAttribute("idProveedor") int idProveedor,Model modelo,HttpServletResponse responseHttp) throws IOException {
		if (LoginController.session.getToken() != null) {
			modelo.addAttribute("user", LoginController.session.getUser());
			ResponseResultado resultado= new ResponseResultado();
			ResponseOrdenImprimir response= new ResponseOrdenImprimir();
			String pathOrden=LoginController.rutaPDFOrdenes+"\\orden_"+idOrden+".pdf";

			File archivo = new File(pathOrden);
			if (!archivo.exists()) {
				response = ImprimirUtils.imprimirOrdenCompra(idOrden,idProveedor);

				if(response.isStatus()) {
					OutputStream fileOutputStream = new FileOutputStream(pathOrden);
					HtmlConverter.convertToPdf(ImprimirUtils.armarPDFOrdenCompra(response), fileOutputStream);
				}else{
					resultado.setResultado("No se pudo generar el PDF de la orden "+idOrden);
					resultado.setCode(400);
					resultado.setStatus(false);
					String json = (new Gson()).toJson(resultado);
					responseHttp.setContentType("application/json");
					responseHttp.setCharacterEncoding("UTF-8");
					responseHttp.getWriter().write(json);
				}
			}
				resultado.setResultado(pathOrden);
				resultado.setCode(200);
				resultado.setStatus(true);

				String json = (new Gson()).toJson(resultado);
				responseHttp.setContentType("application/json");
				responseHttp.setCharacterEncoding("UTF-8");
				responseHttp.getWriter().write(json);



			}
	}
	
	
	@PostMapping({LoginController.ruta+ "/imprimir-prefactura" })
	public void imprimirPrefactura(@ModelAttribute("idPrefactura") String idPrefactura,@ModelAttribute("idCliente") String idCliente,
			@ModelAttribute("codigo") String codigoFact,Model modelo,HttpServletResponse responseHttp) throws IOException {
		ResponseResultado resultado= new ResponseResultado();

		try {
			if (LoginController.session.getToken() != null) {
				modelo.addAttribute("user", LoginController.session.getUser());
				ResponsePrefacturamprimir response= new ResponsePrefacturamprimir();
				String pathOrden=LoginController.rutaPDFPrefacturas+codigoFact+".pdf";

				File archivo = new File(pathOrden);
				if (!archivo.exists()) {
					response = ImprimirUtils.imprimirPrefactura(idPrefactura,idCliente);

					if(response.isStatus()) {
						OutputStream fileOutputStream = new FileOutputStream(pathOrden);
						HtmlConverter.convertToPdf(ImprimirUtils.armarPDFPrefactura(response,codigoFact), fileOutputStream);
					}else{
						resultado.setResultado("No se pudo generar el PDF de la orden "+idPrefactura);
						resultado.setCode(400);
						resultado.setStatus(false);
						String json = (new Gson()).toJson(resultado);
						responseHttp.setContentType("application/json");
						responseHttp.setCharacterEncoding("UTF-8");
						responseHttp.getWriter().write(json);
					}
				}
					resultado.setResultado(pathOrden);
					resultado.setCode(200);
					resultado.setStatus(true);

					String json = (new Gson()).toJson(resultado);
					responseHttp.setContentType("application/json");
					responseHttp.setCharacterEncoding("UTF-8");
					responseHttp.getWriter().write(json);



				}
		} catch (Exception e) {
			resultado.setResultado(e.getMessage());
			resultado.setCode(404);
			resultado.setStatus(false);

			String json = (new Gson()).toJson(resultado);
			responseHttp.setContentType("application/json");
			responseHttp.setCharacterEncoding("UTF-8");
			responseHttp.getWriter().write(json);
		}
		
		
	}
	
	@PostMapping({ LoginController.ruta+"/crear-factura-compra" })
	public void crearFacturaCompra(@ModelAttribute("accion") String accion, @ModelAttribute("idCompra") int idCompra,
								   @ModelAttribute("idProveedor") int idProveedor, @ModelAttribute("estado") int estado,
								   @ModelAttribute("idOrden") int idOrden, @ModelAttribute("plazo") int plazo,
								   @ModelAttribute("forma_pago") int forma_pago, @ModelAttribute("fecha") String fecha,
								   @ModelAttribute("factura") String factura, @ModelAttribute("receptor") String receptor,
								   @ModelAttribute("deposito") int deposito, @ModelAttribute("moneda") int moneda, Model modelo,
								   HttpServletResponse responseHttp) throws IOException, ParseException, SQLException {
		if (LoginController.session.getToken() != null) {
			modelo.addAttribute("user", LoginController.session.getUser());
			modelo.addAttribute("lstOrdenAprobadas", Utils.obtenerListaOrdenesAprobadas());
			RequestFacturaCompra request = new RequestFacturaCompra();
			FacturaCompra facturaCompra = new FacturaCompra();
			ResponseFacturaCompra response;
			if (accion.equalsIgnoreCase("uProveedor")) {
				response = LoginController.conStock.obtenerFacturaCompraPorID(idCompra);
				if (response.isStatus()) {
					facturaCompra.setId_entrada_compra(response.getFacturaCompra().getId_entrada_compra());
					facturaCompra.setEstado(response.getFacturaCompra().getEstado());
					facturaCompra.setId_proveedor(idProveedor);
					facturaCompra.setId_usuario(LoginController.session.getUser().getIdusuario());
					facturaCompra.setFecha_hora_creado(response.getFacturaCompra().getFecha_hora_creado());
					facturaCompra.setFecha_hora(response.getFacturaCompra().getFecha_hora());
				}
			}

			if (accion.equalsIgnoreCase("inicial")) {
				facturaCompra.setId_entrada_compra(0);
				facturaCompra.setEstado(1);
				facturaCompra.setId_proveedor(4);
				facturaCompra.setId_usuario(LoginController.session.getUser().getIdusuario());
				//Calendar calendar = Calendar.getInstance();
				//SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				facturaCompra.setFecha_hora_creado(Utils.obtenerFechaPorFormato(FormatoFecha.YYYYMMDDH24.getFormato()));
				facturaCompra.setFecha_hora(Utils.obtenerFechaPorFormato(FormatoFecha.YYYYMMDDH24.getFormato()));
			}

			if (accion.equalsIgnoreCase("update")) {
				response = LoginController.conStock.obtenerFacturaCompraPorID(idCompra);
				if (response.isStatus()) {
					facturaCompra.setEstado(estado);
					facturaCompra.setFecha_hora(fecha);
					facturaCompra.setId_plazo(plazo);
					facturaCompra.setNro_factura(factura);
					facturaCompra.setId_cotizacion_moneda("");
					facturaCompra.setId_deposito(deposito);
					facturaCompra.setId_entrada_compra(idCompra);
					facturaCompra.setId_forma_pago(forma_pago);
					facturaCompra.setId_moneda(moneda);
					facturaCompra.setId_orden_compra(idOrden);
					facturaCompra.setId_proveedor(idProveedor);
					facturaCompra.setId_sucursal(LoginController.session.getUser().getEmpleado().getIdsucursal());
					facturaCompra.setId_usuario(LoginController.session.getUser().getIdusuario());
					facturaCompra.setId_usuario_recibio(receptor);
					facturaCompra.setFecha_hora_creado(response.getFacturaCompra().getFecha_hora_creado());
				}
			}

			request.setFacturaCompra(facturaCompra);
			ResponseResultado responseTemp = LoginController.conStock.crearFacturaCompra(request);
			String json;
			if (responseTemp.isStatus()) {
				if (accion.equalsIgnoreCase("inicial")) {
					ResponseResultado responseF = LoginController.conStock.obtenerNumeroFactura(
							facturaCompra.getFecha_hora(), LoginController.session.getUser().getIdusuario());
					if (responseF.isStatus()) {
						responseF.setTemporal(Utils.generarNumeroFactura(2));
						json = (new Gson()).toJson(responseF);
						responseHttp.setContentType("application/json");
						responseHttp.setCharacterEncoding("UTF-8");
						responseHttp.getWriter().write(json);
					}
				} else if (accion.equalsIgnoreCase("update") || accion.equalsIgnoreCase("delete")) {
					if (accion.equalsIgnoreCase("update")) {
						ResponseOrdenCompra responseOrden = LoginController.conStock.obtenerOrdenCompraPorID(idOrden);
						if (responseOrden.isStatus()) {
							new OrdenCompra();
							OrdenCompra ordenCompra = responseOrden.getOrdenCompra();
							ordenCompra.setEstado(4);
							RequestCrearOrdenCompra requestUp = new RequestCrearOrdenCompra();
							requestUp.setOrdenCompra(ordenCompra);
							ResponseResultado responseO = LoginController.conStock.crearOrdenCompra(requestUp);
							responseO.isStatus();
						}
					}

					json = (new Gson()).toJson(responseTemp);
					responseHttp.setContentType("application/json");
					responseHttp.setCharacterEncoding("UTF-8");
					responseHttp.getWriter().write(json);
				}
			} else {
				json = (new Gson()).toJson(responseTemp);
				responseHttp.setContentType("application/json");
				responseHttp.setCharacterEncoding("UTF-8");
				responseHttp.getWriter().write(json);
			}
		} else {
			responseHttp.setContentType("application/json");
			responseHttp.setCharacterEncoding("UTF-8");
			responseHttp.getWriter().write("LoginController.session caducada");
		}
	}

	@PostMapping({ LoginController.ruta+"/buscar-receptor" })
	public void listadoDeUsuarioReceptores(@ModelAttribute("accion") String accion,
										   @ModelAttribute("q") String busqueda, Model modelo, HttpServletResponse responseHttp)
			throws IOException, ParseException {
		if (LoginController.session.getToken() != null) {
			modelo.addAttribute("user", LoginController.session.getUser());
			List<UsuariosReceptores> lstReceptores = new ArrayList<UsuariosReceptores>();
			List<User> usuarios = LoginController.conUser.listadoUsuarios(LoginController.session.getToken()).getUser();
			Iterator var8 = usuarios.iterator();

			while (true) {
				User usuario;
				do {if (!var8.hasNext()) {
					String json = (new Gson()).toJson(lstReceptores);
					responseHttp.setContentType("application/json");
					responseHttp.setCharacterEncoding("UTF-8");
					responseHttp.getWriter().write(json);
					return;
				}

					usuario = (User) var8.next();
				} while (!usuario.getUsuario().toUpperCase().contains(busqueda.toUpperCase())
						&& !usuario.getEmpleado().getNombre().toUpperCase().contains(busqueda.toUpperCase()));

				UsuariosReceptores receptor = new UsuariosReceptores();
				receptor.setId(usuario.getIdusuario());
				receptor.setText(usuario.getEmpleado().getNombre() + " " + usuario.getEmpleado().getApellidos());
				lstReceptores.add(receptor);
				//break;
			}
		} else {
			responseHttp.setContentType("application/json");
			responseHttp.setCharacterEncoding("UTF-8");
			responseHttp.getWriter().write("LoginController.session caducada");
		}
	}

	@PostMapping({ LoginController.ruta+"/crear-orden-compra" })
	public void crearOrdenCompra(@ModelAttribute("accion") String accion, @ModelAttribute("idOrden") int idOrden,
								 @ModelAttribute("idProveedor") int idProveedor, @ModelAttribute("estado") int estado,
								 @ModelAttribute("fecha") String fecha, @ModelAttribute("plazo") int plazo,
								 @ModelAttribute("forma_pago") int forma_pago, @ModelAttribute("ordenCompra") OrdenCompra ordenCompra,
								 Model modelo, HttpServletResponse responseHttp) throws IOException, ParseException {
		if (LoginController.session.getToken() != null) {
			modelo.addAttribute("user", LoginController.session.getUser());
			RequestCrearOrdenCompra request = new RequestCrearOrdenCompra();
			ResponseOrdenCompra response = new ResponseOrdenCompra();
			if (accion.equalsIgnoreCase("uProveedor")) {
				response = LoginController.conStock.obtenerOrdenCompraPorID(idOrden);
				if (response.isStatus()) {
					ordenCompra.setId_usuario(response.getOrdenCompra().getId_usuario());
					ordenCompra.setId_orden_compra(idOrden);
					ordenCompra.setId_proveedor(idProveedor);
					ordenCompra.setId_forma_pago(response.getOrdenCompra().getId_forma_pago());
					ordenCompra.setId_plazo(response.getOrdenCompra().getId_plazo());
					ordenCompra.setEstado(response.getOrdenCompra().getEstado());
					ordenCompra.setFecha_hora(response.getOrdenCompra().getFecha_hora());
					ordenCompra.setFecha_hora_creado(response.getOrdenCompra().getFecha_hora_creado());
				}
			}

			if (accion.equalsIgnoreCase("inicial")) {
				ordenCompra.setId_usuario(LoginController.session.getUser().getIdusuario());
				ordenCompra.setEstado(1);
				ordenCompra.setId_orden_compra(0);
				ordenCompra.setId_forma_pago(1);
				ordenCompra.setId_proveedor(0);
				ordenCompra.setId_plazo(1);
				//Calendar calendar = Calendar.getInstance();
				//SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				ordenCompra.setFecha_hora_creado(Utils.obtenerFechaPorFormato(FormatoFecha.YYYYMMDDH24.getFormato()));
				ordenCompra.setFecha_hora(Utils.obtenerFechaPorFormato(FormatoFecha.YYYYMMDDH24.getFormato()));
				ordenCompra.setId_sucursal(LoginController.session.getUser().getEmpleado().getIdsucursal());
			}

			if (accion.equalsIgnoreCase("update")) {
				response = LoginController.conStock.obtenerOrdenCompraPorID(idOrden);
				if (response.isStatus()) {
					ordenCompra.setId_usuario(response.getOrdenCompra().getId_usuario());
					ordenCompra.setEstado(estado);
					ordenCompra.setId_orden_compra(response.getOrdenCompra().getId_orden_compra());
					ordenCompra.setId_forma_pago(forma_pago);
					ordenCompra.setId_proveedor(idProveedor);
					ordenCompra.setId_plazo(plazo);
					ordenCompra.setFecha_hora_creado(response.getOrdenCompra().getFecha_hora_creado());
					ordenCompra.setFecha_hora(fecha);
					ordenCompra.setId_sucursal(LoginController.session.getUser().getEmpleado().getIdsucursal());
				}
			}

			if (estado == 3 || estado == 5) {
				RequestGuardarModifOC requestM = new RequestGuardarModifOC();
				OrdenCompraModificaciones modif = new OrdenCompraModificaciones();
				modif.setId_orden_compra(idOrden);
				modif.setId_orden_modif(0);
				modif.setId_usuario_autorizo(LoginController.session.getUser().getIdusuario());
				//Calendar calendar = Calendar.getInstance();
				//SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				modif.setFecha_autorizo(Utils.obtenerFechaPorFormato(FormatoFecha.YYYYMMDDH24.getFormato()));
				requestM.setOrdenCompraModif(modif);
				ResponseResultado responseTemp = LoginController.conStock.addModificacionOC(requestM);
				if (!responseTemp.isStatus()) {
					String json = (new Gson()).toJson(responseTemp);
					responseHttp.setContentType("application/json");
					responseHttp.setCharacterEncoding("UTF-8");
					responseHttp.getWriter().write(json);
				}

				String pathOrden=LoginController.rutaPDFOrdenes+"\\orden_"+idOrden+".pdf";

				File archivo = new File(pathOrden);
				if (!archivo.exists()) {
				ResponseOrdenImprimir responsePDF = ImprimirUtils.imprimirOrdenCompra(idOrden, idProveedor);

					if (responsePDF.isStatus()) {
						OutputStream fileOutputStream = new FileOutputStream(pathOrden);
						HtmlConverter.convertToPdf(ImprimirUtils.armarPDFOrdenCompra(responsePDF), fileOutputStream);
					}
				}
			}

			if (accion.equalsIgnoreCase("delete")) {
				response = LoginController.conStock.obtenerOrdenCompraPorID(idOrden);
				if (response.isStatus()) {
					ordenCompra = response.getOrdenCompra();
					//Calendar calendar = Calendar.getInstance();
					//SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
					ordenCompra.setFecha_baja(Utils.obtenerFechaPorFormato(FormatoFecha.YYYYMMDDH24.getFormato()));
				}
			}

			request.setOrdenCompra(ordenCompra);
			ResponseResultado responseTemp = LoginController.conStock.crearOrdenCompra(request);
			String json = "";
			if (responseTemp.isStatus()) {
				if (accion.equalsIgnoreCase("inicial")) {
					ResponseResultado responseN = LoginController.conStock.obtenerNumeroOrden(
							ordenCompra.getFecha_hora(), LoginController.session.getUser().getIdusuario());
					if (responseN.isStatus()) {
						json = (new Gson()).toJson(responseN);
						responseHttp.setContentType("application/json");
						responseHttp.setCharacterEncoding("UTF-8");
						responseHttp.getWriter().write(json);
					}
				} else if (accion.equalsIgnoreCase("update") || accion.equalsIgnoreCase("delete")) {
					json = (new Gson()).toJson(responseTemp);
					responseHttp.setContentType("application/json");
					responseHttp.setCharacterEncoding("UTF-8");
					responseHttp.getWriter().write(json);
				}
			} else {
				json = (new Gson()).toJson(responseTemp);
				responseHttp.setContentType("application/json");
				responseHttp.setCharacterEncoding("UTF-8");
				responseHttp.getWriter().write(json);
			}
		} else {
			responseHttp.setContentType("application/json");
			responseHttp.setCharacterEncoding("UTF-8");
			responseHttp.getWriter().write("LoginController.session caducada");
		}

	}
	
	
	@PostMapping({ LoginController.ruta+"/crear-prefactura-venta" })
	public void crearPrefactura(@ModelAttribute("accion") String accion, @ModelAttribute("idPrefactura") String idPrefactura,
								 @ModelAttribute("idCliente") String idCliente, @ModelAttribute("estado") int estado,
								 @ModelAttribute("fecha") String fecha,
								 @ModelAttribute("forma_pago") int forma_pago, @ModelAttribute("prefactura") Prefactura prefactura,
								 Model modelo, HttpServletResponse responseHttp) throws IOException, ParseException, SQLException {
		if (LoginController.session.getToken() != null) {
			modelo.addAttribute("user", LoginController.session.getUser());
			ResponseListaPrefactura responseOld = LoginController.conStock.listadoPrefactura();
			ResponseMonedas responseMoneda = LoginController.conParam.listarMonedas();
			if (responseMoneda.isStatus()) {
				modelo.addAttribute("listaMoneda",  responseMoneda.getMonedas() );
			}else {
				modelo.addAttribute("listaMoneda", new ArrayList<Moneda>());

			}
			List<ResponsePrefactura> prefact= new ArrayList<ResponsePrefactura>();
			    if(responseOld.getLstPrefacturas()!=null) {
				 prefact=responseOld.getLstPrefacturas().stream().filter(e->e.getEstado().equalsIgnoreCase("BORRADOR")).toList();

			    }
				if(prefact.size()>0 && !prefact.isEmpty() && accion.equalsIgnoreCase("inicial")) {
					ResponsePrefactClient respuesta= new ResponsePrefactClient();
					respuesta.setCode(201);
					ResponsePrefactClient response = LoginController.conStock.obtenerPrefacturaPorID(prefact.get(0).getId_prefactura());
					response.setCode(201);					
					respuesta.setResultado(prefact.get(0).getId_prefactura());				
					String json = (new Gson()).toJson(response);
					responseHttp.setContentType("application/json");
					responseHttp.setCharacterEncoding("UTF-8");
					responseHttp.getWriter().write(json);
				}else {
				
				ResponsePrefactClient response = new ResponsePrefactClient();
				if (accion.equalsIgnoreCase("uCliente")) {
					response = LoginController.conStock.obtenerPrefacturaPorID(idPrefactura);
					if (response.isStatus()) {
						prefactura.setId_usuario(response.getPrefactura().getId_usuario());
						prefactura.setId_prefactura(idPrefactura);
						prefactura.setCod_factura(response.getPrefactura().getCod_factura());
						prefactura.setId_cliente(idCliente);
						prefactura.setId_moneda(response.getPrefactura().getId_moneda());
						prefactura.setId_plazo(response.getPrefactura().getId_plazo());
						prefactura.setEstado(response.getPrefactura().getEstado());
						prefactura.setFecha_hora(response.getPrefactura().getFecha_hora());
						prefactura.setFecha_hora_creado(response.getPrefactura().getFecha_hora_creado());
					}
				}

				if (accion.equalsIgnoreCase("inicial")) {
					prefactura.setId_usuario(LoginController.session.getUser().getIdusuario());
					prefactura.setEstado(1);
					prefactura.setCod_factura(Utils.generarNumeroFactura(1));
					prefactura.setId_prefactura(UUID.randomUUID().toString());
					prefactura.setId_cliente("1");
					prefactura.setId_cliente(idCliente);
					prefactura.setId_plazo(1);
					prefactura.setId_moneda(forma_pago);
					//Calendar calendar = Calendar.getInstance();
					//SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
					prefactura.setFecha_hora_creado(Utils.obtenerFechaPorFormato(FormatoFecha.YYYYMMDDH24.getFormato()));
					prefactura.setFecha_hora(Utils.obtenerFechaPorFormato(FormatoFecha.YYYYMMDD.getFormato()));
					//prefactura.setId_sucursal(LoginController.session.getUser().getEmpleado().getIdsucursal());
				}

				if (accion.equalsIgnoreCase("update")) {
					response = LoginController.conStock.obtenerPrefacturaPorID(idPrefactura);
					if (response.isStatus()) {
						prefactura.setId_usuario(response.getPrefactura().getId_usuario());
						prefactura.setEstado(estado);
						prefactura.setId_prefactura(response.getPrefactura().getId_prefactura());
						prefactura.setId_moneda(forma_pago);
						prefactura.setId_cliente(idCliente);
						prefactura.setId_plazo(1);
						prefactura.setCod_factura(response.getPrefactura().getCod_factura());
						prefactura.setFecha_hora_creado(response.getPrefactura().getFecha_hora_creado());
						prefactura.setFecha_hora(fecha);
						//prefactura.setId_sucursal(LoginController.session.getUser().getEmpleado().getIdsucursal());
					}
				}

				if (estado == 5) {
					ResponseMofPorIdPrefactura responseM= LoginController.conStock.obtenerModificacionPorIdPrefactura(idPrefactura);
					if(responseM.isStatus()) {
						PrefacturaModificaciones modif = responseM.getModificaciones();
						modif.setId_prefactura(modif.getId_prefactura());
						modif.setId_prefactura_modif(modif.getId_prefactura_modif());
						//modif.setId_usuario_autorizo(LoginController.session.getUser().getIdusuario());
						modif.setId_usuario_cancela(LoginController.session.getUser().getIdusuario());
						//Calendar calendar = Calendar.getInstance();
						//SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
						//modif.setFecha_autorizo(Utils.obtenerFechaPorFormato(FormatoFecha.YYYYMMDDH24.getFormato()));
						modif.setFecha_cancela(Utils.obtenerFechaPorFormato(FormatoFecha.YYYYMMDDH24.getFormato()));
						ResponseResultado responseTemp = LoginController.conStock.addModificacionPrefactura(modif);
						if (!responseTemp.isStatus()) {
							String json = (new Gson()).toJson(responseTemp);
							responseHttp.setContentType("application/json");
							responseHttp.setCharacterEncoding("UTF-8");
							responseHttp.getWriter().write(json);
						}
					}else {
						PrefacturaModificaciones modif = new PrefacturaModificaciones();
						modif.setId_prefactura(idPrefactura);
						modif.setId_prefactura_modif(0);
						modif.setId_usuario_cancela(LoginController.session.getUser().getIdusuario());
						//Calendar calendar = Calendar.getInstance();
						//SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
						modif.setFecha_cancela(Utils.obtenerFechaPorFormato(FormatoFecha.YYYYMMDDH24.getFormato()));
						ResponseResultado responseTemp = LoginController.conStock.addModificacionPrefactura(modif);
						if (!responseTemp.isStatus()) {
							String json = (new Gson()).toJson(responseTemp);
							responseHttp.setContentType("application/json");
							responseHttp.setCharacterEncoding("UTF-8");
							responseHttp.getWriter().write(json);
						}
					}
					

					
				}else if (estado == 3){
					PrefacturaModificaciones modif = new PrefacturaModificaciones();
					modif.setId_prefactura(idPrefactura);
					modif.setId_prefactura_modif(0);
					modif.setId_usuario_autorizo(LoginController.session.getUser().getIdusuario());
					//Calendar calendar = Calendar.getInstance();
					//SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
					modif.setFecha_autorizo(Utils.obtenerFechaPorFormato(FormatoFecha.YYYYMMDDH24.getFormato()));
					ResponseResultado responseTemp = LoginController.conStock.addModificacionPrefactura(modif);
					if (!responseTemp.isStatus()) {
						String json = (new Gson()).toJson(responseTemp);
						responseHttp.setContentType("application/json");
						responseHttp.setCharacterEncoding("UTF-8");
						responseHttp.getWriter().write(json);
					}
					
					String pathPrefactura=LoginController.rutaPDFPrefacturas+"prefactura_"+idPrefactura+".pdf";

					File archivo = new File(pathPrefactura);
					//if (archivo.exists()) {
					ResponsePrefacturamprimir responsePDF = ImprimirUtils.imprimirPrefactura(idPrefactura, idCliente);

						if (responsePDF.isStatus()) {
							OutputStream fileOutputStream = new FileOutputStream(pathPrefactura);
							HtmlConverter.convertToPdf(ImprimirUtils.armarPDFPrefactura(responsePDF,prefactura.getCod_factura()), fileOutputStream);
						}
					//}
				}

				if (accion.equalsIgnoreCase("delete")) {
					response = LoginController.conStock.obtenerPrefacturaPorID(idPrefactura);
					if (response.isStatus()) {
						prefactura = response.getPrefactura();
						//Calendar calendar = Calendar.getInstance();
						//SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
						prefactura.setFecha_baja(Utils.obtenerFechaPorFormato(FormatoFecha.YYYYMMDDH24.getFormato()));
					}
				}

				//request.set (prefactura);
				ResponseResultado responseTemp = LoginController.conStock.crearPrefactura(prefactura);
				String json = "";
				if (responseTemp.isStatus()) {
					if (accion.equalsIgnoreCase("inicial")) {
						/*ResponseResultado responseN = LoginController.conStock.obtenerNumeroPrefactura(
								prefactura.getFecha_hora(), LoginController.session.getUser().getIdusuario());*/
						//if (responseN.isStatus()) {
						ResponseResultado responseN = new ResponseResultado();
						responseN.setCode(200);
						responseN.setResultado(prefactura.getCod_factura());
							json = (new Gson()).toJson(responseN);
							responseHttp.setContentType("application/json");
							responseHttp.setCharacterEncoding("UTF-8");
							responseHttp.getWriter().write(json);
						//}
					} else if (accion.equalsIgnoreCase("update") || accion.equalsIgnoreCase("delete")) {
						json = (new Gson()).toJson(responseTemp);
						responseHttp.setContentType("application/json");
						responseHttp.setCharacterEncoding("UTF-8");
						responseHttp.getWriter().write(json);
					}
				} else {
					json = (new Gson()).toJson(responseTemp);
					responseHttp.setContentType("application/json");
					responseHttp.setCharacterEncoding("UTF-8");
					responseHttp.getWriter().write(json);
				}
			}
			
		} else {
			responseHttp.setContentType("application/json");
			responseHttp.setCharacterEncoding("UTF-8");
			responseHttp.getWriter().write("LoginController.session caducada");
		}

	}


	@PostMapping({ LoginController.ruta+"/detalle-orden-compra" })
	public void insertarDetalleOrden(@ModelAttribute("accion") String accion, @ModelAttribute("idOrden") int idOrden,
									 @ModelAttribute("idProducto") String idProducto, @ModelAttribute("cantidad") int cantidad,
									 @ModelAttribute("importe") double importe, Model modelo, HttpServletResponse responseHttp)
			throws IOException, ParseException {
		if (LoginController.session.getToken() != null) {
			modelo.addAttribute("user", LoginController.session.getUser());
			RequestGuardarDetalleOC request = new RequestGuardarDetalleOC();
			DetalleOrdenCompra detalle = new DetalleOrdenCompra();
			if (accion.equalsIgnoreCase("insert")) {
				ResponseResultado responseIdMoneda = LoginController.conParam.monedaPorDefecto();
				detalle.setCantidad(cantidad);
				detalle.setImporte(importe);
				detalle.setId_orden_de_compra_detalle(0);
				detalle.setId_producto(idProducto);
				detalle.setId_moneda(Integer.parseInt(responseIdMoneda.getResultado()));
				detalle.setId_orden_compra(idOrden);
				request.setDetalleOrdenCompra(detalle);
				ResponseResultado response = LoginController.conStock.addDetalleOC(request);

				String json = (new Gson()).toJson(response);
				responseHttp.setContentType("application/json");
				responseHttp.setCharacterEncoding("UTF-8");
				responseHttp.getWriter().write(json);

			}
		} else {
			responseHttp.setContentType("application/json");
			responseHttp.setCharacterEncoding("UTF-8");
			responseHttp.getWriter().write("LoginController.session caducada");
		}

	}

	@PostMapping({ LoginController.ruta+"/detalle-prefactura" })
	public void insertarDetallePrefactura(@ModelAttribute("accion") String accion, @ModelAttribute("id") String id,
									 @ModelAttribute("idProducto") String idProducto, @ModelAttribute("cantidad") int cantidad,
									 @ModelAttribute("importe") double importe, Model modelo, HttpServletResponse responseHttp)
			throws IOException, ParseException {
		if (LoginController.session.getToken() != null) {
			modelo.addAttribute("user", LoginController.session.getUser());
			PrefacturaDetalle request = new PrefacturaDetalle();
			PrefacturaDetalle detalle = new PrefacturaDetalle();
			if (accion.equalsIgnoreCase("insert")) {
				ResponseResultado responseIdMoneda = LoginController.conParam.monedaPorDefecto();
				detalle.setCantidad(cantidad);
				detalle.setImporte(importe);
				detalle.setId_prefactura_detalle(0);
				detalle.setId_producto(idProducto);
				detalle.setId_moneda(Integer.parseInt(responseIdMoneda.getResultado()));
				detalle.setId_prefactura(id);
				ResponseResultado response = LoginController.conStock.addDetallePrefactura(detalle);

				String json = (new Gson()).toJson(response);
				responseHttp.setContentType("application/json");
				responseHttp.setCharacterEncoding("UTF-8");
				responseHttp.getWriter().write(json);

			}
		} else {
			responseHttp.setContentType("application/json");
			responseHttp.setCharacterEncoding("UTF-8");
			responseHttp.getWriter().write("LoginController.session caducada");
		}

	}
	
	@PostMapping({ LoginController.ruta+"/detalle-factura-compra" })
	public void insertarDetalleFacturaCompra(@ModelAttribute("accion") String accion,
											 @ModelAttribute("idCompra") int idCompra, @ModelAttribute("idProducto") String idProducto,
											 @ModelAttribute("cantidad") int cantidad, @ModelAttribute("importe") double importe, Model modelo,
											 HttpServletResponse responseHttp) throws IOException, ParseException {
		if (LoginController.session.getToken() != null) {
			modelo.addAttribute("user", LoginController.session.getUser());
			RequestGuardarDetalleFC request = new RequestGuardarDetalleFC();
			DetalleFacturaCompra detalle = new DetalleFacturaCompra();
			if (accion.equalsIgnoreCase("insert")) {
				ResponseResultado responseIdMoneda = LoginController.conParam.monedaPorDefecto();
				detalle.setCantidad(cantidad);
				detalle.setImporte(importe);
				detalle.setId_entrada_compra_detalle(0);
				detalle.setId_producto(idProducto);
				detalle.setId_moneda(Integer.parseInt(responseIdMoneda.getResultado()));
				detalle.setId_entrada_compra(idCompra);
				request.setDetalleFacturaCompra(detalle);
				ResponseResultado response = LoginController.conStock.addDetalleFC(request);

				String json = (new Gson()).toJson(response);
				responseHttp.setContentType("application/json");
				responseHttp.setCharacterEncoding("UTF-8");
				responseHttp.getWriter().write(json);

			}
		} else {
			responseHttp.setContentType("application/json");
			responseHttp.setCharacterEncoding("UTF-8");
			responseHttp.getWriter().write("LoginController.session caducada");
		}

	}

	@PostMapping({ LoginController.ruta+"/editar-orden-compra" })
	public void buscarOrdenCompraID(@ModelAttribute("accion") String accion, @ModelAttribute("idOrden") int idOrden,
									Model modelo, HttpServletResponse responseHttp) throws IOException, ParseException {
		if (LoginController.session.getToken() != null) {
			modelo.addAttribute("user", LoginController.session.getUser());
			ResponseOrdenCompra response = LoginController.conStock.obtenerOrdenCompraPorID(idOrden);
			String json;
			if (response.isStatus()) {
				json = (new Gson()).toJson(response);
				responseHttp.setContentType("application/json");
				responseHttp.setCharacterEncoding("UTF-8");
				responseHttp.getWriter().write(json);
			} else {
				json = (new Gson()).toJson(response);
				responseHttp.setContentType("application/json");
				responseHttp.setCharacterEncoding("UTF-8");
				responseHttp.getWriter().write(json);
			}
		}

	}
	
	@PostMapping({ LoginController.ruta+"/editar-prefactura" })
	public void buscarPrefacturaPorID(@ModelAttribute("accion") String accion, @ModelAttribute("id") String id,
									Model modelo, HttpServletResponse responseHttp) throws IOException, ParseException {
		if (LoginController.session.getToken() != null) {
			modelo.addAttribute("user", LoginController.session.getUser());
			ResponsePrefactClient response = LoginController.conStock.obtenerPrefacturaPorID(id);
			String json;
			if (response.isStatus()) {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				Date fechaConvertida = null;
			    fechaConvertida = (Date) dateFormat.parse(response.getPrefactura().getFecha_hora());
			    String fechaComoCadena = dateFormat.format(fechaConvertida);
				response.getPrefactura().setFecha_hora(fechaComoCadena);
				json = (new Gson()).toJson(response);
				responseHttp.setContentType("application/json");
				responseHttp.setCharacterEncoding("UTF-8");
				responseHttp.getWriter().write(json);
			} else {
				json = (new Gson()).toJson(response);
				responseHttp.setContentType("application/json");
				responseHttp.setCharacterEncoding("UTF-8");
				responseHttp.getWriter().write(json);
			}
		}

	}
	
	@PostMapping({ LoginController.ruta+"/editar-factura-compra" })
	public void buscarFacturaCompraID(@ModelAttribute("accion") String accion, @ModelAttribute("idCompra") int idCompra,
									  Model modelo, HttpServletResponse responseHttp) throws IOException, ParseException, SQLException {
		if (LoginController.session.getToken() != null) {
			modelo.addAttribute("user", LoginController.session.getUser());
			modelo.addAttribute("lstOrdenAprobadas", Utils.obtenerListaOrdenesAprobadas());
			ResponseFacturaCompra response = LoginController.conStock.obtenerFacturaCompraPorID(idCompra);
			String json;
			if (response.isStatus()) {
				json = (new Gson()).toJson(response);
				responseHttp.setContentType("application/json");
				responseHttp.setCharacterEncoding("UTF-8");
				responseHttp.getWriter().write(json);
			} else {
				json = (new Gson()).toJson(response);
				responseHttp.setContentType("application/json");
				responseHttp.setCharacterEncoding("UTF-8");
				responseHttp.getWriter().write(json);
			}
		}

	}

	@PostMapping({ LoginController.ruta+"/buscar-proveedor" })
	public void buscarProveedorPorId(@ModelAttribute("accion") String accion, @ModelAttribute("idProveedor") int id,
									 Model modelo, HttpServletResponse responseHttp) throws IOException, ParseException {
		if (LoginController.session.getToken() != null) {
			modelo.addAttribute("user", LoginController.session.getUser());
			ResponseProveedor response = LoginController.conParam.obtenerProveedorPorId(id);
			String json;
			if (response.isStatus()) {
				json = (new Gson()).toJson(response);
				responseHttp.setContentType("application/json");
				responseHttp.setCharacterEncoding("UTF-8");
				responseHttp.getWriter().write(json);
			} else {
				json = (new Gson()).toJson(response);
				responseHttp.setContentType("application/json");
				responseHttp.setCharacterEncoding("UTF-8");
				responseHttp.getWriter().write(json);
			}
		}

	}
	
	@PostMapping({ LoginController.ruta+"/buscar-cliente" })
	public void buscarClientePorId(@ModelAttribute("accion") String accion, @ModelAttribute("idCliente") String id,
									 Model modelo, HttpServletResponse responseHttp) throws IOException, ParseException {
		if (LoginController.session.getToken() != null) {
			modelo.addAttribute("user", LoginController.session.getUser());
			ResponseCliente response = LoginController.conParam.obtenerClientePorId(id);
			String json;
			if (response.isStatus()) {
				json = (new Gson()).toJson(response);
				responseHttp.setContentType("application/json");
				responseHttp.setCharacterEncoding("UTF-8");
				responseHttp.getWriter().write(json);
			} else {
				json = (new Gson()).toJson(response);
				responseHttp.setContentType("application/json");
				responseHttp.setCharacterEncoding("UTF-8");
				responseHttp.getWriter().write(json);
			}
		}

	}


	@PostMapping({ LoginController.ruta+"/items-compra" })
	public void itemsOrdenCompra(@ModelAttribute("accion") String accion, @ModelAttribute("idOrden") String idOrden,
								 @ModelAttribute("evento") String evento, Model modelo, HttpServletResponse responseHttp)
			throws IOException, ParseException, SQLException {
		if (LoginController.session.getToken() != null) {
			modelo.addAttribute("user", LoginController.session.getUser());
			String tabla = "";
			String totales = "";
			int itemPos = 0;
			double totalSuma = 0.0D;
			String dis = "enabled";
			String modificar = "";
			String modal = "";
			if (accion.equalsIgnoreCase("editar")) {
				dis = "disabled";
				modificar = "class='not-active'";
			}

			List<ItemsOrdenCompra> itemsCompra = new ArrayList();
			if (evento.equalsIgnoreCase("factura")) {
				itemsCompra = Utils.listadoItemsFacturas(idOrden);
				modal = "addFactura";
			}

			if (evento.equalsIgnoreCase("orden")) {
				itemsCompra = Utils.listadoItemsCompras(idOrden);
				modal = "addItem";
			}
			
			if (evento.equalsIgnoreCase("prefactura")) {
				itemsCompra = Utils.listadoItemsPrefactura(idOrden);
				modal = "addItem";
			}

			if (((List) itemsCompra).isEmpty()) {
				tabla = "<tr>\r\n<td class='text-center'></td>\r\n<td class='text-center'></td>\r\n<td class='text-center'></td>\r\n<td></td>\r\n<td class='text-right'></td>\r\n<td class='text-right'></td>\r\n<td class='text-right'><a href=\"#\" "
						+ modificar
						+ " onclick=\"eliminar_item('')\" ><img src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAMAAAAoLQ9TAAAAeFBMVEUAAADnTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDx+VWpeAAAAJ3RSTlMAAQIFCAkPERQYGi40TVRVVlhZaHR8g4WPl5qdtb7Hys7R19rr7e97kMnEAAAAaklEQVQYV7XOSQKCMBQE0UpQwfkrSJwCKmDf/4YuVOIF7F29VQOA897xs50k1aknmnmfPRfvWptdBjOz29Vs46B6aFx/cEBIEAEIamhWc3EcIRKXhQj/hX47nGvt7x8o07ETANP2210OvABwcxH233o1TgAAAABJRU5ErkJggg==\"></a></td>\r\n"
						+ "</tr><tr><td colspan='7'>\r\n"
						+ "<button id=\"itemsBtt\" type=\"button\" class=\"btn btn-info btn-sm\" style =\"float: left;\"  onclick=\"abrirModal('"
						+ modal + "')\" " + dis
						+ " ><span class=\"glyphicon glyphicon-plus\"></span> Agregar ítem</button>\r\n"
						+ "</td></tr><tr><td colspan='5' class='text-right'>\r\n" + "<h4>TOTAL </h4></td>\r\n"
						+ "<th class='text-right'>\r\n"
						+ "<input type=\"hidden\" id=\"itemsTabla\" name=\"itemsTabla\" value='" + itemPos + "'>\r\n"
						+ "<h4></h4>\r\n" + "</th><td></td></tr>";
			} else {
				ItemsOrdenCompra items;
				for (Iterator var16 = ((List) itemsCompra).iterator(); var16.hasNext(); totalSuma += items.getTotal()) {
					items = (ItemsOrdenCompra) var16.next();
					++itemPos;
					tabla = tabla + "<tr>\r\n<td class='text-center'>" + itemPos + "</td>\r\n"
							+ "<td class='text-center'>" + items.getCatidad() + "</td>\r\n" + "<td class='text-center'>"
							+ items.getCodigo() + "</td>\r\n" + "<td>" + items.getNombre() + "</td>\r\n"
							+ "<td class='text-right'>" + items.getSimboloUM() + "</td>\r\n" + "<td class='text-right'>"
							+ items.getTotal() + "</td>\r\n" + "<td class='text-right'><a href=\"#\" " + modificar
							+ " onclick=\"eliminar_item('" + items.getId_detalle() + "','" + idOrden
							+ "')\" ><img src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAMAAAAoLQ9TAAAAeFBMVEUAAADnTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDx+VWpeAAAAJ3RSTlMAAQIFCAkPERQYGi40TVRVVlhZaHR8g4WPl5qdtb7Hys7R19rr7e97kMnEAAAAaklEQVQYV7XOSQKCMBQE0UpQwfkrSJwCKmDf/4YuVOIF7F29VQOA897xs50k1aknmnmfPRfvWptdBjOz29Vs46B6aFx/cEBIEAEIamhWc3EcIRKXhQj/hX47nGvt7x8o07ETANP2210OvABwcxH233o1TgAAAABJRU5ErkJggg==\"></a></td></tr>";
				}
				double iva=Utils.obtenerIvaACalcularProducto(itemsCompra,idOrden);
				double totalMasIva=iva+totalSuma;
				//Utils.guardarIvaPrefactura(idOrden, iva, totalMasIva);
				
				totales = "<tr><td colspan='7'>\r\n<button id=\"itemsBtt\" type=\"button\" class=\"btn btn-info btn-sm\" style =\"float: left;\"  onclick=\"abrirModal('"
						+ modal + "')\" " + dis
						+ " ><span class=\"glyphicon glyphicon-plus\"></span> Agregar ítem</button>\r\n"
						+ "</td></tr>"
						+ "<tr><td colspan='5' class='text-right'>\r\n" + "<h4>SUB-TOTAL: </h4></td>\r\n"
						+ "<th class='text-right'>\r\n" + "<h4>" + totalSuma + "</h4>\r\n"
						+ "</th><td></td></tr>"
						+ "<tr><td colspan='5' class='text-right'>\r\n" + "<h4>IVA: </h4></td>\r\n"
								+ "<th class='text-right'>\r\n" + "<h4>" + iva + "</h4>\r\n"
								+ "</th><td></td></tr>"
						+ "<tr><td colspan='5' class='text-right'>\r\n" + "<h4>TOTAL: </h4></td>\r\n"
						+ "<th class='text-right'>\r\n" + "<h4>" + totalMasIva + "</h4>\r\n"
						+ "<input type=\"hidden\" id=\"itemsTabla\" name=\"itemsTabla\" value='" + itemPos + "'>\r\n"
						+ "</th><td></td></tr>";
			}

			String resultado = tabla + totales;
			String json = (new Gson()).toJson(resultado);
			responseHttp.setContentType("application/json");
			responseHttp.setCharacterEncoding("UTF-8");
			responseHttp.getWriter().write(json);
		} else {
			responseHttp.setContentType("application/json");
			responseHttp.setCharacterEncoding("UTF-8");
			responseHttp.getWriter().write("LoginController.session caducada");
		}

	}

	@PostMapping({ LoginController.ruta+"/delete-items-compra" })
	public void deleteItemOrdenCompra(@ModelAttribute("accion") String accion, @ModelAttribute("id") int id,
									  Model modelo, HttpServletResponse responseHttp) throws IOException, ParseException {
		if (LoginController.session.getToken() != null) {
			modelo.addAttribute("user", LoginController.session.getUser());
			if (accion.equalsIgnoreCase("eliminar")) {
				ResponseResultado response = LoginController.conStock.eliminarItemOC(id);
				String json = (new Gson()).toJson(response);
				responseHttp.setContentType("application/json");
				responseHttp.setCharacterEncoding("UTF-8");
				responseHttp.getWriter().write(json);
			}
		} else {
			responseHttp.setContentType("application/json");
			responseHttp.setCharacterEncoding("UTF-8");
			responseHttp.getWriter().write("LoginController.session caducada");
		}

	}

	@PostMapping({ LoginController.ruta+"/delete-items-prefactura" })
	public void deleteItemPrefactura(@ModelAttribute("accion") String accion, @ModelAttribute("id") int id,
									  Model modelo, HttpServletResponse responseHttp) throws IOException, ParseException {
		if (LoginController.session.getToken() != null) {
			modelo.addAttribute("user", LoginController.session.getUser());
			if (accion.equalsIgnoreCase("eliminar")) {
				ResponseResultado response = LoginController.conStock.eliminarItemPrefactura(id);
				String json = (new Gson()).toJson(response);
				responseHttp.setContentType("application/json");
				responseHttp.setCharacterEncoding("UTF-8");
				responseHttp.getWriter().write(json);
			}
		} else {
			responseHttp.setContentType("application/json");
			responseHttp.setCharacterEncoding("UTF-8");
			responseHttp.getWriter().write("LoginController.session caducada");
		}

	}
	@PostMapping({ LoginController.ruta+"/items-proveedor" })
	public void itemsProveedores(@ModelAttribute("accion") String accion, @ModelAttribute("q") String busqueda,
								 Model modelo, HttpServletResponse responseHttp) throws IOException, ParseException, SQLException {
		if (LoginController.session.getToken() != null) {
			modelo.addAttribute("user", LoginController.session.getUser());
			List<ItemsProveedore> itemsCompra = Utils.listadoItemsProveedores(busqueda);
			String json = (new Gson()).toJson(itemsCompra);
			responseHttp.setContentType("application/json");
			responseHttp.setCharacterEncoding("UTF-8");
			responseHttp.getWriter().write(json);
		} else {
			responseHttp.setContentType("application/json");
			responseHttp.setCharacterEncoding("UTF-8");
			responseHttp.getWriter().write("LoginController.session caducada");
		}

	}
	
	@PostMapping({ LoginController.ruta+"/items-cliente" })
	public void itemsCliente(@ModelAttribute("accion") String accion, @ModelAttribute("q") String busqueda,
								 Model modelo, HttpServletResponse responseHttp) throws IOException, ParseException, SQLException {
		if (LoginController.session.getToken() != null) {
			modelo.addAttribute("user", LoginController.session.getUser());
			List<ItemsProveedore> itemsCompra = Utils.listadoItemsClientes(busqueda);
			String json = (new Gson()).toJson(itemsCompra);
			responseHttp.setContentType("application/json");
			responseHttp.setCharacterEncoding("UTF-8");
			responseHttp.getWriter().write(json);
		} else {
			responseHttp.setContentType("application/json");
			responseHttp.setCharacterEncoding("UTF-8");
			responseHttp.getWriter().write("LoginController.session caducada");
		}

	}

	@PostMapping({ LoginController.ruta+"/items-productos" })
	public void itemsProductos(@ModelAttribute("accion") String accion, @ModelAttribute("q") String busqueda, @ModelAttribute("evento") int evento,
			 @ModelAttribute("tipo") int moneda, Model modelo, HttpServletResponse responseHttp) throws IOException, ParseException, SQLException {
		if (LoginController.session.getToken() != null) {
			List<ItemsProducto> itemsProductosNew= new ArrayList<ItemsProducto>();

			if(LoginController.session.getOepnDay().isEmpty()) {				 
                ResponseOpenDay aperturaDia = LoginController.conCaja.openDay(Utils.obtenerFechaPorFormato(FormatoFecha.YYYYMMDD.getFormato()));
                if(aperturaDia.isStatus()) {
                	LoginController.session.setOepnDay(aperturaDia.getOpen());
				}
			}
			
			
			modelo.addAttribute("user", LoginController.session.getUser());
			List<ItemsProducto> itemsProductos = Utils.listadoItemsProductos(busqueda,evento);
			for (ItemsProducto itemsProducto : itemsProductos) {
				ItemsProducto neow = itemsProducto;
				if(itemsProducto.getId_moneda()!=moneda && moneda==1) {
					neow.setPrecio(Utils.formatearDecimales(itemsProducto.getPrecio()*LoginController.session.getOepnDay().stream().filter(e->e.getIdmoneda()==moneda).findAny().get().getValorventa(),2)); 

				}else {
					//Validar que este abierto el dia 
					neow.setPrecio(Utils.formatearDecimales(itemsProducto.getPrecio()/ LoginController.session.getOepnDay().stream().filter(e->e.getIdmoneda()==moneda).findAny().get().getValorventa() ,2)); 

				}
				itemsProductosNew.add(neow);
			}			
			String json = (new Gson()).toJson(itemsProductosNew);
			responseHttp.setContentType("application/json");
			responseHttp.setCharacterEncoding("UTF-8");
			responseHttp.getWriter().write(json);
			
			
		} else {
			responseHttp.setContentType("application/json");
			responseHttp.setCharacterEncoding("UTF-8");
			responseHttp.getWriter().write("LoginController.session caducada");
		}

	}
	
	
	
	
	@PostMapping({LoginController.ruta+"/send-mail-prefactura" })
	@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST })
	@ResponseStatus(HttpStatus.CREATED)
	public void enviarPDF(@RequestParam("id") String idDocumento,@RequestParam("codigo") String codigo,
						  Model modelo,HttpServletResponse responseHttp) throws IOException {
		
		ResponsePrefactClient response= LoginController.conStock.obtenerPrefacturaPorID(idDocumento);
		if(response.isStatus()) {
			ResponseCliente cliente = LoginController.conStock.obtenerClientePorId(response.getPrefactura().getId_cliente());
			if(cliente.isStatus()) {
				//ENVIO MAIL 
				Email confirmar= new Email();
				confirmar.setEmail(cliente.getCliente().getEmail());
				confirmar.setName(cliente.getCliente().getNombres());
				confirmar.setSubject("Confirmar solicitud.");
				confirmar.setAdjunto(true);
				confirmar.setArchivo(Utils.convertFileToBase64(LoginController.rutaPDFPrefacturas+""+codigo+".pdf") );
				confirmar.setNombreArchivo(codigo+".pdf");
				String mensajeEnvio=  " <html lang=\"en\">\r\n"
						+ "<head>\r\n"
						+ "    <meta charset=\"UTF-8\">\r\n"
						+ "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\r\n"
						+ "    <title>Confirmación de Pedido</title>\r\n"
						+ "    <style>\r\n"
						+ "        /* Estilos para el botón */\r\n"
						+ "        .btn {\r\n"
						+ "            display: inline-block;\r\n"
						+ "            font-weight: 400;\r\n"
						+ "            color: #ffffff;\r\n"
						+ "            text-align: center;\r\n"
						+ "            vertical-align: middle;\r\n"
						+ "            user-select: none;\r\n"
						+ "            background-color: #007bff;\r\n"
						+ "            border: 1px solid transparent;\r\n"
						+ "            padding: 0.5rem 1rem;\r\n"
						+ "            font-size: 1rem;\r\n"
						+ "            line-height: 1.5;\r\n"
						+ "            border-radius: 0.25rem;\r\n"
						+ "            text-decoration: none;\r\n"
						+ "        }\r\n"
						+ "\r\n"
						+ "        .btn:hover {\r\n"
						+ "            background-color: #0056b3;\r\n"
						+ "            color: #ffffff;\r\n"
						+ "        }\r\n"
						+ "    </style>\r\n"
						+ "</head>\r\n"
						+ "<body style=\"font-family: Arial, sans-serif; padding: 20px;\">\r\n"
						+ "    <table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"margin: auto;\">\r\n"
						+ "        <tr>\r\n"
						+ "            <td align=\"center\">\r\n"
						+ "                <h2 style=\"color: #007bff;\">Confirmación de Pedido</h2>\r\n"
						+ "                <p>Estimado(a) "+cliente.getCliente().getNombres()+",</p>\r\n"
						+ "                <p>Gracias por su confianza en el equipo de AYALA IT. Por favor, haga clic en el siguiente botón para confirmar su pedido:</p>\r\n"
						+ "                <table cellpadding=\"0\" cellspacing=\"0\">\r\n"
						+ "                    <tr>\r\n"
						+ "                        <td align=\"center\">\r\n"
						+ "                            <a class=\"btn\" href=\""+LoginController.conStock.hostStock+"/prefactura/confirmar-pedido?id="+idDocumento+"\" style=\"background-color: #007bff; color: #ffffff; text-decoration: none; padding: 10px 20px; border-radius: 5px; display: inline-block;\">Confirmar Pedido</a>\r\n"
						+ "                        </td>\r\n"
						+ "                    </tr>\r\n"
						+ "                </table>\r\n"
						+ "                <p>Si el botón no funciona, puede copiar y pegar el siguiente enlace en su navegador:</p>\r\n"
						+ "                <p><a href=\""+LoginController.conStock.hostStock+"/prefactura/confirmar-pedido?id="+idDocumento+"\" style=\"color: #007bff; text-decoration: underline;\">http://localhost:8082/prefactura/confirmar-pedido?id="+idDocumento+"</a></p>\r\n"
						+ "                <p>Gracias,<br>Equipo de AYALA IT</p>\r\n"
						+ "					<p><img src=\"data:image/png;base64,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\" width=\"150\">\r\n"
						+ "      </p>\r\n"
						+ "            </td>\r\n"
						+ "        </tr>\r\n"
						+ "    </table>\r\n"
						+ "</body>\r\n"
						+ "</html>\r\n"
						+ " ";
				confirmar.setMessage(mensajeEnvio);
				
				ResponseResultado envioMail= LoginController.conStock.enviarMailConfirmacion(confirmar);
				
				String json = new Gson().toJson(envioMail);
				responseHttp.setContentType("application/json");
				responseHttp.setCharacterEncoding("UTF-8");
				responseHttp.getWriter().write(json);
			}
			
		}
			
		
				
}
}