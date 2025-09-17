package co.com.pragma.model.exceptions;

import co.com.pragma.model.constants.Errors;

public class MetricNotConfiguredException extends CustomException {

    public final String metric;
    public MetricNotConfiguredException(String metric) {
        super(metric + " " +Errors.METRIC_NOT_CONFIGURED, Errors.METRIC_NOT_CONFIGURED);
        this.metric = metric;
    }
}
