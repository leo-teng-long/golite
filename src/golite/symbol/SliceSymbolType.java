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

	@Override
	public String toString() {
		return "[]" + this.type;
	}

}