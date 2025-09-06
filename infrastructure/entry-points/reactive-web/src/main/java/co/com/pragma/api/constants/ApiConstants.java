package co.com.pragma.api.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiConstants {


    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class ApiPath {
        public static final String BASE_PATH = "/api/v1";
        public static final String LOAN_TYPE_PATH = BASE_PATH + "/tipoprestamo";
        public static final String STATE_PATH = BASE_PATH + "/estado";
        public static final String SOLICITUDE_PATH = BASE_PATH + "/solicitud";
        public static final String API_REPORT_PATH = BASE_PATH + "/report";
        public static final String SWAGGER_PATH = "/swagger-ui.html";
        public static final String DUMMY_SOLICITUDE_DOC_ROUTE = "/dummy-solicitude-doc-route";
        public static final String DUMMY_REPORT_DOC_ROUTE = "/dummy-report-doc-route";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class ApiPathMatchers {
        //PERMIT ALL
        public static final String LOAN_TYPE_MATCHER = ApiPath.LOAN_TYPE_PATH + "/**";
        public static final String STATE_MATCHER = ApiPath.STATE_PATH + "/**";
        public static final String API_DOCS_MATCHER = "/v3/api-docs/**";
        public static final String SWAGGER_UI_MATCHER = "/swagger-ui/**";
        //CLIENTE
        public static final String SOLICITUDE_MATCHER = ApiPath.SOLICITUDE_PATH + "/**";
        //ASESOR
        public static final String REPORT_MATCHER = ApiPath.API_REPORT_PATH + "/**";
        //ADMIN
        //TEST ENDPOINT
        public static final String TEST_MATCHER = "/test-endpoint";

    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Role {
        public static final String CLIENT_ROLE_NAME = "CLIENTE";
        public static final String ADMIN_ROLE_NAME = "ADMIN";
        public static final String ADVISOR_ROLE_NAME = "ASESOR";

    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Report {
        // --- Report Request Schema ---
        public static final String REPORT_REQ_SCHEMA_NAME = "Loan Application Report Request";
        public static final String REPORT_REQ_SCHEMA_DESC = "DTO to filter, paginate, and sort the loan application report.";
        public static final String REPORT_REQ_CLIENT_EMAIL_DESC = "Filter by client email.";
        public static final String REPORT_REQ_CLIENT_NAME_DESC = "Filter by client name (partial search).";
        public static final String REPORT_REQ_CLIENT_ID_DESC = "Filter by client identification.";
        public static final String REPORT_REQ_LOAN_TYPE_DESC = "Filter by loan type.";
        public static final String REPORT_REQ_STATE_DESC = "Filter by application state.";
        public static final String REPORT_REQ_MIN_VALUE_DESC = "Minimum application value for the filter.";
        public static final String REPORT_REQ_MAX_VALUE_DESC = "Maximum application value for the filter.";
        public static final String REPORT_REQ_MIN_SALARY_DESC = "Minimum client base salary for the filter.";
        public static final String REPORT_REQ_MAX_SALARY_DESC = "Maximum client base salary for the filter.";
        // --- Examples ---
        public static final String EXAMPLE_CLIENT_EMAIL = "john.doe@example.com";
        public static final String EXAMPLE_CLIENT_NAME = "John";
        public static final String EXAMPLE_CLIENT_ID = "123456789";
        public static final String EXAMPLE_LOAN_TYPE = "CREDI-HOGAR";
        public static final String EXAMPLE_STATE = "APROBADO";
        public static final String EXAMPLE_MIN_VALUE = "10000.00";
        public static final String EXAMPLE_MAX_VALUE = "50000.00";
        public static final String EXAMPLE_MIN_SALARY = "2000000.00";
        public static final String EXAMPLE_MAX_SALARY = "5000000.00";

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
        //Schema
        public static final String SCHEMA_NAME = "PageableRequest";
        public static final String SCHEMA_DESC = "DTO for pagination and sorting parameters.";

        // --- Examples ---
        public static final String EXAMPLE_SORT_BY_VALUE = "monto";
        public static final String EXAMPLE_SORT_DIR_DESC = "DESC";

        // --- Pagination and Sorting ---
        public static final String PAGEABLE_PAGE_DESC = "Page number to request (starts at 0).";
        public static final String PAGEABLE_SIZE_DESC = "Number of records per page.";
        public static final String PAGEABLE_SORT_BY_DESC = "Field to sort the results by.";
        public static final String PAGEABLE_SORT_DIR_DESC = "Sort direction.";

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
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class ReportDocs {
        // --- Operation Summaries & Descriptions ---
        public static final String REPORT_OP_SUMMARY = "Get a paginated report of loan applications";
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
}