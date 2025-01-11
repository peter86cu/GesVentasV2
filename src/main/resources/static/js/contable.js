var registros = new Array();
let pos = 0;

class MiObjetoConArchivo {
	constructor(id,centrocosto,entradasalida, codigo, nombre, idmoneda, moneda, saldo, estado, usuario, factura, type,iva,fecha) {
		this.id = id;
		this.centrocosto=centrocosto;
		this.entradasalida=entradasalida;
		this.codigo = codigo;
		this.nombre = nombre;
		this.idmoneda = idmoneda;
		this.moneda = moneda;
		this.saldo = saldo;
		this.estado = estado;
		this.usuario = usuario;
		this.factura = factura; // El atributo archivo es un objeto File
		this.type = type;
		this.iva=iva;
		this.fecha=fecha;
	}


}


// Datos de ejemplo (simulando los datos de contabilidad)
const datosContabilidad = [
    { tipo: 'SALARIO IVAN', saldo: 18732, entradaSalida: 'EGRESO' },
    { tipo: 'INTERNET', saldo: 1600, entradaSalida: 'EGRESO' },
    { tipo: 'FACTURA ABITAB', saldo: 141.52, entradaSalida: 'INGRESO' },
    { tipo: 'PUBLICACIONES EN R.S', saldo: 2000, entradaSalida: 'EGRESO' },
    { tipo: 'DGI', saldo: 17000, entradaSalida: 'EGRESO' },
    { tipo: 'BPS', saldo: 17000, entradaSalida: 'EGRESO' },
    { tipo: 'LICENCIA MICROSOFT', saldo: 4073, entradaSalida: 'EGRESO' },
];

// Función para calcular el saldo total basado en 'EGRESO' o 'INGRESO'
function calcularContabilidad() {
    return datosContabilidad
        .filter(item => item.entradaSalida === tipoOperacion)
        .reduce((acc, item) => acc + item.saldo, 0);
}


