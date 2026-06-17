package com.formation.jour2;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EchoServer {
    public static void main(String[] args) {
        int port = 12345;

        ExecutorService pool = Executors.newCachedThreadPool();
        int nbClientsConnectes = 0;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Serveur démarré sur le port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nombre de clients connectés : " + ++nbClientsConnectes);

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
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(
                        socket.getOutputStream(), true)
        ) {
            String message;

            while ((message = in.readLine()) != null) {
                System.out.println("Reçu : " + message);

                // Echo
                out.println(message);
            }
        } catch (IOException e) {
            System.out.println("Client déconnecté.");
        }
    }
}