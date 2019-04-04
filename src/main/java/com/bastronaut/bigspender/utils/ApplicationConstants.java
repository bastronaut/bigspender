package com.bastronaut.bigspender.utils;

public class ApplicationConstants {

    /** API endpoints **/
    public static final String TRANSACTION_IMPORT_POST = "/import/{userid}/transactions";

    /** Constants for csv import files **/
    public static final int EXPECTED_NR_COLUMNS_ING = 9;
    public static final int DATE_COLUMN = 0;
    public static final int NAME_COLUMN = 1;
    public static final int ACCT_COLUMN = 2;
    public static final int RECEIVINGACCTNR_COLUMN = 3;
    public static final int MUTATIONCODE_COLUMN = 4;
    public static final int TRANSACTIONTYPE_COLUMN = 5;
    public static final int AMOUNT_COLUMN = 6;
    public static final int MUTATIONTYPE_COLUMN = 7;
    public static final int STATEMENT_COLUMN = 8;

    /** Regex strings **/
    public static final String CSV_COMMA_SPLIT_PATTERN = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";  // // See: https://stackoverflow.com/questions/18893390/splitting-on-comma-outside-quotes
    public static final String HH_MM_SS_TIMEPATTERN = "(([0-1]?[0-9])|(2[0-3])):[0-5][0-9]:[0-5][0-9]"; // HH:MM:SS
    public static final String HH_MM_TIMEPATTERN = "([01]?[0-9]|2[0-3]):[0-5][0-9]";

}
