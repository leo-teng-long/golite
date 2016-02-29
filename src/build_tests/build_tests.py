"""
TODO
"""

import os
import re
import sys


# Path to programs directory.
PROGS_DIRPATH = os.path.join("..", "programs")

# Path to valid programs directory.
VALID_PROG_DIRPATH = os.path.join(PROGS_DIRPATH, "valid")
# Path to invalid programs directory.
INVALID_PROGS_DIRPATH = os.path.join(PROGS_DIRPATH, "invalid")


# Filepath to test template.
TEST_TEMPALTE_FPATH = os.path.join("build_tests", "GoLiteTest.template.java")


# Filepath to output test source file.
OUT_TEST_FPATH = os.path.join("test", "GoLiteTest.java")


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
		test_name = test_name.replace('2d', 'TwoD')

	if '3d' in test_name:
		test_name = test_name.replace('3d', 'ThreeD')

	return test_name


# Creates the source string for a test method for a test program, with given
# filename and filepath.
def create_test_method_str(prog_fname, prog_fpath):
	test_name = to_test_name(prog_fname)

	test_method_str = "\t@Test\n"
	test_method_str += "\tpublic void %s() throws IOException {\n" % test_name
	test_method_str += "\t\tassertTrue(parse(\"%s\"));\n" % prog_fpath
	test_method_str += "\t}"

	return test_method_str


def main():
	# List of test method strings.
	test_method_strs = []

	# Create a test method for each proram.
	for parent, subdirs, fnames in os.walk(VALID_PROG_DIRPATH):
		for fname in fnames:
			if not fname.endswith('.go'):
				continue

			test_prog_path = os.path.join(parent, fname)

			test_method_strs.append(create_test_method_str(fname,
				test_prog_path))

	# Read the test template source.
	with open(TEST_TEMPALTE_FPATH) as fin:
		test_str = fin.read()

	test_str = test_str.replace("/* INSERT TESTS HERE */", 
		'\n\n'.join(test_method_strs))

	with open(OUT_TEST_FPATH, 'w') as fout:
		fout.write(test_str)


if __name__ == '__main__':
	main()
