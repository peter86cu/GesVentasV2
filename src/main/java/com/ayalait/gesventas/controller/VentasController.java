package com.ayalait.gesventas.controller;

import com.google.gson.Gson;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


import com.ayalait.gesventas.request.RequestVentaDevoluciones;
import com.ayalait.gesventas.utils.*;
import com.ayalait.modelo.*;
import com.ayalait.modelo.ItemsVenta;
import com.ayalait.response.*;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;


@Controller
public class VentasController {

    private static FormatoFecha fechaSinHora;
    private static FormatoFecha fechaConHora;
    private static RequestVentaDevoluciones devolucion= new RequestVentaDevoluciones();
    private static List<VentasProductosDevolucion> lstProductos= new ArrayList<VentasProductosDevolucion>();


    public VentasController() {
        fechaSinHora = FormatoFecha.YYYYMMDD;
        fechaConHora = FormatoFecha.YYYYMMDDH24;
    }

    @PostMapping({LoginController.ruta + "/punto-venta"})
    public void puntoVneta(@ModelAttribute("estado") String estado,@ModelAttribute("id") int idVenta,@ModelAttribute("accion") String accion, Model modelo, RedirectAttributes attribute, HttpServletResponse responseHttp) throws IOException, ParseException {
        if (LoginController.session.getToken() != null) {
            modelo.addAttribute("user", LoginController.session.getUser());
            ResponseListaConfiguraciones response = LoginController.conParam.cargarConfiguraciones();
            ResponseResultado resultado = new ResponseResultado();
            if(estado.equalsIgnoreCase("modificar")) {
            	VentasDevoluciones venta = new VentasDevoluciones();
            	venta.setFecha(Utils.obtenerFechaPorFormato(FormatoFecha.YYYYMMDDH24.getFormato()));
            	venta.setId_venta(idVenta);
            	venta.setId_devolucion(Utils.generarCodigo());
            	venta.setEfectuado(LoginController.session.getUser().getIdusuario());
            	devolucion.setDevolucion(venta);
            	
            }
            if (response.isStatus()) {
            	
            
            		 modelo.addAttribute("claves", LoginController.conParam.cargarConfiguraciones().getConfiguraciones());

                     ResponseValidarEstadoCaja responseApertura = LoginController.conCaja.validarCaja(LoginController.session.getToken(), Utils.obtenerFechaPorFormato(fechaSinHora.getFormato()));
                     if (responseApertura.isStatus()) {


                         ResponseAperturaTerminal responseTerminal = LoginController.conCaja.obtenerAperturaTerminalPorUsuario(LoginController.session.getToken(), LoginController.session.getUser().getIdusuario(), Utils.obtenerFechaPorFormato(fechaSinHora.getFormato()));
                         if (responseTerminal.isStatus()) {
                             if (responseTerminal.getTerminal() == null) {
                                 responseTerminal.setStatus(false);
                                 responseTerminal.setCode(406);
                                 ResponseApertura temp = new ResponseApertura();
                                 temp.setMensaje("El usuario " + LoginController.session.getUser().getUsuario() + " no ha abirto la terminal para inicial las ventas.");
                                 responseTerminal.setTerminalCierre(temp);
                                 String json = (new Gson()).toJson(responseTerminal);
                                 responseHttp.setContentType("application/json");
                                 responseHttp.setCharacterEncoding("UTF-8");
                                 responseHttp.getWriter().write(json);
                             } else {
                                 String json = (new Gson()).toJson(responseTerminal);
                                 responseHttp.setContentType("application/json");
                                 responseHttp.setCharacterEncoding("UTF-8");
                                 responseHttp.getWriter().write(json);
                             }
                         } else {
                             String json = (new Gson()).toJson(responseTerminal);
                             responseHttp.setContentType("application/json");
                             responseHttp.setCharacterEncoding("UTF-8");
                             responseHttp.getWriter().write(json);
                         }
                     } else {
                         String json = (new Gson()).toJson(responseApertura);
                         responseHttp.setContentType("application/json");
                         responseHttp.setCharacterEncoding("UTF-8");
                         responseHttp.getWriter().write(json);
                     }
                


            } else {
                String json = (new Gson()).toJson(response);
                responseHttp.setContentType("application/json");
                responseHttp.setCharacterEncoding("UTF-8");
                responseHttp.getWriter().write(json);
            }
            // return "punto-venta";
            // } else {
            // return "login";
        }
    }

