package org.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class ReadThread extends Thread {
    private BufferedReader reader;
    private Socket socket;

    public ReadThread(Socket socket) {
        this.socket = socket;

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
                }

                System.out.println(response);
            } catch (Exception e) {
                System.out.println("Conexão fechada.");
                this.close();
                break;
            }
        }
    }

    private void close() {
        try {
            this.reader.close();
            this.socket.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}