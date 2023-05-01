package es.uma.rysd.app;

import javax.net.ssl.HttpsURLConnection;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import es.uma.rysd.entities.*;

public class SWClient {
	// Nombre de la aplicación irrelevante
	private final String app_name = "Hidalgo";
	private final int year = 2021;

	private final String url_api = "https://swapi.dev/api/";

	// Obtiene la URL del recurso id del tipo resource
	public String generateEndpoint(String resource, Integer id) {
		return url_api + resource + "/" + id + "/";
	}

	// Dada una URL de un recurso obtiene su ID
	public Integer getIDFromURL(String url) {
		String[] parts = url.split("/");

		return Integer.parseInt(parts[parts.length - 1]);
	}

	// Consulta un recurso y devuelve cuántos elementos tiene
	public int getNumberOfResources(String resource){    	
		URL dir = null;
		HttpsURLConnection connection = null;
		InputStream contenido = null;
		int conf;
		
    	// Creamos la URL correspondiente: https://swapi.dev/api/{recurso}/ 
    	try {
    		dir = new URL(url_api + resource + "/");
    	} catch (MalformedURLException e) {
    		System.err.println("Error al proporcionar la url");
    		return 0;
    	}
		
    	// Creamos la conexión a partir de la URL
    	try {
    		connection = (HttpsURLConnection) dir.openConnection();
    	} catch (IOException e) {
    		System.err.println("Error al establecer la conexión");
    		return 0;
    	}
    	
    	// Añadimos las cabeceras User-Agent y Accept 
    	connection.setRequestProperty("Accept", "application/json");
    	connection.setRequestProperty("User-Agent", app_name + "-" + year);
    	
    	// Indiquamos que es una petición GET
    	try {
    		connection.setRequestMethod("GET");
    	} catch (ProtocolException e) {
    		System.err.println("Error al establecer el método");
    		return 0;
    	}
    	
    	// Comprobamos que el código recibido en la respuesta es correcto
    	try {
    		conf = connection.getResponseCode();
			contenido = connection.getInputStream();
			
			if (conf < 200 || conf > 299) { // Ha de ser del tipo 2XX
	    		System.err.println("Código de respuesta notifica un error");
	        	return 0;
	    	} 
		} catch (IOException e) {
			System.err.println("Error al acceder al contenido");
			return 0;
		}
    	
    	// Deserializamos la respuesta a ResourceCountResponse
        Gson parser = new Gson();
        ResourceCountResponse c = parser.fromJson(new InputStreamReader(contenido), ResourceCountResponse.class);
       
        // Devolvemos el número de elementos
        return c.count;
	}

	public Person getPerson(String urlname) {
		Person p = null;
		HttpsURLConnection connection = null;
		InputStream contenido = null;
		int conf;
			
		// Por si acaso viene como http la pasamos a https
		urlname = urlname.replaceAll("http:", "https:");
		
		URL dir = null;
		try {
			dir = new URL(urlname);
		} catch (MalformedURLException e1) {
			System.err.println("Url proporcionada errónea");
		}

		// Creamos la conexión a partir de la URL recibida
		try {
    		connection = (HttpsURLConnection) dir.openConnection();
    	} catch (IOException e) {
    		System.err.println("Error al establecer la conexión");
    		return p;
    	}
		
		// Añadimos las cabeceras User-Agent y Accept
		connection.setRequestProperty("Accept", "application/json");
    	connection.setRequestProperty("User-Agent", app_name + "-" + year);
    
		// Indiquamos que es una petición GET
    	try {
    		connection.setRequestMethod("GET");
    	} catch (ProtocolException e) {
    		System.err.println("Error al establecer el método");
    		return p;
    	}
		// Compruebamos que el código recibido en la respuesta es correcto
    	try {
    		conf = connection.getResponseCode();
			contenido = connection.getInputStream();
			
			if (conf < 200 || conf > 299) { // Ha de ser del tipo 2XX
	    		System.err.println("Código de respuesta notifica un error");
	        	return p;
	    	} 
		} catch (IOException e) {
			System.err.println("Error al acceder al contenido");
			return p;
		}
		// Deserializamos la respuesta a Person
    	Gson parser = new Gson();
    	try {
			p = parser.fromJson(new InputStreamReader(connection.getInputStream()), Person.class);
		} catch (JsonSyntaxException | JsonIOException | IOException e) {
			System.err.println("Error al parsear la persona");
			return p;
		}
		
    	// A partir de la URL en el campo homeworld obtemos los datos del planeta y lo almacenamos en homeplanet
    	p.homeplanet = getPlanet(p.homeworld);

		return p;
	}

