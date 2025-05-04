package org.server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Server {
    private ServerSocket serverSocket;
    private final Map<String, ConnectionThread> clients = new HashMap<>();
    public static String serverFilesDirectory = "server_files_directory";

    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Servidor iniciado na porta: " + port);

        File logs = new File("logs.txt");

        if(!logs.exists()) logs.createNewFile();
    }

    public void execute() {
        try {
            while (true) {
                Socket client = this.waitForConnection();
                System.out.println("Conexão estabelecida com cliente de ip: " + client.getInetAddress().getHostAddress());
                this.writeIntoFile("[Connection]:" + client.getInetAddress().getHostAddress() + " - " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

                ConnectionThread connectionThread = new ConnectionThread(client, this);
                connectionThread.start();
            }
        } catch (IOException e) {
            System.out.println("Erro no servidor: " + e.getMessage());
        }
    }

    public Socket waitForConnection() throws IOException {
        return this.serverSocket.accept();
    }

    public Set<String> getClients() {
        return this.clients.keySet();
    }

    public ConnectionThread getConnectionThread(String clientName) {
        return this.clients.get(clientName);
    }

    public void addClient(String clientName, ConnectionThread connectionThread) {
        synchronized (this.clients) {
            this.clients.put(clientName, connectionThread);
        }
    }

    private void writeIntoFile(String logMessage) {
        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter("logs.txt", true);
            fileWriter.write(logMessage + "\n");
            fileWriter.close();
        } catch (IOException e) {
            System.out.println("Erro ao salvar o log: " + e.getMessage());
        }
    }

    public void removeClient(String clientName) {
        ConnectionThread connectionThread;

        synchronized (this.clients) {
            connectionThread = this.clients.remove(clientName);
        }

        if (connectionThread != null) {
            System.out.println("Cliente " + clientName + " desconectou.");
            this.writeIntoFile("[Disconnect]:" + connectionThread.getSocket().getInetAddress().getHostAddress() + " - " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
    }
}