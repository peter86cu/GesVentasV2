function crearUsuario() {
	var datos = new FormData();

	$.ajax({
		url: globalPath+"/validar-usuarios-nuevos",
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
				$('#addUsuarios').modal({ backdrop: 'static', keyboard: false })
				$('#addUsuarios').modal('show');
			} else {
				Swal.fire({
					icon: "warning",
					text: data.resultado
				})
			}
		}
	});
}



function guardarUsuario(accion) {
	var datos = new FormData();
	if(accion=="add"){
	var idEmpleado=$("#idempleado").val();
	var usuario= $("#usuario").val();
	var password= $("#password").val();
	var email=$("#email").val();
	var idRol=$("#idrol").val();
	var estado=$("#estados").val();
	var idUsuario="";
	}else if(accion=="edit"){
	var idEmpleado=$("#idempleadoEdit").val();
	var idUsuario=$("#idUsuarioEdit").val();
	var usuario= $("#usuarioEdit").val();
	var password= $("#passwordEdit").val();
	var email=$("#emailEdit").val();
	var idRol=$("#idrolEdit").val();
	var estado=$("#estadosEdit").val();	
	}


 	datos.append("accion",accion);
 	datos.append("email",email);
 	datos.append("usuario",usuario);
 	datos.append("password",password);
 	datos.append("idRol",idRol);
 	datos.append("estado",estado);
 	datos.append("idEmpleado",idEmpleado);
 	datos.append("idUsuario",idUsuario);
 	

	$.ajax({
		url: globalPath+"/adduser",
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
				$(".modal-body input").val("");
				$('#addUsuarios').modal('hide');
				 Swal.fire({
               position: "top-end",
               icon: "success",
               title: data.resultado,
               showConfirmButton: false,
               timer: 1500
             })
             setTimeout(function() {location.reload();}, 1505)
			} else {
				Swal.fire({
					icon: "error",
					text: data.resultado
				})
			}
		}
	});
}


function editarUsuario(idUsuario) {

	var datos = new FormData();

	datos.append("idUsuario", idUsuario,);

	$.ajax({
		url: globalPath+"/editar-usuario",
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
			$("#idempleadoEdit").val(data.user.empleado.idempleado);
			$("#idUsuarioEdit").val(data.user.idusuario);
			$("#nombreEdit").val(data.user.empleado.nombre+" "+data.user.empleado.apellidos);
			$("#usuarioEdit").val(data.user.usuario);
			//$("#passwordEdit").val(data.user.password);
			$("#emailEdit").val(data.user.email);
			$("#idrolEdit > option[value="+data.user.idrol.idrol +"]").attr("selected",true);
			$("#estadosEdit > option[value="+data.user.estado +"]").attr("selected",true);




			$('#editarUsuarios').modal({backdrop: 'static', keyboard: false})
			$('#editarUsuarios').modal('show');

			} else {
				Swal.fire({
					icon: "warning",
					text: data.resultado
				})
			}

		}
	});

}






//CREACION DEL PAGINADO
$(document).ready(function() {
	$("#searchusuarios").keyup(function() {
		_this = this;
		// Show only matching TR, hide rest of them
		$.each($("#userst tbody tr"), function() {
			if ($(this).text().toLowerCase().indexOf($(_this).val().toLowerCase()) === -1)
				$(this).hide();
			else
				$(this).show();
		});
	});
});




//Aqui va el paginado


$(document).ready(function() {

	addPagerToTables('#userst', 8);

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