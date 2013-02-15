package org.dynamicschema.reification;

/**
 * The occurrence of a table (cardinality) in a relation
 * @author sergioc
 *
 */
public class RelationMember {

	private String tableName;
	private Occurrence occurrence;
	
	public RelationMember(String tableName, Occurrence occurrence) {
		this.tableName = tableName;
		this.occurrence = occurrence;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String table) {
		this.tableName = table;
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
		result = prime * result + ((tableName == null) ? 0 : tableName.hashCode());
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
		RelationMember other = (RelationMember) obj;
		if (occurrence != other.occurrence)
			return false;
		if (tableName == null) {
			if (other.tableName != null)
				return false;
		} else if (!tableName.equals(other.tableName))
			return false;
		return true;
	}

}
