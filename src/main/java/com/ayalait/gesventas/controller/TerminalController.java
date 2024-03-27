package com.ayalait.gesventas.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;


import com.ayalait.gesventas.utils.*;
import com.ayalait.modelo.*;
import com.ayalait.response.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import javax.servlet.http.HttpServletResponse;

@Controller
public class TerminalController {

	private static FormatoFecha fechaSinHora;
	private static FormatoFecha fechaConHora;
	private static FormatoFecha hora;
	
	@Autowired
	RestTemplate restTemplate;


	public TerminalController() {
		fechaSinHora = FormatoFecha.YYYYMMDD;
		fechaConHora = FormatoFecha.YYYYMMDDH24;
		hora = FormatoFecha.H24;
	}

	// Pagina abrir caja del dia
	/*
	 * @GetMapping(value = "/abrir-caja") public String abrirCaja(Model modelo) { if
	 * (LoginController.session.getToken() != null) { modelo.addAttribute("user",
	 * LoginController.session.getUser()); ResponseMonedas response =
	 * LoginController.conParam.listarMonedas(); String fecha = ""; if
	 * (response.isStatus()) { modelo.addAttribute("listaMoneda",
	 * response.getMonedas()));
	 * 
	 * HistoricoCambio cambio = new HistoricoCambio();
	 * modelo.addAttribute("fechaapertura", fecha); modelo.addAttribute("cambio",
	 * cambio);
	 * 
	 * return "abrir-caja"; } else { modelo.addAttribute("listaMoneda", new
	 * Moneda()); }
	 * 
	 * } return "redirect:/";
	 * 
	 * }
	 */

	@GetMapping(value = "/abrir-caja")
	public String abrirCaja(Model modelo) {
		if (LoginController.session.getToken() != null) {
			modelo.addAttribute("user", LoginController.session.getUser());

			String fechaHoy = Utils.obtenerFechaPorFormato(fechaSinHora.getFormato());
			LocalDate currentDate = LocalDate.parse(fechaHoy);

			// Get month from date
			Month month = currentDate.getMonth();

			// Get year from date
			int year = currentDate.getYear();

			ResponseListadoAperturaMensual response = LoginController.conCaja.obtenerAperurasMensual(month.getValue(),
					year);

			if (response.isStatus()) {
				modelo.addAttribute("listaAperturaMes", response.getAperturaCaja());

				return "abrir-caja";
			} else {
				modelo.addAttribute("listaAperturaMes", new Moneda());
			}

		}
		return "redirect:/";

	}

	@PostMapping(value = LoginController.ruta + "/cargar-open-dia")
	public void ejecutarOpenDia(Model modelo, HttpServletResponse responseHttp) throws IOException {
		ResponseResultado responseOpen = new ResponseResultado();
		if (LoginController.session.getToken() != null) {
			modelo.addAttribute("user", LoginController.session.getUser());
			ResponseMonedas response = LoginController.conParam.listarMonedas();
			if (response.isStatus()) {
				modelo.addAttribute("listaMoneda", response.getMonedas());

				HistoricoCambio cambio = new HistoricoCambio();
				modelo.addAttribute("fechaapertura", "");
				modelo.addAttribute("cambio", cambio);
				responseOpen.setCode(200);
				responseOpen.setStatus(true);
				String json = new Gson().toJson(responseOpen);
				responseHttp.setContentType("application/json");
				responseHttp.setCharacterEncoding("UTF-8");
				responseHttp.getWriter().write(json);

			} else {
				modelo.addAttribute("listaMoneda", new Moneda());
				responseOpen.setCode(400);
				responseOpen.setResultado("abrir-caja");
				String json = new Gson().toJson(responseOpen);
				responseHttp.setContentType("application/json");
				responseHttp.setCharacterEncoding("UTF-8");
				responseHttp.getWriter().write(json);

			}
		}

	}

	
	// Pagina abir dia
	@PostMapping(value = LoginController.ruta + "/validar-open-dia")
	public void validarDiaOpen(Model modelo, @ModelAttribute("fecha") String fecha,
				HttpServletResponse responseHttp) throws IOException {
		
		if (LoginController.session.getToken() != null) {
			modelo.addAttribute("user", LoginController.session.getUser());
			ResponseValidarEstadoCaja responseResult = LoginController.conCaja.validarCaja(LoginController.session.getToken(), fecha);
			
				String json = new Gson().toJson(responseResult);
				responseHttp.setContentType("application/json");
				responseHttp.setCharacterEncoding("UTF-8");
				responseHttp.getWriter().write(json);
			
			
		}else {
			responseHttp.setContentType("application/json");
			responseHttp.setCharacterEncoding("UTF-8");
			responseHttp.getWriter().write("Session caducada.");
		}
	}
	
