package org.handlers;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileHandler {
    public static void sendFile(OutputStream outputStream, String filePath) throws IOException {
        File file = new File(filePath);
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        FileInputStream fileInputStream = new FileInputStream(file);

        dataOutputStream.writeLong(file.length());

        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
            dataOutputStream.write(buffer, 0, bytesRead);
        }

        dataOutputStream.flush();
        fileInputStream.close();
    }

    public static void receiveFile(InputStream inputStream, String filePath, String fileName) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(inputStream);

        long fileSize = dataInputStream.readLong();
        byte[] buffer = new byte[4096];
        int bytesRead;
        long totalBytesRead = 0;

        Path path = Paths.get(filePath);
        if (Files.notExists(path)) {
            Files.createDirectories(path);
        }

        Path newFilepath = path.resolve(fileName);

        try (FileOutputStream fileOutputStream = new FileOutputStream(newFilepath.toFile())) {

            while (totalBytesRead < fileSize && (bytesRead = dataInputStream.read(buffer, 0, (int)Math.min(buffer.length, fileSize - totalBytesRead))) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
            }
        }
    }
}