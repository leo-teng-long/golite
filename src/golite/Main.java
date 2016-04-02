package golite;

import golite.exception.SymbolTableException;
import golite.exception.TypeCheckException;
import golite.exception.WeederException;
import golite.generator.CodeGenerator;
import golite.symbol.SymbolTable;
import golite.symbol.SymbolTableBuilder;
import golite.type.TypeChecker;
import golite.lexer.*;
import golite.node.*;
import golite.parser.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.PushbackReader;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;


/**
 * Main.
 */
class Main {

    // Valid/Invalid messages to print for phase checks on programs.
    private final static String VALID_MESSAGE = "VALID";
    private final static String INVALID_MESSAGE = "INVALID";

    public static void main(String args[]) {
        // Create the command-line parser.
        CommandLineParser parser = new PosixParser();

        // Container for the command-line option specifications.
        Options options = new Options();

        // Add command-line specifications for the various options.
        options.addOption("scan", false, "check the program passes scanning");
        options.addOption("tokens", false, "display the tokens in the program");
        options.addOption("parse", false, "check the program passes parsing");
        options.addOption("pretty", false, "pretty print the program to file");
        options.addOption("type", false, "check the program passes type checking");
        options.addOption("dumpsymtab", false, "dump the program symbol table to file");
        options.addOption("pptype", false, "typed pretty print the program to file");
        options.addOption("gen", false, "compile and generate Python code");

        options.addOption("ut", false, "allow top-level declarations to be unordered");
        options.addOption("help", false, "display help");

        CommandLine parsed = null;
        try {
            parsed = parser.parse(options, args, false);
        } catch (ParseException e) {
            System.err.println("Parsing failed. ERROR: " + e.getMessage());
        }

        // Throw an error if the number of arguments passed is off.
        if (args.length < 1 || args.length > 3) {
            printUsage();
            System.exit(-1);
        }

        // Make sure the last argument corresponds to a program that exists, otherwise throw an
        // error.
        String programPath = args[args.length - 1];
        if (!programPath.equals("-help") && !new File(programPath).exists()) {
            System.err.println("Parsing failed. ERROR: " + programPath + " does not exist ");
            printUsage();
            System.exit(-1);
        }

        // Flag for whether top declarations are allowed in any order or not.
        boolean ut = parsed.hasOption("ut");

        try {
            if (parsed.hasOption("scan")) {
                if (scan(programPath))
                    System.out.println(VALID_MESSAGE);
                else
                    System.out.println(INVALID_MESSAGE);
            } else if (parsed.hasOption("tokens"))
                displayTokens(programPath);
            else if (parsed.hasOption("parse")) {
                if (parse(programPath))
                    System.out.println(VALID_MESSAGE);
                else
                    System.out.println(INVALID_MESSAGE);
            } else if (parsed.hasOption("pretty"))
                prettyPrint(programPath);
            else if (parsed.hasOption("type")) {
                if (typeCheck(programPath, ut))
                    System.out.println(VALID_MESSAGE);
                else
                    System.out.println(INVALID_MESSAGE);
            } else if (parsed.hasOption("dumpsymtab"))
                dumpSymbolTable(programPath, ut);
            else if (parsed.hasOption("pptype"))
                typedPrettyPrint(programPath, ut);
            else if (parsed.hasOption("gen"))
                generateCode(programPath, ut);
            else if (parsed.hasOption("help"))
                new HelpFormatter().printHelp("GoLite Compiler", options);
            else {
                System.err.println("Parsing failed. ERROR: " + programPath + " does not exist ");
                printUsage();
                System.exit(-1);
            }
        } catch (IOException e) {
            System.err.println("IO ERROR: " + e.getMessage());
            System.exit(-1);
        }
    }

    /**
     * Prints the command-line usage to stderr.
     */
    private static void printUsage() {
        System.err.println("Usage: java golite.Main <scan | tokens | parse | pretty | type | "
            + "dumpsymtab | pptype | gen | help> FILENAME");
    }

    /**
     * Scans a GoLite program.
     *
     * @param inPath - Filepath to GoLite program to scan
     * @return True if the program passed scanning, false otherwise (If false, prints the error to
     *  stderr as well)
     * @throws IOException
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
     * Prints the scanned tokens in a GoLite program, one per line. The scanner token is printed
     * along with the underlying scanned text in brackets (except for TEol token).
     *
     * @param inPath - Filepath to GoLite program to scan
     * @throws IOException
     *
     * Consulted <a href="http://www.sable.mcgill.ca/~hendren/520/2016/tiny/sablecc-3/tiny/Main.java">
     * Main.java</a> of the Tiny language example on the course website.
     */
    private static void displayTokens(String inPath) throws IOException {
        try {
            Lexer lexer = new GoLiteLexer(new PushbackReader(new FileReader(inPath), 1024));

            while (!(lexer.peek() instanceof EOF)) {
                Token token = lexer.next();

                if (token instanceof TEol)
                    System.out.println(token.getClass().getSimpleName());
                else
                    System.out.println(token.getClass().getSimpleName() + " (" + token.getText()
                        + ")");
            }
        } catch (LexerException e) {
            System.err.println("ERROR: " + e);
        }
    }

