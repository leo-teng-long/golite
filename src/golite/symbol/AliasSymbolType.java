package golite.symbol;


/**
 * Alias symbol type.
 */
public class AliasSymbolType extends SymbolType {

	/** Alias. */
	private String alias;
	/** Underlying type. */
	private SymbolType type;

	/**
	 * Constructor.
	 *
	 * @param alias - Alias
	 * @param type - Underlying type
	 */
	public AliasSymbolType(String alias, SymbolType type) {
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
	public SymbolType getType() {
		return this.type;
	}

	@Override
	public SymbolType getUnderlyingType() {
		return this.type.getUnderlyingType();
	}

	@Override
	public String toString() {
		return this.alias;
	}

}
