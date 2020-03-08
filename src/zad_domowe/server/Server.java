package zad_domowe.server;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Server {

    private static Map<String, TCPClientHandlerThread> clientThreads = new HashMap<>();

    private static Lock lock = new ReentrantLock();

    public static int PORT_NUMBER = 12345;

    public static void main(String[] args) {

        ExecutorService executor = Executors.newFixedThreadPool(30);

        // Start UDP & TCP listening threads
        Thread udpThread = new Thread(new UDPServerThread(PORT_NUMBER));
        executor.execute(udpThread);

        Thread tcpThread = new Thread(new TCPServerThread(PORT_NUMBER, executor));
        executor.execute(tcpThread);
    }

    public static Map<String, TCPClientHandlerThread> getClientThreads() {
        return clientThreads;
    }

    public static boolean addClient(TCPClientHandlerThread TCPClientHandlerThread, String nickname) {
        lock.lock();
        try {
            if (clientThreads.keySet().stream().anyMatch(s -> s.equals(nickname))) {
                return false;
            }

            clientThreads.put(nickname, TCPClientHandlerThread);
            return true;
        } finally {
            lock.unlock();
        }
    }

    public static void removeClient(String nickname) {
        lock.lock();
        try {
            clientThreads.remove(nickname);
        } finally {
            lock.unlock();
        }
    }

    public static Lock getLock() {
        return lock;
    }
}
