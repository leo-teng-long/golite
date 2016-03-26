package golite.symbol;


/**
 * Array type.
 */
public class ArrayType extends GoLiteType {

	/** Type of the array. */
	private GoLiteType type;
	/** Array bound. */
	private int bound;

	/**
	 * Constructor.
	 */
	public ArrayType(GoLiteType type, int bound) {
		this.type = type;
		this.bound = bound;
	}

	/**
	 * Getter.
	 */
	public GoLiteType getType() {
		return this.type;
	}

	/**
	 * Getter.
	 */
	public int getBound() {
		return this.bound;
	}

	@Override
	public GoLiteType getUnderlyingType() {
		return new ArrayType(this.type.getUnderlyingType(), this.bound);
	}

	@Override
	public String toString() {
		return "[" + this.bound + "]" + this.type;
	}

}
