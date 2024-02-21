function agregarCliente(valor){

   //Obtengo los valores
   
   var nombre = $('#txt_nombre_cliente').val();
   var ruc = $("#txt_ruc").val();
   var direccion = $('#txt_direccion').val();
   var telefono = $('#txt_telefono').val();
   var fecha_nacimiento = $("#datepicker_cliente").val();
   var email = $('#txt_email').val();
   var tipo_cliente = $("#txt_tipo_cliente").val();
   //var fecha = $('#datepicker').val();
   
   var datos = new FormData();
   var accion = "insert";  

   datos.append("accion",accion);    
   datos.append("nombre",nombre); 
   datos.append("ruc",ruc);
   datos.append("direccion",direccion);
   datos.append("telefono",telefono);    
   datos.append("fecha_nacimiento",fecha_nacimiento); 
   datos.append("email",email);
   datos.append("tipo_cliente",tipo_cliente);
  

   $.ajax({
    url: "ajax/ajaxClientes.php",
    method : "POST",
    data: datos,
    chache: false,
    contentType: false,
    processData:false,
    dataType: "json",
    success: function(respuesta){

     if(respuesta){
      $('#ModalADDClientes').modal('hide');
      Swal.fire({
       position: "top-end",
       icon: "success",
       title: "Cliente agregado",
       showConfirmButton: false,
       timer: 1500
     })
       
       
       if(valor==1)
       {
        setTimeout(function() {location.reload();}, 1505)
      }if(valor==2){

         $('#buscar_cliente').val(nombre);
         $('#ModalNuevoClientes').modal('toggle');
         $('#ModalNuevoClientes').modal('hide');
        
      }

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



 $(function(){
    $("#datepicker_cliente").datepicker({
        //dateFormat: "dd/mm/yy"
        dateFormat: "yy-mm-dd"
    });
});
     
$(function(){
    $("#datepicker_clienteE").datepicker({
        dateFormat: "yy-mm-dd"
    });
});
    



 $(".btnEliminarCliente").click(function(){  

   var idCliente=  $(this).attr("idCliente");

   var datos = new FormData();

   var accion = "delete";

   datos.append("accion",accion);
   datos.append("idCliente",idCliente);

   const swalWithBootstrapButtons = Swal.mixin({
    customClass: {
      confirmButton: 'btn btn-success',
      cancelButton: 'btn btn-danger'
    },
    buttonsStyling: false      
  })

   swalWithBootstrapButtons.fire({
    title: 'Confirma dar de baja al cliente?',  
    showCancelButton: true,
    confirmButtonText: 'Si',
    cancelButtonText: 'No',
    reverseButtons: true
  }).then((result) => {
    if (result.isConfirmed) {

     $.ajax({
      url: "ajax/ajaxClientes.php",
      method : "POST",
      data: datos,
      chache: false,
      contentType: false,
      processData:false,
      dataType: "json",
      success: function(respuesta){
       if(respuesta){
        swalWithBootstrapButtons.fire(
         'Desahabilitado el cliente'                         
         ).then((result)=> {
           if (result.isConfirmed)
             location.reload();
         });

       }else{
        Swal.fire({
         icon: 'error',
         title: 'Ocurrio un error'        
       })
      }

    }
  }); 
   }
})
  })


 $(".btnEditarCliente").click(function(){

  var idCliente = $(this).attr("idCliente");
  var datos = new FormData();  
  var accion = "buscar";
  datos.append("accion",accion);
  datos.append("idCliente",idCliente);

  $.ajax({
   url: "ajax/ajaxClientes.php",
   method : "POST",
   data: datos,
   chache: false,
   contentType: false,
   processData:false,
   dataType: "json",
   success: function(respuesta){     
    $("#txt_nombre_clienteE").val(respuesta["nombres"]);
    $("#txt_rucE").val(respuesta["nro_documento"]);
    $("#txt_direccionE").val(respuesta["direccion"]);  
    $("#txt_telefonoE").val(respuesta["telefono"]); 
    $("#txt_celularE").val(respuesta["celular"]); 
    $("#txt_emailE").val(respuesta["email"]); 
    $("#datepicker_clienteE").val(respuesta["fecha_nacimiento"]);
    $("#txt_tipo_clienteE > option[value="+respuesta["id_tipo_cliente"]+"]").attr("selected",true);
    $("#idCliente").val(respuesta["id_cliente"]); 
    
    if (respuesta["fecha_baja"]==null) {
     $("#customSwitch2").prop('checked', true);
    } else{
     $("#customSwitch2").prop('checked', false);
    }   
                  
    
  }
  

})

})

function updateCliente(event){

   var idCliente=  $('#idCliente').val();
   
   var nombre = $('#txt_nombre_clienteE').val();
   var ruc = $("#txt_rucE").val();
   var direccion = $('#txt_direccionE').val();
   var telefono = $('#txt_telefonoE').val();
   var fecha_nacimiento = $("#datepicker_clienteE").val();
   var email = $('#txt_emailE').val();  
   var tipo_cliente = $('#txt_tipo_clienteE').val();

   var control = document.getElementById("customSwitch2");
     var habilitado;
   if(control.checked)
   {
       habilitado= true;
   }
    else
   {
        habilitado= false;
   }
   
   var datos = new FormData();
   var accion = "update";  

   datos.append("idCliente",idCliente);
   datos.append("accion",accion);    
   datos.append("nombre",nombre); 
   datos.append("ruc",ruc);
   datos.append("direccion",direccion);
   datos.append("telefono",telefono);    
   datos.append("fecha_nacimiento",fecha_nacimiento); 
   datos.append("email",email);
   datos.append("tipo_cliente",tipo_cliente);
   datos.append("habilitado",habilitado);
   

   $.ajax({
     url: "ajax/ajaxClientes.php",
     method : "POST",
     data: datos,
     chache: false,
     contentType: false,
     processData:false,
     dataType: "json",
     success: function(respuesta){

      if(respuesta){
        $('#ModalEditarCliente').modal('hide');
        Swal.fire({
         position: "top-end",
         icon: "success",
         title: "Cliente modificado",
         showConfirmButton: false,
         timer: 1500
       })
        setTimeout(function() {location.reload();}, 1505)   

      }else{

        Swal.fire({
         icon: "error",
         title: "A ocurrido un error"         
        // footer: "<a href>Ver que mensaje dar?</a>"
      })
      }

    }
  }); 

 }