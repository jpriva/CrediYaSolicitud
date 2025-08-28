package co.com.pragma.model.constants;

public class Errors {



    private Errors(){}
    public static final String SOLICITUDE_NULL_CODE = "S001";
    public static final String SOLICITUDE_NULL = "Request is null.";

    public static final String REQUIRED_FIELDS_CODE = "S002";
    public static final String REQUIRED_FIELDS = "is a required field and must be provided.";

    public static final String SIZE_OUT_OF_BOUNDS_CODE = "S003";
    public static final String SIZE_OUT_OF_BOUNDS = "Size out of bounds.";

    public static final String ERROR_SAVING_SOLICITUDE_CODE = "S004";
    public static final String ERROR_SAVING_SOLICITUDE = "Error saving request.";

    public static final String LOAN_TYPE_NOT_FOUND_CODE = "LT001";
    public static final String LOAN_TYPE_NOT_FOUND = "Loan type not found.";

}
