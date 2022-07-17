package lesson1.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static lesson1.constants.Constants.SERVER_PORT;

public class Server {

    public Server() {
        try (ServerSocket server = new ServerSocket(SERVER_PORT)) {
            while (true) {
                System.out.println("Waiting for a client...");
                Socket socket = server.accept();
                System.out.println("New client connected...");
                new Thread(new ClientHandler(this, socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
