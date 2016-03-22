run_tests: compile_tests
	-java -cp jars/hamcrest-core-1.3.jar:jars/junit-4.12.jar: org.junit.runner.JUnitCore test.GoLiteTestSuite > run_tests.log
	less run_tests.log
	rm -f run_tests.log .pretty.go

compile_tests: build_tests
	javac -cp jars/junit-4.12.jar: test/*.java

build_tests: build
	python build_tests/build_tests.py

build: compile_grammar
	javac golite/*.java golite/lexer/*.java golite/parser/*.java golite/node/*.java golite/analysis/*.java golite/*/*.java

test: test_valid test_invalid

test_v: test_valid_v test_invalid_v

test_valid: test_parse_valid test_weed_valid test_type_valid

test_invalid: test_parse_invalid test_weed_invalid test_type_invalid

test_valid_v: test_parse_valid_v test_weed_valid_v test_type_valid_v

test_invalid_v: test_parse_invalid_v test_weed_invalid_v test_type_invalid_v

compile_grammar: clean
	java -jar ./sablecc-3.7/lib/sablecc.jar golite.sablecc -d .

test_parse_valid: build
	for f in ../programs/valid/parse/*.go; do echo $$f; java golite/Main -parse $$f; done
	for f in ../programs/valid/parse/*/*/*.go; do echo $$f; java golite/Main -parse $$f; done

test_parse_invalid: build
	for f in ../programs/invalid/parse/*.go; do echo $$f; java golite/Main -parse $$f; done
	for f in ../programs/invalid/parse/*/*/*.go; do echo $$f; java golite/Main -parse $$f; done

test_parse_valid_v: build
	for f in ../programs/valid/parse/*.go; do echo $$f; java golite/Main -parsev $$f; done
	for f in ../programs/valid/parse/*/*/*.go; do echo $$f; java golite/Main -parsev $$f; done

test_parse_invalid_v: build
	for f in ../programs/invalid/parse/*.go; do echo $$f; java golite/Main -parsev $$f; done
	for f in ../programs/invalid/parse/*/*/*.go; do echo $$f; java golite/Main -parsev $$f; done

test_parse: build test_parse_valid test_parse_invalid

test_parse_v: build test_parse_valid_v test_parse_invalid_v

test_weed_valid: build
	for f in ../programs/valid/weeding/*.go; do echo $$f; java golite/Main -weed $$f; done

test_weed_invalid: build
	for f in ../programs/invalid/weeding/*.go; do echo $$f; java golite/Main -weed $$f; done

test_weed_valid_v: build
	for f in ../programs/valid/weeding/*.go; do echo $$f; java golite/Main -weedv $$f; done

test_weed_invalid_v: build
	for f in ../programs/invalid/weeding/*.go; do echo $$f; java golite/Main -weedv $$f; done

test_weed: build test_weed_valid test_weed_invalid

test_weed_v: build test_weed_valid_v test_weed_invalid_v

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
	rm -rf golite/*.class golite/lexer golite/parser golite/node golite/analysis
