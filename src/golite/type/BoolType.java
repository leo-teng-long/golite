package golite.type;


/**
 * bool type.
 */
public class BoolType extends PrimitiveGoLiteType {

	@Override
    public boolean isCompatible(GoLiteType type) {
        return type.getUnderlyingType() instanceof BoolType;
    }

	@Override
	public String toString() {
		return "bool";
	}

}
