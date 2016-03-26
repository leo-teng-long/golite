package golite.symbol;


/**
 * Alias type.
 */
public class AliasType extends GoLiteType {

	/** Alias. */
	private String alias;
	/** Underlying type. */
	private GoLiteType type;

	/**
	 * Constructor.
	 *
	 * @param alias - Alias
	 * @param type - Underlying type
	 */
	public AliasType(String alias, GoLiteType type) {
		this.alias = alias;
		this.type = type;
	}

	/**
	 * Getter.
	 */
	public String getAlias() {
		return this.alias;
	}

	/**
	 * Getter.
	 */
	public GoLiteType getType() {
		return this.type;
	}

	@Override
	public GoLiteType getUnderlyingType() {
		return this.type.getUnderlyingType();
	}

	@Override
	public String toString() {
		return this.alias;
	}

}
