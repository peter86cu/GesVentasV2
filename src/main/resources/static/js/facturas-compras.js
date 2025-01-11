function agregarFacturaInicial(event) {

	var datos = new FormData();
	var accion = "inicial";

	datos.append("accion", accion);
	datos.append("idCompra", 0);
	datos.append("idProveedor", 0);
	datos.append("idOrden", 0);
	datos.append("plazo", 0);
	datos.append("forma_pago", 0);
	datos.append("fecha", "");
	datos.append("factura", "");
	datos.append("receptor", 0);
	datos.append("deposito", 0);
	datos.append("moneda", 0)
	datos.append("estado", 0);


	idCompra

	$.ajax({
		url: globalPath + "/crear-factura-compra",
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

				$("#idCompra").val(data.resultado);
				//$("#txtNumeroFactura").val(data.temporal);
				document.querySelector('#idFactura').innerText = data.resultado;
				$('#ModalADDFacturas').modal('show');
				mostrar_itemsFactura(data.resultado, "nada");
			} else {
				Swal.fire({
					icon: "error",
					text: data.resultado,
				})
			}
		}
	});


}

function addFacturaCompra(idCompra) {

	var datos = new FormData();
	var accion = "buscar";
	datos.append("accion", accion);
	datos.append("idCompra", idCompra);


	$.ajax({
		url: globalPath + "/editar-factura-compra",
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

				document.querySelector('#idFactura').innerText = data.facturaCompra.id_entrada_compra;
				$("#txtFormaPagoF > option[value=" + data.facturaCompra.id_forma_pago + "]").attr("selected", true);
				$("#txtEnvioF > option[value=" + data.facturaCompra.id_plazo + "]").attr("selected", true);
				$("#txtDeposito > option[value=" + data.facturaCompra.id_deposito + "]").attr("selected", true);
				$("#txtMonedaF > option[value=" + data.facturaCompra.id_moneda + "]").attr("selected", true);
				$("#txtReceptor > option[value=" + data.facturaCompra.id_usuario + "]").attr("selected", true);
				$("#idCompra").val(data.facturaCompra.id_entrada_compra);

				$("#proveedor2").val(data.facturaCompra.id_proveedor);
				$("#datepickerF").val(data.facturaCompra.fecha_hora);
				$("#txtOrdenCompra").val(data.facturaCompra.id_orden_compra);
				$("#txtNumeroFactura").val(data.facturaCompra.nro_factura);
				buscar_proveedor_factura(data.facturaCompra.id_proveedor, "2");
				mostrar_itemsFactura(data.facturaCompra.id_entrada_compra, "nada");
				$('#ModalADDFacturas').modal('show');

			}
		}


	});

}


function editarFacturaCompra(idCompra) {

	var datos = new FormData();
	var accion = "buscar";
	datos.append("accion", accion);
	datos.append("idCompra", idCompra);

	$.ajax({
		url: globalPath + "/editar-factura-compra",
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
				if (data.facturaCompra.id_orden_compra == 1) {
					$("#txtOrdenCompraED > option[value=1]").attr("selected", true);
				} else {
					$("#txtOrdenCompraED > option[value=" + data.facturaCompra.id_orden_compra + "]").attr("selected", true);

				}
				document.querySelector('#idCompraE').innerText = data.facturaCompra.id_entrada_compra;
				//document.querySelector('#datepicker').innerText = respuesta["fecha_hora"];
				$("#txtFormaPagoFE > option[value=" + data.facturaCompra.id_forma_pago + "]").attr("selected", true);
				$("#txtEnvioFE > option[value=" + data.facturaCompra.id_plazo + "]").attr("selected", true);
				$("#txtDepositoE > option[value=" + data.facturaCompra.id_deposito + "]").attr("selected", true);
				$("#txtMonedaFE > option[value=" + data.facturaCompra.id_moneda + "]").attr("selected", true);
				if (data.facturaCompra.estado != 2)
					$("#txtReceptorE > option[value=" + data.facturaCompra.id_usuario_recibio + "]").attr("selected", true);
				//$("#txtOrdenCompraE > option[value=" + data.facturaCompra.id_orden_compra + "]").attr("selected", true);

				buscar_proveedor_factura(data.facturaCompra.id_proveedor, "1");
				mostrar_itemsFactura(data.facturaCompra.id_entrada_compra, "editar");
				$("#txtOrdenCompraE").val(data.facturaCompra.id_orden_compra);
				$("#txtNumeroFacturaE").val(data.facturaCompra.nro_factura);
				$("#idCompra1E").val(data.facturaCompra.id_entrada_compra);
				$("#proveedorCE").val(data.facturaCompra.id_proveedor);
				$("#datepickerCE").val(data.facturaCompra.fecha_hora);

				if (data.facturaCompra.estado == 3) {
					$('#autorizar').attr("disabled", true);
					$('#cancelar').attr("disabled", true);
				} else {
					$('#autorizar').attr("disabled", false);
					$('#cancelar').attr("disabled", false);
				}

				$('#ModalEditarCompras').modal('show');

			}
		}


	});


}



