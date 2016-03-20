package golite.type;

import golite.analysis.*;
import golite.node.*;
import golite.exception.*;
import golite.util.*;
import golite.symbol.*;
import java.util.*;

public class TypeChecker extends DepthFirstAdapter {

    /* Class attributes */
    private SymbolTable symbolTable;
    private HashMap<Node, PTypeExpr> typeTable;
    private LineAndPos lineAndPos;

    /** Constructor **/
    public TypeChecker() {
        symbolTable = new SymbolTable();
        typeTable = new HashMap<Node, PTypeExpr>();
        lineAndPos = new LineAndPos();
    }

    public TypeChecker(SymbolTable symbolTable, HashMap<Node, PTypeExpr> typeTable) {
        this.symbolTable = symbolTable;
        this.typeTable = typeTable;
        typeTable = new HashMap<Node, PTypeExpr>();
        lineAndPos = new LineAndPos();
    }

    @Override
    public void inStart(Start node)
    {
        symbolTable.enterScope();
    }

    @Override
    public void outStart(Start node)
    {
        symbolTable.exitScope();
        symbolTable.printSymbols();
        for (Node n: typeTable.keySet())
        {
            System.out.println("Node: " + n + " Key: " + n.getClass() + " Value: " + typeTable.get(n).getClass());
        }
    }

    @Override
    public void caseAVarsTopDec(AVarsTopDec node) {
        // taken care of by SymbolTableBuilder
    }

    @Override
    public void caseATypesTopDec(ATypesTopDec node) {
        // taken care of by SymbolTableBuilder
    }

    /* Type check plus op-assign statement */
    @Override
    public void outAPlusAssignStmt(APlusAssignStmt node) {
        PTypeExpr lhs = typeTable.get(node.getLhs());
        PTypeExpr rhs = typeTable.get(node.getRhs());

        // (to-do) ...
    }

    /* Type check numeric op-assign statement */
    @Override
    public void outAMinusAssignStmt(AMinusAssignStmt node) {
        PTypeExpr lhs = typeTable.get(node.getLhs());
        PTypeExpr rhs = typeTable.get(node.getRhs());
        if (!isNumericType(lhs) || !isNumericType(rhs)) {
            Node errorNode = !isNumericType(lhs) ? node.getLhs() : node.getRhs();
            callTypeCheckException(errorNode, "Binary operand '-' cannot be applied to non-numeric type");
        }
        if (isIntType(lhs) && isFloatType(rhs)) {
            callTypeCheckException(node.getRhs(), "Op assign '-=' - cannot assign float to int variable");
        }
    }

    @Override
    public void outAStarAssignStmt(AStarAssignStmt node) {
        PTypeExpr lhs = typeTable.get(node.getLhs());
        PTypeExpr rhs = typeTable.get(node.getRhs());
        if (!isNumericType(lhs) || !isNumericType(rhs)) {
            Node errorNode = !isNumericType(lhs) ? node.getLhs() : node.getRhs();
            callTypeCheckException(errorNode, "Binary operand '*' cannot be applied to non-numeric type");
        }
        if (isIntType(lhs) && isFloatType(rhs)) {
            callTypeCheckException(node.getRhs(), "Op assign '*=' - cannot assign float to int variable");
        }
    }

    @Override
    public void outASlashAssignStmt(ASlashAssignStmt node) {
        PTypeExpr lhs = typeTable.get(node.getLhs());
        PTypeExpr rhs = typeTable.get(node.getRhs());
        if (!isNumericType(lhs) || !isNumericType(rhs)) {
            Node errorNode = !isNumericType(lhs) ? node.getLhs() : node.getRhs();
            callTypeCheckException(errorNode, "Binary operand '/' cannot be applied to non-numeric type");
        }
        if (isIntType(lhs) && isFloatType(rhs)) {
            callTypeCheckException(node.getRhs(), "Op assign '/=' - cannot assign float to int variable");
        }
    }

    /* Type check int op-assign statements */
    @Override
    public void outAPercAssignStmt(APercAssignStmt node) {
        PTypeExpr lhs = typeTable.get(node.getLhs());
        PTypeExpr rhs = typeTable.get(node.getRhs());
        if (!isIntType(lhs) || !isIntType(rhs)) {
            Node errorNode = !isIntType(lhs) ? node.getLhs() : node.getRhs();
            callTypeCheckException(errorNode, " Op assign '%=' - cannot be applied to non-int type");
        }
    }

    @Override
    public void outAAndAssignStmt(AAndAssignStmt node) {
        PTypeExpr lhs = typeTable.get(node.getLhs());
        PTypeExpr rhs = typeTable.get(node.getRhs());
        if (!isIntType(lhs) || !isIntType(rhs)) {
            Node errorNode = !isIntType(lhs) ? node.getLhs() : node.getRhs();
            callTypeCheckException(errorNode, " Op assign '&=' - cannot be applied to non-int type");
        }
    }

    @Override
    public void outAPipeAssignStmt(APipeAssignStmt node) {
        PTypeExpr lhs = typeTable.get(node.getLhs());
        PTypeExpr rhs = typeTable.get(node.getRhs());
        if (!isIntType(lhs) || !isIntType(rhs)) {
            Node errorNode = !isIntType(lhs) ? node.getLhs() : node.getRhs();
            callTypeCheckException(errorNode, " Op assign '|=' - cannot be applied to non-int type");
        }
    }

