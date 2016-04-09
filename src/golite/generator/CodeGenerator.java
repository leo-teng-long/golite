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
     * Code generation - empty expressions
     *
     */
    @Override
    public void caseAEmptyExpr(AEmptyExpr node) {
        this.inAEmptyExpr(node);

        // do nothing;

        this.outAEmptyExpr(node);
    }

    /**
     * Code generation - binary operators
     *      '+', '-', '*', '/', '%'
     *
     */
    @Override
    public void caseAAddExpr(AAddExpr node) {
        this.inAAddExpr(node);

        addLeftParen();

        if (node.getLeft() != null) {
            node.getLeft().apply(this);
        }

        this.buffer.append(" + ");

        if (node.getRight() != null) {
            node.getRight().apply(this);
        }

        addRightParen();

        this.outAAddExpr(node);

        /** TODO: modify to handle string */
    }

    @Override
    public void caseASubtractExpr(ASubtractExpr node) {
        this.inASubtractExpr(node);

        addLeftParen();

        if (node.getLeft() != null) {
            node.getLeft().apply(this);
        }

        this.buffer.append(" - ");

        if (node.getRight() != null) {
            node.getRight().apply(this);
        }

        addRightParen();

        this.outASubtractExpr(node);
    }

    @Override
    public void caseAMultExpr(AMultExpr node) {
        this.inAMultExpr(node);

        addLeftParen();

        if (node.getLeft() != null) {
            node.getLeft().apply(this);
        }

        this.buffer.append(" * ");

        if (node.getRight() != null) {
            node.getRight().apply(this);
        }

        addRightParen();

        this.outAMultExpr(node);
    }

    @Override
    public void caseADivExpr(ADivExpr node) {
        this.inADivExpr(node);

        addLeftParen();

        if (node.getLeft() != null) {
            node.getLeft().apply(this);
        }

        this.buffer.append(" / ");

        if (node.getRight() != null) {
            node.getRight().apply(this);
        }

        addRightParen();

        this.outADivExpr(node);
    }

    @Override
    public void caseAModExpr(AModExpr node) {
        this.inAModExpr(node);

        addLeftParen();

        if (node.getLeft() != null) {
            node.getLeft().apply(this);
        }

        this.buffer.append(" % ");

        if (node.getRight() != null) {
            node.getRight().apply(this);
        }

        addRightParen();

        this.outAModExpr(node);
    }

    /**
     * Code generation - bit operators
     *      '&', '|', '^', '&^', '<<', '>>'
     *
     */
    @Override
    public void caseABitAndExpr(ABitAndExpr node) {
        this.inABitAndExpr(node);

        addLeftParen();

        if (node.getLeft() != null) {
            node.getLeft().apply(this);
        }

        this.buffer.append(" & ");

        if (node.getRight() != null) {
            node.getRight().apply(this);
        }

        addRightParen();

        this.outABitAndExpr(node);
    }

    @Override
    public void caseABitOrExpr(ABitOrExpr node) {
        this.inABitOrExpr(node);

        addLeftParen();

        if (node.getLeft() != null) {
            node.getLeft().apply(this);
        }

        this.buffer.append(" | ");

        if (node.getRight() != null) {
            node.getRight().apply(this);
        }

        addRightParen();

        this.outABitOrExpr(node);
    }

    @Override
    public void caseABitXorExpr(ABitXorExpr node) {
        this.inABitXorExpr(node);

        addLeftParen();

        if (node.getLeft() != null) {
            node.getLeft().apply(this);
        }

        this.buffer.append(" ^ ");

        if (node.getRight() != null) {
            node.getRight().apply(this);
        }

        addRightParen();

        this.outABitXorExpr(node);
    }

    @Override
    public void caseABitClearExpr(ABitClearExpr node) {
        this.inABitClearExpr(node);

        /** TODO: not handled */

        this.outABitClearExpr(node);
    }

    @Override
    public void caseABitLshiftExpr(ABitLshiftExpr node) {
        this.inABitLshiftExpr(node);

        addLeftParen();

        if (node.getLeft() != null) {
            node.getLeft().apply(this);
        }

        this.buffer.append(" << ");

        if (node.getRight() != null) {
            node.getRight().apply(this);
        }

        addRightParen();

        this.outABitLshiftExpr(node);
    }

    @Override
    public void caseABitRshiftExpr(ABitRshiftExpr node) {
        this.inABitRshiftExpr(node);

        addLeftParen();

        if (node.getLeft() != null) {
            node.getLeft().apply(this);
        }

        this.buffer.append(" >> ");

        if (node.getRight() != null) {
            node.getRight().apply(this);
        }

        addRightParen();

        this.outABitRshiftExpr(node);
    }

    /**
     * Code generation - unary operators
     *      '+', '-', '^', '!'
     *
     */
    @Override
    public void caseAPosExpr(APosExpr node) {
        this.inAPosExpr(node);

        addLeftParen();

        this.buffer.append('+');
        addSpace();

        if (node.getExpr() != null) {
            node.getExpr().apply(this);
        }

        addRightParen();

        this.outAPosExpr(node);
    }

    @Override
    public void caseANegExpr(ANegExpr node) {
        this.inANegExpr(node);

        addLeftParen();

        this.buffer.append('-');
        addSpace();

        if (node.getExpr() != null) {
            node.getExpr().apply(this);
        }

        addRightParen();

        this.outANegExpr(node);
    }

    @Override
    public void caseABitCompExpr(ABitCompExpr node) {
        this.inABitCompExpr(node);

        addLeftParen();

        this.buffer.append('~');
        addSpace();

        if (node.getExpr() != null) {
            node.getExpr().apply(this);
        }

        addRightParen();

        this.outABitCompExpr(node);
    }

    @Override
    public void caseANotExpr(ANotExpr node) {
        this.inANotExpr(node);

        addLeftParen();

        this.buffer.append('!');
        addSpace();

        if (node.getExpr() != null) {
            node.getExpr().apply(this);
        }

        addRightParen();

        this.outANotExpr(node);
    }

    /**
     * Code generation - relational operators
     *
     */
    @Override
    public void caseAEqExpr(AEqExpr node) {
        this.inAEqExpr(node);

        addLeftParen();

        if (node.getLeft() != null) {
            node.getLeft().apply(this);
        }

        this.buffer.append(" == ");

        if (node.getRight() != null) {
            node.getRight().apply(this);
        }

        addRightParen();

        this.outAEqExpr(node);

        /** TODO: modify to handle string, array, struct */
    }

    @Override
    public void caseANeqExpr(ANeqExpr node) {
        this.inANeqExpr(node);

        addLeftParen();

        if (node.getLeft() != null) {
            node.getLeft().apply(this);
        }

        this.buffer.append(" != ");

        if (node.getRight() != null) {
            node.getRight().apply(this);
        }

        addRightParen();

        this.outANeqExpr(node);

        /** TODO: modify to handle string, array, struct */
    }

    @Override
    public void caseALtExpr(ALtExpr node) {
        this.inALtExpr(node);

        addLeftParen();

        if (node.getLeft() != null) {
            node.getLeft().apply(this);
        }

        this.buffer.append(" < ");

        if (node.getRight() != null) {
            node.getRight().apply(this);
        }

        addRightParen();

        this.outALtExpr(node);

        /** TODO: modify to handle string */
    }

    @Override
    public void caseALteExpr(ALteExpr node) {
        this.inALteExpr(node);

        addLeftParen();

        if (node.getLeft() != null) {
            node.getLeft().apply(this);
        }

        this.buffer.append(" <= ");

        if (node.getRight() != null) {
            node.getRight().apply(this);
        }

        addRightParen();

        this.outALteExpr(node);

        /** TODO: modify to handle string */
    }

    @Override
    public void caseAGtExpr(AGtExpr node) {
        this.inAGtExpr(node);

        addLeftParen();

        if (node.getLeft() != null) {
            node.getLeft().apply(this);
        }

        this.buffer.append(" > ");

        if (node.getRight() != null) {
            node.getRight().apply(this);
        }

        addRightParen();

        this.outAGtExpr(node);

        /** TODO: modify to handle string */
    }

    @Override
    public void caseAGteExpr(AGteExpr node) {
        this.inAGteExpr(node);

        addLeftParen();

        if (node.getLeft() != null) {
            node.getLeft().apply(this);
        }

        this.buffer.append(" >= ");

        if (node.getRight() != null) {
            node.getRight().apply(this);
        }

        addRightParen();

        this.outAGteExpr(node);

        /** TODO: modify to handle string */
    }

    /**
     * Code generation - conditional operators
     *      '&&', '||'
     *
     */
    @Override
    public void caseAAndExpr(AAndExpr node) {
        this.inAAndExpr(node);

        addLeftParen();

        if (node.getLeft() != null) {
            node.getLeft().apply(this);
        }

        this.buffer.append(" && ");

        if (node.getRight() != null) {
            node.getRight().apply(this);
        }

        addRightParen();

        this.outAAndExpr(node);
    }

    @Override
    public void caseAOrExpr(AOrExpr node) {
        this.inAOrExpr(node);

        addLeftParen();

        if (node.getLeft() != null) {
            node.getLeft().apply(this);
        }

        this.buffer.append(" || ");

        if (node.getRight() != null) {
            node.getRight().apply(this);
        }

        addRightParen();

        this.outAOrExpr(node);
    }

    /**
     * Code generation - function calls
     *
     */
    @Override
    public void caseAFuncCallExpr(AFuncCallExpr node) {
        this.inAFuncCallExpr(node);

        if (node.getId() != null) {
            this.buffer.append(node.getId().getText());
        }

        addLeftParen();

        {
            List<PExpr> copy = new ArrayList<PExpr>(node.getExpr());

            for (int i = 0; i < copy.size(); i++) {
                if (i > 0) {
                    addComma();
                    addSpace();
                }
                copy.get(i).apply(this);
            }
        }

        addRightParen();

        this.outAFuncCallExpr(node);
    }

    /**
     * Code generation - append function
     *
     */
    @Override
    public void caseAAppendExpr(AAppendExpr node) {
        this.inAAppendExpr(node);

        /* TODO: not handled */

        this.outAAppendExpr(node);
    }

    /**
     * Code generation - type casting
     *
     */
    @Override
    public void caseATypeCastExpr(ATypeCastExpr node) {
        this.inATypeCastExpr(node);

        /* TODO: not handled */

        this.outATypeCastExpr(node);
    }

    /**
     * Code generation - struct fields
     *
     */
    @Override
    public void caseAFieldExpr(AFieldExpr node) {
        this.inAFieldExpr(node);

        /** TODO: not handled */

        this.outAFieldExpr(node);
    }

    /**
     * Code generation - array/slice elements
     *
     */
    @Override
    public void caseAArrayElemExpr(AArrayElemExpr node) {
        this.inAArrayElemExpr(node);

        /** TODO: not handled */

        this.outAArrayElemExpr(node);
    }

    /**
     * Code generation - variable expressions:
     *      blank, id
     *
     */
    @Override
    public void caseABlankExpr(ABlankExpr node) {
        this.inABlankExpr(node);

        this.buffer.append('_');

        this.outABlankExpr(node);
    }

    @Override
    public void caseAVariableExpr(AVariableExpr node) {
        this.inAVariableExpr(node);

        if (node.getId() != null) {
            this.buffer.append(node.getId().getText());
        }

        this.outAVariableExpr(node);
    }

    /**
     * Code generation - numeric literals:
     *      int, oct, hex, float
     *
     */
    @Override
    public void caseAIntLitExpr(AIntLitExpr node) {
        this.inAIntLitExpr(node);

        if (node.getIntLit() != null) {
            this.buffer.append(node.getIntLit().getText());
        }

        this.outAIntLitExpr(node);
    }

    @Override
    public void caseAOctLitExpr(AOctLitExpr node) {
        this.inAOctLitExpr(node);

        if (node.getOctLit() != null) {
            this.buffer.append(node.getOctLit().getText());
        }

        this.outAOctLitExpr(node);
    }

    @Override
    public void caseAHexLitExpr(AHexLitExpr node) {
        this.inAHexLitExpr(node);

        if (node.getHexLit() != null) {
            this.buffer.append(node.getHexLit().getText());
        }

        this.outAHexLitExpr(node);
    }

    @Override
    public void caseAFloatLitExpr(AFloatLitExpr node) {
        this.inAFloatLitExpr(node);

        if (node.getFloatLit() != null) {
            this.buffer.append(node.getFloatLit().getText());
        }

        this.outAFloatLitExpr(node);
    }

    /**
     * Code generation - rune literals
     *
     */
    @Override
    public void caseARuneLitExpr(ARuneLitExpr node) {
        this.inARuneLitExpr(node);

        /** TODO: handle rune as int or char */

        this.outARuneLitExpr(node);
    }

    /**
     * Code generation - string literals:
     *      interpreted, raw
     *
     */
    @Override
    public void caseAInterpretedStringLitExpr(AInterpretedStringLitExpr node) {
        this.inAInterpretedStringLitExpr(node);

        if (node.getInterpretedStringLit() != null) {
            this.buffer.append(node.getInterpretedStringLit().getText());
        }

        this.outAInterpretedStringLitExpr(node);
    }

    @Override
    public void caseARawStringLitExpr(ARawStringLitExpr node) {
        this.inARawStringLitExpr(node);

        /** TODO: not handled */

        this.outARawStringLitExpr(node);
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
