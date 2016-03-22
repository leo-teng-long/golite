package golite.exception;

import golite.node.*;

@SuppressWarnings("serial")
public class SymbolException extends RuntimeException {
    public SymbolException(String message) {
        super(message);
    }
}
