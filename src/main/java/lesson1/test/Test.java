package lesson1.test;

import java.io.*;
import java.nio.ByteBuffer;

import static lesson1.constants.Constants.UPLOAD_COMMAND;

public class Test {
    public static void main(String[] args) {
        String fOutPath = "src/main/java/lesson1";
                //"src/main/java/lesson1/client";
        String fInPath = "src/main/java/lesson1/test";
        String fileName = "Doctor.Strange.in.the.Multiverse.of.Madness.2022.2160p.WEB-DL.10bit.HDR.x265.mkv";
               // "test.txt";

        /*
        отправка файла
         */

        File fOut = new File(fOutPath,fileName);
        long l = fOut.length();
        byte[] fOutName = fileName.getBytes();
        byte[] fOutSize = longToBytes(fOut.length());
        byte[] fOutData = new byte[(int) l];
        byte[] outData = new byte[(int) (
                /*длина команды*/                       1
                /*длина имени файла в байтах*/          + 1 /* =1 байт*/
                /*имя файла в виде байт массива*/       + fOutName.length
                /*размер файла в виде байт массива*/    + fOutSize.length /* =8 байт*/
                /*содержимое файла в виде байт массива*/+ fOutData.length)];

        outData[0] = UPLOAD_COMMAND;
        System.out.println("команда " + UPLOAD_COMMAND);

        outData[1] = (byte) fileName.length();
        System.out.println("длина имени файла в байтах " + fileName.length());

        System.out.print("имя файла в виде байт массива ");
        for (int i = 0; i < fOutName.length; i++) {
            System.out.print(fOutName[i] + " ");
        }
        System.out.println();

        System.out.print("команда + длина имени файла в байтах + имя файла в виде байт массива ");
        System.arraycopy(fOutName, 0, outData, 2, fOutName.length);
        for (int i = 0; i < outData.length; i++) {
            System.out.print(outData[i] + " ");
        }
        System.out.println();

        System.out.print("размер файла в виде байт массива ");
        for (int i = 0; i < fOutSize.length; i++) {
            System.out.print(fOutSize[i] + " ");
        }
        System.out.println();

        System.out.print("команда + длина имени файла + имя файла в байтах + массив размера файла ");
        System.arraycopy(fOutSize, 0, outData, fOutName.length + 2, fOutSize.length);
        for (int i = 0; i < outData.length; i++) {
            System.out.print(outData[i] + " ");
        }
        System.out.println();

        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(fOut))) {
            bufferedInputStream.read(fOutData, 0, fOutData.length);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.print("содержимое файла в виде байт массива ");
        for (int i = 0; i < fOutData.length; i++) {
            System.out.print(fOutData[i] + " ");
        }
        System.out.println();

        System.out.print("команда + длина имени файла + имя файла в байтах + массив размера файла + содержимое файла в виде байт массива ");
        System.arraycopy(fOutData, 0, outData, fOutName.length + fOutSize.length + 2, fOutData.length);
        for (int i = 0; i < outData.length; i++) {
            System.out.print(outData[i] + " ");
        }
        System.out.println();

        /*
        Получение файла
         */

        byte[] inData = outData;

        byte[] fInName = new byte[inData[1]];
        for (int i = 0; i < inData[1]; i++) {
            fInName[i] = inData[i + 2];
        }

        File fIn = new File(fInPath, new String(fInName));
        if (!fIn.exists()) {
            try {
                fIn.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            fIn.delete();
        }

        byte[] fInSize = new byte[8];
        for (int i = 0; i < fInSize.length; i++) {
            fInSize[i] = inData[i + 2 + fileName.length()];
        }

        try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(fIn))) {
            long fInSizeLong = bytesToLong(fInSize);
            if (fInSizeLong > Integer.MAX_VALUE) {
                int cycleCount = (int) (fInSizeLong / Integer.MAX_VALUE);
                int remnant = (int) (fInSizeLong % Integer.MAX_VALUE);
                for (int i = 0; i < cycleCount; i++) {
                    byte[] receivedData = new byte[Integer.MAX_VALUE];
                    for (int j = 0; j < receivedData.length; i++) {
                        receivedData[j] = inData[j + 2 + fInName.length + fInSize.length + (Integer.MAX_VALUE * i)];
                    }
                    bufferedOutputStream.write(receivedData);
                }
                byte[] receivedData = new byte[remnant];
                for (int i = 0; i < remnant; i++) {
                    receivedData[i] = inData[i + 2 + fInName.length + fInSize.length];
                }
                bufferedOutputStream.write(receivedData);
            } else {
                byte[] receivedData = new byte[(int) fInSizeLong];
                for (int i = 0; i < receivedData.length; i++) {
                    receivedData[i] = inData[i + 2 + fInName.length + fInSize.length];
                }
                bufferedOutputStream.write(receivedData);
            }
//            int count;
//            while ((count = dataInputStream.read(receivedData)) > 0) {
//                bufferedOutputStream.write(receivedData, 0, count);
//            }
            bufferedOutputStream.flush();
            System.out.println("File uploded to server...");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }
    public static long bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.put(bytes);
        buffer.flip();//need flip
        return buffer.getLong();
    }
}
