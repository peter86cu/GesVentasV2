var caja_cuadrada = 3;

var valorcompra = [];
var valorventa = [];

function Cambio(idmoneda, value) {
	this.idmoneda = idmoneda;
	this.value = value;

}
function irPosVenta() {
	window.location.href = "punto-venta";
}
function obtenerValue(id) { // Obtengo el valor entrado

	var obtenerV = $("#" + id).val();
	obtenerV = parseInt(obtenerV);

	return obtenerV;
}

function validarEntradaCompra(id, valor) {
	
	var arq = new Cambio(id, valor);
	valorcompra.push(arq);

}

function validarEntradaVenta(id, valor) {
	

	var arq = new Cambio(id, valor);
	valorventa.push(arq);

}

function guardarArqueo() {

	//var id_moneda ="moneda_"+id;


	var size = $("#sizeMonedas").val();

	for (var i = 1; i <= size; i++) {

		validarEntradaCompra(i, $("#valorcompra_" + i).val());
		validarEntradaVenta(i, $("#valorventa_" + i).val());

	}

	var datos = new FormData();
	var fechaApertura = $("#fechaapertura").val();

	var accion = "insert";

	datos.append("accion", accion);
	datos.append("fechaApertura", fechaApertura);
	datos.append("arrayCompra", JSON.stringify(valorcompra));
	datos.append("arrayVenta", JSON.stringify(valorventa));


	$.ajax({
		url: globalPath + "/abrir-dia",
		method: "POST",
		data: datos,
		chache: false,
		contentType: false,
		processData: false,
		dataType: "json",
		success: function(respuesta) {
			var response = JSON.stringify(respuesta, null, '\t');
			var data = JSON.parse(response);
			console.log(data.code);
			if (data.code == 200) {
				// $('#arqueoCaja').modal('hide');
				valorventa = [];
				valorcompra =[];
				Swal.fire({
					position: "top-end",
					icon: "success",
					title: data.resultado,
					showConfirmButton: false,
					timer: 1500
				})
				//Ocultar DIV
					var x = document.getElementById("loadingModal");
				    if (x.style.display === "none") {
				        x.style.display = "block";
				    } else {
				        x.style.display = "none";
				    }
				cerrarModal('abrirDia');
				

				//setTimeout(function() { location.href = "./inicio"; }, 1505)
			} else {
				Swal.fire({
					icon: "error",
					text: data.resultado
				})
				//setTimeout(function() {location.reload();}, 1505)
			}


		}
	});


}

function guardarArqueoTerminal() {

	//var id_moneda ="moneda_"+id;
	var datos = new FormData();
	var idApertura = $("#idApertura").val();

	var tipo_arqueo = $("#txtTipo").val();

	var id_estado_arqueo = $("#txtId_tipo_arqueo").val();

	if (id_estado_arqueo == 1) {
		var cuadre = id_estado_arqueo;
	} else {
		var cuadre = caja_cuadrada;
	}

	if (tipo_arqueo == 3) {
		eliminarVentasAbiertas(idApertura);
	}


	var accion = "insert";

	datos.append("accion", accion);
	datos.append("idApertura", idApertura);
	datos.append("idCuadre", cuadre);
	datos.append("tipoArqueo", id_estado_arqueo);
	datos.append("array", JSON.stringify(arqueos));

	$.ajax({
		url: globalPath + "/guardar-arqueo-detalle",
		method: "POST",
		data: datos,
		chache: false,
		contentType: false,
		processData: false,
		dataType: "json",
		success: function(respuesta) {
			var response = JSON.stringify(respuesta, null, '\t');
			var datos = JSON.parse(response);
			if (datos.code = 200) {
				$('#arqueoCaja').modal('hide');
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
				setTimeout(function() { location.reload(); }, 1505)
			}


		}
	});


}