    @Override
    public void outACarotAssignStmt(ACarotAssignStmt node) {
        PTypeExpr lhs = typeTable.get(node.getLhs());
        PTypeExpr rhs = typeTable.get(node.getRhs());
        if (!isIntType(lhs) || !isIntType(rhs)) {
            Node errorNode = !isIntType(lhs) ? node.getLhs() : node.getRhs();
            callTypeCheckException(errorNode, " Op assign '^=' - cannot be applied to non-int type");
        }
    }

    @Override
    public void caseAAmpCarotAssignStmt(AAmpCarotAssignStmt node) {
        PTypeExpr lhs = typeTable.get(node.getLhs());
        PTypeExpr rhs = typeTable.get(node.getRhs());
        if (!isIntType(lhs) || !isIntType(rhs)) {
            Node errorNode = !isIntType(lhs) ? node.getLhs() : node.getRhs();
            callTypeCheckException(errorNode, " Op assign '&^=' - cannot be applied to non-int type");
        }
    }

    @Override
    public void caseALshiftAssignStmt(ALshiftAssignStmt node) {
        PTypeExpr lhs = typeTable.get(node.getLhs());
        PTypeExpr rhs = typeTable.get(node.getRhs());
        if (!isIntType(lhs) || !isIntType(rhs)) {
            Node errorNode = !isIntType(lhs) ? node.getLhs() : node.getRhs();
            callTypeCheckException(errorNode, " Op assign '<<=' - cannot be applied to non-int type");
        }
    }

    @Override
    public void caseARshiftAssignStmt(ARshiftAssignStmt node) {
        PTypeExpr lhs = typeTable.get(node.getLhs());
        PTypeExpr rhs = typeTable.get(node.getRhs());
        if (!isIntType(lhs) || !isIntType(rhs)) {
            Node errorNode = !isIntType(lhs) ? node.getLhs() : node.getRhs();
            callTypeCheckException(errorNode, " Op assign '>>=' - cannot be applied to non-int type");
        }
    }

    /* Type check increment & decrement statements */
    @Override
    public void caseAIncrStmt(AIncrStmt node) {
        if (node.getExpr() != null) {
            node.getExpr().apply(this);
            if (!isNumericType(typeTable.get(node.getExpr()))) {
                callTypeCheckException(node.getExpr(), "Increment statement - operand is not of numeric type");
            }
        }
    }

    @Override
    public void caseADecrStmt(ADecrStmt node) {
        if (node.getExpr() != null) {
            node.getExpr().apply(this);
            if (!isNumericType(typeTable.get(node.getExpr()))) {
                callTypeCheckException(node.getExpr(), "Decrement statement - operand is not of numeric type");
            }
        }
    }

    /* Type check print & println statements */
    @Override
    public void caseAPrintStmt(APrintStmt node) {
        List<PExpr> copy = new ArrayList<PExpr>(node.getExpr());
        for (PExpr e : copy) {
            e.apply(this);
            if (!isBaseType(typeTable.get(e))) {
                callTypeCheckException(e, "Print statement - expression is not of base type");
            }
        }
    }

    @Override
    public void caseAPrintlnStmt(APrintlnStmt node) {
        List<PExpr> copy = new ArrayList<PExpr>(node.getExpr());
        for (PExpr e : copy) {
            e.apply(this);
            if (!isBaseType(typeTable.get(e))) {
                callTypeCheckException(e, "Println statement - expression is not of base type");
            }
        }
    }

    /* Type check continue & break statement */
    @Override
    public void caseAContinueStmt(AContinueStmt node) {
        // Trivially well-typed
    }

    @Override
    public void caseABreakStmt(ABreakStmt node) {
        // Trivially well-typed
    }

    /* Type check return statement */
    @Override
    public void caseAReturnStmt(AReturnStmt node) {
        AFuncTopDec funcDec = getParentFuncDec(node);
        if (node.getExpr() != null) {
            node.getExpr().apply(this);
            if (funcDec.getTypeExpr() == null) {
                callTypeCheckException(node.getExpr(), "Return statement - declared function has no return type");
            }
            if (!isSameType(typeTable.get(node.getExpr()), funcDec.getTypeExpr())) {
                callTypeCheckException(node.getExpr(), "Return statement - return type does not match declared function");
            }
        } else {
            if (funcDec.getTypeExpr() != null) {
                callTypeCheckException(null, "Return statement - declared function has return type, cannot return nothing");
            }
        }
    }

    /* Type check if-else statement */
    @Override
    public void caseAIfElseStmt(AIfElseStmt node) {
        if (node.getCondition() != null) {
            node.getCondition().apply(this);
        }
        {
            symbolTable.enterScope();
            List<PStmt> copy = new ArrayList<PStmt>(node.getIfBlock());
            for (PStmt e : copy) {
                e.apply(this);
            }
            symbolTable.exitScope();
        }
        {
            List<PElseif> copy = new ArrayList<PElseif>(node.getElseif());
            for (PElseif e : copy) {
                e.apply(this);
            }
        }
        {
            symbolTable.enterScope();
            List<PStmt> copy = new ArrayList<PStmt>(node.getElseBlock());
            for (PStmt e : copy) {
                e.apply(this);
            }
            symbolTable.exitScope();
        }
    }