    /**
     * Parses a GoLite program.
     *
     * @param inPath - Filepath to GoLite program to parse
     * @return True if the program passed parsing, false otherwise (If false, prints the error to
     *  stderr as well)
     * @throws IOException
     */
    private static boolean parse(String inPath) throws IOException {
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
     * Pretty print a GoLite program to file. Given an input file of the form 'foo.go', the method
     * writes these results to 'foo.pretty.go'.
     *
     * @param inPath - Filepath to GoLite program
     * @throws IOException
     */
    private static void prettyPrint(String inPath) throws IOException {
        try {
            Lexer lexer = new GoLiteLexer(new PushbackReader(new FileReader(inPath), 1024));
            Parser parser = new Parser(lexer);
            Weeder weeder = new Weeder();

            Start tree = parser.parse();
            tree.apply(weeder);

            PrettyPrinter pp = new PrettyPrinter();
            tree.apply(pp);

            dump(pp.getPrettyPrint(), inPath, ".pretty.go");
        } catch (Exception e) {
            System.err.println("ERROR: " + e);
        }
    }

    /**
     * Type check a GoLite program.
     *
     * @param inPath - Filepath to GoLite program
     * @param ut - Flag indicating whether top-declarations are allowed to be unordered
     * @return True if the program passes type checking, false otherwise (If false, prints the error to
     *  stderr as well)
     * @throws IOException
     */
    private static boolean typeCheck(String inPath, boolean ut) throws IOException {
        try {
            Lexer lexer = new GoLiteLexer(new PushbackReader(new FileReader(inPath), 1024));
            Parser parser = new Parser(lexer);
            Weeder weeder = new Weeder();

            Start ast = parser.parse();
            ast.apply(weeder);

            TypeChecker typeChecker = null;
            if (ut) {
                SymbolTableBuilder symbolTableBuilder = new SymbolTableBuilder();
                ast.apply(symbolTableBuilder);

                typeChecker = new TypeChecker(symbolTableBuilder.getTable());
            } else
                typeChecker = new TypeChecker();

            ast.apply(typeChecker);
        } catch (LexerException|ParserException|SymbolTableException|WeederException|TypeCheckException e) {
            System.err.println("ERROR: " + e);
            return false;
        }

        return true;
    }

    /**
     * Dumps the symbol table for a GoLite program. Given an input file of the form 'foo.go', the
     * method writes these results to 'foo.symtab'.
     *
     * @param inPath - Filepath to GoLite program
     * @param ut - Flag indicating whether top-declarations are allowed to be unordered.
     * @throws IOException
     */
    private static void dumpSymbolTable(String inPath, boolean ut) throws IOException {
        try {
            Lexer lexer = new GoLiteLexer(new PushbackReader(new FileReader(inPath), 1024));
            Parser parser = new Parser(lexer);
            Weeder weeder = new Weeder();

            Start ast = parser.parse();
            ast.apply(weeder);

            TypeChecker typeChecker = null;
            if (ut) {
                SymbolTableBuilder symbolTableBuilder = new SymbolTableBuilder();
                ast.apply(symbolTableBuilder);

                typeChecker = new TypeChecker(symbolTableBuilder.getTable());
            } else
                typeChecker = new TypeChecker();

            ast.apply(typeChecker);

            dump(typeChecker.getSymbolTable().getLog(), inPath, ".symtab");
        } catch (LexerException|ParserException|WeederException|SymbolTableException|TypeCheckException e) {
            System.err.println("ERROR: " + e);
            System.exit(-1);
        }
    }

   /**
     * Typed pretty print a GoLite program to file. Given an input file of the form 'foo.go', the
     * method writes these results to 'foo.pptype.go'.
     *
     * @param inPath - Filepath to GoLite program
     * @param ut - Flag indicating whether top-declarations are allowed to be unordered
     * @throws IOException
     */
    public static void typedPrettyPrint(String inPath, boolean ut) throws IOException {
        try {
            Lexer lexer = new GoLiteLexer(new PushbackReader(new FileReader(inPath), 1024));
            Parser parser = new Parser(lexer);
            Weeder weeder = new Weeder();

            Start ast = parser.parse();
            ast.apply(weeder);

            TypeChecker typeChecker = null;
            if (ut) {
                SymbolTableBuilder symbolTableBuilder = new SymbolTableBuilder();
                ast.apply(symbolTableBuilder);

                typeChecker = new TypeChecker(symbolTableBuilder.getTable());
            } else
                typeChecker = new TypeChecker();

            ast.apply(typeChecker);

            TypedPrettyPrinter tpp = new TypedPrettyPrinter(typeChecker.getTypeTable());
            ast.apply(tpp);

            dump(tpp.getPrettyPrint(), inPath, ".pptype.go");
        } catch (LexerException|ParserException|WeederException|SymbolTableException|TypeCheckException e) {
            System.err.println("ERROR: " + e);
            System.exit(-1);
        }
    }

    /**
     * Compile a GoLite program and generate the corresponding Python code to file. Given an input
     * file of the form 'foo.go', the method writes these results to 'foo.golite.py'.
     *
     * @param inPath - Filepath to GoLite program
     * @param ut - Flag indicating whether top-declarations are allowed to be unordered
     * @throws IOException
     */
    private static void generateCode(String inPath, boolean ut) throws IOException {
        try {
            Lexer lexer = new GoLiteLexer(new PushbackReader(new FileReader(inPath), 1024));
            Parser parser = new Parser(lexer);
            Weeder weeder = new Weeder();

            Start ast = parser.parse();
            ast.apply(weeder);

            TypeChecker typeChecker = null;
            if (ut) {
                SymbolTableBuilder symbolTableBuilder = new SymbolTableBuilder();
                ast.apply(symbolTableBuilder);

                typeChecker = new TypeChecker(symbolTableBuilder.getTable());
            } else
                typeChecker = new TypeChecker();

            ast.apply(typeChecker);

            CodeGenerator codeGenerator = new CodeGenerator();
            ast.apply(codeGenerator);

            dump(codeGenerator.getGeneratedCode(), inPath, ".golite.py");
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
