package com.formation.jour2;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class HttpServer {
    public static void main(String[] args) {
        int port = 9090;

        ExecutorService pool = Executors.newCachedThreadPool();
        final AtomicInteger nbClientsConnectes = new AtomicInteger(0);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Serveur démarré sur le port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nombre de clients connectés : " + nbClientsConnectes.incrementAndGet());

                System.out.println("Client connecté : "
                        + clientSocket.getInetAddress());

                pool.submit(() -> handleClient(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket socket) {
        try (
                socket;
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(
                        socket.getOutputStream(), true)
        ) {
            /*
             * GET / HTTP/1.1
             * Host: localhost:8080
             * Connection: keep-alive
             * User-Agent: Mozilla/5.0
             */

            //Lecture premier ligne HTTP
            String requestLine = in.readLine();
            if (requestLine == null) {
                return;
            }
            System.out.println("Requête : " + requestLine);

            // Lecture des headers
            String line;
            while ((line = in.readLine()) != null
                    && !line.isEmpty()) {
                String[] header = line.split(":");
                String key = header[0].trim();
                String value = header[1];
                for(int i = 2 ; i < header.length; i++) {
                    value += ":" + header[i];
                }
                System.out.println("keyHeader: " + key + " - valueHeader : " + value);
            }

            String[] parts = requestLine.split(" ");

            String method = parts[0];
            String path = parts[1];

            String body;
            String status;

            if ("GET".equals(method) && "/".equals(path)) {
                status = "200 OK";
                body = "<h1>Accueil</h1>";
            } else if ("GET".equals(method) && "/bonjour".equals(path)) {
                status = "200 OK";
                body = "<h1>Bonjour</h1>";
            } else if ("GET".equals(method) && "/test.html".equals(path)) {
                status = "200 OK";
                body = "";
                File file = new File("src/main/resources/test.html");

                try (BufferedReader reader =
                             new BufferedReader(new FileReader(file))) {

                    String lineFile;

                    while ((lineFile = reader.readLine()) != null) {
                        body += lineFile;
                    }
                }
            } else {
                status = "404 Not Found";
                body = "<h1>404 - Page non trouvée</h1>";
            }

            String response =
                    "HTTP/1.1 " + status + "\r\n" + "Content-Type: text/html;" + "\r\n"
                            + "Content-Length: " + body.length() + "\r\n"
                            + "\r\n" +  body + "\r\n";

            out.print(response);
            out.flush();

        } catch (IOException e) {
            System.out.println("Client déconnecté.");
        }
    }
}