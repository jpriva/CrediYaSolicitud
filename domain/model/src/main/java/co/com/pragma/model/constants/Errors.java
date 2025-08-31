package co.com.pragma.model.constants;

public class Errors {



    private Errors(){}
    public static final String SOLICITUDE_NULL_CODE = "S001";
    public static final String SOLICITUDE_NULL = "Application is null.";

    public static final String REQUIRED_FIELDS_CODE = "S002";
    public static final String REQUIRED_FIELDS = "is a required field and must be provided.";

    public static final String SIZE_OUT_OF_BOUNDS_CODE = "S003";
    public static final String SIZE_OUT_OF_BOUNDS = "Size out of bounds.";

    public static final String ERROR_SAVING_SOLICITUDE_CODE = "S004";
    public static final String ERROR_SAVING_SOLICITUDE = "Error saving application.";

    public static final String VALUE_OUT_OF_BOUNDS_CODE = "S005";

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

}