function buscar_proveedor_factura(id, paso) {

	var datos = new FormData();

	var accion = "buscarProveedor";
	datos.append("accion", accion);
	datos.append("idProveedor", id,);

	$.ajax({
		url: globalPath + "/buscar-proveedor",
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

				if (paso == "1") {
					document.querySelector('#proveedorNE').innerText = data.proveedor.razon_social;
					document.querySelector('#direccionE').innerText = data.proveedor.direccion;
					document.querySelector('#emailE').innerText = data.proveedor.email;
					document.querySelector('#telefonoE').innerText = data.proveedor.telefono;
					document.querySelector('#autorizo').innerText = data.proveedor.razon_social;
					document.querySelector('#cancelo').innerText = data.proveedor.razon_social;
				} if (paso == "2") {
					document.querySelector('#proveedorNF').innerText = data.proveedor.razon_social;
					document.querySelector('#direccionF').innerText = data.proveedor.direccion;
					document.querySelector('#emailF').innerText = data.proveedor.email;
					document.querySelector('#telefonoF').innerText = data.proveedor.telefono;

				}
			} else {
				Swal.fire({
					icon: "error",
					text: data.result,
				})
			}
		}


	})
}


function mostrar_itemsFactura(id, accion) {

	var datos = new FormData();

	datos.append("accion", accion);
	datos.append("idOrden", id);
	datos.append("id", "");
	datos.append("evento", "factura");

	//}

	$.ajax({
		url: globalPath + "/items-compra",
		method: "POST",
		data: datos,
		chache: false,
		contentType: false,
		processData: false,
		dataType: "json",
		beforeSend: function(objeto) {
			$('.items').html('Cargando...');
		},
		success: function(data) {
			$(".items").html(data).fadeIn('slow');
		}
	});

}




