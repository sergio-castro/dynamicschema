package org.dynamicschema.context;

import java.util.HashMap;
import java.util.Map;

import org.dynamicschema.Column;
import org.dynamicschema.Relation;
import org.dynamicschema.Table;


public class ProxyRelationalContextManager implements IRelationalContextManager {

	private Map<Relation, Map<Column, Object>> bindings;
	private IRelationalContextManager ctx;
	
	public ProxyRelationalContextManager(Map<Column, Object> relationBindings, Relation relation, IRelationalContextManager ctx) {
		this.bindings = new HashMap<Relation, Map<Column, Object>>();
		bindings.put(relation,  relationBindings);
		this.ctx = ctx;
	}
	
	public IRelationalContextManager getProxiedContext() {
		return ctx;
	}
	
	public ProxyRelationalContextManager(Map<Relation, Map<Column, Object>> bindings, IRelationalContextManager ctx) {
		this.bindings = bindings;
		this.ctx = ctx;
	}

	@Override
	public RelationContext getRelationContext(Relation relation) {
		return ctx.getRelationContext(relation);
	}

	@Override
	public TableContext createTableContext(Relation relation, Table table) {
		return ctx.createTableContext(relation, table);
	}

	@Override
	public String newAliasName(String tableName) {
		return ctx.newAliasName(tableName);
	}
	
	@Override
	public RelationContext getOrCreateRelationContext(Relation relation) {
		return ctx.getOrCreateRelationContext(relation);
	}
	
	@Override
	public Object getColumnValue(Relation relation, Column column) {
		Object columnValue = null;
		Map<Column, Object> columnBindings = bindings.get(relation);
		if(columnBindings != null)
			columnValue = columnBindings.get(column);
		if(columnValue == null)
			columnValue = ctx.getColumnValue(relation, column);
		return columnValue;
	}
	
	
}
