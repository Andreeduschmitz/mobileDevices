package org.server;

import org.enums.CommandEnum;
import org.handlers.FileHandler;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

public class ConnectionThread extends Thread {
    private Server server;
    private Socket socket;
    private String clientName;
    private InputStream inputStream;
    private OutputStream outputStream;
    private PrintWriter writer;

    public ConnectionThread(Socket socket, Server server) {
        this.server = server;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            this.inputStream = this.socket.getInputStream();
            this.outputStream = this.socket.getOutputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(this.inputStream));
            this.writer = new PrintWriter(this.outputStream, true);

            this.clientName = reader.readLine();
            this.server.addClient(this.clientName, this);

            boolean continueReading = true;

            while (continueReading) {
                String clientMessage = reader.readLine();
                String[] messageParts = clientMessage.split(" ");
                CommandEnum command;

                try {
                    command = CommandEnum.getValue(messageParts[0]);
                } catch (IllegalArgumentException e) {
                    this.writer.println("Comando inválido.");
                    continue;
                }

                switch (command) {
                    case USERS:
                        this.listClients();
                        break;

                    case SEND_MESSAGE:
                        this.sendMessage(messageParts);
                        break;

                    case SEND_FILE:
                        if (messageParts.length < 3) {
                            this.writer.println("Uso: SEND_FILE <destinatario> <caminho>");
                            break;
                        }
                        String receiverName = messageParts[1];
                        String filePath = messageParts[2];

                        this.receiveFile(Server.serverFilesDirectory);
                        this.sendMessage(new String[]{ "", receiverName, CommandEnum.SEND_FILE.toString() });
                        this.sendFile(filePath, receiverName);
                        break;

                    case OUT:
                        continueReading = false;
                        this.finishConnection();
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println("Erro no cliente: " + e.getMessage());
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
        ConnectionThread connectionThread = this.getReceiverConnectionThread(receiverName);

        if (connectionThread == null) {
            return;
        }

        String message = String.join(" ", Arrays.copyOfRange(messageParts, 2, messageParts.length));
        connectionThread.getWriter().println("[" + clientName + "]: " + message);
    }

    private void receiveFile(String filePath) {
        try {
            FileHandler.receiveFile(this.inputStream, filePath);
        } catch (IOException e) {
            System.out.println("Erro ao receber o arquivo do cliente: " + e.getMessage());
        }
    }

    private void sendFile(String filePath, String receiverName) {
        ConnectionThread connectionThread = this.getReceiverConnectionThread(receiverName);

        if (connectionThread != null) {
            try {
                FileHandler.sendFile(connectionThread.outputStream, filePath);
            } catch (IOException e) {
                System.out.println("Erro ao enviar o arquivo para o cliente: " + e.getMessage());
            }
        }
    }

    private ConnectionThread getReceiverConnectionThread(String receiverName) {
        if (!this.server.getClients().contains(receiverName)) {
            this.writer.println("Cliente " + receiverName + " não está conectado.");
            return null;
        }

        return this.server.getConnectionThread(receiverName);
    }

    public PrintWriter getWriter() {
        return this.writer;
    }

    public OutputStream getOutputStream() {
        return this.outputStream;
    }

    public Socket getSocket() {
        return this.socket;
    }

    private void finishConnection() {
        this.server.removeClient(this.clientName);
    }
}