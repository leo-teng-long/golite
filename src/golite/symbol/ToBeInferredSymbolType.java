package golite.symbol;


/**
 * A placeholder symbol type indicating the type for the given symbol must be inferred.
 */
class ToBeInferredSymbolType extends SymbolType {

	@Override
	public String toString() {
		return "[TBI]";
	}

}
