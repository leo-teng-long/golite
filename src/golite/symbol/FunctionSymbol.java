package golite.symbol;

import golite.type.GoLiteType;
import golite.type.VoidType;
import golite.node.Node;

import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.Iterator;


/**
 * Function symbol in symbol table.
 */
public class FunctionSymbol extends Symbol {

	/** Argument types. */
	private ArrayList<GoLiteType> argTypes;

	/**
	 * Constructor.
	 *
	 * @param id - Id
	 * @param returnType - Return type of function (null if void)
	 * @param node - Corresponding AST node
	 */
	public FunctionSymbol(String name, GoLiteType returnType, Node node) {
		this.name = name;
		this.type = (returnType == null) ? new VoidType() : returnType;
		this.node = node;
		this.argTypes = new ArrayList<GoLiteType>();
	}

	/**
	 * Constructor for void function.
	 *
	 * @param id - Id
	 * @param node - Corresponding AST node
	 */
	public FunctionSymbol(String name, Node node) {
		this(name, null, node);
	}

	/**
	 * Getter.
	 */
	public ArrayList<GoLiteType> getArgTypes() {
		return this.argTypes;
	}

	/**
	 * Setter.
	 */
	public void setArgTypes(ArrayList<GoLiteType> argTypes) {
		this.argTypes = argTypes;
	}

	/**
	 * Add the given argument type.
	 *
	 * @param argType - Argument Type
	 */
	public void addArgType(GoLiteType argType) {
		this.argTypes.add(argType);
	}

	/**
	 * Add a certain number of the given argument type
	 *
	 * @param argType - Argument Type
	 * @param cnt - Number to add
	 */
	public void addArgType(GoLiteType argType, int cnt) {
		for (int i = 0; i < cnt; i++)
			this.argTypes.add(argType);
	}

	@Override
	public String getTypeString() {
		StringBuilder s = new StringBuilder();
		
		s.append("(");
		boolean rest = false;
		for (GoLiteType t : this.argTypes) {
			if (rest)
				s.append(", ");
			else
				rest = true;
			s.append(t.toString());
		}
		s.append(")");

		s.append(" -> ");

		s.append("(");
		s.append(this.type.toString());
		s.append(")");

		return s.toString();
	}

	@Override
	public String getUnderlyingTypeString() {
		StringBuilder s = new StringBuilder();
		
		s.append("(");
		boolean rest = false;
		for (GoLiteType t : this.argTypes) {
			if (rest)
				s.append(", ");
			else
				rest = true;
			s.append(t.getUnderlyingType().toString());
		}
		s.append(")");

		s.append(" -> ");

		s.append("(");
		s.append(this.type.getUnderlyingType().toString());
		s.append(")");

		return s.toString();
	}

}
