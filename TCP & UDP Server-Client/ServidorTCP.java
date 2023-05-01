package es.uma.rysd.bloque2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Marcos Hidalgo Baños
 */

class ServidorTCP {
    public static String invertir(String s) {
        StringBuilder sbr = new StringBuilder(s);
        sbr.reverse();
        return sbr.toString();
    }

    public static void main(String[] args)
    {
        // DATOS DEL SERVIDOR
        int port = Integer.parseInt(args[0]);

        // SOCKETS
        ServerSocket server = null; // Pasivo (recepción de peticiones)
        Socket client = null;       // Activo (atención al cliente)

        // FLUJOS PARA EL ENVÍO Y RECEPCIÓN
        BufferedReader in = null;
        PrintWriter out = null;

        //* Creamos e inicalizamos el socket del servidor (socket pasivo)
        try {
            server = new ServerSocket(port, 1); // backlog a 1 para hacer que el max de clientes en cola sea uno
        } catch (IOException e) {
            System.err.println("ERROR al crear el ServerSocket en puerto " + port);
            System.exit(-1);
        }

        System.out.println("ESTADO: ServerSocket creado en puerto " + port);

        while (true) // Bucle de recepción de conexiones entrantes
        {
            System.out.println("ESTADO: Esperando conexiones de clientes...");

            //* Esperamos conexiones entrantes
            try {
                client = server.accept();
            } catch (IOException e) {
                System.err.println("ERROR mientras se esperaba nuevas conexiones");
                System.exit(-1);
            }

            System.out.println("ESTADO: cliente " + client.getRemoteSocketAddress() + " conectado a puerto " + port);

            //* Inicializamos los flujos de entrada/salida del socket conectado
            try {
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                out = new PrintWriter(client.getOutputStream(), true);
            } catch (IOException e) {
                System.err.println("ERROR al inicializar el flujo de entrada y/o salida");
            }

            out.println("Bienvendo al servicio de inversión de textos");

            boolean salir = false;
            while(!salir) // Inicio bucle del servicio de un cliente
            {
                //* Recibimos el texto en line enviado por el cliente a través del flujo de entrada del socket conectado
                String line = null;
                try {
                    line = in.readLine();
                } catch (IOException e) {
                    System.err.println("ERROR de entrada/salida al leer el socket");
                }

                //* Procedemos a analizar el contenido almacenado en line
                if (line != null)
                {
                    System.out.println("ESTADO: mensaje '" + line + "' de cliente " + client.getRemoteSocketAddress());
                    //* Comprobamos si es fin de conexion - TERMINAR -
                    if (line.compareTo("TERMINAR") != 0) {
                        line = invertir(line);

                        System.out.println("ESTADO: enviando mensaje '" + line + "' al cliente " + client.getRemoteSocketAddress());
                        //* Enviamos texto al cliente a traves del flujo de salida del socket conectado
                        out.println(line);
                    } else { // El cliente quiere cerrar conexión, ha enviado - TERMINAR -
                        out.println("VALE");
                        salir = true;
                    }
                }

            } // fin del servicio

            //* Cerramos flujos y socket
            try {
                in.close();
                client.close();
            } catch (IOException e) {
                System.err.println("ERROR al cerrar el flujo de entrada o el socket del cliente " + client.getRemoteSocketAddress());
            }
            out.close();

        } // fin del bucle
    } // fin del metodo
}
