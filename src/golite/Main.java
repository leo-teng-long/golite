package golite;

import golite.type.*;
import golite.symbol.*;
import golite.parser.*;
import golite.lexer.*;
import golite.node.*;
import golite.exception.*;

import java.io.*;
import java.util.*;


/**
 * Main.
 */
class Main {
    /* Verbose flag. */
    private static boolean verbose;

    public static void main(String args[]) {
        verbose = false;

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
            } else if (args[0].equals("-parsev")) {
                verbose = true;
                if (parse(args[1]))
                    System.out.println("VALID");
                else
                    System.out.println("INVALID");
            } else if (args[0].equals("-pretty")) {
                prettyPrint(args[1]);
            } else if (args[0].equals("-pptype")) {
                typedPrettyPrint(args[1]);
            // Print scanner tokens to stdout.
            } else if (args[0].equals("-printTokens")) {
                displayTokens(args[1]);
            } else if (args[0].equals("-weed")) {
                weed(args[1]);
            } else if (args[0].equals("-weedv")) {
                verbose = true;
                weed(args[1]);
            } else if (args[0].equals("-type")) {
                type(args[1]);
            } else if (args[0].equals("-typev")) {
                verbose = true;
                type(args[1]);
            } else if (args[0].equals("-dumpsymtab")) {
                dumpSymbolTable(args[1]);
            } else {
                printUsage();
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    /**
     * Prints the command-line usage to stderr.
     */
    public static void printUsage() {
        System.err.println("Usage: Main -[scan | parse | pretty | pptype | printTokens | "
            + "dumpsymtab | weed | weedv | type | typev ] filename");
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
            GoLiteWeeder weeder = new GoLiteWeeder();

            Start ast = p.parse();
            ast.apply(weeder);
        }
        catch (LexerException|ParserException|GoLiteWeederException e) {
            if (verbose) {
                System.err.println("ERROR: " + e);
            }
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
            GoLiteWeeder weeder = new GoLiteWeeder();

            Start tree = parser.parse();
            tree.apply(weeder);

            PrettyPrinter pp = new PrettyPrinter();
            tree.apply(pp);

            String prettyPrint = pp.getPrettyPrint();

            String filename = new File(inPath).getName();
            String name = filename.substring(0, filename.indexOf('.'));
            PrintWriter out = new PrintWriter(new FileWriter(name + ".pretty.go"));
            out.print(prettyPrint);
            out.close();
        } catch (Exception e) {
            System.err.println("ERROR: " + e);
        }
    }

    /**
     * Type pretty print a GoLite program
     *
     * @param inPath - Filepath to GoLite program
     */
    public static void typedPrettyPrint(String inPath) throws IOException {
        try {
            Lexer lexer = new GoLiteLexer(new PushbackReader(new FileReader(inPath), 1024));
            Parser parser = new Parser(lexer);
            GoLiteWeeder weed = new GoLiteWeeder();
            Start start = parser.parse();
            start.apply(weed);
            SymbolTableBuilder symbolBuilder = new SymbolTableBuilder();
            start.apply(symbolBuilder);
            SymbolTable symbolTable = symbolBuilder.getSymbolTable();
            HashMap<Node, PTypeExpr> typeTable = symbolBuilder.getTypeTable();
            TypeChecker typeChecker = new TypeChecker(symbolTable, typeTable);
            start.apply(typeChecker);

            TypedPrettyPrinter typePrinter = new TypedPrettyPrinter(typeChecker.getTypeTable());
            start.apply(typePrinter);
            String prettyPrint = typePrinter.getPrettyPrint();
            String filename = new File(inPath).getName();
            String name = filename.substring(0, filename.indexOf('.'));
            PrintWriter out = new PrintWriter(new FileWriter(name + ".pptype.go"));
            out.print(prettyPrint);
            out.close();
            System.out.println(prettyPrint);
        } catch (Exception e) {
            System.err.println("ERROR: " + e);
            // TODO: Debug.
            e.printStackTrace();
        }
    }

    /**
     * Weed a GoLite program.
     * @param inPath - Filepath to GoLite program
     */
    public static boolean weed(String inPath) throws IOException {
        try {
            Lexer lexer = new GoLiteLexer(new PushbackReader(new FileReader(inPath), 1024));
            Parser parser = new Parser(lexer);
            GoLiteWeeder weed = new GoLiteWeeder();

            Start tree = parser.parse();
            tree.apply(weed);
            System.out.println("VALID");
        } catch (LexerException|ParserException|GoLiteWeederException e) {
            System.out.println("INVALID");
            if (verbose) {
                System.err.println("ERROR: " + e);
                e.printStackTrace();
            }
            return false;
        }
        return true;
    }

    /**
    * Typecheck a GoLite program.
    * @param inPath - Filepath to GoLite program
    */
    public static boolean type(String inPath) throws IOException {
        try {
            Lexer lexer = new GoLiteLexer(new PushbackReader(new FileReader(inPath), 1024));
            Parser parser = new Parser(lexer);
            GoLiteWeeder weed = new GoLiteWeeder();
            Start start = parser.parse();
            start.apply(weed);
            SymbolTableBuilder symbolBuilder = new SymbolTableBuilder();
            start.apply(symbolBuilder);
            SymbolTable symbolTable = symbolBuilder.getSymbolTable();
            HashMap<Node, PTypeExpr> typeTable = symbolBuilder.getTypeTable();
            TypeChecker typeChecker = new TypeChecker(symbolTable, typeTable);
            start.apply(typeChecker);
            System.out.println("VALID");
            if (verbose)
            {
                System.out.println("###SYMBOL TABLE:###");
                symbolTable.printSymbols();
                System.out.println("###TYPE TABLE:###");
                for (Node n: typeTable.keySet())
                {
                    System.out.println("Node: " + n + " Key: " + n.getClass() + " Value: " + typeTable.get(n).getClass());
                }
                System.out.println("\n\n\n");
            }
        } catch (LexerException|ParserException|SymbolException|GoLiteWeederException|TypeCheckException e) {
            System.out.println("INVALID");
            if (verbose) {
                System.err.println("ERROR: " + e);
                e.printStackTrace();
            }
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
    public static boolean dumpSymbolTable(String inPath) throws IOException {
        try {
            Lexer lexer = new GoLiteLexer(new PushbackReader(new FileReader(inPath), 1024));
            Parser parser = new Parser(lexer);
            GoLiteWeeder weeder = new GoLiteWeeder();

            Start start = parser.parse();
            start.apply(weeder);

            SymbolTableBuilder symbolBuilder = new SymbolTableBuilder();
            start.apply(symbolBuilder);

            SymbolTable symbolTable = symbolBuilder.getSymbolTable();

            dump(symbolTable.toPrettyString(), inPath, ".symtab");
        } catch (LexerException|ParserException|SymbolException|GoLiteWeederException|TypeCheckException e) {
            System.out.println("INVALID");
            if (verbose) {
                System.err.println("ERROR: " + e);
                e.printStackTrace();
            }
            return false;
        }
        return true;
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
