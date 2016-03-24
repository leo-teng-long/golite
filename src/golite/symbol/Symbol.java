package golite.symbol;


/**
 * Symbol in symbol table.
 */
abstract public class Symbol {

	/** Name of symbol. */
	private String name;
	
	/*
	 * Getter.
	 */
	public String getName() {
		return this.name;
	}

}
