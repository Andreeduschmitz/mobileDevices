package org.operation;

public enum OperationModeEnum {
    CLIENT("cliente"),
    SERVER("servidor"),;

    private String mode;

    OperationModeEnum(String mode) {
        this.mode = mode;
    }

    public static OperationModeEnum getValue(String mode) throws IllegalArgumentException {
        for (OperationModeEnum operationModeEnum : OperationModeEnum.values()) {
            if (operationModeEnum.mode.equals(mode)) {
                return operationModeEnum;
            }
        }

        throw new IllegalArgumentException("Modo de operação não existente.");
    }
}
