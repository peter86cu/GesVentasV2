

function abrirCaja(idUsuario) {

  
  var datos = new FormData();
  var accion = "inicio";

  datos.append("accion",accion);
  var resul;

  $.ajax({
    url: "ajax/ajaxApertura.php",
    method : "POST",
    data: datos,
    chache: false,
    contentType: false,
    processData:false,
    dataType: "json",
    success: function(respuesta){

     if(respuesta==null){             

       var datosnuevo = new FormData();
       var accion2 = "respuesta";
       datosnuevo.append("accion",accion2);

       $.ajax({
        url: "ajax/ajaxApertura.php",
        method : "POST",
        data: datosnuevo,
        chache: false,
        contentType: false,
        processData:false,
        dataType: "json",
        success: function(resultado){ 
          if(resultado){              

            $("#txtConsecutivo").val(resultado["nro_consecutivo"]);
            $("#idApertura").val(resultado["id_apertura_cajero"]);
            $("#txtTipo > option[value="+1+"]").attr("selected",true);        
            $("#txtTipo").attr('disabled', 'disabled');              
            $('#modalInicioCaja').modal('show');
            $('#btEjecutar').hide();
            //document.getElementById("btEjecutar").visible = false;

          }else{

             Swal.fire({
             icon: "warning",
             title: "Mensaje",
             text: "No hay cajas disponible para trabajar"               
           })
          

 

          }

        }}); 


     }else{
      
      $("#txtConsecutivo").val(respuesta["nro_consecutivo"]);
      $("#idApertura").val(respuesta["id_apertura_cajero"]);             
      $("#txtTurno > option[value="+respuesta["id_turno"]+"]").attr("selected",true);
      $("#txtTurno").attr('disabled', 'disabled');
      $("#txtTipo > option[value="+1+"]").attr("disabled",true);  
      document.getElementById("btEjecutar").visible = true;  
      document.getElementById("btAbrir").style.display = 'none';    
      $('#modalInicioCaja').modal('show');
    }


  }
}); 
}


function eliminaAperturaInicio() {

  
  var datos = new FormData();
  var accion = "inicio";

  datos.append("accion",accion);
  var resul;

  $.ajax({
    url: "ajax/ajaxApertura.php",
    method : "POST",
    data: datos,
    chache: false,
    contentType: false,
    processData:false,
    dataType: "json",
    success: function(respuesta){

     if(respuesta==null){             

     }else{
      
      $("#txtConsecutivo").val(respuesta["nro_consecutivo"]);
      $("#idApertura").val(respuesta["id_apertura_cajero"]);             
      $("#txtTurno > option[value="+respuesta["id_turno"]+"]").attr("selected",true);
      $("#txtTurno").attr('disabled', 'disabled');
      $("#txtTipo > option[value="+1+"]").attr("disabled",true);  
      document.getElementById("btEjecutar").visible = true;  
      document.getElementById("btAbrir").style.display = 'none';    
      $('#modalInicioCaja').modal('show');
    }


  }
}); 
}


function eliminarVentasAbiertas(idApertura){

 var datos = new FormData();
  datos.append("accion","deleteVentasAbiertas");
   datos.append("idApertura",idApertura);
   var resultado=false;

   $.ajax({
    url: "ajax/ajaxVentas.php",
    method : "POST",
    data: datos,
    chache: false,
    contentType: false,
    processData:false,
    dataType: "json",
    success: function(respuesta){
            if(respuesta){            
        
    } 
  }
}); 


}






function eliminarApertura(){

 var datos = new FormData();
 var idApertura= $('#idApertura').val();
alert(idApertura);
  datos.append("accion","deleteAperturaInicial");
   datos.append("idApertura",idApertura);
   var resultado=false;

   $.ajax({
    url: "ajax/ajaxApertura.php",
    method : "POST",
    data: datos,
    chache: false,
    contentType: false,
    processData:false,
    dataType: "json",
    success: function(respuesta){
            if(respuesta){            
         $('#modalInicioCaja').modal('hide');
    } 
  }
}); 


}



