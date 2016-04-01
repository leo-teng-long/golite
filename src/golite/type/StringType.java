package golite.type;


/**
 * String type.
 */
public class StringType extends PrimitiveGoLiteType {

	@Override
    public boolean isCompatible(GoLiteType type) {
        return type.getUnderlyingType() instanceof StringType;
    }

	@Override
	public String toString() {
		return "string";
	}

}
