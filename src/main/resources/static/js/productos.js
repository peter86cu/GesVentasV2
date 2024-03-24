let tipoProductoTemp = 0;
let categoriaTemp = 0;
let marcaTemp = 0;

/*jQuery('#addProductos').on('shown.bs.modal',function() {
   var anchoCSS = 300,
	   anchoEfectivo = jQuery(this).find('.modal-content').outerWidth(),
	   margenIzquierdo=(400-anchoEfectivo)/2;
	   
   jQuery(this).find('.modal-content').css('margin-left',margenIzquierdo);
   
});

$('#addProductos').on('show.bs.modal', function () {
	   $(this).find('.modal-body').css({
			  width:'auto',
			  height:'auto',
			  'max-height':'70%',
			  'max-width':'50%'
	   });
});
*/
var banderaTamano;
const caracteristicas = new Map();
//var caracteristicas={};
function agregarDetalle() {

	//Obtengo los valores
	//var idProducto=  $(this).attr("idProducto");
	var key = $("#key").val();
	var value = $('#value').val();
	caracteristicas.set(key,value);


	var datos = $("#detalleProducto").val();
	if (datos == "") {
		$("#detalleProducto").val( key + ":" + value);

	} else {
		var nuevo = key + ":" + value;
		$("#detalleProducto").val(datos += '\n' + nuevo);
	}
	
	$("#key").val("");
	$('#value').val("");

}

function guardarDetalleProducto() {

   
	var datos = new FormData();

	var detalle = $("#detalleProducto").val();
	var caract = $('#caracteristica').val();
	
	
	const autoConvertMapToObject = (caracteristicas) => {
  const obj = {};
  for (const item of [...caracteristicas]) {
    const [
      key,
      value
    ] = item;
    obj[key] = value;
  }
  return obj;
}

const obj = autoConvertMapToObject(caracteristicas)

	var temp= JSON.stringify(obj);


	datos.append('detalle', detalle);
	datos.append('descripcion', caract);
    datos.append('mapa', temp);

	

	$.ajax({
		url: globalPath + "/detalle-producto",
		method: "POST",
		data: datos,
		chache: false,
		contentType: false,
		processData: false,
		dataType: "json",
		success: function(respuesta) {
			var response = JSON.stringify(respuesta, null, '\t');
			var data = JSON.parse(response);
			if (data.code == 200) {

			    cerrarModalDetalle('agregarDetalle');
				mensajeOKSinActPagina(data.resultado);
				


			} else {

				Swal.fire({
					icon: "error",
					text: data.resultado
				})
			}
		}
	});

}

$(document).ready(function() {

	//--------------------- SELECCIONAR FOTO PRODUCTO ---------------------
	$("#foto1").on("change", function() {

		var uploadFoto = document.getElementById("foto1").value;
		var foto = document.getElementById("foto1").files;
		var nav = window.URL || window.webkitURL;
		var contactAlert = document.getElementById('form_alert');
		if (uploadFoto != '') {
			var type = foto[0].type;
			var name = foto[0].name;
			var w = foto[0].width;
			var h = foto[0].height;


			// Si el tamaño de la imagen fue validado
			if (banderaTamano) {
				return true;
			}

			var img = new Image();
			img.onload = function dimension() {
				if (this.width.toFixed(0) != 150 && this.height.toFixed(0) != 150) {
					alert('Las medidas deben ser: 900 x 400');
				} else {
					alert('Imagen correcta :)');
					// El tamaño de la imagen fue validado
					banderaTamano = true;

					// Buscamos el formulario
					var form = document.getElementById('formulario');
					// Enviamos de nuevo el formulario con la bandera modificada.
					form.submit();
				}
			};
			if (type != 'image/jpeg' && type != 'image/jpg' && type != 'image/png') {
				contactAlert.innerHTML = '<p class="errorArchivo">El archivo no es válido.</p>';
				$("#img").remove();
				$(".delPhoto").addClass('notBlock');
				$('#foto1').val('');
				return false;
			} else {
				contactAlert.innerHTML = '';
				$("#img").remove();
				$(".delPhoto").removeClass('notBlock');
				var objeto_url = nav.createObjectURL(this.files[0]);
				$(".prevPhoto").append("<img id='img' src=" + objeto_url + ">");
				$(".upimg label").remove();

				var imagen = $('#foto1')[0].files[0];

				if (imagen == undefined && !banderaTamano) {
					Swal.fire({
						icon: "error",
						text: "Falta seleccionar imagen del producto"
					})
				} else {
					var datos = new FormData();

					datos.append('archivo', imagen);

					$.ajax({
						url: globalPath + "/impagenes-producto",
						method: "POST",
						data: datos,
						chache: false,
						contentType: false,
						processData: false,
						dataType: "json",
						success: function(respuesta) {
							var response = JSON.stringify(respuesta, null, '\t');
							var data = JSON.parse(response);
							if (data.code == 200) {


								mensajeOKSinActPagina(data.resultado);


							} else {

								Swal.fire({
									icon: "error",
									text: data.resultado
								})
							}
						}
					});


				}

			}
		} else {
			alert("No selecciono foto");
			$("#img").remove();
		}
	});

	$('.delPhoto').click(function() {
		$('#foto1').val('');
		$(".delPhoto").addClass('notBlock');
		$("#img").remove();

	});

});


