package org.server;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server extends ServerSocket {
    public Server(int port) throws IOException {
        super(port);
        System.out.println("Servidor iniciado na porta: " + port);
    }

    public Socket waitForConnection() throws IOException {
        return super.accept();
    }

    public boolean connectionEstablished(Socket client) throws IOException {
        Scanner clientIn = new Scanner(client.getInputStream());
        return clientIn.hasNextLine();
    }

    public String getMessage(Socket client) throws IOException {
        Scanner clientIn = new Scanner(client.getInputStream());
        return clientIn.nextLine();
    }

    public void sendMessage(Socket client, String message) throws IOException {
        PrintStream clientOut = new PrintStream(client.getOutputStream());
        clientOut.println(message);
        clientOut.flush();
    }

    public void closeServer () throws IOException {
        super.close();
    }
}
