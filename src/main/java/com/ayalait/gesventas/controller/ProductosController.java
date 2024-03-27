package com.ayalait.gesventas.controller;

import com.google.gson.Gson;

import com.ayalait.gesventas.request.*;
import com.ayalait.gesventas.service.wsParametros;
import com.ayalait.gesventas.service.wsStock;
import com.ayalait.gesventas.utils.*;
import com.ayalait.modelo.*;
import com.ayalait.response.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.*;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
public class ProductosController {
	@Autowired
	RestTemplate restTemplate;

	
	//private String rutaImagenProducto;
    private static ResponseTipoProducto responseTP= new ResponseTipoProducto();
    private static ResponseCategorias responseCat = new ResponseCategorias();
    private static ResponseListaMarcasProducto responseMarcas= new ResponseListaMarcasProducto();
    private static ResponseListaModeloProducto responseModelo= new ResponseListaModeloProducto();
    private static List<ProductoImagenes>  imageProd= new ArrayList<ProductoImagenes>();
    private static ProductoDetalles detalleProd= new ProductoDetalles();
	void cargarServer() throws IOException {
		Properties p = new Properties();
		
		try {
			URL url = this.getClass().getClassLoader().getResource("application.properties");
			if (url == null) {
				throw new IllegalArgumentException("application.properties" + " is not found 1");
			} else {
				InputStream propertiesStream = url.openStream();
				//InputStream propertiesStream = ClassLoader.getSystemResourceAsStream("application.properties");
				p.load(propertiesStream);
				propertiesStream.close();
				//this.rutaImagenProducto = p.getProperty("server.uploaderProductos");
			}
		} catch (FileNotFoundException var3) {
			Logger.getLogger(Session.class.getName()).log(Level.SEVERE, (String) null, var3);
		}

	}

	public ProductosController() {
		try {
			this.cargarServer();
			LoginController.conStock= new wsStock();
			LoginController.conParam= new wsParametros();
			ResponseResultado response= LoginController.conStock.validarConectividadServidor();
			if(response.isStatus()) {
				responseMarcas= LoginController.conParam.listadoMarcasProducto();
				responseModelo= LoginController.conParam.listadoModelosProducto();
			}
			
		} catch (IOException var2) {
			Logger.getLogger(Session.class.getName()).log(Level.SEVERE, (String) null, var2);
		}
	}

