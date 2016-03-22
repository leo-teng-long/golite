package golite.exception;

import golite.node.*;


/**
 * GoLiteWeeder exception.
 */
@SuppressWarnings("serial")
public class WeederException extends RuntimeException {

    public GoLiteWeederException(String message) {
        super(message);
    }

}