function cerrarModalProducto(nombreModal) {
	$(".modal-body input").val("")
	$(".modal-body select").val("0")
	$('#tabla1 > tbody').empty();
	$('#tabla2 > tbody').empty();
	$('#tabla3 > tbody').empty();

	$('#idiva').val("0");
	$('#tipoproducto').val("0");
	$('#um').val("0");
	$('#inventariable').val("3");
	$('#disponible').val("3");
	document.getElementById('catDIV').style.display = 'none';
	document.getElementById('marcaDIV').style.display = 'none';
	document.getElementById('marcaDIV').style.display = 'none';
	document.getElementById('modeloDIV').style.display = 'none';


	$('#' + nombreModal).modal('hide');

}

function cerrarModalDetalle(cerrarModal) {
	$(".modal-body input").val("")
	$(".modal-body select").val("")
	$(".modal-body textarea").val("")
	$('#' + cerrarModal).modal('hide');

}

function abrirModalDetalle(nombreModal) {
	activarLoader();
	$('#' + nombreModal).modal({ backdrop: 'static', keyboard: false })
	$('#' + nombreModal).modal('show');
desactivarLoading();

}



function selectProductoCategoria(event) {
	var x = $('#tipoproducto').val();

	if (x == tipoProductoTemp) {
		tipoProductoTemp = x;
	} else {
		//var divCategoria = document.getElementById('catDIV');
		//divCategoria.remove();
		document.getElementById('marcaDIV').style.display = 'none';
		document.getElementById('catDIV').style.display = 'block';
		$(".categoria").empty();
		$(document).ready(function() {
			$("#addProductos").find("#categoria").val("");
			//$("#addProductos input[type='checkbox']").prop('checked', false).change();
			$(".categoria").select2({
				dropdownParent: $('#addProductos .modal-body'),
				theme: 'bootstrap-5',
				language: {
					inputTooShort: function() {
						return "Debe ingresar 2 caracteres";
					},
					minimumInputLength: function() {
						return "Debe ingresar 2 caracteres";
					},
					noResults: function() {
						tipoProductoTemp = 0;
						return "No hay resultado";
					},
					searching: function() {

						return "Buscando..";
					}
				},

				ajax: {
					url: globalPath + "/buscar-parametro-producto",
					method: "POST",
					dataType: 'json',
					delay: 250,
					data: function(params) {
						return {
							id: params.term,
							cod: x,
							queBusco: 'Categoria'
						};
					},
					processResults: function(data) {
						return {
							results: data
						};
					},
					cache: false
				},
				minimumInputLength: 2
			}).on('change', function(e) {
				$('#idCategoria').val($('.categoria').select2('data')[0].id);
				$("#categoria").select2('data', null);

				document.getElementById('marcaDIV').style.display = 'block';


			})
		});
		tipoProductoTemp = x;
	}

}


