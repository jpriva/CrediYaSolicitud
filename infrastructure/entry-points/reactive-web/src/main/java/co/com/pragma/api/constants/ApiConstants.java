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

        // --- Pagination and Sorting ---
        public static final String REPORT_REQ_PAGE_DESC = "Page number to request (starts at 0).";
        public static final String REPORT_REQ_SIZE_DESC = "Number of records per page.";
        public static final String REPORT_REQ_SORT_BY_DESC = "Field to sort the results by.";
        public static final String REPORT_REQ_SORT_DIR_DESC = "Sort direction.";

        // --- Sort By Values (for allowableValues) ---
        public static final String SORT_BY_VALUE = "value";
        public static final String SORT_BY_CLIENT_NAME = "clientName";
        public static final String SORT_BY_STATE = "state";

        // --- Sort Direction Values (for allowableValues) ---
        public static final String SORT_DIR_ASC = "ASC";
        public static final String SORT_DIR_DESC = "DESC";

        // --- Examples ---
        public static final String EXAMPLE_SORT_BY_VALUE = "value";
        public static final String EXAMPLE_SORT_DIR_DESC = "DESC";
        public static final String EXAMPLE_CLIENT_EMAIL = "john.doe@example.com";
        public static final String EXAMPLE_CLIENT_NAME = "John";
        public static final String EXAMPLE_CLIENT_ID = "123456789";
        public static final String EXAMPLE_LOAN_TYPE = "CREDI-HOGAR";
        public static final String EXAMPLE_STATE = "APROBADO";
        public static final String EXAMPLE_MIN_VALUE = "10000.00";
        public static final String EXAMPLE_MAX_VALUE = "50000.00";
        public static final String EXAMPLE_MIN_SALARY = "2000000.00";
        public static final String EXAMPLE_MAX_SALARY = "5000000.00";

        // --- Defaults ---
        public static final String DEFAULT_PAGE = "0";
        public static final String DEFAULT_SIZE = "10";

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
}