    @GetMapping({"/punto-venta"})
    public String accesoPuntoVenta(Model modelo, RedirectAttributes attribute) {
        if (LoginController.session.getToken() != null) {
            modelo.addAttribute("user", LoginController.session.getUser());
            ResponseListaConfiguraciones response = LoginController.conParam.cargarConfiguraciones();
            if(response.isStatus()) {
                modelo.addAttribute("claves", LoginController.conParam.cargarConfiguraciones().getConfiguraciones());

            }else {
                modelo.addAttribute("claves", new ArrayList<Configuraciones>());

            }
            ResponseListaEstadoVentasDevueltos responseVD= LoginController.conTerminal.lstadoEstadoCausaDevueltosVentas();
            if(responseVD.isStatus()) {
                modelo.addAttribute("estadoDevueltos", responseVD.getCausaDevueltos());

            }else {
            	modelo.addAttribute("estadoDevueltos", new ArrayList<VentasCausaDevueltos>());
            }
            ResponseListaFormasCobro formasCobro=LoginController.conParam.listaFormasCobros();
            if(formasCobro.isStatus()) {
                modelo.addAttribute("formaCobros", formasCobro.getFormasCobro());
            }else {
                modelo.addAttribute("formaCobros", new ArrayList<FormasCobro>());

            }
        	ResponseMonedas responseM = LoginController.conParam.listarMonedas();
			if (responseM.isStatus()) {
				modelo.addAttribute("listaMoneda", responseM.getMonedas());
			}else {
				modelo.addAttribute("listaMoneda", new ArrayList<Moneda>());

			}
            
            
            return "punto-venta";


        } else {
            return "login";
        }
    }


    @GetMapping({"/listado-ventas"})
    public String listadoVentas(Model modelo, RedirectAttributes attribute) {
        if (LoginController.session.getToken() != null) {
            modelo.addAttribute("user", LoginController.session.getUser());
            boolean acceso=false;
            if(LoginController.session.getUser().getIdrol().getIdrol()==1 || LoginController.session.getUser().getIdrol().getIdrol()==2 || LoginController.session.getUser().getIdrol().getIdrol()==4) {
            	acceso=true;
            }
            ResponseUltimas10Venta response= LoginController.conTerminal.ultimas10Ventas(LoginController.session.getUser().getIdusuario(), acceso);
            
            ResponseListaUsuario listaUser=LoginController.conUser.listadoUsuarios(LoginController.session.getToken());
            
            if(listaUser.isStatus()) {
                modelo.addAttribute("listaUsuarios", listaUser.getUser());
            }else {
                modelo.addAttribute("listaUsuarios", new ArrayList<User>());

            }
            
            if(response.isStatus()) {            	
                modelo.addAttribute("listaVentas", response.getVenta());
            }else {
                modelo.addAttribute("listaVentas", new ArrayList<Ventas>());
            }
            
            ResponseListaEstadoVentas responseEV= LoginController.conTerminal.lstadoEstadoVentas();
            if(responseEV.isStatus()) {
                modelo.addAttribute("estadoVenta", responseEV.getEstadoVentas());

            }else {
                modelo.addAttribute("estadoVenta", new ArrayList<VentasEstados>());

            }
			ResponseMonedas responseM = LoginController.conParam.listarMonedas();
            if(responseM.isStatus()) {
                modelo.addAttribute("listaMoneda", responseM.getMonedas());
            }
            return "listado-ventas";


        } else {
            return "login";
        }
    }

