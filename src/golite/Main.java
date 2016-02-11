package golite;

import golite.parser.*;
import golite.lexer.*;
import golite.node.*;
import java.io.*;
 

/**
 * Main.
 */
class Main {
    public static void main(String args[]) {
        try {
            // Display the scanned tokens in the GoLite program designated by
            // the argument filepath.
            displayTokens(args[0]);
        } catch(Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Prints the scanned tokens in a GoLite program, one per line. The scanner token is printed
     * along with the underlying scanned text in brackets (except for TEol token).
     *
     * @param inPath - Filepath to GoLite program to scan
     */
    public static void displayTokens(String inPath) throws LexerException, IOException {

        Lexer lexer = new GoLiteLexer(new PushbackReader(new FileReader(inPath), 1024));

        while (!(lexer.peek() instanceof EOF)) {
            Token token = lexer.next();

            if (token instanceof TEol)
                System.out.println(token.getClass().getSimpleName());
            else
                System.out.println(token.getClass().getSimpleName() + " (" + token.getText() + ")");
        }
    }
}
