package com.ayalait.gesventas.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

  
import com.ayalait.gesventas.request.RequestAddEmpleado;
import com.ayalait.gesventas.request.RequestAddUsuario;

 import com.ayalait.gesventas.utils.FormatoFecha;
import com.ayalait.gesventas.utils.Utils;
import com.ayalait.modelo.*;
import com.ayalait.response.*;
import com.ayalait.response.ResponseListaBancos;
import com.ayalait.utils.CompositeIdMarcasEmpleado;
import com.ayalait.utils.ErrorState;
import com.google.gson.Gson;

@Controller
public class EmpleadosController {

	static Empleado empleado = new Empleado();
	static EmpleadoTitulos empl = new EmpleadoTitulos();
	static EmpleadoSalud salud = new EmpleadoSalud();
	static EmpleadoTrabajo work = new EmpleadoTrabajo();
	static EmpleadoBanco banco = new EmpleadoBanco();
	ErrorState error= new ErrorState();
	static ResponseListaMarcasEmpleados responseMarcaFiltre = new ResponseListaMarcasEmpleados();
	@GetMapping({ "/empleados" })
	public String empleados(Model modelo) throws SQLException, IOException {
		if (LoginController.session.getToken() != null) {
			// List<Estados> listaEstados = Utils.obtenerListaEstado();
			List<Empleado> listaEmpleados = new ArrayList<Empleado>();

			ResponseListaTipoDoc responseTD = LoginController.conEmpl.listadoTipoDocumento();
			if (responseTD.isStatus()) {
				modelo.addAttribute("tipoDocumento", responseTD.getDocumento() );
			} else {
				modelo.addAttribute("tipoDocumento", new ArrayList<TipoDocumento>());

			}
			ResponseListaEmpleados listaEmp = LoginController.conEmpl.listadEmpleado();
			if (listaEmp.isStatus()) {
				listaEmpleados.add(new Empleado("0", "SELECCIONAR"));
				listaEmpleados =  listaEmp.getEmpleados()  ;
				modelo.addAttribute("listaEmpleados",  listaEmp.getEmpleados() );
			} else {
				modelo.addAttribute("listaEmpleados", new ArrayList<Empleado>());
			}
			modelo.addAttribute("user", LoginController.session.getUser());
			if (!listaEmpleados.isEmpty()) {
				for (Empleado empleado : listaEmpleados) {
					if(empleado.getFoto()==null)
						empleado.setFoto("img_empleado.png");
					File file = new File(LoginController.rutaDowloadEmpleado + empleado.getFoto());
					if(!file.exists()) {
						byte trans[] = Base64.getDecoder().decode(empleado.getImagen());
						Files.write(Paths.get(LoginController.rutaDowloadEmpleado + empleado.getFoto()), trans);
					}
					

				}
			}

			ResponseListaPaises responsePais = LoginController.conParam.listadoPaises();
			if (responsePais.isStatus()) {
				modelo.addAttribute("listaPais",  responsePais.getPaises() );
			} else {
				modelo.addAttribute("listaPais", new ArrayList<Paises>());
			}
			ResponseListaDepartamentos response = LoginController.conParam.listadoDepartamentos();
			if (response.isStatus()) {
				modelo.addAttribute("listaDepa", response.getDepartamentos());
			} else {
				modelo.addAttribute("listaDepa", new ArrayList<Departamentos>());

			}
			ResponseListaCargos responseCargos = LoginController.conParam.listadoCargos(1);
			if (responseCargos.isStatus()) {
				modelo.addAttribute("listaCargos", responseCargos.getCargos());

			} else {
				modelo.addAttribute("listaCargos", new ArrayList<Cargos>());

			}

			ResponseListaHorarioTrabajo responseHorario = LoginController.conEmpl.listadoHorarioTrabajo();
			if (responseHorario.isStatus()) {
				modelo.addAttribute("listaHorario", responseHorario.getHorario());

			} else {
				modelo.addAttribute("listaHorario", new ArrayList<HorarioTrabajo>());

			}
			ResponseListaParentesco responseParents = LoginController.conParam.listadoParentescos();
			if (responseParents.isStatus()) {
				modelo.addAttribute("listaParents", responseParents.getParentesco());

			} else {
				modelo.addAttribute("listaParents", new ArrayList<Parentesco>());

			}
			ResponseMonedas responseM = LoginController.conParam.listarMonedas();
			if (responseM.isStatus()) {
				modelo.addAttribute("listaMoneda", responseM.getMonedas());
			} else {
				modelo.addAttribute("listaMoneda", new ArrayList<Moneda>());

			}
			ResponseListaFormasCobro responseFC= LoginController.conParam.listaFormasCobros();
			if(responseFC.isStatus()) {
				modelo.addAttribute("listaFormasCobro",responseFC.getFormasCobro());
			}else {
				modelo.addAttribute("listaFormasCobro",new ArrayList<FormasCobro>());

			}
			
			ResponseListaBancos responseBanco= LoginController.conEmpl.listadoBancos();
			if(responseBanco.isStatus()) {
				modelo.addAttribute("listaBanco",responseBanco.getBanco());
			}else {
				modelo.addAttribute("listaBanco",new ArrayList<Banco>());
			}
			

			return "empleados";
		}
		return "redirect:/";
	}

