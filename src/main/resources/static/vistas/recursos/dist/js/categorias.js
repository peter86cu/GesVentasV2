function agregarCategoria(event){

   //Obtengo los valores
   
   var categoria = $('#txt_Nombre').val();
   var descripcion = $("#txt_descripcion").val();
   var estado = $('#select_estado').val();
   
   var datos = new FormData();
   var accion = "insert";  

   datos.append("accion",accion);    
   datos.append("categoria",categoria); 
   datos.append("descripcion",descripcion);
   datos.append("estado",estado);

   $.ajax({
    url: "ajax/ajaxCategorias.php",
    method : "POST",
    data: datos,
    chache: false,
    contentType: false,
    processData:false,
    dataType: "json",
    success: function(respuesta){

     if(respuesta){
      $('#ModalADDCategoria').modal('hide');
      Swal.fire({
       position: "top-end",
       icon: "success",
       title: "Categoría agregado",
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



 $(".btnEliminarCategoria").click(function(){  

   var idCategoria=  $(this).attr("idCategoria");

   var datos = new FormData();

   var accion = "delete";

   datos.append("accion",accion);
   datos.append("idCategoria",idCategoria);

   const swalWithBootstrapButtons = Swal.mixin({
    customClass: {
      confirmButton: 'btn btn-success',
      cancelButton: 'btn btn-danger'
    },
    buttonsStyling: false      
  })

   swalWithBootstrapButtons.fire({
    title: 'Confirma desahabilitar la categoría?',    
    showCancelButton: true,
    confirmButtonText: 'Si',
    cancelButtonText: 'No',
    reverseButtons: true
  }).then((result) => {
    if (result.isConfirmed) {

     $.ajax({
      url: "ajax/ajaxCategorias.php",
      method : "POST",
      data: datos,
      chache: false,
      contentType: false,
      processData:false,
      dataType: "json",
      success: function(respuesta){
       if(respuesta){
        swalWithBootstrapButtons.fire(
         'La categoría ha sido desahabilitado'                        
         ).then((result)=> {
           if (result.isConfirmed)
             location.reload();
         });

       }else{
        Swal.fire({
         icon: 'error',
         title: 'No se pudo desahabilitar la categoría' 
         
       })
      }

    }
  });  
   } 
})

})


 $(".btnEditarCategoria").click(function(){

  var idCategoria = $(this).attr("idCategoria");
  var datos = new FormData();  
  var accion = "buscar";
  datos.append("accion",accion);
  datos.append("idCategoria",idCategoria,);

  $.ajax({
   url: "ajax/ajaxCategorias.php",
   method : "POST",
   data: datos,
   chache: false,
   contentType: false,
   processData:false,
   dataType: "json",
   success: function(respuesta){     
    $("#idCategoria").val(respuesta["id_categoria_producto"]);
    $("#txt_NombreEdit").val(respuesta["categoria"]);
    $("#txt_descripcionEdit").val(respuesta["descripcion"]);                     
    $("#select_estadoEdit > option[value="+respuesta["estado"]+"]").attr("selected",true);

  }
  

})

})

function updateCategoria(event){ 

   var idCategoria=  $('#idCategoria').val();
   var categoria = $('#txt_NombreEdit').val();
   var descripcion = $("#txt_descripcionEdit").val();
   var estado = $('#select_estadoEdit').val();
   var datos = new FormData();
   var accion = "update";
   
   datos.append("accion",accion);    
   datos.append("categoria",categoria); 
   datos.append("descripcion",descripcion);
   datos.append("estado",estado);
   datos.append("idCategoria",idCategoria);
   

   $.ajax({
     url: "ajax/ajaxCategorias.php",
     method : "POST",
     data: datos,
     chache: false,
     contentType: false,
     processData:false,
     dataType: "json",
     success: function(respuesta){

      if(respuesta){
        $('#ModalEditarCategoria').modal('hide');
        Swal.fire({
         position: "top-end",
         icon: "success",
         title: "Categoría modificado",
         showConfirmButton: false,
         timer: 1500
       })
        setTimeout(function() {location.reload();}, 1505)   

      }else{

        Swal.fire({
         icon: "error",
         title: "No se pudo actualizar el rol"
         //text: "A ocurrido un error!",
        // footer: "<a href>Ver que mensaje dar?</a>"
      })
      }

    }
  }); 

 }