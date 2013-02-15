package org.dynamicschema.context;

import java.util.HashMap;
import java.util.Map;


public class RelationContext {
	private Map<TableNode, TableContext> relationContextMap;
	
	public RelationContext() {
		relationContextMap = new HashMap<TableNode, TableContext>();
	}
	
	public TableContext getTableContext(TableNode tableNode) {
		return relationContextMap.get(tableNode);
	}
	
	public void addTableContext(TableNode tableNode, TableContext tableContext) {
		relationContextMap.put(tableNode, tableContext);
	}
	
	public TableContext getOrCreateTableContext(TableNode tableNode) {
		TableContext tableContext = relationContextMap.get(tableNode);
		if(tableContext==null) {
			tableContext = new TableContext();
			addTableContext(tableNode, tableContext);
		}
		return tableContext;
	}

}

