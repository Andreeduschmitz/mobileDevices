package org.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    Socket socket;
    Scanner scanner;
    PrintStream clientOut;

    public Client(String serverIp, int serverPort) throws IOException {
        this.socket = new Socket(serverIp, serverPort);;
        this.scanner = new Scanner(System.in);
        this.clientOut = new PrintStream(socket.getOutputStream());
    }

    public Client(Socket socket, String serverIp, int serverPort) throws IOException {
        this.socket = socket;
        this.scanner = new Scanner(System.in);
        this.clientOut = new PrintStream(socket.getOutputStream());
    }

    public InputStream getMessage() throws IOException {
        return socket.getInputStream();
    }

    public boolean isOnline() {
        return scanner.hasNextLine();
    }

    public void sendMessage() {
        clientOut.println(scanner.nextLine());
    }

    public void close() throws IOException {
        socket.close();
        scanner.close();
        clientOut.close();
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public PrintStream getClientOut() {
        return clientOut;
    }

    public void setClientOut(PrintStream clientOut) {
        this.clientOut = clientOut;
    }
}