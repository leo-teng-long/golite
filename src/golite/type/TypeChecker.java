package golite.type;

import golite.exception.TypeCheckException;
import golite.symbol.FunctionSymbol;
import golite.symbol.Symbol;
import golite.symbol.SymbolTable;
import golite.symbol.TypeAliasSymbol;
import golite.symbol.VariableSymbol;
import golite.util.LineAndPosTracker;
import golite.analysis.*;
import golite.node.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;


/**
 * Type checker.
 */
public class TypeChecker extends DepthFirstAdapter {

	/** Symbol table. */
	private SymbolTable symbolTable;

	/** Type table. */
	private HashMap<Node, GoLiteType> typeTable;

	/** Line and position tracker for AST nodes. */
    private LineAndPosTracker lineAndPosTracker;

	/**
	 * Constructor.
	 *
	 * @param table - Symbol table with 0th and global scopes initialized (Check out
	 * {@link golite.symbol.SymbolTableBuilder})
	 */
	public TypeChecker(SymbolTable table) {
		super();
		this.symbolTable = table;

		this.typeTable = new HashMap<Node, GoLiteType>();
		this.lineAndPosTracker = new LineAndPosTracker();
	}
	
	/**
     * Returns the GoLite type for the given type expression.
     *
     * @param node - Type expression AST node
     * @return Corresponding GoLite type
     * @throws TypeCheckerException
     */
    private GoLiteType getType(PTypeExpr node) {
        if (node == null)
            return null;

        if (node instanceof ABoolTypeExpr)
            return new BoolType();
        else if (node instanceof AIntTypeExpr)
            return new IntType();
        else if (node instanceof AFloatTypeExpr)
            return new FloatType();
        else if (node instanceof ARuneTypeExpr)
            return new RuneType();
        else if (node instanceof AStringTypeExpr)
            return new StringType();
        else if (node instanceof AAliasTypeExpr) {
        	TId id = ((AAliasTypeExpr) node).getId();
            GoLiteType type = this.symbolTable.getSymbolType(id.getText());

            if (type == null)
            	this.throwTypeCheckException(id, "Undefined: " + id.getText());
            
            return new AliasType(id.getText(), type);
        } else if (node instanceof AArrayTypeExpr) {
            PExpr pExpr = ((AArrayTypeExpr) node).getExpr();

            int bound = 0;
            if (pExpr instanceof AIntLitExpr)
                bound = Integer.parseInt(((AIntLitExpr) pExpr).getIntLit().getText());
            else if (pExpr instanceof AOctLitExpr)
                bound = Integer.parseInt(((AOctLitExpr) pExpr).getOctLit().getText(), 8);
            else if (pExpr instanceof AHexLitExpr)
                bound = Integer.parseInt(((AHexLitExpr) pExpr).getHexLit().getText(), 16);
            else 
                this.throwTypeCheckException(pExpr, "Non-integer array bound");

            return new ArrayType(getType(((AArrayTypeExpr) node).getTypeExpr()),
                bound);
        } else if (node instanceof ASliceTypeExpr)
            return new SliceType(getType(((ASliceTypeExpr) node).getTypeExpr()));
        else if (node instanceof AStructTypeExpr) {
            StructType structType = new StructType();

            // Keep track of the field Id's to ensure there are no duplicates.
            HashSet<String> fieldIds = new HashSet<String>();

            // Loop over the field specifications.
            for (PFieldSpec pFieldSpec : ((AStructTypeExpr) node).getFieldSpec()) {
                // Get the optional Id's.
                LinkedList<POptId> pOptIds = ((ASpecFieldSpec) pFieldSpec).getOptId();

                // Loop over each Id.
                for(POptId pOptId : pOptIds) {
                    // Do not consider blank Id's.
                    if (pOptId instanceof AIdOptId) {
                        TId id = ((AIdOptId) pOptId).getId();

                        // Throw an error if a duplicate field is encountered.
                        if (fieldIds.contains(id.getText()))
                            this.throwTypeCheckException(id, "Duplicate field " + id.getText());

                        structType.addField(id.getText(),
                            getType(((ASpecFieldSpec) pFieldSpec).getTypeExpr()));
                        fieldIds.add(id.getText());
                    }
                }
            }

            return structType;
        }

        return null;
    }


