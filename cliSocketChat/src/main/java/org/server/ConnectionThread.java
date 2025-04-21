package org.server;

import org.enums.CommandEnum;

import java.io.*;
import java.net.Socket;

public class ConnectionThread extends Thread {
    Server server;
    Socket socket;
    String clientName;
    private PrintWriter writer;

    public ConnectionThread(Socket socket, Server server) {
        this.server = server;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            InputStream input = this.socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            OutputStream output = this.socket.getOutputStream();
            this.writer = new PrintWriter(output, true);

            this.clientName = reader.readLine();
            this.server.addClient(this.clientName, this);

            boolean continueReading = true;

            while (continueReading) {
                String clientMessage = reader.readLine();
                String[] messageParts = clientMessage.split(" ");
                CommandEnum command = CommandEnum.valueOf(messageParts[0]);

                switch (command) {
                    case USERS:
                        this.listClients();
                    case SEND_MESSAGE:
                        this.sendMessage(messageParts);
                    case SEND_FILE:
                        this.sendFile(messageParts);
                    case OUT:
                        continueReading = false;
                        this.finishConnection();
                }
            }

        } catch (Exception e) {
            System.out.println("Erro ao ler mensagem do cliente: " + e.getMessage());
        }
    }

    private void listClients() {
        String usersList = String.join("/n", this.server.getConnections());
        writer.println("Usuários conectados:/n " + usersList);
    }

    private void sendMessage(String[] messageParts) {
        String receiverName = messageParts[1];
        String message = messageParts[2];

        if (this.server.getConnections().contains(receiverName)) {
            this.writer.println("Cliente " + receiverName + " não está conectado.");
            return;
        }

        ConnectionThread connectionThread = this.server.getConnectionThread(receiverName);
        connectionThread.getWriter().println(message);
    }

    private void sendFile(String[] messageParts) {

    }

    private void finishConnection() {
        this.server.removeClient(this.clientName);
    }

    public PrintWriter getWriter() {
        return writer;
    }
}