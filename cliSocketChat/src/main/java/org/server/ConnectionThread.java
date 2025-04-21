package org.server;

import org.enums.CommandEnum;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

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
                CommandEnum command = CommandEnum.getValue(messageParts[0]);

                switch (command) {
                    case USERS:
                        this.listClients();
                        break;
                    case SEND_MESSAGE:
                        this.sendMessage(messageParts);
                        break;
                    case SEND_FILE:
                        this.sendFile(messageParts);
                        break;
                    case OUT:
                        continueReading = false;
                        this.finishConnection();
                        break;
                }
            }

        } catch (Exception e) {
            System.out.println("Erro no cliente.");
            this.finishConnection();
        }
    }

    private void listClients() {
        if (this.server.getClients().size() == 1) {
            this.writer.println("Nenhum cliente conectado.");
            return;
        }

        this.writer.println("Clientes conectados:");

        for(String client : this.server.getClients()) {
            if (!this.clientName.equals(client)) {
                this.writer.println(client);
            }
        }
    }

    private void sendMessage(String[] messageParts) {
        String receiverName = messageParts[1];
        String message = String.join(" ", Arrays.copyOfRange(messageParts, 2, messageParts.length));

        if (!this.server.getClients().contains(receiverName)) {
            this.writer.println("Cliente " + receiverName + " não está conectado.");
            this.listClients();
            return;
        }

        ConnectionThread connectionThread = this.server.getConnectionThread(receiverName);
        connectionThread.getWriter().println("[" + clientName + "]: " + message);
    }

    //TODO implementar envio de arquivos
    private void sendFile(String[] messageParts) {

    }

    private void finishConnection() {
        this.server.removeClient(this.clientName);
    }

    public PrintWriter getWriter() {
        return writer;
    }

    public Socket getSocket() {
        return this.socket;
    }
}