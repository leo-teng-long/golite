package golite.symbol;

import golite.type.GoLiteType;
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

	// Equality performed on the symbol name.
	@Override
    public boolean equals(Object o) {
    	if (!(o instanceof Symbol))
        	return false;

        if (this == o)
        	return true;

        Symbol other = (Symbol) o;
        return this.name.equals(other.getName());
    }

    // Hash code derived from the symbol name.
    @Override
    public int hashCode() {
        return this.name.hashCode();
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
		if (this.type == null)
			return "null";

		return this.type.toString();
	}

	/**
	 * Returns a print-friendly string representation of the underlying type.
	 *
	 * @return Print-friendly string representation of the underlying type.
	 */
	public String getUnderlyingTypeString() {
		if (this.type == null)
			return "null";
		
		return this.type.getUnderlyingType().toString();
	}

}