	public Planet getPlanet(String urlname) {
		Planet p = null;
		HttpsURLConnection connection = null;
		InputStream contenido = null;
		int conf;
				
		// Por si acaso viene como http la pasamos a https
		urlname = urlname.replaceAll("http:", "https:");
				
		URL dir = null;
		try {
			dir = new URL(urlname);
		} catch (MalformedURLException e1) {
			System.err.println("Url proporcionada errónea");
		}
			
		// Tratamos de forma adecuada las posibles excepciones que pueden producirse

		// Creamos la conexión a partir de la URL recibida
		try {
    		connection = (HttpsURLConnection) dir.openConnection();
    	} catch (IOException e) {
    		System.err.println("Error al establecer la conexión");
    		return p;
    	}

		// Añadimos las cabeceras User-Agent y Accept 
		connection.setRequestProperty("Accept", "application/json");
    	connection.setRequestProperty("User-Agent", app_name + "-" + year);
    
		// Indiquamos que es una petición GET
    	try {
    		connection.setRequestMethod("GET");
    	} catch (ProtocolException e) {
    		System.err.println("Error al establecer el método");
    		return p;
    	}
    	
		// Compruebamos que el código recibido en la respuesta es correcto
    	try {
    		conf = connection.getResponseCode();
			contenido = connection.getInputStream();
			
			if (conf < 200 || conf > 299) { // Ha de ser del tipo 2XX
	    		System.err.println("Código de respuesta notifica un error");
	        	return p;
	    	} 
		} catch (IOException e) {
			System.err.println("Error al acceder al contenido");
			return p;
		}
    	
		// Deserializamos la respuesta a Planet
    	Gson parser = new Gson();
    	try {
			p = parser.fromJson(new InputStreamReader(connection.getInputStream()), Planet.class);
		} catch (JsonSyntaxException | JsonIOException | IOException e) {
			System.err.println("Error al parsear el planeta");
		}
    	
		return p;
	}

	public Person search(String name) {
		Person p = null;
		URL dir = null;
		SearchResponse s = null;
		HttpsURLConnection connection = null;
		InputStream contenido = null;
		int conf;

		// Creamos la conexión a partir de la URL (url_api + name tratado)
		try {
			dir = new URL(url_api + "people/?" + URLEncoder.encode(name, "utf-8"));
		} catch (MalformedURLException | UnsupportedEncodingException e) {
			System.err.println("Error al acceder al recurso web");
			return p;
		} 
		
		try {
    		connection = (HttpsURLConnection) dir.openConnection();
    	} catch (IOException e) {
    		System.err.println("Error al establecer la conexión");
    		return p;
    	}

		// Añadimos las cabeceras User-Agent y Accept 
		connection.setRequestProperty("Accept", "application/json");
    	connection.setRequestProperty("User-Agent", app_name + "-" + year);

		// Indiquamos que es una petición GET
    	try {
    		connection.setRequestMethod("GET");
    	} catch (ProtocolException e) {
    		System.err.println("Error al establecer el método");
    		return p;
    	}

		// Compruebamos que el código recibido en la respuesta es correcto
    	try {
    		conf = connection.getResponseCode();
			contenido = connection.getInputStream();
			
			if (conf < 200 || conf > 299) { // Ha de ser del tipo 2XX
	    		System.err.println("Código de respuesta notifica un error");
	        	return p;
	    	} 
		} catch (IOException e) {
			System.err.println("Error al acceder al contenido");
			return p;
		}

		// Deserializamos la respuesta a SearchResponse -> usando la primera posición del array como resultado
    	Gson parser = new Gson();
    	try {
    		s = parser.fromJson(new InputStreamReader(connection.getInputStream()), SearchResponse.class);
		} catch (JsonSyntaxException | JsonIOException | IOException e) {
			System.err.println("Error al parsear el planeta");
		}
    	p = s.results[0];
    	
		// A partir de la URL en el campo homeworld obtenemos los datos del planeta y lo almacenamos homeplanet
    	p.homeplanet = getPlanet(p.homeworld);

		return p; 
	}
	
	public Vehicle getVehicle(String urlname) {
		Vehicle p = null;
		HttpsURLConnection connection = null;
		InputStream contenido = null;
		int conf;
			
		// Por si acaso viene como http la pasamos a https
		urlname = urlname.replaceAll("http:", "https:");
		
		URL dir = null;
		try {
			dir = new URL(urlname);
		} catch (MalformedURLException e1) {
			System.err.println("Url proporcionada errónea");
		}

		// Creamos la conexión a partir de la URL recibida
		try {
    		connection = (HttpsURLConnection) dir.openConnection();
    	} catch (IOException e) {
    		System.err.println("Error al establecer la conexión");
    		return p;
    	}
		
		// Añadimos las cabeceras User-Agent y Accept
		connection.setRequestProperty("Accept", "application/json");
    	connection.setRequestProperty("User-Agent", app_name + "-" + year);
    
		// Indiquamos que es una petición GET
    	try {
    		connection.setRequestMethod("GET");
    	} catch (ProtocolException e) {
    		System.err.println("Error al establecer el método");
    		return p;
    	}
		// Compruebamos que el código recibido en la respuesta es correcto
    	try {
    		conf = connection.getResponseCode();
			contenido = connection.getInputStream();
			
			if (conf < 200 || conf > 299) { // Ha de ser del tipo 2XX
	    		System.err.println("Código de respuesta notifica un error");
	        	return p;
	    	} 
		} catch (IOException e) {
			// Hay algunos casos en los que conf es null. Creo es algo malo con el servidor
			return p; // Devolvemos null indicando que no se ha podido obtener
		}
		// Deserializamos la respuesta a Person
    	Gson parser = new Gson();
    	try {
			p = parser.fromJson(new InputStreamReader(connection.getInputStream()), Vehicle.class);
		} catch (JsonSyntaxException | JsonIOException | IOException e) {
			System.err.println("Error al parsear el vehiculo");
			return p;
		}
	
		return p;
	}

}