function selectProductoMarca(event) {
	var x = $('#tipoproducto').val();
	var y = $('#txtCategoria').val();

	if (x != tipoProductoTemp) {
		//var divCategoria = document.getElementById('catDIV');
		//divCategoria.remove();
		//document.getElementById('catDIV').style.display = 'block';
		$(".marca").empty();
		$(document).ready(function() {
			$("#addProductos").find(".marca").val("");
			//$("#addProductos input[type='checkbox']").prop('checked', false).change();
			$(".categoria").select2({
				dropdownParent: $('#addProductos .modal-body'),
				theme: 'bootstrap-5',
				language: {
					inputTooShort: function() {
						return "Debe ingresar 2 caracteres";
					},
					minimumInputLength: function() {
						return "Debe ingresar 2 caracteres";
					},
					noResults: function() {
						categoriaTemp = 0;
						return "No hay resultado";
					},
					searching: function() {

						return "Buscando..";
					}
				},

				ajax: {
					url: globalPath + "/buscar-parametro-producto",
					method: "POST",
					dataType: 'json',
					delay: 250,
					data: function(params) {
						return {
							id: params.term,
							cod: y,
							queBusco: 'Categoria'
						};
					},
					processResults: function(data) {
						if (data == null)
							tipoProductoTemp = 0;
						return {
							results: data
						};
					},
					cache: false
				},
				minimumInputLength: 2
			}).on('change', function(e) {
				$('#idMarcaa').val($('.marca').select2('data')[0].id);
				$("#marca").select2('data', null);
			})
		});

		categoriaTemp = y;
	} else if (y != categoriaTemp) { //Compara la categoria para habilitar las marcas
		document.getElementById('marcaDIV').style.display = 'block';

		$(".txtCategoria").empty();
		$(document).ready(function() {
			$("#addProductos").find("#marca").val("");
			$(".marca").empty();
			$(".marca").select2({
				dropdownParent: $('#addProductos .modal-body'),
				theme: 'bootstrap-5',
				language: {
					inputTooShort: function() {
						return "Debe ingresar 2 caracteres";
					},
					minimumInputLength: function() {
						return "Debe ingresar 2 caracteres";
					},
					noResults: function() {
						categoriaTemp = 0;
						return "No hay resultado";
					},
					searching: function() {

						return "Buscando..";
					}
				},

				ajax: {
					url: globalPath + "/buscar-parametro-producto",
					method: "POST",
					dataType: 'json',
					delay: 250,
					data: function(params) {
						return {
							id: params.term,
							cod: y,
							queBusco: 'Marcas'
						};
					},
					processResults: function(data) {
						return {
							results: data
						};
					},
					cache: false
				},
				minimumInputLength: 2
			}).on('change', function(e) {
				$('#idMarca').val($('.marca').select2('data')[0].id);
				$("#marca").select2('data', null);


			})
		});
		categoriaTemp = y;
	}

}


