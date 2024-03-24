package com.ayalait.gesventas;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.ayalait.gesventas.controller.LoginController;

@Configuration
public class DocumentosConfig implements WebMvcConfigurer{

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		WebMvcConfigurer.super.addResourceHandlers(registry);
		/******************DESARROLLO**************************/
		if(LoginController.desarrollo) {
			registry.addResourceHandler("/empleados/**").addResourceLocations("file:/C:/recursos/empleados/");
			registry.addResourceHandler("/productos/**").addResourceLocations("file:/C:/recursos/productos/");
			registry.addResourceHandler("/titulos/**").addResourceLocations("file:/C:/recursos/titulos/");
			registry.addResourceHandler("/ordenes/**").addResourceLocations("file:/C:/recursos/ordenes/");
			registry.addResourceHandler("/prefacturas/**").addResourceLocations("file:/C:/recursos/prefacturas/");	
		}else {
			registry.addResourceHandler("/empleados/**").addResourceLocations("file:/home/jelastic/conf/empleados/");
			registry.addResourceHandler("/productos/**").addResourceLocations("file:/home/jelastic/conf/productos/");
			registry.addResourceHandler("/titulos/**").addResourceLocations("file:/home/jelastic/conf/titulos/");
			registry.addResourceHandler("/ordenes/**").addResourceLocations("file:/home/jelastic/conf/ordenes/");
			registry.addResourceHandler("/prefacturas/**").addResourceLocations("file:/home/jelastic/conf/prefacturas/");
		}
		/*registry.addResourceHandler("/empleados/**").addResourceLocations("file:/C:/recursos/empleados/");
		registry.addResourceHandler("/productos/**").addResourceLocations("file:/C:/recursos/productos/");
		registry.addResourceHandler("/titulos/**").addResourceLocations("file:/C:/recursos/titulos/");
		registry.addResourceHandler("/ordenes/**").addResourceLocations("file:/C:/recursos/ordenes/");
		registry.addResourceHandler("/prefacturas/**").addResourceLocations("file:/C:/recursos/prefacturas/");*/
		/****************************FIN***************************/
		
		/***********************PRODUCCION*******************************/
		/*registry.addResourceHandler("/empleados/**").addResourceLocations("file:/home/jelastic/conf/empleados/");
		registry.addResourceHandler("/productos/**").addResourceLocations("file:/home/jelastic/conf/productos/");
		registry.addResourceHandler("/titulos/**").addResourceLocations("file:/home/jelastic/conf/titulos/");
		registry.addResourceHandler("/ordenes/**").addResourceLocations("file:/home/jelastic/conf/ordenes/");
		registry.addResourceHandler("/prefacturas/**").addResourceLocations("file:/home/jelastic/conf/prefacturas/");*/
		/*************************FIN*****************************/
	}

}
