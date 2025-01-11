package com.ayalait.gesventas.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import com.ayalait.gesventas.NotificacionesWebSocketHandler;
import com.ayalait.gesventas.utils.*;
import com.ayalait.modelo.*;
import com.ayalait.response.*;
import com.ayalait.web.ResponseVisitantes;
import com.ayalait.web.VisitantesLog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import javax.servlet.http.HttpServletResponse;

@Controller
public class DashboardController {

	private static FormatoFecha fechaSinHora;
	private static FormatoFecha fechaConHora;
	private static FormatoFecha hora;
	
	@Autowired
	RestTemplate restTemplate;
	 @Autowired
	 NotificacionesWebSocketHandler webSocketHandler;

	public DashboardController() {
		fechaSinHora = FormatoFecha.YYYYMMDD;
		fechaConHora = FormatoFecha.YYYYMMDDH24;
		hora = FormatoFecha.H24;
	}

	
	@GetMapping({ "/inicio" })
	public String showFormInicio(Model modelo) {
		if (LoginController.session.getToken() != null) {
			modelo.addAttribute("user", LoginController.session.getUser());
			ResponseMonedas response = LoginController.conParam.listarMonedas();
			ResponseVisitantes responseVisitantes= LoginController.conDashboard.visitantesMensuales();
			
			if(responseVisitantes.isStatus()) {
				modelo.addAttribute("visitantesMensuales", responseVisitantes.getVisitantes());
				
				int totalVisitas = responseVisitantes.getVisitantes().stream()
                        .mapToInt(VisitantesLog::getCantidad)
                        .sum();
				modelo.addAttribute("totalVisitantes", totalVisitas);
				
			}else {
				modelo.addAttribute("visitantesMensuales", new ArrayList<VisitantesLog>());
				modelo.addAttribute("totalVisitantes", 0);
			}
			
			String fecha = "";
			if (response.isStatus()) {
				modelo.addAttribute("listaMoneda", response.getMonedas());

				HistoricoCambio cambio = new HistoricoCambio();
				modelo.addAttribute("fechaapertura", fecha);
				modelo.addAttribute("cambio", cambio);


			}else {
				modelo.addAttribute("listaMoneda", new ArrayList<Moneda>());
			}


			return "inicio";
		}
		return "redirect:/";
	}
	
	
	@GetMapping("/send-notification/{username}")
	@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST })
	@ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> sendNotification(@PathVariable String username) {
        try {
            // Aquí enviamos un mensaje al usuario específico a través del WebSocket
            webSocketHandler.sendMessageToUser(username, "¡Hola! Tienes una nueva notificación.");

            return ResponseEntity.ok("Notificación enviada exitosamente.");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al enviar la notificación.");
        }
    }

}
