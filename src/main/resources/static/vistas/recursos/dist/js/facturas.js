function agregarFacturaInicial(event){

  var datos = new FormData();
  var accion = "inicial";

  datos.append("accion",accion);

  $.ajax({
    url: "ajax/ajaxFacturasCompras.php",
    method : "POST",
    data: datos,
    chache: false,
    contentType: false,
    processData:false,
    dataType: "json",
    success: function(respuesta){

     if(respuesta){

      $("#idCompra").val(respuesta);
      document.querySelector('#idFactura').innerText = respuesta;
      $('#ModalADDFacturas').modal('show');
      mostrar_itemsFactura(respuesta,"nada");
    }else{
      Swal.fire({
       icon: "error",
       title: "Oops...",
       text: "A ocurrido un error!",
       footer: "<a href>Ver que mensaje dar?</a>"
     })
    }
  }
});


}



function abrirAgregarFactura(event){
     
      $('#ModalADDFacturas').modal('show');
      mostrar_itemsFactura(respuesta,"nada");
   
}


$('#myModalCompra').on('hidden.bs.modal', function (e) {
  $(this)
  .find("input,textarea,select,span")
  .val('')
  .end()
  .find("input[type=checkbox], input[type=radio]")
  .prop("checked", "")
  .end();
})

$(".ModalEditarCompras").click(function(){

  var idCompra = $(this).attr("idCompra");
  var datos = new FormData();
  var accion = "buscar";
  datos.append("accion",accion);
  datos.append("idCompra",idCompra);



  $.ajax({
   url: "ajax/ajaxFacturasCompras.php",
   method : "POST",
   data: datos,
   chache: false,
   contentType: false,
   processData:false,
   dataType: "json",
   success: function(respuesta){
console.log(respuesta);
    if(respuesta["id_orden_compra"]==1){
      $("#txtOrdenCompra > option[value=1]").attr("selected",true);      
    }else{
      $("#txtOrdenCompra > option[value="+respuesta["id_orden_compra"]+"]").attr("selected",true);
      
    }
    document.querySelector('#idCompraE').innerText = respuesta["id_entrada_compra"];
    //document.querySelector('#datepicker').innerText = respuesta["fecha_hora"];
    $("#txtFormaPagoFE > option[value="+respuesta["id_forma_pago"]+"]").attr("selected",true);
    $("#txtEnvioFE > option[value="+respuesta["id_plazo"]+"]").attr("selected",true);
    $("#txtDepositoE > option[value="+respuesta["id_deposito"]+"]").attr("selected",true);
    $("#txtMonedaFE > option[value="+respuesta["id_moneda"]+"]").attr("selected",true);
    $("#txtReceptorE > option[value="+respuesta["id_usuario_recibio"]+"]").attr("selected",true);
    buscar_proveedor_factura(respuesta["id_proveedor"],"1");
    mostrar_itemsFactura(respuesta["id_entrada_compra"],"editar");
    $("#txtOrdenCompraE").val(respuesta["id_orden_compra"]);
    $("#txtNumeroFacturaE").val(respuesta["nro_factura"]);
    $("#idCompra1E").val(respuesta["id_entrada_compra"]);
    $("#proveedorCE").val(respuesta["id_proveedor"]);
    $("#datepickerCE").val(respuesta["fecha_hora"]);

    if(respuesta["estado"]==3){
      $('#autorizar').attr("disabled", true);
      $('#cancelar').attr("disabled", true);
    }else{
      $('#autorizar').attr("disabled", false);
      $('#cancelar').attr("disabled", false);
    }


   /* validarNumeroFactura(2);
    validarFecha(2);
    validarOrdenCompra(2);
    validarFormaPago(2);
    validarMetodoEnvio(2);
    validarDeposito(2);
    validarReceptor(2);
    validarMoneda(2);*/
  }


});

})


