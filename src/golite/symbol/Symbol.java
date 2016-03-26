package golite.symbol;

import golite.node.Node;


/**
 * Symbol in symbol table.
 */
public abstract class Symbol {

	/** Name of symbol. */
	protected String name;
	
	/** Type of symbol. */
	protected GoLiteType type;

	/** AST node corresponding to the symbol declaration. */
	protected Node node;

	/*
	 * Getter.
	 */
	public String getName() {
		return this.name;
	}

	/*
	 * Getter.
	 */
	public GoLiteType getType() {
		return this.type;
	}

	/*
	 * Setter.
	 */
	public void setType(GoLiteType type) {
		this.type = type;
	}

	/*
	 * Getter.
	 */
	public Node getNode() {
		return this.node;
	}

	/**
	 * Returns the underlying type.
	 *
	 * @return Underlying type.
	 */
	public GoLiteType getUnderlyingType() {
		return this.type.getUnderlyingType();
	}

	/**
	 * Returns a print-friendly string representation of the type.
	 *
	 * @return Print-friendly string representation of the type.
	 */
	public String getTypeString() {
		return this.type.toString();
	}

	/**
	 * Returns a print-friendly string representation of the underlying type.
	 *
	 * @return Print-friendly string representation of the underlying type.
	 */
	public String getUnderlyingTypeString() {
		return this.type.getUnderlyingType().toString();
	}

}
