package golite.exception;

import golite.node.*;

@SuppressWarnings("serial")
public class WeedException extends Exception
{
    public WeedException(String message)
    {
        super(message);
    }

}