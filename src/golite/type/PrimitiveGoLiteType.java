package golite.type;


/**
 * Primitive type.
 */
public abstract class PrimitiveGoLiteType extends GoLiteType {

	// Equality is performed on the class.
	@Override
    public boolean equals(Object o) {
    	if (!(o instanceof PrimitiveGoLiteType))
        	return false;

        if (this == o)
        	return true;

        PrimitiveGoLiteType other = (PrimitiveGoLiteType) o;
        return this.getClass().equals(other.getClass());
    }

    // Hash code is derived from the class.
    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }

}