	@PostMapping({ LoginController.ruta+"/addproducto" })
	public void guardarProductos(@ModelAttribute("accion") String accion,
			@ModelAttribute("idProducto") String idProducto, @ModelAttribute("codigo") String codigo,
			@ModelAttribute("nombre") String nombre, @ModelAttribute("minimo") int minimo,
			@ModelAttribute("precioV") double precioV, @ModelAttribute("iva") int iva,
			@ModelAttribute("categoria") int categoria , @ModelAttribute("marca") int marca,
			@ModelAttribute("modelo") int modeloMarca, @ModelAttribute("tipo_producto") int tipo_producto,
			@ModelAttribute("unidad_medida") int unidad_medida, @ModelAttribute("inventario") boolean inventario,
			@ModelAttribute("disponible") boolean disponible,@ModelAttribute("moneda") int moneda, /*@RequestParam("foto") MultipartFile file,*/ Model modelo,
			RedirectAttributes attribute, HttpServletResponse responseHttp) throws IOException {
		if (LoginController.session.getToken() != null) {
			try {
				ResponseResultado response = new ResponseResultado();
				modelo.addAttribute("user", LoginController.session.getUser());
				Producto producto = new Producto();
				RequestAddProducto request = new RequestAddProducto();
				/*if (file.isEmpty()) {
					response.setCode(406);
					response.setResultado("Debe seleccionar una imagen del producto para poder continuar.");
					String json = (new Gson()).toJson(response);
					responseHttp.setContentType("application/json");
					responseHttp.setCharacterEncoding("UTF-8");
					responseHttp.getWriter().write(json);
				}*/
				if (accion.equalsIgnoreCase("insert")) {
					producto.setId(Utils.generarCodigo());
					request.setAccion("Add");
				} else {
					producto.setId(idProducto);
					request.setAccion("Update");
				}

				String fileName = codigo+"_" + producto.getId() + ".jpg";

				modelo.addAttribute("user", LoginController.session.getUser());
				producto.setFoto(imageProd.get(0).getNombre());
				producto.setCodigo(codigo);
				producto.setNombre(nombre);
				producto.setPrecioventa(precioV);
				producto.setCategoria(categoria);
				producto.setCantidadminima(minimo);
				producto.setDisponible(disponible);
				producto.setIdiva(iva);
				producto.setTipoproducto(tipo_producto);
				producto.setInventariable(inventario);
				producto.setUm(unidad_medida);
				producto.setMarca(marca);
				producto.setModelo(modeloMarca);
				producto.setImagen(imageProd.get(0).getImagen());
				producto.setMoneda(moneda);
				/*Path path = Paths.get(LoginController.rutaDowloadProducto + fileName, new String[0]);
				Files.copy(file.getInputStream(), path, new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });


				byte[] fileContent = Files.readAllBytes(path);
				String base64 = Base64.getEncoder().encodeToString(fileContent);
				 producto.setImagen(base64);*/
				request.setImagenes(imageProd);
				request.setDetalle(detalleProd);
				request.setProducto(producto);
				response = LoginController.conStock.addProducto(request);
				if (response.isStatus()) {
					imageProd= new ArrayList<ProductoImagenes>();
					detalleProd= new ProductoDetalles();
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

			} catch (IOException e) {
				e.printStackTrace();
				attribute.addFlashAttribute("error", e.getMessage());

			}
		}else {
			responseHttp.setContentType("application/json");
			responseHttp.setCharacterEncoding("UTF-8");
			responseHttp.getWriter().write("Session caducada");
		}

	}
	
	
	@PostMapping({ LoginController.ruta + "/impagenes-producto" })
	public void agregarimagenesProducto( @RequestParam("archivo") MultipartFile file,  Model modelo, HttpServletResponse responseHttp)
			throws IOException, ParseException, SQLException {
		if (LoginController.session.getToken() != null) {
			modelo.addAttribute("user", LoginController.session.getUser());

			String fileName = file.getOriginalFilename();
			ProductoImagenes img= new ProductoImagenes();
				
			Path path = Paths.get(LoginController.rutaDowloadProducto + fileName, new String[0]);
			Files.copy(file.getInputStream(), path, new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });

			byte[] fileContent = Files.readAllBytes(path);
			String base64 = Base64.getEncoder().encodeToString(fileContent);

			img.setId(Utils.generarCodigo());
			img.setEstado(1);
			img.setImagen(base64);
			img.setNombre(fileName);

			imageProd.add(img);

			ResponseResultado response = new ResponseResultado();
			response.setCode(200);
			response.setResultado("Agregado a la lista de imagenes del producto. Puede cargar más!!");
			String json = (new Gson()).toJson(response);
			responseHttp.setContentType("application/json");
			responseHttp.setCharacterEncoding("UTF-8");
			responseHttp.getWriter().write(json);

		}

	}
	
	@PostMapping({ LoginController.ruta + "/detalle-producto" })
	public void agregarDetalleProducto( @RequestParam("detalle") String detalle,@RequestParam("descripcion") String descripcion,
			@RequestParam("mapa") String  myMap,Model modelo, HttpServletResponse responseHttp)
			throws IOException, ParseException, SQLException {
		if (LoginController.session.getToken() != null) {
			modelo.addAttribute("user", LoginController.session.getUser());


			detalleProd.setId(Utils.generarCodigo());
			detalleProd.setEstado(1);
			detalleProd.setDescripcion(descripcion);
			detalleProd.setMap(myMap);

			

			ResponseResultado response = new ResponseResultado();
			response.setCode(200);
			response.setResultado("Datos guardados correstamente.");
			String json = (new Gson()).toJson(response);
			responseHttp.setContentType("application/json");
			responseHttp.setCharacterEncoding("UTF-8");
			responseHttp.getWriter().write(json);

		}

	}

