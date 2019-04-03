package com.bastronaut.bigspender.models;

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
}