function abrirCaja() {

	var datos = new FormData();
	var accion = "inicio";

	datos.append("accion", accion);
	datos.append("idApertura", 0);
	datos.append("idTurno", 0);

	$.ajax({
		url: globalPath + "/buscar-apertura-terminal",
		method: "POST",
		data: datos,
		chache: false,
		contentType: false,
		processData: false,
		dataType: "json",
		success: function(respuesta) {
			var response = JSON.stringify(respuesta, null, '\t');
			var datos = JSON.parse(response);
			if (datos.code == 200 && datos.terminal == null) {

				var datosnuevo = new FormData();
				var accion2 = "respuesta";
				datosnuevo.append("accion", accion2);
				datosnuevo.append("idApertura", 0);
				datosnuevo.append("idTurno", 0);
				$.ajax({
					url: globalPath + "/buscar-apertura-terminal",
					method: "POST",
					data: datosnuevo,
					chache: false,
					contentType: false,
					processData: false,
					dataType: "json",
					success: function(resultado) {
						var response = JSON.stringify(resultado, null, '\t');
						var datos = JSON.parse(response);
						if (datos.code == 200) {

							$("#txtConsecutivo").val(datos.terminal.nroConsecutivo);
							$("#idApertura").val(datos.terminal.idAperturaCajero);
							$("#txtCaja").val(datos.terminal.idCaja);

							//$("#txtTurno").val(datos.terminal.idTurno);

							$("#txtTipo > option[value=" + 1 + "]").attr("selected", true);
							$("#txtTipo").attr('disabled', 'disabled');
							$('#modalInicioCaja').modal('show');
							$('#btEjecutar').hide();
							//document.getElementById("btEjecutar").visible = false;

						} else {

							Swal.fire({
								icon: "warning",
								text: datos.resultado
							})




						}

					}
				});


			} else if (datos.code == 406) {
				/*	Swal.fire({
						icon: "warning",
						text: datos.resultado + " Ir a -> TPV/Abrir día"
					})*/

				Swal.fire({
					title: datos.terminalCierre.mensaje,
					showDenyButton: true,
					showCancelButton: false,
					confirmButtonText: 'Cerrar Terminal',
					denyButtonText: `Cancelar`,
				}).then((result) => {
					/* Read more about isConfirmed, isDenied below */
					if (result.isConfirmed) {
						if (datos.code == 406) {
							for (var i = 0; i < datos.terminalCierre.lstAperturas.length; i++) {
								$("#fechaCierre").val(datos.terminalCierre.lstAperturas[i].fechaInicio);
								$("#txtConsecutivo").val(datos.terminalCierre.lstAperturas[i].nroConsecutivo);
								$("#idApertura").val(datos.terminalCierre.lstAperturas[i].idAperturaCajero);
								$("#txtTurno > option[value=" + datos.terminalCierre.lstAperturas[i].idTurno + "]").attr("selected", true);
								$("#txtCaja > option[value=" + datos.terminalCierre.lstAperturas[i].idCaja + "]").attr("selected", true);
								$("#txtTurno").attr('disabled', 'disabled');
								$("#txtTipo").attr('disabled', 'disabled');
								$("#txtTipo > option[value=" + 3 + "]").attr("selected", true);
								document.getElementById("btEjecutar").visible = true;
								document.getElementById("btAbrir").style.display = 'none';
								$('#modalInicioCaja').modal({ backdrop: 'static', keyboard: false })
								$('#modalInicioCaja').modal('show');
							}


						} else if (result.isDenied) {
							window.location.href = 'inicio'
						}
					}
				})


			} else if (datos.code == 400) {
				Swal.fire({
					icon: "warning",
					text: datos.resultado + " Ir a -> TPV/Abrir día"

				})

			} else {

				$("#txtConsecutivo").val(datos.terminal.nroConsecutivo);
				$("#idApertura").val(datos.terminal.idAperturaCajero);
				$("#txtTurno > option[value=" + datos.terminal.idTurno + "]").attr("selected", true);
				$("#txtCaja > option[value=" + datos.terminal.idCaja + "]").attr("selected", true);

				$("#txtTurno").attr('disabled', 'disabled');
				$("#txtTipo > option[value=" + 1 + "]").attr("disabled", true);
				document.getElementById("btEjecutar").visible = true;
				document.getElementById("btAbrir").style.display = 'none';
				$('#modalInicioCaja').modal({ backdrop: 'static', keyboard: false })

				$('#modalInicioCaja').modal('show');
			}


		}
	});
}


