package golite.symbol;

import golite.type.AliasType;
import golite.type.GoLiteType;
import golite.node.Node;


/**
 * Type alias symbol in symbol table.
 */
public class TypeAliasSymbol extends Symbol {

	/**
	 * Constructor.
	 *
	 * @param alias - alias
	 * @param type - Type to alias
	 * @param node - Corresponding AST node
	 */
	public TypeAliasSymbol(String alias, GoLiteType type, Node node) {
		this.name = alias;
		this.type = type;
		this.node = node;
	}

	/**
	 * Returns the alias as an alias type.
	 *
	 * @return Alias type
	 */
	public AliasType getAliasType() {
		return new AliasType(this.name, this.type);
	}

}
