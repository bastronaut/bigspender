package com.bastronaut.bigspender.utils;

public class ApplicationConstants {

    /* API endpoints */
    public static final String TRANSACTION_IMPORT_ENDPOINT = "/transactionimport";
    public static final String TRANSACTIONS_ENDPOINT = "/transactions";
    public static final String TRANSACTION_ENDPOINT = "/transactions/{transactionid}";
    public static final String USERS_ENDPOINT = "/users";
    public static final String USER_RESOURCE_ENDPOINT = "/users/{userid}";
    public static final String LABELS_ENDPOINT = "/labels";
    public static final String LABELS_BY_TRANSACTION_ENDPOINT = "/transactions/{transactionid}/labels/";
    /* Weakness in API design, have to find a better way to allow operating on label <> transaction links */
    public static final String TRANSACTION_LABELS_ENDPOINT = "/transactions/labels"; // Add/Remove labels to transactions
    public static final String TRANSACTIONS_BY_LABEL_ENDPOINT = "/transactions/labels/{labelid}"; // Get transactions by label

    public static final String LOGIN_ENDPOINT = "/login";
    public static final String LOGOUT_ENDPOINT = "/logout";


    /* User constants */
    public static final int PASSWORDMINSIZE = 8;
    public static final int MAX_LOGIN_ATTEMPTS = 3;
    public static final int HOURS_ACCOUNT_LOCKED = 24;
    public static final int MINUTES_ACCOUNT_LOCKED = 60 * HOURS_ACCOUNT_LOCKED;

    /* ING */
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

    /* Regex strings */
    public static final String CSV_COMMA_SPLIT_PATTERN = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";  // // See: https://stackoverflow.com/questions/18893390/splitting-on-comma-outside-quotes
    public static final String HH_MM_SS_TIMEPATTERN = " (([0-1]?[0-9])|(2[0-3])):[0-5][0-9]:[0-5][0-9] "; // HH:MM:SS
    public static final String HH_MM_TIMEPATTERN = " ([01]?[0-9]|2[0-3]):[0-5][0-9] "; // HH:MM

    /* Webservice error messages */
    /* User error messages */
    public static final String ERRORMSG_PASSWORD_TOO_SHORT = "Invalid password, password length is too short";
    public static final String ERRORMSG_INVALID_EMAIL = "Invalid email address, not well-formed";
    public static final String ERRORMSG_USER_EXISTS = "User already exists: %s";
    public static final String ERRORMSG_USER_NOTFOUND = "User not found: %s";
    public static final String ERRORMSG_USER_EMAIL_NULL = "Field: email is required";
    public static final String ERRORMSG_USER_PW_NULL = "Field: password is required";
    public static final String ERRORMSG_USER_PW_SIZE = "Field: password requires a minimum length of 8";
    public static final String ERRORMSG_LABEL_NAME_EMPTY = "Field: name is required for label";
    public static final String ERRORMSG_LABEL_EMPTY = "Field: label is required";
    public static final String ERRORMSG_LABEL_REMOVE_EMPTY = "Field: labels must not be empty - requires label ids";
    public static final String ERRORMSG_LABEL_REMOVE_ID_EMPTY = "Field: id must not be empty for label";
    public static final String INVALID_UPDATE_INFORMATION = "No updateable information provided";
    public static final String ERRORMSG_LINKS_EMPTY = "Field: links is required";
    public static final String ERRORMSG_TRANSACTIONID_EMPTY = "Field: transactionId is required";
    public static final String ERRORMSG_TOO_MANY_LOGIN_ATTEMPTS = "Too many login attempts. Login disabled for 24 hours";
    public static final String ERRORMSG_REGISTRATION_NOT_ALLOWED = "Registration not allowed while logged in";


    /* Transaction error messages */
    public static final String ERRORMSG_INVALID_TXID = "Invalid transaction id: %s";
    public static final String ERRORMSG_NONEXISTINGTX_FOR_USER = "Transaction with id %s for user %s does not exist";
    public static final String ERRORMSG_MISSING_TRANSACTION_IDS = "No transaction IDs to delete specified";

    public static final String ERRORMSG_NO_AMOUNT = "Field: amount is required";
    public static final String ERRORMSG_NO_TRANSACTION_TYPE = "Field: type is required";

    /* Transaction import error messages */
    public static final String ERRORMSG_NOFILE_IMPORT = "No file was posted with parametername 'file'";


    /* Labels */
    public static final String DEFAULT_LABELCOLOR = "#000";
    public static final String DEFAULT_LABELNAME = "NEW LABEL, CHANGE NAME";


}
