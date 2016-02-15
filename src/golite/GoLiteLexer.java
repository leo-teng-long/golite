package golite;

import java.io.PushbackReader;

import golite.lexer.Lexer;
import golite.lexer.Lexer.State;
import golite.node.*;


/**
 * GoLite Scanner.
 */
public class GoLiteLexer extends Lexer {
    /** Tracks the last token seen. */
    private Token lastToken = null;

    /**
     * @param in - GoLite program reader
     */
    public GoLiteLexer(PushbackReader in) {
        super(in);
    }

    /**
     * Checks if a semi-colon token needs to be entered into the scanning stream.
     * 
     * @return True if a semi-colon token (TSemi) needs to be inserted into the
     *  scanning stream
     */
    private boolean requiresSemi() {
        // if (state != State.NORMAL)
        //     return false;

        return
            (this.token instanceof TEol || this.token instanceof EOF) &&
            (this.lastToken instanceof TId ||
                // Keywords.
                this.lastToken instanceof TBreak ||
                this.lastToken instanceof TContinue ||
                this.lastToken instanceof TFallthrough ||
                this.lastToken instanceof TReturn ||
                // Types.
                this.lastToken instanceof TInt ||
                this.lastToken instanceof TFloat64 ||
                this.lastToken instanceof TBool ||
                this.lastToken instanceof TRune ||
                this.lastToken instanceof TString ||
                // Operators.
                this.lastToken instanceof TPlusPlus ||
                this.lastToken instanceof TMinusMinus ||
                this.lastToken instanceof TRparen ||
                this.lastToken instanceof TRsquare ||
                this.lastToken instanceof TRbrace ||
                // Literals.
                this.lastToken instanceof TIntLit ||
                this.lastToken instanceof TOctLit ||
                this.lastToken instanceof THexLit ||
                this.lastToken instanceof TFloatLit ||
                this.lastToken instanceof TRuneLit ||
                this.lastToken instanceof TInterpretedStringLit ||
                this.lastToken instanceof TRawStringLit);
    }

    /**
     * Updates the scanning stream.
     */
    protected void filter() {
        if (requiresSemi())
            this.token = new TSemi();

        this.lastToken = this.token;
    }
}
