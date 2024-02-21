package com.ayalait.gesventas.controller;

import com.google.gson.Gson;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


import com.ayalait.gesventas.request.RequestVentaDevoluciones;
import com.ayalait.gesventas.utils.*;
import com.ayalait.modelo.*;
import com.ayalait.modelo.ItemsVenta;
import com.ayalait.response.*;
import com.ayalait.utils.ItemsSelect2;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;


@Controller
public class ContableController {

    private static FormatoFecha fechaSinHora;
    private static FormatoFecha fechaConHora;
    private static RequestVentaDevoluciones devolucion= new RequestVentaDevoluciones();
    private static List<VentasProductosDevolucion> lstProductos= new ArrayList<VentasProductosDevolucion>();


    public ContableController() {
        fechaSinHora = FormatoFecha.YYYYMMDD;
        fechaConHora = FormatoFecha.YYYYMMDDH24;
    }

   
    @GetMapping({"/gastos"})
    public String tipoGastos(Model modelo, RedirectAttributes attribute) {
        if (LoginController.session.getToken() != null) {
        	ResponseMonedas responseM = LoginController.conParam.listarMonedas();
			String fecha = "";
			modelo.addAttribute("user", LoginController.session.getUser());
			if (responseM.isStatus()) {
				modelo.addAttribute("listaMoneda",  responseM.getMonedas() );

				HistoricoCambio cambio = new HistoricoCambio();
				modelo.addAttribute("fechaapertura", fecha);
				modelo.addAttribute("cambio", cambio);
				 

			}else {
				modelo.addAttribute("listaMoneda", new ArrayList<Moneda>());
			}
			return "gastos";
           


        } else {
            return "login";
        }
    }

    @PostMapping({ LoginController.ruta + "/buscar-gastos" })
	public void obtenerTitulo(@ModelAttribute("accion") String accion, @ModelAttribute("q") String busqueda,
							  Model modelo, HttpServletResponse responseHttp) throws IOException, ParseException, SQLException {
		if (LoginController.session.getToken() != null) {
			List<ItemsSelect2> lstItems= new ArrayList<ItemsSelect2>();
			modelo.addAttribute("user", LoginController.session.getUser());
			 ResponseListaTipoGastos response = LoginController.conContable.listaTipoGastos();
			 if(response.isStatus()) {
				 if(!response.getTipoGasto().isEmpty()) {
					 if(busqueda.toUpperCase().equalsIgnoreCase("Todo")) {
						 for(TipoGastos gasto: response.getTipoGasto()) {
							 ItemsSelect2 items= new ItemsSelect2();
							 items.setId(String.valueOf(gasto.getId()) );
							 items.setText(gasto.getTipogasto());
							 lstItems.add(items);

						 } 
					 }else {
						 for(TipoGastos gasto: response.getTipoGasto()) {
							 if(gasto.getTipogasto().contains(busqueda.toUpperCase())) {
								 ItemsSelect2 items= new ItemsSelect2();
								 items.setId(String.valueOf(gasto.getId()) );
								 items.setText(gasto.getTipogasto());
								 lstItems.add(items); 
							 }
							  
					 }
					

					 }
				 }
			 }
			String json = (new Gson()).toJson(lstItems);
			responseHttp.setContentType("application/json");
			responseHttp.setCharacterEncoding("UTF-8");
			responseHttp.getWriter().write(json);
		} else {
			responseHttp.setContentType("application/json");
			responseHttp.setCharacterEncoding("UTF-8");
			responseHttp.getWriter().write("LoginController.session caducada");
		}

	}
   

}