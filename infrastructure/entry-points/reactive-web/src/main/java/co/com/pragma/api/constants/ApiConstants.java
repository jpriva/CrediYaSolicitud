package co.com.pragma.api.constants;

public class ApiConstants {

    private ApiConstants() {}

    public static final class ApiPaths {
        private ApiPaths() {}
        public static final String BASE_PATH = "/api/v1";
        public static final String SOLICITUDE_PATH = BASE_PATH + "/solicitud";
    }

    public static final class Operations {
        private Operations() {}
        public static final String SAVE_SOLICITUDE_OPERATION_ID = "saveLoanApplication";
        public static final String SAVE_SOLICITUDE_REQUEST_BODY_DESC = "Loan Application Requested Data";
    }

    public static final class Responses {
        private Responses() {}
        public static final String SAVE_SOLICITUDE_SUCCESS_CREATED_DESC = "Loan Application Created Successfully";
        public static final String SUCCESS_CREATED_CODE = "201";
        public static final String SAVE_SOLICITUDE_BAD_REQUEST_DESC = "Invalid request (e.g. missing or incorrectly formatted data)";
        public static final String BAD_REQUEST_CODE = "400";
        public static final String SAVE_SOLICITUDE_CONFLICT_DESC = "Data conflict (e.g. email already exists)";
        public static final String CONFLICT_CODE = "409";
    }
}