    @Override
    public void caseAElifElseif(AElifElseif node) {
        if (node.getCondition() != null) {
            node.getCondition().apply(this);
        }
        {
            symbolTable.enterScope();
            List<PStmt> copy = new ArrayList<PStmt>(node.getBlock());
            for (PStmt e : copy) {
                e.apply(this);
            }
            symbolTable.exitScope();
        }
    }

    @Override
    public void caseAConditionCondition(AConditionCondition node) {
        if (node.getStmt() != null) {
            node.getStmt().apply(this);
        }
        if (node.getExpr() != null) {
            node.getExpr().apply(this);
            if (!isBoolType(typeTable.get(node.getExpr()))) {
                callTypeCheckException(node.getExpr(), "If-Else statement - condition must evaluate bool type");
            }
        }
    }

    /* Type check swtich statement */
    @Override
    public void caseASwitchStmt(ASwitchStmt node) {
        if (node.getStmt() != null) {
            node.getStmt().apply(this);
        }
        if (node.getExpr() != null) {
            node.getExpr().apply(this);
        }
        {
            symbolTable.enterScope();
            List<PCaseBlock> copy = new ArrayList<PCaseBlock>(node.getCaseBlock());
            for (PCaseBlock e : copy) {
                e.apply(this);
            }
            symbolTable.exitScope();
        }
    }

    @Override
    public void caseABlockCaseBlock(ABlockCaseBlock node) {
        if (node.getCaseCondition() != null) {
            node.getCaseCondition().apply(this);
        }
        {
            List<PStmt> copy = new ArrayList<PStmt>(node.getStmt());
            for (PStmt e : copy) {
                e.apply(this);
            }
        }
    }

    @Override
    public void caseAExprsCaseCondition(AExprsCaseCondition node) {
        if (((ASwitchStmt) node.parent().parent()).getExpr() != null) {
            PExpr expr = ((ASwitchStmt) node.parent().parent()).getExpr();
            List<PExpr> copy = new ArrayList<PExpr>(node.getExpr());
            for (PExpr e : copy) {
                e.apply(this);
                if (!isSameType(typeTable.get(expr), typeTable.get(e))) {
                    callTypeCheckException(e, "Switch statement - case expression type does not match");
                }
            }
        } else {
            List<PExpr> copy = new ArrayList<PExpr>(node.getExpr());
            for (PExpr e : copy) {
                e.apply(this);
                if (!isBoolType(typeTable.get(e))) {
                    callTypeCheckException(e, "Switch statement - case expression must evaluate to bool type");
                }
            }
        }
    }

    @Override
    public void caseADefaultCaseCondition(ADefaultCaseCondition node) {
        // Trivially well-typed
    }

    /* Type check loop (for & while) statements */
    @Override
    public void caseAForLoopStmt(AForLoopStmt node) {
        if (node.getInit() != null) {
            node.getInit().apply(this);
        }
        if (node.getExpr() != null) {
            node.getExpr().apply(this);
            if (!isBoolType(typeTable.get(node.getExpr()))) {
                callTypeCheckException(node.getExpr(), "For loop statement - loop expression must evaluate to bool type");
            }
        }
        if (node.getEnd() != null) {
            node.getEnd().apply(this);
        }
        {
            symbolTable.enterScope();
            List<PStmt> copy = new ArrayList<PStmt>(node.getBlock());
            for (PStmt e : copy) {
                e.apply(this);
            }
            symbolTable.exitScope();
        }
    }

    @Override
    public void caseAWhileLoopStmt(AWhileLoopStmt node) {
        if (node.getExpr() != null) {
            node.getExpr().apply(this);
            if (!isBoolType(typeTable.get(node.getExpr()))) {
                callTypeCheckException(node.getExpr(), "For loop statement - loop expression mush evaluate to bool type");
            }
        }
        {
            symbolTable.enterScope();
            List<PStmt> copy = new ArrayList<PStmt>(node.getBlock());
            for (PStmt e : copy) {
                e.apply(this);
            }
            symbolTable.exitScope();
        }
    }

    /* Type check binary arithemic operators */
    @Override
    public void outAAddExpr(AAddExpr node) {
        PTypeExpr left = typeTable.get(node.getLeft());
        PTypeExpr right = typeTable.get(node.getRight());
        if (isNumericType(left) && isNumericType(right)) {
            if (isIntType(left) && isIntType(right)) {
                typeTable.put(node, new AIntTypeExpr());
            } else {
                typeTable.put(node, new AFloatTypeExpr());
            }
        } else if (isStringType(left) && isStringType(right)) {
            typeTable.put(node, new AStringTypeExpr());
        } else {
            callTypeCheckException(node.getLeft(), "Binary operator '+' has miss-matched operands");
        }
    }

