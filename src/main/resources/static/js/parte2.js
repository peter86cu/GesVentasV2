

desahbilitar();

function desahbilitar() {

	if (getQueryVariable("estado") == "modificar") {

		var prod = $('#txtProductoV');
		$(prod).attr('disabled', 'disabled');

		var cant = $('#txtCantP');
		$(cant).attr('disabled', 'disabled');


		$('#btPagar').attr('disabled', 'disabled');


		$('#btCancelar').attr('disabled', 'disabled');




	} else {

		document.getElementById("btDevolver").style.display = 'none';
	}

}


function dolarcambio(t, evt) {
	var cambio = '42';
	var valor = $(t).val() * cambio;

	$(t).parent().parent().children('td').eq(2).find('input').val(valor);
	var charCode = (evt.which) ? evt.which : evt
	if (charCode == '13') {
		$(t).parent().parent().children('td').eq(2).find('input').focus();
	}
}
function chang1(t) {
	if ($(t).val() == 'dolar') {
		$(t).parent().parent().parent().children('td').eq(1).html('<input type="text" id="dolar" onkeyup="dolarcambio(this,event)" size="4"/>');
		$(t).parent().parent().parent().children('td').eq(1).find('input').focus();
		$("#credito_html").hide();
	} else if ($(t).val() == 'credito') {
		$(t).parent().parent().parent().children('td').eq(1).html('');
		$("#credito_html").show();
		$("#meses").focus();
	} else {
		$(t).parent().parent().parent().children('td').eq(1).html('');
		$("#credito_html").hide();
	}
}
function chang(t) {
	if ($(t).val() == 'dolar') {
		$(t).parent().parent().children('td').eq(1).html('<input type="text" id="dolar" onkeyup="dolarcambio(this,event)" size="4"/>');
		$("#credito_html").hide();
		$(t).parent().parent().children('td').eq(1).find('input').focus();
	} else if ($(t).val() == 'credito') {
		$(t).parent().parent().children('td').eq(1).html('');
		$("#credito_html").show();
		$("#meses").focus();
	} else {
		$(t).parent().parent().children('td').eq(1).html('');
		$("#credito_html").hide();
	}
}
function agregar_forma_cobro(t) {

	var suma = 0;
	$('.cantidad').each(function(i) {
		if ($(this).val() != '' && $(this).val() != 'undefined') {
			if (parseInt($(this).val())) {
				suma = suma + parseInt($(this).val());
			}
		}

	});
	var selec = '';
	var array = Array();
	$('.pago').each(function(index, element) {
		if ($(this).val() == 'efectivo') {
			array.push('efectivo');
		}
		if ($(this).val() == 'credito') {
			array.push('credito');
		}
		if ($(this).val() == 'mastercard') {
			array.push('mastercard');
		}
		if ($(this).val() == 'visa') {
			array.push('visa');
		}
		if ($(this).val() == 'amex') {
			array.push('amex');
		}
		if ($(this).val() == 'cabal') {
			array.push('cabal');
		}
		if ($(this).val() == 'panal') {
			array.push('panal');
		}
		if ($(this).val() == 'dolar') {
			array.push('dolar');
		}
		if ($(this).val() == 'publicidad') {
			array.push('publicidad');
		}
		if ($(this).val() == 'cheque') {
			array.push('cheque');
		}
		if ($(this).val() == 'debito') {
			array.push('debito');
		}


	});

	var selec = '';
	if (jQuery.inArray('efectivo', array) == '-1') {
		selec += '<option value="efectivo" selected="selected">Efectivo</option>';
	}

	if (jQuery.inArray('mastercard', array) == '-1') {
		selec += '<option value="mastercard">MasterCard</option>';
	}
	if (jQuery.inArray('visa', array) == '-1') {
		selec += '<option value="visa">Visa</option>';
	}
	if (jQuery.inArray('amex', array) == '-1') {
		selec += '<option value="amex">American Experss</option>';
	}

	if (jQuery.inArray('cabal', array) == '-1') {
		selec += '<option value="cabal">Cabal</option>';
	}
	if (jQuery.inArray('credito', array) == '-1') {
		selec += '<option value="credito">CREDITO CLIENTE</option>';
	}
	if (jQuery.inArray('panal', array) == '-1') {
		selec += '<option value="panal">Panal</option>';
	}
	//<?php if($_claves["dolar"]!='') { ?>
	if (jQuery.inArray('dolar', array) == '-1') {
		selec += '<option value="dolar">Dolar</option>';
	}
	//<?php } ?>
	if (jQuery.inArray('publicidad', array) == '-1') {
		selec += '<option value="publicidad">Publicidad y Propaganda</option>';
	}
	if (jQuery.inArray('cheque', array) == '-1') {
		selec += ' <option value="cheque">Cheque</option>';
	}
	if (jQuery.inArray('debito', array) == '-1') {
		selec += '<option value="debito">Debito</option>';
	}





	if (parseInt($('#total_venta').val()) > suma) {
		var valor = parseInt($('#total_venta').val()) - suma;


		$('#tablita tbody').append('<tr>\
  <td><select style="width :300px;" class="pago" id="selectFormaPago" name="selectFormaPago" onchange="chang(this)">'+ selec + '</select></td>\
  <td id="de"></td>\
  <td><input type="text" style="width :200px;" name="cantidad" onkeypress="cargar(event,this)"  class="cantidad" value="'+ valor + '" onfocus/> <label style="cursor:pointer" href="javascript:void(0)" onclick="quitar(this)">[ X ]</label></td>\
  </tr>');


		$('#txtSaldoPago').focus();
		$('#finalizar').hide('fast');
	} else {
		var credito = 0;
		$("#selectFormaPago").each(function() {
			if ($(this).val() == 'credito') {
				credito = 1;
			}
		});
		if (credito == 1) {
			$("#credito_html").show();
			$("#meses").focus();
			if ($("#cliente").val() == '1') {
				alert("Debe seleccionar un cliente Valido");
				return false;
			} else {

				$('#vuelto').html(suma - parseInt($('#total_venta').val()).toString());
				$('#finalizar').show('fast');
			}
		} else {
			$('#vuelto').html(suma - parseInt($('#total_venta').val()).toString());
			$('#finalizar').show('fast');
		}


	}

	$("#selectFormaPago").each(function() {
		if ($(this).val() == 'credito') {
			$("#credito_html").show();
			$("#meses").focus();
		}
	})

}
function guardar(evt) {
	var charCode = (evt.which) ? evt.which : evt
	var total = $('#total_venta').val();
	var id_venta = $('#txtVenta').val();
	var id_cliente = $('#idCliente').val();
	var forma_pago = '';
	var cantidad = '';
	var vuelto = $('#vuelto').html();
	var puntos = $("#puntos").val();
	var accion = "normal";
	var interes_venta = $("#interes_venta").val();
	var redondeo = $("#redondeo").val();
	var iva = $("#iva").val();
	var meses = $("#meses").val();
	var suma = 0;
	var id_autoriza = '';
	
	if (getQueryVariable("estado") == "modificar") {
		accion = "modificar";
		meses=2;
		interes_venta=18;
	}
	
	if(accion=="modificar"){
		id_autoriza = $("#id_autoriza").val()
	}
	
	$('#idVenta').val(id_venta);	
	
	$('.cantidad').each(function(i) {
		cantidad += $(this).val() + '|';
		if ($(this).val() != '' && $(this).val() != 'undefined') {
			if (parseInt($(this).val())) {
				suma = suma + parseInt($(this).val());
			}
		}
	});

	
	$('.pago').each(function(i) {
		forma_pago += $(this).val() + '|';
	});
	var tipo = '';
	$('input[type="radio"]').each(function(i) {
		if ($(this).parent().attr('class') == "checked") {
			tipo = $(this).val();
		}
	});



	if (parseInt($('#total_venta').val()) <= suma) {
		console.log("Entro a ver POST")
		
		var datos = new FormData();
		datos.append("total", total);
		datos.append("id_venta", id_venta);
		datos.append("forma_pago", forma_pago);
		datos.append("cantidad", cantidad);
		datos.append("tipo", tipo);
		datos.append("cliente", id_cliente);
		datos.append("vuelto", vuelto);
		datos.append("iva", iva);
		datos.append("meses", meses);
		datos.append("interes_venta", interes_venta);
		datos.append("redondeo", redondeo);
	    datos.append("puntos", puntos);	    
	    datos.append("id_autoriza", id_autoriza);
		datos.append("accion", accion);
		
		
		$.ajax({
		url: globalPath+"/guardar-venta",
		method: "POST",
		data: datos,
		chache: false,
		contentType: false,
		processData: false,
		dataType: "json",
		success: function(data) {
				var response = JSON.stringify(data, null, '\t');
				var datos = JSON.parse(response);
				if (datos.status) {

					//jQuery('#confirm-form').modal();
					$('#confirm-form').modal('hide');
					/*Swal.fire({
						position: "top-end",
						icon: "success",
						title: "Pago exitoso",
						showConfirmButton: false,
						timer: 1100
					})*/

					/*if (getQueryVariable("estado") == "modificar") {
						setTimeout(function() { location.href = "./listado-ventas"; }, 1505)
					} else {
						setTimeout(function() { location.reload(); }, 1505)
						inicioNuevaVenta();
					}*/
					var bandera = 0;
					$('.pago').each(function(i) {
						console.log('Tipo de pago')
						console.log($(this).val())
						if ($(this).val() != 'efectivo') {
							bandera = 1;
							cerrarModal('pagarVenta');
							abrirModal('ModalActualizaIdPago')
							///break;
						}
					});

					if (bandera == 0) {
						cerrarModal('pagarVenta');
						Swal.fire({
							position: "top-end",
							icon: "success",
							title: "Pago exitoso",
							showConfirmButton: false,
							timer: 1100
						})

						if (getQueryVariable("estado") == "modificar") {
							setTimeout(function() { location.href = "./listado-ventas"; }, 1505)
						} else {
							setTimeout(function() { location.reload(); }, 1505)
							inicioNuevaVenta();
						}
					}

					//datosImprimirVenta(id_venta);

				} else {
					alert('No se pudo Guardar Ocurrio un error interno:\n:' + data.respuesta);
				}
			}
		});
	}
	/*else {
		//alert('los valores no superan a la venta');
		Swal.fire({
			icon: "error",
			text: "Los valores no superan a la venta."
		})
	}*/

}



