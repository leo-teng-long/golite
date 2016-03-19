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
            System.out.println("Node: " + n + " Key: " + n.getClass() + " Value: " + typeTable.get(n).getClass());
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
    @Override 
    public void inAFuncTopDec(AFuncTopDec node)
    {
        symbolTable.addSymbol(node.getId().getText(), node);
        symbolTable.enterScope();
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
                putArgGroup(name, (AArgArgGroup) a); //Hack -- works because there is only one type of arg_group in sablecc grammar
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
        System.out.println(node);
        System.out.println(node.getClass());
    }

    /* More helper methods */
    private List<TId> getIds(Node node)
    {
        if (node instanceof AShortAssignStmt)
        {
            return ((AShortAssignStmt) node).getId();
        }
        if (node instanceof ASpecVarSpec)
        {
            return ((ASpecVarSpec) node).getId();
        }
        callTypeCheckException(node, "Ids not found for " + node.getClass());
        return null;
    }

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
        callTypeCheckException(node, "Did not find type for " + node.getClass());
        return null;
    }
}
