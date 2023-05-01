package es.uma.rysd.bloque2;

/**
 *
 * @author Marcos Hidalgo Baños
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClienteTCP {

    public static void main(String[] args) {

        // DATOS DEL SERVIDOR:
        String serverName = args[0];
        int serverPort = Integer.parseInt(args[1]);

        // SOCKET
        Socket serviceSocket = null;

        // FLUJOS PARA EL ENVÍO Y RECEPCIÓN
        PrintWriter out = null;
        BufferedReader in = null;

        //* Creamos el socket y lo conectamos con servidor
        try {
            serviceSocket = new Socket(serverName, serverPort);
        } catch (IOException e) {
            System.err.println("Servidor actualmente inoperativo. Disculpe las molestias.");
            System.exit(-1);
        }

        System.out.println("Conectado al servidor con IP '" + serverName + "' y con puerto '" + serverPort + "'");

        //* Inicializar los flujos de entrada/salida del socket conectado en las variables PrintWriter y BufferedReader
        try {
            in = new BufferedReader(new InputStreamReader(serviceSocket.getInputStream()));
            out = new PrintWriter(serviceSocket.getOutputStream(), true);
        } catch (IOException e) {
            System.err.println("ERROR al inicializar el flujo de entrada y/o salida");
        }

        //* Recibimos el mensaje de bienvenida del servidor y lo mostramos
        try {
            System.out.println(in.readLine());
        } catch (IOException e) {
            System.out.println("Conexión con el servidor perdida. Terminando comunicación.");
            System.exit(-1);
        }

        // Obtenemos el texto por teclado
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String userInput = null;

        System.out.println("Introduzca un texto a enviar (END para acabar): ");
        try {
            userInput = stdIn.readLine();
        } catch (IOException e) {
            System.out.println("Conexión con el servidor perdida. Terminando comunicación.");
            System.exit(-1);
        }

        if (userInput != null) {
            //* Comprobamos si el usuario ha iniciado el fin de la interacción
            while (userInput.compareTo("END") != 0) { // bucle del servicio

                //* Enviamos el texto en userInput al servidor a través del flujo de salida del socket conectado
                out.println(userInput);

                System.out.println("Conectado a " + serverPort + ", esperando respuesta...");

                //* Recibimos el texto enviado por el servidor a través del flujo de entrada del socket conectado
                try {
                    String line = in.readLine();
                    System.out.println("Respuesta: " + line);
                } catch (IOException e) {
                    System.out.println("Conexión con el servidor perdida. Terminando comunicación.");
                    System.exit(-1);
                }

                // Leer texto de usuario por teclado
                System.out.println("Introduzca un texto a enviar (END para acabar)");
                try {
                    userInput = stdIn.readLine();
                } catch (IOException e) {
                    System.out.println("Conexión con el servidor perdida. Terminando comunicación.");
                    System.exit(-1);
                }
            } // Fin del bucle de servicio en cliente
        }

        // Salimos porque el cliente quiere terminar la interaccion, ha introducido END
        //* Enviamos - TERMINAR - al servidor para indicar el fin del Servicio
        out.println("TERMINAR");

        //* Recibimos el - VALE - del Servidor
        try {
            System.out.println(in.readLine());
        } catch (IOException e) {
            System.out.println("Conexión con el servidor perdida. Terminando comunicación.");
            System.exit(-1);
        }

        //* Cerramos flujos y socket
        try {
            in.close();
            out.close();
            serviceSocket.close();
        } catch (IOException e) {
            System.err.println("Error al cerrar las conexiones.");
            System.exit(-1);
        }

    }
}