$(document).ready(function() {
	$(".gastos").select2({
		dropdownParent: $('#gastosNuevoModal'),
		theme: 'bootstrap-5',
		ajax: {
			url: globalPath + "/buscar-gastos",
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

		$("#idTipoGasto").val($('.gastos').select2('data')[0].id);
		$("#tipoGasto").val($('.gastos').select2('data')[0].type);
		//$("#precio").val($('.productos').select2('data')[0].precio);

	})
});





$(document).ready(function() {
	$(".centrocosto").select2({
		dropdownParent: $('#gastosNuevoModal'),
		theme: 'bootstrap-5',
		ajax: {
			url: globalPath + "/buscar-centro-costo",
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

		$("#codigoCC").val($('.centrocosto').select2('data')[0].type);
		$("#centroc").val($('.centrocosto').select2('data')[0].id);
		//$("#precio").val($('.productos').select2('data')[0].precio);

	})
});


function insertar() {

	

	var codigo = document.getElementById("idTipoGasto").value;
	
	var centroCosto = document.getElementById("codigoCC").value;

	var type = document.getElementById("tipoGasto").value;

	var nombre = $('#gasto :selected').text();

	var idMoneda = document.getElementById("idMoneda").value;

	var moneda = $('#idMoneda :selected').text();

	var userId = document.getElementById("userId").value;

	var saldo = parseFloat(document.getElementById("inputValor").value);

	var checkbox = document.getElementById("iva");
	
	var entradasalida = document.getElementById("estado").value;
	
	var fecha = $('#datepicker').val();
	
	var iva=0;
	if (checkbox.checked) {
		iva=1;
		} 

var ciclo=true;

	if(centroCosto==""){
		ciclo=false;
		Swal.fire({
			icon: "warning",
			text: "No ha seleccionado un centro de costo valido"
		})
	}else if(type==""){
	ciclo=false;
		Swal.fire({
			icon: "warning",
			text: "No ha seleccionado un gasto o ingreso valido"
		})	
	}else if(entradasalida==""){
	ciclo=false;
		Swal.fire({
			icon: "warning",
			text: "No ha seleccionado si es un ingreso o egreso"
		})	
	}else if(idMoneda==""){
	ciclo=false;
		Swal.fire({
			icon: "warning",
			text: "No ha seleccionado la moneda"
		})	
	}else if(saldo==""){
	ciclo=false;
		Swal.fire({
			icon: "warning",
			text: "Debe esntrar un saldo valido"
		})	
	}
	
	if(ciclo){
		let objetoConArchivo = new MiObjetoConArchivo(pos,centroCosto, entradasalida,codigo, nombre, idMoneda, moneda, saldo, "PENDIENTE", userId, null, type,iva,fecha);
	registros.push(objetoConArchivo);
	//registros.push({ 'codigo': codigo, 'nombre': nombre, 'moneda': moneda,'saldo': saldo,'estado':'PENDIENTE','usuario':userId});
	document.getElementById("centroc").value="";
	document.getElementById("gasto").value="";
	document.getElementById("idMoneda").value="Seleccionar";
	document.getElementById("inputValor").value="";
	document.getElementById("estado").value="Seleccionar";
	pos++;
	mostrarTabla();
	}

	

}

function mostrarTabla() {
	var id = 0;
	const tiempoTranscurrido = Date.now();
	const hoy = new Date(tiempoTranscurrido);

	for (var i = 0; i < registros.length; i++) {

		var tr2 = `<tr style="background: #ccffcc" id="fila` + i + `">
		   <td style="width: 5%"><h5><strong>`+ registros[i].centrocosto + `</strong></h5></td>
           <td style="width: 25%"><h5 id="id"><strong>`+ registros[i].nombre + `</strong></h5></td>
           <td style="width: 7%"><h5 id="id"><strong>`+ registros[i].entradasalida + `</strong></h5></td>
           <td style="width: 15%"><h5><strong>` + registros[i].type + `</strong></td>
           <td style="width: 25%"><h5><strong>` + registros[i].moneda + `</strong></td>
           <td style="width: 8%"><h5><strong>` + registros[i].saldo + `</strong></td>
            <td style="width: 10%"><h5><strong> <a href="#" class="mailbox-attachment-name" id="fileSubido_`+ i + `"><i class="fas fa-paperclip"></i></a></strong></td>

           <td style="width: 5%"><strong><label for="file_`+ i + `"><i class="fas fa-cloud-download-alt"></i></label><input type="file" style="display:none"  name="file" id="file_` + i + `" class="fichero_` + i + `" onchange="cargarFichero(` + i + `)"/></strong></td>
           
           <td style="width: 5%" class='text-right'><a href="#" onclick="eliminarFila(`+ i + `)" ><img  src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAMAAAAoLQ9TAAAAeFBMVEUAAADnTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDx+VWpeAAAAJ3RSTlMAAQIFCAkPERQYGi40TVRVVlhZaHR8g4WPl5qdtb7Hys7R19rr7e97kMnEAAAAaklEQVQYV7XOSQKCMBQE0UpQwfkrSJwCKmDf/4YuVOIF7F29VQOA897xs50k1aknmnmfPRfvWptdBjOz29Vs46B6aFx/cEBIEAEIamhWc3EcIRKXhQj/hX47nGvt7x8o07ETANP2210OvABwcxH233o1TgAAAABJRU5ErkJggg==" ></a></td>

           </tr>`;


		//hoy.toLocaleDateString()
		/*celda.appendChild(document.createTextNode(registros[i].codigo));
		celda.appendChild(document.createTextNode(registros[i].nombre));
		celda.appendChild(document.createTextNode(registros[i].nombre));*/

	}

	$("#cuerpo-items").append(tr2);

}

function actualizarValorPorId(lista, id, nuevoValor) {
	lista.forEach(objeto => {
		if (objeto.id === id) {
			objeto.factura = nuevoValor;
		}
	});
}

function cargarFichero(id) {
	/* const fileSelector = document.getElementById('file');
	  fileSelector.addEventListener('change', (event) => {
		const fileList = event.target.files;
		console.log(fileList);
	  })	*/

	var imagen = $('.fichero_' + id)[0].files[0];
	/*if (imagen.type && (!imagen.type.startsWith('image/*') || !imagen.type.startsWith('application/pdf') )) {
		console.log('File is not an image.', imagen.type, file);
		return;
	}*/

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
	//let archivo = new File(imagen, imagen.name, { type: imagen.type });
	//let blob = new Blob([imagen], { type: imagen.type });
	let archivo = new File([imagen], imagen.name, { type: imagen.type });

	actualizarValorPorId(registros, id, archivo);


}


function guardarGastos() {


	try {
		if (registros.length > 0) {
			var guardadoOK = "";
			var ruta = "";

			for (let i = 0; i < registros.length; i++) {
				var datos = new FormData();
				if (registros[i].type === "GASTO INDIRECTO") {

					if (registros[i].factura == null) {
						Swal.fire({
							icon: "warning",
							text: "Debe cargar la factura asociada al gasto indirecto"
						})
						break;

					} else {
						datos.append("factura", registros[i].factura);
						ruta = "/add-gastos";
					}




				} else {
					registros[i].estado = "MENSUAL";
					ruta = "/add-costo";
				}
				
				datos.append("cc", registros[i].centrocosto);
				datos.append("iva", registros[i].iva);
				datos.append("es", registros[i].entradasalida);
				datos.append("idtipogasto", registros[i].codigo);
				datos.append("moneda", registros[i].idmoneda);
				datos.append("tipo", registros[i].nombre);
				datos.append("saldo", registros[i].saldo);                                                             
				datos.append("estado", registros[i].estado);
				datos.append("fecha", registros[i].fecha);

				$.ajax({
					url: globalPath + ruta,
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
							guardadoOK = "OK";
							$('#gastosNuevoModal').modal('hide');
							Swal.fire({
								position: "top-end",
								icon: "success",
								title: data.resultado,
								showConfirmButton: false,
								timer: 1500
							})
							setTimeout(function() { location.reload(); }, 1505)

						} else {
							guardadoOK = "NOK";
							Swal.fire({
								icon: "error",
								text: datos.error.menssage,

							})
						}

					}

				})



			}

			if (guardadoOK === "OK") {
				$('#gastosNuevoModal').modal('hide');
				Swal.fire({
					position: "top-end",
					icon: "success",
					title: data.resultado,
					showConfirmButton: false,
					timer: 1500
				})
				setTimeout(function() { location.reload(); }, 1505)
			}



		}


	} catch (error) {
		Swal.fire({
			icon: "error",
			text: console.error(error)
		})
	}




	pos = 0;

}

