package org.dynamicschema.visitor;

import org.dynamicschema.reification.Relation;
import org.dynamicschema.reification.Schema;
import org.dynamicschema.reification.Table;

/*
 * Visits the objects in a database schema
 */
public class SchemaVisitor {

	public SchemaVisitor() {}
	
	public void visit(Schema schema) {
		for(Table table : schema.getTables())
			visit(table);
	}
	
	public void visit(Table table) {
		visit(table.getBaseRelation());
		for(Relation transitiveRelation : table.getRelationModel()) {
			visit(transitiveRelation);
		}
	}
	
	public void visit(Relation relation) {
		if(doVisit(relation)) {
			for(Table table : relation.getTables())
				visit(relation, table);
		}
	}
	
	public void visit(Relation relation, Table table) {
		doVisit(relation, table);
	}
	
	/*
	 * If the method returns true, then the visitor should visit the tables in the relation
	 */
	public boolean doVisit(Relation relation) {return true;}
	
	public void doVisit(Relation relation, Table table) {}
}
