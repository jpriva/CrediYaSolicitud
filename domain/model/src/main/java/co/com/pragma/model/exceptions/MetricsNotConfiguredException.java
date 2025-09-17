package co.com.pragma.model.exceptions;

import co.com.pragma.model.constants.Errors;

public class MetricsNotConfiguredException extends CustomException {

    public MetricsNotConfiguredException() {
        super(Errors.METRICS_NOT_CONFIGURED, Errors.METRICS_NOT_CONFIGURED);
    }
}
