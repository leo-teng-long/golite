package golite.type;

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
	public static class Field {

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
		public String getId() {
			return this.id;
		}

		/**
		 * Getter.
		 */
		public GoLiteType getType() {
			return this.type;
		}

		// Equality is performed on the Id and type.
		@Override
	    public boolean equals(Object o) {
	    	if (!(o instanceof Field))
	        	return false;

	        if (this == o)
	        	return true;

	        Field other = (Field) o;
	        return this.id.equals(other.getId()) && this.type.equals(other.getType());
	    }

	    // Hash code is derived from the Id and type.
	    @Override
	    public int hashCode() {
	        return 997 * this.id.hashCode() ^ 991 * this.type.hashCode();
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
	 * Returns the number of fields.
	 *
	 * @return Number of fields
	 */
	public int size() {
		return this.fields.size();
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
	 * Checks if a field with the given Id exists in the struct.
	 *
	 * @param id - Field Id
	 * @return True if the field exists, false otherwise
	 */
	public boolean hasField(String id) {
		for (Field f : this.fields) {
			if (f.getId().equals(id))
				return true;
		}

		return false;
	}

	/**
	 * Returns the field with the given Id.
	 *
	 * @param id - Field Id
	 * @return Corresponding field
	 * @throws IllegalArgumentException if no such field exists.
	 */
	public Field getField(String id) {
		for (Field f : this.fields) {
			if (f.getId().equals(id))
				return f;
		}

		throw new IllegalArgumentException("No such field " + id);
	}

	/**
	 * Returns the type of the field with the given Id.
	 *
	 * @param id - Field Id
	 * @return Type of the field
	 */
	public GoLiteType getFieldType(String id) {
		return this.getField(id).getType();
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

	// Equality is performed on fields.
	@Override
    public boolean equals(Object o) {
    	if (!(o instanceof StructType))
        	return false;

        if (this == o)
        	return true;

        StructType other = (StructType) o;

        if (this.fields.size() != other.size())
        	return false;

        Iterator<Field> iter = other.getFieldIterator();
        Field otherField = null;
        for (int i = 0; iter.hasNext(); i++) {
        	if (!this.fields.get(i).equals(iter.next()))
        		return false;
        }

        return true;
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
