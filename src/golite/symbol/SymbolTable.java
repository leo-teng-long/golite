package golite.symbol;

import java.lang.StringBuilder;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;


/**
 * Symbol table.
 */
public class SymbolTable {

	/** Scopes are stored in a deque, with each scope represented as a hash map from identifier to
	  * symbol. */
	private Deque<HashMap<String, Symbol>> scopes;
	/** Stores the string representation of the symbol table. */
	private StringBuilder buffer;

	/**
	 * Constructor.
	 */
	public SymbolTable() {
		this.scopes = new LinkedList<HashMap<String, Symbol>>();
		this.buffer = new StringBuilder();
	}

	// Adds the given string to the buffer.
	private void addToBuffer(String s) {
        this.buffer.append(s);
        this.buffer.append(System.getProperty("line.separator"));
    }

    /**
     * Enter a scope, pushing it onto the stack.
     */
	public void scope() {
		this.scopes.push(new HashMap<String, Symbol>());
		this.addToBuffer("ENTER SCOPE");
	}

	/**
     * Exit a scope, popping it off the stack.
     */
	public void unscope() {
		this.scopes.pop();
		this.addToBuffer("EXIT SCOPE");
	}

	/**
     * Get the symbol with the given name.
     *
     * @param name - Name
     * @return The symbol if it exists, null otherwise.
     */
	public Symbol getSymbol(String name) {
		Symbol symbol = null;
		for (HashMap<String, Symbol> scope : this.scopes) {
			symbol = scope.get(name);
			if (symbol != null)
				break;
		}

		return symbol;
	}

	/**
	 * Returns the symbol with the given name from the current scope.
	 *
	 * @param name - Symbol name
	 * @param Corresponding symbol if it exists, null otherwise
	 */
	public Symbol getSymbolFromCurrentScope(String name) {
		return this.scopes.peek().get(name);
	}

	/**
	 * Puts the given symbol into the symbol table (in the current scope).
	 *
	 * @param symbol - Symbol
	 */
	public void putSymbol(Symbol symbol) {
		String name = symbol.getName();
		this.scopes.peek().put(name, symbol);
		this.addToBuffer(symbol.getClass().getSimpleName() + "\t" + name + "\t"
			+ symbol.getTypeString());
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

	@Override
	public String toString() {
		return this.buffer.toString();
	}

}