function exit(aplicacion) {

	if (aplicacion == 1) {
		$('#ModalDevolucion').modal('hide');
		setTimeout(function() { location.reload(); }, 5)
	}


}


function actualizarNumeroFacturaTarjeta(evt) {

	var charCode = (evt.which) ? evt.which : evt
	if (charCode == '13') {
		//guardar(evt);

		var idVenta= $('#idVenta').val();
		var transaccion= $('#txtfac').val();
        var datos = new FormData();
		datos.append("id_transaccion",transaccion );
		datos.append("idVenta",idVenta);
        cerrarModal('ModalActualizaIdPago');
		
        $.ajax({
		url: globalPath+"/guardar-id-transaccion-tarjeta-venta",
		method: "POST",
		data: datos,
		chache: false,
		contentType: false,
		processData: false,
		dataType: "json",
		success: function(data) {
				var response = JSON.stringify(data, null, '\t');
				var datos = JSON.parse(response);
				if (datos.status) {
					Swal.fire({
			position: "top-end",
			icon: "success",
			title: "Pago exitoso",
			showConfirmButton: false,
			timer: 1100
		})
		
		if (getQueryVariable("estado") == "modificar") {
			setTimeout(function() { location.href = "./listado-ventas"; }, 1505)
		} else {
			setTimeout(function() { location.reload(); }, 1505)
			inicioNuevaVenta();
		}
	    }else{
		 Swal.fire({
			icon: "error",
			text: datos.resultado
		})
		
		} 
		
	    }
	 });
   }
}