function selectProductoModelo(event) {
	var x = $('#tipoproducto').val();
	var y = $('#txtCategoria').val();
	var z = $('#txtMarca').val();

	if (x != tipoProductoTemp) {
		selectProductoCategoria(event);

	} else if (y != categoriaTemp) { //Compara la categoria para habilitar las marcas
		selectProductoModelo(event)

	} else if (z != marcaTemp) {
		document.getElementById('modeloDIV').style.display = 'block';

		$(".txtModelo").empty();
		$(document).ready(function() {
			//$("#addProductos").find("#marca").val("");
			$(".modelo").empty();
			$(".modelo").select2({
				dropdownParent: $('#addProductos .modal-body'),
				theme: 'bootstrap-5',
				language: {
					inputTooShort: function() {
						return "Debe ingresar 2 caracteres";
					},
					minimumInputLength: function() {
						return "Debe ingresar 2 caracteres";
					},
					noResults: function() {
						marcaTemp = 0;
						return "No hay resultado";
					},
					searching: function() {

						return "Buscando..";
					}
				},

				ajax: ({
					url: globalPath + "/buscar-parametro-producto",
					method: "POST",
					dataType: 'json',
					delay: 250,
					data: function(params) {
						return {
							id: params.term,
							cod: z,
							queBusco: 'Modelo'
						};
					},
					processResults: function(data) {
						return {
							results: data
						};
					},
					cache: false
				}),
				minimumInputLength: 2
			}).on('change', function(e) {
				$('#idModelo').val($('.modelo').select2('data')[0].id);
				$("#modelo").select2('data', null);

			})
		}).fail(function(jqXHR, textStatus, errorThrown) {

			var msg = '';
			if (jqXHR.status === 0) {
				msg = 'No connection.\n Verify Network.';
				//ERR_CONNECTION_REFUSED hits this one
			} else if (jqXHR.status == 404) {
				msg = 'Requested page not found. [404]';
			} else if (jqXHR.status == 500) {
				msg = 'Internal Server Error [500].';
			} else if (exception === 'parsererror') {
				msg = 'Requested JSON parse failed.';
			} else if (exception === 'timeout') {
				msg = 'Time out error.';
			} else if (exception === 'abort') {
				msg = 'Ajax request aborted.';
			} else {
				msg = 'Uncaught Error.\n' + jqXHR.responseText;
			}


			Swal.fire({

				icon: textStatus,
				title: msg,
				showConfirmButton: false,
				timer: 2100
			})
			setTimeout(function() { location.reload(); }, 2505)
		})
		marcaTemp = z;
	}

}


function validarChange(event, objeto) {
	/*$('#'+objeto).change(function(e) {
		$('#'+objeto).find("option:selected").each(function() {
			if ($(this).val().trim() =="0") {
				$('#'+objeto).addClass('is-invalid');
			}else{
				$("#"+objeto).removeClass("is-invalid");
			}
		})
	
		//debe ejecutarse el otro codigo si todos los campos estan completos;
	})	*/
	$(document).ready(function() {
		$(document).on('change', '#' + objeto, function() {
			if ($(this).val().trim() == "0") {
				$('#' + objeto).addClass('is-invalid');
			} else {
				$("#" + objeto).removeClass("is-invalid");
			}
		});
	})
}

function validarInput(event, objeto) {
	//var a= $("#"+objeto).text($(this).val());
	$(document).ready(function() {
		$("#" + objeto).on("input", function() {
			// Print entered value in a div box
			if ($("#" + objeto).val() != null) {
				$("#" + objeto).removeClass("is-invalid");
			} else {
				$('#' + objeto).addClass('is-invalid');
			}
		});
	});
}

function validarCampos() {
	var validar = true;

	if ($("#inputCodigo").val() == "") {
		$('#inputCodigo').addClass('is-invalid');
		validar = false;
	} if ($("#inputNombre").val() == "") {
		$('#inputNombre').addClass('is-invalid');
		validar = false;
	} if ($("#inputMinimo").val() == "0") {
		$('#inputMinimo').addClass('is-invalid');
		validar = false;
	} if ($("#inputPVenta").val() == "0.0") {
		$('#inputPVenta').addClass('is-invalid');
		validar = false;
	} if ($("#idiva").val() == null) {
		$('#idiva').addClass('is-invalid');
		validar = false;
	} if ($("#tipoproducto").val() == null) {
		$('#tipoproducto').addClass('is-invalid');
		validar = false;
	} if ($("#txtCategoria").val() == null) {
		$('#txtCategoria').addClass('is-invalid');
		validar = false;
	} if ($("#txtMarca").val() == null) {
		$('#txtMarca').addClass('is-invalid');
		validar = false;
	} if ($("#um").val() == null) {
		$('#um').addClass('is-invalid');
		validar = false;
	} if ($("#inventariable").val() == null) {
		$('#inventariable').addClass('is-invalid');
		validar = false;
	} if ($("#disponible").val() == null) {
		$('#disponible').addClass('is-invalid');
		validar = false;
	} if ($("#idMoneda").val() == null) {
		$('#idMoneda').addClass('is-invalid');
		validar = false;
	}


	return validar;

}