    @Override
    public void outASubtractExpr(ASubtractExpr node) {
        PTypeExpr left = typeTable.get(node.getLeft());
        PTypeExpr right = typeTable.get(node.getRight());
        if (!isNumericType(left) || !isNumericType(right)) {
            Node errorNode = !isNumericType(left) ? node.getLeft() : node.getRight();
            callTypeCheckException(errorNode, "Binary operator '-' can only be applied to numeric");
        }
        if (isFloatType(left) || isFloatType(right)) {
            typeTable.put(node, new AFloatTypeExpr());
        } else {
            typeTable.put(node, new AIntTypeExpr());
        }
    }

    @Override
    public void outAMultExpr(AMultExpr node) {
        PTypeExpr left = typeTable.get(node.getLeft());
        PTypeExpr right = typeTable.get(node.getRight());
        if (!isNumericType(left) || !isNumericType(right)) {
            Node errorNode = !isNumericType(left) ? node.getLeft() : node.getRight();
            callTypeCheckException(errorNode, "Binary operator '*' can only be applied to numeric");
        }
        if (isFloatType(left) || isFloatType(right)) {
            typeTable.put(node, new AFloatTypeExpr());
        } else {
            typeTable.put(node, new AIntTypeExpr());
        }
    }

    @Override
    public void outADivExpr(ADivExpr node) {
        PTypeExpr left = typeTable.get(node.getLeft());
        PTypeExpr right = typeTable.get(node.getRight());
        if (!isNumericType(left) || !isNumericType(right)) {
            Node errorNode = !isNumericType(left) ? node.getLeft() : node.getRight();
            callTypeCheckException(errorNode, "Binary operator '/' can only be applied to numeric");
        }
        if (isFloatType(left) || isFloatType(right)) {
            typeTable.put(node, new AFloatTypeExpr());
        } else {
            typeTable.put(node, new AIntTypeExpr());
        }
    }

    @Override
    public void outAModExpr(AModExpr node) {
        PTypeExpr left = typeTable.get(node.getLeft());
        PTypeExpr right = typeTable.get(node.getRight());
        if (!isIntType(left) || !isIntType(right)) {
            Node errorNode = !isNumericType(left) ? node.getLeft() : node.getRight();
            callTypeCheckException(errorNode, "Binary operator '%' can only be applied to integer");
        }
        typeTable.put(node, new AIntTypeExpr());
    }

    /* Type check binary bit operators */
    @Override
    public void outABitAndExpr(ABitAndExpr node) {
        PTypeExpr left = typeTable.get(node.getLeft());
        PTypeExpr right = typeTable.get(node.getRight());
        if (!isIntType(left) || !isIntType(right)) {
            Node errorNode = !isIntType(left) ? node.getLeft() : node.getRight();
            callTypeCheckException(errorNode, "Binary operator '&' can only be applied to integer");
        }
        typeTable.put(node, new AIntTypeExpr());
    }

    @Override
    public void outABitOrExpr(ABitOrExpr node) {
        PTypeExpr left = typeTable.get(node.getLeft());
        PTypeExpr right = typeTable.get(node.getRight());
        if (!isIntType(left) || !isIntType(right)) {
            Node errorNode = !isIntType(left) ? node.getLeft() : node.getRight();
            callTypeCheckException(errorNode, "Binary operator '|' can only be applied to integer");
        }
        typeTable.put(node, new AIntTypeExpr());
    }

    @Override
    public void outABitXorExpr(ABitXorExpr node) {
        PTypeExpr left = typeTable.get(node.getLeft());
        PTypeExpr right = typeTable.get(node.getRight());
        if (!isIntType(left) || !isIntType(right)) {
            Node errorNode = !isIntType(left) ? node.getLeft() : node.getRight();
            callTypeCheckException(errorNode, "Binary operator '^' can only be applied to integer");
        }
        typeTable.put(node, new AIntTypeExpr());
    }

    @Override
    public void outABitClearExpr(ABitClearExpr node) {
        PTypeExpr left = typeTable.get(node.getLeft());
        PTypeExpr right = typeTable.get(node.getRight());
        if (!isIntType(left) || !isIntType(right)) {
            Node errorNode = !isIntType(left) ? node.getLeft() : node.getRight();
            callTypeCheckException(errorNode, "Binary operator '&^' can only be applied to integer");
        }
        typeTable.put(node, new AIntTypeExpr());
    }

    @Override
    public void outABitLshiftExpr(ABitLshiftExpr node) {
        PTypeExpr left = typeTable.get(node.getLeft());
        PTypeExpr right = typeTable.get(node.getRight());
        if (!isIntType(left) || !isIntType(right)) {
            Node errorNode = !isIntType(left) ? node.getLeft() : node.getRight();
            callTypeCheckException(errorNode, "Binary operator '<<' can only be applied to integer");
        }
        typeTable.put(node, new AIntTypeExpr());
    }

    @Override
    public void outABitRshiftExpr(ABitRshiftExpr node) {
        PTypeExpr left = typeTable.get(node.getLeft());
        PTypeExpr right = typeTable.get(node.getRight());
        if (!isIntType(left) || !isIntType(right)) {
            Node errorNode = !isIntType(left) ? node.getLeft() : node.getRight();
            callTypeCheckException(errorNode, "Binary operator '>>' can only be applied to integer");
        }
        typeTable.put(node, new AIntTypeExpr());
    }

