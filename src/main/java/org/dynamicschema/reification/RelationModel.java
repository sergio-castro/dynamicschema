package org.dynamicschema.reification;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dynamicschema.visitor.SchemaVisitor;

public class RelationModel implements Iterable<Relation> {

	private List<Relation> relations;
	private Schema schema;
	
	public RelationModel() {
		this(new ArrayList<Relation>());
	}
	
	public RelationModel(List<Relation> relations) {
		setRelations(relations);
	}

	/**
	 * Answers a relation given its relation name
	 * @param relationName
	 * @return
	 */
	public Relation getRelation(String relationName) {
		Relation relation = null;
		for(Relation r : relations) {
			if(r.getName().equals(relationName)) {
				relation = r;
				break;
			}
		}
		return relation;
	}
	
	public List<Relation> getRelations() {
		return relations;
	}

	public void setRelations(List<Relation> relations) {
		this.relations = new ArrayList<Relation>();
		for(Relation relation : relations) {
			addRelation(relation);
		}
	}

	public void addRelation(Relation relation) {
		relations.add(relation);
		relation.attach(this);
	}
	
	public Schema getSchemaOrThrow() {
		if(schema == null)
			throw new RuntimeException("RelationModel not attached to a Schema");
		return getSchema();
	}
	
	public Schema getSchema() {
		return schema;
	}
	
	public void attach(Schema schema) {
		this.schema = schema;
	}
	
	public void accept(SchemaVisitor visitor) {
		if(visitor.doVisit(this)) {
			for(Relation relation : getRelations()) {
				relation.accept(visitor);
			}
		}
	}

	/**
	 * A TableRelation models the occurrence of a table in a relation.
	 * This method answers a list of all the occurrences of a given table in this relation model
	 * @param table
	 * @return
	 */
	public List<TableRelation> getTableRelations(DBTable table) {
		List<TableRelation> tableRelations = new ArrayList<TableRelation>();
		for(Relation relation : getRelations()) {
			tableRelations.addAll(relation.getTableRelations(table));
		}
		return tableRelations;
	}
	

	
	public List<TableRelation> getEagerRelations(DBTable table) {
		List<TableRelation> tableRelations = new ArrayList<TableRelation>();
		for(TableRelation tableRelation : getTableRelations(table)) {
			if(tableRelation.getFetching().equals(Fetching.EAGER))
				tableRelations.add(tableRelation);
		}
		return tableRelations;
	}
	
	public List<TableRelation> getLazyRelations(DBTable table) {
		List<TableRelation> tableRelations = new ArrayList<TableRelation>();
		for(TableRelation tableRelation : getTableRelations(table)) {
			if(tableRelation.getFetching().equals(Fetching.LAZY))
				tableRelations.add(tableRelation);
		}
		return tableRelations;
	}
	
	@Override
	public Iterator<Relation> iterator() {
		return relations.iterator();
	}
	
	
	/*
	 * Get relation  with between 2 tables and with a given name
	 */
	public Relation getRelation(String table1_Name, String table2_Name, String rel_name){
		for (Relation  rel : getRelations()) {
			
			if(rel.isBinary() && rel.getName().equals(rel_name)){
			
				List<DBTable> tables = rel.getTables();
				if(containsTableWithName(tables, table1_Name) && containsTableWithName(tables, table2_Name))
						return rel;
			}
		}
		return null;
	}
	
	private boolean containsTableWithName(List<DBTable> tables, String name){
		
		for (DBTable dbTable : tables) {
			if(dbTable.getName().equals(name))
				return true;
				
		}
		return false;
	}

}


