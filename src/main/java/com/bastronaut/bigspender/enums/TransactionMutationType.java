package com.bastronaut.bigspender.enums;

import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;


/**
 * May be specific to ING? All taken from ING export
 */
public enum TransactionMutationType {
    ONLINEBANKIEREN("Online bankieren"),
    BETAALAUTOMAAT("Betaalautomaat"),
    DIVERSEN("Diversen"),
    INCASSO("Incasso"),
    OVERSCHRIJVING("Overschrijving"),
    UNKNOWN("UNKNOWN");

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


    // TODO not ideal, think about how to deal with null safety
    public static String getByTransactionMutationType(@Nullable final TransactionMutationType transactionMutationType) {
        if (transactionMutationType != null) {
            return transactionMutationType.getType();
        }
        return StringUtils.EMPTY;
    }

}