    /* Type check unary operators */
    @Override
    public void outAPosExpr(APosExpr node) {
        PTypeExpr type = typeTable.get(node.getExpr());
        if (!isNumericType(type)) {
            callTypeCheckException(node.getExpr(), "Unary operator '+' can only be applied to numeric");
        }
        if (isFloatType(type)) {
            typeTable.put(node, new AFloatTypeExpr());
        } else {
            typeTable.put(node, new AIntTypeExpr());
        }
    }

    @Override
    public void outANegExpr(ANegExpr node) {
        PTypeExpr type = typeTable.get(node.getExpr());
        if (!isNumericType(type)) {
            callTypeCheckException(node.getExpr(), "Unary operator '-' can only be applied to numeric");
        }
        if (isFloatType(type)) {
            typeTable.put(node, new AFloatTypeExpr());
        } else {
            typeTable.put(node, new AIntTypeExpr());
        }
    }

    @Override
    public void outABitCompExpr(ABitCompExpr node) {
        PTypeExpr type = typeTable.get(node.getExpr());
        if (isIntType(type)) {
            callTypeCheckException(node.getExpr(), "Unary operator '^' can only be applied to integer");
        }
        typeTable.put(node, new AIntTypeExpr());
    }

    @Override
    public void outANotExpr(ANotExpr node) {
        PTypeExpr type = typeTable.get(node.getExpr());
        if (isBoolType(type)) {
            callTypeCheckException(node.getExpr(), "Unary operator '!' can only be applied to boolean");
        }
        typeTable.put(node, new ABoolTypeExpr());
    }

    /* Type check relational operands */
    @Override
    public void outAEqExpr(AEqExpr node) {
        PTypeExpr left = typeTable.get(node.getLeft());
        PTypeExpr right = typeTable.get(node.getRight());
        if (!isComparableType(left) || !isComparableType(right)) {
            Node errorNode = !isComparableType(left) ? node.getLeft() : node.getRight();
            callTypeCheckException(errorNode, "Relational operator '==' can only be applied to comparable");
        }
        typeTable.put(node, new ABoolTypeExpr());
    }

    @Override
    public void outANeqExpr(ANeqExpr node) {
        PTypeExpr left = typeTable.get(node.getLeft());
        PTypeExpr right = typeTable.get(node.getRight());
        if (!isComparableType(left) || !isComparableType(right)) {
            Node errorNode = !isComparableType(left) ? node.getLeft() : node.getRight();
            callTypeCheckException(errorNode, "Relational operator '!=' can only be applied to comparable");
        }
        typeTable.put(node, new ABoolTypeExpr());
    }

    @Override
    public void outALtExpr(ALtExpr node) {
        PTypeExpr left = typeTable.get(node.getLeft());
        PTypeExpr right = typeTable.get(node.getRight());
        if (!isOrderedType(left) || !isOrderedType(right)) {
            Node errorNode = !isOrderedType(left) ? node.getLeft() : node.getRight();
            callTypeCheckException(errorNode, "Relational operator '<' can only be applied to ordered");
        }
        typeTable.put(node, new ABoolTypeExpr());
    }

    @Override
    public void outALteExpr(ALteExpr node) {
        PTypeExpr left = typeTable.get(node.getLeft());
        PTypeExpr right = typeTable.get(node.getRight());
        if (!isOrderedType(left) || !isOrderedType(right)) {
            Node errorNode = !isOrderedType(left) ? node.getLeft() : node.getRight();
            callTypeCheckException(errorNode, "Relational operator '<=' can only be applied to ordered");
        }
        typeTable.put(node, new ABoolTypeExpr());
    }

    @Override
    public void outAGtExpr(AGtExpr node) {
        PTypeExpr left = typeTable.get(node.getLeft());
        PTypeExpr right = typeTable.get(node.getRight());
        if (!isOrderedType(left) || !isOrderedType(right)) {
            Node errorNode = !isOrderedType(left) ? node.getLeft() : node.getRight();
            callTypeCheckException(errorNode, "Relational operator '>' can only be applied to ordered");
        }
        typeTable.put(node, new ABoolTypeExpr());
    }

    @Override
    public void outAGteExpr(AGteExpr node) {
        PTypeExpr left = typeTable.get(node.getLeft());
        PTypeExpr right = typeTable.get(node.getRight());
        if (!isOrderedType(left) || !isOrderedType(right)) {
            Node errorNode = !isOrderedType(left) ? node.getLeft() : node.getRight();
            callTypeCheckException(errorNode, "Relational operator '>=' can only be applied to ordered");
        }
        typeTable.put(node, new ABoolTypeExpr());
    }

    /* Type check conditional operators */
    @Override
    public void outAAndExpr(AAndExpr node) {
        PTypeExpr left = typeTable.get(node.getLeft());
        PTypeExpr right = typeTable.get(node.getRight());
        if (!isBoolType(left) || !isBoolType(right)) {
            Node errorNode = !isBoolType(left) ? node.getLeft() : node.getRight();
            callTypeCheckException(errorNode, "Conditional operator '&&' can only be applied to boolean");
        }
        typeTable.put(node, new ABoolTypeExpr());
    }

