package golite;

import golite.exception.SymbolTableException;
import golite.exception.TypeCheckException;
import golite.exception.WeederException;
import golite.type.TypeChecker;
import golite.lexer.*;
import golite.node.*;
import golite.parser.*;

import java.io.*;
import java.util.*;


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
            // Pretty print.
            } else if (args[0].equals("-pretty")) {
                prettyPrint(args[1]);
            // Type check.
            } else if (args[0].equals("-type")) {
                if (typeCheck(args[1]))
                    System.out.println("VALID");
                else
                    System.out.println("INVALID");
            // Typed pretty print.
            } else if (args[0].equals("-pptype"))
                throw new UnsupportedOperationException();
            // Print tokens.
            else if (args[0].equals("-printTokens"))
                displayTokens(args[1]);
            // Dump symbol table.
            else if (args[0].equals("-dumpsymtab"))
                dumpSymbolTable(args[1]);
            else
                printUsage();
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    /**
     * Prints the command-line usage to stderr.
     */
    private static void printUsage() {
        System.err.println("Usage: Main -[scan | parse | pretty | pptype | printTokens | "
            + "dumpsymtab | weed | weedv | type | typev ] filename");
        System.exit(-1);
    }

    /**
     * Scans a GoLite program.
     *
     * @param inPath - Filepath to GoLite program to scan
     * @return True if the program passed scanning, false otherwise (If false, prints the error to
     *  stderr as well)
     */
    private static boolean scan(String inPath) throws IOException {
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
            Weeder weeder = new Weeder();

            Start ast = p.parse();
            ast.apply(weeder);
        } catch (LexerException|ParserException|WeederException e) {
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
            Weeder weeder = new Weeder();

            Start tree = parser.parse();
            tree.apply(weeder);

            PrettyPrinter pp = new PrettyPrinter();
            tree.apply(pp);

            String prettyPrint = pp.getPrettyPrint();
            
            dump(prettyPrint, inPath, ".pretty.go");
        } catch (Exception e) {
            System.err.println("ERROR: " + e);
        }
    }

    /**
     * Type check a GoLite program.
     *
     * @param inPath - Filepath to GoLite program
     */
    public static boolean typeCheck(String inPath) throws IOException {
        try {
            Lexer lexer = new GoLiteLexer(new PushbackReader(new FileReader(inPath), 1024));
            Parser parser = new Parser(lexer);
            Weeder weeder = new Weeder();

            Start ast = parser.parse();
            ast.apply(weeder);

            TypeChecker typeChecker = new TypeChecker();
            ast.apply(typeChecker);
        } catch (LexerException|ParserException|SymbolTableException|WeederException|TypeCheckException e) {
            System.err.println("ERROR: " + e);               
            return false;
        }

        return true;
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

    /**
     * Dumps the symbol table for a GoLite program. Given an input file of the form 'foo.go', the
     * method writes these results to 'foo.symtab'.
     *
     * @param inPath - Filepath to GoLite program
     */
    public static void dumpSymbolTable(String inPath) throws IOException {
        try {
            Lexer lexer = new GoLiteLexer(new PushbackReader(new FileReader(inPath), 1024));
            Parser parser = new Parser(lexer);
            Weeder weeder = new Weeder();

            Start ast = parser.parse();
            ast.apply(weeder);

            TypeChecker typeChecker = new TypeChecker();
            ast.apply(typeChecker);

            dump(typeChecker.getSymbolTable().getLog(), inPath, ".symtab");
        } catch (LexerException|ParserException|WeederException|SymbolTableException|TypeCheckException e) {
            System.err.println("ERROR: " + e);
            System.exit(-1);
        }
    }

    /**
     * Dumps the data dervied from the given input file of the form 'foo.go', to a file in the
     * current folder with the same name but specified extension.
     *
     * @param data - Data to dump
     * @param inPath - Filepath to input
     * @param ext - Output extension
     */
    private static void dump(String data, String inPath, String ext) throws IOException {
        String filename = new File(inPath).getName();
        String name = filename.substring(0, filename.indexOf('.'));
        PrintWriter out = new PrintWriter(new FileWriter(name + ext));
        out.print(data);
        out.close();
    }

}
