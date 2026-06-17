package com.formation.jour2;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class EmetteurUDP {
    public static void main(String[] args) {
        try {
            DatagramSocket socket = new DatagramSocket();
            socket.setSoTimeout(5000);
            String message = "Bonjour depuis le client UDP !";
            byte[] buffer = message.getBytes();

            InetAddress adresse = InetAddress.getByName("localhost");

            DatagramPacket packet = new DatagramPacket(
                    buffer,
                    buffer.length,
                    adresse,
                    5080
            );

            socket.send(packet);

            System.out.println("Message envoyé.");

            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
