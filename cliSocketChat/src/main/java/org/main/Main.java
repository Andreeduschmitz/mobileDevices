package org.main;

import org.client.Client;
import org.connection.Connection;
import org.operation.OperationModeEnum;
import org.server.Server;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.println("Qual modo você deseja operar? (cliente/servidor)");
        String operationModeString = in.nextLine();

        boolean validOperaitonMode = false;
        OperationModeEnum operationModeEnum = null;
        while (!validOperaitonMode) {
            try {
                operationModeEnum = OperationModeEnum.getValue(operationModeString);
                validOperaitonMode = true;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage() + " tente valores válidos. (cliente/servidor)");
            }
        }

        System.out.println("Insira a porta do " + operationModeEnum.toString() + ":");
        int port = in.nextInt();

        Closeable socket = null;
        switch (operationModeEnum) {
            case CLIENT:
                Main.runOnClientMode(port);
            case SERVER:
                Main.runOnServerMode(port);
        }
    }

    public static void runOnServerMode (int port) {
        Server server = null;
        try {
            server = new Server(port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        while (true) {
            Socket client = null;

            try {
                client = server.waitForConnection();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            System.out.println("Conexão estabelecida com cliente de ip: " + client.getInetAddress().getHostAddress());
            new Connection(client).start();
        }
    }

    // TODO externalizar o cliente e suas tratativas
    public static void runOnClientMode (int port) {
        Scanner in = new Scanner(System.in);
        System.out.println("Insira o ip do servidor:");
        String serverIp = in.nextLine();

        Client client = null;
        try {
            client = new Client(serverIp, port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}