function agregarProveedor(event){

   //Obtengo los valores
   
   var razon_social = $('#txt_razon_social').val();
   var ruc = $("#txt_ruc").val();
   var direccion = $('#txt_direccion').val();
   var telefono = $('#txt_telefono').val();
   var celular = $("#txt_celular").val();
   var email = $('#txt_email').val();
   var web = $('#txt_web').val();
   
   var datos = new FormData();
   var accion = "insert";  

   datos.append("accion",accion);    
   datos.append("razon_social",razon_social); 
   datos.append("ruc",ruc);
   datos.append("direccion",direccion);
   datos.append("telefono",telefono);    
   datos.append("celular",celular); 
   datos.append("email",email);
   datos.append("web",web);

   $.ajax({
    url: "ajax/ajaxProveedores.php",
    method : "POST",
    data: datos,
    chache: false,
    contentType: false,
    processData:false,
    dataType: "json",
    success: function(respuesta){

     if(respuesta){
      $('#ModalADDProveedor').modal('hide');
      Swal.fire({
       position: "top-end",
       icon: "success",
       title: "Proveedor agregado",
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


 }



 $(".btnEliminarProveedor").click(function(){  

   var idProveedor=  $(this).attr("idProveedor");

   var datos = new FormData();

   var accion = "delete";

   datos.append("accion",accion);
   datos.append("idProveedor",idProveedor);

   const swalWithBootstrapButtons = Swal.mixin({
    customClass: {
      confirmButton: 'btn btn-success',
      cancelButton: 'btn btn-danger'
    },
    buttonsStyling: false      
  })

   swalWithBootstrapButtons.fire({
    title: 'Confirma dar de baja al proveedor?',  
    showCancelButton: true,
    confirmButtonText: 'Si',
    cancelButtonText: 'No',
    reverseButtons: true
  }).then((result) => {
    if (result.isConfirmed) {

     $.ajax({
      url: "ajax/ajaxProveedores.php",
      method : "POST",
      data: datos,
      chache: false,
      contentType: false,
      processData:false,
      dataType: "json",
      success: function(respuesta){
       if(respuesta){
        swalWithBootstrapButtons.fire(
         'Desahabilitado el proveedor'                         
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


 $(".btnEditarProveedor").click(function(){

  var idProveedor = $(this).attr("idProveedor");
  var datos = new FormData();  
  var accion = "buscar";
  datos.append("accion",accion);
  datos.append("idProveedor",idProveedor);

  $.ajax({
   url: "ajax/ajaxProveedores.php",
   method : "POST",
   data: datos,
   chache: false,
   contentType: false,
   processData:false,
   dataType: "json",
   success: function(respuesta){     
    $("#txt_razon_socialE").val(respuesta["razon_social"]);
    $("#txt_rucE").val(respuesta["ruc"]);
    $("#txt_direccionE").val(respuesta["direccion"]);  
    $("#txt_telefonoE").val(respuesta["telefono"]); 
    $("#txt_celularE").val(respuesta["celular"]); 
    $("#txt_emailE").val(respuesta["email"]); 
    $("#txt_webE").val(respuesta["web"]);  
    $("#idProveedor").val(respuesta["id_proveedor"]); 
    
    /*if (respuesta["fecha_baja"]==null) {
     $("#customSwitch1").prop('checked', true);
    } else{
     $("#customSwitch1").prop('checked', false);
    }*/


     $("#customSwitch1").on( 'change', function() {
    if( $(this).is(':checked') ) {
        $("#customSwitch1").prop('checked', true);

    } else {
       $("#customSwitch1").prop('checked', false);
    }
});  
                  
    
  }
  

})

})

function updateProveedorJS(event){

   var idProveedor=  $('#idProveedor').val();
   
   var razon_social = $('#txt_razon_socialE').val();
   var ruc = $("#txt_rucE").val();
   var direccion = $('#txt_direccionE').val();
   var telefono = $('#txt_telefonoE').val();
   var celular = $("#txt_celularE").val();
   var email = $('#txt_emailE').val();
   var web = $('#txt_webE').val();

   //var control = document.getElementById("customSwitch1");
     var habilitado;

     
   if($('#customSwitch1').is(':checked'))
   {
       habilitado= 1;
   }
    else
   {
        habilitado= 0;
   }
   
   var datos = new FormData();
   var accion = "update";  

   datos.append("idProveedor",idProveedor);
   datos.append("accion",accion);    
   datos.append("razon_social",razon_social); 
   datos.append("ruc",ruc);
   datos.append("direccion",direccion);
   datos.append("telefono",telefono);    
   datos.append("celular",celular); 
   datos.append("email",email);
   datos.append("web",web);
   datos.append("habilitado",habilitado);
   

   $.ajax({
     url: "ajax/ajaxProveedores.php",
     method : "POST",
     data: datos,
     chache: false,
     contentType: false,
     processData:false,
     dataType: "json",
     success: function(respuesta){

      if(respuesta){
        $('#ModalEditarProveedor').modal('hide');
        Swal.fire({
         position: "top-end",
         icon: "success",
         title: "Proveedor modificado",
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