package org.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    private String serverIp;
    private int serverPort;

    public Client(String serverIp, int serverPort) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
    }

    public void execute () {
        try {
            Socket socket = new Socket(this.serverIp, this.serverPort);

            new ReadThread(socket).start();
            new WriteThread(socket).start();
        } catch (UnknownHostException e) {
            System.out.println("Servidor não encontrado: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erro ao conectar com o servidor: " + e.getMessage());
        }
    }
}