package org.client;

import org.enums.CommandEnum;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

public class WriteThread extends Thread {
    private PrintWriter writer;
    private OutputStream output;
    private volatile boolean stop = false;

    public WriteThread(Socket socket) {
        try {
            this.output = socket.getOutputStream();
            this.writer = new PrintWriter(this.output, true);
        } catch (IOException e) {
            System.out.println("Erro na conexão: " + e.getMessage());
        }
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Digite seu nome de usuário: ");
        String clientName = scanner.nextLine();
        this.writer.println(clientName);

        while (!this.stop && scanner.hasNextLine()) {
            String message = scanner.nextLine();
            String[] messageParts = message.split(" ");
            CommandEnum command = null;

            try {
                command = CommandEnum.getValue(messageParts[0]);
            } catch (IllegalArgumentException e) {
                System.out.println("Comando inválido. Os comandos válidos são: " + String.join(" - ", Arrays.toString(CommandEnum.values())));
            }

            if (command == CommandEnum.SEND_FILE) {
                this.sendFile(messageParts[1]);
            } else {
                this.writer.println(message);
            }

            if (CommandEnum.OUT.equals(command)) break;
        }
        System.out.println("Conexão finalizada.");
    }

    // TODO implementar a captura do arquivo e o envio do mesmo
    private void sendFile(String filePath) {

    }

    public void stopWriteThread() {
        this.stop = true;
        System.out.println("Conexão finalizada.");
    }
}