function datosImprimirVenta(idVenta) {


	VentanaCentrada('./pdf/documentos/factura.php?idVenta=' + idVenta, 'Venta', '', '1024', '768', 'true');


}

function quitar(id) {
	var td = id.parentNode;
	var tr = td.parentNode;
	var table = tr.parentNode;
	table.removeChild(tr);

}
$(function() {
	$('#total_venta_confirmar').html("Gs. " + $('#total_venta').val());
	$('#cantidad').attr('value', $('#total_venta').val());
	document.getElementById('cantidad').focus();
})


function number_format(number, decimals, dec_point, thousands_sep) {
	number = (number + '').replace(/[^0-9+\-Ee.]/g, '');
	var n = !isFinite(+number) ? 0 : +number,
		prec = !isFinite(+decimals) ? 0 : Math.abs(decimals),
		sep = (typeof thousands_sep === 'undefined') ? ',' : thousands_sep,
		dec = (typeof dec_point === 'undefined') ? '.' : dec_point,
		s = '',
		toFixedFix = function(n, prec) {
			var k = Math.pow(10, prec);
			return '' + Math.round(n * k) / k;
		};
	// Fix for IE parseFloat(0.55).toFixed(0) = 0;
	s = (prec ? toFixedFix(n, prec) : '' + Math.round(n)).split('.');
	if (s[0].length > 3) {
		s[0] = s[0].replace(/\B(?=(?:\d{3})+(?!\d))/g, sep);
	}
	if ((s[1] || '').length < prec) {
		s[1] = s[1] || '';
		s[1] += new Array(prec - s[1].length + 1).join('0');
	}
	return s.join(dec);
}