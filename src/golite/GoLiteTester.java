package golite;

import golite.parser.*;
import golite.lexer.*;
import golite.node.*;
import golite.PrettyPrinter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.*;
import java.util.ArrayList;


public class GoLiteTester {

	/** Path to programs directory. */
	public static final Path PROGRAMS_DIRPATH = Paths.get("..", "programs");

	/** Path to valid programs directory. */
	public static final Path VALID_PROGRAMS_DIRPATH = PROGRAMS_DIRPATH.resolve("valid");
	/** Path to invalid programs directory. */
	public static final Path INVALID_PROGRAMS_DIRPATH = PROGRAMS_DIRPATH.resolve("invalid");

	public static void main(String[] args) throws IOException {
		File programDir = new File(PROGRAMS_DIRPATH.toString());

		ArrayList<File> validPrograms = getPrograms(VALID_PROGRAMS_DIRPATH);
		ArrayList<File> invalidPrograms = getPrograms(INVALID_PROGRAMS_DIRPATH);
		
		System.out.println("Testing...");

		System.out.println("Parsing valid programs...");

		int validProgramParsePasses = 0;
		for (File p: validPrograms) {
			if (parse(p.getPath()))
				validProgramParsePasses++;
			else
				System.out.println("FAILED: " + p);
		}

		System.out.println("Passed " + validProgramParsePasses + " / " + validPrograms.size() +
			".");

		System.out.println("Parsing invalid programs...");

		int invalidProgramParsePasses = 0;
		for (File p: invalidPrograms) {
			if (!parse(p.getPath()))
				invalidProgramParsePasses++;
			else
				System.out.println("FAILED: " + p);
		}

		System.out.println("Passed " + invalidProgramParsePasses + " / " + invalidPrograms.size() +
			".");

		System.out.println("Checking pretty printing invariant on valid programs...");

		int validProgramPrettyPasses = 0;
		for (File p: validPrograms) {
			if (checkPrettyInvariant(p.getPath()))
				validProgramPrettyPasses++;
			else
				System.out.println("FAILED: " + p);
		}

		System.out.println("Passed " + validProgramPrettyPasses + " / " + validPrograms.size() +
			".");
	}

	/**
	 * Retrieves a list of all programs in the given directory.
	 *
	 * @param dirpath - Path to directory with programs
	 * @return List of program files
	 */
	private static ArrayList<File> getPrograms(Path dirpath) {
		ArrayList<File> programs = new ArrayList<File>();

		for (File f: new File(dirpath.toString()).listFiles()) {
			if (f.isFile() && f.getName().endsWith(".go"))
				programs.add(f);
			else if (f.isDirectory()) {
				for (File p: getPrograms(Paths.get(f.getPath())))
					programs.add(p);
			}
		}

		return programs;
	}

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

}