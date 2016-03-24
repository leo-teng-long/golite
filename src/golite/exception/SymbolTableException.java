package golite.exception;

/**
 * Symbol table exception.
 */
@SuppressWarnings("serial")
public class SymbolTableException extends RuntimeException {

    public SymbolTableException(String message) {
        super(message);
    }

}
