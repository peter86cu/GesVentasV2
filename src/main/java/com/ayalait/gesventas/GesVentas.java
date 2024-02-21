package com.ayalait.gesventas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;




@SpringBootApplication
public class GesVentas {

	
	public static void main(String[] args) {
		SpringApplication.run(GesVentas.class, args);
	}

	 @Bean
	public RestTemplate restTemplate() {
	    return new RestTemplate();
	} 
}
