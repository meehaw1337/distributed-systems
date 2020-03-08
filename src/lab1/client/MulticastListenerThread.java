package lab1.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Arrays;

public class MulticastListenerThread implements Runnable {

    private String nickname;

    public MulticastListenerThread(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public void run() {
        byte[] receiveBuffer = new byte[1024];

        try {
            MulticastSocket multicastSocket = new MulticastSocket(Client.MULTICAST_PORT);

            InetAddress group = InetAddress.getByName(Client.MULTICAST_ADDRESS);

            multicastSocket.joinGroup(group);

            while (true) {
                Arrays.fill(receiveBuffer, (byte) 0);
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);

                multicastSocket.receive(receivePacket);

                String msg = new String(receivePacket.getData());
                String nickname, message;

                try {
                    nickname = msg.split("!UDP!")[0];
                    message = msg.split("!UDP!")[1];
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("Invalid message format: " + msg);

                    continue;
                }

                if (!this.nickname.equals(nickname)) {
                    System.out.println("Received via Multicast: " + nickname + ": " + message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
