package es.uma.rysd.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import es.uma.rysd.entities.Person;
import es.uma.rysd.entities.Person;
import es.uma.rysd.entities.Planet;
import es.uma.rysd.entities.Vehicle;

public class Main {	
	private static Random rand; // para números aleatorios
	private static Scanner sc; // para leer de teclado
	private final static String proxy = "proxy.lcc.uma.es";
	private final static String proxy_port = "3128";
	
    public static void main(String[] args) {
    	// Descomente las siguiente líneas si lo está probando en el laboratorio y accede a Internet usando el proxy
    	// System.setProperty("https.proxyHost",proxy); 
    	// System.setProperty("https.proxyPort",proxy_port);
    	
        SWClient sw = new SWClient();
        String respuesta = null;
    	rand = new Random();
        sc = new Scanner(System.in);

        do{
        	masAlto(sw);
        	quienVive1(sw);
        	quienVive2(sw);
        	categoriaVehiculo(sw);
	       	System.out.println("Desea otra ronda (s/n)?");
	       	respuesta = sc.nextLine();
	    }while(respuesta.equals("s"));
        sc.close();
        
    }
    
    // Genera un número entre 0 y max-1 que no haya sido usado previamente (los usados vienen en l)
    public static Integer getRandomResource(int max, List<Integer> l){
    	if(max == l.size()) return null;

    	Integer p = rand.nextInt(max);
    	while(l.contains(p)){
    		p = (p+1)%max;
    	}
    	return p;
    }
    
    // Pregunta que obtiene dos recursos iguales (personajes en este caso) y los compara
    public static void masAlto(SWClient sw){
    	// Obteniendo la cantidad de gente almacenada
    	int max_people = sw.getNumberOfResources("people");
    	if(max_people == 0){
    		System.out.println("No se encontraron personas.");
    		return;
    	}
    	
    	System.out.println("Generando nueva pregunta...");
    	// Cogiendo dos personas al azar sin repetir
        List<Integer> used = new ArrayList<Integer>();
    	List<Person> people = new ArrayList<Person>();
    	int contador = 0;
    	while(contador < 2){
    		Integer p = getRandomResource(max_people, used);
    		Person person = sw.getPerson(sw.generateEndpoint("people", p));
    		if(person == null){
    			System.out.println("Hubo un error al encontrar el recurso " + p);
    		} else {
    			people.add(person);
    			contador++;
    		}
    		used.add(p);
    	}
    	
    	// Escribiendo la pregunta y leyendo la opción
    	Integer n = null;
    	do{
    		System.out.println("¿Quién es más alto? [0] "+ people.get(0).name + " o [1] " + people.get(1).name);
    		try{
    			n = Integer.parseInt(sc.nextLine());
    		}catch(NumberFormatException ex){
    			n = -1;
    		}
    	}while(n != 0 && n != 1);
    	
    	// Mostrando la información de los personajes elegidos
    	for(Person p: people){
    		System.out.println(p.name + " mide " + p.height);
    	}
    	
    	// Resultado
    	if(Double.parseDouble(people.get(n).height) >= Double.parseDouble(people.get((n+1)%2).height)){
    		System.out.println("Enhorabuena!!! "+ acierto[rand.nextInt(acierto.length)]);
    	} else {
    		System.out.println("Fallaste :( " + fracaso[rand.nextInt(fracaso.length)]);
    	}
    }
    