	@GetMapping({ "/users-profile" })
	public String user_profile(Model modelo) {
		if (LoginController.session.getToken() != null) {
			modelo.addAttribute("user", LoginController.session.getUser());
			Date fecha = Utils.obtenerFechaPorFormatoDateSQLDate();
			ResponseMarcasPorEmpleado responseMarca = LoginController.conEmpl
					.buscarMarcasEmpleado(LoginController.session.getUser().getEmpleado().getIdempleado(), fecha);
			modelo.addAttribute("marca", responseMarca);

			ResponseMonedas responseM = LoginController.conParam.listarMonedas();
			if (responseM.isStatus()) {
				modelo.addAttribute("listaMoneda", responseM.getMonedas());
			} else {
				modelo.addAttribute("listaMoneda", new ArrayList<Moneda>());

			}

			return "users-profile";
		}
		return "redirect:/";
	}

	@GetMapping({ "/calendario-marcas" })
	public String marcasCalendario(Model modelo) {
		if (LoginController.session.getToken() != null) {
			modelo.addAttribute("user", LoginController.session.getUser());

			ResponseMonedas responseM = LoginController.conParam.listarMonedas();
			if (responseM.isStatus()) {
				modelo.addAttribute("listaMoneda", responseM.getMonedas());
			} else {
				modelo.addAttribute("listaMoneda", new ArrayList<Moneda>());

			}

			return "calendario-marcas";
		}
		return "redirect:/";
	}

	@GetMapping({ "/pagos" })
	public String generarPagos(Model modelo) {
		if (LoginController.session.getToken() != null) {
			modelo.addAttribute("user", LoginController.session.getUser());
			ResponseListaMesPago meses = LoginController.conEmpl.mesesAPagar();
			if (meses.isStatus()) {
				modelo.addAttribute("meses", meses.getMes());

			} else {
				modelo.addAttribute("meses", new ArrayList<MesPago>());

			}
			ResponseMonedas responseM = LoginController.conParam.listarMonedas();
			if (responseM.isStatus()) {
				modelo.addAttribute("listaMoneda", responseM.getMonedas());
			} else {
				modelo.addAttribute("listaMoneda", new ArrayList<Moneda>());

			}
			ResponseListaEmpleados listaEmp = LoginController.conEmpl.listadEmpleado();
			if (listaEmp.isStatus()) {
				modelo.addAttribute("listaEmpleados", listaEmp.getEmpleados());
			} else {
				modelo.addAttribute("listaEmpleados", new ArrayList<Empleado>());
			}
			
			ResponseMesAProcesar mesProcesa= LoginController.conEmpl.mesAProcesar();
			if(mesProcesa.isStatus()) {
				modelo.addAttribute("mesAProcesar", mesProcesa.getMesProcesar());
			}else {
				modelo.addAttribute("mesAProcesar", new CalendarioMesAProcesar());

			}
			return "pagos";
		}
		return "redirect:/";
	}

