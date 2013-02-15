package org.dynamicschema.reification;

/**
 * The occurrence of a table (cardinality) in a relation
 * @author sergioc
 *
 */
public class TableOccurrence {

	private DBTable table;
	private Occurrence occurrence;
	
	public TableOccurrence(DBTable table, Occurrence occurrence) {
		this.table = table;
		this.occurrence = occurrence;
	}

	public DBTable getTable() {
		return table;
	}

	public void setTable(DBTable table) {
		this.table = table;
	}

	public Occurrence getOccurrence() {
		return occurrence;
	}

	public void setOccurrence(Occurrence occurrence) {
		this.occurrence = occurrence;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((occurrence == null) ? 0 : occurrence.hashCode());
		result = prime * result + ((table == null) ? 0 : table.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TableOccurrence other = (TableOccurrence) obj;
		if (occurrence != other.occurrence)
			return false;
		if (table == null) {
			if (other.table != null)
				return false;
		} else if (!table.equals(other.table))
			return false;
		return true;
	}
	

	
}
