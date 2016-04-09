package golite.generator;

import golite.analysis.*;
import golite.node.*;
import java.util.*;
import java.io.*;

/**
 * GoLite Code Generator
 *
 */
public class CodeGenerator extends DepthFirstAdapter {

    /** Buffer storing generated python code */
    private StringBuffer buffer;
    /** Keep track of how many tabs need to be added */
    private int tabDepth;

    /**
     * CodeGenerator Constructor
     */
    public CodeGenerator() {
        this.buffer = new StringBuffer();
        this.tabDepth = 0;
    }

    /**
     * Return the generated python code as string
     *
     */
    public String getGeneratedCode() {
        return this.buffer.toString();
    }

    /**
     * Private methods
     *
     */
    private void addSpace() {
        this.buffer.append(' ');
    }

    private void addLine() {
        this.buffer.append('\n');
    }

    private void addTabs() {
        for (int i = 0; i < this.tabDepth; i++) {
            this.buffer.append('\t');
        }
    }

    private void addComma() {
        this.buffer.append(',');
    }

    private void addDot() {
        this.buffer.append('.');
    }

    private void addColon() {
        this.buffer.append(':');
    }

    private void addSemi() {
        this.buffer.append(';');
    }

    private void addLeftParen() {
        this.buffer.append('(');
    }

    private void addRightParen() {
        this.buffer.append(')');
    }

    private void addLeftBracket() {
        this.buffer.append('[');
    }

    private void addRightBracket() {
        this.buffer.append(']');
    }

    private void addLeftBrace() {
        this.buffer.append('{');
    }

    private void addRightBrace() {
        this.buffer.append('}');
    }

    private void enterCodeBlock() {
        addTabs();
        addLeftBrace();

        addLine();

        this.tabDepth++;
    }

    private void exitCodeBlock() {
        this.tabDepth--;

        addTabs();
        addRightBrace();

        addLine();
    }

    private void generateStatement(PStmt e) {
        addTabs();

        e.apply(this);
        addSemi();

        addLine();
    }

}
