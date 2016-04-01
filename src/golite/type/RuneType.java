package golite.type;


/**
 * rune type.
 */
public class RuneType extends PrimitiveGoLiteType {

	@Override
    public boolean isCompatible(GoLiteType type) {
        return type.getUnderlyingType() instanceof RuneType;
    }

	@Override
	public String toString() {
		return "rune";
	}

}
