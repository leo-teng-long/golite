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
     * Overhead for Generated Python Code
     *
     */
    @Override
    public void inAProgProg(AProgProg node) {
        generateImport();
    }

    @Override
    public void outAProgProg(AProgProg node) {
        generateMain();
    }

    @Override
    public void caseAProgProg(AProgProg node) {
        this.inAProgProg(node);

        {
            List<PTopDec> copy = new ArrayList<PTopDec>(node.getTopDec());

            for (PTopDec e : copy) {
                e.apply(this);
                addLines(1);
            }
        }

        this.outAProgProg(node);
    }

    private void generateImport() {
        buffer.append("from __future__ import print_function\n");
        addLines(1);
    }

    private void generateMain() {
        buffer.append("if __name__ == '__main__':\n");
        buffer.append("\tmain()\n");
    }

    /**
     * Top-Level Variable Declarations
     *
     */
    @Override
    public void caseAVarsTopDec(AVarsTopDec node) {
        /* TODO */
    }

    @Override
    public void caseASpecVarSpec(ASpecVarSpec node) {
        /* TODO */
    }

    /**
     * Top-Level Type Declarations
     *
     */
    @Override
    public void caseATypesTopDec(ATypesTopDec node) {
        /* TODO */
    }

    @Override
    public void caseASpecTypeSpec(ASpecTypeSpec node) {
        /* TODO */
    }

    /**
     * Top-Level Function Declarations
     *
     */
    @Override
    public void caseAFuncTopDec(AFuncTopDec node) {
        this.inAFuncTopDec(node);

        buffer.append("def");
        addSpace();

        if (node.getId() != null) {
            buffer.append(node.getId().getText());
        }

        addLeftParen();

        {
            List<PArgGroup> copy = new ArrayList<PArgGroup>(node.getArgGroup());

            for (int i = 0; i < copy.size(); i++) {
                if (i > 0) {
                    addComma();
                    addSpace();
                }
                copy.get(i).apply(this);
            }
        }

        addRightParen();
        addColon();

        // do nothing with return type info;

        {
            enterCodeBlock();

            List<PStmt> copy = new ArrayList<PStmt>(node.getStmt());
            for (PStmt e : copy) {
                generateStatement(e);
            }

            exitCodeBlock();
        }

        this.outAFuncTopDec(node);
    }

    @Override
    public void caseAArgArgGroup(AArgArgGroup node) {
        this.inAArgArgGroup(node);

        {
            List<TId> copy = new ArrayList<TId>(node.getId());

            for (int i = 0; i < copy.size(); i++) {
                if (i > 0) {
                    addComma();
                    addSpace();
                }
                buffer.append(copy.get(i).getText());
            }
        }

        // do nothing with argument type info;

        this.outAArgArgGroup(node);
    }

    /**
     * Empty Statements
     *
     */
    @Override
    public void caseAEmptyStmt(AEmptyStmt node) {
        // do nothing;
    }

    /**
     * Variable Declaration Statements
     *
     */
    @Override
    public void caseAVarDecStmt(AVarDecStmt node) {
        /* TODO */
    }

    @Override
    public void caseAShortAssignStmt(AShortAssignStmt node) {
        this.inAShortAssignStmt(node);

        {
            List<POptId> copy = new ArrayList<POptId>(node.getOptId());

            for (int i = 0; i < copy.size(); i++) {
                if (i > 0) {
                    addComma();
                    addSpace();
                }
                copy.get(i).apply(this);
            }
        }

        buffer.append(" = ");

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

        this.outAShortAssignStmt(node);
    }

    @Override
    public void caseABlankOptId(ABlankOptId node) {
        this.inABlankOptId(node);

        buffer.append('_');

        this.outABlankOptId(node);
    }

    @Override
    public void caseAIdOptId(AIdOptId node) {
        this.inAIdOptId(node);

        if (node.getId() != null) {
            buffer.append(node.getId().getText());
        }

        this.outAIdOptId(node);
    }

    /**
     * Type Declaration Statements
     *
     */
    @Override
    public void caseATypeDecStmt(ATypeDecStmt node) {
        /* TODO */
    }

    /**
     * Assignment Statements
     *
     */
    @Override
    public void caseAAssignStmt(AAssignStmt node) {
        this.inAAssignStmt(node);

        {
            List<PExpr> copy = new ArrayList<PExpr>(node.getLhs());

            for (int i = 0; i < copy.size(); i++) {
                if (i > 0) {
                    addComma();
                    addSpace();
                }
                copy.get(i).apply(this);
            }
        }

        buffer.append(" = ");

        {
            List<PExpr> copy = new ArrayList<PExpr>(node.getRhs());

            for (int i = 0; i < copy.size(); i++) {
                if (i > 0) {
                    addComma();
                    addSpace();
                }
                copy.get(i).apply(this);
            }
        }

        this.outAAssignStmt(node);
    }

    /**
     * Arithmetic Op-Assign Statements:
     *  '+=', '-=', '*=', '/=', '%='
     *
     */
    @Override
    public void caseAPlusAssignStmt(APlusAssignStmt node) {
        this.inAPlusAssignStmt(node);

        if (node.getLhs() != null) {
            node.getLhs().apply(this);
        }

        buffer.append(" += ");

        if (node.getRhs() != null) {
            node.getRhs().apply(this);
        }

        this.outAPlusAssignStmt(node);
    }

    @Override
    public void caseAMinusAssignStmt(AMinusAssignStmt node) {
        this.inAMinusAssignStmt(node);

        if (node.getLhs() != null) {
            node.getLhs().apply(this);
        }

        buffer.append(" -= ");

        if (node.getRhs() != null) {
            node.getRhs().apply(this);
        }

        this.outAMinusAssignStmt(node);
    }

    @Override
    public void caseAStarAssignStmt(AStarAssignStmt node) {
        this.inAStarAssignStmt(node);

        if (node.getLhs() != null) {
            node.getLhs().apply(this);
        }

        buffer.append(" *= ");

        if (node.getRhs() != null) {
            node.getRhs().apply(this);
        }

        this.outAStarAssignStmt(node);
    }

    @Override
    public void caseASlashAssignStmt(ASlashAssignStmt node) {
        this.inASlashAssignStmt(node);

        if (node.getLhs() != null) {
            node.getLhs().apply(this);
        }

        buffer.append(" /= ");

        if (node.getRhs() != null) {
            node.getRhs().apply(this);
        }

        this.outASlashAssignStmt(node);
    }

    @Override
    public void caseAPercAssignStmt(APercAssignStmt node) {
        this.inAPercAssignStmt(node);

        if (node.getLhs() != null) {
            node.getLhs().apply(this);
        }

        buffer.append(" %= ");

        if (node.getRhs() != null) {
            node.getRhs().apply(this);
        }

        this.outAPercAssignStmt(node);
    }

    /**
     * Bit Op-Assign Statements:
     *  '&=', '|=', '^=', '&^=', '<<=', '>>='
     *
     */
    @Override
    public void caseAAndAssignStmt(AAndAssignStmt node) {
        this.inAAndAssignStmt(node);

        if (node.getLhs() != null) {
            node.getLhs().apply(this);
        }

        buffer.append(" &= ");

        if (node.getRhs() != null) {
            node.getRhs().apply(this);
        }

        this.outAAndAssignStmt(node);
    }

    @Override
    public void caseAPipeAssignStmt(APipeAssignStmt node) {
        this.inAPipeAssignStmt(node);

        if (node.getLhs() != null) {
            node.getLhs().apply(this);
        }

        buffer.append(" |= ");

        if (node.getRhs() != null) {
            node.getRhs().apply(this);
        }

        this.outAPipeAssignStmt(node);
    }

    @Override
    public void caseACarotAssignStmt(ACarotAssignStmt node) {
        this.inACarotAssignStmt(node);

        if (node.getLhs() != null) {
            node.getLhs().apply(this);
        }

        buffer.append(" ^= ");

        if (node.getRhs() != null) {
            node.getRhs().apply(this);
        }

        this.outACarotAssignStmt(node);
    }

    @Override
    public void caseAAmpCarotAssignStmt(AAmpCarotAssignStmt node) {
        this.inAAmpCarotAssignStmt(node);

        if (node.getLhs() != null) {
            node.getLhs().apply(this);
        }

        buffer.append(" &= ~ ");

        if (node.getRhs() != null) {
            node.getRhs().apply(this);
        }

        this.outAAmpCarotAssignStmt(node);
    }

    @Override
    public void caseALshiftAssignStmt(ALshiftAssignStmt node) {
        this.inALshiftAssignStmt(node);

        if (node.getLhs() != null) {
            node.getLhs().apply(this);
        }

        buffer.append(" <<= ");

        if (node.getRhs() != null) {
            node.getRhs().apply(this);
        }

        this.outALshiftAssignStmt(node);
    }

    @Override
    public void caseARshiftAssignStmt(ARshiftAssignStmt node) {
        this.inARshiftAssignStmt(node);

        if (node.getLhs() != null) {
            node.getLhs().apply(this);
        }

        buffer.append(" >>= ");

        if (node.getRhs() != null) {
            node.getRhs().apply(this);
        }

        this.outARshiftAssignStmt(node);
    }

    /**
     * Increment, Decrement & Expression Statements
     *
     */
    @Override
    public void caseAIncrStmt(AIncrStmt node) {
        this.inAIncrStmt(node);

        if (node.getExpr() != null) {
            node.getExpr().apply(this);
        }

        buffer.append(" += 1");

        this.outAIncrStmt(node);
    }

    @Override
    public void caseADecrStmt(ADecrStmt node) {
        this.inADecrStmt(node);

        if (node.getExpr() != null) {
            node.getExpr().apply(this);
        }

        buffer.append(" -= 1");

        this.outADecrStmt(node);
    }

    @Override
    public void caseAExprStmt(AExprStmt node) {
        this.inAExprStmt(node);

        if (node.getExpr() != null) {
            node.getExpr().apply(this);
        }

        this.outAExprStmt(node);
    }

    /**
     * Print & Println Statements
     *
     */
    @Override
    public void caseAPrintStmt(APrintStmt node) {
        this.inAPrintStmt(node);

        buffer.append("print");
        addLeftParen();

        {
            List<PExpr> copy = new ArrayList<PExpr>(node.getExpr());

            for (int i = 0; i < copy.size(); i++) {
                if (i > 0) {
                    buffer.append(" + ");
                }

                buffer.append("str");
                addLeftParen();

                copy.get(i).apply(this);

                addRightParen();
            }

            if (!copy.isEmpty()) {
                addComma();
                addSpace();
            }
        }

        buffer.append("end = ''");
        addRightParen();

        this.outAPrintStmt(node);
    }

    @Override
    public void caseAPrintlnStmt(APrintlnStmt node) {
        this.inAPrintlnStmt(node);

        buffer.append("print");
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

        this.outAPrintlnStmt(node);
    }

    /**
     * Continue, Break & Return Statements
     *
     */
    @Override
    public void caseAContinueStmt(AContinueStmt node) {
        this.inAContinueStmt(node);

        buffer.append("continue");

        this.outAContinueStmt(node);
    }

    @Override
    public void caseABreakStmt(ABreakStmt node) {
        this.inABreakStmt(node);

        buffer.append("break");

        this.outABreakStmt(node);
    }

    @Override
    public void caseAReturnStmt(AReturnStmt node) {
        this.inAReturnStmt(node);

        buffer.append("return");
        addSpace();

        if (node.getExpr() != null) {
            node.getExpr().apply(this);
        }

        this.outAReturnStmt(node);
    }

    /**
     * If-Else Statements
     *
     */
    @Override
    public void caseAIfElseStmt(AIfElseStmt node) {
        /* TODO */
    }

    @Override
    public void caseAConditionCondition(AConditionCondition node) {
        /* TODO */
    }

    /**
     * Switch Statements
     *
     */
    @Override
    public void caseASwitchStmt(ASwitchStmt node) {
        /* TODO */
    }

    @Override
    public void caseABlockCaseBlock(ABlockCaseBlock node) {
        /* TODO */
    }

    @Override
    public void caseAExprsCaseCondition(AExprsCaseCondition node) {
        /* TODO */
    }

    @Override
    public void caseADefaultCaseCondition(ADefaultCaseCondition node) {
        /* TODO */
    }

    /**
     * For & While Loops
     *
     */
    @Override
    public void caseALoopStmt(ALoopStmt node) {
        this.inALoopStmt(node);

        /**
         * Only used when generating for Loops
         */
        if (node.getInit() != null) {
            deleteLastCharacter();
            generateStatement(node.getInit());
        }

        /**
         * Only used when generating for Loops
         */
        if (node.getInit() != null && node.getEnd() != null) {
            addTabs();
        }

        buffer.append("while");
        addSpace();

        if (node.getExpr() != null) {
            node.getExpr().apply(this);
        } else {
            buffer.append("True");
        }

        addColon();

        enterCodeBlock();

        List<PStmt> copy = new ArrayList<PStmt>(node.getBlock());
        for (PStmt e : copy) {
            generateStatement(e);
        }

        /**
         * Only used when generating for Loops
         */
        if (node.getEnd() != null) {
            generateStatement(node.getEnd());
        }

        exitCodeBlock();

        this.outALoopStmt(node);
    }

    /**
     * Block Statements
     *
     */
    @Override
    public void caseABlockStmt(ABlockStmt node) {
        this.inABlockStmt(node);

        {
            List<PStmt> copy = new ArrayList<PStmt>(node.getStmt());
            for (PStmt e : copy) {
                generateStatement(e);
            }
        }

        this.outABlockStmt(node);
    }

    /**
     * Empty Expressions
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

    private void addColon() {
        buffer.append(':');
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

    private void addTabs() {
        for (int i = 0; i < tabDepth; i++) {
            buffer.append('\t');
        }
    }

    private void addLines(int n) {
        for (int i = 0; i < n; i++) {
            buffer.append('\n');
        }
    }

    private void generateStatement(PStmt e) {
        addTabs();
        e.apply(this);
        addLines(1);
    }

    private void enterCodeBlock() {
        addLines(1);
        tabDepth++;
        /**
         * Add empty function call to prevent unexpected indented block
         */
        addTabs();
        buffer.append("pass\n");
    }

    private void exitCodeBlock() {
        tabDepth--;
    }

}
