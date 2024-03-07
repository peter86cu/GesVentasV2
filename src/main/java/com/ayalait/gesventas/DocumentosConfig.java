package com.ayalait.gesventas;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class DocumentosConfig implements WebMvcConfigurer{

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		WebMvcConfigurer.super.addResourceHandlers(registry);
		/******************DESARROLLO**************************/
		/*registry.addResourceHandler("/empleados/**").addResourceLocations("file:/C:/recursos/empleados/");
		registry.addResourceHandler("/productos/**").addResourceLocations("file:/C:/recursos/productos/");
		registry.addResourceHandler("/empleados/**").addResourceLocations("file:/C:/recursos/empleados/");
		registry.addResourceHandler("/ordenes/**").addResourceLocations("file:/C:/recursos/ordenes/");
		registry.addResourceHandler("/prefacturas/**").addResourceLocations("file:/C:/recursos/prefacturas/");*/
		/****************************FIN***************************/
		
		/***********************PRODUCCION*******************************/
		registry.addResourceHandler("/empleados/**").addResourceLocations("file:/home/jelastic/conf/empleados");
		registry.addResourceHandler("/productos/**").addResourceLocations("file:/home/jelastic/conf/productos/");
		registry.addResourceHandler("/empleados/**").addResourceLocations("file:/home/jelastic/conf/empleados/");
		/*************************FIN*****************************/
	}

}
