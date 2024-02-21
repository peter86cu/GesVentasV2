function abrirWEB(id) {
	window.open("punto-venta?id="+id+"&estado=modificar", 'Nombre de la ventana');
}


function entrarCaja() {

	var user = $("#txt_usuario").val();
	var pass = $("#txt_contra").val();
	var datos = new FormData();
	var accion = "loginCaja";
	datos.append("accion", accion);
	datos.append("user", user);
	datos.append("pass", pass);

	$.ajax({
		url: "ajax/ajaxVentas.php",
		method: "POST",
		data: datos,
		chache: false,
		contentType: false,
		processData: false,
		dataType: "json",
		success: function(respuesta) {
			if (respuesta) {
				$('#ModalLoginCaja').modal('hide');
				var datos = new FormData();
				var accion = "buscarArqueo";
				datos.append("accion", accion);

				$.ajax({
					url: "ajax/ajaxVentas.php",
					method: "POST",
					data: datos,
					chache: false,
					contentType: false,
					processData: false,
					dataType: "json",
					success: function(respuesta) {

						if (respuesta['id_apertura_cajero'] != null) {

							var datos = new FormData();
							var accion = "insert";
							datos.append("accion", accion);
							datos.append("idApertura", respuesta['id_apertura_cajero']);

							$.ajax({
								url: "ajax/ajaxVentas.php",
								method: "POST",
								data: datos,
								chache: false,
								contentType: false,
								processData: false,
								dataType: "json",
								success: function(datos) {

									if (datos) {
										setTimeout(function() { location.reload(); }, 5)
										window.open("ventas", 'Nombre de la ventana', "fullscreen,location=no,menubar=no,status=no,toolbar=no,RESIZABLE=0");
									}

								}

							});


						} else {

							Swal.fire({
								icon: "error",
								title: "Oops...",
								text: "No existe una apertura de caja!"
							})

						}
					}
				});

			}
		}
	});
}
$(document).keydown(function(tecla) {
	if (tecla.keyCode == 112) {

		var session = $("#variable_sesion").val();
		if (session == "no_login") {
			$('#ModalLoginCaja').modal('show');
		} else {
			setTimeout(function() { location.reload(); }, 5)
			window.open("ventas", 'Nombre de la ventana', "fullscreen,location=no,menubar=no,status=no,toolbar=no,RESIZABLE=0");
		}

	}
});


$(document).keydown(function(tecla) {
	if (tecla.keyCode == 9 && $("#buscar_cliente").val().length == 8) {

		var datos = new FormData();
		var accion = "buscarCliente";
		datos.append("accion", accion);
		datos.append("ci", $("#buscar_cliente").val());
		datos.append("idVenta", $("#txtVenta").val());


		$.ajax({
			url: globalPath+"/buscar-actualizar-cliente",
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
					$('#buscar_cliente').val(datos.cliente.nombres);
					$('#idCliente').val(datos.cliente.id_cliente);
					$('#idCliente').html(datos.cliente.id_cliente);
				} else {
					//abrirModal('abrirDiaTemp');
					//$('#ModalNuevoClientes').modal('show');
					
					//$('#ModalNuevoClientes').modal({backdrop: 'static', keyboard: false})
					$('#ModalNuevoClientes').modal('show');

				}
			}
		});


	}
});






//para el key
function setQuantity(key) {
	$("#txtProductoV").val($("#txtProductoV").val() + key);
}
function keyboardDEL() {
	if ($("#items .seleccionado").length > 0) {
		var selector = $("#items .seleccionado").children("td");
		$.ajax({
			url: "ajax_item",
			data: "producto_cod=" + selector.eq(1).html() + '&id_venta=' + $("#id_venta").val() + '&cantidad=-' + selector.eq(0).html() + "&del=true",
			type: "POST",
			dataType: "json",
			success: function(data) {
				if (data.producto_no_existe == '1') {
					alert('El producto no existe');
				} else {
					if (data.no_hay_stock == '1') {
						alert('Ya no hay en Stock');
					} else if (data.producto_no_vendible == '1') {
						alert('El producto no se puede vender!');
					} else {
						if (data.supera_cantidad == '1') {
							alert('Supera el limite del la cantidad en Stock');
						} else {
							$('#items tbody').html(data.html);
							$('#total').html('Gs. ' + number_format(data.total, 0, ',', '.'));
							$('#total_venta').attr('value', data.total);
							$('#total_en_letras').html(data.total_en_letras);
							$('#iva5').html(data.iva5);
							$('#iva10').html(data.iva10);
							$('#ivainput5').val(data.ivainput5)
							$('#ivainput10').val(data.ivainput10)
							$('#sumaexcento').html(data.sumaexcento);
							$('#sumaiva5').html(data.sumaiva5);
							$('#sumaiva10').html(data.sumaiva10);
							if ($('#total_venta').val() > 0) {
								$('#cancelar_venta').hide();
							} else {
								$('#cancelar_venta').show();
							}
						}
					}
				}
				document.getElementById('producto_cod').focus();
			}
		});

	}



}



function buscarSimboloMonedaJS(idMoneda) {



	var datos = new FormData();
	var accion = "buscarMoneda";
	datos.append("accion", accion);
	datos.append("idMoneda", idMoneda);


	$.ajax({
		url: "ajax/ajaxProductos.php",
		method: "POST",
		data: datos,
		chache: false,
		contentType: false,
		processData: false,
		dataType: "json",
		success: function(respuesta) {

			document.querySelector('#monedaV').innerText = respuesta;
		}
	});

}

//CREACION DEL PAGINADO
$(document).ready(function() {
	$("#searchlistadoventas").keyup(function() {
		_this = this;
		// Show only matching TR, hide rest of them
		$.each($("#listaventasops tbody tr"), function() {
			if ($(this).text().toLowerCase().indexOf($(_this).val().toLowerCase()) === -1)
				$(this).hide();
			else
				$(this).show();
		});
	});
});

//Aqui va el paginado


$(document).ready(function() {

	addPagerToTables('#listaventasops', 8);

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



