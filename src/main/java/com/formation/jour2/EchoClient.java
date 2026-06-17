package com.formation.jour2;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class EchoClient {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 12345;

        try (
                Socket socket = new Socket(host, port);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(
                        socket.getOutputStream(), true);
                Scanner scanner = new Scanner(System.in)
        ) {
            System.out.println("Connecté au serveur.");

            while (true) {
                System.out.print("Message : ");
                String msg = scanner.nextLine();

                out.println(msg);

                String response = in.readLine();
                System.out.println("Echo serveur : " + response);

                if ("quit".equalsIgnoreCase(msg)) {
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}