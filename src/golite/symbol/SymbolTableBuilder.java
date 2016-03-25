package golite.symbol;

import golite.exception.SymbolTableException;
import golite.util.LineAndPosTracker;
import golite.analysis.*;
import golite.node.*;

import java.util.HashSet;
import java.util.LinkedList;


/**
 * Builds the global scope of the symbol table for a GoLite program.
 */
public class SymbolTableBuilder extends DepthFirstAdapter {

	/** Symbol table. */
	private SymbolTable table;

    /** Line and position tracker for AST nodes. */
    private LineAndPosTracker lineAndPosTracker = new LineAndPosTracker();


    /** Creates a symbol table and makes the first pass over the program to initialize the table
      * with top-level declarations. */
    private static class FirstPasser extends DepthFirstAdapter {
        
        /** Symbol table. */
        private SymbolTable table;

        /** Line and position tracker for AST nodes. */
        private LineAndPosTracker lineAndPosTracker;

        /**
         * Constructor.
         *
         * @param lineAndPosTracker - Initialized line and position tracker.
         */
        public FirstPasser(LineAndPosTracker lineAndPosTracker) {
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

        // Create an empty symbol table and initialize the 0th scope.
        @Override
        public void inStart(Start node) {
            // Gather all line and position information.
            node.apply(this.lineAndPosTracker);

            // Enter the 0-th scope and never leave it.
            this.table = new SymbolTable();
            this.table.scope();

            // Initialize boolean literals.
            Symbol trueSymbol = new VariableSymbol("true", new BoolSymbolType());
            Symbol falseSymbol = new VariableSymbol("false", new BoolSymbolType());
            this.table.putSymbol(trueSymbol);
            this.table.putSymbol(falseSymbol);
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
                                new ToBeInferredSymbolType()));
                        } else
                            // Add a variable symbol to the symbol table.
                            this.table.putSymbol(new VariableSymbol(id.getText(),
                                this.getSymbolType(pTypeExpr)));
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
                        this.getSymbolType(pTypeExpr)));
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
        }

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

        // Make the first pass.
        FirstPasser firstPasser = new FirstPasser(this.lineAndPosTracker);
        node.apply(firstPasser);

        this.table = firstPasser.getTable();
    }

}
