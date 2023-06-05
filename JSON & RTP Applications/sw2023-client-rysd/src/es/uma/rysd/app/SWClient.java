package es.uma.rysd.app;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import es.uma.rysd.entities.*;

public class SWClient {
	// TODO: Complete el nombre de la aplicaci�n
    private final String app_name = "My_App_MarcosHidalgo";
    private final int year = 2023;
    
	// La url es distinta a la que se nos propociona (la otra no funciona)
    private final String url_api = "https://swapi.py4e.com/api/";

	/* ------------------------------ */
    /* Metodos auxiliares facilitados */
	/* ------------------------------ */    

    // Obtiene la URL del recurso id del tipo resource
	public String generateEndpoint(String resource, Integer id){
		return url_api + resource + "/" + id + "/";
	}
	
	// Dada una URL de un recurso obtiene su ID
	public Integer getIDFromURL(String url){
		String[] parts = url.split("/");

		return Integer.parseInt(parts[parts.length-1]);
	}

	/* ------------------------------ */
    /* Metodos propios de la practica */
	/* ------------------------------ */ 
	
	// Consulta un recurso y devuelve cu�ntos elementos tiene
	public int getNumberOfResources(String resource){   

		// TODO: Trate de forma adecuada las posibles excepciones que pueden producirse
		
    	// TODO: Cree la URL correspondiente: https://swapi.dev/api/{recurso}/ 
		// reemplazando el recurso por el par�metro 
		String url = url_api +  resource + "/";

		URL direccion;
		try {
			direccion = new URL(url);
		} catch (MalformedURLException e) {
			System.err.println("ERROR. No se puede acceder a la url formada.");
			return 0;
		}

    	// TODO: Cree la conexi�n a partir de la URL
		HttpsURLConnection connection;
		try {
			connection = (HttpsURLConnection) direccion.openConnection();
		} catch (IOException e) {
			System.err.println("ERROR. Establecimiento de la conexion fallido.");
			return 0;
		} 	

		// TODO: A�ada las cabeceras User-Agent y Accept (vea el enunciado)		
		connection.setRequestProperty("Accept", "application/json");
		connection.setRequestProperty("User-Agent", app_name + "-" + year);
		
		try {
			// TODO: Indique que es una petici�n GET
			connection.setRequestMethod("GET");
		} catch (ProtocolException e) {
			System.err.println("Error al establecer el metodo");
			return 0;
		}
    	
    	// TODO: Compruebe que el c�digo recibido en la respuesta es correcto
		try {
			if (connection.getResponseCode() < 200 || connection.getResponseCode() > 299) {
	    		System.err.println("Codigo de respuesta notifica un error");
	        	return 0;
	    	} 
		} catch (IOException e) {
			System.err.println("No se ha podido obtener el codigo de respuesta.");
			return 0;
		}

		// TODO: Obtenga el InputStream de la conexi�n
		InputStream in;
        try {
			in = connection.getInputStream(); 
		} catch (IOException e) {
			System.err.println("ERROR. No se ha podido deserializar la respuesta.");
			return 0;
		}

		// TODO: Deserialice la respuesta a ResourceCountResponse
		Gson parser = new Gson();
		ResourceCountResult c = parser.fromJson(new InputStreamReader(in), ResourceCountResult.class);
		
		// TODO: Devuelva el n�mero de elementos
        return c.count;
	}
	
