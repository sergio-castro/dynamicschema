package org.dynamicschema.visitor;

import org.dynamicschema.reification.Column;
import org.dynamicschema.reification.ColumnModel;
import org.dynamicschema.reification.Relation;
import org.dynamicschema.reification.RelationModel;
import org.dynamicschema.reification.Schema;
import org.dynamicschema.reification.DBTable;

/**
 * An interface for visitors of the schema
 * Non-leaf nodes answer a boolean in their doVisit methods. If the result is false, children should not be visited
 * @author sergioc
 *
 */
public interface SchemaVisitor {

	public abstract boolean doVisit(Schema schema);
	
	public abstract boolean doVisit(DBTable table);
	
	public abstract boolean doVisit(ColumnModel columnModel);
	
	public abstract void doVisit(Column column); //column is a leaf the tree representing the schema, so it does not make sense to return a boolean indicating if children should be visited
	
	public abstract boolean doVisit(RelationModel relationModel);
	
	public abstract void doVisit(Relation relation); //relation is a leaf the tree representing the schema, so it does not make sense to return a boolean indicating if children should be visited

}
