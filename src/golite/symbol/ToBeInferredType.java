package golite.symbol;


/**
 * A placeholder type indicating the type for a given program element must be inferred.
 */
class ToBeInferredType extends GoLiteType {

	@Override
	public String toString() {
		return "[TBI]";
	}

}