	public Person getPerson(String urlname) {
    	Person p = null;
    	// Por si acaso viene como http la pasamos a https
    	urlname = urlname.replaceAll("http:", "https:");

    	// TODO: Trate de forma adecuada las posibles excepciones que pueden producirse
		    	
    	// TODO: Cree la conexi�n a partir de la URL recibida
		URL direccion;
		try {
			direccion = new URL(urlname);
		} catch (MalformedURLException e) {
			System.err.println("ERROR. No se puede acceder a la url formada.");
			return p;
		}

		HttpsURLConnection connection;
		try {
			connection = (HttpsURLConnection) direccion.openConnection();
		} catch (IOException e) {
			System.err.println("ERROR. Establecimiento de la conexion fallido.");
			return p;
		}

		// TODO: A�ada las cabeceras User-Agent y Accept (vea el enunciado)		
		connection.setRequestProperty("User-Agent", app_name + "-" + year);
		connection.setRequestProperty("Accept", "application/json");

		try {
			// TODO: Indique que es una petici�n GET
			connection.setRequestMethod("GET");
		} catch (ProtocolException e) {
			System.err.println("Error al establecer el metodo");
			return p;
		}
    	
    	// TODO: Compruebe que el c�digo recibido en la respuesta es correcto
		try {
			if (connection.getResponseCode() < 200 || connection.getResponseCode() > 299) {
	    		System.err.println("Codigo de respuesta notifica un error");
	        	return p;
	    	}
		} catch (IOException e) {
			System.err.println("No se ha podido obtener el codigo de respuesta.");
			return p;
		}
    	
    	// TODO: Obtenga el InputStream de la conexi�n
		InputStream in;
        try {
			in = connection.getInputStream(); 
		} catch (IOException e) {
			System.err.println("ERROR. No se ha podido deserializar la respuesta.");
			return p;
		}

		Gson parser = new Gson();
        try {
			p = parser.fromJson(new InputStreamReader(in), Person.class);
		} catch (JsonSyntaxException | JsonIOException e) {
			System.err.println("Error al parsear la persona");
			return p;
		}
		
    	// A partir de la URL en el campo homeworld obtemos los datos del planeta y lo almacenamos en homeplanet
    	p.homeplanet = getWorld(p.homeworld);

    	return p;
	}

	public World getWorld(String urlname) {
    	World w = null;
    	// Por si acaso viene como http la pasamos a https
    	urlname = urlname.replaceAll("http:", "https:");

    	// TODO: Trate de forma adecuada las posibles excepciones que pueden producirse
		    	
    	// TODO: Cree la conexi�n a partir de la URL recibida
		URL direccion;
		try {
			direccion = new URL(urlname);
		} catch (MalformedURLException e) {
			System.err.println("ERROR. No se puede acceder a la url formada.");
			return w;
		}

		HttpsURLConnection connection;
		try {
			connection = (HttpsURLConnection) direccion.openConnection();
		} catch (IOException e) {
			System.err.println("ERROR. Establecimiento de la conexion fallido.");
			return w;
		}

		// TODO: A�ada las cabeceras User-Agent y Accept (vea el enunciado)		
		connection.setRequestProperty("User-Agent", app_name + "-" + year);
		connection.setRequestProperty("Accept", "application/json");

		try {
			// TODO: Indique que es una petici�n GET
			connection.setRequestMethod("GET");
		} catch (ProtocolException e) {
			System.err.println("Error al establecer el metodo");
			return w;
		}
    	
    	// TODO: Compruebe que el c�digo recibido en la respuesta es correcto
		try {
			if (connection.getResponseCode() < 200 || connection.getResponseCode() > 299) {
	    		System.err.println("Codigo de respuesta notifica un error");
	        	return w;
	    	}
		} catch (IOException e) {
			System.err.println("No se ha podido obtener el codigo de respuesta.");
			return w;
		}
    	
    	// TODO: Obtenga el InputStream de la conexi�n
		InputStream in;
        try {
			in = connection.getInputStream(); 
		} catch (IOException e) {
			System.err.println("ERROR. No se ha podido deserializar la respuesta.");
			return w;
		}
    	
    	// TODO: Deserialice la respuesta a World
		Gson parser = new Gson();
		try {
			w = parser.fromJson(new InputStreamReader(in), World.class);
		} catch (JsonSyntaxException | JsonIOException e) {
			System.err.println("Error al parsear el planeta");
		}

        return w;
	}

