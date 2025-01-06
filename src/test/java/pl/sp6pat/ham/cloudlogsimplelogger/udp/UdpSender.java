package pl.sp6pat.ham.cloudlogsimplelogger.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UdpSender {
    public static void main(String[] args) {
        String message = "Hello, UDP!";
        String host = "localhost";
        int port = 9876;

        try (DatagramSocket socket = new DatagramSocket()) {
            byte[] data = message.getBytes();
            InetAddress address = InetAddress.getByName(host);

            DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
            socket.send(packet);

            System.out.println("Wysłano pakiet: " + message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}