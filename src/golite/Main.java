package golite;

import golite.parser.*;
import golite.lexer.*;
import golite.node.*;
import java.io.*;
 

// TODO: Document.
class Main {
    public static void main(String args[]) {
        try {
            displayTokens(args[0]);
        } catch(Exception e) {
            System.out.println(e);
        }
    }

    // TODO: Document.
    public static void displayTokens(String inPath) throws LexerException, IOException {

        Lexer lexer = new GoLiteLexer(new PushbackReader(new FileReader(inPath), 1024));

        while (!(lexer.peek() instanceof EOF)) {
            Token token = lexer.next();
            System.out.println(token.getClass().getSimpleName() + " (" + token.getText() + ")");
        }
    }
}
