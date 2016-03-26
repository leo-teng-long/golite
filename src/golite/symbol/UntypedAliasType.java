package golite.symbol;

import golite.type.GoLiteType;


/**
 * Untyped alias type, where the type is not set (Used as a placeholder in the first pass of the
 * program for symbol table construction).
 */
class UnTypedAliasType extends GoLiteType {

	/** Alias. */
	private String alias;

	/**
	 * Constructor.
	 *
	 * @param alias - Alias
	 */
	public UnTypedAliasType(String alias) {
		this.alias = alias;
	}

	/**
	 * Getter.
	 */
	public String getAlias() {
		return this.alias;
	}

	@Override
	public String toString() {
		return this.alias;
	}

}
