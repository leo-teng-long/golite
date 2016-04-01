package test;

import golite.GoLiteLexer;
import golite.PrettyPrinter;
import golite.Weeder;
import golite.exception.SymbolTableException;
import golite.exception.TypeCheckException;
import golite.exception.WeederException;
import golite.symbol.SymbolTable;
import golite.symbol.SymbolTableBuilder;
import golite.type.TypeChecker;
import golite.lexer.*;
import golite.parser.*;
import golite.node.*;

import java.io.*;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
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
    private static boolean parse(String inPath) throws IOException, LexerException, ParserException {
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
    private static boolean checkPrettyInvariant(String inPath) throws IOException, LexerException, ParserException {
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
    public static boolean typeCheck(String inPath) throws IOException, LexerException, ParserException {
        Lexer lexer = new GoLiteLexer(new PushbackReader(new FileReader(inPath), 1024));
        Parser parser = new Parser(lexer);
        Weeder weeder = new Weeder();

        Start ast = parser.parse();
        ast.apply(weeder);

        TypeChecker typeChecker = new TypeChecker();
        ast.apply(typeChecker);

        return true;
    }

<<<INSERT TESTS HERE>>>
	
}
