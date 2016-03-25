package golite.symbol;

import golite.exception.SymbolTableException;
import golite.util.LineAndPosTracker;
import golite.analysis.*;
import golite.node.*;

import java.util.LinkedList;


/**
 * Builds the symbol table for a GoLite program.
 */
public class SymbolTableBuilder extends DepthFirstAdapter {

	/** Symbol table. */
	private SymbolTable table;
	/** Line and position tracker for AST nodes. */
	private LineAndPosTracker lineAndPosTracker = new LineAndPosTracker();

	/**
	 * Getter.
	 */
	public SymbolTable getTable() {
		return this.table;
	}

	/**
	 * Return the symbol type for the given type expression.
     *
     * @param node - Type expression AST node
     * @return Corresponding symbol type
	 */
	private SymbolType getSymbolType(PTypeExpr node) {
		if (node == null)
			return null;

		if (node instanceof ABoolTypeExpr)
			return new BoolSymbolType();
		else if (node instanceof AIntTypeExpr)
			return new IntSymbolType();
		else if (node instanceof AFloatTypeExpr)
			return new FloatSymbolType();
		else if (node instanceof ARuneTypeExpr)
			return new RuneSymbolType();
		else if (node instanceof AStringTypeExpr)
			return new StringSymbolType();
        else if (node instanceof AArrayTypeExpr) {
            PExpr pExpr = ((AArrayTypeExpr) node).getExpr();

            int bound = 0;
            if (pExpr instanceof AIntLitExpr)
                bound = Integer.parseInt(((AIntLitExpr) pExpr).getIntLit().getText());
            else if (pExpr instanceof AOctLitExpr)
                bound = Integer.parseInt(((AOctLitExpr) pExpr).getOctLit().getText(), 8);
            else if (pExpr instanceof AHexLitExpr)
                bound = Integer.parseInt(((AHexLitExpr) pExpr).getHexLit().getText(), 16);
            else 
                this.throwSymbolTableException(node, "Non-integer array bound");

            return new ArraySymbolType(this.getSymbolType(((AArrayTypeExpr) node).getTypeExpr()),
                bound);
        } else if (node instanceof ASliceTypeExpr) {
            return new SliceSymbolType(this.getSymbolType(((ASliceTypeExpr) node).getTypeExpr()));
        }

		return null;
	}

	/**
     * Throws a symbol table exception after annotating the message with line and position
     * information.
     *
     * @param node - AST node
     * @param msg - Error message
     * @throws SymbolTableException
     */
    private void throwSymbolTableException(Node node, String msg) {
        Integer line = this.lineAndPosTracker.getLine(node);
        Integer pos = this.lineAndPosTracker.getPos(node);

        throw new SymbolTableException("[" + line + "," + pos + "] " + msg);
    }

    /**
     * Checks if the given Id has already been defined in the current scope, throwing an a symbol
     * table exception if it is.
     *
     * @param id - Id token
     * @throws SymbolTableException
     */
	private void checkifDeclaredInCurrentScope(TId id) {
		if (this.table.defSymbolInCurrentScope(id.getText()))
			this.throwSymbolTableException(id, id.getText() + " redeclared in this block");
	}
	
	@Override
    public void inStart(Start node) {
    	// Gather all line and position information.
        node.apply(this.lineAndPosTracker);

        this.table = new SymbolTable();
        // Enter the 0-th scope and never leave it.
      	this.table.scope();

        // Initialize boolean literals.
        Symbol trueSymbol = new VariableSymbol("true", new BoolSymbolType());
        Symbol falseSymbol = new VariableSymbol("false", new BoolSymbolType());
        this.table.putSymbol(trueSymbol);
        this.table.putSymbol(falseSymbol);

        // Shadow them.
        this.table.scope();
    }


    @Override
    public void caseASpecVarSpec(ASpecVarSpec node) {
        LinkedList<POptId> pOptIds = node.getOptId();

        // Loop over each Id.
        for(POptId p : pOptIds) {
        	// Do not consider blank Id's.
        	if (p instanceof AIdOptId) {
        		TId id = ((AIdOptId) p).getId();
        		PTypeExpr pTypeExpr = node.getTypeExpr();
        		// Add a variable symbol to the symbol table.
            	this.table.putSymbol(new VariableSymbol(id.getText(),
            		this.getSymbolType(pTypeExpr)));
            }
        }
    }

	@Override
    public void caseAFuncTopDec(AFuncTopDec node) {
     	// Function name.
        TId id = node.getId();

        // Throw an error if the name is already taken by another identifier in the current scope.
     	this.checkifDeclaredInCurrentScope(id);

        // Return type expression.
     	PTypeExpr pTypeExpr = node.getTypeExpr();

     	FunctionSymbol funcSymbol = null;
     	// No return type.
     	if (pTypeExpr == null)
     		funcSymbol = new FunctionSymbol(id.getText(), null);
     	// Has return type.
     	else
            funcSymbol = new FunctionSymbol(id.getText(), this.getSymbolType(pTypeExpr));

     	// Add argument types to the function symbol.
     	AArgArgGroup g = null;
     	for (PArgGroup p : node.getArgGroup()) {
     		g = (AArgArgGroup) p;
     		funcSymbol.addArgType(this.getSymbolType(g.getTypeExpr()), g.getId().size());
     	}

     	// Enter symbol into the table.
     	this.table.putSymbol(funcSymbol);

     	// Enter the function body.
     	this.table.scope();

     	// Add the argument symbols to the current scope.
     	for (PArgGroup p : node.getArgGroup()) {
     		g = (AArgArgGroup) p;
     		p.apply(this);
     	}

     	// Recurse on each statement.
     	for (PStmt s: node.getStmt())
     		s.apply(this);

     	// Exit the fucntion body.
     	this.table.unscope();
    }

    @Override
    public void caseAArgArgGroup(AArgArgGroup node) {
		SymbolType type = this.getSymbolType(node.getTypeExpr());

		// Loop over each identifier in the group and add a new variable symbol for each.
		for (TId id : node.getId()) {
			this.checkifDeclaredInCurrentScope(id);
			this.table.putSymbol(new VariableSymbol(id.getText(), type));
		}
	}

	// Unscope upon exiting the program.
    @Override
	public void outStart(Start node) {
		this.table.unscope();
	}

}