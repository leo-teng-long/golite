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

    /**
     * Getter.
     */
    public HashMap<Node, PTypeExpr> getTypeTable() {
        return this.typeTable;
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
        symbolTable.exitScope();
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
        if (!isAssignable(node.getLhs())) {
            callTypeCheckException(node.getLhs(), "Op-assign '+=': LHS not assignable");
        }
        PTypeExpr lhs = typeTable.get(node.getLhs());
        PTypeExpr rhs = typeTable.get(node.getRhs());
        if (!isSameType(lhs, rhs)) {
            callTypeCheckException(node.getLhs(), "Op-assign '+=': mismatched operand type");
        }
        if (!isOrderedType(lhs) || !isOrderedType(rhs)) {
            Node errorNode = !isOrderedType(lhs) ? node.getLhs() : node.getRhs();
            callTypeCheckException(errorNode, "Op-assign '+=': not defined for non-numeric or non-string");
        }
    }

    /* Type check numeric op-assign statement */
    @Override
    public void outAMinusAssignStmt(AMinusAssignStmt node) {
        if (!isAssignable(node.getLhs())) {
            callTypeCheckException(node.getLhs(), "Op-assign '-=': LHS not assignable");
        }
        PTypeExpr lhs = typeTable.get(node.getLhs());
        PTypeExpr rhs = typeTable.get(node.getRhs());
        if (!isSameType(lhs, rhs)) {
            callTypeCheckException(node.getLhs(), "Op-assign '-=': mismatched operand type");
        }
        if (!isNumericType(lhs) || !isNumericType(rhs)) {
            Node errorNode = !isNumericType(lhs) ? node.getLhs() : node.getRhs();
            callTypeCheckException(errorNode, "Op-assign '-=': not defined for non-numeric");
        }
    }

    @Override
    public void outAStarAssignStmt(AStarAssignStmt node) {
        if (!isAssignable(node.getLhs())) {
            callTypeCheckException(node.getLhs(), "Op-assign '*=': LHS not assignable");
        }
        PTypeExpr lhs = typeTable.get(node.getLhs());
        PTypeExpr rhs = typeTable.get(node.getRhs());
        if (!isSameType(lhs, rhs)) {
            callTypeCheckException(node.getLhs(), "Op-assign '*=': mismatched operand type");
        }
        if (!isNumericType(lhs) || !isNumericType(rhs)) {
            Node errorNode = !isNumericType(lhs) ? node.getLhs() : node.getRhs();
            callTypeCheckException(errorNode, "Op-assign '*=': not defined for non-numeric");
        }
    }

    @Override
    public void outASlashAssignStmt(ASlashAssignStmt node) {
        if (!isAssignable(node.getLhs())) {
            callTypeCheckException(node.getLhs(), "Op-assign '/=': LHS not assignable");
        }
        PTypeExpr lhs = typeTable.get(node.getLhs());
        PTypeExpr rhs = typeTable.get(node.getRhs());
        if (!isSameType(lhs, rhs)) {
            callTypeCheckException(node.getLhs(), "Op-assign '/=': mismatched operand type");
        }
        if (!isNumericType(lhs) || !isNumericType(rhs)) {
            Node errorNode = !isNumericType(lhs) ? node.getLhs() : node.getRhs();
            callTypeCheckException(errorNode, "Op-assign '/=': not defined for non-numeric");
        }
    }

    /* Type check int op-assign statements */
    @Override
    public void outAPercAssignStmt(APercAssignStmt node) {
        if (!isAssignable(node.getLhs())) {
            callTypeCheckException(node.getLhs(), "Op-assign '%=': LHS not assignable");
        }
        PTypeExpr lhs = typeTable.get(node.getLhs());
        PTypeExpr rhs = typeTable.get(node.getRhs());
        if (!isSameType(lhs, rhs)) {
            callTypeCheckException(node.getLhs(), "Op-assign '%=': mismatched operand type");
        }
        if (!isIntOrRuneType(lhs) || !isIntOrRuneType(rhs)) {
            Node errorNode = !isIntOrRuneType(lhs) ? node.getLhs() : node.getRhs();
            callTypeCheckException(errorNode, "Op-assign '%=': not defined for non-integer");
        }
    }

    @Override
    public void outAAndAssignStmt(AAndAssignStmt node) {
        if (!isAssignable(node.getLhs())) {
            callTypeCheckException(node.getLhs(), "Op-assign '&=': LHS not assignable");
        }
        PTypeExpr lhs = typeTable.get(node.getLhs());
        PTypeExpr rhs = typeTable.get(node.getRhs());
        if (!isSameType(lhs, rhs)) {
            callTypeCheckException(node.getLhs(), "Op-assign '&=': mismatched operand type");
        }
        if (!isIntOrRuneType(lhs) || !isIntOrRuneType(rhs)) {
            Node errorNode = !isIntOrRuneType(lhs) ? node.getLhs() : node.getRhs();
            callTypeCheckException(errorNode, "Op-assign '&=': not defined for non-integer");
        }
    }

    @Override
    public void outAPipeAssignStmt(APipeAssignStmt node) {
        if (!isAssignable(node.getLhs())) {
            callTypeCheckException(node.getLhs(), "Op-assign '|=': LHS not assignable");
        }
        PTypeExpr lhs = typeTable.get(node.getLhs());
        PTypeExpr rhs = typeTable.get(node.getRhs());
        if (!isSameType(lhs, rhs)) {
            callTypeCheckException(node.getLhs(), "Op-assign '|=': mismatched operand type");
        }
        if (!isIntOrRuneType(lhs) || !isIntOrRuneType(rhs)) {
            Node errorNode = !isIntOrRuneType(lhs) ? node.getLhs() : node.getRhs();
            callTypeCheckException(errorNode, "Op-assign '|=': not defined for non-integer");
        }
    }

    @Override
    public void outACarotAssignStmt(ACarotAssignStmt node) {
        if (!isAssignable(node.getLhs())) {
            callTypeCheckException(node.getLhs(), "Op-assign '^=': LHS not assignable");
        }
        PTypeExpr lhs = typeTable.get(node.getLhs());
        PTypeExpr rhs = typeTable.get(node.getRhs());
        if (!isSameType(lhs, rhs)) {
            callTypeCheckException(node.getLhs(), "Op-assign '^=': mismatched operand type");
        }
        if (!isIntOrRuneType(lhs) || !isIntOrRuneType(rhs)) {
            Node errorNode = !isIntOrRuneType(lhs) ? node.getLhs() : node.getRhs();
            callTypeCheckException(errorNode, "Op-assign '^=': not defined for non-integer");
        }
    }

    @Override
    public void outAAmpCarotAssignStmt(AAmpCarotAssignStmt node) {
        if (!isAssignable(node.getLhs())) {
            callTypeCheckException(node.getLhs(), "Op-assign '&^=': LHS not assignable");
        }
        PTypeExpr lhs = typeTable.get(node.getLhs());
        PTypeExpr rhs = typeTable.get(node.getRhs());
        if (!isSameType(lhs, rhs)) {
            callTypeCheckException(node.getLhs(), "Op-assign '&^=': mismatched operand type");
        }
        if (!isIntOrRuneType(lhs) || !isIntOrRuneType(rhs)) {
            Node errorNode = !isIntOrRuneType(lhs) ? node.getLhs() : node.getRhs();
            callTypeCheckException(errorNode, "Op-assign '&^=': not defined for non-integer");
        }
    }

    @Override
    public void outALshiftAssignStmt(ALshiftAssignStmt node) {
        if (!isAssignable(node.getLhs())) {
            callTypeCheckException(node.getLhs(), "Op-assign '<<=': LHS not assignable");
        }
        PTypeExpr lhs = typeTable.get(node.getLhs());
        PTypeExpr rhs = typeTable.get(node.getRhs());
        if (!isSameType(lhs, rhs)) {
            callTypeCheckException(node.getLhs(), "Op-assign '<<=': mismatched operand type");
        }
        if (!isIntOrRuneType(lhs) || !isIntOrRuneType(rhs)) {
            Node errorNode = !isIntOrRuneType(lhs) ? node.getLhs() : node.getRhs();
            callTypeCheckException(errorNode, "Op-assign '<<=': not defined for non-integer");
        }
    }

    @Override
    public void outARshiftAssignStmt(ARshiftAssignStmt node) {
        if (!isAssignable(node.getLhs())) {
            callTypeCheckException(node.getLhs(), "Op-assign '>>=': LHS not assignable");
        }
        PTypeExpr lhs = typeTable.get(node.getLhs());
        PTypeExpr rhs = typeTable.get(node.getRhs());
        if (!isSameType(lhs, rhs)) {
            callTypeCheckException(node.getLhs(), "Op-assign '>>=': mismatched operand type");
        }
        if (!isIntOrRuneType(lhs) || !isIntOrRuneType(rhs)) {
            Node errorNode = !isIntOrRuneType(lhs) ? node.getLhs() : node.getRhs();
            callTypeCheckException(errorNode, "Op-assign '>>=': not defined for non-integer");
        }
    }

    /* Type check increment & decrement statements */
    @Override
    public void outAIncrStmt(AIncrStmt node) {
        if (!isAssignable(node.getExpr())) {
            callTypeCheckException(node.getExpr(), "Increment '++': expression not assignable");
        }
        PTypeExpr type = typeTable.get(node.getExpr());
        if (!isNumericType(type)) {
            callTypeCheckException(node.getExpr(), "Increment '++': expression not of numeric type");
        }
    }

    @Override
    public void outADecrStmt(ADecrStmt node) {
        if (!isAssignable(node.getExpr())) {
            callTypeCheckException(node.getExpr(), "Decrement '--': expression not assignable");
        }
        PTypeExpr type = typeTable.get(node.getExpr());
        if (!isNumericType(type)) {
            callTypeCheckException(node.getExpr(), "Decrement '--': expression not of numeric type");
        }
    }

    /* Type check print & println statements */
    @Override
    public void caseAPrintStmt(APrintStmt node) {
        List<PExpr> copy = new ArrayList<PExpr>(node.getExpr());
        for (PExpr e : copy) {
            e.apply(this);
            PTypeExpr type = typeTable.get(e);
            if (!isBaseType(type)) {
                callTypeCheckException(e, "Print: expression is not of base type");
            }
        }
    }

    @Override
    public void caseAPrintlnStmt(APrintlnStmt node) {
        List<PExpr> copy = new ArrayList<PExpr>(node.getExpr());
        for (PExpr e : copy) {
            e.apply(this);
            PTypeExpr type = typeTable.get(e);
            if (!isBaseType(type)) {
                callTypeCheckException(e, "Println: expression is not of base type");
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
            if (funcDec.getTypeExpr() == null) {
                callTypeCheckException(node.getExpr(), "Return: declared function has no return type");
            }
            node.getExpr().apply(this);
            PTypeExpr type = typeTable.get(node.getExpr());
            if (!isSameType(type, funcDec.getTypeExpr())) {
                callTypeCheckException(node.getExpr(), "Return: expression returned not matched function return type");
            }
        } else {
            if (funcDec.getTypeExpr() != null) {
                callTypeCheckException(funcDec, "Return: declared function cannot return void");
            }
        }
    }

    /* Type check if-else statement */
    @Override
    public void caseAIfElseStmt(AIfElseStmt node) {
        symbolTable.enterScope();
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
            symbolTable.enterScope();
            List<PStmt> copy = new ArrayList<PStmt>(node.getElseBlock());
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
            PTypeExpr type = typeTable.get(node.getExpr());
            if (!isBoolType(type)) {
                callTypeCheckException(node.getExpr(), "If-else: condition expression not evaluated to bool type");
            }
        }
    }

    /* Type check swtich statement */
    @Override
    public void caseASwitchStmt(ASwitchStmt node) {
        symbolTable.enterScope();
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
        symbolTable.exitScope();
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
            PTypeExpr exprType = typeTable.get(expr);
            List<PExpr> copy = new ArrayList<PExpr>(node.getExpr());
            for (PExpr e : copy) {
                e.apply(this);
                PTypeExpr type = typeTable.get(e);
                if (!isSameType(exprType, type)) {
                    callTypeCheckException(e, "Switch: case expression mismatched with switch expression");
                }
            }
        } else {
            List<PExpr> copy = new ArrayList<PExpr>(node.getExpr());
            for (PExpr e : copy) {
                e.apply(this);
                PTypeExpr type = typeTable.get(e);
                if (!isBoolType(type)) {
                    callTypeCheckException(e, "Switch: case expression not evaluated to bool type");
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
    public void caseALoopStmt(ALoopStmt node)
    {
        symbolTable.enterScope();
        if (node.getInit() != null)
        {
            node.getInit().apply(this);
        }
        if (node.getExpr() != null) {
            node.getExpr().apply(this);
            PTypeExpr type = typeTable.get(node.getExpr());
            if (!isBoolType(type)) {
                callTypeCheckException(node.getExpr(), "For: loop expression not evaluated to bool type");
            }
        }
        if (node.getEnd() != null) {
            node.getEnd().apply(this);
        }
        {
            List<PStmt> copy = new ArrayList<PStmt>(node.getBlock());
            for (PStmt e : copy) {
                e.apply(this);
            }
        }
        symbolTable.exitScope();
    }

    /* Type check binary arithemic operators */
    @Override
    public void outAAddExpr(AAddExpr node) {
        PTypeExpr left = typeTable.get(node.getLeft());
        PTypeExpr right = typeTable.get(node.getRight());
        if (!isSameType(left, right)) {
            callTypeCheckException(node.getLeft(), "Binary '+': mismatched operand type");
        }
        if (!isOrderedType(left) || !isOrderedType(right)) {
            Node errorNode = !isOrderedType(left) ? node.getLeft() : node.getRight();
            callTypeCheckException(errorNode, "Binary '+': not defined for non-numeric or non-string");
        }
        if (isIntType(left)) {
            typeTable.put(node, new AIntTypeExpr());
        } else if (isFloatType(left)) {
            typeTable.put(node, new AFloatTypeExpr());
        } else if (isRuneType(left)) {
            typeTable.put(node, new ARuneTypeExpr());
        } else {
            typeTable.put(node, new AStringTypeExpr());
        }
    }

    @Override
    public void outASubtractExpr(ASubtractExpr node) {
        PTypeExpr left = typeTable.get(node.getLeft());
        PTypeExpr right = typeTable.get(node.getRight());
        if (!isSameType(left, right)) {
            callTypeCheckException(node.getLeft(), "Binary '-': mismatched operand type");
        }
        if (!isNumericType(left) || !isNumericType(right)) {
            Node errorNode = !isNumericType(left) ? node.getLeft() : node.getRight();
            callTypeCheckException(errorNode, "Binary '-': not defined for non-numeric");
        }
        if (isIntType(left)) {
            typeTable.put(node, new AIntTypeExpr());
        } else if (isFloatType(left)) {
            typeTable.put(node, new AFloatTypeExpr());
        } else {
            typeTable.put(node, new ARuneTypeExpr());
        }
    }

    @Override
    public void outAMultExpr(AMultExpr node) {
        PTypeExpr left = typeTable.get(node.getLeft());
        PTypeExpr right = typeTable.get(node.getRight());
        if (!isSameType(left, right)) {
            callTypeCheckException(node.getLeft(), "Binary '*': mismatched operand type");
        }
        if (!isNumericType(left) || !isNumericType(right)) {
            Node errorNode = !isNumericType(left) ? node.getLeft() : node.getRight();
            callTypeCheckException(errorNode, "Binary '*': not defined for non-numeric");
        }
        if (isIntType(left)) {
            typeTable.put(node, new AIntTypeExpr());
        } else if (isFloatType(left)) {
            typeTable.put(node, new AFloatTypeExpr());
        } else {
            typeTable.put(node, new ARuneTypeExpr());
        }
    }

    @Override
    public void outADivExpr(ADivExpr node) {
        PTypeExpr left = typeTable.get(node.getLeft());
        PTypeExpr right = typeTable.get(node.getRight());
        if (!isSameType(left, right)) {
            callTypeCheckException(node.getLeft(), "Binary '/': mismatched operand type");
        }
        if (!isNumericType(left) || !isNumericType(right)) {
            Node errorNode = !isNumericType(left) ? node.getLeft() : node.getRight();
            callTypeCheckException(errorNode, "Binary '/': not defined for non-numeric");
        }
        if (isIntType(left)) {
            typeTable.put(node, new AIntTypeExpr());
        } else if (isFloatType(left)) {
            typeTable.put(node, new AFloatTypeExpr());
        } else {
            typeTable.put(node, new ARuneTypeExpr());
        }
    }

    @Override
    public void outAModExpr(AModExpr node) {
        PTypeExpr left = typeTable.get(node.getLeft());
        PTypeExpr right = typeTable.get(node.getRight());
        if (!isSameType(left, right)) {
            callTypeCheckException(node.getLeft(), "Binary '%': mismatched operand type");
        }
        if (!isIntOrRuneType(left) || !isIntOrRuneType(right)) {
            Node errorNode = !isIntOrRuneType(left) ? node.getLeft() : node.getRight();
            callTypeCheckException(errorNode, "Binary '%': not defined for non-integer");
        }
        if (isIntType(left)) {
            typeTable.put(node, new AIntTypeExpr());
        } else {
            typeTable.put(node, new ARuneTypeExpr());
        }
    }

    /* Type check binary bit operators */
    @Override
    public void outABitAndExpr(ABitAndExpr node) {
        PTypeExpr left = typeTable.get(node.getLeft());
        PTypeExpr right = typeTable.get(node.getRight());
        if (!isSameType(left, right)) {
            callTypeCheckException(node.getLeft(), "Binary '&': mismatched operand type");
        }
        if (!isIntOrRuneType(left) || !isIntOrRuneType(right)) {
            Node errorNode = !isIntOrRuneType(left) ? node.getLeft() : node.getRight();
            callTypeCheckException(errorNode, "Binary '&': not defined for non-integer");
        }
        if (isIntType(left)) {
            typeTable.put(node, new AIntTypeExpr());
        } else {
            typeTable.put(node, new ARuneTypeExpr());
        }
    }

    @Override
    public void outABitOrExpr(ABitOrExpr node) {
        PTypeExpr left = typeTable.get(node.getLeft());
        PTypeExpr right = typeTable.get(node.getRight());
        if (!isSameType(left, right)) {
            callTypeCheckException(node.getLeft(), "Binary '|': mismatched operand type");
        }
        if (!isIntOrRuneType(left) || !isIntOrRuneType(right)) {
            Node errorNode = !isIntOrRuneType(left) ? node.getLeft() : node.getRight();
            callTypeCheckException(errorNode, "Binary '|': not defined for non-integer");
        }
        if (isIntType(left)) {
            typeTable.put(node, new AIntTypeExpr());
        } else {
            typeTable.put(node, new ARuneTypeExpr());
        }
    }

    @Override
    public void outABitXorExpr(ABitXorExpr node) {
        PTypeExpr left = typeTable.get(node.getLeft());
        PTypeExpr right = typeTable.get(node.getRight());
        if (!isSameType(left, right)) {
            callTypeCheckException(node.getLeft(), "Binary '^': mismatched operand type");
        }
        if (!isIntOrRuneType(left) || !isIntOrRuneType(right)) {
            Node errorNode = !isIntOrRuneType(left) ? node.getLeft() : node.getRight();
            callTypeCheckException(errorNode, "Binary '^': not defined for non-integer");
        }
        if (isIntType(left)) {
            typeTable.put(node, new AIntTypeExpr());
        } else {
            typeTable.put(node, new ARuneTypeExpr());
        }
    }

    @Override
    public void outABitClearExpr(ABitClearExpr node) {
        PTypeExpr left = typeTable.get(node.getLeft());
        PTypeExpr right = typeTable.get(node.getRight());
        if (!isSameType(left, right)) {
            callTypeCheckException(node.getLeft(), "Binary '&^': mismatched operand type");
        }
        if (!isIntOrRuneType(left) || !isIntOrRuneType(right)) {
            Node errorNode = !isIntOrRuneType(left) ? node.getLeft() : node.getRight();
            callTypeCheckException(errorNode, "Binary '&^': not defined for non-integer");
        }
        if (isIntType(left)) {
            typeTable.put(node, new AIntTypeExpr());
        } else {
            typeTable.put(node, new ARuneTypeExpr());
        }
    }

    @Override
    public void outABitLshiftExpr(ABitLshiftExpr node) {
        PTypeExpr left = typeTable.get(node.getLeft());
        PTypeExpr right = typeTable.get(node.getRight());
        if (!isSameType(left, right)) {
            callTypeCheckException(node.getLeft(), "Binary '<<': mismatched operand type");
        }
        if (!isIntOrRuneType(left) || !isIntOrRuneType(right)) {
            Node errorNode = !isIntOrRuneType(left) ? node.getLeft() : node.getRight();
            callTypeCheckException(errorNode, "Binary '<<': not defined for non-integer");
        }
        if (isIntType(left)) {
            typeTable.put(node, new AIntTypeExpr());
        } else {
            typeTable.put(node, new ARuneTypeExpr());
        }
    }

    @Override
    public void outABitRshiftExpr(ABitRshiftExpr node) {
        PTypeExpr left = typeTable.get(node.getLeft());
        PTypeExpr right = typeTable.get(node.getRight());
        if (!isSameType(left, right)) {
            callTypeCheckException(node.getLeft(), "Binary '>>': mismatched operand type");
        }
        if (!isIntOrRuneType(left) || !isIntOrRuneType(right)) {
            Node errorNode = !isIntOrRuneType(left) ? node.getLeft() : node.getRight();
            callTypeCheckException(errorNode, "Binary '>>': not defined for non-integer");
        }
        if (isIntType(left)) {
            typeTable.put(node, new AIntTypeExpr());
        } else {
            typeTable.put(node, new ARuneTypeExpr());
        }
    }

    /* Type check unary operators */
    @Override
    public void outAPosExpr(APosExpr node) {
        PTypeExpr type = typeTable.get(node.getExpr());
        if (!isNumericType(type)) {
            callTypeCheckException(node.getExpr(), "Unary '+': not defined for non-numeric");
        }
        if (isIntType(type)) {
            typeTable.put(node, new AIntTypeExpr());
        } else if (isFloatType(type)) {
            typeTable.put(node, new AFloatTypeExpr());
        } else {
            typeTable.put(node, new ARuneTypeExpr());
        }
    }

    @Override
    public void outANegExpr(ANegExpr node) {
        PTypeExpr type = typeTable.get(node.getExpr());
        if (!isNumericType(type)) {
            callTypeCheckException(node.getExpr(), "Unary '-': not defined for non-numeric");
        }
        if (isIntType(type)) {
            typeTable.put(node, new AIntTypeExpr());
        } else if (isFloatType(type)) {
            typeTable.put(node, new AFloatTypeExpr());
        } else {
            typeTable.put(node, new ARuneTypeExpr());
        }
    }

    @Override
    public void outABitCompExpr(ABitCompExpr node) {
        PTypeExpr type = typeTable.get(node.getExpr());
        if (!isIntOrRuneType(type)) {
            callTypeCheckException(node.getExpr(), "Unary '^': not defined for non-integer");
        }
        if (isIntType(type)) {
            typeTable.put(node, new AIntTypeExpr());
        } else {
            typeTable.put(node, new ARuneTypeExpr());
        }
    }

    @Override
    public void outANotExpr(ANotExpr node) {
        PTypeExpr type = typeTable.get(node.getExpr());
        if (!isBoolType(type)) {
            callTypeCheckException(node.getExpr(), "Unary '!': not defined for non-boolean");
        }
        typeTable.put(node, new ABoolTypeExpr());
    }

    /* Type check relational operands */
    @Override
    public void outAEqExpr(AEqExpr node) {
        PTypeExpr left = typeTable.get(node.getLeft());
        PTypeExpr right = typeTable.get(node.getRight());
        if (!isSameType(left, right)) {
            callTypeCheckException(node.getLeft(), "Relational '==': mismatched operand type");
        }
        if (!isComparableType(left) || !isComparableType(right)) {
            Node errorNode = !isComparableType(left) ? node.getLeft() : node.getRight();
            callTypeCheckException(errorNode, "Relational '==': not defined for non-comparable");
        }
        typeTable.put(node, new ABoolTypeExpr());
    }

    @Override
    public void outANeqExpr(ANeqExpr node) {
        PTypeExpr left = typeTable.get(node.getLeft());
        PTypeExpr right = typeTable.get(node.getRight());
        if (!isSameType(left, right)) {
            callTypeCheckException(node.getLeft(), "Relational '!=': mismatched operand type");
        }
        if (!isComparableType(left) || !isComparableType(right)) {
            Node errorNode = !isComparableType(left) ? node.getLeft() : node.getRight();
            callTypeCheckException(errorNode, "Relational '!=': not defined for non-comparable");
        }
        typeTable.put(node, new ABoolTypeExpr());
    }

    @Override
    public void outALtExpr(ALtExpr node) {
        PTypeExpr left = typeTable.get(node.getLeft());
        PTypeExpr right = typeTable.get(node.getRight());
        if (!isSameType(left, right)) {
            callTypeCheckException(node.getLeft(), "Relational '<': mismatched operand type");
        }
        if (!isOrderedType(left) || !isOrderedType(right)) {
            Node errorNode = !isOrderedType(left) ? node.getLeft() : node.getRight();
            callTypeCheckException(errorNode, "Relational '<': not defined for non-ordered");
        }
        typeTable.put(node, new ABoolTypeExpr());
    }

    @Override
    public void outALteExpr(ALteExpr node) {
        PTypeExpr left = typeTable.get(node.getLeft());
        PTypeExpr right = typeTable.get(node.getRight());
        if (!isSameType(left, right)) {
            callTypeCheckException(node.getLeft(), "Relational '<=': mismatched operand type");
        }
        if (!isOrderedType(left) || !isOrderedType(right)) {
            Node errorNode = !isOrderedType(left) ? node.getLeft() : node.getRight();
            callTypeCheckException(errorNode, "Relational '<=': not defined for non-ordered");
        }
        typeTable.put(node, new ABoolTypeExpr());
    }

    @Override
    public void outAGtExpr(AGtExpr node) {
        PTypeExpr left = typeTable.get(node.getLeft());
        PTypeExpr right = typeTable.get(node.getRight());
        if (!isSameType(left, right)) {
            callTypeCheckException(node.getLeft(), "Relational '>': mismatched operand type");
        }
        if (!isOrderedType(left) || !isOrderedType(right)) {
            Node errorNode = !isOrderedType(left) ? node.getLeft() : node.getRight();
            callTypeCheckException(errorNode, "Relational '>': not defined for non-ordered");
        }
        typeTable.put(node, new ABoolTypeExpr());
    }

    @Override
    public void outAGteExpr(AGteExpr node) {
        PTypeExpr left = typeTable.get(node.getLeft());
        PTypeExpr right = typeTable.get(node.getRight());
        if (!isSameType(left, right)) {
            callTypeCheckException(node.getLeft(), "Relational '>=': mismatched operand type");
        }
        if (!isOrderedType(left) || !isOrderedType(right)) {
            Node errorNode = !isOrderedType(left) ? node.getLeft() : node.getRight();
            callTypeCheckException(errorNode, "Relational '>=': not defined for non-ordered");
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
            callTypeCheckException(errorNode, "Conditional '&&': not defined for non-boolean");
        }
        typeTable.put(node, new ABoolTypeExpr());
    }

    @Override
    public void outAOrExpr(AOrExpr node) {
        PTypeExpr left = typeTable.get(node.getLeft());
        PTypeExpr right = typeTable.get(node.getRight());
        if (!isBoolType(left) || !isBoolType(right)) {
            Node errorNode = !isBoolType(left) ? node.getLeft() : node.getRight();
            callTypeCheckException(errorNode, "Conditional '||': not defined for non-boolean");
        }
        typeTable.put(node, new ABoolTypeExpr());
    }

    /* Type check variables */
    @Override
    public void outAVariableExpr(AVariableExpr node) {
        PTypeExpr type = getType(node.getId());
        typeTable.put(node, type);
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
        return node instanceof AIntTypeExpr;
    }

    private boolean isFloatType(PTypeExpr node) {
        return node instanceof AFloatTypeExpr;
    }

    private boolean isRuneType(PTypeExpr node) {
        return node instanceof ARuneTypeExpr;
    }

    private boolean isStringType(PTypeExpr node) {
        return node instanceof AStringTypeExpr;
    }

    private boolean isIntOrRuneType(PTypeExpr node) {
        return isIntType(node) || isRuneType(node);
    }

    private boolean isNumericType(PTypeExpr node) {
        return isIntOrRuneType(node) || isFloatType(node);
    }

    private boolean isOrderedType(PTypeExpr node) {
        return isNumericType(node) || isStringType(node);
    }

    private boolean isComparableType(PTypeExpr node) {
        return isBoolType(node) || isOrderedType(node) || isArrayType(node);
    }

    private boolean isBaseType(PTypeExpr node) {
        return isBoolType(node) || isNumericType(node) || isStringType(node);
    }

    private boolean isCustomType(PTypeExpr node) {
        return node instanceof ACustomTypeExpr;
    }

    private boolean isArrayType(PTypeExpr node) {
        return node instanceof AArrayTypeExpr;
    }

    private boolean isSliceType(PTypeExpr node) {
        return node instanceof ASliceTypeExpr;
    }

    private boolean isStructType(PTypeExpr node) {
        return node instanceof AStructTypeExpr;
    }

    private boolean isSameType(PTypeExpr node1, PTypeExpr node2) {
        boolean isSameType = node1.getClass() == node2.getClass();
        if (isSameType && node1 instanceof ACustomTypeExpr) {
            return isSameCustomType((ACustomTypeExpr) node1, (ACustomTypeExpr) node2);
        }
        else if (isSameType && node1 instanceof AStructTypeExpr)
        {
            return isSameStruct((AStructTypeExpr) node1, (AStructTypeExpr) node2);
        }
        else if (isSameType && node1 instanceof AArrayTypeExpr)
        {
            String length1 = getNum(((AArrayTypeExpr) node1).getExpr());
            String length2 = getNum(((AArrayTypeExpr) node2).getExpr());
            int dimension1 = getDimension((AArrayTypeExpr) node1);
            int dimension2 = getDimension((AArrayTypeExpr) node2);
            boolean recursive = true;
            if (dimension1 > 0)
            {
                recursive = isSameType(((AArrayTypeExpr) node1).getTypeExpr(), ((AArrayTypeExpr) node2).getTypeExpr());
            }
            return (length1.equals(length2) && dimension1 == dimension2 && recursive && (isSameType(((AArrayTypeExpr) node1).getTypeExpr(), ((AArrayTypeExpr) node2).getTypeExpr())));
        }
        else if (isSameType && node1 instanceof ASliceTypeExpr)
        {
            int dimension1 = getDimension((ASliceTypeExpr) node1);
            int dimension2 = getDimension((ASliceTypeExpr) node2);
            return ((dimension1 == dimension2) && (isSameType(((ASliceTypeExpr) node1).getTypeExpr(), ((ASliceTypeExpr) node2).getTypeExpr())));
        }
        return isSameType;
    }

    private String getNum(PExpr node)
    {
        if (node instanceof AOctLitExpr)
        {
            return getNum((AOctLitExpr) node);
        }
        else if (node instanceof AHexLitExpr)
        {
            return getNum((AHexLitExpr) node);
        }
        else if (node instanceof AIntLitExpr)
        {
            return getNum((AIntLitExpr) node);
        }
        callTypeCheckException(node, "Invalid array length");
        return null;
    }

    private String getNum(AOctLitExpr node)
    {
        return node.getOctLit().getText();
    }

    private String getNum(AIntLitExpr node)
    {
        return node.getIntLit().getText();
    }

    private String getNum(AHexLitExpr node)
    {
        return node.getHexLit().getText();
    }

    private boolean isSameCustomType(ACustomTypeExpr node1, ACustomTypeExpr node2)
    {
        PTypeExpr t1 = getType(node1);
        PTypeExpr t2 = getType(node2);
        boolean theSame = isSameType(t1, t2);
        if (theSame)
        {
            if (t1 instanceof AStructTypeExpr)
            {
                return isSameStruct((AStructTypeExpr) t1, (AStructTypeExpr) t2);
            }
            return true;
        }
        return false;
    }

    private ArrayList<TId> getIds(Node node)
    {
        if (node instanceof ASpecVarSpec)
        {
            ArrayList<POptId> optIds = new ArrayList<POptId>(((ASpecVarSpec) node).getOptId());
            ArrayList<TId> ids = new ArrayList<TId>();
            for (POptId o: optIds)
            {
                if (o instanceof AIdOptId)
                {
                    ids.add(((AIdOptId) o).getId());
                }
            }
            return ids;
        }
        else if (node instanceof ASpecTypeSpec)
        {
            POptId optId = ((ASpecTypeSpec) node).getOptId();
            ArrayList<TId> ids = new ArrayList<TId>();
            if (optId instanceof AIdOptId)
            {
                ids.add(((AIdOptId) optId).getId());
            }
            return ids;
        }
        else if (node instanceof AShortAssignStmt)
        {
            ArrayList<POptId> optIds = new ArrayList<POptId>(((AShortAssignStmt) node).getOptId());
            ArrayList<TId> ids = new ArrayList<TId>();
            for (POptId o: optIds)
            {
                if (o instanceof AIdOptId)
                {
                    ids.add(((AIdOptId) o).getId());
                }
            }
            return ids;
        }
        else if (node instanceof AStructSubStructSub)
        {
            ArrayList<POptId> optIds = new ArrayList<POptId>(((AStructSubStructSub) node).getOptId());
            ArrayList<TId> ids = new ArrayList<TId>();
            for (POptId o: optIds)
            {
                if (o instanceof AIdOptId)
                {
                    ids.add(((AIdOptId) o).getId());
                }
            }
            return ids;
        }
        return new ArrayList<TId>();
    }

    private boolean isSameStruct(AStructTypeExpr node1, AStructTypeExpr node2)
    {
        ArrayList<TId> ids1 = getIds(node1);
        ArrayList<TId> ids2 = getIds(node2);
        boolean sameLength = ids1.size() == ids2.size();
        if(sameLength)
        {
            boolean sameName = true;
            for (int i=0; i<ids1.size(); i++)
            { 
                String name1 = getIds(node1).get(i).getText();
                String name2 = getIds(node2).get(i).getText();
                sameName = name1.equals(name2);
                if (!sameName)
                {
                    return sameName;
                }
            }
        }
        return sameLength;
    }

    private boolean isAssignable(PExpr node) {
        return (node instanceof AVariableExpr) || (node instanceof AFieldExpr) || (node instanceof AArrayElemExpr);
    }

    private AFuncTopDec getParentFuncDec(AReturnStmt node) {
        Node parent = node;
        while (!(parent instanceof AFuncTopDec)) {
            parent = parent.parent();
        }
        return (AFuncTopDec) parent;
    }

    /**
     * Throws a type check exception after annotating the message with line and position information.
     *
     * @param node - AST node
     * @param s - Error message
     * @throws TypeCheckException
     */
    private void callTypeCheckException(Node node, String s) {
        String message = "";
        if (node != null) {
            node.apply(lineAndPos);
            message += "[" + lineAndPos.getLine(node) + "," + lineAndPos.getPos(node) + "] ";
        }
        message += s;
        throw new TypeCheckException(message);
        /*TypeCheckException e = new TypeCheckException(message);
        e.printStackTrace();
        System.exit(1);*/
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
            List<TId> ids = new ArrayList<TId>(getIds(node));
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

    private void putTypeExpr(String name, PTypeExpr node)
    {
        if (isStructType(node))
        {
            for (PStructSub a: ((AStructTypeExpr) node).getStructSub())
            {
                for (TId id: (getIds((AStructSubStructSub) a)))
                {
                    String newName = name + "." + id.getText();
                    symbolTable.addSymbol(newName, a);
                    typeTable.put(a, getType(a));
                    putTypeExpr(newName, ((AStructSubStructSub) a).getTypeExpr());
                }
            }
        }
    }

    @Override
    public void inASpecTypeSpec(ASpecTypeSpec node)
    {
        for (TId t: getIds(node))
        {
            String name = t.getText();
            symbolTable.addSymbol(name, node.getTypeExpr());
            putTypeExpr(name, node.getTypeExpr());
        }
    }

    @Override
    public void outAShortAssignStmt(AShortAssignStmt node) {
        List<TId> ids = new ArrayList<TId>(getIds(node));
        List<PExpr> exprs = new ArrayList<PExpr>(node.getExpr());
        boolean hasNewId = false;
        for (int i = 0; i < ids.size(); i++) {
            if (symbolTable.containsId(ids.get(i).getText())) {
                String identifier = ids.get(i).getText();
                TId idNode = getIdentifierTIdNode(identifier, symbolTable.getSymbol(identifier, node));
                PTypeExpr idType = typeTable.get(idNode);
                PTypeExpr exprType = typeTable.get(exprs.get(i));
                if (!isSameType(idType, exprType)) {
                    callTypeCheckException(exprs.get(i), "Short assign ':=': mismatched expression type");
                }
                symbolTable.removeSymbol(ids.get(i).getText());
                typeTable.remove(idNode);
            } else {
                hasNewId = true;
            }
            symbolTable.addSymbol(ids.get(i).getText(), node);
            typeTable.put(ids.get(i), getType(exprs.get(i)));
        }
        if (!hasNewId) {
            callTypeCheckException(node, "Short assign ':=': no new variables declared");
        }
    }

    /* Help method for handling short assign */
    private TId getIdentifierTIdNode(String id, Node node) {
        List<TId> copy;
        if (node instanceof ASpecVarSpec) {
            copy = new ArrayList<TId>(getIds((ASpecVarSpec) node));
        } else {
            copy = new ArrayList<TId>(getIds((AShortAssignStmt) node));
        }
        
        for (TId e : copy) {
            if (e.getText().equals(id)) {
                return e;
            }
        }
        return null;
    }

    /* Type Check Field Expressions */

    @Override
    public void outAFieldExpr(AFieldExpr node)
    {
        PTypeExpr type = getType(node.getExpr());
        String id = node.getId().getText();
        if (type instanceof AStructTypeExpr)
        {
            for (PStructSub a: ((AStructTypeExpr) type).getStructSub())
            {
                PTypeExpr argType = ((AStructSubStructSub) a).getTypeExpr();
                for (TId argId: (getIds((AStructSubStructSub) a)))
                {
                    if (argId.getText().equals(id))
                    {
                        typeTable.put(node, argType); // Well typed!
                    }
                }
            }
        }
        else if (type instanceof ACustomTypeExpr)
        {
            PTypeExpr subType = getType(((ACustomTypeExpr) type).getId());
            if (subType instanceof AStructTypeExpr)
            {
                for (PStructSub a: ((AStructTypeExpr) subType).getStructSub())
                {
                    PTypeExpr argType = ((AStructSubStructSub) a).getTypeExpr();
                    for (TId argId: getIds((AStructSubStructSub) a))
                    {
                        if (argId.getText().equals(id))
                        {
                            typeTable.put(node, argType); // Well typed!
                        }
                    }
                }
            }
        }
        else
        {
            callTypeCheckException(node, "Fields calls can only be used on struct types, not " + type.getClass());
        }
    }

    /* Type check assignment statements */

    private boolean isSameFields(AStructTypeExpr lhs, AStructTypeExpr rhs)
    {
        int lhsLen = ((List<PStructSub>) lhs.getStructSub()).size();
        int rhsLen = ((List<PStructSub>) rhs.getStructSub()).size();
        if (lhsLen == rhsLen)
        {
            List<PStructSub> aRhs = rhs.getStructSub();
            List<PStructSub> aLhs = lhs.getStructSub();
            for (int i=0; i<lhsLen; i++)
            {
                AStructSubStructSub a1 = (AStructSubStructSub) aRhs.get(i);
                AStructSubStructSub a2 = (AStructSubStructSub) aLhs.get(i);
                if(!isSameType(a1.getTypeExpr(), a2.getTypeExpr()))
                {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private boolean isSameFields(AStructTypeExpr lhs, ACustomTypeExpr custom)
    {
        PTypeExpr rhs = getType(custom);
        if (rhs instanceof AStructTypeExpr)
        {
            return isSameFields(lhs, (AStructTypeExpr) rhs);
        }
        return false;
    }

    @Override
    public void outAAssignStmt(AAssignStmt node)
    {
        List<PExpr> ids = node.getLhs();
        List<PExpr> exprs = node.getRhs();
        int length = ids.size();
        for (int i=0; i < length; i++)
        {
            PTypeExpr lhs = getType(ids.get(i));
            PTypeExpr rhs = getType(exprs.get(i));
            if (!isSameType(lhs, rhs))
            {
                if (lhs instanceof AStructTypeExpr && rhs instanceof ACustomTypeExpr)
                {
                    boolean sameFields = isSameFields((AStructTypeExpr) lhs, (ACustomTypeExpr) rhs);
                    if (!sameFields)
                    {
                        callTypeCheckException(node, "Struct on right side must be assignment compatable with field");
                    }
                }
                else
                {
                    callTypeCheckException(node, "Types on left and right sides of assignment statements must be assignment compatable");
                }
            }
        }
    }

    /* Append type checking */
    private int getDimension(ASliceTypeExpr node)
    {
        PTypeExpr elementType = node.getTypeExpr();
        if (elementType instanceof ASliceTypeExpr)
        {
            return 1 + getDimension((ASliceTypeExpr) elementType);
        }
        return 0;
    }

    private int getDimension(AArrayTypeExpr node)
    {
        PTypeExpr elementType = node.getTypeExpr();
        if (elementType instanceof AArrayTypeExpr)
        {
            return 1 + getDimension((AArrayTypeExpr) elementType);
        }
        return 0;
    }

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
                if (sliceElementType instanceof ASliceTypeExpr)
                {
                    if (!(getDimension((ASliceTypeExpr) sliceElementType) == getDimension((ASliceTypeExpr) exprType)))
                    {
                        callTypeCheckException(node, "Tried to append row with wrong dimension to slice");
                    }
                }
                else if (sliceElementType instanceof ACustomTypeExpr)
                {
                    if (!isSameCustomType((ACustomTypeExpr) sliceElementType, (ACustomTypeExpr) exprType))
                    {
                        callTypeCheckException(node, "Tried to append mismatching structs");
                    }
                }
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
        if (decl instanceof AFuncTopDec)
        {
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
            if (paramTypes.size() != argTypes.size())
            {
                callTypeCheckException(node, "Wrong number of parameters passed to function call");
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
        else if (decl instanceof ASpecTypeSpec)
        {
            PTypeExpr castType = ((ASpecTypeSpec) decl).getTypeExpr();
            if    (castType instanceof AIntTypeExpr
            || castType instanceof AFloatTypeExpr
            || castType instanceof ARuneTypeExpr
            || castType instanceof ABoolTypeExpr)
            {
                typeTable.put(node, new ACustomTypeExpr(node.getId())); //Well typed!
                typeTable.put(decl, new ACustomTypeExpr(node.getId()));
            }//TODO: Error here?
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
            if (declaration instanceof ASpecVarSpec)
            {
                ASpecVarSpec dec = (ASpecVarSpec) declaration;
                int idx = getIds(dec).indexOf(id);
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
                return dec.getTypeExpr();
            }
            else if (declaration instanceof AStructTypeExpr)
            {
                return (AStructTypeExpr) declaration;
            }
            else if (declaration instanceof AShortAssignStmt)
            {
                AShortAssignStmt dec = (AShortAssignStmt) declaration;
                for (TId i: getIds(dec))
                {
                    if (i.getText().equals(id))
                    {
                        int idx = getIds(dec).indexOf(i);
                        return typeTable.get(dec.getExpr().get(idx));
                    }
                }
            }
            else if (declaration instanceof AStructSubStructSub)
            {
                return typeTable.get(declaration);
            }
            else if (declaration instanceof AArgArgGroup)
            {
                return typeTable.get(declaration);
            }
            else if (declaration instanceof ABoolTypeExpr)
            {
                typeTable.put(node, ((ABoolTypeExpr) declaration));
                typeTable.put(declaration, ((ABoolTypeExpr) declaration));
                return typeTable.get(node);
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
        else if (node instanceof AStructSubStructSub)
        {
            return ((AStructSubStructSub) node).getTypeExpr();
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
            /* TODO:
             * THIS DOES NOT WORK FOR CUSTOM ALIAS TYPE CASTS!
             * WHEN A CUSTOM TYPE CAST IS CALLED ON A LITERAL, THE AFuncCallExpr NODE HAS NULL id. :'(
             */
            String id = (((AFuncCallExpr) node).getId()).getText();
            Node decl = symbolTable.getSymbol(id, node);
            if (decl instanceof AFuncTopDec)
            {
                return ((AFuncTopDec) decl).getTypeExpr();
            }
            else if (decl instanceof ASpecTypeSpec)
            {
                return ((ASpecTypeSpec) decl).getTypeExpr();
            }
        }
        else if (node instanceof ACustomTypeExpr)
        {
            return getType(((ACustomTypeExpr) node).getId());
        }
        else
        {   try{
                return typeTable.get(node);
            }
            catch (Exception e)
            {
                callTypeCheckException(node, "Did not find type for " + node.getClass());
            }
        }
        callTypeCheckException(node, "Did not find type for " + node.getClass() + " tried to return null");
        return null;
    }
}
