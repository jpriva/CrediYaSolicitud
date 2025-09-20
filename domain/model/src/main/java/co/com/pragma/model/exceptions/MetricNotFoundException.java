package co.com.pragma.model.exceptions;

import co.com.pragma.model.constants.Errors;

public class MetricNotFoundException extends CustomException {

    public final String metric;
    public MetricNotFoundException(String metric) {
        super(metric + " " + Errors.METRIC_NOT_FOUND, Errors.METRIC_NOT_FOUND_CODE);
        this.metric = metric;
    }
}
