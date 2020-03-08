package lab1.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

public class UDPListenerThread implements Runnable {

    private DatagramSocket datagramSocket;

    public UDPListenerThread(DatagramSocket datagramSocket) {
        this.datagramSocket = datagramSocket;
    }

    @Override
    public void run() {
        byte[] receiveBuffer = new byte[1024];

        try {
            while (true) {
                Arrays.fill(receiveBuffer, (byte) 0);
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);

                datagramSocket.receive(receivePacket);

                String msg = new String(receivePacket.getData());
                System.out.println("Received via UDP: " + msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
