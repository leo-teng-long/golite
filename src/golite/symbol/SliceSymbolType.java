package golite.symbol;


/**
 * Slice symbol type.
 */
public class SliceSymbolType extends SymbolType {

	/** Type of the slice. */
	private SymbolType type;

	/**
	 * Constructor.
	 */
	public SliceSymbolType(SymbolType type) {
		this.type = type;
	}

	/**
	 * Getter
	 */
	public SymbolType getType() {
		return this.type;
	}

	@Override
	public SymbolType getUnderlyingType() {
		return new SliceSymbolType(this.type.getUnderlyingType());
	}

	@Override
	public String toString() {
		return "[]" + this.type;
	}

}