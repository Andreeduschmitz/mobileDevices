package org.client;

import org.enums.CommandEnum;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class WriteThread extends Thread {
    private Socket socket;
    private Client client;
    private PrintWriter writer;

    public WriteThread(Socket socket, Client cliente) {
        this.socket = socket;
        this.client = cliente;

        try {
            OutputStream output = socket.getOutputStream();
            this.writer = new PrintWriter(output, true);

        } catch (IOException e) {
            System.out.println("Erro na conexão: " + e.getMessage());
        }
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Digite seu nome de usuário: ");
        String clientName = scanner.nextLine();
        this.client.setClientName(clientName);
        this.writer.println(clientName);

        while (scanner.hasNextLine()) {
            String message = scanner.nextLine();
            String[] messageParts = message.split(" ");

            CommandEnum command = null;

            try {
                command = CommandEnum.valueOf(messageParts[0]);
            } catch (IllegalArgumentException e) {
                System.out.println("Comando inválido. (/users - /message - /file - /out)");
            }

            this.writer.println(message);

            if (CommandEnum.OUT.equals(command)) break;
        }

        try {
            this.socket.close();
        } catch (IOException e) {
            System.out.println("Erro ao fechar a conexão: " + e.getMessage());
        }
    }
}