	@GetMapping({ "/productos" })
	public String productos(Model modelo) throws SQLException, IOException {
		if (LoginController.session.getToken() != null) {
			ResponseListaProductos response = LoginController.conStock.consultarListaProductos();
			if (response.isStatus()) {
				modelo.addAttribute("user", LoginController.session.getUser());
				List<Producto> lstProductos=  response.getProductos();
				if(!lstProductos.isEmpty()) {
					for(Producto producto: lstProductos) {
						File file = new File(LoginController.rutaDowloadProducto +producto.getFoto());
						if(!file.exists()) {
							byte trans[] = Base64.getDecoder().decode(producto.getImagen());
							Files.write(Paths.get(LoginController.rutaDowloadProducto +producto.getFoto()), trans);
						}
						
						
					}
				}
				
				
				modelo.addAttribute("listaProductos",  response.getProductos() );

				 responseCat = LoginController.conParam.listarCategorias();
				if (responseCat.isStatus()) {
					modelo.addAttribute("categorias",  responseCat.getCategorias() );
				}
				 responseTP = LoginController.conParam.listarTiposProductos();
				if (responseTP.isStatus()) {					
					modelo.addAttribute("listaTipoProd",  responseTP.getTipoProductos() );
				}
				ResponseUM responseUM = LoginController.conParam.listarUM();
				if (responseUM.isStatus()) {
					modelo.addAttribute("listaUM",  responseUM.getUm());
				}
				ResponseImpuestos responseIVA = LoginController.conParam.listarImpuestos();
				if (responseIVA.isStatus()) {
					modelo.addAttribute("listaImpuestos",  responseIVA.getImpuestos() );
				}
				Producto producto = new Producto();
				modelo.addAttribute("producto", producto);

				List<DatosStock> stock=Utils.obtenerStock();
				if(!stock.isEmpty()){
					modelo.addAttribute("lstStock", stock);
				}else{
					modelo.addAttribute("lstStock", new ArrayList<DatosStock>());
				}
				ResponseMonedas responseM = LoginController.conParam.listarMonedas();
				String fecha = "";
				if (response.isStatus()) {
					modelo.addAttribute("listaMoneda",  responseM.getMonedas() );

					HistoricoCambio cambio = new HistoricoCambio();
					modelo.addAttribute("fechaapertura", fecha);
					modelo.addAttribute("cambio", cambio);


				}else {
					modelo.addAttribute("listaMoneda", new ArrayList<Moneda>());
				}


				return "productos";
			}
		}
		return "redirect:/";
	}

