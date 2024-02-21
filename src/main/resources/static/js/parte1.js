

function getQueryVariable(variable) {
	// Estoy asumiendo que query es window.location.search.substring(1);
	var query = window.location.search.substring(1);
	var vars = query.split("&");
	for (var i = 0; i < vars.length; i++) {
		var pair = vars[i].split("=");
		if (pair[0] == variable) {
			return pair[1];
		}
	}
	return false;
}

function ponleFocusCantidadProductos() {
	document.getElementById("txtCantP").focus();
	$('#txtCantP').val(1);
}



showTime();
function showTime() {
	myDate = new Date();
	hours = myDate.getHours();
	minutes = myDate.getMinutes();
	seconds = myDate.getSeconds();
	if (hours < 10) hours = 0 + hours;
	if (minutes < 10) minutes = "0" + minutes;
	if (seconds < 10) seconds = "0" + seconds;
	$("#HoraActual").text(hours + ":" + minutes + ":" + seconds);
	setTimeout("showTime()", 1000);

}

ponleFocusylimpiarProductos();
function ponleFocusylimpiarProductos() {
	document.getElementById("txtProductoV").focus();
	$('#txtProductoV').val("");
}

    validarAperturaDia();
function validarAperturaDia() {
	var accion = "";
	var estado="normal";
	var idVenta=0;
if (getQueryVariable("estado") == "modificar") {
	 estado="modificar";
	 idVenta=getQueryVariable("id");
	}else{
		accion = "buscarArqueoCaja"
	}
	var datos = new FormData();
	datos.append("accion", accion);
	datos.append("estado", estado);
	datos.append("id", idVenta);

	$.ajax({
		url: globalPath+ "/punto-venta",
		method: "POST",
		data: datos,
		chache: false,
		contentType: false,
		processData: false,
		dataType: "json",
		success: function(resp) {
			var response = JSON.stringify(resp, null, '\t');
			var datos = JSON.parse(response);
			if (datos.status) {
				inicio();
			}else if(datos.code==400){
				Swal.fire({
					icon: "error",
					text: datos.resultado,
				})
				
			}else {

				Swal.fire({
					title: datos.terminalCierre.mensaje,
					showDenyButton: true,
					showCancelButton: false,
					confirmButtonText: 'Ir',
					denyButtonText: `Cancelar`,
				}).then((result) => {
					/* Read more about isConfirmed, isDenied below */
					if (result.isConfirmed) {
						if(datos.code == 406){
						window.location.href = 'gestion-terminal';
						}else if(datos.code==400){
						window.location.href = 'abrir-caja';	
						}
					} else if (result.isDenied) {
						window.location.href = 'inicio'
					}
				})
				//Este
				/*Swal.fire({
					icon: "error",
					text: datos.resultado,
				})
				setTimeout(function() { window.location.href = 'inicio'; }, 3000)*/
				//window.location.href = 'inicio';

			}
		}

	})
}


