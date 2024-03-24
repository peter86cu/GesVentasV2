function abrirModal(modal) {

	$('#' + modal).modal('show');


}



function agregarPrefacturaInicial(event) {

	var datos = new FormData();
	var accion = "inicial";
	var idPrefactura = 0;
	var idCliente = 1;
	datos.append("estado", 1);
	datos.append("accion", accion);
	datos.append("idPrefactura", idPrefactura);
	datos.append("idCliente", idCliente);
	datos.append("fecha", "");
	datos.append("plazo", 0);
	datos.append("forma_pago", 0);

	$.ajax({
		url: globalPath+"/crear-prefactura-venta",
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

				$("#idPrefacturaT").val(data.resultado);
				document.querySelector('#idPrefactura').innerText = data.resultado;
				$('#ModalADDPrefactura').modal('show');
				mostrar_items(data.resultado, "nada");
			} else {
				Swal.fire({
					icon: "error",
					text: data.error.menssage
				})
			}
		}
	});


}

function addPrefactura(id) {

	var datos = new FormData();
	var accion = "buscar";
	datos.append("accion", accion);
	datos.append("id", id);


	$.ajax({
		url: globalPath+"/editar-prefactura",
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

				document.querySelector('#idPrefactura').innerText = data.prefactura.id_prefactura;
				//document.querySelector('#datepicker').innerText = respuesta["fecha_hora"]; 
				$("#txtFormaPago > option[value=" + data.prefactura.id_forma_pago + "]").attr("selected", true);
				$("#txtEnvio > option[value=" + data.prefactura.id_plazo + "]").attr("selected", true);
				$("#idPrefacturaT").val(data.prefactura.id_prefactura);
				$("#cliente").val(data.prefactura.id_cliente);
				$("#datepicker").val(data.prefactura.fecha_hora);
				$("#id_proveedor").val(data.prefactura.id_cliente);
				buscar_cliente_prefactura(data.prefactura.id_cliente, "2");
				mostrar_items(data.prefactura.id_prefactura, "nada");

				$('#ModalADDPrefactura').modal('show');
			} else {
				Swal.fire({
					icon: "error",
					text: data.resultado,
				})
			}
		}


	});


}

function editarPrefactura(id, estado) {

	var datos = new FormData();

	var accion = "buscar";
	datos.append("accion", accion);
	datos.append("id", id);


	$.ajax({
		url: globalPath+"/editar-prefactura",
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
				document.querySelector('#idPrefacturaE').innerText = data.prefactura.id_prefactura;
				//document.querySelector('#datepicker').innerText = respuesta["fecha_hora"]; 
				$("#txtFormaPagoE > option[value=" + data.prefactura.id_forma_pago + "]").attr("selected", true);
				$("#txtEnvioE > option[value=" + data.prefactura.id_plazo + "]").attr("selected", true);
				buscar_cliente_prefactura(data.prefactura.id_cliente, "1");
				mostrar_items(data.prefactura.id_prefactura, "editar");

				$("#idPrefactura1E").val(data.prefactura.id_prefactura);
				$("#clienteE1").val(data.prefactura.id_cliente);
				$("#datepickerE").val(data.prefactura.fecha_hora);

				if (estado == 4) {
					document.getElementById("bttautorizo").disabled = true;
					document.getElementById("bttcancelo").disabled = true;
				}
				if (estado == 3) {
					document.getElementById("bttautorizo").disabled = true;
				}

				$('#ModalEditarPrefactura').modal('show');

			} else {
				Swal.fire({
					icon: "error",
					text: data.resultado,
				})
			}

		}

	});


}



