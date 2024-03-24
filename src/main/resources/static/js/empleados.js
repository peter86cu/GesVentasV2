var accionCheck = "";
function check() {
	if ($('#ingreso').is(':checked')) {
		$("#labelFechaIngreso").show();
		$("#fecha").show();
		$("#labelFechaEgreso").hide();
		accionCheck = "ingreso";

		$("#ingreso").prop('checked', true);
	}
	else if ($('#egreso').is(':checked')) {
		$("#labelFechaIngreso").hide();
		$("#labelFechaEgreso").show();
		$("#fecha").show();
		$("#egreso").prop('checked', false);
		accionCheck = "egreso";
	}
}


$(document).ready(function() {
	$(".titulos").select2({
		dropdownParent: $('#ModalEditarEmpleado'),
		theme: 'bootstrap-5',
		ajax: {
			url: globalPath + "/buscar-titulo",
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
		var datos = $("#opcionTitulo").val();
		var modificar = "";
		/*		if (datos == "") {
					$("#opcionTitulo").val("ID: " + $('.titulos').select2('data')[0].id + " - TITULO: " + $('.titulos').select2('data')[0].text + ";");
		
		var tr2 = `<tr style="background: #ccffcc">
				   <td style="width: 5%"><h5><strong>`+ $('.titulos').select2('data')[0].id + `</strong></h5></td><td style="width: 40%"><h5><strong>` + $('.titulos').select2('data')[0].text + `</strong></td>
		<td style="width: 40%" align="center"><h5><strong><i class="fas fa-paperclip"></i></strong><td class='text-right'><a href="#" onclick="" ><img ` + modificar + ` src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAMAAAAoLQ9TAAAAeFBMVEUAAADnTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDx+VWpeAAAAJ3RSTlMAAQIFCAkPERQYGi40TVRVVlhZaHR8g4WPl5qdtb7Hys7R19rr7e97kMnEAAAAaklEQVQYV7XOSQKCMBQE0UpQwfkrSJwCKmDf/4YuVOIF7F29VQOA897xs50k1aknmnmfPRfvWptdBjOz29Vs46B6aFx/cEBIEAEIamhWc3EcIRKXhQj/hX47nGvt7x8o07ETANP2210OvABwcxH233o1TgAAAABJRU5ErkJggg==" ></a></td></tr>`;
		$("#cuerpo-items").append(tr2);
				
				} else {*/


		if ($('.titulos').select2('data').length > 0) {
			var id = $('.titulos').select2('data')[0].id;
			var valor = $('.titulos').select2('data')[0].text;
			var tr2 = `<tr style="background: #ccffcc" id="fila` + id + `">
           <td style="width: 5%"><h5 id="id"><strong>`+ id + `</strong></h5></td><td style="width: 10%"><h5><strong>` + valor + `</strong></td>
           <td style="width: 10%"><h5><strong> <a href="#" class="mailbox-attachment-name" id="fileSubido_`+ id + `"><i class="fas fa-paperclip"></i></a></strong></td>
           <td style="width: 80%"><strong><label for="file_`+ id + `"><i class="fas fa-cloud-download-alt"></i></label><input type="file" style="display:none"  name="file" id="file_` + id + `" class="fichero_` + id + `" onchange="cargarFichero(` + id + `)"/></strong></td>
           <td class='text-right'><a href="#" onclick="eliminarFila(`+ id + `)" ><img ` + modificar + ` src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAMAAAAoLQ9TAAAAeFBMVEUAAADnTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDx+VWpeAAAAJ3RSTlMAAQIFCAkPERQYGi40TVRVVlhZaHR8g4WPl5qdtb7Hys7R19rr7e97kMnEAAAAaklEQVQYV7XOSQKCMBQE0UpQwfkrSJwCKmDf/4YuVOIF7F29VQOA897xs50k1aknmnmfPRfvWptdBjOz29Vs46B6aFx/cEBIEAEIamhWc3EcIRKXhQj/hX47nGvt7x8o07ETANP2210OvABwcxH233o1TgAAAABJRU5ErkJggg==" ></a></td></tr>`;
			$("#cuerpo-items").append(tr2);
			$("#titulos").select2("val", "");
		}
		//	var nuevo = "ID: " + $('.titulos').select2('data')[0].id + " - TITULO: " + $('.titulos').select2('data')[0].text;
		//	$("#opcionTitulo").val(datos += '\n' + nuevo + ";");




	})
});

function tuFuncionEnArchivoJS(listaModelo) {
	return listaModelo;
}


function abrirModal(nombreModal) {
	$('#' + nombreModal).modal({ backdrop: 'static', keyboard: false })
	$('#' + nombreModal).modal('show');

}



function generarCalendarioEmpleados() {
	var mes = $("#mesAProcesar").val();


	if (mes == null ) {
		Swal.fire({
			icon: "error",
			text: "Debe selecionar el mes a procesar."
		})
	} else {

		var datos = new FormData();
		datos.append("mes", mes);
		datos.append("accion", "procesar");
		$.ajax({
			url: globalPath + "/generar-calendario-mes",
			method: "POST",
			data: datos,
			chache: false,
			contentType: false,
			processData: false,
			dataType: "json",
			success: function(respuesta) {
				var response = JSON.stringify(respuesta, null, '\t');
				var data = JSON.parse(response);
				if(data.code==200){
					$('#calendarioModal').modal('hide');
					Swal.fire({
						icon: "success",
						text: data.resultado
					})
				}else if(data.code==405){
					  Swal.fire({
        text: data.resultado,
        //type: "success",
        icon: "success",
        showCancelButton: true,
        confirmButtonText: "OK",
        cancelButtonText: "No",
        confirmButtonColor: "#3085d6",
        cancelButtonColor: "#d33",
    }).then((result) => {
        if (result.value) {
			var mes = $("#mesAProcesar").val();
           var datos = new FormData();
		datos.append("mes", mes);
		datos.append("accion", "reprocesar");
		$.ajax({
			url: globalPath + "/generar-calendario-mes",
			method: "POST",
			data: datos,
			chache: false,
			contentType: false,
			processData: false,
			dataType: "json",
			success: function(respuesta) {
				var response = JSON.stringify(respuesta, null, '\t');
				var data = JSON.parse(response);
				if(data.status){
					$('#calendarioModal').modal('hide');
					Swal.fire({
						icon: "warning",
						text: data.resultado
					})
				}else if(data.code==405){
					
					
					const swalWithBootstrapButtons = Swal.mixin({
		customClass: {
			confirmButton: 'btn btn-success',
			cancelButton: 'btn btn-danger'
		},
		buttonsStyling: false
	})

	swalWithBootstrapButtons.fire({
		title: data.resultad,

		icon: 'warning',
		showCancelButton: true,
		confirmButtonText: 'Si ',
		cancelButtonText: 'No',
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
							'Procesando!',
							data.resultado,
							'success'
						).then((result) => {
							if (result.isConfirmed)
								location.reload();
						});

					} else {
						Swal.fire({
							icon: 'error',
							text: data.error.menssage,
						})
					}
$('#calendarioModal').modal('hide');
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
		    }
		   })

        }else{
			
		}
    });
				}else if(!data.status){
					Swal.fire({
						icon: "error",
						text: data.resultado
					})
				}
		    }
		   })
		}
	}

function mesAProcesarMarcas() {
	var mes = $("#mes").val();
	var estado = $("#estado").val();
$('#marcasProcessEmpl > tbody').empty();
$('#cuerpo-items > tfoot').empty();
$('#cabeza-items > tfoot').empty();

	if (mes == null || estado == null) {
		Swal.fire({
			icon: "error",
			text: "Debe selecionar todos los campos."
		})
	} else {

		var datos = new FormData();
		datos.append("mes", mes);
		datos.append("estado", estado);


		document.addEventListener('DOMContentLoaded', function() {
			var listaElementos = document.getElementById('listaElementos');
			var listaModelo = JSON.parse(listaElementos.getAttribute('data-lista'));

			// Llama a tu función y pasa la lista como parámetro
			listaModeloTemp = tuFuncionEnArchivoJS(listaModelo);
		});



		$.ajax({
			url: globalPath + "/procesar-marcas",
			method: "POST",
			data: datos,
			chache: false,
			contentType: false,
			processData: false,
			dataType: "json",
			success: function(respuesta) {
				var response = JSON.stringify(respuesta, null, '\t');
				var data = JSON.parse(response);
				if (data.status) {
					$('#empleadost > tbody').empty();
					$('#cuerpo-items > tfoot').empty();
					$('#cabeza-items > tfoot').empty();

					// Obtén la referencia al elemento <thead>
					var thead = document.querySelector('thead');

					// Elimina todos los elementos hijos del <thead>
					while (thead.firstChild) {
						thead.removeChild(thead.firstChild);
					}

					if (data.code == 200) {
						$('#asistenciaModal').modal('hide');

						//Generar tabla con datos
						var encabezado = `<tr style="background: #778899">
										<th>NÚMERO EMPLEADO</th>
										<th>NOMBRE Y APELLIDOS</th> 
										<th>FECHA</th>
										<th>MARCA ENTRADA</th> 
										<th>MARCA SALIDA</th> 
										<th>ESTADO</th> 
										<th>PROCESO</th>
										<th>INPUT</th>
										<th>ACCIÓN</th></tr>`;

						$("#cabeza-items").append(encabezado);
						for (var i = 0; i < data.marcas.length; i++) {
							for (var j = 0; j < listaModelo.length; j++) {
								var nombre = "";
								var numero = 0;
								var idEmpleadoOK = "";
								if (listaModelo[j].idempleado == data.marcas[i].compositeId.idempleado) {
									nombre = listaModelo[j].nombre + " " + listaModelo[j].apellidos;
									numero = listaModelo[j].numeroempleado;
									$("#empleadoId").val(listaModelo[j].idempleado);
									idEmpleadoOK = listaModelo[j].idempleado;
									break;
								}
							}
							var marcaEntrada = "SIN DEFINIR";
							var marcaSalida = "SIN DEFINIR";

							if (data.marcas[i].marcasalida != null) {
								marcaSalida = data.marcas[i].marcasalida;
							} if (data.marcas[i].marcaentrada != null) {
								marcaEntrada = data.marcas[i].marcaentrada;
							}

							var tr2 = `<tr id="fila`+ i + `">
                                        <td style="width: 8%">`+ numero + `</td>
                                        <td style="width: 20%">` + nombre + `</td>
                                        <td style="width: 10%">` + data.marcas[i].compositeId.fecha + `</td>
                                        <td contenteditable="true" style="width: 15%">` + marcaEntrada + `</td>
                                        <td contenteditable="true" style="width: 15%">` + marcaSalida + `</td>
                                        <td style="width: 12%">` + data.marcas[i].estado + `</td>
                                        <td style="width: 10%">` + data.marcas[i].proceso + `</td>
                                        <td style="width: 10%">` + data.marcas[i].tipo + `</td>
                                        <td>
									<button class="btn btn-primary "
										onclick="gestionarMarcaEmpleado('`+ idEmpleadoOK + `','fila`+ i + `'); return false"
										style="color: #000000;background-color: #66CDAA;border-color: #20B2AA"><i class="fas fa-check-circle"></i></button></td><tr>`;


							$("#cuerpo-items").append(tr2);

						}
					} else {
						Swal.fire({
							icon: "error",
							text: data.resultado
						})
					}
				} else {
					Swal.fire({
						icon: "warning",
						text: data.error.menssage
					})
				}

			}
		})

	}




}


function mesAProcesarTodasMarcas() {
	var mes = $("#mesProceso").val();
	
	if (mes == null) {
		Swal.fire({
			icon: "error",
			text: "Debe selecionar el mes a procesar."
		})
	} else {

		var datos = new FormData();
		datos.append("mes", mes);
		
			$.ajax({
			url: globalPath + "/procesar-marcas-all",
			method: "POST",
			data: datos,
			chache: false,
			contentType: false,
			processData: false,
			dataType: "json",
			success: function(respuesta) {
				var response = JSON.stringify(respuesta, null, '\t');
				var data = JSON.parse(response);
				if (data.status) {
					Swal.fire({
						icon: "warning",
						text: data.resultado
					})
			
			  }
			}
		})

	}




}

function filterByNumeroEmpleado(){
		var mes = $("#mesMarca").val();
		var empleado = $("#numeroEmpleado").val();
		$('#marcasProcessEmpl > tbody').empty();
		$('#cuerpo-items > tfoot').empty();
		$('#cabeza-items > tfoot').empty();
	if (mes == null || empleado =="") {
		Swal.fire({
			icon: "error",
			text: "Debe completar los campos."
		})
	} else {
		var datos = new FormData();
		datos.append("mes", mes);
		datos.append("documento",empleado);
		$.ajax({
			url: globalPath + "/obtener-marcas-procesadas",
			method: "POST",
			data: datos,
			chache: false,
			contentType: false,
			processData: false,
			dataType: "json",
			success: function(respuesta) {
				var response = JSON.stringify(respuesta, null, '\t');
				var data = JSON.parse(response);
				if (data.status) {
					

					// Obtén la referencia al elemento <thead>
					var thead = document.querySelector('thead');

					// Elimina todos los elementos hijos del <thead>
					while (thead.firstChild) {
						thead.removeChild(thead.firstChild);
					}

					if (data.code == 200) {
						$('#marcasEmpleado').modal('hide');
 					const boton = document.getElementById("btnExportar");
 					 boton.removeAttribute('disabled');
						//Generar tabla con datos
						var encabezado = `<tr style="background: #778899">
										<th>DOCUMENTO</th>
										<th>NOMBRE Y APELLIDOS</th> 
										<th>FECHA</th>
										<th>ESTADO</th> 
										<th>HORAS</th> 
										<th>MINUTOS</th> 
										`;

						$("#cabeza-items").append(encabezado);
						for (var i = 0; i < data.process.length; i++) {
							
							var tr2 = `<tr>                                       
                                        <td style="width: 10%">` + data.process[i].documento + `</td>
                                        <td style="width: 15%">` + data.process[i].nombreEmpleado + `</td>
                                        <td style="width: 15%">` + data.process[i].fecha + `</td>
                                        <td style="width: 12%">` + data.process[i].estado + `</td>
                                        <td style="width: 10%">` + data.process[i].horas + `</td>
                                        <td style="width: 10%">` + data.process[i].minutos + `</td>
                                       <tr>`;


							$("#cuerpo-items").append(tr2);

						}
					} else {
						Swal.fire({
							icon: "error",
							text: data.resultado
						})
					}
				} else {
					Swal.fire({
						icon: "warning",
						text: data.error.menssage
					})
				}

			}
		})
		
		
}
}


function exportar(value){
	if(value==1){
		 $("#marcasProcessEmpl").table2excel({
      filename: "file.xls"
    });
    }
}


function exportTableToExcel(tableElement, filename) {
  const table = document.querySelector(tableElement);
  const tableData = [];

  // Extract table data
  table.querySelectorAll('tr').forEach(row => {
    const rowData = [];
    row.querySelectorAll('th, td').forEach(cell => {
      rowData.push(cell.textContent);
    });
    tableData.push(rowData);
  });

  // Create worksheet
  const worksheet = XLSX.utils.aoa_to_sheet(tableData);

  // Create workbook
  const workbook = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(workbook, worksheet, $("#mesMarca").val());

  // Export the workbook to Excel file
  XLSX.writeFile(workbook, $("#numeroEmpleado").val()+".xlsx");
}

function gestionarMarcaEmpleado(idEmpleado, fila) {

    let obtenerDato = document.getElementsByTagName("th");
  
    
    let obtenerFila = document.getElementById(fila);

// Obtenemos los elementos td de la fila
let elementosFila = obtenerFila.getElementsByTagName("td");

// Mostramos la colección HTML de la fila.

	var entrada = elementosFila[3].innerHTML
	var salida = elementosFila[4].innerHTML
					$('#empleadost > tbody').empty();
					$('#cuerpo-items > tfoot').empty();
					$('#cabeza-items > tfoot').empty();
	if (entrada == "" || salida == "" || entrada=="SIN DEFINIR" || salida=="SIN DEFINIR") {
		Swal.fire({
			icon: "warning",
			text: "Debe entrar una Hora de entrada o de Salida valida."
		})
	} else {
		var datos = new FormData();
		datos.append("idEmpleado", idEmpleado);
		datos.append("entrada", entrada);
		datos.append("salida", salida);
		$.ajax({
			url: globalPath + "/procesar-marcas-empleado",
			method: "POST",
			data: datos,
			chache: false,
			contentType: false,
			processData: false,
			dataType: "json",
			success: function(respuesta) {
				var response = JSON.stringify(respuesta, null, '\t');
				var data = JSON.parse(response);
				if (data.status) {					
					mensajeOK(data.resultado)
				}else if(data.code==202){
					mensajeHoraExtra(data.resultado);
				}else if(!data.status){
					mensajeErrorSinActPagina(data.error.menssage);
				}
			}
		})
	}




}

function procesarCalendarioPorEmpleado() {
	var inputNumeroEmpleado = $("#numeroEmpleado").val();

	var datos = new FormData();
	datos.append("numero", inputNumeroEmpleado);
          $('#empleadost > tbody').empty();
					$('#cuerpo-items > tfoot').empty();
					$('#cabeza-items > tfoot').empty();
	$.ajax({
		url: globalPath + "/filtrar-calendario",
		method: "POST",
		data: datos,
		chache: false,
		contentType: false,
		processData: false,
		dataType: "json",
		success: function(respuesta) {
			var response = JSON.stringify(respuesta, null, '\t');
			var data = JSON.parse(response);
			if (data.code == 200) { }
		}
	})


}

function eliminarFila(index) {
	$("#fila" + index).remove();
	$("#data").load(" #data");
	$("#titulos").select2("val", "");
}

function cargarFichero(id) {
	/* const fileSelector = document.getElementById('file');
	  fileSelector.addEventListener('change', (event) => {
		const fileList = event.target.files;
		console.log(fileList);
	  })	*/
	var imagen = $('.fichero_' + id)[0].files[0];
	if (imagen.type && !imagen.type.startsWith('image/')) {
		console.log('File is not an image.', imagen.type, file);
		return;
	}

	if (imagen != "") {
		var TmpPath = URL.createObjectURL(imagen);
		$('#fileSubido_' + id).prop('href', TmpPath);
		var parametro = 'fileSubido_' + id;
		document.getElementById(parametro).innerHTML = imagen.name;
	} else {
		Swal.fire({
			icon: "error",
			text: "No ha seleccionado un archivo valido."
		})
	}



}

function guardarEmpleadoFinal() {

	var depa = $("#departamento").val();
	var cargo = $("#cargo").val();
	var horario = $("#horario").val();
	var idEmpleado = $("#idEmpleado").val();
	var salario = $("#salario").val();

	if (depa == "" || cargo == "" || horario == "" || salario == "") {
		Swal.fire({
			icon: "error",
			text: "Debe completar todos los campos."
		})
	} else {
		var datos = new FormData();
		datos.append("depa", depa);
		datos.append("cargo", cargo);
		datos.append("horario", horario);
		datos.append("idEmpleado", idEmpleado);
		datos.append("salarioNominal", salario);


		$.ajax({
			url: globalPath + "/guardar-empleado",
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
					$('#ModalEditarEmpleado').modal('hide');

					Swal.fire({
						icon: "success",
						text: data.resultado
					})
				} else {
					Swal.fire({
						icon: "error",
						text: data.resultado
					})
				}
			}
		})


	}

}