	// Pagina abir dia
	@PostMapping(value = LoginController.ruta + "/abrir-dia")
	public void guardarAbrirDia(Model modelo, @ModelAttribute("fechaApertura") String fecha,
			RedirectAttributes attribute, @ModelAttribute("arrayCompra") String compra,
			@ModelAttribute("arrayVenta") String venta, @ModelAttribute("accion") String accion,
			HttpServletResponse responseHttp) throws IOException {
		if (LoginController.session.getToken() != null) {
			modelo.addAttribute("user", LoginController.session.getUser());

			AperturaCaja apertura = new AperturaCaja(Utils.generarCodigo(), fecha,
					LoginController.session.getUser().getIdusuario(), null, "ABIERTO");
			ResponseResultado responseHistorico = null;
			ResponseResultado responseCaja = LoginController.conCaja.abrirCaja(apertura,
					LoginController.session.getToken());
			if (responseCaja.isStatus()) {
				LoginController.session.setIdAperturaDia(apertura.getId());
				ObjectMapper objectMapper = new ObjectMapper();
				List<CambioTemp> lstCompra = objectMapper.readValue(compra, new TypeReference<List<CambioTemp>>() {
				});
				List<CambioTemp> lstVenta = objectMapper.readValue(venta, new TypeReference<List<CambioTemp>>() {
				});
				for (int i = 0; i < lstCompra.size(); i++) {
					lstCompra.get(i).getIdmoneda();
					for (int j = 0; j < lstVenta.size(); j++) {
						if (lstCompra.get(i).getIdmoneda().equalsIgnoreCase(lstVenta.get(j).getIdmoneda())) {
							HistoricoCambio cambioH = new HistoricoCambio(Utils.generarCodigo(),
									String.valueOf(lstCompra.get(i).getIdmoneda()),
									Double.parseDouble(lstCompra.get(i).getValue()),
									Double.parseDouble(lstVenta.get(j).getValue()), apertura.getId());
							responseHistorico = LoginController.conCaja.guardarHistoricoCambio(cambioH,
									LoginController.session.getToken());
							if (!responseHistorico.isStatus())
								break;
						}
					}
				}
				//Obtener id apertura del dia
				
                ResponseOpenDay aperturaDia = LoginController.conCaja.openDay(fecha);
				if(aperturaDia.isStatus()) {
					LoginController.session.setOepnDay(aperturaDia.getOpen());
				}
				String json = new Gson().toJson(responseCaja);
				responseHttp.setContentType("application/json");
				responseHttp.setCharacterEncoding("UTF-8");
				responseHttp.getWriter().write(json);

			} else {
				ResponseResultado responseFail = new ResponseResultado();
				responseFail.setCode(400);
				responseFail.setResultado("La caja esta esta abierta para la fecha: " + fecha);
				String json = new Gson().toJson(responseFail);
				responseHttp.setContentType("application/json");
				responseHttp.setCharacterEncoding("UTF-8");
				responseHttp.getWriter().write(json);
			}

		} else {
			responseHttp.setContentType("application/json");
			responseHttp.setCharacterEncoding("UTF-8");
			responseHttp.getWriter().write("Session caducada.");
		}
		// return "redirect:/";

	}

