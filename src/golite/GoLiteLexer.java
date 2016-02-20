package golite;

import java.io.PushbackReader;

import golite.lexer.Lexer;
import golite.lexer.Lexer.State;
import golite.node.*;


/**
 * GoLite Scanner.
 *
 * Consulted <a href="http://www.sable.mcgill.ca/~hendren/520/2016/semicolon-test/go/GoLexer.java">
 * GoLexer.java</a> in the example SableCC code for handling the GoLite semicolon rule.
 */
public class GoLiteLexer extends Lexer {
    /** Tracks the last token seen. */
    private Token lastToken = null;
    /** Tracks the last "effective" token, i.e. a non-ignored token. */
    private Token lastEffectiveToken = null;

    /**
     * @param in - GoLite program reader
     */
    public GoLiteLexer(PushbackReader in) {
        super(in);
    }

    /**
     * Checks if the given token is "effective" or not (i.e. is a non-ignored
     * token).
     *
     * @param t - Token
     * @return True if the token is effective, false otherwise
     */
    private boolean isEffectiveToken(Token t) {
        return !(t instanceof TBlank || t instanceof TComment || t instanceof TEol);
    }

    /**
     * Checks if a semi-colon token needs to be entered into the scanning stream.
     * 
     * @return True if a semi-colon token (TSemi) needs to be inserted into the
     *  scanning stream
     */
    private boolean requiresSemi() {
        return
            (this.token instanceof TEol || this.token instanceof EOF) &&
            (this.lastEffectiveToken instanceof TId ||
                // Keywords.
                this.lastEffectiveToken instanceof TBreak ||
                this.lastEffectiveToken instanceof TContinue ||
                this.lastEffectiveToken instanceof TFallthrough ||
                this.lastEffectiveToken instanceof TReturn ||
                // Types.
                this.lastEffectiveToken instanceof TInt ||
                this.lastEffectiveToken instanceof TFloat64 ||
                this.lastEffectiveToken instanceof TBool ||
                this.lastEffectiveToken instanceof TRune ||
                this.lastEffectiveToken instanceof TString ||
                // Operators.
                this.lastEffectiveToken instanceof TPlusPlus ||
                this.lastEffectiveToken instanceof TMinusMinus ||
                this.lastEffectiveToken instanceof TRparen ||
                this.lastEffectiveToken instanceof TRsquare ||
                this.lastEffectiveToken instanceof TRbrace ||
                // Literals.
                this.lastEffectiveToken instanceof TBoolLit ||
                this.lastEffectiveToken instanceof TIntLit ||
                this.lastEffectiveToken instanceof TOctLit ||
                this.lastEffectiveToken instanceof THexLit ||
                this.lastEffectiveToken instanceof TFloatLit ||
                this.lastEffectiveToken instanceof TRuneLit ||
                this.lastEffectiveToken instanceof TInterpretedStringLit ||
                this.lastEffectiveToken instanceof TRawStringLit);
    }

    /**
     * Updates the scanning stream.
     */
    protected void filter() {
        if (this.requiresSemi())
            this.token = new TSemi();

        if (this.isEffectiveToken(this.token))
            this.lastEffectiveToken = this.token;

        this.lastToken = this.token;
    }
}
