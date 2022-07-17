package lesson1.server;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable{
    private Server server;
    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private static int MAX_FILE_SIZE = 1024*1024;

    private String fileUploadedPath = "src/main/java/lesson1/server";
    private String fileToUploadName = "test.txt";

    public ClientHandler(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            this.dataInputStream = new DataInputStream(socket.getInputStream());
            this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
         readFile(fileUploadedPath, fileToUploadName);
    }

    private void readFile(String filePath, String fileName) {
        File fileUploading = new File(filePath, fileName);

        if (!fileUploading.exists()) {
            try {
                fileUploading.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            //сделать проверку: файл уже существует - вы хотите заменить его?
            System.out.println("File already exist, replacing...");
            fileUploading.delete();
        }
        try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(fileUploading))) {
            byte[] inData = new byte[MAX_FILE_SIZE];
            int count;
            while ((count = dataInputStream.read(inData)) > 0) {
                bufferedOutputStream.write(inData, 0, count);
            }
            bufferedOutputStream.flush();
            System.out.println("File uploded to server...");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