	@PostMapping({ LoginController.ruta + "/buscar-titulo" })
	public void obtenerTitulo(@ModelAttribute("accion") String accion, @ModelAttribute("q") String busqueda,
							  Model modelo, HttpServletResponse responseHttp) throws IOException, ParseException, SQLException {
		if (LoginController.session.getToken() != null) {
			modelo.addAttribute("user", LoginController.session.getUser());
			List<Titulos> itemsProductos = Utils.buscarTitulos(busqueda);
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

	@PostMapping({ LoginController.ruta + "/addEmpleado" })
	public void agregarNuevoEmpleado(@ModelAttribute("accion") String accion, @ModelAttribute("nombre") String nombre,
									 @ModelAttribute("apellidos") String apellidos, @ModelAttribute("tipoDoc") int tipoDoc,
									 @ModelAttribute("documento") String documento, @ModelAttribute("fecha") String fecha,
									 @ModelAttribute("fechaNac") String fechaNac, @ModelAttribute("sexo") int sexo,
									 @ModelAttribute("paisDoc") int paisDoc, @ModelAttribute("paisResidencia") int paisResidencia,
									 @ModelAttribute("address") String address, @ModelAttribute("phone") String phone,
									 @ModelAttribute("email") String email, @RequestParam("foto") MultipartFile file,
									 @ModelAttribute("accionCheck") String accionCheck, Model modelo, HttpServletResponse responseHttp)
			throws IOException, ParseException, SQLException {
		if (LoginController.session.getToken() != null) {
			ResponseResultado response = new ResponseResultado();
			modelo.addAttribute("user", LoginController.session.getUser());

			ResponseResultado validarEmpleado = LoginController.conEmpl.validarEmpledoPorDocumento(documento);
			if (validarEmpleado.isStatus()) {
				if (file.isEmpty()) {
					response.setCode(406);
					response.setResultado("Debe seleccionar una imagen del producto para poder continuar.");
					String json = (new Gson()).toJson(response);
					responseHttp.setContentType("application/json");
					responseHttp.setCharacterEncoding("UTF-8");
					responseHttp.getWriter().write(json);
				}
				empleado.setIdempleado(Utils.generarCodigo());
				String fileName = documento + "_" + empleado.getIdempleado() + ".jpg";
				empleado.setFoto(fileName);
				empleado.setNombre(nombre);
				empleado.setApellidos(apellidos);
				empleado.setIdtipodocumento(tipoDoc);
				empleado.setDocumento(documento);
				empleado.setNumeroempleado(0);
				empleado.setCorreo_personal(email);
				empleado.setTelefono(phone);
				empleado.setDireccion(address);
				empleado.setSexo(sexo);
				empleado.setPais_documento(paisDoc);
				empleado.setPais_residencia(paisResidencia);
				empleado.setIdempresa(LoginController.session.getId_empresa());
				empleado.setFechanacimiento(fechaNac);
				Path path = Paths.get(LoginController.rutaDowloadEmpleado + fileName,
						new String[0]);
				Files.copy(file.getInputStream(), path, new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });

				byte[] fileContent = Files.readAllBytes(path);
				String base64 = Base64.getEncoder().encodeToString(fileContent);
				empleado.setImagen(base64);

				response.setCode(200);
				response.setResultado(empleado.getIdempleado());
				String json = (new Gson()).toJson(response);
				responseHttp.setContentType("application/json");
				responseHttp.setCharacterEncoding("UTF-8");
				responseHttp.getWriter().write(json);
			} else {
				String json = (new Gson()).toJson(validarEmpleado);
				responseHttp.setContentType("application/json");
				responseHttp.setCharacterEncoding("UTF-8");
				responseHttp.getWriter().write(json);
			}

		}
	}

	@PostMapping({ LoginController.ruta + "/guardar-empleado" })
	public void guardarDatosEmpleados(@ModelAttribute("idEmpleado") String idEmpleado, @ModelAttribute("depa") int depa,
									  @ModelAttribute("cargo") int cargo, @ModelAttribute("horario") int horario,
									  @ModelAttribute("salarioNominal") float salarioNominal,Model modelo,
									  HttpServletResponse responseHttp) throws IOException, ParseException, SQLException {
		if (LoginController.session.getToken() != null) {
			modelo.addAttribute("user", LoginController.session.getUser());

			RequestAddEmpleado request = new RequestAddEmpleado();
			request.setEmpleado(empleado);
			ResponseResultado responseAddEmpleado = LoginController.conEmpl.addEmpleado(request);
			if (responseAddEmpleado.isStatus()) {
				ResponseResultado responseAddTitulo = LoginController.conEmpl.addEmpleadoTitulos(empl);
				if (responseAddTitulo.isStatus()) {
					//if(work!=null)
					ResponseResultado responseTrabajo = LoginController.conEmpl.addEmpleadoTrabajos(work);
					if (responseTrabajo.isStatus()) {
						ResponseResultado responseSalud = LoginController.conEmpl.addEmpleadoSalud(salud);
						if (responseSalud.isStatus()) {
							ResponseResultado responseBanco= LoginController.conEmpl.addEmpleadoBanco(banco);
							if(responseBanco.isStatus()) {
								EmpleadoCargo puesto = new EmpleadoCargo();
								puesto.setId(0);
								puesto.setIddepartamento(depa);
								puesto.setIdempleado(idEmpleado);
								puesto.setIdcargo(cargo);
								puesto.setIdhorario(horario);
								puesto.setSalarionominal(salarioNominal);
								ResponseResultado responseCargo = LoginController.conEmpl.addEmpleadoCargo(puesto);
								if (responseCargo.isStatus()) {

									String json = (new Gson()).toJson(responseCargo);
									responseHttp.setContentType("application/json");
									responseHttp.setCharacterEncoding("UTF-8");
									responseHttp.getWriter().write(json);

								} else {
									LoginController.conEmpl.eliminarEmpleado(idEmpleado);
									LoginController.conEmpl.eliminarEmpleadoTitulo(idEmpleado);
									LoginController.conEmpl.eliminarEmpleadoTrabajo(idEmpleado);
									LoginController.conEmpl.eliminarEmpleadoSalud(idEmpleado);

									String json = (new Gson()).toJson(responseCargo);
									responseHttp.setContentType("application/json");
									responseHttp.setCharacterEncoding("UTF-8");
									responseHttp.getWriter().write(json);
								}
							}else {
								LoginController.conEmpl.eliminarEmpleado(idEmpleado);
								LoginController.conEmpl.eliminarEmpleadoTitulo(idEmpleado);
								LoginController.conEmpl.eliminarEmpleadoTrabajo(idEmpleado);

								String json = (new Gson()).toJson(responseSalud);
								responseHttp.setContentType("application/json");
								responseHttp.setCharacterEncoding("UTF-8");
								responseHttp.getWriter().write(json);
							}
						
						} else {
							LoginController.conEmpl.eliminarEmpleado(idEmpleado);
							LoginController.conEmpl.eliminarEmpleadoTitulo(idEmpleado);
							LoginController.conEmpl.eliminarEmpleadoTrabajo(idEmpleado);

							String json = (new Gson()).toJson(responseSalud);
							responseHttp.setContentType("application/json");
							responseHttp.setCharacterEncoding("UTF-8");
							responseHttp.getWriter().write(json);
						}
					} else {
						LoginController.conEmpl.eliminarEmpleado(idEmpleado);
						LoginController.conEmpl.eliminarEmpleadoTitulo(idEmpleado);

						String json = (new Gson()).toJson(responseTrabajo);
						responseHttp.setContentType("application/json");
						responseHttp.setCharacterEncoding("UTF-8");
						responseHttp.getWriter().write(json);

					}
				} else {
					LoginController.conEmpl.eliminarEmpleado(idEmpleado);

					String json = (new Gson()).toJson(responseAddTitulo);
					responseHttp.setContentType("application/json");
					responseHttp.setCharacterEncoding("UTF-8");
					responseHttp.getWriter().write(json);

				}
			} else {
				String json = (new Gson()).toJson(responseAddEmpleado);
				responseHttp.setContentType("application/json");
				responseHttp.setCharacterEncoding("UTF-8");
				responseHttp.getWriter().write(json);
			}
		}
	}

	@PostMapping({ LoginController.ruta + "/editar-empleado" })
	public void editarEmpleado(@ModelAttribute("idEmpleado") String idEmpleado, Model modelo,
							   HttpServletResponse responseHttp) throws IOException, ParseException, SQLException {
		if (LoginController.session.getToken() != null) {
			modelo.addAttribute("user", LoginController.session.getUser());
			ResponseBuscarEmpleado response = LoginController.conEmpl
					.buscarEmpleadoPorId(LoginController.session.getToken(), idEmpleado);

			if (response.isStatus()) {
				String json = (new Gson()).toJson(response);
				responseHttp.setContentType("application/json");
				responseHttp.setCharacterEncoding("UTF-8");
				responseHttp.getWriter().write(json);
			}
		}

	}

	@GetMapping({ "/buscarCargosPorDepa" })
	public @ResponseBody List<Cargos> todosLosCargosPorDepartamento(Model modelo, @ModelAttribute("idDepa") int idDepa,
																	@ModelAttribute("ajax") boolean ajax
			/* @RequestParam(value = "idDepa", required = true) int idDepa */) {
		ResponseListaCargos response = LoginController.conParam.listadoCargos(idDepa);
		if (response.isStatus()) {
			return  response.getCargos() ;
		}
		return new ArrayList<Cargos>();
	}

	@PostMapping({ LoginController.ruta + "/titulo-empleado" })
	public void agregarDatosProfesionales(@ModelAttribute("idEmpleado") String idEmpleado,
										  @ModelAttribute("idTitulo") int idTitulo, @RequestParam("archivo") MultipartFile file,
										  @ModelAttribute("trabajos") String trabajos, Model modelo, HttpServletResponse responseHttp)
			throws IOException, ParseException, SQLException {
		if (LoginController.session.getToken() != null) {
			modelo.addAttribute("user", LoginController.session.getUser());

			String fileName = file.getOriginalFilename();

			Path path = Paths.get(LoginController.rutaDowloadTitulos + fileName, new String[0]);
			Files.copy(file.getInputStream(), path, new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });

			byte[] fileContent = Files.readAllBytes(path);
			String base64 = Base64.getEncoder().encodeToString(fileContent);

			empl.setId(0);
			empl.setIdempleado(idEmpleado);
			empl.setIdtitulo(idTitulo);
			empl.setArchivo(base64);
			empl.setNombre_archivo(fileName);

			if (!trabajos.equalsIgnoreCase("")) {
				work.setId(0);
				work.setIdempleado(idEmpleado);
				work.setTrabajos(trabajos);
			}

			ResponseResultado response = new ResponseResultado();
			response.setCode(200);
			response.setResultado(idEmpleado);
			String json = (new Gson()).toJson(response);
			responseHttp.setContentType("application/json");
			responseHttp.setCharacterEncoding("UTF-8");
			responseHttp.getWriter().write(json);

		}

	}

