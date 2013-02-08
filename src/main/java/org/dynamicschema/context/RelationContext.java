package org.dynamicschema.context;

import java.util.HashMap;
import java.util.Map;

import org.dynamicschema.Column;
import org.dynamicschema.Table;


public class RelationContext {
	private Map<Table, TableContext> tableContextMap;
	//private Map<Column, Object> columnValueMap;
	
	public RelationContext() {
		tableContextMap = new HashMap<Table, TableContext>();
		//columnValueMap = new HashMap<Column, Object>();
	}
	
	public TableContext getTableContext(Table table) {
		return tableContextMap.get(table);
	}
	
	public void addTableContext(Table table, TableContext tableContext) {
		tableContextMap.put(table, tableContext);
	}
	
	public TableContext getOrCreateTableContext(Table table) {
		TableContext tableContext = tableContextMap.get(table);
		if(tableContext==null) {
			tableContext = new TableContext();
			addTableContext(table, tableContext);
		}
		return tableContext;
	}
	
	public Object getColumnValue(Column column) {
		TableContext tableContext = tableContextMap.get(column.getTable());
		return tableContext.columnName(column);
		/*
		Object columnValue = columnValueMap.get(column);
		if(columnValue == null) {
			TableContext tableContext = tableContextMap.get(column.getTable());
			columnValue = tableContext.columnName(column);
		}
		return columnValue;
		*/
	}

}