function agregarProductos(event) {

	if (!validarCampos()) {
		Swal.fire({
			icon: "warning",
			text: "Debe completar los campos obligatorios."
		})
	} else {
activarLoader();
		//Obtengo los valores
		//var idProducto=  $(this).attr("idProducto");
		var codigo = $('#inputCodigo').val();
		var nombre = $("#inputNombre").val();
		var minimo = $('#inputMinimo').val();
		var precioV = $('#inputPVenta').val();
		var iva = $('#idiva').val();
		var categoria = $('#txtCategoria').val();
		var marca = $('#txtMarca').val();
		var tipo_producto = $('#tipoproducto').val();
		var unidad_medida = $('#um').val();
		var inventario = $('#inventariable').val();
		var disponible = $('#disponible').val();
		var modelo = $('#idModelo').val();
		var moneda = $('#idMoneda').val();


		var datos = new FormData();

		var accion = "insert";

		/*var imagen = $('#foto1')[0].files[0];

		if (imagen == undefined) {
			Swal.fire({
				icon: "error",
				text: "Falta seleccionar imagen del producto"
			})
		}*/

		datos.append("accion", accion);
		datos.append("idProducto", 0);
		datos.append("codigo", codigo);
		datos.append("nombre", nombre);
		datos.append("minimo", minimo);
		datos.append("precioV", precioV);
		datos.append("iva", iva);
		datos.append("categoria", categoria);
		datos.append("marca", marca);
		datos.append("tipo_producto", tipo_producto);
		datos.append("unidad_medida", unidad_medida);
		datos.append("inventario", inventario);
		datos.append("disponible", disponible);
		datos.append("modelo", modelo);
		datos.append("moneda", moneda);
		//datos.append('foto', imagen);

		$.ajax({
			url: globalPath + "/addproducto",
			method: "POST",
			data: datos,
			chache: false,
			contentType: false,
			processData: false,
			dataType: "json",
			success: function(respuesta) {
				desactivarLoading();
				var response = JSON.stringify(respuesta, null, '\t');
				var data = JSON.parse(response);
				if (data.code == 200) {

					$('#ModalADDProductos').modal('hide');
					Swal.fire({
						position: "top-end",
						icon: "success",
						title: data.resultado,
						showConfirmButton: false,
						timer: 1500
					})

					setTimeout(function() { location.reload(); }, 1505)
				} else {

					Swal.fire({
						icon: "error",
						text: data.resultado
					})
				}
			}
		});


	}



}


