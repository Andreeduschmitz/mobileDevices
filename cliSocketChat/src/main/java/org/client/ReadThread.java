package org.client;

import org.enums.CommandEnum;
import org.handlers.FileHandler;

import java.io.*;
import java.net.Socket;

public class ReadThread extends Thread {
    private BufferedReader reader;
    private Socket socket;
    private WriteThread writeThread;
    private InputStream inputStream;

    public ReadThread(Socket socket, WriteThread writeThread) {
        this.socket = socket;
        this.writeThread = writeThread;

        try {
            this.inputStream = socket.getInputStream();
            this.reader = new BufferedReader(new InputStreamReader(this.inputStream));
        } catch (Exception e) {
            System.out.println("Erro ao realizar leitura: " + e.getMessage());
        }
    }

    public void run() {
        while (true) {
            try {
                String response = this.reader.readLine();

                 if (response == null) {
                    this.close();
                    break;
                }

                if (response.startsWith(CommandEnum.SEND_FILE.toString())) {
                    String[] messageParts = response.split(" ");
                    this.receiveFile(messageParts[1], messageParts[2]);
                    continue;
                }

                System.out.println(response);
            } catch (Exception e) {
                System.out.println("Conexão fechada.");
                this.close();
                break;
            }
        }
    }

    private void receiveFile(String senderName, String fileName) {
        try {
            FileHandler.receiveFile(this.inputStream, Client.CLIENT_FILES_DIRECTORY, fileName);
        } catch (IOException e) {
            System.out.println("Erro ao receber um arquivo de " + senderName + " " + e.getMessage());
            throw new RuntimeException(e);
        }

        System.out.println("["+ senderName + "] " + "Enviou o arquivo " + fileName);
    }

    private void close() {
        try {
            this.reader.close();
            this.socket.close();
            this.writeThread.stopWriteThread();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}