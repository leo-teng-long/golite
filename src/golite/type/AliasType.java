package golite.type;


/**
 * Alias type.
 */
public class AliasType extends GoLiteType {

	/** Alias. */
	private String alias;
	/** Underlying type. */
	private GoLiteType type;

	/**
	 * Constructor.
	 *
	 * @param alias - Alias
	 * @param type - Underlying type
	 */
	public AliasType(String alias, GoLiteType type) {
		this.alias = alias;
		this.type = type;
	}

	/**
	 * Getter.
	 */
	public String getAlias() {
		return this.alias;
	}

	/**
	 * Getter.
	 */
	public GoLiteType getType() {
		return this.type;
	}

	@Override
	public GoLiteType getUnderlyingType() {
		return this.type.getUnderlyingType();
	}

	@Override
	public boolean isCompatible(GoLiteType type) {
		// Check if the given type appears anywhere in the chain of aliases for
		// this alias type.
		GoLiteType t = this;
		do {
            if (t.equals(type))
                return true;

            if (t instanceof AliasType)
                t = ((AliasType) t).getType();
        } while (t instanceof AliasType);

        return false;
	}

	// Equality performed on the alias.
	@Override
    public boolean equals(Object o) {
    	if (!(o instanceof AliasType))
        	return false;

        if (this == o)
        	return true;

        AliasType other = (AliasType) o;
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
