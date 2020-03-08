package zad_domowe.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

public class TCPServerThread implements Runnable {

    private ServerSocket serverSocket;

    private int portNumber;

    private ExecutorService executor;

    public TCPServerThread(int portNumber, ExecutorService executor) {
        this.portNumber = portNumber;
        this.executor = executor;
    }

    @Override
    public void run() {
        try {
            // create socket
            serverSocket = new ServerSocket(portNumber);

            while (true) {
                // accept client
                Socket clientSocket = serverSocket.accept();
                TCPClientHandlerThread clientThread = new TCPClientHandlerThread(clientSocket);
                executor.execute(clientThread);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
