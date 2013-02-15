package org.dynamicschema.reification;

import java.util.ArrayList;
import java.util.List;

import org.dynamicschema.sql.util.RelationCondition;
import org.dynamicschema.visitor.SchemaVisitor;

public class Relation {

	private RelationModel relationModel;
	private String name;
	private RelationCondition condition;
	private List<TableOccurrence> cardinality;
	private List<Fetching> fetchings;
	
	public static Fetching defaultFetching(List<TableOccurrence> cardinality, int pos) {
		if(cardinality.size() > 2) //terciary or bigger relationship
			return Fetching.LAZY;
		DBTable targetTable = cardinality.get(pos).getTable();
		for(int i = 0; i < cardinality.size(); i++) {
			if(i == pos)
				continue;
			if(cardinality.get(i).getTable().equals(targetTable)) //recursive relationship
				return Fetching.LAZY;
			if(!cardinality.get(i).getOccurrence().equals(Occurrence.ONE)) //if there is at least table with a cardinality different than one, then the fetching is lazy
				return Fetching.LAZY;
		}
		return Fetching.EAGER;
	}
	
	public static List<Fetching> defaultFetchings(List<TableOccurrence> cardinality) {
		List<Fetching> fetchings = new ArrayList<Fetching>();
		for(int i = 0; i<cardinality.size(); i++) {
			fetchings.add(defaultFetching(cardinality, i));
		}
		return fetchings;
	}

	
	public Relation(String name, List<TableOccurrence> cardinality, RelationCondition condition) {
		this(name, cardinality, condition, defaultFetchings(cardinality));
	}
	
	public Relation(String name, List<TableOccurrence> cardinality, RelationCondition condition, List<Fetching> fetchings) {
		setName(name);
		setCardinality(cardinality);
		setCondition(condition);
		setFetchings(fetchings);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if(name==null || name.isEmpty())
			throw new RuntimeException(name+" is not a valid name for a relation");
		this.name = name;
	}
	
	public List<DBTable> getTables() {
		List<DBTable> tables = new ArrayList<DBTable>();
		for(TableOccurrence tableOccurrence : cardinality) {
			tables.add(tableOccurrence.getTable());
		}
		return tables;
	}

	public RelationCondition getCondition() {
		return condition;
	}

	public void setCondition(RelationCondition condition) {
		this.condition = condition;
	}

	public List<TableOccurrence> getCardinality() {
		return cardinality;
	}

//	public boolean isUnary() {
//		return cardinality.size() == 1;
//	}

	public boolean isBinary() {
		return cardinality.size() == 2;
	}

	public boolean isTerciary() {
		return cardinality.size() == 3;
	}

	
	public void setCardinality(List<TableOccurrence> cardinality) {
		this.cardinality = cardinality;
	}

	public List<Fetching> getFetchings() {
		return fetchings;
	}

	public void setFetchings(List<Fetching> fetchings) {
		this.fetchings = fetchings;
	}
	
	public void attach(RelationModel relationModel) {
		this.relationModel = relationModel;
	}
	
	public List<TableRelation> getTableRelations(DBTable table) {
		List<TableRelation> tableRelations = new ArrayList<TableRelation>();
		for(int i=0; i<cardinality.size(); i++) {
			if(cardinality.get(i).getTable().equals(table)) {
				tableRelations.add(getTableRelation(i));
			}
		}
		return tableRelations;
	}

	private TableRelation getTableRelation(int pos) {
		return new TableRelation(this, pos, fetchings.get(pos));
	}

//	public boolean includes(Table table) {
//		for(Table auxTable : getTables()) {
//			if(auxTable.equals(table))
//				return true;
//		}
//		return false;
//	}
	
	public RelationModel getRelationModelOrThrow() {
		if(relationModel == null)
			throw new RuntimeException("Relation not attached to a RelationModel");
		return getRelationModel();
	}
	
	public RelationModel getRelationModel() {
		return relationModel;
	}
	
	public Schema getSchemaOrThrow() {
		return getRelationModelOrThrow().getSchemaOrThrow();
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	public void accept(SchemaVisitor visitor) {
		visitor.doVisit(this);
	}
	
	/*
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Relation2))
			return false;
		Relation2 relation = (Relation2)o;
		return this.getName().equals(relation.getName());
	}
	
	@Override
	public int hashCode() {
		return getName().hashCode();
	}
	*/
}
