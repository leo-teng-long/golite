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
import java.util.Iterator;
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

    // Keeps track of the function symbol when entering the body of a function.
    private FunctionSymbol currentFunctionSymbol;
    // Keeps track of the switch condition type when entering the body of a switch.
    private GoLiteType currentSwitchCondType;

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
	 * Returns whether the given type is integer or rune.
	 *
	 * @param type - Type
	 * @return True if it's integer or rune, false otherwise
	 */
    private boolean isIntOrRuneType(GoLiteType type) {
        return type instanceof IntType || type instanceof RuneType;
    }

	/**
	 * Returns whether the given type is numeric (i.e. integer, rune, or float).
	 *
	 * @param type - Type
	 * @return True if it's numeric, false otherwise
	 */
    private boolean isNumericType(GoLiteType type) {
        return this.isIntOrRuneType(type) || type instanceof FloatType;
    }

    /**
	 * Returns whether the given type is ordered (i.e. integer, rune, float, or string).
	 *
	 * @param type - Type
	 * @return True if it's ordered, false otherwise
	 */
    private boolean isOrderedType(GoLiteType type) {
        return this.isNumericType(type) || type instanceof StringType;
    }

    /**
	 * Returns whether the given type is comparable (i.e. boolean, ordered, array, or struct).
	 *
	 * @param type - Type
	 * @return True if it's comparable, false otherwise
	 */
    private boolean isComparableType(GoLiteType type) {
        // Boolean's and ordered's are comparable.
        if (type instanceof BoolType || this.isOrderedType(type))
        	return true;

        // Arrays are comparable if their element type is comparable.
        if (type instanceof ArrayType)
        	return this.isComparableType(((ArrayType) type).getType());

        // Structs are comparable if all their fields are comparable.
        if (type instanceof StructType) {
        	Iterator<StructType.Field> fieldIter = ((StructType) type).getFieldIterator();
        	while (fieldIter.hasNext()) {
        		if (!this.isComparableType(fieldIter.next().getType()))
        			return false;
        	}

        	return true;
        }

        return false;
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
        else if (node instanceof AShortAssignStmt) {
            LinkedList<POptId> pOptIds = ((AShortAssignStmt) node).getOptId();
            
            for (POptId o: pOptIds) {
            	// Ignore blank Id's.
                if (o instanceof AIdOptId)
                    ids.add(((AIdOptId) o).getId());
            }
        }

        return ids;
    }

    /**
     * Get the variable symbol corresponding to the given Id token.
     *
     * @param id - Id token
     * @return Corresponding variable symbol
     * @throws TypeCheckException if the symbol is undefined or is not a variable symbol
     */
    private VariableSymbol getVariableSymbol(TId id) {
    	// Get the corresponding symbol.
        Symbol symbol = this.symbolTable.getSymbol(id.getText());

        // Symbol was never declared, so throw an error.
        if (symbol == null)
			this.throwTypeCheckException(id, "Undefined: " + id.getText());
		// Symbol is not a variable, so throw an error.
		else if (!(symbol instanceof VariableSymbol))
			this.throwTypeCheckException(id,
				id.getText() + " is not a variable");

		return (VariableSymbol) symbol;
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

        // Set the current function symbol so descendants can access function information.
        this.currentFunctionSymbol = funcSymbol;

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

     	this.currentFunctionSymbol = null;
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

    /** Type check statements. **/

    // Empty statement.
    @Override
    public void outAEmptyStmt(AEmptyStmt node) {
    	// Trivially typed.
    }

    // Short assignment statement.
    @Override
    public void outAShortAssignStmt(AShortAssignStmt node) {
       	// Get L.H.S. (non-blank) Id's.
        ArrayList<TId> ids = this.getIds(node);
        // Get R.H.S. expressions.
        ArrayList<PExpr> pExprs = new ArrayList<PExpr>(node.getExpr());

        // Flag for whether the L.H.S. has any new variables declared in the current scope.
        boolean hasNewDecInCurrentScope = false;
        // Loop through the Id's in sequence, tracking the position.
        for (int i = 0; i < ids.size(); i++) {
        	TId id = ids.get(i);
        	String name = id.getText();

        	// Get the corresponding expression node.
    		PExpr pExpr = pExprs.get(i);
    		// Get its GoLite type.
    		GoLiteType exprType = this.getType(pExpr);

    		// Expression is a void function call.
        	if (exprType instanceof VoidType) {
        		TId funcId = ((AFuncCallExpr) pExpr).getId();
        		this.throwTypeCheckException(pExpr,
        			funcId.getText() + "() used as a value");
        	}

        	// A symbol with the given name already exists in the current scope.
            if (symbolTable.defSymbolInCurrentScope(name)) {
            	// Get the variable symbol.
                VariableSymbol symbol = this.getVariableSymbol(id);

        		// Check the underlying type (in case of an alias) of the L.H.S for
        		// compatibility with the surface type of the R.H.S. expression, throwing an
        		// error if their incompatible.
        		if (!symbol.getUnderlyingType().equals(exprType))
        			this.throwTypeCheckException(pExpr, "Cannot use type " + exprType
        				+ " as type " + symbol.getType() + " in assignment");
            // No such symbol exists in the current scope.
            } else {
            	// Go ahead and add it to the symbol table, using its inferred type, which may
            	// shadow an outer scope symbol with the same name.
            	this.symbolTable.putSymbol(new VariableSymbol(name, exprType, node));
                hasNewDecInCurrentScope = true;
	        }

	        this.typeTable.put(id, exprType);
        }

        // Throw an error is no new variables are declared on the L.H.S.
        if (!hasNewDecInCurrentScope)
            this.throwTypeCheckException(node, "No new variables on left side of :=");
    }

    // Assignment statement.
    @Override
    public void outAAssignStmt(AAssignStmt node) {
    	// L.H.S. and R.H.S. expressions (Both sides are checked to be the same size by the weeder).
        LinkedList<PExpr> pExprsLHS = node.getLhs();
        LinkedList<PExpr> pExprsRHS = node.getRhs();

        for (int i = 0; i < pExprsLHS.size(); i++) {
            GoLiteType leftExprType = this.getType(pExprsLHS.get(i));

            PExpr rightExpr = pExprsRHS.get(i);
            GoLiteType rightExprType = this.getType(rightExpr);

            if (!leftExprType.getUnderlyingType().equals(rightExprType))
                this.throwTypeCheckException(rightExpr, "Cannot use type " + rightExprType
        				+ " as type " + leftExprType + " in assignment");
        }
    }

    /* Type check op-assign statements. */

    // Plus-assignment statement.
    @Override
    public void outAPlusAssignStmt(APlusAssignStmt node) {
    	// L.H.S. is guaranteed to be an assignable by the grammar.
        GoLiteType leftExprType = this.getType(node.getLhs());
        GoLiteType rightExprType = this.getType(node.getRhs());

        // Throw an error if the types are not compatible.
        if (leftExprType.getUnderlyingType().equals(rightExprType))
            this.throwTypeCheckException(node, "Invalid operation '+=': Mismatched types "
            	+ leftExprType + " and " + rightExprType);

        // Throw an error if the type is not ordered and hence cannot be plused.
        if (!this.isOrderedType(leftExprType))
            this.throwTypeCheckException(node,
            	"Invalid operation '+=': Operator '+' not defined on " + leftExprType);
    }

    // Minus-assignment statement.
    @Override
    public void outAMinusAssignStmt(AMinusAssignStmt node) {
    	// L.H.S. is guaranteed to be an assignable by the grammar.
        GoLiteType leftExprType = this.getType(node.getLhs());
        GoLiteType rightExprType = this.getType(node.getRhs());

        // Throw an error if the types are not compatible.
        if (leftExprType.getUnderlyingType().equals(rightExprType))
            this.throwTypeCheckException(node, "Invalid operation '-=': Mismatched types "
            	+ leftExprType + " and " + rightExprType);

        // Throw an error if the type is not numeric and hence cannot be minused.
        if (!this.isNumericType(leftExprType))
            this.throwTypeCheckException(node,
            	"Invalid operation '-=': Operator '-' not defined on " + leftExprType);
    }

    // Mult-assignment statement.
    @Override
    public void outAStarAssignStmt(AStarAssignStmt node) {
    	// L.H.S. is guaranteed to be an assignable by the grammar.
        GoLiteType leftExprType = this.getType(node.getLhs());
        GoLiteType rightExprType = this.getType(node.getRhs());

        // Throw an error if the types are not compatible.
        if (leftExprType.getUnderlyingType().equals(rightExprType))
            this.throwTypeCheckException(node, "Invalid operation '*=': Mismatched types "
            	+ leftExprType + " and " + rightExprType);

        // Throw an error if the type is not numeric and hence cannot be multipled.
        if (!this.isNumericType(leftExprType))
            this.throwTypeCheckException(node,
            	"Invalid operation '*=': Operator '*' not defined on " + leftExprType);
    }

    // Div-assignment statement.
    @Override
    public void outASlashAssignStmt(ASlashAssignStmt node) {
    	// L.H.S. is guaranteed to be an assignable by the grammar.
        GoLiteType leftExprType = this.getType(node.getLhs());
        GoLiteType rightExprType = this.getType(node.getRhs());

        // Throw an error if the types are not compatible.
        if (leftExprType.getUnderlyingType().equals(rightExprType))
            this.throwTypeCheckException(node, "Invalid operation '/=': Mismatched types "
            	+ leftExprType + " and " + rightExprType);

        // Throw an error if the type is not numeric and hence cannot be divided.
        if (!this.isNumericType(leftExprType))
            this.throwTypeCheckException(node,
            	"Invalid operation '/=': Operator '/' not defined on " + leftExprType);
    }

    // Mod-assignment statement.
    @Override
    public void outAPercAssignStmt(APercAssignStmt node) {
    	// L.H.S. is guaranteed to be an assignable by the grammar.
        GoLiteType leftExprType = this.getType(node.getLhs());
        GoLiteType rightExprType = this.getType(node.getRhs());

        // Throw an error if the types are not compatible.
        if (leftExprType.getUnderlyingType().equals(rightExprType))
            this.throwTypeCheckException(node, "Invalid operation '%=': Mismatched types "
            	+ leftExprType + " and " + rightExprType);

        // Throw an error if the type is not integer or rune and hence cannot be modulo'd.
        if (!this.isIntOrRuneType(leftExprType))
            this.throwTypeCheckException(node,
            	"Invalid operation '%=': Operator '%' not defined on " + leftExprType);
    }

    // Bit-and-assignment statement.
    @Override
    public void outAAndAssignStmt(AAndAssignStmt node) {
        // L.H.S. is guaranteed to be an assignable by the grammar.
        GoLiteType leftExprType = this.getType(node.getLhs());
        GoLiteType rightExprType = this.getType(node.getRhs());

        // Throw an error if the types are not compatible.
        if (leftExprType.getUnderlyingType().equals(rightExprType))
            this.throwTypeCheckException(node, "Invalid operation '&=': Mismatched types "
            	+ leftExprType + " and " + rightExprType);

        // Throw an error if the type is not integer or rune and hence cannot be bit-anded.
        if (!this.isIntOrRuneType(leftExprType))
            this.throwTypeCheckException(node,
            	"Invalid operation '&=': Operator '&' not defined on " + leftExprType);
    }

    // Bit-or-assignment statement.
    @Override
    public void outAPipeAssignStmt(APipeAssignStmt node) {
        // L.H.S. is guaranteed to be an assignable by the grammar.
        GoLiteType leftExprType = this.getType(node.getLhs());
        GoLiteType rightExprType = this.getType(node.getRhs());

        // Throw an error if the types are not compatible.
        if (leftExprType.getUnderlyingType().equals(rightExprType))
            this.throwTypeCheckException(node, "Invalid operation '|=': Mismatched types "
            	+ leftExprType + " and " + rightExprType);

        // Throw an error if the type is not integer or rune and hence cannot be bit-or'd.
        if (!this.isIntOrRuneType(leftExprType))
            this.throwTypeCheckException(node,
            	"Invalid operation '|=': Operator '|' not defined on " + leftExprType);
    }

    // Bit-xor-assignment statement.
    @Override
    public void outACarotAssignStmt(ACarotAssignStmt node) {
        // L.H.S. is guaranteed to be an assignable by the grammar.
        GoLiteType leftExprType = this.getType(node.getLhs());
        GoLiteType rightExprType = this.getType(node.getRhs());

        // Throw an error if the types are not compatible.
        if (leftExprType.getUnderlyingType().equals(rightExprType))
            this.throwTypeCheckException(node, "Invalid operation '^=': Mismatched types "
            	+ leftExprType + " and " + rightExprType);

        // Throw an error if the type is not integer or rune and hence cannot be bit-xor'd.
        if (!this.isIntOrRuneType(leftExprType))
            this.throwTypeCheckException(node,
            	"Invalid operation '^=': Operator '^' not defined on " + leftExprType);
    }

    // Bit-clear-assignment statement.
    @Override
    public void outAAmpCarotAssignStmt(AAmpCarotAssignStmt node) {
        // L.H.S. is guaranteed to be an assignable by the grammar.
        GoLiteType leftExprType = this.getType(node.getLhs());
        GoLiteType rightExprType = this.getType(node.getRhs());

        // Throw an error if the types are not compatible.
        if (leftExprType.getUnderlyingType().equals(rightExprType))
            this.throwTypeCheckException(node, "Invalid operation '&^=': Mismatched types "
            	+ leftExprType + " and " + rightExprType);

        // Throw an error if the type is not integer or rune and hence cannot be bit-cleared.
        if (!this.isIntOrRuneType(leftExprType))
            this.throwTypeCheckException(node,
            	"Invalid operation '&^=': Operator '&^' not defined on " + leftExprType);
    }

    // Left-shift-assignment statement.
    @Override
    public void outALshiftAssignStmt(ALshiftAssignStmt node) {
        // L.H.S. is guaranteed to be an assignable by the grammar.
        GoLiteType leftExprType = this.getType(node.getLhs());
        GoLiteType rightExprType = this.getType(node.getRhs());

        // Throw an error if the types are not compatible.
        if (leftExprType.getUnderlyingType().equals(rightExprType))
            this.throwTypeCheckException(node, "Invalid operation '<<=': Mismatched types "
            	+ leftExprType + " and " + rightExprType);

        // Throw an error if the type is not integer or rune and hence cannot be left-shifted.
        if (!this.isIntOrRuneType(leftExprType))
            this.throwTypeCheckException(node,
            	"Invalid operation '<<=': Operator '<<' not defined on " + leftExprType);
    }

    // Right-shift-assignment statement.
    @Override
    public void outARshiftAssignStmt(ARshiftAssignStmt node) {
        // L.H.S. is guaranteed to be an assignable by the grammar.
        GoLiteType leftExprType = this.getType(node.getLhs());
        GoLiteType rightExprType = this.getType(node.getRhs());

        // Throw an error if the types are not compatible.
        if (leftExprType.getUnderlyingType().equals(rightExprType))
            this.throwTypeCheckException(node, "Invalid operation '>>=': Mismatched types "
            	+ leftExprType + " and " + rightExprType);

        // Throw an error if the type is not integer or rune and hence cannot be right-shifted.
        if (!this.isIntOrRuneType(leftExprType))
            this.throwTypeCheckException(node,
            	"Invalid operation '>>=': Operator '>>' not defined on " + leftExprType);
    }

    // Expression statement.
    @Override
    public void outAExprStmt(AExprStmt node) {
    	// Well-typedness is checked in expression nodes.
    }

    // Print statement.
    @Override
    public void outAPrintStmt(APrintStmt node) {
        for (PExpr pExpr : node.getExpr()) {
            GoLiteType type = this.getType(pExpr);
            
            // Only primitive types can be printed.
            if (!(type instanceof PrimitiveGoLiteType))
                this.throwTypeCheckException(pExpr, "Cannot print type " + type);
        }
    }

    // Println statement.
    @Override
    public void outAPrintlnStmt(APrintlnStmt node) {
        for (PExpr pExpr : node.getExpr()) {
            GoLiteType type = this.getType(pExpr);
            
            // Only primitive types can be printed.
            if (!(type instanceof PrimitiveGoLiteType))
                this.throwTypeCheckException(pExpr, "Cannot println type " + type);
        }
    }

    // Continue statement.
    @Override
    public void outAContinueStmt(AContinueStmt node) {
    	// Trivially typed.
    }

    // Break statement.
    @Override
    public void outABreakStmt(ABreakStmt node) {
    	// Trivially typed.
    }

    // Return statement.
    @Override
    public void outAReturnStmt(AReturnStmt node) {
        // Current function's return type.
        GoLiteType returnType = this.currentFunctionSymbol.getReturnType();
        
        // Return expression.
        PExpr pExpr = node.getExpr();

        // Empty return statement.
        if (pExpr == null) {
        	// Throw an error if the return type is non-void.
            if (!(returnType instanceof VoidType))
                this.throwTypeCheckException(node, "Not enough arguments to return");
        // Non-empty return statement.
        } else {
        	// Throw an error if the return type is void.
         	if (returnType instanceof VoidType)
                this.throwTypeCheckException(pExpr, "Too many arguments to return");

            // Check the return type and return expression for type compatibility.
            GoLiteType exprType = this.typeTable.get(pExpr);
            if (returnType.getUnderlyingType().equals(exprType))
                this.throwTypeCheckException(pExpr, "Cannot use type " + exprType + " as type "
                	+ returnType + " in return argument");
        }
    }

    // If-else statement.
    @Override
    public void caseAIfElseStmt(AIfElseStmt node) {
        // Create a new scope for the if-else initializer and blocks.
        this.symbolTable.scope();

        // Type check the condition, if it exists.
        PCondition cond = node.getCondition();
        if (cond != null)
            cond.apply(this);

        // Enter the if-block if it exists.
        LinkedList<PStmt> pStmts = node.getIfBlock();
        if (pStmts != null) {
        	// Create a new scope for the if-block.
	        this.symbolTable.scope();
	        // Type check the if-block statements.
	        for (PStmt s : pStmts)
	            s.apply(this);
	        // Exit the scope for the if-block.
	        this.symbolTable.unscope();
        }

        // Enter the else-block if it exists.
        pStmts = node.getElseBlock();
        if (pStmts != null) {
		    // Create a new scope for the else-block.
		    this.symbolTable.scope();
		    // Type check the else-block statements.
		    for (PStmt s : pStmts)
		        s.apply(this);
		    // Exit the scope for the else-block.
		    this.symbolTable.unscope();
		}

        // Exit the scope for the if-else initializer and blocks.
    	this.symbolTable.unscope();
    }

    // If-else statement condition.
    @Override
    public void outAConditionCondition(AConditionCondition node) {
    	// If-else expression.
        PExpr pExpr = node.getExpr();
        if (pExpr != null) {
        	// Make sure the expression evaluates to a boolean, otherwise throw an error.
            GoLiteType type = this.getType(pExpr);
            if (!(type instanceof BoolType))
                this.throwTypeCheckException(pExpr,
                	"Non-bool (type " + type + ") used as if condition");
        }
    }

    // Switch statement.
    @Override
    public void caseASwitchStmt(ASwitchStmt node) {
         // Create a new scope for the switch initializer and blocks.
        this.symbolTable.scope();
        
        // Type check the initial statement, if it exists.
        PStmt pStmt = node.getStmt();
        if (pStmt != null)
            pStmt.apply(this);

        // Store the previous switch condition type and restore it at the end (for handling nested
        // switches).
        GoLiteType prevSwitchCondType = this.currentSwitchCondType;

        // Switch condition expression.
        PExpr pExpr = node.getExpr();
        if (pExpr == null)
        	// Set the condition type to boolean if no condition is provided.
        	this.currentSwitchCondType = new BoolType();
        else {
        	pExpr.apply(this);
        	this.currentSwitchCondType = this.getType(pExpr);

        	// Make sure the condition type is not void, otherwise throw an error.
        	if (this.currentSwitchCondType instanceof VoidType)
        		this.throwTypeCheckException(pExpr, "Void used as value");
        }
            
        // Type check each case block.
        for (PCaseBlock c : node.getCaseBlock())
            c.apply(this);
        
        // Restore the current switch condition type.
        this.currentSwitchCondType = prevSwitchCondType;

        // Exit the scope for the switch initializer and blocks.
    	this.symbolTable.unscope();
    }

    // Case block.
    @Override
    public void inABlockCaseBlock(ABlockCaseBlock node) {
        // Create a new scope for the case block.
        this.symbolTable.scope();
    }

    @Override
    public void outABlockCaseBlock(ABlockCaseBlock node) {
        // Exit the scope for the case block.
        this.symbolTable.unscope();
    }

    // Non-default case block contiion.
    @Override
    public void outAExprsCaseCondition(AExprsCaseCondition node) {
        // Make sure each expression in the case condition is type compatible with the switch
        // condition.
        for (PExpr pExpr : node.getExpr()) {
            GoLiteType type = this.typeTable.get(pExpr);
            if (this.currentSwitchCondType.getUnderlyingType().equals(type))
                this.throwTypeCheckException(pExpr, "Invalid case in switch (mismatched types "
                	+ type + " and " + this.currentSwitchCondType + ")");
        }
    }

    // Default case block condition.
    @Override
    public void outADefaultCaseCondition(ADefaultCaseCondition node) {
        // Trivially well-typed.
    }

    // Loop statement.
    @Override
    public void caseALoopStmt(ALoopStmt node) {
    	// Create a new scope for the loop initializer and body.
        this.symbolTable.scope();

        // Type check the initial statement, if it exists.
        PStmt pStmt = node.getInit();
        if (pStmt != null)
            pStmt.apply(this);

        // Loop condition.
    	PExpr pExpr = node.getExpr();
    	// If the condition is not empty, make sure it evaluates to a boolean.
    	if (pExpr != null) {
            pExpr.apply(this);
            GoLiteType condType = this.typeTable.get(pExpr);
            if (!(condType instanceof BoolType))
                this.throwTypeCheckException(pExpr,
                	"Non-bool (type " + condType + ") used as for condition");
        }

        // Type check the end statement, if it exists.
        pStmt = node.getEnd();
        if (pStmt != null)
            pStmt.apply(this);

        // Create a new scope for the loop body.
        this.symbolTable.scope();

        // Type check the body statements.
        for (PStmt s : node.getBlock())
            s.apply(this);

        // Exit the scope for the loop body.
        this.symbolTable.unscope();
        // Exit the scope for the loop initializer and body.
    	this.symbolTable.unscope();
    }

    // Block statement.
    @Override
    public void inABlockStmt(ABlockStmt node) {
    	// Create a new scope.
		this.symbolTable.scope();
	}

	@Override
	public void outABlockStmt(ABlockStmt node) {
		// Drop the block scope.
		this.symbolTable.unscope();
	}

    /** Type check expressions. **/

    @Override
    public void outAEmptyExpr(AEmptyExpr node) {
    	// Trivially well-typed.
    }

    /* Binary arithmetic expressions. */

    // Addition expression.
    @Override
    public void outAAddExpr(AAddExpr node) {
        // Left and right hand expressions their types.
        PExpr leftExpr = node.getLeft();
        PExpr rightExpr = node.getRight();
        GoLiteType leftExprType = this.getType(leftExpr);
        GoLiteType rightExprType = this.getType(rightExpr);

        // Make sure operands are type compatible, otherwise throw an error.
        if (!(leftExprType.getUnderlyingType().equals(rightExprType)
        	|| rightExprType.getUnderlyingType().equals(leftExprType)))
            this.throwTypeCheckException(node,
            	"Invalid operation '+': mismatched types " + leftExprType + " and "
            	+ rightExprType);

        // Make sure the underlying operand type is ordered, otherwise throw an error.
        if (!this.isOrderedType(leftExprType))
            this.throwTypeCheckException(node,
            	"Invalid operation '+': operator not defined on " + leftExprType);
        
        typeTable.put(node, leftExprType.getUnderlyingType());
    }

    // Subtraction expression.
    @Override
    public void outASubtractExpr(ASubtractExpr node) {
        // Left and right hand expressions their types.
        PExpr leftExpr = node.getLeft();
        PExpr rightExpr = node.getRight();
        GoLiteType leftExprType = this.getType(leftExpr);
        GoLiteType rightExprType = this.getType(rightExpr);

        // Make sure operands are type compatible, otherwise throw an error.
        if (!(leftExprType.getUnderlyingType().equals(rightExprType)
        	|| rightExprType.getUnderlyingType().equals(leftExprType)))
            this.throwTypeCheckException(node,
            	"Invalid operation '-': mismatched types " + leftExprType + " and "
            	+ rightExprType);

        // Make sure the underlying operand type is numeric, otherwise throw an error.
        if (!this.isNumericType(leftExprType))
            this.throwTypeCheckException(node,
            	"Invalid operation '-': operator not defined on " + leftExprType);
        
        typeTable.put(node, leftExprType.getUnderlyingType());
    }

    // Multiplication expression.
    @Override
    public void outAMultExpr(AMultExpr node) {
        // Left and right hand expressions their types.
        PExpr leftExpr = node.getLeft();
        PExpr rightExpr = node.getRight();
        GoLiteType leftExprType = this.getType(leftExpr);
        GoLiteType rightExprType = this.getType(rightExpr);

        // Make sure operands are type compatible, otherwise throw an error.
        if (!(leftExprType.getUnderlyingType().equals(rightExprType)
        	|| rightExprType.getUnderlyingType().equals(leftExprType)))
            this.throwTypeCheckException(node,
            	"Invalid operation '*': mismatched types " + leftExprType + " and "
            	+ rightExprType);

        // Make sure the underlying operand type is numeric, otherwise throw an error.
        if (!this.isNumericType(leftExprType))
            this.throwTypeCheckException(node,
            	"Invalid operation '*': operator not defined on " + leftExprType);
        
        typeTable.put(node, leftExprType.getUnderlyingType());
    }

    // Division expression.
    @Override
    public void outADivExpr(ADivExpr node) {
        // Left and right hand expressions their types.
        PExpr leftExpr = node.getLeft();
        PExpr rightExpr = node.getRight();
        GoLiteType leftExprType = this.getType(leftExpr);
        GoLiteType rightExprType = this.getType(rightExpr);

        // Make sure operands are type compatible, otherwise throw an error.
        if (!(leftExprType.getUnderlyingType().equals(rightExprType)
        	|| rightExprType.getUnderlyingType().equals(leftExprType)))
            this.throwTypeCheckException(node,
            	"Invalid operation '/': mismatched types " + leftExprType + " and "
            	+ rightExprType);

        // Make sure the underlying operand type is numeric, otherwise throw an error.
        if (!this.isNumericType(leftExprType))
            this.throwTypeCheckException(node,
            	"Invalid operation '/': operator not defined on " + leftExprType);
        
        typeTable.put(node, leftExprType.getUnderlyingType());
    }

    // Modulo expression.
    @Override
    public void outAModExpr(AModExpr node) {
        // Left and right hand expressions their types.
        PExpr leftExpr = node.getLeft();
        PExpr rightExpr = node.getRight();
        GoLiteType leftExprType = this.getType(leftExpr);
        GoLiteType rightExprType = this.getType(rightExpr);

        // Make sure operands are type compatible, otherwise throw an error.
        if (!(leftExprType.getUnderlyingType().equals(rightExprType)
        	|| rightExprType.getUnderlyingType().equals(leftExprType)))
            this.throwTypeCheckException(node,
            	"Invalid operation '%': mismatched types " + leftExprType + " and "
            	+ rightExprType);

        // Make sure the underlying operand type is integer or rune, otherwise throw an error.
        if (!this.isIntOrRuneType(leftExprType))
            this.throwTypeCheckException(node,
            	"Invalid operation '%': operator not defined on " + leftExprType);
        
        typeTable.put(node, leftExprType.getUnderlyingType());
    }

    // Bit-and expression.
    @Override
    public void outABitAndExpr(ABitAndExpr node) {
         // Left and right hand expressions their types.
        PExpr leftExpr = node.getLeft();
        PExpr rightExpr = node.getRight();
        GoLiteType leftExprType = this.getType(leftExpr);
        GoLiteType rightExprType = this.getType(rightExpr);

       	// Make sure operands are type compatible, otherwise throw an error.
	    if (!(leftExprType.getUnderlyingType().equals(rightExprType)
	    	|| rightExprType.getUnderlyingType().equals(leftExprType)))
	        this.throwTypeCheckException(node,
	        	"Invalid operation '&': mismatched types " + leftExprType + " and "
	        	+ rightExprType);

	    // Make sure the underlying operand type is integer or rune, otherwise throw an error.
	    if (!this.isIntOrRuneType(leftExprType))
	        this.throwTypeCheckException(node,
	        	"Invalid operation '&': operator not defined on " + leftExprType);
	    
	    typeTable.put(node, leftExprType.getUnderlyingType());
    }

    // Bit-or expression.
    @Override
    public void outABitOrExpr(ABitOrExpr node) {
         // Left and right hand expressions their types.
        PExpr leftExpr = node.getLeft();
        PExpr rightExpr = node.getRight();
        GoLiteType leftExprType = this.getType(leftExpr);
        GoLiteType rightExprType = this.getType(rightExpr);

       	// Make sure operands are type compatible, otherwise throw an error.
	    if (!(leftExprType.getUnderlyingType().equals(rightExprType)
	    	|| rightExprType.getUnderlyingType().equals(leftExprType)))
	        this.throwTypeCheckException(node,
	        	"Invalid operation '|': mismatched types " + leftExprType + " and "
	        	+ rightExprType);

	    // Make sure the underlying operand type is integer or rune, otherwise throw an error.
	    if (!this.isIntOrRuneType(leftExprType))
	        this.throwTypeCheckException(node,
	        	"Invalid operation '|': operator not defined on " + leftExprType);
	    
	    typeTable.put(node, leftExprType.getUnderlyingType());
    }

    // Bit-xor expression.
    @Override
    public void outABitXorExpr(ABitXorExpr node) {
         // Left and right hand expressions their types.
        PExpr leftExpr = node.getLeft();
        PExpr rightExpr = node.getRight();
        GoLiteType leftExprType = this.getType(leftExpr);
        GoLiteType rightExprType = this.getType(rightExpr);

       	// Make sure operands are type compatible, otherwise throw an error.
	    if (!(leftExprType.getUnderlyingType().equals(rightExprType)
	    	|| rightExprType.getUnderlyingType().equals(leftExprType)))
	        this.throwTypeCheckException(node,
	        	"Invalid operation '^': mismatched types " + leftExprType + " and "
	        	+ rightExprType);

	    // Make sure the underlying operand type is integer or rune, otherwise throw an error.
	    if (!this.isIntOrRuneType(leftExprType))
	        this.throwTypeCheckException(node,
	        	"Invalid operation '^': operator not defined on " + leftExprType);
	    
	    typeTable.put(node, leftExprType.getUnderlyingType());
    }

    // Bit-clear expression.
    @Override
    public void outABitClearExpr(ABitClearExpr node) {
         // Left and right hand expressions their types.
        PExpr leftExpr = node.getLeft();
        PExpr rightExpr = node.getRight();
        GoLiteType leftExprType = this.getType(leftExpr);
        GoLiteType rightExprType = this.getType(rightExpr);

       	// Make sure operands are type compatible, otherwise throw an error.
	    if (!(leftExprType.getUnderlyingType().equals(rightExprType)
	    	|| rightExprType.getUnderlyingType().equals(leftExprType)))
	        this.throwTypeCheckException(node,
	        	"Invalid operation '&^': mismatched types " + leftExprType + " and "
	        	+ rightExprType);

	    // Make sure the underlying operand type is integer or rune, otherwise throw an error.
	    if (!this.isIntOrRuneType(leftExprType))
	        this.throwTypeCheckException(node,
	        	"Invalid operation '&^': operator not defined on " + leftExprType);
	    
	    typeTable.put(node, leftExprType.getUnderlyingType());
    }

    // Bit-left-shift expression.
    @Override
    public void outABitLshiftExpr(ABitLshiftExpr node) {
         // Left and right hand expressions their types.
        PExpr leftExpr = node.getLeft();
        PExpr rightExpr = node.getRight();
        GoLiteType leftExprType = this.getType(leftExpr);
        GoLiteType rightExprType = this.getType(rightExpr);

       	// Make sure operands are type compatible, otherwise throw an error.
	    if (!(leftExprType.getUnderlyingType().equals(rightExprType)
	    	|| rightExprType.getUnderlyingType().equals(leftExprType)))
	        this.throwTypeCheckException(node,
	        	"Invalid operation '<<': mismatched types " + leftExprType + " and "
	        	+ rightExprType);

	    // Make sure the underlying operand type is integer or rune, otherwise throw an error.
	    if (!this.isIntOrRuneType(leftExprType))
	        this.throwTypeCheckException(node,
	        	"Invalid operation '<<': operator not defined on " + leftExprType);
	    
	    typeTable.put(node, leftExprType.getUnderlyingType());
    }

    // Bit-right-shift expression.
    @Override
    public void outABitRshiftExpr(ABitRshiftExpr node) {
         // Left and right hand expressions their types.
        PExpr leftExpr = node.getLeft();
        PExpr rightExpr = node.getRight();
        GoLiteType leftExprType = this.getType(leftExpr);
        GoLiteType rightExprType = this.getType(rightExpr);

       	// Make sure operands are type compatible, otherwise throw an error.
	    if (!(leftExprType.getUnderlyingType().equals(rightExprType)
	    	|| rightExprType.getUnderlyingType().equals(leftExprType)))
	        this.throwTypeCheckException(node,
	        	"Invalid operation '>>': mismatched types " + leftExprType + " and "
	        	+ rightExprType);

	    // Make sure the underlying operand type is integer or rune, otherwise throw an error.
	    if (!this.isIntOrRuneType(leftExprType))
	        this.throwTypeCheckException(node,
	        	"Invalid operation '>>': operator not defined on " + leftExprType);
	    
	    typeTable.put(node, leftExprType.getUnderlyingType());
    }

    /* Unary expressions. */

    // Plus unary expression.
    @Override
    public void outAPosExpr(APosExpr node) {
    	PExpr pExpr = node.getExpr();
    	// Expression type.
        GoLiteType type = this.getType(pExpr);

        // Throw an error if the expression is not numeric.
        if (!this.isNumericType(type))
            this.throwTypeCheckException(pExpr,
            	"Invalid oepration '+': undefined for type " + type);

        this.typeTable.put(node, type);
    }

    // Minus unary expression.
    @Override
    public void outANegExpr(ANegExpr node) {
    	PExpr pExpr = node.getExpr();
    	// Expression type.
        GoLiteType type = this.getType(pExpr);

        // Throw an error if the expression is not numeric.
        if (!this.isNumericType(type))
            this.throwTypeCheckException(pExpr,
            	"Invalid oepration '-': undefined for type " + type);

        this.typeTable.put(node, type);
    }

    // Bit complement unary expression.
    @Override
    public void outABitCompExpr(ABitCompExpr node) {
    	PExpr pExpr = node.getExpr();
    	// Expression type.
        GoLiteType type = this.getType(pExpr);

        // Throw an error if the expression is not integer or rune.
        if (!this.isIntOrRuneType(type))
            this.throwTypeCheckException(pExpr,
            	"Invalid oepration '^': undefined for type " + type);

        this.typeTable.put(node, type);
    }

    // Not unary expression.
    @Override
    public void outANotExpr(ANotExpr node) {
    	PExpr pExpr = node.getExpr();
    	// Expression type.
        GoLiteType type = this.getType(pExpr);

        // Throw an error if the expression is not boolean.
        if (!(type instanceof BoolType))
            this.throwTypeCheckException(pExpr,
            	"Invalid oepration '!': undefined for type " + type);

        this.typeTable.put(node, type);
    }

    /* Type check comparison expressions. */

    // '==' expression.
    @Override
    public void outAEqExpr(AEqExpr node) {
        // Left and right hand expression types.
        GoLiteType leftExprType = this.getType(node.getLeft());
        GoLiteType rightExprType = this.getType(node.getRight());

        // Make sure operands have the same surface type, otherwise throw an error.
        if (!leftExprType.equals(rightExprType))
            this.throwTypeCheckException(node.getLeft(),
            	"Invalid operation '==': (mismatched types " + leftExprType
            		+ " and " + rightExprType + ")");

        // Make sure the underlying operand type is comparable, otherwise throw an error.
        if (!isComparableType(leftExprType))
            this.throwTypeCheckException(node,
            	"Invalid operation '==': undefined for type " + leftExprType);

        this.typeTable.put(node, new BoolType());
    }

    // '!=' expression.
    @Override
    public void outANeqExpr(ANeqExpr node) {
        // Left and right hand expression types.
        GoLiteType leftExprType = this.getType(node.getLeft());
        GoLiteType rightExprType = this.getType(node.getRight());

        // Make sure operands have the same surface type, otherwise throw an error.
        if (!leftExprType.equals(rightExprType))
            this.throwTypeCheckException(node.getLeft(),
            	"Invalid operation '!=': (mismatched types " + leftExprType
            		+ " and " + rightExprType + ")");

        // Make sure the underlying operand type is comparable, otherwise throw an error.
        if (!isComparableType(leftExprType))
            this.throwTypeCheckException(node,
            	"Invalid operation '!=': undefined for type " + leftExprType);

        this.typeTable.put(node, new BoolType());
    }

    // "<" expression.
    @Override
    public void outALtExpr(ALtExpr node) {
    	// Left and right hand expression types.
        GoLiteType leftExprType = this.getType(node.getLeft());
        GoLiteType rightExprType = this.getType(node.getRight());

        // Make sure operands have the same surface type, otherwise throw an error.
        if (!leftExprType.equals(rightExprType))
            this.throwTypeCheckException(node.getLeft(),
            	"Invalid operation '<': (mismatched types " + leftExprType
            		+ " and " + rightExprType + ")");

        // Make sure the underlying operand type is ordered, otherwise throw an error.
        if (!this.isOrderedType(leftExprType.getUnderlyingType()))
            this.throwTypeCheckException(node,
            	"Invalid operation '<': undefined for type " + leftExprType);
 
        this.typeTable.put(node, new BoolType());
    }

    // "<=" expression.
    @Override
    public void outALteExpr(ALteExpr node) {
    	// Left and right hand expression types.
        GoLiteType leftExprType = this.getType(node.getLeft());
        GoLiteType rightExprType = this.getType(node.getRight());

        // Make sure operands have the same surface type, otherwise throw an error.
        if (!leftExprType.equals(rightExprType))
            this.throwTypeCheckException(node.getLeft(),
            	"Invalid operation '<=': (mismatched types " + leftExprType
            		+ " and " + rightExprType + ")");

        // Make sure the underlying operand type is ordered, otherwise throw an error.
        if (!this.isOrderedType(leftExprType.getUnderlyingType()))
            this.throwTypeCheckException(node,
            	"Invalid operation '<=': undefined for type " + leftExprType);
 
        this.typeTable.put(node, new BoolType());
    }

    // ">" expression.
    @Override
    public void outAGtExpr(AGtExpr node) {
    	// Left and right hand expression types.
        GoLiteType leftExprType = this.getType(node.getLeft());
        GoLiteType rightExprType = this.getType(node.getRight());

        // Make sure operands have the same surface type, otherwise throw an error.
        if (!leftExprType.equals(rightExprType))
            this.throwTypeCheckException(node.getLeft(),
            	"Invalid operation '>': (mismatched types " + leftExprType
            		+ " and " + rightExprType + ")");

        // Make sure the underlying operand type is ordered, otherwise throw an error.
        if (!this.isOrderedType(leftExprType.getUnderlyingType()))
            this.throwTypeCheckException(node,
            	"Invalid operation '>': undefined for type " + leftExprType);
 
        this.typeTable.put(node, new BoolType());
    }

    // ">=" expression.
    @Override
    public void outAGteExpr(AGteExpr node) {
    	// Left and right hand expression types.
        GoLiteType leftExprType = this.getType(node.getLeft());
        GoLiteType rightExprType = this.getType(node.getRight());

        // Make sure operands have the same surface type, otherwise throw an error.
        if (!leftExprType.equals(rightExprType))
            this.throwTypeCheckException(node.getLeft(),
            	"Invalid operation '>=': (mismatched types " + leftExprType
            		+ " and " + rightExprType + ")");

        // Make sure the underlying operand type is ordered, otherwise throw an error.
        if (!this.isOrderedType(leftExprType.getUnderlyingType()))
            this.throwTypeCheckException(node,
            	"Invalid operation '>=': undefined for type " + leftExprType);
 
        this.typeTable.put(node, new BoolType());
    }

    /* Logical expressions. */

    // And expression.
    @Override
    public void outAAndExpr(AAndExpr node) {
        // Left and right hand expressions their types.
        PExpr leftExpr = node.getLeft();
        PExpr rightExpr = node.getRight();
        GoLiteType leftExprType = this.getType(leftExpr);
        GoLiteType rightExprType = this.getType(rightExpr);

        if (!(leftExprType instanceof BoolType))
        	this.throwTypeCheckException(node,
        		"Invalid operation '&&': undefined for type "+ leftExprType);

        if (!(rightExprType instanceof BoolType))
            this.throwTypeCheckException(node,
            	"Invalid operation '&&': undefined for type " + rightExprType);

        this.typeTable.put(node, new BoolType());
    }

    // Or expression.
    @Override
    public void outAOrExpr(AOrExpr node) {
        // Left and right hand expressions their types.
        PExpr leftExpr = node.getLeft();
        PExpr rightExpr = node.getRight();
        GoLiteType leftExprType = this.getType(leftExpr);
        GoLiteType rightExprType = this.getType(rightExpr);

        if (!(leftExprType instanceof BoolType))
        	this.throwTypeCheckException(node,
        		"Invalid operation '||': undefined for type " + leftExprType);

        if (!(rightExprType instanceof BoolType))
            this.throwTypeCheckException(node, 
            	"Invalid operation '||': undefined for type " + rightExprType);

        this.typeTable.put(node, new BoolType());
    }

    // Function call.
    @Override
    public void outAFuncCallExpr(AFuncCallExpr node) {
        TId id = node.getId();
        String name = id.getText();

        Symbol symbol = this.symbolTable.getSymbol(name);

        // If no corresponding symbol exists, throw an error.
        if (symbol == null)
        	this.throwTypeCheckException(id, "Undefined: " + name);

		// Passed argument expressions.
        LinkedList<PExpr> pExprs = node.getExpr();
        int numExprs = pExprs.size();        	

        // Symbol is a function.
        if (symbol instanceof FunctionSymbol) {
	        FunctionSymbol funcSymbol = (FunctionSymbol) symbol;
	        // Argument types.
			ArrayList<GoLiteType> argTypes = funcSymbol.getArgTypes();
			int numArgTypes = argTypes.size();

	        // Too many arguments are passed, so throw an error.
	        if (numArgTypes < numExprs)
	        	this.throwTypeCheckException(node, "Too many arguments in call to " + name);

	        // Too few arguments are passed, so throw an error.
	        if (numArgTypes > numExprs)
	        	this.throwTypeCheckException(node, "Not enough arguments in call to " + name);

	        // Check type compatibility of the declared argument types and passed argument types.
	        for (int i = 0; i < numExprs; i++) {
	        	GoLiteType argType = argTypes.get(i);
	        	GoLiteType exprType = this.getType(pExprs.get(i));

	        	if (!argType.getUnderlyingType().equals(exprType))
	        		this.throwTypeCheckException(node, "Cannot use type " + exprType + " as type "
	        			+ argType + " in argument to " + name);
	        }

	        this.typeTable.put(node, funcSymbol.getReturnType());
	    // Symbol is a type alias for casting.
    	} else if (symbol instanceof TypeAliasSymbol) {
    		// Must have at least one argument expression, otherwise throw an error.
    		if (numExprs < 1)
    			this.throwTypeCheckException(node, "Missing argument to conversion to " + name);
    		if (numExprs > 1)
    			this.throwTypeCheckException(node, "Too many arguments to conversion to " + name);

    		TypeAliasSymbol typeAliasSymbol = ((TypeAliasSymbol) symbol);

    		// Underlying type must be primitive (except string, which is already weeded out).
    		GoLiteType underlyingType = typeAliasSymbol.getUnderlyingType();

    		if (!(underlyingType instanceof PrimitiveGoLiteType))
    			this.throwTypeCheckException(id,
    				"Type alias " + name + " must map to a non-string primitive type");

    		// Expression must have type that is a non-string primitive.
    		GoLiteType argType = this.getType(pExprs.get(0));
    		if (!(argType instanceof PrimitiveGoLiteType) || argType instanceof StringType)
    			this.throwTypeCheckException(id, "Arugment to conversion to " + name
    				+ " must map to a non-string primitive type");

    		this.typeTable.put(node, typeAliasSymbol.getAliasType());
    	// If the symbol is not a function or a type alias, throw an error.
    	} else
    		this.throwTypeCheckException(id,
        		"Cannot call non-function " + name + " (type " + symbol.getType() + ")");
    }

    // Array access.
    @Override
    public void outAArrayElemExpr(AArrayElemExpr node) {
    	// Array expression and type.
    	PExpr arrayExpr = node.getArray();
    	GoLiteType arrayExprType = this.getType(arrayExpr);
    	// Index expression and type.
    	PExpr indexExpr = node.getIndex();
		GoLiteType indexExprType = this.getType(indexExpr);

		// Make sure index is of type integer, otherwise throw an error.
		// TODO: Remove this check from the weeder.
		if (!(indexExprType instanceof IntType))
			this.throwTypeCheckException(indexExpr, "Non-integer array bound");

		// Make sure the array expression evaluates to an array or slice, otherwise throw an error.
		if (arrayExprType instanceof ArrayType)
        	this.typeTable.put(node, ((ArrayType) arrayExprType).getType().getUnderlyingType());
        else if (arrayExprType instanceof SliceType)
        	this.typeTable.put(node, ((SliceType) arrayExprType).getType().getUnderlyingType());
        else
        	this.throwTypeCheckException(arrayExpr, "Invalid operation '[]': type " + arrayExprType
				+ " does not support indexing");
    }

    // Enter a variable expression into the type table.
    @Override
    public void outAVariableExpr(AVariableExpr node) {
    	VariableSymbol symbol = this.getVariableSymbol(node.getId());
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
