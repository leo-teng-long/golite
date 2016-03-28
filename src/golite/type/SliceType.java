package golite.type;


/**
 * Slice type.
 */
public class SliceType extends GoLiteType {

	/** Type of each element in the slice. */
	private GoLiteType type;

	/**
	 * Constructor.
	 */
	public SliceType(GoLiteType type) {
		this.type = type;
	}

	/**
	 * Getter
	 */
	public GoLiteType getType() {
		return this.type;
	}

	/**
	 * Returns the element type.
	 *
	 * @return Element type
	 */
	public GoLiteType getElemType() {
		return this.type;
	}

	@Override
	public GoLiteType getUnderlyingType() {
		return new SliceType(this.type.getUnderlyingType());
	}

	// Equality is performed on the type.
	@Override
    public boolean equals(Object o) {
    	if (!(o instanceof SliceType))
        	return false;

        if (this == o)
        	return true;

        SliceType other = (SliceType) o;
        return this.type.equals(other.getType());
    }

    // Hash code is derived from the type.
    @Override
    public int hashCode() {
        return this.type.hashCode();
    }

	@Override
	public String toString() {
		return "[]" + this.type;
	}

}