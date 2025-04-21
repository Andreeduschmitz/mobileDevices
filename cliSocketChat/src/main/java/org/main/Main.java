package org.main;

import org.client.Client;
import org.server.ConnectionThread;
import org.enums.OperationModeEnum;
import org.server.Server;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner in = new Scanner(System.in);

        boolean validOperaitonMode = false;
        OperationModeEnum operationModeEnum = null;

        while (!validOperaitonMode) {
            try {
                System.out.println("Qual modo você deseja operar? (cliente/servidor)");
                String operationModeString = in.nextLine();
                operationModeEnum = OperationModeEnum.getValue(operationModeString);
                validOperaitonMode = true;
            } catch (IllegalArgumentException e) {
                System.out.println(operationModeEnum + " é um modo inválido, insira modos válidos. (cliente/servidor)");
            }
        }

        System.out.println("Insira a porta do servidor:");
        int port = in.nextInt();

        switch (operationModeEnum) {
            case CLIENT:
                System.out.println("Insira o ip do servidor:");
                String serverIp = in.next();
                new Client(serverIp, port).execute();
            case SERVER:
                new Server(port).execute();
            default:
                throw new RuntimeException(new IllegalArgumentException("Modo de operação inválido."));
        }
    }
}