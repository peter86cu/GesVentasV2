function abrirModal(nombreModal){
$('#'+nombreModal).modal({backdrop: 'static', keyboard: false})
$('#'+nombreModal).modal('show');

}

function cerrarModal(nombreModal){
$(".modal-body input").val("")
$('#'+nombreModal).modal('hide');
}


function ejecutarProceso(idProceso){


$('#myModal').modal('show');




var datos = new FormData();


	datos.append("accion", "");
	datos.append("proceso", idProceso);
	
	

 $.ajax({
		url: globalPath+"/ejecutar-proceso",
		method: "POST",
		data: datos,
		chache: false,
		contentType: false,
		processData: false,
		dataType: "json",
		beforeSend: function(objeto) {
			  var progress = setInterval(function() {
				var $bar = $('.bar'); 
    if ($bar.width()==466) {
      
        // complete!
        
        // reset progree bar
        clearInterval(progress);
        $('.progress').removeClass('active');
        $bar.width(0);
        
        // update modal 
        
    } else if($bar.width()!=null || $bar.width()<466){
      
        // perform processing logic (ajax) here
        $bar.width($bar.width()+100);
    }
    
    $bar.text($bar.width()/5 + "%");
	}, 600);

		},
		success: function(data) {
			var response = JSON.stringify(data, null, '\t');
			var datos = JSON.parse(response);
			
		$('#myModal .modal-body').html(datos.resultado);
        $('#myModal .hide,#myModal .in').toggleClass('hide in');
        
        // re-enable modal allowing close
        $('#myModal').data('reenable',true);
       // $('#myModal').modal('show');
		}
	});
}



 function stopApplication(idProceso) {
	
	var datos = new FormData();
	datos.append("proceso", idProceso);
	
	$.ajax({
		url: globalPath + "/stop-servidor",
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
					icon: "success",
					title: data.resultado
				})
				
				}else{
					Swal.fire({
					icon: "error",
					title: data.resultado
				})
				}
	
       
    }
    })
  }




function restartApplication(idProceso) {
	
	var datos = new FormData();
	datos.append("proceso", idProceso);
	
	$.ajax({
		url: globalPath + "/reiniciar-servidor",
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
					icon: "success",
					title: data.resultado
				})
				
				}else{
					Swal.fire({
					icon: "error",
					title: data.resultado
				})
				}
	
       
    }
    })
  }
//function ejecutarProceso(idProceso){

 




/*	var datos = new FormData();

	var accion = "ejecutar";
	datos.append("idProceso", idProceso);
	datos.append("accion", accion);
$('#content').html('<div class="loading"><img src="images/loader.gif" alt="loading" /><br/>Un momento, por favor...</div>');
	$.ajax({
		url: "/buscar-proveedor",
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

				if (paso == "1") {
					document.querySelector('#proveedorNE').innerText = data.proveedor.razon_social;
					document.querySelector('#direccionE').innerText = data.proveedor.direccion;
					document.querySelector('#emailE').innerText = data.proveedor.email;
					document.querySelector('#telefonoE').innerText = data.proveedor.telefono;
					document.querySelector('#autorizo').innerText = data.proveedor.razon_social;
					document.querySelector('#cancelo').innerText = data.proveedor.razon_social;
				} if (paso == "2") {
					document.querySelector('#proveedorNF').innerText = data.proveedor.razon_social;
					document.querySelector('#direccionF').innerText = data.proveedor.direccion;
					document.querySelector('#emailF').innerText = data.proveedor.email;
					document.querySelector('#telefonoF').innerText = data.proveedor.telefono;

				}
			} else {
				Swal.fire({
					icon: "error",
					text: data.result,
				})
			}
		}


	})*/


//}


