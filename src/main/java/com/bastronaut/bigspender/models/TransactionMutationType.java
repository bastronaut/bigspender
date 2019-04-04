package com.bastronaut.bigspender.models;

import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;


public enum TransactionMutationType {
    ONLINEBANKIEREN("Online bankieren"),
    BETAALAUTOMAAT("Betaalautomaat"),
    DIVERSEN("Diversen");

    final String type;

    private TransactionMutationType(final String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    /**
     * Returns the enum by its string
     * @param type the transaction type according to the transaction import row
     * @return enum matching with the string or null if no matching enum exists
     */
    public static TransactionMutationType getByValue(final String type) {
        for (TransactionMutationType mutationType: TransactionMutationType.values()) {
            if (StringUtils.equalsIgnoreCase(mutationType.getType(), type)) {
                return mutationType;
            }
        }
        return null;
    }
}
