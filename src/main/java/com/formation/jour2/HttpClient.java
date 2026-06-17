package com.formation.jour2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class HttpClient {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 9090;

        try (
                Socket socket = new Socket(host, port);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(
                        socket.getOutputStream(), true);
        ) {
            System.out.println("Connecté au serveur Http.");

            // Requête HTTP
            out.println("GET /bonjour HTTP/1.1");
            out.println("Host: localhost");
            out.println(); // ligne vide obligatoire

            String line;

            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}