function inicio() {
	document.title = "POS | Caja ";

	var datos = new FormData();
	var accion = "buscarArqueoCaja";
	var idVenta=0;
	
	if (getQueryVariable("estado") == "modificar") {
	 idVenta=getQueryVariable("id");
	 //accion = "modificar";
	}
	datos.append("accion", accion);
	datos.append("idVenta", idVenta);
	
	$.ajax({
		url: globalPath+"/datos-ventas",
		method: "POST",
		data: datos,
		chache: false,
		contentType: false,
		processData: false,
		dataType: "json",
		success: function(resp) {
			var response = JSON.stringify(resp, null, '\t');
			var datos = JSON.parse(response);
			if (resp.length > 0) {
				$('#txtApertura').val(resp[0].id_apertura_cajero);
				$('#txtVenta').val(resp[0].id_venta);
				$('#txtOrden').val(resp[0].consecutivo);
				$('#buscar_cliente').val(resp[0].nombre);
				$('#idCliente').val(resp[0].id_cliente);

				genera_tabla();

			} else {
				var datos = new FormData();
				var accion = "buscarArqueo";
				datos.append("accion", accion);
				datos.append("idApertura", 0);

				$.ajax({
					url: globalPath+"/datos-arqueo",
					method: "POST",
					data: datos,
					chache: false,
					contentType: false,
					processData: false,
					dataType: "json",
					success: function(respuesta) {
						var response = JSON.stringify(respuesta, null, '\t');
						var datos = JSON.parse(response);
						if (datos.length > 0) {
							genera_tabla();
							enviarVentaId(datos[0].id_venta, datos[0].id_apertura_cajero);
							$('#txtApertura').val(datos[0].id_apertura_cajero);
							$('#txtVenta').val(datos[0].id_venta);
							$('#txtOrden').val(datos[0].consecutivo);

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
	});
}

function inicioNuevaVenta() {

	var datos = new FormData();
	var accion = "buscarArqueo";
	datos.append("accion", accion);
	datos.append("idApertura", 0);

	$.ajax({
		url: globalPath+"/datos-arqueo",
		method: "POST",
		data: datos,
		chache: false,
		contentType: false,
		processData: false,
		dataType: "json",
		success: function(respuesta) {
			var response = JSON.stringify(respuesta, null, '\t');
			var datos = JSON.parse(response);
			if (datos.length > 0) {
				genera_tabla();
				enviarVentaId(datos[0].id_venta, datos[0].id_apertura_cajero);
				$('#txtApertura').val(datos[0].id_apertura_cajero);
				$('#txtVenta').val(datos[0].id_venta);
				$('#txtOrden').val(datos[0].consecutivo);

			} else {

				Swal.fire({
					icon: "error",					
					text: datos.resultado
				})

			}

		}
	});

}
function enviarVentaId(idVenta) {
	var datos = new FormData();
	var accion = "enviarDatos";
	datos.append("accion", accion);
	datos.append("idVenta", idVenta);


	$.ajax({
		url: "punto-venta.html",
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

var input = document.querySelector('#txtCantP')
var fontSize = 40
// Tamaño inicial
input.style.fontSize = `${fontSize}px` 

input.addEventListener('input', () => {
  var l = input.value.length
  var s = (l/5) | 0 // Disminuimos 1px cada 5 caracteres
  input.style.fontSize = `${(fontSize - s) || 1}px`
})


var input = document.querySelector('#txtProductoV')
var fontSize = 40
// Tamaño inicial
input.style.fontSize = `${fontSize}px` 

input.addEventListener('input', () => {
  var l = input.value.length
  var s = (l/5) | 0 // Disminuimos 1px cada 5 caracteres
  input.style.fontSize = `${(fontSize - s) || 1}px`
})




$(document).ready(function() {
	$("#txtProductoV").keypress(function(e) {
		//no recuerdo la fuente pero lo recomiendan para
		//mayor compatibilidad entre navegadores.
		var code = (e.keyCode ? e.keyCode : e.which);
		//if (code == 13 && $(this).val().length >= 6) {
		if ( $(this).val().length == 10 || code == 13 ) {

			

			var codigo = $("#txtProductoV").val();
			var datos = new FormData();
			var accion = "buscarP";
			datos.append("accion", accion);
			datos.append("codigo", codigo);

			$.ajax({
				url:globalPath+ "/buscar-producto",
				method: "POST",
				data: datos,
				chache: false,
				contentType: false,
				processData: false,
				dataType: "json",
				success: function(respuesta) {
					var response = JSON.stringify(respuesta, null, '\t');
					var datos = JSON.parse(response);

					if (datos.status) {

						document.querySelector('#nombreProductoV').innerText = datos.producto.nombre;
						document.querySelector('#codigoProdV').innerText = datos.producto.codigo;
						document.querySelector('#precioP').innerText = datos.producto.precioventa;
						document.querySelector('#paraCodigo').innerText = "Código:";
						//buscarSimboloMonedaJS(datos.producto.id_moneda);
						$("#idImagenP").attr("src", "productos/" + datos.producto.foto);
						ponleFocusylimpiarProductos();
						
						if (getQueryVariable("estado") == "modificar") {
							eliminar_producto_devueltos( datos.producto.id);
							
								}else{
									insertDetalleVenta(datos.producto.id);
									$("#txtCantP").val(1);
								}
						/*} else {
							alert("No hay stock disponible")
							//error de que no hay suficiente es stock
							ponleFocusylimpiarProductos();
						}*/


					} else {
						Swal.fire({
							position: "top-end",
							icon: "error",
							title: datos.resultado,
							showConfirmButton: false,
							timer: 1100
						})
						ponleFocusylimpiarProductos();
					}
				}

			})

		}
	});
});


$(document).ready(function() {
	$("#txtCantP").keypress(function(e) {
		//no recuerdo la fuente pero lo recomiendan para
		//mayor compatibilidad entre navegadores.
		var code = (e.keyCode ? e.keyCode : e.which);
		if (code == 13 || $(this).val().length >= 7) {

			var codigo = $("#txtProductoV").val();
			var datos = new FormData();
			var accion = "buscarP";
			datos.append("accion", accion);
			datos.append("codigo", codigo);

			$.ajax({
				url: globalPath+"/buscar-producto",
				method: "POST",
				data: datos,
				chache: false,
				contentType: false,
				processData: false,
				dataType: "json",
				success: function(respuesta) {
					var response = JSON.stringify(respuesta, null, '\t');
					var datos = JSON.parse(response);

					if (datos.status) {

						document.querySelector('#nombreProductoV').innerText = datos.producto.nombre;
						document.querySelector('#codigoProdV').innerText = datos.producto.codigo;
						document.querySelector('#precioP').innerText = datos.producto.precioventa;
						document.querySelector('#paraCodigo').innerText = "Código:";
						//buscarSimboloMonedaJS(datos.producto.id_moneda);
						$("#idImagenP").attr("src", "productos/" + datos.producto.foto);
						ponleFocusylimpiarProductos();
						insertDetalleVenta(datos.producto.id);
						/*} else {
							alert("No hay stock disponible")
							//error de que no hay suficiente es stock
							ponleFocusylimpiarProductos();
						}*/


					} else {
						Swal.fire({
							position: "top-end",
							icon: "error",
							title: datos.resultado,
							showConfirmButton: false,
							timer: 1100
						})
						ponleFocusylimpiarProductos();
					}
				}

			})

		}
	});
});

function genera_tabla() {

	var datos = new FormData();
	var accion = "buscarVentas";
	if (getQueryVariable("estado") == "modificar") {
		var idVenta = getQueryVariable("id");
	} else {
		var idVenta = $("#txtVenta").val();
	}



	datos.append("accion", accion);
	datos.append("idVenta", idVenta);
	console.log(accion);
	$.ajax({
		url: "venta-tiempo-real",
		method: "POST",
		data: datos,
		chache: false,
		contentType: false,
		processData: false,
		dataType: "json",
		success: function(resul) {
			console.log(resul);
			var response = JSON.stringify(resul, null, '\t');
            var datos = JSON.parse(response);
			$('#g-table > tbody').empty();
			$('#h-table-saldos > tfoot').empty();
			
			var suma_total = 0;			
			var iva5 = 0;
			var iva10 = 0;
			
			var classs = '';
                if(datos.status){
                for (var i = 0; i < datos.itemsVentaActual.length; i++) {

                				if (datos.itemsVentaActual[i].cantidad != 0) {
                					if (i % 2 == 0) {
                						classs = 'gradeA odd';
                					} else {
                						classs = 'gradeA even';
                					}

                				}

                				var modificar = "";
                				var style = "background: #ccffcc";
                				if (getQueryVariable("estado") == 'modificar'){
										modificar = "class='not-active'";
										//style="background: #f91717";
										style = "background: #ccffcc";
									}
                					

                				var tr2 = `<tr style="`+style+ `">
                                        <td style="width: 5%"><h5><strong>`+ datos.itemsVentaActual[i].cantidad + `</strong></h5></td><td style="width: 12%"><h5><strong>` + datos.itemsVentaActual[i].codigo + `</strong></td><td style="width: 34%"><h5><strong>` + datos.itemsVentaActual[i].nombre + `</strong></td><td style="width: 12%" align="center"><h5><strong>` + datos.itemsVentaActual[i].precio + ' ' + datos.simboloMoneda + `</strong></td><td style="width: 12%" align="center"><h5><strong>` + datos.itemsVentaActual[i].monto + `</strong><td class='text-right'><a href="#" onclick="eliminar_producto(` +idVenta+ ',' + datos.itemsVentaActual[i].id_venta_detalle + ',' +'\''+ datos.itemsVentaActual[i].id_producto  +'\''+ `)" ><img ` + modificar + ` src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAMAAAAoLQ9TAAAAeFBMVEUAAADnTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDznTDx+VWpeAAAAJ3RSTlMAAQIFCAkPERQYGi40TVRVVlhZaHR8g4WPl5qdtb7Hys7R19rr7e97kMnEAAAAaklEQVQYV7XOSQKCMBQE0UpQwfkrSJwCKmDf/4YuVOIF7F29VQOA897xs50k1aknmnmfPRfvWptdBjOz29Vs46B6aFx/cEBIEAEIamhWc3EcIRKXhQj/hX47nGvt7x8o07ETANP2210OvABwcxH233o1TgAAAABJRU5ErkJggg==" ></a></td></tr>`;

                				$("#cuerpo-items").append(tr2);
                			}


                			var datos2 = `<tr>
                                      <th align="right"><label  id="total" style="font-size:200%; width:400%; margin-bottom:0;">`+ datos.simboloMoneda + suma_total + `
                                      </label></th>
                                      </tr>`;

                			var datos4 = `<tr>
                                      <th align="right"><label  id="ajuste" style="font-size:150%; width:400%; margin-bottom:0; ">`+ datos.simboloMoneda + suma_total + `
                                      </label></th>
                                      </tr>`;

                			var datos5 = `<tr>
                                      <th align="right"><label  id="subtotal" style="font-size:150%; width:400%; margin-bottom:0; ">`+ datos.simboloMoneda + suma_total + `
                                      </label></th>
                                      </tr>`;

                			var ivabasico = `<tr>
                                      <th align="right"><label  id="basico" style="font-size:150%; width:400%; margin-bottom:0; ">`+ datos.simboloMoneda + iva10 + `
                                      </label></th>
                                      </tr>`;

                			var ivaminimo = `<tr>
                                      <th align="right"><label  id="minimo" style="font-size:150%; width:400%; margin-bottom:0; ">`+ datos.simboloMoneda + iva5 + `
                                      </label></th>
                                      </tr>`;

                			var datos3 = `<tr>
                                      <th width="5%"></th>
                                      <th align="right"  style="background:#7F8793;color:#fff">datos.IVA<input type="hidden" id="ivainput5" value=""></th>
                                      <th align="left" id="iva5">`+ datos.ivaCalculado + `</th>
                                      </tr>`;
                            if (getQueryVariable("estado") == "modificar") {
					var lineas = `<tr>
                                      <th align="right">--------------------------------------------------------------------------------------</th>
                                      </tr>`;
							var datosDevolucion = `<tr>
                                      <th align="right"><label  id="devoluciones" style="font-size:200%; width:400%; margin-bottom:0;">`+ datos.simboloMoneda + suma_total + `
                                      </label></th>
                                      </tr>`;
								}          

                			var ajusteTotal = Math.ceil(number_format(suma_total + iva5 + iva10, 2, '.', '.'));
                			var ajusteResta = number_format(suma_total + iva5 + iva10, 2, '.', '.');
                			var ajTotal = number_format((ajusteTotal - ajusteResta), 2, '.', '.');

                			var test = Math.ceil(number_format(iva10, 2, ',', '.'));
                			//$("#result").append(datos3);
                			var total_sumado = parseFloat(suma_total) + number_format(iva10, 2, ',', '.') + ajTotal;
                			var total_final = parseInt(total_sumado);
                			var suma = Math.floor(parseFloat(suma_total) + parseFloat(iva5));
                			$("#resulttotal").append(datos5);
                			$("#resulttotal").append(ivabasico);
                			    
	
                			$("#resulttotal").append(datos4);
                			$("#resulttotal").append(datos2);
                			
                			if (getQueryVariable("estado") == "modificar") {
									$("#resulttotal").append(lineas);
									$("#resulttotal").append(datosDevolucion);
								}
                			var objNum5 = new Number(suma);
                			$("#total_venta").val(datos.totalAPagar);
                			$('#subtotal').html("Sub Total: " + datos.simboloMoneda +" "+ datos.subTotal);
                			$('#basico').html(datos.IVA +" "+ datos.simboloMoneda +" " + datos.ivaCalculado);
                			if (getQueryVariable("estado") == "modificar") {
							var suma=0;
							if($('#precioP').html() == " "){
								suma=0;
							}else{
								suma=$('#sumaDevuelto').val();
								suma=parseInt(suma)+parseInt($('#precioP').html());
								$('#sumaDevuelto').val(suma);
							}
							
                			$('#devoluciones').html("Devolución: " + datos.simboloMoneda +" -" + suma);
								}
                			$('#ajuste').html("Ajuste de redondeo: " + datos.simboloMoneda +" " + datos.ajusteRedondeo);
                			$('#total').html("TOTAL A PAGAR: " + datos.simboloMoneda +" "+ datos.totalAPagar);
                			document.querySelector('#total_venta_confirmar').innerText =datos.totalAPagar; //parseFloat(datos.totalAPagar, 10);
                			if (getQueryVariable("estado") == "modificar") {
							   document.querySelector('#total_venta_confirmar').innerText = suma;
							   $('#cantidad').val($('#total_venta').val());
							   document.querySelector('#vuelto').innerText=suma;
							   $('#finalizar').removeAttr('style');
								}
	
                			$("#iva").val(datos.ivaCalculado);
                			$("#idCliente").val(datos.id_cliente);
                			//$("#iva10").val(objNum2.toPrecision(3));
                			$("#redondeo").val(datos.ajusteRedondeo);
                			var puntos = 0;
                			if (datos.totalAPagar < 200) {
                				puntos = 1;
                			} else {
                				puntos = Math.floor(datos.totalAPagar / 200);
                			}

                			console.log("el valor " + objNum5);
                			var objNum4 = new Number(puntos);
                			$("#puntos").val(objNum4);
                			$("#puntos").html(Math.floor(objNum4));
                }else{
                var datos2 = `<tr>
                                      <th align="right"><label  id="total" style="font-size:200%; width:400%; margin-bottom:0;">`+ 0 + `
                                      </label></th>
                                      </tr>`;

                			var datos4 = `<tr>
                                      <th align="right"><label  id="ajuste" style="font-size:150%; width:400%; margin-bottom:0; ">`+0 + `
                                      </label></th>
                                      </tr>`;

                			var datos5 = `<tr>
                                      <th align="right"><label  id="subtotal" style="font-size:150%; width:400%; margin-bottom:0; ">`+ 0 + `
                                      </label></th>
                                      </tr>`;

                			var ivabasico = `<tr>
                                      <th align="right"><label  id="basico" style="font-size:150%; width:400%; margin-bottom:0; ">`+ 0 + `
                                      </label></th>
                                      </tr>`;

                			var ivaminimo = `<tr>
                                      <th align="right"><label  id="minimo" style="font-size:150%; width:400%; margin-bottom:0; ">`+ 0 + `
                                      </label></th>
                                      </tr>`;

                			var datos3 = `<tr>
                                      <th width="5%"></th>
                                      <th align="right"  style="background:#7F8793;color:#fff">datos.IVA<input type="hidden" id="ivainput5" value=""></th>
                                      <th align="left" id="iva5">`+ 0 + `</th>
                                      </tr>`;

                                      $("#resulttotal").append(datos5);
                                      $("#resulttotal").append(ivabasico);
                                      $("#resulttotal").append(datos4);
                                       $("#resulttotal").append(datos2);
                                       $('#subtotal').html("Sub Total: " + datos.simboloMoneda +" "+ datos.subTotal);
                                       $('#basico').html(datos.IVA +" "+ datos.simboloMoneda +" " + datos.ivaCalculado);
                                        $('#ajuste').html("Ajuste de redondeo: " + datos.simboloMoneda +" " + datos.ajusteRedondeo);
                                        $('#total').html("TOTAL A PAGAR: " + datos.simboloMoneda +" "+ datos.totalAPagar);

                }


		}

	});



}



function insertDetalleVenta(idProducto) {
	var datos = new FormData();
	var accion = "insertVentaDetalle";
	var idVenta = $('#txtVenta').val();
	var cantidad = $('#txtCantP').val();

	datos.append("accion", accion);
	datos.append("idVenta", idVenta);
	datos.append("cantidad", cantidad);
	datos.append("idProducto", idProducto);
	$.ajax({
		url: globalPath+"/add-detalle-venta",
		method: "POST",
		data: datos,
		chache: false,
		contentType: false,
		processData: false,
		dataType: "json",
		success: function(resul) {
			var response = JSON.stringify(resul, null, '\t');
			var datos = JSON.parse(response);
			if (datos.status) {
			//Por ahora no mostrar el mensaje
				/* Swal.fire({
                         position: "top-end",
                         icon: "success",
                         title: datos.resultado,
                         showConfirmButton: false,
                         timer: 1100
                       })*/
                       // setTimeout(function() {location.reload();}, 5)
				genera_tabla();

			} else {
				Swal.fire({
					icon: "error",
					text: datos.resultado,
				})
			}

		}
	});



}


function eliminar_producto (idVenta,idVentaDetalle,idProducto) {

	var accion = "eliminar";
	var datos = new FormData();
	datos.append("accion", accion);
	datos.append("idVenta", idVenta);
	datos.append("idVentaDetalle", idVentaDetalle);
	datos.append("idProducto", idProducto);

	$.ajax({
		url: "eliminar-producto-venta",
		method: "POST",
		data: datos,
		chache: false,
		contentType: false,
		processData: false,
		dataType: "json",
		success: function(result) {
		var response = JSON.stringify(result, null, '\t');
        			var datos = JSON.parse(response);
        			if (datos.status) {
				$('#g-table > tbody').empty();
				$('#h-table-saldos > tfoot').empty();
				genera_tabla();

		}else{
		       Swal.fire({
        			icon: 'error',
        			title: datos.respuesta,
        			})
		  }
		}
	});
}


function eliminar_producto_devueltos (idProducto) {

	var accion = "eliminar";
	var datos = new FormData();
	var idVenta = getQueryVariable("id");
	var cantidad= $('#txtCantP').val();
	datos.append("accion", accion);
	datos.append("idVenta", idVenta);
	datos.append("idProducto", idProducto);
	datos.append("cantidad", cantidad);

	$.ajax({
		url: globalPath+"/eliminar-producto-devolucion",
		method: "POST",
		data: datos,
		chache: false,
		contentType: false,
		processData: false,
		dataType: "json",
		success: function(result) {
		var response = JSON.stringify(result, null, '\t');
        			var datos = JSON.parse(response);
        			if (datos.status) {
				$('#g-table > tbody').empty();
				$('#h-table-saldos > tfoot').empty();
				genera_tabla();

		}else{
		       Swal.fire({
        			icon: 'error',
        			title: datos.respuesta,
        			})
		  }
		}
	});
}

function cancelarVenta() {

	var idVenta = $('#txtVenta').val();
	var accion = "cancelar";
	var datos = new FormData();
	datos.append("accion", accion);
	datos.append("idVenta", idVenta);

	const swalWithBootstrapButtons = Swal.mixin({
		customClass: {
			confirmButton: 'btn btn-success',
			cancelButton: 'btn btn-danger'
		},
		buttonsStyling: false
	})

	swalWithBootstrapButtons.fire({
		title: 'Esta seguro de cancelar la venta?',
		icon: 'warning',
		showCancelButton: true,
		confirmButtonText: 'Si',
		cancelButtonText: 'No',
		reverseButtons: true
	}).then((result) => {
		if (result.isConfirmed) {

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
						swalWithBootstrapButtons.fire(
							'Eliminado!',
							'La venta ha sido cancelada.',
							'success'
						).then((result) => {
							if (result.isConfirmed)
								location.reload();
						});

					} else {
						Swal.fire({
							icon: 'error',
							title: 'Oops...',
							text: 'Ocurrio un error cancelando la venta'

						})
					}

				}
			});


		}
	})

}

function devolverVenta(opcion) {


	var idVenta = $('#txtVenta').val();
	var idCausa = $('#txt_tipo_causa').val();
	var idEstadoDev = $('#txt_tipo_estado_dev').val();
	var idAutoriza = $('#id_autoriza').val();

	var datos = new FormData();

	datos.append("idVenta", idVenta);
	datos.append("idCausa", idCausa);
	datos.append("idEstadoDev", idEstadoDev);
	datos.append("idAutoriza", idAutoriza);
	if (opcion == 1) {
		
		$('#btPagar').removeAttr('disabled');
		 $('#btDevolver').attr('disabled');
		$('#txtProductoV').removeAttr('disabled');
		//$('#ModalDevolucion').modal('hide');
		cerrarModal('ModalDevolucion');
		
			$('#devolucion').val("OK");
		//var accion = "devolucion_completa";
		Swal.fire({icon: 'success',
				text: 'Caja habilitada para la devolución.'
					})
		
	}else{
		datos.append("accion", accion);

	const swalWithBootstrapButtons = Swal.mixin({
		customClass: {
			confirmButton: 'btn btn-success',
			cancelButton: 'btn btn-danger'
		},
		buttonsStyling: false
	})

	swalWithBootstrapButtons.fire({
		title: 'La compra sera devuelta?',
		icon: 'warning',
		showCancelButton: true,
		confirmButtonText: 'Si',
		cancelButtonText: 'No',
		reverseButtons: true
	}).then((result) => {
		if (result.isConfirmed) {

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
						swalWithBootstrapButtons.fire(
							'Eliminado!',
							'La venta ha sido cancelada.',
							'success'
						).then((result) => {
							if (result.isConfirmed)
								location.reload();
						});
					} else {
						Swal.fire({
							icon: 'error',
							title: 'Oops...',
							text: 'Ocurrio un error cancelando la venta'

						})
					}
					window.close();
					location.reload();


				}

			});


		}
	})
	}
	

}




function autorizarCambiosDevoluciones() {

	var user = $('#txt_usuario').val();
	var pass = $('#txt_contra').val();
	var accion = "autorizar";
	var datos = new FormData();
	datos.append("accion", accion);
	datos.append("user", user);
	datos.append("pass", pass);

	$.ajax({
		url: globalPath+"/autorizar-devolucion",
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
			//if (respuesta != 2 || respuesta != 3 || respuesta != 4) {

				$('#ModalAutorizar').modal('hide');
				$('#ModalDevolucion').modal('show');
				document.querySelector('#txt_autorizado').innerText = "Autorizado por: " + data.user.empleado.nombre+ " "+data.user.empleado.apellidos;
				//$('#txt_tipo_estado_dev').val(data.user.idusuario);
				
				var button = $('#btAceptar');
				$(button).removeAttr('disabled');



				var button1 = $('#btAutorizar');
				$(button1).attr('disabled', 'disabled');
				$('#id_autoriza').val(data.user.idusuario);

			} else {

				Swal.fire({
					icon: "error",
					title: data.resultado


				})

				document.querySelector('#txt_autorizado').innerText = "";
				$('#ModalAutorizar').modal('show');
			} /*if (respuesta == 3) {

				Swal.fire({
					icon: "error",
					title: "No tienes permiso para esta acción. Notificar a un administrador."

				})
				document.querySelector('#txt_autorizado').innerText = "";
				$('#ModalAutorizar').modal('show');

			} if (respuesta == 4) {

				Swal.fire({
					icon: "error",
					title: "No se encontro el usuario"


				})
				document.querySelector('#txt_autorizado').innerText = "";
				$('#ModalAutorizar').modal('show');
			}*/
		}
	});

}



