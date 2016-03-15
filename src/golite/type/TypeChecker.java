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

    public TypeChecker(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
        typeTable = new HashMap<Node, PTypeExpr>();
        lineAndPos = new LineAndPos();
    }

    public void inStart(Start node)
    {
        symbolTable.enterScope();
    }

    public void outStart(Start node)
    {
        symbolTable.exitScope();
        symbolTable.printSymbols();
        for (Node n: typeTable.keySet())
        {
            System.out.println("Node: " + n + " Type: " + typeTable.get(n).getClass());
        }
    }

    /* Type check binary arithemic operators */
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
    public void outABitAndExpr(ABitAndExpr node) {
        PTypeExpr left = typeTable.get(node.getLeft());
        PTypeExpr right = typeTable.get(node.getRight());
        if (!isIntType(left) || !isIntType(right)) {
            Node errorNode = !isIntType(left) ? node.getLeft() : node.getRight();
            callTypeCheckException(errorNode, "Binary operator '&' can only be applied to integer");
        }
        typeTable.put(node, new AIntTypeExpr());
    }

    public void outABitOrExpr(ABitOrExpr node) {
        PTypeExpr left = typeTable.get(node.getLeft());
        PTypeExpr right = typeTable.get(node.getRight());
        if (!isIntType(left) || !isIntType(right)) {
            Node errorNode = !isIntType(left) ? node.getLeft() : node.getRight();
            callTypeCheckException(errorNode, "Binary operator '|' can only be applied to integer");
        }
        typeTable.put(node, new AIntTypeExpr());
    }

    public void outABitXorExpr(ABitXorExpr node) {
        PTypeExpr left = typeTable.get(node.getLeft());
        PTypeExpr right = typeTable.get(node.getRight());
        if (!isIntType(left) || !isIntType(right)) {
            Node errorNode = !isIntType(left) ? node.getLeft() : node.getRight();
            callTypeCheckException(errorNode, "Binary operator '^' can only be applied to integer");
        }
        typeTable.put(node, new AIntTypeExpr());
    }

    public void outABitClearExpr(ABitClearExpr node) {
        PTypeExpr left = typeTable.get(node.getLeft());
        PTypeExpr right = typeTable.get(node.getRight());
        if (!isIntType(left) || !isIntType(right)) {
            Node errorNode = !isIntType(left) ? node.getLeft() : node.getRight();
            callTypeCheckException(errorNode, "Binary operator '&^' can only be applied to integer");
        }
        typeTable.put(node, new AIntTypeExpr());
    }

    public void outABitLshiftExpr(ABitLshiftExpr node) {
        PTypeExpr left = typeTable.get(node.getLeft());
        PTypeExpr right = typeTable.get(node.getRight());
        if (!isIntType(left) || !isIntType(right)) {
            Node errorNode = !isIntType(left) ? node.getLeft() : node.getRight();
            callTypeCheckException(errorNode, "Binary operator '<<' can only be applied to integer");
        }
        typeTable.put(node, new AIntTypeExpr());
    }

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

    public void outABitCompExpr(ABitCompExpr node) {
        PTypeExpr type = typeTable.get(node.getExpr());
        if (isIntType(type)) {
            callTypeCheckException(node.getExpr(), "Unary operator '^' can only be applied to integer");
        }
        typeTable.put(node, new AIntTypeExpr());
    }

    public void outANotExpr(ANotExpr node) {
        PTypeExpr type = typeTable.get(node.getExpr());
        if (isBoolType(type)) {
            callTypeCheckException(node.getExpr(), "Unary operator '!' can only be applied to boolean");
        }
        typeTable.put(node, new ABoolTypeExpr());
    }

    /* Type check relational operands */
    public void outAEqExpr(AEqExpr node) {
        PTypeExpr left = typeTable.get(node.getLeft());
        PTypeExpr right = typeTable.get(node.getRight());
        if (!isComparableType(left) || !isComparableType(right)) {
            Node errorNode = !isComparableType(left) ? node.getLeft() : node.getRight();
            callTypeCheckException(errorNode, "Relational operator '==' can only be applied to comparable");
        }
        typeTable.put(node, new ABoolTypeExpr());
    }

    public void outANeqExpr(ANeqExpr node) {
        PTypeExpr left = typeTable.get(node.getLeft());
        PTypeExpr right = typeTable.get(node.getRight());
        if (!isComparableType(left) || !isComparableType(right)) {
            Node errorNode = !isComparableType(left) ? node.getLeft() : node.getRight();
            callTypeCheckException(errorNode, "Relational operator '!=' can only be applied to comparable");
        }
        typeTable.put(node, new ABoolTypeExpr());
    }

    public void outALtExpr(ALtExpr node) {
        PTypeExpr left = typeTable.get(node.getLeft());
        PTypeExpr right = typeTable.get(node.getRight());
        if (!isOrderedType(left) || !isOrderedType(right)) {
            Node errorNode = !isOrderedType(left) ? node.getLeft() : node.getRight();
            callTypeCheckException(errorNode, "Relational operator '<' can only be applied to ordered");
        }
        typeTable.put(node, new ABoolTypeExpr());
    }

    public void outALteExpr(ALteExpr node) {
        PTypeExpr left = typeTable.get(node.getLeft());
        PTypeExpr right = typeTable.get(node.getRight());
        if (!isOrderedType(left) || !isOrderedType(right)) {
            Node errorNode = !isOrderedType(left) ? node.getLeft() : node.getRight();
            callTypeCheckException(errorNode, "Relational operator '<=' can only be applied to ordered");
        }
        typeTable.put(node, new ABoolTypeExpr());
    }

    public void outAGtExpr(AGtExpr node) {
        PTypeExpr left = typeTable.get(node.getLeft());
        PTypeExpr right = typeTable.get(node.getRight());
        if (!isOrderedType(left) || !isOrderedType(right)) {
            Node errorNode = !isOrderedType(left) ? node.getLeft() : node.getRight();
            callTypeCheckException(errorNode, "Relational operator '>' can only be applied to ordered");
        }
        typeTable.put(node, new ABoolTypeExpr());
    }

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
    public void outAAndExpr(AAndExpr node) {
        PTypeExpr left = typeTable.get(node.getLeft());
        PTypeExpr right = typeTable.get(node.getRight());
        if (!isBoolType(left) || !isBoolType(right)) {
            Node errorNode = !isBoolType(left) ? node.getLeft() : node.getRight();
            callTypeCheckException(errorNode, "Conditional operator '&&' can only be applied to boolean");
        }
        typeTable.put(node, new ABoolTypeExpr());
    }

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
    public void outAIntLitExpr(AIntLitExpr node) {
        typeTable.put(node, new AIntTypeExpr());
    }

    public void outAOctLitExpr(AOctLitExpr node) {
        typeTable.put(node, new AIntTypeExpr());
    }

    public void outAOctLitExpr(AHexLitExpr node) {
        typeTable.put(node, new AIntTypeExpr());
    }

    public void outAFloatLitExpr(AFloatLitExpr node) {
        typeTable.put(node, new AFloatTypeExpr());
    }

    public void outARuneLitExpr(ARuneLitExpr node) {
        typeTable.put(node, new ARuneTypeExpr());
    }

    /* Type check string literals */
    public void outAInterpretedStringLitExpr(AInterpretedStringLitExpr node) {
        typeTable.put(node, new AStringTypeExpr());
    }

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
    @Override //Modified
    public void inAFuncTopDec(AFuncTopDec node)
    {
        symbolTable.addSymbol(node.getId().getText(), node);
        symbolTable.enterScope();
        defaultIn(node);
    }

    @Override //Modified
    public void outAFuncTopDec(AFuncTopDec node)
    {
        defaultOut(node);
        symbolTable.exitScope();
    }

    /* Add var and type specifications to symbol table */

    @Override //Modified
    public void outASpecVarSpec(ASpecVarSpec node)
    {
        {
            List<TId> copy = new ArrayList<TId>(node.getId());
            for(TId e : copy)
            {
                symbolTable.addSymbol(e.getText(), node);
            }
            if (node.getTypeExpr() != null)
            {
                for (PExpr e: node.getExpr())
                {
                    if (typeTable.get(e).getClass() != node.getTypeExpr().getClass())
                    {
                        System.out.println(e);
                        callTypeCheckException(e, "Expression type does not match declared variable type");
                    }
                }
            }
        }
    }

    private void putArgGroupNames(String name, AArgArgGroup node) {
        for (TId id: node.getId())
        {
            symbolTable.addSymbol(name + "." + id.getText(), node.getTypeExpr());
            putTypeExprNames(name + "." + id.getText(), node.getTypeExpr());
        }
    }

    private void putTypeExprNames(String name, PTypeExpr node)
    {
        //TODO: Handle other types of type expressions
        if (isStructType(node))
        {
            for (PArgGroup a: ((AStructTypeExpr) node).getArgGroup())
            {
                putArgGroupNames(name, (AArgArgGroup) a); //Hack -- works because there is only one type of arg_group in sablecc grammar
            }
        }
    }

    @Override
    public void inASpecTypeSpec(ASpecTypeSpec node)
    {
        String name = node.getId().getText();
        symbolTable.addSymbol(name, node.getTypeExpr());
        putTypeExprNames(name, node.getTypeExpr());
    }

    @Override //Modified
    public void inAShortAssignStmt(AShortAssignStmt node)
    {
        {
            List<TId> copy = new ArrayList<TId>(node.getId());
            for(TId e : copy)
            {
                symbolTable.addSymbol(e.getText(), node);
            }
        }
    }

    /* Add variable expressions to type table */
    @Override
    public void inAVariableExpr(AVariableExpr node)
    {
        defaultIn(node);
        TId id = node.getId();
        Node dec_node = symbolTable.getSymbol(id.getText(), node);
        PTypeExpr type;
        if (dec_node instanceof ASpecVarSpec)
        {
            ASpecVarSpec dec = (ASpecVarSpec) dec_node;
            int idx = dec.getId().indexOf(id);
            if (dec.getTypeExpr() != null)
            {
                type = dec.getTypeExpr();
            } 
            else 
            {
                type = typeTable.get(dec.getExpr().get(idx));
            }
            typeTable.put(node, type);
        } 
        else if (dec_node instanceof ASpecTypeSpec)
        {
            ASpecTypeSpec dec = (ASpecTypeSpec) dec_node;
            type = dec.getTypeExpr();
            typeTable.put(node, type);
        } //TODO: add other conditions
    }
}
