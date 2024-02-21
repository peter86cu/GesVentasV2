$(".btnEditarProducto").click(function(){

var idProducto = $(this).attr("idProducto");
var datos = new FormData();
var accion = "buscar";

datos.append("accion",accion);
datos.append("idProducto",idProducto,);

$.ajax({
       url: "ajax/ajaxProductos.php",
       method : "POST",
       data: datos,
       chache: false,
       contentType: false,
       processData:false,
       dataType: "json",
       success: function(respuesta){
      
        
           $("#idProducto").val(respuesta["id_producto"]);           
           $("#inputCodigoEdit").val(respuesta["codigo"]);
           $("#inputNombreEdit").val(respuesta["nombre"]);
           $("#inputMinimoEdit").val(respuesta["minimo"]);
           $("#inputPVentaEdit").val(respuesta["precio_venta"]);          
           $("#inputIVAVentaEdit > option[value="+respuesta["id_impuesto"]+"]").attr("selected",true);
           $("#inputCategoriaEdit > option[value="+respuesta["id_categoria"]+"]").attr("selected",true);
           $("#input_id_marcaEdit > option[value="+respuesta["id_marca"]+"]").attr("selected",true);
           $("#input_tipo_productoEdit > option[value="+respuesta["id_tipo_producto"]+"]").attr("selected",true);
           $("#inputUnidadEdit > option[value="+respuesta["id_unidad_medida"]+"]").attr("selected",true);
           $("#inputInveEdit > option[value="+respuesta["inventariable"]+"]").attr("selected",true);
           $("#inputDispoEdit > option[value="+respuesta["disponible"]+"]").attr("selected",true);
           $("#inputMonedaEdit > option[value="+respuesta["id_moneda"]+"]").attr("selected",true);           
           //document.getElementById("foto").val("vistas/recursos/dist/img/productos/"+respuesta["foto"]);
          
          // $("#foto").val("vistas/recursos/dist/img/productos/"+respuesta["foto"]);
          //document.getElementById("foto").val('');
           var imageObj = new Image();
          
          imageObj.src = "";
          imageObj.src = "vistas/recursos/dist/img/productos/"+respuesta["foto"];
           $("#img").remove();
                        $(".delPhoto").removeClass('notBlock');
                        $(".prevPhoto").append("<img id='img' src="+imageObj.src+">");
                        $(".upimg label").remove();
          
                         $('.delPhoto').click(function(){
                           
      $('#foto').val('');
      $(".delPhoto").addClass('notBlock');
      $("#img").remove();

    });


       }


  

})

})


function updateProducto(event){ 

   
  var idProducto=  $('#idProducto').val();;
	var codigo = $('#inputCodigoEdit').val();
   var nombre = $("#inputNombreEdit").val();
   var minimo = $('#inputMinimoEdit').val();
   var precioV = $('#inputPVentaEdit').val();
   var iva =     $('#inputIVAVentaEdit').val();
   var categoria = $('#inputCategoriaEdit').val();
   var marca = $('#input_id_marcaEdit').val();
   var tipo_producto = $('#input_tipo_productoEdit').val();
   var unidad_medida = $('#inputUnidadEdit').val();
   var inventario = $('#inputInveEdit').val();
   var disponible = $('#inputDispoEdit').val();
   var moneda = $('#inputMonedaEdit').val();
   
   
   var datos = new FormData();

var accion = "update";

var imagen  =$('#foto')[0].files[0];

datos.append("accion",accion);
datos.append("idProducto",idProducto);
datos.append("codigo",codigo);
datos.append("nombre",nombre);
datos.append("minimo",minimo);
datos.append("precioV",precioV);
datos.append("iva",iva);
datos.append("categoria",categoria);
datos.append("marca",marca);
datos.append("tipo_producto",tipo_producto);
datos.append("unidad_medida",unidad_medida);
datos.append("inventario",inventario);
datos.append("disponible",disponible);
datos.append("moneda",moneda);
datos.append('foto', imagen);


	
      $.ajax({
         url: "ajax/ajaxProductos.php",
         method : "POST",
         data: datos,
         chache: false,
         contentType: false,
         processData:false,
         dataType: "json",
         success: function(respuesta){

            if(respuesta){
           $('#ModalEditarProductos').modal('hide');
            Swal.fire({
               position: "top-end",
               icon: "success",
               title: "Producto actualizado",
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

function agregarProductos(event){
   
   //Obtengo los valores
   var idProducto=  $(this).attr("idProducto");
	var codigo = $('#inputCodigo').val();
   var nombre = $("#inputNombre").val();
   var minimo = $('#inputMinimo').val();
   var precioV = $('#inputPVenta').val();
   var iva =     $('#inputIVAVenta').val();
   var categoria = $('#inputCategoria').val();
   var marca = $('#input_id_marca').val();
   var tipo_producto = $('#input_tipo_producto').val();
   var unidad_medida = $('#inputUnidad').val();
   var inventario = $('#inputInve').val();
   var disponible = $('#inputDispo').val();
   var moneda = $('#inputMoneda').val(); 
   
   
   var datos = new FormData();

      var accion = "insert";

      var imagen  =$('#foto')[0].files[0];

      datos.append("accion",accion);
      datos.append("idProducto",idProducto);
      datos.append("codigo",codigo);
      datos.append("nombre",nombre);
      datos.append("minimo",minimo);
      datos.append("precioV",precioV);
      datos.append("iva",iva);
      datos.append("categoria",categoria);
      datos.append("marca",marca);
      datos.append("tipo_producto",tipo_producto);
      datos.append("unidad_medida",unidad_medida);
      datos.append("inventario",inventario);
      datos.append("disponible",disponible);
      datos.append("moneda",moneda);
      datos.append('foto', imagen);

  

   $.ajax({
      url: "ajax/ajaxProductos.php",
      method : "POST",
      data: datos,
      chache: false,
      contentType: false,
      processData:false,
      dataType: "json",
      success: function(respuesta){

         if(respuesta){
           $('#ModalADDProductos').modal('hide');
            Swal.fire({
               position: "top-end",
               icon: "success",
               title: "Producto guardado",
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


   $(".btnEliminarProducto").click(function(){  

   var idProducto=  $(this).attr("idProducto");

   var datos = new FormData();

   var accion = "delete";

   datos.append("accion",accion);
   datos.append("idProducto",idProducto);

   const swalWithBootstrapButtons = Swal.mixin({
      customClass: {
        confirmButton: 'btn btn-success',
        cancelButton: 'btn btn-danger'
      },
      buttonsStyling: false      
    })
    
    swalWithBootstrapButtons.fire({
      title: 'Estas seguro?',
      text: "Dar de baja al producto !",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Si! ',
      cancelButtonText: 'No!',
      reverseButtons: true
    }).then((result) => {
      if (result.isConfirmed) {

         $.ajax({
            url: "ajax/ajaxProductos.php",
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
                     'El producto ha sido dado de baja.',
                     'success'                   
                   ).then((result)=> {
                     if (result.isConfirmed)
                     location.reload();
                   });
                  
               }else{
                  Swal.fire({
                     icon: 'error',
                     title: 'Oops...',
                     text: 'Ocurrio un error eliminando el producto!',
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