    @Override
    public void outAOrExpr(AOrExpr node) {
        PTypeExpr left = typeTable.get(node.getLeft());
        PTypeExpr right = typeTable.get(node.getRight());
        if (!isBoolType(left) || !isBoolType(right)) {
            Node errorNode = !isBoolType(left) ? node.getLeft() : node.getRight();
            callTypeCheckException(errorNode, "Conditional operator '||' can only be applied to boolean");
        }
        typeTable.put(node, new ABoolTypeExpr());
    }

    /* Type check numeric literals */
    @Override
    public void outAIntLitExpr(AIntLitExpr node) {
        typeTable.put(node, new AIntTypeExpr());
    }

    @Override
    public void outAOctLitExpr(AOctLitExpr node) {
        typeTable.put(node, new AIntTypeExpr());
    }

    @Override
    public void outAHexLitExpr(AHexLitExpr node) {
        typeTable.put(node, new AIntTypeExpr());
    }

    @Override
    public void outAFloatLitExpr(AFloatLitExpr node) {
        typeTable.put(node, new AFloatTypeExpr());
    }

    @Override
    public void outARuneLitExpr(ARuneLitExpr node) {
        typeTable.put(node, new ARuneTypeExpr());
    }

    /* Type check string literals */
    @Override
    public void outAInterpretedStringLitExpr(AInterpretedStringLitExpr node) {
        typeTable.put(node, new AStringTypeExpr());
    }

    @Override
    public void outARawStringLitExpr(ARawStringLitExpr node) {
        typeTable.put(node, new AStringTypeExpr());
    }

    /* Helper methods */
    private boolean isBoolType(PTypeExpr node) {
        return node instanceof ABoolTypeExpr;
    }

    private boolean isIntType(PTypeExpr node) {
        return (node instanceof AIntTypeExpr) || (node instanceof ARuneTypeExpr);
    }

    private boolean isFloatType(PTypeExpr node) {
        return node instanceof AFloatTypeExpr;
    }

    private boolean isNumericType(PTypeExpr node) {
        return isIntType(node) || isFloatType(node);
    }

    private boolean isStringType(PTypeExpr node) {
        return node instanceof AStringTypeExpr;
    }

    private boolean isOrderedType(PTypeExpr node) {
        return isNumericType(node) || isStringType(node);
    }

    public boolean isComparableType(PTypeExpr node) {
        return isOrderedType(node) || isBoolType(node);
    }

    private boolean isStructType(PTypeExpr node) {
        return node instanceof AStructTypeExpr;
    }

    private boolean isBaseType(PTypeExpr node) {
        return isBoolType(node) || isNumericType(node) || isStringType(node);
    }

    private boolean isSameType(PTypeExpr node1, PTypeExpr node2) {
        return ((node1 instanceof ABoolTypeExpr) && (node2 instanceof ABoolTypeExpr))
                || ((node1 instanceof AIntTypeExpr) && (node2 instanceof AIntTypeExpr))
                || ((node1 instanceof AFloatTypeExpr) && (node2 instanceof AFloatTypeExpr))
                || ((node1 instanceof ARuneTypeExpr) && (node2 instanceof ARuneTypeExpr))
                || ((node1 instanceof AStringTypeExpr) && (node2 instanceof AStringTypeExpr))
                || ((node1 instanceof ASliceTypeExpr) && (node2 instanceof ASliceTypeExpr))
                || (node1.getClass() == node2.getClass());
    }

    private AFuncTopDec getParentFuncDec(AReturnStmt node) {
        Node parent = node;
        while (!(parent instanceof AFuncTopDec)) {
            parent = parent.parent();
        }
        return (AFuncTopDec) parent;
    }

    private void callTypeCheckException(Node node, String s) {
        String message = "";
        if (node != null) {
            node.apply(lineAndPos);
            message += "[" + lineAndPos.getLine(node) + "," + lineAndPos.getPos(node) + "] ";
        }
        message += s;
        TypeCheckException e = new TypeCheckException(message);
        e.printStackTrace();
        System.exit(1);
    }

    /* Change scope when entering and exiting functions */
    @Override
    public void inAFuncTopDec(AFuncTopDec node)
    {
        symbolTable.enterScope();
        List<PArgGroup> argGroups = node.getArgGroup();
        for (PArgGroup a: argGroups)
        {
            for(TId i: ((AArgArgGroup) a).getId())
            {
                symbolTable.addSymbol(i.getText(), a);
                typeTable.put(a, ((AArgArgGroup) a).getTypeExpr());
            }
        }
        defaultIn(node);
    }

    @Override
    public void outAFuncTopDec(AFuncTopDec node)
    {
        defaultOut(node);
        symbolTable.exitScope();
    }

    /* Add var and type specifications to symbol table */

    @Override
    public void outASpecVarSpec(ASpecVarSpec node)
    {
        {
            List<TId> ids = new ArrayList<TId>(node.getId());
            List<PExpr> exprs = node.getExpr();
            for (TId e: ids)
            {
                symbolTable.addSymbol(e.getText(), node);
            }
            if (node.getTypeExpr() != null)
            {
                for (PExpr e: exprs)
                {
                    if (typeTable.get(e).getClass() != node.getTypeExpr().getClass())
                    {
                        callTypeCheckException(e, "Expression type does not match declared variable type");
                    }

                }
                for (TId e: ids)
                {
                        typeTable.put(e, node.getTypeExpr());
                }
            }
            else
            {
                for (int i = 0; i < ids.size(); i++)
                {
                    typeTable.put(ids.get(i), typeTable.get(exprs.get(i)));
                }
            }
        }
    }