	@GetMapping(value = "/gestion-terminal")
	public String gestionTerminal(Model modelo) throws SQLException {
		if (LoginController.session.getToken() != null) {
			modelo.addAttribute("user", LoginController.session.getUser());

			modelo.addAttribute("listaArqueos", Utils.listadoArqueosTerminales(LoginController.session.getUser().getIdusuario()));

			ResponseListaTipoCaudre response = LoginController.conParam.listarTipoCuadre();
			if (response.isStatus()) {
				modelo.addAttribute("listaTipoCuadre", response.getTipoCaudre());
			} else {
				modelo.addAttribute("listaTipoCuadre", new ArrayList<TipoCuadre>());
			}

			ResponseListaTipoArqueo responseTA = LoginController.conParam.listarTipoArqueo();
			if (responseTA.isStatus()) {
				modelo.addAttribute("listaTipoArqueo", responseTA.getTipoArqueo());
			} else {
				modelo.addAttribute("listaTipoArqueo", new ArrayList<TipoArqueo>());
			}

			List<Billetes> lstBilletes = new ArrayList<Billetes>();
			ResponseMonedas responseM = LoginController.conParam.listarMonedas();
			if (responseM.isStatus()) {
				for (Moneda m : responseM.getMonedas()) {
					if (m.getDefecto() == 1) {
						modelo.addAttribute("monedaDefecto", m);
						ResponseListaBilletes responseB = LoginController.conParam.listarBilletes();
						if (responseB.isStatus()) {
							for (Billetes billete : responseB.getBilletes()) {
								if (billete.getId_moneda() == m.getId()) {
									lstBilletes.add(billete);
								}
							}
							modelo.addAttribute("listaBilletes", lstBilletes);
						} else {
							modelo.addAttribute("listaBilletes", new ArrayList<Billetes>());
						}
						break;
					}
				}
				modelo.addAttribute("listaMoneda", responseM.getMonedas());

				HistoricoCambio cambio = new HistoricoCambio();
				modelo.addAttribute("fechaapertura", "");
				modelo.addAttribute("cambio", cambio);
			} else {
				modelo.addAttribute("monedaDefecto", new Moneda());
			}

			ResponseListaTurnos responseT = LoginController.conParam.listarTurnos();
			if (responseT.isStatus()) {
				modelo.addAttribute("listaTurnos", responseT.getTurnos());
			} else {
				modelo.addAttribute("listaTurnos", new ArrayList<Turnos>());
			}

			ResponseListaCajas responseC = LoginController.conParam.listarCajas();
			if (responseC.isStatus()) {
				modelo.addAttribute("listaCajas", responseC.getCaja());
			} else {
				modelo.addAttribute("listaCajas", new ArrayList<Turnos>());
			}

			return "gestion-terminal";
		}

		return "redirect:/";

	}

