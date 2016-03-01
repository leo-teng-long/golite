package test;

import golite.lexer.*;
import golite.parser.*;
import golite.node.*;
import golite.GoLiteLexer;
import golite.PrettyPrinter;

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
            p.parse();
        }
        catch (LexerException|ParserException e) {
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
            Start tree = parser.parse();

            PrettyPrinter printer = new PrettyPrinter();
            tree.apply(printer);

            String prettyParse = printer.getBufferString();

            lexer = new GoLiteLexer(new PushbackReader(new StringReader(prettyParse), 1024));
            parser = new Parser(lexer);
            tree = parser.parse();

            printer = new PrettyPrinter();
            tree.apply(printer);

            String prettyParsePrettyParse = printer.getBufferString();

            return prettyParse.equals(prettyParsePrettyParse);
        } catch (Exception e) {
            return false;
        }
    }

<<<INSERT TESTS HERE>>>
	
}
