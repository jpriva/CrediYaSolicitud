package co.com.pragma.model.constants;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DefaultValues {
    private DefaultValues(){}

    public static final String PENDING_STATE = "PENDIENTE";
    public static final String APPROVED_STATE = "APROBADO";
    public static final String REJECTED_STATE = "RECHAZADO";

    public static final String VALUE_FIELD = "Value";
    public static final String TERM_FIELD = "Term";
    public static final String EMAIL_FIELD = "Email";
    public static final String LOAN_TYPE_FIELD = "LoanType";

    public static final int MAX_LOAN_TYPE = Integer.MAX_VALUE;
    public static final int MIN_LOAN_TYPE = 0;
    public static final int MAX_LENGTH_TERM = 360;//In months 360 months = 30 years
    public static final int MIN_LENGTH_TERM = 1;
    public static final int MAX_LENGTH_EMAIL = 100;
    public static final RoundingMode DEFAULT_ROUNDING_MODE = RoundingMode.HALF_UP;

}
