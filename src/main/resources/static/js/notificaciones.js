
const socketUrl = 'ws://localhost:7008';
var websocket = new WebSocket(socketUrl);


// Aquí define el ID de usuario que deseas enviar
const userId = $("#userId").val();





websocket.onopen = function(event) {
	console.log("Conexión WebSocket abierta.");
	websocket.send(JSON.stringify({
		userId: userId,
		content: 'Hola servidor, soy otro mensaje desde el cliente.'
	}));

	websocket.onmessage = function(message) {
		var jsonData = JSON.parse(message.data);
		var not = "";
		if (jsonData !== null) {
			document.getElementById('cantNoti').innerText = jsonData.length;
			document.getElementById('cantNotiT').innerText = jsonData.length;
			for (var i = 0; i < jsonData.length; i++) {
				let fecha = new Date(jsonData[i].fecha);
				let fechaActual = new Date();
				let diferenciaMilisegundos = fechaActual - fecha;
				let segundos = Math.floor(diferenciaMilisegundos / 1000);
				let minutos = Math.floor(segundos / 60);
				let horas = Math.floor(minutos / 60);
				let dias = Math.floor(horas / 24);

				horas = horas % 24;
				minutos = minutos % 60;
				segundos = segundos % 60;
				var horaMuestra = horas + " hor. " + minutos + " min. " + segundos + " seg.";

				not += `<li class="notification-item">
								<i class="` + jsonData[i].clase + `"></i>
								<div>
									<h4>` + jsonData[i].cantidad +" - " + jsonData[i].tipo + `</h4>								
									<a href="javaScript:abrirModal('notificacionModal')">` + jsonData[i].notificacion + `</a>
									<p>` + horaMuestra + `</p>
								</div>
							</li>
							<li>
								<hr class="dropdown-divider">
							</li>`;

			}
			$("#listaNoti").append(not);
		}
	}

};
websocket.onclose = function() {
	console.log("Disconnected from WebSocket");
};

websocket.onerror = function(error) {
	console.log("WebSocket error: " + error);
};

/*function enviarMensaje(message) {
		   if (websocket.readyState === WebSocket.OPEN) {
			   websocket.send(JSON.stringify(message));
			   console.log('Mensaje enviado al servidor:', message);
			   
			   websocket.onmessage = function (message) {
   var jsonData = JSON.parse(message.data);
   if (jsonData.message !== null) {
	   // chats.value += jsonData.message + "\n";
	   }
	   }
	   
		   } else {
			   console.error('La conexión WebSocket no está abierta.');
		   }
	   }
	   
		// Ejemplo de uso de la función enviarMensaje
	   enviarMensaje({
		   userId: userId,
		   content: 'Hola servidor, soy otro mensaje desde el cliente.'
	   });


	   
	   
  // Evento en caso de error en la conexión WebSocket
websocket.onerror = function(error) {
   console.error('Error en la conexión WebSocket:', error);
};


// Ejemplo de uso de la función enviarMensaje
	   
	  /* const userId = 1; // Replace with dynamic user ID as needed
	   const socket = new WebSocket("ws://localhost:7002/ws/notifications");

	   socket.onopen = function() {
		   console.log("Connected to WebSocket");
	   };

	   socket.onmessage = function(event) {
		   const notifications = JSON.parse(event.data);
		   displayNotifications(notifications);
	   };

	   socket.onclose = function() {
		   console.log("Disconnected from WebSocket");
	   };

	   socket.onerror = function(error) {
		   console.log("WebSocket error: " + error);
	   };

	   function displayNotifications(notifications) {
		   const notificationsDiv = document.getElementById("notifications");
		   notificationsDiv.innerHTML = "";
		   notifications.forEach(notification => {
			   const p = document.createElement("p");
			   p.textContent = notification.message;
			   notificationsDiv.appendChild(p);
		   });
	   }

	   // Function to trigger notification sending from the server
	   function fetchNotifications() {
		   fetch(`/send-notifications/${userId}`)
			   .then(response => response.json())
			   .then(data => console.log("Notifications sent:", data))
			   .catch(error => console.error("Error sending notifications:", error));
	   }

	   // Fetch notifications when the page loads
	   window.onload = fetchNotifications;
	   
	 //window.onload = fetchNotifications;   
	 */