	// Guardar Arqueo terminal y detalle
	@PostMapping(LoginController.ruta + "/guardar-arqueo-detalle")
	public void guardarArqueoyDetalle(@ModelAttribute("accion") String accion,
			@ModelAttribute("idApertura") int idApertura, @ModelAttribute("idCuadre") int idCuadre,
			@ModelAttribute("tipoArqueo") int tipoArqueo, @ModelAttribute("array") String array, Model modelo,
			HttpServletResponse responseHttp) throws IOException, ParseException, SQLException {
		ResponseAperturaTerminal response = new ResponseAperturaTerminal();

		if (LoginController.session.getToken() != null) {
			modelo.addAttribute("user", LoginController.session.getUser());
			ArqueoTerminal arqueo = new ArqueoTerminal();
			arqueo.setIdArqueo(0);
			arqueo.setIdCuadre(idCuadre);
			arqueo.setIdEstadoArqueo(tipoArqueo);
			arqueo.setIdAperturaCajero(idApertura);
			arqueo.setIdSucursal(LoginController.session.getUser().getEmpleado().getIdsucursal());
			arqueo.setIdUsuario(LoginController.session.getUser().getIdusuario());
			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
			arqueo.setFechaHora(formato.format(calendar.getTime()));
			ResponseResultado responseArqueo = LoginController.conTerminal.guardarArqueoTerminal(arqueo,
					LoginController.session.getToken());
			if (responseArqueo.isStatus()) {
				int idArqueo = Utils.getArqueo(idApertura, LoginController.session.getUser().getIdusuario());
				ObjectMapper objectMapper = new ObjectMapper();
				List<DetalleArqueo> lstDetalle = objectMapper.readValue(array,
						new TypeReference<List<DetalleArqueo>>() {
						});
				for (int i = 0; i < lstDetalle.size(); i++) {
					ArqueosDetalle detalle = new ArqueosDetalle();
					detalle.setIdArqueo(idArqueo);
					detalle.setIdArqueoDetalle(0);
					detalle.setIdBillete(lstDetalle.get(i).getId());
					detalle.setValor(lstDetalle.get(i).getValue());
					ResponseResultado responseArqueoDetalle = LoginController.conTerminal
							.guardarDetalleArqueoTerminal(detalle, LoginController.session.getToken());
					if (!responseArqueoDetalle.isStatus()) {
						String json = new Gson().toJson(responseArqueoDetalle);
						responseHttp.setContentType("application/json");
						responseHttp.setCharacterEncoding("UTF-8");
						responseHttp.getWriter().write(json);
						break;
					}
				}

				if (tipoArqueo == 3) {
					ResponseAperturaTerminal responseAT = LoginController.conTerminal
							.obtenerAperturaTerminalPorId(LoginController.session.getToken(), idApertura);
					if (responseAT.isStatus()) {
						AperturasTerminal apertura = responseAT.getTerminal();
						apertura.setFechaHoraCierre(Utils.obtenerFechaPorFormato(fechaConHora.getFormato()));
						apertura.setIdTipoArqueo(tipoArqueo);
						ResponseResultado responseUP = LoginController.conTerminal.cerrarTerminal(apertura,
								LoginController.session.getToken());
						if (!Utils.cambiarEstadoCaja(LoginController.session.getIp(), 1)) {
							responseArqueo.setCode(406);
							responseArqueo.setResultado("No se pudo actualizar el estado de la caja");
							String json = new Gson().toJson(responseArqueo);
							responseHttp.setContentType("application/json");
							responseHttp.setCharacterEncoding("UTF-8");
							responseHttp.getWriter().write(json);
						}

					} else {
						ResponseResultado deleteArqueo = LoginController.conTerminal.eliminarArqueoYDetalle(idArqueo);
						if (deleteArqueo.isStatus()) {
							responseArqueo.setResultado(deleteArqueo.getResultado());
							String json = new Gson().toJson(responseArqueo);
							responseHttp.setContentType("application/json");
							responseHttp.setCharacterEncoding("UTF-8");
							responseHttp.getWriter().write(json);
						} else {
							responseArqueo.setCode(406);
							responseArqueo.setResultado("No se pudo actualizar el estado de la caja");
							String json = new Gson().toJson(responseArqueo);
							responseHttp.setContentType("application/json");
							responseHttp.setCharacterEncoding("UTF-8");
							responseHttp.getWriter().write(json);
						}

					}

				}
				String json = new Gson().toJson(responseArqueo);
				responseHttp.setContentType("application/json");
				responseHttp.setCharacterEncoding("UTF-8");
				responseHttp.getWriter().write(json);

			} else {
				String json = new Gson().toJson(responseArqueo);
				responseHttp.setContentType("application/json");
				responseHttp.setCharacterEncoding("UTF-8");
				responseHttp.getWriter().write(json);
			}

		} else {

			LoginController.session = new Session();
			response.setCode(400);
			response.setResultado("Session caducada");
			String json = new Gson().toJson(response);
			responseHttp.setContentType("application/json");
			responseHttp.setCharacterEncoding("UTF-8");
			responseHttp.getWriter().write(json);
		}

	}

