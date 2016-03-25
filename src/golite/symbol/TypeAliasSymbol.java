package golite.symbol;


/**
 * Type alias symbol in symbol table.
 */
public class TypeAliasSymbol extends Symbol {

	/**
	 * Constructor.
	 *
	 * @param alias - alias
	 * @param type - Type to alias
	 */
	public TypeAliasSymbol(String alias, SymbolType type) {
		this.name = alias;
		this.type = type;
	}

}
