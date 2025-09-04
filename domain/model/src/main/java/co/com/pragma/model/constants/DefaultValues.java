package co.com.pragma.model.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.RoundingMode;
import java.text.DecimalFormat;
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DefaultValues {

    public static final String PENDING_STATE = "PENDIENTE";
    public static final String APPROVED_STATE = "APROBADO";
    public static final String REJECTED_STATE = "RECHAZADO";
    public static final String MANUAL_STATE = "MANUAL";
    public static final String PENDING_STATE_DESC = "La solicitud está pendiente de revisión.";
    public static final String APPROVED_STATE_DESC = "La solicitud ha sido aprobada.";
    public static final String REJECTED_STATE_DESC = "La solicitud ha sido rechazada.";
    public static final String MANUAL_STATE_DESC = "La solicitud está pendiente de revisión manual.";

    public static final String VALUE_FIELD = "Value";
    public static final String DEADLINE_FIELD = "Deadline";
    public static final String EMAIL_FIELD = "Email";
    public static final String LOAN_TYPE_FIELD = "LoanType";
    public static final String ID_NUMBER_FIELD = "Id number";

    public static final String MOST_BE_BETWEEN = "most be between";
    public static final String AND_CONNECTOR = "and";

    public static final int MAX_LOAN_TYPE = Integer.MAX_VALUE;
    public static final int MIN_LOAN_TYPE = 0;
    public static final int MAX_LENGTH_DEADLINE = 360;//In months 360 months = 30 years
    public static final int MIN_LENGTH_DEADLINE = 1;
    public static final int MAX_LENGTH_EMAIL = 100;
    public static final RoundingMode DEFAULT_ROUNDING_MODE = RoundingMode.HALF_UP;

    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,###.00");

}
