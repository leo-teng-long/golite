package golite.symbol;

import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.Iterator;


/**
 * Struct symbol type.
 */
public class StructSymbolType extends SymbolType {

	/**
	 * Struct field.
	 */
	protected static class Field {

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

		/**
		 * Getter.
		 */
		protected String getId() {
			return this.id;
		}

		/**
		 * Getter.
		 */
		protected SymbolType getType() {
			return this.type;
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
	 * Returns an iterator over the fields.
	 * 
	 * @return Iterator over fields
	 */
	public Iterator<Field> getFieldIterator() {
		return this.fields.iterator();
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
	public SymbolType getUnderlyingType() {
		StructSymbolType underlyingStructSymbolType = new StructSymbolType();

		for (Field f : this.fields)
			underlyingStructSymbolType.addField(f.getId(), f.getType().getUnderlyingType());

		return underlyingStructSymbolType;
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
