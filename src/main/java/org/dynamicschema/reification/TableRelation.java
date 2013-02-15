package org.dynamicschema.reification;

import java.util.ArrayList;
import java.util.List;

public class TableRelation {

	private Relation relation;
	private int indexTableInRelation;
	
	public TableRelation(Relation relation, int indexTableInRelation) {
		this.relation = relation;
		this.indexTableInRelation = indexTableInRelation;
	}

	public int getIndexTableInRelation() {
		return indexTableInRelation;
	}
	
	public TableOccurrence getBaseTableOccurrence() {
		return relation.getCardinality().get(indexTableInRelation);
	}

	public List<TableOccurrence> getRelationTablesOccurrences() {
		List<TableOccurrence> relationTableOccurrences = new ArrayList<TableOccurrence>();
		for(int i=0; i<relation.getCardinality().size(); i++) {
			if(i == indexTableInRelation)
				continue;
			relationTableOccurrences.add(relation.getCardinality().get(i));
		}
		return relationTableOccurrences;
	}

	public Relation getRelation() {
		return relation;
	}

	public String getRole() {
		return relation.getRoles().get(indexTableInRelation);
	}
	
	public Fetching getFetching() {
		return getRelation().getFetching(indexTableInRelation);
	}

	public int indexInRelation(TableOccurrence tableOccurrence) {
		return getRelation().getCardinality().indexOf(tableOccurrence);
	}

	public boolean equivalent(TableRelation tableRelation) {
		return (this.relation.equals(tableRelation.relation) && this.indexTableInRelation == tableRelation.indexTableInRelation);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + indexTableInRelation;
		result = prime * result
				+ ((relation == null) ? 0 : relation.hashCode());
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
		TableRelation other = (TableRelation) obj;
		if (indexTableInRelation != other.indexTableInRelation)
			return false;
		if (relation == null) {
			if (other.relation != null)
				return false;
		} else if (!relation.equals(other.relation))
			return false;
		return true;
	}
	

	
	
	
}
