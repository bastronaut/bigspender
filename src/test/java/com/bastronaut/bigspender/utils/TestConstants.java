package com.bastronaut.bigspender.utils;

import org.springframework.test.web.servlet.MockMvc;

import java.util.Base64;

public class TestConstants {

    /** Sample files for transactions **/
    public static final String SAMPLE_CSV_PATH = "./src/test/resources/ingtransactions/sample-transactions.csv";
    public static final String SUBSET_SAMPLE_CSV_PATH = "./src/test/resources/ingtransactions/subset-valid-transactions.csv";
    public static final String FAKE_TRANSACTIONS_CSV_PATH = "./src/test/resources/ingtransactions/fake-transactions-test.csv";

    /** Constants for User related tests **/
    public static final String USERID_PARAM_REPLACE = "{userid}";
    public static final String USERS_ENDPOINT = "/users";
    public static final String USER_ENDPOINT = "/users/" + USERID_PARAM_REPLACE;
    public static final String TRANSACTIONID_PARAM_REPLACE = "{transactionid}";
    public static final String TRANSACTION_IMPORT_ENDPOINT = "/transactionimport";
    public static final String TRANSACTIONS_ENDPOINT = "/transactions";
    public static final String TRANSACTION_ENDPOINT = "/transactions/" + TRANSACTIONID_PARAM_REPLACE;
    public static final String NAME_PARAM = "name";
    public static final String EMAIL_PARAM = "email";
    public static final String PASSWORD_PARAM = "password";
    public static final String TEST_EMAIL = "test@email.com";
    public static final String TEST_PASSWORD = "testpassword";
    public static final String TEST_EMAIL_UPDATE = "update@email.com";
    public static final String TEST_PASSWORD_UPDATE = "12345678";
    public static final String USERPW = TEST_EMAIL + ":" + TEST_PASSWORD;
    public static final String LOGINHEADER_ENCODED = "Basic " + (Base64.getEncoder().encodeToString(USERPW.getBytes()));


    /** Exception text constants **/
    public static final String ERROR_MESSAGE_PARAM = "message";
    public static final String ERROR_DETAILS_PARAM = "details";
    public static final String ERROR_DATE_FIELD = "date";

    public static final String REGISTRATION_ERROR_PARAM = "Registration error";
    public static final String UPDATE_ERROR_MSG = "User update error";
    public static final String LABEL_ERROR_MSG = "Label error";
    public static final String TRANSACTIONIMPORT_ERROR_PARAM = "Transaction Import error";


    public static final String ERRORMSG_USER_EMAIL_NULL = "Field: email is required";
    public static final String ERRORMSG_USER_PW_NULL = "Field: password is required";
    public static final String ERRORMSG_USER_PW_SIZE = "Field: password requires a minimum length of 8";
    public static final String ERRORMSG_INVALID_EMAIL = "Invalid email address, not well-formed";
    public static final String ERRORMSG_LABEL_NAME_EMPTY = "Field: name is required for label";
    public static final String ERRORMSG_LABEL_EMPTY = "Field: label is required";
    public static final String ERRORMSG_TRANSACTIONIMPORT_FILE_NULL = "No file was posted with parametername 'file'";
    public static final String ERRORMSG_REGISTRATION_NOT_ALLOWED = "Registration not allowed while logged in";

    /** Label related tests **/
    public static final String LABELSID_PARAM_REPLACE = "{labelid}";
    public static final String LABELS_ENDPOINT = "/labels";
    public static final String LABEL_ENDPOINT = "/labels/" + LABELSID_PARAM_REPLACE;
    public static final String LABELS_PER_TRANSACTION_ENDPOINT = "/transactions/"+ TRANSACTIONID_PARAM_REPLACE + "/labels/";
    public static final String TRANSACTION_LABELS = "/transactions/labels"; // To add labels to transactions
    public static final String TRANSACTIONS_BY_LABELS = "/transactions/labels/" + LABELSID_PARAM_REPLACE;

    public static final String DEFAULT_LABELCOLOR = "#000";
    public static final String DEFAULT_LABELNAME = "NEW LABEL, CHANGE NAME";

    public static final String ERRORMSG_TOO_MANY_LOGIN_ATTEMPTS = "Too many login attempts. Login disabled for 24 hours";
    public static final int MAX_LOGIN_ATTEMPTS = 3;


}