    @PostMapping({LoginController.ruta +"/guardar-venta"})
    public void guardarVenta(@ModelAttribute("accion") String accion,@ModelAttribute("total") BigDecimal total, @ModelAttribute("id_venta") int idVenta,
                             @ModelAttribute("forma_pago") String forma_pago, @ModelAttribute("cantidad") String cantidad,
                             @ModelAttribute("tipo") String tipo, @ModelAttribute("cliente") String cliente,
                             @ModelAttribute("vuelto") int vuelto, @ModelAttribute("iva") BigDecimal iva5,
                             @ModelAttribute("puntos") int puntos,
                             @ModelAttribute("meses") int meses, @ModelAttribute("interes_venta") int interes_venta,
                             @ModelAttribute("redondeo") BigDecimal redondeo,@ModelAttribute("id_autoriza") String id_autoriza, Model modelo, HttpServletResponse responseHttp) throws IOException, ParseException {
        ResponseResultado responseAdd = new ResponseResultado();
        if (LoginController.session.getToken() != null) {
            String[] arrayFormaPago = forma_pago.split("\\|");
            String[] arrayCantidad = cantidad.split("\\|");
            if (arrayFormaPago != null) {
                ResponseListaFormasCobro responseFC = LoginController.conParam.listaFormasCobros();
                ResponseMonedas responseM = LoginController.conParam.listarMonedas();

                ResponseValidarEstadoCaja responseCaja = LoginController.conCaja.validarCaja(LoginController.session.getToken(), fechaSinHora.getFormato());

                for (int i = 0; i < arrayFormaPago.length; ++i) {
                    double val_cotizacion = 0.0D;
                    double val_input_credito = 0.0D;
                    int idFormaCobro = 0;
                    new BigDecimal(0);
                    List<FormasCobro> var33= new ArrayList<FormasCobro>();
                    int var32 =   responseFC.getFormasCobro().size();

                    for (int var31 = 0; var31 < var32; ++var31) {
                        FormasCobro forma = responseFC.getFormasCobro().get(var31);
                        if (arrayFormaPago[i].equals(forma.getSimbolo()) && forma.getIdFormaCobro() == 7) {
                            idFormaCobro = forma.getIdFormaCobro();
                            List<Moneda> var37= new ArrayList<Moneda>();
                            int var36 =  responseM.getMonedas().size();

                            for (int var35 = 0; var35 < var36; ++var35) {
                                Moneda moneda = var37.get(var35);
                                if (moneda.getIdCambioVenta() == forma.getIdFormaCobro()) {
                                    ResponseHistoricoCambio responseHC = LoginController.conCaja.obtenerHitoricoCambioPorIDAperturaMoneda(LoginController.session.getToken(), responseCaja.getApertura().getId(), moneda.getId());
                                    if (responseHC.isStatus()) {
                                        val_cotizacion = responseHC.getHistoricoCambio().getValorcompra();
                                        break;
                                    }
                                }
                            }
                        } else if (arrayFormaPago[i].equals(forma.getSimbolo())) {
                            idFormaCobro = forma.getIdFormaCobro();
                            break;
                        }
                    }

                    BigDecimal cantTemp = new BigDecimal(Integer.parseInt(arrayCantidad[i]));
                    BigDecimal cobro;
                    if (cantTemp.compareTo(total) == 1) {
                        cobro = total;
                    } else {
                        cobro = cantTemp;
                    }

                    VentasCobro venta = new VentasCobro();
                    venta.setIdVentaCobro(0);
                    venta.setIdVenta(idVenta);
                    venta.setIdFormaCobro(idFormaCobro);
                    venta.setCobro(cobro);
                    venta.setCondicion(0);
                    venta.setCobro(cobro);
                    venta.setValor(cantTemp);
                    responseAdd = LoginController.conStock.guardarVentaCobro(venta);
                }

                if (responseAdd.isStatus()) {
                    if (!cliente.equalsIgnoreCase("1")) {

                        //Guardo los puntos cliente
                    }

                    ResponseVenta responseVenta = LoginController.conStock.obtenerVentaPorId(idVenta);
                    if (responseVenta.isStatus()) {
                        Ventas update = responseVenta.getVenta();
                        update.setMonto_total(total.doubleValue());
                        if(accion.equalsIgnoreCase("modificar")) {
                        	update.setEstado(5);
                        	update.setId_usuario_autoriza(id_autoriza);
                        }else {
                        	update.setEstado(2);
                        }
                        
                        update.setFecha_hora_cerrado(Utils.obtenerFechaPorFormato(fechaConHora.getFormato()));
                        update.setIva5(iva5.doubleValue());
                        update.setIva10(0);
                        update.setRedondeo(redondeo.doubleValue());
                        update.setId_venta(idVenta);
                        if(cliente!="")
                        update.setId_cliente(cliente);
                        ResponseResultado updateVenta = LoginController.conStock.addVenta(update);
                        String json;
                        if (updateVenta.isStatus()) {
                        	if(accion.equalsIgnoreCase("modificar")) {
                        		VentasDevoluciones venta = devolucion.getDevolucion();
                            	venta.setSaldo_devuelto(vuelto);
                            	ResponseResultado responseDevolucion= LoginController.conTerminal.guardarDevolucionVenta(devolucion);
                        	}
                        	
                        	
                        	
                            //Inserto los puntos del cliente
                            if (!cliente.equalsIgnoreCase("1")) {
                                if (Utils.insertarPuntosClientes(Integer.parseInt(cliente), idVenta, puntos)) {
                                    json = (new Gson()).toJson(updateVenta);
                                    responseHttp.setContentType("application/json");
                                    responseHttp.setCharacterEncoding("UTF-8");
                                    responseHttp.getWriter().write(json);
                                } else {
                                    updateVenta.setCode(400);
                                    updateVenta.setResultado("No se pudo insertar los puntos del cliente.");
                                    json = (new Gson()).toJson(updateVenta);
                                    responseHttp.setContentType("application/json");
                                    responseHttp.setCharacterEncoding("UTF-8");
                                    responseHttp.getWriter().write(json);
                                }
                            } else {
                                json = (new Gson()).toJson(updateVenta);
                                responseHttp.setContentType("application/json");
                                responseHttp.setCharacterEncoding("UTF-8");
                                responseHttp.getWriter().write(json);
                            }


                        } else {
                            json = (new Gson()).toJson(updateVenta);
                            responseHttp.setContentType("application/json");
                            responseHttp.setCharacterEncoding("UTF-8");
                            responseHttp.getWriter().write(json);
                        }
                    }
                }
            }
        }

    }
    
