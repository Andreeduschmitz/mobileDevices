package org.server;

import org.apache.commons.lang3.StringUtils;
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

                if(StringUtils.isEmpty(clientMessage)) {
                    this.writer.println("Mensagem mal formada. Tente -> <comando> <destinatário> <conteudo>");
                    break;
                }

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
                            this.writer.println("Comando de envio de arquivos mal formado. Tente -> <comando> <destinatário> <conteudo>");
                            break;
                        }

                        String receiverName = messageParts[1];
                        String fileName = messageParts[2];
                        this.receiveFile(Server.SERVER_TEMP_FILES_DIRECTORY, fileName);
                        Thread.sleep(100);

                        ConnectionThread connectionThread = this.getReceiverConnectionThread(receiverName);
                        if (connectionThread == null) {
                            break;
                        }

                        // Envia o comando para o destinatário se preparar para o recebimento do arquivo
                        String message = CommandEnum.SEND_FILE + " " + this.clientName + " " + fileName;
                        connectionThread.getWriter().println(message);
                        Thread.sleep(100);

                        this.sendFile(Server.SERVER_TEMP_FILES_DIRECTORY + "/" + fileName, receiverName);
                        Thread.sleep(100);

                        File file = new File(Server.SERVER_TEMP_FILES_DIRECTORY + "/" + fileName);
                        if (file.exists() && file.isFile()) {
                            file.delete();
                        }

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

    private String receiveFile(String filePath, String fileName) throws IOException {
        try {
            FileHandler.receiveFile(this.inputStream, filePath, fileName);
        } catch (IOException e) {
            System.out.println("Erro ao receber o arquivo do cliente: " + e.getMessage());
            return null;
        }

        return fileName;
    }

    private void sendFile(String filePath, String receiverName) {
        ConnectionThread connectionThread = this.getReceiverConnectionThread(receiverName);

        if (connectionThread == null) {
            return;
        }

        try {
            FileHandler.sendFile(connectionThread.outputStream, filePath);
        } catch (IOException e) {
            System.out.println("Erro ao enviar o arquivo para o cliente: " + e.getMessage());
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
        try {
            this.socket.close();
            this.outputStream.close();
            this.inputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}