    // Pregunta que relaciona varios recursos:
    // - Elige un recurso (planeta en este caso)
    // - Pregunta sobre algún recurso relacionado (persona que nació o fue crear ahí)
    // - Busca ese recurso y comprueba si está relacionado con el primero (si la persona buscada tiene como planeta el original)
    public static void quienVive1(SWClient sw){
    	// Obteniendo la cantidad de planetas
    	int max_planet = sw.getNumberOfResources("planets");
    	if(max_planet == 0){
    		System.out.println("No se encontraron planetas.");
    		return;
    	}
    	
    	System.out.println("Generando nueva pregunta...");
    	// Obteniendo el planeta (que tenga personas)
        List<Integer> used = new ArrayList<Integer>();
        Planet planet = null;
        do{
        	Integer p = getRandomResource(max_planet, used);
        	planet = sw.getPlanet(sw.generateEndpoint("planets", p));
    		if(planet == null){
    			System.out.println("Hubo un error al encontrar el recurso " + p);
    		}
        	used.add(p);
        }while(planet == null || planet.residents == null || planet.residents.length < 1);

        // Planteamos la pregunta
        String s = null;
   		System.out.println("¿Quién nació o fue creado en " + planet.name + "?");
   		s = sc.nextLine();
   		// Buscamos la persona indicada
   		Person p = sw.search(s);
   		
   		// Validamos la respuesta y mostramos sus datos
   		if(p == null){
   			System.out.println("No hay nadie con ese nombre");
   		} else {
   			System.out.println(p.name + " nació en " + p.homeplanet.name);
   		}
   		
   		// Resultados
   		if(p != null && p.homeplanet.name.equals(planet.name)){
    		System.out.println("Enhorabuena!!! "+ acierto[rand.nextInt(acierto.length)]);
    	} else {
    		System.out.println("Fallaste :( " + fracaso[rand.nextInt(fracaso.length)]);
    	}
    }
    
    // Similar a la previa pero en vez de solicitar que escriba se le ofrecen alternativa para ello:
    // - Se elige una al azar de las disponibles en el recurso, persona del planeta (la correcta)
    // - Se buscar aleatoriamente otras 3 que no estén relacionados con el recurso (las incorrectas)
    // - Se inserta la correcta de forma aleatoria
    public static void quienVive2(SWClient sw){
    	
    	// Obteniendo la cantidad de planetas y personas
    	int max_people = sw.getNumberOfResources("people");
    	int max_planet = sw.getNumberOfResources("planets");
    	if(max_people == 0 || max_planet == 0){
    		System.out.println("No se encontraron personas o planetas.");
    		return;
    	}
    	
    	System.out.println("Generando nueva pregunta...");
    	// Obteniendo el planeta (con personas)
        List<Integer> used = new ArrayList<Integer>();
        Planet planet = null;
        do{
        	Integer p = getRandomResource(max_planet, used);
        	planet = sw.getPlanet(sw.generateEndpoint("planets", p));
    		if(planet == null){
    			System.out.println("Hubo un error al encontrar el recurso " + p);
    		}
        	used.add(p);
        }while(planet == null || planet.residents == null || planet.residents.length < 1);
        used.clear();
        // Cogemos uno al azar como acierto
        String [] residents = planet.residents;
        Person correcta = sw.getPerson(residents[rand.nextInt(residents.length)]);
        // Metemos todas las personas del planeta como usados para que no se seleccionen
        for(String s: residents){
        	used.add(sw.getIDFromURL(s));
        }
        
        // Buscamos 3 más
        List<Person> people = new ArrayList<Person>();
        int contador = 0;
    	while(contador < 3){
    		Integer p = getRandomResource(max_people, used);
    		Person person = sw.getPerson(sw.generateEndpoint("people", p));
    		if(person == null){
    			System.out.println("Hubo un error al encontrar el recurso " + p);
    		} else {
    			people.add(person);
    			contador++;
    		}
    		used.add(p);
    	}
    	// Metemos el correcto de forma aleatoria
    	int pos_acierto = rand.nextInt(4);
    	people.add(pos_acierto, correcta);
    	
    	// Leemos la opción
    	Integer n = null;
    	do{
    		System.out.print("¿Quién nació o fue fabricado en "+planet.name +"?");
    		for(int i = 0; i < 4; i++){
    			System.out.print(" ["+i+"] "+ people.get(i).name);
    		}
    		System.out.println();
    		try{
    			n = Integer.parseInt(sc.nextLine());
    		}catch(NumberFormatException ex){
    			n = -1;
    		}
    	}while(n < 0 || n > 3);
    	
    	// Se muestran los resultados    	
    	for(Person p: people){
    		System.out.println(p.name + " nació en " + p.homeplanet.name);
    	}
    	
    	// Resultados
    	if(n == pos_acierto){
    		System.out.println("Enhorabuena!!! "+ acierto[rand.nextInt(acierto.length)]);
    	} else {
    		System.out.println("Fallaste :( " + fracaso[rand.nextInt(fracaso.length)]);
    	}
    }   
      