$(".ModalADDFacturas").click(function(){

  var idCompra = $(this).attr("idCompra");

  var datos = new FormData();

  var accion = "buscar";
  datos.append("accion",accion);
  datos.append("idCompra",idCompra);


  $.ajax({
   url: "ajax/ajaxFacturasCompras.php",
   method : "POST",
   data: datos,
   chache: false,
   contentType: false,
   processData:false,
   dataType: "json",
   success: function(respuesta){
console.log(respuesta);
    document.querySelector('#idFactura').innerText = respuesta["id_entrada_compra"];
    //document.querySelector('#datepicker').innerText = respuesta["fecha_hora"];
    $("#txtFormaPagoF > option[value="+respuesta["id_forma_pago"]+"]").attr("selected",true);
    $("#txtEnvioF > option[value="+respuesta["id_plazo"]+"]").attr("selected",true);
    $("#txtDeposito > option[value="+respuesta["id_deposito"]+"]").attr("selected",true);
    $("#txtMonedaF > option[value="+respuesta["id_moneda"]+"]").attr("selected",true);
    $("#txtReceptor > option[value="+respuesta["id_usuario"]+"]").attr("selected",true);
    $("#idCompra").val(respuesta["id_entrada_compra"]);

    $("#proveedor2").val(respuesta["id_proveedor"]);
    $("#datepickerF").val(respuesta["fecha_hora"]);
    $("#txtOrdenCompra").val(respuesta["id_orden_compra"]);
    $("#txtNumeroFactura").val(respuesta["nro_factura"]);
    buscar_proveedor_factura(respuesta["id_proveedor"],"2");
    mostrar_itemsFactura(respuesta["id_entrada_compra"],"nada");
    validarNumeroFactura(1);
    validarFecha(1);
    validarOrdenCompra(1);
    validarFormaPago(1);
    validarMetodoEnvio(1);
    validarDeposito(1);
    validarReceptor(1);
    validarMoneda(1);


  }


});

})


function validarOrdenCompra(modo){
   var result=false;

  if(modo==2){
   var idorden = $('#txtOrdenCompraE').val();
 }else{
  var idorden = $('#txtOrdenCompra').val();
}


var datos = new FormData();
var accion = "validar";
datos.append("accion",accion);
datos.append("idOrden",idorden);

if(idorden==''){
  $('#txtOrdenCompra').addClass("form-control is-invalid");
  $('#txtOrdenCompraE').addClass("form-control is-invalid");
  result =false;
}else{
 $.ajax({
   url: "ajax/ajaxFacturasCompras.php",
   method : "POST",
   data: datos,
   chache: false,
   contentType: false,
   processData:false,
   dataType: "json",
   success: function(respuesta){

    if(respuesta==idorden || idorden ){
      $('#txtOrdenCompra').removeClass("form-control is-invalid");
      $('#txtOrdenCompra').addClass("form-control is-valid");
      $('#txtOrdenCompraE').removeClass("form-control is-invalid");
      $('#txtOrdenCompraE').addClass("form-control is-valid");
      result=true;

    }else{
      $('#txtOrdenCompra').addClass("form-control is-invalid");
      $('#txtOrdenCompraE').addClass("form-control is-invalid");
      result=false;
    }
  }
});
}
return result;
}

function validarFormaPago(modo){
  var result=false;
  if(modo==2){
   var cod = document.getElementById("txtFormaPagoFE").value;
   var combo = document.getElementById("txtFormaPagoFE");
 }else{
   var cod = document.getElementById("txtFormaPagoF").value;
   var combo = document.getElementById("txtFormaPagoF");
 }
 var selected = combo.options[combo.selectedIndex].text;

 if (cod>0) {
  $('#txtFormaPagoF').removeClass("form-control is-invalid");
  $('#txtFormaPagoF').addClass("form-control is-valid");
  $('#txtFormaPagoFE').removeClass("form-control is-invalid");
  $('#txtFormaPagoFE').addClass("form-control is-valid");
  result=true;
}else{
  $('#txtFormaPagoF').addClass("form-control is-invalid");
  $('#txtFormaPagoE').addClass("form-control is-invalid");
  result=false;
}
return result;

}


