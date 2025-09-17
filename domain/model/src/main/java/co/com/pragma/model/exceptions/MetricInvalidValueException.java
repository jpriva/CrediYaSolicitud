package co.com.pragma.model.exceptions;

import co.com.pragma.model.constants.Errors;

public class MetricInvalidValueException extends CustomException {

    public final String metric;
    public MetricInvalidValueException(String metric) {
        super(metric + " " + Errors.METRIC_INVALID_VALUE, Errors.METRIC_INVALID_VALUE_CODE);
        this.metric = metric;
    }
}
