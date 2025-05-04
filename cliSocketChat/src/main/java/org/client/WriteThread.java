package org.client;

import org.apache.commons.lang3.StringUtils;
import org.enums.CommandEnum;
import org.handlers.FileHandler;

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

            if (StringUtils.isBlank(message)) {
                System.out.println("Comando mal formado. Tente -> <comando> <destinatário> <conteudo>");
                continue;
            }

            try {
                command = CommandEnum.getValue(messageParts[0]);
            } catch (IllegalArgumentException e) {
                System.out.println("Comando inválido. Os comandos válidos são: " + String.join(" - ", Arrays.toString(CommandEnum.values())));
                continue;
            }

            if (command == CommandEnum.SEND_FILE) {
                this.sendFile(messageParts[2], messageParts[1]);
            } else {
                this.writer.println(message);
            }

            if (CommandEnum.OUT.equals(command)) break;
        }
    }

    private void sendFile(String filePath, String clientName) {
        File file = new File(filePath);

        if (!file.exists() || !file.isFile()) {
            System.out.println("O arquivo " + filePath + " não existe ou não é um arquivo válido.");
            return;
        }

        // Envia o comando para o servidor se preparar para o recebimento do arquivo
        this.writer.println(CommandEnum.SEND_FILE + " " + clientName + " " + file.getName());

        try {
            Thread.sleep(100);
            FileHandler.sendFile(output, filePath);
        } catch (Exception e) {
            System.out.println("Erro ao enviar o arquivo: " + e.getMessage());
            return;
        }

        System.out.println("Arquivo enviado com sucesso.");
    }

    public void stopWriteThread() {
        this.stop = true;
        System.out.println("Conexão finalizada.");
    }
}
