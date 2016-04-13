"""
Automatic test generator for the GoLite compiler.
"""

import argparse
import logging
import os
import re
import sys


# Configure logging.
logging.basicConfig(format="%(levelname)s: [%(asctime)s] %(message)s",
	level=logging.INFO)


# Path to Vince's reference compiler on the McGill teaching servers.
REF_COMPILER_PATH = "/home/course/cs520/golitec"


# Path to programs directory.
PROGS_DIRPATH = os.path.join("..", "programs")

# Path to valid programs directories.
VALID_PROGS_DIRPATH = os.path.join(PROGS_DIRPATH, "valid")
VALID_ACTUAL_PROGS_DIRPATH = os.path.join(VALID_PROGS_DIRPATH, "actual")
VALID_GENERAL_PROGS_DIRPATH = os.path.join(VALID_PROGS_DIRPATH, "general")
VALID_SYNTAX_PROGS_DIRPATH = os.path.join(VALID_PROGS_DIRPATH, "syntax")
VALID_TYPE_PROGS_DIRPATH = os.path.join(VALID_PROGS_DIRPATH, "type")
VALID_GEN_PROGS_DIRPATH = os.path.join(VALID_PROGS_DIRPATH, "gen")

# Path to invalid programs directories.
INVALID_PROGS_DIRPATH = os.path.join(PROGS_DIRPATH, "invalid")
INVALID_SYNTAX_PROGS_DIRPATH = os.path.join(INVALID_PROGS_DIRPATH, "syntax")
INVALID_TYPE_PROGS_DIRPATH = os.path.join(INVALID_PROGS_DIRPATH, "type")

# Directory name for tests from other groups.
OTHER_GROUPS_PROGS_DIRNAME = "other_groups"


# Filepath to test class template.
TEST_CLASS_TEMPALTE_FPATH = os.path.join("build_tests",
	"GoLiteTestTemplate.java")

# Filepath to test suite class template.
SUITE_TEMPALTE_FPATH = os.path.join("build_tests",
	"GoLiteTestSuiteTemplate.java")


# Test directory path.
OUT_TEST_DIRPATH = "test"
# Output test suite source filepath.
OUT_SUITE_FPATH = os.path.join(OUT_TEST_DIRPATH, "GoLiteTestSuite.java")

# Output name for test checking valid syntax is correctly parsed.
OUT_VALID_PARSE_TNAME = "GoLiteValidSyntaxTest"
# Output name for test checking invalid syntax is not parsed.
OUT_INVALID_PARSE_TNAME = "GoLiteInvalidSyntaxTest"
# Output name for test checking pretty printing.
OUT_PRETTY_TNAME = "GoLitePrettyPrintTest"
# Output name for test checking valid typing is correctly verified.
OUT_VALID_TYPE_TNAME = "GoLiteValidTypingTest"
# Output name for test checking invalid typing is correctly caught.
OUT_INVALID_TYPE_TNAME = "GoLiteInvalidTypingTest"
# Output name for test checking code generation.
OUT_GEN_TNAME = "GoLiteCodeGenerationTest"


def capitalize(in_str):
	"""
	Capitalize a string.

	@param in_str - Input string
	@return Capitalized version of string
	"""

	if len(in_str) == 0:
		return in_str
	elif len(in_str) == 1:
		return in_str[0].upper()
	else:
		return in_str[0].upper() + in_str[1:]


def uncapitalize(in_str):
	"""
	Uncapitalize a string.

	@param in_str - Input string
	@return Uncapitalized version of string
	"""

	if len(in_str) == 0:
		return in_str
	elif len(in_str) == 1:
		return in_str[0].lower()
	else:
		return in_str[0].lower() + in_str[1:]


def to_camel_case(in_str):
	"""
	Transforms a lowercase alphanumeric string split on hyphens and underscores
	to camel case.

	@param in_str - Input string
	@return Camel case version of string
	"""

	return uncapitalize(''.join(map(capitalize, re.findall(r'[0-9a-zA-Z]+',
		in_str))))


