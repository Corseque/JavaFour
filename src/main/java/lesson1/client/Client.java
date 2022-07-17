package lesson1.client;

import javafx.event.ActionEvent;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;

import static lesson1.constants.Constants.*;

public class Client {
    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private String fileToUploadPath = "src/main/java/lesson1/client";
    private String fileToUploadName = "test.txt";

    public static void main(String[] args) {
        new Client();
    }

    public Client() {
        openConnection();
    }

    private void openConnection() {
        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            System.out.println("Connected to server...");
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            uploadFile(fileToUploadPath, fileToUploadName);
//            Thread writeThread = new Thread(() -> uploadFile(fileToUploadPath, fileToUploadName));
//            writeThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    private void closeConnection() {
        try {
            dataOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            dataInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void uploadFile(String filePath, String fileName) {
        File fileToUpload = new File(filePath, fileName);
        byte[] fName = fileName.getBytes();
        byte[] fSizeInBytes = longToBytes(fileToUpload.length());
        byte[] fData = new byte[(int) fileToUpload.length()];

        byte[] outData = new byte[(int) (
                /*длина команды*/                       1
                /*длина имени файла в байтах*/          + 1 /* =1 байт*/
                /*имя файла в виде байт массива*/       + fName.length
                /*размер файла в виде байт массива*/    + fSizeInBytes.length /* =8 байт*/
                /*содержимое файла в виде байт массива*/+ fData.length)];
        outData[0] = UPLOAD_COMMAND;
        outData[1] = (byte) "test.txt".length();
        System.arraycopy(fName, 0, outData, 2, fName.length);
        System.arraycopy(fSizeInBytes, 0, outData, fName.length + 2, fName.length);

        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(fileToUpload))) {
            bufferedInputStream.read(fData,0,fData.length);
            System.arraycopy(fData, 0, outData, fName.length + fSizeInBytes.length + 2, fData.length);

            System.out.println("Sending " + filePath + " (" + outData.length + " bytes)...");
            dataOutputStream.write(outData,0,outData.length);
            dataOutputStream.flush();
            System.out.println("Done...");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }
    public long bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.put(bytes);
        buffer.flip();//need flip
        return buffer.getLong();
    }

    public void uploadFile(ActionEvent actionEvent) {
        uploadFile(fileToUploadPath, fileToUploadName);
    }
}
