package org.connection;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Connection extends Thread {
    private Map<String, String> connectionsMap = new HashMap<>();
    private Socket socket;

    public Connection(final Socket socket) {
        this.socket = socket;
    }

    public void run() {
        Scanner saida = null;

        try {
            saida = new Scanner(socket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        while (saida.hasNextLine()) {
            System.out.println(saida.nextLine());
        }
    }
}