function cambiarPassword() {

	var vieja = $("#currentPassword").val();
	var nueva = $("#newPassword").val();
	var renueva = $("#renewPassword").val();

	if (vieja == "" || nueva == "" || renueva == "") {
		Swal.fire({
			icon: "error",
			text: "Debes completar todos los campos."
		})
	} else if (vieja == nueva) {
		Swal.fire({
			icon: "error",
			text: "La clave nueva no puede ser igual que la anterior."
		})
	} else {
		var datos = new FormData();
		datos.append("vieja", vieja);
		datos.append("nueva", nueva);
		datos.append("renueva", renueva);
		$.ajax({
			url: globalPath + "/cambiar-password",
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
		})
	}




}


function marcarEntraSalida() {

	var entrada = false;
	var salida = false;

	var isDisabledEntrada = $('#entrada').prop('disabled');
	var isDisabledSalida = $('#salida').prop('disabled');

	if ($('#entrada').is(':checked') && !isDisabledEntrada) {
		entrada = true;
	} else if ($('#salida').is(':checked') && !isDisabledSalida) {
		salida = true;
	} else {
		Swal.fire({
			icon: "error",
			text: "Debe seleccionar una marca."
		})
	}

	if (entrada || salida) {
		var datos = new FormData();
		datos.append("marcaEntrada", entrada);
		datos.append("marcaSalida", salida);

		$.ajax({
			url: globalPath + "/marca-empleado",
			method: "POST",
			data: datos,
			chache: false,
			contentType: false,
			processData: false,
			dataType: "json",
			success: function(respuesta) {
				var response = JSON.stringify(respuesta, null, '\t');
				var data = JSON.parse(response);
				if (data.status) {

					Swal.fire({
						position: "top-end",
						icon: "success",
						title: data.resultado,
						showConfirmButton: false,
						timer: 1500
					})
					$('#entrada').attr('disabled');

					setTimeout(function() { location.reload(); }, 1505)



				} else {
					Swal.fire({
						icon: "error",
						text: data.error.menssage
					})
				}
			}
		})


	}

}

