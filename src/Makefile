# Run the generated tests for the GoLite compiler.
run_tests: compile_tests
	-java -cp jars/hamcrest-core-1.3.jar:jars/junit-4.12.jar:jars/assertj-core-3.3.0.jar: org.junit.runner.JUnitCore test.GoLiteTestSuite > run_tests.log
	less run_tests.log
	rm -f run_tests.log

# Run the generated tests for Vince's reference GoLite compiler (must be run on a McGill teaching server, e.g. Mimi).
run_ref_tests: compile_ref_tests
	-java -cp jars/hamcrest-core-1.3.jar:jars/junit-4.12.jar:jars/assertj-core-3.3.0.jar: org.junit.runner.JUnitCore test.GoLiteTestSuite > run_tests.log
	less run_tests.log
	rm -f run_tests.log

# Compile the generated tests for the GoLite compiler.
compile_tests: build_tests
	javac -cp jars/junit-4.12.jar:jars/assertj-core-3.3.0.jar: test/*.java

# Compile the generated tests for Vince's reference GoLite compiler (must be run on a McGill teaching server, e.g. Mimi).
compile_ref_tests: build_ref_tests
	javac -cp jars/junit-4.12.jar:jars/assertj-core-3.3.0.jar: test/*.java

# Build the tests for the GoLite compiler.
build_tests: build
	python build_tests/build_tests.py

# Build the tests for Vince's reference GoLite compiler (must be run on a McGill teaching server, e.g. Mimi).
build_ref_tests: build
	python build_tests/build_tests.py -r

# Build the source.
build: compile_grammar
	javac -cp jars/commons-cli-1.3.1.jar: golite/*.java golite/lexer/*.java golite/parser/*.java golite/node/*.java golite/analysis/*.java golite/*/*.java

# Compile the SableCC 3 grammar.
compile_grammar: clean
	java -jar ./sablecc-3.7/lib/sablecc.jar golite.sablecc -d .

# Clean the source by removing automatically generated source and test files, and .class files.
clean:
	rm -rf golite/lexer golite/parser golite/node golite/analysis test/
	find . -name "*.class" -type f -delete
