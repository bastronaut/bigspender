package com.bastronaut.bigspender.enums;


import com.bastronaut.bigspender.models.Transaction;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;

public enum TransactionCode {
    GT("GT"),
    BA("BA"),
    UNKNOWN("UNKNOWN");

    final String type;

    TransactionCode(final String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static TransactionCode getByValue(final String code) {
        for (TransactionCode transactionCode: TransactionCode.values()) {
            if (StringUtils.equalsIgnoreCase(transactionCode.getType(), code)) {
                return transactionCode;
            }
        }
        return null;
    }

    // TODO not ideal, think about how to deal with null safety
    public static String getByTransactionCode(@Nullable final TransactionCode transactionCode) {
        if (transactionCode != null) {
            return transactionCode.getType();
        }
        return StringUtils.EMPTY;
    }

}
