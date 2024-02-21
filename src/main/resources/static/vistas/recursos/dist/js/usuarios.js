function agregarUsuario(event){

   //Obtengo los valores
   
   var nombre = $('#txt_nombre').val();
   var usuario = $("#txt_user").val();
   var pass = $('#txt_pass').val();
   var mail = $('#txt_mail').val();
   var sucursal = $('#txt_sucursal').val();
   var rol = $('#txt_rol').val();
   
   var datos = new FormData();
   var accion = "insert";   

   datos.append("accion",accion);    
   datos.append("rol",rol); 
   datos.append("nombre",nombre);
   datos.append("usuario",usuario);
   datos.append("pass",pass); 
   datos.append("mail",mail);
   datos.append("sucursal",sucursal);

   $.ajax({
    url: "ajax/ajaxUsuarios.php",
    method : "POST",
    data: datos,
    chache: false,
    contentType: false,
    processData:false,
    dataType: "json",
    success: function(respuesta){
     if(respuesta){ 
      $('#ModalADDUsuarios').modal('hide');     
      Swal.fire({
       position: "top-end",
       icon: "success",
       title: "Usuario agregado",
       showConfirmButton: false,
       timer: 1500
     })

      setTimeout(function() {location.reload();}, 1505)
    }else{

      Swal.fire({
       icon: "error",
       title: "Oops...",
       text: "A ocurrido un error!",
        // footer: "<a href>Ver que mensaje dar?</a>"
      })
    }
  }
});	


 }



 $(".btnEliminarUsuario").click(function(){  

   var idUsuario=  $(this).attr("idUsuario");

   var datos = new FormData();

   var accion = "delete";

   datos.append("accion",accion);
   datos.append("idUsuario",idUsuario);

   const swalWithBootstrapButtons = Swal.mixin({
    customClass: {
      confirmButton: 'btn btn-success',
      cancelButton: 'btn btn-danger'
    },
    buttonsStyling: false      
  })

   swalWithBootstrapButtons.fire({
    title: 'Confirma desahabilitar el usuario?',    
    showCancelButton: true,
    confirmButtonText: 'Si',
    cancelButtonText: 'No',
    reverseButtons: true
  }).then((result) => {
    if (result.isConfirmed) {

     $.ajax({
      url: "ajax/ajaxUsuarios.php",
      method : "POST",
      data: datos,
      chache: false,
      contentType: false,
      processData:false,
      dataType: "json",
      success: function(respuesta){
       if(respuesta){
        swalWithBootstrapButtons.fire(
         'Desahabilitado el usuario.'                          
         ).then((result)=> {
           if (result.isConfirmed)
             location.reload();
         });

       }else{
        Swal.fire({
         icon: 'error',
         title: 'Ocurrio un error'        
        // footer: '<a href>Why do I have this issue?</a>'
      })
      }

    }
  });   


   } 
})

})

 

 $(".btnEditarUsuario").click(function(){


   var idUsuario = $(this).attr("idUsuario");
   var datos = new FormData();

   var accion = "buscar";

   datos.append("accion",accion);
   datos.append("idUsuario",idUsuario,);


   $.ajax({
     url: "ajax/ajaxUsuarios.php",
     method : "POST",
     data: datos,
     chache: false,
     contentType: false,
     processData:false,
     dataType: "json",
     success: function(respuesta){

      $("#idUsuario").val(respuesta["id_usuario"]);
      $("#txt_nombreE").val(respuesta["nombres"]);
      $("#txt_userE").val(respuesta["usuario"]); 
      $("#txt_mailE").val(respuesta["email"]);                    
      $("#txt_sucursalE > option[value="+respuesta["sucursal"]+"]").attr("selected",true);
      $("#txt_rolE > option[value="+respuesta["nivel"]+"]").attr("selected",true);


    }


  });


 })


function updateUsuario(event){
  
   var idUsuario = $('#idUsuario').val();   
   var nombre = $('#txt_nombreE').val();
   var usuario = $("#txt_userE").val();
   var pass = $('#txt_passE').val();
   var mail = $('#txt_mailE').val();
   var sucursal = $('#txt_sucursalE').val();
   var rol = $('#txt_rolE').val(); 
   var datos = new FormData();
   var accion = "update";

   datos.append("accion",accion);    
   datos.append("rol",rol); 
   datos.append("nombre",nombre);
   datos.append("usuario",usuario);
   datos.append("pass",pass); 
   datos.append("mail",mail);
   datos.append("sucursal",sucursal);
   datos.append("idUsuario",idUsuario);

   $.ajax({
    url: "ajax/ajaxUsuarios.php",
    method : "POST",
    data: datos,
    chache: false,
    contentType: false,
    processData:false,
    dataType: "json",
    success: function(respuesta){
          
      if(respuesta){
     $('#ModalEditarUsuario').modal('hide'); 
      Swal.fire({
       position: "top-end",
       icon: "success",
       title: "Usuario modificado",
       showConfirmButton: false,
       timer: 1500
     })
       setTimeout(function() {location.reload();}, 1505)
    }else{

      Swal.fire({
       icon: "error",
       title: "Ocurrio un error."      
     })
    }
      
    }
  });
}






 