    @PostMapping({LoginController.ruta + "/guardar-devolucion"})
    public void guardarDevolucion(@ModelAttribute("user") String user, @ModelAttribute("pass") String pass,
                                           Model modelo, HttpServletResponse responseHttp) throws IOException, ParseException {
        if (LoginController.session.getToken() != null) {
        	ResponseResultado response = new ResponseResultado();
            modelo.addAttribute("user", LoginController.session.getUser());
             
            
           
        	if (response.getCode()==200) {
        		
        	}
        }
    }
    
    
    @PostMapping({LoginController.ruta + "/guardar-id-transaccion-tarjeta-venta"})
    public void actualizarIdTransaccionTarjeta(@ModelAttribute("idVenta") int idVenta, @ModelAttribute("id_transaccion") String id_transaccion,
                                           Model modelo, HttpServletResponse responseHttp) throws IOException, ParseException {
        if (LoginController.session.getToken() != null) {
    		ResponseResultado response =  LoginController.conTerminal.actualizarTransaccionVentaTarjeta(idVenta, id_transaccion);
            modelo.addAttribute("user", LoginController.session.getUser());

    		
    			String json = (new Gson()).toJson(response);
                 responseHttp.setContentType("application/json");
                 responseHttp.setCharacterEncoding("UTF-8");
                 responseHttp.getWriter().write(json);
			
           
        }
    }
        
