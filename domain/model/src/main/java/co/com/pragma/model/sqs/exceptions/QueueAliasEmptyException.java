package co.com.pragma.model.sqs.exceptions;

import co.com.pragma.model.constants.Errors.QueueError;
import co.com.pragma.model.exceptions.CustomException;

public class QueueAliasEmptyException extends CustomException {
    public QueueAliasEmptyException() {
        super(QueueError.ALIAS_EMPTY, QueueError.ALIAS_EMPTY_CODE);
    }
}
