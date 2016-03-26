package golite.type;


/**
 * Array type.
 */
public class ArrayType extends GoLiteType {

	/** Type of the array. */
	private GoLiteType type;
	/** Array bound. */
	private int bound;

	/**
	 * Constructor.
	 */
	public ArrayType(GoLiteType type, int bound) {
		this.type = type;
		this.bound = bound;
	}

	/**
	 * Getter.
	 */
	public GoLiteType getType() {
		return this.type;
	}

	/**
	 * Getter.
	 */
	public int getBound() {
		return this.bound;
	}

	@Override
	public GoLiteType getUnderlyingType() {
		return new ArrayType(this.type.getUnderlyingType(), this.bound);
	}

	// Equality is performed on the type and bound.
	@Override
    public boolean equals(Object o) {
    	if (!(o instanceof ArrayType))
        	return false;

        if (this == o)
        	return true;

        ArrayType other = (ArrayType) o;
        return this.type.equals(other.getType()) && this.bound == other.getBound();
    }

    // Hash code is derived from the type and bound.
    @Override
    public int hashCode() {
        return 1013 * (this.type.hashCode()) ^ 1009 * this.bound;
    }

	@Override
	public String toString() {
		return "[" + this.bound + "]" + this.type;
	}

}