    @PostMapping({LoginController.ruta + "/autorizar-devolucion"})
    public void autorizarDevolucion(@ModelAttribute("user") String user, @ModelAttribute("pass") String pass,
                                           Model modelo, HttpServletResponse responseHttp) throws IOException, ParseException {
        if (LoginController.session.getToken() != null) {
    		ResponseResultado response = new ResponseResultado();

            modelo.addAttribute("user", LoginController.session.getUser());
            ResponseUsuario usuarioLogin = new ResponseUsuario();
            response = LoginController.conUser.obtenerToken(user, pass);
        	if (response.getCode()==200) {
				
				 usuarioLogin = LoginController.conUser.obtenerDatosUsuarioLogin(response.getResultado(), user);
				if(usuarioLogin.isStatus()) {
					if(usuarioLogin.getUser().getIdrol().getDescripcion().equalsIgnoreCase("SUPERVISOR")|| usuarioLogin.getUser().getIdrol().getDescripcion().equalsIgnoreCase("ADMINISTRADOR")||
							usuarioLogin.getUser().getIdrol().getDescripcion().equalsIgnoreCase("MASTER")	) {
						response.setCode(200);
						response.setStatus(true);
						VentasDevoluciones venta=devolucion.getDevolucion();
						venta.setAprobado(usuarioLogin.getUser().getIdusuario());
						devolucion.setDevolucion(venta);
						response.setResultado("Autorizado a "+ usuarioLogin.getUser().getEmpleado().getNombre()+" "+usuarioLogin.getUser().getEmpleado().getApellidos());
						String json = (new Gson()).toJson(usuarioLogin);
                        responseHttp.setContentType("application/json");
                        responseHttp.setCharacterEncoding("UTF-8");
                        responseHttp.getWriter().write(json);
					}else {
						response.setCode(400);
						response.setStatus(false);
						response.setResultado("El usuario no esta autorizado. Contactar con SUPERVISOR o administrador.");
						String json = (new Gson()).toJson(response);
                        responseHttp.setContentType("application/json");
                        responseHttp.setCharacterEncoding("UTF-8");
                        responseHttp.getWriter().write(json);
						
					}
				}
        	}else {
        		usuarioLogin.setCode(400);
        		usuarioLogin.setStatus(false);
        		usuarioLogin.setResultado(response.getResultado());
				String json = (new Gson()).toJson(usuarioLogin);
                responseHttp.setContentType("application/json");
                responseHttp.setCharacterEncoding("UTF-8");
                responseHttp.getWriter().write(json);
        	}
        }
    }

