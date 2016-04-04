package golite;

import java.util.*;
import golite.exception.*;
import golite.util.*;
import golite.node.*;
import golite.analysis.*;


/**
 * GoLite Weeder.
 */
// TODO: Add string of error-causing code in select error messages.
public class Weeder extends DepthFirstAdapter {

    /** Line and position tracker for token AST nodes. */
    private LineAndPosTracker lineAndPosTracker = new LineAndPosTracker();
    /** Tracks the loop nesting depth in a traversal. */
    private int loopDepth = 0;

    /** Finds a break statement in an AST. */
    private static class BreakStmtFinder extends DepthFirstAdapter {
        /** Root node of AST. */
        private Node root;
        /** Flag for whether the AST contains a breat statement or not. */
        private boolean found;

        /**
         * Constructor.
         *
         * @param node - Root node of AST
         */
        private BreakStmtFinder(Node node) {
            super();
            this.root = node;
            this.found = false;
            this.root.apply(this);
        }

        /**
         * Returns whether a break statement is contained or not.
         *
         * @return True if the AST contains a break statement, false otherwise.
         */
        protected boolean found() {
            return this.found;
        }

        // Set found to true.
        @Override
        public void inABreakStmt(ABreakStmt node) {
            this.found = true;
        }

    }

    /**
     * Throws a weeder exception after annotating the message with line and position information.
     *
     * @param node - AST node
     * @param msg - Error message
     * @throws WeederException
     */
    private void throwWeederException(Node node, String msg) {
        Integer line = this.lineAndPosTracker.getLine(node);
        Integer pos = this.lineAndPosTracker.getPos(node);

        throw new WeederException("[" + line + "," + pos + "] " + msg);
    }

    /**
     * Throws a weeder exception for missing a return statement.
     *
     * @param node - AST node
     * @throws WeederException
     */
    private void throwMissingReturnError(Node node) {
        throwWeederException(node, "Missing return");
    }

    // Gather line and position information.
    @Override 
    public void inStart(Start node) {
        node.apply(this.lineAndPosTracker);
    }

    // Throw an error if the function is non-void and the body doesn't have a return on every
    // branch of execution.
    @Override
    public void inAFuncTopDec(AFuncTopDec node) {
        if (node.getTypeExpr() != null)
            this.checkBlockHasReturn(node, node.getStmt());
    }

    /**
     * Recursively check the given block has a return on every execution path, throwing an error if
     * it doesn't.
     *
     * @param node - Root node of block
     * @param stmts - Block statements
     */
    private void checkBlockHasReturn(Node node, LinkedList<PStmt> stmts) {
        // Throw an error when the body is empty.
        if (stmts == null || stmts.size() == 0)
            this.throwMissingReturnError(node);

        PStmt lastStmt = stmts.get(stmts.size() - 1);

        // If the block doesn't end in a returnable, throw an error.
        if (!isReturnable(lastStmt))
            throwWeederException(node, "Missing return at end of function");

        // If the block doesn't end in a return statement, then have to go recursive.
        if (!(lastStmt instanceof AReturnStmt)) {
            // Check block statements recursively for required return statements.
            if (lastStmt instanceof ABlockStmt)
                this.checkBlockHasReturn(lastStmt, ((ABlockStmt) lastStmt).getStmt());
            
            // Check if-else statements recursively, by checking the if-block and else-block, for
            // required return statements.
            if (lastStmt instanceof AIfElseStmt) {
                AIfElseStmt ifElseStmtNode = (AIfElseStmt) lastStmt;
                this.checkBlockHasReturn(ifElseStmtNode, ifElseStmtNode.getIfBlock());
                this.checkBlockHasReturn(ifElseStmtNode, ifElseStmtNode.getElseBlock());
            }

            // Check switch statements recursively, by checking each case block, for required
            // return statements, and making sure there is a default case.
            if (lastStmt instanceof ASwitchStmt) {
                boolean hasDefaultCase = false;

                for (PCaseBlock pCaseBlock : ((ASwitchStmt) lastStmt).getCaseBlock()) {
                    ABlockCaseBlock caseBlock = (ABlockCaseBlock) pCaseBlock;
                    this.checkBlockHasReturn(caseBlock, caseBlock.getStmt());

                    // Default case has been found.
                    if ((caseBlock.getCaseCondition()) instanceof ADefaultCaseCondition)
                        hasDefaultCase = true;
                }

                // Throw an error if there is no default case.
                if (!hasDefaultCase)
                    this.throwMissingReturnError(lastStmt);
            }

            // Check if a for loop statement doesn't have an expression and doesn't have a break
            // statment anywhere in it's body.
            if (lastStmt instanceof ALoopStmt) {
                ALoopStmt loopStmt = (ALoopStmt) lastStmt;
                BreakStmtFinder breakStmtFinder = new BreakStmtFinder(loopStmt);
                if (loopStmt.getExpr() != null && !(loopStmt.getExpr() instanceof AEmptyExpr)
                    || breakStmtFinder.found())
                    this.throwMissingReturnError(loopStmt);
            }
        }
    }

