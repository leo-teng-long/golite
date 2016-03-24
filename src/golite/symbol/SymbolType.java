package golite.symbol;


/**
 * Symbol type.
 */
public abstract class SymbolType {

	/**
	 * Returns the underlying type, which for non-alias types is the type itself.
	 *
	 * @return Underlying type
	 */
	public SymbolType getUnderlyingType() {
		return this;
	}

}
