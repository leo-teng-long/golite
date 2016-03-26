package golite.symbol;

import golite.exception.SymbolTableException;
import golite.util.LineAndPosTracker;
import golite.analysis.*;
import golite.node.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;


/**
 * Builds the 0th and global scope of the symbol table for a GoLite program.
 */
public class SymbolTableBuilder extends DepthFirstAdapter {

	/** Symbol table. */
	private SymbolTable table;

    /** Line and position tracker for AST nodes. */
    private LineAndPosTracker lineAndPosTracker = new LineAndPosTracker();

    /** Makes the first pass over the program to initialize the table with top-level declarations. */
    private static class FirstPasser extends DepthFirstAdapter {
        
        /** Symbol table. */
        private SymbolTable table;

        /** Line and position tracker for AST nodes. */
        private LineAndPosTracker lineAndPosTracker;

        /**
         * Constructor.
         *
         * @param table - Symbol table
         * @param lineAndPosTracker - Initialized line and position tracker
         */
        public FirstPasser(SymbolTable table, LineAndPosTracker lineAndPosTracker) {
            this.table = table;
            this.lineAndPosTracker = lineAndPosTracker;
        }

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
            else if (node instanceof AAliasTypeExpr)
                return new UnTypedAliasSymbolType(((AAliasTypeExpr) node).getId().getText());
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
            } else if (node instanceof ASliceTypeExpr)
                return new SliceSymbolType(this.getSymbolType(((ASliceTypeExpr) node).getTypeExpr()));
            else if (node instanceof AStructTypeExpr) {
                StructSymbolType structSymbolType = new StructSymbolType();

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
                                throwSymbolTableException(id, "Duplicate field " + id.getText());

                            structSymbolType.addField(id.getText(),
                                this.getSymbolType(((ASpecFieldSpec) pFieldSpec).getTypeExpr()));
                            fieldIds.add(id.getText());
                        }
                    }
                }

                return structSymbolType;
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

        // Add top variable declarations into the symbol table.
        @Override
        public void caseAVarsTopDec(AVarsTopDec node) {
            // Loop over the variable specifications.
            for(PVarSpec pVarSpec : node.getVarSpec()) {
                // Get the optional Id's.
                LinkedList<POptId> pOptIds = ((ASpecVarSpec) pVarSpec).getOptId();

                // Loop over each Id.
                for(POptId pOptId : pOptIds) {
                    // Do not consider blank Id's.
                    if (pOptId instanceof AIdOptId) {
                        TId id = ((AIdOptId) pOptId).getId();

                        // Throw an error if the name is already taken by another identifier in the
                        // global scope.
                        this.checkifDeclaredInCurrentScope(id);

                        PTypeExpr pTypeExpr = ((ASpecVarSpec) pVarSpec).getTypeExpr();
                        // The type has not been specified.
                        if (pTypeExpr == null) {
                            // A variable symbol is added to the symbol table with a placeholder
                            // indicating the type must be inferred.
                            this.table.putSymbol(new VariableSymbol(id.getText(),
                                new ToBeInferredSymbolType(), node));
                        } else
                            // Add a variable symbol to the symbol table.
                            this.table.putSymbol(new VariableSymbol(id.getText(),
                                this.getSymbolType(pTypeExpr), node));
                    }
                }
            }
        }

        // Add a top-level type variables into the symbol table.
        @Override
        public void caseATypesTopDec(ATypesTopDec node) {
            // Loop over the type specifications.
            for(PTypeSpec pTypeSpec : node.getTypeSpec()) {
                // Get the optional Id.
                POptId pOptId = ((ASpecTypeSpec) pTypeSpec).getOptId();

                // Do not consider a blank Id.
                if (pOptId instanceof AIdOptId) {
                    TId id = ((AIdOptId) pOptId).getId();

                    // Throw an error if the name is already taken by another identifier in the
                    // global scope.
                    this.checkifDeclaredInCurrentScope(id);

                    PTypeExpr pTypeExpr = ((ASpecTypeSpec) pTypeSpec).getTypeExpr();
                    // Add a type alias symbol to the symbol table.
                    this.table.putSymbol(new TypeAliasSymbol(id.getText(),
                        this.getSymbolType(pTypeExpr), node));
                }
            }
        }

        // Add top-level function declarations into the symbol table.
        @Override
        public void caseAFuncTopDec(AFuncTopDec node) {
            // Function name.
            TId id = node.getId();

            // Throw an error if the name is already taken by another identifier in the global
            // scope.
            this.checkifDeclaredInCurrentScope(id);

            // Return type expression.
            PTypeExpr pTypeExpr = node.getTypeExpr();

            FunctionSymbol funcSymbol = null;
            // No return type.
            if (pTypeExpr == null)
                funcSymbol = new FunctionSymbol(id.getText(), null, node);
            // Has return type.
            else
                funcSymbol = new FunctionSymbol(id.getText(), this.getSymbolType(pTypeExpr), node);

            // Add argument types to the function symbol.
            AArgArgGroup g = null;
            for (PArgGroup p : node.getArgGroup()) {
                g = (AArgArgGroup) p;
                funcSymbol.addArgType(this.getSymbolType(g.getTypeExpr()), g.getId().size());
            }

            // Enter symbol into the table.
            this.table.putSymbol(funcSymbol);
        }

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
     * Getter.
     */
    public SymbolTable getTable() {
        return this.table;
    }
	
	@Override
    public void inStart(Start node) {
    	// Gather all line and position information.
        node.apply(this.lineAndPosTracker);

         // Enter the 0-th scope.
        this.table = new SymbolTable();
        this.table.scope();

        // Initialize boolean literals.
        Symbol trueSymbol = new VariableSymbol("true", new BoolSymbolType(), node);
        Symbol falseSymbol = new VariableSymbol("false", new BoolSymbolType(), node);
        this.table.putSymbol(trueSymbol);
        this.table.putSymbol(falseSymbol);

        // Enter the global scope.
        this.table.scope();

        // Make the first pass.
        FirstPasser firstPasser = new FirstPasser(this.table, this.lineAndPosTracker);
        node.apply(firstPasser);

        // Resolve all global symbols that are typed with an alias by passing over all the symbols
        // in the global scope.
        for (Symbol symbol : this.table.getSymbolsFromCurrentScope()) {
            try {
                // For variable and type alias symbols, resolve to the underlying type.
                if (symbol instanceof VariableSymbol || symbol instanceof TypeAliasSymbol)
                    symbol.setType(this.getResolvedSymbolType(symbol.getType()));
                // For functions symbols, resolve argument and return types to their corresponding
                // underlying types.
                else if (symbol instanceof FunctionSymbol) {
                    // Resolve the return type.
                    symbol.setType(this.getResolvedSymbolType(symbol.getType()));

                    // Get resolved versions of the argument types.
                    ArrayList<SymbolType> resolvedArgTypes = new ArrayList<SymbolType>();
                    for (SymbolType argType : ((FunctionSymbol) symbol).getArgTypes())
                        resolvedArgTypes.add(this.getResolvedSymbolType(argType));
                    
                    // Set the argument types to the resolved versions.
                    ((FunctionSymbol) symbol).setArgTypes(resolvedArgTypes);
                }
            } catch (SymbolTableException e) {
                throwSymbolTableException(symbol.getNode(), e.getMessage());
            }
        }
            
        System.out.println(this.table);
    }

    /**
     * Recursively resolves the given type to its most underlying type.
     *
     * @param type - Symbol type
     * @return Resolved underlying type
     */
    private SymbolType getResolvedSymbolType(SymbolType type) {
        // Alias.
        if (type instanceof AliasSymbolType) {
            String alias = ((AliasSymbolType) type).getAlias();
            SymbolType aliasedType = ((AliasSymbolType) type).getType();

            // Get the resolved type for the aliased type, which could be a type alias.
            return new AliasSymbolType(alias, this.getResolvedSymbolType(aliasedType));
        // Array.
        } else if (type instanceof ArraySymbolType) {
            int bound = ((ArraySymbolType) type).getBound();
            SymbolType arrayType = ((ArraySymbolType) type).getType();

            // Get the resolved type for the element type, which could include a type alias.
            return new ArraySymbolType(this.getResolvedSymbolType(arrayType), bound);
        // Slice.
        } else if (type instanceof SliceSymbolType) {
            SymbolType sliceType = ((SliceSymbolType) type).getType();

            // Get the resolved type for the element type, which could include a type alias.
            return new SliceSymbolType(this.getResolvedSymbolType(sliceType));
        // Struct.
        } else if (type instanceof StructSymbolType) {
            StructSymbolType resolvedStructSymbolType = new StructSymbolType();

            // Get the resolved type for each field type.
            Iterator<StructSymbolType.Field> fieldIter = ((StructSymbolType) type).getFieldIterator();
            while (fieldIter.hasNext()) {
                StructSymbolType.Field field = fieldIter.next();
                resolvedStructSymbolType.addField(field.getId(),
                    this.getResolvedSymbolType(field.getType()));
            }

            return resolvedStructSymbolType;   
        // Untyped alias.             
        } else if (type instanceof UnTypedAliasSymbolType) {
            String alias = ((UnTypedAliasSymbolType) type).getAlias();
            // Find the corresponding type alias symbol.
            Symbol typeAliasSymbol = this.table.getSymbol(alias);

            // If the type alias symbol cannot be found, i.e. is undefined, then throw an exception.
            if (typeAliasSymbol == null)
                throw new SymbolTableException("Undefined: " + alias);

            // Return a typed alias type with the aliased type fully resolved.
            return new AliasSymbolType(alias, this.getResolvedSymbolType(typeAliasSymbol.getType()));
        }

        // For base types.
        return type;
    }

    // Unscope the global and 0-th scope upon program exit.
    @Override
    public void outStart(Start node) {
        this.table.unscope();
        this.table.unscope();
    }

}
