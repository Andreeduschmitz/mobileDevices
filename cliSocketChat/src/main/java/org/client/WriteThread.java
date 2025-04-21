package org.client;

import org.enums.CommandEnum;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

public class WriteThread extends Thread {
    private Socket socket;
    private PrintWriter writer;

    public WriteThread(Socket socket) {
        this.socket = socket;

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
        this.writer.println(clientName);

        while (scanner.hasNextLine()) {
            String message = scanner.nextLine();
            String[] messageParts = message.split(" ");

            CommandEnum command = null;

            try {
                command = CommandEnum.getValue(messageParts[0]);
            } catch (IllegalArgumentException e) {
                System.out.println("Comando inválido. Os comandos válidos são: " + String.join(" - ", Arrays.toString(CommandEnum.values())));
            }

            this.writer.println(message);

            if (CommandEnum.OUT.equals(command)) break;
        }

        try {
            this.socket.close();
            this.writer.close();
        } catch (IOException e) {
            System.out.println("Erro ao fechar a conexão: " + e.getMessage());
        }
    }
}
