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
 
 
		registros.push({ 'codigo': codigo, 'nombre': nombre, 'moneda': moneda,'saldo': saldo});
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
           <td style="width: 10%"><h5 id="id"><strong>`+ registros[i].nombre + `</strong></h5></td><td style="width: 10%"><h5><strong>` + registros[i].moneda + `</strong></td>
           <td style="width: 10%"><h5><strong>` + registros[i].saldo + `</strong></td>
           <td style="width: 10%"><h5><strong> <a href="#" class="mailbox-attachment-name" id="fileSubido_`+ registros[i].saldo + `"><i class="fas fa-paperclip"></i></a></strong></td>
           <td style="width: 10%"><strong><label for="file_`+ id + `"><i class="fas fa-cloud-download-alt"></i></label><input type="file" style="display:none"  name="file" id="file_` + id + `" class="fichero_` + id + `" onchange="cargarFichero(` + id + `)"/></strong></td>
           <td style="width: 10%"><h5><strong>` + hoy.toLocaleDateString(); + `</strong></td>
           <td class='text-right'><a href="#" onclick="eliminarFila(`+ id + `)" ><img src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAMAAAAoLQ9TAAAAeFBMVEUAAADnTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDx+VWpeAAAAJ3RSTlMAAQIFCAkPERQYGi40TVRVVlhZaHR8g4WPl5qdtb7Hys7R19rr7e97kMnEAAAAaklEQVQYV7XOSQKCMBQE0UpQwfkrSJwCKmDf/4YuVOIF7F29VQOA897xs50k1aknmnmfPRfvWptdBjOz29Vs46B6aFx/cEBIEAEIamhWc3EcIRKXhQj/hX47nGvt7x8o07ETANP2210OvABwcxH233o1TgAAAABJRU5ErkJggg==" ></a></td></tr>`;
			
 
		/*celda.appendChild(document.createTextNode(registros[i].codigo));
		celda.appendChild(document.createTextNode(registros[i].nombre));
		celda.appendChild(document.createTextNode(registros[i].nombre));*/
 
	}
 
$("#cuerpo-items").append(tr2);
 
}
 