	@PostMapping(LoginController.ruta + "/buscar-apertura-terminal")
	public void obtenerDatosTerminal(@ModelAttribute("accion") String accion,
			@ModelAttribute("fechaCierre") String fechaCierre, @ModelAttribute("idApertura") int idApertura,
			@ModelAttribute("idTurno") int idTurno, Model modelo, HttpServletResponse responseHttp)
			throws IOException, ParseException, SQLException {
		ResponseAperturaTerminal response = new ResponseAperturaTerminal();
		ResponseResultado respuesta = new ResponseResultado();

		if (LoginController.session.getToken() != null) {

			modelo.addAttribute("user", LoginController.session.getUser());
			ResponseValidarEstadoCaja aperturaDia = LoginController.conCaja.validarCaja(
					LoginController.session.getToken(), Utils.obtenerFechaPorFormato(fechaSinHora.getFormato()));
			if (aperturaDia.getApertura() == null) {
				respuesta.setCode(400);
				respuesta.setError(aperturaDia.getError());
				respuesta.setResultado(aperturaDia.getResultado());
				String json = new Gson().toJson(respuesta);
				responseHttp.setContentType("application/json");
				responseHttp.setCharacterEncoding("UTF-8");
				responseHttp.getWriter().write(json);

			} else {
				if (accion.equalsIgnoreCase("inicio") || accion.equalsIgnoreCase("buscar")) {
					response = LoginController.conCaja.obtenerAperturaTerminalPorUsuario(
							LoginController.session.getToken(), LoginController.session.getUser().getIdusuario(),
							Utils.obtenerFechaPorFormato(fechaSinHora.getFormato()));

					String json = new Gson().toJson(response);
					responseHttp.setContentType("application/json");
					responseHttp.setCharacterEncoding("UTF-8");
					responseHttp.getWriter().write(json);
				}
				if (accion.equalsIgnoreCase("ventas")) {

					String json = new Gson().toJson(
							Utils.buscarMontoDeVentas(idApertura, LoginController.session.getUser().getIdusuario()));
					responseHttp.setContentType("application/json");
					responseHttp.setCharacterEncoding("UTF-8");
					responseHttp.getWriter().write(json);
				}
				if (accion.equalsIgnoreCase("deleteVentasAbiertas")) {

					Utils.eliminarVentasNoEfectuadas(idApertura);
				}
				if (accion.equalsIgnoreCase("cierre")) {
					response = LoginController.conCaja.obtenerAperturaTerminalPorUsuario(
							LoginController.session.getToken(), LoginController.session.getUser().getIdusuario(),
							fechaCierre);
					if (response.isStatus()) {
						AperturasTerminal apertura = response.getTerminal();
						apertura.setFechaHoraCierre(Utils.obtenerFechaPorFormato(fechaSinHora.getFormato()));
						ResponseResultado responseCerrarTerminal = LoginController.conTerminal.cerrarTerminal(apertura,
								LoginController.session.getToken());
						if (responseCerrarTerminal.isStatus()) {
							String json = new Gson().toJson(response);
							responseHttp.setContentType("application/json");
							responseHttp.setCharacterEncoding("UTF-8");
							responseHttp.getWriter().write(json);
						}
					}
				}
				if (accion.equalsIgnoreCase("respuesta")) {

					ResponseCaja responseCaja = LoginController.conParam.datosCaja(Utils.ObtenerIPCaja());

					if (responseCaja.isStatus()) {

						if (responseCaja.getCaja().getIp().equalsIgnoreCase(LoginController.session.getIp())) {
							AperturasTerminal terminal = new AperturasTerminal();
							terminal.setIdAperturaCajero(0);
							terminal.setFechaInicio(Utils.obtenerFechaPorFormato(fechaSinHora.getFormato()));
							terminal.setIdUsuario(LoginController.session.getUser().getIdusuario());
							terminal.setIdSucursal(LoginController.session.getUser().getEmpleado().getIdsucursal());
							terminal.setHoraInicio(Utils.obtenerFechaPorFormato(hora.getFormato()));
							terminal.setNroConsecutivo(Utils.generarNroConsecutivoInicial());
							terminal.setIdUsuarioAlta(0);
							terminal.setIdTipoArqueo(1);
							terminal.setIdCaja(Integer.parseInt(responseCaja.getCaja().getIdcaja()));
							terminal.setFechaHora(Utils.obtenerFechaPorFormato(fechaConHora.getFormato()));
							terminal.setIdAperturaDia(aperturaDia.getApertura().getId());
							ResponseResultado responseAbrir = LoginController.conTerminal.abrirTerminal(terminal,
									LoginController.session.getToken());
							if (responseAbrir.isStatus()) {

								if (!Utils.cambiarEstadoCaja(responseCaja.getCaja())) {
									ResponseResultado responseFail = new ResponseResultado();
									responseFail.setCode(400);
									responseFail.setResultado("No se pudo actualizar el estado de la caja "
											+ responseCaja.getCaja().getNombre() + " con IP: "
											+ responseCaja.getCaja().getIp());
									String json = new Gson().toJson(responseFail);
									responseHttp.setContentType("application/json");
									responseHttp.setCharacterEncoding("UTF-8");
									responseHttp.getWriter().write(json);
								} else {
									ResponseAperturaTerminal responseT = LoginController.conCaja
											.obtenerAperturaTerminalPorUsuario(LoginController.session.getToken(),
													LoginController.session.getUser().getIdusuario(),
													Utils.obtenerFechaPorFormato(fechaSinHora.getFormato()));
									if (responseT.isStatus()) {
										String json = new Gson().toJson(responseT);
										responseHttp.setContentType("application/json");
										responseHttp.setCharacterEncoding("UTF-8");
										responseHttp.getWriter().write(json);
									}

								}
							}

						} else {
							ResponseResultado resposeE = new ResponseResultado();
							resposeE.setCode(500);
							resposeE.setResultado("La caja se encuentra "
									+ Utils.obtenerEstadoCaja(responseCaja.getCaja().getEstado()));
							String json = new Gson().toJson(resposeE);
							responseHttp.setContentType("application/json");
							responseHttp.setCharacterEncoding("UTF-8");
							responseHttp.getWriter().write(json);
						}

					} else {
						String json = new Gson().toJson(responseCaja);
						responseHttp.setContentType("application/json");
						responseHttp.setCharacterEncoding("UTF-8");
						responseHttp.getWriter().write(json);
					}
				}

				if (accion.equalsIgnoreCase("update")) {
					ResponseAperturaTerminal responseT = LoginController.conCaja.obtenerAperturaTerminalPorUsuario(
							LoginController.session.getToken(), LoginController.session.getUser().getIdusuario(),
							Utils.obtenerFechaPorFormato(fechaSinHora.getFormato()));
					if (responseT.isStatus()) {
						AperturasTerminal apertura = responseT.getTerminal();
						apertura.setIdTurno(idTurno);
						ResponseResultado responseAbrir = LoginController.conTerminal.abrirTerminal(apertura,
								LoginController.session.getToken());
						if (responseAbrir.isStatus()) {
							String json = new Gson().toJson(responseT);
							responseHttp.setContentType("application/json");
							responseHttp.setCharacterEncoding("UTF-8");
							responseHttp.getWriter().write(json);
						}
					}
				}
			}

		} else {

			LoginController.session = new Session();
			response.setCode(400);
			response.setResultado("Session caducada");
			String json = new Gson().toJson(response);
			responseHttp.setContentType("application/json");
			responseHttp.setCharacterEncoding("UTF-8");
			responseHttp.getWriter().write(json);
		}

	}

