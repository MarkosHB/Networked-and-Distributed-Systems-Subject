package es.uma.rysd.bloque2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author Marcos Hidalgo Baños
 */

public class ClienteUDP {
    public static void main(String[] args) throws IOException {

        //* Comprobamos que el paso de parametros corresponde al eperado
        if (args.length < 2) {
            System.out.println("Espero IP y puerto y no los encuentro");
            System.exit(1);
        }

        //* DATOS DEL SERVIDOR:
        String serverName = args[0]; //direccion local
        int serverPort = Integer.parseInt(args[1]); // puerto de conexion

        //* Socket para el envio/recepcion de datagramas
        DatagramSocket serviceSocket = null;

        //* Creamos el socket asociandolo al primer puerto libre
        try {
            serviceSocket = new DatagramSocket();
        } catch (SocketException e) {
            System.err.println("Error al crear el socket");
            System.exit(-1);
        }

        // INICIALIZA ENTRADA POR TECLADO
        BufferedReader stdIn = new BufferedReader( new InputStreamReader(System.in) );
        System.out.print("Introduzca un texto a enviar (END para acabar): ");

        //* Leemos la cadena almacenada userInput
        String userInput = null;
        try {
            userInput = stdIn.readLine(); /*CADENA ALMACENADA EN userInput*/
        } catch (Exception e) {
            System.err.println("No puedo leer de teclado");
            System.exit(-1);
        }

        //* Comprobamos si el usuario quiere terminar el servicio
        while (userInput != null && !"END".equals(userInput))
        {
            byte [] datosEnvio = userInput.getBytes(StandardCharsets.UTF_8);
            //* Creamos el datagrama con la cadena escrita en el cuerpo
            DatagramPacket envioInfo = new DatagramPacket(
                    datosEnvio,
                    datosEnvio.length,
                    InetAddress.getByName(serverName),
                    serverPort);

            //* Enviamos el datagrama a traves del socket
            try {
                serviceSocket.send(envioInfo);
            } catch (Exception e) {
                System.err.println("No se puede enviar la informacion");
                System.exit(-1);
            }

            System.out.println("STATUS: Waiting for the reply");

            //* Crear e inicializar un datagrama VACIO para recibir la respuesta de máximo 500 bytes
            byte [] datosRecibidos = new byte[500];
            DatagramPacket recepcion = new DatagramPacket(datosRecibidos, datosRecibidos.length);

            //* Recibimos el datagrama de respuesta
            try {
                serviceSocket.receive(recepcion);
            } catch (Exception e) {
                System.err.println("No se puede recibir la informacion");
                System.exit(-1);

            }

            //* Extraemos el contenido del cuerpo del datagrama en variable line
            String line = new String(
                    recepcion.getData(),
                    recepcion.getOffset(),
                    recepcion.getLength(),
                    StandardCharsets.UTF_8);

            System.out.println("Respuesta del servidor: " + line);
            System.out.println("Introduzca un texto a enviar (END para acabar): ");
            try {
                userInput = stdIn.readLine();
            } catch (Exception e) {
                System.err.println("No puedo leer de teclado");
                System.exit(-1);
            }
        }

        System.out.println("STATUS: Closing client");

        //* Cerramos el socket del cliente
        serviceSocket.close();

        System.out.println("STATUS: closed");
    }
}
