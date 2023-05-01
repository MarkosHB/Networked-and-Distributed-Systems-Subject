package es.uma.rysd.bloque2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author Marcos Hidalgo Baños
 */

public class ServidorUDP {
    public static String invertir(String s) {
        StringBuilder sbr = new StringBuilder(s);
        sbr.reverse();
        return sbr.toString();
    }

    public static void main(String[] args)
    {
        // DATOS DEL SERVIDOR
        int port = Integer.parseInt(args[0]); // puerto del servidor

        // SOCKET
        DatagramSocket server = null;

        //* Creamos e inicalizamos el socket del servidor
        try {
            server = new DatagramSocket(port);
        } catch (IOException e) {
            System.err.println("ERROR al crear el DatagramSocket en puerto " + port);
            System.exit(-1);
        }

        System.out.println("ESTADO: DatagramSocket creado en puerto " + port);

        // Funcion PRINCIPAL del servidor
        while (true)
        {
            System.out.println("ESTADO: Esperando conexiones de clientes...");

            //* Creamos e inicializamos un datagrama VACIO para recibir la respuesta de máximo 500 bytes
            byte [] datosRecibidos = new byte[500];
            DatagramPacket recepcion = new DatagramPacket(datosRecibidos, datosRecibidos.length);

            //* Recibimos el datagrama
            try {
                server.receive(recepcion);
            } catch (Exception e) {
                System.err.println("No se puede recibir la informacion");
                System.exit(-1);
            }

            //* Obtenemos el texto recibido
            String line = new String(
                    recepcion.getData(),
                    recepcion.getOffset(),
                    recepcion.getLength(),
                    StandardCharsets.UTF_8);

            //* Mostramos por pantalla la direccion socket (IP y puerto) del cliente y su texto
            System.out.println("ESTADO: Recibido el mensaje '" + line + "' del cliente con IP '" +
                    recepcion.getAddress().toString() + "' y cuyo puerto es " +  recepcion.getPort());

            // Invertimos la linea
            line = invertir(line);
            System.out.println("ESTADO: Enviando el mensaje '" + line + "' del cliente con IP '" +
                    recepcion.getAddress().toString() + "' y cuyo puerto es " +  recepcion.getPort());

            //* Creamos el datagrama de respuesta
            DatagramPacket sender = new DatagramPacket(
                    line.getBytes(StandardCharsets.UTF_8),
                    line.length(),
                    recepcion.getAddress(),
                    recepcion.getPort());


            //* Enviamos el datagrama de respuesta
            try {
                server.send(sender);
            } catch (IOException e) {
                System.err.println("No se puede mandar la informacion");
                System.exit(-1);
            }

        } // Fin del bucle del servicio
    }

}
