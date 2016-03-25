package golite.symbol;


/**
 * Type alias symbol in symbol table.
 */
public class TypeAliasSymbol extends Symbol {

	/**
	 * Constructor.
	 *
	 * @param id - Alias Id
	 * @param type - Type to alias
	 */
	public TypeAliasSymbol(String id, SymbolType type) {
		this.name = id;
		this.type = type;
	}

}
