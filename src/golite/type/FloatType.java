package golite.type;


/**
 * float64 type.
 */
public class FloatType extends PrimitiveGoLiteType {

	@Override
    public boolean isCompatible(GoLiteType type) {
        return type.getUnderlyingType() instanceof FloatType;
    }

	@Override
	public String toString() {
		return "float64";
	}

}
