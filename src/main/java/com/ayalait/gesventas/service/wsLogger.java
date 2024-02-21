package com.ayalait.gesventas.service;


 
  import com.ayalait.response.ResponseResultado;
import com.ayalait.utils.ErrorState;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;


@Controller
public final class wsLogger {

	
	private String hostLogger;

	ObjectWriter ow = (new ObjectMapper()).writer().withDefaultPrettyPrinter();

	@Autowired
	RestTemplate restTemplate= new RestTemplate();

   void cargarServer() throws IOException {
      Properties p = new Properties();

      try {
         URL url = this.getClass().getClassLoader().getResource("application.properties");
         if (url == null) {
            throw new IllegalArgumentException("application.properties" + " is not found 1");
         } else {
            InputStream propertiesStream = url.openStream();
            //InputStream propertiesStream = ClassLoader.getSystemResourceAsStream("application.properties");
            p.load(propertiesStream);
            propertiesStream.close();
            this.hostLogger = p.getProperty("server.logger");
         }
      } catch (FileNotFoundException var3) {
         Logger.getLogger(wsLogger.class.getName()).log(Level.SEVERE, (String)null, var3);
      }

   }

   public wsLogger() {
      try {
         this.cargarServer();
      } catch (IOException var2) {
         Logger.getLogger(wsLogger.class.getName()).log(Level.SEVERE, (String)null, var2);
      }

   }

   public ResponseResultado guardarLog(String user, String pwd) {
      
      ResponseResultado responseResult = new ResponseResultado();

		try {

			String url = this.hostLogger + "/login/token?user=" + user + "&pwd=" + pwd;
			 
			URI uri = new URI(url);
			ResponseEntity<String> response = restTemplate.exchange(uri , HttpMethod.POST, null,String.class);

			if (response.getStatusCodeValue() == 200) {

				responseResult.setStatus(true);
				responseResult.setResultado(response.getBody());

			}

		} catch (org.springframework.web.client.HttpServerErrorException e) {
			ErrorState data = new ErrorState();
			data.setCode(e.getStatusCode().value());
			data.setMenssage(e.getMessage());
			responseResult.setCode(data.getCode());
			responseResult.setError(data);
			 
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 

		return responseResult;
      
   } 

  
}