function agregaNuevoEmpleado(event) {

	//Obtengo los valores
	//var idProducto=  $(this).attr("idProducto");
	var nombre = $("#nombre").val();
	var apellidos = $('#apellidos').val();
	var tipoDoc = $('#tipoDoc').val();
	var sexo = $('#sexo').val();
	var documento = $('#documento').val();
	var fecha = $('#fecha').val();
	var fechaNac = $('#fechaNac').val();
	var paisDoc = $('#paisDoc').val();
	var paisResidencia = $('#paisResidencia').val();
	var address = $('#address').val();
	var phone = $('#phone').val();
	var email = $('#email').val();


	if (nombre == "" || apellidos == "" || tipoDoc == "" || sexo == "" || documento == "" || fecha == "" || fechaNac == "" || paisDoc == ""
		|| paisResidencia == "" || address == "" || phone == "" || email == "") {
		Swal.fire({
			icon: "error",
			text: "Debe completar todos los campos para continuar."
		})


	} else {

		var datos = new FormData();

		var accion = "insert";
		var imagen = "";
		imagen = $('#foto1')[0].files[0];
		if (imagen == "") {
			imagen = "SinImagen";
		}
		datos.append("accion", accion);
		datos.append("nombre", nombre);
		datos.append("apellidos", apellidos);
		datos.append("tipoDoc", tipoDoc);
		datos.append("documento", documento);
		datos.append("fecha", fecha);
		datos.append("fechaNac", fechaNac);
		datos.append("accionCheck", accionCheck);
		datos.append("sexo", sexo);
		datos.append("paisDoc", paisDoc);
		datos.append("paisResidencia", paisResidencia);
		datos.append("address", address);
		datos.append("phone", phone);
		datos.append("email", email);
		datos.append('foto', imagen);

		$.ajax({
			url: globalPath + "/addEmpleado",
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


					$("#profile-edit").removeClass("active");
					$("#profile-edit-div").removeClass("show active");

					var div = document.getElementById("profile-profesion");
					div.className += " active";

					var div2 = document.getElementById("profile-profesion-div");
					div2.className += " show active";

					$('#idEmpleado').val(data.resultado)


					/*	$('#ModalAddEmpleado').modal('hide');
						Swal.fire({
							position: "top-end",
							icon: "success",
							title: data.resultado,
							showConfirmButton: false,
							timer: 1500
						})
		
						setTimeout(function() { location.reload(); }, 1505)*/
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

function next(pos) {

	if (pos == 1) {

		$("#profile-profesion").removeClass("active");
		$("#profile-profesion-div").removeClass("show active");

		var div = document.getElementById("profile-edit");
		div.className += " active";

		var div2 = document.getElementById("profile-edit-div");
		div2.className += " show active";


		//$("#profile-edit").removeClass("active");
		//$("#profile-edit-div").removeClass("show active");


	} else if (pos == 2) {
		$("#profile-salud").removeClass("active");
		$("#profile-salud-div").removeClass("show active");

		var div = document.getElementById("profile-profesion");
		div.className += " active";

		var div2 = document.getElementById("profile-profesion-div");
		div2.className += " show active";
	} else if (pos == 3) {
		$("#profile-banco").removeClass("active");
		$("#profile-banco-div").removeClass("show active");

		var div = document.getElementById("profile-salud");
		div.className += " active";

		var div2 = document.getElementById("profile-salud-div");
		div2.className += " show active";
	} else if (pos == 4) {
		$("#profile-cargo").removeClass("active");
		$("#profile-cargo-div").removeClass("show active");

		var div = document.getElementById("profile-banco");
		div.className += " active";

		var div2 = document.getElementById("profile-banco-div");
		div2.className += " show active";
	}

}


function guardarDatosSalud() {
	var idEmpleado = $("#idEmpleado").val();
	var nombreContacto = $("#nombreContacto").val();

	var contacto = $("#numeroContacto").val();
	var vencimiento = $("#vencimiento").val();


	if (contacto = "" || vencimiento == "" || nombreContacto == "") {
		Swal.fire({
			icon: "error",
			text: "Debe completar los campos."
		})
	} else {
		var datos = new FormData();
		datos.append("idEmpleado", idEmpleado);
		datos.append("contacto", contacto);
		datos.append("nombreContacto", nombreContacto);
		datos.append("vencimiento", vencimiento);

		$.ajax({
			url: globalPath + "/datos-salud-empleado",
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

					$("#profile-salud").removeClass("active");
					$("#profile-salud-div").removeClass("show active");



					var div = document.getElementById("profile-banco");
					div.className += " active";

					var div2 = document.getElementById("profile-banco-div");
					div2.className += " show active";

					$('#idEmpleado').val(data.resultado)
				}
			}
		})
	}



}

function guardarDatosBanco() {
	var idEmpleado = $("#idEmpleado").val();
	let banco = $("#bancoId").val();
	var cuenta = $("#numeroCuenta").val();

	let formaCobro = $("#formaCobro").val();
	let moneda = $("#monedaId").val();


	if (banco == "" || cuenta == "" || formaCobro == "" || moneda == "") {
		Swal.fire({
			icon: "error",
			text: "Debe completar los campos."
		})
	} else {
		var datos = new FormData();
		datos.append("idEmpleado", idEmpleado);
		datos.append("banco", Math.floor(banco));
		datos.append("cuenta", cuenta);
		datos.append("formaCobro", Math.floor(formaCobro));
		datos.append("moneda", Math.floor(moneda));


		$.ajax({
			url: globalPath + "/datos-banco-empleado",
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

					$("#profile-banco").removeClass("active");
					$("#profile-banco-div").removeClass("show active");



					var div = document.getElementById("profile-cargo");
					div.className += " active";

					var div2 = document.getElementById("profile-cargo-div");
					div2.className += " show active";

					$('#idEmpleado').val(data.resultado)
				}
			}
		})
	}



}