function validarMetodoEnvio(modo){
  var result=false;
  if(modo==2){
   var cod = document.getElementById("txtEnvioFE").value;
   var combo = document.getElementById("txtEnvioFE");
 }else{
   var cod = document.getElementById("txtEnvioF").value;
   var combo = document.getElementById("txtEnvioF");
 }
 var selected = combo.options[combo.selectedIndex].text;

 if (cod>0) {
  $('#txtEnvioF').removeClass("form-control is-invalid");
  $('#txtEnvioF').addClass("form-control is-valid");
  $('#txtEnvioFE').removeClass("form-control is-invalid");
  $('#txtEnvioFE').addClass("form-control is-valid");
  result=true;
}else{
  $('#txtEnvioF').addClass("form-control is-invalid");
  $('#txtEnvioFE').addClass("form-control is-invalid");
  result=false;
}
return result;
}

function validarDeposito(modo){
  /* Para obtener el valor */
  var result=false;
  if(modo==2){
   var cod = document.getElementById("txtDepositoE").value;
   var combo = document.getElementById("txtDepositoE");
 }else{
   var cod = document.getElementById("txtDeposito").value;
   var combo = document.getElementById("txtDeposito");
 }
 var selected = combo.options[combo.selectedIndex].text;

 if (cod>0) {
  $('#txtDepositoE').removeClass("form-control is-invalid");
  $('#txtDepositoE').addClass("form-control is-valid");
  $('#txtDeposito').removeClass("form-control is-invalid");
  $('#txtDeposito').addClass("form-control is-valid");
   result=true;
}else{
  $('#txtDepositoE').addClass("form-control is-invalid");
  $('#txtDeposito').addClass("form-control is-invalid");
  result=false;
}
return result;
}

function validarReceptor(modo){
  /* Para obtener el valor */
  var result=false;
  if(modo==2){
   var cod = document.getElementById("txtReceptorE").value;
   var combo = document.getElementById("txtReceptorE");
 }else{
   var cod = document.getElementById("txtReceptor").value;
   var combo = document.getElementById("txtReceptor");
 }
 var selected = combo.options[combo.selectedIndex].text;

 if (selected!='Seleccione') {
  $('#txtReceptorE').removeClass("form-control is-invalid");
  $('#txtReceptorE').addClass("form-control is-valid");
  $('#txtReceptor').removeClass("form-control is-invalid");
  $('#txtReceptor').addClass("form-control is-valid");
  result =true;
}else{
  $('#txtReceptorE').addClass("form-control is-invalid");
  $('#txtReceptor').addClass("form-control is-invalid");
  result=false;
}
return result;
}


function validarMoneda(modo){
  var result=false;
  if(modo==2){
   var cod = document.getElementById("txtMonedaFE").value;
   var combo = document.getElementById("txtMonedaFE");
 }else{
   var cod = document.getElementById("txtMonedaF").value;
   var combo = document.getElementById("txtMonedaF");
 }
 var selected = combo.options[combo.selectedIndex].text;

 if (cod>0) {
  $('#txtMonedaFE').removeClass("form-control is-invalid");
  $('#txtMonedaFE').addClass("form-control is-valid");
  $('#txtMonedaF').removeClass("form-control is-invalid");
  $('#txtMonedaF').addClass("form-control is-valid");
  result=true;
}else{
  $('#txtMonedaFE').addClass("form-control is-invalid");
  $('#txtMonedaF').addClass("form-control is-invalid");
  result=false
}
return result;

}