	@PostMapping({ LoginController.ruta + "/datos-salud-empleado" })
	public void agregarDatosSalud(@ModelAttribute("idEmpleado") String idEmpleado,
								  @ModelAttribute("contacto") String contacto, @ModelAttribute("nombreContacto") String nombreContacto,
								  @ModelAttribute("vencimiento") String vencimiento, Model modelo, HttpServletResponse responseHttp)
			throws IOException, ParseException, SQLException {
		if (LoginController.session.getToken() != null) {
			modelo.addAttribute("user", LoginController.session.getUser());

			salud.setId(0);
			salud.setIdempleado(idEmpleado);
			salud.setNombreContacto(nombreContacto);
			salud.setContacto(contacto);
			salud.setVencimiento(vencimiento);

			ResponseResultado response = new ResponseResultado();
			response.setCode(200);
			response.setResultado(idEmpleado);
			String json = (new Gson()).toJson(response);
			responseHttp.setContentType("application/json");
			responseHttp.setCharacterEncoding("UTF-8");
			responseHttp.getWriter().write(json);

		}

	}
	
	@PostMapping({ LoginController.ruta + "/generar-calendario-mes" })
	public void generarCalendarioMes(@ModelAttribute("mes") String mes,@ModelAttribute("accion") String accion,
								   Model modelo, HttpServletResponse responseHttp)
			throws IOException, ParseException, SQLException {
		if (LoginController.session.getToken() != null) {
			modelo.addAttribute("user", LoginController.session.getUser());
			String[] data = mes.split("-");
			int mesF = Integer.parseInt(data[0]);
			int anio = Integer.parseInt(data[1]);
		    ResponseResultado response= LoginController.conEmpl.existeMesAProcesar(mesF, anio);
		    if(response.isStatus()) {
		    	boolean resultado= Boolean.parseBoolean(response.getResultado()) ;
		    	if(resultado && (accion.equalsIgnoreCase("procesar")|| accion.equalsIgnoreCase("reprocesar"))) {
		    		ResponseResultado aProcesar=LoginController.conEmpl.generarCalendarioEmpleado(accion,mesF, anio);
		    		aProcesar.setResultado("Se esta procesando el calendario de empleados para la fecha: "+mes);
		    		aProcesar.setCode(200);
		    		aProcesar.setStatus(true);
		    		String json = (new Gson()).toJson(aProcesar);
					responseHttp.setContentType("application/json");
					responseHttp.setCharacterEncoding("UTF-8");
					responseHttp.getWriter().write(json);
		    		
		    	}else {
		    		response.setCode(405);
		    		response.setResultado("El mes seleccionado se ha procesado. Desea reprocesarlo de nuevo?");
		    		String json = (new Gson()).toJson(response);
					responseHttp.setContentType("application/json");
					responseHttp.setCharacterEncoding("UTF-8");
					responseHttp.getWriter().write(json);	
		    	}
		    }
			
			/*String json = (new Gson()).toJson(response);
			responseHttp.setContentType("application/json");
			responseHttp.setCharacterEncoding("UTF-8");
			responseHttp.getWriter().write(json);*/

		}

	}
	
