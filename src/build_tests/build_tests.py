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


# Filepath to test class template.
TEST_CLASS_TEMPALTE_FPATH = os.path.join("build_tests",
	"GoLiteTest.template.java")
# Filepath to test suite class template.
SUITE_TEMPALTE_FPATH = os.path.join("build_tests",
	"GoLiteTestSuite.template.java")


# Assert method names.
ASSERT_TRUE = "assertTrue"
ASSERT_FALSE = "assertFalse"


# Test directory path.
OUT_TEST_DIRPATH = "test"
# Output test suite source filepath.
OUT_SUITE_FPATH = os.path.join(OUT_TEST_DIRPATH, "GoLiteTestSuite.java")


# Capitalizes a given string.
def capitalize(in_str):
	if len(in_str) == 0:
		return in_str
	elif len(in_str) == 1:
		return in_str[0].upper()
	else:
		return in_str[0].upper() + in_str[1:]


# Uncapitalizes a given string.
def uncapitalize(in_str):
	if len(in_str) == 0:
		return in_str
	elif len(in_str) == 1:
		return in_str[0].lower()
	else:
		return in_str[0].lower() + in_str[1:]


# Transforms a lowercase alphanumeric string split on hyphens and underscores to
# camel case.
def to_camel_case(in_str):
	return uncapitalize(''.join(map(capitalize, re.findall(r'[0-9a-zA-Z]+',
		in_str))))


# Takes a test program filename and transforms it into a test method name.
def to_test_name(prog_fname):
	test_name = to_camel_case(prog_fname[:-3]) + "Test"

	if '2d' in test_name:
		test_name = test_name.replace('2d', 'TwoDim')

	if '3d' in test_name:
		test_name = test_name.replace('3d', 'ThreeDim')

	return test_name


# Creates the source string for a test method for a test program, with given
# filename and filepath.
# TODO: Document
def create_test_method_str(prog_fname, prog_fpath, assert_true):
	test_name = to_test_name(prog_fname)

	assert_method_name = ASSERT_TRUE if assert_true else ASSERT_FALSE

	test_method_str = "\t@Test\n"
	test_method_str += "\tpublic void %s() throws IOException {\n" % test_name
	test_method_str += "\t\t%s(parse(\"%s\"));\n" % \
		(assert_method_name, prog_fpath)
	test_method_str += "\t}"

	return test_method_str


# Creates the source string for a test, with given test name and test programs
# located in the given directory.
# TODO: Document.
def create_test_str(test_name, progs_dirpath, assert_true, out_path):
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

	test_str = test_str.replace("/* INSERT NAME HERE */", test_name)
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

	create_test_str('GoLiteValidTest', VALID_PROGS_DIRPATH, True,
		os.path.join(OUT_TEST_DIRPATH, 'GoLiteValidTest.java'))

	create_test_str('GoLiteInvalidTest', INVALID_PROGS_DIRPATH, False,
		os.path.join(OUT_TEST_DIRPATH, 'GoLiteInvalidTest.java'))

	with open(SUITE_TEMPALTE_FPATH) as fin:
		suite_str = fin.read()

	suite_str = suite_str.replace("/* INSERT TEST CLASSES HERE */",
		"GoLiteValidTest.class,\n\tGoLiteInvalidTest.class")

	with open(OUT_SUITE_FPATH, 'w') as fout:
		fout.write(suite_str)


if __name__ == '__main__':
	main()
