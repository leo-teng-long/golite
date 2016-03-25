package golite.symbol;

import java.lang.StringBuilder;
import java.util.ArrayList;


/**
 * Struct symbol type.
 */
public class StructSymbolType extends SymbolType {

	/**
	 * Struct field.
	 */
	private static class Field {

		/** Field Id. */
		private String id;
		/** Field type. */
		private SymbolType type;

		/**
		 * Constructor/
		 *
		 * @param id - Id
		 * @param type - Type
		 */
		private Field(String id, SymbolType type) {
			this.id = id;
			this.type = type;
		}

		@Override
		public String toString() {
			return this.id + " " + this.type;
		}

	}

	/** Fields. */
	private ArrayList<Field> fields;

	/**
	 * Constructor.
	 */
	public StructSymbolType() {
		super();
		this.fields = new ArrayList<Field>();
	}

	/**
	 * Adds a field to the struct type.
	 *
	 * @param id - Field Id
	 * @param type - Field type
	 */
	public void addField(String id, SymbolType type) {
		this.fields.add(new Field(id, type));
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("struct(");

		boolean rest = false;
		for (Field f : this.fields) {
			if (rest)
				s.append(", ");
			else
				rest = true;
			s.append(f.toString());
		}
			
		s.append(")");
		return s.toString();
	}

}
