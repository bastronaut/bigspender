package com.bastronaut.bigspender.utils;

public class TestConstants {

    /** Sample files for transactions **/
    public static final String SAMPLE_CSV_PATH = "./src/test/resources/ingtransactions/sample-transactions.csv";
    public static final String SUBSET_SAMPLE_CSV_PATH = "./src/test/resources/ingtransactions/subset-valid-transactions.csv";
    public static final String FAKE_TRANSACTIONS_CSV_PATH = "./src/test/resources/ingtransactions/fake-transactions-test.csv";

    /** Constants for User related tests **/
    public static final String USERS_ENDPOINT = "/users";
    public static final String USERS_UPDATE_ENDPOINT = "/users/{userid}";
    public static final String USERS_GET_INFO_ENDPOINT = "/users/{userid}";
    public static final String USERID_PARAM_REPLACE = "{userid}";
    public static final String TRANSACTIONID_PARAM_REPLACE = "{transactionid}";
    public static final String TRANSACTION_IMPORT_ENDPOINT = "/users/" + USERID_PARAM_REPLACE + "/transactionimport";
    public static final String TRANSACTIONS_ENDPOINT = "/users/" + USERID_PARAM_REPLACE +  "/transactions";
    public static final String TRANSACTION_ENDPOINT = "/users/" + USERID_PARAM_REPLACE + "/transactions/" + TRANSACTIONID_PARAM_REPLACE;
    public static final String NAME_PARAM = "name";
    public static final String EMAIL_PARAM = "email";
    public static final String PASSWORD_PARAM = "password";
    public static final String TEST_EMAIL = "test@email.com";
    public static final String TEST_FIRSTNAME = "tester";
    public static final String TEST_PASSWORD = "testpassword";
    public static final String TEST_EMAIL_UPDATE = "update@email.com";
    public static final String TEST_FIRSTNAME_UPDATE = "updated";
    public static final String TEST_PASSWORD_UPDATE = "updated";

    /** Exception text constants **/
    public static final String ERROR_MESSAGE_PARAM = "message";
    public static final String ERROR_DETAILS_PARAM = "details";
    public static final String ERROR_DATE_FIELD = "date";


}
