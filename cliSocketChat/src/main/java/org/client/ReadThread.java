package org.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class ReadThread extends Thread {
    private BufferedReader reader;
    private Socket socket;
    private WriteThread writeThread;

    public ReadThread(Socket socket, WriteThread writeThread) {
        this.socket = socket;
        this.writeThread = writeThread;

        try {
            InputStream input = socket.getInputStream();
            this.reader = new BufferedReader(new InputStreamReader(input));
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

                System.out.println(response);
            } catch (Exception e) {
                System.out.println("Conexão fechada.");
                this.close();
                break;
            }
        }
    }

    // TODO implementar o recebimento do arquivo enviado para o destinatário
    private void receiveFile() {

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