package co.com.pragma.api.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiConstants {


    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class ApiPath {
        public static final String BASE_PATH = "/api/v1";
        //Loan type
        public static final String LOAN_TYPE_PATH = BASE_PATH + "/tipoprestamo";
        //State
        public static final String STATE_PATH = BASE_PATH + "/estado";
        //Loan application
        public static final String SOLICITUDE_PATH = BASE_PATH + "/solicitud";
        //Update State
        public static final String SOLICITUDE_UPDATE_PATH = SOLICITUDE_PATH + "/{id}";
        //Calculate Debt capacity
        public static final String DEBT_CAPACITY_PATH = BASE_PATH + "/calcular-capacidad";
        //***********************
        public static final String SWAGGER_PATH = "/swagger-ui.html";
        public static final String DUMMY_SOLICITUDE_DOC_ROUTE = "/dummy-solicitude-doc-route";
        public static final String DUMMY_REPORT_DOC_ROUTE = "/dummy-report-doc-route";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class ApiPathMatchers {
        //PERMIT ALL
        public static final String LOAN_TYPE_MATCHER = ApiPath.LOAN_TYPE_PATH + "/**";
        public static final String STATE_MATCHER = ApiPath.STATE_PATH + "/**";
        public static final String API_DOCS_MATCHER = "/solicitude/api-docs/**";
        public static final String SWAGGER_UI_MATCHER = "/solicitude/swagger-ui/**";
        //SUPER_USER
        public static final String ACTUATOR_MATCHER = "/solicitude/actuator/**";
        public static final String HEALTH_CHECK_MATCHER = "/solicitude/actuator/health/**";
        public static final String SOLICITUDE_MATCHER = ApiPath.SOLICITUDE_PATH + "/**";
        public static final String DEBT_CAPACITY_MATCHER = ApiPath.DEBT_CAPACITY_PATH + "/**";
        //ADMIN
        //TEST ENDPOINT
        public static final String TEST_MATCHER = "/test-endpoint";

    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Role {
        public static final String SUPER_USER_ROLE_NAME = "SUPER_USER";
        public static final String CLIENT_ROLE_NAME = "CLIENTE";
        public static final String ADMIN_ROLE_NAME = "ADMIN";
        public static final String ADVISOR_ROLE_NAME = "ASESOR";
        public static final String CALLBACK_ROLE = "CALLBACK_ROLE";

    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Report {

        // --- Report Response Schema ---
        public static final String REPORT_RES_SCHEMA_NAME = "Loan Application Report Response";
        public static final String REPORT_RES_SCHEMA_DESC = "Data for a single row in the loan application report.";
        public static final String REPORT_RES_SOLICITUDE_ID_DESC = "Unique identifier of the loan application.";
        public static final String REPORT_RES_VALUE_DESC = "Value of the loan application.";
        public static final String REPORT_RES_DEADLINE_DESC = "Deadline of the loan application in months.";
        public static final String REPORT_RES_CLIENT_EMAIL_DESC = "Client's email address.";
        public static final String REPORT_RES_CLIENT_NAME_DESC = "Client's full name.";
        public static final String REPORT_RES_CLIENT_ID_DESC = "Client's identification number.";
        public static final String REPORT_RES_LOAN_TYPE_DESC = "Name of the loan type.";
        public static final String REPORT_RES_INTEREST_RATE_DESC = "Interest rate of the loan.";
        public static final String REPORT_RES_STATE_DESC = "Current state of the application.";
        public static final String REPORT_RES_BASE_SALARY_DESC = "Client's base salary at the time of the application.";
        public static final String REPORT_RES_TOTAL_DEBT_DESC = "Client's total debt at the time of the application.";

        // --- Response Examples ---
        public static final String EXAMPLE_RES_SOLICITUDE_ID = "101";
        public static final String EXAMPLE_RES_VALUE = "25000.00";
        public static final String EXAMPLE_RES_DEADLINE = "24";
        public static final String EXAMPLE_RES_CLIENT_EMAIL = "jane.doe@example.com";
        public static final String EXAMPLE_RES_CLIENT_NAME = "Jane Doe";
        public static final String EXAMPLE_RES_CLIENT_ID = "987654321";
        public static final String EXAMPLE_RES_LOAN_TYPE = "CREDI-VEHICULO";
        public static final String EXAMPLE_RES_INTEREST_RATE = "9.20";
        public static final String EXAMPLE_RES_STATE = "APROBADO";
        public static final String EXAMPLE_RES_BASE_SALARY = "3500000.00";
        public static final String EXAMPLE_RES_TOTAL_DEBT = "1500000.00";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Pageable {
        // --- Sort By Values (for allowableValues) ---
        public static final String SORT_BY_VALUE = "monto";
        public static final String SORT_BY_CLIENT_EMAIL = "email";
        public static final String SORT_BY_STATE = "estado";
        public static final String SORT_BY_LOAN_TYPE = "tipo_prestamo";

        // --- Sort Direction Values (for allowableValues) ---
        public static final String SORT_DIR_ASC = "ASC";
        public static final String SORT_DIR_DESC = "DESC";

        // --- Defaults ---
        public static final String DEFAULT_PAGE = "0";
        public static final String DEFAULT_SIZE = "10";
        public static final String REPORT_REQ_PAGEABLE_DESC = "Pagination and sorting parameters.";

        // --- Paginated Response Schema ---
        public static final String PAGINATED_RESPONSE_SCHEMA_DESC = "Container for paginated list results.";
        public static final String PAGINATED_CONTENT_DESC = "The content of the current page.";
        public static final String PAGINATED_CURRENT_PAGE_DESC = "The current page number (0-indexed).";
        public static final String PAGINATED_PAGE_SIZE_DESC = "The number of elements requested per page.";
        public static final String PAGINATED_TOTAL_ELEMENTS_DESC = "The total number of elements across all pages.";
        public static final String PAGINATED_TOTAL_PAGES_DESC = "The total number of pages available.";
        public static final String PAGINATED_HAS_NEXT_DESC = "Indicates if there is a next page.";
        public static final String PAGINATED_HAS_PREVIOUS_DESC = "Indicates if there is a previous page.";
        public static final String EXAMPLE_PAGINATED_CURRENT_PAGE = "0";
        public static final String EXAMPLE_PAGINATED_PAGE_SIZE = "10";
        public static final String EXAMPLE_PAGINATED_TOTAL_ELEMENTS = "100";
        public static final String EXAMPLE_PAGINATED_TOTAL_PAGES = "10";
        public static final String EXAMPLE_PAGINATED_HAS_NEXT = "true";
        public static final String EXAMPLE_PAGINATED_HAS_PREVIOUS = "false";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class ReportDocs {
        // --- Operation Summaries & Descriptions ---
        public static final String REPORT_OP_SUMMARY = "Get a paginated report of loan applications for advisor's role only";
        public static final String REPORT_OP_DESC = "Retrieves a list of loan applications based on the provided filter, sort, and pagination criteria.";

        // --- Parameter Descriptions ---
        public static final String PARAM_CLIENT_EMAIL_DESC = "Filter by client's email (supports partial match).";
        public static final String PARAM_CLIENT_NAME_DESC = "Filter by client's name (supports partial match).";
        public static final String PARAM_CLIENT_ID_DESC = "Filter by client's identification number (supports partial match).";
        public static final String PARAM_LOAN_TYPE_DESC = "Filter by the name of the loan type (supports partial match).";
        public static final String PARAM_STATE_DESC = "Filter by the application state (doesn't supports partial match) (e.g., APROBADO, PENDIENTE, RECHAZADO).";
        public static final String PARAM_MIN_VALUE_DESC = "Filter by minimum loan value.";
        public static final String PARAM_MAX_VALUE_DESC = "Filter by maximum loan value.";
        public static final String PARAM_MIN_SALARY_DESC = "Filter by minimum client base salary.";
        public static final String PARAM_MAX_SALARY_DESC = "Filter by maximum client base salary.";
        public static final String PARAM_PAGE_DESC = "Page number to retrieve (0-indexed).";
        public static final String PARAM_SIZE_DESC = "Number of records per page.";
        public static final String PARAM_SORT_BY_DESC = "Field to sort by.";
        public static final String PARAM_SORT_DIR_DESC = "Sort direction.";

        // --- Response Types ---
        public static final String RES_TYPE_STRING = "string";
        public static final String RES_TYPE_NUMBER = "number";
        public static final String RES_TYPE_INTEGER = "integer";

        // --- Response Descriptions ---
        public static final String RES_200_DESC = "Report generated successfully.";
        public static final String RES_401_DESC = "Unauthorized. A valid token is required.";
        public static final String RES_500_DESC = "Internal server error.";

        public static final String PAGINATED_REPORT_SCHEMA_DESCRIPTION = "A paginated list of loan application reports.";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class FilterParams {
        public static final String CLIENT_EMAIL = "clientEmail";
        public static final String CLIENT_NAME = "clientName";
        public static final String CLIENT_IDENTIFICATION = "clientIdentification";
        public static final String LOAN_TYPE = "loanType";
        public static final String STATE = "state";
        public static final String MIN_VALUE = "minValue";
        public static final String MAX_VALUE = "maxValue";
        public static final String MIN_BASE_SALARY = "minBaseSalary";
        public static final String MAX_BASE_SALARY = "maxBaseSalary";
        public static final String PAGE = "page";
        public static final int DEFAULT_PAGE = 0;
        public static final String SIZE = "size";
        public static final int DEFAULT_SIZE = 10;
        public static final String SORT_BY = "sortBy";
        public static final String SORT_DIRECTION = "sortDirection";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class ApiConfig {
        public static final String DESCRIPTION_BEARER_AUTH = "Enter the JWT token obtained from the login endpoint.";
        public static final String NAME_BEARER_AUTH = "bearerAuth";
        public static final String SCHEME_BEARER = "bearer";
        public static final String BEARER_FORMAT_JWT = "JWT";
        public static final String TITLE_API = "Crediya Auth API Microservice";
        public static final String VERSION_API = "1.0.0";
        public static final String DESCRIPTION_API = "This is the API for Crediya Auth Microservice";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class StateChangeDocs {
        // --- Operation Summaries & Descriptions ---
        public static final String APPROVE_REJECT_OP_SUMMARY = "Update loan application state for advisor's role only.";
        public static final String APPROVE_REJECT_OP_DESC = "Updates the state of a loan application to either 'APROBADO' or 'RECHAZADO'.";

        // --- Response Types ---
        public static final String RES_TYPE_STRING = "string";
        public static final String RES_TYPE_NUMBER = "number";
        public static final String RES_TYPE_INTEGER = "integer";

        // --- Response Descriptions ---
        public static final String RES_200_DESC = "Report generated successfully.";
        public static final String RES_401_DESC = "Unauthorized. A valid token is required.";
        public static final String RES_500_DESC = "Internal server error.";

        // --- Request Schema Status ---
        public static final String UPDATE_STATE_DESC = "The new state for the solicitude.";
        public static final String EXAMPLE_UPDATE_STATE = "APROBADO";
        public static final String APPROVED_STATE = "APROBADO";
        public static final String REJECTED_STATE = "RECHAZADO";
        public static final String MANUAL_STATE = "MANUAL";

        // --- Request Schema IdLoanApplication ---
        public static final String ID_SOLICITUDE_DESC = "The id of the Loan Application for status update.";
        public static final String EXAMPLE_ID_SOLICITUDE = "8";
    }
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class ApiOperations {
        public static final String GET_ALL_LOAN_TYPES_SUMMARY = "Get all loan types";
        public static final String GET_ALL_LOAN_TYPES_DESCRIPTION = "Returns a list of all available loan types.";
        public static final String SAVE_SOLICITUDE_SUMMARY = "Create a new loan application";
        public static final String SAVE_SOLICITUDE_DESCRIPTION = "Receives the data to create a new loan application and returns the created object.";
        public static final String OPERATION_SAVE_SOLICITUDE_ID = "saveLoanApplication";
        public static final String OPERATION_REPORT_SOLICITUDE_ID = "getReportLoans";
        public static final String OPERATION_SAVE_SOLICITUDE_BODY_DESC = "Loan Application Requested Data";
        public static final String OPERATION_UPDATE_SOLICITUDE_STATUS_ID = "updateSolicitudeStatus";
        public static final String OPERATION_REPORT_BODY_DESC = "Report filters and pagination Requested Data";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class ApiResponses {
        public static final String RESPONSE_OK_CODE = "200";
        public static final String RESPONSE_CREATED_CODE = "201";
        public static final String RESPONSE_BAD_REQUEST_CODE = "400";
        public static final String RESPONSE_NOT_FOUND_CODE = "404";
        public static final String RESPONSE_CONFLICT_CODE = "409";
        public static final String RESPONSE_REPORT_OK_DESC = "Fetch Report Successfully";
        public static final String RESPONSE_SAVE_SOLICITUDE_CREATED_DESC = "Loan Application Created Successfully";
        public static final String RESPONSE_SAVE_SOLICITUDE_BAD_REQUEST_DESC = "Invalid request (e.g. missing or incorrectly formatted data)";
        public static final String RESPONSE_SAVE_SOLICITUDE_CONFLICT_DESC = "Data conflict (e.g. email already exists)";
        public static final String RESPONSE_UPDATE_SOLICITUDE_OK_DESC = "Loan Application status updated successfully";
        public static final String RESPONSE_UPDATE_SOLICITUDE_NOT_FOUND_DESC = "The loan application with the specified ID was not found.";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Schemas {
        public static final String SOLICITUDE_RESPONSE_SCHEMA_NAME = "Loan Application Response";
        public static final String SOLICITUDE_RESPONSE_SCHEMA_DESCRIPTION = "Loan Application Data.";
        public static final String STATE_SCHEMA_NAME = "State";
        public static final String STATE_SCHEMA_DESCRIPTION = "State Data.";
        public static final String LOAN_TYPE_SCHEMA_NAME = "Loan Type";
        public static final String LOAN_TYPE_SCHEMA_DESCRIPTION = "Loan Type Data.";
        public static final String LOAN_TYPE_REQUEST_SCHEMA_NAME = "Loan Type Request";
        public static final String LOAN_TYPE_REQUEST_SCHEMA_DESCRIPTION = "Loan Type Data.";
        public static final String SOLICITUDE_REQUEST_SCHEMA_NAME = "Loan Application Request";
        public static final String SOLICITUDE_REQUEST_SCHEMA_DESCRIPTION = "Loan Application Data.";
        public static final String ERROR_SCHEMA_NAME = "Error Response";
        public static final String ERROR_SCHEMA_DESCRIPTION = "Error Response Details";
        public static final String PAGINATED_RESPONSE_SCHEMA_NAME = "PaginatedResponse";
        public static final String PAGINATED_REPORT_SCHEMA_NAME = "PaginatedSolicitudeReportResponse";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class SolicitudeDocs {
        public static final String UPDATE_SOLICITUDE_STATUS_BODY_DESC = "The new status for the application.";


        public static final String SOLICITUDE_ID_DESCRIPTION = "Loan application's unique identifier.";
        public static final String SOLICITUDE_ID_PARAM_DESCRIPTION = "The unique identifier of the loan application to update.";
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

        // Request Schema
        public static final String SOLICITUDE_REQUEST_VALUE_DESCRIPTION = "Loan application's value.";
        public static final String SOLICITUDE_REQUEST_DEADLINE_DESCRIPTION = "Loan application's deadline in months.";
        public static final String SOLICITUDE_REQUEST_EMAIL_DESCRIPTION = "User's email";
        public static final String SOLICITUDE_REQUEST_ID_NUMBER_DESCRIPTION = "User's id number";
        public static final String SOLICITUDE_REQUEST_LOAN_TYPE_DESCRIPTION = "Loan Type.";

        // Validations
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
        public static final String SOLICITUDE_OP_SUMMARY = "Saves a new Loan Application for client's role only.";
        public static final String SOLICITUDE_OP_DESC = "Saves a new Loan Application for clients only.";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class StateDocs {
        public static final String STATE_ID_DESCRIPTION = "State identifier.";
        public static final String STATE_NAME_DESCRIPTION = "State name.";
        public static final String STATE_DESCRIPTION_DESCRIPTION = "State description.";
        public static final String EXAMPLE_STATE_ID = "1";
        public static final String EXAMPLE_STATE_NAME = "PENDIENTE";
        public static final String EXAMPLE_STATE_DESCRIPTION = "La solicitud está pendiente de revisión.";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class LoanTypeDocs {
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

        // Request Schema
        public static final String LOAN_TYPE_REQUEST_ID_DESCRIPTION = "Loan type id";
        public static final String VALIDATION_LOAN_TYPE_ID_NOT_NULL = "Loan type id can't be null";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class ErrorDocs {
        public static final String ERROR_TIMESTAMP_DESCRIPTION = "Timestamp of when the error occurred.";
        public static final String ERROR_PATH_DESCRIPTION = "The path where the error occurred.";
        public static final String ERROR_CODE_DESCRIPTION = "A unique code identifying the error.";
        public static final String ERROR_MESSAGE_DESCRIPTION = "A human-readable message describing the error.";
        public static final String EXAMPLE_ERROR_TIMESTAMP = "2025-01-01T00:00:00.000Z";
        public static final String EXAMPLE_ERROR_CODE = "DOM-001";
        public static final String EXAMPLE_ERROR_MESSAGE = "An error occurred while processing the request.";
    }
}