	/**
     * Throws a type check exception after annotating the message with line and position
     * information.
     *
     * @param node - AST node
     * @param msg - Error message
     * @throws TypeCheckException
     */
    private void throwTypeCheckException(Node node, String msg) {
        Integer line = this.lineAndPosTracker.getLine(node);
        Integer pos = this.lineAndPosTracker.getPos(node);

        throw new TypeCheckException("[" + line + "," + pos + "] " + msg);
    }

    /**
     * Checks if the given Id has already been defined in the current scope.
     *
     * @param id - Id token
     * @throws TypeCheckException if the Id is already defined in the current scope
     */
    private void checkifDeclaredInCurrentScope(TId id) {
        if (this.symbolTable.defSymbolInCurrentScope(id.getText()))
            this.throwTypeCheckException(id, id.getText() + " redeclared in this block");
    }

    /**
     * Get the type of the given AST node from the type table.
     *
     * @param node - AST node
     * @return Type
     * @throws TypeCheckException if the node doesn't exist in the table.
     */
	private GoLiteType getType(Node node) {
		GoLiteType type = this.typeTable.get(node);

		if (type == null)
			this.throwTypeCheckException(node,
				"Type missing for " + node.getClass().getSimpleName());

		return type;
	}

	/**
	 * Get Id tokens from the given AST node.
	 *
	 * @param node - AST node
	 * @return List of Id tokens
	 */
	private ArrayList<TId> getIds(Node node) {
        ArrayList<TId> ids = new ArrayList<TId>();

        // Variable specification.
        if (node instanceof ASpecVarSpec) {
            LinkedList<POptId> pOptIds = ((ASpecVarSpec) node).getOptId();
            
            for (POptId o: pOptIds) {
            	// Ignore blank Id's.
                if (o instanceof AIdOptId)
                    ids.add(((AIdOptId) o).getId());
            }
        // Type specification.
        } else if (node instanceof ASpecTypeSpec) {
            POptId pOptId = ((ASpecTypeSpec) node).getOptId();
            
            if (pOptId instanceof AIdOptId)
                ids.add(((AIdOptId) pOptId).getId());
        } else if (node instanceof AArgArgGroup)
        	ids = new ArrayList<TId>(((AArgArgGroup) node).getId());

        return ids;
    }

	@Override
    public void inStart(Start node) {
    	// Gather all line and position information.
        node.apply(this.lineAndPosTracker);
    }

    // Unscope the 0th scope upon exit.
    @Override
    public void outStart(Start node) {
        this.symbolTable.unscope();
    }

    // Unscope the global scope upon program exit.
    @Override
    public void outAProgProg(AProgProg node) {
    	this.symbolTable.unscope();
    }