def to_test_name(prog_fname):
	"""
	Takes a test program filename and transforms it into a test method name.

	@param prog_fname - Input program filename
	@return Corresponding test method name
	"""

	test_name = to_camel_case(prog_fname[:-3]) + "Test"

	if '2d' in test_name:
		test_name = test_name.replace('2d', 'TwoDim')
	elif '3d' in test_name:
		test_name = test_name.replace('3d', 'ThreeDim')
	elif test_name[0].isdigit():
		test_name = '_' + test_name

	return test_name


def is_other_groups_test(prog_fpath):
	"""
	Checks if the test program is from another group.

	@param prog_fpath - Filepath to program
	@return True if the program corresponds to another group, false otherwise.
	"""

	return OTHER_GROUPS_PROGS_DIRNAME in os.path.split(prog_fpath)[0]


def create_test_method_str(prog_fname, prog_fpath, tpe, ref):
	"""
	Creates the source string for a test method from a corresponding test
	program.

	@param prog_fname - Input program filename
	@param prog_fpath - Filepath to program
	@param tpe - 'valid_parse' for testing the correct parsing of the program,
		'invalid_parse' for testing no parse is produced for the program,
		'pretty' for testing the pretty printer on the program, 'valid_type'
		for testing the correct type check of the program, 'invalid_type' for
		testing type check flags the program, or 'gen' for testing the correct
		output of generated Python code after compilation
	@param ref - If True, then the test method is built for the reference
		compiler, otherwise it's build for the GoLite compiler
	@return Corresponding test method source
	"""

	# Create the method body for a test asserting the truth of a boolean method
	# acting on the given filepath to a program. 
	def create_assert_true_method_body(check_method_name, prog_fpath, tabs):
		return ('\t' * tabs) + "assertTrue(%s(\"%s\"));" % \
			(check_method_name, prog_fpath)

	# Create the method body for a test asserting the throwing of a list of
	# exceptions from a given method acting on the given filepath to a program.
	def create_assert_exception_method_body(check_method_name, exceptions,
		prog_fpath, tabs):

		if len(exceptions) == 1:
			body = ('\t' * tabs)
			body += "assertThatThrownBy(() -> "
			body += "{ %s(\"%s\"); }).isInstanceOf(%s.class);" \
				% (check_method_name, prog_fpath, exceptions[0])
		else:
			body = ('\t' * tabs) + "try {\n"
			
			body += ('\t' * (tabs + 1))
			body += "assertThatThrownBy(() -> "
			body += "{ %s(\"%s\"); }).isInstanceOf(%s.class);\n" \
				% (check_method_name, prog_fpath, exceptions[0])

			body += ('\t' * tabs) + "} catch (AssertionError e%d) {\n" \
				% (len(exceptions) - 1)
			body += create_assert_exception_method_body(check_method_name,
				exceptions[1:], prog_fpath, tabs + 1) + "\n"
			body += ('\t' * tabs) + "}"

		return body

	# Create the method body for a code generator test, asserting the generated
	# code produces output that's equal to the expected output for a program
	# with given filepath.
	def create_gen_method_body(prog_fpath, tabs):
		# Path to the program's corresponding .out file with the expected
		# output.
		prog_out_fpath = os.path.splitext(prog_fpath)[0] + ".out"

		# Temporary file for generated Python code.
		gen_prog_fname = ".tmp.golite.py"
		gen_prog_path = os.path.join(OUT_TEST_DIRPATH, gen_prog_fname)

		body = '\t' * tabs
		body += "File genProgF = new File(\"%s\");\n" % gen_prog_path
		body += '\t' * tabs
		body += "BufferedReader r = null;\n"

		body += ('\t' * tabs) + "try {\n"
		
		body += '\t' * (tabs + 1)
		body += "generateCode(\"%s\", \"%s\");\n\n" % (prog_fpath,
			gen_prog_path)

		body += '\t' * (tabs + 1)
		body += "ProcessBuilder pb "
		body += "= new ProcessBuilder(\"python\", \"%s\");\n" % (gen_prog_path)
		body += '\t' * (tabs + 1)
		body += "Process p = pb.start();\n"
		body += '\t' * (tabs + 1)
		body += "p.waitFor();\n\n"

		body += '\t' * (tabs + 1)
		body += "String gen = \"\";\n\n"

		body += '\t' * (tabs + 1)
		body += "r = new "
		body += "BufferedReader(new InputStreamReader(p.getInputStream()));\n"
		body += '\t' * (tabs + 1)
		body += "String s;\n"
		body += '\t' * (tabs + 1)
		body += "while ((s = r.readLine()) != null) gen += s + \"\\n\";\n"
		body += '\t' * (tabs + 1)
		body += "r.close();\n\n"

		body += '\t' * (tabs + 1)
		body += "r = new "
		body += "BufferedReader(new InputStreamReader(p.getErrorStream()));\n"
		body += '\t' * (tabs + 1)
		body += "while ((s = r.readLine()) != null) gen += s + \"\\n\";\n"
		body += '\t' * (tabs + 1)
		body += "r.close();\n\n"

		body += '\t' * (tabs + 1)
		body += "File exOutF = new File(\"%s\");\n\n" % prog_out_fpath

		body += '\t' * (tabs + 1)
		body += "r = new BufferedReader(new FileReader(exOutF));\n"
		body += '\t' * (tabs + 1)
		body += "String ex = \"\";\n"
		body += '\t' * (tabs + 1)
		body += "while ((s = r.readLine()) != null) ex += s + \"\\n\";\n\n"

		body += '\t' * (tabs + 1)
		body += "assertEquals(ex, gen);\n"

		body += ('\t' * tabs) + "} finally {\n"
		body += '\t' * (tabs + 1)
		body += "if (r != null) r.close();\n"
		body += '\t' * (tabs + 1)
		body += "genProgF.delete();\n"
		body += ('\t' * tabs) + "}"
		
		return body

	# Create the method body for a reference compiler test asserting either the
	# equality or inequality of the compiler's response to the success response
	# for the given option (phase), for the program with given filepath.
	def create_ref_method_body(option, prog_fpath, equals, tabs):
		body = '\t' * tabs
		body += "assertEquals" if equals else "assertNotEquals"
		body += "(\"OK\", runReferenceCompiler(\"%s\", \"%s\", \"%s\"));" \
			% (REF_COMPILER_PATH, prog_fpath, option)

		return body

	test_name = to_test_name(prog_fname)

	if is_other_groups_test(prog_fpath):
		test_name = "Group" + os.path.basename(os.path.dirname(prog_fpath)) + \
			capitalize(test_name)

	if tpe == 'valid_parse' and ref:
		method_body = create_ref_method_body("parse", prog_fpath, True, 2)
	elif tpe == 'valid_parse':
		method_body = create_assert_true_method_body("parse", prog_fpath, 2)
	elif tpe == 'invalid_parse' and ref:
		method_body = create_ref_method_body("parse", prog_fpath, False, 2)
	elif tpe == 'invalid_parse':
		method_body = create_assert_exception_method_body("parse",
			["LexerException", "ParserException", "WeederException"],
			prog_fpath, 2)
	elif tpe == 'pretty':
		method_body = create_assert_true_method_body("checkPrettyInvariant",
			prog_fpath, 2)
	elif tpe == 'valid_type' and ref:
		method_body = create_ref_method_body("typecheck", prog_fpath, True, 2)
	elif tpe == 'valid_type':
		method_body = create_assert_true_method_body("typeCheck", prog_fpath, 2)
	elif tpe == 'invalid_type' and ref:
		method_body = create_ref_method_body("typecheck", prog_fpath, False, 2)
	elif tpe == 'invalid_type':
		method_body = create_assert_exception_method_body("typeCheck",
			["SymbolTableException", "TypeCheckException"], prog_fpath, 2)
	elif tpe == 'gen':
		method_body = create_gen_method_body(prog_fpath, 2)
	else:
		raise ValueError("'tpe' argument must be 'valid_parse', "
			"'invalid_parse', 'pretty', 'valid_type', or 'invalid_type'.")

	test_method_str = "\t@Test\n"
	test_method_str += "\tpublic void %s() " % test_name
	if ref or tpe == 'gen':
		test_method_str += "throws IOException, InterruptedException, "
		test_method_str += "LexerException, ParserException {\n"
	else:
		test_method_str += "throws IOException, LexerException, "
		test_method_str += "ParserException {\n"
	test_method_str += "%s\n" % method_body
	test_method_str += "\t}"

	return test_method_str