    // Método propio creado para la práctica
    // Se le muestra al usuario tres vehículos distintos y un tipo de vehículo
    // Debe asociar la categoría a uno de los tres vehículos
    public static void categoriaVehiculo(SWClient sw){
    	// Obteniendo la cantidad de vehiculos almacenada
    	int max_vehicles = sw.getNumberOfResources("vehicles");
    	if (max_vehicles == 0){
    		System.out.println("No se encontraron vehículos.");
    		return;
    	}
    	
    	System.out.println("Generando nueva pregunta...");
    	// Cogiendo tres vehiculos al azar sin repetir
        List<Integer> used = new ArrayList<Integer>();
    	List<Vehicle> vehicles = new ArrayList<Vehicle>();
    	int contador = 0;
    	while(contador < 3){
    		Integer p = getRandomResource(max_vehicles, used);
    		Vehicle v = sw.getVehicle(sw.generateEndpoint("vehicles", p));
    		if(v == null){
    			// Hay casos en los que el servidor no reacciona a algunas peticiones
    			// No creo que sea problema de mi código, así que opto por obviar los v = null
    			continue; 
    		} else {
    			vehicles.add(v);
    			contador++;
    		}
    		used.add(p);
    	}
    	
    	// Elegimos una solución de las opciones ofrecidas
    	Random r = new Random();
    	String solution = vehicles.get(r.nextInt(3)).vehicle_class;
    	
    	// Escribiendo la pregunta y leyendo la opción
    	Integer n = null;
    	do{
    		System.out.println("¿Qué vehículo es de la categoría " + solution + "? [0] " + vehicles.get(0).name +
    							" , [1] " + vehicles.get(1).name + " o [2] " + vehicles.get(2).name );
    		try{
    			n = Integer.parseInt(sc.nextLine());
    		}catch(NumberFormatException ex){
    			n = -1;
    		}
    	}while(n != 0 && n != 1 && n!=2);
    	
    	// Mostrando la información de los vehiculos elegidos
    	for(Vehicle v: vehicles){
    		System.out.println(v.name + " es de la clase " + v.vehicle_class);
    	}
    	
    	// Resultado
    	if(solution == vehicles.get(n).vehicle_class){
    		System.out.println("Enhorabuena!!! "+ acierto[rand.nextInt(acierto.length)]);
    	} else {
    		System.out.println("Fallaste :( " + fracaso[rand.nextInt(fracaso.length)]);
    	}
    }
  
	private static String [] acierto = {"Ese es el camino", 
			"Eres uno con la Fuerza. La Fuerza está contigo",
			"Que la fuerza te acompañe",
			"Nada ocurre por accidente",
			"Sin duda, maravillosa tu mente es",
			"Cuando te fuiste, no era más que el aprendiz. Ahora eres el maestro",
			"La Fuerza te está llamando, déjala entrar",
			"Tu cantidad de midiclorianos debe ser muy alta",
			"Una lección aprendida es una lección ganada",
			"Debes creer en ti mismo o nadie lo hará",
			"El camino a la sabiduria es sencillo para aquellos que no se dejan cegar por el ego" };
	private static String [] fracaso = {"El mejor profesor, el fracaso es.",
			"El miedo es el camino hacia el Lado Oscuro. El miedo lleva a la ira, la ira lleva al odio, el odio lleva al sufrimiento. Percibo mucho miedo en ti",
			"Tu carencia de fe resulta molesta",
			"La capacidad de hablar no te hace inteligente",
			"Concéntrate en el momento. Siente, no pienses, usa tu instinto",
			"No lo intentes. Hazlo, o no lo hagas, pero no lo intentes",
			"Paciencia, utiliza la Fuerza. Piensa",
			"Siento una perturbación en la fuerza",
			"El lado oscurso se intensifica en ti",
			"El primer paso para corregir un error es la paciencia",
			"El exceso de confianza es el mas peligroso de los descuidos",
			"El camino de la ignorancia es guiado por el miedo" };

}
