package org.dynamicschema.context;

import org.dynamicschema.reification.Column;
import org.dynamicschema.reification.Relation;
import org.dynamicschema.reification.Table;

public interface IRelationalContextManager {

	public RelationContext getRelationContext(Relation relation);
	
	public TableContext createTableContext(Relation relation, Table table);
	
	public String newAliasName(String tableName);
	
	public RelationContext getOrCreateRelationContext(Relation relation);
	
	public Object getColumnValue(Relation relation, Column column);
}
