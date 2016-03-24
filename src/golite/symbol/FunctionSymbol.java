package golite.symbol;

import java.lang.StringBuilder;
import java.util.ArrayList;


/**
 * Function symbol in symbol table.
 */
public class FunctionSymbol extends Symbol {

	/** Argument types. */
	private ArrayList<SymbolType> argTypes;

	/**
	 * Constructor.
	 *
	 * @param id - Id
	 * @param type - Type
	 */
	public FunctionSymbol(String name, SymbolType returnType) {
		this.name = name;
		this.type = type;
		this.argTypes = new ArrayList<SymbolType>();
	}

	/**
	 * Add the given argument type.
	 *
	 * @param argType - Argument Type
	 */
	public void addArgType(SymbolType argType) {
		this.argTypes.add(argType);
	}

	/**
	 * Add a certain number of the given argument type
	 *
	 * @param argType - Argument Type
	 * @param cnt - Number to add
	 */
	public void addArgType(SymbolType argType, int cnt) {
		for (int i = 0; i < cnt; i++)
			this.argTypes.add(argType);
	}

	@Override
	public String getTypeString() {
		StringBuilder s = new StringBuilder();
		
		s.append("(");
		boolean rest = false;
		for (SymbolType t : this.argTypes) {
			if (rest)
				s.append(", ");
			else
				rest = true;
			s.append(t.toString());
		}
		s.append(")");

		s.append(" -> ");

		s.append("(");
		if (this.type != null)
			s.append(this.type.toString());
		s.append(")");

		return s.toString();
	}

}
