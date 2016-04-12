package golite.symbol;

import golite.exception.SymbolTableException;
import golite.type.GoLiteType;

import java.lang.StringBuilder;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;


/**
 * Symbol table.
 */
public class SymbolTable {

	/** Scopes are stored in a deque, with each scope represented as a hash map from identifier to
	  * symbol. */
	private Deque<LinkedHashMap<String, Symbol>> scopes;
	/** Logs the actions performed on the symbol table. */
	private StringBuilder logger;

	/**
	 * Constructor.
	 */
	public SymbolTable() {
		this.scopes = new ArrayDeque<LinkedHashMap<String, Symbol>>();
		this.logger = new StringBuilder();

		this.log("(KIND\tNAME\tTYPE)");
		this.log("");
	}

	// Adds the given string to the internal logger.
	private void log(String s) {
        this.logger.append(s);
        this.logger.append(System.getProperty("line.separator"));
    }

    /**
     * Enter a scope, pushing it onto the stack.
     */
	public void scope() {
		this.scopes.push(new LinkedHashMap<String, Symbol>());
		this.log("ENTER SCOPE");
	}

	/**
     * Exit a scope, popping it off the stack.
     */
	public void unscope() {
		this.scopes.pop();
		this.log("EXIT SCOPE");
	}

	/**
	 * Checks if the current scope is the global scope.
	 *
	 * @return True if the current scope is the global scope, false otherwise
	 */
	public boolean inGlobalScope() {
		return this.scopes.size() == 2;
	}

	/**
	 * Return the (most-inner) scope depth of the symbol with the given name.
	 *
	 * @param name - Name
	 * @return Scope depth of symbol
	 * @throws SymbolTableException if no symbol with the given name exists.
	 */
	public int getScopeDepth(String name) {
		int i = this.scopes.size() - 1;
		for (LinkedHashMap<String, Symbol> scope : this.scopes) {
			if (scope.get(name) != null)
				return i;

			i--;
		}

		throw new SymbolTableException("ERROR: " + name + " not in Symbol table");
	}

	/**
     * Get the symbol with the given name.
     *
     * @param name - Name
     * @return The symbol if it exists, null otherwise
     */
	public Symbol getSymbol(String name) {
		Symbol symbol = null;
		for (LinkedHashMap<String, Symbol> scope : this.scopes) {
			symbol = scope.get(name);
			if (symbol != null)
				break;
		}

		return symbol;
	}

	/**
     * Get type for the symbol with the given name.
     *
     * @param name - Name
     * @return Type of the symbol if it exists, null otherwise
     */
	public GoLiteType getSymbolType(String name) {
		Symbol symbol = this.getSymbol(name);

		return (symbol == null) ? null : symbol.getType();
	}

	/**
	 * Returns the symbol with the given name from the current scope.
	 *
	 * @param name - Symbol name
	 * @return Corresponding symbol if it exists, null otherwise
	 */
	public Symbol getSymbolFromCurrentScope(String name) {
		return this.scopes.peek().get(name);
	}

	/**
	 * Returns the symbols in the current scope.
	 *
	 * @return Collection of symbols in the current scope
	 */
	public Collection<Symbol> getSymbolsFromCurrentScope() {
		return this.scopes.peek().values();
	}

	/**
	 * Puts the given symbol into the symbol table (in the current scope).
	 *
	 * @param symbol - Symbol
	 */
	public void putSymbol(Symbol symbol) {
		String name = symbol.getName();
		this.scopes.peek().put(name, symbol);

		this.log(symbol.getClass().getSimpleName() + "\t" + name + "\t" + symbol.getTypeString());
	}

	/**
	 * Check if the symbol with the given name is defined already in the current scope.
	 *
	 * @param name - Symbol name
	 * @param True if such a symbol exists, false otherwise.
	 */
	public boolean defSymbolInCurrentScope(String name) {
		return (this.getSymbolFromCurrentScope(name) != null);
	}

	/**
     * Check if a symbol with the given name exists in the symbol table.
     *
     * @param name - Name
     * @return True if the symbol exists, false otherwise.
     */
	public boolean defSymbol(String name) {
		return (this.getSymbol(name) != null);
	}

	/**
	 * Get the symbol table activity log.
	 *
	 * @return Activity log as a string
	 */
	public String getLog() {
		return this.logger.toString();
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		// Header.
		s.append("(KIND\tNAME\tTYPE\tUNDERLYING TYPE)\n\n");

		// Iterate over the scopes from outermost to innermost.
		Iterator<LinkedHashMap<String, Symbol>> scopeIter = this.scopes.descendingIterator();
		int i = 0;
		while (scopeIter.hasNext()) {
			s.append("SCOPE #" + i + "\n");

			LinkedHashMap<String, Symbol> scope = scopeIter.next();
			for (Map.Entry<String, Symbol> entry : scope.entrySet()) {
			    String name = entry.getKey();
			    Symbol symbol = entry.getValue();
			    
		    	s.append(symbol.getClass().getSimpleName() + "\t" + name + "\t"
		    		+ symbol.getTypeString() + "\t" + symbol.getUnderlyingTypeString() + "\n");
			}

			s.append("\n");
			i++;
		}

		s.setLength(s.length() - 1);
		return s.toString();
	}

}
