package org.enums;

public enum CommandEnum {
    USERS("/users"),
    SEND_MESSAGE("/message"),
    SEND_FILE("/file"),
    OUT("/sair");

    private String command;

    private CommandEnum(String command) {
        this.command = command;
    }

    public static CommandEnum getValue(String command) throws IllegalArgumentException {
        for (CommandEnum commandEnum : CommandEnum.values()) {
            if(commandEnum.command.equals(command)){
                return commandEnum;
            }
        }

        throw new IllegalArgumentException("Comando não existente.");
    }

    @Override
    public String toString() {
        return this.command;
    }
}