$(document).ready(function() {
	$("#addFactura").find("input,textarea,select").val("");
	$("#addFactura input[type='checkbox']").prop('checked', false).change();
	$(".proveedor2").select2({
		dropdownParent: $('#ModalADDFacturas'),
		theme: 'bootstrap-5',

		ajax: {
			url: globalPath + "/items-proveedor",
			method: "POST",
			dataType: 'json',
			delay: 250,
			data: function(params) {
				return {
					q: params.term // search term
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

		var idProveedor = $('.proveedor2').select2('data')[0].id;
		var nombre = $('.proveedor2').select2('data')[0].text;
		var email = $('.proveedor2').select2('data')[0].email;
		var telefono = $('.proveedor2').select2('data')[0].telefono;
		var direccion = $('.proveedor2').select2('data')[0].direccion;
		$('#idProveedorF').val(idProveedor);
		$('#proveedorNF').html(nombre);
		$('#emailF').html(email);
		$('#telefonoF').html(telefono);
		$('#direccionF').html(direccion);
		//guardar_proveedorFactura(idProveedor);
		$("#proveedor2").select2('data', null);

	})
});

$(document).ready(function() {

	$(".productoFac").select2({
		dropdownParent: $('#addFactura'),
		theme: 'bootstrap-5',
		ajax: {
			url: globalPath + "/items-productos",
			method: "POST",
			dataType: 'json',
			delay: 250,
			data: function(params) {
				return {
					q: params.term, // search term
					evento:2,
					tipo:1
				};
			},
			processResults: function(data) {
				return {
					results: data
				};
			},
			cache: true
		},
		minimumInputLength: 2
	}).on('change', function(e) {

		$("#descripcion").val($('.productoFac').select2('data')[0].nombre);
		$("#precio").val($('.productoFac').select2('data')[0].precio);
		$("#producto").select2('data', null);


	})

});

$(document).ready(function() {
	$("#addFactura").find("input,textarea,select").val("");
	$("#addFactura input[type='checkbox']").prop('checked', false).change();
	$(".receptor").select2({
		dropdownParent: $('#ModalADDFacturas'),
		theme: 'bootstrap-5',
		language: {

			noResults: function() {

				return "No hay resultado";
			},
			searching: function() {

				return "Buscando..";
			}
		},

		ajax: {
			url: globalPath + "/buscar-receptor",
			method: "POST",
			dataType: 'json',
			delay: 250,
			data: function(params) {
				return {
					q: params.term // search term
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
		$('#idReceptor').val($('.receptor').select2('data')[0].id);
		$("#receptor").select2('data', null);

	})
});

function salir(modal) {

	$('#' + modal).modal('hide');
	setTimeout(function() { location.reload(); }, 0)
}

$(document).ready(function() {
	$("#ModalEditarCompras").find("input,textarea,select").val("");
	$("#ModalEditarCompras input[type='checkbox']").prop('checked', false).change();
	$(".receptorE").select2({
		dropdownParent: $('#ModalEditarCompras'),
		theme: 'bootstrap-5',

		ajax: {
			url: globalPath + "/buscar-receptor",
			method: "POST",
			dataType: 'json',
			delay: 250,
			data: function(params) {
				return {
					q: params.term // search term
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
		$('#idReceptorE').val($('.receptorE').select2('data')[0].id);
		$("#receptorE").select2('data', null);

	})
});

function guardarDetalleCompra(event) {


	var idProducto = $('#producto').val();
	var idCompra = $('#idCompra').val();
	var cantidad = $('#cantidadC').val();
	var importe = $('#precioC').val();
	var datos = new FormData();
	var accion = "insert";

	datos.append("accion", accion);
	datos.append("idProducto", idProducto);
	datos.append("idCompra", idCompra);
	datos.append("cantidad", cantidad);
	datos.append("importe", importe);

	$.ajax({
		url: globalPath + "/detalle-factura-compra",
		method: "POST",
		data: datos,
		chache: false,
		contentType: false,
		processData: false,
		dataType: "json",
		success: function(respuesta) {
			var response = JSON.stringify(respuesta, null, '\t');
			var datos = JSON.parse(response);
			if (datos.code == 200) {
				$('#addFactura').modal('hide');
				Swal.fire({
					position: "top-end",
					icon: "success",
					title: datos.resultado,
					showConfirmButton: false,
					timer: 1100
				})

				mostrar_itemsFactura(idCompra, "nada");

			} else {
				Swal.fire({
					icon: "error",
					text: datos.resultado,
				})
			}
		}
	});
}

function guardar_compra(estado) {

	if (estado == 1) {
		var var_estado = 2;
	}
	if (estado == 2) {
		var var_estado = 3;
	}
	if (estado == 3) {
		var var_estado = 4;
	}




	var idOrden = $('#txtOrdenCompra').val();
	var idCompra = $('#idCompra').val();
	var plazo = $('#txtEnvioF').val();
	var forma_pago = $('#txtFormaPagoF').val();
	var fecha = $('#datepickerF').val();
	var factura = $('#txtNumeroFactura').val();
	var receptor = $('#idReceptor').val();
	var deposito = $('#txtDeposito').val();
	var moneda = $('#txtMonedaF').val();
	var itemsTabla = $('#itemsTabla').val();
	var idProveedor = $('#idProveedorF').val();
	var itemsTabla = $('#itemsTabla').val();

	var datos = new FormData();
	var accion = "update";

	if (itemsTabla == 0) {
		Swal.fire({
			icon: "warning",
			text: "Debe agregar al menos un producto a la orden",
		})
	} else if (idCompra == null || plazo == null || forma_pago == null || deposito == null || fecha == "" || factura == "" || (estado != 1 && receptor == "") || moneda == null) {
		Swal.fire({
			icon: "warning",
			text: "Debe completar todos los campos",
		})
	} else {

		datos.append("accion", accion);
		datos.append("idOrden", idOrden);
		datos.append("idCompra", idCompra);
		datos.append("plazo", plazo);
		datos.append("forma_pago", forma_pago);
		datos.append("fecha", fecha);
		datos.append("factura", factura);
		datos.append("receptor", receptor);
		datos.append("deposito", deposito);
		datos.append("moneda", moneda)
		datos.append("estado", var_estado);
		datos.append("idProveedor", idProveedor);


		$.ajax({
			url: globalPath + "/crear-factura-compra",
			method: "POST",
			data: datos,
			chache: false,
			contentType: false,
			processData: false,
			dataType: "json",
			success: function(respuesta) {
				var response = JSON.stringify(respuesta, null, '\t');
				var datos = JSON.parse(response);

				if (datos.code == 200) {
					$('#ModalADDFacturas').modal('hide');
					Swal.fire({
						position: "top-end",
						icon: "success",
						title: datos.resultado,
						showConfirmButton: false,
						timer: 1500
					})
					setTimeout(function() { location.reload(); }, 1505)
				} else {

					Swal.fire({
						icon: "error",
						text: datos.resultado

					})
				}
			}
		});

	}



}

function guardar_compraEditadas(estado) {

	if (estado == 1) {
		var var_estado = 2;
	}
	if (estado == 2) {
		var var_estado = 3;
	}
	if (estado == 3) {
		var var_estado = 4;
	}

	var idOrden = $('#txtOrdenCompraED').val();
	var idCompra = $('#idCompra1E').val();
	var plazo = $('#txtEnvioFE').val();
	var forma_pago = $('#txtFormaPagoFE').val();
	var fecha = $('#datepickerCE').val();
	var factura = $('#txtNumeroFacturaE').val();
	var receptor = $('#idReceptorE').val();
	var deposito = $('#txtDepositoE').val();
	var moneda = $('#txtMonedaFE').val();
	var idProveedor = $('#proveedorCE').val();
	var itemsTabla = $('#itemsTabla').val();

	var datos = new FormData();
	var accion = "update";

	if (itemsTabla == 0) {
		Swal.fire({
			icon: "warning",
			text: "Debe agregar al menos un producto a la orden",
		})
	} else if (idCompra == null || plazo == null || forma_pago == null || fecha == "" || factura == "" || (estado != 1 && receptor == "") || moneda == null) {
		Swal.fire({
			icon: "warning",
			text: "Debe completar todos los campos",
		})

	} else {


		datos.append("accion", accion);
		datos.append("idOrden", idOrden);
		datos.append("idCompra", idCompra);
		datos.append("plazo", plazo);
		datos.append("forma_pago", forma_pago);
		datos.append("fecha", fecha);
		datos.append("factura", factura);
		datos.append("receptor", receptor);
		datos.append("deposito", deposito);
		datos.append("moneda", moneda)
		datos.append("estado", var_estado);
		datos.append("idProveedor", idProveedor);


		if (var_estado > 1) {
			$.ajax({
				url: globalPath + "/crear-factura-compra",
				method: "POST",
				data: datos,
				chache: false,
				contentType: false,
				processData: false,
				dataType: "json",
				success: function(respuesta) {
					var response = JSON.stringify(respuesta, null, '\t');
					var datos = JSON.parse(response);

					if (datos.code == 200) {

						//notificacion("Orden de compra", "Actualizado orden # " + idOrden);
						$('#ModalEditarCompras').modal('hide');
						Swal.fire({
							position: "top-end",
							icon: "success",
							title: datos.resultado,
							showConfirmButton: false,
							timer: 1500
						})
						setTimeout(function() { location.reload(); }, 1505)
					} else {

						Swal.fire({
							icon: "error",
							text: datos.resultado,
						})
					}

				}
			});
		} else if (var_estado == 4) {
			Swal.fire({
				icon: "error",
				title: "Oops...",
				text: "Esta orden ya esta cancelada",

			})

		}
	}




}

function guardar_proveedorFactura(idProveedor) {


	var idCompra = $('#idCompra').val();
	var datos = new FormData();
	var accion = "uProveedor";
	datos.append("accion", accion);
	datos.append("idProveedor", idProveedor);
	datos.append("idCompra", idCompra);

	$.ajax({
		url: globalPath + "/crear-factura-compra",
		method: "POST",
		data: datos,
		chache: false,
		contentType: false,
		processData: false,
		dataType: "json",
		success: function(respuesta) {


		}
	});



}




//CREACION DEL PAGINADO 
$(document).ready(function() {
	$("#searchcompras").keyup(function() {
		_this = this;
		// Show only matching TR, hide rest of them
		$.each($("#compras tbody tr"), function() {
			if ($(this).text().toLowerCase().indexOf($(_this).val().toLowerCase()) === -1)
				$(this).hide();
			else
				$(this).show();
		});
	});
});



//Aqui va el paginado


$(document).ready(function() {

	addPagerToTables('#compras', 8);

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