function validarNumeroFactura(modo){
  var result=false;
  if (modo==2) {
    var numeroFac= $("#txtNumeroFacturaE").val();
  }else{
    var numeroFac= $("#txtNumeroFactura").val();
  }

  if(numeroFac !=''){
    $('#txtNumeroFactura').removeClass("form-control is-invalid");
    $('#txtNumeroFactura').addClass("form-control is-valid");
    $('#txtNumeroFacturaE').removeClass("form-control is-invalid");
    $('#txtNumeroFacturaE').addClass("form-control is-valid");
    result=true;
  }else{
    $('#txtNumeroFactura').removeClass("form-control is-valid");
    $('#txtNumeroFactura').addClass("form-control is-invalid");
    $('#txtNumeroFacturaE').removeClass("form-control is-valid");
    $('#txtNumeroFacturaE').addClass("form-control is-invalid");
    result=false;
  }
  return result;
}


function validarFecha(modo){
   var result=false;
  if (modo==2) {
    var fecha = $("#datepickerCE").val();
  }else{
    var fecha = $("#datepickerF").val();
  }

  if(fecha !=''){
    $('#datepickerF').removeClass("form-control is-invalid");
    $('#datepickerF').addClass("form-control is-valid");
    $('#datepickerCE').removeClass("form-control is-invalid");
    $('#datepickerCE').addClass("form-control is-valid");
      result=true;
  }else{
     // $('#datepickerF').removeClass("form-control is-valid");
     $('#datepickerF').addClass("form-control is-invalid");
     $('#datepickerCE').addClass("form-control is-invalid");
      result=false;
   }
   return result;
 }

/*$(document).ready(function()
  {
  $("#txtNumeroFactura").focus(function(){
        $(this).css("background-color", "#FFFFCC");
  });

  $("#texto2").focus(function(){
    $(this).hide("slow");
  });
});*/

function guardarDetalleCompra(event){


  var idProducto = $('#producto').val();
  var idCompra = $('#idCompra').val();
  var cantidad = $('#cantidadC').val();
  var importe = $('#precioC').val();
  var datos = new FormData();
  var accion = "insert";

  datos.append("accion",accion);
  datos.append("idProducto",idProducto);
  datos.append("idCompra",idCompra);
  datos.append("cantidad",cantidad);
  datos.append("importe",importe);

  $.ajax({
    url: "ajax/ajaxFacturasCompras.php",
    method : "POST",
    data: datos,
    chache: false,
    contentType: false,
    processData:false,
    dataType: "json",
    success: function(respuesta){
      if(respuesta){
        $('#myModalCompra').modal('hide');
        Swal.fire({
         position: "top-end",
         icon: "success",
         title: "Item agregado",
         showConfirmButton: false,
         timer: 1100
       })

        mostrar_itemsFactura(idCompra,"nada");

      }else{
        Swal.fire({
         icon: "error",
         title: "Oops...",
         text: "A ocurrido un error!",
         footer: "<a href>Ver que mensaje dar?</a>"
       })
      }
    }
  });
}

function mostrar_itemsFactura(id,accion){

  if(accion=="nada"){
    var parametros={"action":"ajax","idCompra":id,"action2":"nada"};
  }

  if(accion=="editar"){
    var parametros={"action":"ajax","idCompra":id,"action2":"editar"};
  }
  $.ajax({
    url:'ajax/itemsFactura.php',
    data: parametros,
    beforeSend: function(objeto){
     $('.items').html('Cargando...');
   },
   success:function(data){
    $(".items").html(data).fadeIn('slow');
  }
})
}

