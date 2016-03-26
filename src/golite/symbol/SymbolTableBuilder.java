package golite.symbol;

import golite.exception.SymbolTableException;
import golite.exception.TypeException;
import golite.type.AliasType;
import golite.type.ArrayType;
import golite.type.BoolType;
import golite.type.FloatType;
import golite.type.GoLiteType;
import golite.type.IntType;
import golite.type.RuneType;
import golite.type.SliceType;
import golite.type.StringType;
import golite.type.StructType;
import golite.util.LineAndPosTracker;
import golite.analysis.*;
import golite.node.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;


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
         * Returns the type for the given type expression.
         *
         * @param node - Type expression AST node
         * @return Corresponding type
         * @throws SymbolTableException
         */
        public GoLiteType getType(PTypeExpr node) {
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
            else if (node instanceof AAliasTypeExpr)
                return new UnTypedAliasType(((AAliasTypeExpr) node).getId().getText());
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
                    this.throwSymbolTableException(pExpr, "Non-integer array bound");

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
                                this.throwSymbolTableException(id, "Duplicate field " + id.getText());

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
         * Checks if the given Id has already been defined in the current scope.
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
                        // The type has been specified, in which case add a variable symbol to the
                        // symbol table. Otherwise, skip over the declaration and let the type
                        // checker perform inference and a corresponding variable symbol to the
                        // symbol table.
                        if (pTypeExpr != null)
                            this.table.putSymbol(new VariableSymbol(id.getText(),
                                this.getType(pTypeExpr), pVarSpec));
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
                        this.getType(pTypeExpr), pTypeSpec));
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
                funcSymbol = new FunctionSymbol(id.getText(), node);
            // Has return type.
            else
                funcSymbol = new FunctionSymbol(id.getText(), this.getType(pTypeExpr), node);

            // Add argument types to the function symbol.
            AArgArgGroup g = null;
            for (PArgGroup p : node.getArgGroup()) {
                g = (AArgArgGroup) p;
                funcSymbol.addArgType(this.getType(g.getTypeExpr()), g.getId().size());
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
	
    // Intialize the 0th scope.
	@Override
    public void inStart(Start node) {
    	// Gather all line and position information.
        node.apply(this.lineAndPosTracker);

         // Enter the 0th scope.
        this.table = new SymbolTable();
        this.table.scope();

        // Initialize boolean literals.
        Symbol trueSymbol = new VariableSymbol("true", new BoolType(), node);
        Symbol falseSymbol = new VariableSymbol("false", new BoolType(), node);
        this.table.putSymbol(trueSymbol);
        this.table.putSymbol(falseSymbol);
    }

    // Initialize the global scope.
    @Override
    public void inAProgProg(AProgProg node) {
        // Enter the global scope.
        this.table.scope();

        // Make the first pass.
        FirstPasser firstPasser = new FirstPasser(this.table, this.lineAndPosTracker);
        node.apply(firstPasser);
    }

    @Override
    public void outAProgProg(AProgProg node) {
        // Resolve all global symbols that are typed with an alias by passing over all the symbols
        // in the global scope.
        for (Symbol symbol : this.table.getSymbolsFromCurrentScope()) {
            try {
                // For variable and type alias symbols, resolve to the underlying type.
                if (symbol instanceof VariableSymbol || symbol instanceof TypeAliasSymbol)
                    symbol.setType(this.getResolvedType(symbol.getType(),
                        new Stack<String>()));
                // For functions symbols, resolve argument and return types to their corresponding
                // underlying types.
                else if (symbol instanceof FunctionSymbol) {
                    // Resolve the return type.
                    symbol.setType(this.getResolvedType(symbol.getType(),
                        new Stack<String>()));

                    // Get resolved versions of the argument types.
                    ArrayList<GoLiteType> resolvedArgTypes = new ArrayList<GoLiteType>();
                    for (GoLiteType argType : ((FunctionSymbol) symbol).getArgTypes())
                        resolvedArgTypes.add(this.getResolvedType(argType, new Stack<String>()));
                    
                    // Set the argument types to the resolved versions.
                    ((FunctionSymbol) symbol).setArgTypes(resolvedArgTypes);
                }
            } catch (SymbolTableException e) {
                throwSymbolTableException(symbol.getNode(), e.getMessage());
            }
        }
    }

    /**
     * Recursively resolves the given type to its most underlying type.
     *
     * @param type - Symbol type
     * @param aliases - Stack of aliases for checking recursive type definitions.
     * @return Resolved underlying type
     */
    private GoLiteType getResolvedType(GoLiteType type, Stack<String> aliases) {
        // Alias.
        if (type instanceof AliasType) {
            String alias = ((AliasType) type).getAlias();
            GoLiteType aliasedType = ((AliasType) type).getType();

            // Alias has already been encountered in the recursion and hence, throw an error.
            if (aliases.search(alias) != -1)
                throw new SymbolTableException("Invalid recursive type " + alias);

            // Get the resolved type for the aliased type, which could be a type alias.
            aliases.push(alias);
            AliasType aliasType = new AliasType(alias,
                this.getResolvedType(aliasedType, aliases));
            aliases.pop();

            return aliasType;
        // Array.
        } else if (type instanceof ArrayType) {
            int bound = ((ArrayType) type).getBound();
            GoLiteType arrayType = ((ArrayType) type).getType();

            // Get the resolved type for the element type, which could include a type alias.
            return new ArrayType(this.getResolvedType(arrayType, aliases), bound);
        // Slice.
        } else if (type instanceof SliceType) {
            GoLiteType sliceType = ((SliceType) type).getType();

            // Get the resolved type for the element type, which could include a type alias.
            return new SliceType(this.getResolvedType(sliceType, aliases));
        // Struct.
        } else if (type instanceof StructType) {
            StructType resolvedStructType = new StructType();

            // Get the resolved type for each field type.
            Iterator<StructType.Field> fieldIter = ((StructType) type).getFieldIterator();
            while (fieldIter.hasNext()) {
                StructType.Field field = fieldIter.next();
                resolvedStructType.addField(field.getId(),
                    this.getResolvedType(field.getType(), aliases));
            }

            return resolvedStructType;   
        // Untyped alias.             
        } else if (type instanceof UnTypedAliasType) {
            String alias = ((UnTypedAliasType) type).getAlias();

            // Alias has already been encountered in the recursion and hence, throw an error.
            if (aliases.search(alias) != -1)
                throw new SymbolTableException("Invalid recursive type " + alias);

            // Find the corresponding type alias symbol.
            Symbol typeAliasSymbol = this.table.getSymbol(alias);

            // If the type alias symbol cannot be found, i.e. is undefined, then throw an exception.
            if (typeAliasSymbol == null)
                throw new SymbolTableException("Undefined: " + alias);

            // Return a typed alias type with the aliased type fully resolved.
            aliases.push(alias);
            AliasType aliasType = new AliasType(alias,
                this.getResolvedType(typeAliasSymbol.getType(), aliases));
            aliases.pop();

            return aliasType;
        }

        // For base types.
        return type;
    }

}