def to_template_marker(in_str):
	"""
	Returns the given string as a template insertion point.

	@param in_str - Input string
	@return String as a template insertion point.
	"""

	return "<<<" + in_str + ">>>"


def create_test(test_name, progs_dirpaths, tpe, ref, test_ignore_path,
	out_path):
	"""
	Creates the source string for a test and saves it to file.

	@param test_name - Test name (Becomes the class name of the test)
	@param progs_dirpaths - Directory paths to input test programs folders
	@param tpe - 'valid_parse' for testing the correct parsing of the program,
		'invalid_parse' for testing no parse is produced for the program,
		'pretty' for testing the pretty printer on the program, 'valid_type'
		for testing the correct type check of the program, 'invalid_type' for
		testing type check flags the program, or 'gen' for testing the correct
		output of generated Python code after compilation
	@param ref - If True, then the test is built for the reference compiler,
		otherwise it's build for the GoLite compiler
	@param test_ignore_path - Filepath to test ignore file, listing filepaths to
		tests to ignore.
	@param out_path - Output file to test source file
	"""

	# Load filepaths to tests to ignore, if the test ignore file exists.
	if test_ignore_path and os.path.exists(test_ignore_path):
		logging.info("Reading test ignore file from %s..." % test_ignore_path)
		with open(test_ignore_path) as fin:
			tests_to_ignore = set([l.strip() for l in fin
				if not l.strip() == "" and not l.startswith('#')])
	else:
		if not test_ignore_path:
			logging.info("No test ignore file.")
		else:
			logging.info("No test ignore file at %s." % test_ignore_path)
		tests_to_ignore = set()

	# List of test method strings.
	test_method_strs = []

	# Create a test method for each program.
	for progs_dirpath in progs_dirpaths:
		for parent, subdirs, fnames in os.walk(progs_dirpath):
			for fname in fnames:
				if not fname.endswith('.go'):
					continue

				test_prog_path = os.path.join(parent, fname)

				if not test_prog_path in tests_to_ignore:
					test_method_strs.append(create_test_method_str(fname,
						test_prog_path, tpe, ref))

	# Read the test template source.
	with open(TEST_CLASS_TEMPALTE_FPATH) as fin:
		test_str = fin.read()

	# Insert the test name.
	test_str = test_str.replace(to_template_marker("INSERT NAME HERE"),
		test_name)
	# Insert the test methods.
	test_str = test_str.replace(to_template_marker("INSERT TESTS HERE"),
		'\n\n'.join(test_method_strs))

	# Create output test directory if it doesn't already exist.
	if not os.path.exists(OUT_TEST_DIRPATH):
		os.makedirs(OUT_TEST_DIRPATH)

	# Output test source.
	with open(out_path, 'w') as fout:
		fout.write(test_str)


