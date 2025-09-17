package co.com.pragma.model.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Errors {

    public static final String SOLICITUDE_NULL_CODE = "S001";
    public static final String SOLICITUDE_NULL = "Application not found.";

    public static final String REQUIRED_FIELDS_CODE = "S002";
    public static final String REQUIRED_FIELDS = "is a required field and must be provided.";

    public static final String SIZE_OUT_OF_BOUNDS_CODE = "S003";
    public static final String SIZE_OUT_OF_BOUNDS = "Size out of bounds.";

    public static final String ERROR_SAVING_SOLICITUDE_CODE = "S004";
    public static final String ERROR_SAVING_SOLICITUDE = "Error saving application.";

    public static final String VALUE_OUT_OF_BOUNDS_CODE = "S005";
    public static final String VALUE_OUT_OF_BOUNDS = "Value out of bounds.";

    public static final String INVALID_FIELD_CODE = "S003";
    public static final String INVALID_FIELD = "Invalid Value.";

    public static final String LOAN_TYPE_NOT_FOUND_CODE = "LT001";
    public static final String LOAN_TYPE_NOT_FOUND = "Loan type not found.";

    public static final String LOAN_TYPE_MIN_MAX_VALUE_ERROR_CODE = "LT002";
    public static final String LOAN_TYPE_MIN_MAX_VALUE_ERROR = "Loan type is not available.";

    public static final String FAIL_READ_REQUEST_CODE = "W001";
    public static final String FAIL_READ_REQUEST = "Failed to read HTTP message.";

    public static final String UNKNOWN_CODE = "UNKNOWN";
    public static final String UNKNOWN_ERROR = "We are sorry, something went wrong. Please try again later.";

    public static final String USER_NOT_FOUND_CODE = "U101";
    public static final String USER_NOT_FOUND = "User not found with idNumber: ";

    public static final String COMMUNICATION_FAILED_CODE = "C001";
    public static final String COMMUNICATION_FAILED = "Communication failed with user service";

    public static final String ERROR_FROM_USER_SERVICE = "Error from user service: ";

    public static final String INVALID_CREDENTIALS_CODE = "IC001";
    public static final String INVALID_CREDENTIALS = "Invalid credentials.";

    public static final String INVALID_ENDPOINT_CODE = "IE001";
    public static final String INVALID_ENDPOINT = "Invalid endpoint.";

    public static final String ACCESS_DENIED_CODE = "AD001";
    public static final String ACCESS_DENIED = "Access denied. You do not have the necessary permissions to access this resource.";

    public static final String USER_GATEWAY_COMMUNICATION_FAILED = "Communication failed with user service";
    public static final String USER_GATEWAY_COMMUNICATION_FAILED_CODE = "GW-002";

    public static final String USER_GATEWAY_ERROR_FROM_SERVICE = "Error from user service";
    public static final String USER_GATEWAY_ERROR_FROM_SERVICE_CODE = "GW-001";

    public static final String METRIC_NOT_FOUND = "Metric not found";
    public static final String METRIC_NOT_FOUND_CODE = "M001";

    public static final String METRIC_INVALID_VALUE = "Metric invalid value";
    public static final String METRIC_INVALID_VALUE_CODE = "M002";

    public static final String METRICS_NOT_CONFIGURED = "Metrics not configured";
    public static final String METRICS_NOT_CONFIGURED_CODE = "M003";

    public static final String METRIC_NOT_CONFIGURED = "Metric not configured";
    public static final String METRIC_NOT_CONFIGURED_CODE = "M004";

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class QueueError{
        public static final String ALIAS_EMPTY = "Queue not set.";
        public static final String ALIAS_EMPTY_CODE = "Q001";
        public static final String NOT_FOUND = "Queue not found.";
        public static final String NOT_FOUND_CODE = "Q002";
    }

}
