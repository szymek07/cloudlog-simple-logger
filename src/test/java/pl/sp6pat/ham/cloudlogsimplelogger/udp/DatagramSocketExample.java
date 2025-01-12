package pl.sp6pat.ham.cloudlogsimplelogger.udp;

import javax.swing.*;
import java.awt.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class DatagramSocketExample {
    private JFrame frame;
    private JCheckBox checkBox;
    private DatagramSocket socket;
    private Thread socketThread;
    private volatile boolean running = false;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DatagramSocketExample::new);
    }

    public DatagramSocketExample() {
        frame = new JFrame("DatagramSocket Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setLayout(new FlowLayout());
        checkBox = new JCheckBox("Uruchom DatagramSocket");
        checkBox.addActionListener(e -> {
            if (checkBox.isSelected()) {
                startSocket();
            } else {
                stopSocket();
            }
        });
        frame.add(checkBox);
        frame.setVisible(true);
    }

    private void startSocket() {
        try {
            socket = new DatagramSocket(8080);
            running = true;
            socketThread = new Thread(() -> {
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                while (running) {
                    try {
                        System.out.println("Czekam na pakiety...");
                        socket.receive(packet);
                        String received = new String(packet.getData(), 0, packet.getLength());
                        System.out.println("Odebrano: " + received);
                    } catch (Exception ex) {
                        if (running) {
                            ex.printStackTrace();
                        }
                    }
                }
                System.out.println("Socket został zatrzymany.");
            });
            socketThread.start();
            System.out.println("DatagramSocket uruchomiony.");
        } catch (Exception ex) {
            ex.printStackTrace();
            checkBox.setSelected(false);
        }
    }

    private void stopSocket() {
        try {
            running = false;
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            if (socketThread != null && socketThread.isAlive()) {
                socketThread.join();
            }
            System.out.println("DatagramSocket zatrzymany.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}