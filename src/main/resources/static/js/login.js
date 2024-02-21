let globalPath="GesVentas";
let aperturaCajero="0";



function inicioSession() {
	activarLoader();
	var datos = new FormData();
	var user = $("#username").val();
	var pass = $("#password").val();

	if(user=="" || pass==""){
		Swal.fire({
					icon: "error",
					text: "El usuario o contraseña no puede estar vacio."
				})
	}else{
			var accion = "login";

	datos.append("username", user);
	datos.append("password", pass);

	$.ajax({
		url: globalPath+"/login",
		method: "POST",
		data: datos,
		chache: false,
		contentType: false,
		processData: false,
		dataType: "json",
		success: function(respuesta) {
			desactivarLoading();
			var response = JSON.stringify(respuesta, null, '\t');
			var data = JSON.parse(response);
			console.log(data.code);
			if (data.status) {
				window.location = "inicio";
			} else {
				Swal.fire({
					icon: "error",
					text: data.error.menssage
				})
				window.location = "redirect:/";
			}


		}
	});
	}




}




$(document).ready(function() {
	$("#password").keypress(function(e) {
		//no recuerdo la fuente pero lo recomiendan para
		//mayor compatibilidad entre navegadores.
		var code = (e.keyCode ? e.keyCode : e.which);
		if (code == 13) {
			inicioSession();
		}
	})
});

