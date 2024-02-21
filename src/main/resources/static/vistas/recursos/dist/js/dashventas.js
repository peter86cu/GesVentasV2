$(document).ready(function() {

var datos = new FormData();
var accion = "dasbventas";  

   datos.append("accion",accion); 

   var ticksStyle = {
    fontColor: '#495057',
    fontStyle: 'bold'
  }

   var mode = 'index'
  var intersect = true

    $.ajax({      
    url: "ajax/ajaxVentas.php",
    method : "POST",
    data: datos,
    chache: false,
    contentType: false,
    processData:false,
    dataType: "json",
        success: function(result) {
        

            var meses = ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 'Jul','Aug', 'Sep', 'Oct', 'Nov', 'Dic'];
            var anios = [];
            var cantidad_actual = [];
            var cantidad_pasado = [];
            var monto_actual = [];          
            var monto_pasado = [];
           

          
          for(var i=0; i<meses.length; i++){        
               
               var auxActual=[];
               var auxPasado=[];
               var estaActual=0;
                var estaPasado=0;
                 for (var j = 0; j < result.length; j++) {
                    
                     if(meses[i]==result[j].mes && result[j].anio==2021){
                        estaActual=1;
                        auxActual=result[j];
                          
                        break;                                                        
                 }
                }


                for (var j = 0; j < result.length; j++) {
                    
                     if(meses[i]==result[j].mes && result[j].anio==2020){
                        estaPasado=1;
                        auxPasado=result[j];
                        
                        break;                                                        
                 }
                }



                if(estaActual==1){
                     cantidad_actual.push(auxActual.vendido);
                     monto_actual.push(auxActual.monto_total);
                } else if(estaActual==0 ){
                    cantidad_actual.push(0);
                    monto_actual.push(0);
                }

                 if(estaPasado==1){
                    cantidad_pasado.push(auxPasado.vendido);
                    monto_pasado.push(auxPasado.monto_total);
                } else if(estaPasado==0 ){
                    cantidad_pasado.push(0);
                    monto_pasado.push(0);
                }            
                
           
        }


 
 var $salesChart = $('#sales-chart')
  // eslint-disable-next-line no-unused-vars
  var salesChart = new Chart($salesChart, {
    type: 'bar',
    data: {
      labels: ['ENE', 'FEB', 'MAR', 'ABR', 'MAY', 'JUN', 'JUL','AGO', 'SEP', 'OCT', 'NOV', 'DIC'],
      datasets: [
        {
          backgroundColor: '#007bff',
          borderColor: '#007bff',
          data: monto_actual
        },
        {
          backgroundColor: '#ced4da',
          borderColor: '#ced4da',
         data: monto_pasado
        }
      ]
    },
    options: {
      maintainAspectRatio: false,
      tooltips: {
        mode: mode,
        intersect: intersect
      },
      hover: {
        mode: mode,
        intersect: intersect
      },
      legend: {
        display: false
      },
      scales: {
        yAxes: [{
          // display: false,
          gridLines: {
            display: true,
            lineWidth: '4px',
            color: 'rgba(0, 0, 0, .2)',
            zeroLineColor: 'transparent'
          },
          ticks: $.extend({
            beginAtZero: true,

            // Include a dollar sign in the ticks
            callback: function (value) {
              if (value >= 1000) {
                value /= 1000
                value += 'k'
              }

              return '$' + value
            }
          }, ticksStyle)
        }],
        xAxes: [{
          display: true,
          gridLines: {
            display: false
          },
          ticks: ticksStyle
        }]
      }
    }
  })

        },
        error: function(data) {
            console.log(data);
        }
    });
});





            