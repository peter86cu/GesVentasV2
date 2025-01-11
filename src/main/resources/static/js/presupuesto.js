

// Cargar las cuentas bancarias al abrir el modal
function cargarCuentasBancarias() {
    fetch("http://localhost:7003/api/cuentas")
        .then(response => response.json())
        .then(cuentas => {
            const selectCuenta = document.getElementById("cuentaBancaria");
            selectCuenta.innerHTML = "<option value='' selected disabled>Seleccionar Banco y Cuenta</option>";
            cuentas.forEach(cuenta => {
                const option = document.createElement("option");
                option.value = cuenta.id;
                option.textContent = `${cuenta.banco} - ${cuenta.numeroCuenta} (${cuenta.moneda})`;
                
                 // Agrega los atributos data para almacenar banco y numeroCuenta
                option.setAttribute("data-banco", cuenta.banco);
                option.setAttribute("data-numeroCuenta", cuenta.numeroCuenta);
                
                selectCuenta.appendChild(option);
            });
            
             // Agrega el evento change después de cargar las opciones
            selectCuenta.addEventListener("change", function() {
                const selectedOption = this.options[this.selectedIndex];
                
                // Obtén los valores de los atributos data
                bancoSeleccionado = selectedOption.getAttribute("data-banco");
                numeroCuentaSeleccionada = selectedOption.getAttribute("data-numeroCuenta");

                console.log("Banco seleccionado:", bancoSeleccionado);
                console.log("Número de cuenta seleccionada:", numeroCuentaSeleccionada);
            });
            
        })
        .catch(error => console.error("Error cargando cuentas:", error));
}

// Llamar a cargarCuentasBancarias cuando se abra el modal
document.getElementById("presupuestoNuevoModal").addEventListener("show.bs.modal", cargarCuentasBancarias);


// Función para guardar el presupuesto
function guardarPresupuesto() {
    const cuentaId = document.getElementById("cuentaBancaria").value;
    const presupuestoInicialPesos = parseFloat(document.getElementById("presupuestoInicial").value);
    const presupuestoInicialDolares = parseFloat(document.getElementById("presupuestoInicialDolares").value);
    const mes = new Date().getMonth() + 1;
    const anio = new Date().getFullYear();

    if (!cuentaId || isNaN(presupuestoInicialPesos) || isNaN(presupuestoInicialDolares)) {
        alert("Por favor completa todos los campos.");
        return;
    }

    const presupuestoData = {
        cuentaId: numeroCuentaSeleccionada ,
        presupuestoInicialPesos: presupuestoInicialPesos,
        presupuestoInicialDolares: presupuestoInicialDolares,
        mes: mes,
        anio: anio,
        centroCostoId: document.getElementById("centrocID").value,
        banco:bancoSeleccionado,
        usuarioRegistra:document.getElementById("userId").value
    };
    
    //console.log(JSON.stringify(presupuestoData));
    alert(JSON.stringify(presupuestoData));

    fetch("http://localhost:7003/api/presupuestos", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(presupuestoData)
    })
    .then(response => {
    if (response.ok) {
        return response.json();  // Asumiendo que el servidor envía JSON
    } else {
        // Si la respuesta no es exitosa, también devolvemos el mensaje de error en el cuerpo
        return response.text().then(errorMessage => { 
            throw new Error(errorMessage);
        });
    }
})
.then(data => {
    // Aquí se obtiene el mensaje en caso de éxito
    mensajeOK(data.message);
})
.catch(error => {
    // Manejo del mensaje de error
    console.error("Error:", error.message);
    Swal.fire({
					icon: "error",
					text: error.message
				})
    //alert("Error al guardar el presupuesto: " + error.message);
});
}


$(document).ready(function() {
	$(".centrocostoPre").select2({
		dropdownParent: $('#presupuestoNuevoModal'),
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

		$("#codigoCC").val($('.centrocostoPre').select2('data')[0].type);
		$("#centrocID").val($('.centrocostoPre').select2('data')[0].id);
		//$("#precio").val($('.productos').select2('data')[0].precio);

	})
});


// Llamar a cargarPresupuestoData cuando se abra el modal
document.getElementById("presupuestoModal").addEventListener("show.bs.modal", cargarPresupuestoData);

function cargarPresupuestoData() {
    fetch("http://localhost:7003/api/presupuestos/centro-costo") // Reemplaza con el endpoint correcto
        .then(response => response.json())
        .then(data => {
            const presupuestoTable = document.getElementById("presupuestoData");
            presupuestoTable.innerHTML = ""; // Limpiar la tabla antes de llenarla

            data.forEach(item => {
                const row = document.createElement("tr");

                row.innerHTML = `
                    <td>${item.centro_costo_codigo}</td>
                    <td>${item.centro_costo_descripcion}</td>
                    <td>${item.mes}</td>
                    <td>${item.anio}</td>
                    <td>${item.banco}</td>
                    <td>${item.cuenta_banco}</td>
                    <td>${item.inicial_pesos}</td>
                    <td>${item.presupuesto_restante_pesos}</td>
                    <td>${item.inicial_dolares}</td>
                    <td>${item.presupuesto_restante_dolares}</td>
                    <td>${item.saldo_cuenta}</td>
                    <td>${item.estado}</td>
                `;

                presupuestoTable.appendChild(row);
            });
        })
        .catch(error => console.error("Error cargando los datos del presupuesto:", error));
}

// Llamar a cargarPresupuestoData cuando se abra el modal
document.getElementById("presupuestoModal").addEventListener("show.bs.modal", cargarPresupuestoData);