    /**
     * Checks if the given node is a "returnable", i.e. capable of ending a non-void function.
     *
     * @param node - AST node
     * @return True if the node denotes something that can end a non-void function, false otherwise.
     */
    private boolean isReturnable(Node node) {
        return (node instanceof AReturnStmt
            || node instanceof ABlockStmt
            || node instanceof AIfElseStmt
            || node instanceof ASwitchStmt
            || node instanceof ALoopStmt);
    }

    // Throw an error if an expression statement does not comprise solely of a function call.
    @Override
    public void inAExprStmt(AExprStmt node) {
        PExpr pExpr = node.getExpr();
        if (!(pExpr instanceof AFuncCallExpr))
            // TODO: Add string of error-causing code.
            this.throwWeederException(node, "Evaluated but not used");
    }

    // Throw an error if the number of identifiers on the R.H.S. of a variable specification is not
    // equal to the number of expressions on the L.H.S.
    @Override
    public void inASpecVarSpec(ASpecVarSpec node) {
        int idListLength = node.getOptId().size();
        int exprListLength = node.getExpr().size();
        
        if (exprListLength > 0 && idListLength != exprListLength)
            this.throwWeederException(node,
                "L.H.S and R.H.S. of variable declaration don't match");        
    }

    // Throw an error if the number of identifiers on the R.H.S. of a short assignment is not equal
    // to the number of expressions on the L.H.S. or the L.H.S. is a sole blank.
    @Override
    public void inAShortAssignStmt(AShortAssignStmt node) {
        LinkedList<POptId> optIds = node.getOptId();
        
        int optIdListLength = optIds.size();
        int exprListLength = node.getExpr().size();
        
        if (exprListLength > 0 && optIdListLength != exprListLength)
            this.throwWeederException(node,
                "Assignment count mismatch: " + optIdListLength + " = " + exprListLength);    

        if (optIdListLength == 1 && optIds.getFirst() instanceof ABlankOptId)
            this.throwWeederException(node, "No new variables declared on the left side of :=");
    }

    // Throw an error if the number of identifiers on the R.H.S. of an assignment statement is not
    // equal to the number of expressions on the L.H.S.
    @Override
    public void inAAssignStmt(AAssignStmt node) {
        int LHSLength = node.getLhs().size();
        int RHSLength = node.getRhs().size();
        
        if (LHSLength != RHSLength)
            throwWeederException(node,
                "L.H.S and R.H.S. of assignment don't match");        
    }

    // Throw an error if increment is applied to a non-incrementable. 
    @Override
    public void inAIncrStmt(AIncrStmt node) {
        PExpr pExpr = node.getExpr();
        if (!this.isNonConstant(pExpr))
            // TODO: Add string of error-causing code.
            this.throwWeederException(node, "Cannot assign");
    }

