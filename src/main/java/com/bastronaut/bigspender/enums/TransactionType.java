package com.bastronaut.bigspender.enums;

import org.apache.commons.lang3.StringUtils;

public enum TransactionType {
    BIJ("Bij"),
    AF("Af");

    public String getType() {
        return type;
    }

    final String type;

    TransactionType(String type) {
        this.type = type;
    }

    public static TransactionType getByType(String type) {
        for (TransactionType transactionType: TransactionType.values()) {
            if (StringUtils.equalsIgnoreCase(transactionType.getType(), type)) {
                return transactionType;
            }
        }
        return null;
    }
}
