package golite.symbol;

import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.Iterator;


/**
 * Struct type.
 */
public class StructType extends GoLiteType {

	/**
	 * Struct field.
	 */
	protected static class Field {

		/** Field Id. */
		private String id;
		/** Field type. */
		private GoLiteType type;

		/**
		 * Constructor/
		 *
		 * @param id - Id
		 * @param type - Type
		 */
		private Field(String id, GoLiteType type) {
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
		protected GoLiteType getType() {
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
	public StructType() {
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
	public void addField(String id, GoLiteType type) {
		this.fields.add(new Field(id, type));
	}

	@Override
	public GoLiteType getUnderlyingType() {
		StructType underlyingStructType = new StructType();

		for (Field f : this.fields)
			underlyingStructType.addField(f.getId(), f.getType().getUnderlyingType());

		return underlyingStructType;
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
