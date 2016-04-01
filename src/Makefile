run_tests: compile_tests
	-java -cp jars/hamcrest-core-1.3.jar:jars/junit-4.12.jar:jars/assertj-core-3.3.0.jar: org.junit.runner.JUnitCore test.GoLiteTestSuite > run_tests.log
	less run_tests.log
	rm -f run_tests.log .pretty.go

compile_tests: build_tests
	javac -cp jars/junit-4.12.jar:jars/assertj-core-3.3.0.jar: test/*.java

build_tests: build
	python build_tests/build_tests.py

build: compile_grammar
	javac -cp jars/commons-cli-1.3.1.jar: golite/*.java golite/lexer/*.java golite/parser/*.java golite/node/*.java golite/analysis/*.java golite/*/*.java

compile_grammar: clean
	java -jar ./sablecc-3.7/lib/sablecc.jar golite.sablecc -d .

clean:
	rm -rf golite/lexer golite/parser golite/node golite/analysis
	find . -name "*.class" -type f -delete
