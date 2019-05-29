package com.bastronaut.bigspender.utils;

public class ApplicationConstants {

    /** API endpoints **/
    public static final String TRANSACTION_IMPORT_ENDPOINT = "/users/{userid}/transactionimport";
    public static final String TRANSACTIONS_ENDPOINT = "/users/{userid}/transactions";
    public static final String TRANSACTION_ENDPOINT = "/users/{userid}/transactions/{transactionid}";
    public static final String USERS_ENDPOINT = "/users";
    public static final String USERS_UPDATE_ENDPOINT = "/users/{userid}";

    /** Parameters */
    public static final String TRANSACTIONID_PARAM = "transactionIds";
    public static final String TRANSACTIONID_SEPERATOR = "transactionIds";

    /** ING **/
    public static final String CSV_HEADER_DATE = "Datum";
    public static final String CSV_HEADER_NAME = "Naam / Omschrijving";
    public static final String CSV_HEADER_ACCOUNT = "Rekening";
    public static final String CSV_HEADER_RECEIVINGACCOUNT = "Tegenrekening";
    public static final String CSV_HEADER_CODE = "Code";
    public static final String CSV_HEADER_TYPE = "Af Bij";
    public static final String CSV_HEADER_AMOUNT = "Bedrag (EUR)";
    public static final String CSV_HEADER_MUTATIONTYPE = "MutatieSoort";
    public static final String CSV_HEADER_STATEMENT = "Mededelingen";
    public static final int EXPECTED_NR_COLUMNS_ING = 9;

    /** Regex strings **/
    public static final String CSV_COMMA_SPLIT_PATTERN = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";  // // See: https://stackoverflow.com/questions/18893390/splitting-on-comma-outside-quotes
    public static final String HH_MM_SS_TIMEPATTERN = " (([0-1]?[0-9])|(2[0-3])):[0-5][0-9]:[0-5][0-9] "; // HH:MM:SS
    public static final String HH_MM_TIMEPATTERN = " ([01]?[0-9]|2[0-3]):[0-5][0-9] "; // HH:MM


    /* */

}
