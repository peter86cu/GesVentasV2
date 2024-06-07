package com.ayalait.gesventas.imprimir;

import com.ayalait.gesventas.controller.LoginController;
 
import com.ayalait.gesventas.utils.Conexion;
import com.ayalait.gesventas.utils.Utils;
import com.ayalait.modelo.Cliente;
import com.ayalait.modelo.Configuraciones;
import com.ayalait.modelo.Empresa;
import com.ayalait.modelo.Proveedor;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImprimirUtils {

    private static Conexion conexion = new Conexion();

    public static ResponseOrdenImprimir imprimirOrdenCompra(int idOrden, int idProveedor){
        ResponseOrdenImprimir response= new ResponseOrdenImprimir();
        List<ItemOrden> lstItems= new ArrayList<ItemOrden>();
        Empresa empresa= new Empresa();
        Proveedor proveedor= new Proveedor();
        try {
            String consulta="select p.codigo,p.nombre, oc.cantidad, oc.importe, o.fecha_hora FROM  ordenes_de_compras_detalle oc , producto p, ordenes_de_compras o\n" +
                    " where oc.id_producto=p.id AND o.id_orden_compra=oc.id_orden_compra AND  oc.id_orden_compra="+idOrden;
            ResultSet orden = conexion.getConexion(consulta);
            if(orden!=null){
                while (orden.next()){
                    ItemOrden item= new ItemOrden();
                    item.setCantidad(orden.getInt("cantidad"));
                    item.setCodigo(orden.getString("codigo"));
                    item.setImporte(orden.getDouble("importe"));
                    item.setNombre(orden.getString("nombre"));
                    item.setTotal(item.getCantidad()*item.getImporte());
                    response.setFecha(orden.getString("fecha_hora"));
                    lstItems.add(item);
                }
                String consultaEmpresa="select * from empresa where id_empresa="+ LoginController.session.getId_empresa();
                ResultSet  rEmpresa= conexion.getConexion(consultaEmpresa);
                if(rEmpresa!=null){
                    while (rEmpresa.next()){
                        empresa.setDireccion(rEmpresa.getString("direccion"));
                        empresa.setEmail(rEmpresa.getString("email"));
                        empresa.setRut(rEmpresa.getString("rut"));
                        empresa.setNombre(rEmpresa.getString("nombre"));
                        empresa.setTelefono(rEmpresa.getString("telefono"));
                    }
                }
                String consultaProveedor="select * from proveedores where id_proveedor="+idProveedor;
                ResultSet  rProveedor= conexion.getConexion(consultaProveedor);
                if(rProveedor!=null){
                    while (rProveedor.next()){
                        proveedor.setDireccion(rProveedor.getString("direccion"));
                        proveedor.setEmail(rProveedor.getString("email"));
                        proveedor.setTelefono(rProveedor.getString("telefono"));
                        proveedor.setRazon_social(rProveedor.getString("razon_social"));
                        proveedor.setRuc(rProveedor.getString("ruc"));
                        proveedor.setWeb(rProveedor.getString("web"));
                    }
                }

            }

            String aprobado=" SELECT CONCAT(e.nombre,' ',e.apellidos) AS nombre from usuarios u inner join ordenes_compras_modificaciones om\n" +
                    "          on(u.idusuario=om.id_usuario_autorizo) JOIN empleado e ON e.idempleado=u.idempleado and om.id_orden_compra="+idOrden+" LIMIT 1";
            ResultSet  rAprobado= conexion.getConexion(aprobado);
            if(rAprobado!=null){
                while (rAprobado.next()){
                    response.setAprobadoPor(rAprobado.getString("nombre"));
                }
            }

            if(empresa!=null && proveedor!=null && !lstItems.isEmpty()){
                response.setCode(200);
                response.setStatus(true);
                response.setEmpresa(empresa);
                response.setProveedor(proveedor);
                double total=0;
                for(ItemOrden item: lstItems){
                    total+=item.getTotal();
                }
                response.setIdOrden(idOrden);
                response.setTotal(total);
                response.setLstItems(lstItems);
                return response;
            }else{
                response.setCode(400);
                response.setStatus(false);
                response.setResultado("Error consultando los datos, consulte con un administrador.");
                return response;
            }


        }catch (Exception e){
            response.setCode(400);
            response.setStatus(false);
            response.setResultado(e.getMessage());
            return response;
        }
    }

    public static ResponsePrefacturamprimir imprimirPrefactura(String idPrefactura, String idCliente){
    	ResponsePrefacturamprimir response= new ResponsePrefacturamprimir();
        List<ItemOrden> lstItems= new ArrayList<ItemOrden>();
        Empresa empresa= new Empresa();
        Cliente cliente= new Cliente();
        try {
            String consulta="select p.codigo,p.nombre, oc.cantidad, oc.importe, o.fecha_hora, m.simbolo, m.moneda, u.simbolo as um FROM  prefactura_detalle oc , producto p, prefacturas o, moneda m, unidades_medidas u\r\n"
            		+ " where oc.id_producto=p.id AND o.id_prefactura=oc.id_prefactura AND m.id=oc.id_moneda AND p.um=u.id_unidad_medida  and oc.id_prefactura='"+idPrefactura+"'";
            ResultSet orden = Conexion.getConexion(consulta);
            if(orden!=null){
                while (orden.next()){
                    ItemOrden item= new ItemOrden();
                    item.setCantidad(orden.getInt("cantidad"));
                    item.setCodigo(orden.getString("codigo"));
                    item.setImporte(orden.getDouble("importe"));
                    item.setNombre(orden.getString("nombre"));
                    item.setSimboloMoneda(orden.getString("simbolo"));
                    item.setMoneda(orden.getString("moneda"));
                    item.setUm(orden.getString("um"));
                    item.setTotal(item.getCantidad()*item.getImporte());
                    response.setFecha(orden.getString("fecha_hora"));
                    lstItems.add(item);
                }
                String consultaEmpresa="select * from empresa where id_empresa="+ LoginController.session.getId_empresa();
                ResultSet  rEmpresa= Conexion.getConexion(consultaEmpresa);
                if(rEmpresa!=null){
                    while (rEmpresa.next()){
                        empresa.setDireccion(rEmpresa.getString("direccion"));
                        empresa.setEmail(rEmpresa.getString("email"));
                        empresa.setRut(rEmpresa.getString("rut"));
                        empresa.setNombre(rEmpresa.getString("nombre"));
                        empresa.setTelefono(rEmpresa.getString("telefono"));
                    }
                }
                String consultaProveedor="select * from clientes where id_cliente='"+idCliente+"'";
                ResultSet  rProveedor= conexion.getConexion(consultaProveedor);
                if(rProveedor!=null){
                    while (rProveedor.next()){
                    	cliente.setDireccion(rProveedor.getString("direccion"));
                    	cliente.setEmail(rProveedor.getString("email"));
                    	cliente.setTelefono(Integer.parseInt( rProveedor.getString("telefono")));
                    	cliente.setNombres(rProveedor.getString("nombres"));
                    	cliente.setNroDocumento(rProveedor.getString("nro_documento"));
                        //proveedor.setWeb(rProveedor.getString("web"));
                    }
                }

            }

            String aprobado=" SELECT CONCAT(e.nombre,' ',e.apellidos) AS nombre from usuarios u inner join prefacturas_modificaciones om\r\n"
            		+ "  on(u.idusuario=om.id_usuario_autorizo) JOIN empleado e ON e.idempleado=u.idempleado and om.id_prefactura='"+idPrefactura+"' LIMIT 1";
            ResultSet  rAprobado= conexion.getConexion(aprobado);
            if(rAprobado!=null){
                while (rAprobado.next()){
                    response.setAprobadoPor(rAprobado.getString("nombre"));
                }
            }

            if(empresa!=null && cliente!=null && !lstItems.isEmpty()){
                response.setCode(200);
                response.setStatus(true);
                response.setEmpresa(empresa);
                response.setCliente(cliente);
                double total=0;
                for(ItemOrden item: lstItems){
                    total+=item.getTotal();
                }
                response.setIdPrefactura(idPrefactura);
                response.setTotal(total);
                response.setLstItems(lstItems);
                return response;
            }else{
                response.setCode(400);
                response.setStatus(false);
                response.setResultado("Error consultando los datos, consulte con un administrador.");
                return response;
            }


        }catch (Exception e){
            response.setCode(400);
            response.setStatus(false);
            response.setResultado(e.getMessage());
            return response;
        }
    }



    public static String armarPDFOrdenCompra(ResponseOrdenImprimir response){
        List<Configuraciones> confi =  LoginController.conParam.cargarConfiguraciones().getConfiguraciones() ;

        String css="<style type=\"text/css\">\n" +
                "\n" +
                "table { vertical-align: top; }\n" +
                "tr    { vertical-align: top; }\n" +
                "td    { vertical-align: top; }\n" +
                "\n" +
                ".text-center{\n" +
                "\ttext-align:center\n" +
                "}\n" +
                ".text-right{\n" +
                "\ttext-align:right\n" +
                "}\n" +
                "table th, td{\n" +
                "\tfont-size:13px\n" +
                "}\n" +
                ".detalle td{\n" +
                "\tborder:solid 1px #bdc3c7;\n" +
                "\tpadding:5px;\n" +
                "}\n" +
                ".items{\n" +
                "\tborder:solid 1px #bdc3c7;\n" +
                "\n" +
                "}\n" +
                ".items td, th{\n" +
                "\tpadding:10px;\n" +
                "}\n" +
                ".items th{\n" +
                "\tbackground-color: #c0392b;\n" +
                "\tcolor:white;\n" +
                "\n" +
                "}\n" +
                ".border-bottom{\n" +
                "\tborder-bottom: solic 1px #bdc3c7;\n" +
                "}\n" +
                "table.page_footer {width: 100%; border: none; background-color: white; padding: 2mm;border-collapse:collapse; border: none;}\n" +
                "} \n" +
                "</style>";

        String pdf1="<html lang=\"en\" xmlns:th=\"http://www.thymeleaf.org\">" +
                "<table class=\"page_footer\">\n" +
                "      <tr>\n" +
                "        <td style=\"width: 50%; text-align: right\">\n" +
                
                "        </td>\n" +
                "      </tr>\n" +
                "    </table>\n" +                
                "  <table cellspacing=\"0\" style=\"width: 100%;\">\n" +
                "    <tr>\n" +
                "      <td  style=\"width: 33%; color: #444444;\">\n" +
                "      </td>\n" +
                "      <td style=\"width: 34%;\">\n" +
                "        <strong>E-mail : </strong>"+response.getEmpresa().getEmail()+"<br>\n" +
                "        <strong>Teléfono : </strong> "+response.getEmpresa().getTelefono()+"<br>\n" +
                "        <strong>Sitio web : </strong> <label>\"ayalait.com.uy\"</label><br>\n" +
                "      </td>\n" +
                "      <td style=\"width: 33%;\">\n" +
                "        <strong>"+response.getEmpresa().getNombre()+" </strong> <br>\n" +
                "        <strong>Dirección : </strong> "+response.getEmpresa().getDireccion()+"<br>\n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </table>\n" +
                "  <br>\n" +
                "  <hr style=\"display: block;height: 1px;border: 1.5px solid #c0392b;    margin: 0.5em 0;    padding: 0;\">\n" +
                "  <table cellspacing=\"0\" style=\"width: 100%;\">\n" +
                "    <tr>\n" +
                "      <td  style=\"width: 10%; \">\n" +
                "      </td>\n" +
                "      <td style=\"width: 80%;text-align:center;font-size:22px;color:#c0392b;padding:10px; border-radius: 5px; \">\n" +
                "        ORDEN DE COMPRA\n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </table>\n" +
                "  <br>\n" +
                "  <table cellspacing=\"0\" style=\"width: 100%;\">\n" +
                "    <tr>\n" +
                "      <td  style=\"width: 60%; \">\n" +
                "      </td>\n" +
                "      <td  style=\"width: 20%;color:white;background-color:#c0392b;padding:5px;text-align:center \">\n" +
                "        <strong style=\"font-size:14px;\" >ORDEN #</strong>\n" +
                "      </td>\n" +
                "      <td  style=\"width: 20%; color:white;background-color:#c0392b;padding:5px;text-align:center \" >\n" +
                "        <strong style=\"font-size:14px;\">FECHA</strong>\n" +
                "      </td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td  style=\"width: 60%; \">\n" +
                "      </td>\n" +
                "      <td  style=\"width: 20%;padding:5px;text-align:center;border:solid 1px #bdc3c7;font-size:15px\">\n" +
                "      "+response.getIdOrden()+"</td>\n" +
                "      <td  style=\"width: 20%;padding:5px;text-align:center;border:solid 1px #bdc3c7;font-size:15px \" >\n" +
                "        "+response.getFecha()+"</td>\n" +
                "    </tr>\n" +
                "  </table>";

        String pdf2=" <br>\n" +
                "  <table cellspacing=\"0\" style=\"width: 100%;\" class=\"detalle\">\n" +
                "    <tr>\n" +
                "      <td  style=\"width: 50%; \">\n" +
                "        <strong style=\"font-size:18px;color:#2c3e50\">Proveedor</strong>\n" +
                "      </td>\n" +
                "      <td  style=\"width: 50%; \">\n" +
                "        <strong style=\"font-size:18px;color:#2c3e50\">Enviar a</strong>\n" +
                "      </td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td  style=\"width: 50%; \">\n" +
                "        <strong>Nombre: </strong>"+response.getProveedor().getRazon_social()+"<br>\n" +
                "        <strong>Dirección: </strong>"+response.getProveedor().getDireccion()+"<br>\n" +
                "        <strong>E-mail: </strong>"+response.getProveedor().getEmail()+"<br>\n" +
                "        <strong>Teléfono: </strong>"+response.getProveedor().getTelefono()+"</td>\n" +
                "      <td  style=\"width: 50%; \">\n" +
                "        <strong>RUT: </strong> "+response.getEmpresa().getRut()+"<br>\n" +
                "        <strong>Empresa: </strong> "+response.getEmpresa().getNombre()+"<br>\n" +
                "        <strong>Dirección: </strong> "+response.getEmpresa().getDireccion()+"<br>\n" +
                "        <strong>E-mail: </strong> "+response.getEmpresa().getEmail()+"<br>\n" +
                "        <strong>Teléfono: </strong> "+response.getEmpresa().getTelefono()+"</label></td>\n" +
                "\t  </tr>\n" +
                "  </table>\n" +
                "  <br>\n" +
                "  <table cellspacing=\"0\" style=\"width: 100%;\" class=\"detalle\">\n" +
                "    <tr>\n" +
                "      <td  style=\"width: 50%; \">\n" +
                "        <strong style=\"font-size:16px;color:#2c3e50\">Condiciones de pago</strong>\n" +
                "      </td>\n" +
                "      <td  style=\"width: 50%; \">\n" +
                "        <strong style=\"font-size:16px;color:#2c3e50\">Método de envío</strong>\n" +
                "      </td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td  style=\"width: 50%; \">\n" +
                "        <?php echo $condiciones;?>\n" +
                "      </td>\n" +
                "      <td  style=\"width: 50%; \">\n" +
                "        <?php echo $envio;?>\n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </table>\n" +
                "  <br>";
        String table="  <tr>\n";
        String pdf3="<table cellspacing=\"0\" style=\"width: 100%;\" class='items'>\n" +
                "    <tr>\n" +
                "      <th style=\"text-align:center;width:10%\">Cantidad</th>\n" +
                "      <th style=\"text-align:center;width:10%\">Código</th>\n" +
                "      <th style=\"text-align:left;width:40%\">Descripción</th>\n" +
                "      <th style=\"text-align:right;width:20%\">Ultimo Precio</th>\n" +
                "      <th style=\"text-align:right;width:20%\">Total</th>\n" +
                "    </tr>\n" ;
        for(int i=0; i<response.getLstItems().size();i++) {
            table = table +
                    "      <td class=\"border-bottom text-center\" >"+response.getLstItems().get(i).getCantidad()+"</td>\n" +
                    "      <td class=\"border-bottom text-center\">"+response.getLstItems().get(i).getCodigo()+"</td>\n" +
                    "      <td class=\"border-bottom\">"+response.getLstItems().get(i).getNombre()+"</td>\n" +
                    "      <td class=\"border-bottom text-right\">"+response.getLstItems().get(i).getImporte()+"</td>\n" +
                    "      <td class='border-bottom text-right' \" >"+response.getLstItems().get(i).getTotal()+"</td></tr>" ;


        }
        //table=table+" </tr>\n" ;
        String fin=	"  <tr > <td colspan=4 class='text-right' style=\"font-size:24px;color: #c0392b\">TOTAL  </td>\n" +
                "      <td class='text-right' style=\"font-size:24px;color:#c0392b\">"+response.getTotal()+" </td>\n" +
                "    </tr>\n" +
                "  </table>";

        String fin1="  <br>\n" +
                "  <p>\n" +
                "    Autorizado por : <label>"+response.getAprobadoPor()+"</label> <br>\n" +
                "  </p>\n" +
                "  <br><br>\n" +
                "  <p class='text-center'>Si tiene alguna consulta relacionada con esta orden de compra, por favor contáctenos : <br><label>"+confi.get(2).getValor()+"</label>, <label>"+response.getEmpresa().getTelefono()+"</label>, <label>"+confi.get(1).getValor()+"</label> </p>\n" +
                "</html>";

        return css+pdf1+pdf2+pdf3+table+fin+fin1;
    }
    
    
    
    
    public static String armarPDFPrefactura(ResponsePrefacturamprimir response, String codFac){
        List<Configuraciones> confi =  LoginController.conParam.cargarConfiguraciones().getConfiguraciones() ;

        String css="<style type=\"text/css\">\n" +
                "\n" +
                "table { vertical-align: top; }\n" +
                "tr    { vertical-align: top; }\n" +
                "td    { vertical-align: top; }\n" +
                "\n" +
                ".text-center{\n" +
                "\ttext-align:center\n" +
                "}\n" +
                ".text-right{\n" +
                "\ttext-align:right\n" +
                "}\n" +
                "table th, td{\n" +
                "\tfont-size:13px\n" +
                "}\n" +
                ".detalle td{\n" +
                "\tborder:solid 1px #6A05C6;\n" +
                "\tpadding:5px;\n" +
                "}\n" +
                ".items{\n" +
                "\tborder:solid 1px detalle;\n" +
                "\n" +
                "}\n" +
                ".items td, th{\n" +
                "\tpadding:10px;\n" +
                "}\n" +
                ".items th{\n" +
                "\tbackground-color: #aa00cb;\n" +
                "\tcolor:white;\n" +
                "\n" +
                "}\n" +
                ".border-bottom{\n" +
                "\tborder-bottom: solic 1px #6A05C6\n" +
                "}\n" +
                "table.page_footer {width: 100%; border: none; background-color: white; padding: 2mm;border-collapse:collapse; border: none;}\n" +
                "} \n" +
                "</style>";

        String pdf1="<html lang=\"en\" xmlns:th=\"http://www.thymeleaf.org\">" +
                "<table class=\"page_footer\">\n" +
                "      <tr>\n" +
                "        <td style=\"width: 50%; text-align: right\">\n" +
                "          &copy;  	 \n" +
                "        </td>\n" +
                "      </tr>\n" +
                "    </table>\n" +
                "  <table cellspacing=\"0\" style=\"width: 100%;\">\n" +
                "    <tr>\n" +
                "      <td  style=\"width: 33%; color: #444444;\"><img src=\"data:image/png;base64,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\" width=\"150\">\n" +
                "      </td>\n" +
                "      <td style=\"width: 34%;\">\n" +
                "        <strong>E-mail : </strong>"+response.getEmpresa().getEmail()+"<br>\n" +
                "        <strong>Teléfono : </strong> "+response.getEmpresa().getTelefono()+"<br>\n" +
                "      <!--  <strong>Sitio web : </strong> <label th:text=\"${orden.empresa.web}\"></label><br> -->\n" +
                "      </td>\n" +
                "      <td style=\"width: 33%;\">\n" +
                "        <strong>"+response.getEmpresa().getNombre()+" </strong> <br>\n" +
                "        <strong>Dirección : </strong> "+response.getEmpresa().getDireccion()+"<br>\n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </table>\n" +
                "  <br>\n" +
                "  <hr style=\"display: block;height: 1px;border: 1.5px solid #c0392b;    margin: 0.5em 0;    padding: 0;\">\n" +
                "  <table cellspacing=\"0\" style=\"width: 100%;\">\n" +
                "    <tr>\n" +
                "      <td  style=\"width: 10%; \">\n" +
                "      </td>\n" +
                "      <td style=\"width: 80%;text-align:center;font-size:22px;color:#c0392b;padding:10px; border-radius: 5px; \">\n" +
                "        PREFACTURA DE VENTA\n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </table>\n" +
                "  <br>\n" +
                "  <table cellspacing=\"0\" style=\"width: 100%;\">\n" +
                "    <tr>\n" +
                "      <td  style=\"width: 60%; \">\n" +
                "      </td>\n" +
                "      <td  style=\"width: 20%;color:white;background-color:#aa00cb;padding:5px;text-align:center \">\n" +
                "        <strong style=\"font-size:14px;\" >No. PREFACTURA</strong>\n" +
                "      </td>\n" +
                "      <td  style=\"width: 20%; color:white;background-color:#aa00cb;padding:5px;text-align:center \" >\n" +
                "        <strong style=\"font-size:14px;\">FECHA</strong>\n" +
                "      </td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td  style=\"width: 60%; \">\n" +
                "      </td>\n" +
                "      <td  style=\"width: 20%;padding:5px;text-align:center;border:solid 1px #bdc3c7;font-size:15px\">\n" +
                "      "+codFac+"</td>\n" +
                "      <td  style=\"width: 20%;padding:5px;text-align:center;border:solid 1px #bdc3c7;font-size:15px \" >\n" +
                "        "+response.getFecha()+"</td>\n" +
                "    </tr>\n" +
                "  </table>";
        String moneda="";

        if(!response.getLstItems().isEmpty()) {
       	 moneda=response.getLstItems().get(0).getMoneda(); 
        }
        String pdf2=" <br>\n" +
                "  <table cellspacing=\"0\" style=\"width: 100%;\" class=\"detalle\">\n" +
                "    <tr>\n" +
                "      <td  style=\"width: 50%; \">\n" +
                "        <strong style=\"font-size:18px;color:#2c3e50\">Cliente</strong>\n" +
                "      </td>\n" +
                "      <td  style=\"width: 50%; \">\n" +
                "        <strong style=\"font-size:18px;color:#2c3e50\">Enviado por</strong>\n" +
                "      </td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td  style=\"width: 50%; \">\n" +
                "        <strong>Nombre: </strong>"+response.getCliente().getNombres()+"<br>\n" +
                "        <strong>Dirección: </strong>"+response.getCliente().getDireccion()+"<br>\n" +
                "        <strong>E-mail: </strong>"+response.getCliente().getEmail()+"<br>\n" +
                "        <strong>Teléfono: </strong>"+response.getCliente().getTelefono()+"</td>\n" +
                "      <td  style=\"width: 50%; \">\n" +
                "        <strong>RUT: </strong> "+response.getEmpresa().getRut()+"<br>\n" +
                "        <strong>Empresa: </strong> "+response.getEmpresa().getNombre()+"<br>\n" +
                "        <strong>Dirección: </strong> "+response.getEmpresa().getDireccion()+"<br>\n" +
                "        <strong>E-mail: </strong> "+response.getEmpresa().getEmail()+"<br>\n" +
                "        <strong>Teléfono: </strong> "+response.getEmpresa().getTelefono()+"</label></td>\n" +
                "\t  </tr>\n" +
                "  </table>\n" +
                "  <br>\n" +
                "  <table cellspacing=\"0\" style=\"width: 100%;\" class=\"detalle\">\n" +
                "    <tr>\n" +
                "      <td  style=\"width: 50%; \">\n" +
                "        <strong style=\"font-size:16px;color:#2c3e50\">Moneda de pago</strong>\n" +
                "      </td>\n" +
                "      <td  style=\"width: 50%; \">\n" +
                "        <strong style=\"font-size:16px;color:#2c3e50\">Método de envío</strong>\n" +
                "      </td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td  style=\"width: 50%; \">\n" +
                "        "+moneda +
                "      </td>\n" +
                "      <td  style=\"width: 50%; \">\n" +
                "        <?php echo $envio;?>\n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </table>\n" +
                "  <br>";
        String table="  <tr>\n";
        String pdf3="<table cellspacing=\"0\" style=\"width: 100%;\" class='items'>\n" +
                "    <tr>\n" +
                "      <th style=\"text-align:center;width:10%\">Cantidad</th>\n" +
                "      <th style=\"text-align:center;width:10%\">Código</th>\n" +
                "      <th style=\"text-align:left;width:35%\">Descripción</th>\n" +
                "      <th style=\"text-align:right;width:10%\">Costo</th>\n" +
                "      <th style=\"text-align:right;width:5%\">U/M</th>\n" +
                "      <th style=\"text-align:right;width:10%\">Total</th>\n" +
                "    </tr>\n" ;
        String simboloMoneda="";
        for(int i=0; i<response.getLstItems().size();i++) {
            table = table +
                    "      <td class=\"border-bottom text-center\" >"+response.getLstItems().get(i).getCantidad()+"</td>\n" +
                    "      <td class=\"border-bottom text-center\">"+response.getLstItems().get(i).getCodigo()+"</td>\n" +
                    "      <td class=\"border-bottom\">"+response.getLstItems().get(i).getNombre()+"</td>\n" +
                    "      <td class=\"border-bottom text-right\">"+response.getLstItems().get(i).getImporte()+"</td>\n" +
                    "      <td class=\"border-bottom text-right\">"+response.getLstItems().get(i).getUm()+"</td>\n" +
                    "      <td class='border-bottom text-right' \" >"+response.getLstItems().get(i).getTotal()+"</td></tr>" ;
            	simboloMoneda=response.getLstItems().get(i).getSimboloMoneda();

        }
        //table=table+" </tr>\n" ;
        double ivaCalculo= Utils.obtenerIvaAFactura(response.getLstItems(),response.getIdPrefactura());
        double total= ivaCalculo+response.getTotal();
        
        String subTotal=" <tr > <td colspan=4 class='text-right' style=\"font-size:20px;color: #c0392b\">SUBTOTAL </td>\n" +
        		"      <td class='text-right' style=\"font-size:18px;color:#c0392b;width:5%\">"+ simboloMoneda+" </td>\n" +
                "      <td class='text-right' style=\"font-size:20px;color:#c0392b\">"+ response.getTotal()+" </td>\n" +
                "    </tr>\n";
        String iva=	"  <tr > <td colspan=4 class='text-right' style=\"font-size:20px;color: #c0392b\">IVA </td>\n" +
        		"      <td class='text-right' style=\"font-size:18px;color:#c0392b;width:5%\">"+ simboloMoneda+" </td>\n" +
                "      <td class='text-right' style=\"font-size:20px;color:#c0392b\">"+ ivaCalculo+" </td>\n" +
                "    </tr>\n";
        
        
        String fin=	"  <tr > <td colspan=4 class='text-right' style=\"font-size:24px;color: #c0392b\">TOTAL </td>\n" +
        		"      <td class='text-right' style=\"font-size:18px;color:#c0392b;width:5%\">"+ simboloMoneda+" </td>\n" +
                "      <td class='text-right' style=\"font-size:20px;color:#c0392b\">"+ total+" </td>\n" +
                "    </tr>\n" +
                "  </table>";

        String fin1="  <br>\n" +
                "  <p>\n" +
                "    Autorizado por : <label>"+response.getAprobadoPor()+"</label> <br>\n" +
                "  </p>\n" +
                "  <br><br>\n" +
                "  <p class='text-center'>Si tiene alguna consulta relacionada con esta prefactura, por favor contáctenos a: <br><label>"+response.getEmpresa().getNombre()+"</label>, <label>"+response.getEmpresa().getTelefono()+"</label>, <label>"+response.getEmpresa().getEmail()+"</label> </p>\n" +
                "</html>";

        return css+pdf1+pdf2+pdf3+table+subTotal+iva+fin+fin1;
    }
}
