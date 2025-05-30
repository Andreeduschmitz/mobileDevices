package org.main;

import org.client.Client;
import org.enums.OperationModeEnum;
import org.server.Server;

import java.io.IOException;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner input = new Scanner(System.in);

        boolean validOperaitonMode = false;
        OperationModeEnum operationModeEnum = null;
        String operationModeString = null;

        while (!validOperaitonMode) {
            try {
                System.out.println("Qual modo você deseja operar? " + String.join(" - ", Arrays.toString(OperationModeEnum.values())));
                operationModeString = input.nextLine();
                operationModeEnum = OperationModeEnum.getValue(operationModeString);
                validOperaitonMode = true;
            } catch (IllegalArgumentException e) {
                System.out.println(operationModeString + " é um modo inválido, insira modos válidos. " + String.join(" - ", Arrays.toString(OperationModeEnum.values())));
            }
        }

        System.out.println("Insira a porta do servidor:");
        int port = 0;
        boolean validPort = false;

        while (!validPort) {
            try {
                port = input.nextInt();
                validPort = true;
            } catch (InputMismatchException e) {
                System.out.println("Porta inválida. Digite uma porta válida:");
                input.nextLine();
            }
        }

        switch (operationModeEnum) {
            case CLIENT:
                System.out.println("Insira o ip do servidor:");
                String serverIp = null;
                boolean validServerIp = false;

                while (!validServerIp) {
                    try {
                        serverIp = input.next();
                        validServerIp = true;
                    } catch (InputMismatchException e) {
                        System.out.println("Ip inválido. Digite um ip válido:");
                    }
                }

                new Client(serverIp, port).execute();
                break;
            case SERVER:
                new Server(port).execute();
                break;
            default:
                throw new RuntimeException(new IllegalArgumentException("Modo de operação inválido."));
        }
    }
}