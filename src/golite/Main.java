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
            if (args.length == 0)
                printUsage();
            else if (args[0].equals("-print")) {
                if (args.length == 1)
                    printUsage();
                else
                    // Display the scanned tokens in the GoLite program designated by
                    // the argument filepath.
                    displayTokens(args[1]);
            } else {
                if (scan(args[0]))
                    System.out.println("VALID");
                else
                    System.out.println("INVALID");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Prints the command-line usage to stderr.
     */
    public static void printUsage() {
        System.err.println("Usage: Main [-print] filename");
    }

    /**
     * Scans a GoLite program.
     *
     * @param inPath - Filepath to GoLite program to scan
     * @return True if the program passed scanning, false otherwise (If false, prints the error to
     *  stderr as well)
     */
    public static boolean scan(String inPath) throws IOException {
        Token token = null;

        try {
            for (
                Lexer lexer = new GoLiteLexer(new PushbackReader(new FileReader(inPath), 1024));
                !(lexer.peek() instanceof EOF);
                token = lexer.next()
            ) {}
        } catch (LexerException le) {
            System.err.println(le);
            return false;
        }
        
        return true;
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
