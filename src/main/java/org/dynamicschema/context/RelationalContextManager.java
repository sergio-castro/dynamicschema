package org.dynamicschema.context;

import java.util.HashMap;
import java.util.Map;

import org.dynamicschema.reification.Table;

/*
 * Keep track of used alias names
 * Provides a method answering a new alias for a table that warranties the alias has not been used in any possible occurrence of the table in a SQL expression
 */
public class RelationalContextManager {

	private Map<String, Integer> aliasesCounter;
	private int offset;
	private RelationContext relationContext;
	
	public RelationalContextManager() {
		aliasesCounter = new HashMap<String, Integer>();
		relationContext = new RelationContext();
	}

	private int getOffset() {
		return offset;
	}
	
	private int addOffset(int delta) {
		return offset+=delta;
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
	
	public RelationContext getRelationContext() {
		return relationContext;
	}

	public TableContext createTableContext(TableNode tableNode) {
		return createTableContext(tableNode, null);
	}
	
	public TableContext createTableContext(TableNode tableNode, Map<String, Object> bindings) {
		TableContext tableContext = relationContext.getOrCreateTableContext(tableNode);
		Table table = tableNode.getTable();
		String alias = newAliasName(table.getName());
		tableContext.setAlias(alias);
		tableContext.setOffset(getOffset());
		addOffset(table.getColumnModel().size());
		tableContext.setBindings(bindings);
		return tableContext;
	}
	
}