function eliminaAperturaInicio(aperturaEliminar, idArqueo, idApertura, idUsuario, fecha) {
	var datos = new FormData();

	if (document.getElementById("txtTipo").disabled) {
		var accion = "cierre";
		var tipo = $('#txtTipo').val();
		//var fecha = fecha;
		datos.append("accion", accion);
		datos.append("fechaCierre", "");
		datos.append("tipoArqueo", tipo);
		datos.append("idArqueo", 0);
		datos.append("idApertura", 0);
		datos.append("idUsuario", idUsuario);
	} else if (aperturaEliminar == 1) {
		var accion = "normal";
		var tipo = $('#txtTipo').val();
		datos.append("accion", accion);
		datos.append("fechaCierre", fecha);
		datos.append("tipoArqueo", tipo);
		datos.append("idArqueo", 0);
		datos.append("idApertura", 0);
		datos.append("idUsuario", idUsuario);

	} else if (aperturaEliminar == 2) {
		var accion = "delete";
		var tipo = $('#txtTipo').val();
		var fecha = $('#fechaCierre').val();
		datos.append("accion", accion);
		datos.append("fechaCierre", fecha);
		datos.append("tipoArqueo", tipo);
		datos.append("idArqueo", idArqueo);
		datos.append("idApertura", idApertura);
		datos.append("idUsuario", idUsuario);


	}

	if (tipo != 1 && aperturaEliminar == 1) {
		Swal.fire({
			icon: "warning",
			text: "EL arque no se puede eliminar en esta acción."
		})

	} else {
		$.ajax({
			url: globalPath + "/eliminar-apertura-terminal",
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
					mensajeOK(datos.resultado);

				} else {
					mensajeError(datos.resultado)
				}
				cerrarModal('modalInicioCaja');
				//$(".modal-body input").val("")
				//$('#modalInicioCaja').modal('hide');
				//setTimeout(function() { location.reload(); }, 1505)

			}

		});

	}


}

//Listado de ventas por arqueo de usuario
function listarArqueosPorUsuarioCierre(idUsuario, fecha) {
	var datos = new FormData();

	//datos.append("accion", accion);
	datos.append("idUsuario", idUsuario);
	datos.append("fecha", fecha);
	$.ajax({
		url: globalPath + "/listar-ventas-apertura-usuario",
		method: "POST",
		data: datos,
		chache: false,
		contentType: false,
		processData: false,
		dataType: "json",
		success: function(respuesta) {

			var response = JSON.stringify(respuesta, null, '\t');
			var datos = JSON.parse(response);
			var tr1 = `<tr style="background: #ccffcc">`;
			var tr2 = "";
			var tr3 = "";

			var totalEfectivo = 0;
			var totalTarjeta = 0;

			if (datos.code == 200) {
				for (var i = 0; i < datos.lstVentas.length; i++) {

					tr2 = tr2 + `<tr ><td>` + datos.lstVentas[i].id_venta_cobro + `</td>
           			<td>`+ datos.lstVentas[i].tipoCobro + `</td>          
           			<td>`+ datos.lstVentas[i].cobro + `</td></tr>`;

					if (datos.lstVentas[i].tipoCobro == "Efectivo") {
						totalEfectivo = totalEfectivo + datos.lstVentas[i].cobro;
					} else {
						totalTarjeta = totalTarjeta + datos.lstVentas[i].cobro;
					}

				}
				var total = totalEfectivo + totalTarjeta;

				var t1 = `<tr><td style="background: rgb(64, 128, 128); width: 15px;"><strong>TOTAL EFECTIVO</strong></td>
           			<td style="text-align: center; width: 10%">`+ totalEfectivo + `</td></tr>`;

				var t2 = `<tr><td style="background: rgb(64, 128, 128); width: 8%"><strong>TOTAL TARJETA</strong></td>
           			<td style="text-align: center; width: 10%">`+ totalTarjeta + `</td></tr>`;

				var t3 = `<tr><td style="background: rgb(64, 128, 128); width: 8%"><strong>TOTAL</strong></td>
           			<td style="text-align: center; width: 10%">`+ total + `</td></tr>`;
				tr3 = t1 + t2 + t3;

				abrirModal('listaVentasArqueoUsuario');
				$("#tabla2-item").append(tr2);
				$("#tabla3-item").append(tr3);

			} else {
				Swal.fire({
					icon: 'error',
					title: datos.resultado,
				})

			}

		}

	});



}


