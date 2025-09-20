package co.com.pragma.model.sqs.exceptions;

import co.com.pragma.model.constants.Errors.QueueError;
import co.com.pragma.model.exceptions.CustomException;

public class QueueNotFoundException extends CustomException {
    public QueueNotFoundException() {
        super(QueueError.NOT_FOUND, QueueError.NOT_FOUND_CODE);
    }
}
