function abrirModal(modal){

$('#'+modal).modal('show');


}

function salirModal(modal){

$('#'+modal).modal('hide');
  
}



function salir(modal){

$('#'+modal).modal('hide');
  setTimeout(function() {location.reload();}, 0)
}

$(document).ready(function(){

    //--------------------- SELECCIONAR FOTO PRODUCTO ---------------------
    $("#foto").on("change",function(){
      
    	var uploadFoto = document.getElementById("foto").value;
        var foto       = document.getElementById("foto").files;
        var nav = window.URL || window.webkitURL;
        var contactAlert = document.getElementById('form_alert');       
            if(uploadFoto !='')
            {
                var type = foto[0].type;
                var name = foto[0].name;
                if(type != 'image/jpeg' && type != 'image/jpg' && type != 'image/png')
                {
                    contactAlert.innerHTML = '<p class="errorArchivo">El archivo no es válido.</p>';                        
                    $("#img").remove();
                    $(".delPhoto").addClass('notBlock');
                    $('#foto').val('');
                    return false;
                }else{  
                        contactAlert.innerHTML='';
                        $("#img").remove();
                        $(".delPhoto").removeClass('notBlock');
                        var objeto_url = nav.createObjectURL(this.files[0]);
                        $(".prevPhoto").append("<img id='img' src="+objeto_url+">");
                        $(".upimg label").remove();
                        
                    }
              }else{
              	alert("No selecciono foto");
                $("#img").remove();
              }              
    });

    $('.delPhoto').click(function(){
    	$('#foto').val('');
    	$(".delPhoto").addClass('notBlock');
    	$("#img").remove();

    });

});



function notificacion(tipo, descripcion){

console.log(descripcion);
var accion = "noti";
var datos = new FormData();
datos.append("tipoNotif",tipo);
datos.append("descripcion",descripcion);
datos.append("accion",accion);


$.ajax({
  url: "ajax/ajaxGeneral.php",
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


/*$(document).ready(function() {
  // intervalo
  setInterval(function() {
    // petición ajax
    $.ajax({
      url: 'claseYoQueSe.php',
      success: function(data) {
        // reemplazo el texto que va dentro de #Ejemplo
        $('#Ejemplo').text(data);
      }
    });
  }, 10000); // cada 10 segundos, el valor es en milisegundos
});*/


function notificacion_update(id){

var accion = "update";
var datos = new FormData();
datos.append("id",id);
datos.append("accion",accion);

 
$.ajax({
  url: "ajax/ajaxGeneral.php",
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


function VentanaCentrada(theURL,winName,features, myWidth, myHeight, isCenter) { //v3.0
  if(window.screen)if(isCenter)if(isCenter=="true"){
    var myLeft = (screen.width-myWidth)/2;
    var myTop = (screen.height-myHeight)/2;
    features+=(features!='')?',':'';
    features+=',left='+myLeft+',top='+myTop;
  }
  window.open(theURL,winName,features+((features!='')?',':'')+'width='+myWidth+',height='+myHeight);
}








function comprobar_refresco() {
    var datos = new FormData(); 
    var accion ="user";    
    datos.append("accion",accion);
 

     $.ajax({
     url: "procesos/dashboard.php",
     method : "POST",
     data: datos,
     chache: false,
     contentType: false,
     processData:false,
     dataType: "json",
     success: function(respuesta){      
      if(respuesta){
       document.querySelector('#login').innerText = respuesta;
       // $( "#idLogin" ).load(window.location.href + " #idLogin" );
     }



   }
 });
}


function agregarCajas(event){

   //Obtengo los valores
   
   var nombre = $('#txt_Nombre').val();
   var ip = $("#txt_descripcion").val();
   var sucursal = $('#inputSucursal').val();
   var estado = $('#inputCaja').val();
   
   var datos = new FormData();
   var accion = "insert";  

   datos.append("accion",accion);    
   datos.append("nombre",nombre); 
    datos.append("sucursal",sucursal); 
   datos.append("ip",ip);
   datos.append("estado",estado);

   $.ajax({
    url: "ajax/ajaxGeneral.php",
    method : "POST",
    data: datos,
    chache: false,
    contentType: false,
    processData:false,
    dataType: "json",
    success: function(respuesta){

     if(respuesta){
      $('#ModalADDCajas').modal('hide');
      Swal.fire({
       position: "top-end",
       icon: "success",
       title: "Caja nueva agregada",
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


$(".btnEditarCajas").click(function(){

  var idCaja = $(this).attr("idCaja");
  var datos = new FormData();  
  var accion = "buscarCaja";
  datos.append("accion",accion);
  datos.append("idCaja",idCaja,);

  $.ajax({
   url: "ajax/ajaxGeneral.php",
   method : "POST",
   data: datos,
   chache: false,
   contentType: false,
   processData:false,
   dataType: "json",
   success: function(respuesta){     
    $("#idCaja").val(respuesta["id_caja"]);
    $("#txt_NombreEdit").val(respuesta["nombre"]);
    $("#txt_ipEdit").val(respuesta["ip"]); 
    $("#inputSucursalEdit > option[value="+respuesta["id_sucursal"]+"]").attr("selected",true);                    
    $("#inputCajaEdit > option[value="+respuesta["id_tipo_caja"]+"]").attr("selected",true);

  }
  

})

})


function updateCaja(event){ 

   var idCaja=  $('#idCaja').val();
   var nombre = $('#txt_NombreEdit').val();
   var ip = $("#txt_ipEdit").val();
   var sucursal = $("#inputSucursalEdit").val();
   var estado = $('#inputCajaEdit').val();
   var datos = new FormData();
   var accion = "updateCaja";
   
   datos.append("accion",accion);    
   datos.append("nombre",nombre); 
   datos.append("ip",ip);
   datos.append("sucursal",sucursal);
   datos.append("estado",estado);
   datos.append("idCaja",idCaja);
   

   $.ajax({
     url: "ajax/ajaxGeneral.php",
     method : "POST",
     data: datos,
     chache: false,
     contentType: false,
     processData:false,
     dataType: "json",
     success: function(respuesta){

      if(respuesta){
        $('#ModalEditarCajas').modal('hide');
        Swal.fire({
         position: "top-end",
         icon: "success",
         title: "Los datos fueron modificados correctamente",
         showConfirmButton: false,
         timer: 1500
       })
        setTimeout(function() {location.reload();}, 1505)   

      }else{

        Swal.fire({
         icon: "error",
         title: "No se pudo actualizar."
         //text: "A ocurrido un error!",
        // footer: "<a href>Ver que mensaje dar?</a>"
      })
      }

    }
  }); 

 }

/*function comprobar_session() {
    var datos = new FormData();
     var id_usuario = $('#id_usuario_session').val(); 
     var ips = $('#ips_session').val();
    var accion ="session";    
    datos.append("accion",accion);
     datos.append("idUsuario",id_usuario);
     datos.append("iPsession",ips);
    

     $.ajax({
     url: "procesos/dashboard.php",
     method : "POST",
     data: datos,
     chache: false,
     contentType: false,
     processData:false,
     dataType: "json",
     success: function(respuesta){      
      if(respuesta>1){
       //document.querySelector('#login').innerText = respuesta;
       // $( "#idLogin" ).load(window.location.href + " #idLogin" );
       session_destroy();
       window.location='login';
     }



   }
 });
}*/