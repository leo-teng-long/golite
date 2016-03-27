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
     * Empty expression
     *  (do we need this?)
     *
     */
    @Override
    public void caseAEmptyExpr(AEmptyExpr node) {
        // do nothing;
    }

    /**
     * Arithmetic Operators:
     *  '+', '-', '*', '/', '%'
     *
     */
    @Override
    public void caseAAddExpr(AAddExpr node) {
        this.inAAddExpr(node);

        addLeftParen();

        if (node.getLeft() != null) {
            node.getLeft().apply(this);
        }

        buffer.append(" + ");

        if (node.getRight() != null) {
            node.getRight().apply(this);
        }

        addRightParen();

        this.outAAddExpr(node);
    }

    @Override
    public void caseASubtractExpr(ASubtractExpr node) {
        this.inASubtractExpr(node);

        addLeftParen();

        if (node.getLeft() != null) {
            node.getLeft().apply(this);
        }

        buffer.append(" - ");

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

        buffer.append(" * ");

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

        buffer.append(" / ");

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

        buffer.append(" % ");

        if (node.getRight() != null) {
            node.getRight().apply(this);
        }

        addRightParen();

        this.outAModExpr(node);
    }

    /**
     * Bit Operators:
     *  '&', '|', '^', '&^', '<<', '>>'
     *
     */
    @Override
    public void caseABitAndExpr(ABitAndExpr node) {
        this.inABitAndExpr(node);

        addLeftParen();

        if (node.getLeft() != null) {
            node.getLeft().apply(this);
        }

        buffer.append(" & ");

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

        buffer.append(" | ");

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

        buffer.append(" ^ ");

        if (node.getRight() != null) {
            node.getRight().apply(this);
        }

        addRightParen();

        this.inABitXorExpr(node);
    }

    @Override
    public void caseABitClearExpr(ABitClearExpr node) {
        this.inABitClearExpr(node);

        addLeftParen();

        if (node.getLeft() != null) {
            node.getLeft().apply(this);
        }

        buffer.append(" &~ ");

        if (node.getRight() != null) {
            node.getRight().apply(this);
        }

        addRightParen();

        this.outABitClearExpr(node);
    }

    @Override
    public void caseABitLshiftExpr(ABitLshiftExpr node) {
        this.inABitLshiftExpr(node);

        addLeftParen();

        if (node.getLeft() != null) {
            node.getLeft().apply(this);
        }

        buffer.append(" << ");

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

        buffer.append(" >> ");

        if (node.getRight() != null) {
            node.getRight().apply(this);
        }

        addRightParen();

        this.outABitRshiftExpr(node);
    }

    /**
     * Unary Operators:
     *  '+', '-', '^', '!'
     *
     */
    @Override
    public void caseAPosExpr(APosExpr node) {
        this.inAPosExpr(node);

        addLeftParen();

        buffer.append('+');
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

        buffer.append('-');
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

        buffer.append('~');
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

        buffer.append("not");
        addSpace();

        if (node.getExpr() != null) {
            node.getExpr().apply(this);
        }

        addRightParen();

        this.outANotExpr(node);
    }

    /**
     * Relational Operators:
     *  '==', '!=', '<', '<=', '>', '>='
     *
     */
    @Override
    public void caseAEqExpr(AEqExpr node) {
        this.inAEqExpr(node);

        addLeftParen();

        if (node.getLeft() != null) {
            node.getLeft().apply(this);
        }

        buffer.append(" == ");

        if (node.getRight() != null) {
            node.getRight().apply(this);
        }

        addRightParen();

        this.outAEqExpr(node);
    }

    @Override
    public void caseANeqExpr(ANeqExpr node) {
        this.inANeqExpr(node);

        addLeftParen();

        if (node.getLeft() != null) {
            node.getLeft().apply(this);
        }

        buffer.append(" != ");

        if (node.getRight() != null) {
            node.getRight().apply(this);
        }

        addRightParen();

        this.outANeqExpr(node);
    }

    @Override
    public void caseALtExpr(ALtExpr node) {
        this.inALtExpr(node);

        addLeftParen();

        if (node.getLeft() != null) {
            node.getLeft().apply(this);
        }

        buffer.append(" < ");

        if (node.getRight() != null) {
            node.getRight().apply(this);
        }

        addRightParen();

        this.outALtExpr(node);
    }

    @Override
    public void caseALteExpr(ALteExpr node) {
        this.inALteExpr(node);

        addLeftParen();

        if (node.getLeft() != null) {
            node.getLeft().apply(this);
        }

        buffer.append(" <= ");

        if (node.getRight() != null) {
            node.getRight().apply(this);
        }

        addRightParen();

        this.outALteExpr(node);
    }

    @Override
    public void caseAGtExpr(AGtExpr node) {
        this.inAGtExpr(node);

        addLeftParen();

        if (node.getLeft() != null) {
            node.getLeft().apply(this);
        }

        buffer.append(" > ");

        if (node.getRight() != null) {
            node.getRight().apply(this);
        }

        addRightParen();

        this.outAGtExpr(node);
    }

    @Override
    public void caseAGteExpr(AGteExpr node) {
        this.inAGteExpr(node);

        addLeftParen();

        if (node.getLeft() != null) {
            node.getLeft().apply(this);
        }

        buffer.append(" >= ");

        if (node.getRight() != null) {
            node.getRight().apply(this);
        }

        addRightParen();

        this.outAGteExpr(node);
    }

    /**
     * Conditional operator: '||', '&&'
     *
     */
    @Override
    public void caseAAndExpr(AAndExpr node) {
        this.inAAndExpr(node);

        addLeftParen();

        if (node.getLeft() != null) {
            node.getLeft().apply(this);
        }

        buffer.append(" and ");

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

        buffer.append(" or ");

        if (node.getRight() != null) {
            node.getRight().apply(this);
        }

        addRightParen();

        this.outAOrExpr(node);
    }

    /**
     * Function calls, append & type casting
     *
     */
    @Override
    public void caseAFuncCallExpr(AFuncCallExpr node) {
        this.inAFuncCallExpr(node);

        if (node.getId() != null) {
            buffer.append(node.getId().getText());
        }

        {
            addLeftParen();

            List<PExpr> copy = new ArrayList<PExpr>(node.getExpr());
            for (PExpr e : copy) {
                addSpace();
                e.apply(this);
                addComma();
            }
            if (copy.size() > 0) {
                deleteLastCharacter();
            }

            addSpace();
            addRightParen();
        }

        this.outAFuncCallExpr(node);
    }

    @Override
    public void caseAAppendExpr(AAppendExpr node) {
        /* TODO */
    }

    @Override
    public void caseATypeCastExpr(ATypeCastExpr node) {
        /* TODO */
    }

    /**
     * Array/slice elements & fields
     *
     */
    @Override
    public void caseAArrayElemExpr(AArrayElemExpr node) {
        /* TODO */
    }

    @Override
    public void caseAFieldExpr(AFieldExpr node) {
        /* TODO */
    }

    /**
     * Identifiers
     *
     */
    @Override
    public void caseABlankExpr(ABlankExpr node) {
        /* TODO */
    }

    @Override
    public void caseAVariableExpr(AVariableExpr node) {
        this.inAVariableExpr(node);

        if (node.getId() != null) {
            buffer.append(node.getId().getText());
        }

        this.outAVariableExpr(node);
    }

    /**
     * Literals
     *
     */
    @Override
    public void caseAIntLitExpr(AIntLitExpr node) {
        this.inAIntLitExpr(node);

        if (node.getIntLit() != null) {
            buffer.append(node.getIntLit().getText());
        }

        this.outAIntLitExpr(node);
    }

    @Override
    public void caseAOctLitExpr(AOctLitExpr node) {
        /* TODO */
    }

    @Override
    public void caseAHexLitExpr(AHexLitExpr node) {
        /* TODO */
    }

    @Override
    public void caseAFloatLitExpr(AFloatLitExpr node) {
        this.inAFloatLitExpr(node);

        if (node.getFloatLit() != null) {
            buffer.append(node.getFloatLit().getText());
        }

        this.outAFloatLitExpr(node);
    }

    @Override
    public void caseARuneLitExpr(ARuneLitExpr node) {
        /* TODO */
    }

    @Override
    public void caseAInterpretedStringLitExpr(AInterpretedStringLitExpr node) {
        /* TODO */
    }

    @Override
    public void caseARawStringLitExpr(ARawStringLitExpr node) {
        /* TODO */
    }

    /**
     * Private methods
     *
     */
    private void addSpace() {
        buffer.append(' ');
    }

    private void addComma() {
        buffer.append(',');
    }

    private void addLeftParen() {
        buffer.append('(');
    }

    private void addRightParen() {
        buffer.append(')');
    }

    private void deleteLastCharacter() {
        buffer.deleteCharAt(buffer.length() - 1);
    }

}