    // Adds global variables to the symbol table that require type inference and checks other
    // global variables have declared types that are consistent with their initializing expressions
    // (if any).
    @Override
    public void outAVarsTopDec(AVarsTopDec node) {
        // Loop over the variable specifications.
        for(PVarSpec pVarSpec : node.getVarSpec()) {
            // Get the expressions on the R.H.S.
            LinkedList<PExpr> pExprs = ((ASpecVarSpec) pVarSpec).getExpr();

	        // Flag for whether the variables are initialized with expressions.
	        boolean isInitialized = (pExprs.size() > 0);

            // Loop over each Id, tracking the position in the specfication.
            int i = 0;
            // Symbol table already checks if the name is already taken by another identifier in the
            // global scope.
            for (TId id : this.getIds(((ASpecVarSpec) pVarSpec))) {
                PTypeExpr pTypeExpr = ((ASpecVarSpec) pVarSpec).getTypeExpr();
                // Type must be inferred.
                if (pTypeExpr == null) {
                	// Expression should exist, otherwise a parser or weeder would've caught the
                	// Error.
                	PExpr pExpr = ((ASpecVarSpec) pVarSpec).getExpr().get(i);

                	GoLiteType type = this.getType(pExpr);

                	// Expression is a void function call 
                	if (type instanceof VoidType) {
                		TId funcId = ((AFuncCallExpr) pExpr).getId();
                		this.throwTypeCheckException(pExpr,
                			funcId.getText() + "() used as a value");
                	}
                		
                	this.symbolTable.putSymbol(new VariableSymbol(id.getText(), type, node));
                // Type is declared and so check that the type declaration and initializing
                // expressions (if any) are type compatible.
                } else {
                	// GoLite type of the type expression.
                	GoLiteType typeExprType = this.getType(pTypeExpr);

                	// Variable is initialized with an expression.
                	if (isInitialized) {
                		// Get the corresponding expression node.
                		PExpr pExpr = ((ASpecVarSpec) pVarSpec).getExpr().get(i);
                		// Get its GoLite type.
                		GoLiteType exprType = this.getType(pExpr);
                		
                		// Check the types for compatibility, throwing an error if not.
                		if (!typeExprType.equals(exprType))
                			this.throwTypeCheckException(pExpr,
                				"Cannot use value of type " + exprType + " for "
                				+ typeExprType);
                	}
                }

                // Increment to next position.
                i++;
            }
        }
    }

    // Enter the body of a function declaration.
    @Override
    public void caseAFuncTopDec(AFuncTopDec node) {
     	// Function name, which is guaranteed to be unique, otherwise the symbol table would have
     	// caught the duplicate.
        TId id = node.getId();
        // Function symbol is already in the symbol table.
        FunctionSymbol funcSymbol = (FunctionSymbol) this.symbolTable.getSymbol(id.getText());

     	// Enter the function body.
     	this.symbolTable.scope();

     	// Add the argument symbols to the current scope.
     	for (PArgGroup p : node.getArgGroup())
     		p.apply(this);

     	// Recurse on each statement.
     	for (PStmt s: node.getStmt())
     		s.apply(this);

     	// Exit the fucntion body.
     	this.symbolTable.unscope();
    }

    // Add function arguments as variable symbols into the current scope.
    @Override
    public void inAArgArgGroup(AArgArgGroup node) {
        GoLiteType type = this.getType(node.getTypeExpr());

        // Loop over each identifier in the group and add a new variable symbol into the function
        // scope for each.
        for (TId id : this.getIds(node)) {
        	// Throw an error if multiple arguments have the same Id.
            this.checkifDeclaredInCurrentScope(id);
            this.symbolTable.putSymbol(new VariableSymbol(id.getText(), type, node));
        }
    }

