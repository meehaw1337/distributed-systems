package lab1.client;

import java.io.BufferedReader;
import java.util.Scanner;

public class TCPListenerThread implements Runnable {

    private Scanner in;

    public TCPListenerThread(BufferedReader reader) {
        this.in = new Scanner(reader);
    }

    @Override
    public void run() {
        while (in.hasNextLine()) {
            String response = in.nextLine();

            System.out.println("Received via TCP: " + response);
        }

        System.out.println("Disconnected from the server");
    }
}
