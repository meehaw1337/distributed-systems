package lab1.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static String ADDRESS = "localhost";
    public static int PORT = 12345;

    public static String MULTICAST_ADDRESS = "225.0.0.0";
    public static int MULTICAST_PORT = 12346;

    public static void main(String[] args) throws IOException {

        // connection parameters
        String hostName = "localhost";
        InetAddress address = InetAddress.getByName(Client.ADDRESS);
        InetAddress multicastAddress = InetAddress.getByName(Client.MULTICAST_ADDRESS);

        // TCP & UDP sockets
        Socket socket = null;
        DatagramSocket datagramSocket = null;
        DatagramSocket multicastSocket = null;

        try {
            // create socket, bind UDP socket so it has the same port number as TCP socket
            socket = new Socket(hostName, Client.PORT);

            // Pass null as parameter so the socket is not bound by default
            datagramSocket = new DatagramSocket(null);
            datagramSocket.bind(socket.getLocalSocketAddress());
            multicastSocket = new DatagramSocket();

            // in & out streams, keyboard input scanner
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Scanner scanner = new Scanner(System.in);

            // acquire client's nickname, send it to the server
            System.out.println("Write your nickname: ");
            String nickname = scanner.nextLine();
            out.println(nickname);

            // start keyboard input thread and tcp, udp & multicast listener threads
            Thread inputThread = new Thread(new InputThread(datagramSocket, out, scanner, nickname, address,
                    multicastSocket, multicastAddress, Client.PORT, Client.MULTICAST_PORT));
            inputThread.start();

            Thread udpThread = new Thread(new UDPListenerThread(datagramSocket));
            udpThread.start();

            Thread tcpThread = new Thread(new TCPListenerThread(in));
            tcpThread.start();

            Thread multicastThread = new Thread(new MulticastListenerThread(nickname));
            multicastThread.start();

            // keep running until other threads are running
            inputThread.join();
            udpThread.join();
            tcpThread.join();
            multicastThread.join();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }

}