    @PostMapping({LoginController.ruta + "/datos-arqueo"})
    public void buscardatosAperturaInicial(@ModelAttribute("accion") String accion, @ModelAttribute("idApertura") int idApertura,
                                           Model modelo, HttpServletResponse responseHttp) throws IOException, ParseException, SQLException {
        if (LoginController.session.getToken() != null) {
            new ResponseResultado();
            modelo.addAttribute("user", LoginController.session.getUser());
            if (accion.equalsIgnoreCase("buscarArqueo")) {
                idApertura = Utils.buscasAperturaCajero(LoginController.session.getUser().getIdusuario());
                if (idApertura > 0) {
                    Ventas ventaInicial = new Ventas();
                    ventaInicial.setId_venta(0);
                    ventaInicial.setEstado(1);
                    ventaInicial.setId_usuario(LoginController.session.getUser().getIdusuario());
                    ventaInicial.setId_sucursal(LoginController.session.getUser().getEmpleado().getIdsucursal());
                    ventaInicial.setNro_consecutivo(Utils.generarNroConsecutivo(LoginController.session.getUser().getIdusuario(), idApertura));
                    ventaInicial.setId_apertura_cajero(idApertura);
                    ventaInicial.setId_cliente("1");
                    ventaInicial.setFecha_hora(Utils.obtenerFechaPorFormato(fechaConHora.getFormato()));
                    ResponseResultado response = LoginController.conStock.addVenta(ventaInicial);
                    List<ItemsAperturaCajero> ventaNueva = Utils.buscasAperturaCajeroVentas(LoginController.session.getUser().getIdusuario(),true,0);
                    String json;
                    if (response.isStatus()) {
                        json = (new Gson()).toJson(ventaNueva);
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
        } else {
            responseHttp.setContentType("application/json");
            responseHttp.setCharacterEncoding("UTF-8");
            responseHttp.getWriter().write("LoginController.session caducada");
        }

    }

    @PostMapping({LoginController.ruta + "/buscar-ventas-apertura"})
    public void validarVentasApertura(@ModelAttribute("accion") String accion, @ModelAttribute("id") String idUsuario, Model modelo, HttpServletResponse responseHttp) throws IOException, ParseException {
        ResponseAperturaTerminal response = new ResponseAperturaTerminal();
        String json;
        if (LoginController.session.getToken() != null) {
            modelo.addAttribute("user", LoginController.session.getUser());
            response = LoginController.conCaja.obtenerAperturaTerminalPorUsuario(LoginController.session.getToken(), idUsuario, Utils.obtenerFechaPorFormato(fechaSinHora.getFormato()));
            json = (new Gson()).toJson(response);
            responseHttp.setContentType("application/json");
            responseHttp.setCharacterEncoding("UTF-8");
            responseHttp.getWriter().write(json);
        } else {
            LoginController.session = new Session();
            response.setCode(400);
            response.setResultado("LoginController.session caducada");
            json = (new Gson()).toJson(response);
            responseHttp.setContentType("application/json");
            responseHttp.setCharacterEncoding("UTF-8");
            responseHttp.getWriter().write(json);
        }

    }

    @PostMapping({LoginController.ruta + "/buscar-actualizar-cliente"})
    public void buscarClientePorCI(@ModelAttribute("accion") String accion, @ModelAttribute("ci") String ci, @ModelAttribute("idVenta") int idVenta, Model modelo, HttpServletResponse responseHttp) throws IOException, ParseException {
        ResponseCliente response = new ResponseCliente();
        String json;
        if (LoginController.session.getToken() != null) {
            modelo.addAttribute("user", LoginController.session.getUser());
            response = LoginController.conParam.obtenerClientePorCI(ci);
            if (response.isStatus()) {
                new Ventas();
                ResponseVenta ventaUP = LoginController.conStock.obtenerVentaPorId(idVenta);
                Ventas venta = ventaUP.getVenta();
                venta.setId_cliente(response.getCliente().getIdCliente());
                ResponseResultado responseUpdate = LoginController.conStock.addVenta(venta);
                if (responseUpdate.isStatus()) {
                    json = (new Gson()).toJson(response);
                    responseHttp.setContentType("application/json");
                    responseHttp.setCharacterEncoding("UTF-8");
                    responseHttp.getWriter().write(json);
                }
            } else {
                json = (new Gson()).toJson(response);
                responseHttp.setContentType("application/json");
                responseHttp.setCharacterEncoding("UTF-8");
                responseHttp.getWriter().write(json);
            }
        } else {
            LoginController.session = new Session();
            response.setCode(400);
            response.setResultado("");
            json = (new Gson()).toJson(response);
            responseHttp.setContentType("application/json");
            responseHttp.setCharacterEncoding("UTF-8");
            responseHttp.getWriter().write(json);
        }

    }

    @PostMapping({LoginController.ruta + "/datos-ventas"})
    public void buscarVentaPOS(@ModelAttribute("accion") String accion,@ModelAttribute("idVenta") int idVenta, @ModelAttribute("codigo") String busqueda, Model modelo, HttpServletResponse responseHttp) throws IOException, ParseException, SQLException {
        if (LoginController.session.getToken() != null) {
            new ResponseResultado();
            boolean evento=true;
            modelo.addAttribute("user", LoginController.session.getUser());
            if (accion.equalsIgnoreCase("buscarArqueoCaja")) {
            	if(idVenta!=0) {
            		evento=false;
            	}
            		
                String json = (new Gson()).toJson(Utils.buscasAperturaCajeroVentas(LoginController.session.getUser().getIdusuario(),evento,idVenta));
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

    @PostMapping({LoginController.ruta + "/add-detalle-venta"})
    public void addDetalleVenta(@ModelAttribute("accion") String accion, @ModelAttribute("idVenta") int idVenta, @ModelAttribute("cantidad") BigDecimal cantidad, @ModelAttribute("idProducto") String idProducto, Model modelo, HttpServletResponse responseHttp) throws IOException, ParseException, SQLException {
        if (LoginController.session.getToken() != null) {
            modelo.addAttribute("user", LoginController.session.getUser());
            VentasDetalle detalle = new VentasDetalle();
            detalle.setCantidad(cantidad);
            detalle.setIdProducto(idProducto);
            detalle.setIdVenta(idVenta);
            detalle.setIdVentaDetalle(0);
            ResponseResultado response = LoginController.conStock.addDetalleVenta(detalle);
            if (response.isStatus()) {
                if (Utils.actualizarStockProducto(idProducto, cantidad)) {
                    if (!Utils.insertarControlExistencia(idProducto, cantidad, idVenta, LoginController.session.getUser().getIdusuario(), Utils.obtenerFechaPorFormato(fechaSinHora.getFormato()))) {
                        response.setCode(406);
                        response.setResultado("No se puedo actualizar el control de existencia del producto.");
                        String json = (new Gson()).toJson(response);
                        responseHttp.setContentType("application/json");
                        responseHttp.setCharacterEncoding("UTF-8");
                        responseHttp.getWriter().write(json);
                    }

                } else {
                    response.setCode(406);
                    response.setResultado("No se puedo actualizar el Stock.");
                    String json = (new Gson()).toJson(response);
                    responseHttp.setContentType("application/json");
                    responseHttp.setCharacterEncoding("UTF-8");
                    responseHttp.getWriter().write(json);
                }

            }
            String json = (new Gson()).toJson(response);
            responseHttp.setContentType("application/json");
            responseHttp.setCharacterEncoding("UTF-8");
            responseHttp.getWriter().write(json);
        }

    }

    @PostMapping({"/venta-tiempo-real"})
    public void ventasTiemporeal(@ModelAttribute("accion") String accion, @ModelAttribute("idVenta") int idVenta, Model modelo, HttpServletResponse responseHttp) throws IOException, ParseException, SQLException {
        if (LoginController.session.getToken() != null) {
            modelo.addAttribute("user", LoginController.session.getUser());
            ResponseItemsVentas response = Utils.buscarVentasTiempoReal(idVenta);
            PuntoVentaDatos resultado = new PuntoVentaDatos();
            List<ItemVentaActual> lstItem = new ArrayList<>();

            double subTotal = 0;
            double ajuste = 0;
            double total = 0;
            double ivaClaculo = 0;

            List<Configuraciones> confi =  LoginController.conParam.cargarConfiguraciones().getConfiguraciones() ;
            //Busco el id del impuesto
            int idImpuestoCalcular = 0;
            double ivaAplicar = 0;
            String ivaAMostrar = "";
            for (Configuraciones iva : confi) {
                if (iva.getId() == 18) {
                    idImpuestoCalcular = Integer.parseInt(iva.getValor());
                    break;
                }
            }
            //Buscar lista de impuesto
            ResponseImpuestos impuestos = LoginController.conParam.listarImpuestos();
            if (impuestos.isStatus()) {
                for (Impuesto iva : impuestos.getImpuestos()) {
                    if (idImpuestoCalcular == iva.getId_impuesto()) {
                        ivaAplicar = iva.getAplicar();
                        ivaAMostrar = iva.getDescripcion();
                        break;
                    }
                }
            }

            if (response.isStatus()) {
            	resultado.setStatus(true);
                resultado.setCode(response.getCode());
                double productoIvaSuma = 0;
                for (ItemsVenta item : response.getItemsVentas()) {
                    ItemVentaActual ventaActual = new ItemVentaActual();
                    ventaActual.setMonto(item.getCantidad() * item.getPrecio());
                    if (item.getIdiva() == idImpuestoCalcular) {
                        productoIvaSuma += ventaActual.getMonto();
                    }
                    ventaActual.setId_venta_detalle(item.getId_venta_detalle());
                    ventaActual.setId_producto(item.getId());
                    ventaActual.setCantidad(item.getCantidad());
                    ventaActual.setCodigo(item.getCodigo());
                    ventaActual.setPrecio(item.getPrecio());
                    ventaActual.setNombre(item.getNombre());
                    resultado.setSimboloMoneda(item.getMoneda());
                    resultado.setId_cliente(item.getId_cliente());
                    lstItem.add(ventaActual);
                }
                resultado.setItemsVentaActual(lstItem);


                for (ItemVentaActual itemV : lstItem) {
                    subTotal += itemV.getMonto();

                }
                ivaClaculo = (ivaAplicar * productoIvaSuma) / 100;
                ajuste = Math.ceil(ivaClaculo);
                total = ajuste + subTotal;
                resultado.setIVA(ivaAMostrar);
                resultado.setAjusteRedondeo(ajuste);
                resultado.setIvaCalculado(ivaClaculo);
                resultado.setSubTotal(subTotal);
                resultado.setTotalAPagar(total);

                String json = (new Gson()).toJson(resultado);
                responseHttp.setContentType("application/json");
                responseHttp.setCharacterEncoding("UTF-8");
                responseHttp.getWriter().write(json);

            } else {
                ResponseMonedas responseMoneda = LoginController.conParam.listarMonedas();

                if (responseMoneda.isStatus()) {
                    List<Moneda> lstMoneda =  responseMoneda.getMonedas();
                    for (Moneda defaultM : lstMoneda) {
                        if (defaultM.getDefecto() == 1) {
                            resultado.setSimboloMoneda(defaultM.getSimbolo());
                            break;
                        }
                    }
                }

                resultado.setIVA(ivaAMostrar);
                resultado.setAjusteRedondeo(ajuste);
                resultado.setIvaCalculado(ivaClaculo);
                resultado.setSubTotal(subTotal);
                resultado.setTotalAPagar(total);
                String json = (new Gson()).toJson(resultado);
                responseHttp.setContentType("application/json");
                responseHttp.setCharacterEncoding("UTF-8");
                responseHttp.getWriter().write(json);
            }

        }

    }

   @PostMapping({"/eliminar-producto-venta"})
   public void eliminarProductoVentasAjax(@ModelAttribute("accion") String accion, @ModelAttribute("idVentaDetalle") int idVentaDetalle,
                                          @ModelAttribute("idVenta") int idVenta,@ModelAttribute("idProducto") String idProducto,
                                          Model modelo, HttpServletResponse responseHttp) throws IOException, ParseException, SQLException {

       if(LoginController.session.getToken() != null){
           modelo.addAttribute("user", LoginController.session.getUser());
           ResponseResultado response = Utils.eliminarProductoVenta(idVenta,idVentaDetalle,idProducto);
           if(response.isStatus()) {
        	   
        	   if (Utils.actualizarStockProductoEliminadoVenta(idProducto, new BigDecimal(1))) {
        		   String json = (new Gson()).toJson(response);
                   responseHttp.setContentType("application/json");
                   responseHttp.setCharacterEncoding("UTF-8");
                   responseHttp.getWriter().write(json);
        	   }else {
        		   String json = (new Gson()).toJson("No se pudo actualizar el Stock del producto");
                   responseHttp.setContentType("application/json");
                   responseHttp.setCharacterEncoding("UTF-8");
                   responseHttp.getWriter().write(json);
        	   }
        		   
           }
           

       }else{
          String json = (new Gson()).toJson("Session caducada");
          responseHttp.setContentType("application/json");
          responseHttp.setCharacterEncoding("UTF-8");
          responseHttp.getWriter().write(json);
       }
   }
   
   
   @PostMapping({LoginController.ruta+"/eliminar-producto-devolucion"})
   public void eliminarProductoVentasDevuelto(@ModelAttribute("accion") String accion, 
                                          @ModelAttribute("idVenta") int idVenta,@ModelAttribute("idProducto") String idProducto,
                                          @ModelAttribute("cantidad") int cantidad, Model modelo, HttpServletResponse responseHttp) throws IOException, ParseException, SQLException {

       if(LoginController.session.getToken() != null){
           modelo.addAttribute("user", LoginController.session.getUser());
           ResponseResultado response = Utils.eliminarProductoDevuelto(idVenta,idProducto);
           if(response.isStatus()) {
        	   VentasProductosDevolucion productos= new VentasProductosDevolucion();
        	   productos.setId(Utils.generarCodigo());
        	   productos.setId_producto(idProducto);
        	   productos.setCantidad(cantidad);
        	   productos.setId_devolucion(devolucion.getDevolucion().getId_devolucion());
        	   lstProductos.add(productos);
        	   devolucion.setLstProductos(lstProductos);
        	   
           }
           String json = (new Gson()).toJson(response);
           responseHttp.setContentType("application/json");
           responseHttp.setCharacterEncoding("UTF-8");
           responseHttp.getWriter().write(json);

       }else{
          String json = (new Gson()).toJson("Session caducada");
          responseHttp.setContentType("application/json");
          responseHttp.setCharacterEncoding("UTF-8");
          responseHttp.getWriter().write(json);
       }
   }

}