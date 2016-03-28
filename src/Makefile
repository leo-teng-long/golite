run_tests: compile_tests
	-java -cp jars/hamcrest-core-1.3.jar:jars/junit-4.12.jar:jars/assertj-core-3.3.0.jar: org.junit.runner.JUnitCore test.GoLiteTestSuite > run_tests.log
	less run_tests.log
	rm -f run_tests.log .pretty.go

compile_tests: build_tests
	javac -cp jars/junit-4.12.jar:jars/assertj-core-3.3.0.jar: test/*.java

build_tests: build
	python build_tests/build_tests.py

build: compile_grammar
	javac golite/*.java golite/lexer/*.java golite/parser/*.java golite/node/*.java golite/analysis/*.java golite/*/*.java

compile_grammar: clean
	java -jar ./sablecc-3.7/lib/sablecc.jar golite.sablecc -d .

test_type_valid: build
	for f in ../programs/valid/type/*.go; do echo $$f; java golite/Main -type $$f; done

test_type_invalid: build
	for f in ../programs/invalid/type/*.go; do echo $$f; java golite/Main -type $$f; done

test_type_valid_v: build
	for f in ../programs/valid/type/*.go; do echo $$f; java golite/Main -typev $$f; done

test_type_invalid_v: build
	for f in ../programs/invalid/type/*.go; do echo $$f; java golite/Main -typev $$f; done

test_type: build test_type_valid test_type_invalid

test_type_v: build test_type_valid_v test_type_invalid_v

clean:
	rm -rf golite/lexer golite/parser golite/node golite/analysis
	find . -name "*.class" -type f -delete
