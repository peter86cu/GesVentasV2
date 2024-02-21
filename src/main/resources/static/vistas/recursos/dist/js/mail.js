
function leerMail(idMail,cantidad,accion_mail){

  if(accion_mail=='nuevo'){
    var datos = new FormData(); 
    var accion ="borrador";    
    datos.append("accion",accion);
    

    $.ajax({
     url: "ajax/procesoMail.php",
     method : "POST",
     data: datos,
     chache: false,
     contentType: false,
     processData:false,
     dataType: "json",
     success: function(respuesta){
      console.log(respuesta)
      if(respuesta){
       window.location.href = 'redactar-mail';
     }



   }
 });


  }else{
    var datos = new FormData(); 
    var accion ="parametros";
     datos.append("id_mail",idMail);
    datos.append("sin_leer",cantidad);
    datos.append("accion",accion);
    datos.append("accion_mail",accion_mail);
    
    

    $.ajax({
     url: "ajax/procesoMail.php",
     method : "POST",
     data: datos,
     chache: false,
     contentType: false,
     processData:false,
     dataType: "json",
     success: function(respuesta){

      if(accion_mail=='responder' || accion_mail=='borrador'){
        window.location.href = 'redactar-mail';
      }else{
        window.location.href = 'read-mail';
      }

      

    }
  });
  }

}


function nuevo_mail_borrado(accion_mail){


  var datos = new FormData(); 
  var accion ="borrador";    
  datos.append("accion",accion);
  datos.append("accion_mail",accion_mail);

  $.ajax({
   url: "ajax/procesoMail.php",
   method : "POST",
   data: datos,
   chache: false,
   contentType: false,
   processData:false,
   dataType: "json",
   success: function(respuesta){
    console.log(accion_mail)
    if(respuesta){
      if(accion_mail=='responder'){
        window.location.href = 'redactar-mail';
      }else{
        window.location.href = 'read-mail';
      }
    }




  }
});
}


function actualizar_Borrador(id){


 var para  =$('#txt_para').val();
 var asunto  =$('#txt_asunto').val();
 var body  =$('#compose-textarea').val();
 var datos = new FormData(); 
 var accion ="actualizar_borrador";    
 datos.append("accion",accion);
 datos.append("para",para);
 datos.append("asunto",asunto);
 datos.append("body",body);
 datos.append("id_mail",id);



 $.ajax({
   url: "ajax/procesoMail.php",
   method : "POST",
   data: datos,
   chache: false,
   contentType: false,
   processData:false,
   dataType: "json",
   success: function(respuesta){
    console.log(accion_mail)
    if(respuesta){
      if(accion_mail=='responder'){
        window.location.href = 'redactar-mail';
      }else{
        window.location.href = 'read-mail';
      }
    }




  }
});
}

function eliminarAdjunto(fichero, id){

  var datos = new FormData(); 
  var accion ="eliminar_adjunto";    
  datos.append("accion",accion);
  datos.append("fichero_adjunto",fichero);
  datos.append("id_fichero",id);

  $.ajax({
   url: "ajax/procesoMail.php",
   method : "POST",
   data: datos,
   chache: false,
   contentType: false,
   processData:false,
   dataType: "json",
   success: function(respuesta){
    console.log(respuesta)
    if(respuesta){
     $( "#here" ).load(window.location.href + " #here" );
   }

 }
});
}



function validarFicheroAdjunto(){


  var datos = new FormData(); 
  var accion ="validar_fichero";
  var adjunto  =$('#idAdjunto')[0].files[0];
  datos.append("accion",accion);
  datos.append("adjunto",adjunto);

  $.ajax({
   url: "ajax/procesoMail.php",
   method : "POST",
   data: datos,
   chache: false,
   contentType: false,
   processData:false,
   dataType: "json",
   success: function(respuesta){
     if(respuesta){
      $( "#here" ).load(window.location.href + " #here" );
    }

  }
});


}


function responderMail(idMail,accion_mail){


  var datos = new FormData(); 
  var accion ="responder";
  datos.append("id_mail",idMail);    
  datos.append("accion",accion);
  datos.append("accion_mail",accion_mail);

  $.ajax({
   url: "ajax/procesoMail.php",
   method : "POST",
   data: datos,
   chache: false,
   contentType: false,
   processData:false,
   dataType: "json",
   success: function(respuesta){
    window.location.href = 'read-mail';

  }
});
}


function descargarMail(carpeta){


  var datos = new FormData(); 
  var accion ="descargar";
  datos.append("carpeta",carpeta); 
  datos.append("accion",accion);    

  $.ajax({
   url: "mail/downloadMail.php",
   method : "POST",
   data: datos,
   chache: false,
   contentType: false,
   processData:false,
   dataType: "json",
   beforeSend: function() {
    $('#ModalLoaderRecibidos').modal("show"); 
  },
  complete: function(datos){
    console.log(datos)
    if(datos['responseJSON']=='Ha fallado la conexión. Intentelo mas tarde o ponganse en contacto con el administrador.'){

     $('#ModalLoaderRecibidos').modal("hide");        
     Swal.fire({
       icon: "error",
       text: datos['responseJSON']
       
     })

   }else{

    $('#ModalLoaderRecibidos').modal("hide");

    Swal.fire({
      title: datos['responseJSON'],
      showDenyButton: false,
      showCancelButton: false,
      confirmButtonText: `OK`,

    }).then((result) => {

      if (result.isConfirmed) {
        location.reload();
      } 
    })
  }


}
});
}

