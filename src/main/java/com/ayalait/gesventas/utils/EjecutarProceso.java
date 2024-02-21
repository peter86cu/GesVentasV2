package com.ayalait.gesventas.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

 import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;

import com.ayalait.response.ResponseEjecutarProceso;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;


public class EjecutarProceso {

	public static void main(String[] args) throws IOException {
		
		//respaldoBD();
		//restaurarBD();

		//EntityManagerFactory emf= Persistence.createEntityManagerFactory("test");
		//EntityManager em= emf.createEntityManager();
		//TypedQuery<DatosStock> consulta= em.createQuery("select s.id_producto, s.cantidad stock from stock s JOIN producto p ON(p.id=s.id_producto) AND p.disponible=1",DatosStock.class);

	     String databaseName = "test";
	        String username = "root";
	        String password = "admin";
	        String backupFilePath = "C:\\My Backups\\temp.sql";

	        String[] command = new String[]{"C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysqldump", "--user=" + username, "--password=" + password, databaseName, "-r", backupFilePath};

	        try {
	            ProcessBuilder processBuilder = new ProcessBuilder(command);
	            Process process = processBuilder.start();

	            // Esperar a que el proceso termine y obtener el código de salida
	            int exitCode = process.waitFor();
	            if (exitCode == 0) {
	                System.out.println("Copia de seguridad creada exitosamente en: " + backupFilePath);
	            } else {
	                System.out.println("Ocurrió un error al crear la copia de seguridad. Código de salida: " + exitCode);
	            }
	        } catch (IOException | InterruptedException e) {
	            e.printStackTrace();
	        }
	    }

	
	
	
	
	
	public static String SALVA= "";
	public static ResponseEjecutarProceso response= new ResponseEjecutarProceso();

	
	public static ResponseEjecutarProceso respaldoBD() throws IOException{
		Process p = Runtime.getRuntime().exec("C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysqldump -v --opt --events --routines --triggers --default-character-set=utf8 -u root -padmin test");
		new HiloLector(p.getErrorStream()).start();
		InputStream is = p.getInputStream();//Pedimos la entrada
		FileOutputStream fos = new FileOutputStream("backup_tienda1.sql"); //creamos el archivo para le respaldo
		byte[] buffer = new byte[1000]; //Creamos una variable de tipo byte para el buffer

		int leido = is.read(buffer); //Devuelve el número de bytes leídos o -1 si se alcanzó el final del stream.
		while (leido > 0) {
			fos.write(buffer, 0, leido);//Buffer de caracteres, Desplazamiento de partida para empezar a escribir caracteres, Número de caracteres para escribir
			leido = is.read(buffer);
		}

		fos.close();//Cierra respaldo
		
		return response;
	}
	
	/*public static ResponseEjecutarProceso respaldoBDTest() {
		 String databaseName = "nombre_de_la_base_de_datos";
	        String username = "usuario";
	        String password = "contraseña";
	        String backupFilePath = "ruta_al_archivo_de_salvaguarda.sql";
	        
	        String[] command = new String[]{"mysqldump", "--user=" + username, "--password=" + password, databaseName, "-r", backupFilePath};

	        try {
	            ProcessBuilder processBuilder = new ProcessBuilder(command);
	            Process process = processBuilder.start();

	            // Esperar a que el proceso termine y obtener el código de salida
	            int exitCode = process.waitFor();
	            if (exitCode == 0) {
	                System.out.println("Copia de seguridad creada exitosamente en: " + backupFilePath);
	            } else {
	                System.out.println("Ocurrió un error al crear la copia de seguridad. Código de salida: " + exitCode);
	            }
	        } catch (IOException | InterruptedException e) {
	            e.printStackTrace();
	        }
	    }*/


	
	
	
	public static void restaurarBD() throws IOException {
		Process p = Runtime.getRuntime().exec("C:\\\\Program Files\\\\MySQL\\\\MySQL Server 8.0\\\\bin\\\\mysql -u root -padmin restaura");
		new HiloLector(p.getErrorStream()).start();

		 
		OutputStream os = p.getOutputStream(); //Pedimos la salida
		FileInputStream fis = new FileInputStream("backup_tienda1.sql"); //creamos el archivo para le respaldo
		byte[] buffer = new byte[1000]; //Creamos una variable de tipo byte para el buffer
		 
		int leido = fis.read(buffer);//Devuelve el número de bytes leídos o -1 si se alcanzó el final del stream.
		while (leido > 0) {
			os.write(buffer, 0, leido); //Buffer de caracteres, Desplazamiento de partida para empezar a escribir caracteres, Número de caracteres para escribir
			leido = fis.read(buffer);
		}
		 
		os.flush(); //vacía los buffers de salida
		os.close(); //Cierra
		fis.close(); //Cierra respaldo
	}

}