function buscar_proveedor_factura(id,paso){

  var datos = new FormData();

  var accion = "buscarProveedor";
  datos.append("accion",accion);
  datos.append("idProveedor",id,);

  $.ajax({
   url: "ajax/ajaxFacturasCompras.php",
   method : "POST",
   data: datos,
   chache: false,
   contentType: false,
   processData:false,
   dataType: "json",
   success: function(respuesta){


     if(paso=="1"){
      document.querySelector('#proveedorNE').innerText = respuesta["razon_social"];
      document.querySelector('#direccionE').innerText = respuesta["direccion"];
      document.querySelector('#emailE').innerText = respuesta["email"];
      document.querySelector('#telefonoE').innerText = respuesta["telefono"];
      document.querySelector('#autorizo').innerText = respuesta["razon_social"];
      document.querySelector('#cancelo').innerText = respuesta["razon_social"];
    }if(paso=="2"){
      document.querySelector('#proveedorNF').innerText = respuesta["razon_social"];
      document.querySelector('#direccionF').innerText = respuesta["direccion"];
      document.querySelector('#emailF').innerText = respuesta["email"];
      document.querySelector('#telefonoF').innerText = respuesta["telefono"];

    }

  }


})
}


$(document).ready(function() {
  $( ".proveedor2" ).select2({
    ajax: {
      url: "ajax/buscarProveedor.php",
      dataType: 'json',
      delay: 250,
      data: function (params) {
        return {
                q: params.term // search term
              };
            },
            processResults: function (data) {
              return {
                results: data
              };
            },
            cache: false
          },
          minimumInputLength: 2
        }).on('change', function (e){

          var idProveedor = $('.proveedor2').select2('data')[0].id;
          var nombre = $('.proveedor2').select2('data')[0].text;
          var email = $('.proveedor2').select2('data')[0].email;
          var telefono = $('.proveedor2').select2('data')[0].telefono;
          var direccion = $('.proveedor2').select2('data')[0].direccion;
          $('#idProveedorF').html(idProveedor);
          $('#proveedorNF').html(nombre);
          $('#emailF').html(email);
          $('#telefonoF').html(telefono);
          $('#direccionF').html(direccion);
          guardar_proveedorFactura(idProveedor);
          $("#proveedor2").select2('data', null);

        })
      });




$(document).ready(function() {
  $( ".producto" ).select2({
    ajax: {
      url: "ajax/buscarProductos.php",
      dataType: 'json',
      delay: 250,
      data: function (params) {
        return {
                q: params.term // search term
              };
            },
            processResults: function (data) {
              return {
                results: data
              };
            },
            cache: true
          },
          minimumInputLength: 2
        }).on('change', function (e){

          $("#descripcion").val($('.producto').select2('data')[0].nombre);
          $("#precio").val($('.producto').select2('data')[0].precio);



        })
         $("#producto").empty();
      });


function eliminar_item(id){
  var idOrden = $('#idOrdenT1').val();
  $.ajax({
    type: "GET",
    url: "ajax/items.php",
    data: "action=ajax&id="+id,
    beforeSend: function(objeto){
     $('.items').html('Cargando...');
   },
   success: function(data){
        //$(".items").html(data).fadeIn('slow');
        mostrar_items(idOrden,"nada");
      }
    });
}



/*function eliminarBorrador(){

  var datos = new FormData();
var accion = "elimiarBorrador";
 var idCompra = $('#idCompra').val();
datos.append("idCompra",idCompra);
datos.append("accion",accion);

$.ajax({
  url: "ajax/ajaxFacturasCompras.php",
  method : "POST",
  data: datos,
  chache: false,
  contentType: false,
  processData:false,
  dataType: "json",
  success: function(respuesta){

   if(respuesta){
    $('#ModalADDFacturas').modal('hide');
    Swal.fire({
     position: "top-end",
     icon: "success",
     title: "Entrada eliminada con exito.",
     showConfirmButton: false,
     timer: 1500
   })
    setTimeout(function() {location.reload();}, 1505)
  }

}*/

