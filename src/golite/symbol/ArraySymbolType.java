package golite.symbol;


/**
 * Array symbol type.
 */
public class ArraySymbolType extends SymbolType {

	/** Type of the array. */
	private SymbolType type;
	/** Array bound. */
	private int bound;

	/**
	 * Constructor.
	 */
	public ArraySymbolType(SymbolType type, int bound) {
		this.type = type;
		this.bound = bound;
	}

	/**
	 * Getter.
	 */
	public SymbolType getType() {
		return this.type;
	}

	/**
	 * Getter.
	 */
	public int getBound() {
		return this.bound;
	}

	@Override
	public SymbolType getUnderlyingType() {
		return new ArraySymbolType(this.type.getUnderlyingType(), this.bound);
	}

	@Override
	public String toString() {
		return "[" + this.bound + "]" + this.type;
	}

}
