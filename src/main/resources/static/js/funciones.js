


function activarLoader(){
	abrirModal('loadingModal');
}

function desactivarLoading(){
	cerrarModal('loadingModal');
	}
function validarServer(accion) {
  // $('#loadingModal' ).modal({ backdrop: 'static', keyboard: false })
	$('#loadingModal').modal('show');
	var datos = new FormData();
	datos.append("accion", accion);

	$.ajax({
		url: globalPath + "/validar-server",
		method: "POST",
		data: datos,
		chache: false,
		contentType: false,
		processData: false,
		timeout: 15000,
		dataType: "json",
		success: function(respuesta) {
			var response = JSON.stringify(respuesta, null, '\t');
			var data = JSON.parse(response);
			if (data.status) {
				if ("/cargar-open-dia" == data.resultado) {
					//Ocultar DIV
					var x = document.getElementById("loadingTemplate");
				    if (x.style.display === "none") {
				        x.style.display = "block";
				    } else {
				        x.style.display = "none";
				    }
					$.ajax({
						url: globalPath + data.resultado,
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
								
								$('#abrirDia').modal({ backdrop: 'static', keyboard: false })
								$('#abrirDia').modal('show');
								//  var ventana = window.open('inicio');





							} else {
								
								window.location.href = data.resultado;
							}
						}						
					});

				} else {
					
					window.location.href = data.resultado;
				}
			} else if (!data.status) {
				
				
				Swal.fire({
					icon: "error",
					title: data.resultado,
					 showDenyButton: false,
				}).then((result) => {
			  /* Read more about isConfirmed, isDenied below */
			  if (result.isConfirmed) {
			    desactivarLoading();
			  }})
			}
			

		}

	}).fail(function(jqXHR, textStatus, errorThrown) {

		var msg = '';
		if (jqXHR.status === 0) {
			msg = 'No connection.\n Verify Network.';
			//ERR_CONNECTION_REFUSED hits this one
		} else if (jqXHR.status == 404) {
			msg = 'Requested page not found. [404]';
		} else if (jqXHR.status == 500) {
			msg = 'Internal Server Error [500].';
		} else if (exception === 'timeout') {
			msg = 'Time out error.';
		} else if (exception === 'abort') {
			msg = 'Ajax request aborted.';
		} else {
			msg = 'Uncaught Error.\n' + jqXHR.responseText;
		}


		Swal.fire({

			icon: textStatus,
			title: msg,
			showConfirmButton: false,
			timer: 2100
		})
		setTimeout(function() { location.reload(); }, 2505)
	});
}

function mensajeOK(mensaje) {
	Swal.fire({
		title: mensaje,
		type: 'success',
		icon: "success",
		showCancelButton: false,
		confirmButtonText: 'OK',
		cancelButtonText: "No",
		confirmButtonColor: '#3085d6',
		cancelButtonColor: '#d33',
	}).then((result) => {
		if (result.value) {
			location.reload();
		}
		return false;
	})
}

function mensajeOKSinActPagina(mensaje) {
	Swal.fire({
		title: mensaje,
		type: 'success',
		icon: "success",
		showCancelButton: false,
		confirmButtonText: 'OK',
		cancelButtonText: "No",
		confirmButtonColor: '#3085d6',
		cancelButtonColor: '#d33',
	}).then((result) => {
		if (result.value) {
			return true;
		}
		return false;
	})
}
function mensajeHoraExtra(mensaje) {
	Swal.fire({
		text: mensaje,
		icon: "success",
		showCancelButton: false,
		confirmButtonText: 'Aprobar',
		cancelButtonText: "Rechazar",
		confirmButtonColor: '#3085d6',
		cancelButtonColor: '#d33',
	}).then((result) => {
		if (result.value) {
			return true;
		}
		return false;
	})
}
function mensajeErrorSinActPagina(mensaje) {
	Swal.fire({
		title: mensaje,
		type: 'error',
		icon: "error",
		showCancelButton: false,
		confirmButtonText: 'OK',
		cancelButtonText: "No",
		confirmButtonColor: '#3085d6',
		cancelButtonColor: '#d33',
	}).then((result) => {
		if (result.value) {
			return true;
		}
		return false;
	})
}

function mensajeError(mensaje) {
	Swal.fire({
		title: mensaje,
		type: 'error',
		icon: "error",
		showCancelButton: false,
		confirmButtonText: 'OK',
		cancelButtonText: "No",
		confirmButtonColor: '#3085d6',
		cancelButtonColor: '#d33',
	}).then((result) => {
		if (result.value) {
			location.reload();
		}
		return false;
	})
}

function mensajeWarning(mensaje) {
	Swal.fire({
		title: mensaje,
		type: 'warning',
		icon: "warning",
		showCancelButton: false,
		confirmButtonText: 'OK',
		cancelButtonText: "No",
		confirmButtonColor: '#3085d6',
		cancelButtonColor: '#d33',
	}).then((result) => {
		if (result.value) {
			location.reload();
		}
		return false;
	})
}



function abrirFiltroCalendario(accion) {
	$('#' + accion).modal({ backdrop: 'static', keyboard: false })
	$('#' + accion).modal('show');
}


function validarServerOrdenCompra() {
	$.ajax({
		url: globalPath + "/validar-server",
		method: "POST",
		data: null,
		chache: false,
		contentType: false,
		processData: false,
		dataType: "json",
		success: function(respuesta) {
			var response = JSON.stringify(respuesta, null, '\t');
			var data = JSON.parse(response);
			if (data.code == 200) {
				window.location.href = data.resultado;
			} else if (data.code == 500) {
				Swal.fire({
					icon: "error",
					title: data.resultado
				})
			}

		}

	})
}



function abrirModal(nombreModal) {
	$('#' + nombreModal).modal({ backdrop: 'static', keyboard: false })
	$('#' + nombreModal).modal('show');

}

function cerrarModal(nombreModal) {
	/*$(".modal-body input").val("")
	$(".modal-body select").val("")
	$('#tabla1 > tbody').empty();
	$('#tabla2 > tbody').empty();
	$('#tabla3 > tbody').empty();*/
	$('#' + nombreModal).modal('hide');
}

/*
$(function() {
	$("#datepicker").datepicker({
		dateFormat: "yy-mm-dd"
	});
});*/


function abrirWEB() {
	window.open("punto-venta", 'Nombre de la ventana', "fullscreen,location=no,menubar=no,status=no,toolbar=no,RESIZABLE=0");
}



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