function habilitar_btAutorizar() {

	var button = $('#btAutorizar');
	$(button).removeAttr('disabled');

}


function cancelarVentaSalida() {

	var idVenta = $('#txtVenta').val();
	var accion = "eliminarSalir";
	var datos = new FormData();
	datos.append("accion", accion);
	datos.append("idVenta", idVenta);

	$.ajax({
		url: "ajax/ajaxVentas.php",
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




function autorizarLogin() {
	//$('#ModalDevolucion').modal('hide');
	//$('#ModalAutorizar').modal('show');
	
$('#ModalDevolucion').modal({backdrop: 'static', keyboard: false})
$('#ModalAutorizar').modal('show');
}

/*function abrir() {

	if ($('#total_venta').val() > 0) {
		var opt = {
			autoOpen: false,
			modal: true,
			width: 550,
			height: 650

		};

		var theDialog = $("#confirm-form").dialog(opt);
		theDialog.dialog("open");


		//  $('#confirm-form').dialog('open').html("<div align='center'><img src='cargando.gif' /></div>").load("ajax_formas_cobros");
	} else {
		Swal.fire({
					icon: "warning",
					title: "Debe realizar la compra de al menos un producto."


				})
		document.getElementById('producto_cod').focus();
	}
}*/

/*function validarCodigoProducto(modo){
  var result=false;
  if (modo==2) {
    var numeroFac= $("#txtProductoV").val();
  }else{
    var numeroFac= $("#txtProductoV").val();
  }

  if(numeroFac !=''){
    $('#txtProductoV').removeClass("form-control is-invalid");
    $('#txtProductoV').addClass("form-control is-valid");
    $('#txtProductoV').removeClass("form-control is-invalid");
    $('#txtProductoV').addClass("form-control is-valid");
    result=true;
  }else{
    $('#txtProductoV').removeClass("form-control is-valid");
    $('#txtProductoV').addClass("form-control is-invalid");
    $('#txtProductoV').removeClass("form-control is-valid");
    $('#txtProductoV').addClass("form-control is-invalid");
    result=false;
  }
  return result;
}*/

function abrir() {
	if ($('#total_venta').val() > 0) {
		//$('#pagarVenta').modal('show');
		abrirModal('pagarVenta');
		}
	else{
		Swal.fire({
					icon: "warning",
					title: "Debe realizar la compra de al menos un producto."


				})
		document.getElementById('#txtProductoV').focus();
	}
	}

function cargar(evt, t) {

	var charCode = (evt.which) ? evt.which : evt

	if (charCode == '13') {
		if ($(t).hasClass("cantidad")) {

			agregar_forma_cobro(t);
		} else {
			agregar_forma_cobro(t);
		}
	}
	var cant = $('#cantidad').val();
	var total = $('#total_venta').val();
	var vuelto = document.getElementById("vuelto").innerHTML;

	if (charCode == '13') {
		guardar(evt);

	}
}

function habilitar_combo() {

	document.getElementById("txt_tipo_causa").style.display = "block";
	document.getElementById("txt_causa").style.display = "block";


}


