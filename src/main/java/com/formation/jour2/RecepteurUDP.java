package com.formation.jour2;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class RecepteurUDP {
    public static void main(String[] args) {
        try {
            DatagramSocket socket = new DatagramSocket(5080);

            byte[] buffer = new byte[1024];

            System.out.println("Serveur UDP en attente de messages...");

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                socket.receive(packet);

                String message = new String(
                        packet.getData(),
                        0,
                        packet.getLength()
                );

                System.out.println("Message reçu : " + message);
                System.out.println("Depuis : " +
                        packet.getAddress().getHostAddress() +
                        ":" + packet.getPort());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}