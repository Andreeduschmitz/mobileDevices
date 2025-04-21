package org.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class ReadThread extends Thread {
    private BufferedReader reader;
    private Client client;

    public ReadThread(Socket socket, Client client) {
        this.client = client;

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
                System.out.println("\n" + response);
            } catch (IOException e) {
                System.out.println("Erro ao ler a mensagem: " + e.getMessage());
            }
        }
    }
}