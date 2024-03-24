var registros = new Array();



$(document).ready(function() {
	$(".gastos").select2({
		dropdownParent: $('#gastosNuevoModal .modal-body'),
		theme: 'bootstrap-5',
		ajax: {
			url: globalPath+"/buscar-gastos",
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


	function insertar(){
 
 
		var codigo=document.getElementById("idTipoGasto").value;
 
		var nombre=$('#gasto :selected').text();
		
		var idMoneda=document.getElementById("idMoneda").value;
		
		var moneda=$('#idMoneda :selected').text();
		
		var userId=document.getElementById("userId").value;
 
		var saldo=parseFloat(document.getElementById("inputValor").value);
 
 
		registros.push({ 'codigo': codigo, 'nombre': nombre, 'moneda': moneda,'saldo': saldo,'estado':'PENDIENTE','usuario':userId});
		/*document.getElementById("txtCod").value="";
		document.getElementById("txtNom").value="";
		document.getElementById("txtNota").value="";*/
 
		mostrarTabla();
 
	}
	
function mostrarTabla(){
 var id=0;
const tiempoTranscurrido = Date.now();
const hoy = new Date(tiempoTranscurrido);

	for(var i=0;i<registros.length;i++){
 
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



}


function guardarGastos() {

	
	var archivo = "";
	try {
		var resume_table = document.getElementById("h-table");
		var existe = false;
		var gasto="";
		var moneda=""
		var saldo=0;
		var gastosArray= new Array();
		var pos=0;


		for (var i = 0, row; row = resume_table.rows[i]; i++) {
			//alert(cell[i].innerText);
			for (var j = 0, col; col = row.cells[j]; j++) {
				//alert(col[j].innerText);
				//console.log(`Txt: ${col.innerText} \tFila: ${i} \t Celda: ${j}`);
				if (j == 0 && i >= 1) {
					gasto = col.innerText;

				}
				if (j == 1 && i >= 1) {
					moneda = col.innerText;
				}
				if (j == 2 && i >= 1) {
					saldo = col.innerText;
				}
				if (j == 3 && i >= 1) {
					console.log(`Txt: ${col.innerText} \tFila: ${i} \t Celda: ${j}`);
					archivo = $('.fichero_' + pos)[0].files[0];
						if (archivo == null) {
							Swal.fire({
								icon: "error",
								text: "Debe cargar la factura para continuar."
							})
							return
					        }else{
								existe = true;
							}
							pos++;						
						}
						
						if(existe){
							

					var objJavaScript = {
					    tipogasto: gasto,
					    moneda: moneda,
					    saldo: saldo,
					    factura: new Blob([JSON.stringify(archivo)],{type:'multipart/form-data'}),
					    estado: "PENDIENTE"
					}
						gastosArray.push(objJavaScript);
						existe=false;
					}
						
					}
					
				
			}	
						if(gastosArray.length>0){
							var datos = new FormData();
							datos.append("gasto", JSON.stringify(gastosArray));
							//datos.append("trabajos", trabajos);
							//datos.append("idTitulo", pos);
							//datos.append("idEmpleado", idEmpleado);

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
					
							
						


			}catch (error) {
		Swal.fire({
			icon: "error",
			text: console.error(error)
		})
	}






}

