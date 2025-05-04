package org.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    private String serverIp;
    private int serverPort;
    public static String clientFilesDirectory = "client_files_directory";

    public Client(String serverIp, int serverPort) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
    }

    public void execute () {
        try {
            Socket socket = new Socket(this.serverIp, this.serverPort);

            WriteThread writeThread = new WriteThread(socket);
            new ReadThread(socket, writeThread).start();
            writeThread.start();
        } catch (UnknownHostException e) {
            System.out.println("Servidor não encontrado: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erro ao conectar com o servidor: " + e.getMessage());
        }
    }
}