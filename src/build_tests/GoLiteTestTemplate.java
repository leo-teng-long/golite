package test;

import golite.GoLiteLexer;
import golite.PrettyPrinter;
import golite.Weeder;
import golite.exception.SymbolTableException;
import golite.exception.TypeCheckException;
import golite.exception.WeederException;
import golite.generator.CodeGenerator;
import golite.symbol.SymbolTable;
import golite.symbol.SymbolTableBuilder;
import golite.type.TypeChecker;
import golite.lexer.*;
import golite.parser.*;
import golite.node.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.PushbackReader;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class <<<INSERT NAME HERE>>> {

	/**
     * Parses a GoLite program.
     *
     * @param inPath - Filepath to GoLite program to parse
     * @return True if the program type checks, otherwise an exception is thrown
     * @throws IOException if something goes wrong with the reading of the program
     * @throws Compiler-specific exceptions if a failure is encountered during compilation
     */
    private static boolean parse(String inPath)
        throws IOException, LexerException, ParserException {

        Lexer lexer = new GoLiteLexer(new PushbackReader(new FileReader(inPath), 1024));
        Parser p = new Parser(lexer);
        Weeder weeder = new Weeder();

        Start ast = p.parse();
        ast.apply(weeder);

        return true;
    }

    /**
     * Checks the pretty print invariant,
     *
     *    pretty(parse(program)) = pretty(parse(pretty(parse(program))))
     *
     * on a GoLite program.
     *
     * @param inPath - Filepath to GoLite program
     * @return True if the variant is passed, false otherwise
     * @throws IOException if something goes wrong with the reading of the program
     * @throws Compiler-specific exceptions if a failure is encountered during compilation
     */
    private static boolean checkPrettyInvariant(String inPath)
        throws IOException, LexerException, ParserException {

        Lexer lexer = new GoLiteLexer(new PushbackReader(new FileReader(inPath), 1024));
        Parser parser = new Parser(lexer);
        Weeder weeder = new Weeder();

        Start ast = parser.parse();
        ast.apply(weeder);

        PrettyPrinter pp = new PrettyPrinter();
        ast.apply(pp);

        String prettyPrint = pp.getPrettyPrint();

        lexer = new GoLiteLexer(new PushbackReader(new StringReader(prettyPrint), 1024));
        parser = new Parser(lexer);
        ast = parser.parse();

        pp = new PrettyPrinter();
        ast.apply(pp);

        String prettyPrintPrettyPrint = pp.getPrettyPrint();

        return prettyPrint.equals(prettyPrintPrettyPrint);
    }

    /**
     * Type check a GoLite program.
     *
     * @param inPath - Filepath to GoLite program
     * @return True if the program type checks, otherwise an exception is thrown
     * @throws IOException if something goes wrong with the reading of the program
     * @throws Compiler-specific exceptions if a failure is encountered during compilation
     */
    private static boolean typeCheck(String inPath)
        throws IOException, LexerException, ParserException {

        Lexer lexer = new GoLiteLexer(new PushbackReader(new FileReader(inPath), 1024));
        Parser parser = new Parser(lexer);
        Weeder weeder = new Weeder();

        Start ast = parser.parse();
        ast.apply(weeder);

        TypeChecker typeChecker = new TypeChecker();
        ast.apply(typeChecker);

        return true;
    }

    /**
     * Compile a GoLite program and generate the corresponding Python code to file.
     *
     * @param inPath - Filepath to GoLite program
     * @param outPath - Filepath to output Python program
     * @throws IOException
     * @throws Compiler-specific exceptions if a failure is encountered during compilation
     */
    private static void generateCode(String inPath, String outPath)
        throws IOException, LexerException, ParserException {

        Lexer lexer = new GoLiteLexer(new PushbackReader(new FileReader(inPath), 1024));
        Parser parser = new Parser(lexer);
        Weeder weeder = new Weeder();

        Start ast = parser.parse();
        ast.apply(weeder);

        TypeChecker typeChecker = new TypeChecker();
        ast.apply(typeChecker);

        CodeGenerator codeGenerator = new CodeGenerator(typeChecker.getTypeTable());
        ast.apply(codeGenerator);

        try (PrintWriter out = new PrintWriter(new FileWriter(outPath))) {
            out.print(codeGenerator.getGeneratedCode());
        }
    }

    /**
     * Run a phase of Vince's reference GoLite compiler on a GoLite program.
     *
     * @param path - Path to reference compiler
     * @param progPath - Path to the input GoLite program
     * @param option - Option string corresponding to the phase to run ('scan', 'parse', or
     *  'typecheck')
     * @return The output from stdout of the compiler if it's non-empty, otherwise the output from
     *  stderr
     * @throws IOException if something goes wrong with the reading of the program
     * @throws InterruptedException if something goes wrong in the running of reference compiler
     */
    private static String runReferenceCompiler(String path, String progPath, String option)
        throws IOException, InterruptedException {

        ProcessBuilder pb = new ProcessBuilder(path, option, progPath);
        Process p = pb.start();
        p.waitFor();

        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String out = reader.readLine();

        if (out == null) {
            reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            out = reader.readLine();
        }

        return out;
    } 
                              
<<<INSERT TESTS HERE>>>
	
}
