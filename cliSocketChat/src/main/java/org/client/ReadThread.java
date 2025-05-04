package org.client;

import org.enums.CommandEnum;
import org.handlers.FileHandler;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

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

                if (response.endsWith(CommandEnum.SEND_FILE.toString())) {
                    String[] messageParts = response.split(" ");
                    this.receiveFile(messageParts[0]);
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

    private void receiveFile(String senderName) {
        try {
            FileHandler.receiveFile(this.inputStream, Client.clientFilesDirectory);
        } catch (IOException e) {
            System.out.println("Erro ao receber um arquivo de " + senderName + ": " + e.getMessage());
            return;
        }

        System.out.println( senderName + "(enviou um arquivo)");
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