function guardar_compra(estado){

 if(estado==1){
  var var_estado  =2;
}
if(estado==2){
  var var_estado  =3;
}
if(estado==3){
  var var_estado  =4;
}


if(!validarReceptor(1) || !validarMoneda(1) || !validarNumeroFactura(1) || !validarDeposito(1)
  || !validarMetodoEnvio(1) || !validarFormaPago(1) || !validarFecha(1) ){
  Swal.fire({
  icon: 'error',
  title: 'Oops...',
  text: 'Hay campos obligatorios sin completar!',

})
}else{

var idOrden = $('#txtOrdenCompra').val();
var idCompra = $('#idCompra').val();
var plazo = $('#txtEnvioF').val();
var forma_pago = $('#txtFormaPagoF').val();
var fecha = $('#datepickerF').val();
var factura = $('#txtNumeroFactura').val();
var receptor = $('#txtReceptor').val();
var deposito = $('#txtDeposito').val();
var moneda = $('#txtMonedaF').val();
var datos = new FormData();
var accion = "update";

datos.append("accion",accion);
datos.append("idOrden",idOrden);
datos.append("idCompra",idCompra);
datos.append("plazo",plazo);
datos.append("forma_pago",forma_pago);
datos.append("fecha",fecha);
datos.append("factura",factura);
datos.append("receptor",receptor);
datos.append("deposito",deposito);
datos.append("moneda",moneda)
datos.append("estado",var_estado);

$.ajax({
  url: "ajax/ajaxFacturasCompras.php",
  method : "POST",
  data: datos,
  chache: false,
  contentType: false,
  processData:false,
  dataType: "json",
  success: function(respuesta){

   if(respuesta){
    $('#ModalADDFacturas').modal('hide');
    Swal.fire({
     position: "top-end",
     icon: "success",
     title: "Entrada emitida con exito.",
     showConfirmButton: false,
     timer: 1500
   })
    setTimeout(function() {location.reload();}, 1505)
  }else{

    Swal.fire({
     icon: "error",
     title: "Oops...",
     text: "A ocurrido un error!"
     
   })
  }
}
});
}

}

$(function(){
  $("#datepickerF").datepicker({
    dateFormat: "yy-mm-dd"
  });
});

$(function(){
  $("#datepickerCE").datepicker({
    dateFormat: "yy-mm-dd"
  });
});



function guardar_compraEditadas(estado){

 if(estado==1){
  var var_estado  =2;
}
if(estado==2){
  var var_estado  =3;
}
if(estado==3){
  var var_estado  =4;
}

if(!validarReceptor(2) || !validarMoneda(2) || !validarNumeroFactura(2) || !validarDeposito(2)
  || !validarMetodoEnvio(2) || !validarFormaPago(2) || !validarFecha(2) ){
  Swal.fire({
  icon: 'error',
  title: 'Oops...',
  text: 'Hay campos obligatorios sin completar!',

})
}else{
var idOrden = $('#txtOrdenCompraE').val();
var idCompra = $('#idCompra1E').val();
var plazo = $('#txtEnvioFE').val();
var forma_pago = $('#txtFormaPagoFE').val();
var fecha = $('#datepickerCE').val();
var factura = $('#txtNumeroFacturaE').val();
var receptor = $('#txtReceptorE').val();
var deposito = $('#txtDepositoE').val();
var moneda = $('#txtMonedaFE').val();
var datos = new FormData();
var accion = "update";

datos.append("accion",accion);
datos.append("idOrden",idOrden);
datos.append("idCompra",idCompra);
datos.append("plazo",plazo);
datos.append("forma_pago",forma_pago);
datos.append("fecha",fecha);
datos.append("factura",factura);
datos.append("receptor",receptor);
datos.append("deposito",deposito);
datos.append("moneda",moneda)
datos.append("estado",var_estado);

if(var_estado>1){
  $.ajax({
    url: "ajax/ajaxFacturasCompras.php",
    method : "POST",
    data: datos,
    chache: false,
    contentType: false,
    processData:false,
    dataType: "json",
    success: function(respuesta){

     if(respuesta){
    console.log( notificacion("Orden de compra", "Actualizado orden # "+idOrden)) ;
     notificacion("Orden de compra", "Actualizado orden # "+idOrden);
      $('#ModalEditarCompras').modal('hide');
      Swal.fire({
       position: "top-end",
       icon: "success",
       title: "Compra actualizada con exito.",
       showConfirmButton: false,
       timer: 1500
     })
      setTimeout(function() {location.reload();}, 1505)
    }else{

      Swal.fire({
       icon: "error",
       title: "Oops...",
       text: "A ocurrido un error!",
       footer: "<a href>Ver que mensaje dar?</a>"
     })
    }
  }
});
}else if(var_estado==4){
 Swal.fire({
   icon: "error",
   title: "Oops...",
   text: "Esta orden ya esta cancelada",

 })

}
}




}


