package lab1.server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;
import java.util.Map;

public class UDPServerThread implements Runnable {

    private DatagramSocket datagramSocket;

    private int port;

    public UDPServerThread(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try {
            this.datagramSocket = new DatagramSocket(port);
            byte[] receiveBuffer = new byte[1024];

            while (true) {
                Arrays.fill(receiveBuffer, (byte) 0);
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);

                datagramSocket.receive(receivePacket);
                String msg = new String(receivePacket.getData());
                String nickname, message;

                try {
                    nickname = msg.split("!UDP!")[0];
                    message = msg.split("!UDP!")[1];
                } catch (IndexOutOfBoundsException e ) {
                    System.out.println("Invalid message format: " + msg);

                    continue;
                }

                System.out.println("UDP message from: " + nickname + ", message: " + message);

                this.sendMessageToOtherClients(nickname + ": " + message, nickname);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (datagramSocket != null) {
                datagramSocket.close();
            }
        }
    }

    private void sendMessageToOtherClients(String message, String nickname) {
        Server.getLock().lock();

        try {
            Map<String, TCPClientHandlerThread> clientThreads = Server.getClientThreads();

            try {
                for (TCPClientHandlerThread clientThread : clientThreads.values()) {
                    if (!clientThread.getNickname().equals(nickname)) {
                        byte[] sendBuffer = message.getBytes();

                        DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length,
                                clientThread.getClientSocket().getInetAddress(), clientThread.getClientSocket().getPort());

                        datagramSocket.send(sendPacket);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } finally {
            Server.getLock().unlock();
        }
    }
}
