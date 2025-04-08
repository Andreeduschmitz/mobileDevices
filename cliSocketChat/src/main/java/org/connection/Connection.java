package org.connection;

import org.client.Client;
import org.server.Server;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Connection extends Thread {
    private Client client;
    private Server server;
    private Map<String, String> connectionsMap = new HashMap<>();

    public Connection(Client client) {
        this.client = client;
    }

    public void run() {
        Scanner clientIn = null;

        try {
            clientIn = new Scanner(this.client.getMessage(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}