package golite;

import golite.lexer.*;
import golite.parser.*;
import golite.node.*;
import golite.GoLiteLexer;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;
import org.junit.Test;


/**
 * GoLite test suite.
 */
public class GoLiteTest {

	public static final Path PROGRAMS_DIRPATH = Paths.get("..", "..", "programs");

	/**
     * Parses a GoLite program.
     *
     * @param inPath - Filepath to GoLite program to parse
     * @return True if the program passed parsing, false otherwise
     */
    public static boolean parse(String inPath) throws IOException {
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

	@Test
	public void underscoreIdTest() throws IOException {
		assertTrue(parse("../../programs/underscore_id.go"));
	}
}
