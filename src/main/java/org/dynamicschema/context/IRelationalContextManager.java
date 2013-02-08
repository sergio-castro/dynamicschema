package org.dynamicschema.context;

import org.dynamicschema.Column;
import org.dynamicschema.Relation;
import org.dynamicschema.Table;

public interface IRelationalContextManager {

	public RelationContext getRelationContext(Relation relation);
	
	public TableContext createTableContext(Relation relation, Table table);
	
	public String newAliasName(String tableName);
	
	public RelationContext getOrCreateRelationContext(Relation relation);
	
	public Object getColumnValue(Relation relation, Column column);
}