    // Throw an error if decrement is applied to a non-decrementable.
    @Override
    public void inADecrStmt(ADecrStmt node) {
        PExpr pExpr = node.getExpr();
        if (!this.isNonConstant(pExpr))
            // TODO: Add string of error-causing code.
            this.throwWeederException(node, "Cannot assign");
    }

    // Throw an error if a switch statement contains multiple default cases.
    @Override
    public void inASwitchStmt(ASwitchStmt node) {
        boolean hasDefaultCase = false;
        
        for (PCaseBlock pCaseBlock : node.getCaseBlock()) {
            ABlockCaseBlock caseBlock = (ABlockCaseBlock) pCaseBlock;

            // Default case has been found.
            if ((caseBlock.getCaseCondition()) instanceof ADefaultCaseCondition) {
                if (hasDefaultCase)
                    this.throwWeederException(caseBlock,
                        "Switch statement contains multiple default cases");

                hasDefaultCase = true;
            }
        }
    }

    // Throw an error if a break statement occurs outside a loop.
    @Override
    public void inABreakStmt(ABreakStmt node) {
        if (!this.inLoop())
            this.throwWeederException(node, "break outside loop");
    }

    // Throw an error if a continue statement occurs outside a loop.
    @Override 
    public void inAContinueStmt(AContinueStmt node) {
        if (!this.inLoop())
            this.throwWeederException(node, "continue outside loop");
    }

    // Throw an error is the last part is a short assignment, otherwise increase the loop depth by
    // 1.
    @Override
    public void inALoopStmt(ALoopStmt node) {
       if (node.getEnd() instanceof AShortAssignStmt)
            this.throwWeederException(node, "Cannot declare in the for-increment");

        this.loopDepth++;
    }

    // Decrease the loop depth by 1.
    @Override 
    public void outALoopStmt(ALoopStmt node) {
        this.loopDepth--;
    }

    /**
     * Checks whether the traversal is in a loop.
     *
     * @return True if the loop depth is greater than 0, false otherwise
     */
    private boolean inLoop() {
        return (this.loopDepth > 0);
    }

    // Throws an error if the array bound is not an integer.
    @Override
    public void inAArrayTypeExpr(AArrayTypeExpr node) {
        PExpr pExpr = node.getExpr();

        if (!(pExpr instanceof AIntLitExpr
            || pExpr instanceof AOctLitExpr
            || pExpr instanceof AHexLitExpr))
            this.throwWeederException(node, "Non-integer array bound");
    }

    // Throw error if type casting for string.
    @Override
    public void inATypeCastExpr(ATypeCastExpr node) {
        if (node.getTypeExpr() instanceof AStringTypeExpr)
            this.throwWeederException(node, "Cannot cast to type string");
    }

    // Throw an error if the object in a field access is not non-constant and not a function call.
    @Override
    public void inAFieldExpr(AFieldExpr node) {
        PExpr obj = node.getExpr();
        if (!(this.isNonConstant(obj) || obj instanceof AFuncCallExpr))
            // TODO: Change message to "Invalid operation" + (error-causing code).
            this.throwWeederException(node, "Invalid field access operation");
    }    

    // Throw an error if the array in an array access is not non-constant and not a function call. 
    @Override
    public void inAArrayElemExpr(AArrayElemExpr node) {
        PExpr array = node.getArray();
        if (!(this.isNonConstant(array) || array instanceof AFuncCallExpr))
            // TODO: Change message to "Invalid operation" + (error-causing code).
            this.throwWeederException(node, "Invalid array access operation");
    }

    /**
     * Checks whether the given expression is non-constant, i.e. a variable,
     * field, or array element.
     *
     * @param pExpr - Production expression node
     * @return True if the expression is non-constant, false otherwise
     */
    private boolean isNonConstant(PExpr pExpr) {
        return (pExpr instanceof AVariableExpr
            || pExpr instanceof AFieldExpr
            || pExpr instanceof AArrayElemExpr);
    }

}
