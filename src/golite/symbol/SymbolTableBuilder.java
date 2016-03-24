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
     	TId id = node.getId();

     	this.checkifDeclaredInCurrentScope(id);

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