	@PostMapping({ LoginController.ruta + "/datos-banco-empleado" })
	public void agregarDatosBanco(@ModelAttribute("idEmpleado") String idEmpleado,
								  @ModelAttribute("banco") int bancoId, @ModelAttribute("cuenta") String cuenta,
								  @ModelAttribute("formaCobro") int formaCobro,@ModelAttribute("moneda") int moneda, Model modelo, HttpServletResponse responseHttp)
			throws IOException, ParseException, SQLException {
		if (LoginController.session.getToken() != null) {
			modelo.addAttribute("user", LoginController.session.getUser());

			banco.setIdbanco(bancoId);			
			banco.setIdempleado(idEmpleado);
			banco.setIdformacobro(formaCobro);
			banco.setIdmoneda(moneda);
			banco.setCuenta(cuenta);

			ResponseResultado response = new ResponseResultado();
			response.setCode(200);
			response.setResultado(idEmpleado);
			String json = (new Gson()).toJson(response);
			responseHttp.setContentType("application/json");
			responseHttp.setCharacterEncoding("UTF-8");
			responseHttp.getWriter().write(json);

		}

	}

	@PostMapping({ LoginController.ruta + "/marca-empleado" })
	public void guardarMarcaEmpleado(@ModelAttribute("marcaEntrada") boolean marcaEntrada,
									 @ModelAttribute("marcaSalida") boolean marcaSalida, Model modelo, HttpServletResponse responseHttp)
			throws IOException, ParseException, SQLException {
		ResponseResultado response = new ResponseResultado();
		try {
			if (LoginController.session.getToken() != null) {
				modelo.addAttribute("user", LoginController.session.getUser());
				MarcasEmpleado marcas = new MarcasEmpleado();
				Date fecha = Utils.obtenerFechaPorFormatoDateSQLDate();
				CompositeIdMarcasEmpleado compositeId = new CompositeIdMarcasEmpleado();
				ResponseCalendarioEmpleado responseHorario = LoginController.conEmpl.consultarHorarioLaboral(
						LoginController.session.getUser().getEmpleado().getDocumento(),fecha.toLocalDate().getDayOfMonth(), fecha.toLocalDate().getMonth().getValue(),fecha.toLocalDate().getYear());
				if (!responseHorario.isStatus()) {
					String json = (new Gson()).toJson(responseHorario);
					responseHttp.setContentType("application/json");
					responseHttp.setCharacterEncoding("UTF-8");
					responseHttp.getWriter().write(json);
	//

				} else {

					ResponseMarcasPorEmpleado responseMarca = LoginController.conEmpl
							.buscarMarcasEmpleado(LoginController.session.getUser().getEmpleado().getIdempleado(), fecha);
					if (responseMarca.isStatus()) {

						marcas = responseMarca.getMarcas();
						compositeId = marcas.getCompositeId();
						compositeId.setFecha(responseMarca.getMarcas().getCompositeId().getFecha());

					} else {
						compositeId.setIdempleado(LoginController.session.getUser().getEmpleado().getIdempleado());
						compositeId.setIdmarca(Utils.generarCodigo());
						marcas.setEstado("MARCA");
						compositeId.setFecha(Utils.obtenerFechaPorFormato(FormatoFecha.YYYYMMDD.getFormato()));
						marcas.setIdhorario(  responseHorario.getHorario().get(0).getIdHorario() );


					}

					if (marcaEntrada) {
						marcas.setMarcaentrada(Utils.obtenerFechaPorFormato(FormatoFecha.H24.getFormato()));
					}
					if (marcaSalida) {
						marcas.setMarcasalida(Utils.obtenerFechaPorFormato(FormatoFecha.H24.getFormato()));
					}
					marcas.setTipo("MANUAL");
					marcas.setProceso("SIN PROCESAR");
					marcas.setCompositeId(compositeId);
					 response = LoginController.conEmpl.addMarcaEmpleado(marcas);

					String json = (new Gson()).toJson(response);
					responseHttp.setContentType("application/json");
					responseHttp.setCharacterEncoding("UTF-8");
					responseHttp.getWriter().write(json);

				}
			}
		} catch (Exception e) {
			response.setCode(404);
			response.setStatus(false);
			response.setResultado(e.getMessage());
			error.setMenssage(e.getMessage());
			response.setError(error);
			String json = (new Gson()).toJson(response);
			responseHttp.setContentType("application/json");
			responseHttp.setCharacterEncoding("UTF-8");
			responseHttp.getWriter().write(json);
		}
		
		

	}

