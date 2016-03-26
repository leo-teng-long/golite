package golite.type;


/**
 * Void type (for void functions).
 */
public class VoidType extends GoLiteType {

	// Equality is performed on the class.
	@Override
    public boolean equals(Object o) {
    	if (!(o instanceof VoidType))
        	return false;

        if (this == o)
        	return true;

        VoidType other = (VoidType) o;
        return this.getClass().equals(other.getClass());
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
