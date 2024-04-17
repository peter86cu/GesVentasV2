var registros = new Array();
let pos = 0;

class MiObjetoConArchivo {
	constructor(id, codigo, nombre, idmoneda, moneda, saldo, estado, usuario, factura) {
		this.id = id;
		this.codigo = codigo;
		this.nombre = nombre;
		this.idmoneda = idmoneda;
		this.moneda = moneda;
		this.saldo = saldo;
		this.estado = estado;
		this.usuario = usuario;
		this.factura = factura; // El atributo archivo es un objeto File
	}


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
		//$("#precio").val($('.productos').select2('data')[0].precio);

	})
});


function insertar() {


	var codigo = document.getElementById("idTipoGasto").value;

	var nombre = $('#gasto :selected').text();

	var idMoneda = document.getElementById("idMoneda").value;

	var moneda = $('#idMoneda :selected').text();

	var userId = document.getElementById("userId").value;

	var saldo = parseFloat(document.getElementById("inputValor").value);


	let objetoConArchivo = new MiObjetoConArchivo(pos, codigo, nombre, idMoneda, moneda, saldo, "PENDIENTE", userId, null);
	registros.push(objetoConArchivo);
	//registros.push({ 'codigo': codigo, 'nombre': nombre, 'moneda': moneda,'saldo': saldo,'estado':'PENDIENTE','usuario':userId});
	/*document.getElementById("txtCod").value="";
	document.getElementById("txtNom").value="";
	document.getElementById("txtNota").value="";*/
	pos++;
	mostrarTabla();

}

function mostrarTabla() {
	var id = 0;
	const tiempoTranscurrido = Date.now();
	const hoy = new Date(tiempoTranscurrido);

	for (var i = 0; i < registros.length; i++) {

		var tr2 = `<tr style="background: #ccffcc" id="fila` + i + `">
           <td style="width: 35%"><h5 id="id"><strong>`+ registros[i].nombre + `</strong></h5></td>
           <td style="width: 25%"><h5><strong>` + registros[i].moneda + `</strong></td>
           <td style="width: 10%"><h5><strong>` + registros[i].saldo + `</strong></td>
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
			for (let i = 0; i < registros.length; i++) {
				console.log(registros[i]);
				let ultimoValor = registros[0].factura;
				var datos = new FormData();
				//datos.append("gasto", JSON.stringify(registros[i]));
				datos.append("factura", ultimoValor);
				datos.append("tipogasto", registros[i].codigo);
				datos.append("moneda", registros[i].idmoneda);
				datos.append("saldo", registros[i].saldo);
				datos.append("estado", registros[i].estado);

				$.ajax({
					url: globalPath + "/add-gastos",
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

							$('#gastosNuevoModal').modal('hide');
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
								text: datos.error.menssage,

							})
						}

					}

				})



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

