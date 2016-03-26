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


public class <<<INSERT NAME HERE>>> {

	/**
     * Parses a GoLite program.
     *
     * @param inPath - Filepath to GoLite program to parse
     * @return True if the program passed parsing, false otherwise
     */
    private static boolean parse(String inPath) throws IOException {
        try {
            Lexer lexer = new GoLiteLexer(new PushbackReader(new FileReader(inPath), 1024));
            Parser p = new Parser(lexer);
            Weeder weeder = new Weeder();

            Start ast = p.parse();
            ast.apply(weeder);
        }
        catch (LexerException|ParserException|WeederException e) {
            return false;
        }

        return true;
    }

    /**
     * Checks the pretty print invariant,
     *
     *    pretty(parse(program)) = pretty(parse(pretty(parse(program))))
     *
     * on a GoLite program.
     *
     * @param inPath - Filepath to GoLite program to check
     * @return True if the program passed, false otherwise
     */
    private static boolean checkPrettyInvariant(String inPath) throws IOException {
        try {
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
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Type check a GoLite program.
     *
     * @param inPath - Filepath to GoLite program
     * @return True if the program passed type-checking, false otherwise
     */
    public static boolean typeCheck(String inPath) throws IOException {
        try {
            Lexer lexer = new GoLiteLexer(new PushbackReader(new FileReader(inPath), 1024));
            Parser parser = new Parser(lexer);
            Weeder weeder = new Weeder();

            Start ast = parser.parse();
            ast.apply(weeder);

            SymbolTableBuilder symbolTableBuilder = new SymbolTableBuilder();
            ast.apply(symbolTableBuilder);

            SymbolTable symbolTable = symbolTableBuilder.getTable();
            TypeChecker typeChecker = new TypeChecker(symbolTable);
            ast.apply(typeChecker);
        } catch (LexerException|ParserException|SymbolTableException|WeederException|TypeCheckException e) {             
            return false;
        }

        return true;
    }

<<<INSERT TESTS HERE>>>
	
}
