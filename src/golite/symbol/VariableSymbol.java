package golite.symbol;

import golite.node.Node;


/**
 * Variable symbol in symbol table.
 */
public class VariableSymbol extends Symbol {

	/**
	 * Constructor.
	 *
	 * @param id - Variable Id
	 * @param type - Type
	 * @param node - Corresponding AST node
	 */
	public VariableSymbol(String id, GoLiteType type, Node node) {
		this.name = id;
		this.type = type;
		this.node = node;
	}

}
