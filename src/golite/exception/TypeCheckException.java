package golite.exception;

import golite.node.*;

@SuppressWarnings("serial")
public class TypeCheckException extends RuntimeException {
    public TypeCheckException(String message) {
        super(message);
    }
}
