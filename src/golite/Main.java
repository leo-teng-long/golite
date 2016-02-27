package golite;

import golite.parser.*;
import golite.lexer.*;
import golite.node.*;
import golite.PrettyPrinter;
import java.io.*;


/**
 * Main.
 */
class Main {

    public static void main(String args[]) {
        try {
            if (args.length < 2 || args.length > 2)
                printUsage();
            // Scan.
            else if (args[0].equals("-scan")) {
                if (scan(args[1]))
                    System.out.println("VALID");
                else
                    System.out.println("INVALID");
            // Parse.
            } else if (args[0].equals("-parse")) {
                if (parse(args[1]))
                    System.out.println("VALID");
                else
                    System.out.println("INVALID");
            // Print scanner tokens to stdout.
            } else if (args[0].equals("-printTokens"))
                displayTokens(args[1]);
            else if (args[0].equals("-pretty"))
                prettyPrint(args[1]);
            else {
                printUsage();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Prints the command-line usage to stderr.
     */
    public static void printUsage() {
        System.err.println("Usage: Main -[scan | parse | pretty | printTokens] filename");
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
            System.err.println("ERROR: " + le);
            return false;
        }

        return true;
    }

    /**
     * Parses a GoLite program.
     *
     * @param inPath - Filepath to GoLite program to parse
     * @return True if the program passed parsing, false otherwise (If false, prints the error to
     *  stderr as well)
     */
    public static boolean parse(String inPath) throws IOException {
        try {
            Lexer lexer = new GoLiteLexer(new PushbackReader(new FileReader(inPath), 1024));
            Parser p = new Parser(lexer);
            p.parse();
        }
        catch (LexerException|ParserException e) {
            System.err.println("ERROR: " + e);
            return false;
        }

        return true;
    }

    /**
     * Pretty print a GoLite program.
     *
     * @param inPath - Filepath to GoLite program
     */
    public static void prettyPrint(String inPath) throws IOException {
        try {
            Lexer lexer = new GoLiteLexer(new PushbackReader(new FileReader(inPath), 1024));
            Parser parser = new Parser(lexer);
            Start tree = parser.parse();

            String fileName = inPath.substring(0, inPath.indexOf('.'));
            PrettyPrinter printer = new PrettyPrinter(fileName);
            tree.apply(printer);
        } catch (Exception e) {
            System.err.println("ERROR: " + e);
        }
    }

    /**
     * Prints the scanned tokens in a GoLite program, one per line. The scanner token is printed
     * along with the underlying scanned text in brackets (except for TEol token).
     *
     * @param inPath - Filepath to GoLite program to scan
     *
     * Consulted <a href="http://www.sable.mcgill.ca/~hendren/520/2016/tiny/sablecc-3/tiny/Main.java">
     * Main.java</a> of the Tiny language example on the course website.
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