function guardar_proveedorFactura(idProveedor){


  var idCompra = $('#idCompra').val();
  var datos = new FormData();
  var accion = "uProveedor";
  datos.append("accion",accion);
  datos.append("idProveedor",idProveedor);
  datos.append("idCompra",idCompra);

  $.ajax({
    url: "ajax/ajaxFacturasCompras.php",
    method : "POST",
    data: datos,
    chache: false,
    contentType: false,
    processData:false,
    dataType: "json",
    success: function(respuesta){


    }
  });

}


$(".btnEliminarFactura").click(function(){

 var idCompra =  $(this).attr("idCompra");
 var datos = new FormData();
 var accion = "delete";

 datos.append("accion",accion);
 datos.append("idCompra",idCompra);

 const swalWithBootstrapButtons = Swal.mixin({
  customClass: {
    confirmButton: 'btn btn-success',
    cancelButton: 'btn btn-danger'
  },
  buttonsStyling: false
})

 swalWithBootstrapButtons.fire({
  title: 'Estas seguro?',
  text: "De eliminar la oden!",
  icon: 'warning',
  showCancelButton: true,
  confirmButtonText: 'Si! ',
  cancelButtonText: 'No!',
  reverseButtons: true
}).then((result) => {
  if (result.isConfirmed) {

   $.ajax({
    url: "ajax/ajaxFacturasCompras.php",
    method : "POST",
    data: datos,
    chache: false,
    contentType: false,
    processData:false,
    dataType: "json",
    success: function(respuesta){
     if(respuesta){
      swalWithBootstrapButtons.fire(
       'Eliminado!',
       'La orden a sido eliminada.',
       'success'
       ).then((result)=> {
         if (result.isConfirmed)
           location.reload();
       });

     }else{
      Swal.fire({
       icon: 'error',
       title: 'Oops...',
       text: 'Ocurrio un error eliminando la orden!',
       footer: '<a href>Why do I have this issue?</a>'
     })
    }

  }
});

 } else if (
  /* Read more about handling dismissals below */
  result.dismiss === Swal.DismissReason.cancel
  ) {
  swalWithBootstrapButtons.fire(
    'Cancelado',
    'Has cambiado de idea. Bien :)',
    'error'
    )
}
})

})


function datosImprimir(idOrden){


  VentanaCentrada('./pdf/documentos/orden.php?idOrden='+idOrden,'Orden','','1024','768','true');


}

//Busqueda en a tabla

$(document).ready(function(){
 $("#searchfacturas").keyup(function(){
 _this = this;
 // Show only matching TR, hide rest of them
 $.each($("#factura tbody tr"), function() {
 if($(this).text().toLowerCase().indexOf($(_this).val().toLowerCase()) === -1)
 $(this).hide();
 else
 $(this).show();
 });
 });
});



//Aqui va el paginado


$(document).ready(function(){
    
 addPagerToTables('#factura', 8);
    
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
    let numPages = Math.ceil(tBodyRows.length/rowsPerPage);

    let colCount = 
    [].slice.call(
        table.querySelector('tr').cells
    )
    .reduce((a,b) => a + parseInt(b.colSpan), 0);

    table
    .createTFoot()
    .insertRow()
    .innerHTML = `<td colspan=${colCount}><div class="nav"></div></td>`;

    if(numPages == 1)
        return;

    for(i = 0;i < numPages;i++) {

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




