package org.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {
    private ServerSocket serverSocket;
    private Map<String, ConnectionThread> clients = new HashMap<>();

    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Servidor iniciado na porta: " + port);
    }

    public Socket waitForConnection() throws IOException {
        return this.serverSocket.accept();
    }

    public void execute() {
        try {
            while (true) {
                Socket client = this.waitForConnection();
                System.out.println("Conexão estabelecida com cliente de ip: " + client.getInetAddress().getHostAddress());

                ConnectionThread connectionThread = new ConnectionThread(client, this);
                connectionThread.start();
            }
        } catch (IOException e) {
            System.out.println("Erro ao tentar conectar com um cliente: " + e.getMessage());
        }
    }

    public Set<String> getConnections() {
        return this.clients.keySet();
    }

    public ConnectionThread getConnectionThread(String clientName) {
        return this.clients.get(clientName);
    }

    public void addClient(String clientName, ConnectionThread connectionThread) {
        this.clients.put(clientName, connectionThread);
    }

    public void removeClient(String clientName) {
        ConnectionThread connectionThread = this.clients.remove(clientName);

        if (connectionThread != null) {
            System.out.println("Cliente: " + clientName + " realizou logout.");
        }
    }
}