//Arqueo tes
function Arqueo(id, value) {
	this.id = id;
	this.value = value;

}


function obtenerValue(id) { // Obtengo el valor entrado

	var obtenerV = $("#" + id).val();
	obtenerV = parseInt(obtenerV);

	return obtenerV;
}


var arqueos = [];


function validarEntrada(id, id_moneda) {

	var encontre = false;
	var pos;
	if (arqueos.length < 1) {

		var arq = new Arqueo(id, obtenerValue(id_moneda));
		arqueos.push(arq);

	} else {

		for (var i = 0; i < arqueos.length; i++) {

			if (arqueos[i].id == id) {
				encontre = true;
				pos = i;
				break;
			}
		}

		if (!encontre) {
			var arq = new Arqueo(id, obtenerValue(id_moneda));
			arqueos.push(arq);

		} else {
			arqueos[pos].value = obtenerValue(id_moneda);

		}
	}
}



var caja_cuadrada = 3;


function sumar(id, t) {
	var id_moneda = "moneda_" + id;


	var tipo_arqueo = $("#txtId_tipo_arqueo").val();
	var saldo = $("#saldo_inicial").val();


	validarEntrada(id, id_moneda);

	if (t.value == 1) {
		valor = $(t).attr("placeholder") * 1;
	} else {
		valor = parseFloat($(t).attr("placeholder")) * parseFloat(t.value);

	}

	if (t.value === "") {
		valor = 0;
	}


	$(t).parent().parent().find('td').eq(2).find('.total').html(valor);

	var total = 0;
	$(".total").each(function() {
		total += parseFloat($(this).html());
	})

	$("#_total_arqueo").html("$ " + total);
	if (total == saldo && tipo_arqueo != 1) {
		//alert('Las caja esta cuadrada');
		
//		Swal.fire({
//			position: "top-end",
//			icon: "success",
//			title: "Las caja esta cuadrada",
//			showConfirmButton: false,
//			timer: 1100
//		})

		caja_cuadrada = 2;
	}




}


