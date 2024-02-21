function agregarBilletes(event){

   //Obtengo los valores
   
   var idMoneda = $('#idMoneda1').val();
   var monto = $('#txt_monto').val();
   var descripcion = $("#txt_descripcion").val();
   var estado = $('#select_estado').val();
   var datos = new FormData();
   var accion = "insert";  

   datos.append("accion",accion);    
   datos.append("monto",monto); 
   datos.append("descripcion",descripcion);
   datos.append("idMoneda",idMoneda);
   datos.append("estado",estado);

   $.ajax({
    url: "ajax/ajaxBilletes.php",
    method : "POST",
    data: datos,
    chache: false,
    contentType: false,
    processData:false,
    dataType: "json",
    success: function(respuesta){

     if(respuesta){
      $('#ModalADDBilletes').modal('hide');
      Swal.fire({
       position: "top-end",
       icon: "success",
       title: "Billete agregado",
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



 $(".btnEliminarBilletes").click(function(){  

   var idBillete=  $(this).attr("idBillete");

   var datos = new FormData();

   var accion = "delete";

   datos.append("accion",accion);
   datos.append("idBillete",idBillete);

   const swalWithBootstrapButtons = Swal.mixin({
    customClass: {
      confirmButton: 'btn btn-success',
      cancelButton: 'btn btn-danger'
    },
    buttonsStyling: false      
  })

   swalWithBootstrapButtons.fire({
    title: 'Confirma desahabilitar este billete ?',    
    showCancelButton: true,
    confirmButtonText: 'Si',
    cancelButtonText: 'No',
    reverseButtons: true
  }).then((result) => {
    if (result.isConfirmed) {

     $.ajax({
      url: "ajax/ajaxBilletes.php",
      method : "POST",
      data: datos,
      chache: false,
      contentType: false,
      processData:false,
      dataType: "json",
      success: function(respuesta){
       if(respuesta){
        swalWithBootstrapButtons.fire(
         'El billete ha sido desahabilitado'                        
         ).then((result)=> {
           if (result.isConfirmed)
             location.reload();
         });

       }else{
        Swal.fire({
         icon: 'error',
         title: 'No se pudo desahabilitar el billete' 
         
       })
      }

    }
  });  
   } 
})

})





 $(".btnADDBilletes").click(function(){

  var idMoneda = $(this).attr("idMoneda");
     
    $("#idMoneda1").val(idMoneda);
   
     $('#ModalADDBillete').modal('hide');

})



 $(".btnEditarBilletes").click(function(){

  var idBillete = $(this).attr("idBillete");
  var datos = new FormData();  
  var accion = "buscar";
  datos.append("accion",accion);
  datos.append("idBillete",idBillete,);

  $.ajax({
   url: "ajax/ajaxBilletes.php",
   method : "POST",
   data: datos,
   chache: false,
   contentType: false,
   processData:false,
   dataType: "json",
   success: function(respuesta){     
    $("#idBillete").val(respuesta["id_billete"]);
    $("#idMoneda2").val(respuesta["id_moneda"]);
    $("#txt_montoE").val(respuesta["monto"]);
    $("#txt_descripcionEdit").val(respuesta["descripcion"]);                     
    $("#select_estadoEdit > option[value="+respuesta["estado"]+"]").attr("selected",true);

  }
  

})

})

function updateBillete(event){ 

   var idBillete =  $('#idBillete').val();
   var idMoneda =  $('#idMoneda2').val();
   var monto = $('#txt_montoE').val();
   var descripcion = $("#txt_descripcionEdit").val();
   var estado = $('#select_estadoEdit').val();
   var datos = new FormData();
   var accion = "update";
   
   datos.append("accion",accion);    
   datos.append("monto",monto); 
   datos.append("descripcion",descripcion);
   datos.append("estado",estado);
   datos.append("idBillete",idBillete);
   datos.append("idMoneda",idMoneda);
   

   $.ajax({
     url: "ajax/ajaxBilletes.php",
     method : "POST",
     data: datos,
     chache: false,
     contentType: false,
     processData:false,
     dataType: "json",
     success: function(respuesta){

      if(respuesta){
        $('#ModalEditarBillete').modal('hide');
        Swal.fire({
         position: "top-end",
         icon: "success",
         title: "Billete modificado",
         showConfirmButton: false,
         timer: 1500
       })
        setTimeout(function() {location.reload();}, 1505)   

      }else{

        Swal.fire({
         icon: "error",
         title: "No se pudo actualizar el billete"
         //text: "A ocurrido un error!",
        // footer: "<a href>Ver que mensaje dar?</a>"
      })
      }

    }
  }); 

 }