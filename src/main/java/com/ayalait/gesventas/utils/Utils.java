package com.ayalait.gesventas.utils;


import com.ayalait.gesventas.controller.LoginController;
import com.ayalait.gesventas.imprimir.ItemOrden;
import com.ayalait.gesventas.service.wsRoles;
import com.ayalait.modelo.AccionesGestion;
import com.ayalait.modelo.Caja;
import com.ayalait.modelo.Empresa;
import com.ayalait.modelo.Gestiones;
import com.ayalait.modelo.Modulos;
import com.ayalait.modelo.Roles;
import com.ayalait.modelo.Seguridad;
import com.ayalait.modelo.Titulos;
import com.ayalait.modelo.User;
import com.ayalait.response.ResponseItemsVentas;
import com.ayalait.response.ResponseListaRoles;
import com.ayalait.response.ResponseResultado;
import com.google.gson.Gson;

  
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import com.mysql.cj.jdbc.result.ResultSetImpl;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class Utils {


	private static wsRoles conRoles = new wsRoles();

	private static Conexion conexion = new Conexion();
	private static FormatoFecha formatoFechaHoy;
	private static FormatoFecha formatoFechaSinHora;
	
	public static String CLASSWARNING="bi bi-exclamation-circle text-warning";
	public static String CLASSDANGER="bi bi-x-circle text-danger";
	public static String CLASSSUCCESS="bi bi-check-circle text-success";
	public static String CLASSPRIMARY="bi bi-info-circle text-primary";

	public Utils() {
		formatoFechaHoy = FormatoFecha.YYYYMMDDH24;
		formatoFechaSinHora= FormatoFecha.YYYYMMDD;


	}
	
	
	public static String convertFileToBase64(String filePath) {
        try {
            // Lee el archivo como un array de bytes
            Path path = Paths.get(filePath);
            byte[] fileBytes = Files.readAllBytes(path);

            // Convierte el array de bytes a una cadena Base64
            String base64String = Base64.getEncoder().encodeToString(fileBytes);

            return base64String;
        } catch (IOException e) {
            // Maneja la excepción en caso de error al leer el archivo
            e.printStackTrace();
            return null;
        }
    }
	
	public static Double formatearDecimales(Double numero, Integer numeroDecimales) {
		return Math.round(numero * Math.pow(10, numeroDecimales)) / Math.pow(10, numeroDecimales);
		}
	
	
	public static void resizeFile(String imagePathToRead, String imagePathToWrite, int resizeWidth,
		      int resizeHeight) throws IOException {
		    File fileToRead = new File(imagePathToRead);
		    BufferedImage bufferedImageInput = ImageIO.read(fileToRead);

		    BufferedImage bufferedImageOutput =
		        new BufferedImage(resizeWidth, resizeHeight, bufferedImageInput.getType());

		    Graphics2D g2d = bufferedImageOutput.createGraphics();
		    g2d.drawImage(bufferedImageInput, 0, 0, resizeWidth, resizeHeight, null);
		    g2d.dispose();

		    String formatName = imagePathToWrite.substring(imagePathToWrite.lastIndexOf(".") + 1);

		    ImageIO.write(bufferedImageOutput, formatName, new File(imagePathToWrite));
		  }
	

	public static void agregarLog(Seguridad seguridad, RestTemplate template, User usuario, String accion,
								  String metadata, String resultado) {
		seguridad.setAccion(accion);
		seguridad.setId(generarCodigo());
		seguridad.setFecha_login(obtenerFechaPorFormato(formatoFechaHoy.getFormato()));
		seguridad.setIdUsuario(String.valueOf(usuario.getIdusuario()));
		seguridad.setMetadata(metadata);
		seguridad.setResultado(resultado);
		template.exchange("http://localhost:8086/addlogueo", HttpMethod.POST,
				new HttpEntity(seguridad, (MultiValueMap) null), String.class, new Object[0]);
	}

	public static void actualizarLog(Seguridad seguridad, RestTemplate template, User usuario) {
		seguridad.setFecha_logout(obtenerFechaPorFormato(formatoFechaHoy.getFormato()));
		template.exchange("http://localhost:8086/updatelogueo", HttpMethod.POST,
				new HttpEntity(seguridad, (MultiValueMap) null), String.class, new Object[0]);
	}

	public static String obtenerFechaPorFormato(String formato) {
		Date fecha = new Date(Calendar.getInstance().getTimeInMillis());
		SimpleDateFormat formatter = new SimpleDateFormat(formato);
		return formatter.format(fecha);
	}
	
	public static long obtenerFechaPorFormatoDateSQL() {			
		Date fecha = new Date(Calendar.getInstance().getTimeInMillis());		
		return fecha.getTime();
	}

	
	public static Date obtenerFechaPorFormatoDateSQLDate() {			
		Date fecha = new Date(Calendar.getInstance().getTimeInMillis());		
		return fecha;
	}
	
	public static String generarCodigo() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}

	public static List<Estados> obtenerListaEstado() throws SQLException {
		List<Estados> lstEstados = new ArrayList<Estados>();
		ResultSet estado = Conexion.getConexion("select * from estados");

		try {
			lstEstados.add(new Estados(0, "SELECCIONAR"));

			while (estado.next()) {
				lstEstados.add(new Estados(estado.getInt("id"), estado.getString("estado")));
			}
			estado.close();
		} catch (SQLException var2) {
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, (String) null, var2);
		}finally {
			if(estado!=null)
				estado.close();
		}

		return lstEstados;
	}

	public static List<DatosStock> obtenerStock() throws SQLException {
		List<DatosStock> lstStock = new ArrayList<DatosStock>();
		ResultSet stock = Conexion.getConexion("select s.id_producto, s.cantidad stock from stock s JOIN producto p ON(p.id=s.id_producto) AND p.disponible=1");

		try {
			if(stock!=null){
				while (stock.next()) {
					lstStock.add(new DatosStock(stock.getString("id_producto"), stock.getInt("stock")));
				}
			}else {
				return lstStock;
			}
			

		} catch (SQLException var2) {
			
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, (String) null, var2);
		}finally {
			if(stock!=null)
				stock.close();
		}

		return lstStock;
	}

	public static List<Integer> obtenerListaOrdenesAprobadas() throws SQLException {
		List<Integer> lstOrdes = new ArrayList<Integer>();
		ResultSet aprobados = Conexion.getConexion("select oc.id_orden_compra  from ordenes_de_compras oc");
		try {
			
			if(aprobados!=null){
				while (aprobados.next()) {
					lstOrdes.add(aprobados.getInt("id_orden_compra"));
				}
			}else{
				return lstOrdes;
			}
			
		} catch (SQLException var2) {
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, (String) null, var2);
		}finally {
			if(aprobados!=null)
				aprobados.close();
		}

		return lstOrdes;
	}

	public static List<ServiciosAdmin> listadoServiciosAdmin() throws SQLException {
		List<ServiciosAdmin> lstService = new ArrayList<ServiciosAdmin>();
		ResultSet aprobados =null;
		ResultSet idEstado =null;
		try {
			 aprobados = Conexion.getConexion("SELECT ad.*  FROM administracion_sistema ad");
           if(aprobados!=null){
			   while (aprobados.next()) {
				   ServiciosAdmin service = new ServiciosAdmin();
				   service.setId(aprobados.getInt("id"));
				   service.setProceso(aprobados.getString("proceso"));
				   service.setFecha_ultima_ejecucion(aprobados.getString("fecha_ultima_ejecucion"));
					 idEstado = Conexion.getConexion("SELECT al.* FROM  administracion_log al  WHERE al.id_administracion="+service.getId()+" ORDER BY al.id DESC LIMIT 1");
					while(idEstado.next()) {
						service.setId_estado(idEstado.getInt("id_estado") );
					}

				   lstService.add(service);
			   }
		   }else{
			   return lstService;
		   }

		} catch (SQLException var3) {
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, (String) null, var3);
		}finally {
			if(aprobados!=null)
				aprobados.close();
			if(idEstado!=null)
				idEstado.close();
		}

		return lstService;
	}

	public static String generarNumeroFactura(int id) throws SQLException {
		String numero = "";
		ResultSet estado = Conexion.getConexion("SELECT CONCAT(codigo,'-',(select round( rand()*100000))) numero from codigo_factura where id="+id);
		try {
			while ( estado.next()) {
				 numero = estado.getString("numero");
			
			}
		} catch (SQLException var2) {
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, (String) null, var2);
		}finally {
			if(estado!=null)
				estado.close();
		}

		return numero;
	}
	
	public static String obtenerUnidadMedida(int id) throws SQLException {
		String numero = "";
		ResultSet estado = Conexion.getConexion("SELECT u.simbolo  FROM unidades_medidas u WHERE u.id_unidad_medida="+id);
		try {
			while ( estado.next()) {
				 numero = estado.getString("simbolo");
			
			}
		} catch (SQLException var2) {
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, (String) null, var2);
		}finally {
			if(estado!=null)
				estado.close();
		}

		return numero;
	}
	
	
	
	public static double obtenerIvaACalcularProducto(List<ItemsOrdenCompra> itemsCompra,String id) throws SQLException {
		double calculoIVA=0;
		double iva1=0;
		int cantidad=0;
		double sumado=0;
		ResultSet estado=null;
		try {
		if(!itemsCompra.isEmpty()) {
			for(ItemsOrdenCompra item: itemsCompra) {
				 estado = Conexion.getConexion("SELECT i.aplicar as iva,pf.cantidad FROM prefactura_detalle pf JOIN producto p ON (pf.id_producto=p.id) "
						+ "JOIN impuestos i ON(i.id_impuesto=p.idiva) WHERE p.codigo='"+item.getCodigo()+"' AND pf.id_prefactura='"+id+"' GROUP BY i.aplicar,pf.importe");
				while ( estado.next()) {
					 iva1 = estado.getDouble("iva");
					 if(item.getImporte()>0)
					 cantidad= estado.getInt("cantidad");
				
				}
				sumado=item.getImporte();
				if(cantidad>0) {
					calculoIVA=calculoIVA+(iva1*(sumado*cantidad));
				}else {
					calculoIVA=calculoIVA+(iva1*(sumado));
				}
				
			}
		}
		
		
		} catch (SQLException var2) {
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, (String) null, var2);
		}finally {
			if(estado!=null)
				estado.close();
		}

		return Math.floor(calculoIVA);
	}
	
	
	public static double obtenerIvaAFactura(List<ItemOrden> itemsCompra, String id)  {
		double calculoIVA=0;
		double iva1=0;
		int cantidad=0;
		double sumado=0;
		ResultSet estado=null;
		try {
		if(!itemsCompra.isEmpty()) {
			for(ItemOrden item: itemsCompra) {
				 estado = Conexion.getConexion("SELECT i.aplicar as iva,pf.cantidad FROM prefactura_detalle pf JOIN producto p ON (pf.id_producto=p.id) "
						+ "JOIN impuestos i ON(i.id_impuesto=p.idiva) WHERE p.codigo='"+item.getCodigo()+"' AND pf.id_prefactura='"+id+"'  GROUP BY i.aplicar,pf.importe");
				while ( estado.next()) {
					 iva1 = estado.getDouble("iva");
					 if(item.getTotal()>0)
					 cantidad= estado.getInt("cantidad");
				
				}
				
				sumado=item.getImporte();
				if(cantidad>0) {
					calculoIVA=calculoIVA+(iva1*(sumado*cantidad));
				}else {
					calculoIVA=calculoIVA+(iva1*(sumado));
				}
				
			}
		}
		
		
		} catch (SQLException var2) {
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, (String) null, var2);
		}finally {
			if(estado!=null)
				try {
					estado.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}

		return Math.floor(calculoIVA);
	}

	public static int getArqueo(int IdApertura, String idUsuario) throws SQLException {
		int id = 0;
		ResultSet arqueo = Conexion.getConexion("select id_arqueo from arqueos where id_apertura_cajero="
				+ IdApertura + " AND id_usuario='" + idUsuario + "'");
		try {
			while(arqueo.next()) {
				id = arqueo.getInt("id_arqueo");
			} 
		} catch (SQLException var4) {
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, (String) null, var4);
		}finally {
			if(arqueo!=null)
				arqueo.close();
		}

		return id;
	}

	public static Empresa obtenerDatosEmpresa() throws SQLException {
		Empresa empresa = new Empresa();
		ResultSet emp = Conexion.getConexion("select * from empresa");
		try {			

			while (emp.next()) {
				empresa.setNombre(emp.getString("nombre"));
				empresa.setId_empresa(emp.getString("id_empresa"));
				empresa.setDireccion(emp.getString("direccion"));
				empresa.setTelefono(emp.getString("telefono"));
				empresa.setEmail(emp.getString("email"));
				empresa.setRut(emp.getString("rut"));
			}
		} catch (SQLException var2) {
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, (String) null, var2);
		}finally {
			if(emp!=null)
				emp.close();
		}

		return empresa;
	}

	public static double buscarMontoDeVentas(int idApertura, String idUsuario) throws SQLException {
		int id_venta = 0;
		double montoInicial = 0.0D;
		ResultSet total= null;
		int monto_ventas = 0;
		int var8;
		ResultSet id = Conexion.getConexion(
				"select v.id_venta from ventas v, aperturas_terminal ac where v.id_apertura_cajero=ac.id_apertura_cajero and  ac.fecha_hora_cierre is null and v.estado=2 and v.id_apertura_cajero="
						+ idApertura); 
		try {
			while(id.next()) {
			var8 = id.getInt("id_venta");
			}
			
			
			montoInicial = obtenerMontoAperturaInicial(idApertura, idUsuario);

			for (total = Conexion.getConexion(
					"select sum(v.monto_total) monto_total from ventas v, ventas_cobros vc where v.id_venta= vc.id_venta and v.estado=2 and vc.id_forma_cobro= 1 and v.id_apertura_cajero="
							+ idApertura); total.next(); monto_ventas = total.getInt("monto_total")) {
			}
			
			
			
		} catch (SQLException var7) {
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, (String) null, var7);
		}finally {
			if(id!=null)
				id.close();
			if(total!=null)
				total.close();
		}
		
		if (monto_ventas == 0)
			return 0;
		return (double) monto_ventas + montoInicial;

		
	}

	public static int generarNroConsecutivoInicial() throws SQLException {
		int consecutivo = 1000;
		ResultSet id = Conexion.getConexion("SELECT  case when max(nro_consecutivo) is null then 1000 else max(nro_consecutivo)+1000 end as consecutivo FROM aperturas_terminal");

		try {
			if(id!=null){
				while (id.next()){
					consecutivo = id.getInt("consecutivo");
				}}else{
				consecutivo=Math.max(1,999);
			}
		} catch (SQLException var2) {
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, (String) null, var2);
		}finally {
			if(id!=null)
				id.close();
		}

		return consecutivo;
	}

	public static int generarNroConsecutivo(String idUsuario, int idApertura) throws SQLException {
		int consecutivo = 1000;
		ResultSet ventas= null;
		String consulta="select  case when MAX( v.nro_consecutivo) is NULL then ac.nro_consecutivo else MAX( v.nro_consecutivo)+1 END as consecutivo  from ventas v, aperturas_terminal ac where \r\nac.id_apertura_cajero=v.id_apertura_cajero AND v.estado=2 and v.id_usuario='"
				+ idUsuario + "' and ac.fecha_hora_cierre is null ";
        /*consulta="select  ac.nro_consecutivo+1  consecutivo  from ventas v, aperturas_terminal ac, clientes c where  ac.id_apertura_cajero=v.id_apertura_cajero\n" +
				" and v.id_cliente=c.id_cliente and v.id_usuario='"+ idUsuario + "' and v.fecha_hora_cerrado is null";*/

		String existeVentas="  SELECT nro_consecutivo FROM ventas WHERE id_usuario='"+idUsuario+"' and id_apertura_cajero="+idApertura+" ORDER by id_venta DESC  limit 1";
		try {
			 ventas = Conexion.getConexion(existeVentas);
			if(((ResultSetImpl) ventas).getRows().size()>0){

				String ventasAct=" select   MAX( v.nro_consecutivo)+1  as consecutivo  " +
						" from ventas v, aperturas_terminal ac where ac.id_apertura_cajero=v.id_apertura_cajero AND  v.id_usuario='"+idUsuario+"' " +
						" and ac.fecha_hora_cierre is NULL";
				for (ResultSet id = Conexion.getConexion(ventasAct); id
						.next(); consecutivo = id.getInt("consecutivo")) {
				}
			}else{
				String consecutivoInicial="SELECT  nro_consecutivo AS consecutivo FROM  aperturas_terminal ac WHERE ac.id_usuario='"+idUsuario+"' and ac.fecha_hora_cierre is NULL";
				for (ResultSet id = Conexion.getConexion(consecutivoInicial); id
						.next(); consecutivo = id.getInt("consecutivo")) {
				}

			}

            /*for (ResultSet id = Conexion.getConexion(consulta); id
									.next(); consecutivo = id.getInt("consecutivo")) {
			}*/
		} catch (SQLException var3) {
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, (String) null, var3);
			return consecutivo;

		}finally {
			if(ventas!=null)
				ventas.close();
		}

		return consecutivo;
	}

	public static double obtenerMontoAperturaInicial(int idApertura, String idUsuario) throws SQLException {
		double monto_inicial = 0.0D;
		ResultSet id=null;
		try {
			for ( id = Conexion.getConexion(
					"select sum(ad.valor*b.monto) saldo_apertura_inicial from arqueos ar , arqueos_detalle ad , billetes b, aperturas_terminal ac where ac.id_apertura_cajero=ar.id_apertura_cajero and ar.id_arqueo=ad.id_arqueo and ad.id_billete=b.id_billete\r\n\t\tand ar.id_estado_arqueo=1 and ac.id_usuario='"
							+ idUsuario + "' and ac.id_apertura_cajero=" + idApertura
							+ " and ac.fecha_hora_cierre is null"); id
						 .next(); monto_inicial = (double) id.getInt("saldo_apertura_inicial")) {
			}
		} catch (SQLException var5) {
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, (String) null, var5);
		}finally {
			if(id!=null)
				id.close();
		}

		return monto_inicial;
	}

	 public static List<Roles> obtenerListaRoles(String token) {
		List<Roles> roles = new ArrayList<Roles>();
		roles.add(new Roles(0, "SELECCIONAR"));
		ResponseListaRoles response = conRoles.consultarListaRoles(token);
		if (response.isStatus()) {
			List<Roles> var6;
			int var5 = (var6 = response.getRoles()).size();

			for (int var4 = 0; var4 < var5; ++var4) {
				Roles role = var6.get(var4);
				Roles rol = new Roles();
				rol.setDescripcion(role.getDescripcion());
				rol.setIdrol(role.getIdrol());
				List<Modulos> modules = new ArrayList<Modulos>();
				List<Gestiones> gestiones = new ArrayList<Gestiones>();
				/*Iterator var11 = role.getModulos().iterator();

				while (var11.hasNext()) {
					Modulos modulo = (Modulos) var11.next();
					Modulos mod = new Modulos(modulo.getIdmodulo(), modulo.getModulo());

					for (int i = 0; i < modulo.getGestiones().size(); ++i) {
						Gestiones gest = new Gestiones();
						gest.setIdgestion(((Gestiones) modulo.getGestiones().get(i)).getIdgestion());
						gest.setNombre(((Gestiones) modulo.getGestiones().get(i)).getNombre());
						List<AccionesGestion> accion = new ArrayList<AccionesGestion>();

						for (int j = 0; j < ((Gestiones) modulo.getGestiones().get(i)).getAccion().size(); ++j) {
							AccionesGestion acc = new AccionesGestion();
							acc.setAccion(
									((AccionesGestion) ((Gestiones) modulo.getGestiones().get(i)).getAccion().get(j))
											.getAccion());
							acc.setIdaccion(
									((AccionesGestion) ((Gestiones) modulo.getGestiones().get(i)).getAccion().get(j))
											.getIdaccion());
							accion.add(acc);
						}

						gest.setAccion(accion);
						gestiones.add(gest);
					}

					mod.setGestiones(gestiones);
					modules.add(mod);
				}

				rol.setModulos(modules);*/
				roles.add(rol);
			}
		}

		return roles;
	} 

	public static String obtenerEstadoCaja(int estado) throws SQLException {
		String estado_caja = "";
		ResultSet cajas=null;

		try {
			for ( cajas = Conexion.getConexion("select estado from estado_caja where id=" + estado); cajas
					.next(); estado_caja = cajas.getString("estado")) {
			}
		} catch (SQLException var3) {
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, (String) null, var3);
		}finally {
			if(cajas!=null)
				cajas.close();
		}

		return estado_caja;
	}

	public static List<ResponseOrdenes> listadoOrdenesCompra() throws SQLException {
		List<ResponseOrdenes> lstOrden = new ArrayList<ResponseOrdenes> ();
		ResultSet compra=null;
		try {
			compra = Conexion.getConexion(
					"SELECT oc.id_orden_compra, DATE_FORMAT(oc.fecha_hora, '%d-%m-%Y')   as fecha, p.descripcion as plazo,f.descripcion as forma_pago, (select count(*) from ordenes_de_compras_detalle where id_orden_compra=oc.id_orden_compra) as items, e.descripcion as estado," +
							" e.id_orden_compra_estado as id_estado, CONCAT(emp.nombre,' ',emp.apellidos) nombre, pro.razon_social as proveedor, pro.id_proveedor     FROM plazos p, formas_pagos f, ordenes_de_compras_estados e,usuarios u,empleado emp,ordenes_de_compras oc\r\n        left join proveedores  as pro on oc.id_proveedor=pro.id_proveedor\r\n        WHERE oc.id_plazo=p.id_plazo and oc.id_forma_pago = f.id_forma_pago and e.id_orden_compra_estado = oc.estado and u.idusuario=oc.id_usuario and oc.fecha_baja is NULL AND emp.idempleado=u.idempleado order by id_orden_compra desc");
           if(compra!=null){
			   while (compra.next()) {
				   ResponseOrdenes orden = new ResponseOrdenes();
				   orden.setEstado(compra.getString("estado"));
				   orden.setFecha(compra.getString("fecha"));
				   orden.setForma_pago(compra.getString("forma_pago"));
				   orden.setPlazo(compra.getString("plazo"));
				   orden.setItems(compra.getString("items"));
				   orden.setId_estado(compra.getString("id_estado"));
				   orden.setNombre(compra.getString("nombre"));
				   orden.setProveedor(compra.getString("proveedor"));
				   orden.setId_orden_compra(compra.getString("id_orden_compra"));
				   orden.setId_proveedor(compra.getInt("id_proveedor"));
				   lstOrden.add(orden);
			   }}else{
			   return lstOrden;
		   }
		} catch (SQLException var3) {
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, (String) null, var3);
			return lstOrden;
		}finally {
			if(compra!=null)
				compra.close();
		}

		return lstOrden;
	}
	
	
	public static List<ResponseOrdenes> listadoPrefactura() throws SQLException {
		List<ResponseOrdenes> lstOrden = new ArrayList<ResponseOrdenes> ();
		ResultSet compra=null;
		try {
			compra = Conexion.getConexion(
					"SELECT oc.id_orden_compra, DATE_FORMAT(oc.fecha_hora, '%d-%m-%Y')   as fecha, p.descripcion as plazo,f.descripcion as forma_pago, (select count(*) from ordenes_de_compras_detalle where id_orden_compra=oc.id_orden_compra) as items, e.descripcion as estado," +
							" e.id_orden_compra_estado as id_estado, CONCAT(emp.nombre,' ',emp.apellidos) nombre, pro.razon_social as proveedor, pro.id_proveedor     FROM plazos p, formas_pagos f, ordenes_de_compras_estados e,usuarios u,empleado emp,ordenes_de_compras oc\r\n        left join proveedores  as pro on oc.id_proveedor=pro.id_proveedor\r\n        WHERE oc.id_plazo=p.id_plazo and oc.id_forma_pago = f.id_forma_pago and e.id_orden_compra_estado = oc.estado and u.idusuario=oc.id_usuario and oc.fecha_baja is NULL AND emp.idempleado=u.idempleado order by id_orden_compra desc");
           if(compra!=null){
			   while (compra.next()) {
				   ResponseOrdenes orden = new ResponseOrdenes();
				   orden.setEstado(compra.getString("estado"));
				   orden.setFecha(compra.getString("fecha"));
				   orden.setForma_pago(compra.getString("forma_pago"));
				   orden.setPlazo(compra.getString("plazo"));
				   orden.setItems(compra.getString("items"));
				   orden.setId_estado(compra.getString("id_estado"));
				   orden.setNombre(compra.getString("nombre"));
				   orden.setProveedor(compra.getString("proveedor"));
				   orden.setId_orden_compra(compra.getString("id_orden_compra"));
				   orden.setId_proveedor(compra.getInt("id_proveedor"));
				   lstOrden.add(orden);
			   }}else{
			   return lstOrden;
		   }
		} catch (SQLException var3) {
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, (String) null, var3);
			return lstOrden;
		}finally {
			if(compra!=null)
				compra.close();
		}

		return lstOrden;
	}

	
	public static List<VentasPorArqueoUsuario> listadoVentasPorArqueoUsuario(String idUsuario, String fecha) throws SQLException {
		List<VentasPorArqueoUsuario> lstVentas = new ArrayList<VentasPorArqueoUsuario> ();
		ResultSet compra=null;
		try {
			compra = Conexion.getConexion("SELECT ater.id_apertura_cajero,ater.fecha_inicio,vc.id_venta_cobro,vc.cobro, fc.descripcion FROM aperturas_terminal ater, apertura_dia ad, arqueos a, ventas v, ventas_cobros vc, formas_cobros fc WHERE ater.id_apertura_dia=ad.id AND a.id_apertura_cajero=ater.id_apertura_cajero \r\n"
					+ "AND v.id_apertura_cajero=ater.id_apertura_cajero AND vc.id_venta=v.id_venta AND fc.id_forma_cobro=vc.id_forma_cobro AND ater.id_usuario='"+idUsuario+"'"
					+ "AND ater.fecha_hora_cierre IS NOT NULL AND ater.fecha_inicio='"+fecha+"' AND ater.id_tipo_arqueo=3 AND a.id_cuadre=2 AND v.estado=2");
           if(compra!=null){
			   while (compra.next()) {
				   VentasPorArqueoUsuario venta = new VentasPorArqueoUsuario();
				   venta.setId_apertura_cajero(compra.getInt("id_apertura_cajero"));
				   venta.setFecha_inicio(compra.getString("fecha_inicio"));
				   venta.setId_venta_cobro(compra.getInt("id_venta_cobro"));
				   venta.setCobro(compra.getInt("cobro"));
				   venta.setTipoCobro(compra.getString("descripcion"));
				   lstVentas.add(venta);
			   }}else{
			   return lstVentas;
		   }
		} catch (SQLException var3) {
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, (String) null, var3);
			return lstVentas;
		}finally {
			if(compra!=null)
				compra.close();
		}

		return lstVentas;
	}

	
	public static List<ResponseArqueos> listadoArqueosTerminales(String idUsuario) throws SQLException {
		List<ResponseArqueos> lstArqueos = new ArrayList<ResponseArqueos>();
		//String var1 = "SELECT ar.id_arqueo,\r\n        a.id_apertura_cajero,\r\n        a.hora_inicio as inicio,\r\n        a.fecha_hora_cierre as fin,\r\n        a.fecha_hora_cierre,\r\n        CONCAT(emp.nombre,' ',emp.apellidos) nombre,\r\n        s.nombre as sucursal,\r\n        ar.fecha_hora as fecha,ta.id AS id_tipo_arqueo,ta.descripcion,ar.id_cuadre,a.id_caja,a.id_turno\r\n        FROM usuarios u,aperturas_terminal a,sucursal s,empleado emp, arqueos ar, tipo_arqueo ta\r\n        WHERE u.idusuario=ar.id_usuario and ar.fecha_baja is null and s.id=ar.id_sucursal and ar.id_apertura_cajero=a.id_apertura_cajero\r\n        and ta.id= ar.id_estado_arqueo AND emp.idempleado=u.idempleado  AND fecha_hora_cierre IS null";
		ResultSet arq=null;
		try {
			String consulta="SELECT ar.id_arqueo, a.id_apertura_cajero, a.hora_inicio as inicio, a.fecha_hora_cierre as fin,a.fecha_hora_cierre, CONCAT(emp.nombre,' ',emp.apellidos) nombre," +
					"ar.fecha_hora as fecha,ta.id AS id_tipo_arqueo,ta.descripcion,ar.id_cuadre,a.id_caja,a.id_turno, u.idusuario,a.fecha_hora_cierre FROM usuarios u,aperturas_terminal a,empleado emp, arqueos ar, tipo_arqueo ta" +
					" WHERE u.idusuario=ar.id_usuario and ar.id_apertura_cajero=a.id_apertura_cajero  and ta.id= ar.id_estado_arqueo AND emp.idempleado=u.idempleado  AND  u.idusuario='"+ idUsuario+"' ORDER BY a.id_apertura_cajero DESC";
			 arq = Conexion.getConexion(consulta);

			if(arq!=null){
				while (arq.next()) {
					ResponseArqueos arqueos = new ResponseArqueos();
					arqueos.setId_arqueo(arq.getInt("id_arqueo"));
					arqueos.setFecha_hora_cierre(arq.getString("fin"));
					arqueos.setInicio(arq.getString("inicio"));
					arqueos.setNombre(arq.getString("nombre"));
					arqueos.setId_tipo_arqueo(arq.getInt("id_tipo_arqueo"));
					arqueos.setId_cuadre(arq.getInt("id_cuadre"));
					arqueos.setId_caja(arq.getInt("id_caja"));
					arqueos.setDescripcion(arq.getString("descripcion"));
					arqueos.setId_turno(arq.getInt("id_turno"));
					arqueos.setSucursal("");
					arqueos.setFecha_hora_cierre(arq.getString("fecha_hora_cierre"));
					arqueos.setId_usuario(arq.getString("idusuario"));
					arqueos.setFecha(arq.getString("fecha"));
					arqueos.setId_apertura_cajero(arq.getInt("id_apertura_cajero"));
					lstArqueos.add(arqueos);
				}
			}else {
				return lstArqueos;
			}

		} catch (SQLException var4) {
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, (String) null, var4);
		}finally {
			if(arq!=null)
				arq.close();
		}

		return lstArqueos;
	}

	public static List<ItemsOrdenCompra> listadoItemsCompras(String idOrden) throws SQLException {
		List<ItemsOrdenCompra> lstItems = new ArrayList<ItemsOrdenCompra>();
		ResultSet ordenes=null;
		try {
			ordenes = Conexion.getConexion(
					"select o.id_orden_de_compra_detalle as id_detalle, o.cantidad, o.importe, p.codigo,p.nombre from ordenes_de_compras_detalle o inner join producto p \r\n                             on (o.id_producto=p.id) and o.id_orden_compra='"
							+ idOrden + "'  group by p.codigo");

			while (ordenes.next()) {
				ItemsOrdenCompra items = new ItemsOrdenCompra();
				items.setCatidad(ordenes.getInt("cantidad"));
				items.setCodigo(ordenes.getString("codigo"));
				items.setId_detalle(ordenes.getInt("id_detalle"));
				items.setImporte(ordenes.getDouble("importe"));
				items.setNombre(ordenes.getString("nombre"));
				items.setTotal((double) items.getCatidad() * items.getImporte());
				lstItems.add(items);
			}
		} catch (SQLException var4) {
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, (String) null, var4);
		}finally {
			if(ordenes!=null)
				ordenes.close();
		}

		return lstItems;
	}
	
	
	public static List<ItemsOrdenCompra> listadoItemsPrefactura(String idOrden) throws SQLException {
		List<ItemsOrdenCompra> lstItems = new ArrayList<ItemsOrdenCompra>();
		ResultSet ordenes=null;
		try {
			ordenes = Conexion.getConexion(
					"select o.id_prefactura_detalle as id_detalle, o.cantidad, o.importe, p.codigo,p.nombre,p.um from prefactura_detalle o inner join producto p \r\n"
					+ " on (o.id_producto=p.id) and o.id_prefactura='"+idOrden+"'  group by p.codigo");
			if(ordenes!=null) {
				while (ordenes.next()) {
					ItemsOrdenCompra items = new ItemsOrdenCompra();
					items.setCatidad(ordenes.getInt("cantidad"));
					items.setCodigo(ordenes.getString("codigo"));
					items.setId_detalle(ordenes.getInt("id_detalle"));
					items.setImporte(ordenes.getDouble("importe"));
					items.setNombre(ordenes.getString("nombre"));
					items.setSimboloUM( obtenerUnidadMedida(ordenes.getInt("um")) );
					items.setTotal((double) items.getCatidad() * items.getImporte());
					lstItems.add(items);
				}
			}
			
		} catch (SQLException var4) {
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, (String) null, var4);
		}finally {
			if(ordenes!=null)
				ordenes.close();
		}

		return lstItems;
	}

	public static List<ItemsOrdenCompra> listadoItemsFacturas(String idOrden) throws SQLException {
		List<ItemsOrdenCompra> lstItems = new ArrayList<ItemsOrdenCompra> ();
		ResultSet ordenes=null;
		try {
			ordenes = Conexion.getConexion(
					"select o.id_entrada_compra_detalle as id_detalle, o.cantidad, o.importe, p.codigo,p.nombre\r\n\t\t\t\t from entradas_compras_detalle o inner join producto p \t\t\t\t  on (o.id_producto=p.id) and o.id_entrada_compra='"
							+ idOrden + "'  group by p.codigo");

			while (ordenes.next()) {
				ItemsOrdenCompra items = new ItemsOrdenCompra();
				items.setCatidad(ordenes.getInt("cantidad"));
				items.setCodigo(ordenes.getString("codigo"));
				items.setId_detalle(ordenes.getInt("id_detalle"));
				items.setImporte(ordenes.getDouble("importe"));
				items.setNombre(ordenes.getString("nombre"));
				items.setTotal((double) items.getCatidad() * items.getImporte());
				lstItems.add(items);
			}
		} catch (SQLException var4) {
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, (String) null, var4);
		}finally {
			if(ordenes!=null)
				ordenes.close();
		}

		return lstItems;
	}

	public static List<FacturasComprasItems> listadoFacturasCompra() throws SQLException {
		List<FacturasComprasItems> lstItems = new ArrayList<FacturasComprasItems>();
		ResultSet ordenes=null;
		try {
			ordenes = Conexion.getConexion(
					"SELECT ec.id_entrada_compra,\r\n        DATE_FORMAT(ec.fecha_hora, '%d-%m-%Y') as fecha,\r\n        (select count(*) from entradas_compras_detalle where id_entrada_compra=ec.id_entrada_compra) as items,\r\n        e.descripcion as estado,\r\n        e.id_entrada_compra_estado as id_estado,\r\n        CONCAT(emp.nombre,' ',emp.apellidos) nombre,\r\n        d.descripcion,\r\n        ec.id_usuario_recibio,\r\n        ec.id_usuario,ec.id_orden_compra,\r\n        pro.razon_social as proveedor,\r\n        (select CONCAT ( sum(cantidad*importe), ' ',m.moneda)  from entradas_compras_detalle, moneda m where id_entrada_compra=ec.id_entrada_compra and m.id=ec.id_moneda ) as total\r\n        FROM   entradas_compras_estados e,usuarios u,empleado emp,entradas_compras ec\r\n        left join proveedores  as pro on ec.id_proveedor=pro.id_proveedor\r\n        LEFT JOIN depositos as d on ec.id_deposito=d.id_deposito\r\n        WHERE  e.id_entrada_compra_estado = ec.estado and u.idusuario=ec.id_usuario AND emp.idempleado=u.idempleado and ec.fecha_baja is null order by id_entrada_compra desc");

			while (ordenes.next()) {
				FacturasComprasItems items = new FacturasComprasItems();
				items.setDescripcion(ordenes.getString("descripcion"));
				items.setEstado(ordenes.getString("estado"));
				items.setFecha(ordenes.getString("fecha"));
				items.setId(ordenes.getInt("id_entrada_compra"));
				items.setId_estado(ordenes.getInt("id_estado"));
				items.setId_orden_compra(ordenes.getInt("id_orden_compra"));
				items.setId_usuario_recibo(ordenes.getString("id_usuario_recibio"));
				items.setItems(ordenes.getInt("items"));
				items.setId_usuario(ordenes.getString("id_usuario"));
				items.setNombres(ordenes.getString("nombre"));
				items.setProveedor(ordenes.getString("proveedor"));
				items.setTotal(ordenes.getString("total"));
				lstItems.add(items);
			}
		} catch (SQLException var3) {
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, (String) null, var3);
		}finally {
			if(ordenes!=null)
				ordenes.close();
		}

		return lstItems;
	}

	public static List<OCAprobadas> listadoOCAprobadas() throws SQLException {
		List<OCAprobadas> lstItems = new ArrayList<OCAprobadas>();
		ResultSet ordenes=null;
		try {
			 ordenes = Conexion.getConexion(
					"SELECT om.id_orden_compra, CONCAT(e.nombre,' ',e.apellidos) nombre from usuarios u join ordenes_compras_modificaciones om\r\n          on(u.idusuario=om.id_usuario_autorizo) JOIN empleado e ON(e.idempleado=u.idempleado);");
           if(ordenes!=null){
			   while (ordenes.next()) {
				   OCAprobadas items = new OCAprobadas();
				   items.setId(ordenes.getInt("id_orden_compra"));
				   items.setNombre(ordenes.getString("nombre"));
				   lstItems.add(items);
			   }}else{
			   return lstItems;
		   }
		} catch (SQLException var3) {
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, (String) null, var3);
			return lstItems;
		}finally {
			if(ordenes!=null)
				ordenes.close();
		}

		return lstItems;
	}

	public static List<ItemsProducto> listadoItemsProductos(String search, int evento) throws SQLException {
		List<ItemsProducto> lstItems = new ArrayList<ItemsProducto>();
		ResultSet producto =null;
		try {
			 producto = Conexion.getConexion("SELECT * FROM producto WHERE codigo LIKE '%" + search
					+ "%' or nombre LIKE '%" + search + "%' and evento="+evento+" LIMIT 40");

			while (producto.next()) {
				ItemsProducto items = new ItemsProducto();
				items.setText(producto.getString("codigo"));
				items.setId(producto.getString("id"));
				items.setNombre(producto.getString("nombre"));
				items.setPrecio(producto.getDouble("precioventa") /*getPrecioProducto(items.getId())*/);
				items.setId_moneda(producto.getInt("moneda"));
				lstItems.add(items);
			}
		} catch (SQLException var4) {
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, (String) null, var4);
		}finally {
			if(producto!=null)
				producto.close();
		}

		return lstItems;
	}
	
	
	public static List<Titulos> buscarTitulos(String search) throws SQLException {
		List<Titulos> lstItems = new ArrayList<Titulos>();
		ResultSet titulos =null;
		try {
			titulos = Conexion.getConexion("SELECT * FROM empleado_titulos_profesionales WHERE text LIKE '%" + search
					+ "%'  LIMIT 40");

			while (titulos.next()) {
				Titulos items = new Titulos();
				items.setText(titulos.getString("text"));
				items.setId(titulos.getInt("id"));
				
				lstItems.add(items);
			}
		} catch (SQLException var4) {
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, (String) null, var4);
		}finally {
			if(titulos!=null)
				titulos.close();
		}

		return lstItems;
	}


	public static String buscarIdProductoPorId(String search) throws SQLException {
		String id = "";
		ResultSet producto =null;
		try {
			for ( producto = Conexion.getConexion("SELECT id FROM producto WHERE codigo='" + search + "' and  disponible=1"); producto
					.next(); id = producto.getString("id")) {
			}
		} catch (SQLException var3) {
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, (String) null, var3);
		}finally {
			if(producto!=null)
				producto.close();
		}

		return id;
	}

	public static int obtenerDisponibilidadStockPorProducto(String idProducto) throws SQLException {
		int cantidad = 0;
		ResultSet producto =null;
		try {
			for (producto = Conexion.getConexion(
					"select case when cantidad is NULL then 0 ELSE cantidad END cantidad from stock where id_producto='"
							+ idProducto + "'"); producto.next(); cantidad = producto.getInt("cantidad")) {
			}
		} catch (SQLException var3) {
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, (String) null, var3);
		}finally {
			if(producto!=null)
				producto.close();
		}

		return cantidad;
	}

	public static double getPrecioProducto(String id_producto) throws SQLException {
		double precio = 0.0D;
		ResultSet valor =null;
		try {
			for ( valor = Conexion.getConexion(
					"SELECT precio FROM lista_precio_producto WHERE id_producto='" + id_producto + "'"); valor
						 .next(); precio = valor.getDouble("precio")) {
			}
		} catch (SQLException var4) {
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, (String) null, var4);
		}finally {
			if(valor!=null)
				valor.close();
		}

		return precio;
	}

	public static List<ItemsProveedore> listadoItemsProveedores(String search) throws SQLException {
		List<ItemsProveedore> lstItems = new ArrayList<ItemsProveedore>();
		ResultSet proveedor =null;
		try {
			 proveedor = Conexion.getConexion("SELECT * FROM proveedores WHERE razon_social LIKE '%" + search
					+ "%' and fecha_baja is null LIMIT 40");
			if(proveedor!=null){
				while (proveedor.next()) {
					ItemsProveedore items = new ItemsProveedore();
					items.setText(proveedor.getString("razon_social"));
					items.setId(proveedor.getString("id_proveedor"));
					items.setDireccion(proveedor.getString("direccion"));
					items.setEmail(proveedor.getString("email"));
					items.setTelefono(proveedor.getString("telefono"));
					lstItems.add(items);
				}
			}else{
				return lstItems;
			}

		} catch (SQLException var4) {
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, (String) null, var4);
		}finally {
			if(proveedor!=null)
				proveedor.close();
		}

		return lstItems;
	}
	
	
	public static List<ItemsProveedore> listadoItemsClientes(String search) throws SQLException {
		List<ItemsProveedore> lstItems = new ArrayList<ItemsProveedore>();
		ResultSet proveedor =null;
		try {
			 proveedor = Conexion.getConexion("SELECT * FROM clientes WHERE nombres LIKE '%" + search
					+ "%' and fecha_baja is null and id_tipo_cliente in(3,5) LIMIT 40");
			if(proveedor!=null){
				while (proveedor.next()) {
					ItemsProveedore items = new ItemsProveedore();
					items.setText(proveedor.getString("nombres"));
					items.setId(proveedor.getString("id_cliente"));
					items.setDireccion(proveedor.getString("direccion"));
					items.setEmail(proveedor.getString("email"));
					items.setTelefono(proveedor.getString("telefono"));
					lstItems.add(items);
				}
			}else{
				return lstItems;
			}

		} catch (SQLException var4) {
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, (String) null, var4);
		}finally {
			if(proveedor!=null)
				proveedor.close();
		}

		return lstItems;
	}

	public static ResponseItemsVentas buscarVentasTiempoReal(int idVenta) throws SQLException {
		ResponseItemsVentas response = new ResponseItemsVentas();
		List<com.ayalait.modelo.ItemsVenta> lstItems = new ArrayList<com.ayalait.modelo.ItemsVenta>();
		boolean encontre = false;
		ResultSet venta = null;
		try {
			for ( venta = Conexion.getConexion(
					"select p.idiva,p.id, p.codigo, sum(d.cantidad) as cantidad,precio_venta(p.id) precio, p.nombre, (select m.simbolo from moneda m where m.defecto=1 ) moneda,d.id_venta_detalle, v.id_cliente from ventas_detalle d, ventas v, producto p where v.id_venta="
							+ idVenta
							+ " and v.id_venta=d.id_venta and d.id_producto =p.id and cantidad <>0 group by p.id "); venta
						 .next(); encontre = true) {
				com.ayalait.modelo.ItemsVenta items = new com.ayalait.modelo.ItemsVenta();
				items.setIdiva(venta.getInt("idiva"));
				items.setId(venta.getString("id"));
				items.setCodigo(venta.getString("codigo"));
				items.setCantidad(venta.getInt("cantidad"));
				items.setPrecio(venta.getDouble("precio"));
				items.setNombre(venta.getString("nombre"));
				items.setMoneda(venta.getString("moneda"));
				items.setId_venta_detalle(venta.getInt("id_venta_detalle"));
				items.setId_cliente(venta.getInt("id_cliente"));
				lstItems.add(items);
			}

			String json = (new Gson()).toJson(lstItems);
			List<com.ayalait.modelo.ItemsVenta> data =   new Gson().fromJson(json, List.class);
			if (encontre) {
				response.setCode(200);
				response.setStatus(true);
				response.setItemsVentas(lstItems);
			} else {
				response.setCode(400);
				response.setItemsVentas(lstItems);
				response.setResultado("No se encontro venta.");
			}
		} catch (SQLException var7) {
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, (String) null, var7);
		}finally {
			if(venta!=null)
				venta.close();
		}

		return response;
	}

	public static List<ItemsAperturaCajero> buscasAperturaCajeroVentas(String idUsuario,boolean accion, int idVenta) throws SQLException {
		List<ItemsAperturaCajero> lstItems = new ArrayList<ItemsAperturaCajero>();
		ResultSet venta=null;
		try {
			
			String consulta="";
			if(accion) {
				consulta=" select ac.id_apertura_cajero,  v.nro_consecutivo  consecutivo, v.id_venta,c.nombres,c.id_cliente  from ventas v, aperturas_terminal ac, clientes c where  ac.id_apertura_cajero=v.id_apertura_cajero and v.id_cliente=c.id_cliente and v.id_usuario='"
						+ idUsuario + "' and v.fecha_hora_cerrado is null";
			}else {
				consulta=" select ac.id_apertura_cajero,  v.nro_consecutivo  consecutivo, v.id_venta,c.nombres,c.id_cliente  from ventas v, aperturas_terminal ac, clientes c where  ac.id_apertura_cajero=v.id_apertura_cajero and v.id_cliente=c.id_cliente  and v.id_venta="+idVenta+"";
			}
			
			 venta = Conexion.getConexion(consulta);

			while (venta.next()) {
				ItemsAperturaCajero items = new ItemsAperturaCajero();
				items.setConsecutivo(venta.getString("consecutivo"));
				items.setId_apertura_cajero(venta.getInt("id_apertura_cajero"));
				items.setId_cliente(venta.getInt("id_cliente"));
				items.setId_venta(venta.getInt("id_venta"));
				items.setNombre(venta.getString("nombres"));
				lstItems.add(items);
			}
		} catch (SQLException var4) {
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, (String) null, var4);
		}finally {
			if(venta!=null)
				venta.close();
		}

		return lstItems;
	}

	public static int buscasAperturaCajero(String idUsuario) throws SQLException {
		int idAperturaCajero = 0;
		ResultSet apertura =null;
		try {
			for ( apertura = Conexion.getConexion(" select ac.id_apertura_cajero from  aperturas_terminal ac where  ac.id_usuario='"
					+ idUsuario + "' and ac.fecha_hora_cierre is null"); apertura
						 .next(); idAperturaCajero = apertura.getInt("id_apertura_cajero")) {
			}
		} catch (SQLException var3) {
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, (String) null, var3);
		}finally {
			if(apertura!=null)
				apertura.close();
		}

		return idAperturaCajero;
	}

	 public  static ResponseResultado eliminarProductoVenta(int idVenta, int idVentaDetalle, String idProducto) throws SQLException{
		ResponseResultado response = new ResponseResultado();
		ResultSet ventas =null;
		try {
			String consulta="select max(vd.id_venta_detalle) as id from ventas v, ventas_detalle vd where v.id_venta=vd.id_venta and estado= 1 and v.id_usuario='"+LoginController.session.getUser().getIdusuario()+"' and vd.id_venta= '"+idVenta+"' and vd.id_producto= '"+idProducto+"' ";
			 ventas = Conexion.getConexion(consulta);
			if(ventas!=null){
				while (ventas.next()){
					response=LoginController.conTerminal.eliminarProductoEnVentaDetalle(ventas.getInt("id"));
				}
			}else{
				response.setStatus(false);
				response.setCode(400);
				response.setResultado("No se encontro la venta.");
				return response;
			}
		}catch (Exception e){
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, (String) null, e.getMessage());

		}finally {
			if(ventas!=null)
				ventas.close();
		}
		return response;
		} 
	
	
	 public  static ResponseResultado eliminarProductoDevuelto(int idVenta,  String idProducto) throws SQLException{
		ResponseResultado response = new ResponseResultado();
		ResultSet ventas =null;
		try {
			String consulta="select max(vd.id_venta_detalle) as id from ventas v, ventas_detalle vd where v.id_venta=vd.id_venta and vd.id_venta= '"+idVenta+"' and vd.id_producto= '"+idProducto+"' ";
			 ventas = Conexion.getConexion(consulta);
			if(ventas!=null){
				while (ventas.next()){
					response=LoginController.conTerminal.eliminarProductoEnVentaDetalle(ventas.getInt("id"));
				}
			}else{
				response.setStatus(false);
				response.setCode(400);
				response.setResultado("No se encontro la venta.");
				return response;
			}
		}catch (Exception e){
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, (String) null, e.getMessage());

		}finally {
			if(ventas!=null)
				ventas.close();
		}
		return response;
		} 
	
		public static String obtenerConsecutivo() throws SQLException {
			String consecutivo = "";
			ResultSet conse=null;
			try {
				if (Conexion.getUpdate("UPDATE consecutivo_venta SET id=LAST_INSERT_ID(id+1);") > 0) {
					for ( conse = Conexion.getConexion("select * from consecutivo_venta"); conse
							.next(); consecutivo = conse.getString("id")) {
					}
				} else {
					Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, (String) null,
							"Error actualizando consecutivo de venta");
				}
			} catch (SQLException var2) {
				Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, (String) null, var2);
			}finally {
				if(conse!=null)
					conse.close();
			}

			return consecutivo;
		}

		
		public static String guardarIvaPrefactura(int idPrefactura, double iva, double total) throws SQLException {
			String consecutivo = "";
			ResultSet conse=null;
			try {
				if (Conexion.getUpdate(" INSERT INTO  prefactura_calculo_iva VALUE("+idPrefactura+","+iva+","+total+");") > 0) {
					for ( conse = Conexion.getConexion("select * from consecutivo_venta"); conse
							.next(); consecutivo = conse.getString("id")) {
					}
				} else {
					Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, (String) null,
							"Error insertando calculo del iva de la prefactura con id "+idPrefactura);
				}
			} catch (SQLException var2) {
				Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, (String) null, var2);
			}finally {
				if(conse!=null)
					conse.close();
			}

			return consecutivo;
		}
		
		public static boolean actualizarStockProducto(String idProducto, BigDecimal cantidad) throws SQLException {
			boolean resultado=false;
			double cantidadReal=0;
			ResultSet stock =null;
			try {

				for ( stock = Conexion.getConexion("select cantidad  from stock WHERE stock.id_producto='"+idProducto+"'");
					 stock.next();
					 cantidadReal = stock.getInt("cantidad")) {
				}
				if(cantidadReal>0) {
					double cant= cantidadReal-cantidad.intValue();
					if (Conexion.getUpdate("UPDATE stock SET stock.cantidad="+cant+" WHERE stock.id_producto='"+idProducto+"'") > 0) {
						resultado= true;
					} else {
						Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, (String) null,
								"Error actualizando stock de producto");
					}

				}


			} catch (SQLException var2) {
				Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, (String) null, var2);
			}finally {
				if(stock!=null)
					stock.close();
			}
			return resultado;

		}
		
		

		public static boolean actualizarStockProductoEliminadoVenta(String idProducto, BigDecimal cantidad) throws SQLException {
			boolean resultado=false;
			double cantidadReal=0;
			ResultSet stock =null;
			try {

				for ( stock = Conexion.getConexion("select cantidad  from stock WHERE stock.id_producto='"+idProducto+"'");
					 stock.next();
					 cantidadReal = stock.getInt("cantidad")) {
				}
				if(cantidadReal>0) {
					double cant= cantidadReal+cantidad.intValue();
					if (Conexion.getUpdate("UPDATE stock SET stock.cantidad="+cant+" WHERE stock.id_producto='"+idProducto+"'") > 0) {
						resultado= true;
					} else {
						Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, (String) null,
								"Error actualizando stock de producto");
					}

				}


			} catch (SQLException var2) {
				Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, (String) null, var2);
			}finally {
				if(stock!=null)
					stock.close();
			}
			return resultado;

		}

		public static boolean insertarControlExistencia(String idProducto, BigDecimal cantidad,int idVenta, String idUsuario, String fecha) throws ParseException, SQLException {
			boolean resultado=false;
			double cantidadReal=0;
			int costoUnitario=0;
			int costoResultante=0;
			int idSucursal=0;
			int idMoneda=0;
			int idUM=0;
			int idDeposito=0;
			double cantidadResult=0;
			ResultSet control =null;
			try {

				for ( control = Conexion.getConexion("SELECT * from control_existencias WHERE control_existencias.id_producto='"+idProducto+"'  \r\n"
						+ "order by control_existencias.id_control_existencia DESC LIMIT 1;");
					 control.next();
					 cantidadReal = control.getDouble("cantidad"),
							 cantidadResult=control.getDouble("cantidad_resultante"),
							 costoResultante=control.getInt("costo_resultante"),
							 costoUnitario=control.getInt("costo_unitario"),
							 idSucursal=control.getInt("id_sucursal"),
							 idMoneda=control.getInt("id_moneda"),
							 idUM=control.getInt("id_unidad_medida"),
							 idDeposito=control.getInt("id_deposito")) {
				}
				double cant= cantidadResult-cantidad.intValue();
				if (Conexion.getUpdate("INSERT INTO control_existencias(id_producto,transaccion,cantidad,costo_unitario,cantidad_resultante,costo_resultante,id_sucursal,id_moneda,id_usuario,id_factura,id_unidad_medida,id_deposito) "
						+ "VALUES ('"+idProducto+"', 2, "+cantidadReal+", "+costoUnitario+", "+cant+", "+costoResultante+", "
						+idSucursal+", "+idMoneda+", '"+idUsuario+"',"+idVenta+", "+idUM+", "+idDeposito+");") > 0) {
					resultado= true;
				} else {
					Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, (String) null,
							"Error actualizando stock de producto");

				}


			} catch (SQLException var2) {
				Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, (String) null, var2);
			}finally {
				if(control!=null)
					control.close();
			}
			return resultado;

		}

		public static boolean insertarPuntosClientes(int idCliente,int id_venta, int puntos) {
			boolean resultado = false;
			String fechaI = obtenerFechaPorFormato(FormatoFecha.YYYYMMDD.getFormato());
			if (Conexion.getUpdate("INSERT INTO clientes_puntos VALUES ( 0," + idCliente + ", " + id_venta + ", '" + fechaI + "','2023-12-2025', " + puntos + ");") > 0) {
				resultado = true;
			}
			return resultado;
		}


		public static void eliminarVentasNoEfectuadas(int idApertura) {
			Conexion.getUpdate("DELETE  from ventas WHERE id_apertura_cajero=" + idApertura + " AND estado=1");
		}

		public static boolean cambiarEstadoCaja(Caja caja) {
			String updateCaja = "update caja set estado=2 where idcaja=" + caja.getIdcaja();
			int cajas = Conexion.getUpdate(updateCaja);
			return cajas > 0;
		}

		public static boolean cambiarEstadoCaja(String ip, int estado) {
			String updateCaja = "update caja set estado="+estado+" where ip='"+ip+"'";
			int cajas = Conexion.getUpdate(updateCaja);
			return cajas > 0;
		}

		public static String ObtenerIPCaja() {
			String ip = "";

			try {
				ip = InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException var2) {
				Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, (String) null, var2);
			}

			return ip;
		}

		public static int getCajaDisponible() throws SQLException {
			int idCaja = 0;
			ResultSet conse =null;

			try {
				for (conse = Conexion.getConexion("select * from caja where  ip='" + ObtenerIPCaja() + "'"); conse
						.next(); idCaja = conse.getInt("idcaja")) {
				}
			} catch (SQLException var2) {
				Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, (String) null, var2);
			}finally {
				if(conse!=null)
					conse.close();
			}

			return idCaja;
		}
}