function guardarDatosPersonales() {

	var trabajos = $("#opcionTrabajos").val();
	var idEmpleado = $("#idEmpleado").val();
	var imagen = "";
	try {
		var resume_table = document.getElementById("g-table");
		var pos = 0;
		var titulo = "";
		var existe = false;


		for (var i = 0, row; row = resume_table.rows[i]; i++) {
			//alert(cell[i].innerText);
			for (var j = 0, col; col = row.cells[j]; j++) {
				//alert(col[j].innerText);
				//console.log(`Txt: ${col.innerText} \tFila: ${i} \t Celda: ${j}`);
				if (j == 0 && i >= 2) {
					pos = col.innerText;

				}
				if (j == 1 && i >= 2) {
					titulo = col.innerText;
				}
				if (i >= 2) {
					console.log(`Txt: ${col.innerText} \tFila: ${i} \t Celda: ${j}`);
					if (j == 2) {

						imagen = $('#file_' + pos)[0].files[0];
						if (imagen == null) {
							Swal.fire({
								icon: "error",
								text: "Debe cargar el archivo al título: " + titulo
							})
							return
						} else {
							existe = true;
							var datos = new FormData();
							datos.append("archivo", imagen);
							datos.append("trabajos", trabajos);
							datos.append("idTitulo", pos);
							datos.append("idEmpleado", idEmpleado);

							$.ajax({
								url: globalPath + "/titulo-empleado",
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

										$("#profile-edit").removeClass("active");
										$("#profile-edit-div").removeClass("show active");

										$("#profile-profesion").removeClass("active");
										$("#profile-profesion-div").removeClass("show active");

										var div = document.getElementById("profile-salud");
										div.className += " active";

										var div2 = document.getElementById("profile-salud-div");
										div2.className += " show active";

										$('#idEmpleado').val(data.resultado)
									}
								}
							})


						}

					}
				}
			}
			/*	if (existe == false) {
					Swal.fire({
						icon: "error",
						text: "Debe seleccionar al menos un título."
					})
				}*/
		}
	}
	catch (error) {
		Swal.fire({
			icon: "error",
			text: console.error(error)
		})
	}



	/*if (titulos == "") {
		Swal.fire({
			icon: "error",
			text: "Debe seleccionar al menos un título."
		})
	} else if (imagen == "") {
		Swal.fire({
			icon: "error",
			text: "Debe seleccionar al menos un título."
		})
	} else {


		var datos = new FormData();
		datos.append("titulos", titulos);
		datos.append("trabajos", trabajos);
		datos.append("idEmpleado", idEmpleado);

		$.ajax({
			url: globalPath + "/datos-personales-empleado",
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

				}
			}
		})


	}*/





}