	@PostMapping(LoginController.ruta + "/apertura-terminal-open")
	public void obtenerDatosAperturaTerminal(Model modelo, @ModelAttribute("accion") String accion,
			@ModelAttribute("fechaCierre") String fechaCierre, HttpServletResponse responseHttp)
			throws IOException, ParseException, SQLException {

		if (accion.equalsIgnoreCase("")) {
			fechaCierre = Utils.obtenerFechaPorFormato(fechaSinHora.getFormato());
		}
		ResponseResultado respuesta = new ResponseResultado();
		ResponseValidarEstadoCaja aperturaDia = LoginController.conCaja.validarCaja(LoginController.session.getToken(),
				fechaCierre);
		if (aperturaDia.getApertura() == null) {
			respuesta.setCode(406);
			respuesta.setResultado(aperturaDia.getResultado());
			String json = new Gson().toJson(respuesta);
			responseHttp.setContentType("application/json");
			responseHttp.setCharacterEncoding("UTF-8");
			responseHttp.getWriter().write(json);

		} else {
			ResponseCaja responseCaja = LoginController.conParam.datosCaja(Utils.ObtenerIPCaja());

			if (responseCaja.isStatus()) {

				if (responseCaja.getCaja().getIp().equalsIgnoreCase(LoginController.session.getIp())) {
					AperturasTerminal terminal = new AperturasTerminal();
					terminal.setIdAperturaCajero(0);
					terminal.setFechaInicio(Utils.obtenerFechaPorFormato(fechaSinHora.getFormato()));
					terminal.setIdUsuario(LoginController.session.getUser().getIdusuario());
					terminal.setIdSucursal(LoginController.session.getUser().getEmpleado().getIdsucursal());
					terminal.setHoraInicio(Utils.obtenerFechaPorFormato(hora.getFormato()));
					terminal.setNroConsecutivo(Utils.generarNroConsecutivoInicial());
					terminal.setIdUsuarioAlta(0);
					terminal.setIdTipoArqueo(1);
					terminal.setIdCaja(Integer.parseInt(responseCaja.getCaja().getIdcaja()));
					terminal.setFechaHora(Utils.obtenerFechaPorFormato(fechaConHora.getFormato()));
					terminal.setIdAperturaDia(String.valueOf(aperturaDia.getApertura().getId()));
					ResponseResultado responseAbrir = LoginController.conTerminal.abrirTerminal(terminal,
							LoginController.session.getToken());
					if (responseAbrir.isStatus()) {

						if (!Utils.cambiarEstadoCaja(responseCaja.getCaja())) {
							ResponseResultado responseFail = new ResponseResultado();
							responseFail.setCode(400);
							responseFail.setResultado(
									"No se pudo actualizar el estado de la caja " + responseCaja.getCaja().getNombre()
											+ " con IP: " + responseCaja.getCaja().getIp());
							String json = new Gson().toJson(responseFail);
							responseHttp.setContentType("application/json");
							responseHttp.setCharacterEncoding("UTF-8");
							responseHttp.getWriter().write(json);
						} else {
							ResponseAperturaTerminal responseT = LoginController.conCaja
									.obtenerAperturaTerminalPorUsuario(LoginController.session.getToken(),
											LoginController.session.getUser().getIdusuario(),
											Utils.obtenerFechaPorFormato(fechaSinHora.getFormato()));
							if (responseT.isStatus()) {
								// LoginController.session.setIdCaja(String.valueOf(responseT.getTerminal().getIdCaja()));
								String json = new Gson().toJson(responseT);
								responseHttp.setContentType("application/json");
								responseHttp.setCharacterEncoding("UTF-8");
								responseHttp.getWriter().write(json);
							}

						}
					}

				} else {
					ResponseResultado resposeE = new ResponseResultado();
					resposeE.setCode(500);
					resposeE.setResultado(
							"La caja se encuentra " + Utils.obtenerEstadoCaja(responseCaja.getCaja().getEstado()));
					String json = new Gson().toJson(resposeE);
					responseHttp.setContentType("application/json");
					responseHttp.setCharacterEncoding("UTF-8");
					responseHttp.getWriter().write(json);
				}

			} else {
				String json = new Gson().toJson(responseCaja);
				responseHttp.setContentType("application/json");
				responseHttp.setCharacterEncoding("UTF-8");
				responseHttp.getWriter().write(json);
			}
		}
	}