    // Add non-global variables declared into the symbol table, performing type compatability checks
    // and type inference, if necessary.
    @Override
    public void outASpecVarSpec(ASpecVarSpec node) {
        // Get the expressions on the R.H.S.
        LinkedList<PExpr> pExprs = node.getExpr();

        // Flag for whether the variables are initialized with expressions.
        boolean isInitialized = (pExprs.size() > 0);

        // Loop over each Id, tracking the position in the specfication.
        int i = 0;
       	for (TId id : this.getIds(node)) {
           	// Skip variable specifications in the global scope, they're already taken care of in
           	// symbol table building.
            if (this.symbolTable.inGlobalScope())
            	return;

           	// Throw an error if a symbol with the given Id already exists in the current scope
           	// and the current scope.
            this.checkifDeclaredInCurrentScope(id);

            PTypeExpr pTypeExpr = node.getTypeExpr();
            // Type not declared and so must be inferred.
            if (pTypeExpr == null) {
            	// Expression should exist, otherwise a parser or weeder would've caught the
            	// Error.
            	PExpr pExpr = node.getExpr().get(i);

            	GoLiteType type = this.getType(pExpr);

            	// Expression is a void function call 
            	if (type instanceof VoidType) {
            		TId funcId = ((AFuncCallExpr) pExpr).getId();
            		this.throwTypeCheckException(pExpr,
            			funcId.getText() + "() used as a value");
            	}
            		
            	this.symbolTable.putSymbol(new VariableSymbol(id.getText(), type, node));
            } else {
            	// GoLite type of the type expression.
            	GoLiteType typeExprType = this.getType(pTypeExpr);

            	// Variable is initialized with an expression.
            	if (isInitialized) {
            		// Get the corresponding expression node.
            		PExpr pExpr = node.getExpr().get(i);
            		// Get its GoLite type.
            		GoLiteType exprType = this.getType(pExpr);
            		
            		// Check the underlying type (in case of an alias) of the L.H.S for
            		// compatibility with the surface type of the R.H.S. expression, throwing an
            		// error if their incompatible.
            		if (!typeExprType.getUnderlyingType().equals(exprType))
            			this.throwTypeCheckException(pExpr, "Cannot use type " + exprType
            				+ " as type " + typeExprType + " in assignment");
            	}

            	// Put a new variable symbol into the symbol table.
            	this.symbolTable.putSymbol(new VariableSymbol(id.getText(), typeExprType,
            		node));
            }

            // Increment the position.
            i++;
        }
    }

    @Override
    public void inASpecTypeSpec(ASpecTypeSpec node) {
    	// Skip type specifications in the global scope, they're already taken care of in symbol
    	// table building.
        if (this.symbolTable.inGlobalScope())
        	return;

        for (TId id: this.getIds(node)) {
        	// Throw an error if the name is already taken by another identifier in the current
        	// scope.
            this.checkifDeclaredInCurrentScope(id);

            // Get the GoLite type of the type expression.
            GoLiteType type = this.getType(node.getTypeExpr());
            // Add a type alias symbol to the symbol table.
            this.symbolTable.putSymbol(new TypeAliasSymbol(id.getText(), type, node));
            this.typeTable.put(node, type);
        }
    }

    @Override
    public void outAVariableExpr(AVariableExpr node) {
    	// Get the corresponding symbol.
        Symbol symbol = this.symbolTable.getSymbol(node.getId().getText());

        // Symbol was never declared, so throw an error.
        if (symbol == null)
			this.throwTypeCheckException(node.getId(), "Undefined: " + node.getId().getText());
		// Symbol is not a variable, so throw an error.
		else if (!(symbol instanceof VariableSymbol))
			this.throwTypeCheckException(node.getId(),
				node.getId().getText() + " is not a variable");

        this.typeTable.put(node, symbol.getType());
    }

    /* Type check literals. */

    // Decimal integer.
    @Override
    public void outAIntLitExpr(AIntLitExpr node) {
        this.typeTable.put(node, new IntType());
    }

    // Octal integer.
    @Override
    public void outAOctLitExpr(AOctLitExpr node) {
        this.typeTable.put(node, new IntType());
    }

    // Hexidecimal integer.
    @Override
    public void outAHexLitExpr(AHexLitExpr node) {
        this.typeTable.put(node, new IntType());
    }

    // Float.
    @Override
    public void outAFloatLitExpr(AFloatLitExpr node) {
        this.typeTable.put(node, new FloatType());
    }

    // Rune.
    @Override
    public void outARuneLitExpr(ARuneLitExpr node) {
        this.typeTable.put(node, new RuneType());
    }

    // Interpreted string.
    @Override
    public void outAInterpretedStringLitExpr(AInterpretedStringLitExpr node) {
        typeTable.put(node, new StringType());
    }

    // Raw string.
    @Override
    public void outARawStringLitExpr(ARawStringLitExpr node) {
        typeTable.put(node, new StringType());
    }

}
