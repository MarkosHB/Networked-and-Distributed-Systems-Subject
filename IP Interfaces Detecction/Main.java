package pkgMain;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.net.InterfaceAddress;
import java.util.List;
import java.util.StringJoiner;



public class Main {
	
	public static void main (String []args) {
		Enumeration<NetworkInterface> lista; // Estructura de datos principal
		try {
			lista = NetworkInterface.getNetworkInterfaces();  // Recogemos las interfaces de red en la variable

			while (lista.hasMoreElements()) {	// Iteramos sobre todos los elementos de la lista
				StringBuilder sb = new StringBuilder();
				NetworkInterface elem = lista.nextElement();  //cogemos el siguiente elemento

				if (elem != null) {		// comprobamos de que hemos cogido un elemento
					byte[] mac = elem.getHardwareAddress();   // obtenemos su direccion mac asociada
					List<InterfaceAddress> ips = elem.getInterfaceAddresses(); // obtenemos su listado de IPs asociado
					if (ips.size() == 0) continue; // Nos aseguramos de que tenga al menos una IP, sino nos lo saltamos

					sb.append(elem.getName() + ": ");    // escribimos el nombre de la interfaz

					/** Seccion 1. Direccion MAC **/
					sb.append("MAC = ");
					if (mac != null) {	// comprobamos de que hemos cogido una direccion mac
						sb.append(mac2text(mac)); // la guardamos en el string builder

						/** Tarea personal 1 **/
						if ((mac[0] >> 6 & 1) == 1) { 	// And con el septimo y octavo bit del primer octeto del primer byte
							sb.append(" - local - ");
						} else {
							sb.append(" - global - ");
						}

						/** Tarea personal 2 **/
						if (elem.isUp()) { 	// Vemos si esta activa la interfaz
							sb.append("(up)");
						} else {
							sb.append("(down)");
						}

					} else {
						sb.append("No disponible");	// si no ha sido capaz de obtenerla, mensaje por defecto
					}

					sb.append(" | ");

					/** Seccion 2. Direccion IP **/
					sb.append("IP = ");
					InterfaceAddress ip = ips.get(0);	// Del listado de IPs nos quedamos con la primera para simplificar
					sb.append(ip.getAddress().getHostAddress());	// Guardamos dicha direccion

					sb.append(" ");

					/** Seccion 3. Id de red con prefijo **/
					sb.append(ip2text(ip.getAddress().getHostAddress(), ip.getNetworkPrefixLength()));

					System.out.println(sb.toString());
				}
			}
		} catch (SocketException e) {
			System.out.println(e.toString());  // Si ocurre algun error imprimimos su traza
		}
		
	}

	/**
	 * Muestra el identificador de la red a la que pertence incluyendo su prefijo
	 * @param ip incluye la direccion IP
	 * @param networkPrefixLength cantidad de 1s que tiene (mascara de red)
	 * @return String con la forma: "(___.___.___.___/__)"
	 */
	private static String ip2text(String ip, short networkPrefixLength) {
		StringJoiner sj = new StringJoiner("/", "(", ")");

		sj.add(ip.substring(0, ip.lastIndexOf('.') + 1) + "0");

		sj.add(String.format("%d", networkPrefixLength));
		return sj.toString();
	}

	/**
	 * Transforma una direccion MAC de un array de bytes a un string
	 * @param mac es un array de bytes
	 * @return string con la direccion MAC con la forma: "__:__:__:__:__:__"
	 */
	public static String mac2text(byte[] mac) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < mac.length; i++) {
			sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? ":" : ""));
		}
		return sb.toString();
	}
}





