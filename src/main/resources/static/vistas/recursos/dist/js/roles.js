

function AccesoModulosNiveles(modulo,menu, nivel) {

  this.modulo = modulo;
  this.menu = menu;
  this.nivel = nivel;
}

var modulo = [];


function agregarRol(event){


var idRol = $('#idRol').val();
var rol = $('#txt_Nombre').val();
var descripcion = $("#txt_descripcion").val();
var estado = $('#select_estado').val();

var datos = new FormData();
var accion = "insert";  

datos.append("accion",accion);    
datos.append("rol",rol); 
datos.append("idRol",idRol); 
datos.append("descripcion",descripcion);
datos.append("estado",estado);


$.ajax({
    url: "ajax/ajaxRoles.php",
    method : "POST",
    data: datos,
    chache: false,
    contentType: false,
    processData:false,
    dataType: "json",
    success: function(respuesta){

     if(respuesta){
        
      $('#ModalADDRoles').modal('hide');      
      Swal.fire({
               position: "top-end",
               icon: "success",
               title: "Agregado el rol",
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





function agregarMenuNiveles(event){

   //Obtengo los valores



   var selectedAcceso = new Array();

   var cantidadmenu= $('#menuSize').val();
   var idModulo= $('#idModulo').val();



  
   var modulos='';
   var niveles='';
   var posModulos=0;
   var menu=1;

   while(cantidadmenu>0){
              
       if ($('#modulo_'+idModulo+"_"+posModulos).is(':checked') ) {
        
        /*var idMenu=$('#idMenu_'+menu).val();
          modulo.push($('#idMenu_'+menu).val());*/
          var nivel = new Array();
          var cantidadnivel = 9; 
          var posNiveles=0;
          while(cantidadnivel>0){            
                             
            if ($("#niveles_"+idModulo+"_"+posModulos+"_"+posNiveles).is(':checked') ) {  
            var res=$("#idNivel_"+idModulo+"_"+posModulos+"_"+posNiveles).val();           
              nivel.push($("#idNivel_"+idModulo+"_"+posModulos+"_"+posNiveles).val());

          }
          cantidadnivel--;
          posNiveles++;   
      }

      var acc = new AccesoModulosNiveles(idModulo,$("#idMenu_"+posModulos).val(),nivel);
      selectedAcceso.push(acc);         
      nivel = [];   

  } 
posModulos++; 
  cantidadmenu--;
menu++;

} 


var datos = new FormData();
var accion = "insertMenuXNiveles";  

datos.append("accion",accion); 
datos.append("array",JSON.stringify(selectedAcceso));

$.ajax({
    url: "ajax/ajaxRoles.php",
    method : "POST",
    data: datos,
    chache: false,
    contentType: false,
    processData:false,
    dataType: "json",
    success: function(respuesta){

     if(respuesta>0){
    $('#idRol').val(respuesta);
    $('#ModalOpcionesMenu').modal('hide');
    $('#ModalADDRoles').modal('show');
       
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



function updateRol(event){

   //Obtengo los valores



   var selectedAcceso = new Array();

   var cantidadrol = $('#rolSizeE').val();

   

   var modulos='';
   var niveles='';
   var posModulos=0;

   while(cantidadrol>0){
       posModulos++;   
      if ($('#moduloE_'+posModulos).is(':checked') ) {
        
          modulo.push($("#idModuloE_"+posModulos).val());
          var nivel = new Array();
          var cantidadnivel = $('#nivelesSizeE').val(); 
         
          var posNiveles=0;
          while(cantidadnivel>0){            
            posNiveles++;                    
            if ($("#nivelesE_"+posNiveles+"_"+posModulos).is(':checked') ) {             
              nivel.push($("#idNivelE_"+posNiveles).val());

          }
          cantidadnivel--;
      }

      var acc = new AccesoModulosNiveles($("#idModuloE_"+posModulos).val(),nivel);
      selectedAcceso.push(acc);         
      nivel = [];   

  } 

  cantidadrol--;


} 



   var idRol=  $('#idRolE').val();
   var rol = $('#txt_NombreEdit').val();
   var descripcion = $("#txt_descripcionEdit").val();
   var estado = $('#select_estadoEdit').val();
   var datos = new FormData();
   var accion = "update";

   datos.append("accion",accion);    
   datos.append("rol",rol); 
   datos.append("descripcion",descripcion);
   datos.append("estado",estado);
   datos.append("idRol",idRol);
   datos.append("array",JSON.stringify(selectedAcceso));

$.ajax({
     url: "ajax/ajaxRoles.php",
     method : "POST",
     data: datos,
     chache: false,
     contentType: false,
     processData:false,
     dataType: "json",
     success: function(respuesta){

      if(respuesta){
        $('#ModalEditarRoles').modal('hide');
        
        setTimeout(function () {
    $(".loader-page").css({visibility:"hidden",opacity:"0"})
  }, 2000);

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


$(".btnEliminarRol").click(function(){  

   var idRol=  $(this).attr("idRol");

   var datos = new FormData();

   var accion = "delete";

   datos.append("accion",accion);
   datos.append("idRol",idRol);

   const swalWithBootstrapButtons = Swal.mixin({
    customClass: {
      confirmButton: 'btn btn-success',
      cancelButton: 'btn btn-danger'
  },
  buttonsStyling: false      
})

   swalWithBootstrapButtons.fire({
    title: 'Confirma desahabilitar el rol?',    
    showCancelButton: true,
    confirmButtonText: 'Si',
    cancelButtonText: 'No',
    reverseButtons: true
}).then((result) => {
    if (result.isConfirmed) {

     $.ajax({
      url: "ajax/ajaxRoles.php",
      method : "POST",
      data: datos,
      chache: false,
      contentType: false,
      processData:false,
      dataType: "json",
      success: function(respuesta){
       if(respuesta){
        swalWithBootstrapButtons.fire(
         'El rol ha sido desahabilitado'                        
         ).then((result)=> {
           if (result.isConfirmed)
             location.reload();
     });

     }else{
        Swal.fire({
         icon: 'error',
         title: 'No se pudo desahabilitar el rol' 

     })
    }

}
});  
 } 
})

})

function moduloNivel(id_nivel,descripcion){

    this.id_nivel=id_nivel;
    this.descripcion=descripcion;
    
}

$(".btnEditarRol").click(function(){

  var idRol = $(this).attr("idRol");
  var datos = new FormData();  
  var accion = "buscar";
  datos.append("accion",accion);
  datos.append("idRol",idRol);

  $.ajax({
   url: "ajax/ajaxRoles.php",
   method : "POST",
   data: datos,
   chache: false,
   contentType: false,
   processData:false,
   dataType: "json",
   success: function(respuesta){     
    $("#idRolE").val(idRol);
    $("#txt_NombreEdit").val(respuesta["rol"]);
    $("#txt_descripcionEdit").val(respuesta["descripcion"]);                     
    $("#select_estadoEdit > option[value="+respuesta["estado"]+"]").attr("selected",true);

    if(respuesta["id_rol"]){
      var datos = new FormData();  
      var accion = "lista_modulos";
      datos.append("accion",accion);

      $.ajax({
       url: "ajax/ajaxRoles.php",
       method : "POST",
       data: datos,
       chache: false,
       contentType: false,
       processData:false,
       dataType: "json",
       success: function(modulos){


        if(modulos){

          var datos = new FormData();  
          var accion = "modulos_niveles";
          datos.append("accion",accion);
          datos.append("idRol",respuesta["id_rol"]);
          $.ajax({
           url: "ajax/ajaxRoles.php",
           method : "POST",
           data: datos,
           chache: false,
           contentType: false,
           processData:false,
           dataType: "json",
           success: function(valor){    


            $('#k-table > tfoot').empty();

            var cantidadModulos=0;
            var cantidadNiveles=1;

            var niveles = ["Vista", "Add","Editar","Eliminar", "Imprimir", "Notificar", "Abir y cerrar caja","Autorizar"];

            var objetoModulo=null;
               // $("#rolSizeE").val(modulos.length);   
               

            for(var j=0; j<modulos.length; j++){
               var esta=false;
               cantidadModulos++;
               var listadoniveles=[];
               var objetoModulo = new Object();
               for(var i=0; i<valor.length; i++){ 



                if(modulos[j].id_modulo==valor[i].id_modulo){

                    esta=true;
                    var data = new moduloNivel(valor[i].id_nivel,valor[i].descripcion);
                    listadoniveles.push(data);
                    objetoModulo= modulos[j];
                                    //valor.splice(i, 1);

                                }

                            }

                            var tr1 =``;
                            var tr2 =``;
                            var tr3 =``;
                            var tr4 =``;
                            var tr5 =``;  


                            if(esta==true){

                                tr1 = `<tr > `;

                                tr2= ` <td align="float-right"  >
                                <input align="left" type="hidden" id="idModuloE_`+objetoModulo.id_modulo+`" name="idModuloE" value="`+objetoModulo.id_modulo+`">
                                <input align="left" id="moduloE_`+objetoModulo.id_modulo+`" class="form-check-input" type="checkbox" checked onclick="checkEdit(`+objetoModulo.id_modulo+`)">
                                <label align="left"  class="form-check-label">`+objetoModulo.nombre+`</label></td>`;       

                                var temp=0;  
                                var cont=1;                           


                                var val=0;                     

                                var encontre=false;
                                for(var h=0; h<niveles.length; h++){                                   
                                    if(objetoModulo.id_modulo==modulos[j].id_modulo && validarExiste(listadoniveles,niveles[h],temp)){
                                       tr3+=    `<td ><input type="hidden" id="idNivelE_`+cantidadNiveles+`" name="idNivelE" value="`+cont+`">
                                       <input  class="nivelE_`+objetoModulo.id_modulo+`" id="nivelesE_`+cantidadNiveles+`_`+modulos[j].id_modulo+`"  class="form-check-input" checked type="checkbox" >
                                       <label  class="form-check-label">`+niveles[h]+`</label></td>`; 
                                   }else {                                     

                                    tr3+=    `<td ><input type="hidden" id="idNivelE_`+cantidadNiveles+`" name="idNivelE" value="`+cont+`">
                                    <input  class="nivelE_`+modulos[j].id_modulo+`" id="nivelesE_`+cantidadNiveles+`_`+modulos[j].id_modulo+`"  class="form-check-input"  type="checkbox" >
                                    <label  class="form-check-label">`+niveles[h]+`</label></td>`;  
                                } 
                                                   
                                cantidadNiveles++;
                                cont++;
                            }
                             val++; 
                            listadoniveles=[];



                            t4 =` </tr> `;
                        }else{
                          tr1 = `<tr >`;  

                          tr2=  `  <td align="float-right"  >
                          <input align="left" type="hidden" id="idModuloE_`+modulos[j].id_modulo+`" name="idModuloE" value="`+modulos[j].id_modulo+`">
                          <input align="left" id="moduloE_`+modulos[j].id_modulo+`" class="form-check-input" type="checkbox" onclick="checkEdit(`+modulos[j].id_modulo+`)">
                          <label align="left"  class="form-check-label">`+modulos[j].nombre+`</label></td>`; 

                          var temp=0;  
                          var cont=1;                           
                          var val=0;    

                          for(var h=0; h<niveles.length; h++){                            
                            var pos2=1; 
                            tr3+=    `<td ><input type="hidden" id="idNivelE_`+cantidadNiveles+`" name="idNivelE" value="`+cont+`">
                            <input  class="nivelE_`+modulos[j].id_modulo+`" id="nivelesE_`+cantidadNiveles+`_`+modulos[j].id_modulo+`"  class="form-check-input"  type="checkbox" disabled >
                            <label  class="form-check-label">`+niveles[h]+`</label></td>`;  



                            cont++;
                            pos2++;
                            temp++;
                            val++;
                            cantidadNiveles++;
                        }                   


                        tr4=  `</tr> `; 
                    }




                    $("#result").append(tr1+tr2+tr3+tr4);      


                }

                 $("#nivelesSizeE").val(cantidadNiveles);
            }


        })


}
}
})

}
}
})
})


function validarExiste(lista,valor, intera){

    var esta=false;

    for (var i = 0; i < lista.length; i++) {        
        if(lista[i].descripcion==valor){
            esta= true;
            break;
        }
    }
   
    return esta;

}






function check(id,pos){

 $("#modulo_"+id+"_"+pos).on( 'change', function() {
    if( $(this).is(':checked') ) {
       
        // Hacer algo si el checkbox ha sido seleccionado
        $("input.nivel_"+id+"_"+pos).removeAttr("disabled");

    } else {
       
        // Hacer algo si el checkbox ha sido deseleccionado
        $("input.nivel_"+id+"_"+pos).attr("disabled", true);
    }
});

} 



function opcionesMenu(id){

 $("#modulo_"+id).on( 'change', function() {
    
    if( $(this).is(':checked') && $("#modulo_"+id).prop("enabled", true) ) {
        $("#modulo_"+id).attr("disabled", true);
         

        // Hacer algo si el checkbox ha sido seleccionado
         $('#ModalADDRoles').modal('hide');     
      //cargarOpcionesMenu(id);
      console.log(id);
     $('#ModalOpcionesMenu').data('modal', null);
      ejecutarReporte(id)


    } else {
        // Hacer algo si el checkbox ha sido deseleccionado
        $('#ModalOpcionesMenu').modal('hide');
        //$("input.nivel_"+id).attr("disabled", true);
    }
});

} 




var menus = [];



function procesoReporte(idModulo) {

 var datos = new FormData();
   var accion = "buscarOpcionesXmodulos";
   datos.append("accion",accion);   
   datos.append("idModulo",idModulo);

    
    $.ajax({
        url: "ajax/ajaxRoles.php",
       method : "POST",
      data: datos,
        chache: false,
         contentType: false,
         processData:false,
         dataType: "json",
         beforeSend: function() {
         // setting a timeout        
         // $('.loader-page').show();
     
          /*  abrirModal('ModalOpcionesMenu');
            $("#ModalOpcionesMenu").val(null).trigger("change");*/

           
            $("#opciones tr").remove(); 
             abrirModal('ModalOpcionesMenu');
            //$("#ModalOpcionesMenu").val(null).trigger("change");
        

      
    
                
        
      }, 
        error: function(){
            alert("error petición ajax");
        },
        success: function(data){
            menus= new Array();
            menus = data;
            //console.log(munus)
        }
    }).done(function(data){
  
        enviar(0);
    })


    
}


 
function ejecutarReporte(ids) {
//abrirModal('ModalOpcionesMenu');
   
   procesoReporte(ids)


}

var columna=0;
       
      function enviar(indice){
        var tabla="";
        // Es importante notar que estamos utilizando jQuery
         $('#opciones > tfoot').empty();
        var menuSize = ` <input type="hidden"  id="menuSize" name="menuSize"  value="`+menus.length+`" > `;
        $("#opc").append(menuSize);   
         if(indice < menus.length){
           // console.log("Menu id " + menus[indice].id_menu);
            var tr1 = `<tr ><td align="float-right"  >
     <input align="left" type="hidden" id="idMenu_`+indice+`" name="idMenu" value="`+menus[indice].id_menu+`">
     <input align="left" type="hidden" id="idModulo" name="idModulo" value="`+menus[indice].id_modulo+`">
     <input align="left" id="modulo_`+menus[indice].id_modulo+`_`+indice+`" class="form-check-input" type="checkbox" onclick="check(`+menus[indice].id_modulo+`,`+indice+`)">
       <label align="left"  class="form-check-label">`+menus[indice].nombre+`</label></td>`;
            tabla=tabla+tr1;
             var accion = "buscarOpcionesXniveles";
              var datos = new FormData();
     datos.append("accion",accion);   
     datos.append("idNivel",menus[indice].id_menu);  
          $.ajax({
            url: "ajax/ajaxRoles.php",
         method : "POST",
         data: datos,
         chache: false,
         contentType: false,
         processData:false,
         dataType: "json",  
            }).done(function(data) {
                //$("#opc").append(tr1);
               // console.log(tr1)
                for(var j=0; j<data.length; j++){
                    // console.log("Recurs " + indice);
              var tr2 = ` <td ><input type="hidden" id="idNivel_`+menus[indice].id_modulo+`_`+indice+`_`+j+`" name="idNivel" value="`+data[j].id_nivel_acceso+`">
                          <input  class="nivel_`+menus[indice].id_modulo+`_`+indice+`" id="niveles_`+menus[indice].id_modulo+`_`+indice+`_`+j+`"  class="form-check-input" type="checkbox" disabled>
                          <label  class="form-check-label">`+data[j].descripcion+`</label></td>`;
                         // console.log(tr2)
                         tabla=tabla+tr2;
                                    
                          }
                          var tr3= `</tr"> `;                         
                   tabla=tabla+tr3; 
                   $("#opc").append(tabla); 
                   columna++;         
              enviar(indice + 1);
          });
        }
      }
    




function checkEdit(id){

 $("#moduloE_"+id).on( 'change', function() {
    if( $(this).is(':checked') ) {
        // Hacer algo si el checkbox ha sido seleccionado
        $("input.nivelE_"+id).removeAttr("disabled");

    } else {
        // Hacer algo si el checkbox ha sido deseleccionado
        $("input.nivelE_"+id).attr("disabled", true);
    }
});

}



function buscarNivelesMenu(id){

 var datos = new FormData();
   var accion = "buscarOpcionesXniveles";
   datos.append("accion",accion);   
   datos.append("idNivel",id);
  var result = [];

$.ajax({
     url: "ajax/ajaxRoles.php",
     method : "POST",
     data: datos,
     chache: false,
     contentType: false,
     processData:false,
     dataType: "json",     
      success: function(niveles) {

        console.log(niveles);

            return niveles;
        

      }

  })

return result;

}