	public Person search(String name){
    	Person p = null;
		QueryResponse s = null;

    	// TODO: Trate de forma adecuada las posibles excepciones que pueden producirse
		    	
    	// TODO: Cree la conexi�n a partir de la URL recibida
		URL direccion;
		try {
			direccion = new URL(url_api + "people/?" + URLEncoder.encode(name, "utf-8"));
		} catch (MalformedURLException | UnsupportedEncodingException e) {
			System.err.println("ERROR. No se puede acceder a la url formada.");
			return p;
		}

		HttpsURLConnection connection;
		try {
			connection = (HttpsURLConnection) direccion.openConnection();
		} catch (IOException e) {
			System.err.println("ERROR. Establecimiento de la conexion fallido.");
			return p;
		}

		// TODO: A�ada las cabeceras User-Agent y Accept (vea el enunciado)		
		connection.setRequestProperty("User-Agent", app_name + "-" + year);
		connection.setRequestProperty("Accept", "application/json");

		try {
			// TODO: Indique que es una petici�n GET
			connection.setRequestMethod("GET");
		} catch (ProtocolException e) {
			System.err.println("Error al establecer el metodo");
			return p;
		}
			
    	
    	// TODO: Compruebe que el c�digo recibido en la respuesta es correcto
		try {
			if (connection.getResponseCode() < 200 || connection.getResponseCode() > 299) {
	    		System.err.println("Codigo de respuesta notifica un error");
	        	return p;
	    	}
		} catch (IOException e) {
			System.err.println("No se ha podido obtener el codigo de respuesta.");
			return p;
		}
    	
    	// TODO: Obtenga el InputStream de la conexi�n
    	InputStream in;
        try {
			in = connection.getInputStream(); 
		} catch (IOException e) {
			System.err.println("ERROR. No se ha podido deserializar la respuesta.");
			return p;
		}

		// TODO: Deserialice la respuesta a QueryResponse -> Use la primera posici�n del array como resultado
		Gson parser = new Gson();
		try {
			s = parser.fromJson(new InputStreamReader(in), QueryResponse.class);
		} catch (JsonSyntaxException | JsonIOException e) {
			System.err.println("Error al parsear el planeta");
		}
        // TODO: Para las preguntas 2 y 3 (no necesita completar esto para la pregunta 1)
    	// TODO: A partir de la URL en el campo homreworld obtenga los datos del planeta y almac�nelo en atributo homeplanet
		p = s.results[0];
		p.homeplanet = getWorld(p.homeworld);
	
		return p;
    }

	public Vehicle getVehicle(String urlname) {
		Vehicle v = null;
    	// Por si acaso viene como http la pasamos a https
    	urlname = urlname.replaceAll("http:", "https:");

    	// TODO: Trate de forma adecuada las posibles excepciones que pueden producirse
		    	
    	// TODO: Cree la conexi�n a partir de la URL recibida
		URL direccion;
		try {
			direccion = new URL(urlname);
		} catch (MalformedURLException e) {
			System.err.println("ERROR. No se puede acceder a la url formada.");
			return v;
		}

		HttpsURLConnection connection;
		try {
			connection = (HttpsURLConnection) direccion.openConnection();
		} catch (IOException e) {
			System.err.println("ERROR. Establecimiento de la conexion fallido.");
			return v;
		}

		// TODO: A�ada las cabeceras User-Agent y Accept (vea el enunciado)		
		connection.setRequestProperty("User-Agent", app_name + "-" + year);
		connection.setRequestProperty("Accept", "application/json");

		try {
			// TODO: Indique que es una petici�n GET
			connection.setRequestMethod("GET");
		} catch (ProtocolException e) {
			System.err.println("Error al establecer el metodo");
			return v;
		}
    	
    	// TODO: Compruebe que el c�digo recibido en la respuesta es correcto
		try {
			if (connection.getResponseCode() < 200 || connection.getResponseCode() > 299) {
	    		//System.err.println("Codigo de respuesta notifica un error");
	        	return v;
	    	}
		} catch (IOException e) {
			System.err.println("No se ha podido obtener el codigo de respuesta.");
			return v;
		}
    	
    	// TODO: Obtenga el InputStream de la conexi�n
		InputStream in;
        try {
			in = connection.getInputStream(); 
		} catch (IOException e) {
			System.err.println("ERROR. No se ha podido deserializar la respuesta.");
			return v;
		}
    	
    	// TODO: Deserialice la respuesta a Vehicle
		Gson parser = new Gson();
		try {
			v = parser.fromJson(new InputStreamReader(in), Vehicle.class);
		} catch (JsonSyntaxException | JsonIOException e) {
			System.err.println("Error al parsear el vehiculo");
		}

        return v;
	}

}
