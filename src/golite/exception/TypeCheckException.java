package golite.exception;

import golite.node.*;

@SuppressWarnings("serial")
public class TypeCheckException extends Exception {
    public TypeCheckException(String message) {
        super(message);
    }
}