	@PostMapping(LoginController.ruta + "/eliminar-apertura-terminal")
	public void eliminarAperturaInicial(Model modelo, @ModelAttribute("accion") String accion, @ModelAttribute("idArqueo") int idArqueo,
			 @ModelAttribute("idApertura") int idApertura,@ModelAttribute("idUsuario") String idUsuario,
			@ModelAttribute("fechaCierre") String fechaCierre, @ModelAttribute("tipoArqueo") String tipoArqueo,
			HttpServletResponse responseHttp) throws IOException, ParseException, SQLException {
		if (LoginController.session.getToken() != null) {
			modelo.addAttribute("user", LoginController.session.getUser());
			ResponseAperturaTerminal response = new ResponseAperturaTerminal();
			ResponseResultado responseDelete = new ResponseResultado();
			 if(accion.equalsIgnoreCase("cierre")){
			fechaCierre = Utils.obtenerFechaPorFormato(fechaSinHora.getFormato());
			 }
			if(accion.equalsIgnoreCase("delete")) {
				double ventas = Utils.buscarMontoDeVentas(idApertura,idUsuario);
				if (ventas > 0) {
					responseDelete.setCode(406);
					responseDelete.setResultado("No se pudo eliminar la apertura ya que cuenta con ventas activas.");
					String json = new Gson().toJson(responseDelete);
					responseHttp.setContentType("application/json");
					responseHttp.setCharacterEncoding("UTF-8");
					responseHttp.getWriter().write(json);
				}else {
					responseDelete = LoginController.conCaja.eliminarAperturaTerminal(LoginController.session.getToken(), idApertura);
					if (Utils.cambiarEstadoCaja(LoginController.session.getIp(), 1)) {
						String json = new Gson().toJson(responseDelete);
						responseHttp.setContentType("application/json");
						responseHttp.setCharacterEncoding("UTF-8");
						responseHttp.getWriter().write(json);
					} else {
						responseDelete.setCode(406);
						responseDelete.setResultado("No se pudo actualizar el estado de la caja");
						String json = new Gson().toJson(responseDelete);
						responseHttp.setContentType("application/json");
						responseHttp.setCharacterEncoding("UTF-8");
						responseHttp.getWriter().write(json);
					}	
				}
				
			}else {
				response = LoginController.conCaja.obtenerAperturaTerminalPorUsuario(LoginController.session.getToken(),
						LoginController.session.getUser().getIdusuario(), fechaCierre);
				
				//ResponseResultado deleteArqueo = LoginController.conTerminal.eliminarArqueoYDetalle(idArqueo);

				if (response.isStatus()) {
					double ventas = Utils.buscarMontoDeVentas(response.getTerminal().getIdAperturaCajero(),
							LoginController.session.getUser().getIdusuario());
					if (ventas > 0) {
						responseDelete.setCode(406);
						responseDelete.setResultado("No se pudo eliminar la apertura ya que cuenta con ventas activas.");
						String json = new Gson().toJson(responseDelete);
						responseHttp.setContentType("application/json");
						responseHttp.setCharacterEncoding("UTF-8");
						responseHttp.getWriter().write(json);
					} else {
						responseDelete = LoginController.conCaja.eliminarAperturaTerminal(
								LoginController.session.getToken(), response.getTerminal().getIdAperturaCajero());
						if (Utils.cambiarEstadoCaja(LoginController.session.getIp(), 1)) {
							String json = new Gson().toJson(responseDelete);
							responseHttp.setContentType("application/json");
							responseHttp.setCharacterEncoding("UTF-8");
							responseHttp.getWriter().write(json);
						} else {
							responseDelete.setCode(406);
							responseDelete.setResultado("No se pudo actualizar el estado de la caja");
							String json = new Gson().toJson(responseDelete);
							responseHttp.setContentType("application/json");
							responseHttp.setCharacterEncoding("UTF-8");
							responseHttp.getWriter().write(json);
						}
					}

				}
			}
			

		} else {
			ResponseResultado response = new ResponseResultado();
			response.setCode(406);
			response.setResultado("Sessión caducada");
			String json = new Gson().toJson(response);
			responseHttp.setContentType("application/json");
			responseHttp.setCharacterEncoding("UTF-8");
			responseHttp.getWriter().write(json);
		}
	}