    private void putArgGroup(String name, AArgArgGroup node) {
        for (TId id: node.getId())
        {
            String newName = name + "." + id.getText();
            symbolTable.addSymbol(newName, node);
            typeTable.put(node, getType(node));
            putTypeExpr(newName, node.getTypeExpr());
        }
    }

    private void putTypeExpr(String name, PTypeExpr node)
    {
        //TODO: Handle other types of type expressions
        if (isStructType(node))
        {
            for (PArgGroup a: ((AStructTypeExpr) node).getArgGroup())
            {
                putArgGroup(name, (AArgArgGroup) a);
            }
        }
    }

    @Override
    public void inASpecTypeSpec(ASpecTypeSpec node)
    {
        String name = node.getId().getText();
        symbolTable.addSymbol(name, node.getTypeExpr());
        putTypeExpr(name, node.getTypeExpr());
    }

    @Override
    public void outAShortAssignStmt(AShortAssignStmt node)
    {
        {
            List<TId> ids = new ArrayList<TId>(node.getId());
            List<PExpr> exprs = new ArrayList<PExpr>(node.getExpr());
            int length = ids.size();
            for(int i=0; i < length; i++)
            {
                symbolTable.addSymbol(ids.get(i).getText(), node);
                typeTable.put(ids.get(i), getType(exprs.get(i)));
            }
        }
    }

    /* Type Check Field Expressions */

    @Override
    public void outAFieldExpr(AFieldExpr node)
    {
        //Check that Expr is well typed with Struct type
        //Check that Struct type has a field named id
        PTypeExpr type = getType(node.getExpr());
        String id = node.getId().getText();
        if (type instanceof AStructTypeExpr)
        {
            for (PArgGroup a: ((AStructTypeExpr) type).getArgGroup())
            {
                PTypeExpr argType = ((AArgArgGroup) a).getTypeExpr();
                for (TId argId: ((AArgArgGroup) a).getId())
                {
                    if (argId.getText().equals(id))
                    {
                        typeTable.put(node, argType); // Well typed!
                    }
                }
            }
        }
        else
        {
            callTypeCheckException(node, "Fields calls can only be used on struct types");
        }
    }

    /* Type check assignment statements */

    @Override
    public void outAAssignStmt(AAssignStmt node)
    {
        List<PExpr> ids = node.getLhs();
        List<PExpr> exprs = node.getRhs();
        int length = ids.size();
        for (int i=0; i < length; i++)
        {
            if (getType(ids.get(i)).getClass() != getType(exprs.get(i)).getClass())
            {
                callTypeCheckException(node, "Types on left hand and right hand sides of assignment statements must be assignment compatable");
            }
        }
    }

    /* Add variable expressions to type table */
    @Override
    public void inAVariableExpr(AVariableExpr node)
    {
        defaultIn(node);
        PTypeExpr type = getType(node.getId());
        typeTable.put(node, type);
    }

    /* Append type checking */
    @Override
    public void outAAppendExpr(AAppendExpr node)
    {
        PTypeExpr idType = getType(node.getId());
        if (idType instanceof ASliceTypeExpr)
        {
            PTypeExpr sliceElementType = ((ASliceTypeExpr) idType).getTypeExpr();
            PTypeExpr exprType = getType(node.getExpr());
            if (sliceElementType.getClass() == exprType.getClass())
            {
                typeTable.put(node, idType); // Well typed!
            }
            else
            {
                callTypeCheckException(node, "Expression and slice types do not match");
            }
        }
        else
        {
            callTypeCheckException(node, "Append must be passed a slice variable and an expression");
        }
    }

    /* Type check type casts and function calls */
    @Override
    public void outATypeCastExpr(ATypeCastExpr node)
    {
        PTypeExpr castType = getType(node);
        PTypeExpr paramType = getType(node.getExpr());
        if    (castType instanceof AIntTypeExpr
            || castType instanceof AFloatTypeExpr
            || castType instanceof ARuneTypeExpr
            || castType instanceof ABoolTypeExpr)
        {
            if    (paramType instanceof AIntTypeExpr
                || paramType instanceof AFloatTypeExpr
                || paramType instanceof ARuneTypeExpr
                || paramType instanceof ABoolTypeExpr)
            {
                typeTable.put(node, castType); // Well typed!
            }
            else
            {
                callTypeCheckException(node, "Invalid type cast. Use int, float64, bool, rune, or a type alias that maps to one of those four.");
            }
        }
        else
        {
            callTypeCheckException(node, "Invaid type cast. Use int, float64, bool, rune, or a type alias that maps to one of those four.");
        }
    }