	@PostMapping({ LoginController.ruta + "/procesar-marcas" })
	public void procesarMarcas(@ModelAttribute("mes") String mes, @ModelAttribute("estado") String estado, Model modelo,
							   HttpServletResponse responseHttp) throws IOException, ParseException, SQLException {
		if (LoginController.session.getToken() != null) {
			modelo.addAttribute("user", LoginController.session.getUser());
			String[] data = mes.split("-");
			int mesF = Integer.parseInt(data[0]);
			int anio = Integer.parseInt(data[1]);
			Date fecha = Utils.obtenerFechaPorFormatoDateSQLDate();
			/*ResponseCalendarioEmpleado responseHorario = LoginController.conEmpl.consultarHorarioLaboral(
					LoginController.session.getUser().getEmpleado().getDocumento(),fecha.toLocalDate().getDayOfMonth(), fecha.toLocalDate().getMonth().getValue(), fecha.toLocalDate().getYear());
			*/
			
			responseMarcaFiltre = LoginController.conEmpl.filtrarMarcas(mesF, anio, estado);
			String json = (new Gson()).toJson(responseMarcaFiltre);
			responseHttp.setContentType("application/json");
			responseHttp.setCharacterEncoding("UTF-8");
			responseHttp.getWriter().write(json);

		}
	}
	

