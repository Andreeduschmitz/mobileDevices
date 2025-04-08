package org.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class Client extends Socket {
    Scanner scanner;
    PrintStream clientOut;

    public Client(String serverIp, int serverPort) throws IOException {
        super(serverIp, serverPort);
        this.scanner = new Scanner(System.in);
        this.clientOut = new PrintStream(super.getOutputStream());
    }

    public InputStream getMessage() throws IOException {
        return super.getInputStream();
    }

    public boolean isOnline() {
        return scanner.hasNextLine();
    }

    public void sendMessage() {
        clientOut.println(scanner.nextLine());
    }

    public void close() throws IOException {
        super.close();
        scanner.close();
        clientOut.close();
    }

    public PrintStream getClientOut() {
        return clientOut;
    }

    public void setClientOut(PrintStream clientOut) {
        this.clientOut = clientOut;
    }
}