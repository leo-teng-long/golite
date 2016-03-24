package golite.symbol;


/**
 * Variable symbol in symbol table.
 */
public class VariableSymbol extends Symbol {

	/**
	 * Constructor.
	 *
	 * @param id - Variable Id
	 * @param type - Type
	 */
	public VariableSymbol(String id, SymbolType type) {
		this.name = id;
		this.type = type;
	}

}