function editarProducto(idProducto) {

	var datos = new FormData();

	datos.append("idProducto", idProducto,);

	$.ajax({
		url: globalPath + "/editar-producto",
		method: "POST",
		data: datos,
		chache: false,
		contentType: false,
		processData: false,
		dataType: "json",
		success: function(respuesta) {
			var response = JSON.stringify(respuesta, null, '\t');
			var data = JSON.parse(response);
			if (data.code == 200) {
				var codigo = $('#inputCodigo').val();
				var nombre = $("#inputNombre").val();
				var minimo = $('#inputMinimo').val();
				var precioV = $('#inputPVenta').val();
				var iva = $('#idiva').val();
				var categoria = $('#categoria').val();
				//var marca = $('#input_id_marca').val();
				var tipo_producto = $('#tipoproducto').val();
				var unidad_medida = $('#um').val();
				var inventario = $('#inventariable').val();
				var disponible = $('#disponible').val();

				$("#idProducto").val(data.producto.id);
				$("#inputCodigoEdit").val(data.producto.codigo);
				$("#inputNombreEdit").val(data.producto.nombre);
				$("#inputMinimoEdit").val(data.producto.cantidadminima);
				$("#inputPVentaEdit").val(data.producto.precioventa);
				$("#inputIVAVentaEdit > option[value=" + data.producto.idiva + "]").attr("selected", true);
				$("#inputCategoriaEdit > option[value=" + data.producto.categoria + "]").attr("selected", true);
				// $("#input_id_marcaEdit > option[value="+data.producto.marca"]+"]").attr("selected",true);
				$("#input_tipo_productoEdit > option[value=" + data.producto.tipoproducto + "]").attr("selected", true);
				$("#inputUnidadEdit > option[value=" + data.producto.um + "]").attr("selected", true);
				var inventariable;
				if (data.producto.inventariable) {
					inventariable = 1;
				} else {
					inventariable = 0;
				}
				$("#inputInveEdit > option[value=" + inventariable + "]").attr("selected", true);
				var disponible;
				if (data.producto.disponible) {
					disponible = 1;
				} else {
					disponible = 0;
				}

				$("#inputDispoEdit > option[value=" + disponible + "]").attr("selected", true);
				//$("#inputMonedaEdit > option[value="+respuesta["id_moneda"]+"]").attr("selected",true);
				//document.getElementById("foto").val("vistas/recursos/dist/img/productos/"+respuesta["foto"]);
				//$("#foto").val(data.producto.foto);
				var fotoUp = "productos/" + data.producto.foto;
				$("#up").val(fotoUp);
				//document.getElementById("foto").value="productos/"+data.producto.foto;
				//$('#addProductos').modal({backdrop: 'static', keyboard: false})
				$('#ModalEditarProductos').modal('show');

			} else {
				Swal.fire({
					icon: "error",
					text: data.resultado
				})
			}


		}


	})

}


function updateProducto(event) {


	var idProducto = $('#idProducto').val();;
	var codigo = $('#inputCodigoEdit').val();
	var nombre = $("#inputNombreEdit").val();
	var minimo = $('#inputMinimoEdit').val();
	var precioV = $('#inputPVentaEdit').val();
	var iva = $('#inputIVAVentaEdit').val();
	var categoria = $('#inputCategoriaEdit').val();
	// var marca = $('#input_id_marcaEdit').val();
	var tipo_producto = $('#input_tipo_productoEdit').val();
	var unidad_medida = $('#inputUnidadEdit').val();
	var inventario = $('#inputInveEdit').val();
	var disponible = $('#inputDispoEdit').val();
	//var moneda = $('#inputMonedaEdit').val();


	var datos = new FormData();

	var accion = "update";

	var imagen = $('#foto1')[0].files[0];

	datos.append("accion", accion);
	datos.append("idProducto", idProducto);
	datos.append("codigo", codigo);
	datos.append("nombre", nombre);
	datos.append("minimo", minimo);
	datos.append("precioV", precioV);
	datos.append("iva", iva);
	datos.append("categoria", categoria);
	//datos.append("marca",marca);
	datos.append("tipo_producto", tipo_producto);
	datos.append("unidad_medida", unidad_medida);
	datos.append("inventario", inventario);
	datos.append("disponible", disponible);
	//datos.append("moneda",moneda);
	datos.append('foto', imagen);



	$.ajax({
		url: globalPath + "/addproducto",
		method: "POST",
		data: datos,
		chache: false,
		contentType: false,
		processData: false,
		dataType: "json",
		success: function(respuesta) {
			var response = JSON.stringify(respuesta, null, '\t');
			var data = JSON.parse(response);
			if (data.code == 200) {
				$('#ModalEditarProductos').modal('hide');
				Swal.fire({
					position: "top-end",
					icon: "success",
					title: data.resultado,
					showConfirmButton: false,
					timer: 1500
				})

				setTimeout(function() { location.reload(); }, 1505)
			} else {

				Swal.fire({
					icon: "error",
					text: data.resultado
				})
			}
		}
	});

}


