package org.dynamicschema.visitor;

import org.dynamicschema.Fetching;
import org.dynamicschema.Relation;
import org.dynamicschema.Table;
import org.dynamicschema.context.IRelationalContextManager;

/*
 * Visits a table and all its relationships
 */
public abstract class TableRelationsVisitor {

	protected Fetching fetching; //only relations with this kind of fetching will be visited
	
	public TableRelationsVisitor() {
		this(null);
	}
	
	public TableRelationsVisitor(Fetching fetching) {  
		this.fetching=fetching;
	}
	
	public void visit(Table table) {
		if(fetching==null || fetching.equals(Fetching.EAGER))
			visit(table.getBaseRelation());
		for(Relation transitiveRelation : table.getRelationModel()) {
			if(fetching==null || fetching.equals(transitiveRelation.getFetching()))
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
		if(doVisit(relation, table) && !relation.isBaseRelation()) {
			visit(table);
		}
	}
	
	/*
	 * If the method returns true, then the visitor should visit the tables in the relation
	 */
	public boolean doVisit(Relation relation) {return true;}
	
	/*
	 * If the method returns true, then the visitor should continue visiting the relations of the table sent as a second parameter
	 */
	public boolean doVisit(Relation relation, Table table) {return true;}

}
