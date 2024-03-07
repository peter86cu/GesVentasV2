package com.ayalait.gesventas.controller;

import com.ayalait.gesventas.imprimir.ImprimirUtils;
import com.ayalait.gesventas.imprimir.ResponseOrdenImprimir;
import com.ayalait.gesventas.imprimir.ResponsePrefacturamprimir;
import com.google.gson.Gson;

import java.io.*;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


import com.ayalait.gesventas.request.*;
import com.ayalait.gesventas.utils.*;
import com.ayalait.modelo.*;
import com.ayalait.response.*;
import com.ayalait.utils.ResponsePrefactura;
import com.itextpdf.html2pdf.HtmlConverter;
import com.multishop.modelo.Prefactura;
import com.multishop.modelo.PrefacturaDetalle;
import com.multishop.modelo.PrefacturaModificaciones;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletResponse;

@Controller
public class OrdenesFacturasController {

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

			return "/orden-compra";
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

			return "/prefactura";
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
			String pathOrden=System.getProperty("user.dir")+"\\src\\main\\resources\\static\\pdf\\ordenes\\orden_"+idOrden+".pdf";

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
	public void imprimirPrefactura(@ModelAttribute("idPrefactura") int idPrefactura,@ModelAttribute("idCliente") int idCliente,Model modelo,HttpServletResponse responseHttp) throws IOException {
		if (LoginController.session.getToken() != null) {
			modelo.addAttribute("user", LoginController.session.getUser());
			ResponseResultado resultado= new ResponseResultado();
			ResponsePrefacturamprimir response= new ResponsePrefacturamprimir();
			String pathOrden=LoginController.rutaPDFPrefacturas+"prefactura_"+idPrefactura+".pdf";

			File archivo = new File(pathOrden);
			if (!archivo.exists()) {
				response = ImprimirUtils.imprimirPrefactura(idPrefactura,idCliente);

				if(response.isStatus()) {
					OutputStream fileOutputStream = new FileOutputStream(pathOrden);
					HtmlConverter.convertToPdf(ImprimirUtils.armarPDFPrefactura(response), fileOutputStream);
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
						responseF.setTemporal(Utils.generarNumeroFactura());
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

				String pathOrden=System.getProperty("user.dir")+"\\src\\main\\resources\\static\\pdf\\ordenes\\orden_"+idOrden+".pdf";

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
	public void crearPrefactura(@ModelAttribute("accion") String accion, @ModelAttribute("id") int id,
								 @ModelAttribute("idCliente") int idCliente, @ModelAttribute("estado") int estado,
								 @ModelAttribute("fecha") String fecha, @ModelAttribute("plazo") int plazo,
								 @ModelAttribute("forma_pago") int forma_pago, @ModelAttribute("prefactura") Prefactura prefactura,
								 Model modelo, HttpServletResponse responseHttp) throws IOException, ParseException {
		if (LoginController.session.getToken() != null) {
			modelo.addAttribute("user", LoginController.session.getUser());
			//RequestCrearOrdenCompra request = new RequestCrearOrdenCompra();
			//Prefactura request= new Prefactura();
			ResponsePrefactClient response = new ResponsePrefactClient();
			if (accion.equalsIgnoreCase("uCliente")) {
				response = LoginController.conStock.obtenerPrefacturaPorID(id);
				if (response.isStatus()) {
					prefactura.setId_usuario(response.getPrefactura().getId_usuario());
					prefactura.setId_prefactura(id);
					prefactura.setId_cliente(idCliente);
					prefactura.setId_forma_pago(response.getPrefactura().getId_forma_pago());
					prefactura.setId_plazo(response.getPrefactura().getId_plazo());
					prefactura.setEstado(response.getPrefactura().getEstado());
					prefactura.setFecha_hora(response.getPrefactura().getFecha_hora());
					prefactura.setFecha_hora_creado(response.getPrefactura().getFecha_hora_creado());
				}
			}

			if (accion.equalsIgnoreCase("inicial")) {
				prefactura.setId_usuario(LoginController.session.getUser().getIdusuario());
				prefactura.setEstado(1);
				prefactura.setId_prefactura(id);
				prefactura.setId_forma_pago(1);
				prefactura.setId_cliente(idCliente);
				prefactura.setId_plazo(1);
				//Calendar calendar = Calendar.getInstance();
				//SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				prefactura.setFecha_hora_creado(Utils.obtenerFechaPorFormato(FormatoFecha.YYYYMMDDH24.getFormato()));
				prefactura.setFecha_hora(Utils.obtenerFechaPorFormato(FormatoFecha.YYYYMMDDH24.getFormato()));
				//prefactura.setId_sucursal(LoginController.session.getUser().getEmpleado().getIdsucursal());
			}

			if (accion.equalsIgnoreCase("update")) {
				response = LoginController.conStock.obtenerPrefacturaPorID(id);
				if (response.isStatus()) {
					prefactura.setId_usuario(response.getPrefactura().getId_usuario());
					prefactura.setEstado(estado);
					prefactura.setId_prefactura(response.getPrefactura().getId_prefactura());
					prefactura.setId_forma_pago(forma_pago);
					prefactura.setId_cliente(idCliente);
					prefactura.setId_plazo(plazo);
					prefactura.setFecha_hora_creado(response.getPrefactura().getFecha_hora_creado());
					prefactura.setFecha_hora(fecha);
					//prefactura.setId_sucursal(LoginController.session.getUser().getEmpleado().getIdsucursal());
				}
			}

			if (estado == 5) {
				ResponseMofPorIdPrefactura responseM= LoginController.conStock.obtenerModificacionPorIdPrefactura(id);
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
					modif.setId_prefactura(id);
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
				modif.setId_prefactura(id);
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
				
				String pathPrefactura=LoginController.rutaPDFPrefacturas+"\\prefactura_"+id+".pdf";

				File archivo = new File(pathPrefactura);
				//if (archivo.exists()) {
				ResponsePrefacturamprimir responsePDF = ImprimirUtils.imprimirPrefactura(id, idCliente);

					if (responsePDF.isStatus()) {
						OutputStream fileOutputStream = new FileOutputStream(pathPrefactura);
						HtmlConverter.convertToPdf(ImprimirUtils.armarPDFPrefactura(responsePDF), fileOutputStream);
					}
				//}
			}

			if (accion.equalsIgnoreCase("delete")) {
				response = LoginController.conStock.obtenerPrefacturaPorID(id);
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
					ResponseResultado responseN = LoginController.conStock.obtenerNumeroPrefactura(
							prefactura.getFecha_hora(), LoginController.session.getUser().getIdusuario());
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
	public void insertarDetallePrefactura(@ModelAttribute("accion") String accion, @ModelAttribute("id") int id,
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
	public void buscarPrefacturaPorID(@ModelAttribute("accion") String accion, @ModelAttribute("id") int id,
									Model modelo, HttpServletResponse responseHttp) throws IOException, ParseException {
		if (LoginController.session.getToken() != null) {
			modelo.addAttribute("user", LoginController.session.getUser());
			ResponsePrefactClient response = LoginController.conStock.obtenerPrefacturaPorID(id);
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
	public void buscarClientePorId(@ModelAttribute("accion") String accion, @ModelAttribute("idCliente") int id,
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
	public void itemsOrdenCompra(@ModelAttribute("accion") String accion, @ModelAttribute("idOrden") int idOrden,
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
							+ "<td class='text-right'>" + items.getImporte() + "</td>\r\n" + "<td class='text-right'>"
							+ items.getTotal() + "</td>\r\n" + "<td class='text-right'><a href=\"#\" " + modificar
							+ " onclick=\"eliminar_item('" + items.getId_detalle() + "','" + idOrden
							+ "')\" ><img src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAMAAAAoLQ9TAAAAeFBMVEUAAADnTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDx+VWpeAAAAJ3RSTlMAAQIFCAkPERQYGi40TVRVVlhZaHR8g4WPl5qdtb7Hys7R19rr7e97kMnEAAAAaklEQVQYV7XOSQKCMBQE0UpQwfkrSJwCKmDf/4YuVOIF7F29VQOA897xs50k1aknmnmfPRfvWptdBjOz29Vs46B6aFx/cEBIEAEIamhWc3EcIRKXhQj/hX47nGvt7x8o07ETANP2210OvABwcxH233o1TgAAAABJRU5ErkJggg==\"></a></td></tr>";
				}

				totales = "<tr><td colspan='7'>\r\n<button id=\"itemsBtt\" type=\"button\" class=\"btn btn-info btn-sm\" style =\"float: left;\"  onclick=\"abrirModal('"
						+ modal + "')\" " + dis
						+ " ><span class=\"glyphicon glyphicon-plus\"></span> Agregar ítem</button>\r\n"
						+ "</td></tr><tr><td colspan='5' class='text-right'>\r\n" + "<h4>TOTAL </h4></td>\r\n"
						+ "<th class='text-right'>\r\n" + "<h4>" + totalSuma + "</h4>\r\n"
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
	public void itemsProductos(@ModelAttribute("accion") String accion, @ModelAttribute("q") String busqueda,
							   Model modelo, HttpServletResponse responseHttp) throws IOException, ParseException, SQLException {
		if (LoginController.session.getToken() != null) {
			modelo.addAttribute("user", LoginController.session.getUser());
			List<ItemsProducto> itemsProductos = Utils.listadoItemsProductos(busqueda);
			String json = (new Gson()).toJson(itemsProductos);
			responseHttp.setContentType("application/json");
			responseHttp.setCharacterEncoding("UTF-8");
			responseHttp.getWriter().write(json);
		} else {
			responseHttp.setContentType("application/json");
			responseHttp.setCharacterEncoding("UTF-8");
			responseHttp.getWriter().write("LoginController.session caducada");
		}

	}
}