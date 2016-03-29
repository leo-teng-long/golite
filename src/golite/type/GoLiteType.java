package golite.type;


/**
 * GoLite type.
 */
public abstract class GoLiteType {

	/**
	 * Returns the underlying type, which for non-alias types is the type itself.
	 *
	 * @return Underlying type
	 */
	public GoLiteType getUnderlyingType() {
		return this;
	}

	/**
	 * Checks whether the given type is compatible.
	 *
	 * @param type - Type
	 * @return True if the types are compatible, false otherwise. 
	 */
	public abstract boolean isCompatible(GoLiteType type);

}
