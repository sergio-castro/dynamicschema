package org.dynamicschema.reification;

import java.util.ArrayList;
import java.util.List;

public class TableRelation {

	private Relation relation;
	private int indexSourceTable;
	private Fetching fetching;
	
	public TableRelation(Relation relation, int indexSourceTable, Fetching fetching) {
		this.relation = relation;
		this.indexSourceTable = indexSourceTable;
		this.fetching = fetching;
	}

	public int getIndexSourceTable() {
		return indexSourceTable;
	}
	
	public TableOccurrence getBaseTableOccurrence() {
		return relation.getCardinality().get(indexSourceTable);
	}

	public List<TableOccurrence> getRelationTablesOccurrences() {
		List<TableOccurrence> relationTableOccurrences = new ArrayList<TableOccurrence>();
		for(int i=0; i<relation.getCardinality().size(); i++) {
			if(i == indexSourceTable)
				continue;
			relationTableOccurrences.add(relation.getCardinality().get(i));
		}
		return relationTableOccurrences;
	}

	public Relation getRelation() {
		return relation;
	}

	public Fetching getFetching() {
		return fetching;
	}

	public int indexInRelation(TableOccurrence tableOccurrence) {
		return getRelation().getCardinality().indexOf(tableOccurrence);
	}
}
