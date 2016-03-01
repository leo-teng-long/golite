"""
TODO
"""

import os
import re
import sys


# Path to programs directory.
PROGS_DIRPATH = os.path.join("..", "programs")

# Path to valid programs directory.
VALID_PROGS_DIRPATH = os.path.join(PROGS_DIRPATH, "valid")
# Path to invalid programs directory (Only syntax for now).
INVALID_PROGS_DIRPATH = os.path.join(PROGS_DIRPATH, os.path.join("invalid",
	"syntax"))


# Filepath to parser test class template.
TEST_CLASS_TEMPALTE_FPATH = os.path.join("build_tests",
	"GoLiteParserTestTemplate.java")

# Filepath to test suite class template.
SUITE_TEMPALTE_FPATH = os.path.join("build_tests",
	"GoLiteTestSuiteTemplate.java")


# Assert method names.
ASSERT_TRUE = "assertTrue"
ASSERT_FALSE = "assertFalse"


# Test directory path.
OUT_TEST_DIRPATH = "test"
# Output test suite source filepath.
OUT_SUITE_FPATH = os.path.join(OUT_TEST_DIRPATH, "GoLiteTestSuite.java")

# Output name for test checking valid syntax is correctly parsed.
OUT_VALID_TEST_NAME = "GoLiteValidSyntaxTest"
# Output name for test checking invalid syntax is not parsed.
OUT_INVALID_TEST_NAME = "GoLiteInvalidSyntaxTest"


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

	if '3d' in test_name:
		test_name = test_name.replace('3d', 'ThreeDim')

	return test_name


def create_test_method_str(prog_fname, prog_fpath, assert_true):
	"""
	Creates the source string for a test method from a corresponding test
	program.

	@param prog_fname - Input program filename
	@param prog_fpath - Filepath to program
	@param assert_true - If true, then assertion must be true, otherwise it must
		be false
	@return Corresponding test method source
	"""

	test_name = to_test_name(prog_fname)

	assert_method_name = ASSERT_TRUE if assert_true else ASSERT_FALSE

	test_method_str = "\t@Test\n"
	test_method_str += "\tpublic void %s() throws IOException {\n" % test_name
	test_method_str += "\t\t%s(parse(\"%s\"));\n" % \
		(assert_method_name, prog_fpath)
	test_method_str += "\t}"

	return test_method_str


def create_test(test_name, progs_dirpath, assert_true, out_path):
	"""
	Creates the source string for a test and saves it to file.

	@param test_name - Test name (Becomes the class name of the test)
	@param progs_dirpath - Directory path to input test programs folder
	@param assert_true - If true, then assertions must be true, otherwise they
		must be false
	@param out_path - Output file to test source file
	"""

	# List of test method strings.
	test_method_strs = []

	# Create a test method for each valid program.
	for parent, subdirs, fnames in os.walk(progs_dirpath):
		for fname in fnames:
			if not fname.endswith('.go'):
				continue

			test_prog_path = os.path.join(parent, fname)

			test_method_strs.append(create_test_method_str(fname,
				test_prog_path, assert_true))

	# Read the test template source.
	with open(TEST_CLASS_TEMPALTE_FPATH) as fin:
		test_str = fin.read()

	# Insert the test name.
	test_str = test_str.replace("/* INSERT NAME HERE */", test_name)
	# Insert the test methods.
	test_str = test_str.replace("/* INSERT TESTS HERE */",
		'\n\n'.join(test_method_strs))

	# Create output test directory if it doesn't already exist.
	if not os.path.exists(OUT_TEST_DIRPATH):
		os.makedirs(OUT_TEST_DIRPATH)

	# Output test source.
	with open(out_path, 'w') as fout:
		fout.write(test_str)


def main():
	# List of test method strings.
	test_method_strs = []

	# Create the parser test for syntactically valid programs.
	create_test(OUT_VALID_TEST_NAME, VALID_PROGS_DIRPATH, True,
		os.path.join(OUT_TEST_DIRPATH, '%s.java' % OUT_VALID_TEST_NAME))

	# Create the parser test for syntactically invalid programs.
	create_test(OUT_INVALID_TEST_NAME, INVALID_PROGS_DIRPATH, False,
		os.path.join(OUT_TEST_DIRPATH, '%s.java' % OUT_INVALID_TEST_NAME))

	# Read in the test suite template.
	with open(SUITE_TEMPALTE_FPATH) as fin:
		suite_str = fin.read()

	# Insert the test names into the test suite runner class.
	suite_str = suite_str.replace("/* INSERT TEST CLASSES HERE */",
		"%s.class,\n\t%s.class" % (OUT_VALID_TEST_NAME, OUT_INVALID_TEST_NAME))

	# Save the test suite source to file.
	with open(OUT_SUITE_FPATH, 'w') as fout:
		fout.write(suite_str)


if __name__ == '__main__':
	main()
