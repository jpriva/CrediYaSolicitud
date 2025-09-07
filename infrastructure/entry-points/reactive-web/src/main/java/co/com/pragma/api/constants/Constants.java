package co.com.pragma.api.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {

    // --- General Schemas ---
    public static final String SOLICITUDE_RESPONSE_SCHEMA_NAME = "Loan Application Response";
    public static final String SOLICITUDE_RESPONSE_SCHEMA_DESCRIPTION = "Loan Application Data.";
    public static final String STATE_SCHEMA_NAME = "State";
    public static final String STATE_SCHEMA_DESCRIPTION = "State Data.";
    public static final String LOAN_TYPE_SCHEMA_NAME = "Loan Type";
    public static final String LOAN_TYPE_SCHEMA_DESCRIPTION = "Loan Type Data.";

    // --- Solicitude Schema Descriptions & Examples ---
    public static final String SOLICITUDE_ID_DESCRIPTION = "Loan application's unique identifier.";
    public static final String SOLICITUDE_VALUE_DESCRIPTION = "Loan application's value.";
    public static final String SOLICITUDE_DEADLINE_DESCRIPTION = "Loan application's deadline in months.";
    public static final String SOLICITUDE_EMAIL_DESCRIPTION = "User's email";
    public static final String SOLICITUDE_STATE_DESCRIPTION = "State of the Loan application.";
    public static final String SOLICITUDE_LOAN_TYPE_DESCRIPTION = "Loan Type of the Loan application.";
    public static final String EXAMPLE_SOLICITUDE_ID = "123";
    public static final String EXAMPLE_SOLICITUDE_VALUE = "123456.78";
    public static final String EXAMPLE_SOLICITUDE_DEADLINE = "12";
    public static final String EXAMPLE_EMAIL = "john.doe@example.com";
    public static final String EXAMPLE_ID_NUMBER = "123456789";

    // --- State Schema Descriptions & Examples ---
    public static final String STATE_ID_DESCRIPTION = "State identifier.";
    public static final String STATE_NAME_DESCRIPTION = "State name.";
    public static final String STATE_DESCRIPTION_DESCRIPTION = "State description.";
    public static final String EXAMPLE_STATE_ID = "1";
    public static final String EXAMPLE_STATE_NAME = "PENDIENTE";
    public static final String EXAMPLE_STATE_DESCRIPTION = "La solicitud está pendiente de revisión.";

    // --- LoanType Schema Descriptions & Examples ---
    public static final String LOAN_TYPE_ID_DESCRIPTION = "Loan Type identifier.";
    public static final String LOAN_TYPE_NAME_DESCRIPTION = "Loan Type name.";
    public static final String LOAN_TYPE_MIN_VALUE_DESCRIPTION = "Minimum amount for the loan type.";
    public static final String LOAN_TYPE_MAX_VALUE_DESCRIPTION = "Maximum amount for the loan type.";
    public static final String LOAN_TYPE_INTEREST_RATE_DESCRIPTION = "Interest rate for the loan type.";
    public static final String LOAN_TYPE_AUTO_VALIDATION_DESCRIPTION = "Indicates if the loan type has automatic validation.";
    public static final String EXAMPLE_LOAN_TYPE_ID = "1";
    public static final String EXAMPLE_LOAN_TYPE_NAME = "CREDI-HOGAR";
    public static final String EXAMPLE_LOAN_TYPE_MIN_VALUE = "50000000.00";
    public static final String EXAMPLE_LOAN_TYPE_MAX_VALUE = "800000000.00";
    public static final String EXAMPLE_LOAN_TYPE_INTEREST_RATE = "7.20";
    public static final String EXAMPLE_LOAN_TYPE_AUTO_VALIDATION = "false";

    // --- LoanType Request Schema ---
    public static final String LOAN_TYPE_REQUEST_SCHEMA_NAME = "Loan Type Request";
    public static final String LOAN_TYPE_REQUEST_SCHEMA_DESCRIPTION = "Loan Type Data.";
    public static final String LOAN_TYPE_REQUEST_ID_DESCRIPTION = "Loan type id";
    public static final String VALIDATION_LOAN_TYPE_ID_NOT_NULL = "Loan type id can't be null";

    // --- Solicitude Request Schema ---
    public static final String SOLICITUDE_REQUEST_SCHEMA_NAME = "Loan Application Request";
    public static final String SOLICITUDE_REQUEST_SCHEMA_DESCRIPTION = "Loan Application Data.";
    public static final String SOLICITUDE_REQUEST_VALUE_DESCRIPTION = "Loan application's value.";
    public static final String SOLICITUDE_REQUEST_DEADLINE_DESCRIPTION = "Loan application's deadline in months.";
    public static final String SOLICITUDE_REQUEST_EMAIL_DESCRIPTION = "User's email";
    public static final String SOLICITUDE_REQUEST_ID_NUMBER_DESCRIPTION = "User's id number";
    public static final String SOLICITUDE_REQUEST_LOAN_TYPE_DESCRIPTION = "Loan Type.";

    // --- Solicitude Request Validations ---
    public static final String VALIDATION_VALUE_NOT_NULL = "Value can't be null";
    public static final String VALIDATION_VALUE_MIN = "Minimum value depends on loan type";
    public static final String VALIDATION_VALUE_MAX = "Maximum value depends on loan type";
    public static final String VALIDATION_DEADLINE_NOT_NULL = "Deadline can't be null";
    public static final String VALIDATION_DEADLINE_MIN = "Minimum value must be at least 1";
    public static final String VALIDATION_DEADLINE_MAX = "Maximum value must be at least 360, equivalent to 30 years";
    public static final String VALIDATION_EMAIL_NOT_BLANK = "Email can't be blank";
    public static final String VALIDATION_ID_NUMBER_NOT_BLANK = "Id number can't be blank";
    public static final String VALIDATION_EMAIL_FORMAT = "Email must be in a valid format";
    public static final String VALIDATION_LOAN_TYPE_NOT_NULL = "Loan Type can't be null";

    // --- API Paths ---
    public static final String API_BASE_PATH = "/api/v1";
    public static final String API_SOLICITUDE_PATH = API_BASE_PATH + "/solicitud";
    public static final String API_REPORT_PATH = API_BASE_PATH + "/report";

    // --- API Operations ---
    public static final String GET_ALL_LOAN_TYPES_SUMMARY = "Get all loan types";
    public static final String GET_ALL_LOAN_TYPES_DESCRIPTION = "Returns a list of all available loan types.";
    public static final String SAVE_SOLICITUDE_SUMMARY = "Create a new loan application";
    public static final String SAVE_SOLICITUDE_DESCRIPTION = "Receives the data to create a new loan application and returns the created object.";
    public static final String OPERATION_SAVE_SOLICITUDE_ID = "saveLoanApplication";
    public static final String OPERATION_REPORT_SOLICITUDE_ID = "getReportLoans";
    public static final String OPERATION_SAVE_SOLICITUDE_BODY_DESC = "Loan Application Requested Data";
    public static final String OPERATION_REPORT_BODY_DESC = "Report filters and pagination Requested Data";

    // --- API Responses ---
    public static final String RESPONSE_OK_CODE = "200";
    public static final String RESPONSE_CREATED_CODE = "201";
    public static final String RESPONSE_BAD_REQUEST_CODE = "400";
    public static final String RESPONSE_CONFLICT_CODE = "409";
    public static final String RESPONSE_REPORT_OK_DESC = "Fetch Report Successfully";
    public static final String RESPONSE_SAVE_SOLICITUDE_CREATED_DESC = "Loan Application Created Successfully";
    public static final String RESPONSE_SAVE_SOLICITUDE_BAD_REQUEST_DESC = "Invalid request (e.g. missing or incorrectly formatted data)";
    public static final String RESPONSE_SAVE_SOLICITUDE_CONFLICT_DESC = "Data conflict (e.g. email already exists)";

    // --- Error DTO Schema ---
    public static final String ERROR_SCHEMA_NAME = "Error Response";
    public static final String ERROR_SCHEMA_DESCRIPTION = "Error Response Details";
    public static final String ERROR_TIMESTAMP_DESCRIPTION = "Timestamp of when the error occurred.";
    public static final String ERROR_PATH_DESCRIPTION = "The path where the error occurred.";
    public static final String ERROR_CODE_DESCRIPTION = "A unique code identifying the error.";
    public static final String ERROR_MESSAGE_DESCRIPTION = "A human-readable message describing the error.";
    public static final String EXAMPLE_ERROR_TIMESTAMP = "2025-01-01T00:00:00.000Z";
    public static final String EXAMPLE_ERROR_CODE = "DOM-001";
    public static final String EXAMPLE_ERROR_MESSAGE = "An error occurred while processing the request.";
}