    @Override
    public void outAFuncCallExpr(AFuncCallExpr node)
    {
        String id = node.getId().getText();
        Node decl = symbolTable.getSymbol(id, node);
        List<PArgGroup> argGroups = ((AFuncTopDec) decl).getArgGroup();
        ArrayList<PTypeExpr> argTypes = new ArrayList<PTypeExpr>();
        List<PExpr> paramTypes = node.getExpr();
        for (PArgGroup a: argGroups)
        {
            PTypeExpr argType = getType(a);
            for (TId i: ((AArgArgGroup) a).getId())
            {
                argTypes.add(argType);
            }
        }
        for (int i = 0; i<argTypes.size(); i++)
        {
            if (argTypes.get(i).getClass() == getType(paramTypes.get(i)).getClass())
            {
                continue;
            }
            else
            {
                callTypeCheckException(node, "Function parameter types do not match with function signature");
            }

        }
        // Well typed!
        PTypeExpr type = getType(node);
        if (type != null)
        {
            typeTable.put(node, type);
        }
    }

    /* Type check Array Elements */
    @Override
    public void outAArrayElemExpr(AArrayElemExpr node)
    {
        PTypeExpr arrayType = getType(node.getArray());
        PTypeExpr idxType = getType(node.getIndex());
        if (idxType instanceof AIntTypeExpr)
        {
            if (arrayType instanceof AArrayTypeExpr)
            {
                typeTable.put(node, ((AArrayTypeExpr) arrayType).getTypeExpr()); // Well typed!
            }
            else if (arrayType instanceof ASliceTypeExpr)
            {
                typeTable.put(node, ((ASliceTypeExpr) arrayType).getTypeExpr()); // Well typed!
            }
            else
            {
                callTypeCheckException(node, "Indexing can only be used on arrays and slices");
            }
        }
        else
        {
            callTypeCheckException(node, "Index should be of type int");
        }
    }

    /* Type check expressions completely */
    @Override
    public void outAExprStmt(AExprStmt node)
    {
        PTypeExpr type = getType(node);
        if (type != null)
        {
            typeTable.put(node, type);
        }
    }

    /* More helper methods */

    private PTypeExpr getType(Node node)
    {
        //Takes in a node and returns the type node from the AST/typeTable
        if (node instanceof AVariableExpr)
        {
            return getType(((AVariableExpr) node).getId());
        }
        else if (node instanceof TId)
        {

            String id = ((TId) node).getText();
            Node declaration = symbolTable.getSymbol(id, node);
            System.out.println(declaration);
            System.out.println(declaration.getClass());
            if (declaration instanceof ASpecVarSpec)
            {
                ASpecVarSpec dec = (ASpecVarSpec) declaration;
                int idx = dec.getId().indexOf(id);
                PTypeExpr typeExpr = getType(declaration);
                if (typeExpr != null)
                {
                    return typeExpr;
                }
                else
                {
                    return typeTable.get(dec.getExpr().get(idx));
                }
            }
            else if (declaration instanceof ASpecTypeSpec)
            {
                ASpecTypeSpec dec = (ASpecTypeSpec) declaration;
                System.out.println(dec);
                return dec.getTypeExpr();
            }
            else if (declaration instanceof AStructTypeExpr)
            {
                return (AStructTypeExpr) declaration;
            }
            else if (declaration instanceof AShortAssignStmt)
            {
                AShortAssignStmt dec = (AShortAssignStmt) declaration;
                for (TId i: dec.getId())
                {
                    if (i.getText().equals(id))
                    {
                        int idx = dec.getId().indexOf(i);
                        return typeTable.get(dec.getExpr().get(idx));
                    }
                }
            }
            else if (declaration instanceof AArgArgGroup)
            {
                return typeTable.get(declaration);
            }
        }
        else if (node instanceof AIntLitExpr
                || node instanceof AFloatLitExpr
                || node instanceof ARuneLitExpr
                || node instanceof AOctLitExpr
                || node instanceof AHexLitExpr
                || node instanceof AInterpretedStringLitExpr
                || node instanceof ARawStringLitExpr)
        {
            return typeTable.get(node);
        }
        else if (node instanceof AShortAssignStmt)
        {
            return null;
        }
        else if (node instanceof ASpecVarSpec)
        {
            PTypeExpr type = ((ASpecVarSpec) node).getTypeExpr();
            if (type != null)
            {
                return type;
            }
            return null;
        }
        else if (node instanceof AStructTypeExpr)
        {
            return (AStructTypeExpr) node;
        }
        else if (node instanceof AArgArgGroup)
        {
            return ((AArgArgGroup) node).getTypeExpr();
        }
        else if (node instanceof AFieldExpr)
        {
            return typeTable.get(node);
        }
        else if (node instanceof ATypeCastExpr)
        {
            return ((ATypeCastExpr) node).getTypeExpr();
        }
        else if (node instanceof AArrayElemExpr)
        {
            return typeTable.get(node);
        }
        else if (node instanceof AExprStmt)
        {
            return getType(((AExprStmt) node).getExpr());
        }
        else if (node instanceof AFuncCallExpr)
        {
            String id = ((AFuncCallExpr) node).getId().getText();
            Node decl = symbolTable.getSymbol(id, node);
            if (decl instanceof AFuncTopDec)
            {
                return ((AFuncTopDec) decl).getTypeExpr();
            }
        }
        else
        {   try{
                return typeTable.get(node);
            }
            catch (Exception e)
            {
                System.out.println(node);
                callTypeCheckException(node, "Did not find type for " + node.getClass());
            }
        }
        return null;
    }
}
