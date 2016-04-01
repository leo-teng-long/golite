package golite.type;


/**
 * int type.
 */
public class IntType extends PrimitiveGoLiteType {

	@Override
    public boolean isCompatible(GoLiteType type) {
        return type.getUnderlyingType() instanceof IntType;
    }

	@Override
	public String toString() {
		return "int";
	}

}
