package lab1.server;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;

public class TCPClientHandlerThread implements Runnable {

    private Socket clientSocket;

    private PrintWriter out;

    private Scanner in;

    private String nickname;

    public TCPClientHandlerThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            // in & out streams
            this.out = new PrintWriter(clientSocket.getOutputStream(), true);
            this.in = new Scanner(new InputStreamReader(clientSocket.getInputStream()));

            this.nickname = in.nextLine();

            if (!Server.addClient(this, this.nickname)) {
                out.println("This name is already used by someone else. Restart the client and choose other name.");
                clientSocket.close();
                return;
            }

            System.out.println("Client " + this.nickname + " connected");

            while (in.hasNextLine()) {
                // read msg, send response
                String msg = in.nextLine();
                System.out.println("Message received from " + this.nickname + ": " + msg);
                this.sendMessageToOtherClients(this.nickname + ": " + msg);
            }

            // remove the client from the map
            System.out.println("Client " + this.nickname + " disconnected");
            Server.removeClient(this.nickname);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessageToOtherClients(String message) {
        Server.getLock().lock();

        try {
            Map<String, TCPClientHandlerThread> clientThreads = Server.getClientThreads();

            for (TCPClientHandlerThread clientThread : clientThreads.values()) {
                if (!clientThread.getNickname().equals(this.nickname)) {
                    clientThread.getOut().println(message);
                }
            }
        } finally {
            Server.getLock().unlock();
        }
    }

    public PrintWriter getOut() {
        return out;
    }

    public void setOut(PrintWriter out) {
        this.out = out;
    }

    public Scanner getIn() {
        return in;
    }

    public void setIn(Scanner in) {
        this.in = in;
    }

    public String getNickname() {
        return nickname;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }
}
