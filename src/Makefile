run_tests: compile_tests
	-java -cp jars/hamcrest-core-1.3.jar:jars/junit-4.12.jar: org.junit.runner.JUnitCore test.GoLiteTestSuite
	rm -f .pretty.go

compile_tests: build_tests
	javac -cp jars/junit-4.12.jar: test/*.java

build_tests: build
	python build_tests/build_tests.py

build: compile_grammar
	javac golite/*.java golite/lexer/*.java golite/parser/*.java golite/node/*.java golite/analysis/*.java golite/*/*.java

compile_grammar: clean
	java -jar ./sablecc-3.7/lib/sablecc.jar golite.sablecc -d .

test_type_valid: build
	for f in ../programs/valid/type/*.go; do echo $$f; java golite/Main -symbol $$f; done

test_type_invalid: build
	for f in ../programs/invalid/type/*.go; do echo $$f; java golite/Main -symbol $$f; done

test_type_valid_v: build
	for f in ../programs/valid/type/*.go; do echo $$f; java golite/Main -symbolv $$f; done

test_type_invalid_v: build
	for f in ../programs/invalid/type/*.go; do echo $$f; java golite/Main -symbolv $$f; done

test_type: build test_type_valid test_type_invalid
	echo "done"

test_type_v: build test_type_valid_v test_type_invalid_v
	echo "done"

clean:
	rm -rf golite/*.class golite/lexer golite/parser golite/node golite/analysis