function buscar_cliente_prefactura(id, paso) {

	var datos = new FormData();

	var accion = "buscarCliente";
	datos.append("accion", accion);
	datos.append("idCliente", id,);

	$.ajax({
		url: globalPath+"/buscar-cliente",
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
					document.querySelector('#clienteNE').innerText = data.cliente.nombres;
					document.querySelector('#direccionE').innerText = data.cliente.direccion;
					document.querySelector('#emailE').innerText = data.cliente.email;
					document.querySelector('#telefonoE').innerText = data.cliente.telefono;
					document.querySelector('#autorizo').innerText = data.cliente.nombres;
					document.querySelector('#cancelo').innerText = data.cliente.nombres;
				} if (paso == "2") {

					$('#id_cliente').val(data.cliente.id_cliente);
					$('#clienteN').html(data.cliente.nombres);
					$('#direccion').html(data.cliente.direccion);
					$('#email').html(data.cliente.email);
					$('#telefono').html(data.cliente.telefono);
					//document.querySelector('#proveedorN').innerText = data.proveedor.razon_social;
					//document.querySelector('#direccion').innerText = data.proveedor.direccion;
					//document.querySelector('#email').innerText = data.proveedor.email;
					//document.querySelector('#telefono').innerText = data.proveedor.telefono;
			//$('#ModalEditarPrefactura').modal('show');

				}
			} /*else {
				Swal.fire({
					icon: "error",
					text: data.result,
				})
			}*/
		}


	})
}


function guardarDetallePrefactura(event) {


	var idProducto = $('#producto').val();
	var id = $('#idPrefacturaT').val();
	var cantidad = $('#cantidad').val();
	var importe = $('#precio').val();
	var datos = new FormData();
	var accion = "insert";

	datos.append("accion", accion);
	datos.append("idProducto", idProducto);
	datos.append("id", id);
	datos.append("cantidad", cantidad);
	datos.append("importe", importe);


	$.ajax({
		url: globalPath+"/detalle-prefactura",
		method: "POST",
		data: datos,
		chache: false,
		contentType: false,
		processData: false,
		dataType: "json",
		success: function(respuesta) {
		   var response = JSON.stringify(respuesta, null, '\t');
			var datos = JSON.parse(response);
			if (datos.code==200) {
				$("#addItem").find("input,textarea,select,select2").val("");
				$("#addItem input[type='checkbox']").prop('checked', false).change();
				$('#addItem').modal('hide');
				Swal.fire({
					position: "top-end",
					icon: "success",
					title: "Item agregado",
					showConfirmButton: false,
					timer: 1100
				})


				mostrar_items(id, "nada");

			} else {
				Swal.fire({
					icon: "error",					
					text: datos.resultado
					
				})
			}
		}
	});
}





