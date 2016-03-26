package golite.type;


/**
 * Slice type.
 */
public class SliceType extends GoLiteType {

	/** Type of the slice. */
	private GoLiteType type;

	/**
	 * Constructor.
	 */
	public SliceType(GoLiteType type) {
		this.type = type;
	}

	/**
	 * Getter
	 */
	public GoLiteType getType() {
		return this.type;
	}

	@Override
	public GoLiteType getUnderlyingType() {
		return new SliceType(this.type.getUnderlyingType());
	}

	@Override
	public String toString() {
		return "[]" + this.type;
	}

}