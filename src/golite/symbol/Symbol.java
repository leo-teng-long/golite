package golite.symbol;


/**
 * Symbol in symbol table.
 */
public abstract class Symbol {

	/** Name of symbol. */
	protected String name;
	
	/** Type of symbol. */
	protected SymbolType type;

	/*
	 * Getter.
	 */
	public String getName() {
		return this.name;
	}

	/*
	 * Getter.
	 */
	public SymbolType getType() {
		return this.type;
	}

	/**
	 * Returns the toString() of the type.
	 *
	 * @return toString() of the type.
	 */
	public String getTypeString() {
		return this.type.toString();
	}

}