function obtenerCostosDirectos() {

	var mes = $("#mes").val();
	$('#h-table-costo > tbody').empty();
	$('#cuerpo-items > tfoot').empty();
	$('#cabeza-items > tfoot').empty();

	if (mes == null) {
		Swal.fire({
			icon: "error",
			text: "Debe selecionar el mes a consultar."
		})
	} else {

		var datos = new FormData();
		datos.append("mes", mes);




		$.ajax({
			url: globalPath + "/costos-directos",
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

					$('#h-table-costo > tbody').empty();
					$('#cuerpo-items-costo > tfoot').empty();

					// Obtén la referencia al elemento <thead>
					var thead = document.querySelector('thead');

					// Elimina todos los elementos hijos del <thead>
					while (thead.firstChild) {
						thead.removeChild(thead.firstChild);
					}
					var totalDiaMes = 0;
					var totalHorasDias = 0;
					var costoHora = 0;
					if (data.code == 200) {
						cerrarModal('filtroCostoModal');
						abrirModal("calculoUtilidadesModal")
						//$('#calculoUtilidadesModal').modal('show');


						for (var i = 0; i < data.libroDiario.length; i++) {

							var tr2 = `<tr id="fila` + i + `">
                                        <td style="width: 50%">`+ data.libroDiario[i].tipo + `</td>
                                        <td style="width: 10%">` + data.libroDiario[i].saldo + `</td>
                                        <td contenteditable="true" style="width: 15%">` + totalDiaMes + `</td>
                                        <td contenteditable="true" style="width: 15%">` + totalHorasDias + `</td>
                                        <td style="width: 8%">` + costoHora + `</td>
                                        
                                        <td>
									<button class="btn btn-primary "
										onclick="calculoCosto('`+ data.libroDiario[i].saldo + `','fila` + i + `'); return false"
										style="color: #000000;background-color: #66CDAA;border-color: #20B2AA"><i class="fas fa-check-circle"></i></button>
										<a href="#" onclick="eliminarFila(`+ i + `)" ><img  src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAMAAAAoLQ9TAAAAeFBMVEUAAADnTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDx+VWpeAAAAJ3RSTlMAAQIFCAkPERQYGi40TVRVVlhZaHR8g4WPl5qdtb7Hys7R19rr7e97kMnEAAAAaklEQVQYV7XOSQKCMBQE0UpQwfkrSJwCKmDf/4YuVOIF7F29VQOA897xs50k1aknmnmfPRfvWptdBjOz29Vs46B6aFx/cEBIEAEIamhWc3EcIRKXhQj/hX47nGvt7x8o07ETANP2210OvABwcxH233o1TgAAAABJRU5ErkJggg==" ></a>
									</td><tr>`;


							$("#cuerpo-items-costo").append(tr2);

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

function calculoCosto(saldo, fila) {


	let obtenerFila = document.getElementById(fila);

	// Obtenemos los elementos td de la fila
	let elementosFila = obtenerFila.getElementsByTagName("td");

	// Mostramos la colección HTML de la fila.

	let diaMes = elementosFila[2].innerHTML
	let horasDias = elementosFila[3].innerHTML

	let resultado = saldo / diaMes / horasDias;

	let numeroEntero = parseInt(resultado);

	elementosFila[4].innerHTML = numeroEntero;

	//obtenerFila.style.backgroundColor = "0C6A35";
	//obtenerFila.style.color = "#333";



}






function calcularCostoHH() {

	var suma = 0;
	// Obtener referencia a la tabla
	var tabla = document.getElementById("h-table-costo");

	// Iterar sobre cada fila de la tabla
	for (var i = 1; i < tabla.rows.length; i++) {
		// Obtener referencia a la fila actual
		var fila = tabla.rows[i];

		// Iterar sobre cada celda de la fila
		for (var j = 0; j < fila.cells.length; j++) {
			// Obtener referencia a la celda actual
			var celda = fila.cells[j];

			if (j == 4) {
				var contenido = celda.innerHTML;
				suma += parseInt(contenido);
			}


			// Hacer algo con el contenido de la celda, como imprimirlo en la consola
			console.log("Contenido de la celda:", contenido);
		}

	}
	document.getElementById("calculoHH").innerHTML = suma;
	if (suma > 0) {
		var boton = document.getElementById("btGuardar");
		boton.disabled = false;
	}

}


function guardarCalculo() {
	var datos = new FormData();
	var calculo = document.getElementById("calculoHH").innerHTML;
	var mes = $("#mes").val();
	datos.append("calculo", calculo);
	datos.append("fecha", mes);
	$.ajax({
		url: globalPath + "/calculo-costos",
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
				$('#calculoUtilidadesModal').modal('hide');
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
					text: data.error.menssage,

				})
			}

		}

	})

}


function guardarCentroCosto() {
	var datos = new FormData();
	var codigo = $("#codigo").val();
	var descripcion = $("#descripcion").val();
	var estado = $("#estado").val();
	
	datos.append("codigo", codigo);
	datos.append("descripcion", descripcion);
	datos.append("estado", estado);


	$.ajax({
		url: globalPath + "/add-centro-costo",
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
				$('#calculoUtilidadesModal').modal('hide');
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
					text: datos.error.menssage,

				})
			}

		}

	})

}

