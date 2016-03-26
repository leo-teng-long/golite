package golite.exception;


/**
 * Type check exception.
 */
@SuppressWarnings("serial")
public class TypeCheckException extends RuntimeException {

    public TypeCheckException(String message) {
        super(message);
    }
    
}
