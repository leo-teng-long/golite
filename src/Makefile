run: build
	./run.sh

run_tests: compile_tests
	java -cp jars/hamcrest-core-1.3.jar:jars/junit-4.12.jar: org.junit.runner.JUnitCore test.GoLiteTestSuite

compile_tests: build_tests
	javac -cp jars/junit-4.12.jar: test/*.java

build_tests: build
	python build_tests/build_tests.py

build: compile_grammar
	javac golite/*.java golite/lexer/*.java golite/parser/*.java golite/node/*.java golite/analysis/*.java

compile_grammar: clean
	java -jar ./sablecc-3.7/lib/sablecc.jar golite.sablecc -d .

buildsrc:
	rm -rf golite/*.class
	javac golite/*.java

clean:
	rm -rf golite/*.class golite/lexer golite/parser golite/node golite/analysis