function eliminarProducto(idProducto, nombre) {

	var datos = new FormData();
	datos.append("idProducto", idProducto);

	const swalWithBootstrapButtons = Swal.mixin({
		customClass: {
			confirmButton: 'btn btn-success',
			cancelButton: 'btn btn-danger'
		},
		buttonsStyling: false
	})

	swalWithBootstrapButtons.fire({
		title: 'Estas seguro de eliminar el producto: ' + nombre,
		icon: 'error',
		showCancelButton: true,
		confirmButtonText: 'Si! ',
		cancelButtonText: 'No!',
		reverseButtons: true
	}).then((result) => {
		if (result.isConfirmed) {

			$.ajax({
				url: globalPath + "/eliminar-producto",
				method: "POST",
				data: datos,
				chache: false,
				contentType: false,
				processData: false,
				dataType: "json",
				success: function(respuesta) {
					var response = JSON.stringify(respuesta, null, '\t');
					var data = JSON.parse(response);
					if (data.code == 200) {
						swalWithBootstrapButtons.fire(
							data.resultado, '',

							'success'
						).then((result) => {
							if (result.isConfirmed)
								location.reload();
						});

					} else {
						Swal.fire({
							icon: 'error',
							text: data.resultado,

						})
					}

				}
			});

		} else if (
			/* Read more about handling dismissals below */
			result.dismiss === Swal.DismissReason.cancel
		) {
			swalWithBootstrapButtons.fire(
				'Acción cancelada', '',
				'warning'
			)
		}
	})



}



//CREACION DEL PAGINADO
$(document).ready(function() {
	$("#searchprodcutos").keyup(function() {
		_this = this;
		// Show only matching TR, hide rest of them
		$.each($("#productost tbody tr"), function() {
			if ($(this).text().toLowerCase().indexOf($(_this).val().toLowerCase()) === -1)
				$(this).hide();
			else
				$(this).show();
		});
	});
});




//Aqui va el paginado


$(document).ready(function() {

	addPagerToTables('#productost', 8);

});

function addPagerToTables(tables, rowsPerPage = 10) {

	tables =
		typeof tables == "string"
			? document.querySelectorAll(tables)
			: tables;

	for (let table of tables)
		addPagerToTable(table, rowsPerPage);

}

function addPagerToTable(table, rowsPerPage = 10) {

	let tBodyRows = table.querySelectorAll('tBody tr');
	let numPages = Math.ceil(tBodyRows.length / rowsPerPage);

	let colCount =
		[].slice.call(
			table.querySelector('tr').cells
		)
			.reduce((a, b) => a + parseInt(b.colSpan), 0);

	table
		.createTFoot()
		.insertRow()
		.innerHTML = `<td colspan=${colCount}><div class="nav"></div></td>`;

	if (numPages == 1)
		return;

	for (i = 0; i < numPages; i++) {

		let pageNum = i + 1;

		table.querySelector('.nav')
			.insertAdjacentHTML(
				'beforeend',
				`<a class="bot" href="#" rel="${i}"> ${pageNum}</a> `
			);

	}

	changeToPage(table, 1, rowsPerPage);

	for (let navA of table.querySelectorAll('.nav a'))
		navA.addEventListener(
			'click',
			e => changeToPage(
				table,
				parseInt(e.target.innerHTML),
				rowsPerPage
			)
		);

}

function changeToPage(table, page, rowsPerPage) {

	let startItem = (page - 1) * rowsPerPage;
	let endItem = startItem + rowsPerPage;
	let navAs = table.querySelectorAll('.nav a');
	let tBodyRows = table.querySelectorAll('tBody tr');

	for (let nix = 0; nix < navAs.length; nix++) {

		if (nix == page - 1)
			navAs[nix].classList.add('active');
		else
			navAs[nix].classList.remove('active');

		for (let trix = 0; trix < tBodyRows.length; trix++)
			tBodyRows[trix].style.display =
				(trix >= startItem && trix < endItem)
					? 'table-row'
					: 'none';

	}

}