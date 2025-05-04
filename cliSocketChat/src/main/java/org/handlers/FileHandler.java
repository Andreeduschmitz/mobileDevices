package org.handlers;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileHandler {
    public static String FILE_TRANSFER_MARKER = "FILE_TRANSFER";

    public static boolean sendFile(OutputStream outputStream, String filePath) throws IOException {
        File file = new File(filePath);

        if (!file.exists() || !file.isFile()) {
            System.out.println("O arquivo " + filePath + " não existe ou não é um arquivo válido.");
            return false;
        }

        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        FileInputStream fileInputStream = new FileInputStream(file);

        dataOutputStream.writeUTF(FileHandler.FILE_TRANSFER_MARKER);
        dataOutputStream.writeUTF(file.getName());
        dataOutputStream.writeLong(file.length());

        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
            dataOutputStream.write(buffer, 0, bytesRead);
        }

        dataOutputStream.flush();
        fileInputStream.close();
        return true;
    }

    public static String receiveFile(InputStream inputStream, String filePath) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(inputStream);

        String marker = dataInputStream.readUTF();
        if (!FileHandler.FILE_TRANSFER_MARKER.equals(marker)) {
            throw new IOException("Marcador de início de arquivo inválido: " + marker);
        }

        String fileName = dataInputStream.readUTF();
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
            while (totalBytesRead < fileSize &&
                    (bytesRead = dataInputStream.read(buffer, 0, (int)Math.min(buffer.length, fileSize - totalBytesRead))) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
            }
        }

        return fileName;
    }
}