def main():
	parser_description = ("Generates test automatically for the GoLite "
		"compiler or Vince's reference compiler.")
	parser = argparse.ArgumentParser(description=parser_description)

	parser.add_argument('-i', '--ignore', dest='ignore_path',
		help="Path to test ignore file")
	
	parser.add_argument('-r', '--ref', dest='ref', action='store_true',
		help="Generate tests for Vince's reference compiler")

	args = parser.parse_args()

	# Check that tests are being built on one of the McGill teaching servers,
	# where the reference compiler exists.
	if args.ref and not os.path.exists(REF_COMPILER_PATH):
		sys.stderr.write("ERROR: Reference compiler doesn't exist at %s (Make "
			"sure tests are being built on one of McGill's teaching servers "
			"(e.g. Mimi))\n" % REF_COMPILER_PATH)
		sys.exit(-1)

	# List of test method strings.
	test_method_strs = []

	valid_syntax_progs_dirpaths = [VALID_ACTUAL_PROGS_DIRPATH,
		VALID_GENERAL_PROGS_DIRPATH, VALID_SYNTAX_PROGS_DIRPATH]

	valid_type_progs_dirpaths = [VALID_ACTUAL_PROGS_DIRPATH,
		VALID_GENERAL_PROGS_DIRPATH, VALID_TYPE_PROGS_DIRPATH]

	# Create the parser test for syntactically valid programs.
	logging.info("Creating parser test for syntactically valid programs...")
	create_test(OUT_VALID_PARSE_TNAME, valid_syntax_progs_dirpaths,
		'valid_parse', args.ref, args.ignore_path,
		os.path.join(OUT_TEST_DIRPATH, '%s.java' % OUT_VALID_PARSE_TNAME))

	# Create the parser test for syntactically invalid programs.
	logging.info("Creating parser test for syntactically invalid programs...")
	create_test(OUT_INVALID_PARSE_TNAME, [INVALID_SYNTAX_PROGS_DIRPATH],
		'invalid_parse', args.ref, args.ignore_path,
			os.path.join(OUT_TEST_DIRPATH, '%s.java' % OUT_INVALID_PARSE_TNAME))

	# Create the pretty printer test (but not for the reference compiler).
	if not args.ref:
		logging.info("Creating pretty printer tests...")
		create_test(OUT_PRETTY_TNAME, valid_syntax_progs_dirpaths, 'pretty',
			False, args.ignore_path, os.path.join(OUT_TEST_DIRPATH,
				'%s.java' % OUT_PRETTY_TNAME))

	# Create the type checker test for correctly-typed programs.
	logging.info("Creating type checker test for correctly-typed programs....")
	create_test(OUT_VALID_TYPE_TNAME, valid_type_progs_dirpaths, 'valid_type',
		args.ref, args.ignore_path, os.path.join(OUT_TEST_DIRPATH,
			'%s.java' % OUT_VALID_TYPE_TNAME))

	# Create the type checker test for incorrectly-typed programs.
	logging.info("Creating type checker test for incorrectly-typed "
		"programs....")
	create_test(OUT_INVALID_TYPE_TNAME, [INVALID_TYPE_PROGS_DIRPATH],
		'invalid_type', args.ref, args.ignore_path,
			os.path.join(OUT_TEST_DIRPATH, '%s.java' % OUT_INVALID_TYPE_TNAME))

	# Create the code generator test (but not for the reference compiler).
	if not args.ref:
		logging.info("Creating code generator tests...")
		create_test(OUT_GEN_TNAME,
			[VALID_ACTUAL_PROGS_DIRPATH, VALID_GEN_PROGS_DIRPATH], 'gen', False,
			args.ignore_path, os.path.join(OUT_TEST_DIRPATH,
				'%s.java' % OUT_GEN_TNAME))

	# Read in the test suite template.
	with open(SUITE_TEMPALTE_FPATH) as fin:
		suite_str = fin.read()

	# Insert the test names into the test suite runner class.
	if args.ref:
		suite_str = suite_str.replace(
			to_template_marker("INSERT TEST CLASSES HERE"),
			"%s.class,\n\t%s.class,\n\t%s.class,\n\t%s.class" %
				(OUT_VALID_PARSE_TNAME, OUT_INVALID_PARSE_TNAME,
					OUT_VALID_TYPE_TNAME, OUT_INVALID_TYPE_TNAME))
	else:
		suite_str = suite_str.replace(
			to_template_marker("INSERT TEST CLASSES HERE"),
			"%s.class,\n\t%s.class,\n\t%s.class,\n\t%s.class,\n\t%s.class,\n\t%s.class" %
				(OUT_VALID_PARSE_TNAME, OUT_INVALID_PARSE_TNAME,
					OUT_PRETTY_TNAME, OUT_VALID_TYPE_TNAME,
					OUT_INVALID_TYPE_TNAME, OUT_GEN_TNAME))

	# Save the test suite source to file.
	with open(OUT_SUITE_FPATH, 'w') as fout:
		fout.write(suite_str)


if __name__ == '__main__':
	main()
