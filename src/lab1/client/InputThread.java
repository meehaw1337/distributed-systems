package lab1.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class InputThread implements Runnable {

    private DatagramSocket datagramSocket;

    private DatagramSocket multicastSocket;

    private PrintWriter out;

    private Scanner scanner;

    private String nickname;

    private InetAddress address;

    private InetAddress multicastAddress;

    private int portNumber;

    private int multicastPortNumber;

    public InputThread(DatagramSocket datagramSocket, PrintWriter out, Scanner scanner, String nickname, InetAddress address,
                       DatagramSocket multicastSocket, InetAddress multicastAddress, int portNumber, int multicastPortNumber) {
        this.datagramSocket = datagramSocket;
        this.out = out;
        this.scanner = scanner;
        this.nickname = nickname;
        this.address = address;
        this.multicastAddress = multicastAddress;
        this.portNumber = portNumber;
        this.multicastPortNumber = multicastPortNumber;
        this.multicastSocket = multicastSocket;
    }

    @Override
    public void run() {
        try {
            while (true) {
                System.out.println("Write your message: ");
                String message = scanner.nextLine();


                if (message.equals("U")) {
                    System.out.println("Write your UDP message: ");
                    String udpMessage = scanner.nextLine();

                    DatagramPacket sendPacket = createUDPPacket(udpMessage, nickname, address, portNumber);

                    // send UDP message
                    datagramSocket.send(sendPacket);
                } else if (message.equals("M")) {
                    System.out.println("Write your Multicast message: ");
                    String multicastMessage = scanner.nextLine();

                    DatagramPacket sendPacket = createUDPPacket(multicastMessage, nickname, multicastAddress, multicastPortNumber);

                    // send multicast message
                    multicastSocket.send(sendPacket);
                } else {
                    // send TCP message
                    out.println(message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private DatagramPacket createUDPPacket(String message, String nickname, InetAddress address, int portNumber) {
        String udpMessage = nickname + "!UDP!" + message;
        byte[] buffer = udpMessage.getBytes();
        return new DatagramPacket(buffer, buffer.length, address, portNumber);
    }
}