	// Metodo para eliminar producto por id
	@PostMapping({ LoginController.ruta+"/eliminar-producto" })
	public void eliminarProducto(@ModelAttribute("idProducto") String idProducto, Model modelo,
			HttpServletResponse responseHttp) throws IOException, ParseException, SQLException {
		if (LoginController.session.getToken() != null) {
			ResponseResultado response = new ResponseResultado();
			int cantidad = Utils.obtenerDisponibilidadStockPorProducto(idProducto);
			if (cantidad > 0) {
				response.setStatus(false);
				response.setCode(406);
				response.setResultado(
						"El producto tiene " + cantidad + " en existencia en stock. No puede ser eliminado");
				String json = new Gson().toJson(response);
				responseHttp.setContentType("application/json");
				responseHttp.setCharacterEncoding("UTF-8");
				responseHttp.getWriter().write(json);
			} else {
				modelo.addAttribute("user", LoginController.session.getUser());
				response = LoginController.conStock.eliminarProducto(idProducto);
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

	
	
	// Metodo para buscar parametros por id
	@PostMapping({ LoginController.ruta+"/buscar-parametro-producto" })
	public void buscarParametroProducto(@ModelAttribute("id") String busqueda,@ModelAttribute("cod") int codigo, @ModelAttribute("queBusco") String queBusco, Model modelo,
			HttpServletResponse responseHttp) throws IOException, ParseException, SQLException {
		if (LoginController.session.getToken() != null) {
			    Iterator tipo=null;
				List<UsuariosReceptores> lstEncontrados= new ArrayList<UsuariosReceptores>();
				
				if("Categoria".equalsIgnoreCase(queBusco)) {
					List<Categoria> temp=  responseCat.getCategorias();
					tipo = temp.iterator();
					if(busqueda.toUpperCase().equalsIgnoreCase("Todo")) {
						for(Categoria cat : temp) {
							if(cat.getId_tipo_producto()==codigo) {
								UsuariosReceptores receptor = new UsuariosReceptores();
								receptor.setId(String.valueOf(cat.getId_categoria_producto()) );
								receptor.setText(cat.getCategoria());
								lstEncontrados.add(receptor);
							}
							
						}
						if(lstEncontrados.isEmpty()) {
							UsuariosReceptores receptor = new UsuariosReceptores();
							receptor.setId("1");
							receptor.setText("No tiene");
							lstEncontrados.add(receptor);
						}
						String json = (new Gson()).toJson(lstEncontrados);
						responseHttp.setContentType("application/json");
						responseHttp.setCharacterEncoding("UTF-8");
						responseHttp.getWriter().write(json);
						return;
					}else {
						while (true) {
							Categoria categoria;
							do {if (!tipo.hasNext()) {
								if(lstEncontrados.isEmpty()) {
									UsuariosReceptores receptor = new UsuariosReceptores();
									receptor.setId("1");
									receptor.setText("No tiene");
									lstEncontrados.add(receptor);
								}
								String json = (new Gson()).toJson(lstEncontrados);
								responseHttp.setContentType("application/json");
								responseHttp.setCharacterEncoding("UTF-8");
								responseHttp.getWriter().write(json);
								return;
							}

								categoria = (Categoria) tipo.next();
							} while ( categoria.getId_tipo_producto()!=codigo);
							if(categoria.getCategoria().toUpperCase().contains(busqueda.toUpperCase())) {
								UsuariosReceptores receptor = new UsuariosReceptores();
								receptor.setId(String.valueOf(categoria.getId_categoria_producto()) );
								receptor.setText(categoria.getCategoria());
								lstEncontrados.add(receptor);
							}
						}	
					}
				
				}else if("Marcas".equalsIgnoreCase(queBusco)) {
					
					List<MarcaProducto> temp=  responseMarcas.getMarcas();
					tipo= Arrays.asList(temp).iterator();
					tipo = temp.iterator();
					if(busqueda.toUpperCase().equalsIgnoreCase("Todo")) {
						for(MarcaProducto cat : temp) {
							if(cat.getId_categoria()==codigo) {
								UsuariosReceptores receptor = new UsuariosReceptores();
								receptor.setId(String.valueOf(cat.getId_marca()) );
								receptor.setText(cat.getMarca());
								lstEncontrados.add(receptor);
							}
							
							
						}
						if(lstEncontrados.isEmpty()) {
							UsuariosReceptores receptor = new UsuariosReceptores();
							receptor.setId("1");
							receptor.setText("No tiene");
							lstEncontrados.add(receptor);
						}
						
						String json = (new Gson()).toJson(lstEncontrados);
						responseHttp.setContentType("application/json");
						responseHttp.setCharacterEncoding("UTF-8");
						responseHttp.getWriter().write(json);
						return;
					
					}else {
						while (true) {
							MarcaProducto marca;
							do {if (!tipo.hasNext()) {
								if(lstEncontrados.isEmpty()) {
									UsuariosReceptores receptor = new UsuariosReceptores();
									receptor.setId("1");
									receptor.setText("No tiene");
									lstEncontrados.add(receptor);
								}
								String json = (new Gson()).toJson(lstEncontrados);
								responseHttp.setContentType("application/json");
								responseHttp.setCharacterEncoding("UTF-8");
								responseHttp.getWriter().write(json);
								return;
							}

								marca = (MarcaProducto) tipo.next();
							} while ( marca.getId_categoria()!=codigo);
							if(marca.getMarca().toUpperCase().contains(busqueda.toUpperCase())) {
								UsuariosReceptores receptor = new UsuariosReceptores();
								receptor.setId(String.valueOf(marca.getId_marca()) );
								receptor.setText(marca.getMarca());
								lstEncontrados.add(receptor);
							}
						}	
					}
					
					
					
				}else if("Modelo".equalsIgnoreCase(queBusco)) {
					List<ModeloProducto> temp=  responseModelo.getModelo();
					tipo= Arrays.asList(responseModelo.getModelo()).iterator();
					tipo = temp.iterator();
					if(busqueda.toUpperCase().equalsIgnoreCase("Todo")) {
						for(ModeloProducto cat : temp) {
							
							if(cat.getId_marca()==codigo) {
								UsuariosReceptores receptor = new UsuariosReceptores();
								receptor.setId(String.valueOf(cat.getId_modelo()) );
								receptor.setText(cat.getModelo());
								lstEncontrados.add(receptor);
							}
							
						}
						if(lstEncontrados.isEmpty()) {
							UsuariosReceptores receptor = new UsuariosReceptores();
							receptor.setId("1");
							receptor.setText("No tiene");
							lstEncontrados.add(receptor);
						}
						String json = (new Gson()).toJson(lstEncontrados);
						responseHttp.setContentType("application/json");
						responseHttp.setCharacterEncoding("UTF-8");
						responseHttp.getWriter().write(json);
						return;
					
					}else {
						while (true) {
							ModeloProducto modeloP;
							do {if (!tipo.hasNext()) {
								if(lstEncontrados.isEmpty()) {
									UsuariosReceptores receptor = new UsuariosReceptores();
									receptor.setId("1");
									receptor.setText("No tiene");
									lstEncontrados.add(receptor);
								}
								String json = (new Gson()).toJson(lstEncontrados);
								responseHttp.setContentType("application/json");
								responseHttp.setCharacterEncoding("UTF-8");
								responseHttp.getWriter().write(json);
								return;
							}

							modeloP = (ModeloProducto) tipo.next();
							} while ( modeloP.getId_marca()!=codigo);
							if(modeloP.getModelo().toUpperCase().contains(busqueda.toUpperCase())) {
								UsuariosReceptores receptor = new UsuariosReceptores();
								receptor.setId(String.valueOf(modeloP.getId_modelo()) );
								receptor.setText(modeloP.getModelo());
								lstEncontrados.add(receptor);
							}
						}
					}
					
				}
				
		
			} else {
				responseHttp.setContentType("application/json");
				responseHttp.setCharacterEncoding("UTF-8");
				responseHttp.getWriter().write("LoginController.session caducada");
			}

		
	}
//Metodo para buscar producto por codigo para realizar la venta
	@PostMapping({LoginController.ruta+ "/buscar-producto" })
	public void buscarProductoVenta(@ModelAttribute("accion") String accion, @ModelAttribute("codigo") String busqueda,
			Model modelo, HttpServletResponse responseHttp) throws IOException, ParseException, SQLException {
		if (LoginController.session.getToken() != null) {
			ResponseResultado response = new ResponseResultado();
			modelo.addAttribute("user", LoginController.session.getUser());
			String id = Utils.buscarIdProductoPorId(busqueda);
			if (!id.equalsIgnoreCase("")) {
				int cantidad = Utils.obtenerDisponibilidadStockPorProducto(id);
				if (cantidad > 0) {
					ResponseProducto responseP = LoginController.conStock.obtenerProductoPorId(id);
					String json = new Gson().toJson(responseP);
					responseHttp.setContentType("application/json");
					responseHttp.setCharacterEncoding("UTF-8");
					responseHttp.getWriter().write(json);
				} else {
					response.setCode(500);
					response.setResultado("No hay stock disponible");
					String json = new Gson().toJson(response);
					responseHttp.setContentType("application/json");
					responseHttp.setCharacterEncoding("UTF-8");
					responseHttp.getWriter().write(json);
				}
			} else {
				response.setCode(500);
				response.setResultado("No existe el producto.");
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

	// Metodo para buscar producto por codigo para editarlo
	@PostMapping({LoginController.ruta+ "/editar-producto" })
	public void buscarProductoEditar(@ModelAttribute("idProducto") String busqueda, Model modelo,
			HttpServletResponse responseHttp) throws IOException, ParseException {
		if (LoginController.session.getToken() != null) {
			modelo.addAttribute("user", LoginController.session.getUser());
			ResponseProducto response = LoginController.conStock.obtenerProductoPorId(busqueda);
			if (response.isStatus()) {

				String json = new Gson().toJson(response);
				responseHttp.setContentType("application/json");
				responseHttp.setCharacterEncoding("UTF-8");
				responseHttp.getWriter().write(json);

			} else {
				response.setCode(500);
				response.setResultado("No hay stock disponible");
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
}
