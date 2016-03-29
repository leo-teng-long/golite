package golite.symbol;

import golite.type.GoLiteType;


/**
 * Untyped alias type, where the type is not set (Used as a placeholder in the first pass of the
 * program for symbol table construction).
 */
class UnTypedAliasType extends GoLiteType {

	/** Alias. */
	private String alias;

	/**
	 * Constructor.
	 *
	 * @param alias - Alias
	 */
	public UnTypedAliasType(String alias) {
		this.alias = alias;
	}

	/**
	 * Getter.
	 */
	public String getAlias() {
		return this.alias;
	}

	@Override
	public boolean isCompatible(GoLiteType type) {
        return this.equals(type);
	}

	// Equality performed on the alias.
	@Override
    public boolean equals(Object o) {
    	if (!(o instanceof UnTypedAliasType))
        	return false;

        if (this == o)
        	return true;

        UnTypedAliasType other = (UnTypedAliasType) o;
        return this.alias.equals(other.getAlias());
    }

    // Hash code derived from the alias.
    @Override
    public int hashCode() {
        return this.alias.hashCode();
    }

	@Override
	public String toString() {
		return this.alias;
	}

}