function agregaTrabajos() {

	//Obtengo los valores
	//var idProducto=  $(this).attr("idProducto");
	var trabajo = $("#trabajo").val();
	var tiempo = $('#tiempo').val();



	var datos = $("#opcionTrabajos").val();
	if (datos == "") {
		$("#opcionTrabajos").val("TRABAJO: " + trabajo + " - TIEMPO: " + tiempo);

	} else {
		var nuevo = "TRABAJO: " + trabajo + " - TIEMPO: " + tiempo;
		$("#opcionTrabajos").val(datos += '\n' + nuevo);
	}

}

function editarEmpleado(idEmpleado) {

	var datos = new FormData();

	datos.append("idEmpleado", idEmpleado,);

	$.ajax({
		url: globalPath + "/editar-empleado",
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

				$("#nombre").val(data.empleados.nombre);
				$("#apellidos").val(data.empleados.apellidos);
				$("#tipoDoc > option[value=" + data.empleados.idtipodocumento + "]").attr("selected", true);
				$("#documento").val(data.empleados.apellidos);
				$("#fecha").val(data.empleados.fechanacimiento);
				$('#ModalAddEmpleado').modal('show');
			} else {
				Swal.fire({
					icon: "error",
					text: data.resultado
				})
			}

		}

	})

}







//CREACION DEL PAGINADO
$(document).ready(function() {
	$("#searchempleados").keyup(function() {
		_this = this;
		// Show only matching TR, hide rest of them
		$.each($("#empleadost tbody tr"), function() {
			if ($(this).text().toLowerCase().indexOf($(_this).val().toLowerCase()) === -1)
				$(this).hide();
			else
				$(this).show();
		});
	});
});




//Aqui va el paginado


$(document).ready(function() {

	addPagerToTables('#empleadost', 8);

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