	@PostMapping({ LoginController.ruta + "/obtener-marcas-procesadas" })
	public void obtenerMarcasEmpleado(@ModelAttribute("mes") String mes, @ModelAttribute("documento") String documento, Model modelo,
							   HttpServletResponse responseHttp) throws IOException, ParseException, SQLException {
		if (LoginController.session.getToken() != null) {
			modelo.addAttribute("user", LoginController.session.getUser());
			String[] data = mes.split("-");
			int mesF = Integer.parseInt(data[0]);
			int anio = Integer.parseInt(data[1]);
			
			ResponseMarcasProcessEmpl response = LoginController.conEmpl.obtenerMarcasProcesadasEmpleado(documento,mesF, anio);
			String json = (new Gson()).toJson(response);
			responseHttp.setContentType("application/json");
			responseHttp.setCharacterEncoding("UTF-8");
			responseHttp.getWriter().write(json);

		}
	}
	
	@PostMapping({ LoginController.ruta + "/procesar-marcas-all" })
	public void procesarMarcasAll(@ModelAttribute("mes") String mes, Model modelo,
							   HttpServletResponse responseHttp) throws IOException, ParseException, SQLException {
		if (LoginController.session.getToken() != null) {
			modelo.addAttribute("user", LoginController.session.getUser());
			String[] data = mes.split("-");
			int mesF = Integer.parseInt(data[0]);
			int anio = Integer.parseInt(data[1]);
						
			ResponseResultado response = LoginController.conEmpl.procesoMarcasAsistencia(mesF, anio);
			response.setCode(200);
			response.setStatus(true);
			response.setResultado("Las marcas para la fecha: "+mes+" se estan procesando.");
			String json = (new Gson()).toJson(response);
			responseHttp.setContentType("application/json");
			responseHttp.setCharacterEncoding("UTF-8");
			responseHttp.getWriter().write(json);

		}
	}
	
