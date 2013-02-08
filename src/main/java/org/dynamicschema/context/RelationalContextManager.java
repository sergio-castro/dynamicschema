package org.dynamicschema.context;

import java.util.HashMap;
import java.util.Map;

import org.dynamicschema.Column;
import org.dynamicschema.Relation;
import org.dynamicschema.Table;


/*
 * Keep a track of uses alias names
 * Provides a method answering a new alias for a table that warranties the alias has not been used in any possible occurrence of the table in a SQL expression
 */
public class RelationalContextManager implements IRelationalContextManager {

	private Map<String, Integer> aliasesCounter;
	//private AliasDictionary aliasDictionary;
	private Map<Relation, RelationContext> relationContextMap;
	private int offset;
	
	
	public RelationalContextManager() {
		aliasesCounter = new HashMap<String, Integer>();
		relationContextMap = new HashMap<Relation, RelationContext>();
	}
	

	
	private int getOffset() {
		return offset;
	}
	
	private int addOffset(int delta) {
		return offset+=delta;
	}
	
	public RelationContext getRelationContext(Relation relation) {
		return relationContextMap.get(relation);
	}
	
	public RelationContext getOrCreateRelationContext(Relation relation) {
		RelationContext relationContext = relationContextMap.get(relation);
		if(relationContext==null) {
			relationContext = new RelationContext();
			relationContextMap.put(relation, relationContext);
		}
		return relationContext;
	}

	public TableContext createTableContext(Relation relation, Table table) {
		RelationContext relationContext = getOrCreateRelationContext(relation);
		TableContext tableContext = relationContext.getOrCreateTableContext(table);
		String alias = newAliasName(table.getName());
		tableContext.setAlias(alias);
		tableContext.setOffset(getOffset());
		addOffset(table.getColumnModel().size());
		//table.configureRelationalContext(this);
		
		return tableContext;
	}
	
	public String newAliasName(String tableName) {
		Integer occurences = aliasesCounter.get(tableName);
		if(occurences == null)
			occurences = 1;
		else
			occurences++;
		aliasesCounter.put(tableName, occurences);
		return tableName + occurences;
	}

	public Object getColumnValue(Relation relation, Column column) {
		RelationContext relationContext = getRelationContext(relation);
		return relationContext.getColumnValue(column);
	}

}
