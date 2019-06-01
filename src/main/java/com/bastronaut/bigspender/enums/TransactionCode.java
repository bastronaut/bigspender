package com.bastronaut.bigspender.enums;


import org.apache.commons.lang3.StringUtils;

public enum TransactionCode {
    GT("GT"),
    BA("BA");

    final String type;

    TransactionCode(final String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static TransactionCode getByValue(String code) {
        for (TransactionCode transactionCode: TransactionCode.values()) {
            if (StringUtils.equalsIgnoreCase(transactionCode.getType(), code)) {
                return transactionCode;
            }
        }
        return null;
    }
}