function abrirTurno(arqueo) {

	var datos = new FormData();
	if (arqueo == 1) {
		var accion = "update";
		var turno = $('#txtTurno').val();
		var caja = $('#txtCaja').val();
		var idApertura = $("#idApertura").val();
		datos.append("accion", accion);
		datos.append("idTurno", turno);
		//datos.append("caja", caja);
		datos.append("idApertura", idApertura);
		document.getElementById("btAbrir").visible = true;
		datos.append("fechaCierre", "");


	} else if (arqueo == 2 && !document.getElementById("txtTipo").disabled) {

		var accion = "buscar";
		datos.append("accion", accion);
		datos.append("idApertura", 0);
		datos.append("idTurno", 0);
		document.getElementById("btAbrir").visible = false;
		document.getElementById("btEjecutar").visible = true;
		datos.append("fechaCierre", "");

	}
	if (arqueo != 1 && document.getElementById("txtTipo").disabled) {
		var accion = "cierre";
		var fecha = $('#fechaCierre').val();
		datos.append("idApertura", 0);
		datos.append("idTurno", 0);
		document.getElementById("btAbrir").visible = false;
		document.getElementById("btEjecutar").visible = true;
		datos.append("accion", accion);
		datos.append("fechaCierre", fecha);
	}
	$.ajax({
		url: globalPath + "/buscar-apertura-terminal",
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
				$('#modalInicioCaja').modal({ backdrop: 'static', keyboard: false })

				$('#modalInicioCaja').modal('hide');
				if (arqueo == 1) { aperturaCajero = datos; }

				buscarVentasJs(datos.terminal.idAperturaCajero, datos, arqueo);


			} else {
				Swal.fire({
					icon: "error",
					text: datos.resultado,
				})
			}

		}
	});

}



function buscarVentasJs(idApertura, resultado, arqueo) {

	if (aperturaCajero != "") {
		resultado = aperturaCajero;
	}

	var datos = new FormData();

	var accion = "ventas";
	datos.append("accion", accion);
	datos.append("idApertura", idApertura);
	datos.append("idTurno", 0);


	$.ajax({
		url: globalPath + "/buscar-apertura-terminal",
		method: "POST",
		data: datos,
		chache: false,
		contentType: false,
		processData: false,
		dataType: "json",
		success: function(result) {

			if (result > 0 || arqueo == 1) {
				$.ajax({
					url: globalPath + "/buscar-apertura-terminal",
					method: "POST",
					data: datos,
					chache: false,
					contentType: false,
					processData: false,
					dataType: "json",
					success: function(respuesta) {

						document.querySelector('#_saldo_inicial').innerText = respuesta;
						$("#saldo_inicial").val(respuesta);

						$("#txtId_tipo_arqueo").val($("#txtTipo").val());
						$('#arqueoCaja').modal({ backdrop: 'static', keyboard: false })
						$('#arqueoCaja').modal('show');

					}
				});
			} else {
				mensajeWarning("No existen ventas !");
				/*Swal.fire({
					icon: "warning",
					text: "No existen ventas !"
				})*/
				//setTimeout(function() { location.reload(); }, 1505)
			}
		}
	});

}

function eliminarVentasAbiertas(idApertura) {

	var datos = new FormData();
	datos.append("accion", "deleteVentasAbiertas");
	datos.append("idApertura", idApertura);
	datos.append("idTurno", 0);


	$.ajax({
		url: globalPath + "/buscar-apertura-terminal",
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

function cerrarDia(){
	
	/*const swalWithBootstrapButtons = Swal.mixin({
		customClass: {
			confirmButton: 'btn btn-success',
			cancelButton: 'btn btn-danger'
		},
		buttonsStyling: false
	})

	swalWithBootstrapButtons.fire({
		title: 'Cerrar día',
		icon: 'error',
		showCancelButton: true,
		confirmButtonText: 'Si ',
		cancelButtonText: 'No',
		reverseButtons: true
	}).then((result) => {
		if (result.isConfirmed) {
			} else if (
			
			result.dismiss === Swal.DismissReason.cancel
		) {
			swalWithBootstrapButtons.fire(
				'Acción cancelada', '',
				'warning'
			)
		}
		
		})*/ 
		
		abrirModal('cerrarDia');
		document.getElementById("cerrarDia").style.display = "block";
		var cant = 5;
		for (var i = 1; i <= cant; i++) {
		
		 jQuery(document).ready(function(){
 
	jQuery('#AvanzaModal').on('hidden.bs.modal', function (e) {
	    jQuery(this).removeData('bs.modal');
	    jQuery(this).find('.modal-content').empty();
	})
 
    })

	}
}