function descargarMailEnviados(carpeta){

  var datos = new FormData(); 
  var accion ="descargar_enviados";
  datos.append("carpeta",carpeta); 
  datos.append("accion",accion);    

  $.ajax({
   url: "mail/downloadMail.php",
   method : "POST",
   data: datos,
   chache: false,
   contentType: false,
   processData:false,
   dataType: "json",
   beforeSend: function() {
    $('#ModalLoaderRecibidos').modal("show"); 
  },
  complete: function(respuesta){

    console.log(respuesta['responseJSON'])
    if(respuesta['responseJSON']=='OK'){ 
      $('#ModalLoaderRecibidos').modal("hide");    
      window.location.href = 'mailsend';       
    } if(respuesta['responseJSON']=='Buzon actualizado'){
      $('#ModalLoaderRecibidos').modal("hide");

      window.location.href = 'mailsend';
    } if (!respuesta['responseJSON']=='OK' || !respuesta['responseJSON']=='Buzon actualizado'){
      Swal.fire({
       icon: "error",
       text: respuesta['responseJSON']
       
     })
       // window.location.href = 'mailsend';
     }
   }
 });
}



function marcarLeido(idMail,accion_mail){

  if(accion_mail=='salida'){
    leerMail(idMail,0,accion_mail);
  }else{

    var datos = new FormData(); 
    var accion = "marcarLectura";
    datos.append("accion",accion);
    datos.append("id_mail",idMail); 
    datos.append("accion_mail",accion_mail);
  
    $.ajax({
     url: "ajax/procesoMail.php",
     method : "POST",
     data: datos,
     chache: false,
     contentType: false,
     processData:false,
     dataType: "json",
     success: function(respuesta){  

      leerMail(idMail,respuesta,accion_mail);
      

    }  
  });
  }

}



function enviarMail(idMail){

  var datos = new FormData(); 
  var accion = "enviar";
  var para = $("#txt_para").val();
  var asunto = $("#txt_asunto").val();
  var body = $("#compose-textarea").val();

  datos.append("accion",accion);
  datos.append("para",para); 
  datos.append("asunto",asunto); 
  datos.append("body",body);
  datos.append("id_mail",idMail);
  

  $.ajax({
   url: "mail/sendMail.php",
   method : "POST",
   data: datos,
   chache: false,
   contentType: false,
   processData:false,
   dataType: "json",
   beforeSend: function() {
    $('#ModalLoaderRecibidos').modal("show"); 
  },
  complete: function(respuesta){ 
    console.log(respuesta)
    if(respuesta) {
     $('#ModalLoaderRecibidos').modal("hide");     

     window.location.href = 'mailbox';

   }else{
    $('#ModalLoaderRecibidos').hide(); 
    Swal.fire({
     icon: "error",
     title: respuesta['responseJSON']

   })
  }  
}
});

}




function moverDelete(accion_mail){

  if(listado.length>0){
   var datos = new FormData(); 
   var accion = "delete";
   datos.append("accion",accion);   
   datos.append("accion_mail",accion_mail);
   datos.append("array",JSON.stringify(listado));

   $.ajax({
     url: "ajax/procesoMail.php",
     method : "POST",
     data: datos,
     chache: false,
     contentType: false,
     processData:false,
     dataType: "json",
     success: function(respuesta){  

       if(respuesta){  
       $( "#cuerpo" ).load(window.location.href + " #cuerpo" );      
        location.reload();
      }
      

    }  
  });
 }else{

  Swal.fire({
   icon: "error",
   title: "Debe seleccionar un elemento"

 })

}

}

function listadoSelect(id) {
  this.id = id;


}
listado = []; 
function selectMail(id) {
  // Get the checkbox
  var checkBox = document.getElementById("check1_"+id);
  
  if (checkBox.checked == true){
    var ids = new listadoSelect( id);
    listado.push(ids);
    console.log(listado.length)
  } if(checkBox.checked == false) {
    for(var i=0; i<listado.length; i++){

      if (listado[i].id ==id){  
        encontre=true;
        pos=i;  
        break;
      }     
    }

    if (encontre) { 
     listado.splice(pos,1); 
     console.log("eliminado > 0 "+listado.length)  
   }
 }
}


function moverDeleteLectura(idMail,accion_mail){


 var datos = new FormData(); 
 var accion = "delete_lectura";
 datos.append("accion",accion);   
 datos.append("id_mail",idMail); 
 datos.append("accion_mail",accion_mail);
 

 $.ajax({
   url: "ajax/procesoMail.php",
   method : "POST",
   data: datos,
   chache: false,
   contentType: false,
   processData:false,
   dataType: "json",
   success: function(respuesta){  
    console.log(respuesta)
    if(respuesta){
     if(accion_mail=="entrada"){
       window.location.href = 'mailbox';
     }

     if (accion_mail="salida") {
      window.location.href = 'mailsend';
    }
  }


}  
});

 
}
