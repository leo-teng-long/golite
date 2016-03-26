package golite.symbol;

import golite.node.Node;

import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.Iterator;


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
	 * @param returnType - Return type of function (null if void)
	 * @param node - Corresponding AST node
	 */
	public FunctionSymbol(String name, SymbolType returnType, Node node) {
		this.name = name;
		this.type = returnType;
		this.node = node;
		this.argTypes = new ArrayList<SymbolType>();
	}

	/**
	 * Getter.
	 */
	public ArrayList<SymbolType> getArgTypes() {
		return this.argTypes;
	}

	/**
	 * Setter.
	 */
	public void setArgTypes(ArrayList<SymbolType> argTypes) {
		this.argTypes = argTypes;
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

	@Override
	public String getUnderlyingTypeString() {
		StringBuilder s = new StringBuilder();
		
		s.append("(");
		boolean rest = false;
		for (SymbolType t : this.argTypes) {
			if (rest)
				s.append(", ");
			else
				rest = true;
			s.append(t.getUnderlyingType().toString());
		}
		s.append(")");

		s.append(" -> ");

		s.append("(");
		if (this.type != null)
			s.append(this.type.getUnderlyingType().toString());
		s.append(")");

		return s.toString();
	}

}