function abrirTurno(arqueo) {
 
  var resultado = new Object();
   var datos = new FormData();
    if(arqueo==1){
   var accion = "update";
   var turno = $('#txtTurno').val(); 
   var caja = $('#txtCaja').val();
   var idApertura =  $("#idApertura").val();
   datos.append("accion",accion);
   datos.append("turno",turno);
   datos.append("caja",caja);
   datos.append("idApertura",idApertura);
   document.getElementById("btAbrir").visible = true;
  
    }else if(arqueo==2) {
      
       var accion = "buscar";       
       datos.append("accion",accion);
       document.getElementById("btAbrir").visible = false;
       document.getElementById("btEjecutar").visible = true; 
    }    

   $.ajax({
    url: "ajax/ajaxApertura.php",
    method : "POST",
    data: datos,
    chache: false,
    contentType: false,
    processData:false,
    dataType: "json",
    success: function(respuesta){
            if(respuesta){
              $('#modalInicioCaja').modal('hide');
               buscarVentasJs(respuesta['id_apertura_cajero'],respuesta,arqueo);
        
    } 
  }
}); 

 }

 function buscarVentasJs(idApertura , resultado,arqueo) {



  var datos = new FormData();
  
  var accion = "ventas"; 
  datos.append("accion",accion);
  datos.append("idApertura",idApertura);

  var accion_valida = "validar"; 
  var datosv = new FormData();

  $.ajax({
    url: "ajax/ajaxApertura.php",
    method : "POST",
    data: datos,
    chache: false,
    contentType: false,
    processData:false,
    dataType: "json",
    success: function(result){ 

      if(result || arqueo==1){
     $.ajax({
    url: "ajax/ajaxApertura.php",
    method : "POST",
    data: datos,
    chache: false,
    contentType: false,
    processData:false,
    dataType: "json",
    success: function(respuesta){     
     
    
    document.querySelector('#nombreCaja').innerText = resultado['id_caja'];
      document.querySelector('#_saldo_inicial').innerText = respuesta;
        $("#saldo_inicial").val(respuesta);       
      $("#txtId_arqueo").val(resultado['id_apertura_cajero']); 
      $("#txtId_tipo_arqueo").val($("#txtTipo").val()); 
      $('#arqueoCaja').modal('show');
    
  }
}); 
}else{
 Swal.fire({
     icon: "error",
     title: "Oops...",
     text: "No existen ventas !"               
   })
setTimeout(function() {location.reload();}, 1505)
}
   }
}); 
  


  
}



function Arqueo(id, value) {
  this.id = id;
  this.value = value;

}


function obtenerValue(id) { // Obtengo el valor entrado

  var obtenerV = $("#"+id).val() ;
  obtenerV = parseInt(obtenerV);
  
  return obtenerV;
}


var arqueos = [];


function validarEntrada(id, id_moneda){

  var encontre =false;
  var pos;
  if(arqueos.length<1){
   
    var arq = new Arqueo( id, obtenerValue(id_moneda));
    arqueos.push(arq);
   
  }else{

    for(var i=0; i<arqueos.length; i++){
        
      if (arqueos[i].id ==id){  
        encontre=true;
        pos=i;  
        break;
      }     
    }

    if (!encontre) { 
      var arq = new Arqueo( id, obtenerValue(id_moneda));
      arqueos.push(arq);
     
    }else{
      arqueos[pos].value = obtenerValue(id_moneda);       
         
    }
  }
}



var caja_cuadrada=3;


function sumar(id,t){
 var id_moneda ="moneda_"+id;
 

 var tipo_arqueo=$("#txtId_tipo_arqueo").val();
 var saldo=$("#saldo_inicial").val();


    validarEntrada(id, id_moneda);

    if(t.value==1){
      valor =$(t).attr("placeholder")*1;
    }else{
      valor =parseFloat($(t).attr("placeholder"))*parseFloat(t.value);  

    }

     if(t.value=== ""){
     valor =0;
  }
   

    $(t).parent().parent().find('td').eq(2).find('.total').html(valor);

    var total=0;
    $(".total").each(function(){
      total += parseFloat($(this).html());
    })

    $("#_total_arqueo").html("$ "+total);
    if(total==saldo && tipo_arqueo!=1){   
      alert('Las caja esta cuadrada');
      caja_cuadrada=2;
    }
 



}

function guardarArqueo() {

//var id_moneda ="moneda_"+id;
var datos = new FormData();
var idApertura = $("#txtId_arqueo").val();

var tipo_arqueo = $("#txtTipo").val();

var id_estado_arqueo =   $("#txtId_tipo_arqueo").val();

if(id_estado_arqueo==1){
  var cuadre =  id_estado_arqueo;
}else {
  var cuadre = caja_cuadrada;
}

if(tipo_arqueo==3){
  eliminarVentasAbiertas(idApertura);
}


var accion = "insert"; 

datos.append("accion",accion);
datos.append("idApertura",idApertura);
datos.append("idCuadre",cuadre);
datos.append("tipoArqueo",id_estado_arqueo);
datos.append("array",JSON.stringify(arqueos));

$.ajax({
  url: "ajax/ajaxApertura.php",
  method : "POST",
  data: datos,
  chache: false,
  contentType: false,
  processData:false,
  dataType: "json",
  success: function(respuesta){
   if(respuesta){
    $('#arqueoCaja').modal('hide');
    Swal.fire({
     position: "top-end",
     icon: "success",
     title: "Guardado el arqueo correctamente",
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
    setTimeout(function() {location.reload();}, 1505)
  }


}
}); 


}


function datosImprimirArqueo(idApertura, idArqueo, id_tipo_arqueo){


  VentanaCentrada('./pdf/documentos/arqueo.php?idApertura='+idApertura+'&idArqueo='+idArqueo+'&id_tipo_arqueo='+id_tipo_arqueo,'Arqueo','','1024','768','true');  


}

