package golite.type;


/**
 * Void type (for void functions).
 */
public class VoidType extends GoLiteType {

    @Override
    public boolean isCompatible(GoLiteType type) {
        return type.getUnderlyingType() instanceof VoidType;
    }

	// Equality is performed on the class.
	@Override
    public boolean equals(Object o) {
    	return o instanceof VoidType;
    }

    // Hash code is derived from the class.
    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }

	@Override
	public String toString() {
		return "";
	}

}
