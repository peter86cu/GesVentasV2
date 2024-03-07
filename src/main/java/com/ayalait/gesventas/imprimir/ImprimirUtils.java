package com.ayalait.gesventas.imprimir;

import com.ayalait.gesventas.controller.LoginController;
 
import com.ayalait.gesventas.utils.Conexion;
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

    public static ResponsePrefacturamprimir imprimirPrefactura(int idPrefactura, int idCliente){
    	ResponsePrefacturamprimir response= new ResponsePrefacturamprimir();
        List<ItemOrden> lstItems= new ArrayList<ItemOrden>();
        Empresa empresa= new Empresa();
        Cliente cliente= new Cliente();
        try {
            String consulta="select p.codigo,p.nombre, oc.cantidad, oc.importe, o.fecha_hora FROM  prefactura_detalle oc , producto p, prefacturas o\r\n"
            		+ " where oc.id_producto=p.id AND o.id_prefactura=oc.id_prefactura AND  oc.id_prefactura="+idPrefactura;
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
                String consultaProveedor="select * from clientes where id_cliente="+idCliente;
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
            		+ "  on(u.idusuario=om.id_usuario_autorizo) JOIN empleado e ON e.idempleado=u.idempleado and om.id_prefactura="+idPrefactura+" LIMIT 1";
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
                "          &copy; <?php echo \"mipagina.com \"; echo  $anio=date('Y'); ?>\n" +
                "        </td>\n" +
                "      </tr>\n" +
                "    </table>\n" +
                "  </page_footer>\n" +
                "  <table cellspacing=\"0\" style=\"width: 100%;\">\n" +
                "    <tr>\n" +
                "      <td  style=\"width: 33%; color: #444444;\">\n" +
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
    
    
    
    
    public static String armarPDFPrefactura(ResponsePrefacturamprimir response){
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
                "          &copy; <?php echo \"mipagina.com \"; echo  $anio=date('Y'); ?>\n" +
                "        </td>\n" +
                "      </tr>\n" +
                "    </table>\n" +
                "  </page_footer>\n" +
                "  <table cellspacing=\"0\" style=\"width: 100%;\">\n" +
                "    <tr>\n" +
                "      <td  style=\"width: 33%; color: #444444;\">\n" +
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
                "      "+response.getIdPrefactura()+"</td>\n" +
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
}