	@PostMapping({ LoginController.ruta + "/procesar-marcas-empleado" })
	public void procesarMarcasEmpleado(@ModelAttribute("idEmpleado") String idEmpleado,@ModelAttribute("entrada") String entrada,
							   @ModelAttribute("salida") String salida,Model modelo,
							   HttpServletResponse responseHttp) throws IOException, ParseException, SQLException {
		if (LoginController.session.getToken() != null) {
			modelo.addAttribute("user", LoginController.session.getUser());
			ResponseResultado response= new ResponseResultado();
			if(responseMarcaFiltre==null) {
				response.setCode(400);
				response.setStatus(false);
				response.setResultado("Busqueda caduca, intente de nuevo.");
				String json = (new Gson()).toJson(response);
				responseHttp.setContentType("application/json");
				responseHttp.setCharacterEncoding("UTF-8");
				responseHttp.getWriter().write(json);
			}else {
				MarcasEmpleado marca= responseMarcaFiltre.getMarcas().stream().filter(e -> e.getCompositeId().getIdempleado().equalsIgnoreCase(idEmpleado)).findFirst().get();
				marca.setMarcaentrada(entrada);
				marca.setMarcasalida(salida);
				response= LoginController.conEmpl.procesarMarcaEmpleado(marca);
				//responseMarcaFiltre = new ResponseListaMarcasEmpleados();
				response.setResultado("La marca se envio a procesar.");
				response.setCode(200);
				response.setStatus(true);
				String json = (new Gson()).toJson(response);
				responseHttp.setContentType("application/json");
				responseHttp.setCharacterEncoding("UTF-8");
				responseHttp.getWriter().write(json);
			}
			

		}
	}

	@PostMapping({ LoginController.ruta + "/filtrar-calendario" })
	public void calendarioMarcas(@ModelAttribute("numero") String numero, @ModelAttribute("mes") int mes,
								 @ModelAttribute("annio") int anio, Model modelo, HttpServletResponse responseHttp)
			throws IOException, ParseException, SQLException {
		if (LoginController.session.getToken() != null) {

			ResponseCalendarioEmpleado response = LoginController.conEmpl.consultarHorarioLaboral(numero,0, mes, anio);
			String json = (new Gson()).toJson(response);
			responseHttp.setContentType("application/json");
			responseHttp.setCharacterEncoding("UTF-8");
			responseHttp.getWriter().write(json);

		}
	}

	@PostMapping({ LoginController.ruta + "/cambiar-password" })
	public void cambiarPasword(@ModelAttribute("vieja") String vieja, @ModelAttribute("nueva") String nueva,
							   @ModelAttribute("renueva") String renueva, Model modelo, HttpServletResponse responseHttp)
			throws IOException, ParseException, SQLException {
		if (LoginController.session.getToken() != null) {
			modelo.addAttribute("user", LoginController.session.getUser());

			ResponseResultado response = LoginController.conUser
					.obtenerToken(LoginController.session.getUser().getUsuario(), vieja);
			if (response.getCode() == 200) {
				RequestAddUsuario request = new RequestAddUsuario();
				User user = LoginController.session.getUser();

				user.setPassword(renueva);
				request.setUser(user);
				ResponseResultado update = LoginController.conUser.cambiarPassword(LoginController.session.getToken(),
						LoginController.session.getUser().getIdusuario(), nueva);
				String json = (new Gson()).toJson(update);
				responseHttp.setContentType("application/json");
				responseHttp.setCharacterEncoding("UTF-8");
				responseHttp.getWriter().write(json);

			} else {
				if (response.getCode() == 406) {
					response.setResultado("La clave anterior no es correcta.");
					String json = (new Gson()).toJson(response);
					responseHttp.setContentType("application/json");
					responseHttp.setCharacterEncoding("UTF-8");
					responseHttp.getWriter().write(json);
				} else {
					String json = (new Gson()).toJson(response);
					responseHttp.setContentType("application/json");
					responseHttp.setCharacterEncoding("UTF-8");
					responseHttp.getWriter().write(json);
				}

			}
		}
	}
}
