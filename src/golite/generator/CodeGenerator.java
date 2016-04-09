package golite.generator;

import golite.analysis.*;
import golite.node.*;
import golite.symbol.*;
import golite.type.*;
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
    /** Keep track of the end statement of for loop */
    private PStmt lastForEnd;

    /** Contain information about expressions */
    private HashMap<Node, GoLiteType> typeTable;

    /**
     * CodeGenerator Constructor
     */
    public CodeGenerator(HashMap<Node, GoLiteType> typeTable) {
        this.buffer = new StringBuffer();
        this.tabDepth = 0;
        this.typeTable = typeTable;
    }

    /**
     * Wrap an expression in bit_mask()
     */
    public void bitMask(Node n)
    {
        if (typeTable.get(n) instanceof IntType || typeTable.get(n) instanceof RuneType)
        {
            buffer.append("bit_mask(");
        }                    
        n.apply(this);
        if (typeTable.get(n) instanceof IntType || typeTable.get(n) instanceof RuneType)
        {
            buffer.append(")");
        }   
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

    private void generateImport() {
        buffer.append("from __future__ import print_function\n");
        addLines(1);
        buffer.append("bit_mask = lambda x : (x + 2**31) % 2**32 - 2**31\n");
        addLines(1);
    }

    private void generateMain() {
        buffer.append("if __name__ == '__main__':\n");
        buffer.append("\tmain()\n");
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

    /**
     * Top-Level Variable Declarations
     *
     */
    @Override
    public void caseAVarsTopDec(AVarsTopDec node) {
        this.inAVarsTopDec(node);

        {
            List<PVarSpec> copy = new ArrayList<PVarSpec>(node.getVarSpec());
            for (PVarSpec e : copy) {
                e.apply(this);
                addLines(1);
            }
        }

        this.outAVarsTopDec(node);
    }

    @Override
    public void caseASpecVarSpec(ASpecVarSpec node) {
        this.inASpecVarSpec(node);

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

        if (node.getTypeExpr() != null && node.getExpr().size() == 0) {
            PTypeExpr type = node.getTypeExpr();
            String defaultValue = null;

            if (type instanceof ABoolTypeExpr) {
                defaultValue = "False";
            } else if (type instanceof AIntTypeExpr) {
                defaultValue = "0";
            } else if (type instanceof AFloatTypeExpr) {
                defaultValue = "0.";
            } else if (type instanceof ARuneTypeExpr) {
                /* TODO */
            } else if (type instanceof AStringTypeExpr) {
                defaultValue = "";
            }

            for (int i = 0; i < node.getOptId().size(); i++) {
                if (i > 0) {
                    addComma();
                    addSpace();
                }
                buffer.append(defaultValue);
            }
        }

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

        this.outASpecVarSpec(node);
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

            exitCodeBlock(isBlockEmpty(copy));
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
        this.inAVarDecStmt(node);

        {
            List<PVarSpec> copy = new ArrayList<PVarSpec>(node.getVarSpec());

            for (int i = 0; i < copy.size(); i++) {
                if (i > 0) {
                    addLines(1);
                    addTabs();
                }
                copy.get(i).apply(this);
            }
        }

        this.outAVarDecStmt(node);
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
                                    
                bitMask(copy.get(i));
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
                bitMask(copy.get(i));
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
            bitMask(node.getRhs());
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
            bitMask(node.getRhs());
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
            bitMask(node.getRhs());
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
            bitMask(node.getRhs());
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
            bitMask(node.getRhs());
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
            bitMask(node.getRhs());
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
            bitMask(node.getRhs());
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
            bitMask(node.getRhs());
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
            bitMask(node.getRhs());
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
            bitMask(node.getRhs());
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
            bitMask(node.getRhs());
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

        addLines(1);
        addTabs();
        if (node.getExpr() != null) {
            node.getExpr().apply(this);
        }
        buffer.append(" = ");
        if (node.getExpr() != null) {
            bitMask(node.getExpr());
        }

        this.outAIncrStmt(node);
    }

    @Override
    public void caseADecrStmt(ADecrStmt node) {
        this.inADecrStmt(node);

        if (node.getExpr() != null) {
            node.getExpr().apply(this);
        }

        buffer.append(" -= 1");

        addLines(1);
        addTabs();
        if (node.getExpr() != null) {
            node.getExpr().apply(this);
        }
        buffer.append(" = ");
        if (node.getExpr() != null) {
            bitMask(node.getExpr());
        }
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

        if (lastForEnd != null) {
            lastForEnd.apply(this);
            addLines(1);
            addTabs();
        }
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
        this.inAIfElseStmt(node);

        if (node.getCondition() != null) {
            node.getCondition().apply(this);
        }

        {
            enterCodeBlock();

            List<PStmt> copy = new ArrayList<PStmt>(node.getIfBlock());
            for (PStmt e : copy) {
                generateStatement(e);
            }

            exitCodeBlock(isBlockEmpty(copy));
        }

        addTabs();
        buffer.append("else");
        addColon();

        {
            enterCodeBlock();

            List<PStmt> copy = new ArrayList<PStmt>(node.getElseBlock());
            for (PStmt e : copy) {
                generateStatement(e);
            }

            exitCodeBlock(isBlockEmpty(copy));
        }

        this.outAIfElseStmt(node);
    }

    @Override
    public void caseAConditionCondition(AConditionCondition node) {
        this.inAConditionCondition(node);

        if (node.getStmt() != null) {
            node.getStmt().apply(this);
            addLines(1);
            addTabs();
        }

        buffer.append("if");
        addSpace();

        if (node.getExpr() != null) {
            node.getExpr().apply(this);
        }

        addColon();

        this.outAConditionCondition(node);
    }

    /**
     * Switch Statements
     *
     */
    @Override
    public void caseASwitchStmt(ASwitchStmt node) {
        this.inASwitchStmt(node);

        if (node.getStmt() != null) {
            node.getStmt().apply(this);
            addLines(1);
            addTabs();
        }

        PExpr switchExpr = node.getExpr();

        List<PCaseBlock> caseBlocks = new ArrayList<PCaseBlock>(node.getCaseBlock());

        PCaseBlock defaultBlock = null;
        for (PCaseBlock block : caseBlocks) {
            PCaseCondition condition = ((ABlockCaseBlock) block).getCaseCondition();
            if (condition instanceof ADefaultCaseCondition) {
                defaultBlock = block;
                break;
            }
        }

        for (int i = 0; i < caseBlocks.size(); i++) {
            PCaseCondition condition = ((ABlockCaseBlock) caseBlocks.get(i)).getCaseCondition();
            if (condition instanceof ADefaultCaseCondition) {
                continue;
            }

            if (i == 0) {
                buffer.append("if");
            } else {
                addTabs();
                buffer.append("elif");
            }
            addSpace();

            List<PExpr> caseExprs = new ArrayList<PExpr>(((AExprsCaseCondition) condition).getExpr());
            for (int j = 0; j < caseExprs.size(); j++) {
                if (j > 0) {
                    buffer.append(" or ");
                }

                if (switchExpr != null) {
                    addLeftParen();
                    switchExpr.apply(this);
                    buffer.append(" == ");
                    caseExprs.get(j).apply(this);
                    addRightParen();
                } else {
                    caseExprs.get(j).apply(this);
                }
            }

            addColon();

            caseBlocks.get(i).apply(this);
        }

        if (defaultBlock != null) {
            if (caseBlocks.size() == 1) {
                buffer.append("if");
                addSpace();
                buffer.append("True");
            } else {
                addTabs();
                buffer.append("else");
            }

            addColon();

            defaultBlock.apply(this);
        }

        this.outASwitchStmt(node);
    }

    @Override
    public void caseABlockCaseBlock(ABlockCaseBlock node) {
        this.inABlockCaseBlock(node);

        if (node.getCaseCondition() != null) {
            // do nothing;
        }

        {
            enterCodeBlock();

            List<PStmt> copy = new ArrayList<PStmt>(node.getStmt());
            for (PStmt e : copy) {
                generateStatement(e);
            }

            exitCodeBlock(isBlockEmpty(copy));
        }

        this.outABlockCaseBlock(node);
    }

    @Override
    public void caseAExprsCaseCondition(AExprsCaseCondition node) {
        // do nothing;
    }

    @Override
    public void caseADefaultCaseCondition(ADefaultCaseCondition node) {
        // do nothing;
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
            node.getInit().apply(this);
            addLines(1);
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
        /**
         * Only used when generating for Loops
         */
        lastForEnd = node.getEnd();
        if (node.getEnd() != null) {
            copy.add(node.getEnd());
        }

        for (PStmt e : copy) {
            generateStatement(e);
        }

        exitCodeBlock(isBlockEmpty(copy));

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
            bitMask(node.getLeft());
        }

        buffer.append(" + ");

        if (node.getRight() != null) {
            bitMask(node.getRight());
        }

        addRightParen();

        this.outAAddExpr(node);
    }

    @Override
    public void caseASubtractExpr(ASubtractExpr node) {
        this.inASubtractExpr(node);

        addLeftParen();

        if (node.getLeft() != null) {
            bitMask(node.getLeft());
        }

        buffer.append(" - ");

        if (node.getRight() != null) {
            bitMask(node.getRight());
        }

        addRightParen();

        this.outASubtractExpr(node);
    }

    @Override
    public void caseAMultExpr(AMultExpr node) {
        this.inAMultExpr(node);

        addLeftParen();

        if (node.getLeft() != null) {
            bitMask(node.getLeft());
        }

        buffer.append(" * ");

        if (node.getRight() != null) {
            bitMask(node.getRight());
        }

        addRightParen();

        this.outAMultExpr(node);
    }

    @Override
    public void caseADivExpr(ADivExpr node) {
        this.inADivExpr(node);

        addLeftParen();

        if (node.getLeft() != null) {
            bitMask(node.getLeft());
        }

        buffer.append(" / ");

        if (node.getRight() != null) {
            bitMask(node.getRight());
        }

        addRightParen();

        this.outADivExpr(node);
    }

    @Override
    public void caseAModExpr(AModExpr node) {
        this.inAModExpr(node);

        addLeftParen();

        if (node.getLeft() != null) {
            bitMask(node.getLeft());
        }

        buffer.append(" % ");

        if (node.getRight() != null) {
            bitMask(node.getRight());
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
            bitMask(node.getLeft());
        }

        buffer.append(" & ");

        if (node.getRight() != null) {
            bitMask(node.getRight());
        }

        addRightParen();

        this.outABitAndExpr(node);
    }

    @Override
    public void caseABitOrExpr(ABitOrExpr node) {
        this.inABitOrExpr(node);

        addLeftParen();

        if (node.getLeft() != null) {
            bitMask(node.getLeft());
        }

        buffer.append(" | ");

        if (node.getRight() != null) {
            bitMask(node.getRight());
        }

        addRightParen();

        this.outABitOrExpr(node);
    }

    @Override
    public void caseABitXorExpr(ABitXorExpr node) {
        this.inABitXorExpr(node);

        addLeftParen();

        if (node.getLeft() != null) {
            bitMask(node.getLeft());
        }

        buffer.append(" ^ ");

        if (node.getRight() != null) {
            bitMask(node.getRight());
        }

        addRightParen();

        this.inABitXorExpr(node);
    }

    @Override
    public void caseABitClearExpr(ABitClearExpr node) {
        this.inABitClearExpr(node);

        addLeftParen();

        if (node.getLeft() != null) {
            bitMask(node.getLeft());
        }

        buffer.append(" &~ ");

        if (node.getRight() != null) {
            bitMask(node.getRight());
        }

        addRightParen();

        this.outABitClearExpr(node);
    }

    @Override
    public void caseABitLshiftExpr(ABitLshiftExpr node) {
        this.inABitLshiftExpr(node);

        addLeftParen();

        if (node.getLeft() != null) {
            bitMask(node.getLeft());
        }

        buffer.append(" << ");

        if (node.getRight() != null) {
            bitMask(node.getRight());
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
            bitMask(node.getRight());
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
            bitMask(node.getExpr());
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
            bitMask(node.getExpr());
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
            bitMask(node.getExpr());
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
            bitMask(node.getExpr());
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
            bitMask(node.getLeft());
        }

        buffer.append(" == ");

        if (node.getRight() != null) {
            bitMask(node.getRight());
        }

        addRightParen();

        this.outAEqExpr(node);
    }

    @Override
    public void caseANeqExpr(ANeqExpr node) {
        this.inANeqExpr(node);

        addLeftParen();

        if (node.getLeft() != null) {
            bitMask(node.getLeft());
        }

        buffer.append(" != ");

        if (node.getRight() != null) {
            bitMask(node.getRight());
        }

        addRightParen();

        this.outANeqExpr(node);
    }

    @Override
    public void caseALtExpr(ALtExpr node) {
        this.inALtExpr(node);

        addLeftParen();

        if (node.getLeft() != null) {
            bitMask(node.getLeft());
        }

        buffer.append(" < ");

        if (node.getRight() != null) {
            bitMask(node.getRight());
        }

        addRightParen();

        this.outALtExpr(node);
    }

    @Override
    public void caseALteExpr(ALteExpr node) {
        this.inALteExpr(node);

        addLeftParen();

        if (node.getLeft() != null) {
            bitMask(node.getLeft());
        }

        buffer.append(" <= ");

        if (node.getRight() != null) {
            bitMask(node.getRight());
        }

        addRightParen();

        this.outALteExpr(node);
    }

    @Override
    public void caseAGtExpr(AGtExpr node) {
        this.inAGtExpr(node);

        addLeftParen();

        if (node.getLeft() != null) {
            bitMask(node.getLeft());
        }

        buffer.append(" > ");

        if (node.getRight() != null) {
            bitMask(node.getRight());
        }

        addRightParen();

        this.outAGtExpr(node);
    }

    @Override
    public void caseAGteExpr(AGteExpr node) {
        this.inAGteExpr(node);

        addLeftParen();

        if (node.getLeft() != null) {
            bitMask(node.getLeft());
        }

        buffer.append(" >= ");

        if (node.getRight() != null) {
            bitMask(node.getRight());
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
            bitMask(node.getLeft());
        }

        buffer.append(" and ");

        if (node.getRight() != null) {
            bitMask(node.getRight());
        }

        addRightParen();

        this.outAAndExpr(node);
    }

    @Override
    public void caseAOrExpr(AOrExpr node) {
        this.inAOrExpr(node);

        addLeftParen();

        if (node.getLeft() != null) {
            bitMask(node.getLeft());
        }

        buffer.append(" or ");

        if (node.getRight() != null) {
            bitMask(node.getRight());
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
        this.inABlankExpr(node);

        buffer.append('_');

        this.outABlankExpr(node);
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
        this.inAOctLitExpr(node);

        if (node.getOctLit() != null) {
            buffer.append(node.getOctLit().getText());
        }

        this.outAOctLitExpr(node);
    }

    @Override
    public void caseAHexLitExpr(AHexLitExpr node) {
        this.inAHexLitExpr(node);

        if (node.getHexLit() != null) {
            buffer.append(node.getHexLit().getText());
        }

        this.outAHexLitExpr(node);
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
        this.inARuneLitExpr(node);

        if (node.getRuneLit() != null) {
            String s = node.getRuneLit().getText();
            if (s.equals("'\\t'"))
            {
                buffer.append(9);
            } 
            else if (s.equals("'\\b'"))
            {
                buffer.append(8);
            }
            else if (s.equals("'\\f'"))
            {
                buffer.append(12);
            }
            else if (s.equals("'\\a'"))
            {
                buffer.append(7);
            }
            else if (s.equals("'\\n'"))
            {
                buffer.append(10);
            }
            else if (s.equals("'\\r'"))
            {
                buffer.append(13);
            }
            else if (s.equals("'\\v'"))
            {
                buffer.append(11);
            }
            else if (s.equals("'\\\\'"))
            {
                buffer.append(92);
            }
            else if (s.equals("'\\''"))
            {
                buffer.append(39);
            }
            else
            {
                buffer.append((int) s.substring(1, s.length()-1).charAt(0));
            }
        }

        this.outARuneLitExpr(node);
    }

    @Override
    public void caseAInterpretedStringLitExpr(AInterpretedStringLitExpr node) {
        this.inAInterpretedStringLitExpr(node);

        buffer.append(node.getInterpretedStringLit().getText());

        this.outAInterpretedStringLitExpr(node);
    }

    @Override
    public void caseARawStringLitExpr(ARawStringLitExpr node) {
        this.inARawStringLitExpr(node);

        String rawString = node.getRawStringLit().getText();
        StringBuffer pythonString = new StringBuffer();

        pythonString.append('r');
        pythonString.append('"');
        pythonString.append(rawString.substring(1, rawString.length() - 1));
        pythonString.append('"');

        buffer.append(pythonString.toString());

        this.outARawStringLitExpr(node);
    }

    /**
     * Private methods
     *
     */
    private void addSpace() {
        buffer.append(' ');
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

    private void generateStatement(PStmt e) {
        if (e instanceof AEmptyStmt) {
            return;
        }

        addTabs();
        e.apply(this);
        addLines(1);
    }

    private void enterCodeBlock() {
        addLines(1);
        tabDepth++;
    }

    private void exitCodeBlock(boolean addPass) {
        /**
         * Add empty function call to prevent unexpected indented block
         */
        if (addPass) {
            addTabs();
            buffer.append("pass");
            addLines(1);
        }
        tabDepth--;
    }

    private boolean isBlockEmpty(List<PStmt> copy) {
        boolean flag = true;

        for (PStmt e : copy) {
            if (!(e instanceof AEmptyStmt)) {
                flag = false;
                break;
            }
        }

        return flag;
    }

}
