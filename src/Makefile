build: clean
	java -jar ../../../sablecc-3.7/lib/sablecc.jar ../grammar.sablecc -d .
	javac golite/*.java golite/lexer/*.java golite/parser/*.java golite/node/*.java golite/analysis/*.java

clean:
	rm -rf golite/*.class golite/lexer golite/parser golite/node golite/analysis