	@PostMapping(LoginController.ruta + "/listar-ventas-apertura-usuario")
	public void listaVentasAperturaUsuario(Model modelo, @ModelAttribute("idUsuario") String idUsuario,
			@ModelAttribute("fecha") String fecha,HttpServletResponse responseHttp) throws IOException, ParseException, SQLException {
		if (LoginController.session.getToken() != null) {
			modelo.addAttribute("user", LoginController.session.getUser());
			ResponseListaVentasPorArqueoUsuario response = new ResponseListaVentasPorArqueoUsuario();
			List<VentasPorArqueoUsuario> lstVentas = new ArrayList<VentasPorArqueoUsuario>();			
			lstVentas = Utils.listadoVentasPorArqueoUsuario(idUsuario, fecha);

			if (lstVentas.isEmpty()) {
				response.setCode(400);
				response.setResultado("No se encontraron datos para la apertura del usuario.");
				String json = new Gson().toJson(response);
				responseHttp.setContentType("application/json");
				responseHttp.setCharacterEncoding("UTF-8");
				responseHttp.getWriter().write(json);
			} else {
				response.setCode(200);
				response.setStatus(true);
				response.setLstVentas(lstVentas);
				String json = new Gson().toJson(response);
				responseHttp.setContentType("application/json");
				responseHttp.setCharacterEncoding("UTF-8");
				responseHttp.getWriter().write(json);
			}
		}
	}

}