function mostrar_items(id, accion) {

	var datos = new FormData();

	//if (accion == "nada") {
	//var accion = "nada";
	datos.append("accion", accion);
	datos.append("idOrden", id);
	datos.append("id", "");
	datos.append("evento", "prefactura");

	//}

	$.ajax({
		url: globalPath+"/items-compra",
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


function eliminar_item(id, idOrden) {
	//var idOrden = $('#idOrdenT1').val();
	var datos = new FormData();
	var accion = "eliminar";
	datos.append("accion", accion);
	datos.append("id", id);


	$.ajax({
		url: globalPath+"/delete-items-compra",
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
			var response = JSON.stringify(data, null, '\t');
			var datos = JSON.parse(response);

			if (datos.code == 200) {
				mostrar_items(idOrden, "nada");
				Swal.fire({
					position: "top-end",
					icon: "success",
					title: datos.resultado,
					showConfirmButton: false,
					timer: 1100
				})

			} else {
				Swal.fire({
					icon: "error",
					text: datos.resultado,
				})
			}
		}
	});
}




$(document).ready(function() {
	$(".productos").select2({
		//dropdownParent: $('#ModalADDOrdenes'),
		dropdownParent: $('#addItem'),
		theme: 'bootstrap-5',
		ajax: {
			url: globalPath+"/items-productos",
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
			cache: true
		},
		minimumInputLength: 2
	}).on('change', function(e) {

		$("#descripcionProducto").val($('.productos').select2('data')[0].nombre);
		$("#precio").val($('.productos').select2('data')[0].precio);

	})
});

//Buscar proveedores

$(document).ready(function() {

	$(".cliente").select2({

		dropdownParent: $('#ModalADDPrefactura'),
		theme: 'bootstrap-5',
		ajax: {
			url: globalPath+"/items-cliente",
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

			cache: true
		},
		minimumInputLength: 2

	}).on('change', function(e) {
		var idCliente = $('.cliente').select2('data')[0].id;
		var nombre = $('.cliente').select2('data')[0].text;
		var email = $('.cliente').select2('data')[0].email;
		var telefono = $('.cliente').select2('data')[0].telefono;
		var direccion = $('.cliente').select2('data')[0].direccion;
		$('#id_cliente').html(idCliente);
		$('#clienteN').html(nombre);
		$('#email').html(email);
		$('#telefono').html(telefono);
		$('#direccion').html(direccion);
		guardar_cliente(idCliente);

	})

});


function guardar_cliente(idCliente) {
	var id = $('#idPrefacturaT').val();
	var datos = new FormData();
	var accion = "uCliente";
	datos.append("accion", accion);
	$('#id_cliente').val(idCliente);
	datos.append("idCliente", idCliente);
	datos.append("idPrefactura", id);
	datos.append("estado", 1);
	datos.append("fecha", "");
	datos.append("plazo", 0);
	datos.append("forma_pago", 0);

	$.ajax({
		url: globalPath+"/crear-prefactura-venta",
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


function guardar_orden(estado) {

	if (estado == 1) {
		var var_estado = 2;
	}
	if (estado == 2) {
		var var_estado = 3;
	}
	if (estado == 3) {
		var var_estado = 4;
	}
	if (estado == 4) {
		var var_estado = 5;
	}


	if (estado == 0) {
		$('#ModalADDPrefactura').modal('hide');
		Swal.fire({
			position: "top-end",
			icon: "success",
			title: "Se guardo la prefactura como borrador.",
			showConfirmButton: false,
			timer: 1500
		})
		setTimeout(function() { location.reload(); }, 1505)
	} else {


		var idCliente = $('#id_cliente').val();
		var id = $('#idPrefacturaT').val();
		var plazo = $('#txtEnvio').val();
		var forma_pago = $('#txtFormaPago').val();
		var fecha = $('#datepicker').val();
		var datos = new FormData();
		var accion = "update";
		var itemsTabla = $('#itemsTabla').val();
		if (itemsTabla == 0) {
			Swal.fire({
				icon: "warning",
				text: "Debe agregar al menos un producto a la orden",
			})
		} if (idCliente == null || plazo == null || forma_pago == null || fecha == "") {
			Swal.fire({
				icon: "warning",
				text: "Debe completar todos los campos",
			})
		} else {
			datos.append("accion", accion);
			datos.append("idCliente", idCliente);
			datos.append("idPrefactura", id);
			datos.append("plazo", plazo);
			datos.append("forma_pago", forma_pago);
			datos.append("fecha", fecha);
			datos.append("estado", var_estado);

			$.ajax({
				url: globalPath+"/crear-prefactura-venta",
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
						$('#ModalADDPrefactura').modal('hide');
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
		}



	}
}

function guardar_prefacturaEditadas(estado) {
	if (estado == 1) {
		var var_estado = 2;
	}
	if (estado == 2) {
		var var_estado = 3;
	}
	if (estado == 3) {
		var var_estado = 4;
	}
	if (estado == 5) {
		var var_estado = 5;
	}


	var idCliente = $('#clienteE1').val();
	var id = $('#idPrefactura1E').val();
	var plazo = $('#txtEnvioE').val();
	var forma_pago = $('#txtFormaPagoE').val();
	var fecha = $('#datepickerE').val();
	var datos = new FormData();
	var accion = "update";

	datos.append("accion", accion);
	datos.append("idCliente", idCliente);
	datos.append("idPrefactura", id);
	datos.append("plazo", plazo);
	datos.append("forma_pago", forma_pago);
	datos.append("fecha", fecha);
	datos.append("estado", var_estado);

	if (var_estado > 1) {
		$.ajax({
			url: globalPath+"/crear-prefactura-venta",
			method: "POST",
			data: datos,
			chache: false,
			contentType: false,
			processData: false,
			dataType: "json",
			success: function(respuesta) {

				var response = JSON.stringify(respuesta, null, '\t');
				var datas = JSON.parse(response);

				if (datas.code == 200) {
					$('#ModalEditarOrdenes').modal('hide');
					Swal.fire({
						position: "top-end",
						icon: "success",
						title: datas.resultado,
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
	} else if (var_estado == 5) {
		Swal.fire({
			icon: "error",
			text: "Esta orden ya esta cancelada",

		})

	}


}


function eliminarOrdenCompra(idOrden) {
	//var idOrden=  $(this).attr("idOrden");
	var datos = new FormData();
	var accion = "delete";

	datos.append("accion", accion);
	datos.append("idProveedor", 0);
	datos.append("idOrden", idOrden);
	datos.append("estado", 1);
	datos.append("fecha", "");
	datos.append("plazo", 0);
	datos.append("forma_pago", 0);

	const swalWithBootstrapButtons = Swal.mixin({
		customClass: {
			confirmButton: 'btn btn-success',
			cancelButton: 'btn btn-danger'
		},
		buttonsStyling: false
	})

	swalWithBootstrapButtons.fire({
		title: 'Estas seguro de eliminar la orden con ID: ' + idOrden,

		icon: 'error',
		showCancelButton: true,
		confirmButtonText: 'Si! ',
		cancelButtonText: 'No!',
		reverseButtons: true
	}).then((result) => {
		if (result.isConfirmed) {

			$.ajax({
				url: globalPath+"/crear-orden-compra",
				method: "POST",
				data: datos,
				chache: false,
				contentType: false,
				processData: false,
				dataType: "json",
				success: function(respuesta) {
					var response = JSON.stringify(respuesta, null, '\t');
					var datas = JSON.parse(response);
					if (datas.code == 200) {
						swalWithBootstrapButtons.fire(
							'Eliminado!',
							'La orden a sido eliminada.',
							'success'
						).then((result) => {
							if (result.isConfirmed)
								location.reload();
						});

					} else {
						Swal.fire({
							icon: 'error',
							text: 'Ocurrio un error eliminando la orden!',
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
	$("#searchordenes").keyup(function() {
		_this = this;
		// Show only matching TR, hide rest of them
		$.each($("#ordenes tbody tr"), function() {
			if ($(this).text().toLowerCase().indexOf($(_this).val().toLowerCase()) === -1)
				$(this).hide();
			else
				$(this).show();
		});
	});
});

function datosImprimir(idPrefactura,idCliente){
        var datos = new FormData();

        	datos.append("idCliente", idCliente);
        	datos.append("idPrefactura", idPrefactura);
                 $.ajax({
        				url: globalPath+"/imprimir-prefactura",
        				method: "POST",
        				data: datos,
        				chache: false,
        				contentType: false,
        				processData: false,
        				dataType: "json",
        				success: function(respuesta) {
        					var response = JSON.stringify(respuesta, null, '\t');
        					var datas = JSON.parse(response);
        					if (datas.code == 200) {
        					//window.open(datas.resultado,"_blank");
        					ventanaCentrada('/prefacturas/prefactura_'+idPrefactura+'.pdf','Prefactura','','1024','768','true');


        					} else {
        						Swal.fire({
        							icon: 'error',
        							text: datas.resultado,
        						})
        					}

        				}
        			});




   }
function ventanaCentrada(theURL,winName,features, myWidth, myHeight, isCenter) { //v3.0
 if(window.screen)if(isCenter)if(isCenter=="true"){
    var myLeft = (screen.width-myWidth)/2;
    var myTop = (screen.height-myHeight)/2;
    features+=(features!='')?',':'';
    features+=',left='+myLeft+',top='+myTop;
  }
  window.open(theURL,winName,features+((features!='')?',':'')+'width='+myWidth+',height='+myHeight);
}

//Aqui va el paginado


$(document).ready(function() {

	addPagerToTables('#ordenes', 8);

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
